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

package com.tencent.kuikly.a2ui.dsl

import com.tencent.kuikly.a2ui.golden.A2UIGoldenCorpus
import com.tencent.kuikly.a2ui.parser.A2UIParseError
import com.tencent.kuikly.a2ui.parser.A2UIStreamParser
import com.tencent.kuikly.a2ui.protocol.A2UIMessage
import com.tencent.kuikly.a2ui.protocol.A2UIMessageType
import com.tencent.kuikly.a2ui.views.A2UISliderView
import com.tencent.kuikly.core.base.CrossPlatFeature
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.manager.KotlinMethod
import com.tencent.kuikly.core.manager.NativeMethod
import com.tencent.kuikly.core.manager.PagerManager
import com.tencent.kuikly.core.nvi.NativeBridge
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.views.DivView
import com.tencent.kuikly.core.views.ImageEvent
import com.tencent.kuikly.core.views.InputEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Isolated old/new executions through real DSL/Flex/shadow; no native UI is presented. */
@Suppress("DEPRECATION")
class A2UIApplierReplayTest {
    private class Recorder : NativeBridge.NativeBridgeDelegate {
        val trace = mutableListOf<String>()
        private val callbackIds = linkedMapOf<String, Int>()
        private val refs = linkedMapOf<Int, Int>()
        fun ref(value: Int): String = if (value < 0) "root:$value" else "ref-${refs.getOrPut(value) { refs.size }}"
        override fun callNative(methodId: Int, arg0: Any?, arg1: Any?, arg2: Any?, arg3: Any?, arg4: Any?, arg5: Any?): Any? {
            val args = mutableListOf(arg0, arg1, arg2, arg3, arg4, arg5)
            when (methodId) {
                NativeMethod.CREATE_RENDER_VIEW, NativeMethod.REMOVE_RENDER_VIEW, NativeMethod.SET_VIEW_PROP,
                NativeMethod.SET_RENDER_VIEW_FRAME, NativeMethod.CALCULATE_RENDER_VIEW_SIZE, NativeMethod.CALL_VIEW_METHOD,
                NativeMethod.CREATE_SHADOW, NativeMethod.REMOVE_SHADOW, NativeMethod.SET_SHADOW_PROP,
                NativeMethod.SET_SHADOW_FOR_VIEW, NativeMethod.CALL_SHADOW_METHOD -> args[1] = ref(arg1 as Int)
                NativeMethod.INSERT_SUB_RENDER_VIEW -> {
                    args[1] = ref(arg1 as Int)
                    args[2] = ref(arg2 as Int)
                }
            }
            if (methodId == NativeMethod.SET_TIMEOUT) {
                val callback = args[2] as String
                args[2] = "callback-${callbackIds.getOrPut(callback) { callbackIds.size }}"
            }
            if (methodId == NativeMethod.CALL_VIEW_METHOD || methodId == NativeMethod.CALL_MODULE_METHOD) {
                val callback = args[4] as? String
                if (callback != null) args[4] = "callback-${callbackIds.getOrPut(callback) { callbackIds.size }}"
            }
            trace.add("native:$methodId:${args.joinToString("|") { it?.toString() ?: "<null>" }}")
            return when (methodId) {
                NativeMethod.CALCULATE_RENDER_VIEW_SIZE -> "40.0|20.0"
                NativeMethod.FIRE_FATAL_EXCEPTION -> error("Core fatal: $arg1")
                else -> null
            }
        }
    }

    private class RecordingHost(private val pager: A2UIPager, private val recorder: Recorder, embedded: Boolean) : A2UIHost {
        override val hostPagerId get() = pager.hostPagerId
        override val registry = if (embedded) A2UIViewRegistry() else pager.registry
        override val surfaceWidth get() = 375f
        override val rootContainer: ViewContainer<*, *> = if (embedded) DivView().also { root ->
            pager.addChild(root, { attr { width(375f) } })
            pager.insertDomSubView(root, pager.getSubviews().size - 1)
        } else pager
        private var taskId = 0
        override fun fullScreenModalSize(): Pair<Float, Float> = 375f to 812f
        override fun addTaskWhenLayoutFinish(task: () -> Unit) {
            val id = taskId++
            recorder.trace.add("enqueue:$id")
            pager.addTaskWhenLayoutFinish {
                recorder.trace.add("run:$id")
                task()
            }
        }
        override fun performLayoutAndReportHeight() {
            recorder.trace.add("layout")
            pager.performLayoutAndReportHeight()
        }
    }

    private class Execution(host: A2UIHost, legacy: Boolean, private val recorder: Recorder) {
        private val old = if (legacy) A2UILegacyApplier(host) else null
        private val current = if (legacy) null else A2UIApplier(host)
        val registry = host.registry
        fun create(tag: Int, type: String, parent: Int = 0, props: Map<String, Any?>? = null) {
            if (old != null) old.createComponent(tag, type, parent, -1, props)
            else current!!.createComponent(tag, type, parent, -1, props)
        }
        fun update(tag: Int, props: Map<String, Any?>) {
            if (old != null) old.updateComponent(tag, "IgnoredUpdateType", props)
            else current!!.updateComponent(tag, "IgnoredUpdateType", props)
        }
        fun remove(tag: Int, parent: Int = 0) {
            if (old != null) old.removeComponent(tag, parent) else current!!.removeComponent(tag, parent)
        }
        fun consume(message: A2UIMessage) {
            when (message.type) {
                A2UIMessageType.CREATE_COMPONENT -> {
                    if (old != null) old.createComponent(message.tag, message.viewName ?: "KRView", message.parentTag, message.index, message.props)
                    else current!!.createComponent(message.tag, message.viewName ?: "KRView", message.parentTag, message.index, message.props)
                }
                A2UIMessageType.UPDATE_COMPONENT -> message.props?.let { update(message.tag, it) }
                A2UIMessageType.REMOVE_COMPONENT -> remove(message.tag, message.parentTag)
                A2UIMessageType.BATCH_UPDATE -> message.messages?.forEach(::consume)
                A2UIMessageType.STREAM_END -> layout()
                else -> Unit
            }
        }
        fun layout() { if (old != null) old.performLayout() else current!!.performLayout() }
        fun destroy() { if (old != null) old.destroy() else current!!.destroy() }
        fun installCallbacks(label: String) {
            val input: (Int, String, String) -> Unit = { tag, key, value -> recorder.trace.add("$label:input:$tag:$key:$value") }
            val choice: (Int, String, String) -> Unit = { tag, parent, option -> recorder.trace.add("$label:choice:$tag:$parent:$option") }
            val slider: (Int, String, Float) -> Unit = { tag, key, value -> recorder.trace.add("$label:slider:$tag:$key:$value") }
            val tab: (String, Int) -> Unit = { id, index -> recorder.trace.add("$label:tab:$id:$index") }
            val modal: (String) -> Unit = { id -> recorder.trace.add("$label:modal:$id") }
            val link: (String) -> Unit = { href -> recorder.trace.add("$label:link:$href") }
            if (old != null) {
                old.onTextFieldValueChanged = input
                old.onChoicePickerValueChanged = choice
                old.onSliderValueChanged = slider
                old.onTabSelected = tab
                old.onModalOpen = modal
                old.onModalClose = modal
                old.onMarkdownLinkClick = link
            } else {
                current!!.onTextFieldValueChanged = input
                current.onChoicePickerValueChanged = choice
                current.onSliderValueChanged = slider
                current.onTabSelected = tab
                current.onModalOpen = modal
                current.onModalClose = modal
                current.onMarkdownLinkClick = link
            }
        }
        fun fire(tag: Int, name: String, payload: JSONObject = JSONObject()) {
            val view = assertNotNull(registry.getView(tag))
            recorder.trace.add("event:$tag:$name:$payload")
            view.onFireEvent(name, payload)
        }
        fun snapshot(tags: Iterable<Int>) {
            for (tag in tags) {
                val view = registry.getView(tag)
                recorder.trace.add(if (view == null) "view:$tag:<removed>" else {
                    val node = view.flexNode
                    "view:$tag:${view::class.simpleName}:${recorder.ref(view.nativeRef)}:${registry.getTagByNativeRef(view.nativeRef)}:${node.styleWidth}:${node.styleHeight}:${node.styleMaxWidth}:${node.layoutFrame}"
                })
            }
        }
    }

    private fun replay(legacy: Boolean, embedded: Boolean, scenario: (Execution, Recorder) -> Unit): List<String> {
        val id = "applier-replay"
        val oldPage = BridgeManager.currentPageId
        val oldCatch = BridgeManager.catchException
        val oldContext = NativeBridge.isContextThread
        val oldFlat = CrossPlatFeature.isIgnoreRenderViewForFlatLayer
        val recorder = Recorder()
        BridgeManager.init(false)
        NativeBridge.isContextThread = true
        CrossPlatFeature.isIgnoreRenderViewForFlatLayer = true
        BridgeManager.registerNativeBridge(id, NativeBridge().apply { delegate = recorder })
        PagerManager.registerPageRouter(id) { A2UIPager() }
        BridgeManager.currentPageId = id
        try {
            PagerManager.createPager(id, id, """{"rootViewWidth":375.0,"rootViewHeight":10000.0,"nativeBuild":2}""")
            val pager = PagerManager.getPager(id) as A2UIPager
            val host = RecordingHost(pager, recorder, embedded)
            val execution = Execution(host, legacy, recorder)
            scenario(execution, recorder)
            execution.destroy()
        } finally {
            BridgeManager.callKotlinMethod(KotlinMethod.DESTROY_INSTANCE, id)
            BridgeManager.currentPageId = oldPage
            BridgeManager.init(oldCatch)
            NativeBridge.isContextThread = oldContext
            CrossPlatFeature.isIgnoreRenderViewForFlatLayer = oldFlat
        }
        return recorder.trace.toList()
    }

    private fun compare(scenario: (Execution, Recorder) -> Unit) {
        for (embedded in listOf(false, true)) {
            val before = replay(true, embedded, scenario)
            val after = replay(false, embedded, scenario)
            assertTrue(before.any { it.startsWith("native:${NativeMethod.CREATE_RENDER_VIEW}:") })
            assertEquals(before.size, after.size, "trace length, external root=$embedded")
            before.indices.forEach { index -> assertEquals(before[index], after[index], "operation $index, external root=$embedded") }
        }
    }

    @Test
    fun treeLifecycleAndIncrementalPropertiesMatchLegacyNativeTrace() = compare { execution, _ ->
        execution.create(1, "List", props = mapOf("width" to 200, "height" to 120))
        execution.create(2, "Text", 1, mapOf("text" to "first", "fontSize" to 14, "width" to 100))
        execution.update(2, linkedMapOf("text" to "next", "width" to 80, "opacity" to 0.7, "__disabled" to true))
        execution.create(3, "UnknownFactoryName", props = mapOf("width" to 90, "customProp" to listOf(1, 2), "__private" to "ignored"))
        execution.layout()
        execution.snapshot(1..3)
        execution.remove(2, 1)
        execution.create(2, "Text", 1, mapOf("text" to "recreated"))
        execution.update(999, emptyMap())
        execution.remove(999)
        execution.layout()
        execution.snapshot(1..3)
    }

    @Test
    fun textMarkdownAndPostLayoutOperationsMatchLegacyTrace() = compare { execution, recorder ->
        execution.create(1, "View", props = mapOf("flexDirection" to "row", "width" to 300, "paddingLeft" to 12, "paddingRight" to 8))
        execution.create(2, "Text", 1, mapOf("text" to "A long streaming text", "fontSize" to 16, "__textInRowPostLayout" to "true"))
        execution.create(3, "View", 1, mapOf("width" to 40, "height" to 80))
        execution.create(4, "Divider", 1, mapOf("width" to 1, "__verticalDividerFollowParent" to true))
        assertTrue(recorder.trace.any { it.startsWith("enqueue:") })
        execution.layout()
        execution.update(2, mapOf("text" to "# Heading\n**bold** [link](https://example.com)", "markdown" to true, "__textInRowPostLayout" to "true"))
        execution.installCallbacks("latest")
        execution.layout()
        execution.update(2, mapOf("text" to "plain again", "markdown" to false, "__textAlignPostLayout" to "center"))
        execution.layout()
        assertTrue(recorder.trace.any { it.startsWith("run:") })
        execution.snapshot(1..4)
    }

    @Test
    fun controlInitializationAndValueOnlyUpdatesMatchLegacyTrace() = compare { execution, _ ->
        execution.create(1, "CheckBox", props = mapOf("width" to 24, "height" to 24, "checked" to true, "__primaryColor" to "#FF0000", "__checkBoxBindingPath" to "checked"))
        execution.update(1, mapOf("checked" to false))
        execution.update(1, mapOf("opacity" to 0.8))
        execution.create(2, "Slider", props = mapOf("width" to 200, "height" to 40, "min" to 10, "max" to 20, "steps" to 5, "value" to 13, "__sliderBindingPath" to "range"))
        execution.layout()
        execution.update(2, mapOf("value" to 18))
        execution.update(2, mapOf("max" to 30))
        execution.create(3, "DateTimeInput", props = mapOf("width" to 200, "height" to 40, "enableDate" to true, "value" to "2026-11-23"))
        execution.update(3, mapOf("__dateTimeBindingPath" to "date", "enableTime" to true))
        execution.layout()
        execution.snapshot(1..3)
    }

    @Test
    fun callbackReplacementBindingChangesAndCompositeClicksMatchLegacyTrace() = compare { execution, recorder ->
        execution.create(1, "TextField", props = mapOf("width" to 200, "height" to 40, "text" to "start", "__textFieldBindingPath" to "old"))
        execution.installCallbacks("first")
        execution.fire(1, InputEvent.TEXT_DID_CHANGE, JSONObject().put("text", "one"))
        execution.update(1, mapOf("__textFieldBindingPath" to "new"))
        execution.installCallbacks("replacement")
        execution.fire(1, InputEvent.TEXT_DID_CHANGE, JSONObject().put("text", "two"))
        execution.create(2, "View", props = mapOf("touchEnable" to true, "__choicePickerParentId" to "choice", "__choiceOptionValue" to "a"))
        execution.fire(2, "click")
        execution.update(2, mapOf("__choiceOptionValue" to "b", "__choicePickerParentId" to "other"))
        execution.fire(2, "click")
        execution.create(3, "View", props = mapOf("touchEnable" to true, "__tabsParentId" to "tabs", "__tabIndex" to 1))
        execution.fire(3, "click")
        execution.create(4, "View", props = mapOf("touchEnable" to true, "__modalTriggerId" to "modal"))
        execution.fire(4, "click")
        assertTrue(recorder.trace.contains("replacement:input:1:new:two"))
        assertTrue(recorder.trace.contains("replacement:choice:2:choice:a"))
        assertTrue(recorder.trace.contains("replacement:tab:tabs:1"))
        assertTrue(recorder.trace.contains("replacement:modal:modal"))
    }

    @Test
    fun mediaResolutionAndSizingOperationsMatchLegacyTrace() = compare { execution, recorder ->
        execution.create(1, "Image", props = mapOf("src" to "https://example.com/a.png", "__featureAutoSizeMaxWidthDp" to 180, "__featureAutoSizeMaxHeightDp" to 120))
        execution.fire(1, ImageEvent.LOAD_RESOLUTION, JSONObject().put("imageWidth", 600).put("imageHeight", 300))
        val image = assertNotNull(execution.registry.getView(1))
        assertEquals(180f, image.flexNode.styleWidth)
        assertEquals(90f, image.flexNode.styleHeight)
        execution.update(1, mapOf("src" to "https://example.com/b.png", "__featureAutoSizeHeader" to true, "__featureAutoSizeMaxHeightDp" to 80))
        execution.fire(1, ImageEvent.LOAD_RESOLUTION, JSONObject().put("imageWidth", 300).put("imageHeight", 600))
        assertEquals(120f, image.flexNode.styleHeight)
        execution.create(2, "Video", props = mapOf("src" to "video.mp4", "resizeMode" to "contain", "muted" to true))
        execution.update(2, mapOf("muted" to false))
        execution.create(3, "AudioPlayer", props = mapOf("src" to "audio.mp3", "playControl" to "pause"))
        execution.create(4, "KRBlurView", props = mapOf("blurRadius" to 4, "blurStyle" to "dark"))
        execution.create(5, "KRActivityIndicatorView", props = mapOf("color" to "#FF0000", "animating" to 1))
        execution.create(6, "KRPAGView", props = mapOf("src" to "effect.pag", "repeatCount" to 2))
        execution.create(7, "KRAPNGView", props = mapOf("src" to "effect.png", "repeatCount" to 2))
        execution.create(8, "Image", props = mapOf("src" to "header.png", "__featureAutoSizeHeader" to true, "__featureAutoSizeMaxHeightDp" to 200))
        execution.fire(8, ImageEvent.LOAD_RESOLUTION, JSONObject().put("imageWidth", 300).put("imageHeight", 100))
        assertEquals(125f, assertNotNull(execution.registry.getView(8)).flexNode.styleHeight)
        execution.layout()
        assertTrue(recorder.trace.count { it == "layout" } >= 4)
        execution.snapshot(1..8)
    }

    @Test
    fun removalAndLateLayoutRetainLegacyLifecycleBehavior() = compare { execution, recorder ->
        execution.create(1, "View", props = mapOf("width" to 300))
        execution.create(2, "Text", 1, mapOf("text" to "queued", "__textAlignPostLayout" to "center"))
        execution.remove(2, 1)
        execution.layout()
        execution.create(3, "View", props = mapOf("__choicePickerParentId" to "choice", "__choiceOptionValue" to "a"))
        execution.installCallbacks("before")
        execution.fire(3, "click")
        execution.remove(3)
        execution.create(3, "View", props = mapOf("__choicePickerParentId" to "choice", "__choiceOptionValue" to "b"))
        execution.installCallbacks("after")
        execution.fire(3, "click")
        assertTrue(recorder.trace.contains("before:choice:3:choice:a"))
        assertTrue(recorder.trace.none { it == "after:choice:3:choice:b" })
        execution.create(4, "Modal", props = mapOf("__modalId" to "modal"))
        assertEquals(812f, assertNotNull(execution.registry.getView(4)).flexNode.styleHeight)
        execution.remove(4)
        execution.create(4, "Modal", props = mapOf("__modalId" to "modal"))
        assertEquals(0f, assertNotNull(execution.registry.getView(4)).flexNode.styleHeight)
        execution.layout()
        execution.snapshot(1..4)
    }

    @Test
    fun parsedGoldenStreamRetainsWholeAndChunkedCoreExecutionTrace() {
        val case = A2UIGoldenCorpus.cases.first()
        fun run(legacy: Boolean, embedded: Boolean, chunkSize: Int) = replay(legacy, embedded) { execution, _ ->
            val parser = A2UIStreamParser()
            val tags = linkedSetOf<Int>()
            val errors = mutableListOf<String>()
            parser.setListener(object : A2UIStreamParser.A2UIStreamListener {
                override fun onMessageParsed(message: A2UIMessage) {
                    if (message.type == A2UIMessageType.CREATE_COMPONENT) tags.add(message.tag)
                    execution.consume(message)
                }
                override fun onError(error: A2UIParseError) { errors.add(error.message) }
                override fun onStreamEnd() = Unit
            })
            if (chunkSize == Int.MAX_VALUE) parser.feed(case.stream)
            else case.stream.chunked(chunkSize).forEach(parser::feed)
            parser.finish()
            assertEquals(emptyList(), errors)
            assertTrue(tags.isNotEmpty())
            execution.snapshot(tags)
        }
        for (embedded in listOf(false, true)) {
            val expected = run(true, embedded, Int.MAX_VALUE)
            assertEquals(expected, run(false, embedded, Int.MAX_VALUE))
            assertEquals(expected, run(true, embedded, 17))
            assertEquals(expected, run(false, embedded, 17))
        }
    }

    @Test
    fun sliderBindingsPreserveStoredRangeAndSnapOnLatestCallback() {
        replay(false, false) { execution, recorder ->
            execution.create(1, "Slider", props = mapOf("width" to 200, "height" to 40, "min" to 10, "max" to 20, "steps" to 5, "value" to 13, "__sliderBindingPath" to "range"))
            execution.installCallbacks("late")
            execution.update(1, mapOf("max" to 30))
            val view = execution.registry.getView(1) as A2UISliderView
            view.getViewEvent().progressDidChangedHandlerFn?.invoke(0.31f)
            assertTrue(recorder.trace.contains("late:slider:1:range:14.0"))
        }
    }
}
