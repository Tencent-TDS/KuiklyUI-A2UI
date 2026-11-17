
# A2UI v1.0 Bidirectional RPC — Host Integration Guide (PR-6b)

Status: **DRAFT — PR-6b**
Audience: integrators of
`a2ui-render-android`, `OpenKuiklyA2UIIOSRender`, `a2ui-render-ohos`
Scope: wire-protocol & bridge APIs only — business-level function
registration is still per-app and out of scope.

---

## 1. What changed in PR-6a / PR-6b

| PR | What landed |
|---|---|
| PR-6a | Kotlin core: `A2UIRpcDispatcher`, inbound parsing of 4 wire keys, outbound serialisation hooks, `BridgeManager` public API (typed). |
| PR-6b | New `methodId = 108` (`SEND_RPC_ENVELOPE`) on the Kotlin→native channel; JSON-flavored `BridgeManager` APIs for Java/ObjC/ArkTS hosts; iOS `A2UIRpcBridge` facade; Android `A2UIRpcBridge` facade; OHOS pre-dispatch inside `A2UIOhosPager`. |

No v0.9 semantics are affected.

---

## 2. Wire protocol (recap)

Four new top-level envelope keys on the v1.0 stream
(see `evolution_guide.md §Bidirectional function calls`):

* `callRendererFunction` — agent → renderer
* `rendererFunctionResponse` — renderer → agent (reply to the above)
* `callAgentFunction` — renderer → agent
* `agentFunctionResponse` — agent → renderer (reply to the above)

Common envelope shape:

```jsonc
{
  "version": "1.0",
  "callRendererFunction": {
    "surfaceId": "surface-1",
    "requestId": "req-abcdef",
    "name": "openPage",
    "args": { "path": "/profile" },
    "timeoutMs": 5000           // optional
  }
}
```

Response shape (exactly one of `result` / `error`):

```jsonc
{
  "version": "1.0",
  "rendererFunctionResponse": {
    "surfaceId": "surface-1",
    "requestId": "req-abcdef",
    "result": { "status": "ok" }
    // OR  "error": { "code": "UNKNOWN_FUNCTION", "message": "...", "details": {...} }
  }
}
```

Built-in error codes (see `A2UIRpcError.Codes`):

`UNKNOWN_FUNCTION`, `INVALID_ARGS`, `USER_CANCELLED`, `TIMEOUT`,
`INTERNAL_ERROR`, `WIRE_FORMAT_ERROR`, `SURFACE_CLOSED`.
Hosts MAY introduce extra codes using the `x-` prefix.

---

## 3. Inbound path (agent → renderer)

The agent's wire frames enter the renderer through the normal
`WRITE_STREAM` channel — **no new methodId is needed on this direction**.
The Kotlin core parses the envelope, drops it into
`A2UIRpcDispatcher.pending`, and calls the host-registered handler.

### 3.1 Android (Java / Kotlin)

```kotlin
A2UIRpcBridge.get().setHandler(instanceId) { envelopeJson ->
    // envelopeJson = {"surfaceId":"...","requestId":"...","name":"...","args":{...}}
    val req = JSONObject(envelopeJson)
    when (req.getString("name")) {
        "openPage" -> {
            router.push(req.getJSONObject("args").getString("path"))
            A2UIRpcBridge.get().respond(
                instanceId,
                req.getString("surfaceId"),
                req.getString("requestId"),
                /* resultJson = */ "{\"status\":\"ok\"}"
            )
        }
        else -> A2UIRpcBridge.get().fail(
            instanceId,
            req.getString("surfaceId"),
            req.getString("requestId"),
            code = "UNKNOWN_FUNCTION",
            message = "no such function"
        )
    }
}
```

### 3.2 iOS (Swift)

```swift
A2UIRpcBridge.shared.setHandler(instanceId: instanceId) { envelopeJson in
    guard let data = envelopeJson.data(using: .utf8),
          let obj = try? JSONSerialization.jsonObject(with: data) as? [String: Any]
    else { return }
    let surfaceId = obj["surfaceId"] as? String ?? ""
    let requestId = obj["requestId"] as? String ?? ""
    let name = obj["name"] as? String ?? ""
    switch name {
    case "openPage":
        router.push(/* ... */)
        A2UIRpcBridge.shared.respond(
            instanceId: instanceId,
            surfaceId: surfaceId,
            requestId: requestId,
            resultJson: #"{"status":"ok"}"#
        )
    default:
        A2UIRpcBridge.shared.fail(
            instanceId: instanceId,
            surfaceId: surfaceId,
            requestId: requestId,
            code: "UNKNOWN_FUNCTION",
            message: "no such function",
            detailsJson: ""
        )
    }
}
```

### 3.3 HarmonyOS (ArkTS)

Register `A2UIBridgeModule` with a method the Kotlin core expects:

```ts
// A2UIBridgeModule.ets
rpcEnvelope(envelopeJson: string): string | null {
    // outbound (Kotlin → native); see §4
}
```

Inbound (agent → renderer) arrives through the stream `WRITE_STREAM` path
exactly like v0.9; ArkTS does not see it directly. To expose it to the
ArkTS layer, wrap `BridgeManager.setRendererFunctionJsonHandler` in a
small NAPI shim and forward the JSON string up.

---

## 4. Outbound path (Kotlin → native)

The Kotlin core emits outbound RPC envelopes through
**methodId `108` (`SEND_RPC_ENVELOPE`)**:

```
SEND_RPC_ENVELOPE(instanceId, envelopeJson, null, null, null, null)
```

* `instanceId` — the surface's instanceId.
* `envelopeJson` — a complete JSON object carrying either
  `rendererFunctionResponse` or `callAgentFunction`
  (with `"version": "1.0"` at the top level).

### 4.1 Android — intercept in `A2UISurfaceView.callNative(...)`

```kotlin
override fun callNative(methodId: Int, arg0: Any?, arg1: Any?, /* ... */): Any? {
    if (methodId == A2UINativeMethod.SEND_RPC_ENVELOPE) {
        val envelopeJson = arg1 as String
        agentTransport.send(envelopeJson)   // WebSocket / SSE / ...
        return null
    }
    // fall through to the existing switch
}
```

### 4.2 iOS — intercept in `A2UISurfaceView.callNative(...)`

```swift
@objc(callNativeMethodId:arg0:arg1:arg2:arg3:arg4:arg5:)
func callNative(methodId: Int32, arg0: Any?, arg1: Any?, /* ... */) -> Any? {
    if methodId == 108 /* SEND_RPC_ENVELOPE */ {
        if let envelope = arg1 as? String {
            agentTransport.send(envelope)
        }
        return nil
    }
    // fall through to the existing switch
}
```

### 4.3 HarmonyOS — already wired

`A2UIOhosPager.callNativeImpl` in this repo pre-dispatches
`SEND_RPC_ENVELOPE` to `A2UIBridgeModule.rpcEnvelope(envelopeJson)`.
Your ArkTS `A2UIBridgeModule` just needs the method:

```ts
public rpcEnvelope(envelopeJson: string): string | null {
    this.agentTransport.send(envelopeJson)
    return null
}
```

---

## 5. Kuikly-embedded mode (fallback path)

When there is no per-surface `NativeBridge` (i.e. A2UI is hosted inside
a Kuikly page), the core dispatches outbound traffic through the
standard Module channel:

```
A2UIBridgeModule.rpcEnvelope(envelopeJson)     // syncCall = 0
```

Three platforms implement this module today for other A2UI methods
(`showDateTimePicker`, `executeFunctionCall`, `invokeCustomFunction`);
`rpcEnvelope` follows the same pattern and must be added by each host.

---

## 6. Renderer-initiated RPCs (renderer → agent)

Call from the host side when you need the agent to run something
(e.g. fetch data, perform a backend action):

```kotlin
val reqId = A2UIRpcBridge.get().invoke(
    instanceId, surfaceId, "fetchUser",
    argsJson = "{\"id\":42}",
    timeoutMs = 5_000L,
    onComplete = { responseJson ->
        // responseJson = {"surfaceId":"...","requestId":"...","result":...}
        //             or {"surfaceId":"...","requestId":"...","error":{...}}
    }
)
```

`onComplete` fires **exactly once** — on agent reply, timeout, or
surface close.

---

## 7. Lifecycle & threading

* All APIs are expected to be called on the **Kuikly context thread**
  (same thread as `A2UICoreEntry.callKotlinMethod`). Hosts on other
  threads must hop first.
* `respond` / `fail` for a given `requestId` is **idempotent-ish**:
  late / duplicate calls after the dispatcher has already removed the
  pending (e.g. due to timeout) are dropped with a warn log.
* Surface destruction (`A2UIBridgeManager.destroyInstance`) calls
  `A2UIRpcDispatcher.cancelAll(null)`, which fires any pending
  renderer-initiated `onComplete` with `SURFACE_CLOSED`.

---

## 8. Checklist for a host SDK upgrade

- [ ] Recognize `methodId = 108` in `A2UISurfaceView.callNative(...)`.
- [ ] (Optional, embedded mode) Add `rpcEnvelope(envelopeJson)` to
      `A2UIBridgeModule`.
- [ ] Register at least one agent transport binding
      (`A2UIRpcBridge.setHandler` or the Java/ObjC facade).
- [ ] Forward the raw envelope string to your agent channel
      (WebSocket / HTTP / SSE) without additional transformation.
- [ ] Deliver agent-side replies back into the renderer by appending
      them as regular stream chunks via `WRITE_STREAM`.

