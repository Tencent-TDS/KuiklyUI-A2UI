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
import { KRNativeRenderController, KRRecord, Kuikly } from '@kuikly-open/render';
import {
  A2UIKuiklyRenderViewSupport,
  a2uiNativeManager
} from '@kuikly-open/a2ui-render';
import { A2UIDemoAssetUtil, TestCaseData } from '../a2ui/A2UIDemoAssetUtil';

/**
 * A2UIComponentTestPage — generic detail page for the KuiklyPage app.
 *
 * Mirrors `A2UIComponentTestActivity` on Android: this screen hosts a
 * single Kuikly Page (`@Page("A2UICaseList")`, declared in
 * `:demo/A2UICaseListPager.kt`) which internally renders every case
 * as its own `A2UISurface` inside a KMP `Scroller`. We do **not** spin up
 * a separate native `A2UISurface` per case here — the whole "many-cards"
 * layout lives in the Kuikly Pager, so this really is a Kuikly-Page
 * integration path (matching Android/iOS).
 *
 * Route params (all optional aside from `componentDir`):
 *  - `componentDir`  : sub-directory name under `rootDir`, e.g. `"text"`.
 *  - `componentName` : header title (defaults to `componentDir`).
 *  - `rootDir`       : parent rawfile dir (defaults to `"a2ui_components"`).
 *  - `titleSuffix`   : appended to the header title, defaults to `"组件测试"`.
 *
 * pageData contract (see `A2UICaseListPager`):
 *  - `title`  : header string displayed inside the Kuikly Pager.
 *  - `cases`  : JSON array *string*; each entry has `name` / `description`
 *               / `messages` (a JSON array). Cases are read from rawfile
 *               via the aggregate `a2ui_manifest.json` because rawfile has
 *               no directory-listing API.
 */
@Entry
@Component
struct A2UIComponentTestPage {
  @State ready: boolean = false;
  @State componentName: string = '';
  @State titleSuffix: string = '组件测试';
  @State rootDir: string = 'a2ui_components';
  @State componentDir: string = '';
  @State pageData: KRRecord = {};

  private controller: KRNativeRenderController | null = null;
  private delegate = A2UIKuiklyRenderViewSupport.wrap();
  private resMgr = getContext(this).resourceManager;

  aboutToAppear(): void {
    const params = router.getParams() as Record<string, string>;
    if (params) {
      this.componentDir = params['componentDir'] || '';
      this.componentName = params['componentName'] || this.componentDir;
      this.rootDir = params['rootDir'] || 'a2ui_components';
      this.titleSuffix = params['titleSuffix'] || '组件测试';
    }
    this.buildPageData();
  }

  onBackPress(): boolean | void {
    const consumed = this.controller?.onBackPress();
    return consumed === true;
  }

  aboutToDisappear(): void {
    this.controller = null;
  }

  /**
   * Aggregate every JSON test case under `${rootDir}/${componentDir}` into
   * a JSON array string and hand it off via `pageData.cases`.
   *
   * The manifest is the source of truth for "which files live under this
   * component directory" — HarmonyOS' rawfile API does not expose a
   * `list()` call, so we fan out through the pre-baked
   * `a2ui_manifest.json`, mirroring what `sync_demo_assets.sh` produces.
   */
  private buildPageData(): void {
    if (this.componentDir.length === 0) {
      // Still mark ready so the Kuikly page can render an empty state.
      this.pageData = { 'title': this.buildTitle(), 'cases': '[]' };
      this.ready = true;
      return;
    }

    const cases: TestCaseData[] = [];
    try {
      const manifestStr = A2UIDemoAssetUtil.readRawFileSync(this.resMgr, 'a2ui_manifest.json');

      let fileList: string[] = [];
      if (this.rootDir === 'a2ui_components') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'components');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      } else if (this.rootDir === 'a2ui_custom_functions') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'custom_functions');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      } else if (this.rootDir === 'a2ui_custom_components') {
        const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'custom_components');
        fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, this.componentDir);
      }

      for (const fileName of fileList) {
        const filePath = `${this.rootDir}/${this.componentDir}/${fileName}`;
        const jsonStr = A2UIDemoAssetUtil.readRawFileSync(this.resMgr, filePath);
        if (jsonStr.length === 0) continue;
        const testCase = A2UIDemoAssetUtil.parseTestCase(jsonStr);
        if (testCase !== null) {
          if (testCase.name.length === 0) {
            testCase.name = fileName;
          }
          cases.push(testCase);
        }
      }
    } catch (e) {
      console.error(`[A2UIComponentTestPage] Failed to load test cases: ${e}`);
    }

    // Serialise cases as a JSON array string. `TestCaseData.messages` is
    // already a JSON array string (see `A2UIDemoAssetUtil.parseTestCase`),
    // so we inline it verbatim rather than double-encoding.
    let casesJson: string = '[';
    for (let i = 0; i < cases.length; i++) {
      if (i > 0) casesJson += ',';
      const c = cases[i];
      casesJson += '{"name":' + JSON.stringify(c.name) +
        ',"description":' + JSON.stringify(c.description) +
        ',"messages":' + c.messages + '}';
    }
    casesJson += ']';

    this.pageData = { 'title': this.buildTitle(), 'cases': casesJson };
    this.ready = true;
  }

  private buildTitle(): string {
    return `${this.componentName} ${this.titleSuffix}`.trim();
  }

  build() {
    Column() {
      // Header — mirrors Android's #3F51B5 header on this page.
      Column() {
        Row() {
          Text('←')
            .fontSize(20)
            .fontColor(Color.White)
            .onClick(() => router.back())
            .padding({ right: 12 })
          Text(this.buildTitle())
            .fontSize(20)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
        }
        .alignItems(VerticalAlign.Center)

        Text(`宿主：Kuikly Page（Kuikly + @Page A2UICaseList）\n资源：${this.rootDir}/${this.componentDir}/`)
          .fontSize(11)
          .fontColor('#DDDDFF')
          .margin({ top: 4 })
      }
      .width('100%')
      .backgroundColor('#3F51B5')
      .padding({ left: 24, top: 48, right: 24, bottom: 16 })
      .alignItems(HorizontalAlign.Start)

      // Kuikly Pager (`A2UICaseList`) — it iterates over `cases` internally
      // and drops one `A2UISurface { }` per case inside its Flex tree.
      Stack() {
        if (this.ready) {
          Kuikly({
            pageName: 'A2UICaseList',
            pageData: this.pageData,
            delegate: this.delegate,
            nativeManager: a2uiNativeManager,
            onControllerReadyCallback: (controller: KRNativeRenderController): void => {
              this.controller = controller;
              A2UIKuiklyRenderViewSupport.onControllerReadyReplay(controller);
            }
          })
        }
      }
      .layoutWeight(1)
      .width('100%')
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}
