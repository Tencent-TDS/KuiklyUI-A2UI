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
//  A2UIRenderHelper.h
//  OpenA2UIIOSRender
//
//  ObjC helper to call KuiklyRenderLayerHandler methods that Swift can't directly call
//  because KuiklyRenderLayerHandler is implemented in a .mm (ObjC++) file.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class KuiklyRenderLayerHandler;

NS_ASSUME_NONNULL_BEGIN

@interface A2UIRenderHelper : NSObject

+ (void)createRenderViewWithHandler:(KuiklyRenderLayerHandler *)handler
                                tag:(NSNumber *)tag
                           viewName:(NSString *)viewName;

+ (void)createShadowWithHandler:(KuiklyRenderLayerHandler *)handler
                            tag:(NSNumber *)tag
                       viewName:(NSString *)viewName;

+ (void)applyPropWithHandler:(KuiklyRenderLayerHandler *)handler
                         tag:(NSNumber *)tag
                         key:(NSString *)key
                       value:(id)value;

+ (void)applyShadowPropWithHandler:(KuiklyRenderLayerHandler *)handler
                               tag:(NSNumber *)tag
                               key:(NSString *)key
                             value:(id)value;

+ (void)insertSubViewWithHandler:(KuiklyRenderLayerHandler *)handler
                       parentTag:(NSNumber *)parentTag
                        childTag:(NSNumber *)childTag
                         atIndex:(NSInteger)index;

+ (void)applyFrameWithHandler:(KuiklyRenderLayerHandler *)handler
                          tag:(NSNumber *)tag
                        frame:(CGRect)frame;

+ (CGSize)calculateSizeWithHandler:(KuiklyRenderLayerHandler *)handler
                               tag:(NSNumber *)tag
                    constraintSize:(CGSize)constraintSize;

/// Get shadow object for a given tag (safe to call on context queue).
+ (id _Nullable)shadowWithHandler:(KuiklyRenderLayerHandler *)handler
                              tag:(NSNumber *)tag;

/// Get the pre-main-queue task from a shadow object (safe to call on context queue).
/// Returns nil if the shadow doesn't support this.
+ (dispatch_block_t _Nullable)shadowPreTask:(id)shadow;

/// Apply shadow to view (must be called on main thread).
+ (void)applyShadowWithHandler:(KuiklyRenderLayerHandler *)handler
                           tag:(NSNumber *)tag
                        shadow:(id)shadow;

+ (void)removeRenderViewWithHandler:(KuiklyRenderLayerHandler *)handler
                                tag:(NSNumber *)tag;

+ (void)removeShadowWithHandler:(KuiklyRenderLayerHandler *)handler
                            tag:(NSNumber *)tag;

/// Check if a view class supports shadow creation (implements hrv_createShadow)
+ (BOOL)viewSupportsShadow:(NSString *)viewName;

/// Perform a block on Kuikly's context queue (KuiklyRenderThreadManager)
+ (void)performOnContextQueue:(dispatch_block_t)block;

/// Perform a block on the main queue (via KuiklyRenderThreadManager)
+ (void)performOnMainQueue:(dispatch_block_t)block;

/// Check if current thread is the context queue
+ (BOOL)isContextQueue;

/// Call a method on a view
+ (void)callViewMethodWithHandler:(KuiklyRenderLayerHandler *)handler
                              tag:(NSNumber *)tag
                           method:(NSString *)method
                           params:(NSString * _Nullable)params;

/// Call a method on a shadow
+ (NSString * _Nullable)callShadowMethodWithHandler:(KuiklyRenderLayerHandler *)handler
                                                tag:(NSNumber *)tag
                                             method:(NSString *)method
                                             params:(NSString *)params;

// MARK: - Custom Render View Support

/// Register a custom view creator block for a given viewName.
/// When createRenderView is called with this viewName, the creator block
/// will be used instead of NSClassFromString.
/// @param viewName The view name as it appears in the A2UI protocol
/// @param creator A block that returns a new UIView conforming to KuiklyRenderViewExportProtocol
+ (void)registerCustomViewCreator:(UIView * _Nonnull (^)(void))creator
                      forViewName:(NSString *)viewName;

/// Unregister a previously registered custom view creator.
+ (void)unregisterCustomViewCreatorForViewName:(NSString *)viewName;

// MARK: - KRMemoryCacheModule Support

/// Get or create the shared KRMemoryCacheModule instance.
/// Used by A2UISurfaceView to support base64 image loading in KRImageView.
+ (id _Nonnull)sharedMemoryCacheModule;

/// Call a method on the shared KRMemoryCacheModule.
/// @param method The method name (e.g. "setObject")
/// @param params The JSON params string
+ (void)callMemoryCacheModuleMethod:(NSString *)method params:(NSString * _Nullable)params;

/// Get a value from the shared KRMemoryCacheModule by key.
/// @param key The cache key
/// @return The cached value, or nil if not found
+ (id _Nullable)memoryCacheModuleGetValueForKey:(NSString *)key;

@end

NS_ASSUME_NONNULL_END
