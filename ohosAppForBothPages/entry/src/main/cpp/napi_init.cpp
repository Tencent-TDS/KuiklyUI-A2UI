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

#include <cstdlib>  // malloc / free used by A2UI registry writers below

// ============================================================
// NAPI entry point
// ============================================================

static napi_value InitKuikly(napi_env env, napi_callback_info info) {
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

// Push an async "custom view size changed" notification from ArkTS into
// the KMP layer. Companion to [notifyA2UICustomViewSizeChanged] on the
// ArkTS side; routes through A2UIBridgeManager.notifyCustomViewResize.
// Arg layout: (pagerId: string, nativeRef: number, width: number, height: number).
static napi_value A2UINotifyCustomViewResize(napi_env env, napi_callback_info info) {
    size_t argc = 4;
    napi_value argv[4];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 4) {
        return nullptr;
    }
    char* pagerId = GetStringArg(env, argv[0]);
    int32_t nativeRef = 0;
    napi_get_value_int32(env, argv[1], &nativeRef);
    double widthD = 0.0;
    double heightD = 0.0;
    napi_get_value_double(env, argv[2], &widthD);
    napi_get_value_double(env, argv[3], &heightD);
    if (pagerId) {
        libshared_symbols()->kotlin.root.a2uiNotifyCustomViewResize(
            pagerId, nativeRef, (float)widthD, (float)heightD);
    }
    if (pagerId) free(pagerId);
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
        {"a2uiNotifyCustomViewResize", nullptr, A2UINotifyCustomViewResize, nullptr, nullptr, nullptr, napi_default, nullptr},
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
