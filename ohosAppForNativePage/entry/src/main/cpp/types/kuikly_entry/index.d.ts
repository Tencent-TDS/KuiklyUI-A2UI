/*
 * A2UI ohosApp entry - NAPI type declarations for libkuikly_entry.so
 */
export const initKuikly: () => number;

// A2UI v1.0 bidirectional-RPC writers (native-page mode).
//
// Mirror the identically-named exports in `ohosAppForKuiklyPage` —
// `a2uiRespondToRpc` and `a2uiFailRpc` are pure parameter forwarders,
// while `a2uiInvokeAgentFunction` sets up a `napi_threadsafe_function`
// behind the scenes so the renderer→agent completion callback hops onto
// the ArkTS main thread automatically. Returns the Kotlin-side
// requestId, or "" on synchronous dispatch failure (unknown instance,
// or stream still in v0.9 mode).
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
