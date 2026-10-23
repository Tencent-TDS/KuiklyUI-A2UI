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

/**
 * Re-export A2UISurfaceView from @kuikly-a2ui-open/render module.
 *
 * This file serves as a convenience wrapper for the demo app pages.
 * The actual implementation lives in a2ui-render-ohos module, which uses
 * Kuikly's native rendering pipeline (aligned with Android/iOS architecture).
 */

import {
  A2UISurfaceView,
  A2UISurfaceViewController,
  A2UISurfaceViewListener
} from '@kuikly-a2ui-open/render';

export { A2UISurfaceView, A2UISurfaceViewController, A2UISurfaceViewListener };

/**
 * A2UISurface - Demo wrapper component.
 *
 * This is a convenience wrapper used by the demo app pages.
 * It accepts a JSON string of messages (as stored in test asset files),
 * unpacks them, and feeds each envelope to A2UISurfaceViewController.
 *
 * In production usage, you should use A2UISurfaceView directly and
 * feed individual envelope messages via writeStream().
 */
@Component
export struct A2UISurface {
  @Prop config: string = '{}';
  @Prop messages: string = '[]';
  @Prop finished: boolean = false;
  listener?: A2UISurfaceViewListener;

  build() {
    A2UISurfaceView({
      listener: this.listener,
      onReady: (ctrl: A2UISurfaceViewController) => {
        this.feedMessages(ctrl);
      }
    })
  }

  /**
   * Feed the pre-packaged messages to the controller.
   * Demo asset files store messages as a JSON array; we unpack and feed individually.
   */
  private feedMessages(ctrl: A2UISurfaceViewController): void {
    if (this.messages.length === 0 || this.messages === '[]') {
      return;
    }

    try {
      const parsed = JSON.parse(this.messages) as Object;
      if (Array.isArray(parsed)) {
        // JSON array of envelopes - feed each one individually
        for (const envelope of parsed as Object[]) {
          ctrl.writeStream(JSON.stringify(envelope));
        }
      } else {
        // Single envelope object
        ctrl.writeStream(this.messages);
      }

      if (this.finished) {
        ctrl.finishStream();
      }
    } catch (e) {
      console.error(`[A2UISurface] Failed to parse messages: ${e}`);
      if (this.listener?.onError) {
        this.listener.onError(`Failed to parse messages: ${e}`);
      }
    }
  }
}
