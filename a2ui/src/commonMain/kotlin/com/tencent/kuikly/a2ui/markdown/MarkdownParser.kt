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

package com.tencent.kuikly.a2ui.markdown

/**
 * A single styled run of text produced by [MarkdownParser].
 *
 * The parser flattens a Markdown string into a linear list of runs. Block
 * structure (headings, list items, paragraph breaks) is encoded as plain
 * newline runs and bullet prefixes, while inline emphasis is encoded via the
 * style flags. This maps directly onto Kuikly's `RichTextView` span model where
 * each span carries its own font/color/decoration.
 *
 * @property text the literal characters of this run (may be a line break)
 * @property bold whether the run is bold (**text** / __text__ / headings)
 * @property italic whether the run is italic (*text* / _text_)
 * @property strikethrough whether the run is struck through (~~text~~)
 * @property code whether the run is inline code (`text`)
 * @property headingLevel 1..6 for heading content, 0 otherwise
 * @property link the destination URL if this run is a Markdown link, else null
 */
data class MarkdownSpan(
    val text: String,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val strikethrough: Boolean = false,
    val code: Boolean = false,
    val headingLevel: Int = 0,
    val link: String? = null
)

/**
 * A small, dependency-free parser for the "simple Markdown" subset that the
 * A2UI v0.9 protocol specifies for `Text` content (see basic catalog
 * implementation guide: "Text should be rendered using a Markdown parser").
 *
 * Supported:
 * - Headings: `#` .. `######`
 * - Bold: `**text**` or `__text__`
 * - Italic: `*text*` or `_text_`
 * - Strikethrough: `~~text~~`
 * - Inline code: `` `text` ``
 * - Links: `[label](url)`
 * - Unordered lists: lines starting with `-`, `*`, or `+`
 * - Ordered lists: lines starting with `1.`, `2.` ...
 * - Paragraph breaks via blank lines / explicit `\n`
 *
 * Anything that is not recognized (including unmatched delimiters) is preserved
 * as literal text, matching the protocol's "gracefully fallback" guidance.
 */
object MarkdownParser {

    private const val BULLET = "•  "

    /**
     * Cheap pre-check used to decide whether a string is worth parsing as
     * Markdown at all. Plain text without any markers takes the fast path in
     * the renderer and is rendered as a single span.
     */
    fun containsMarkdown(src: String): Boolean {
        if (src.isEmpty()) return false
        // Heading / list markers at the start of any line.
        var atLineStart = true
        var i = 0
        while (i < src.length) {
            val c = src[i]
            if (atLineStart) {
                // skip leading spaces of the line
                if (c == ' ' || c == '\t') {
                    i++
                    continue
                }
                if (c == '#') return true
                if ((c == '-' || c == '*' || c == '+') && i + 1 < src.length && src[i + 1] == ' ') {
                    return true
                }
                if (c.isDigit()) {
                    // ordered list "<n>. "
                    var j = i
                    while (j < src.length && src[j].isDigit()) j++
                    if (j < src.length && src[j] == '.' && j + 1 < src.length && src[j + 1] == ' ') {
                        return true
                    }
                }
            }
            // Inline markers anywhere.
            when (c) {
                '*', '_', '`', '~' -> return true
                '[' -> if (src.indexOf("](", i) != -1) return true
            }
            atLineStart = c == '\n'
            i++
        }
        return false
    }

    /**
     * Parse [src] into a flat list of styled runs ready to be emitted as spans.
     */
    fun parse(src: String): List<MarkdownSpan> {
        val out = mutableListOf<MarkdownSpan>()
        val lines = src.split("\n")
        var emittedBlock = false
        for (rawLine in lines) {
            val line = rawLine.trimEnd()
            if (line.isBlank()) {
                // Blank line -> paragraph break. Collapse leading/trailing blanks.
                if (emittedBlock) out.add(MarkdownSpan("\n"))
                continue
            }
            if (emittedBlock) out.add(MarkdownSpan("\n"))
            emittedBlock = true
            appendBlockLine(line, out)
        }
        // Drop a trailing standalone newline for tidiness.
        if (out.isNotEmpty() && out.last().text == "\n") {
            out.removeAt(out.size - 1)
        }
        return out
    }

    private fun appendBlockLine(line: String, out: MutableList<MarkdownSpan>) {
        // Heading: one to six leading '#', then at least one space.
        var hashes = 0
        while (hashes < line.length && line[hashes] == '#') hashes++
        if (hashes in 1..6 && hashes < line.length && line[hashes] == ' ') {
            val content = line.substring(hashes + 1).trim()
            val runs = parseInline(content)
            for (r in runs) {
                out.add(r.copy(headingLevel = hashes, bold = true))
            }
            return
        }

        // Unordered list: '-', '*' or '+' followed by a space.
        val trimmed = line.trimStart()
        val indentRemoved = line.length - trimmed.length
        if (trimmed.length >= 2 && (trimmed[0] == '-' || trimmed[0] == '*' || trimmed[0] == '+') && trimmed[1] == ' ') {
            out.add(MarkdownSpan(BULLET))
            out.addAll(parseInline(trimmed.substring(2).trim()))
            return
        }

        // Ordered list: "<n>. ".
        if (trimmed.isNotEmpty() && trimmed[0].isDigit()) {
            var j = 0
            while (j < trimmed.length && trimmed[j].isDigit()) j++
            if (j < trimmed.length && trimmed[j] == '.' && j + 1 < trimmed.length && trimmed[j + 1] == ' ') {
                val marker = trimmed.substring(0, j + 1) + " "
                out.add(MarkdownSpan(marker))
                out.addAll(parseInline(trimmed.substring(j + 2).trim()))
                return
            }
        }

        // Plain paragraph line (preserve any leading indentation as spaces).
        if (indentRemoved > 0) out.add(MarkdownSpan(" ".repeat(indentRemoved)))
        out.addAll(parseInline(trimmed))
    }

    /**
     * Parse inline emphasis within a single line into styled runs. Unmatched
     * delimiters are emitted as literal characters.
     */
    private fun parseInline(s: String): List<MarkdownSpan> {
        val runs = mutableListOf<MarkdownSpan>()
        val buf = StringBuilder()
        var i = 0

        fun flush() {
            if (buf.isNotEmpty()) {
                runs.add(MarkdownSpan(buf.toString()))
                buf.clear()
            }
        }

        while (i < s.length) {
            val c = s[i]
            when {
                // Link: [label](url)
                c == '[' -> {
                    val close = s.indexOf(']', i + 1)
                    // The closing ']' must be immediately followed by "(...)".
                    if (close != -1 && close + 1 < s.length && s[close + 1] == '(') {
                        val urlEnd = s.indexOf(')', close + 2)
                        if (urlEnd != -1) {
                            val label = s.substring(i + 1, close)
                            val url = s.substring(close + 2, urlEnd)
                            flush()
                            // Inline emphasis inside link labels is uncommon; keep
                            // the label literal but carry the link + decoration.
                            runs.add(MarkdownSpan(label, link = url))
                            i = urlEnd + 1
                            continue
                        }
                    }
                    buf.append(c); i++
                }
                // Bold: ** or __
                (c == '*' || c == '_') && i + 1 < s.length && s[i + 1] == c -> {
                    val marker = "$c$c"
                    val end = s.indexOf(marker, i + 2)
                    if (end > i + 2 && isEmphasis(s, i, i + 2, end, end + 2, c)) {
                        flush()
                        runs.add(MarkdownSpan(s.substring(i + 2, end), bold = true))
                        i = end + 2
                    } else {
                        buf.append(c); i++
                    }
                }
                // Italic: * or _
                c == '*' || c == '_' -> {
                    val end = s.indexOf(c, i + 1)
                    if (end > i + 1 && isEmphasis(s, i, i + 1, end, end + 1, c)) {
                        flush()
                        runs.add(MarkdownSpan(s.substring(i + 1, end), italic = true))
                        i = end + 1
                    } else {
                        buf.append(c); i++
                    }
                }
                // Strikethrough: ~~
                c == '~' && i + 1 < s.length && s[i + 1] == '~' -> {
                    val end = s.indexOf("~~", i + 2)
                    if (end != -1 && end > i + 2) {
                        flush()
                        runs.add(MarkdownSpan(s.substring(i + 2, end), strikethrough = true))
                        i = end + 2
                    } else {
                        buf.append(c); i++
                    }
                }
                // Inline code: `code`
                c == '`' -> {
                    val end = s.indexOf('`', i + 1)
                    if (end != -1 && end > i + 1) {
                        flush()
                        runs.add(MarkdownSpan(s.substring(i + 1, end), code = true))
                        i = end + 1
                    } else {
                        buf.append(c); i++
                    }
                }
                else -> {
                    buf.append(c); i++
                }
            }
        }
        flush()
        return runs
    }

    /**
     * Validate an emphasis span to avoid common false positives such as
     * `snake_case`, arithmetic like `a * b`, or markers wrapping whitespace.
     *
     * @param openStart index of the opening delimiter
     * @param contentStart index just after the opening delimiter
     * @param contentEnd index of the closing delimiter (exclusive end of content)
     * @param closeEnd index just after the closing delimiter
     * @param marker the delimiter character ('*' or '_')
     */
    private fun isEmphasis(
        s: String,
        openStart: Int,
        contentStart: Int,
        contentEnd: Int,
        closeEnd: Int,
        marker: Char
    ): Boolean {
        if (contentEnd <= contentStart) return false
        // Content must not be flanked by whitespace (CommonMark left/right-flanking).
        if (s[contentStart].isWhitespace() || s[contentEnd - 1].isWhitespace()) return false
        // Underscores must sit at word boundaries so intra-word "_" stays literal.
        if (marker == '_') {
            val before = if (openStart == 0) ' ' else s[openStart - 1]
            val after = if (closeEnd >= s.length) ' ' else s[closeEnd]
            if (before.isLetterOrDigit() || after.isLetterOrDigit()) return false
        }
        return true
    }
}
