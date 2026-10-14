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
//  A2UIVideoViewHandler.m
//  A2UIDemoApp
//
//  Backs the A2UI v0.9 "Video" component (KRVideoView) on iOS.
//  KRVideoView (core-render-ios) delegates actual playback to a host-registered
//  VideoViewCreator. This handler implements KRVideoViewProtocol with the system
//  AVPlayer (AVFoundation), so no third-party player dependency (e.g. VLCKit) is
//  required. Registration happens automatically via +load, mirroring how
//  A2UIComponentExpandHandler registers the image loader.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "KRVideoView.h"

@interface A2UIVideoViewHandler : UIView <KRVideoViewProtocol>

@property (nonatomic, copy) NSString *krv_source;
@property (nonatomic, strong) AVPlayer *player;
@property (nonatomic, strong) AVPlayerItem *playerItem;
@property (nonatomic, strong) id timeObserver;
@property (nonatomic, assign) BOOL firstFrameReported;

@end

@implementation A2UIVideoViewHandler

@synthesize krv_delegate;

+ (void)load {
    [KRVideoView registerVideoViewCreator:^id<KRVideoViewProtocol> _Nonnull(NSString * _Nonnull src, CGRect frame) {
        return [[A2UIVideoViewHandler alloc] initWithFrame:frame source:src];
    }];
}

+ (Class)layerClass {
    return [AVPlayerLayer class];
}

- (instancetype)initWithFrame:(CGRect)frame source:(NSString *)source {
    self = [super initWithFrame:frame];
    if (self) {
        _krv_source = [source copy];
        self.backgroundColor = [UIColor blackColor];
        [self p_setupPlayerIfNeeded];
    }
    return self;
}

- (void)dealloc {
    [self p_teardown];
}

#pragma mark - Private

- (AVPlayerLayer *)playerLayer {
    return (AVPlayerLayer *)self.layer;
}

- (void)p_setupPlayerIfNeeded {
    if (_player || _krv_source.length == 0) {
        return;
    }
    NSURL *url = [NSURL URLWithString:_krv_source];
    if (url == nil) {
        return;
    }

    _playerItem = [AVPlayerItem playerItemWithURL:url];
    _player = [AVPlayer playerWithPlayerItem:_playerItem];
    self.playerLayer.player = _player;
    self.playerLayer.videoGravity = AVLayerVideoGravityResizeAspect;

    // Observe item status for ready/failed transitions.
    [_playerItem addObserver:self
                  forKeyPath:@"status"
                     options:NSKeyValueObservingOptionNew
                     context:nil];
    // Observe play/pause transitions (iOS 10+).
    [_player addObserver:self
              forKeyPath:@"timeControlStatus"
                 options:NSKeyValueObservingOptionNew
                 context:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(p_playerDidReachEnd:)
                                                 name:AVPlayerItemDidPlayToEndTimeNotification
                                               object:_playerItem];

    __weak typeof(self) weakSelf = self;
    _timeObserver = [_player addPeriodicTimeObserverForInterval:CMTimeMakeWithSeconds(0.5, NSEC_PER_SEC)
                                                          queue:dispatch_get_main_queue()
                                                     usingBlock:^(CMTime time) {
        [weakSelf p_handleTimeChange:time];
    }];
}

- (void)p_teardown {
    if (_timeObserver && _player) {
        [_player removeTimeObserver:_timeObserver];
        _timeObserver = nil;
    }
    @try {
        [_playerItem removeObserver:self forKeyPath:@"status"];
        [_player removeObserver:self forKeyPath:@"timeControlStatus"];
    } @catch (__unused NSException *exception) {
        // Observers may not have been added if setup failed; ignore.
    }
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [_player pause];
    _player = nil;
    _playerItem = nil;
}

- (void)p_handleTimeChange:(CMTime)time {
    NSUInteger currentMs = (NSUInteger)(CMTimeGetSeconds(time) * 1000.0);
    NSUInteger totalMs = 0;
    if (_playerItem && CMTIME_IS_NUMERIC(_playerItem.duration)) {
        Float64 dur = CMTimeGetSeconds(_playerItem.duration);
        if (!isnan(dur) && dur > 0) {
            totalMs = (NSUInteger)(dur * 1000.0);
        }
    }
    if ([self.krv_delegate respondsToSelector:@selector(playTimeDidChangedWithCurrentTime:totalTime:)]) {
        [self.krv_delegate playTimeDidChangedWithCurrentTime:currentMs totalTime:totalMs];
    }
    if (!_firstFrameReported && currentMs > 0) {
        _firstFrameReported = YES;
        if ([self.krv_delegate respondsToSelector:@selector(videoFirstFrameDidDisplay)]) {
            [self.krv_delegate videoFirstFrameDidDisplay];
        }
    }
}

- (void)p_playerDidReachEnd:(NSNotification *)notification {
    [self p_notifyState:KRVideoPlayStatePlayEnd];
}

- (void)p_notifyState:(KRVideoPlayState)state {
    if ([self.krv_delegate respondsToSelector:@selector(videoPlayStateDidChangedWithState:extInfo:)]) {
        [self.krv_delegate videoPlayStateDidChangedWithState:state extInfo:@{}];
    }
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary<NSKeyValueChangeKey,id> *)change
                       context:(void *)context {
    if ([keyPath isEqualToString:@"status"] && object == _playerItem) {
        if (_playerItem.status == AVPlayerItemStatusFailed) {
            [self p_notifyState:KRVideoPlayStateFaild];
        } else if (_playerItem.status == AVPlayerItemStatusReadyToPlay) {
            // The A2UI standard Video component only carries a url (no playControl),
            // so auto-play once the item is ready. Mirrors the Android adapter's
            // playWhenReady behavior so the test cases visibly play.
            [_player play];
        }
    } else if ([keyPath isEqualToString:@"timeControlStatus"] && object == _player) {
        switch (_player.timeControlStatus) {
            case AVPlayerTimeControlStatusPlaying:
                [self p_notifyState:KRVideoPlayStatePlaying];
                break;
            case AVPlayerTimeControlStatusPaused:
                [self p_notifyState:KRVideoPlayStatePaused];
                break;
            case AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate:
                [self p_notifyState:KRVideoPlayStateCaching];
                break;
            default:
                break;
        }
    }
}

#pragma mark - KRVideoViewProtocol

- (void)krv_preplay {
    [self p_setupPlayerIfNeeded];
}

- (void)krv_play {
    [self p_setupPlayerIfNeeded];
    [_player play];
}

- (void)krv_pause {
    [_player pause];
}

- (void)krv_stop {
    [_player pause];
    [_player seekToTime:kCMTimeZero];
}

- (void)krv_setVideoContentMode:(KRVideoViewContentMode)videoViewContentMode {
    switch (videoViewContentMode) {
        case KRVideoViewContentModeScaleAspectFill:
            self.playerLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;
            break;
        case KRVideoViewContentModeScaleToFill:
            self.playerLayer.videoGravity = AVLayerVideoGravityResize;
            break;
        case KRVideoViewContentModeScaleAspectFit:
        default:
            self.playerLayer.videoGravity = AVLayerVideoGravityResizeAspect;
            break;
    }
}

- (void)krv_setMuted:(BOOL)muted {
    _player.muted = muted;
}

- (void)krv_setRate:(CGFloat)rate {
    _player.rate = (float)rate;
}

- (void)krv_seekToTime:(NSUInteger)seekTotime {
    CMTime target = CMTimeMakeWithSeconds((Float64)seekTotime / 1000.0, NSEC_PER_SEC);
    [_player seekToTime:target];
}

- (void)krv_setPropWithKey:(NSString *)propKey propValue:(id)propValue {
    // Reserved for business extension; no-op in the demo.
}

- (void)krv_callWithMethod:(NSString *)method params:(NSString *)params {
    // Reserved for business extension; no-op in the demo.
}

@end
