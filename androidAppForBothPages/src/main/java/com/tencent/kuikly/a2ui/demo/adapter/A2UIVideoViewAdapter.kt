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
import android.net.Uri
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
        return false
    }

    override fun call(method: String, params: String?) {}
}
