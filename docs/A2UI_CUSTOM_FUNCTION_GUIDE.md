# A2UI 自定义函数使用指南

## 概述

A2UI 自定义函数（Custom Function）是 A2UI v0.9 协议的扩展能力，允许客户端注册自定义的业务逻辑函数，供 JSON 协议中的组件属性绑定和 Action 触发时调用。

### 核心价值

- **业务逻辑下沉**：将格式化、校验、计算等逻辑放在客户端，服务端只需声明调用意图
- **协议可扩展**：不修改 A2UI 协议本身，即可为 JSON 消息流增加新的计算能力
- **跨平台一致**：三端（Android / iOS / HarmonyOS）使用统一的注册 API 和 JSON 调用语法

### 工作原理

```
┌─────────────────────────────────────────────────────────┐
│                  JSON 消息流 (Server/AI)                   │
│                                                          │
│  "text": {                                               │
│    "call": "getGreeting",                                │
│    "args": { "name": { "path": "/userName" } },          │
│    "returnType": "string"                                │
│  }                                                       │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│              A2UI Core (Kotlin Multiplatform)             │
│                                                          │
│  1. 解析 "call" 字段 → 识别为自定义函数                     │
│  2. 从 A2UICustomFunctionRegistry 查找函数元数据            │
│  3. 解析 args（支持 path 引用、嵌套函数调用、字面量）         │
│  4. 通过 INVOKE_CUSTOM_FUNCTION 桥接调用 Native 端 Handler │
│  5. 接收返回值，按 returnType 进行类型转换                   │
│  6. 将结果绑定到组件属性                                    │
└─────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│            Native Handler (平台侧注册的闭包)               │
│                                                          │
│  { args ->                                               │
│      val name = args.optString("name", "World")          │
│      "Hello, $name!"                                     │
│  }                                                       │
└─────────────────────────────────────────────────────────┘
```

---

## JSON 协议格式

### 1. 作为组件属性值（表达式绑定）

自定义函数可以作为组件属性的值，在数据绑定求值时被调用：

```json
{
  "id": "greeting",
  "component": "Text",
  "text": {
    "call": "getGreeting",
    "args": {
      "name": { "path": "/userName" }
    },
    "returnType": "string"
  }
}
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `call` | string | ✅ | 自定义函数名称 |
| `args` | object | ❌ | 参数键值对，value 支持字面量、`{"path": "..."}` 路径引用 |
| `returnType` | string | ✅ | 返回值类型：`string` / `boolean` / `number` / `void` |

### 2. 在 formatString 内联语法中使用

自定义函数也可以在 `formatString` 的插值表达式中调用：

```json
{
  "id": "price",
  "component": "Text",
  "text": {
    "call": "formatString",
    "args": {
      "value": "折后价: ¥${calculateDiscount(price:${/originalPrice}, rate:${/discountRate})}"
    },
    "returnType": "string"
  }
}
```

**内联语法格式**：`${functionName(param1:${/dataPath1}, param2:${/dataPath2})}`

### 3. 作为 Button 的 Action（void 函数）

自定义函数可以作为按钮点击时触发的 action：

```json
{
  "id": "btn",
  "component": "Button",
  "child": "btnText",
  "action": {
    "functionCall": {
      "call": "logEvent",
      "args": {
        "event": "button_click",
        "data": { "path": "/eventData" }
      },
      "returnType": "void"
    }
  }
}
```

### 4. 作为 check 条件（boolean 函数）

自定义函数可以用于组件的 `checks` 条件判断：

```json
{
  "id": "vipBtn",
  "component": "Button",
  "checks": [
    {
      "condition": {
        "call": "isVip",
        "args": { "level": { "path": "/userLevel" } },
        "returnType": "boolean"
      },
      "message": "仅 VIP 用户可操作"
    }
  ]
}
```

---

## 支持的返回类型

| returnType | 说明 | 适用场景 |
|-----------|------|---------|
| `string` | 返回字符串 | Text.text、格式化显示 |
| `boolean` | 返回布尔值 | check 条件、显隐控制 |
| `number` | 返回数值 | 计算结果、数值显示 |
| `void` | 无返回值 | action 触发、日志上报 |

---

## 为 AI/LLM 生成 Catalog 文件

### 背景

在 A2UI 协议中，**Catalog** 是 Renderer 提供给 Agent（AI/LLM）的能力清单。标准的 basic catalog 定义了 14 个内置函数（`required`、`formatString`、`openUrl` 等）。当你注册了自定义函数后，如果希望 AI/LLM 能够正确生成包含自定义函数调用的 UI JSON，就需要提供一个**扩展的 catalog 文件**，让 AI 知道有哪些自定义函数可用、每个函数接受哪些参数、返回什么类型。

### 何时需要 Catalog 文件

| 场景 | 是否需要 |
|------|---------|
| 客户端与服务端约定好函数协议，服务端手动编写 JSON | 不需要（双方约定即可） |
| AI/LLM 自动生成包含自定义函数调用的 UI JSON | **需要**（AI 需要 catalog 来了解可用函数和参数 schema） |
| 需要对 JSON 进行 schema 校验 | **需要**（catalog 提供 JSON Schema 校验规则） |

### Catalog 文件格式

自定义函数的 catalog 文件遵循标准 A2UI catalog JSON Schema 格式。核心结构如下：

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "custom_functions_catalog.json",
  "title": "My Custom Functions Catalog",
  "description": "Extended catalog with custom functions.",
  "catalogId": "custom_functions_catalog.json",
  "components": {},
  "functions": {
    "myFunction": {
      "type": "object",
      "description": "Description of what this function does, for AI/LLM to understand.",
      "properties": {
        "call": { "const": "myFunction" },
        "args": {
          "type": "object",
          "properties": {
            "param1": {
              "$ref": "https://a2ui.org/specification/v0_9/common_types.json#/$defs/DynamicString",
              "description": "Description of this parameter."
            }
          },
          "required": ["param1"],
          "unevaluatedProperties": false
        },
        "returnType": { "const": "string" }
      },
      "required": ["call", "args"],
      "unevaluatedProperties": false
    }
  },
  "$defs": {
    "anyFunction": {
      "oneOf": [
        { "$ref": "#/functions/myFunction" }
      ]
    }
  },
  "extends": "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json"
}
```

### 关键字段说明

| 字段 | 说明 |
|------|------|
| `catalogId` | Catalog 的唯一标识，在 `createSurface` 消息中引用 |
| `functions` | 自定义函数定义，每个 key 是函数名 |
| `functions.*.properties.call` | 函数名常量，与 JSON 中 `"call"` 字段对应 |
| `functions.*.properties.args` | 参数定义，使用 `DynamicString` / `DynamicNumber` / `DynamicBoolean` 引用支持数据绑定 |
| `functions.*.properties.returnType` | 返回类型常量：`"string"` / `"boolean"` / `"number"` / `"void"` |
| `extends` | 声明继承自 basic catalog，表示同时支持所有标准组件和内置函数 |
| `$defs.anyFunction` | 列出所有自定义函数的引用，供 schema 校验使用 |

### 参数类型引用

根据函数参数的类型，使用对应的 `$ref` 引用：

| 参数类型 | JSON Schema 引用 |
|---------|-----------------|
| 字符串（支持数据绑定） | `$ref: "...common_types.json#/$defs/DynamicString"` |
| 数值（支持数据绑定） | `$ref: "...common_types.json#/$defs/DynamicNumber"` |
| 布尔值（支持数据绑定） | `$ref: "...common_types.json#/$defs/DynamicBoolean"` |
| 纯字面量字符串 | `"type": "string"` |
| 纯字面量数值 | `"type": "number"` |

> **提示**：使用 `Dynamic*` 类型意味着参数值既可以是字面量，也可以是 `{"path": "/xxx"}` 数据绑定引用。

### 如何从注册代码推导 Catalog 定义

根据你在客户端注册的函数 Handler，在 catalog 中定义对应的 JSON Schema：

```kotlin
// Android 注册代码
A2UISurfaceView.registerCustomFunction(
    name = "formatPhone",
    returnType = A2UISurfaceView.RETURN_TYPE_STRING,
    invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
) { args ->
    val phone = args.optString("phone", "")  // → args.phone: DynamicString
    // ... 格式化逻辑
    formattedPhone  // → returnType: "string"
}
```

对应的 catalog 函数定义：

```json
"formatPhone": {
  "type": "object",
  "description": "Formats an 11-digit phone number into 'XXX-XXXX-XXXX' format.",
  "properties": {
    "call": { "const": "formatPhone" },
    "args": {
      "type": "object",
      "properties": {
        "phone": {
          "$ref": "https://a2ui.org/specification/v0_9/common_types.json#/$defs/DynamicString",
          "description": "The raw phone number string (expected 11 digits)."
        }
      },
      "required": ["phone"],
      "unevaluatedProperties": false
    },
    "returnType": { "const": "string" }
  },
  "required": ["call", "args"],
  "unevaluatedProperties": false
}
```

### 在 JSON 消息中引用自定义 Catalog

生成 catalog 文件后，在 `createSurface` 消息中将 `catalogId` 指向你的自定义 catalog：

```json
{
  "version": "v0.9",
  "createSurface": {
    "surfaceId": "my-surface",
    "catalogId": "custom_functions_catalog.json"
  }
}
```

### 参考示例

项目中提供了完整的自定义函数 catalog 示例文件，包含 6 个函数的定义：

```
a2ui/catalogs/
└── custom_functions_catalog.json     # ← 自定义函数 Catalog 文件（AI/LLM 参考）

a2ui/androidAppForNativePage/src/main/assets/a2ui_custom_functions/
├── 01_string_return/
│   ├── 1_custom_greeting.json        # 引用了 custom_functions_catalog.json
│   └── 2_custom_format_phone.json    # 引用了 custom_functions_catalog.json
├── 02_boolean_return/
│   └── 1_custom_is_vip.json          # 引用了 custom_functions_catalog.json
├── 03_number_return/
│   └── 1_custom_calculate_discount.json  # 引用了 custom_functions_catalog.json
├── 04_void_action/
│   └── 1_custom_log_event.json       # 引用了 custom_functions_catalog.json
└── 05_args_passing/
    └── 1_custom_build_summary.json   # 引用了 custom_functions_catalog.json
```

> **提示**：可以直接参考 [`a2ui/catalogs/custom_functions_catalog.json`](../catalogs/custom_functions_catalog.json) 的格式，为你的业务自定义函数编写对应的 catalog 文件。标准 basic catalog 的完整函数定义参见 A2UI 官方规范：<https://a2ui.org/>。

---

## 平台接入指南

### Android

#### 注册自定义函数

```kotlin
import com.tencent.kuikly.a2ui.render.android.A2UISurfaceView

// 注册一个返回 String 的同步函数
A2UISurfaceView.registerCustomFunction(
    name = "getGreeting",                          // 函数名（与 JSON 中 "call" 字段对应）
    returnType = A2UISurfaceView.RETURN_TYPE_STRING, // 返回类型
    invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC    // 调用模式
) { args ->
    // args 是 JSONObject 类型，包含已解析的参数
    val name = args.optString("name", "World")
    "Hello, $name!"  // 返回值
}
```

#### 注销自定义函数

```kotlin
A2UISurfaceView.unregisterCustomFunction("getGreeting")
```

#### API 常量

| 常量 | 值 | 说明 |
|------|---|------|
| `RETURN_TYPE_STRING` | `"string"` | 返回字符串 |
| `RETURN_TYPE_BOOLEAN` | `"boolean"` | 返回布尔值 |
| `RETURN_TYPE_NUMBER` | `"number"` | 返回数值 |
| `RETURN_TYPE_VOID` | `"void"` | 无返回值 |
| `INVOKE_MODE_SYNC` | `"sync"` | 同步调用（在 Kuikly Context Thread 执行） |
| `INVOKE_MODE_ASYNC` | `"async"` | 异步调用（仅 void 函数有效，在主线程执行） |

#### Handler 参数类型

Handler 的 `args` 参数类型为 `org.json.JSONObject`，常用方法：

```kotlin
args.optString("key", "default")   // 获取 String
args.optInt("key", 0)              // 获取 Int
args.optDouble("key", 0.0)         // 获取 Double
args.optBoolean("key", false)      // 获取 Boolean
```

#### 完整示例

```kotlin
class MyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 在使用 A2UISurfaceView 之前注册自定义函数
        registerCustomFunctions()

        // ... 创建和使用 A2UISurfaceView ...
    }

    private fun registerCustomFunctions() {
        // 格式化电话号码
        A2UISurfaceView.registerCustomFunction(
            name = "formatPhone",
            returnType = A2UISurfaceView.RETURN_TYPE_STRING,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val phone = args.optString("phone", "")
            if (phone.length == 11) {
                "${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}"
            } else {
                phone
            }
        }

        // VIP 判断
        A2UISurfaceView.registerCustomFunction(
            name = "isVip",
            returnType = A2UISurfaceView.RETURN_TYPE_BOOLEAN,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val level = args.optInt("level", 0)
            level >= 3
        }

        // 折扣计算（保留2位小数）
        A2UISurfaceView.registerCustomFunction(
            name = "calculateDiscount",
            returnType = A2UISurfaceView.RETURN_TYPE_NUMBER,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val price = args.optDouble("price", 0.0)
            val rate = args.optDouble("rate", 1.0)
            Math.round(price * rate * 100) / 100.0
        }

        // 日志上报（void 函数）
        A2UISurfaceView.registerCustomFunction(
            name = "logEvent",
            returnType = A2UISurfaceView.RETURN_TYPE_VOID,
            invokeMode = A2UISurfaceView.INVOKE_MODE_SYNC
        ) { args ->
            val event = args.optString("event", "")
            val data = args.optString("data", "")
            Log.i("A2UI", "logEvent: event=$event, data=$data")
            null  // void 函数返回 null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 可选：页面销毁时注销
        A2UISurfaceView.unregisterCustomFunction("formatPhone")
        A2UISurfaceView.unregisterCustomFunction("isVip")
        A2UISurfaceView.unregisterCustomFunction("calculateDiscount")
        A2UISurfaceView.unregisterCustomFunction("logEvent")
    }
}
```

---

### iOS

#### 注册自定义函数

```swift
import UIKit
// A2UISurfaceView 在同一 target 中，无需额外 import

// 注册一个返回 String 的同步函数
A2UISurfaceView.registerCustomFunction(
    name: "getGreeting",                           // 函数名
    returnType: A2UISurfaceView.returnTypeString,  // 返回类型
    invokeMode: A2UISurfaceView.invokeModeSync     // 调用模式
) { args in
    // args 是 [String: Any] 类型
    let name = args["name"] as? String ?? "World"
    return "Hello, \(name)!"
}
```

#### 注销自定义函数

```swift
A2UISurfaceView.unregisterCustomFunction(name: "getGreeting")
```

#### API 常量

| 常量 | 值 | 说明 |
|------|---|------|
| `returnTypeString` | `"string"` | 返回字符串 |
| `returnTypeBoolean` | `"boolean"` | 返回布尔值 |
| `returnTypeNumber` | `"number"` | 返回数值 |
| `returnTypeVoid` | `"void"` | 无返回值 |
| `invokeModeSync` | `"sync"` | 同步调用 |
| `invokeModeAsync` | `"async"` | 异步调用（仅 void 函数有效） |

#### Handler 参数类型

Handler 的 `args` 参数类型为 `[String: Any]`，取值方式：

```swift
let str = args["key"] as? String ?? "default"           // String
let num = (args["key"] as? NSNumber)?.intValue ?? 0     // Int
let dbl = (args["key"] as? NSNumber)?.doubleValue ?? 0  // Double
let flag = (args["key"] as? NSNumber)?.boolValue ?? false // Bool
```

> **注意**：数值类型参数在 iOS 端以 `NSNumber` 形式传入，需要通过 `.intValue` / `.doubleValue` 等方法转换。

#### 完整示例

```swift
class MyViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // 在使用 A2UISurfaceView 之前注册自定义函数
        registerCustomFunctions()

        // ... 创建和使用 A2UISurfaceView ...
    }

    private func registerCustomFunctions() {
        // 格式化电话号码
        A2UISurfaceView.registerCustomFunction(
            name: "formatPhone",
            returnType: A2UISurfaceView.returnTypeString,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let phone = args["phone"] as? String ?? ""
            if phone.count == 11 {
                let start = phone.prefix(3)
                let mid = phone.dropFirst(3).prefix(4)
                let end = phone.suffix(4)
                return "\(start)-\(mid)-\(end)"
            }
            return phone
        }

        // VIP 判断
        A2UISurfaceView.registerCustomFunction(
            name: "isVip",
            returnType: A2UISurfaceView.returnTypeBoolean,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let level = (args["level"] as? NSNumber)?.intValue ?? 0
            return level >= 3
        }

        // 折扣计算（保留2位小数）
        A2UISurfaceView.registerCustomFunction(
            name: "calculateDiscount",
            returnType: A2UISurfaceView.returnTypeNumber,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let price = (args["price"] as? NSNumber)?.doubleValue ?? 0.0
            let rate = (args["rate"] as? NSNumber)?.doubleValue ?? 1.0
            let result = (price * rate * 100).rounded() / 100
            return NSNumber(value: result)
        }

        // 日志上报（void 函数）
        A2UISurfaceView.registerCustomFunction(
            name: "logEvent",
            returnType: A2UISurfaceView.returnTypeVoid,
            invokeMode: A2UISurfaceView.invokeModeSync
        ) { args in
            let event = args["event"] as? String ?? ""
            let data = args["data"] as? String ?? ""
            print("logEvent: event=\(event), data=\(data)")
            return nil  // void 函数返回 nil
        }
    }

    deinit {
        // 可选：页面销毁时注销
        A2UISurfaceView.unregisterCustomFunction(name: "formatPhone")
        A2UISurfaceView.unregisterCustomFunction(name: "isVip")
        A2UISurfaceView.unregisterCustomFunction(name: "calculateDiscount")
        A2UISurfaceView.unregisterCustomFunction(name: "logEvent")
    }
}
```

---

### HarmonyOS (OHOS)

#### 注册自定义函数

```typescript
import { registerA2UICustomFunction } from '@kuikly-open/a2ui-render';

// 注册一个返回 String 的同步函数
registerA2UICustomFunction(
    'getGreeting',   // 函数名
    'string',        // 返回类型: 'string' | 'boolean' | 'number' | 'void'
    'sync',          // 调用模式: 'sync' | 'async'
    (args) => {
        // args 是 Record<string, Object> 类型
        const name = (args['name'] as string) ?? 'World';
        return `Hello, ${name}!`;
    }
);
```

#### 注销自定义函数

```typescript
import { unregisterA2UICustomFunction } from '@kuikly-open/a2ui-render';

unregisterA2UICustomFunction('getGreeting');
```

#### API 签名

```typescript
function registerA2UICustomFunction(
    name: string,                              // 函数名
    returnType: string,                        // 'string' | 'boolean' | 'number' | 'void'
    invokeMode: string,                        // 'sync' | 'async'
    handler: (args: Record<string, Object>) => Object | null  // 处理函数
): void

function unregisterA2UICustomFunction(name: string): void
```

#### Handler 参数类型

Handler 的 `args` 参数类型为 `Record<string, Object>`，取值方式：

```typescript
const str = (args['key'] as string) ?? 'default';   // String
const num = (args['key'] as number) ?? 0;           // Number
const flag = (args['key'] as boolean) ?? false;     // Boolean
```

#### 完整示例

```typescript
import { registerA2UICustomFunction, unregisterA2UICustomFunction } from '@kuikly-open/a2ui-render';

@Entry
@Component
struct MyPage {

  aboutToAppear(): void {
    this.registerCustomFunctions();
  }

  aboutToDisappear(): void {
    this.unregisterCustomFunctions();
  }

  private registerCustomFunctions(): void {
    // 格式化电话号码
    registerA2UICustomFunction('formatPhone', 'string', 'sync', (args) => {
      const phone = (args['phone'] as string) ?? '';
      if (phone.length === 11) {
        return `${phone.substring(0, 3)}-${phone.substring(3, 7)}-${phone.substring(7)}`;
      }
      return phone;
    });

    // VIP 判断
    registerA2UICustomFunction('isVip', 'boolean', 'sync', (args) => {
      const level = (args['level'] as number) ?? 0;
      return level >= 3;
    });

    // 折扣计算（保留2位小数）
    registerA2UICustomFunction('calculateDiscount', 'number', 'sync', (args) => {
      const price = (args['price'] as number) ?? 0;
      const rate = (args['rate'] as number) ?? 1;
      return Math.round(price * rate * 100) / 100;
    });

    // 日志上报（void 函数）
    registerA2UICustomFunction('logEvent', 'void', 'sync', (args) => {
      const event = (args['event'] as string) ?? '';
      const data = (args['data'] as string) ?? '';
      console.info(`logEvent: event=${event}, data=${data}`);
      return null;  // void 函数返回 null
    });
  }

  private unregisterCustomFunctions(): void {
    unregisterA2UICustomFunction('formatPhone');
    unregisterA2UICustomFunction('isVip');
    unregisterA2UICustomFunction('calculateDiscount');
    unregisterA2UICustomFunction('logEvent');
  }

  build() {
    // ... 页面 UI ...
  }
}
```

---

## 注意事项

### 注册时机

- 自定义函数必须在 `A2UISurfaceView` 加载 JSON 消息流**之前**注册
- 推荐在页面 `onCreate` / `viewDidLoad` / `aboutToAppear` 中注册
- 函数注册是**全局**的：一旦注册，所有 A2UI 实例都可以调用

### 同步 vs 异步

| 模式 | 适用返回类型 | 执行线程 | 说明 |
|------|------------|---------|------|
| `sync` | 所有类型 | Kuikly Context Thread | 必须快速返回，避免阻塞渲染 |
| `async` | 仅 `void` | 平台主线程 | 适合耗时操作（如网络请求、弹窗） |

> **重要**：非 void 返回类型的函数会被强制为 `sync` 模式，因为表达式求值需要立即获得返回值。

### 保留函数名

以下函数名为 A2UI v0.9 协议内置函数，**不可**被自定义函数覆盖：

- Check 函数：`required`, `regex`, `length`, `numeric`, `email`, `and`, `or`, `not`
- Format 函数：`formatString`, `formatNumber`, `formatCurrency`, `formatDate`, `pluralize`
- Action 函数：`openUrl`

### 参数解析

- `args` 中的值支持 `{"path": "/xxx"}` 语法，框架会自动从 `dataModel` 中解析实际值后传入 Handler
- 嵌套函数调用也会被自动解析（如 `{"call": "otherFunc", ...}`）
- 字面量值（string / number / boolean）直接传入

### 返回值类型约定

| returnType | Android 返回类型 | iOS 返回类型 | OHOS 返回类型 |
|-----------|-----------------|-------------|--------------|
| `string` | `String` | `String` | `string` |
| `boolean` | `Boolean` | `Bool` | `boolean` |
| `number` | `Double` / `Int` | `NSNumber` | `number` |
| `void` | `null` | `nil` | `null` |

---

## Demo 参考

三端 Demo 应用中提供了完整的自定义函数示例页面，包含以下测试用例：

| 测试分组 | 函数 | 说明 |
|---------|------|------|
| 返回 String | `getGreeting(name)` | 拼接问候语 |
| 返回 String | `formatPhone(phone)` | 格式化电话号码 |
| 返回 Boolean | `isVip(level)` | VIP 等级判断 |
| 返回 Number | `calculateDiscount(price, rate)` | 折扣计算 |
| Void Action | `logEvent(event, data)` | 日志上报 |
| 多参数绑定 | `buildSummary(name, count, status)` | 多参数拼接摘要 |

### Demo 源码位置

Native 页接入示例（`androidAppForNativePage` / `iosAppForNativePage` / `ohosAppForNativePage`）中都提供了 Custom Function 演示页：

- **Android**: `a2ui/androidAppForNativePage/src/main/java/com/tencent/kuikly/a2ui/demo/CustomFunctionDemoActivity.kt`
- **iOS**: `a2ui/iosShared/Sources/CustomFunctionDemoViewController.swift`（`iosAppForNativePage` 与 `iosAppForKuiklyPage` 通过 `iosShared` 共用）
- **HarmonyOS**: `a2ui/ohosAppForNativePage/entry/src/main/ets/pages/CustomFunctionDemoPage.ets`

Kuikly 页接入示例（`androidAppForKuiklyPage` / `iosAppForKuiklyPage` / `ohosAppForKuiklyPage`）中的 Custom Function 演示页布局与 Native 页接入一致，注册 API 完全相同——因为 `A2UICustomFunctionRegistry` 是 KMP 进程级单例，一次注册对两种接入模式同时生效。

### 测试 JSON 文件位置

三端各自 App 的资源目录下均放置了同一份测试 JSON，路径分别为：

- Android: `androidAppForNativePage/src/main/assets/a2ui_custom_functions/`（以及 `androidAppForKuiklyPage/…` 同名目录）
- iOS: `iosAppForNativePage/Resources/a2ui_custom_functions/`（以及 `iosAppForKuiklyPage/…` 同名目录）
- HarmonyOS: `ohosAppForNativePage/entry/src/main/resources/rawfile/a2ui_custom_functions/`（以及 `ohosAppForKuiklyPage/…` 同名目录）

每份目录都包含如下子目录：

- `01_string_return/` — String 返回值测试
- `02_boolean_return/` — Boolean 返回值测试
- `03_number_return/` — Number 返回值测试
- `04_void_action/` — Void Action 测试
- `05_args_passing/` — 多参数传递测试

---

## 常见问题

### Q: 自定义函数返回值显示为空？

检查以下几点：
1. 函数是否在 `writeStream` 之前注册
2. `returnType` 是否与实际返回值类型匹配
3. Handler 是否正确返回了值（void 函数返回 `null`/`nil`）

### Q: 参数值为 dict/object 而非预期的字符串？

确认 JSON 中参数使用了 `{"path": "/xxx"}` 语法。框架会自动解析 path 引用为实际值。如果看到原始的 path 对象，说明解析链路可能存在问题。

### Q: 数值参数在 iOS 端获取失败？

iOS 端数值参数以 `NSNumber` 形式传入，需要使用 `(args["key"] as? NSNumber)?.doubleValue` 方式获取，不能直接 `as? Double`。

### Q: 能否注册异步返回值的函数？

目前非 void 函数必须同步返回值（因为表达式求值是同步的）。如果需要异步数据，建议：
1. 先注册函数返回默认值
2. 异步获取数据后，通过 `updateDataModel` 更新数据源触发重新渲染
