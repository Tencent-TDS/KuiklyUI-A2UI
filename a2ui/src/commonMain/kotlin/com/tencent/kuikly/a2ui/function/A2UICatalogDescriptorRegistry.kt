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

package com.tencent.kuikly.a2ui.function

/**
 * Describes the composition constraints a catalog imposes on a single
 * component. Mirrors the v1.0 `catalog_definition.json` fields:
 *
 *   * [allowedParents]: if non-null, the component MAY only be placed
 *     under one of the named parent component types. An empty list is
 *     treated the same as `null` (no constraint) to keep registration
 *     ergonomic.
 *   * [allowedChildren]: if non-null, the component MAY only contain
 *     children whose component type is in the list. Same empty-list
 *     ergonomics as above.
 *
 * Both lists match against the A2UI component type (e.g. `"Button"`,
 * `"A2UIWeb"`), NOT against Kuikly view names (`"KRView"`).
 *
 * The strings `"Surface"` and `"*"` are accepted in [allowedParents]:
 *   * `"Surface"` means "may be a top-level root under the surface".
 *     We model this as "no parent inside the batch" at validation time.
 *   * `"*"` is a wildcard that disables the parent constraint. Prefer
 *     `null` or an empty list when possible; `"*"` is there to keep
 *     catalog-side JSON forms translatable one-to-one.
 */
data class A2UIComponentConstraint(
    val allowedParents: List<String>? = null,
    val allowedChildren: List<String>? = null
) {
    companion object {
        const val ROOT_MARKER: String = "Surface"
        const val WILDCARD: String = "*"
    }

    /**
     * Normalise empty lists to `null`. Called at registration time so
     * downstream validation code can treat `null` as "no constraint"
     * without worrying about degenerate empty-list cases.
     */
    internal fun normalised(): A2UIComponentConstraint {
        val ap = allowedParents?.takeIf { it.isNotEmpty() }
        val ac = allowedChildren?.takeIf { it.isNotEmpty() }
        return if (ap === allowedParents && ac === allowedChildren) this
        else A2UIComponentConstraint(ap, ac)
    }
}

/**
 * Global registry of per-catalog component composition constraints used by
 * [com.tencent.kuikly.a2ui.adapter.A2UIProtocolAdapter.handleUpdateComponents]
 * to enforce v1.0 `allowedParents` / `allowedChildren`.
 *
 * Why a Kotlin-side registry instead of parsing the catalog JSON?
 *   * The catalog JSON lives next to the native platform (host apps ship
 *     their own copy under `assets/` on Android, bundle on iOS, rawfile on
 *     OHOS). The commonMain code deliberately has NO JSON-schema parser
 *     dependency to keep the KMP module lean.
 *   * Host apps already register custom components/functions at startup
 *     (see `A2UICustomViewRegistry.register(...)`); registering the
 *     corresponding `allowedParents` / `allowedChildren` constraint in
 *     the same code path is a natural fit.
 *
 * Catalogs that omit constraints (including the Kuikly-extended basic
 * catalogs shipped in `catalogs/kuikly_a2ui_catalog*.json`) incur no cost
 * and no behavioural change \u2014 the Adapter treats "no entry registered"
 * as "no constraint".
 *
 * Lookup semantics in the Adapter:
 *   1. If the component is tagged with a resolved `catalogId` and this
 *      registry has an entry for `(catalogId, componentType)`, use that
 *      entry.
 *   2. Otherwise, fall back to the catalogId-less entry (if any).
 *   3. If nothing is registered, the component is unconstrained.
 */
object A2UICatalogDescriptorRegistry {

    /** Keyed by `catalogId::componentType`; empty catalogId means "global". */
    private val constraints: MutableMap<String, A2UIComponentConstraint> = mutableMapOf()

    private fun keyOf(catalogId: String?, componentType: String): String =
        if (catalogId.isNullOrEmpty()) componentType else "$catalogId::$componentType"

    /**
     * Register (or replace) a composition constraint for a component type
     * optionally scoped to a specific [catalogId].
     *
     * Passing an empty constraint (both `allowedParents` and
     * `allowedChildren` null-or-empty) is idempotent and simply removes
     * any prior entry \u2014 this mirrors "catalog says nothing about this
     * component" so later lookups report "unconstrained".
     */
    fun register(
        componentType: String,
        constraint: A2UIComponentConstraint,
        catalogId: String? = null
    ) {
        if (componentType.isEmpty()) return
        val norm = constraint.normalised()
        val key = keyOf(catalogId, componentType)
        if (norm.allowedParents == null && norm.allowedChildren == null) {
            constraints.remove(key)
        } else {
            constraints[key] = norm
        }
    }

    /**
     * Remove a previously registered constraint. No-op if none was set.
     */
    fun unregister(componentType: String, catalogId: String? = null) {
        if (componentType.isEmpty()) return
        constraints.remove(keyOf(catalogId, componentType))
    }

    /**
     * Resolve a composition constraint for [componentType], optionally
     * scoped to [catalogId]. Catalog-qualified entries take precedence
     * over global ones (matching the lookup order of
     * [A2UICustomFunctionRegistry.lookup]). Returns `null` if the
     * component is unconstrained.
     */
    fun lookup(componentType: String, catalogId: String? = null): A2UIComponentConstraint? {
        if (componentType.isEmpty()) return null
        if (!catalogId.isNullOrEmpty()) {
            constraints[keyOf(catalogId, componentType)]?.let { return it }
        }
        return constraints[componentType]
    }

    /**
     * Snapshot of all registered constraints \u2014 mainly for diagnostics /
     * tests.
     */
    fun registeredKeys(): Set<String> = constraints.keys.toSet()

    /**
     * Clear every registered constraint. Primarily used by tests.
     */
    fun clearAll() {
        constraints.clear()
    }
}
