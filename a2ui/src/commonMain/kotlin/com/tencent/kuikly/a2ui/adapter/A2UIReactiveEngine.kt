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

internal data class A2UIImpactSet(
    val components: Set<String>,
    val checks: Set<String>,
)

/** Owns subscriptions, not data, expressions, tags or rendering. */
internal class A2UIReactiveEngine {
    private val functionReads = mutableMapOf<String, MutableSet<String>>()
    private val pathReads = mutableMapOf<String, MutableSet<String>>()
    private val checkReads = mutableMapOf<String, MutableSet<String>>()
    private val rules = mutableMapOf<String, List<Map<String, Any?>>>()

    fun functionDependents(key: String): Set<String> = functionReads[key]?.toSet() ?: emptySet()
    fun pathDependents(key: String): Set<String> = pathReads[key]?.toSet() ?: emptySet()
    fun checksDependents(key: String): Set<String> = checkReads[key]?.toSet() ?: emptySet()
    fun hasChecks(id: String): Boolean = rules.containsKey(id)
    fun checksFor(id: String): List<Map<String, Any?>> = rules[id] ?: emptyList()

    fun registerPath(id: String, key: String) {
        pathReads.getOrPut(key) { mutableSetOf() }.add(id)
    }

    fun registerFunction(id: String, key: String) {
        functionReads.getOrPut(key) { mutableSetOf() }.add(id)
    }

    fun registerChecks(id: String, checks: List<*>, paths: Set<String>) {
        val validRules = checks.filterIsInstance<Map<*, *>>().map {
            @Suppress("UNCHECKED_CAST")
            it as Map<String, Any?>
        }
        if (validRules.isEmpty()) return
        rules[id] = validRules
        for (path in paths) {
            checkReads.getOrPut(path.removePrefix("/")) { mutableSetOf() }.add(id)
        }
    }

    fun impact(change: A2UIDataChange, excludedPathComponents: Set<String> = emptySet()): A2UIImpactSet {
        val affected = linkedSetOf<String>()
        val checked = linkedSetOf<String>()
        for (key in change.paths) {
            val root = A2UIDataPath.getRootDataKey(key)
            val lookups = if (change.origin == A2UIUpdateOrigin.CHOICE) linkedSetOf(root)
                else linkedSetOf(key, root)
            for (lookup in lookups) {
                affected.addAll(functionDependents(lookup))
                affected.addAll(pathDependents(lookup).filterNot {
                    it.startsWith("__childlist__") || it in excludedPathComponents
                })
            }
            if (change.origin != A2UIUpdateOrigin.CHOICE) checked.addAll(checksDependents(key))
        }
        if (change.origin != A2UIUpdateOrigin.CHOICE) {
            checked.addAll(affected.filter(::hasChecks))
        }
        return A2UIImpactSet(affected, checked)
    }

    fun release(id: String) {
        functionReads.values.forEach { it.remove(id) }
        pathReads.values.forEach { it.remove(id) }
        checkReads.values.forEach { it.remove(id) }
        rules.remove(id)
    }

    /** Keep the legacy reset scope during structural migration. */
    fun reset() {
        rules.clear()
        checkReads.clear()
    }
}
