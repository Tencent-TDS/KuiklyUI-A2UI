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
 * A2UI v0.9 protocol message data model.
 * Represents a single message in the A2UI protocol stream.
 */
data class A2UIMessage(
    /** Message type */
    val type: A2UIMessageType,
    /** Target surface ID */
    val surfaceId: String,
    /** Target component tag (if applicable) */
    val tag: Int = -1,
    /** Component type name (e.g., "View", "Text", "Image") */
    val viewName: String? = null,
    /** Parent component tag for tree operations */
    val parentTag: Int = -1,
    /** Index for insertion operations */
    val index: Int = -1,
    /** Properties map for component creation/update */
    val props: Map<String, Any?>? = null,
    /** Layout frame: [x, y, width, height] */
    val frame: FloatArray? = null,
    /** Data bindings map */
    val bindings: Map<String, String>? = null,
    /** Method name for callMethod */
    val method: String? = null,
    /** Method parameters */
    val params: String? = null,
    /** Batch messages for batchUpdate */
    val messages: List<A2UIMessage>? = null,
    /** Data payload for updateData */
    val data: Map<String, Any?>? = null,
    /** Surface configuration for createSurface */
    val config: A2UISurfaceConfig? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is A2UIMessage) return false
        return type == other.type && surfaceId == other.surfaceId && tag == other.tag
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + surfaceId.hashCode()
        result = 31 * result + tag
        return result
    }
}

/**
 * Surface configuration for createSurface message.
 */
data class A2UISurfaceConfig(
    /** Surface width in dp */
    val width: Float = 0f,
    /** Surface height in dp */
    val height: Float = 0f,
    /** Background color */
    val backgroundColor: String? = null,
    /** Whether the surface supports incremental rendering */
    val incrementalRender: Boolean = true
)
