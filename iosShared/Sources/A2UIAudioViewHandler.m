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
//  A2UIAudioViewHandler.m
//  A2UIDemoApp
//
//  Backs the A2UI v0.9 "AudioPlayer" component (KRAudioView) on iOS.
//  KRAudioView (core-render-ios) delegates actual playback to a host-registered
//  AudioViewCreator. This handler implements KRAudioViewProtocol with the system
//  AVPlayer (AVFoundation) plus a UIKit transport bar (play/pause + scrubber + time),
//  so no third-party player dependency is required. Registration happens automatically
//  via +load, mirroring A2UIVideoViewHandler.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "KRAudioView.h"

@interface A2UIAudioViewHandler : UIView <KRAudioViewProtocol>

@property (nonatomic, copy) NSString *kra_source;
@property (nonatomic, strong) AVPlayer *player;
@property (nonatomic, strong) AVPlayerItem *playerItem;
@property (nonatomic, strong) id timeObserver;

@property (nonatomic, strong) UIButton *playPauseButton;
@property (nonatomic, strong) UISlider *slider;
@property (nonatomic, strong) UILabel *timeLabel;
@property (nonatomic, assign) BOOL scrubbing;

@end

@implementation A2UIAudioViewHandler

@synthesize kra_delegate;

+ (void)load {
    [KRAudioView registerAudioViewCreator:^id<KRAudioViewProtocol> _Nonnull(NSString * _Nonnull src, CGRect frame) {
        return [[A2UIAudioViewHandler alloc] initWithFrame:frame source:src];
    }];
}

- (instancetype)initWithFrame:(CGRect)frame source:(NSString *)source {
    self = [super initWithFrame:frame];
    if (self) {
        _kra_source = [source copy];
        self.backgroundColor = [UIColor colorWithWhite:0.96 alpha:1.0];
        self.layer.cornerRadius = 8.0;
        self.clipsToBounds = YES;
        [self p_setupControls];
        [self p_setupPlayerIfNeeded];
    }
    return self;
}

- (void)dealloc {
    [self p_teardown];
}

#pragma mark - UI

- (void)p_setupControls {
    _playPauseButton = [UIButton buttonWithType:UIButtonTypeSystem];
    [_playPauseButton setTitle:@"▶" forState:UIControlStateNormal];
    _playPauseButton.titleLabel.font = [UIFont systemFontOfSize:22];
    [_playPauseButton addTarget:self action:@selector(p_onPlayPause) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_playPauseButton];

    _slider = [[UISlider alloc] init];
    _slider.minimumValue = 0;
    _slider.maximumValue = 1;
    [_slider addTarget:self action:@selector(p_onScrubBegin) forControlEvents:UIControlEventTouchDown];
    [_slider addTarget:self action:@selector(p_onScrubEnd) forControlEvents:(UIControlEventTouchUpInside | UIControlEventTouchUpOutside)];
    [self addSubview:_slider];

    _timeLabel = [[UILabel alloc] init];
    _timeLabel.font = [UIFont systemFontOfSize:12];
    _timeLabel.textColor = [UIColor darkGrayColor];
    _timeLabel.text = @"0:00";
    [self addSubview:_timeLabel];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat h = self.bounds.size.height;
    CGFloat btnW = 44;
    _playPauseButton.frame = CGRectMake(8, 0, btnW, h);
    CGFloat labelW = 48;
    _timeLabel.frame = CGRectMake(self.bounds.size.width - labelW - 8, 0, labelW, h);
    CGFloat sliderX = CGRectGetMaxX(_playPauseButton.frame) + 8;
    _slider.frame = CGRectMake(sliderX, 0, CGRectGetMinX(_timeLabel.frame) - sliderX - 8, h);
}

#pragma mark - Player

- (void)p_setupPlayerIfNeeded {
    if (_player || _kra_source.length == 0) {
        return;
    }
    NSURL *url = [NSURL URLWithString:_kra_source];
    if (url == nil) {
        return;
    }

    _playerItem = [AVPlayerItem playerItemWithURL:url];
    _player = [AVPlayer playerWithPlayerItem:_playerItem];

    [_playerItem addObserver:self forKeyPath:@"status" options:NSKeyValueObservingOptionNew context:nil];
    [_player addObserver:self forKeyPath:@"timeControlStatus" options:NSKeyValueObservingOptionNew context:nil];

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
    if (!_scrubbing && totalMs > 0) {
        _slider.value = (float)currentMs / (float)totalMs;
    }
    _timeLabel.text = [self p_formatMs:currentMs];
    if ([self.kra_delegate respondsToSelector:@selector(playTimeDidChangedWithCurrentTime:totalTime:)]) {
        [self.kra_delegate playTimeDidChangedWithCurrentTime:currentMs totalTime:totalMs];
    }
}

- (NSString *)p_formatMs:(NSUInteger)ms {
    NSUInteger totalSec = ms / 1000;
    return [NSString stringWithFormat:@"%lu:%02lu", (unsigned long)(totalSec / 60), (unsigned long)(totalSec % 60)];
}

- (void)p_playerDidReachEnd:(NSNotification *)notification {
    [_playPauseButton setTitle:@"▶" forState:UIControlStateNormal];
    [self p_notifyState:KRAudioPlayStatePlayEnd];
}

- (void)p_notifyState:(KRAudioPlayState)state {
    if ([self.kra_delegate respondsToSelector:@selector(audioPlayStateDidChangedWithState:extInfo:)]) {
        [self.kra_delegate audioPlayStateDidChangedWithState:state extInfo:@{}];
    }
}

#pragma mark - Control actions

- (void)p_onPlayPause {
    if (_player.timeControlStatus == AVPlayerTimeControlStatusPaused) {
        [self kra_play];
    } else {
        [self kra_pause];
    }
}

- (void)p_onScrubBegin {
    _scrubbing = YES;
}

- (void)p_onScrubEnd {
    _scrubbing = NO;
    if (_playerItem && CMTIME_IS_NUMERIC(_playerItem.duration)) {
        Float64 dur = CMTimeGetSeconds(_playerItem.duration);
        if (!isnan(dur) && dur > 0) {
            CMTime target = CMTimeMakeWithSeconds(dur * _slider.value, NSEC_PER_SEC);
            [_player seekToTime:target];
        }
    }
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary<NSKeyValueChangeKey,id> *)change
                       context:(void *)context {
    if ([keyPath isEqualToString:@"status"] && object == _playerItem) {
        if (_playerItem.status == AVPlayerItemStatusFailed) {
            [self p_notifyState:KRAudioPlayStateFailed];
        }
    } else if ([keyPath isEqualToString:@"timeControlStatus"] && object == _player) {
        switch (_player.timeControlStatus) {
            case AVPlayerTimeControlStatusPlaying:
                [_playPauseButton setTitle:@"⏸" forState:UIControlStateNormal];
                [self p_notifyState:KRAudioPlayStatePlaying];
                break;
            case AVPlayerTimeControlStatusPaused:
                [_playPauseButton setTitle:@"▶" forState:UIControlStateNormal];
                [self p_notifyState:KRAudioPlayStatePaused];
                break;
            case AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate:
                [self p_notifyState:KRAudioPlayStateCaching];
                break;
            default:
                break;
        }
    }
}

#pragma mark - KRAudioViewProtocol

- (void)kra_play {
    [self p_setupPlayerIfNeeded];
    [_player play];
}

- (void)kra_pause {
    [_player pause];
}

- (void)kra_stop {
    [_player pause];
    [_player seekToTime:kCMTimeZero];
    [_playPauseButton setTitle:@"▶" forState:UIControlStateNormal];
}

- (void)kra_setRate:(CGFloat)rate {
    _player.rate = (float)rate;
}

- (void)kra_seekToTime:(NSUInteger)seekTotime {
    CMTime target = CMTimeMakeWithSeconds((Float64)seekTotime / 1000.0, NSEC_PER_SEC);
    [_player seekToTime:target];
}

- (void)kra_setPropWithKey:(NSString *)propKey propValue:(id)propValue {
    // Reserved for business extension; no-op in the demo.
}

- (void)kra_callWithMethod:(NSString *)method params:(NSString *)params {
    // Reserved for business extension; no-op in the demo.
}

@end
