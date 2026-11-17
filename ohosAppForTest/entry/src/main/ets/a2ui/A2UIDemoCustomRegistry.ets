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

import { ComponentContent } from '@kit.ArkUI';
import { UIContext } from '@ohos.arkui.UIContext';
import { webview } from '@kit.ArkWeb';
import { KuiklyRenderBaseView, KuiklyRenderCallback, KRValue } from '@kuikly-open/render';
import {
  registerA2UICustomRenderView,
  A2UICustomViewSize,
  registerA2UICustomFunction,
  registerA2UICatalogFunction,
  A2UICustomFunctionHandler
} from '@kuikly-a2ui-open/render';

let installed: boolean = false;

export function installA2UIDemoCustomRegistry(): void {
  if (installed) {
    return;
  }
  registerA2UICustomRenderView(
    'A2UIWeb',
    (): KuiklyRenderBaseView => new A2UIWebRenderView(),
    (cw, _ch): A2UICustomViewSize => ({ width: cw, height: 240 })
  );
  registerA2UICustomRenderView(
    'MyA2UIView',
    (): KuiklyRenderBaseView => new MyA2UIRenderView(),
    (cw, _ch): A2UICustomViewSize => ({ width: cw, height: 200 })
  );
  registerDemoFunction('getGreeting', 'string', 'sync', (args) => {
    const name = (args['name'] as string) ?? 'World';
    return `Hello, ${name}!`;
  });
  registerDemoFunction('formatPhone', 'string', 'sync', (args) => {
    const phone = (args['phone'] as string) ?? '';
    if (phone.length === 11) {
      return `${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}`;
    }
    return phone;
  });
  registerDemoFunction('isVip', 'boolean', 'sync', (args) => {
    const level = (args['level'] as number) ?? 0;
    return level >= 3;
  });
  registerDemoFunction('calculateDiscount', 'number', 'sync', (args) => {
    const price = (args['price'] as number) ?? 0;
    const rate = (args['rate'] as number) ?? 1;
    return Math.round(price * rate * 100) / 100;
  });
  registerDemoFunction('logEvent', 'void', 'sync', (args) => {
    const event = (args['event'] as string) ?? '';
    const data = (args['data'] as string) ?? '';
    console.info(`[CustomFunction] logEvent: event=${event}, data=${data}`);
    return null;
  });
  registerDemoFunction('buildSummary', 'string', 'sync', (args) => {
    const name = (args['name'] as string) ?? 'Unknown';
    const count = (args['count'] as number) ?? 0;
    const status = (args['status'] as string) ?? 'unknown';
    return `项目「${name}」当前状态: ${status}，共 ${count} 个任务`;
  });
  registerA2UICatalogFunction(
    'fetchUser',
    'custom_functions_catalog_v1_0.json',
    '{"returnType":"object","invokeMode":"sync","allowedCallers":"agentOnly","args":{"type":"object","properties":{"id":{"type":"number"}},"required":["id"],"additionalProperties":false}}',
    null
  );
  installed = true;
}

function registerDemoFunction(name: string, returnType: string, invokeMode: string, handler: A2UICustomFunctionHandler): void {
  let argsSchema: string;
  switch (name) {
    case 'getGreeting':
      argsSchema = '{"type":"object","properties":{"name":{"type":"string"}},"required":["name"],"additionalProperties":false}';
      break;
    case 'formatPhone':
      argsSchema = '{"type":"object","properties":{"phone":{"type":"string"}},"required":["phone"],"additionalProperties":false}';
      break;
    case 'isVip':
      argsSchema = '{"type":"object","properties":{"level":{"type":"number"}},"required":["level"],"additionalProperties":false}';
      break;
    case 'calculateDiscount':
      argsSchema = '{"type":"object","properties":{"price":{"type":"number"},"rate":{"type":"number"}},"required":["price","rate"],"additionalProperties":false}';
      break;
    case 'logEvent':
      argsSchema = '{"type":"object","properties":{"event":{"type":"string"},"data":{"type":"string"}},"required":["event"],"additionalProperties":false}';
      break;
    case 'buildSummary':
      argsSchema = '{"type":"object","properties":{"name":{"type":"string"},"count":{"type":"number"},"status":{"type":"string"}},"required":["name","count","status"],"additionalProperties":false}';
      break;
    default:
      throw new Error(`Unknown custom demo function: ${name}`);
  }
  registerA2UICustomFunction(name, returnType, invokeMode, handler);
  registerA2UICatalogFunction(
    name,
    'custom_functions_catalog_v1_0.json',
    `{"returnType":"${returnType}","invokeMode":"${invokeMode}","allowedCallers":"rendererOrAgent","requiresUserActivation":false,"args":${argsSchema}}`,
    handler
  );
}

@Builder
function createMyA2UIView(view: KuiklyRenderBaseView) {
  Column() {
    Text((view as MyA2UIRenderView).text)
      .fontSize(16)
      .fontColor((view as MyA2UIRenderView).textColor)
      .textAlign(TextAlign.Center)
  }
  .width('100%')
  .height('100%')
  .backgroundColor('#E8F5E9')
  .justifyContent(FlexAlign.Center)
  .alignItems(HorizontalAlign.Center)
}

class MyA2UIRenderView extends KuiklyRenderBaseView {
  text: string = '';
  textColor: string | number = '#333333';

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    switch (propKey) {
      case 'text':
        this.text = propValue as string;
        this.updateArkUI();
        return true;
      case 'textColor':
        const value = propValue as string | number;
        const argb = typeof value === 'number' ? value : Number(value);
        this.textColor = Number.isFinite(argb) ? argb >>> 0 : value;
        this.updateArkUI();
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  call(method: string, params: KRValue, callback: KuiklyRenderCallback | null): void {}

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      this.getUIContext() as UIContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createMyA2UIView),
      this
    );
  }
}

@Builder
function createA2UIWebView(view: KuiklyRenderBaseView) {
  Web({ src: (view as A2UIWebRenderView).source, controller: (view as A2UIWebRenderView).webController })
    .width('100%')
    .height('100%')
}

class A2UIWebRenderView extends KuiklyRenderBaseView {
  source: string = '';
  webController: webview.WebviewController = new webview.WebviewController();

  setProp(propKey: string, propValue: KRValue | KuiklyRenderCallback): boolean {
    switch (propKey) {
      case 'source':
        this.source = propValue as string;
        this.updateArkUI();
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  call(method: string, params: KRValue, callback: KuiklyRenderCallback | null): void {}

  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    return new ComponentContent<KuiklyRenderBaseView>(
      this.getUIContext() as UIContext,
      wrapBuilder<[KuiklyRenderBaseView]>(createA2UIWebView),
      this
    );
  }
}
