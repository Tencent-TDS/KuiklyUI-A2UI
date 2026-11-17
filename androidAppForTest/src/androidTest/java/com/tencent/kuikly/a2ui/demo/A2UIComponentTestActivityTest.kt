package com.tencent.kuikly.a2ui.demo

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class A2UIComponentTestActivityTest {
    @Test
    fun directLegacyDirectoryRendersNativeContent() {
        assertDirectoryRenders("v0_9/a2ui_components/text")
    }

    @Test
    fun directV10DirectoryRendersNativeContent() {
        assertDirectoryRenders("v1_0/a2ui_components/text")
    }

    @Test
    fun existingRootAndSubdirectoryEntryStillRendersNativeContent() {
        assertDirectoryRenders("v0_9/a2ui_components", "text")
    }

    @Test
    fun migratedWeatherRendersNativeContent() {
        assertDirectoryRenders("v1_0/a2ui_examples")
    }

    @Test
    fun migratedChildListRendersNativeContent() {
        assertDirectoryRenders("v1_0/a2ui_databinding_tests/03_child_list_template")
    }

    @Test
    fun everyVersionedAssetCanBeListedReadAndValidatedOnDevice() {
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        fun visit(directory: String): Int {
            var count = 0
            for (name in assets.list(directory).orEmpty()) {
                val path = "$directory/$name"
                if (name.endsWith(".json")) {
                    val document = JSONObject(assets.open(path).bufferedReader().use { it.readText() })
                    val messages = document.getJSONArray("messages")
                    A2UIDemoAssetVersion.validate((0 until messages.length()).map {
                        messages.optJSONObject(it)?.optString("version")
                    }, directory)
                    count++
                } else if (assets.list(path).orEmpty().isNotEmpty()) {
                    count += visit(path)
                }
            }
            return count
        }
        assertTrue(visit("v0_9") > 0)
        assertTrue(visit("v1_0") > 0)
    }

    private fun assertDirectoryRenders(root: String, child: String = "") {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val directory = if (child.isEmpty()) root else "$root/$child"
        val expectedCount = context.assets.list(directory).orEmpty().count { it.endsWith(".json") }
        assertTrue("Missing test assets: $directory", expectedCount > 0)
        val intent = Intent(context, A2UIComponentTestActivity::class.java).apply {
            putExtra(A2UIComponentTestActivity.EXTRA_ROOT_DIR, root)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_DIR, child)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_NAME, directory.substringAfterLast('/'))
        }
        ActivityScenario.launch<A2UIComponentTestActivity>(intent).use { scenario ->
            var state = ""
            repeat(100) {
                instrumentation.waitForIdleSync()
                var ready = false
                scenario.onActivity { activity ->
                    val surfaces = descendants(activity.window.decorView).filterIsInstance<A2UISurfaceView>().toList()
                    state = "surfaces=${surfaces.size}, expected=$expectedCount, content=${surfaces.map { "${it.width}x${it.height}/${it.childCount}" }}"
                    ready = surfaces.size == expectedCount && surfaces.all { it.width > 0 && it.height > 0 && it.childCount > 0 }
                }
                if (ready) return
                Thread.sleep(100)
            }
            scenario.onActivity { activity ->
                val surfaces = descendants(activity.window.decorView).filterIsInstance<A2UISurfaceView>().toList()
                assertEquals("$directory: $state", expectedCount, surfaces.size)
                assertTrue("$directory: $state", surfaces.all { it.width > 0 && it.height > 0 && it.childCount > 0 })
            }
        }
    }

    private fun descendants(view: View): Sequence<View> = sequence {
        yield(view)
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) yieldAll(descendants(view.getChildAt(index)))
        }
    }
}
