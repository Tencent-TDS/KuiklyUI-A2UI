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

import Foundation
import OpenA2UIIOSRender

/// Demo-only helpers for feeding A2UI messages into `A2UISurfaceView`.
///
/// The A2UI v0.9 protocol defines the server-to-client channel as a stream of
/// JSON *objects* (envelopes). Some demo assets in this project package multiple
/// envelopes as a single JSON *array* for convenience — that array wrapper is
/// NOT part of the protocol.
///
/// Since `A2UISurfaceView.writeStream(_:)` intentionally rejects array-wrapped
/// input to stay aligned with the protocol, callers that hold such arrays must
/// unpack them and feed each envelope individually. This utility encapsulates
/// that unpacking so demos don't have to repeat it.
enum A2UIDemoStreamUtil {

    /// Unpack a demo-style JSON-array of A2UI envelopes and feed each envelope
    /// into `surfaceView` via `A2UISurfaceView.writeStream(_:)`.
    ///
    /// Also accepts:
    /// - A single envelope JSON object string (fed as-is).
    /// - A blank/whitespace string (no-op).
    ///
    /// Malformed input is logged and skipped instead of crashing the caller.
    ///
    /// - Parameters:
    ///   - surfaceView: target surface view
    ///   - jsonArrayOrObject: either `[ {envelope}, {envelope}, ... ]` or a single `{envelope}`
    static func writeMessages(_ surfaceView: A2UISurfaceView, _ jsonArrayOrObject: String) {
        let trimmed = jsonArrayOrObject.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty { return }

        guard let firstChar = trimmed.first else { return }

        switch firstChar {
        case "[":
            guard let data = jsonArrayOrObject.data(using: .utf8) else {
                NSLog("[A2UIDemoStreamUtil] writeMessages: failed to encode input as UTF-8; input dropped.")
                return
            }
            let parsed: Any
            do {
                parsed = try JSONSerialization.jsonObject(with: data, options: [])
            } catch {
                NSLog("[A2UIDemoStreamUtil] writeMessages: failed to parse JSON array: \(error.localizedDescription)")
                return
            }
            guard let array = parsed as? [Any] else {
                NSLog("[A2UIDemoStreamUtil] writeMessages: top-level is not a JSON array; input dropped.")
                return
            }
            for (index, item) in array.enumerated() {
                guard let object = item as? [String: Any] else {
                    NSLog("[A2UIDemoStreamUtil] writeMessages: item[\(index)] is not a JSON object; skipped. raw=\(item)")
                    continue
                }
                do {
                    let objData = try JSONSerialization.data(withJSONObject: object, options: [])
                    if let objStr = String(data: objData, encoding: .utf8) {
                        surfaceView.writeStream(objStr)
                    } else {
                        NSLog("[A2UIDemoStreamUtil] writeMessages: item[\(index)] serialize→utf8 failed; skipped.")
                    }
                } catch {
                    NSLog("[A2UIDemoStreamUtil] writeMessages: failed to re-serialize item[\(index)]: \(error.localizedDescription)")
                }
            }
        case "{":
            // Validate it parses as an object; still forward the original string on success
            guard let data = jsonArrayOrObject.data(using: .utf8) else {
                NSLog("[A2UIDemoStreamUtil] writeMessages: failed to encode input as UTF-8; input dropped.")
                return
            }
            do {
                _ = try JSONSerialization.jsonObject(with: data, options: [])
            } catch {
                NSLog("[A2UIDemoStreamUtil] writeMessages: failed to parse JSON object: \(error.localizedDescription)")
                return
            }
            surfaceView.writeStream(jsonArrayOrObject)
        default:
            NSLog("[A2UIDemoStreamUtil] writeMessages: input is neither a JSON object nor an array; input dropped.")
        }
    }
}
