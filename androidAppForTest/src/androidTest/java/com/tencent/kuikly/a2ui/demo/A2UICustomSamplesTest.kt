package com.tencent.kuikly.a2ui.demo

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.core.render.android.expand.component.KRRichTextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class A2UICustomSamplesTest {
    @Test
    fun customTextComponentHasNativeContentAndIntrinsicHeight() = withDirectory("a2ui_custom_components/02_my_view") { scenario ->
        awaitContent(scenario) { surfaces ->
            surfaces.size == 1 && nativeViews(surfaces).filterIsInstance<TextView>().any {
                it.text.toString().startsWith("Hello from MyA2UIView!") && it.height > 0 && it.currentTextColor == Color.parseColor("#6200EE")
            }
        }
    }

    @Test
    fun customWebComponentLoadsItsSourceAndHasIntrinsicHeight() = withDirectory("a2ui_custom_components/01_web_view") { scenario ->
        awaitContent(scenario) { surfaces ->
            nativeViews(surfaces).filterIsInstance<WebView>().any {
                it.width > 0 && it.height > 0 && it.originalUrl == "https://framework.tds.qq.com/"
            }
        }
    }

    @Test
    fun customStringFunctionsRenderResolvedDataAndRemainReactive() = withDirectory("a2ui_custom_functions/01_string_return") { scenario ->
        awaitContent(scenario) { surfaces -> texts(surfaces).containsAll(listOf("Hello, Kuikly!", "138-0000-1234")) }
        scenario.onActivity { activity ->
            val surface = descendants(activity.window.decorView).filterIsInstance<A2UISurfaceView>().first()
            surface.writeStream("""{"version":"v0.9","updateDataModel":{"surfaceId":"cf-str-1","value":{"userName":"Updated"}}}""")
            surface.finishStream()
        }
        awaitContent(scenario) { surfaces -> texts(surfaces).contains("Hello, Updated!") }
    }

    @Test
    fun customNumberFunctionIsEvaluatedInsideFormatString() = withDirectory("a2ui_custom_functions/03_number_return") { scenario ->
        awaitContent(scenario) { surfaces -> texts(surfaces).contains("139.3") }
    }

    @Test
    fun customSummaryReceivesAllBoundArguments() = withDirectory("a2ui_custom_functions/05_args_passing") { scenario ->
        awaitContent(scenario) { surfaces -> texts(surfaces).contains("项目「KuiklyUI」当前状态: active，共 42 个任务") }
    }

    @Test
    fun customBooleanFunctionHasNativeContentAndBothThresholdResults() = withDirectory("a2ui_custom_functions/02_boolean_return") { scenario ->
        awaitContent(scenario) { surfaces -> texts(surfaces).any { it.contains("进入 VIP 专区") } }
        val registry = com.tencent.kuikly.a2ui.render.android.A2UIAndroidCustomFunctionRegistry
        assertEquals(false, registry.invokeLocally("isVip", "{\"level\":2}", "boolean|sync"))
        assertEquals(true, registry.invokeLocally("isVip", "{\"level\":3}", "boolean|sync"))
    }

    @Test
    fun customVoidActionContentIsVisible() = withDirectory("a2ui_custom_functions/04_void_action") { scenario ->
        awaitContent(scenario) { surfaces -> texts(surfaces).containsAll(listOf("点击按钮触发自定义 void 函数 logEvent", "触发 logEvent")) }
    }

    @Test
    fun customVoidActionInvokesTheRegisteredHandler() = withDirectory("a2ui_custom_functions/04_void_action") { scenario ->
        awaitContent(scenario) { surfaces -> texts(surfaces).contains("触发 logEvent") }
        val marker = "custom_test_${System.nanoTime()}"
        scenario.onActivity { activity ->
            val surface = descendants(activity.window.decorView).filterIsInstance<A2UISurfaceView>().single()
            surface.writeStream("""{"version":"v0.9","updateDataModel":{"surfaceId":"cf-void-1","value":{"eventData":"$marker"}}}""")
            surface.finishStream()
        }
        tapLabel(scenario, "触发 logEvent")
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        repeat(100) {
            val logs = android.os.ParcelFileDescriptor.AutoCloseInputStream(automation.executeShellCommand("logcat -d -t 500 -s A2UIDemoCustom:I '*:S'"))
                .bufferedReader().use { it.readText() }
            if (logs.contains("logEvent: event=button_click, data=$marker")) return@withDirectory
            Thread.sleep(100)
        }
        throw AssertionError("logEvent handler did not receive the bound eventData")
    }

    private fun tapLabel(scenario: ActivityScenario<A2UIComponentTestActivity>, text: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        var x = 0f
        var y = 0f
        scenario.onActivity { activity ->
            val surfaces = descendants(activity.window.decorView).filterIsInstance<A2UISurfaceView>().toList()
            val label = nativeViews(surfaces).single { actualText(it) == text }
            assertTrue("Action label is not visible", label.isShown && label.width > 0 && label.height > 0)
            val location = IntArray(2)
            label.getLocationOnScreen(location)
            x = location[0] + label.width / 2f
            y = location[1] + label.height / 2f
        }
        val downTime = android.os.SystemClock.uptimeMillis()
        for (action in listOf(android.view.MotionEvent.ACTION_DOWN, android.view.MotionEvent.ACTION_UP)) {
            val event = android.view.MotionEvent.obtain(downTime, android.os.SystemClock.uptimeMillis(), action, x, y, 0)
            event.source = android.view.InputDevice.SOURCE_TOUCHSCREEN
            try {
                instrumentation.sendPointerSync(event)
            } catch (e: SecurityException) {
                throw org.junit.AssumptionViolatedException("Device denies INJECT_EVENTS; real touch interaction is not verified", e)
            } finally {
                event.recycle()
            }
            if (action == android.view.MotionEvent.ACTION_DOWN) Thread.sleep(60)
        }
        instrumentation.waitForIdleSync()
    }

    private fun withDirectory(relative: String, test: (ActivityScenario<A2UIComponentTestActivity>) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, A2UIComponentTestActivity::class.java).apply {
            putExtra(A2UIComponentTestActivity.EXTRA_ROOT_DIR, "v0_9/$relative")
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_DIR, "")
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_NAME, relative.substringAfterLast('/'))
        }
        ActivityScenario.launch<A2UIComponentTestActivity>(intent).use(test)
    }

    private fun awaitContent(scenario: ActivityScenario<A2UIComponentTestActivity>, predicate: (List<A2UISurfaceView>) -> Boolean) {
        var state = ""
        repeat(100) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            var ready = false
            scenario.onActivity { activity ->
                val surfaces = descendants(activity.window.decorView).filterIsInstance<A2UISurfaceView>().toList()
                state = "surfaces=${surfaces.size}, texts=${texts(surfaces)}"
                ready = predicate(surfaces)
            }
            if (ready) return
            Thread.sleep(100)
        }
        throw AssertionError("Missing actual custom content: $state")
    }

    private fun actualText(view: View): String? = when (view) {
        is TextView -> view.text.toString()
        is KRRichTextView -> {
            val getter = view.javaClass.methods.single {
                it.name.startsWith("getText\$") && it.parameterTypes.isEmpty() && it.returnType == String::class.java
            }
            getter.invoke(view) as String
        }
        else -> null
    }

    private fun texts(surfaces: List<A2UISurfaceView>): List<String> = nativeViews(surfaces).mapNotNull(::actualText).toList()

    private fun nativeViews(surfaces: List<A2UISurfaceView>): Sequence<View> = surfaces.asSequence().flatMap { descendants(it) }

    private fun descendants(view: View): Sequence<View> = sequence {
        yield(view)
        if (view is ViewGroup) for (index in 0 until view.childCount) yieldAll(descendants(view.getChildAt(index)))
    }
}
