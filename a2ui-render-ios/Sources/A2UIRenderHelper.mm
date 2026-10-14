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

//
//  A2UIRenderHelper.mm
//  OpenA2UIIOSRender
//
//  ObjC++ helper to call KuiklyRenderLayerHandler methods that Swift can't directly call
//  because KuiklyRenderLayerHandler is implemented in a .mm (ObjC++) file.
//  Also wraps KuiklyRenderThreadManager calls for Swift access.
//

#import "A2UIRenderHelper.h"
#import "KuiklyRenderLayerHandler.h"
#import "KuiklyRenderThreadManager.h"
#import "KRMemoryCacheModule.h"
#import "NSObject+KR.h"
#import "KuiklyRenderModuleExportProtocol.h"

/// Shared KRMemoryCacheModule instance for all A2UISurfaceView instances.
/// This avoids duplicating base64 image cache per card and allows cross-card cache sharing.
static KRMemoryCacheModule *_sharedMemoryCacheModule = nil;

/// Global custom view creator registry.
/// Keyed by viewName → creator block.
static NSMutableDictionary<NSString *, UIView * _Nonnull (^)(void)> *_customViewCreators = nil;
static NSLock *_customViewCreatorsLock = nil;

@implementation A2UIRenderHelper

+ (void)initialize {
    if (self == [A2UIRenderHelper class]) {
        _customViewCreators = [NSMutableDictionary new];
        _customViewCreatorsLock = [[NSLock alloc] init];
    }
}

+ (void)createRenderViewWithHandler:(KuiklyRenderLayerHandler *)handler
                                tag:(NSNumber *)tag
                           viewName:(NSString *)viewName {
    [handler createRenderViewWithTag:tag viewName:viewName];
}

+ (void)createShadowWithHandler:(KuiklyRenderLayerHandler *)handler
                            tag:(NSNumber *)tag
                       viewName:(NSString *)viewName {
    [handler createShadowWithTag:tag viewName:viewName];
}

+ (void)applyPropWithHandler:(KuiklyRenderLayerHandler *)handler
                         tag:(NSNumber *)tag
                         key:(NSString *)key
                       value:(id)value {
    [handler setPropWithTag:tag propKey:key propValue:value];
}

+ (void)applyShadowPropWithHandler:(KuiklyRenderLayerHandler *)handler
                               tag:(NSNumber *)tag
                               key:(NSString *)key
                             value:(id)value {
    [handler setShadowPropWithTag:tag propKey:key propValue:value];
}

+ (void)insertSubViewWithHandler:(KuiklyRenderLayerHandler *)handler
                       parentTag:(NSNumber *)parentTag
                        childTag:(NSNumber *)childTag
                         atIndex:(NSInteger)index {
    [handler insertSubRenderViewWithParentTag:parentTag childTag:childTag atIndex:index];
}

+ (void)applyFrameWithHandler:(KuiklyRenderLayerHandler *)handler
                          tag:(NSNumber *)tag
                        frame:(CGRect)frame {
    [handler setRenderViewFrameWithTag:tag frame:frame];
}

+ (CGSize)calculateSizeWithHandler:(KuiklyRenderLayerHandler *)handler
                               tag:(NSNumber *)tag
                    constraintSize:(CGSize)constraintSize {
    return [handler calculateRenderViewSizeWithTag:tag constraintSize:constraintSize];
}

+ (id)shadowWithHandler:(KuiklyRenderLayerHandler *)handler
                    tag:(NSNumber *)tag {
    return [handler shadowWithTag:tag];
}

+ (dispatch_block_t)shadowPreTask:(id)shadow {
    if ([shadow respondsToSelector:@selector(hrv_taskToMainQueueWhenWillSetShadowToView)]) {
        return [shadow performSelector:@selector(hrv_taskToMainQueueWhenWillSetShadowToView)];
    }
    return nil;
}

+ (void)applyShadowWithHandler:(KuiklyRenderLayerHandler *)handler
                           tag:(NSNumber *)tag
                        shadow:(id)shadow {
    [handler setShadowWithTag:tag shadow:shadow];
}

+ (void)removeRenderViewWithHandler:(KuiklyRenderLayerHandler *)handler
                                tag:(NSNumber *)tag {
    [handler removeRenderViewWithTag:tag];
}

+ (void)removeShadowWithHandler:(KuiklyRenderLayerHandler *)handler
                            tag:(NSNumber *)tag {
    [handler removeShadowWithTag:tag];
}

#pragma mark - Custom Render View Support

+ (void)registerCustomViewCreator:(UIView * _Nonnull (^)(void))creator
                      forViewName:(NSString *)viewName {
    if (!viewName.length || !creator) return;
    [_customViewCreatorsLock lock];
    _customViewCreators[viewName] = [creator copy];
    [_customViewCreatorsLock unlock];
}

+ (void)unregisterCustomViewCreatorForViewName:(NSString *)viewName {
    if (!viewName.length) return;
    [_customViewCreatorsLock lock];
    [_customViewCreators removeObjectForKey:viewName];
    [_customViewCreatorsLock unlock];
}

+ (UIView * _Nullable)createCustomViewForName:(NSString *)viewName {
    [_customViewCreatorsLock lock];
    UIView * (^creator)(void) = _customViewCreators[viewName];
    [_customViewCreatorsLock unlock];
    if (creator) {
        return creator();
    }
    return nil;
}

#pragma mark - Shadow Support Check

+ (BOOL)viewSupportsShadow:(NSString *)viewName {
    Class cls = NSClassFromString(viewName)
                ?: NSClassFromString([viewName stringByReplacingCharactersInRange:NSMakeRange(0, 1) withString:@"K"]);
    if (!cls) {
        return NO;
    }
    return [cls respondsToSelector:@selector(hrv_createShadow)];
}

#pragma mark - Thread Management

+ (void)performOnContextQueue:(dispatch_block_t)block {
    [KuiklyRenderThreadManager performOnContextQueueWithBlock:block];
}

+ (void)performOnMainQueue:(dispatch_block_t)block {
    [KuiklyRenderThreadManager performOnMainQueueWithTask:block sync:NO];
}

+ (BOOL)isContextQueue {
    return [KuiklyRenderThreadManager isContextQueue];
}

+ (void)callViewMethodWithHandler:(KuiklyRenderLayerHandler *)handler
                              tag:(NSNumber *)tag
                           method:(NSString *)method
                           params:(NSString *)params {
    [handler callViewMethodWithTag:tag method:method params:params callback:nil];
}

+ (NSString *)callShadowMethodWithHandler:(KuiklyRenderLayerHandler *)handler
                                      tag:(NSNumber *)tag
                                   method:(NSString *)method
                                   params:(NSString *)params {
    return [handler callShadowMethodWithTag:tag method:method params:params];
}

#pragma mark - KRMemoryCacheModule Support

+ (id)sharedMemoryCacheModule {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _sharedMemoryCacheModule = [[KRMemoryCacheModule alloc] init];
    });
    return _sharedMemoryCacheModule;
}

+ (void)callMemoryCacheModuleMethod:(NSString *)method params:(NSString *)params {
    KRMemoryCacheModule *module = (KRMemoryCacheModule *)[self sharedMemoryCacheModule];
    // Use the standard hrv_callWithMethod interface which handles parameter wrapping
    // (wraps params into @{@"param": params} dict before calling the method selector)
    [module hrv_callWithMethod:method params:params callback:nil];
}

+ (id)memoryCacheModuleGetValueForKey:(NSString *)key {
    KRMemoryCacheModule *module = (KRMemoryCacheModule *)[self sharedMemoryCacheModule];
    return [module memoryObjectForKey:key];
}

@end
