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
