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

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "KuiklyRenderBridge.h"

/**
 * @brief A2UI Demo App component expand handler.
 * Provides basic image loading using NSURLSession (no third-party dependency).
 */
@interface A2UIComponentExpandHandler : NSObject <KuiklyRenderComponentExpandProtocol>
@end

@implementation A2UIComponentExpandHandler

+ (void)load {
    [KuiklyRenderBridge registerComponentExpandHandler:[self new]];
}

- (BOOL)hr_setImageWithUrl:(NSString *)url forImageView:(UIImageView *)imageView {
    if (!url || url.length == 0) {
        imageView.image = nil;
        return YES;
    }

    // Handle the `assets://` scheme used by A2UIIconRegistry and any other
    // caller that references image files bundled with the app. Values look
    // like `assets://a2ui_icons/home.png`. We map them to Bundle.main by
    // treating everything after the scheme as the resource relative path.
    static NSString *const kAssetsPrefix = @"assets://";
    if ([url hasPrefix:kAssetsPrefix]) {
        NSString *relativePath = [url substringFromIndex:kAssetsPrefix.length];
        // The A2UI Icon registry stores paths like `a2ui_icons/home.png`.
        // Xcode `folder` references (blue folders) preserve the sub-path,
        // so we must ask NSBundle for the file inside its containing folder.
        NSString *resource = relativePath.stringByDeletingPathExtension;
        NSString *ext = relativePath.pathExtension;
        NSString *path = [[NSBundle mainBundle] pathForResource:resource ofType:ext];
        UIImage *image = path ? [UIImage imageWithContentsOfFile:path] : nil;
        if (!image) {
            // Fallback: try the flat filename in case the folder reference was
            // added as a group instead of a folder.
            NSString *flatResource = relativePath.lastPathComponent.stringByDeletingPathExtension;
            NSString *flatPath = [[NSBundle mainBundle] pathForResource:flatResource ofType:ext];
            image = flatPath ? [UIImage imageWithContentsOfFile:flatPath] : nil;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            imageView.image = image;
        });
        return YES;
    }

    NSURL *imageURL = [NSURL URLWithString:url];
    if (!imageURL) {
        imageView.image = nil;
        return YES;
    }

    NSURLSessionDataTask *task = [[NSURLSession sharedSession] dataTaskWithURL:imageURL
                                                             completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (data && !error) {
            UIImage *image = [UIImage imageWithData:data];
            if (image) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    imageView.image = image;
                });
            }
        }
    }];
    [task resume];
    return YES;
}

@end
