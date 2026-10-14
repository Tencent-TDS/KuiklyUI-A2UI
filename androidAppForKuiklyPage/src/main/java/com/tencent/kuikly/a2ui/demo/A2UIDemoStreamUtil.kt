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

package com.tencent.kuikly.a2ui.demo

import android.util.Log
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Demo-only helpers for feeding A2UI messages into [A2UISurfaceView].
 *
 * The A2UI v0.9 protocol defines the server-to-client channel as a stream of
 * JSON *objects* (envelopes). Some demo assets in this project package multiple
 * envelopes as a single JSON *array* for convenience — that array wrapper is
 * NOT part of the protocol.
 *
 * Since [A2UISurfaceView.writeStream] intentionally rejects array-wrapped input
 * to stay aligned with the protocol, callers that hold such arrays must unpack
 * them and feed each envelope individually. This utility encapsulates that
 * unpacking so demos don't have to repeat it.
 */
object A2UIDemoStreamUtil {

    private const val TAG = "A2UIDemoStreamUtil"

    /**
     * Unpack a demo-style JSON-array of A2UI envelopes and feed each envelope
     * into [surfaceView] via [A2UISurfaceView.writeStream].
     *
     * Also accepts:
     * - A single envelope JSON object string (fed as-is).
     * - A blank/whitespace string (no-op).
     *
     * Malformed input is logged and skipped instead of crashing the caller.
     *
     * @param surfaceView       target surface view
     * @param jsonArrayOrObject either `[ {envelope}, {envelope}, ... ]` or a single `{envelope}`
     */
    fun writeMessages(surfaceView: A2UISurfaceView, jsonArrayOrObject: String) {
        val trimmed = jsonArrayOrObject.trimStart()
        if (trimmed.isEmpty()) return

        when (trimmed.first()) {
            '[' -> {
                val array = try {
                    JSONArray(jsonArrayOrObject)
                } catch (e: Exception) {
                    Log.e(TAG, "writeMessages: failed to parse JSON array: ${e.message}", e)
                    return
                }
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i)
                    if (obj == null) {
                        Log.e(
                            TAG,
                            "writeMessages: item[$i] is not a JSON object; skipped. raw=${array.opt(i)}"
                        )
                        continue
                    }
                    surfaceView.writeStream(obj.toString())
                }
            }
            '{' -> {
                // Validate it parses as an object; still forward the original string on success
                try {
                    JSONObject(jsonArrayOrObject)
                } catch (e: Exception) {
                    Log.e(TAG, "writeMessages: failed to parse JSON object: ${e.message}", e)
                    return
                }
                surfaceView.writeStream(jsonArrayOrObject)
            }
            else -> {
                Log.e(
                    TAG,
                    "writeMessages: input is neither a JSON object nor an array; input dropped."
                )
            }
        }
    }
}
