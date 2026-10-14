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

    auto api = libshared_symbols();
    int handler = api->kotlin.root.initKuikly();
    napi_value result;
    napi_create_int32(env, handler, &result);
    return result;
}

// ============================================================
// A2UI registry writers (Kuikly-page mode).
//
// These NAPI methods forward ArkTS-side `registerA2UICustomFunction / View /
// Icon` calls straight into the Kotlin-side global registries, bypassing the
// Pager `sendEvent` path that only works when the pager extends `A2UIPager`.
// See `A2UIRegistryBridge.kt` in `:demo` for the Kotlin implementation.
//
// String arguments are pulled from ArkTS with `napi_get_value_string_utf8`
// twice (first for byte length, then for the actual bytes). The buffers are
// freed before the Kotlin bridge call so any exception from the registry
// side never leaks memory here.
// ============================================================

// Fetch a UTF-8 string argument and return it as a heap-allocated buffer.
// The caller must `free()` the returned pointer. Returns NULL on failure.
static char* GetStringArg(napi_env env, napi_value value) {
    size_t needed = 0;
    if (napi_get_value_string_utf8(env, value, NULL, 0, &needed) != napi_ok) {
        return NULL;
    }
    char* buf = (char*)malloc(needed + 1);
    if (!buf) {
        return NULL;
    }
    size_t written = 0;
    if (napi_get_value_string_utf8(env, value, buf, needed + 1, &written) != napi_ok) {
        free(buf);
        return NULL;
    }
    return buf;
}

static napi_value A2UIRegisterCustomFunction(napi_env env, napi_callback_info info) {
    size_t argc = 2;
    napi_value argv[2];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 2) {
        return nullptr;
    }
    char* name = GetStringArg(env, argv[0]);
    char* spec = GetStringArg(env, argv[1]);
    if (name && spec) {
        libshared_symbols()->kotlin.root.a2uiRegisterCustomFunction(name, spec);
    }
    if (name) free(name);
    if (spec) free(spec);
    return nullptr;
}

static napi_value A2UIUnregisterCustomFunction(napi_env env, napi_callback_info info) {
    size_t argc = 1;
    napi_value argv[1];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 1) {
        return nullptr;
    }
    char* name = GetStringArg(env, argv[0]);
    if (name) {
        libshared_symbols()->kotlin.root.a2uiUnregisterCustomFunction(name);
        free(name);
    }
    return nullptr;
}

static napi_value A2UIRegisterCustomView(napi_env env, napi_callback_info info) {
    size_t argc = 1;
    napi_value argv[1];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 1) {
        return nullptr;
    }
    char* viewName = GetStringArg(env, argv[0]);
    if (viewName) {
        libshared_symbols()->kotlin.root.a2uiRegisterCustomView(viewName);
        free(viewName);
    }
    return nullptr;
}

static napi_value A2UIUnregisterCustomView(napi_env env, napi_callback_info info) {
    size_t argc = 1;
    napi_value argv[1];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 1) {
        return nullptr;
    }
    char* viewName = GetStringArg(env, argv[0]);
    if (viewName) {
        libshared_symbols()->kotlin.root.a2uiUnregisterCustomView(viewName);
        free(viewName);
    }
    return nullptr;
}

static napi_value A2UIRegisterIcon(napi_env env, napi_callback_info info) {
    size_t argc = 2;
    napi_value argv[2];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 2) {
        return nullptr;
    }
    char* name = GetStringArg(env, argv[0]);
    char* uri = GetStringArg(env, argv[1]);
    if (name && uri) {
        libshared_symbols()->kotlin.root.a2uiRegisterIcon(name, uri);
    }
    if (name) free(name);
    if (uri) free(uri);
    return nullptr;
}

static napi_value A2UIUnregisterIcon(napi_env env, napi_callback_info info) {
    size_t argc = 1;
    napi_value argv[1];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 1) {
        return nullptr;
    }
    char* name = GetStringArg(env, argv[0]);
    if (name) {
        libshared_symbols()->kotlin.root.a2uiUnregisterIcon(name);
        free(name);
    }
    return nullptr;
}

// Push a DateTimeInput picker result from ArkTS back into the KMP layer.
// Arg layout: (dslInstanceId: string, tag: number, isoString: string).
static napi_value A2UINotifyPickerResult(napi_env env, napi_callback_info info) {
    size_t argc = 3;
    napi_value argv[3];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 3) {
        return nullptr;
    }
    char* dslInstanceId = GetStringArg(env, argv[0]);
    int32_t tag = 0;
    napi_get_value_int32(env, argv[1], &tag);
    char* isoString = GetStringArg(env, argv[2]);
    if (dslInstanceId && isoString) {
        libshared_symbols()->kotlin.root.a2uiNotifyPickerResult(dslInstanceId, tag, isoString);
    }
    if (dslInstanceId) free(dslInstanceId);
    if (isoString) free(isoString);
    return nullptr;
}

EXTERN_C_START
static napi_value Init(napi_env env, napi_value exports) {
    napi_property_descriptor desc[] = {
        {"initKuikly", nullptr, InitKuikly, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiRegisterCustomFunction", nullptr, A2UIRegisterCustomFunction, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiUnregisterCustomFunction", nullptr, A2UIUnregisterCustomFunction, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiRegisterCustomView", nullptr, A2UIRegisterCustomView, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiUnregisterCustomView", nullptr, A2UIUnregisterCustomView, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiRegisterIcon", nullptr, A2UIRegisterIcon, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiUnregisterIcon", nullptr, A2UIUnregisterIcon, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiNotifyPickerResult", nullptr, A2UINotifyPickerResult, nullptr, nullptr, nullptr, napi_default, nullptr},
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
