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

import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType

internal data class A2UICheckRegistration(val rules: List<Map<String, Any?>>, val paths: Set<String>)

internal data class A2UIProjectionPlan(
    val props: Map<String, Any?>,
    val pathDependencies: Map<String, Set<String>>,
    val functionDependencies: Map<String, Set<String>>,
    val definitions: Map<String, Map<String, Any?>>,
    val actions: Map<Int, Any?>,
    val disabledActions: Map<Int, Boolean>,
    val checks: Map<String, A2UICheckRegistration>,
    val initialValues: Map<String, Any?>,
)

internal class A2UIProjectionDraft {
    val pathDependencies = linkedMapOf<String, MutableSet<String>>()
    val functionDependencies = linkedMapOf<String, MutableSet<String>>()
    val definitions = linkedMapOf<String, Map<String, Any?>>()
    val actions = linkedMapOf<Int, Any?>()
    val disabledActions = linkedMapOf<Int, Boolean>()
    val checks = linkedMapOf<String, A2UICheckRegistration>()
    val initialValues = linkedMapOf<String, Any?>()

    fun finish(props: Map<String, Any?>) = A2UIProjectionPlan(
        props.toMap(), pathDependencies.mapValues { it.value.toSet() },
        functionDependencies.mapValues { it.value.toSet() }, definitions.toMap(),
        actions.toMap(), disabledActions.toMap(), checks.toMap(), initialValues.toMap(),
    )
}

internal fun A2UIComponentProjection.planProperties(
    componentType: String,
    definition: Map<String, Any?>,
    kuikly: Boolean = false,
    instanceId: String? = null,
    item: Map<String, Any?>? = null,
    scopePath: String = "",
): A2UIProjectionPlan {
    val previous = pendingProperties
    val draft = A2UIProjectionDraft()
    pendingProperties = draft
    try {
        val props = when {
            item != null && instanceId != null -> computeScopedProperties(componentType, definition, instanceId, item, scopePath)
            kuikly -> computeKuiklyProperties(componentType, definition)
            else -> computeProperties(componentType, definition)
        }
        return draft.finish(props)
    } finally {
        pendingProperties = previous
    }
}

internal fun A2UIComponentProjection.commitProperties(plan: A2UIProjectionPlan): Map<String, Any?> {
    plan.pathDependencies.forEach { (id, paths) -> paths.forEach { reactive.registerPath(id, it) } }
    plan.functionDependencies.forEach { (id, paths) -> paths.forEach { reactive.registerFunction(id, it) } }
    plan.definitions.forEach { (id, definition) -> model.rememberDefinition(id, definition) }
    plan.actions.forEach { (tag, action) -> interactions.registerAction(tag, action) }
    plan.disabledActions.forEach { (tag, disabled) -> interactions.setDisabled(tag, disabled) }
    plan.initialValues.forEach { (path, value) -> model.write(path, value, A2UIUpdateOrigin.BINDING) }
    plan.checks.forEach { (id, registration) -> reactive.registerChecks(id, registration.rules, registration.paths) }
    return plan.props
}

internal fun A2UIComponentProjection.planDefinition(id: String, definition: Map<String, Any?>) {
    val draft = checkNotNull(pendingProperties)
    draft.definitions[id] = definition
}

internal fun A2UIComponentProjection.planAction(tag: Int, action: Any?) {
    checkNotNull(pendingProperties).actions[tag] = action
}

internal fun A2UIComponentProjection.planDisabled(tag: Int, disabled: Boolean) {
    checkNotNull(pendingProperties).disabledActions[tag] = disabled
}

internal fun A2UIComponentProjection.planInitialValue(path: String, value: Any?) {
    checkNotNull(pendingProperties).initialValues[path] = value
}

internal fun <T> A2UIComponentProjection.withoutPendingProperties(block: () -> T): T {
    val previous = pendingProperties
    pendingProperties = null
    try {
        return block()
    } finally {
        pendingProperties = previous
    }
}

internal data class A2UIProjectionBatch(val messages: List<A2UIMessage>)

internal class A2UIMaterializationState {
    private val parents = mutableMapOf<Int, Int>()

    fun contains(tag: Int): Boolean = parents.containsKey(tag)
    fun parentOf(tag: Int): Int? = parents[tag]
    fun reset() = parents.clear()
    fun commit(batch: A2UIProjectionBatch) {
        for (message in batch.messages) {
            when (message.type) {
                A2UIMessageType.CREATE_COMPONENT -> parents[message.tag] = message.parentTag
                A2UIMessageType.REMOVE_COMPONENT -> parents.remove(message.tag)
                else -> Unit
            }
        }
    }
}

internal fun A2UIComponentProjection.commitBatch(batch: A2UIProjectionBatch): List<A2UIMessage> {
    materialization.commit(batch)
    return batch.messages
}
