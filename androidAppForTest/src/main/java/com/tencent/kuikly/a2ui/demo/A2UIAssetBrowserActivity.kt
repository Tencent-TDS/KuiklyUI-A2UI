package com.tencent.kuikly.a2ui.demo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class A2UIAssetBrowserActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DIRECTORY = "asset_directory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val directory = intent.getStringExtra(EXTRA_DIRECTORY) ?: "v0_9"
        require(directory.split('/').first() in setOf("v0_9", "v1_0"))
        require(directory.split('/').none { it == ".." || it.isEmpty() })
        val children = assets.list(directory).orEmpty().sorted()
        val folders = children.filter { assets.list("$directory/$it").orEmpty().isNotEmpty() }
        if (children.any { it.endsWith(".json") }) {
            openSamples(directory)
            finish()
            return
        }
        val entries = buildList {
            if (directory == "v0_9") add("流式渲染兼容测试")
            addAll(folders)
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 48, 24, 24)
            setBackgroundColor(0xFFF5F5F5.toInt())
        }
        layout.addView(TextView(this).apply {
            text = "←  ${if (directory.startsWith("v0_9")) "0.9 兼容回归" else "1.0 回归样本"}\n$directory"
            textSize = 18f
            setPadding(0, 0, 0, 24)
            setOnClickListener { finish() }
        })
        val list = ListView(this).apply {
            adapter = ArrayAdapter(this@A2UIAssetBrowserActivity, android.R.layout.simple_list_item_1, entries)
            setOnItemClickListener { _, _, position, _ ->
                val entry = entries[position]
                when (entry) {
                    "流式渲染兼容测试" -> startActivity(Intent(this@A2UIAssetBrowserActivity, StreamingRenderDemoActivity::class.java))
                    else -> {
                        val nextDirectory = "$directory/$entry"
                        if (assets.list(nextDirectory).orEmpty().any { it.endsWith(".json") }) {
                            openSamples(nextDirectory)
                        } else {
                            startActivity(Intent(this@A2UIAssetBrowserActivity, A2UIAssetBrowserActivity::class.java).apply {
                                putExtra(EXTRA_DIRECTORY, nextDirectory)
                            })
                        }
                    }
                }
            }
        }
        layout.addView(list, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        if (entries.isEmpty()) layout.addView(TextView(this).apply { text = "暂无样本" })
        setContentView(layout)
    }

    private fun openSamples(directory: String) {
        startActivity(Intent(this, A2UIComponentTestActivity::class.java).apply {
            putExtra(A2UIComponentTestActivity.EXTRA_ROOT_DIR, directory)
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_DIR, "")
            putExtra(A2UIComponentTestActivity.EXTRA_COMPONENT_NAME, directory.substringAfterLast('/'))
            putExtra(A2UIComponentTestActivity.EXTRA_TITLE_SUFFIX, "回归样本")
        })
    }
}
