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

import { router } from '@kit.ArkUI';
import {
  A2UISurfaceView,
  A2UISurfaceViewController
} from '@kuikly-a2ui-open/render';

interface GreetingArgs {
  name: string;
}

interface RendererFunctionCall {
  surfaceId: string;
  functionCallId: string;
  callFunction: Record<string, Object>;
}

interface RendererFunctionEnvelope {
  version: string;
  callRendererFunction: RendererFunctionCall;
}

interface MockUser {
  id: number;
  name: string;
  role: string;
}

interface AgentFunctionResponse {
  functionCallId: string;
  value: MockUser;
}

interface AgentFunctionResponseEnvelope {
  version: string;
  agentFunctionResponse: AgentFunctionResponse;
}

/**
 * A2UI v1.0 **RPC round-trip demo**, Native-page edition (OHOS).
 *
 * Counterpart to
 *  - `androidAppForNativePage/.../NativeRpcDemoActivity.kt`
 *  - `iosShared/Sources/A2UIRpcDemoViewController.swift`
 *
 * ### What this page exercises
 *
 * This page validates the full bidirectional A2UI v1.0 RPC loop on OHOS
 * and — importantly — does so **through the render-pod facade only**:
 * this file does NOT import `libkuikly_entry.so`. All RPC calls go
 * through instance methods on [A2UISurfaceViewController]
 * ([invokeAgentFunction] / [setRendererFunctionHandler] /
 * [respondToRpc] / [failRpc]), so host apps don't have to know about
 * the underlying NAPI shim.
 *
 * #### Outbound (renderer → agent)
 *
 *  1. Tapping "Fetch user from agent" calls
 *     [A2UISurfaceViewController.invokeAgentFunction], which routes
 *     through `a2uiNativeManager.invokeAgentFunctionInKmp` → the
 *     `a2uiInvokeAgentFunction` NAPI export → the Kotlin core's
 *     `A2UIBridgeManager.invokeAgentFunctionJson(…)`.
 *  2. The core mints a `callAgentFunction` envelope and sends it out
 *     through `A2UINativeMethod.SEND_RPC_ENVELOPE (methodId=108)`. On
 *     OHOS the envelope is pre-intercepted in `A2UIOhosPager.callNative`
 *     and routed to `A2UIBridgeModule.rpcEnvelope`, which looks up this
 *     surface's `instanceId` in the module-scope
 *     `a2uiRpcOutboundCallbackRegistry` and fires our
 *     [A2UISurfaceViewController.setRpcEnvelopeOutboundHandler] callback.
 *  3. Instead of sending the envelope over a real transport, we **mock
 *     the agent locally**: when a `callAgentFunction` envelope arrives,
 *     synthesise the matching `agentFunctionResponse` and feed it back
 *     to the core via [A2UISurfaceViewController.writeStream]. The core
 *     routes the response through its dispatcher and the completion JSON
 *     lands on our `onComplete` callback (automatically hopped onto the
 *     ArkTS main thread by `napi_create_threadsafe_function`).
 *
 * #### Inbound (agent → renderer)
 *
 *  1. Tapping "Mock agent → getGreeting" synthesises a `callRendererFunction`
 *     envelope locally and feeds it into [A2UISurfaceViewController.writeStream]
 *     — the demo's stand-in for "the agent transport just delivered a
 *     frame to the core".
 *  2. The Kotlin core's RPC dispatcher looks up the
 *     `rendererFunctionHandlers[instanceId]` entry that we installed
 *     on `onReady` via
 *     [A2UISurfaceViewController.setRendererFunctionHandler]. A reverse
 *     `napi_threadsafe_function` behind the scenes hops onto the ArkTS
 *     main thread and lands in [onInboundRendererFunction].
 *  3. The handler acks with
 *     [A2UISurfaceViewController.respondToRpc] /
 *     [A2UISurfaceViewController.failRpc] which produces a
 *     `rendererFunctionResponse` envelope — emitted back out through the
 *     same outbound sink used by the renderer→agent half. The event log
 *     shows all four hops end-to-end.
 */
@Entry
@Component
struct A2UIRpcDemoPage {
  @State status: string = 'Tap a button below to trigger an RPC.';
  @State logLines: string[] = [];

  /** The underlying surface controller, resolved via onReady. */
  private controller: A2UISurfaceViewController | null = null;

  /** Cached instanceId once the controller is ready. */
  @State instanceId: string = '';

  /** Stable surfaceId reused across every envelope on this page. */
  private readonly surfaceId: string = 'rpc-demo-native-ohos';

  /**
   * Monotonic counter for the mock agent's inbound requestIds — keeps
   * the event log readable when the user mashes the button multiple
   * times.
   */
  private mockRequestCounter: number = 0;

  /**
   * Minimal v1.0 `createSurface` envelope that paints a single Text
   * component. The demo's point is the RPC roundtrip, not the UI
   * tree, so we keep this deliberately tiny.
   */
  private readonly initialCreateSurfaceEnvelope: string = (
    '{"version":"1.0","createSurface":{"surfaceId":"rpc-demo-native-ohos","catalogId":"custom_functions_catalog_v1_0.json"}}\n' +
    '{"version":"1.0","updateComponents":{"surfaceId":"rpc-demo-native-ohos","components":[{"id":"root","component":"Column","children":["hint"]},{"id":"hint","component":"Text","text":"Tap a button below to trigger an RPC."}]}}\n'
  );

  /** Rolling log cap — matches the Android / iOS demos. */
  private static readonly MAX_LOG_LINES: number = 200;

  aboutToDisappear(): void {
    // Detach the inbound handler + outbound sink BEFORE dropping the
    // controller reference so a late Kotlin trampoline / outbound
    // envelope from the core's teardown path doesn't fire into a
    // torn-down page. The controller's own destroy() path ends up
    // doing this too, but doing it eagerly here matches the Android /
    // iOS demos.
    this.controller?.setRendererFunctionHandler(null);
    this.controller?.setRpcEnvelopeOutboundHandler(null);
    this.controller = null;
  }

  // ---------------------------------------------------------------
  //  Outbound: renderer → agent
  // ---------------------------------------------------------------

  private invokeAgentFetchUser(): void {
    if (this.controller === null || this.instanceId.length === 0) {
      this.setStatus('Surface not ready yet');
      return;
    }
    const argsJson = '{"id":42}';
    this.setStatus('Host -> invokeAgentFunction fetchUser');
    const reqId: string | null = this.controller.invokeAgentFunction(
      this.surfaceId,
      'fetchUser',
      argsJson,
      5_000, // timeoutMs
      (responseJson: string): void => {
        // Fires on the ArkTS main thread, courtesy of the reverse
        // threadsafe-function in napi_init.cpp.
        this.onAgentFunctionResponse(responseJson);
      }
    );
    if (reqId === null) {
      this.log('! invoke returned null (instance unknown, stream not v1.0, or libkuikly_entry.so too old)');
      this.setStatus('invokeAgentFunction failed (null reqId)');
      return;
    }
    this.log(`→ host invokeAgentFunction reqId=${reqId} args=${argsJson}`);
    // Note: the outbound envelope itself is emitted by the Kotlin core
    // via SEND_RPC_ENVELOPE (methodId=108), which lands in
    // onOutboundEnvelope() below. That function contains the mock-agent
    // that synthesises the matching agentFunctionResponse and feeds it
    // back through writeStream(), closing the loop without needing a
    // real network transport.
  }

  private onAgentFunctionResponse(responseJson: string): void {
    this.log(`← onComplete: ${responseJson}`);
    try {
      const resp = JSON.parse(responseJson) as Record<string, Object>;
      if (resp.result !== undefined) {
        this.setStatus(`fetchUser result: ${JSON.stringify(resp.result)}`);
      } else if (resp.error !== undefined) {
        const err = resp.error as Record<string, Object>;
        this.setStatus(`fetchUser error: ${err.code ?? ''}`);
      } else {
        this.setStatus('fetchUser: (empty response)');
      }
    } catch (e) {
      this.setStatus('fetchUser: (unparseable response)');
    }
  }

  // ---------------------------------------------------------------
  //  Inbound: mock agent → renderer
  // ---------------------------------------------------------------

  /**
   * Synthesise a `callRendererFunction` envelope and feed it into the
   * core via [A2UISurfaceViewController.writeStream] — the demo's
   * stand-in for "the agent transport just delivered a frame".
   *
   * Kotlin will parse the envelope, look up the handler we registered
   * in [onReady] via [a2uiSetRendererFunctionHandler], and invoke our
   * C trampoline; the napi shim then hops onto the ArkTS main thread
   * and lands in [onInboundRendererFunction].
   */
  private simulateAgentCallRendererFunction(): void {
    if (this.controller === null) {
      this.setStatus('Surface not ready yet');
      return;
    }
    this.mockRequestCounter += 1;
    const requestId = `req-${this.mockRequestCounter}`;
    const args: GreetingArgs = { name: 'Ada' };
    const callFunction: Record<string, Object> = {};
    callFunction['@call'] = 'getGreeting';
    callFunction['catalogId'] = 'custom_functions_catalog_v1_0.json';
    callFunction['args'] = args;
    const envelope: RendererFunctionEnvelope = {
      version: '1.0',
      callRendererFunction: {
        surfaceId: this.surfaceId,
        functionCallId: requestId,
        callFunction: callFunction
      }
    };
    const json = JSON.stringify(envelope);
    this.setStatus('Mock agent -> callRendererFunction getGreeting');
    this.log(`agent → ${json}`);
    this.controller.writeStream(json);
  }

  /**
   * Called by the Kotlin dispatcher — through the reverse
   * `napi_threadsafe_function` — for every inbound
   * `callRendererFunction` on this surface. Library host callback fields
   * remain requestId/name/args; these are not official wire properties:
   *
   * ```
   *   { "surfaceId":"...", "requestId":"...",
   *     "name":"...", "args":{...}, "timeoutMs":1234 }
   * ```
   */
  private onInboundRendererFunction(requestJson: string): void {
    this.log(`← agent callRendererFunction: ${requestJson}`);
    if (this.controller === null) {
      return;
    }
    let req: Record<string, Object>;
    try {
      req = JSON.parse(requestJson) as Record<string, Object>;
    } catch (e) {
      this.log('! malformed inbound JSON');
      return;
    }
    const reqSurfaceId = (req.surfaceId as string) ?? '';
    const requestId = (req.requestId as string) ?? '';
    const name = (req.name as string) ?? '';
    const args = req.args as Record<string, Object> | undefined;
    if (name === 'getGreeting') {
      const recipient = (args?.name as string) ?? '';
      this.setStatus(`Inbound getGreeting(name="${recipient}") → ok`);
      this.log(`→ host responding ok for requestId=${requestId}`);
      const resultJson = JSON.stringify(`Hello, ${recipient}!`);
      this.controller.respondToRpc(reqSurfaceId, requestId, resultJson);
      return;
    }
    this.log(`→ host failing (UNKNOWN_FUNCTION) requestId=${requestId}`);
    this.controller.failRpc(
      reqSurfaceId,
      requestId,
      'UNKNOWN_FUNCTION',
      `renderer does not know function '${name}'`
    );
  }

  // ---------------------------------------------------------------
  //  SEND_RPC_ENVELOPE outbound handler + mock agent
  // ---------------------------------------------------------------

  /**
   * Called when the Kotlin core hands us an outbound v1.0 RPC envelope
   * via `A2UISurfaceViewController.setRpcEnvelopeOutboundHandler` — the
   * demo's stand-in for "ship this to the agent over the wire".
   *
   * Two envelope shapes arrive here:
   *  - `{"version":"1.0","callAgentFunction":{…}}` — emitted by
   *    [invokeAgentFetchUser]. We mock-reply with
   *    `agentFunctionResponse` on the same stream.
   *  - `{"version":"1.0","rendererFunctionResponse":{…}}` — emitted
   *    by our own inbound handler's `a2uiRespondToRpc` / `a2uiFailRpc`
   *    calls. A real integration would forward this to the agent
   *    transport; here we just log it.
   */
  private onOutboundEnvelope(envelopeJson: string): void {
    this.log(`agent-wire ← ${envelopeJson}`);
    let env: Record<string, Object>;
    try {
      env = JSON.parse(envelopeJson) as Record<string, Object>;
    } catch (e) {
      return;
    }
    const call = env.callAgentFunction as Record<string, Object> | undefined;
    if (call === undefined || this.controller === null) {
      return;
    }
    // Mock backend: pretend we processed fetchUser(id=42) and reply.
    const requestId = (call.functionCallId as string) ?? '';
    const response: AgentFunctionResponseEnvelope = {
      version: '1.0',
      agentFunctionResponse: {
        functionCallId: requestId,
        value: { id: 42, name: 'Alice', role: 'admin' }
      }
    };
    const respJson = JSON.stringify(response);
    this.log(`mock-agent → ${respJson}`);
    this.controller.writeStream(respJson);
  }

  // ---------------------------------------------------------------
  //  Log helpers
  // ---------------------------------------------------------------

  private log(line: string): void {
    const next: string[] = [line];
    for (let i = 0; i < this.logLines.length && i < A2UIRpcDemoPage.MAX_LOG_LINES - 1; i++) {
      next.push(this.logLines[i]);
    }
    this.logLines = next;
  }

  private setStatus(line: string): void {
    this.status = line;
  }

  private clearLog(): void {
    this.logLines = [];
  }

  // ---------------------------------------------------------------
  //  UI
  // ---------------------------------------------------------------

  build() {
    Column() {
      // Header (blue bar, matches the Android/iOS RPC demo colour #1A73E8)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('v1.0 RPC Demo (Native)')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('宿主：Native ArkUI + A2UISurfaceView\n演示 PR-6d 的 renderer→agent RPC 回路')
          .fontSize(11)
          .fontColor('#C8D8F0')
          .margin({ top: 4 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 48, bottom: 16 })
      .backgroundColor('#1A73E8')
      .alignItems(HorizontalAlign.Start)

      // Body
      Column() {
        // Status banner
        Text(`Status: ${this.status}`)
          .fontSize(12)
          .fontColor('#666666')
          .width('100%')
          .margin({ top: 10, bottom: 8 })

        // The A2UI surface. onReady fires once the controller is
        // bound — install the outbound sink and seed the surface
        // with a tiny createSurface envelope then.
        Column() {
          A2UISurfaceView({
            configJson: '{"protocolVersion":"1.0","strictV10":true}',
            onReady: (ctrl: A2UISurfaceViewController): void => {
              this.controller = ctrl;
              this.instanceId = ctrl.getInstanceId();
              this.log(`[A2UI] controller ready, instanceId=${this.instanceId}`);

              // Install the outbound sink BEFORE writeStream so we
              // don't miss envelopes produced by surface bootstrap.
              ctrl.setRpcEnvelopeOutboundHandler((envelopeJson: string): void => {
                this.onOutboundEnvelope(envelopeJson);
              });

              // Register the inbound callRendererFunction handler via
              // the render-pod facade. Internally this routes through
              // `a2uiNativeManager.setRendererFunctionHandlerInKmp`
              // → the `a2uiSetRendererFunctionHandler` NAPI export,
              // which wraps our JS callback in a reverse
              // napi_threadsafe_function. The Kotlin dispatcher can
              // then fire us from any thread and we always receive
              // the call on the ArkTS main thread.
              const ok = ctrl.setRendererFunctionHandler(
                (requestJson: string): void => {
                  this.onInboundRendererFunction(requestJson);
                }
              );
              if (!ok) {
                this.log('! setRendererFunctionHandler returned false — '
                  + 'libkuikly_entry.so is too old (needs a2uiSetRendererFunctionHandler)');
              }

              // Seed the surface with a placeholder UI.
              ctrl.writeStream(this.initialCreateSurfaceEnvelope);
              // Intentionally no finishStream() — the stream stays
              // open so subsequent writeStream() calls can deliver
              // more envelopes.
            }
          })
        }
        .width('100%')
        .height(140)
        .backgroundColor('#F2F6FB')
        .borderRadius(6)
        .clip(true)

        // ---------- Outbound: renderer → agent ----------
        Button('Fetch user from agent (id=42)')
          .width('100%')
          .height(42)
          .margin({ top: 10 })
          .backgroundColor('#34A853')
          .fontColor(Color.White)
          .fontSize(14)
          .fontWeight(FontWeight.Medium)
          .onClick(() => this.invokeAgentFetchUser())

        // ---------- Inbound: agent → renderer ----------
        Button('Mock agent → getGreeting("Ada")')
          .width('100%')
          .height(42)
          .margin({ top: 8 })
          .backgroundColor('#1A73E8')
          .fontColor(Color.White)
          .fontSize(14)
          .fontWeight(FontWeight.Medium)
          .onClick(() => this.simulateAgentCallRendererFunction())

        Button('Clear event log')
          .width('100%')
          .height(42)
          .margin({ top: 10 })
          .backgroundColor('#BDBDBD')
          .fontColor(Color.White)
          .fontSize(14)
          .onClick(() => this.clearLog())

        // ---------- Event log ----------
        Text('Event log (newest first)')
          .fontSize(13)
          .fontColor('#444444')
          .margin({ top: 10, bottom: 4 })
          .width('100%')

        Scroll() {
          Column() {
            ForEach(this.logLines, (line: string, index: number) => {
              Text(line)
                .fontSize(11)
                .fontColor('#333333')
                .fontFamily('monospace')
                .width('100%')
                .padding({ left: 4, right: 4, top: 2, bottom: 2 })
            }, (line: string, index: number) => `${index}-${line.substring(0, 32)}`)
          }
          .width('100%')
          .alignItems(HorizontalAlign.Start)
        }
        .layoutWeight(1)
        .width('100%')
        .backgroundColor('#FAFAFA')
        .borderRadius(4)
        .padding(6)
        .align(Alignment.Top)
      }
      .layoutWeight(1)
      .padding({ left: 12, right: 12, top: 8, bottom: 12 })
      .alignItems(HorizontalAlign.Start)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}
