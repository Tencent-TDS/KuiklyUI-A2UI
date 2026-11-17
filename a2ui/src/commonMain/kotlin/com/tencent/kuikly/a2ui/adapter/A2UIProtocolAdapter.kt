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
import com.tencent.kuikly.a2ui.protocol.A2UIProtocolVersion
import com.tencent.kuikly.a2ui.protocol.ValidationResult
import com.tencent.kuikly.a2ui.rpc.A2UIRpcDispatcher
import com.tencent.kuikly.a2ui.rpc.A2UIRpcError
import com.tencent.kuikly.a2ui.rpc.A2UIRpcRequest
import com.tencent.kuikly.a2ui.rpc.A2UIRpcResponse

/** Public compatibility facade: composition, command ordering and stream lifecycle only. */
class A2UIProtocolAdapter {
    companion object {}

    private val model = A2UISurfaceModel()
    private val decoder = A2UIProtocolDecoder { model.surfaceId }
    private val values = A2UIValueEngine(decoder)
    private val reactive = A2UIReactiveEngine()
    private val interactionState = A2UIInteractionState()
    private val projection = A2UIComponentProjection(model, decoder, values, reactive, interactionState)
    private val interactions = A2UIInteractionController(model, decoder, values, projection, interactionState)

    val protocolVersion: A2UIProtocolVersion get() = decoder.protocolVersion
    var strictV10: Boolean
        get() = decoder.strictV10
        set(value) { decoder.strictV10 = value }
    var sendDataModel: Boolean
        get() = model.sendDataModel
        private set(value) { model.sendDataModel = value }

    internal var customFunctionInvoker: ((name: String, argsJson: String, spec: String) -> Any?)? = null
        set(value) {
            field = value
            values.customFunctionInvoker = value?.let { invoker ->
                { name, args, spec -> projection.withoutPendingProperties { invoker(name, args, spec) } }
            }
        }
    internal val rpcDispatcher: A2UIRpcDispatcher = A2UIRpcDispatcher()
    internal var onIncomingAgentCall: ((A2UIRpcRequest) -> Unit)? = null
    internal var emitWireMessage: ((String) -> Unit)? = null
    private val rpcAdapter = A2UIRpcAdapter(
        rpcDispatcher = rpcDispatcher,
        getSurfaceId = { model.surfaceId },
        getProtocolVersion = { decoder.protocolVersion },
        getStrictV10 = { decoder.strictV10 },
        getIncomingAgentCall = { onIncomingAgentCall },
        getEmitWireMessage = { emitWireMessage },
        serializeToJson = A2UIJsonValue::serializeToJson,
        getCatalogId = { model.catalogId },
    )

    fun setExpectedProtocolVersion(version: A2UIProtocolVersion?) = decoder.setExpectedProtocolVersion(version)

    fun convertMessage(rawMessage: Map<String, Any?>): List<A2UIMessage> {
        val envelope = decoder.decode(rawMessage) ?: return emptyList()
        val messages = mutableListOf<A2UIMessage>()
        for (command in envelope.commands) {
            when (command.kind) {
                A2UICommandKind.CREATE -> command.payload?.let { messages.addAll(projection.handleCreateSurface(it)) }
                A2UICommandKind.COMPONENTS -> command.payload?.let { messages.addAll(projection.handleUpdateComponents(it)) }
                A2UICommandKind.DATA -> command.payload?.let { messages.addAll(projection.handleUpdateDataModel(it, messages)) }
                A2UICommandKind.CALL_RENDERER -> rpcAdapter.handleCallRendererFunction(command.payload)
                A2UICommandKind.RENDERER_RESPONSE -> rpcAdapter.handleRendererFunctionResponse(command.payload)
                A2UICommandKind.CALL_AGENT -> rpcAdapter.handleCallAgentFunctionIncoming(command.payload)
                A2UICommandKind.AGENT_RESPONSE -> rpcAdapter.handleAgentFunctionResponse(command.payload)
            }
        }
        rpcDispatcher.tick()
        return projection.commitBatch(A2UIProjectionBatch(messages))
    }

    fun convertMessages(rawMessages: List<Map<String, Any?>>): List<A2UIMessage> {
        val messages = mutableListOf<A2UIMessage>()
        rawMessages.forEach { messages.addAll(convertMessage(it)) }
        if (model.surfaceId.isNotEmpty()) {
            messages.add(A2UIMessage(type = A2UIMessageType.STREAM_END, surfaceId = model.surfaceId))
        }
        return messages
    }

    fun reset() {
        projection.reset()
        interactions.reset()
        model.reset()
        reactive.reset()
        decoder.reset()
        rpcDispatcher.cancelAll(surfaceId = null)
    }

    fun getActionForTag(tag: Int): String? = interactions.getActionForTag(tag)
    fun getActionTags(): Set<Int> = interactions.getActionTags()
    fun getDataModelSnapshot(): String = A2UIJsonValue.buildJsonString(model.data)
    fun updateDataModelValue(key: String, value: Any?): List<A2UIMessage> = interactions.updateDataModelValue(key, value)
    fun resolveDataBindings(): List<A2UIMessage> = emptyList()
    fun evaluateChecksForComponent(componentId: String): ValidationResult = projection.evaluateChecksForComponent(componentId)
    fun filterChoicePickerOptions(componentId: String, filterText: String): List<A2UIMessage> =
        interactions.filterChoicePickerOptions(componentId, filterText)
    fun updateChoicePickerSelection(componentId: String, selectedValue: String): List<A2UIMessage> =
        interactions.updateChoicePickerSelection(componentId, selectedValue)
    fun getChoicePickerIdForChipTag(chipTag: Int): String? = interactions.getChoicePickerIdForChipTag(chipTag)
    fun getChoiceOptionValueForChipTag(chipTag: Int): String? = interactions.getChoiceOptionValueForChipTag(chipTag)
    fun getChoicePickerBindingPath(componentId: String): String? = interactions.getChoicePickerBindingPath(componentId)
    fun switchTab(tabsId: String, newIndex: Int): List<A2UIMessage> = interactions.switchTab(tabsId, newIndex)
    fun getTabsIdForHeaderTag(headerTag: Int): String? = interactions.getTabsIdForHeaderTag(headerTag)
    fun getTabIndexForHeaderTag(headerTag: Int): Int = interactions.getTabIndexForHeaderTag(headerTag)
    fun openModal(modalId: String): List<A2UIMessage> = interactions.openModal(modalId)
    fun closeModal(modalId: String): List<A2UIMessage> = interactions.closeModal(modalId)
    fun getModalIdForTriggerTag(triggerTag: Int): String? = interactions.getModalIdForTriggerTag(triggerTag)

    internal fun respondToAgent(surfaceId: String, requestId: String, result: Any?) =
        rpcAdapter.respondToAgent(surfaceId, requestId, result)
    internal fun failAgentRequest(surfaceId: String, requestId: String, error: A2UIRpcError) =
        rpcAdapter.failAgentRequest(surfaceId, requestId, error)
    internal fun invokeAgentFunction(
        surfaceId: String,
        name: String,
        args: Map<String, Any?>,
        timeoutMs: Long?,
        onComplete: (A2UIRpcResponse) -> Unit,
    ): String = rpcAdapter.invokeAgentFunction(surfaceId, name, args, timeoutMs, onComplete)
}
