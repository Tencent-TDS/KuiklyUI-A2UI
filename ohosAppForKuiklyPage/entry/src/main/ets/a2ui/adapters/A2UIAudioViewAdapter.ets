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

import { KRValue, KuiklyRenderBaseView, KuiklyRenderCallback } from '@kuikly-open/render';
import { UIContext } from '@ohos.arkui.UIContext';
import { ComponentContent } from '@kit.ArkUI';
import {
  IKRAudioViewAdapter,
  IKRAudioViewController,
  IKRAudioViewListener
} from '@kuikly-open/render';
import {
  KRAudioPlayState,
  KRAudioView
} from '@kuikly-open/render';
import { media } from '@kit.MediaKit';
import { BusinessError } from '@kit.BasicServicesKit';
import { hilog } from '@kit.PerformanceAnalysisKit';

const AUDIO_TAG = 'A2UIAudioAdapter';

/**
 * A2UI HarmonyOS AudioPlayer adapter, backing the A2UI v0.9 "AudioPlayer" component (KRAudioView).
 *
 * Mirrors the Android `A2UIAudioViewAdapter` (ExoPlayer + PlayerControlView) and
 * iOS `A2UIAudioViewHandler` (AVPlayer + UIKit transport bar) roles: KRAudioView
 * (registered by core-render-ohos ViewsRegisterEntry) delegates the actual audio
 * playback + transport UI to this adapter. The A2UI AudioPlayer catalog entry
 * carries a `url` (and an optional `description`, rendered as a Text above the
 * player by the Kotlin protocol adapter); the implementation guide expects
 * native controls + scrubbing, so this uses `media.AVPlayer` for decoding and
 * an ArkUI Row (play/pause + slider + time labels) as the always-visible
 * transport bar.
 */
@Component
struct A2UIAudioPanel {
  @State audioController: A2UIAudioController | null = null;
  @State isPlaying: boolean = false;
  @State currentTimeMs: number = 0;
  @State totalTimeMs: number = 0;
  @State sliderValue: number = 0;
  private scrubbing: boolean = false;

  build() {
    Row() {
      // Play/pause tap target. Using a plain Row (not a Button) avoids ArkUI
      // Button's default 8-12vp inner padding, which was clipping the glyph.
      // SymbolGlyph draws the HarmonyOS system icon crisply and vertically
      // centered inside the 36vp square.
      Row() {
        SymbolGlyph(this.isPlaying ? $r('sys.symbol.pause_fill') : $r('sys.symbol.play_fill'))
          .fontSize(22)
          .fontColor([Color.Black]);
      }
      .width(36)
      .height(36)
      .justifyContent(FlexAlign.Center)
      .alignItems(VerticalAlign.Center)
      .borderRadius(18)
      .onClick(() => {
        if (this.isPlaying) {
          this.audioController?.pause();
        } else {
          this.audioController?.play();
        }
      });

      Slider({
        value: this.sliderValue,
        min: 0,
        max: 1000,
        step: 1,
        style: SliderStyle.OutSet
      })
        .layoutWeight(1)
        .margin({ left: 4, right: 4 })
        .onChange((value: number, mode: SliderChangeMode) => {
          if (mode === SliderChangeMode.Begin || mode === SliderChangeMode.Moving) {
            this.scrubbing = true;
            this.sliderValue = value;
          } else if (mode === SliderChangeMode.End || mode === SliderChangeMode.Click) {
            this.scrubbing = false;
            if (this.totalTimeMs > 0) {
              const targetMs = Math.floor((value / 1000) * this.totalTimeMs);
              this.audioController?.seekToTime(targetMs);
            }
          }
        });

      // Show "current / total". Auto width + left align keeps the label
      // visually adjacent to the slider instead of hugging the right edge.
      Text(A2UIAudioPanel.formatMs(this.currentTimeMs) + ' / ' + A2UIAudioPanel.formatMs(this.totalTimeMs))
        .fontSize(12)
        .fontColor('#666666')
        .textAlign(TextAlign.Start)
        .margin({ left: 4 });
    }
    .width('100%')
    .height('100%')
    .padding({ left: 8, right: 12 })
    .backgroundColor('#F5F5F5')
    .borderRadius(8)
    .alignItems(VerticalAlign.Center);
  }

  aboutToAppear(): void {
    this.audioController?.panelController.setPlayStateCallback((playing: boolean) => {
      this.isPlaying = playing;
    });
    this.audioController?.panelController.setTimeCallback((currentMs: number, totalMs: number) => {
      this.currentTimeMs = currentMs;
      this.totalTimeMs = totalMs;
      if (!this.scrubbing && totalMs > 0) {
        this.sliderValue = Math.floor((currentMs / totalMs) * 1000);
      }
    });
  }

  aboutToDisappear(): void {
    this.audioController?.panelController.delete();
  }

  static formatMs(ms: number): string {
    const totalSec = Math.max(0, Math.floor(ms / 1000));
    const m = Math.floor(totalSec / 60);
    const s = totalSec % 60;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  }
}

@Builder
function createA2UIAudioPanel(view: KuiklyRenderBaseView) {
  A2UIAudioPanel({
    audioController: (view as KRAudioView).controller! as A2UIAudioController
  });
}

/**
 * State bridge between the KRAudioView controller (outside the @Component tree)
 * and the A2UIAudioPanel @State variables.
 */
class A2UIAudioPanelController {
  private playStateCallback: ((playing: boolean) => void) | undefined = undefined;
  private timeCallback: ((currentMs: number, totalMs: number) => void) | undefined = undefined;

  setPlayStateCallback(cb: (playing: boolean) => void): void {
    this.playStateCallback = cb;
  }

  setTimeCallback(cb: (currentMs: number, totalMs: number) => void): void {
    this.timeCallback = cb;
  }

  notifyPlayState(playing: boolean): void {
    if (this.playStateCallback) {
      this.playStateCallback(playing);
    }
  }

  notifyTime(currentMs: number, totalMs: number): void {
    if (this.timeCallback) {
      this.timeCallback(currentMs, totalMs);
    }
  }

  delete(): void {
    this.playStateCallback = undefined;
    this.timeCallback = undefined;
  }
}

/**
 * IKRAudioViewController implementation. KRAudioView forwards A2UI-generated
 * props/commands (setSrc / play / pause / stop / setRate / seekToTime) to this
 * controller, which drives an underlying `media.AVPlayer` instance.
 */
class A2UIAudioController implements IKRAudioViewController {
  panelController: A2UIAudioPanelController = new A2UIAudioPanelController();
  listeners = new Set<IKRAudioViewListener>();
  private avPlayer: media.AVPlayer | null = null;
  private pendingSrc: string = '';
  private preparing: boolean = false;
  private prepared: boolean = false;
  private autoPlayOnPrepared: boolean = false;
  private pendingSeekMs: number = -1;
  private pendingRate: number = -1;
  private totalTimeMs: number = 0;
  private lastReportedCurrentMs: number = 0;
  private timeTicker: number = -1;

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    return false;
  }

  addListener(listener: IKRAudioViewListener): void {
    this.listeners.add(listener);
  }

  removeListener(listener: IKRAudioViewListener): void {
    this.listeners.delete(listener);
  }

  setSrc(src: string): void {
    if (!src || src === this.pendingSrc) {
      return;
    }
    this.pendingSrc = src;
    this.prepared = false;
    this.preparing = false;
    // Recreate the player for a new source (AVPlayer is single-source).
    this.releasePlayer().then(() => {
      this.createAndPrepare(src);
    });
  }

  play(): void {
    if (!this.avPlayer) {
      // No player yet; mark auto-play once prepare finishes.
      this.autoPlayOnPrepared = true;
      if (this.pendingSrc && !this.preparing) {
        this.createAndPrepare(this.pendingSrc);
      }
      return;
    }
    if (!this.prepared) {
      this.autoPlayOnPrepared = true;
      return;
    }
    this.avPlayer.play().catch((err: BusinessError) => {
      hilog.error(0x0000, AUDIO_TAG, 'play failed: %{public}s', JSON.stringify(err));
      this.notifyState(KRAudioPlayState.Failed);
    });
  }

  pause(): void {
    if (this.avPlayer && this.prepared) {
      this.avPlayer.pause().catch((err: BusinessError) => {
        hilog.error(0x0000, AUDIO_TAG, 'pause failed: %{public}s', JSON.stringify(err));
      });
    }
  }

  stop(): void {
    this.autoPlayOnPrepared = false;
    if (this.avPlayer && this.prepared) {
      this.avPlayer.stop().catch((err: BusinessError) => {
        hilog.error(0x0000, AUDIO_TAG, 'stop failed: %{public}s', JSON.stringify(err));
      });
    }
    // Reset UI back to head.
    this.panelController.notifyPlayState(false);
    this.panelController.notifyTime(0, this.totalTimeMs);
  }

  setRate(rate: number): void {
    // AVPlayer speed enum: 0.75x, 1.00x, 1.25x, 1.75x, 2.00x.
    const speed = A2UIAudioController.pickSpeed(rate);
    if (this.avPlayer && this.prepared) {
      this.avPlayer.setSpeed(speed);
    } else {
      this.pendingRate = rate;
    }
  }

  seekToTime(seekToTimeMs: number): void {
    if (this.avPlayer && this.prepared && this.totalTimeMs > 0) {
      const clamped = Math.max(0, Math.min(seekToTimeMs, this.totalTimeMs));
      this.avPlayer.seek(clamped, media.SeekMode.SEEK_PREV_SYNC);
    } else {
      this.pendingSeekMs = seekToTimeMs;
    }
  }

  private async createAndPrepare(src: string): Promise<void> {
    this.preparing = true;
    try {
      const player = await media.createAVPlayer();
      this.avPlayer = player;
      this.wireEvents(player);
      // Assign URL kicks off the AVPlayer state machine (idle -> initialized).
      player.url = src;
    } catch (err) {
      this.preparing = false;
      hilog.error(0x0000, AUDIO_TAG, 'createAVPlayer failed: %{public}s', JSON.stringify(err));
      this.notifyState(KRAudioPlayState.Failed);
    }
  }

  private wireEvents(player: media.AVPlayer): void {
    player.on('stateChange', async (state: media.AVPlayerState) => {
      switch (state) {
        case 'idle':
          break;
        case 'initialized':
          // Move to prepared. `prepare` returns void; readiness signaled by
          // the next stateChange('prepared').
          try {
            await player.prepare();
          } catch (err) {
            hilog.error(0x0000, AUDIO_TAG, 'prepare failed: %{public}s', JSON.stringify(err));
            this.notifyState(KRAudioPlayState.Failed);
          }
          break;
        case 'prepared':
          this.prepared = true;
          this.preparing = false;
          this.totalTimeMs = player.duration >= 0 ? player.duration : 0;
          this.panelController.notifyTime(0, this.totalTimeMs);
          // Apply any pending settings queued before prepare completed.
          if (this.pendingRate > 0) {
            player.setSpeed(A2UIAudioController.pickSpeed(this.pendingRate));
            this.pendingRate = -1;
          }
          if (this.pendingSeekMs >= 0 && this.totalTimeMs > 0) {
            const target = Math.max(0, Math.min(this.pendingSeekMs, this.totalTimeMs));
            player.seek(target, media.SeekMode.SEEK_PREV_SYNC);
            this.pendingSeekMs = -1;
          }
          if (this.autoPlayOnPrepared) {
            this.autoPlayOnPrepared = false;
            player.play().catch((err: BusinessError) => {
              hilog.error(0x0000, AUDIO_TAG, 'auto-play failed: %{public}s', JSON.stringify(err));
              this.notifyState(KRAudioPlayState.Failed);
            });
          }
          break;
        case 'playing':
          this.startTicker();
          this.panelController.notifyPlayState(true);
          this.notifyState(KRAudioPlayState.Playing);
          break;
        case 'paused':
          this.stopTicker();
          this.panelController.notifyPlayState(false);
          this.notifyState(KRAudioPlayState.Paused);
          break;
        case 'completed':
          this.stopTicker();
          this.panelController.notifyPlayState(false);
          // Report time as fully at duration for the last frame.
          this.panelController.notifyTime(this.totalTimeMs, this.totalTimeMs);
          this.notifyState(KRAudioPlayState.PlayEnd);
          break;
        case 'stopped':
          this.stopTicker();
          this.prepared = false;
          this.panelController.notifyPlayState(false);
          this.panelController.notifyTime(0, this.totalTimeMs);
          break;
        case 'released':
          this.stopTicker();
          break;
        case 'error':
          this.stopTicker();
          this.notifyState(KRAudioPlayState.Failed);
          break;
        default:
          break;
      }
    });

    player.on('timeUpdate', (currentMs: number) => {
      this.lastReportedCurrentMs = currentMs;
      this.panelController.notifyTime(currentMs, this.totalTimeMs);
      this.listeners.forEach((l) => {
        if (l.onPlayTimeDidChanged) {
          l.onPlayTimeDidChanged(currentMs, this.totalTimeMs, this);
        }
      });
    });

    player.on('error', (err: BusinessError) => {
      hilog.error(0x0000, AUDIO_TAG, 'AVPlayer error: %{public}s', JSON.stringify(err));
      this.notifyState(KRAudioPlayState.Failed);
    });
  }

  // The AVPlayer's built-in 'timeUpdate' fires only on discrete jumps; add a
  // gentle ticker so the slider moves smoothly while playing.
  private startTicker(): void {
    this.stopTicker();
    this.timeTicker = setInterval(() => {
      if (this.avPlayer && this.prepared) {
        const cur = this.avPlayer.currentTime;
        if (cur >= 0) {
          this.lastReportedCurrentMs = cur;
          this.panelController.notifyTime(cur, this.totalTimeMs);
        }
      }
    }, 500);
  }

  private stopTicker(): void {
    if (this.timeTicker !== -1) {
      clearInterval(this.timeTicker);
      this.timeTicker = -1;
    }
  }

  private notifyState(state: KRAudioPlayState): void {
    this.listeners.forEach((l) => {
      if (l.onPlayStateDidChanged) {
        l.onPlayStateDidChanged(state, this);
      }
    });
  }

  private async releasePlayer(): Promise<void> {
    this.stopTicker();
    if (this.avPlayer) {
      const p = this.avPlayer;
      this.avPlayer = null;
      this.prepared = false;
      try {
        await p.release();
      } catch (err) {
        hilog.warn(0x0000, AUDIO_TAG, 'release failed: %{public}s', JSON.stringify(err));
      }
    }
  }

  private static pickSpeed(rate: number): media.PlaybackSpeed {
    // Snap host-provided rate to the nearest supported AVPlayer speed.
    if (rate <= 0.85) {
      return media.PlaybackSpeed.SPEED_FORWARD_0_75_X;
    } else if (rate <= 1.10) {
      return media.PlaybackSpeed.SPEED_FORWARD_1_00_X;
    } else if (rate <= 1.35) {
      return media.PlaybackSpeed.SPEED_FORWARD_1_25_X;
    } else if (rate <= 1.85) {
      return media.PlaybackSpeed.SPEED_FORWARD_1_75_X;
    }
    return media.PlaybackSpeed.SPEED_FORWARD_2_00_X;
  }
}

/**
 * Adapter registered into `KuiklyRenderAdapterManager.krAudioViewAdapter`
 * by A2UIDemoAbility.onCreate(), enabling A2UI's `AudioPlayer` component to
 * render an actual native audio player on HarmonyOS.
 */
export class A2UIAudioViewAdapter implements IKRAudioViewAdapter {
  createController(): IKRAudioViewController {
    return new A2UIAudioController();
  }

  createAudioView(ctx: UIContext, view: KuiklyRenderBaseView): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      ctx,
      wrapBuilder<[KuiklyRenderBaseView]>(createA2UIAudioPanel),
      view
    );
  }
}
