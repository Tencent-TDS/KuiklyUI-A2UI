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

#import "A2UIBridgeModule.h"
#import <objc/runtime.h>

/*
 * See the KDoc on `A2UIBridgeModule.h` and KMP-side
 * `A2UIBridgeManager.dispatchToA2UIBridgeModule` for the overall contract.
 *
 * This file ONLY handles the Kuikly-page-embedded dispatch; the Native-page
 * `A2UISurfaceView.callNative(…)` continues to handle methodId=103/106/107
 * directly on its own.
 */
@implementation A2UIBridgeModule

/*
 * Prevent dead-stripping even when the host app forgets `-ObjC` /
 * `-force_load OpenA2UIIOSRender`. `+load` is called by the ObjC runtime
 * at image load time if-and-only-if the class is linked into the image,
 * which with CocoaPods static libs always happens as long as *any* symbol
 * in this translation unit is referenced — and the compiler cannot strip
 * a class whose `+load` is referenced by the runtime itself.
 *
 * We additionally bump `objc_getClass("A2UIBridgeModule")` through the
 * KMP-side `isA2UIBridgeModuleAvailable()` pre-check at
 * `A2UIBridgeManager.dispatchToA2UIBridgeModule` time, so even if `+load`
 * never fires for some unusual packaging reason, the KMP side degrades
 * to a no-op instead of trapping on `NSAssert` inside
 * `KuiklyRenderLayerHandler`.
 */
+ (void)load {
    // Reference the class once so the symbol table keeps it.
    (void)[A2UIBridgeModule class];
}

#pragma mark - KuiklyRenderModuleExportProtocol

/*
 * Dispatcher — selected by `method` name. Payload shapes mirror the
 * Android and OHOS counterparts byte-for-byte (see
 * `a2ui-render-android/.../A2UIBridgeModule.kt` and
 * `a2ui-render-ohos/.../A2UISurfaceView.ets:A2UIBridgeModule`).
 *
 * Threading: `callModuleMethod` dispatches us on the Kuikly Context Queue.
 * We stay on it for synchronous payloads; UIKit work (date-picker, Safari)
 * is hopped to `dispatch_get_main_queue()` by the implementations below.
 */
- (nullable NSString *)hrv_callWithMethod:(nonnull NSString *)method
                                   params:(nullable NSString *)params
                                 callback:(nullable KuiklyRenderCallback)callback {
    if ([method isEqualToString:@"showDateTimePicker"]) {
        [self p_showDateTimePickerWithConfigJson:params];
        return nil;
    }
    if ([method isEqualToString:@"executeFunctionCall"]) {
        [self p_executeFunctionCallWithPayload:params];
        return nil;
    }
    if ([method isEqualToString:@"invokeCustomFunction"]) {
        return [self p_invokeCustomFunctionWithPayload:params];
    }
    if ([method isEqualToString:@"contentHeightChanged"]) {
        // No-op on iOS: the host Kuikly page already drives
        // A2UISurfaceDSLView's intrinsic height through its Flex engine, and
        // standalone surfaces handle CONTENT_HEIGHT_CHANGED (methodId=102)
        // directly in their own callNative switch. Accepted here so the KMP
        // caller doesn't need to branch per platform.
        return nil;
    }
    if ([method isEqualToString:@"actionCallback"]) {
        // No-op on iOS: Kuikly-page-embedded surfaces forward action
        // callbacks via A2UISurfaceDSLView's own `wireBridgeCallbacks`
        // wiring. Accepted here so the KMP caller doesn't need to branch
        // per platform.
        return nil;
    }
    if ([method isEqualToString:@"debugLog"]) {
        if ([params isKindOfClass:[NSString class]]) {
            NSLog(@"[A2UIBridgeModule] %@", params);
        }
        return nil;
    }
    NSLog(@"[A2UIBridgeModule] warn: unknown method '%@'", method);
    return nil;
}

#pragma mark - Date/Time picker

- (void)p_showDateTimePickerWithConfigJson:(nullable NSString *)configJson {
    if (![configJson isKindOfClass:[NSString class]]) { return; }
    // Resolve the Swift-side helper by name so this ObjC file does not need
    // to import the generated Swift bridging header. The helper lives in
    // the same pod (`A2UIBridgeModuleHelper`) and is `@objc`-annotated with
    // a stable selector shape: `+showPickerWithConfigJson:`.
    Class helperCls = objc_lookUpClass("A2UIBridgeModuleHelper");
    if (helperCls == Nil) {
        NSLog(@"[A2UIBridgeModule] warn: A2UIBridgeModuleHelper not linked, skipping showDateTimePicker");
        return;
    }
    SEL sel = NSSelectorFromString(@"showPickerWithConfigJson:");
    if (![helperCls respondsToSelector:sel]) { return; }
    // Hop to main queue — UIKit presentation must happen there.
    dispatch_async(dispatch_get_main_queue(), ^{
        IMP imp = [helperCls methodForSelector:sel];
        void (*fn)(id, SEL, NSString *) = (void (*)(id, SEL, NSString *))imp;
        fn(helperCls, sel, configJson);
    });
}

#pragma mark - executeFunctionCall

- (void)p_executeFunctionCallWithPayload:(nullable NSString *)payload {
    if (![payload isKindOfClass:[NSString class]]) { return; }
    NSRange sep = [payload rangeOfString:@"\n"];
    if (sep.location == NSNotFound) {
        NSLog(@"[A2UIBridgeModule] warn: executeFunctionCall payload missing '\\n'");
        return;
    }
    NSString *name = [payload substringToIndex:sep.location];
    NSString *argsJson = [payload substringFromIndex:(sep.location + 1)];
    Class helperCls = objc_lookUpClass("A2UIBridgeModuleHelper");
    if (helperCls == Nil) { return; }
    SEL sel = NSSelectorFromString(@"executeFunctionCallWithName:argsJson:");
    if (![helperCls respondsToSelector:sel]) { return; }
    // Keep execution on the main queue — the built-in `openUrl` must
    // touch `UIApplication.shared.open` from the main thread.
    dispatch_async(dispatch_get_main_queue(), ^{
        IMP imp = [helperCls methodForSelector:sel];
        void (*fn)(id, SEL, NSString *, NSString *) = (void (*)(id, SEL, NSString *, NSString *))imp;
        fn(helperCls, sel, name, argsJson);
    });
}

#pragma mark - invokeCustomFunction

/*
 * Synchronous dispatch — returns the raw platform value as a JSON-encoded
 * string so the Kuikly Module bus can carry it through unchanged. KMP side
 * `A2UIBridgeManager.dispatchToA2UIBridgeModule` passes syncCall=1 for
 * expression-time invocations; the caller unwraps the JSON back into a
 * String/Boolean/Number/Array on the Kotlin side via its coercer.
 */
- (nullable NSString *)p_invokeCustomFunctionWithPayload:(nullable NSString *)payload {
    if (![payload isKindOfClass:[NSString class]]) { return nil; }
    NSRange first = [payload rangeOfString:@"\n"];
    if (first.location == NSNotFound) {
        NSLog(@"[A2UIBridgeModule] warn: invokeCustomFunction payload missing '\\n' (first)");
        return nil;
    }
    NSRange secondSearch = NSMakeRange(first.location + 1, payload.length - first.location - 1);
    NSRange second = [payload rangeOfString:@"\n" options:0 range:secondSearch];
    if (second.location == NSNotFound) {
        NSLog(@"[A2UIBridgeModule] warn: invokeCustomFunction payload missing '\\n' (second)");
        return nil;
    }
    NSString *name = [payload substringToIndex:first.location];
    NSString *spec = [payload substringWithRange:NSMakeRange(first.location + 1, second.location - first.location - 1)];
    NSString *argsJson = [payload substringFromIndex:(second.location + 1)];

    Class helperCls = objc_lookUpClass("A2UIBridgeModuleHelper");
    if (helperCls == Nil) { return nil; }
    SEL sel = NSSelectorFromString(@"invokeCustomFunctionWithName:argsJson:spec:");
    if (![helperCls respondsToSelector:sel]) { return nil; }
    IMP imp = [helperCls methodForSelector:sel];
    NSString *(*fn)(id, SEL, NSString *, NSString *, NSString *) =
        (NSString *(*)(id, SEL, NSString *, NSString *, NSString *))imp;
    return fn(helperCls, sel, name, argsJson, spec);
}

@end
