package com.tencent.kuikly.a2ui.demo

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SizeF
import android.widget.Toast
import com.tencent.kuikly.a2ui.render.android.A2UIAndroidCustomViewRegistry
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView

internal object A2UIDemoCustomRegistry {
    fun install(context: Context) {
        val applicationContext = context.applicationContext
        A2UISurfaceView.registerCustomRenderView(
            viewName = "A2UIWeb",
            creator = { A2UIDemoWebView(it) },
            measurer = A2UIAndroidCustomViewRegistry.A2UICustomViewMeasurer { widthDp, _ -> SizeF(widthDp, 240f) }
        )
        A2UISurfaceView.registerCustomRenderView(
            viewName = "MyA2UIView",
            creator = { A2UIDemoTextView(it) },
            measurer = A2UIAndroidCustomViewRegistry.A2UICustomViewMeasurer { widthDp, _ -> SizeF(widthDp, 200f) }
        )
        A2UISurfaceView.registerCustomFunction("getGreeting", A2UISurfaceView.RETURN_TYPE_STRING) { args ->
            "Hello, ${args.optString("name", "World")}!"
        }
        A2UISurfaceView.registerCustomFunction("formatPhone", A2UISurfaceView.RETURN_TYPE_STRING) { args ->
            val phone = args.optString("phone", "")
            if (phone.length == 11) "${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}" else phone
        }
        A2UISurfaceView.registerCustomFunction("isVip", A2UISurfaceView.RETURN_TYPE_BOOLEAN) { args ->
            args.optInt("level", 0) >= 3
        }
        A2UISurfaceView.registerCustomFunction("calculateDiscount", A2UISurfaceView.RETURN_TYPE_NUMBER) { args ->
            Math.round(args.optDouble("price", 0.0) * args.optDouble("rate", 1.0) * 100) / 100.0
        }
        A2UISurfaceView.registerCustomFunction("logEvent", A2UISurfaceView.RETURN_TYPE_VOID) { args ->
            val event = args.optString("event", "")
            val data = args.optString("data", "")
            Log.i("A2UIDemoCustom", "logEvent: event=$event, data=$data")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, "logEvent: $event ($data)", Toast.LENGTH_SHORT).show()
            }
            null
        }
        A2UISurfaceView.registerCustomFunction("buildSummary", A2UISurfaceView.RETURN_TYPE_STRING) { args ->
            "项目「${args.optString("name", "Unknown")}」当前状态: ${args.optString("status", "unknown")}，共 ${args.optInt("count", 0)} 个任务"
        }
    }
}
