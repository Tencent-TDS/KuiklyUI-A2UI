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

import { router, promptAction } from '@kit.ArkUI';

/**
 * KuiklyPageComponentDemoPage — component-showcase list page.
 *
 * Mirrors [ComponentDemoActivity] on Android: this is a *native ArkUI*
 * list page (no Kuikly hosting) that enumerates the 18 A2UI v0.9
 * semantic components. Tapping an entry navigates to the shared
 * `A2UIComponentTestPage`, which loads the matching rawfile JSON test
 * cases and renders them via `A2UISurface`.
 *
 * The parent app (`ohosAppForKuiklyPage`) does not otherwise ship a
 * Kuikly-hosted component browser — this page reuses the same static
 * list as the Android side so the demo apps have the same shape.
 */
class ComponentEntry {
  name: string = '';
  desc: string = '';
  color: string = '';

  constructor(name: string, desc: string, color: string) {
    this.name = name;
    this.desc = desc;
    this.color = color;
  }
}

const COMPONENTS: ComponentEntry[] = [
  new ComponentEntry('Text', '文本组件 - 支持文本样式、variant、数据绑定', '#E74C3C'),
  new ComponentEntry('Image', '图片组件 - 支持网络图片、fit模式', '#27AE60'),
  new ComponentEntry('Icon', '图标组件 - 支持标准图标名称和SVG路径', '#9B59B6'),
  new ComponentEntry('Video', '视频组件 - 视频播放能力', '#16A085'),
  new ComponentEntry('AudioPlayer', '音频播放组件 - 音频播放能力', '#2C3E50'),
  new ComponentEntry('Row', '水平布局 - 子组件水平排列，支持justify/align', '#4A90D9'),
  new ComponentEntry('Column', '垂直布局 - 子组件垂直排列，支持justify/align', '#3498DB'),
  new ComponentEntry('List', '列表组件 - 可滚动列表，支持水平/垂直方向', '#F39C12'),
  new ComponentEntry('Card', '卡片组件 - 带圆角和阴影的容器', '#E67E22'),
  new ComponentEntry('Tabs', '标签页组件 - 多标签切换', '#34495E'),
  new ComponentEntry('Modal', '弹窗组件 - 模态弹窗展示', '#2C3E50'),
  new ComponentEntry('Divider', '分割线组件 - 水平/垂直分割线', '#95A5A6'),
  new ComponentEntry('Button', '按钮组件 - 可点击，支持action', '#6C5CE7'),
  new ComponentEntry('TextField', '输入框组件 - 文本输入，支持label/value', '#1ABC9C'),
  new ComponentEntry('CheckBox', '复选框组件 - 布尔值选择', '#8E44AD'),
  new ComponentEntry('ChoicePicker', '选择器组件 - 单选/多选列表', '#D35400'),
  new ComponentEntry('Slider', '滑块组件 - 数值范围选择', '#27AE60'),
  new ComponentEntry('DateTimeInput', '日期时间组件 - 日期/时间选择', '#C0392B')
];

@Entry
@Component
struct KuiklyPageComponentDemoPage {
  private openComponentTest(entry: ComponentEntry): void {
    const dir = entry.name.toLowerCase();
    router.pushUrl({
      url: 'pages/A2UIComponentTestPage',
      params: {
        'componentDir': dir,
        'componentName': entry.name,
        'rootDir': 'a2ui_components',
        'titleSuffix': '组件'
      }
    }).catch((): void => {
      promptAction.showToast({ message: `${entry.name} 暂未提供测试用例` });
    });
  }

  build() {
    Column() {
      // Header (purple bar — mirrors Android #6C5CE7)
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .padding({ right: 12 })
            .onClick(() => router.back())
          Text('组件展示')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .width('100%')
        .alignItems(VerticalAlign.Center)

        Text('A2UI v0.9 标准协议支持的 18 个组件')
          .fontSize(12)
          .fontColor('#DDDDFF')
          .margin({ top: 4 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 48, bottom: 16 })
      .backgroundColor('#6C5CE7')
      .alignItems(HorizontalAlign.Start)

      // Scrollable list
      Scroll() {
        Column() {
          ForEach(COMPONENTS, (item: ComponentEntry) => {
            Row() {
              // Colour indicator bar
              Column()
                .width(4)
                .height(36)
                .backgroundColor(item.color)
                .borderRadius(3)
                .margin({ right: 12 })

              Column() {
                Text(item.name)
                  .fontSize(15)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#333333')
                Text(item.desc)
                  .fontSize(12)
                  .fontColor('#888888')
                  .margin({ top: 3 })
              }
              .layoutWeight(1)
              .alignItems(HorizontalAlign.Start)

              Text('›')
                .fontSize(18)
                .fontColor('#CCCCCC')
                .padding({ left: 8 })
            }
            .width('100%')
            .padding({ left: 12, right: 12, top: 14, bottom: 14 })
            .backgroundColor(Color.White)
            .borderRadius(8)
            .margin({ top: 8 })
            .onClick(() => this.openComponentTest(item))
          }, (item: ComponentEntry) => item.name)
        }
        .padding({ left: 12, right: 12, top: 8, bottom: 16 })
        .width('100%')
        .alignItems(HorizontalAlign.Start)
      }
      .layoutWeight(1)
      .align(Alignment.Top)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}
