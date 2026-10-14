package com.tencent.kuikly.a2ui.demo

import android.app.Application
import com.tencent.kuikly.a2ui.demo.adapter.A2UIAudioViewAdapter
import com.tencent.kuikly.a2ui.demo.adapter.A2UIImageAdapter
import com.tencent.kuikly.a2ui.demo.adapter.A2UIVideoViewAdapter
import com.tencent.kuikly.a2ui.render.android.A2UIAndroidNativeMethodHandler
import com.tencent.kuikly.core.render.android.adapter.KuiklyRenderAdapterManager

/**
 * A2UI Demo Application.
 * Registers the image adapter for KRImageView to load images.
 */
class A2UIDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        // Register image adapter
        if (KuiklyRenderAdapterManager.krImageAdapter == null) {
            KuiklyRenderAdapterManager.krImageAdapter = A2UIImageAdapter(this)
        }
        // Register video adapter so the A2UI v0.9 "Video" component (KRVideoView) can play.
        if (KuiklyRenderAdapterManager.krVideoViewAdapter == null) {
            KuiklyRenderAdapterManager.krVideoViewAdapter = A2UIVideoViewAdapter()
        }
        // Register audio adapter so the A2UI v0.9 "AudioPlayer" component (KRAudioView) can play.
        if (KuiklyRenderAdapterManager.krAudioViewAdapter == null) {
            KuiklyRenderAdapterManager.krAudioViewAdapter = A2UIAudioViewAdapter()
        }
        // Install the A2UI native method handler so that A2UI content embedded
        // inside a Kuikly page can pop DatePickerDialogs, execute client-side
        // functionCall actions, and resolve user-registered custom functions.
        A2UIAndroidNativeMethodHandler.install(this)
    }

    companion object {
        lateinit var application: Application
            private set
    }
}
