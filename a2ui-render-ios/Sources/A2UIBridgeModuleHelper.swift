/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import UIKit
import Foundation

/// Swift helper backing `A2UIBridgeModule.m`.
///
/// `A2UIBridgeModule.m` deliberately stays free of Swift / Kuikly-Swift
/// imports (so this pod does not force a Swift bridging header on hosts
/// that only `#import` the KRBaseModule-based API). Its ObjC dispatcher
/// resolves this helper by name (`objc_lookUpClass("A2UIBridgeModuleHelper")`)
/// and calls three stable `@objc` class methods on it:
///
///   * `+showPickerWithConfigJson:`
///   * `+executeFunctionCallWithName:argsJson:`
///   * `+invokeCustomFunctionWithName:argsJson:spec:`
///
/// Keeping the helper in Swift lets us reuse UIKit APIs idiomatically and
/// defer to `A2UISurfaceView.invokeCustomFunctionLocallyStatic` for the
/// custom-function path without duplicating the handler lookup logic.
///
/// Threading contract (mirrors `A2UIBridgeModule.m`):
///   * `showPickerWithConfigJson` and `executeFunctionCallWithName:argsJson:`
///     are already hopped to the main queue by the ObjC dispatcher; the
///     implementations here can touch UIKit directly.
///   * `invokeCustomFunctionWithName:argsJson:spec:` runs on the Kuikly
///     Context Queue — it must not block.
@objc(A2UIBridgeModuleHelper)
public final class A2UIBridgeModuleHelper: NSObject {

    // MARK: - Date/time picker

    /// Pop the native date/time picker and route its result back to KMP.
    ///
    /// `configJson` carries the picker configuration *and* the owning DSL
    /// surface's `dslInstanceId` (mirrored across platforms — see
    /// `A2UIDSLBridge.showDateTimePicker`). The resulting ISO-8601 string
    /// is pushed back into KMP through
    /// `A2UIPickerResultBridge.shared.send(dslInstanceId:tag:isoString:)`,
    /// which calls `A2UIBridgeManager.callKotlinMethod(PICKER_RESULT, …)`
    /// on the Kuikly Context Queue (hopped via `A2UIRenderHelper.perform(onContextQueue:)`).
    @objc public static func showPickerWithConfigJson(_ configJson: String) {
        guard let data = configJson.data(using: .utf8),
              let config = try? JSONSerialization.jsonObject(with: data) as? [String: Any]
        else { return }

        let tag = (config["tag"] as? NSNumber)?.intValue ?? -1
        let dslInstanceId = config["dslInstanceId"] as? String ?? ""
        guard tag >= 0, !dslInstanceId.isEmpty else {
            NSLog("[A2UIBridgeModuleHelper] showPicker: missing tag/dslInstanceId")
            return
        }

        let enableDate = (config["enableDate"] as? Bool) ?? true
        let enableTime = (config["enableTime"] as? Bool) ?? false
        let value = (config["value"] as? String) ?? ""
        let label = (config["label"] as? String) ?? "Select"
        let minStr = (config["min"] as? String) ?? ""
        let maxStr = (config["max"] as? String) ?? ""

        // Parse initial date (defaults to now if missing/invalid).
        let initialDate: Foundation.Date = Self.parseInitialDate(value)
        let minimumDate: Foundation.Date? = Self.parseDateTimeString(minStr)
        let maximumDate: Foundation.Date? = {
            guard !maxStr.isEmpty else { return nil }
            // Pure date max → extend to end-of-day so the whole day is selectable.
            if !maxStr.contains("T") && !maxStr.contains(":") {
                return Self.parseDateTimeString(maxStr + "T23:59:00")
            }
            return Self.parseDateTimeString(maxStr)
        }()

        let pickerMode: UIDatePicker.Mode = {
            if enableDate && enableTime { return .dateAndTime }
            if enableTime { return .time }
            return .date
        }()

        let alert = UIAlertController(
            title: label,
            message: "\n\n\n\n\n\n\n\n\n\n\n",
            preferredStyle: .actionSheet
        )
        let picker = UIDatePicker()
        picker.datePickerMode = pickerMode
        picker.date = initialDate
        if let minDate = minimumDate { picker.minimumDate = minDate }
        if let maxDate = maximumDate { picker.maximumDate = maxDate }
        if #available(iOS 13.4, *) { picker.preferredDatePickerStyle = .wheels }
        picker.frame = CGRect(x: 0, y: 40, width: alert.view.bounds.width - 20, height: 220)
        picker.autoresizingMask = [.flexibleWidth]
        alert.view.addSubview(picker)

        alert.addAction(UIAlertAction(title: "Done", style: .default) { _ in
            let date = picker.date
            let cal = Calendar.current
            let y = cal.component(.year, from: date)
            let m = cal.component(.month, from: date)
            let d = cal.component(.day, from: date)
            let h = cal.component(.hour, from: date)
            let mm = cal.component(.minute, from: date)
            let iso = String(format: "%04d-%02d-%02dT%02d:%02d:00Z", y, m, d, h, mm)
            // Route back to KMP via the shared bridge. The bridge expects
            // to be called on the Kuikly Context Queue — hop there first.
            Self.sendPickerResultOnContextQueue(dslInstanceId: dslInstanceId, tag: tag, isoString: iso)
        })
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))

        guard let presenter = Self.topViewController() else { return }
        presenter.present(alert, animated: true)
    }

    // MARK: - executeFunctionCall

    /// Execute the built-in A2UI v0.9 `openUrl` action. Unknown names are
    /// logged — hosts that want custom semantics should emit `action.event`
    /// instead.
    @objc public static func executeFunctionCallWithName(_ functionName: String, argsJson: String) {
        guard let data = argsJson.data(using: .utf8),
              let args = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
            NSLog("[A2UIBridgeModuleHelper] executeFunctionCall: bad args JSON")
            return
        }
        switch functionName {
        case "openUrl":
            if let urlStr = args["url"] as? String, let url = URL(string: urlStr) {
                UIApplication.shared.open(url)
            }
        default:
            NSLog("[A2UIBridgeModuleHelper] executeFunctionCall: unknown '%@'", functionName)
        }
    }

    // MARK: - invokeCustomFunction

    /// Delegate to `A2UISurfaceView.invokeCustomFunctionLocallyStatic`, then
    /// coerce the raw return value into a JSON-encoded string so the
    /// Kuikly Module channel can carry it unchanged. KMP side
    /// `A2UIDSLBridge.invokeCustomFunctionOnNative` decodes the JSON and
    /// hands the result (String / Boolean / Number / JSON-array-String)
    /// to the DSL coercer. `nil` passes through as `nil`.
    @objc public static func invokeCustomFunctionWithName(
        _ name: String,
        argsJson: String,
        spec: String
    ) -> NSString? {
        let raw = A2UISurfaceView.invokeCustomFunctionLocallyStatic(
            name: name,
            argsJson: argsJson,
            spec: spec
        )
        return Self.encodeInvokeResult(raw)
    }

    // MARK: - calculateCustomViewSize

    /// Resolve the business-side `measurer` registered for a custom view
    /// via `A2UISurfaceView.registerCustomRenderView(viewName:creator:measurer:)`
    /// and run it inline on the current (Kuikly Context) queue, returning
    /// `"<width>|<height>"` back to the KMP core.
    ///
    /// Payload: `"<viewName>\n<nativeRef>\n<constraintWidth>\n<constraintHeight>"`,
    /// matching the wire format of the Android / OHOS counterparts byte-
    /// for-byte so the KMP side `A2UIBridgeManager.calculateCustomViewSize`
    /// stays on a single implementation.
    ///
    /// Return value is a plain `"<width>|<height>"` string. An empty
    /// NSString signals "no measurer registered / malformed payload /
    /// measurer returned nil" \u2014 the KMP side interprets it as
    /// "fall back to parent-cross-axis / 0" (JSON `width` / `height` still
    /// win over this).
    ///
    /// Threading: `A2UIBridgeModule.m` keeps us on the Kuikly Context
    /// Queue (syncCall=1); the measurer runs inline, so authors must keep
    /// it synchronous and avoid UIKit mutations (see
    /// `A2UISurfaceView.A2UICustomViewMeasurer` for the contract).
    @objc public static func calculateCustomViewSizeWithPayload(_ payload: String) -> NSString {
        let parts = payload.components(separatedBy: "\n")
        guard parts.count >= 4 else { return "" }
        let viewName = parts[0]
        guard let cw = Double(parts[2]), let ch = Double(parts[3]) else { return "" }
        guard let measurer = A2UISurfaceView.lookupMeasurer(viewName: viewName) else {
            return ""
        }
        guard let size = measurer(CGFloat(cw), CGFloat(ch)) else {
            return ""
        }
        return "\(size.width)|\(size.height)" as NSString
    }

    // MARK: - Private helpers

    /// Hop to the Kuikly Context Queue (via the same `A2UIRenderHelper`
    /// used elsewhere in this pod), then call the KMP-side
    /// `A2UIPickerResultBridge` singleton.
    private static func sendPickerResultOnContextQueue(
        dslInstanceId: String,
        tag: Int,
        isoString: String
    ) {
        // Resolve the bridge class by name so we neither need an `@import`
        // (the exported ObjC class name is pinned via @ObjCName on the
        // Kotlin side) nor force the helper to compile against a specific
        // KMP framework name (A2UICore vs A2UIDemo).
        guard let bridgeCls = objc_lookUpClass("A2UIPickerResultBridge") as? NSObject.Type else {
            NSLog("[A2UIBridgeModuleHelper] A2UIPickerResultBridge class not linked")
            return
        }
        let sharedSel = NSSelectorFromString("shared")
        guard bridgeCls.responds(to: sharedSel),
              let shared = bridgeCls.perform(sharedSel)?.takeUnretainedValue() as? NSObject else {
            return
        }
        let sendSel = NSSelectorFromString("sendDslInstanceId:tag:isoString:")
        guard shared.responds(to: sendSel) else { return }
        // Hop onto the Kuikly Context Queue so the KMP side touches
        // `dslBridgeMap` under the invariant it was designed for.
        A2UIRenderHelper.perform(onContextQueue: {
            typealias Fn = @convention(c) (NSObject, Selector, NSString, Int, NSString) -> Void
            let imp = shared.method(for: sendSel)
            let fn = unsafeBitCast(imp, to: Fn.self)
            fn(shared, sendSel, dslInstanceId as NSString, tag, isoString as NSString)
        })
    }

    /// Turn whatever `invokeCustomFunctionLocallyStatic` produced into a
    /// JSON string the KMP coercer can decode. Mirrors the per-case
    /// handling that `A2UISurfaceView.invokeCustomFunctionLocallyStatic`
    /// itself does, but wraps it as JSON so the Module bus — which only
    /// supports NSString payloads — can transport it losslessly.
    private static func encodeInvokeResult(_ raw: Any?) -> NSString? {
        guard let raw = raw else { return nil }
        switch raw {
        case let s as String:
            return "\"\(Self.escapeJSONString(s))\"" as NSString
        case let b as Bool:
            return (b ? "true" : "false") as NSString
        case let n as NSNumber:
            // CFNumber comparison avoids `Bool` leaking in as `NSNumber(0/1)`.
            if CFNumberGetType(n) == .charType {
                return (n.boolValue ? "true" : "false") as NSString
            }
            return "\(n)" as NSString
        case let s as NSString:
            // The static invoker also returns JSON-encoded array strings as
            // NSString. Re-wrap or pass through depending on content.
            if s.hasPrefix("[") || s.hasPrefix("{") {
                return s
            }
            return "\"\(Self.escapeJSONString(s as String))\"" as NSString
        default:
            // Fallback: convert via JSONSerialization for structured values.
            if JSONSerialization.isValidJSONObject(raw),
               let data = try? JSONSerialization.data(withJSONObject: raw, options: []),
               let str = String(data: data, encoding: .utf8) {
                return str as NSString
            }
            return "\"\(Self.escapeJSONString("\(raw)"))\"" as NSString
        }
    }

    private static func escapeJSONString(_ s: String) -> String {
        var out = ""
        for c in s {
            switch c {
            case "\\": out += "\\\\"
            case "\"": out += "\\\""
            case "\n": out += "\\n"
            case "\r": out += "\\r"
            case "\t": out += "\\t"
            default:   out.append(c)
            }
        }
        return out
    }

    private static func parseInitialDate(_ value: String) -> Foundation.Date {
        if value.isEmpty { return Foundation.Date() }
        var components = DateComponents()
        components.year = 2025; components.month = 1; components.day = 1
        components.hour = 0; components.minute = 0
        let datePart = value.components(separatedBy: "T").first ?? value
        let df = datePart.components(separatedBy: "-")
        if df.count >= 3 {
            components.year = Int(df[0]) ?? 2025
            components.month = Int(df[1]) ?? 1
            components.day = Int(df[2]) ?? 1
        }
        if value.contains("T") {
            let timePart = value.components(separatedBy: "T").last?
                .components(separatedBy: "Z").first?
                .components(separatedBy: "+").first ?? ""
            let tf = timePart.components(separatedBy: ":")
            if tf.count >= 2 {
                components.hour = Int(tf[0]) ?? 0
                components.minute = Int(tf[1]) ?? 0
            }
        }
        return Calendar.current.date(from: components) ?? Foundation.Date()
    }

    private static func parseDateTimeString(_ str: String) -> Foundation.Date? {
        if str.isEmpty { return nil }
        var components = DateComponents()
        if str.contains("T") {
            let dp = str.components(separatedBy: "T").first ?? ""
            let df = dp.components(separatedBy: "-")
            if df.count >= 3 {
                components.year = Int(df[0])
                components.month = Int(df[1])
                components.day = Int(df[2])
            }
            let tp = str.components(separatedBy: "T").last?
                .components(separatedBy: "Z").first?
                .components(separatedBy: "+").first ?? ""
            let tf = tp.components(separatedBy: ":")
            if tf.count >= 2 {
                components.hour = Int(tf[0])
                components.minute = Int(tf[1])
            }
            if tf.count >= 3 {
                components.second = Int(tf[2])
            }
        } else if str.contains(":") {
            let tf = str.components(separatedBy: ":")
            if tf.count >= 2 {
                components.hour = Int(tf[0])
                components.minute = Int(tf[1])
            }
        } else {
            let df = str.components(separatedBy: "-")
            if df.count >= 3 {
                components.year = Int(df[0])
                components.month = Int(df[1])
                components.day = Int(df[2])
            }
        }
        return Calendar.current.date(from: components)
    }

    private static func topViewController(_ base: UIViewController? = nil) -> UIViewController? {
        let base = base ?? Self.keyWindowRootViewController()
        if let nav = base as? UINavigationController {
            return topViewController(nav.visibleViewController)
        }
        if let tab = base as? UITabBarController, let selected = tab.selectedViewController {
            return topViewController(selected)
        }
        if let presented = base?.presentedViewController {
            return topViewController(presented)
        }
        return base
    }

    private static func keyWindowRootViewController() -> UIViewController? {
        if #available(iOS 13.0, *) {
            for scene in UIApplication.shared.connectedScenes {
                guard let ws = scene as? UIWindowScene else { continue }
                for window in ws.windows where window.isKeyWindow {
                    return window.rootViewController
                }
            }
        }
        return UIApplication.shared.windows.first { $0.isKeyWindow }?.rootViewController
    }
}
