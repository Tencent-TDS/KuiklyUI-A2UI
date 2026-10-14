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
  IKRVideoViewAdapter,
  IKRVideoViewController,
  IKRVideoViewListener
} from '@kuikly-open/render';
import {
  KRVideoPlayState,
  KRVideoView,
  KRVideoViewContentMode
} from '@kuikly-open/render';

/**
 * A2UI HarmonyOS Video adapter, backing the A2UI v0.9 "Video" component (KRVideoView).
 *
 * Mirrors the Android `A2UIVideoViewAdapter` and iOS `A2UIVideoViewHandler` roles:
 * KRVideoView (auto-registered by core-render-ohos) delegates the actual playback
 * surface to this adapter. The A2UI Video catalog entry only specifies a `url`;
 * the implementation guide expects native controls and scrubbing support, so the
 * underlying ArkUI Video component has `.controls(true)` enabled.
 */
@Component
struct A2UIVideoPanel {
  @State videoViewController: A2UIVideoController | null = null;
  @State muted: boolean = false;
  @State rate: number = 1;
  @State imageFit: ImageFit = ImageFit.Cover;
  // `src` MUST be an @State so the ArkUI Video re-reads it when the URL arrives
  // asynchronously (e.g. A2UI `url: {"path": "/videoUrl"}` resolved from a later
  // `updateDataModel`). Without @State, the controller's plain field update is
  // invisible to the declarative UI and the video stays blank.
  @State src: string = '';
  totalTime: number = 0;

  build() {
    Video({
      controller: this.videoViewController?.viewController,
      src: this.src,
      currentProgressRate: this.rate
    })
      .controls(true) // A2UI Basic Catalog: render with user controls + scrubbing enabled.
      .muted(this.muted)
      .objectFit(this.imageFit)
      .onStart(() => {
        this.videoViewController?.onPlayStateDidChanged(KRVideoPlayState.Playing);
      })
      .onPause(() => {
        this.videoViewController?.onPlayStateDidChanged(KRVideoPlayState.Paused);
      })
      .onFinish(() => {
        this.videoViewController?.onPlayStateDidChanged(KRVideoPlayState.PlayEnd);
      })
      .onStop(() => {
        this.videoViewController?.onPlayStateDidChanged(KRVideoPlayState.PlayEnd);
      })
      .onError(() => {
        this.videoViewController?.onPlayStateDidChanged(KRVideoPlayState.Failed);
      })
      .onPrepared((params) => {
        this.videoViewController?.onFirstFrameDidDisplay();
        this.totalTime = params.duration;
      })
      .onUpdate((params) => {
        this.videoViewController?.onPlayTimeDidChanged(params.time, this.totalTime);
      });
  }

  aboutToAppear(): void {
    // Sync the current src (may already be set if data arrived before the
    // ArkUI @Component was built, e.g. literal-URL cases).
    if (this.videoViewController != null) {
      this.src = this.videoViewController.videoSrc;
    }
    this.videoViewController?.panelController.setSrcCallback((src: string) => {
      this.src = src;
    });
    this.videoViewController?.panelController.setMutedCallback((muted: boolean) => {
      this.muted = muted;
    });
    this.videoViewController?.panelController.setRateCallback((rate: number) => {
      this.rate = rate;
    });
    this.videoViewController?.panelController.setResizeModeCallback((imageFit: ImageFit) => {
      this.imageFit = imageFit;
    });
  }

  aboutToDisappear(): void {
    this.videoViewController?.panelController.delete();
  }
}

@Builder
function createA2UIVideoPanel(view: KuiklyRenderBaseView) {
  A2UIVideoPanel({
    videoViewController: (view as KRVideoView).controller! as A2UIVideoController
  });
}

/**
 * State bridge between the KRVideoView controller (which lives outside the
 * @Component tree) and the actual A2UIVideoPanel @Component's @State variables.
 */
class A2UIVideoPanelController {
  private srcCallback: Function | undefined = () => {
  };
  private mutedCallback: Function | undefined = () => {
  };
  private rateCallback: Function | undefined = () => {
  };
  private imageFitCallback: Function | undefined = () => {
  };

  setSrcCallback(callback: (src: string) => void): void {
    this.srcCallback = callback;
  }

  setSrc(src: string): void {
    if (this.srcCallback) {
      this.srcCallback(src);
    }
  }

  setMutedCallback(callback: (muted: boolean) => void): void {
    this.mutedCallback = callback;
  }

  setRateCallback(callback: (rate: number) => void): void {
    this.rateCallback = callback;
  }

  setResizeModeCallback(callback: (imageFit: ImageFit) => void): void {
    this.imageFitCallback = callback;
  }

  setMuted(muted: boolean): void {
    if (this.mutedCallback) {
      this.mutedCallback(muted);
    }
  }

  setRate(rate: number): void {
    if (this.rateCallback) {
      this.rateCallback(rate);
    }
  }

  setResizeMode(imageFit: ImageFit): void {
    if (this.imageFitCallback) {
      this.imageFitCallback(imageFit);
    }
  }

  delete(): void {
    this.srcCallback = undefined;
    this.mutedCallback = undefined;
    this.rateCallback = undefined;
    this.imageFitCallback = undefined;
  }
}

/**
 * IKRVideoViewController implementation. KRVideoView forwards A2UI-generated
 * props/commands (setSrc / play / pause / stop / setMuted / setRate / setVideoContentMode / seekToTime)
 * to this controller, which then drives the underlying ArkUI Video component.
 */
class A2UIVideoController implements IKRVideoViewController {
  viewController: VideoController = new VideoController();
  panelController: A2UIVideoPanelController = new A2UIVideoPanelController();
  videoSrc: string = '';
  listeners = new Set<IKRVideoViewListener>();

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    return false;
  }

  addListener(listener: IKRVideoViewListener): void {
    this.listeners.add(listener);
  }

  removeListener(listener: IKRVideoViewListener): void {
    this.listeners.delete(listener);
  }

  setSrc(src: string): void {
    const resdir = getContext().resourceDir;
    let resolved: string;
    if (src.startsWith('assets://')) {
      const trailing = src.substring(8);
      resolved = resdir + trailing;
    } else {
      resolved = src;
    }
    this.videoSrc = resolved;
    // Forward to the ArkUI @State via the panel controller so declarative
    // rebinding kicks in. This is what makes late-arriving `updateDataModel`
    // (e.g. `url: {"path": "/videoUrl"}`) actually reach the Video component.
    this.panelController.setSrc(resolved);
  }

  preplay(): void {
    // this.viewController.setCurrentTime(0.04)
  }

  play(): void {
    this.viewController.start();
  }

  pause(): void {
    this.viewController.pause();
  }

  stop(): void {
    this.viewController.stop();
  }

  setVideoContentMode(videoViewContentMode: KRVideoViewContentMode): void {
    if (videoViewContentMode == KRVideoViewContentMode.Contain) {
      this.panelController.setResizeMode(ImageFit.Contain);
    } else if (videoViewContentMode == KRVideoViewContentMode.Cover) {
      this.panelController.setResizeMode(ImageFit.Cover);
    } else if (videoViewContentMode == KRVideoViewContentMode.Stretch) {
      this.panelController.setResizeMode(ImageFit.Fill);
    }
  }

  setMuted(muted: boolean): void {
    this.panelController.setMuted(muted);
  }

  setRate(rate: number): void {
    this.panelController.setRate(rate);
  }

  seekToTime(seekToTimeMs: number): void {
    this.viewController.setCurrentTime(seekToTimeMs / 1000);
  }

  onPlayStateDidChanged = (state: KRVideoPlayState) => {
    this.listeners.forEach((listener) => {
      if (listener.onPlayStateDidChanged) {
        listener.onPlayStateDidChanged(state, this);
      }
    });
  };
  onPlayTimeDidChanged = (currentTime: number, totalTime: number) => {
    this.listeners.forEach((listener) => {
      if (listener.onPlayTimeDidChanged) {
        listener.onPlayTimeDidChanged(currentTime, totalTime, this);
      }
    });
  };
  onFirstFrameDidDisplay = () => {
    this.listeners.forEach((listener) => {
      if (listener.onFirstFrameDidDisplay) {
        listener.onFirstFrameDidDisplay(this);
      }
    });
  };
  onCustomEventWithInfo = (info: Map<string, string>) => {
    this.listeners.forEach((listener) => {
      if (listener.onCustomEventWithInfo) {
        listener.onCustomEventWithInfo(this, info);
      }
    });
  };
}

/**
 * Adapter registered into `KuiklyRenderAdapterManager.krVideoViewAdapter`
 * by A2UIDemoAbility.onCreate(), enabling A2UI's `Video` component to render
 * an actual native video panel on HarmonyOS.
 */
export class A2UIVideoViewAdapter implements IKRVideoViewAdapter {
  createController(): IKRVideoViewController {
    return new A2UIVideoController();
  }

  createVideoView(ctx: UIContext, view: KuiklyRenderBaseView): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      ctx,
      wrapBuilder<[KuiklyRenderBaseView]>(createA2UIVideoPanel),
      view
    );
  }
}
