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
import { util } from '@kit.ArkTS';

/**
 * Utility class for reading A2UI demo JSON assets from rawfile.
 */
export class A2UIDemoAssetUtil {

  /**
   * Read a rawfile as string synchronously.
   */
  static readRawFileSync(resMgr: resourceManager.ResourceManager, path: string): string {
    try {
      const content = resMgr.getRawFileContentSync(path);
      const textDecoder = util.TextDecoder.create('utf-8');
      return textDecoder.decodeToString(content);
    } catch (e) {
      console.error(`[A2UIDemoAssetUtil] Failed to read rawfile: ${path}, error: ${e}`);
      return '';
    }
  }

  /**
   * List files in a rawfile directory.
   * Note: HarmonyOS does not have a direct API to list rawfile directories.
   * We use a workaround by trying to read known file patterns.
   */
  static listRawFileDir(resMgr: resourceManager.ResourceManager, dirPath: string): string[] {
    const files: string[] = [];
    // Try to read files with common naming patterns (1_ to 99_)
    for (let i = 1; i <= 99; i++) {
      // Try multiple naming patterns
      const prefixes = [`${i}_`, `${String(i).padStart(2, '0')}_`];
      for (const prefix of prefixes) {
        // Try to find files by attempting to read them
        // We'll use a brute-force approach with known file extensions
        const testPaths = A2UIDemoAssetUtil.generateTestPaths(dirPath, prefix);
        for (const testPath of testPaths) {
          try {
            resMgr.getRawFileContentSync(testPath);
            // If we get here, the file exists
            const fileName = testPath.substring(dirPath.length + 1);
            if (!files.includes(fileName)) {
              files.push(fileName);
            }
          } catch (_) {
            // File doesn't exist, continue
          }
        }
      }
    }
    return files.sort();
  }

  /**
   * Generate test paths for file discovery.
   * Since HarmonyOS doesn't support listing rawfile directories,
   * we need to know the file names in advance or use a manifest.
   */
  private static generateTestPaths(dirPath: string, prefix: string): string[] {
    // This is a simplified approach - in production, use a manifest file
    return [];
  }

  /**
   * Parse a JSON test case file and extract messages.
   * Returns null if the file is invalid or has no messages.
   */
  static parseTestCase(jsonStr: string): TestCaseData | null {
    try {
      const json = JSON.parse(jsonStr) as Record<string, Object>;
      const name = (json['name'] as string) || '';
      const description = (json['description'] as string) || '';
      const messages = json['messages'] as Object[];
      if (!messages || !Array.isArray(messages)) {
        return null;
      }
      return {
        name: name,
        description: description,
        messages: JSON.stringify(messages)
      };
    } catch (e) {
      console.error(`[A2UIDemoAssetUtil] Failed to parse test case: ${e}`);
      return null;
    }
  }

  /**
   * Get a string array from a JSON object string by key.
   * This avoids variable-indexed property access which ArkTS forbids.
   * @param jsonObjStr - A JSON object string, e.g. '{"text":["a.json","b.json"]}'
   * @param key - The field name to extract
   * @returns The string array value, or empty array if not found
   */
  static getStringArrayByKey(jsonObjStr: string, key: string): string[] {
    const searchKey = `"${key}"`;
    const keyIndex = jsonObjStr.indexOf(searchKey);
    if (keyIndex < 0) return [];

    // Find the colon after the key
    let colonIndex = keyIndex + searchKey.length;
    while (colonIndex < jsonObjStr.length && jsonObjStr.charAt(colonIndex) !== ':') {
      colonIndex++;
    }
    if (colonIndex >= jsonObjStr.length) return [];
    colonIndex++; // skip ':'

    // Skip whitespace
    while (colonIndex < jsonObjStr.length && ' \t\n\r'.includes(jsonObjStr.charAt(colonIndex))) {
      colonIndex++;
    }
    if (colonIndex >= jsonObjStr.length || jsonObjStr.charAt(colonIndex) !== '[') return [];

    // Find matching ']'
    let depth = 1;
    let endIndex = colonIndex + 1;
    let inString = false;
    while (endIndex < jsonObjStr.length && depth > 0) {
      const ch = jsonObjStr.charAt(endIndex);
      if (inString) {
        if (ch === '\\') {
          endIndex++;
        } else if (ch === '"') {
          inString = false;
        }
      } else {
        if (ch === '"') {
          inString = true;
        } else if (ch === '[') {
          depth++;
        } else if (ch === ']') {
          depth--;
        }
      }
      endIndex++;
    }

    const arrayStr = jsonObjStr.substring(colonIndex, endIndex);
    try {
      return JSON.parse(arrayStr) as string[];
    } catch (_) {
      return [];
    }
  }

  /**
   * Get a nested JSON object string by key from a JSON object string.
   * @param jsonObjStr - A JSON object string
   * @param key - The field name to extract
   * @returns The nested object as a JSON string, or '{}' if not found
   */
  static getObjectByKey(jsonObjStr: string, key: string): string {
    const searchKey = `"${key}"`;
    const keyIndex = jsonObjStr.indexOf(searchKey);
    if (keyIndex < 0) return '{}';

    // Find the colon after the key
    let colonIndex = keyIndex + searchKey.length;
    while (colonIndex < jsonObjStr.length && jsonObjStr.charAt(colonIndex) !== ':') {
      colonIndex++;
    }
    if (colonIndex >= jsonObjStr.length) return '{}';
    colonIndex++; // skip ':'

    // Skip whitespace
    while (colonIndex < jsonObjStr.length && ' \t\n\r'.includes(jsonObjStr.charAt(colonIndex))) {
      colonIndex++;
    }
    if (colonIndex >= jsonObjStr.length || jsonObjStr.charAt(colonIndex) !== '{') return '{}';

    // Find matching '}'
    let depth = 1;
    let endIndex = colonIndex + 1;
    let inString = false;
    while (endIndex < jsonObjStr.length && depth > 0) {
      const ch = jsonObjStr.charAt(endIndex);
      if (inString) {
        if (ch === '\\') {
          endIndex++;
        } else if (ch === '"') {
          inString = false;
        }
      } else {
        if (ch === '"') {
          inString = true;
        } else if (ch === '{') {
          depth++;
        } else if (ch === '}') {
          depth--;
        }
      }
      endIndex++;
    }

    return jsonObjStr.substring(colonIndex, endIndex);
  }
}

/**
 * Parsed test case data.
 */
export interface TestCaseData {
  name: string;
  description: string;
  messages: string;
}

/**
 * Component entry data for navigation.
 */
export interface ComponentEntry {
  name: string;
  dir: string;
  description: string;
  color: string;
}

/**
 * Function entry data for navigation.
 */
export interface FunctionEntry {
  name: string;
  description: string;
  color: string;
}

/**
 * Function group data.
 */
export interface FunctionGroup {
  title: string;
  entries: FunctionEntry[];
}

/**
 * Data binding entry data.
 */
export interface BindingEntry {
  dir: string;
  name: string;
  description: string;
  color: string;
}

/**
 * Data binding group data.
 */
export interface BindingGroup {
  title: string;
  entries: BindingEntry[];
}
