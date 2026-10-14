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
 * A2UI v0.9 protocol message types.
 * Defines all supported message operations in the A2UI protocol.
 */
enum class A2UIMessageType(val value: String) {
    // Surface lifecycle
    CREATE_SURFACE("createSurface"),
    DESTROY_SURFACE("destroySurface"),
    RESIZE_SURFACE("resizeSurface"),

    // Component operations
    CREATE_COMPONENT("createComponent"),
    UPDATE_COMPONENT("updateComponent"),
    REMOVE_COMPONENT("removeComponent"),
    MOVE_COMPONENT("moveComponent"),

    // Tree operations
    INSERT_CHILD("insertChild"),
    REMOVE_CHILD("removeChild"),

    // Data operations
    UPDATE_DATA("updateData"),
    BATCH_UPDATE("batchUpdate"),

    // Action/Event
    ACTION("action"),
    EVENT("event"),

    // Layout
    SET_LAYOUT("setLayout"),

    // Method calls
    CALL_METHOD("callMethod"),

    // Stream control
    STREAM_BEGIN("streamBegin"),
    STREAM_END("streamEnd"),

    // Unknown
    UNKNOWN("unknown");

    companion object {
        fun fromValue(value: String): A2UIMessageType {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}
