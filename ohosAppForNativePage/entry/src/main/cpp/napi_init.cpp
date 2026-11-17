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
 * A2UI ohosApp entry C++ layer (native-page mode).
 *
 * This is a minimal napi_init.cpp that:
 * 1. Loads the Kotlin/Native libshared.so (containing A2UI's initKuikly function)
 * 2. Calls initKuikly() to register the "a2ui" page route and callKotlin callback
 * 3. Exports the initKuikly NAPI function for ArkTS to call
 * 4. Exports the A2UI v1.0 RPC NAPI writers so ArkTS hosts can drive the
 *    renderer→agent direction from native-page containers (see
 *    `A2UIRpcDemoPage.ets`). Mirrors the identically-named exports in
 *    `ohosAppForKuiklyPage/.../napi_init.cpp`.
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

#include <cstdlib>  // malloc / free used by the RPC writers below
#include <cstring>  // strdup / memcpy for the RPC callback bridge below
#include <map>      // inbound-handler registry: instanceId → holder*
#include <mutex>    // guards the inbound-handler registry
#include <string>   // map key type

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

// ------------------------------------------------------------
// Helpers
// ------------------------------------------------------------

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
//
// This file is intentionally kept byte-for-byte identical to the RPC
// section of `ohosAppForKuiklyPage/.../napi_init.cpp` so both native-page
// and Kuikly-page hosts expose the same ArkTS-visible API shape.
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

// ─────────────────────────────────────────────────────────────
// Inbound RPC: ArkTS-registered callRendererFunction handler.
//
// Mirror image of the outbound path above. Lifecycle:
//
//   1. ArkTS calls a2uiSetRendererFunctionHandler(instanceId, jsCallback).
//   2. We allocate `RpcInboundHandlerHolder` and create a tsfn from the
//      JS callback. Holder is stored in `g_inboundHandlers[instanceId]`.
//      Any pre-existing holder on the same instanceId is torn down first
//      (release+abort tsfn, delete holder, detach Kotlin-side).
//   3. We call Kotlin's `a2uiSetRendererFunctionHandler(instanceId,
//      holder, &RpcInboundTrampoline)` to install the C-side forwarder.
//   4. Each time the Kotlin dispatcher receives a `callRendererFunction`
//      for this instance, it invokes RpcInboundTrampoline on its own
//      thread. The trampoline strdup's the JSON and
//      napi_call_threadsafe_function's it onto the ArkTS main thread.
//      Unlike the outbound completion we DO NOT release the tsfn here —
//      the handler may fire many times.
//   5. On cleanup ArkTS calls a2uiRemoveRendererFunctionHandler(instanceId).
//      We detach Kotlin-side first (so no new calls arrive), then
//      release+abort the tsfn and free the holder.
//
// Thread-safety: the registry map is guarded by g_inboundMutex; the
// tsfn itself is thread-safe by construction, so no additional
// synchronisation is needed around calls into it.
// ─────────────────────────────────────────────────────────────

typedef struct {
    napi_threadsafe_function tsfn;  // JS-callback handle
} RpcInboundHandlerHolder;

static std::map<std::string, RpcInboundHandlerHolder*> g_inboundHandlers;
static std::mutex g_inboundMutex;

// Called by napi_call_threadsafe_function on the ArkTS main thread.
// `data` is a strdup'd requestJson buffer; `js_callback` is the original
// jsCallback registered by ArkTS.
static void RpcInboundTsfnCallJS(napi_env env, napi_value js_callback,
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
    napi_call_function(env, undef, js_callback, 1, argv, &ret);
    if (json) free(json);
}

// Called by Kotlin on an arbitrary thread for every inbound
// `callRendererFunction`. Copies the JSON (K/N frees it after return)
// and enqueues on the tsfn so the JS callback runs on ArkTS main.
//
// NOTE: unlike RpcCompletionTrampoline (outbound, one-shot) we do NOT
// release the tsfn here — the handler can fire multiple times. The
// tsfn is released from A2UIRemoveRendererFunctionHandler.
extern "C" void RpcInboundTrampoline(void* ctx, const char* json) {
    RpcInboundHandlerHolder* holder = (RpcInboundHandlerHolder*)ctx;
    if (!holder) {
        return;
    }
    char* copy = nullptr;
    if (json) {
        copy = strdup(json);
    }
    napi_status status = napi_call_threadsafe_function(
        holder->tsfn, copy, napi_tsfn_nonblocking);
    if (status != napi_ok) {
        // Queue full or already closed — free the copy to avoid leaking.
        if (copy) free(copy);
    }
}

// Internal helper: pop the current holder for `instanceId`, detach
// Kotlin-side, and tear it down. Returns true if a holder was removed.
// MUST NOT be called while holding g_inboundMutex (we lock internally).
static bool RemoveInboundHandlerLocked(const char* instanceId) {
    RpcInboundHandlerHolder* holder = nullptr;
    {
        std::lock_guard<std::mutex> lk(g_inboundMutex);
        auto it = g_inboundHandlers.find(std::string(instanceId));
        if (it == g_inboundHandlers.end()) {
            return false;
        }
        holder = it->second;
        g_inboundHandlers.erase(it);
    }
    // Detach Kotlin-side first so no new trampoline calls arrive.
    libshared_symbols()->kotlin.root.a2uiRemoveRendererFunctionHandler(
        const_cast<char*>(instanceId));
    if (holder) {
        if (holder->tsfn) {
            napi_release_threadsafe_function(holder->tsfn, napi_tsfn_abort);
        }
        delete holder;
    }
    return true;
}

// Arg layout: (instanceId: string, jsCallback: (requestJson: string) => void)
static napi_value A2UISetRendererFunctionHandler(napi_env env, napi_callback_info info) {
    size_t argc = 2;
    napi_value argv[2];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 2) {
        return nullptr;
    }
    char* instanceId = GetStringArg(env, argv[0]);
    if (!instanceId) {
        return nullptr;
    }
    // Tear down any previous registration on the same instanceId so the
    // caller can idempotently re-install (e.g. after a hot-reload). The
    // Kotlin side's setRendererFunctionJsonHandler is itself idempotent
    // but we still need to release the OLD tsfn here.
    (void)RemoveInboundHandlerLocked(instanceId);

    RpcInboundHandlerHolder* holder = new RpcInboundHandlerHolder();
    holder->tsfn = nullptr;

    napi_value asyncResName;
    napi_create_string_utf8(env, "a2uiSetRendererFunctionHandler",
                            NAPI_AUTO_LENGTH, &asyncResName);
    napi_status st = napi_create_threadsafe_function(
        env,
        argv[1],                   // js_callback
        nullptr,                   // async_resource
        asyncResName,              // async_resource_name
        0,                         // max_queue_size (unlimited)
        1,                         // initial_thread_count
        nullptr, nullptr, nullptr, // finalize/context (unused)
        RpcInboundTsfnCallJS,      // call_js_cb
        &holder->tsfn);
    if (st != napi_ok || holder->tsfn == nullptr) {
        delete holder;
        free(instanceId);
        return nullptr;
    }
    {
        std::lock_guard<std::mutex> lk(g_inboundMutex);
        g_inboundHandlers[std::string(instanceId)] = holder;
    }
    // Install on the Kotlin side AFTER the map insertion so that an
    // inbound call arriving on another thread the instant Kotlin returns
    // can look up `holder` by its own pointer (we pass holder directly
    // as ctx, so this is actually safe either order — but keeping the
    // map in sync first means A2UIRemoveRendererFunctionHandler sees a
    // consistent world even under heavy concurrency).
    libshared_symbols()->kotlin.root.a2uiSetRendererFunctionHandler(
        instanceId, (void*)holder, reinterpret_cast<void*>(&RpcInboundTrampoline));

    free(instanceId);
    return nullptr;
}

// Arg layout: (instanceId: string)
static napi_value A2UIRemoveRendererFunctionHandler(napi_env env, napi_callback_info info) {
    size_t argc = 1;
    napi_value argv[1];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);
    if (argc < 1) {
        return nullptr;
    }
    char* instanceId = GetStringArg(env, argv[0]);
    if (!instanceId) {
        return nullptr;
    }
    (void)RemoveInboundHandlerLocked(instanceId);
    free(instanceId);
    return nullptr;
}

EXTERN_C_START
static napi_value Init(napi_env env, napi_value exports) {
    napi_property_descriptor desc[] = {
        {"initKuikly", nullptr, InitKuikly, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiRespondToRpc", nullptr, A2UIRespondToRpc, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiFailRpc", nullptr, A2UIFailRpc, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiInvokeAgentFunction", nullptr, A2UIInvokeAgentFunction, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiSetRendererFunctionHandler", nullptr, A2UISetRendererFunctionHandler, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"a2uiRemoveRendererFunctionHandler", nullptr, A2UIRemoveRendererFunctionHandler, nullptr, nullptr, nullptr, napi_default, nullptr},
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
