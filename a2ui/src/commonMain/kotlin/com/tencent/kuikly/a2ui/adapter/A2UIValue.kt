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

package com.tencent.kuikly.a2ui.adapter

/**
 * Type-safe representation of dynamic values in A2UI protocol.
 *
 * Instead of using raw strings (where path references like "/key" are indistinguishable
 * from literal text that happens to start with "/"), this sealed class preserves the
 * semantic type information throughout the processing pipeline.
 *
 * This eliminates the fragile `startsWith("/")` heuristic and correctly handles:
 * - Relative paths (e.g., "playIcon" without "/" prefix)
 * - Literal text that starts with "/" (e.g., "/images/logo.png")
 * - Nested/deep paths (e.g., "/nested/deep/key")
 */
sealed class A2UIValue {
    /**
     * A literal value (string, number, etc.) that should be used as-is.
     * No data binding or function evaluation needed.
     */
    data class Literal(val value: Any) : A2UIValue()

    /**
     * A data binding path reference that should be resolved against the data model.
     * The path can be absolute (starting with "/") or relative.
     *
     * @param path The raw path string from the protocol (e.g., "/playIcon" or "title")
     * @param isAbsolute Whether the path is absolute (starts with "/") or relative
     */
    data class PathBinding(val path: String, val isAbsolute: Boolean = true) : A2UIValue() {
        /** The data model key (path without leading "/") */
        val key: String get() = if (isAbsolute) path.removePrefix("/") else path
    }

    /**
     * A function call that should be evaluated with the data model.
     * The definition map contains "call" (function name) and "args" (arguments).
     */
    data class FunctionCall(val definition: Map<*, *>) : A2UIValue()

    /**
     * A data-bound Icon name that must be resolved to an image URI via
     * `A2UIIconRegistry` before being applied to a `KRImageView.src` prop.
     *
     * Used when the host app has registered custom icon images (making the Icon
     * component render as `KRImageView` globally) AND the source A2UI JSON binds
     * the Icon `name` to a data path, e.g. `{"name": {"path": "/currentIcon"}}`.
     *
     * The adapter's value resolvers detect this subtype, resolve the underlying
     * [binding] to obtain the icon name string, then call
     * `A2UIIconRegistry.resolveIconUriOrFallback` to translate the name into a
     * concrete URI. This keeps the "name → URI" mapping out of the generic
     * path-binding resolver so unrelated string properties are never accidentally
     * looked up in the icon registry.
     */
    data class IconPathBinding(val binding: PathBinding) : A2UIValue()
}
