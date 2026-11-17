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
 * **ArkTS-side only** via `registerA2UICustomRenderView(...)`. On OHOS
 * the KMP side therefore cannot drive measurement through the usual
 * `IKRRenderShadowExport` C++ registry (which only accepts creators
 * linked into the host app's own `libxxx_entry.so`). Intrinsic size is
 * instead reported through the ArkTS `measurer` registered alongside
 * the creator — A2UI picks it up via the standard Kuikly Module channel
 * (`A2UIBridgeModule.calculateCustomViewSize`, `syncCall=1`); JSON-side
 * `width` / `height` still act as a hard override. Android and iOS use
 * the same Module channel against platform-specific measurer tables.
 * See `docs/A2UI_CUSTOM_COMPONENT_GUIDE.md` for the authoring contract.
 */

#include "napi/native_api.h"
#include "thirdparty/biz_entry/libshared_api.h"

#include <cstdlib>  // malloc / free used by A2UI registry writers below
#include <cstring>  // strdup / memcpy for the RPC callback bridge below

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

// ============================================================
// A2UI v1.0 Bidirectional-RPC NAPI writers.
//
// Three NAPI methods mirror the Android / iOS RPC facades:
//   - a2uiRespondToRpc     — close an inbound RPC with a success result
//   - a2uiFailRpc          — close an inbound RPC with a failure
//   - a2uiInvokeAgentFunction — fire a renderer->agent RPC, deliver the
//                               reply through a threadsafe function
//
// The first two are pure parameter forwarders (zero callback state); the
// third requires a `napi_threadsafe_function` ref-counted callback holder
// because the Kotlin dispatcher will invoke our C callback on *whatever
// thread the response arrives on* — typically not the ArkTS main thread.
// ============================================================

static napi_value A2UIRespondToRpc(napi_env env, napi_callback_info info) {
    size_t argc = 4;
    napi_value argv[4];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 4) {
        return nullptr;
    }
    char* instanceId = GetStringArg(env, argv[0]);
    char* surfaceId  = GetStringArg(env, argv[1]);
    char* requestId  = GetStringArg(env, argv[2]);
    char* resultJson = GetStringArg(env, argv[3]);
    if (instanceId && surfaceId && requestId && resultJson) {
        libshared_symbols()->kotlin.root.a2uiRespondToRpc(
            instanceId, surfaceId, requestId, resultJson);
    }
    if (instanceId) free(instanceId);
    if (surfaceId)  free(surfaceId);
    if (requestId)  free(requestId);
    if (resultJson) free(resultJson);
    return nullptr;
}

static napi_value A2UIFailRpc(napi_env env, napi_callback_info info) {
    size_t argc = 6;
    napi_value argv[6];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 6) {
        return nullptr;
    }
    char* instanceId  = GetStringArg(env, argv[0]);
    char* surfaceId   = GetStringArg(env, argv[1]);
    char* requestId   = GetStringArg(env, argv[2]);
    char* code        = GetStringArg(env, argv[3]);
    char* message     = GetStringArg(env, argv[4]);
    char* detailsJson = GetStringArg(env, argv[5]);
    if (instanceId && surfaceId && requestId && code && message && detailsJson) {
        libshared_symbols()->kotlin.root.a2uiFailRpc(
            instanceId, surfaceId, requestId, code, message, detailsJson);
    }
    if (instanceId)  free(instanceId);
    if (surfaceId)   free(surfaceId);
    if (requestId)   free(requestId);
    if (code)        free(code);
    if (message)     free(message);
    if (detailsJson) free(detailsJson);
    return nullptr;
}

// ─────────────────────────────────────────────────────────────
// Threadsafe-function plumbing for a2uiInvokeAgentFunction.
//
// Lifecycle:
//
//   1. ArkTS calls a2uiInvokeAgentFunction(..., jsCallback).
//   2. We `napi_create_threadsafe_function` from `jsCallback`, store the
//      handle in a heap-allocated `RpcCallbackHolder` and pass its pointer
//      as the Kotlin-side `callbackCtx`.
//   3. Kotlin fires outbound RPC, mints a requestId, returns.
//   4. When the dispatcher resolves (success/failure/timeout/close),
//      Kotlin invokes our C function `RpcCompletionTrampoline(ctx, json)`
//      on whatever thread it's running on. We `strdup` the JSON (which is
//      only borrowed for the duration of the call) and
//      `napi_call_threadsafe_function` to hop onto the ArkTS main thread.
//   5. On the ArkTS main thread we `napi_call_function(jsCallback, [json])`,
//      `free` the strdup'd buffer, then `napi_release_threadsafe_function`
//      to tear down the holder.
//
// Each invocation owns exactly one holder; the holder is released after
// the first (and only) callback fires, matching the Kotlin dispatcher's
// "onComplete fires exactly once" contract (see RPC_HOST_INTEGRATION.md §7).
// ─────────────────────────────────────────────────────────────

typedef struct {
    napi_threadsafe_function tsfn;  // callback handle for ArkTS main thread
} RpcCallbackHolder;

// Called by napi_call_threadsafe_function on the ArkTS main thread. Receives
// the strdup'd JSON as `data`, the original jsCallback through `js_callback`,
// invokes it, then frees the buffer and releases the holder.
static void RpcTsfnCallJS(napi_env env, napi_value js_callback,
                         void* /* context */, void* data) {
    char* json = (char*)data;
    if (env == nullptr) {
        // env is NULL when the tsfn is being torn down with pending items.
        if (json) free(json);
        return;
    }
    napi_value undef;
    napi_get_undefined(env, &undef);
    napi_value jsonStr;
    napi_create_string_utf8(env,
        json ? json : "",
        json ? strlen(json) : 0,
        &jsonStr);
    napi_value argv[1] = { jsonStr };
    napi_value ret;
    // We intentionally do not propagate exceptions — just log via NAPI
    // (silent-drop keeps the K/N side from blocking on uncaught throws).
    napi_call_function(env, undef, js_callback, 1, argv, &ret);
    if (json) free(json);
}

// Called by Kotlin on an arbitrary thread. Copies the JSON (since the K/N
// scope will free it right after return) and hands it to the threadsafe
// function; the callback then runs on the ArkTS main thread.
extern "C" void RpcCompletionTrampoline(void* ctx, const char* json) {
    RpcCallbackHolder* holder = (RpcCallbackHolder*)ctx;
    if (!holder) {
        return;
    }
    char* copy = nullptr;
    if (json) {
        // strdup is POSIX; if the platform SDK ships without it we can
        // fall back to malloc+memcpy here. OHOS NDK provides strdup.
        copy = strdup(json);
    }
    // Non-blocking enqueue; after the JS callback has run (or the tsfn
    // was aborted) RpcTsfnCallJS frees `copy`.
    napi_status status = napi_call_threadsafe_function(
        holder->tsfn, copy, napi_tsfn_nonblocking);
    if (status != napi_ok) {
        // Queue full or already closed — free the copy to avoid leaking.
        if (copy) free(copy);
    }
    // Release our acquire-on-create ref so the tsfn can be destroyed
    // once the JS side has drained the call queue.
    napi_release_threadsafe_function(holder->tsfn, napi_tsfn_release);
    free(holder);
}

// Arg layout: (instanceId: string, surfaceId: string, name: string,
//              argsJson: string, timeoutMs: number,
//              onComplete: (responseJson: string) => void)
// Returns the Kotlin-side requestId string, or "" on synchronous dispatch
// failure (unknown instance / stream still v0.9).
static napi_value A2UIInvokeAgentFunction(napi_env env, napi_callback_info info) {
    size_t argc = 6;
    napi_value argv[6];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 6) {
        napi_value empty;
        napi_create_string_utf8(env, "", 0, &empty);
        return empty;
    }
    char* instanceId = GetStringArg(env, argv[0]);
    char* surfaceId  = GetStringArg(env, argv[1]);
    char* name       = GetStringArg(env, argv[2]);
    char* argsJson   = GetStringArg(env, argv[3]);
    double timeoutD  = 0.0;
    napi_get_value_double(env, argv[4], &timeoutD);
    // argv[5] = JS callback function

    napi_value retVal;
    napi_create_string_utf8(env, "", 0, &retVal);

    if (!instanceId || !surfaceId || !name || !argsJson) {
        if (instanceId) free(instanceId);
        if (surfaceId)  free(surfaceId);
        if (name)       free(name);
        if (argsJson)   free(argsJson);
        return retVal;
    }

    // Build the threadsafe function. The resource name shows up in HiLog
    // on failures and is cheap to allocate.
    RpcCallbackHolder* holder =
        (RpcCallbackHolder*)malloc(sizeof(RpcCallbackHolder));
    if (!holder) {
        free(instanceId);
        free(surfaceId);
        free(name);
        free(argsJson);
        return retVal;
    }
    holder->tsfn = nullptr;

    napi_value asyncResName;
    napi_create_string_utf8(env, "a2uiInvokeAgentFunction",
                            NAPI_AUTO_LENGTH, &asyncResName);
    napi_status st = napi_create_threadsafe_function(
        env,
        argv[5],              // js_callback
        nullptr,              // async_resource
        asyncResName,         // async_resource_name
        0,                    // max_queue_size (unlimited)
        1,                    // initial_thread_count (us)
        nullptr,              // thread_finalize_data
        nullptr,              // thread_finalize_cb
        nullptr,              // context
        RpcTsfnCallJS,        // call_js_cb
        &holder->tsfn);
    if (st != napi_ok || holder->tsfn == nullptr) {
        free(holder);
        free(instanceId);
        free(surfaceId);
        free(name);
        free(argsJson);
        return retVal;
    }

    // Hand the holder pointer to Kotlin along with the trampoline fn.
    const char* reqId = libshared_symbols()->kotlin.root.a2uiInvokeAgentFunction(
        instanceId,
        surfaceId,
        name,
        argsJson,
        (long)timeoutD,
        (void*)holder,
        reinterpret_cast<void*>(&RpcCompletionTrampoline));

    if (reqId) {
        napi_create_string_utf8(env, reqId, NAPI_AUTO_LENGTH, &retVal);
        // K/N returns a borrowed pointer; no free() here (K/N owns the
        // string's lifetime — identical convention to other K/N return-
        // string functions in this codebase).
    } else {
        // Synchronous dispatch failure: Kotlin will not invoke the
        // trampoline. Tear down the holder immediately to avoid leaking
        // the tsfn ref.
        napi_release_threadsafe_function(holder->tsfn, napi_tsfn_abort);
        free(holder);
    }

    free(instanceId);
    free(surfaceId);
    free(name);
    free(argsJson);
    return retVal;
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
        {"a2uiRespondToRpc", nullptr, A2UIRespondToRpc, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiFailRpc", nullptr, A2UIFailRpc, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiInvokeAgentFunction", nullptr, A2UIInvokeAgentFunction, nullptr, nullptr, nullptr, napi_default, nullptr},
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
