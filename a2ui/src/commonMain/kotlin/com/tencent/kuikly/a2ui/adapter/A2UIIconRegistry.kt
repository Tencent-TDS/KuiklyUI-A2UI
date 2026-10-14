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
 * Business-facing registry for custom Icon images.
 *
 * The A2UI `Icon` component ships with a built-in `ICON_MAP` that renders each icon
 * name as a unicode/emoji glyph via `KRRichTextView`. Some business scenarios need
 * to replace those glyphs with brand/product images.
 *
 * ## Semantics: all-or-nothing routing
 * The registry acts as a global switch:
 * - **Empty registry** → every A2UI `Icon` renders as a text glyph on `KRRichTextView`
 *   (default behavior, backwards compatible).
 * - **Non-empty registry** → every A2UI `Icon` renders as an image on `KRImageView`.
 *   Individual icons that are not registered fall back to the [FALLBACK_KEY] entry.
 *
 * This design keeps the Kuikly view type static for the entire lifetime of an Icon
 * node: it is decided once at registry-setup time and never changes based on runtime
 * data-binding values, so data-bound Icon names (`{"path": "/iconName"}`) are also
 * fully supported without any node type churn.
 *
 * ## Usage
 * ```
 * A2UIIconRegistry.registerIcons(mapOf(
 *     "home"     to "assets://a2ui_icons/home.png",
 *     "search"   to "assets://a2ui_icons/search.png",
 *     "settings" to "assets://a2ui_icons/settings.png",
 *     A2UIIconRegistry.FALLBACK_KEY to "assets://a2ui_icons/_fallback.png"
 * ))
 * ```
 * If a `FALLBACK_KEY` entry is not provided, unregistered icons resolve to the first
 * registered image (deterministic but arbitrary), then to an empty URI.
 *
 * ## Value contract
 * The values are image URIs understood by the host's `IKRImageAdapter`
 * implementation, e.g. `assets://...`, `file://...`, `https://...`, or a
 * `data:image/png;base64,...` URI.
 *
 * ## Keys
 * The keys are Icon `name` values as defined by the A2UI protocol, in either
 * camelCase (`arrowUpward`) or snake_case (`arrow_upward`); both forms are
 * accepted and normalized to camelCase internally.
 */
object A2UIIconRegistry {

    /**
     * Reserved icon name used to look up the fallback image when a runtime `name`
     * (typically arriving via data binding) is not registered. Business apps should
     * include this key when calling [registerIcons] so unregistered names have a
     * predictable rendering.
     */
    const val FALLBACK_KEY: String = "_fallback"

    private val customIcons = mutableMapOf<String, String>()

    /**
     * Register a batch of custom icon images. Existing entries with the same key
     * are overwritten.
     */
    fun registerIcons(icons: Map<String, String>) {
        for ((name, uri) in icons) {
            registerIcon(name, uri)
        }
    }

    /**
     * Register or overwrite a single custom icon image.
     * @param name The Icon `name` (either camelCase or snake_case), or [FALLBACK_KEY].
     * @param uri  Image URI understood by the host's IKRImageAdapter (assets/file/http/base64).
     */
    fun registerIcon(name: String, uri: String) {
        if (name.isEmpty() || uri.isEmpty()) return
        customIcons[normalizeIconName(name)] = uri
    }

    /**
     * Remove a single custom icon image. Safe to call for names that were never
     * registered. Used by the OHOS/iOS bridge layers to forward one-by-one
     * `unregisterA2UIIcon` calls from the business app into the Kotlin core.
     */
    fun unregisterIcon(name: String) {
        if (name.isEmpty()) return
        customIcons.remove(normalizeIconName(name))
    }

    /**
     * True when the host app has not registered any custom icons. In this state the
     * adapter keeps rendering Icons as text glyphs on `KRRichTextView`.
     */
    fun isEmpty(): Boolean = customIcons.isEmpty()

    /**
     * Look up the image URI for a given icon name.
     * @return the URI if the icon is registered, or `null` when the name is missing
     *         (callers should route to [getFallbackUri] in that case).
     */
    fun getIconUri(name: String?): String? {
        if (name.isNullOrEmpty()) return null
        return customIcons[normalizeIconName(name)]
    }

    /**
     * Resolve an icon URI with fallback semantics. Returns:
     * 1. The registered URI for [name] if present.
     * 2. The [FALLBACK_KEY] entry if the host registered one.
     * 3. An empty string, which downstream `KRImageView` will render as a blank slot
     *    (useful for stubbed / not-yet-loaded icon names).
     *
     * Never returns `null`, so the adapter never has to branch on the result.
     */
    fun resolveIconUriOrFallback(name: String?): String {
        val direct = getIconUri(name)
        if (direct != null) return direct
        val fallback = customIcons[FALLBACK_KEY]
        if (fallback != null) return fallback
        return ""
    }

    /** True when at least one custom icon has been registered under [name]. */
    fun hasIcon(name: String?): Boolean = getIconUri(name) != null

    /** Clear every registered custom icon. Intended for tests and hot-reload. */
    fun clear() {
        customIcons.clear()
    }

    /**
     * Convert snake_case icon names to camelCase to match the A2UI protocol enum.
     * Kept in sync with the private helper in [A2UIComponentMapper].
     * The special [FALLBACK_KEY] passes through unchanged.
     */
    private fun normalizeIconName(raw: String): String {
        if (raw == FALLBACK_KEY) return raw
        if (!raw.contains('_')) return raw
        val sb = StringBuilder(raw.length)
        var upperNext = false
        for (c in raw) {
            if (c == '_') {
                upperNext = true
            } else if (upperNext) {
                sb.append(c.uppercaseChar())
                upperNext = false
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }
}
