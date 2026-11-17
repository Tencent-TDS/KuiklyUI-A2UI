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
import ObjectiveC.runtime
import OpenKuiklyA2UIIOSRender

// MARK: - A2UIRpcBridge shadow protocol
//
// Must stay in-sync with
//   a2ui/src/iosMain/kotlin/com/tencent/kuikly/a2ui/bridge/A2UICoreEntry.kt
// where the class is exposed via `@ObjCName("A2UIRpcBridge")` and its
// `companion object` property `shared` is also annotated with `@ObjCName`.
//
// K/N selector shape (names derived from each parameter's Kotlin label —
// the first argument's label becomes part of the selector automatically):
//
//   -setHandlerInstanceId:handler:
//   -removeHandlerInstanceId:
//   -respondInstanceId:surfaceId:requestId:resultJson:
//   -failInstanceId:surfaceId:requestId:code:message:detailsJson:
//   -invokeInstanceId:surfaceId:name:argsJson:timeoutMs:onComplete:
//
// We declare them below as `@objc` instance methods so we can call them
// via a Swift protocol reference acquired through `unsafeBitCast` — the
// exact same pattern `A2UISurfaceView` uses for `A2UICoreEntryProtocol`.
@objc private protocol A2UIRpcBridgeProtocol {
    @objc(setHandlerInstanceId:handler:)
    func setHandler(instanceId: String, handler: @escaping (String) -> Void)

    @objc(removeHandlerInstanceId:)
    func removeHandler(instanceId: String)

    @objc(respondInstanceId:surfaceId:requestId:resultJson:)
    func respond(instanceId: String, surfaceId: String, requestId: String, resultJson: String)

    @objc(failInstanceId:surfaceId:requestId:code:message:detailsJson:)
    func fail(
        instanceId: String,
        surfaceId: String,
        requestId: String,
        code: String,
        message: String,
        detailsJson: String
    )

    @objc(invokeInstanceId:surfaceId:name:argsJson:timeoutMs:onComplete:)
    func invoke(
        instanceId: String,
        surfaceId: String,
        name: String,
        argsJson: String,
        timeoutMs: Int64,
        onComplete: @escaping (String) -> Void
    ) -> String?
}

/// Lazy, process-scope accessor for the Kotlin/Native-emitted
/// `A2UIRpcBridge.shared` singleton.
///
/// Why inline + in the demo module: the render pod already ships
/// `A2UIFrameworkClassResolver` for this exact resolve-via-companion
/// pattern, but it's declared `internal`, so a cross-target demo like
/// this one cannot use it. Rather than widen that API surface for a
/// single call site, we inline a tiny two-hop resolver here and leave
/// the production helper alone.
private enum A2UIRpcBridgeAccessor {

    /// Build the candidate K/N framework-prefix forms we're willing to
    /// try. Mirrors the logic in `A2UIFrameworkClassResolver` (in the
    /// render pod) but inlined here because that helper is `internal`
    /// and not reachable from this cross-target demo. Typical values
    /// of `A2UISurfaceView.frameworkName` are `"A2UICore"` or
    /// `"A2UIDemo"`; when it's nil we fall back to the bare
    /// `@ObjCName` form only.
    private static func candidateClassNames() -> [String] {
        let objCName = "A2UIRpcBridge"
        var out: [String] = []
        if let raw = A2UISurfaceView.frameworkName, !raw.isEmpty {
            // Capitalise the first letter to match K/N's framework
            // prefix convention.
            let normalized: String = {
                guard let first = raw.first else { return raw }
                return String(first).uppercased() + raw.dropFirst()
            }()
            out.append(normalized + objCName)
            out.append(objCName)
            // K/N alternate cache-mode prefix: strip lowercase letters.
            let noLower = normalized.filter { !$0.isLowercase }
            if !noLower.isEmpty && noLower != normalized {
                out.append(noLower + objCName)
            }
            // Stricter: uppercase ASCII letters only (handles "A2UICore"
            // → "AUIC" cache-mode form observed in the wild).
            let uppersOnly: String = String(normalized.unicodeScalars.filter {
                ($0.value >= 0x41 && $0.value <= 0x5A)
            })
            if !uppersOnly.isEmpty && uppersOnly != normalized && uppersOnly != noLower {
                out.append(uppersOnly + objCName)
            }
        } else {
            // No frameworkName set: try the common pods explicitly
            // plus the bare `@ObjCName` fallback.
            out.append("A2UICore" + objCName)
            out.append("A2UIDemo" + objCName)
            out.append(objCName)
        }
        return out
    }

    /// Protected by the Swift runtime's guarantee that enum-static
    /// var initialization is thread-safe on first access. Subsequent
    /// reads are lock-free.
    static let shared: A2UIRpcBridgeProtocol? = resolve()

    private static func resolve() -> A2UIRpcBridgeProtocol? {
        let companionSel = NSSelectorFromString("companion")
        let sharedSel = NSSelectorFromString("shared")
        for name in candidateClassNames() {
            guard let cls = NSClassFromString(name) as? NSObject.Type,
                  cls.responds(to: companionSel) else {
                continue
            }
            guard let companionUM = cls.perform(companionSel),
                  let companion = companionUM.takeUnretainedValue() as AnyObject?,
                  companion.responds(to: sharedSel) else {
                continue
            }
            guard let sharedUM = companion.perform(sharedSel),
                  let raw = sharedUM.takeUnretainedValue() as AnyObject? else {
                continue
            }
            // K/N-emitted class does not statically declare conformance
            // to our local `@objc` protocol, but the selectors line up
            // 1:1 so ObjC `msgSend` dispatch works just fine.
            return unsafeBitCast(raw, to: A2UIRpcBridgeProtocol.self)
        }
        return nil
    }
}

/// A2UI v1.0 **RPC round-trip demo**, Native-page edition (iOS).
///
/// Mirror of
///   androidAppForNativePage/.../NativeRpcDemoActivity.kt
/// — both exercise the PR-6d native-page RPC loop:
///
///   * **Outbound** (renderer → agent): the host calls
///     [A2UIRpcBridgeProtocol.invoke] on the Kotlin bridge, which mints
///     a `callAgentFunction` envelope; the Kotlin core then hands that
///     envelope to the native-page container via
///     `A2UINativeMethod.sendRpcEnvelope (108)`, which lands on
///     [A2UISurfaceView.rpcEnvelopeOutboundHandler]. The handler below
///     runs a local mock agent that replies with
///     `agentFunctionResponse` back through [A2UISurfaceView.writeStream].
///
///   * **Inbound** (agent → renderer): the "Mock agent" button
///     synthesises a `callRendererFunction` envelope and feeds it into
///     [A2UISurfaceView.writeStream]. The Kotlin dispatcher routes it
///     to the handler we registered via
///     [A2UIRpcBridgeProtocol.setHandler], which then uses
///     [A2UIRpcBridgeProtocol.respond] to close the loop.
///
/// This VC relies on `A2UIRpcBridge.shared` being resolvable at runtime
/// — `A2UIRpcBridgeAccessor.shared` tries both the standard K/N prefix
/// (`A2UICore` / `A2UIDemo`) and the bare `@ObjCName` form.
class A2UIRpcDemoViewController: UIViewController {

    // MARK: Constants

    /// Stable surfaceId reused across every envelope on this page.
    private let surfaceId: String = "rpc-demo-native-ios"

    private static let maxLogLines: Int = 200

    /// Minimal v1.0 `createSurface` envelope that paints a single Text
    /// component. The demo's point is the RPC roundtrip, not the UI
    /// tree, so we keep this deliberately tiny.
    private static let initialCreateSurfaceEnvelope: String = """
    {"version":"1.0","createSurface":{"surfaceId":"rpc-demo-native-ios","catalogId":"custom_functions_catalog_v1_0.json"}}
    {"version":"1.0","updateComponents":{"surfaceId":"rpc-demo-native-ios","components":[{"id":"root","component":"Column","children":["hint"]},{"id":"hint","component":"Text","text":"Tap a button below to trigger an RPC."}]}}
    """

    // MARK: UI

    private let statusLabel = UILabel()
    private let logTextView = UITextView()
    private let surfaceView = A2UISurfaceView(frame: .zero)

    // MARK: State

    /// Monotonic counter for the mock agent's inbound requestIds.
    private var mockRequestCounter: Int = 0

    /// Rolling log — newest at top, capped at [maxLogLines].
    private var eventLog: [String] = []

    /// The resolved Kotlin RPC bridge singleton. Captured once in
    /// viewDidLoad so later calls don't re-run the runtime resolver.
    private var rpcBridge: A2UIRpcBridgeProtocol?

    // MARK: Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "v1.0 RPC Demo (Native)"
        view.backgroundColor = .systemBackground

        buildUi()

        // Resolve the Kotlin bridge BEFORE wiring any handler so a
        // missing framework fails loudly with a status banner rather
        // than silently dropping RPCs.
        rpcBridge = A2UIRpcBridgeAccessor.shared
        if rpcBridge == nil {
            setStatus("A2UIRpcBridge not found in runtime — is A2UICore / A2UIDemo linked?")
            log("! A2UIRpcBridge.shared resolve FAILED; outbound & inbound RPC will be no-ops")
        }

        // Install the outbound v1.0 RPC envelope sink BEFORE initSurface().
        // The surface may emit envelopes during its own bootstrap in
        // rare cases (e.g. error responses from a malformed initial
        // envelope), and we don't want to miss them.
        surfaceView.rpcEnvelopeOutboundHandler = { [weak self] envelopeJson in
            // Called on the Kuikly Context Queue; hop to main.
            DispatchQueue.main.async {
                self?.onOutboundEnvelope(envelopeJson)
            }
        }

        // Bootstrap the Kotlin core for this surface.
        surfaceView.initSurface(configJson: "{\"protocolVersion\":\"1.0\",\"strictV10\":true}")

        // Register the inbound callRendererFunction handler.
        if let bridge = rpcBridge {
            bridge.setHandler(instanceId: surfaceView.instanceId) { [weak self] requestJson in
                DispatchQueue.main.async {
                    self?.onInboundRendererFunction(requestJson)
                }
            }
        }

        // Feed an initial createSurface envelope so the surface has
        // visible content while the user exercises the buttons. Must
        // follow initSurface() so writeStream has a valid coreEntry.
        surfaceView.writeStream(Self.initialCreateSurfaceEnvelope)
        // Intentionally no finishStream() — the stream stays open so
        // subsequent writeStream() calls can deliver more envelopes.
    }

    deinit {
        // Prevent late Kotlin dispatcher callbacks from firing into a
        // torn-down VC. Idempotent — safe to call with a nil bridge.
        A2UIRpcBridgeAccessor.shared?.removeHandler(instanceId: surfaceView.instanceId)
    }

    // MARK: - Inbound: mock agent → renderer

    @objc private func simulateAgentCallRendererFunction() {
        mockRequestCounter += 1
        let requestId = "req-\(mockRequestCounter)"
        let envelope: [String: Any] = [
            "version": "1.0",
            "callRendererFunction": [
                "surfaceId": surfaceId,
                "functionCallId": requestId,
                "callFunction": [
                    "@call": "getGreeting",
                    "catalogId": "custom_functions_catalog_v1_0.json",
                    "args": ["name": "Ada"]
                ]
            ]
        ]
        guard
            let data = try? JSONSerialization.data(withJSONObject: envelope, options: []),
            let json = String(data: data, encoding: .utf8)
        else {
            log("! failed to serialise inbound envelope")
            return
        }
        setStatus("Mock agent -> callRendererFunction getGreeting")
        log("agent → \(json)")
        surfaceView.writeStream(json)
    }

    /// Called by `A2UIBridgeManager.setRendererFunctionJsonHandler`
    /// (via `A2UIRpcBridge.setHandler`) on every `callRendererFunction`
    /// arriving on this surface's instance. Host callbacks retain internal
    /// requestId/name/args fields; the stream uses official 1.0 fields.
    private func onInboundRendererFunction(_ requestJson: String) {
        log("← agent callRendererFunction: \(requestJson)")
        guard
            let data = requestJson.data(using: .utf8),
            let any = try? JSONSerialization.jsonObject(with: data, options: []),
            let req = any as? [String: Any]
        else {
            log("! malformed inbound JSON")
            return
        }
        let reqSurfaceId = req["surfaceId"] as? String ?? ""
        let requestId = req["requestId"] as? String ?? ""
        let name = req["name"] as? String ?? ""
        let args = req["args"] as? [String: Any]
        let instanceId = surfaceView.instanceId
        guard let bridge = rpcBridge else {
            log("! inbound ignored: A2UIRpcBridge is nil")
            return
        }
        switch name {
        case "getGreeting":
            let recipient = args?["name"] as? String ?? ""
            setStatus("Inbound getGreeting(name=\"\(recipient)\") → ok")
            log("→ host responding ok for requestId=\(requestId)")
            guard let resultData = try? JSONSerialization.data(
                withJSONObject: "Hello, \(recipient)!", options: [.fragmentsAllowed]
            ), let result = String(data: resultData, encoding: .utf8) else {
                log("! failed to encode greeting response")
                return
            }
            bridge.respond(
                instanceId: instanceId,
                surfaceId: reqSurfaceId,
                requestId: requestId,
                resultJson: result
            )
        default:
            log("→ host failing (UNKNOWN_FUNCTION) requestId=\(requestId)")
            bridge.fail(
                instanceId: instanceId,
                surfaceId: reqSurfaceId,
                requestId: requestId,
                code: "UNKNOWN_FUNCTION",
                message: "renderer does not know function '\(name)'",
                detailsJson: ""
            )
        }
    }

    // MARK: - Outbound: renderer → agent

    @objc private func invokeAgentFetchUser() {
        guard let bridge = rpcBridge else {
            setStatus("A2UIRpcBridge not available")
            return
        }
        let instanceId = surfaceView.instanceId
        let argsJson = "{\"id\":42}"
        setStatus("Host -> invokeAgentFunction fetchUser")
        let reqId = bridge.invoke(
            instanceId: instanceId,
            surfaceId: surfaceId,
            name: "fetchUser",
            argsJson: argsJson,
            timeoutMs: 5_000
        ) { [weak self] responseJson in
            // Fires on the dispatcher's thread (Kuikly Context Queue).
            // Hop to main for UI work.
            DispatchQueue.main.async {
                self?.onAgentFunctionResponse(responseJson)
            }
        }
        if reqId == nil {
            log("! invoke returned nil (instance unknown or stream not v1.0 yet)")
            setStatus("invokeAgentFunction failed (nil reqId)")
            return
        }
        log("→ host invokeAgentFunction reqId=\(reqId ?? "<nil>") args=\(argsJson)")
        // Note: the outbound envelope itself is emitted by Kotlin core
        // via NativeMethod.sendRpcEnvelope (108), which lands in
        // onOutboundEnvelope() below. That function contains the
        // mock-agent that synthesises the matching agentFunctionResponse
        // and feeds it back through writeStream, closing the loop
        // without needing a real network transport.
    }

    private func onAgentFunctionResponse(_ responseJson: String) {
        log("← onComplete: \(responseJson)")
        guard
            let data = responseJson.data(using: .utf8),
            let any = try? JSONSerialization.jsonObject(with: data, options: []),
            let resp = any as? [String: Any]
        else {
            setStatus("fetchUser: (unparseable response)")
            return
        }
        if let result = resp["result"] {
            setStatus("fetchUser result: \(result)")
        } else if let error = resp["error"] as? [String: Any] {
            setStatus("fetchUser error: \(error["code"] ?? "")")
        } else {
            setStatus("fetchUser: (empty response)")
        }
    }

    // MARK: - SEND_RPC_ENVELOPE outbound handler + mock agent

    /// Called when the Kotlin core hands us an outbound v1.0 RPC
    /// envelope via `A2UISurfaceView.rpcEnvelopeOutboundHandler` — the
    /// demo's stand-in for "ship this to the agent over the wire".
    ///
    /// Two envelope shapes arrive here:
    ///  - `{"version":"1.0","callAgentFunction":{…}}` — emitted by
    ///    [invokeAgentFetchUser]. We mock-reply with
    ///    `agentFunctionResponse` on the same stream.
    ///  - `{"version":"1.0","rendererFunctionResponse":{…}}` — emitted
    ///    by our own inbound handler's `respond(…)` call. A real
    ///    integration would forward this to the agent transport; here
    ///    we just log it.
    private func onOutboundEnvelope(_ envelopeJson: String) {
        log("agent-wire ← \(envelopeJson)")
        guard
            let data = envelopeJson.data(using: .utf8),
            let any = try? JSONSerialization.jsonObject(with: data, options: []),
            let env = any as? [String: Any],
            let call = env["callAgentFunction"] as? [String: Any]
        else {
            return
        }
        // Mock backend: pretend we processed fetchUser(id=42) and reply.
        let requestId = call["functionCallId"] as? String ?? ""
        let response: [String: Any] = [
            "version": "1.0",
            "agentFunctionResponse": [
                "functionCallId": requestId,
                "value": ["id": 42, "name": "Alice", "role": "admin"]
            ]
        ]
        guard
            let respData = try? JSONSerialization.data(withJSONObject: response, options: []),
            let respJson = String(data: respData, encoding: .utf8)
        else {
            return
        }
        log("mock-agent → \(respJson)")
        surfaceView.writeStream(respJson)
    }

    // MARK: - UI construction

    private func buildUi() {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(stack)
        NSLayoutConstraint.activate([
            stack.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 12),
            stack.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 16),
            stack.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -16),
            stack.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -12)
        ])

        let title = UILabel()
        title.text = "A2UI v1.0 RPC Demo (Native Page)"
        title.font = .boldSystemFont(ofSize: 18)
        title.textColor = .label
        stack.addArrangedSubview(title)

        statusLabel.text = "Status: Tap a button below to trigger an RPC."
        statusLabel.textColor = .secondaryLabel
        statusLabel.font = .systemFont(ofSize: 12)
        statusLabel.numberOfLines = 0
        stack.addArrangedSubview(statusLabel)

        surfaceView.backgroundColor = UIColor(red: 0.95, green: 0.96, blue: 0.98, alpha: 1.0)
        surfaceView.layer.cornerRadius = 6
        surfaceView.clipsToBounds = true
        surfaceView.translatesAutoresizingMaskIntoConstraints = false
        surfaceView.heightAnchor.constraint(greaterThanOrEqualToConstant: 80).isActive = true
        stack.addArrangedSubview(surfaceView)

        stack.addArrangedSubview(makeButton(
            title: "Mock agent → getGreeting(\"Ada\")",
            bg: UIColor(red: 0.10, green: 0.45, blue: 0.91, alpha: 1.0),
            action: #selector(simulateAgentCallRendererFunction)
        ))
        stack.addArrangedSubview(makeButton(
            title: "Fetch user from agent (id=42)",
            bg: UIColor(red: 0.20, green: 0.65, blue: 0.32, alpha: 1.0),
            action: #selector(invokeAgentFetchUser)
        ))
        stack.addArrangedSubview(makeButton(
            title: "Clear event log",
            bg: UIColor(white: 0.74, alpha: 1.0),
            action: #selector(clearLog)
        ))

        let logTitle = UILabel()
        logTitle.text = "Event log (newest first)"
        logTitle.font = .systemFont(ofSize: 13)
        logTitle.textColor = UIColor(white: 0.27, alpha: 1.0)
        stack.addArrangedSubview(logTitle)

        logTextView.backgroundColor = UIColor(white: 0.98, alpha: 1.0)
        logTextView.font = UIFont.monospacedSystemFont(ofSize: 11, weight: .regular)
        logTextView.textColor = UIColor(white: 0.2, alpha: 1.0)
        logTextView.isEditable = false
        logTextView.isSelectable = true
        logTextView.layer.cornerRadius = 4
        logTextView.layer.borderWidth = 0.5
        logTextView.layer.borderColor = UIColor.separator.cgColor
        stack.addArrangedSubview(logTextView)
    }

    private func makeButton(title: String, bg: UIColor, action: Selector) -> UIButton {
        let button = UIButton(type: .system)
        button.setTitle(title, for: .normal)
        button.setTitleColor(.white, for: .normal)
        button.titleLabel?.font = .systemFont(ofSize: 14, weight: .medium)
        button.backgroundColor = bg
        button.layer.cornerRadius = 6
        button.translatesAutoresizingMaskIntoConstraints = false
        button.heightAnchor.constraint(equalToConstant: 42).isActive = true
        button.addTarget(self, action: action, for: .touchUpInside)
        return button
    }

    // MARK: - Log helpers

    @objc private func clearLog() {
        eventLog.removeAll()
        renderLog()
    }

    private func log(_ line: String) {
        eventLog.insert(line, at: 0)
        if eventLog.count > Self.maxLogLines {
            eventLog.removeLast(eventLog.count - Self.maxLogLines)
        }
        renderLog()
    }

    private func renderLog() {
        logTextView.text = eventLog.joined(separator: "\n")
    }

    private func setStatus(_ line: String) {
        statusLabel.text = "Status: \(line)"
    }
}
