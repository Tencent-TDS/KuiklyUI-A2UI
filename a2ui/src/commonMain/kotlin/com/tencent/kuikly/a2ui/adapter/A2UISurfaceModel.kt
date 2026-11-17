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

internal enum class A2UIUpdateOrigin { WIRE, BINDING, CHOICE }

internal data class A2UIDataChange(
    val paths: Set<String>,
    val origin: A2UIUpdateOrigin,
)

/** Protocol facts for one active surface. No tag, mounted node or dependency table lives here. */
internal class A2UISurfaceModel {
    private val values = mutableMapOf<String, Any?>()
    private val originalDefinitions = mutableMapOf<String, Map<String, Any?>>()
    private val parents = mutableMapOf<String, String>()
    private var batchParents = mutableMapOf<String, String>()
    private val batchVariants = mutableMapOf<String, String>()
    private val catalogs = mutableMapOf<String, String>()

    val data: Map<String, Any?> get() = values
    val definitions: Map<String, Map<String, Any?>> get() = originalDefinitions
    val originalParents: Map<String, String> get() = parents
    val currentParents: Map<String, String> get() = batchParents
    val variants: Map<String, String> get() = batchVariants
    val componentCatalogs: Map<String, String> get() = catalogs

    var surfaceId: String = ""
    var catalogId: String? = null
    var sendDataModel: Boolean = false
    var primaryColor: String = "#00BFFF"

    fun rememberDefinition(id: String, definition: Map<String, Any?>) {
        originalDefinitions[id] = definition
    }

    fun rememberParent(child: String, parent: String) { parents[child] = parent }
    fun rememberVariant(id: String, variant: String) { batchVariants[id] = variant }
    fun rememberCatalog(id: String, catalog: String) { catalogs[id] = catalog }
    fun clearCatalogs() { catalogs.clear() }

    fun beginComponentBatch() {
        batchParents.clear()
        batchVariants.clear()
    }

    fun replaceBatchParents(newParents: Map<String, String>) {
        batchParents = newParents.toMutableMap()
    }

    fun read(path: String): Any? = A2UIDataPath.resolveNestedDataPath(values, path)

    fun write(path: String, value: Any?, origin: A2UIUpdateOrigin): A2UIDataChange {
        A2UIDataPath.setNestedDataPath(values, path, value)
        return A2UIDataChange(linkedSetOf(path), origin)
    }

    fun replaceData(value: Map<String, Any?>, origin: A2UIUpdateOrigin): A2UIDataChange {
        values.clear()
        values.putAll(value)
        return A2UIDataChange(value.keys.toSet(), origin)
    }

    fun clearData() { values.clear() }

    /** Preserve the existing reset scope; definition/parent retention is not changed by extraction. */
    fun reset() {
        values.clear()
        surfaceId = ""
        sendDataModel = false
        catalogId = null
        catalogs.clear()
    }
}
