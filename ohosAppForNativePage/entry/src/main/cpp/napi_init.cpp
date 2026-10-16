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
 * A2UI ohosApp entry C++ layer.
 *
 * This is a minimal napi_init.cpp that:
 * 1. Loads the Kotlin/Native libshared.so (containing A2UI's initKuikly function)
 * 2. Calls initKuikly() to register the "a2ui" page route and callKotlin callback
 * 3. Exports the initKuikly NAPI function for ArkTS to call
 *
 * Unlike the full Kuikly DSL ohosApp, A2UI doesn't need custom adapters
 * (color, font, image) since it uses standard Kuikly rendering.
 *
 * Custom A2UI components (e.g. `A2UIWeb`, `MyA2UIView`) are registered
 * **ArkTS-side only** via `registerA2UICustomRenderView(...)`. On OHOS the
 * KMP side therefore cannot drive measurement through the usual
 * `IKRRenderShadowExport` C++ registry (which only accepts creators
 * linked into the host app's own `libxxx_entry.so`). A2UI's
 * `A2UICustomComponentView` gates on
 * `isCustomComponentShadowRequired = false` on this platform and relies
 * on the JSON-side `width` / `height` instead — see
 * `docs/A2UI_CUSTOM_COMPONENT_GUIDE.md` for the authoring contract.
 */

#include "napi/native_api.h"
#include "thirdparty/biz_entry/libshared_api.h"

// ============================================================
// NAPI entry point
// ============================================================

static napi_value InitKuikly(napi_env env, napi_callback_info info) {
    // Kotlin/Native cinterop notes:
//   `:a2ui` (the runtime-only shared lib) declares
    //   `@CName("initKuikly") fun initKuikly()` in A2UIKuiklyEntry.kt,
    //   which yields a top-level `extern initKuikly()` in libshared_api.h.
    //   The same name inside `api->kotlin.root` therefore gets renamed to
    //   `initKuikly_` by cinterop to avoid the collision. We deliberately
    //   call the top-level symbol — that's the intended C entry point.
    (void)libshared_symbols(); // keeps the symbol table loaded
    int handler = initKuikly();
    napi_value result;
    napi_create_int32(env, handler, &result);
    return result;
}

EXTERN_C_START
static napi_value Init(napi_env env, napi_value exports) {
    napi_property_descriptor desc[] = {
        {"initKuikly", nullptr, InitKuikly, nullptr, nullptr, nullptr, napi_default, nullptr},
    };
    napi_define_properties(env, exports, sizeof(desc) / sizeof(desc[0]), desc);
    return exports;
}
EXTERN_C_END

static napi_module entry_module = {
    .nm_version = 1,
    .nm_flags = 0,
    .nm_filename = nullptr,
    .nm_register_func = Init,
    .nm_modname = "kuikly_entry",
    .nm_priv = static_cast<void *>(0),
    .reserved = {0},
};

extern "C" __attribute__((constructor)) void RegisterKuikly_EntryModule(void) {
    napi_module_register(&entry_module);
}
