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

import { resourceManager } from '@kit.LocalizationKit';
import { A2UIDemoAssetUtil, TestCaseData } from './A2UIDemoAssetUtil';

/**
 * Utility class for streaming A2UI demo messages.
 * Mirrors the functionality of Android's A2UIDemoStreamUtil.
 */
export class A2UIDemoStreamUtil {

  /**
   * Load a test case from rawfile and return parsed data.
   */
  static loadTestCase(resMgr: resourceManager.ResourceManager, filePath: string): TestCaseData | null {
    const jsonStr = A2UIDemoAssetUtil.readRawFileSync(resMgr, filePath);
    if (jsonStr.length === 0) return null;
    return A2UIDemoAssetUtil.parseTestCase(jsonStr);
  }

  /**
   * Load all test cases from a directory.
   * Uses JSON string parsing to avoid variable-indexed property access.
   */
  static loadTestCasesFromDir(
    resMgr: resourceManager.ResourceManager,
    rootDir: string,
    subDir: string,
    manifestStr: string
  ): TestCaseData[] {
    const results: TestCaseData[] = [];

    let fileList: string[] = [];
    if (rootDir === 'a2ui_components') {
      const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'components');
      fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, subDir);
    } else if (rootDir === 'a2ui_function_tests') {
      const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'function_tests');
      fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, subDir);
    } else if (rootDir === 'a2ui_databinding_tests') {
      const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'databinding_tests');
      fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, subDir);
    } else if (rootDir === 'a2ui_theme_tests') {
      const sectionStr = A2UIDemoAssetUtil.getObjectByKey(manifestStr, 'theme_tests');
      fileList = A2UIDemoAssetUtil.getStringArrayByKey(sectionStr, subDir);
    } else if (rootDir === 'a2ui_examples') {
      fileList = A2UIDemoAssetUtil.getStringArrayByKey(manifestStr, 'examples');
    }

    if (fileList.length === 0) return results;

    for (const fileName of fileList) {
      const filePath = rootDir === 'a2ui_examples'
        ? `${rootDir}/${fileName}`
        : `${rootDir}/${subDir}/${fileName}`;
      const testCase = A2UIDemoStreamUtil.loadTestCase(resMgr, filePath);
      if (testCase) {
        if (testCase.name.length === 0) {
          testCase.name = fileName;
        }
        results.push(testCase);
      }
    }

    return results;
  }

  /**
   * Load the manifest file as a raw JSON string.
   */
  static loadManifest(resMgr: resourceManager.ResourceManager): string {
    return A2UIDemoAssetUtil.readRawFileSync(resMgr, 'a2ui_manifest.json');
  }
}
