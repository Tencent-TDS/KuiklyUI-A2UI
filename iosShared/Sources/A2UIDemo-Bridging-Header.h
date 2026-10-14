//
//  A2UIDemo-Bridging-Header.h
//  A2UIDemoApp
//
//  Bridging header to expose Kuikly iOS Render ObjC APIs to Swift.
//

#ifndef A2UIDemo_Bridging_Header_h
#define A2UIDemo_Bridging_Header_h

#import "KuiklyRenderLayerProtocol.h"
#import "KuiklyRenderLayerHandler.h"
#import "KuiklyContextParam.h"
#import "KuiklyRenderThreadManager.h"
#import "KuiklyRenderBridge.h"
#import "A2UIRenderHelper.h"

// Kuikly-Page hosting APIs used by iosAppForKuiklyPage / iosAppForBothPages
// to load @Page-annotated Pager classes from the :demo KMP module via
// KuiklyRenderViewControllerBaseDelegator.
#import "KuiklyRenderViewControllerBaseDelegator.h"

#endif /* A2UIDemo_Bridging_Header_h */
