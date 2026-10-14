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
import { window } from '@kit.ArkUI';
import { common } from '@kit.AbilityKit';
import { A2UISurface } from '../a2ui/A2UISurface';

/**
 * NativeCardDemoPage — Native page single-card sample.
 *
 * Mirrors `androidAppForBothPages/NativePageCardActivity`: a plain ArkUI
 * page hosts one `A2UISurfaceView` (wrapped by the `A2UISurface { }`
 * convenience component) which renders one hand-crafted A2UI v0.9
 * weather card. Intentionally identical (in intent) to the weather card
 * built by `A2UISurfaceDemoPager` on the Kuikly-page path so users can
 * eyeball parity between the two integration modes without any prop
 * juggling.
 */

// Hand-crafted A2UI v0.9 weather-card envelopes. Two envelopes:
//   1. createSurface — declares the surface id.
//   2. updateComponents — declares the DSL component tree.
// Wrapped in a JSON array so `A2UISurface` unpacks and feeds them one by
// one via `writeStream()`. `finished:true` closes the stream after the
// last envelope so no further updates are expected.
const WEATHER_CARD_MESSAGES = '[' +
  '{"version":"v0.9","createSurface":{"surfaceId":"both-native-weather"}},' +
  '{"version":"v0.9","updateComponents":{"surfaceId":"both-native-weather","components":[' +
    '{"id":"root","component":"Card","child":"outer",' +
      '"styles":{"padding":10,' +
        '"backgroundLinearGradient":{"direction":"to-bottom","colorStops":[' +
          '{"color":"#3A6DF0","stop":0.0},' +
          '{"color":"#5B4BE1","stop":0.55},' +
          '{"color":"#7A3FD8","stop":1.0}' +
        ']}}},' +
    '{"id":"outer","component":"Column","align":"stretch","children":["title","subtitle","hero","detail"]},' +
    '{"id":"title","component":"Text","text":"深圳 · 今日天气","variant":"h3",' +
      '"styles":{"color":"#FFFFFF","fontSize":15,"fontWeight":"medium","letterSpacing":1,' +
        '"opacity":0.85,"textAlign":"center","marginTop":8,"marginBottom":0}},' +
    '{"id":"subtitle","component":"Text","text":"周一 · 9月8日 · 体感 29°","variant":"caption",' +
      '"styles":{"color":"#FFFFFF","fontSize":11,"fontWeight":"normal","letterSpacing":0.5,' +
        '"opacity":0.6,"textAlign":"center","marginTop":0,"marginBottom":2,' +
        '"textShadow":{"offsetX":0,"offsetY":1,"radius":2,"color":"#40000000"}}},' +
    '{"id":"hero","component":"Row","align":"center","children":["temp","cond"],' +
      '"styles":{"marginTop":0,"marginBottom":4,"paddingLeft":4,"paddingRight":4}},' +
    '{"id":"temp","component":"Text","text":"27°","variant":"h1",' +
      '"styles":{"color":"#FFFFFF","fontSize":84,"fontWeight":"light","letterSpacing":-2,"flexGrow":1,' +
        '"marginTop":0,"marginBottom":0,' +
        '"transform":{"scale":1.02,"translate":[0,-0.02],"anchor":[0,0.5]},' +
        '"textShadow":{"offsetX":0,"offsetY":2,"radius":12,"color":"#66000000"}}},' +
    '{"id":"cond","component":"Text","text":"多云转晴","variant":"body",' +
      '"styles":{"color":"#FFFFFF","fontSize":17,"fontWeight":"medium","opacity":0.9,' +
        '"textAlign":"right","flexGrow":1,"marginTop":0,"marginBottom":0,' +
        '"textShadow":{"offsetX":0,"offsetY":1,"radius":4,"color":"#4D000000"}}},' +
    '{"id":"detail","component":"Row","justify":"spaceBetween","children":["hum","wind","aqi"],' +
      '"styles":{"marginTop":4,"paddingTop":0}},' +
    '{"id":"hum","component":"Row","align":"center","children":["humText"],' +
      '"styles":{"backgroundColor":"#22FFFFFF","borderRadius":8,' +
        '"paddingLeft":10,"paddingRight":10,"paddingTop":3,"paddingBottom":3,' +
        '"marginLeft":8,"marginTop":0,"marginBottom":8,' +
        '"boxShadow":{"offsetX":0,"offsetY":2,"radius":6,"color":"#33000000","fill":false}}},' +
    '{"id":"humText","component":"Text","text":"湿度 68%","variant":"caption",' +
      '"styles":{"color":"#FFFFFF","fontSize":12,"fontWeight":"medium","opacity":0.9,' +
        '"marginTop":0,"marginBottom":0}},' +
    '{"id":"wind","component":"Row","align":"center","children":["windText"],' +
      '"styles":{"backgroundColor":"#22FFFFFF","borderRadius":8,' +
        '"paddingLeft":10,"paddingRight":10,"paddingTop":3,"paddingBottom":3,' +
        '"marginTop":0,"marginBottom":8,' +
        '"boxShadow":{"offsetX":0,"offsetY":2,"radius":6,"color":"#33000000","fill":false}}},' +
    '{"id":"windText","component":"Text","text":"东南风 3 级","variant":"caption",' +
      '"styles":{"color":"#FFFFFF","fontSize":12,"fontWeight":"medium","opacity":0.9,' +
        '"marginTop":0,"marginBottom":0}},' +
    '{"id":"aqi","component":"Row","align":"center","children":["aqiText"],' +
      '"styles":{"backgroundColor":"#22FFFFFF","borderRadius":8,' +
        '"paddingLeft":10,"paddingRight":10,"paddingTop":3,"paddingBottom":3,' +
        '"marginRight":8,"marginTop":0,"marginBottom":8,' +
        '"boxShadow":{"offsetX":0,"offsetY":2,"radius":6,"color":"#33000000","fill":false}}},' +
    '{"id":"aqiText","component":"Text","text":"空气 良 · 58","variant":"caption",' +
      '"styles":{"color":"#FFFFFF","fontSize":12,"fontWeight":"medium","opacity":0.9,' +
        '"marginTop":0,"marginBottom":0}}' +
  ']}}]';

@Entry
@Component
struct NativeCardDemoPage {
  @State statusBarHeightVp: number = 0;

  aboutToAppear(): void {
    this.resolveStatusBarHeight();
  }

  private resolveStatusBarHeight(): void {
    try {
      const ctx = getContext(this) as common.UIAbilityContext;
      window.getLastWindow(ctx).then((win: window.Window) => {
        const area = win.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM);
        this.statusBarHeightVp = px2vp(area.topRect.height);
      }).catch((): void => {
        // ignore
      });
    } catch (_e) {
      // ignore
    }
  }

  build() {
    Column() {
      // Header — blue-grey band mirroring Android's `#455A64` for this path.
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('Native 页面接入示例')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('宿主：Native ArkUI + A2UISurfaceView')
          .fontSize(11)
          .fontColor('#CFD8DC')
          .margin({ top: 4 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: this.statusBarHeightVp + 16, bottom: 16 })
      .backgroundColor('#455A64')
      .alignItems(HorizontalAlign.Start)
      .expandSafeArea([SafeAreaType.SYSTEM], [SafeAreaEdge.TOP])

      // The single hand-fed A2UI weather card. Wrapped in a white rounded
      // container so the card visually matches the Android version's
      // `GradientDrawable(white + 8dp radius)` frame around the surface view.
      //
      // NOTE ON WIDTH: the inner `A2UISurfaceView` uses `.width('100%')`
      // internally, so this wrapping Column MUST also declare `.width('100%')`
      // to give the child a well-defined parent width. We deliberately
      // provide the left/right screen inset via an outer padding container
      // (not via margin on the card Column itself) — a Column that is both
      // `.width('100%')` and `.margin({left, right})` would overflow to the
      // right of the screen. This mirrors ohosAppForNativePage's
      // `NativeCardDemoPage.ets` layout style.
      Column() {
        Column() {
          A2UISurface({
            messages: WEATHER_CARD_MESSAGES,
            finished: true
          })
        }
        .width('100%')
        .backgroundColor(Color.White)
        .borderRadius(8)
        .clip(true)
      }
      .width('100%')
      .padding({ left: 12, right: 12, top: 12 })

      // The rest of the page stays empty on purpose — this demo is meant to
      // showcase one A2UI card, not scrolling behaviour.
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
    .alignItems(HorizontalAlign.Start)
  }
}
