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
import com.google.android.exoplayer2.ui.PlayerControlView
import com.tencent.kuikly.core.render.android.adapter.IKRAudioView
import com.tencent.kuikly.core.render.android.adapter.IKRAudioViewAdapter
import com.tencent.kuikly.core.render.android.adapter.IKRAudioViewListener
import com.tencent.kuikly.core.render.android.expand.component.KRAudioPlayState

/**
 * Audio player adapter for the A2UI demo, backing the A2UI v0.9 "AudioPlayer" component.
 *
 * KRAudioView (created by A2UISurfaceView) delegates the actual playback + transport
 * controls to this adapter via [createAudioView]. The A2UI AudioPlayer catalog entry
 * carries a `url` (and an optional `description`, rendered as a Text above the player);
 * the implementation guide expects native controls + scrubbing, so this uses ExoPlayer
 * for decoding and a [PlayerControlView] (play/pause + seek bar + time, no video surface)
 * as the always-visible transport bar.
 */
class A2UIAudioViewAdapter : IKRAudioViewAdapter {
    override fun createAudioView(
        context: Context,
        src: String,
        listener: IKRAudioViewListener
    ): IKRAudioView {
        return A2UIAudioView(context, src, listener)
    }
}

private class A2UIAudioView(
    context: Context,
    src: String,
    listener: IKRAudioViewListener
) : PlayerControlView(context), IKRAudioView {

    private val exoPlayer = ExoPlayer.Builder(context).build()

    init {
        // Always-visible transport bar (play/pause + scrubber + time labels).
        showTimeoutMs = 0
        player = exoPlayer
        exoPlayer.addMediaItem(MediaItem.fromUri(Uri.parse(src)))
        exoPlayer.prepare()
        // Audio should not auto-play; the user starts it via the native controls.
        exoPlayer.playWhenReady = false

        exoPlayer.addListener(object : Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    STATE_ENDED ->
                        listener.audioPlayStateDidChangedWithState(
                            KRAudioPlayState.KRAudioPlayStatePlayEnd, mapOf()
                        )
                    STATE_BUFFERING ->
                        listener.audioPlayStateDidChangedWithState(
                            KRAudioPlayState.KRAudioPlayStateCaching, mapOf()
                        )
                    STATE_READY ->
                        if (exoPlayer.playWhenReady) {
                            listener.audioPlayStateDidChangedWithState(
                                KRAudioPlayState.KRAudioPlayStatePlaying, mapOf()
                            )
                        } else {
                            listener.audioPlayStateDidChangedWithState(
                                KRAudioPlayState.KRAudioPlayStatePaused, mapOf()
                            )
                        }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                listener.audioPlayStateDidChangedWithState(
                    KRAudioPlayState.KRAudioPlayStateFailed, mapOf()
                )
            }

            override fun onEvents(player: com.google.android.exoplayer2.Player, events: com.google.android.exoplayer2.Player.Events) {
                listener.playTimeDidChangedWithCurrentTime(
                    player.currentPosition.coerceAtLeast(0L),
                    player.duration.coerceAtLeast(0L)
                )
            }
        })
    }

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
