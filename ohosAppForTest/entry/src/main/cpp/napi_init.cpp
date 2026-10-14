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
 * Custom shadow components use the pure-C API (KRShadowExportC.h) to avoid
 * C++ ABI coupling between libkuikly_entry.so and libkuikly.so.
 */

#include "napi/native_api.h"
#include "thirdparty/biz_entry/libshared_api.h"
#include "libohos_render/export/KRShadowExportC.h"
#include <stdlib.h>

// ============================================================
// A2UIWeb Shadow (pure C, no C++ classes needed)
// ============================================================

static KRShadowHandle A2UIWebCreate(void) {
    // No per-instance state needed for this demo; return a non-NULL sentinel.
    return (KRShadowHandle)(intptr_t)1;
}

static void A2UIWebCalculateSize(KRShadowHandle handle,
                                  double constraint_width,
                                  double constraint_height,
                                  double *out_width,
                                  double *out_height) {
    (void)handle;
    (void)constraint_height;
    *out_width = constraint_width;
    *out_height = 240.0;
}

static void A2UIWebDestroy(KRShadowHandle handle) {
    (void)handle;
}

static const KRShadowVTable kA2UIWebVTable = {
    .set_prop = NULL,
    .calculate_size = A2UIWebCalculateSize,
    .destroy = A2UIWebDestroy,
};

// ============================================================
// MyA2UIView Shadow (pure C, no C++ classes needed)
// ============================================================

static KRShadowHandle MyA2UIViewCreate(void) {
    return (KRShadowHandle)(intptr_t)1;
}

static void MyA2UIViewCalculateSize(KRShadowHandle handle,
                                     double constraint_width,
                                     double constraint_height,
                                     double *out_width,
                                     double *out_height) {
    (void)handle;
    (void)constraint_height;
    *out_width = constraint_width;
    *out_height = 200.0;
}

static void MyA2UIViewDestroy(KRShadowHandle handle) {
    (void)handle;
}

static const KRShadowVTable kMyA2UIViewVTable = {
    .set_prop = NULL,
    .calculate_size = MyA2UIViewCalculateSize,
    .destroy = MyA2UIViewDestroy,
};

// ============================================================
// Registration
// ============================================================

static void RegisterCustomComponentShadows(void) {
    KRRegisterShadowCreator("A2UIWeb", A2UIWebCreate, &kA2UIWebVTable);
    KRRegisterShadowCreator("MyA2UIView", MyA2UIViewCreate, &kMyA2UIViewVTable);
}

// ============================================================
// NAPI entry point
// ============================================================

static int initDone = false;

static napi_value InitKuikly(napi_env env, napi_callback_info info) {
    if (!initDone) {
        initDone = true;
        RegisterCustomComponentShadows();
    }

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
