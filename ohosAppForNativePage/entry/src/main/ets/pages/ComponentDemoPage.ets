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
import { ComponentEntry } from '../a2ui/A2UIDemoAssetUtil';

/**
 * Router params for ComponentTestPage navigation.
 */
class ComponentTestParams {
  componentDir: string = '';
  componentName: string = '';
  rootDir: string = '';
  titleSuffix: string = '';

  constructor(componentDir: string, componentName: string, rootDir: string, titleSuffix: string) {
    this.componentDir = componentDir;
    this.componentName = componentName;
    this.rootDir = rootDir;
    this.titleSuffix = titleSuffix;
  }
}

/**
 * Component Demo Page.
 * Displays a list of A2UI v0.9 protocol components.
 * Selecting a component navigates to ComponentTestPage.
 */
@Entry
@Component
struct ComponentDemoPage {
  private components: ComponentEntry[] = [
    { name: 'Text', dir: 'text', description: '文本组件 - 支持文本样式、variant、数据绑定', color: '#E74C3C' },
    { name: 'Image', dir: 'image', description: '图片组件 - 支持网络图片、fit模式', color: '#27AE60' },
    { name: 'Icon', dir: 'icon', description: '图标组件 - 支持标准图标名称和SVG路径', color: '#9B59B6' },
    { name: 'Video', dir: 'video', description: '视频组件 - 视频播放能力', color: '#16A085' },
    { name: 'AudioPlayer', dir: 'audioplayer', description: '音频播放组件 - 音频播放能力', color: '#2C3E50' },
    { name: 'Row', dir: 'row', description: '水平布局 - 子组件水平排列，支持justify/align', color: '#4A90D9' },
    { name: 'Column', dir: 'column', description: '垂直布局 - 子组件垂直排列，支持justify/align', color: '#3498DB' },
    { name: 'List', dir: 'list', description: '列表组件 - 可滚动列表，支持水平/垂直方向', color: '#F39C12' },
    { name: 'Card', dir: 'card', description: '卡片组件 - 带圆角和阴影的容器', color: '#E67E22' },
    { name: 'Tabs', dir: 'tabs', description: '标签页组件 - 多标签切换', color: '#34495E' },
    { name: 'Modal', dir: 'modal', description: '弹窗组件 - 模态弹窗展示', color: '#2C3E50' },
    { name: 'Divider', dir: 'divider', description: '分割线组件 - 水平/垂直分割线', color: '#95A5A6' },
    { name: 'Button', dir: 'button', description: '按钮组件 - 可点击，支持action', color: '#6C5CE7' },
    { name: 'TextField', dir: 'textfield', description: '输入框组件 - 文本输入，支持label/value', color: '#1ABC9C' },
    { name: 'CheckBox', dir: 'checkbox', description: '复选框组件 - 布尔值选择', color: '#8E44AD' },
    { name: 'ChoicePicker', dir: 'choicepicker', description: '选择器组件 - 单选/多选列表', color: '#D35400' },
    { name: 'Slider', dir: 'slider', description: '滑块组件 - 数值范围选择', color: '#27AE60' },
    { name: 'DateTimeInput', dir: 'datetimeinput', description: '日期时间组件 - 日期/时间选择', color: '#C0392B' }
  ];

  build() {
    Column() {
      // Header
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .onClick(() => router.back())
            .padding({ right: 12 })
          Text('组件展示')
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text('A2UI v0.9 标准协议支持的 18 个组件')
          .fontSize(12)
          .fontColor('#DDDDFF')
          .margin({ top: 4 })
      }
      .width('100%')
      .backgroundColor('#6C5CE7')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Scrollable component list
      Scroll() {
        Column() {
          ForEach(this.components, (item: ComponentEntry) => {
            Row() {
              // Color indicator bar
              Column()
                .width(4)
                .height(36)
                .borderRadius(3)
                .backgroundColor(item.color)
                .margin({ right: 12 })

              // Text column
              Column() {
                Text(item.name)
                  .fontSize(15)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#333333')
                Text(item.description)
                  .fontSize(12)
                  .fontColor('#888888')
                  .margin({ top: 3 })
              }
              .layoutWeight(1)
              .alignItems(HorizontalAlign.Start)

              // Arrow
              Text('›')
                .fontSize(18)
                .fontColor('#CCCCCC')
                .padding({ left: 8 })
            }
            .width('100%')
            .padding({ left: 12, top: 14, right: 12, bottom: 14 })
            .backgroundColor(Color.White)
            .borderRadius(8)
            .shadow({ radius: 2, color: '#1A000000', offsetY: 1 })
            .margin({ top: 8 })
            .onClick(() => {
              router.pushUrl({
                url: 'pages/ComponentTestPage',
                params: new ComponentTestParams(item.dir, item.name, 'a2ui_components', '组件测试')
              });
            })
          }, (item: ComponentEntry) => item.dir)
        }
        .padding({ left: 12, top: 8, right: 12, bottom: 16 })
      }
      .layoutWeight(1)
      .align(Alignment.Top)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}
