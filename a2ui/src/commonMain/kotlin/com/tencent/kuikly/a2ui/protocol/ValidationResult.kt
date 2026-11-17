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

package com.tencent.kuikly.a2ui.protocol

/**
 * Structured outcome of a component-level validation run.
 *
 * Both v0.9 and v1.0 define per-component `checks` arrays whose rules are
 * evaluated top-down; the first failing rule's `message` becomes the user
 * visible error. In v0.9 the renderer propagated this as a bare nullable
 * [String]. v1.0 (see `evolution_guide.md` §"Validation result") formalises
 * the outcome as an object so renderers can:
 *
 *   * distinguish "no rules registered" from "all rules passed"
 *     (the latter is the primary driver for enabling a submit button),
 *   * surface the identifier of the failing rule for analytics /
 *     accessibility announcements,
 *   * and later attach severity / category metadata without another
 *     wire-break.
 *
 * This Kotlin type intentionally stays minimal — the only field that MUST
 * be read by existing call-sites is [errorMessage], which keeps the v0.9
 * semantics intact (null == pass, non-null == fail with user-facing text).
 * The remaining fields are informational and safe to ignore on the v0.9
 * code path (they are populated with `null` there).
 *
 * This class is immutable by construction. Build instances via the
 * [valid] / [failure] factories rather than invoking the primary
 * constructor directly, so new optional fields can be added without
 * breaking existing call-sites.
 */
data class ValidationResult(
    /** `true` iff every registered rule passed (or no rules were registered). */
    val isValid: Boolean,
    /**
     * User-facing error message of the first failing rule, or `null` when
     * [isValid] is `true`. Kept compatible with the v0.9 `String?` return
     * contract — a non-null value here always means "show this to the user".
     */
    val errorMessage: String?,
    /**
     * Protocol-level identifier of the first failing rule (e.g.
     * `"required"`, `"email"`, `"regex"`). `null` on success, and also
     * `null` on failure when the rule did not declare its `call` name
     * (e.g. a raw boolean condition).
     */
    val failedRuleName: String? = null,
) {
    companion object {
        /** Canonical "nothing to report" outcome. Shared singleton. */
        val VALID: ValidationResult = ValidationResult(
            isValid = true,
            errorMessage = null,
            failedRuleName = null,
        )

        /** Explicit pass — for symmetry with [failure]. */
        fun valid(): ValidationResult = VALID

        /**
         * Construct a failing [ValidationResult]. [message] is required
         * (callers should resolve the per-rule fallback text before calling
         * this factory). [ruleName] is optional for raw / inline rules.
         */
        fun failure(message: String, ruleName: String? = null): ValidationResult =
            ValidationResult(
                isValid = false,
                errorMessage = message,
                failedRuleName = ruleName,
            )
    }
}
