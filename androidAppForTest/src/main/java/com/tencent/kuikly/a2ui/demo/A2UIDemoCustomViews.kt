package com.tencent.kuikly.a2ui.demo

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderViewExport

internal class A2UIDemoWebView(context: Context) : FrameLayout(context), IKuiklyRenderViewExport {
    private val webView = WebView(context).apply {
        webViewClient = WebViewClient()
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    init {
        addView(webView)
    }

    override fun setProp(propKey: String, propValue: Any): Boolean {
        if (propKey == "source") {
            val url = propValue.toString()
            if (url.isNotEmpty()) webView.loadUrl(url)
            return true
        }
        return super.setProp(propKey, propValue)
    }

    override fun resetProp(propKey: String): Boolean {
        if (propKey == "source") {
            webView.loadUrl("about:blank")
            return true
        }
        return super.resetProp(propKey)
    }
}

internal class A2UIDemoTextView(context: Context) : FrameLayout(context), IKuiklyRenderViewExport {
    private val textView = TextView(context).apply {
        textSize = 16f
        gravity = Gravity.CENTER
        setTextColor(Color.parseColor("#333333"))
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    init {
        setBackgroundColor(Color.parseColor("#E8F5E9"))
        addView(textView)
    }

    override fun setProp(propKey: String, propValue: Any): Boolean {
        when (propKey) {
            "text" -> textView.text = propValue.toString()
            "textColor" -> {
                val value = propValue.toString().trim()
                val color = value.toLongOrNull()?.toInt() ?: runCatching { Color.parseColor(value) }.getOrNull()
                if (color != null) textView.setTextColor(color)
            }
            else -> return super.setProp(propKey, propValue)
        }
        return true
    }

    override fun resetProp(propKey: String): Boolean {
        when (propKey) {
            "text" -> textView.text = ""
            "textColor" -> textView.setTextColor(Color.parseColor("#333333"))
            else -> return super.resetProp(propKey)
        }
        return true
    }
}
