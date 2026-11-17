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

package com.tencent.kuikly.a2ui.demo.adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.Player.STATE_BUFFERING
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.tencent.kuikly.core.render.android.adapter.IKRVideoView
import com.tencent.kuikly.core.render.android.adapter.IKRVideoViewAdapter
import com.tencent.kuikly.core.render.android.adapter.IKRVideoViewListener
import com.tencent.kuikly.core.render.android.expand.component.KRVideoPlayState
import com.tencent.kuikly.core.render.android.expand.component.KRVideoViewContentMode

/**
 * Video player adapter for the A2UI demo, backing the A2UI v0.9 "Video" component.
 *
 * KRVideoView (created by A2UISurfaceView) delegates the actual playback surface to
 * this adapter via [createVideoView]. The A2UI Video catalog entry only specifies a
 * `url`; the implementation guide expects native controls and scrubbing support, so
 * the underlying ExoPlayer view enables its controller and auto-plays once ready.
 */
class A2UIVideoViewAdapter : IKRVideoViewAdapter {
    override fun createVideoView(
        context: Context,
        src: String,
        listener: IKRVideoViewListener
    ): IKRVideoView {
        return A2UIVideoView(context, src, listener)
    }
}

private class A2UIVideoView(
    context: Context,
    src: String,
    listener: IKRVideoViewListener
) : PlayerView(context), IKRVideoView {

    private val exoPlayer = ExoPlayer.Builder(context)
        .setTrackSelector(DefaultTrackSelector())
        .build()

    /**
     * PR-9 (v1.0) `posterUrl` support.
     *
     * A2UI v1.0 adds an optional poster image shown before playback begins.
     * The core Kotlin [com.tencent.kuikly.a2ui.dsl.A2UIApplier] forwards the
     * resolved URL as a generic `posterUrl` prop through
     * [IKRVideoView.setProp], and we handle it here as a transient overlay
     * [ImageView] placed on top of the ExoPlayer surface. The overlay is
     * hidden the moment ExoPlayer renders its first frame (see
     * [onRenderedFirstFrame]) so playback smoothly replaces the cover image.
     *
     * Images are loaded on a background thread to avoid StrictMode / main-thread
     * network violations; decoding errors are tolerated silently (the overlay
     * just stays blank, which is still better than a video blank).
     */
    private var posterOverlay: ImageView? = null
    private var posterLoadedUrl: String? = null

    init {
        // Per A2UI basic catalog guide: render with user controls + scrubbing enabled.
        useController = true
        player = exoPlayer
        exoPlayer.addMediaItem(MediaItem.fromUri(Uri.parse(src)))
        exoPlayer.prepare()
        // Auto-play the trailer once it is opened (typically inside a Modal).
        exoPlayer.playWhenReady = true

        exoPlayer.addListener(object : Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    STATE_ENDED ->
                        listener.videoPlayStateDidChangedWithState(
                            KRVideoPlayState.KRVideoPlayStatePlayEnd, mapOf()
                        )
                    STATE_BUFFERING ->
                        listener.videoPlayStateDidChangedWithState(
                            KRVideoPlayState.KRVideoPlayStateCaching, mapOf()
                        )
                    STATE_READY ->
                        if (exoPlayer.playWhenReady) {
                            listener.videoPlayStateDidChangedWithState(
                                KRVideoPlayState.KRVideoPlayStatePlaying, mapOf()
                            )
                        } else {
                            listener.videoPlayStateDidChangedWithState(
                                KRVideoPlayState.KRVideoPlayStatePaused, mapOf()
                            )
                        }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                listener.videoPlayStateDidChangedWithState(
                    KRVideoPlayState.KRVideoPlayStateFaild, mapOf()
                )
            }

            override fun onRenderedFirstFrame() {
                // Hide the poster overlay (if any) when the first video frame
                // arrives. This is the exact moment the cover should fade out.
                posterOverlay?.visibility = View.GONE
                listener.videoFirstFrameDidDisplay()
            }
        })
    }

    override fun preplay() {}

    override fun play() {
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun setVideoContentMode(videoViewContentMode: KRVideoViewContentMode) {
        resizeMode = when (videoViewContentMode) {
            KRVideoViewContentMode.KRVideoViewContentModeScaleAspectFill ->
                com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            KRVideoViewContentMode.KRVideoViewContentModeScaleToFill ->
                com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
            else ->
                com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }

    override fun setMuted(muted: Boolean) {
        exoPlayer.volume = if (muted) 0f else 1f
    }

    override fun setRate(rate: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(rate)
    }

    override fun seekToTime(seekToTimeMs: Long) {
        exoPlayer.seekTo(seekToTimeMs)
    }

    override fun setProp(propKey: String, propValue: Any): Boolean {
        if (propKey == "posterUrl") {
            val url = propValue.toString()
            if (url.isEmpty() || url == posterLoadedUrl) return true
            applyPosterUrl(url)
            return true
        }
        return false
    }

    /**
     * Lazily create a full-bleed ImageView on top of the ExoPlayer surface
     * and load the given URL into it. Called from [setProp] when the A2UI
     * core forwards a v1.0 `posterUrl`.
     */
    private fun applyPosterUrl(url: String) {
        posterLoadedUrl = url
        val overlay: ImageView = posterOverlay ?: ImageView(context).also { iv ->
            iv.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            iv.setBackgroundColor(Color.BLACK)
            // PlayerView extends FrameLayout internally, so we can stack the
            // overlay directly on top. Insert as the LAST child so it draws
            // above the SurfaceView; the player's media controller still
            // intercepts taps because PlayerView dispatches touches via its
            // own onTouchEvent chain. The overlay is removed-from-view via
            // visibility=GONE on first rendered frame.
            addView(iv)
            posterOverlay = iv
        }
        overlay.visibility = View.VISIBLE

        // Load on a background thread so network IO never blocks the UI.
        // Keep it dependency-free (no Glide/Coil) — this is a demo adapter,
        // production hosts are expected to swap in their own image pipeline.
        Thread {
            runCatching {
                val connection = java.net.URL(url).openConnection()
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000
                connection.getInputStream().use { stream ->
                    val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                    post {
                        // Only apply if the URL is still the current one;
                        // the user may have triggered another update meanwhile.
                        if (posterLoadedUrl == url && bitmap != null) {
                            overlay.setImageBitmap(bitmap)
                        }
                    }
                }
            }.onFailure {
                // Silent fallback: overlay stays black, video will play as usual.
            }
        }.apply { isDaemon = true }.start()
    }

    override fun call(method: String, params: String?) {}
}
