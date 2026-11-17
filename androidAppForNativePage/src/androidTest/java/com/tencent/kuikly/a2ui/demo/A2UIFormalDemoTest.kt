package com.tencent.kuikly.a2ui.demo

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import com.tencent.kuikly.core.render.android.expand.component.KRRichTextView
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class A2UIFormalDemoTest {
    @Test
    fun everyPackagedFormalAssetDeclaresOnlyOfficialV10Messages() {
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        fun visit(directory: String): Int {
            var count = 0
            for (child in assets.list(directory).orEmpty()) {
                val path = "$directory/$child"
                if (!child.endsWith(".json")) {
                    count += visit(path)
                    continue
                }
                val messages = JSONObject(assets.open(path).bufferedReader().use { it.readText() }).getJSONArray("messages")
                assertTrue(path, messages.length() > 0)
                for (index in 0 until messages.length()) {
                    val message = messages.getJSONObject(index)
                    assertEquals(path, "1.0", message.getString("version"))
                    if (message.has("createSurface")) assertTrue(path, message.getJSONObject("createSurface").getString("catalogId").isNotEmpty())
                }
                count++
            }
            return count
        }
        assertEquals(108, listOf("a2ui_components", "a2ui_custom_components", "a2ui_custom_functions").sumOf(::visit))
    }

    @Test
    fun allTwentyInlineCardsHaveActualNativeChildren() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ActivityScenario.launch<NativeCardDemoActivity>(Intent(context, NativeCardDemoActivity::class.java)).use { scenario ->
            await(scenario) { surfaces -> surfaces.size == 20 && surfaces.all { it.childCount > 0 && it.width > 0 && it.height > 0 } }
        }
    }

    @Test
    fun formalDatesRenderBothMarkdownHeadingAndDateText() = withDirectory("a2ui_components", "text") { scenario ->
        await(scenario) { surfaces -> texts(surfaces).containsAll(listOf("Dec 15, 2025", "2025/12/15")) }
    }

    @Test
    fun formalCustomFunctionsAreRegisteredWhenTheDirectoryIsOpenedDirectly() = withDirectory("a2ui_custom_functions", "01_string_return") { scenario ->
        await(scenario) { surfaces -> texts(surfaces).containsAll(listOf("Hello, Kuikly!", "138-0000-1234")) }
    }

    @Test
    fun formalBooleanUsesRealFunctionResultAndValidationResult() = withDirectory("a2ui_custom_functions", "02_boolean_return") { scenario ->
        await(scenario) { surfaces -> texts(surfaces).contains("当前 userLevel = 5，isVip 返回 true") }
    }

    @Test
    fun formalCustomComponentHasActualContentAndIntrinsicHeight() = withDirectory("a2ui_custom_components", "02_my_view") { scenario ->
        await(scenario) { surfaces -> surfaces.size == 1 && surfaces.flatMap { descendants(it).toList() }.filterIsInstance<TextView>().any {
            it.text.toString().startsWith("Hello from MyA2UIView!") && it.width > 0 && it.height > 0
        } }
    }

    private fun withDirectory(root: String, child: String, test: (ActivityScenario<A2UIComponentTestActivity>) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, A2UIComponentTestActivity::class.java).apply {
            putExtra(A2UIComponentTestActivity.EXTRA_ROOT_DIR, root)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_DIR, child)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_NAME, child)
        }
        ActivityScenario.launch<A2UIComponentTestActivity>(intent).use(test)
    }

    private fun <T : android.app.Activity> await(scenario: ActivityScenario<T>, predicate: (List<A2UISurfaceView>) -> Boolean) {
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
        throw AssertionError(state)
    }

    private fun texts(surfaces: List<A2UISurfaceView>): List<String> = surfaces.asSequence().flatMap(::descendants).mapNotNull { view ->
        when (view) {
            is TextView -> view.text.toString()
            is KRRichTextView -> {
                val getter = view.javaClass.methods.single { it.name.startsWith("getText\$") && it.parameterTypes.isEmpty() && it.returnType == String::class.java }
                getter.invoke(view) as String
            }
            else -> null
        }
    }.toList()

    private fun descendants(view: View): Sequence<View> = sequence {
        yield(view)
        if (view is ViewGroup) for (index in 0 until view.childCount) yieldAll(descendants(view.getChildAt(index)))
    }
}
