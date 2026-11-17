/*
 * A2UI ohosApp entry - NAPI type declarations for libkuikly_entry.so
 */
export const initKuikly: () => number;

// A2UI registry writers (Kuikly-page mode) — forward straight into the
// KMP-side global registries via api->kotlin.root.a2ui…() in napi_init.cpp.
export const a2uiRegisterCustomFunction: (name: string, spec: string) => void;
export const a2uiUnregisterCustomFunction: (name: string) => void;
export const a2uiRegisterCustomView: (viewName: string) => void;
export const a2uiUnregisterCustomView: (viewName: string) => void;
export const a2uiRegisterIcon: (name: string, uri: string) => void;
export const a2uiUnregisterIcon: (name: string) => void;

// A2UI DateTimeInput picker result writer (Kuikly-page mode).
export const a2uiNotifyPickerResult: (dslInstanceId: string, tag: number, isoString: string) => void;

// A2UI custom-view second-pass resize writer (Kuikly-page mode) — invoked
// from the ArkTS-side `notifyA2UICustomViewSizeChanged` helper when a
// custom component's intrinsic size changes asynchronously (e.g. after a
// WebView finishes loading its content).
export const a2uiNotifyCustomViewResize: (pagerId: string, nativeRef: number, width: number, height: number) => void;

// A2UI v1.0 bidirectional-RPC writers. The first two are pure parameter
// forwarders; `a2uiInvokeAgentFunction` sets up a napi_threadsafe_function
// behind the scenes so the renderer->agent callback hops onto the ArkTS
// main thread automatically. Returns the Kotlin-side requestId, or "" on
// synchronous dispatch failure (unknown instance / stream still v0.9).
export const a2uiRespondToRpc: (instanceId: string, surfaceId: string, requestId: string, resultJson: string) => void;
export const a2uiFailRpc: (instanceId: string, surfaceId: string, requestId: string, code: string, message: string, detailsJson: string) => void;
export const a2uiInvokeAgentFunction: (
    instanceId: string,
    surfaceId: string,
    name: string,
    argsJson: string,
    timeoutMs: number,
    onComplete: (responseJson: string) => void
) => string;

// Inbound `callRendererFunction` handler registration. Install a single
// JS callback per instanceId; the napi shim bridges it to the Kotlin
// dispatcher through a reverse napi_threadsafe_function. Calling
// `a2uiSetRendererFunctionHandler` again on the same instanceId replaces
// the existing handler (previous tsfn is aborted). Hosts MUST call
// `a2uiRemoveRendererFunctionHandler` from their teardown path so the
// shim can release the tsfn.
//
// The `requestJson` payload matches `A2UIBridgeManager.setRendererFunctionJsonHandler`:
//   { "surfaceId":"...", "requestId":"...", "name":"...",
//     "args":{...}, "timeoutMs":1234 }  // timeoutMs may be absent
export const a2uiSetRendererFunctionHandler: (
    instanceId: string,
    onCall: (requestJson: string) => void
) => void;
export const a2uiRemoveRendererFunctionHandler: (instanceId: string) => void;
