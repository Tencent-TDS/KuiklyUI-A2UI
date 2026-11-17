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

import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BACKGROUND_COLOR
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BORDER_RAW
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.CARD_DEFAULT_BOX_SHADOW_RAW
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.clearDefaultsIfGradient
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.hexToKuiklyColor
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.normalizeColorFieldsInPlace
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.normalizeHexInShorthand
import com.tencent.kuikly.a2ui.adapter.A2UIColorSupport.withNormalizedColors
import com.tencent.kuikly.a2ui.adapter.A2UIDataPath.parsePathSegments
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.addThousandsSeparator
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.coerceToBool
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.coerceToInt
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.doubleToPlainString
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.findMatchingBrace
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.formatDateString
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.formatDouble
import com.tencent.kuikly.a2ui.adapter.A2UIValueFormatting.splitArguments

import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion

internal class A2UIEvaluationWorker(
    private val context: A2UIEvaluationContext,
    private val decoder: A2UIProtocolDecoder,
    internal val customFunctionInvoker: ((String, String, String) -> Any?)?,
) {
    internal val pathReads = linkedSetOf<String>()
    internal val functionReads = linkedSetOf<String>()
    internal val diagnostics = mutableListOf<String>()
    private fun reportDiagnostic(message: String) {
        diagnostics.add(message)
        println(message)
    }
    private val _protocolVersion get() = decoder.protocolVersion
    private val currentCatalogId get() = context.catalogId
    private val currentTemplateIndex get() = context.index
    private fun readPathKey(map: Map<*, *>) = decoder.readPathKey(map)
    private fun readCallKey(map: Map<*, *>) = decoder.readCallKey(map)
    private fun resolveNestedDataPath(key: String) = A2UIDataPath.resolveNestedDataPath(context.data, key)
    private fun getRootDataKey(key: String) = A2UIDataPath.getRootDataKey(key)
    private fun readMixinKey(map: Map<*, *>, key: String) = decoder.readMixinKey(map, key)
    private val strictScope get() = _protocolVersion == A2UIProtocolVersion.V1_0 && decoder.strictV10

    private fun resolveAbsoluteInScope(key: String, item: Map<String, Any?>): Any? {
        val absolute = resolveNestedDataPath(key)
        if (absolute != null || strictScope) return absolute ?: ""
        return if (item.isNotEmpty()) resolveRelativePath(key, item) else ""
    }

    private fun evaluateIndex(call: Map<*, *>, item: Map<String, Any?>? = null): String {
        val index = currentTemplateIndex ?: run {
            reportDiagnostic("[A2UI] warn: v1.0 @index evaluated outside any ChildList scope; returning empty string.")
            return ""
        }
        val rawOffset = (call["args"] as? Map<*, *>)?.get("offset") ?: return index.toString()
        val resolved = if (item != null) resolveValueWithScope(rawOffset, item) else resolveDynamicArg(rawOffset)
        val offset = when (resolved) {
            is Number -> resolved.toDouble()
            is String -> resolved.toDoubleOrNull()
            else -> null
        }
        if (offset == null || !offset.isFinite()) {
            reportDiagnostic("[A2UI] warn: v1.0 @index offset must resolve to a finite number; returning empty string.")
            return ""
        }
        return doubleToPlainString(index.toDouble() + offset).removeSuffix(".0")
    }
    internal fun resolveActionValue(obj: Any?): Any? {
        return when (obj) {
            is Map<*, *> -> {
                val path = readPathKey(obj)
                if (path != null && obj.size == 1) {
                    return resolveNestedDataPath(path.removePrefix("/"))
                }
                val call = readCallKey(obj)
                if (call != null) {
                    val evaluated = evaluateFunctionCall(obj)
                    if (evaluated != null) {
                        return evaluated
                    }
                    // Unsupported call: keep original map so host can decide.
                }
                obj.entries.associate { (k, v) -> k.toString() to resolveActionValue(v) }
            }
            is List<*> -> obj.map { resolveActionValue(it) }
            else -> obj
        }
    }

    internal fun resolveValue(value: Any?, tracked: Boolean = false): Any? {
        if (value == null) return null

        // Handle A2UIValue sealed class (type-safe path/function references)
        if (value is A2UIValue) {
            return when (value) {
                is A2UIValue.Literal -> value.value
                is A2UIValue.PathBinding -> {
                    val key = value.key
                    // Register path dependency for re-evaluation on data update
                    // Use root key so nested paths like "stat1/value" trigger on "stat1" changes
                    if (tracked) {
                        pathReads.add(getRootDataKey(key))
                    }
                    // Return empty string if not yet resolved (not the path string itself,
                    // which would be rendered as text content and cause incorrect measurement)
                    resolveNestedDataPath(key) ?: ""
                }
                is A2UIValue.IconPathBinding -> {
                    // Data-bound Icon `name` in image mode: register a dependency on the
                    // underlying key so a data update re-renders this Icon, then translate
                    // the resolved name through A2UIIconRegistry (with fallback URI).
                    val key = value.binding.key
                    if (tracked) {
                        pathReads.add(getRootDataKey(key))
                    }
                    val name = resolveNestedDataPath(key)?.toString()
                    A2UIIconRegistry.resolveIconUriOrFallback(name)
                }
                is A2UIValue.FunctionCall -> {
                    // Register dependencies for this function call
                    if (tracked) {
                        collectFunctionDependencies( value.definition)
                    }
                    // Try to evaluate the function
                    evaluateFunctionCall(value.definition) ?: ""
                }
            }
        }

        // Legacy fallback for raw values not wrapped in A2UIValue
        if (value is String) return value
        if (value is Map<*, *>) {
            // Data binding path reference: {"path":"/key"} (v0.9)
            //                        or {"@path":"/key"} (v1.0)
            val path = readPathKey(value)
            if (path != null) {
                val key = path.removePrefix("/")
                if (tracked) {
                    pathReads.add(getRootDataKey(key))
                }
                return resolveNestedDataPath(key) ?: ""
            }
            // FunctionCall: bare `call` in v0.9, `@call` in v1.0.
            val call = readCallKey(value)
            if (call != null) {
                if (tracked) {
                    collectFunctionDependencies( value)
                }
                return evaluateFunctionCall(value) ?: ""
            }
        }
        return value
    }

    /**
     * Register data model dependencies for a function call.
     * When any of the referenced data keys change, the component will be re-evaluated.
     */
    internal fun collectFunctionDependencies(funcCall: Map<*, *>) {
        val paths = extractDataPaths(funcCall)
        for (path in paths) {
            val key = path.removePrefix("/")
            functionReads.add(key)
            // Also register under the ROOT key so that updates keyed on the root object
            // (e.g. a ChoicePicker writing "event/location", re-evaluated via getRootDataKey
            // = "event") still re-evaluate functions that read a deeper path such as
            // "event/location/0". Without this, a formatString over an array element would
            // not refresh when the array changes.
            val rootKey = getRootDataKey(key)
            if (rootKey != key) {
                functionReads.add(rootKey)
            }
        }
    }

    /**
     * Extract all data path references from a function call (recursively).
     */
    internal fun extractDataPaths(obj: Any?): Set<String> {
        val paths = mutableSetOf<String>()
        when (obj) {
            is String -> {
                // Extract all ${...} interpolation references from strings, handling nesting
                extractPathsFromTemplate(obj, paths)
            }
            is Map<*, *> -> {
                // Check if this map is a DataBinding reference
                // (bare `path` in v0.9, `@path` in v1.0 — see readPathKey).
                val path = readPathKey(obj)
                if (path != null) {
                    paths.add(path)
                }
                // Recurse into all values
                for ((_, v) in obj) {
                    paths.addAll(extractDataPaths(v))
                }
            }
            is List<*> -> {
                for (item in obj) {
                    paths.addAll(extractDataPaths(item))
                }
            }
        }
        return paths
    }

    /**
     * Extract all data path references from a template string with nested ${...} support.
     */
    internal fun extractPathsFromTemplate(template: String, paths: MutableSet<String>) {
        var i = 0
        while (i < template.length) {
            if (i < template.length - 1 && template[i] == '$' && template[i + 1] == '{') {
                val exprStart = i + 2
                val exprEnd = findMatchingBrace(template, exprStart)
                if (exprEnd == -1) {
                    i++
                } else {
                    val expr = template.substring(exprStart, exprEnd)
                    extractPathsFromExpr(expr, paths)
                    i = exprEnd + 1
                }
            } else {
                i++
            }
        }
    }

    /**
     * Extract data paths from a single interpolation expression.
     * Recursively handles nested ${...} within function call arguments.
     */
    internal fun extractPathsFromExpr(expr: String, paths: MutableSet<String>) {
        // Check if it's a function call: funcName(...)
        val funcMatch = Regex("(?s)^([a-zA-Z_][a-zA-Z0-9_]*)\\((.*)\\)$").find(expr)
        if (funcMatch != null) {
            // Recursively extract paths from the function arguments
            val argsStr = funcMatch.groupValues[2]
            extractPathsFromTemplate(argsStr, paths)
        } else {
            // It's a path reference
            if (expr.startsWith("/")) {
                paths.add(expr)
            } else {
                paths.add("/$expr")
            }
        }
    }

    /**
     * Evaluate a function call against the current data model.
     * Supports: formatDate, formatNumber, formatCurrency, formatString, pluralize.
     */
    internal fun evaluateFunctionCall(funcCall: Map<*, *>): String? {
        val call = readCallKey(funcCall) ?: return null

        if (call == "@index") return evaluateIndex(funcCall)

        @Suppress("UNCHECKED_CAST")
        val args = funcCall["args"] as? Map<String, Any?> ?: return null

        // PR-8: v1.0 FunctionCall sites may carry a `catalogId` field
        // (bare — NOT `@catalogId` — because it lives INSIDE the
        // FunctionCall envelope rather than on a component mixin, per the
        // v1.0 evolution_guide.md §"Reserved @ prefix" clarification).
        // Passing it to the custom-function lookup lets same-named
        // functions from different catalogs coexist in the same process.
        val fcCatalogId = funcCall["catalogId"] as? String ?: currentCatalogId

        return when (call) {
            "formatDate" -> evaluateFormatDate(args)
            "formatNumber" -> evaluateFormatNumber(args)
            "formatCurrency" -> evaluateFormatCurrency(args)
            "formatString" -> evaluateFormatString(args)
            "pluralize" -> evaluatePluralize(args)
            else -> evaluateCustomFunctionAsString(call, args, fcCatalogId)
        }
    }

    /**
     * Resolve a dynamic value argument (could be literal, path reference, or nested function call).
     */
    internal fun resolveDynamicArg(arg: Any?): Any? {
        if (arg == null) return null
        if (arg is String) return arg
        if (arg is Number) return arg
        if (arg is Boolean) return arg
        if (arg is Map<*, *>) {
            // DataBinding reference: bare `path` in v0.9, `@path` in v1.0.
            val path = readPathKey(arg)
            if (path != null) {
                val key = path.removePrefix("/")
                return resolveNestedDataPath(key)
            }
            // Nested FunctionCall: bare `call` in v0.9, `@call` in v1.0.
            val call = readCallKey(arg)
            if (call != null) {
                return evaluateFunctionCall(arg)
            }
        }
        return arg.toString()
    }

    /**
     * Evaluate formatDate function.
     * Per catalog: formats a timestamp into a string using a Unicode TR35 pattern.
     * Args: value (date string), format (pattern string)
     */
    internal fun evaluateFormatDate(args: Map<String, Any?>): String? {
        val valueArg = resolveDynamicArg(args["value"]) ?: return null
        val formatArg = resolveDynamicArg(args["format"]) as? String ?: return null
        val dateStr = valueArg.toString()

        if (dateStr.isEmpty()) return null

        // Parse ISO 8601 date/time string and format according to pattern
        return formatDateString(dateStr, formatArg)
    }

    /**
     * Evaluate formatNumber function.
     *
     * Per A2UI v0.9 basic catalog, `formatNumber` accepts:
     * - value (DynamicNumber, required)
     * - decimals (DynamicNumber, optional): digits after decimal point (with rounding).
     * - grouping (DynamicBoolean, optional, default true): whether to add thousands separator.
     *
     * Notes:
     * - Inline function calls (from formatString) may pass args as strings, so we accept
     *   "3" -> Int and "true"/"false" -> Boolean here.
     * - `precision` is kept as a legacy alias for backward compatibility.
     */
    internal fun evaluateFormatNumber(args: Map<String, Any?>): String? {
        val value = resolveDynamicArg(args["value"]) ?: return null
        val num = when (value) {
            is Number -> value.toDouble()
            // Robust parse: nested calls may pass a formatted string with thousands
            // separators like "1,234.50". Strip commas before parsing.
            is String -> value.replace(",", "").toDoubleOrNull() ?: return value
            else -> return value.toString()
        }
        val decimals = coerceToInt(resolveDynamicArg(args["decimals"]))
            ?: coerceToInt(resolveDynamicArg(args["precision"])) // legacy alias
        val grouping = coerceToBool(resolveDynamicArg(args["grouping"])) ?: true

        val formatted = if (decimals != null) {
            // Explicit decimals: always round to that number of digits (including 0.00 for zero).
            formatDouble(num, decimals)
        } else {
            // No decimals specified: keep integers as int, non-integers in plain form.
            if (num == num.toLong().toDouble()) {
                num.toLong().toString()
            } else {
                doubleToPlainString(num)
            }
        }
        return if (grouping) addThousandsSeparator(formatted) else formatted
    }

    /**
     * Evaluate formatCurrency function.
     */
    internal fun evaluateFormatCurrency(args: Map<String, Any?>): String? {
        val value = resolveDynamicArg(args["value"]) ?: return null
        val currency = resolveDynamicArg(args["currency"]) as? String ?: "USD"
        val num = when (value) {
            is Number -> value.toDouble()
            // Robust parse: nested calls (e.g. formatNumber) may pass a formatted string
            // with thousands separators like "1,234.50". Strip commas before parsing.
            is String -> value.replace(",", "").toDoubleOrNull() ?: return value
            else -> return value.toString()
        }
        val symbol = when (currency.uppercase()) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "CNY" -> "¥"
            else -> currency
        }
        val formatted = formatDouble(num, 2)
        // Add thousands separator using existing helper (handles decimal part internally)
        val withCommas = addThousandsSeparator(formatted)
        return "$symbol$withCommas"
    }

    /**
     * Evaluate formatString function (string interpolation).
     * @param itemData optional scope data for resolving relative paths in ChildList templates.
     */
    internal fun evaluateFormatString(args: Map<String, Any?>, itemData: Map<String, Any?>? = null): String? {
        val template = resolveDynamicArg(args["value"]) as? String ?: return null
        return resolveFormatStringTemplate(template, itemData)
    }

    /**
     * Resolve a formatString template with support for nested ${...} expressions.
     * Handles: ${/path}, ${relativePath}, ${funcName(arg: value)}, ${func(arg: ${/nested})}
     */
    internal fun resolveFormatStringTemplate(template: String, itemData: Map<String, Any?>? = null): String {
        val result = StringBuilder()
        var i = 0
        while (i < template.length) {
            // Check for escaped \${
            if (i < template.length - 1 && template[i] == '\\' && template[i + 1] == '$') {
                result.append('$')
                i += 2
                continue
            }
            // Check for ${ start
            if (i < template.length - 1 && template[i] == '$' && template[i + 1] == '{') {
                // Find the matching closing } considering nesting
                val exprStart = i + 2
                val exprEnd = findMatchingBrace(template, exprStart)
                if (exprEnd == -1) {
                    // No matching brace found, output as-is
                    result.append(template[i])
                    i++
                } else {
                    val expr = template.substring(exprStart, exprEnd)
                    result.append(evaluateInterpolationExpr(expr, itemData))
                    i = exprEnd + 1 // skip past the closing }
                }
            } else {
                result.append(template[i])
                i++
            }
        }
        return result.toString()
    }

    /**
     * Evaluate a single interpolation expression (the content between ${ and }).
     * Could be: a path like "/reviewCount", a relative path like "temp",
     * or a function call like "formatNumber(value: ${/reviewCount})".
     */
    internal fun evaluateInterpolationExpr(expr: String, itemData: Map<String, Any?>? = null): String {
        // Check if it's a function call: funcName(...)
        val funcMatch = Regex("(?s)^([a-zA-Z_][a-zA-Z0-9_]*)\\((.*)\\)$").find(expr)
        if (funcMatch != null) {
            val funcName = funcMatch.groupValues[1]
            val argsStr = funcMatch.groupValues[2]
            return evaluateInlineFunctionCall(funcName, argsStr, itemData)
        }

        // Otherwise it's a path reference
        val resolved = if (expr.startsWith("/")) {
            // Absolute path
            resolveNestedDataPath(expr.removePrefix("/"))
        } else {
            // Relative path - resolve from itemData first, then fallback to dataModel
            val fromItem = itemData?.let { resolveRelativePath(expr, it) }
            if (fromItem != null && fromItem != "") fromItem else resolveNestedDataPath(expr)
        }
        return stringifyForInterpolation(resolved)
    }

    /**
     * Convert a resolved value to its interpolation string form.
     * Per A2UI v0.9 protocol (docs/a2ui_protocol.md):
     *   Objects/Arrays: Stringified as JSON to ensure consistency across different client implementations.
     *   Primitives are stringified using their natural representation.
     */
    internal fun stringifyForInterpolation(value: Any?): String = when (value) {
        null -> ""
        is Map<*, *>, is List<*> -> toJsonString(value)
        else -> value.toString()
    }

    /**
     * Minimal JSON stringifier for Map/List/String/Number/Boolean/null.
     * Kept private to this adapter to avoid taking on a serialization dependency; the output
     * conforms to RFC 8259 for the value types actually seen in a2ui data models.
     */
    internal fun toJsonString(value: Any?): String {
        val sb = StringBuilder()
        appendJson(sb, value)
        return sb.toString()
    }

    internal fun appendJson(sb: StringBuilder, value: Any?) {
        when (value) {
            null -> sb.append("null")
            is Boolean -> sb.append(value.toString())
            is Number -> {
                // Represent whole doubles without ".0" tail; keep others plain (no scientific).
                val d = value.toDouble()
                if (value is Double || value is Float) {
                    if (!d.isFinite()) {
                        sb.append("null") // JSON has no Infinity/NaN
                    } else if (d == d.toLong().toDouble()) {
                        sb.append(d.toLong().toString())
                    } else {
                        sb.append(doubleToPlainString(d))
                    }
                } else {
                    sb.append(value.toString())
                }
            }
            is String -> appendJsonString(sb, value)
            is Map<*, *> -> {
                sb.append('{')
                var first = true
                for ((k, v) in value) {
                    if (!first) sb.append(',')
                    first = false
                    appendJsonString(sb, k?.toString() ?: "")
                    sb.append(':')
                    appendJson(sb, v)
                }
                sb.append('}')
            }
            is List<*> -> {
                sb.append('[')
                var first = true
                for (item in value) {
                    if (!first) sb.append(',')
                    first = false
                    appendJson(sb, item)
                }
                sb.append(']')
            }
            else -> appendJsonString(sb, value.toString())
        }
    }

    internal fun appendJsonString(sb: StringBuilder, s: String) {
        sb.append('"')
        for (c in s) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                else -> if (c.code < 0x20) {
                    sb.append("\\u")
                    val hex = c.code.toString(16)
                    repeat(4 - hex.length) { sb.append('0') }
                    sb.append(hex)
                } else {
                    sb.append(c)
                }
            }
        }
        sb.append('"')
    }

    /**
     * Evaluate an inline function call parsed from a formatString interpolation.
     * e.g. funcName = "formatNumber", argsStr = "value: ${/reviewCount}"
     */
    internal fun evaluateInlineFunctionCall(funcName: String, argsStr: String, itemData: Map<String, Any?>? = null): String {
        val args = parseInlineFunctionArgs(argsStr, itemData)
        return when (funcName) {
            "formatDate" -> evaluateFormatDate(args) ?: ""
            "formatNumber" -> evaluateFormatNumber(args) ?: ""
            "formatCurrency" -> evaluateFormatCurrency(args) ?: ""
            "formatString" -> evaluateFormatString(args, itemData) ?: ""
            "pluralize" -> evaluatePluralize(args) ?: ""
            else -> evaluateCustomFunctionAsString(
                funcName, args,
                if (_protocolVersion == A2UIProtocolVersion.V1_0) currentCatalogId else null,
            ) ?: ""
        }
    }

    /**
     * Parse inline function arguments from a string like "value: ${/reviewCount}, one: 'review', other: 'reviews'"
     * Returns a map of argument name to resolved value.
     */
    internal fun parseInlineFunctionArgs(argsStr: String, itemData: Map<String, Any?>? = null): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        // Split by commas, but respect nested ${...} and quoted strings
        val argPairs = splitArguments(argsStr)
        for (pair in argPairs) {
            val colonIdx = pair.indexOf(':')
            if (colonIdx == -1) continue
            val key = pair.substring(0, colonIdx).trim()
            val valueStr = pair.substring(colonIdx + 1).trim()
            result[key] = resolveInlineArgValue(valueStr, itemData)
        }
        return result
    }

    /**
     * Resolve an inline argument value. Could be:
     * - A quoted string: 'review' or "review"
     * - A ${...} expression: ${/reviewCount}
     * - A plain number or identifier
     */
    internal fun resolveInlineArgValue(valueStr: String, itemData: Map<String, Any?>? = null): Any? {
        val trimmed = valueStr.trim()
        // Quoted string (single or double quotes)
        if ((trimmed.startsWith("'") && trimmed.endsWith("'")) ||
            (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        // ${...} expression — resolve while preserving the original type.
        // For simple path references (e.g. ${/originalPrice}), return the raw
        // dataModel value (Number, Boolean, String, etc.) so downstream
        // serialization keeps the correct JSON type. For nested function calls
        // (e.g. ${funcName(...)}), delegate to evaluateInlineFunctionCall which
        // returns String (acceptable since the outer context is interpolation).
        if (trimmed.startsWith("\${") && trimmed.endsWith("}")) {
            val innerExpr = trimmed.substring(2, trimmed.length - 1)
            // Check if it's a function call: funcName(...)
            val funcMatch = Regex("(?s)^([a-zA-Z_][a-zA-Z0-9_]*)\\((.*)\\)$").find(innerExpr)
            if (funcMatch != null) {
                // Nested function call — returns String
                val funcName = funcMatch.groupValues[1]
                val argsStr = funcMatch.groupValues[2]
                return evaluateInlineFunctionCall(funcName, argsStr, itemData)
            }
            // Simple path reference — return the raw value preserving its type
            return if (innerExpr.startsWith("/")) {
                resolveNestedDataPath(innerExpr.removePrefix("/"))
            } else {
                val fromItem = itemData?.let { resolveRelativePath(innerExpr, it) }
                if (fromItem != null && fromItem != "") fromItem else resolveNestedDataPath(innerExpr)
            }
        }
        // Number
        trimmed.toDoubleOrNull()?.let { return it }
        trimmed.toIntOrNull()?.let { return it }
        // Plain string (could be a path reference)
        return trimmed
    }

    /**
     * Evaluate pluralize function.
     */
    internal fun evaluatePluralize(args: Map<String, Any?>): String? {
        val value = resolveDynamicArg(args["value"]) ?: return null
        val count = when (value) {
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
        // CLDR plural categories for English: one, other
        val one = resolveDynamicArg(args["one"]) as? String
        val other = resolveDynamicArg(args["other"]) as? String
        val zero = resolveDynamicArg(args["zero"]) as? String

        return when {
            count == 0 && zero != null -> zero
            count == 1 && one != null -> one
            other != null -> other
            else -> ""
        }
    }

    internal fun resolveValueWithScope(value: Any?, itemData: Map<String, Any?>): Any? {
        if (value == null) return null

        // Handle A2UIValue sealed class (type-safe path/function references)
        if (value is A2UIValue) {
            return when (value) {
                is A2UIValue.Literal -> value.value
                is A2UIValue.PathBinding -> {
                    if (value.isAbsolute) {
                        resolveAbsoluteInScope(value.key, itemData)
                    } else {
                        // Relative path - resolve from current item data
                        resolveRelativePath(value.path, itemData)
                    }
                }
                is A2UIValue.IconPathBinding -> {
                    // Data-bound Icon `name` in image mode inside a template scope.
                    // Resolve the underlying path using the same absolute/relative rules
                    // as PathBinding, then translate the name through A2UIIconRegistry.
                    val inner = value.binding
                    val nameRaw: Any? = if (inner.isAbsolute) {
                        resolveAbsoluteInScope(inner.key, itemData)
                    } else {
                        resolveRelativePath(inner.path, itemData)
                    }
                    A2UIIconRegistry.resolveIconUriOrFallback(nameRaw?.toString())
                }
                is A2UIValue.FunctionCall -> {
                    evaluateFunctionCallWithScope(value.definition, itemData)
                }
            }
        }

        // Legacy fallback for raw values not wrapped in A2UIValue
        if (value is String) return value
        if (value is Map<*, *>) {
            val pathRef = readPathKey(value)
            if (pathRef != null) {
                if (pathRef.startsWith("/")) {
                    return resolveAbsoluteInScope(pathRef.removePrefix("/"), itemData)
                } else {
                    // Relative path - resolve from current item data
                    return resolveRelativePath(pathRef, itemData)
                }
            }
            // FunctionCall: bare `call` in v0.9, `@call` in v1.0.
            val call = readCallKey(value)
            if (call != null) {
                return evaluateFunctionCallWithScope(value, itemData)
            }
        }
        return value
    }

    /**
     * Resolve a relative path against item data.
     * Supports nested paths like "address/city", or the extended flatten form
     * with `.`/`[N]` (e.g. `items[0].name`). Returns "" when any segment misses
     * so the caller can render an empty placeholder gracefully.
     */
    internal fun resolveRelativePath(path: String, itemData: Map<String, Any?>): Any? {
        val segments = parsePathSegments(path)
        if (segments.isEmpty()) return ""
        var current: Any? = itemData
        for (part in segments) {
            current = when (current) {
                is Map<*, *> -> current[part]
                is List<*> -> {
                    val idx = part.toIntOrNull()
                    if (idx != null && idx < current.size) current[idx] else null
                }
                else -> null
            }
            if (current == null) return ""
        }
        return current ?: ""
    }

    /**
     * Evaluate a function call with scope-aware argument resolution.
     */
    internal fun evaluateFunctionCallWithScope(funcCall: Map<*, *>, itemData: Map<String, Any?>): String? {
        val call = readCallKey(funcCall) ?: return null

        if (call == "@index") return evaluateIndex(funcCall, itemData)

        @Suppress("UNCHECKED_CAST")
        val args = funcCall["args"] as? Map<String, Any?> ?: return null

        // Resolve args with scope
        val resolvedArgs = mutableMapOf<String, Any?>()
        for ((key, argValue) in args) {
            resolvedArgs[key] = resolveValueWithScope(argValue, itemData)
        }

        // PR-8: v1.0 FunctionCall sites may carry a bare `catalogId`
        // field — same semantics as the non-scope evaluator above.
        val fcCatalogId = funcCall["catalogId"] as? String ?: currentCatalogId

        return when (call) {
            "formatDate" -> evaluateFormatDate(resolvedArgs)
            "formatNumber" -> evaluateFormatNumber(resolvedArgs)
            "formatCurrency" -> evaluateFormatCurrency(resolvedArgs)
            "formatString" -> evaluateFormatString(resolvedArgs, itemData)
            "pluralize" -> evaluatePluralize(resolvedArgs)
            else -> evaluateCustomFunctionAsString(call, resolvedArgs, fcCatalogId)
        }
    }
    internal fun evaluateDynamicBoolean(condition: Any?): Boolean {
        if (condition == null) return true

        // Literal boolean
        if (condition is Boolean) return condition

        // Number treated as boolean (0 = false, non-zero = true)
        if (condition is Number) return condition.toInt() != 0

        // Map: either DataBinding or FunctionCall
        if (condition is Map<*, *>) {
            // DataBinding: bare `path` in v0.9, `@path` in v1.0.
            val path = readPathKey(condition)
            if (path != null) {
                val key = path.removePrefix("/")
                val value = resolveNestedDataPath(key)
                return toBooleanValue(value)
            }

            // FunctionCall: bare `call` in v0.9, `@call` in v1.0.
            val call = readCallKey(condition)
            if (call != null) {
                @Suppress("UNCHECKED_CAST")
                val args = condition["args"] as? Map<String, Any?> ?: emptyMap()
                return evaluateCheckFunction(call, args)
            }
        }

        // String "true"/"false"
        if (condition is String) {
            return condition.lowercase() == "true"
        }

        return true // Default: pass
    }

    /**
     * Convert any value to a boolean for checks evaluation.
     */
    internal fun toBooleanValue(value: Any?): Boolean {
        return when (value) {
            null -> false
            is Boolean -> value
            is Number -> value.toInt() != 0
            is String -> value.isNotEmpty() && value.lowercase() != "false"
            is List<*> -> value.isNotEmpty()
            is Map<*, *> -> value.isNotEmpty()
            else -> true
        }
    }

    /**
     * Evaluate a built-in check function.
     * Supports: required, email, length, regex, numeric, and, or, not.
     */
    internal fun evaluateCheckFunction(call: String, args: Map<String, Any?>): Boolean {
        return when (call) {
            "required" -> evaluateRequired(args)
            "email" -> evaluateEmail(args)
            "length" -> evaluateLength(args)
            "regex" -> evaluateRegex(args)
            "numeric" -> evaluateNumeric(args)
            "and" -> evaluateAnd(args)
            "or" -> evaluateOr(args)
            "not" -> evaluateNot(args)
            else -> evaluateCustomFunctionAsBoolean(call, args)
        }
    }

    /**
     * Evaluate "required" check: value must not be null, undefined, or empty.
     *
     * Per A2UI v0.9, "required" means the user must have provided a value.
     * We interpret truthiness consistently with [toBooleanValue] so that the
     * check remains meaningful across all bindable types:
     *   - Boolean `false` counts as "not provided" (e.g. an unchecked CheckBox).
     *   - Number `0` counts as "not provided" (matches JS `Boolean(0)` semantics
     *     used across A2UI reference implementations).
     *   - Empty String / List / Map count as "not provided".
     * Otherwise the value is considered present and the check passes.
     */
    internal fun evaluateRequired(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])
        return when (value) {
            null -> false
            is Boolean -> value
            is Number -> value.toDouble() != 0.0
            is String -> value.isNotEmpty()
            is List<*> -> value.isNotEmpty()
            is Map<*, *> -> value.isNotEmpty()
            else -> true
        }
    }

    /**
     * Evaluate "email" check: value must be a valid email format.
     * Empty value passes email validation (emptiness should be checked by "required").
     */
    internal fun evaluateEmail(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])?.toString() ?: return true
        // Empty value passes email validation (emptiness should be checked by "required")
        if (value.isEmpty()) return true
        // Simple email regex validation
        return value.matches(Regex("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"))
    }

    /**
     * Evaluate "length" check: string length must be within min/max bounds.
     */
    internal fun evaluateLength(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])?.toString() ?: return false
        val min = (args["min"] as? Number)?.toInt()
        val max = (args["max"] as? Number)?.toInt()

        if (min != null && value.length < min) return false
        if (max != null && value.length > max) return false
        return true
    }

    /**
     * Evaluate "regex" check: value must match the given pattern.
     */
    internal fun evaluateRegex(args: Map<String, Any?>): Boolean {
        val value = resolveCheckArg(args["value"])?.toString() ?: return false
        // Empty value passes regex validation (emptiness should be checked by "required")
        if (value.isEmpty()) return true
        val pattern = args["pattern"] as? String ?: return true
        return try {
            value.matches(Regex(pattern))
        } catch (e: Exception) {
            true // Invalid regex: pass by default
        }
    }

    /**
     * Evaluate "numeric" check: value must be a number within min/max bounds.
     */
    internal fun evaluateNumeric(args: Map<String, Any?>): Boolean {
        val rawValue = resolveCheckArg(args["value"])
        val numValue = when (rawValue) {
            is Number -> rawValue.toDouble()
            is String -> rawValue.toDoubleOrNull() ?: return false
            else -> return false
        }
        val min = (args["min"] as? Number)?.toDouble()
        val max = (args["max"] as? Number)?.toDouble()

        if (min != null && numValue < min) return false
        if (max != null && numValue > max) return false
        return true
    }

    /**
     * Evaluate "and" check: all values must be true.
     */
    internal fun evaluateAnd(args: Map<String, Any?>): Boolean {
        val values = args["values"] as? List<*> ?: return true
        for (value in values) {
            if (!evaluateDynamicBoolean(value)) return false
        }
        return true
    }

    /**
     * Evaluate "or" check: at least one value must be true.
     */
    internal fun evaluateOr(args: Map<String, Any?>): Boolean {
        val values = args["values"] as? List<*> ?: return true
        for (value in values) {
            if (evaluateDynamicBoolean(value)) return true
        }
        return values.isEmpty() // Empty list: pass
    }

    /**
     * Evaluate "not" check: the value must be false.
     */
    internal fun evaluateNot(args: Map<String, Any?>): Boolean {
        val value = args["value"] ?: args["values"]
        if (value is List<*> && value.isNotEmpty()) {
            return !evaluateDynamicBoolean(value[0])
        }
        return !evaluateDynamicBoolean(value)
    }

    /**
     * Resolve a check function argument value.
     * Handles path references, nested function calls, and literal values.
     */
    internal fun resolveCheckArg(arg: Any?): Any? {
        if (arg == null) return null
        if (arg is String) return arg
        if (arg is Number) return arg
        if (arg is Boolean) return arg
        if (arg is Map<*, *>) {
            // DataBinding reference: bare `path` in v0.9, `@path` in v1.0.
            val path = readPathKey(arg)
            if (path != null) {
                val key = path.removePrefix("/")
                return resolveNestedDataPath(key)
            }
            // Nested FunctionCall: bare `call` in v0.9, `@call` in v1.0.
            val call = readCallKey(arg)
            if (call != null) {
                @Suppress("UNCHECKED_CAST")
                val nestedArgs = arg["args"] as? Map<String, Any?> ?: emptyMap()
                // For boolean-returning functions, return the boolean result
                return evaluateCheckFunction(call, nestedArgs)
            }
        }
        return arg
    }
    internal fun evaluateCustomFunctionAsString(
        call: String,
        args: Map<String, Any?>,
        catalogId: String? = null
    ): String? {
        val descriptor = com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry.lookup(call, catalogId)
        if (descriptor == null) {
            return null
        }
        // VOID functions are only valid inside `action.functionCall`, never as
        // expressions — evaluating them here would be a semantic error but we
        // simply return null to keep the UI from crashing on malformed inputs.
        if (descriptor.returnType == com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.VOID) {
            return null
        }

        val invoker = customFunctionInvoker
        if (invoker == null) {
            return null
        }
        // Resolve dynamic arguments (path references, nested function calls)
        // before serializing to JSON — same as built-in functions do via
        // resolveDynamicArg.
        val resolvedArgs = resolveCustomFunctionArgs(args)
        val argsJson = customArgsToJsonString(resolvedArgs)
        val raw = try {
            invoker(call, argsJson, descriptor.toSpec())
        } catch (_: Exception) {
            null
        }
        if (raw == null) {
            return null
        }

        val coerced = coerceCustomReturnToString(raw, descriptor.returnType)
        return coerced
    }

    /**
     * Fallback for [evaluateCheckFunction]: try to evaluate `call(args)` as a
     * user-registered custom check (BOOLEAN-returning) function.
     *
     * Unknown / mistyped custom functions default to `true` to preserve the
     * existing A2UI protocol behaviour ("unknown check ⇒ pass"), matching
     * how the built-in path handles unrecognized names.
     */
    internal fun evaluateCustomFunctionAsBoolean(
        call: String,
        args: Map<String, Any?>,
        catalogId: String? = null
    ): Boolean {
        val descriptor = com.tencent.kuikly.a2ui.function.A2UICustomFunctionRegistry.lookup(call, catalogId)
            ?: return true // Unknown function: pass by default (protocol behaviour)
        if (descriptor.returnType != com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.BOOLEAN) return true

        val invoker = customFunctionInvoker ?: return true
        // Resolve dynamic arguments (path references, nested function calls)
        val resolvedArgs = resolveCustomFunctionArgs(args)
        val argsJson = customArgsToJsonString(resolvedArgs)
        val raw = try {
            invoker(call, argsJson, descriptor.toSpec())
        } catch (e: Exception) {
            null
        } ?: return true

        return coerceCustomReturnToBoolean(raw)
    }

    /**
     * Resolve all dynamic arguments in a custom function's args map.
     * Each value may be:
     *   - A literal (String, Number, Boolean) → kept as-is
     *   - A path reference: {"path": "/key"} → resolved from dataModel
     *   - A nested function call: {"call": "...", "args": {...}} → recursively evaluated
     *
     * This mirrors what built-in functions do via [resolveDynamicArg] on each
     * individual argument.
     */
    internal fun resolveCustomFunctionArgs(args: Map<String, Any?>): Map<String, Any?> {
        val resolved = mutableMapOf<String, Any?>()
        for ((key, value) in args) {
            resolved[key] = resolveDynamicArg(value)
        }
        return resolved
    }

    /**
     * Encode a resolved argument map into a JSON string that is fed to the
     * native invoker. Values are already resolved (path references and nested
     * function calls have been reduced to concrete values by the caller), so
     * a straight JSON encoding is enough. We reuse Kuikly's JSONObject.
     */
    internal fun customArgsToJsonString(args: Map<String, Any?>): String {
        val obj = com.tencent.kuikly.core.nvi.serialization.json.JSONObject()
        for ((k, v) in args) {
            when (v) {
                null -> { /* JSONObject drops null keys — leave key absent */ }
                is Boolean -> obj.put(k, v)
                is Int -> obj.put(k, v)
                is Long -> obj.put(k, v)
                is Float -> obj.put(k, v.toDouble())
                is Double -> obj.put(k, v)
                is Number -> obj.put(k, v.toDouble())
                is String -> obj.put(k, v)
                is Map<*, *> -> obj.put(k, mapToJsonObject(v))
                is List<*> -> obj.put(k, listToJsonArray(v))
                else -> obj.put(k, v.toString())
            }
        }
        return obj.toString()
    }

    internal fun mapToJsonObject(m: Map<*, *>): com.tencent.kuikly.core.nvi.serialization.json.JSONObject {
        val o = com.tencent.kuikly.core.nvi.serialization.json.JSONObject()
        for ((k, v) in m) {
            val key = k?.toString() ?: continue
            when (v) {
                null -> {}
                is Boolean -> o.put(key, v)
                is Number -> o.put(key, v.toDouble())
                is String -> o.put(key, v)
                is Map<*, *> -> o.put(key, mapToJsonObject(v))
                is List<*> -> o.put(key, listToJsonArray(v))
                else -> o.put(key, v.toString())
            }
        }
        return o
    }

    internal fun listToJsonArray(l: List<*>): com.tencent.kuikly.core.nvi.serialization.json.JSONArray {
        val a = com.tencent.kuikly.core.nvi.serialization.json.JSONArray()
        for (v in l) {
            when (v) {
                null -> a.put(null)
                is Boolean -> a.put(v)
                is Number -> a.put(v.toDouble())
                is String -> a.put(v)
                is Map<*, *> -> a.put(mapToJsonObject(v))
                is List<*> -> a.put(listToJsonArray(v))
                else -> a.put(v.toString())
            }
        }
        return a
    }

    /**
     * Coerce the raw value returned by the native invoker into a String, based
     * on the declared A2UI return type. The native side is free to hand us
     * back the closest native scalar (Boolean/Number/String) or a JSON string
     * for ARRAY.
     */
    internal fun coerceCustomReturnToString(
        raw: Any,
        returnType: com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType
    ): String {
        return when (returnType) {
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.STRING -> raw.toString()
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.BOOLEAN ->
                if (coerceCustomReturnToBoolean(raw)) "true" else "false"
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.NUMBER -> when (raw) {
                is Number -> {
                    val d = raw.toDouble()
                    // Preserve integer look for whole numbers (e.g. 3 -> "3"), match
                    // formatNumber's default style so downstream text bindings render
                    // consistently.
                    if (d == d.toLong().toDouble()) d.toLong().toString() else d.toString()
                }
                // On Kotlin/Native (iOS), NSNumber(value: 0.0) may be bridged as
                // Boolean(false) and NSNumber(value: 1.0) as Boolean(true). When the
                // declared return type is NUMBER, coerce booleans back to numeric strings.
                is Boolean -> if (raw) "1" else "0"
                is String -> {
                    // Try to normalize numeric strings (e.g. "139.3" stays as-is,
                    // but "true"/"false" from mis-bridging gets caught above).
                    raw
                }
                else -> raw.toString()
            }
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.ARRAY,
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.OBJECT -> raw.toString()
            com.tencent.kuikly.a2ui.function.A2UIFunctionReturnType.VOID -> ""
        }
    }

    /**
     * Coerce the raw value returned by the native invoker into a Boolean.
     * Accepts native Booleans directly and treats the strings "true"/"false"
     * (case-insensitive) as their obvious values; everything else follows a
     * relaxed "truthy" rule consistent with [toBooleanValue] elsewhere in this
     * file.
     */
    internal fun coerceCustomReturnToBoolean(raw: Any): Boolean {
        return when (raw) {
            is Boolean -> raw
            is Number -> raw.toDouble() != 0.0
            is String -> when (raw.lowercase()) {
                "true", "1", "yes" -> true
                "false", "0", "no", "" -> false
                else -> true
            }
            else -> true
        }
    }
}

internal data class A2UIEvaluationContext(
    val data: Map<String, Any?>,
    val item: Map<String, Any?>? = null,
    val index: Int? = null,
    val catalogId: String? = null,
)

internal data class A2UIEvaluationResult(
    val value: Any?,
    val paths: Set<String>,
    val functions: Set<String>,
    val diagnostics: List<String>,
)

internal class A2UIValueEngine(private val decoder: A2UIProtocolDecoder) {
    var customFunctionInvoker: ((String, String, String) -> Any?)? = null

    fun evaluate(value: Any?, context: A2UIEvaluationContext, tracked: Boolean): A2UIEvaluationResult {
        val worker = A2UIEvaluationWorker(context, decoder, customFunctionInvoker)
        val resolved = if (context.item != null) {
            worker.resolveValueWithScope(value, context.item)
        } else {
            worker.resolveValue(value, tracked)
        }
        return A2UIEvaluationResult(resolved, worker.pathReads.toSet(), worker.functionReads.toSet(), worker.diagnostics.toList())
    }

    fun check(condition: Any?, context: A2UIEvaluationContext): Boolean =
        A2UIEvaluationWorker(context, decoder, customFunctionInvoker).evaluateDynamicBoolean(condition)

    fun function(call: Map<*, *>, context: A2UIEvaluationContext): String? =
        A2UIEvaluationWorker(context, decoder, customFunctionInvoker).evaluateFunctionCall(call)

    fun action(action: Any?, context: A2UIEvaluationContext): Any? =
        A2UIEvaluationWorker(context, decoder, customFunctionInvoker).resolveActionValue(action)

    fun paths(expression: Any?): Set<String> = A2UIEvaluationWorker(
        A2UIEvaluationContext(emptyMap()), decoder, null,
    ).extractDataPaths(expression)
}
