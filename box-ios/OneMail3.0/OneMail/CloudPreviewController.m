//
//  CloudPreviewController.m
//  OneMail
//
//  Created by cse  on 15/11/12.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudPreviewController.h"
#import "AppDelegate.h"
#import "File.h"
#import "File+Remote.h"
#import "Version.h"
#import "FileThumbnail.h"
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import "TransportFilePreviewTaskHandle.h"
#import "TransportTask.h"
#import "TransportTaskHandle.h"

#import "UIAlertView+Blocks.h"


@interface CloudPreviewController ()<UIWebViewDelegate,UIGestureRecognizerDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) Version *version;
@property (nonatomic, strong) TransportFilePreviewTaskHandle *taskHandle;
@property (nonatomic, strong) UIActivityIndicatorView *previewIndicator;
@property (nonatomic, strong) UIView                  *previewLoading;
@property (nonatomic, strong) UIImageView             *previewLoadingThumbnail;
@property (nonatomic, strong) UIProgressView          *previewLoadingProgress;
@property (nonatomic, strong) UILabel                 *previewLoadingTitle;
@property (nonatomic, strong) UIView                  *previewResource;
@property (nonatomic, strong) MPMoviePlayerController *previewVideoPlayer;
@property (nonatomic, strong) AVAudioPlayer           *previewAudioPlayer;
@property (nonatomic, strong) UIButton                *previewAudioControlButton;
@property (nonatomic, strong) UIProgressView          *previewAudioProgress;
@property (nonatomic, strong) UILabel                 *previewAudioTimeLabel;
@property (nonatomic, strong) NSTimer                 *previewAudioTimer;
@property (nonatomic, strong) UIImageView             *previewAudioThumbNail;
@property (nonatomic, strong) UIWebView               *previewWebView;
@property (nonatomic, assign) BOOL                     previewSupport;

/** 进度文件大小 */
@property (strong, nonatomic) UILabel *progressFileSize;
/** 进度文件的百分比 */
@property (strong, nonatomic) UILabel *progressFileFraction;


@end

@implementation CloudPreviewController

- (id)initWithFile:(File *)file {
    self = [super initWithFile:file fileSearch:nil fileTransport:nil];
    if (self) {
        self.file = file;
        if (self.file.fileType.integerValue == TypeRAR || self.file.fileType.integerValue == TypeUnknow) {
            self.previewSupport = NO;
        } else {
            self.previewSupport = YES;
        }
    }
    return self;
}
- (id)initWithFile:(File*)file fileSearch:(NSString *)searchText {
    self = [super initWithFile:file fileSearch:searchText fileTransport:nil];
    if (self) {
        self.file = file;
        if (self.file.fileType.integerValue == TypeRAR || self.file.fileType.integerValue == TypeUnknow) {
            self.previewSupport = NO;
        } else {
            self.previewSupport = YES;
        }
    }
    return self;
}

- (id)initWithVersion:(Version*)version {
    self = [super initWithVersion:version];
    if (self) {
        self.version = version;
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [UIApplication sharedApplication].statusBarHidden = NO;
    [self.navigationController.navigationBar setHidden:NO];
    self.view.backgroundColor = [UIColor whiteColor];
    UIBarButtonItem *backbutton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    self.navigationItem.leftBarButtonItem = backbutton;
    if (self.file) {
        self.title = self.file.fileName;
        NSString *fileDataLocalPath = [self.file fileDataLocalPath];
        if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
            if (self.previewSupport) {
                [self previewFileShow];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:@"不支持打开此格式文件"];
                });
            }
        } else {
            if (self.file.transportTask && self.file.transportTask.taskType.integerValue != TaskFilePreview) {
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudPreviewFileTaskPrompt", nil)];
                return;
            }
            if (self.previewSupport) {
                [self previewFileLoading];
            } else {
                [UIAlertView showAlertViewWithTitle:nil message:@"不支持打开此格式文件,是否仍继续下载?" cancelButtonTitle:getLocalizedString(@"Cancel",nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                    [self previewFileLoading];
                } onCancel:^{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.navigationController popViewControllerAnimated:YES];
                    });
                }];
            }
        }
    }
    if (self.version) {
        self.title = self.version.versionFileName;
        NSString *versionDataLocalPath = [self.version versionDataLocalPath];
        if (versionDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:versionDataLocalPath]) {
            [self previewVersionShow];
        } else {
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudPreviewVersionNoFound", nil)];
        }
    }
    
}
- (void)popViewController{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    if (self.taskHandle) {
        if (self.taskHandle.transportTask.taskStatus.integerValue != TaskSucceed) {
            [self.taskHandle cancel];
        }
        [self.taskHandle removeObserver:self forKeyPath:@"taskFraction"];
    }
    
    if (self.previewVideoPlayer) {
        [self.previewVideoPlayer stop];
        [[NSNotificationCenter defaultCenter]removeObserver:self name:MPMoviePlayerPlaybackDidFinishNotification object:self.previewVideoPlayer];
    }
    if (self.previewAudioPlayer) {
        [self.previewAudioPlayer stop];
    }
    if (self.previewWebView.loading) {
        [self.previewWebView stopLoading];
        [self hideIndicator];
        self.previewWebView.delegate = nil;
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabShow" object:nil];
    
}
- (void)dealloc{
    
}

+ (BOOL) isSupportedImage:(NSString*) fileName {
    BOOL bImage = NO;
    CFStringRef fileExtension = (__bridge CFStringRef)[fileName pathExtension];
    CFStringRef UTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, fileExtension, NULL);
    
    bImage = UTTypeConformsTo(UTI, kUTTypeImage);
    CFRelease(UTI);
    
    return bImage;
}

+ (BOOL) isSupportedVideo:(NSString*) fileName {
    BOOL bVideo = NO;
    CFStringRef fileExtension = (__bridge CFStringRef)[fileName pathExtension];
    CFStringRef UTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, fileExtension, NULL);
    
    bVideo = UTTypeConformsTo(UTI, kUTTypeMovie);
    
    CFRelease(UTI);
    return bVideo;
}

+ (BOOL) isSupportedAudio:(NSString*) fileName {
    BOOL bAudio = NO;
    CFStringRef fileExtension = (__bridge CFStringRef)[fileName pathExtension];
    CFStringRef UTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, fileExtension, NULL);
    
    bAudio = UTTypeConformsTo(UTI, kUTTypeAudio);
    
    CFRelease(UTI);
    return bAudio;
}
//
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context{
    if (object == self.taskHandle) {
        TransportTaskHandle *taskHandle = object;
        if ([keyPath isEqual:@"taskFraction"]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                
                [self.previewLoadingProgress setProgress:taskHandle.taskFraction animated:YES];
                
                float fractionComplete = (NSInteger)floor(taskHandle.taskFraction*100);
                //NSString *progressFileSize = [CommonFunction pretySize:self.file.fileSize.longLongValue];
                //_progressFileSize.text = [NSString stringWithFormat:@"%.2f / %@",[progressFileSize integerValue] * fractionComplete , progressFileSize];
                _progressFileFraction.text = [NSString stringWithFormat:@"%ld%@",(long)fractionComplete,@"%"];
                
            });
        }
    }
}

- (UIView*)previewLoading {
    if (!_previewLoading) {
//        CGFloat statusBarHeight = [UIApplication sharedApplication].statusBarFrame.size.height;
//        CGFloat navBarHeight = self.navigationController.navigationBar.frame.size.height;
        _previewLoading = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.size.width,self.view.size.height)];//10.10- statusBarHeight - navBarHeight
        _previewLoading.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1];
        _previewLoading.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
        
        _previewLoadingThumbnail = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 100, 100)];
        _previewLoadingThumbnail.frame = CGRectMake((CGRectGetWidth(_previewLoading.frame)-CGRectGetWidth(_previewLoadingThumbnail.frame))/2, 104, CGRectGetWidth(_previewLoadingThumbnail.frame), CGRectGetHeight(_previewLoadingThumbnail.frame));
        [FileThumbnail imageWithFile:self.file imageView:_previewLoadingThumbnail];
        [_previewLoading addSubview:_previewLoadingThumbnail];
        
        _previewLoadingProgress = [[UIProgressView alloc] initWithProgressViewStyle:UIProgressViewStyleDefault];
        _previewLoadingProgress.frame = CGRectMake((CGRectGetWidth(_previewLoading.frame)-CGRectGetWidth(_previewLoadingThumbnail.frame))/2 - 20, 250, 140, 10);
        _previewLoadingProgress.transform = CGAffineTransformMakeScale(1.0f, 3.0f);
        _previewLoadingProgress.progress = 0;
        _previewLoadingProgress.progressTintColor = [CommonFunction colorWithString:@"6fcc31" alpha:1];
        _previewLoadingProgress.trackTintColor = [CommonFunction colorWithString:@"cccccc" alpha:1];
        [_previewLoading addSubview:_previewLoadingProgress];
        
        _progressFileSize = [[UILabel alloc]initWithFrame:CGRectMake((CGRectGetWidth(_previewLoading.frame)-CGRectGetWidth(_previewLoadingThumbnail.frame))/2 - 30, 270, 150, 15)];
        _progressFileSize.font = [UIFont systemFontOfSize:15.0f];
        _progressFileSize.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0];
        _progressFileSize.textAlignment = NSTextAlignmentLeft;
        //_progressFileSize.text = [NSString stringWithFormat:@"%@",[CommonFunction pretySize:self.file.fileSize.longLongValue]];
        [self.previewLoading addSubview:_progressFileSize];
        // [[UILabel alloc]initWithFrame:CGRectMake((CGRectGetWidth(_previewLoading.frame)/2, 280, 150, 15)]
        _progressFileFraction =[[UILabel alloc] initWithFrame:CGRectMake((CGRectGetWidth(_previewLoading.frame)-CGRectGetWidth(_previewLoadingThumbnail.frame))/2 - 80, 270, 150, 15)];
        _progressFileFraction.font = [UIFont systemFontOfSize:15.0f];
        _progressFileFraction.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0];
        _progressFileFraction.textAlignment = NSTextAlignmentRight;
        NSInteger fractionComplete = (NSInteger)floor(self.transportTask.taskHandle.taskFraction*100);
        _progressFileFraction.text = [NSString stringWithFormat:@"%ld%@",(long)fractionComplete,@"%"];
        [self.previewLoading addSubview:_progressFileFraction];
        
        
        _previewLoadingTitle = [[UILabel alloc] initWithFrame:CGRectMake(15, 104 + 96 + 10, self.view.size.width - 30, 20)];
        if (self.file) {
            _previewLoadingTitle.text = self.file.fileName;
        }
        if (self.version) {
            _previewLoadingTitle.text = self.version.versionFileName;
        }
        _previewLoadingTitle.font = [UIFont systemFontOfSize:15];
        _previewLoadingTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1];
        _previewLoadingTitle.textAlignment = NSTextAlignmentCenter;
        [self.previewLoading addSubview:_previewLoadingTitle];
    }
    return _previewLoading;
}

- (void)setTransportTask:(TransportTask *)transportTask {
    if (_transportTask != transportTask) {
        if (_transportTask) {
            [_transportTask.taskHandle removeObserver:self forKeyPath:@"taskFraction"];
            [_transportTask.taskHandle removeObserver:self forKeyPath:@"taskTotalUnitCount"];
            [_transportTask removeObserver:self forKeyPath:@"taskStatus"];
        }
        _transportTask = transportTask;
        if (transportTask) {
            [self.previewLoadingProgress setProgress:transportTask.taskFraction.floatValue animated:NO];
            [transportTask.taskHandle addObserver:self forKeyPath:@"taskFraction" options:NSKeyValueObservingOptionNew context:nil];
            [transportTask.taskHandle addObserver:self forKeyPath:@"taskTotalUnitCount" options:NSKeyValueObservingOptionNew context:nil];
            [transportTask addObserver:self forKeyPath:@"taskStatus" options:NSKeyValueObservingOptionNew context:nil];
        }
    }

}

- (UIView*)previewResource {
    if (!_previewResource) {
        _previewResource = [[UIView alloc] initWithFrame:CGRectZero];
        CGFloat screenWidth = CGRectGetWidth([UIScreen mainScreen].bounds);
        CGFloat screenHeight = CGRectGetHeight([UIScreen mainScreen].bounds);
        if ([[UIApplication sharedApplication] statusBarOrientation] == UIDeviceOrientationLandscapeRight
            || [[UIApplication sharedApplication] statusBarOrientation] == UIDeviceOrientationLandscapeLeft) {
            _previewResource.frame = CGRectMake(0, 0, screenHeight, screenWidth);
        } else {
            _previewResource.frame = CGRectMake(0, 0, screenWidth, screenHeight);
        }
        _previewResource.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    }
    return _previewResource;
}

#pragma mark FileLoading
- (void)previewFileLoading {
    [self.view addSubview:self.previewLoading];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        if (!appDelegate.wifiNetwork && [UserSetting defaultSetting].cloudWiFiPrompt.boolValue) {
            [UIAlertView showAlertViewWithTitle:nil message:getLocalizedString(@"CloudDownloadWIFIPrompt", nil) cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                [self doLoadingWithForce:YES];
            } onCancel:^{
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.navigationController popViewControllerAnimated:YES];
                });
            }];
        } else {
            [self doLoadingWithForce:YES];
        }
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudPreviewDownloadFailedPrompt", nil)];
        });
    }];
}
- (void)doLoadingWithForce:(BOOL)force {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    self.taskHandle = (TransportFilePreviewTaskHandle*)[self.file previewWithForce:force];
    [self.taskHandle addObserver:self forKeyPath:@"taskFraction" options:NSKeyValueObservingOptionNew context:nil];
    [appDelegate.remoteManager.downloadService downloadWithTask:self.taskHandle.transportTask taskProgress:^(AFHTTPRequestOperation *downloadOperation, NSProgress *taskProgress) {
        self.taskHandle.requestOperation = downloadOperation;
        self.taskHandle.taskProgress = taskProgress;
    } completionHandler:^(NSURLResponse *response, NSError *error) {
        if (error) {
            if (((NSHTTPURLResponse*)response).statusCode == 404) {
                [self.taskHandle.transportTask.taskHandle failed];
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudPreviewFileNoFound", nil)];
            } else if ([error.domain isEqual:((__bridge NSString*)kCFErrorDomainCFNetwork)] || [error.domain isEqual:NSURLErrorDomain] || [error.domain isEqual:AFNetworkingErrorDomain]) {
                [self.taskHandle.transportTask.taskHandle failed];
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudPreviewDownloadFailedPrompt", nil)];
            } else {
                [self.taskHandle.transportTask.taskHandle failed];
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudPreviewDownloadFailedPrompt", nil)];
            }
        } else {
            NSString *fileCacheLocalPath = [self.file fileCacheLocalPath];
            NSString *fileDataLocalPath = [self.file fileDataLocalPath];
            [self.taskHandle savePath:fileCacheLocalPath toPath:fileDataLocalPath];
            [self.taskHandle.transportTask.taskHandle success];
            if ([CloudPreviewController isSupportedImage:self.file.fileName]) {
                [self.file fileCompressImage];
            }
            [self.previewLoading removeFromSuperview];
            if (self.previewSupport) {
                [self previewFileShow];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:@"不支持打开此格式文件"];
                });
            }
        }
    }];
}
#pragma mark FilePreview
- (void) previewFileShow {
    UITapGestureRecognizer* singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
    singleTap.delegate = self;
    singleTap.cancelsTouchesInView = NO;
    [self.view addGestureRecognizer:singleTap];

    if ([[self class] isSupportedVideo:self.file.fileName]) {
        [self previewVideo];
    } else if ([[self class] isSupportedImage:self.file.fileName]) {
        [self previewImage];
    } else if ([[self class] isSupportedAudio:self.file.fileName]) {
        [self previewAudio];
    }  else {
        [self previewOther];
    }
}

- (void) previewVersionShow {
    if ([[self class] isSupportedVideo:self.version.versionFileName]) {
        [self previewVideo];
    } else if ([[self class] isSupportedImage:self.version.versionFileName]) {
        [self previewImage];
    } else if ([[self class] isSupportedAudio:self.file.fileName]) {
        [self previewAudio];
    } else {
        [self previewOther];
    }
}
#pragma mark image
- (void) previewImage {
    [UIApplication sharedApplication].statusBarHidden = NO;
    [self.navigationController.navigationBar setHidden:NO];
    self.view.backgroundColor = [UIColor blackColor];
    if (self.file) {
        [self loadAlbumInformation];
    }
    if (self.version) {
        [self loadVersionInformation];
    }
}

#pragma mark audio
- (void) previewAudio {
    NSString *dataLocalPath;
    if (self.file) {
        dataLocalPath = [self.file fileDataLocalPath];
    }
    if (self.version) {
        dataLocalPath = [self.version versionDataLocalPath];
    }
    self.previewAudioPlayer = [[AVAudioPlayer alloc] initWithData:[NSData dataWithContentsOfMappedFile:dataLocalPath] error:nil];
    self.previewAudioPlayer.volume = 1.0f;
    self.previewAudioPlayer.pan = 0.0f;
    self.previewAudioPlayer.numberOfLoops = 1;
    [self.previewAudioPlayer prepareToPlay];
    [self.previewAudioPlayer play];
    CGRect navFrame = self.navigationController.navigationBar.frame;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    self.previewAudioThumbNail = [[UIImageView alloc] initWithFrame:CGRectMake(self.view.size.width - 150, navFrame.size.height + statusBarFrame.size.height + 50, 100, 100)];
    if (self.file) {
        [FileThumbnail imageWithFile:self.file imageView:self.previewAudioThumbNail];
    }
    if (self.version) {
        [FileThumbnail imageWithVersion:self.version imageView:self.previewAudioThumbNail];
    }
    
    self.previewAudioControlButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
    [self.previewAudioControlButton setImage:[UIImage imageNamed:@"ic_transfer_pause_nor"] forState:UIControlStateNormal];
    self.previewAudioControlButton.tag = 1;
    [self.previewAudioControlButton addTarget:self action:@selector(controlAudio) forControlEvents:UIControlEventTouchUpInside];
    self.previewAudioProgress = [[UIProgressView alloc] initWithFrame:CGRectMake(0, 0, 220, 8)];
    self.previewAudioProgress.progressTintColor = [UIColor blueColor];
    self.previewAudioProgress.center = self.view.center;
    self.previewAudioControlButton.frame = CGRectMake(CGRectGetMinX(self.previewAudioProgress.frame) - 60, CGRectGetMinY(self.previewAudioProgress.frame) - 18, 44, 44);
    self.previewAudioTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 200, 50)];
    CGPoint point = self.previewAudioProgress.center;
    point.y = point.y + 50;
    self.previewAudioTimeLabel.textColor = [UIColor blackColor];
    self.previewAudioTimeLabel.font = [UIFont systemFontOfSize:14];
    self.previewAudioTimeLabel.textAlignment = NSTextAlignmentCenter;
    self.previewAudioTimeLabel.center = point;
    self.previewAudioTimeLabel.text = [NSString stringWithFormat:@"%02ld:%02ld/%02ld:%02ld",(long)self.previewAudioPlayer.currentTime / 60,((long)self.previewAudioPlayer.currentTime % 60),(long)self.previewAudioPlayer.duration / 60,((long)self.previewAudioPlayer.duration % 60)];
    [self.view addSubview:self.previewAudioProgress];
    [self.view addSubview:self.previewAudioControlButton];
    [self.view addSubview:self.previewAudioTimeLabel];
    [self.view addSubview:self.previewAudioThumbNail];
    self.previewAudioTimer = [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(showAudioProgress) userInfo:nil repeats:YES];
    [self.previewAudioTimer fire];
    for (UIGestureRecognizer *gesture in self.view.gestureRecognizers) {
        [self.view removeGestureRecognizer:gesture];
    }
}
- (void)showAudioProgress{
    self.previewAudioTimeLabel.text = [NSString stringWithFormat:@"%02ld:%02ld/%02ld:%02ld",(long)self.previewAudioPlayer.currentTime / 60,((long)self.previewAudioPlayer.currentTime % 60),(long)self.previewAudioPlayer.duration / 60,((long)self.previewAudioPlayer.duration % 60)];
    self.previewAudioProgress.progress = self.previewAudioPlayer.currentTime / self.previewAudioPlayer.duration;
}
- (void)controlAudio{
    if (self.previewAudioControlButton.tag == 1) {
        self.previewAudioControlButton.tag = 0;
        [self.previewAudioControlButton setImage:[UIImage imageNamed:@"ic_transfer_start_nor"] forState:UIControlStateNormal];
        [self.previewAudioPlayer pause];
    }
    else{
        self.previewAudioControlButton.tag = 1;
        [self.previewAudioControlButton setImage:[UIImage imageNamed:@"ic_transfer_pause_nor"] forState:UIControlStateNormal];
        [self.previewAudioPlayer play];
    }
}

#pragma mark video
- (void) previewVideo {
    self.navigationController.navigationBar.hidden = YES;
    [UIApplication sharedApplication].statusBarHidden = YES;
    
    self.previewVideoPlayer = [[MPMoviePlayerController alloc] init];
    self.previewVideoPlayer.fullscreen = YES;
    self.previewVideoPlayer.movieSourceType = MPMovieSourceTypeFile;
    [self.previewVideoPlayer setControlStyle:MPMovieControlStyleFullscreen];
    self.previewVideoPlayer.view.frame = self.previewResource.bounds;
    self.previewVideoPlayer.scalingMode = MPMovieScalingModeAspectFit;
    self.previewVideoPlayer.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    
    if (self.file) {
        [self.previewVideoPlayer setContentURL:[NSURL fileURLWithPath:[self.file fileDataLocalPath]]];
    }
    if (self.version) {
        [self.previewVideoPlayer setContentURL:[NSURL fileURLWithPath:[self.version versionDataLocalPath]]];
    }
    [self.previewVideoPlayer prepareToPlay];
    self.previewVideoPlayer.shouldAutoplay = YES;
    [self.previewVideoPlayer play];
    
    [self.previewResource addSubview:self.previewVideoPlayer.view];
    [self.previewResource bringSubviewToFront:self.previewVideoPlayer.view];
    [self.view addSubview:self.previewResource];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playVideoDone:) name:MPMoviePlayerPlaybackDidFinishNotification object:self.previewVideoPlayer];
}

- (void)playVideoDone:(id)sender {
    if (self.previewVideoPlayer.playbackState == MPMoviePlaybackStatePlaying ||
        self.previewVideoPlayer.playbackState == MPMoviePlaybackStatePaused) {
        [self.previewVideoPlayer stop];
        [self.previewVideoPlayer.view removeFromSuperview];
        [self.previewResource removeFromSuperview];
        [self.navigationController.navigationBar setHidden:NO];
        [UIApplication sharedApplication].statusBarHidden = NO;
        [self.navigationController popViewControllerAnimated:YES];
    }
}

#pragma mark other
- (void)previewOther {
    
    [UIApplication sharedApplication].statusBarHidden = YES;
    [self.navigationController.navigationBar setHidden:YES];
    NSData* previewData = nil;
    CFStringRef fileExtension = nil;
    if (self.file) {
        previewData = [NSData dataWithContentsOfFile:[self.file fileDataLocalPath] options:NSDataReadingMappedIfSafe error:nil];
        fileExtension = (__bridge CFStringRef)[self.file.fileName pathExtension];
    }
    if (self.version) {
        previewData = [NSData dataWithContentsOfFile:[self.version versionDataLocalPath] options:NSDataReadingMappedIfSafe error:nil];
        fileExtension = (__bridge CFStringRef)[self.version.versionFileName pathExtension];
    }
    if (!previewData || !fileExtension) {
        return;
    }
    if ([[self.file.fileName pathExtension] isEqualToString:@"txt"] &&
        previewData.length > 1024 * 1024) {
        previewData = [previewData subdataWithRange:NSMakeRange(0, 1024 * 1024)];
    }
    self.previewWebView = [[UIWebView alloc] initWithFrame:CGRectZero];
    self.previewWebView.frame = self.previewResource.frame;
    self.previewWebView.backgroundColor = [UIColor whiteColor];
    self.previewWebView.multipleTouchEnabled = YES;
    self.previewWebView.scalesPageToFit = YES;
    self.previewWebView.contentMode = UIViewContentModeScaleToFill;
    self.previewWebView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.previewWebView.delegate = self;
    
    CFStringRef UTI = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, fileExtension, NULL);
    CFStringRef mimeType = UTTypeCopyPreferredTagWithClass(UTI, kUTTagClassMIMEType);
    CFRelease(UTI);
    
//    GBK解码
    [self.previewWebView loadData:previewData MIMEType:(__bridge NSString *)(mimeType) textEncodingName:@"GBK" baseURL:nil];
    CFBridgingRelease(mimeType);
    
    [self.previewResource addSubview:self.previewWebView];
    [self.previewResource bringSubviewToFront:self.previewWebView];
    [self.view addSubview:self.previewResource];

}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [self showIndicator];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [self hideIndicator];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    [self hideIndicator];
    if (error.code == 204) {
        return;
    }
    if(!self.previewLoading.superview) {
        [self.view addSubview:self.previewLoading];
    }
    self.previewLoadingProgress.hidden = YES;
}

#pragma mark indicator
- (UIActivityIndicatorView*)previewIndicator {
    if (!_previewIndicator) {
        _previewIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        _previewIndicator.center = self.view.center;
    }
    return _previewIndicator;
}

- (void) showIndicator {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (!self.previewIndicator.superview) {
            [self.view addSubview:self.previewIndicator];
        }
        if (!self.previewIndicator.isAnimating) {
            [self.previewIndicator startAnimating];
        }
    });
}

- (void) hideIndicator {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.previewIndicator.isAnimating) {
            [self.previewIndicator stopAnimating];
        }
        [self.previewIndicator removeFromSuperview];
    });
}
#pragma mark UITapGestureRecognizer
- (void)handleSingleTap:(UIGestureRecognizer*)sender
{
    if (self.navigationController.navigationBar.hidden) {
        [UIApplication sharedApplication].statusBarHidden = NO;
        [self.navigationController.navigationBar setHidden:NO];
        self.view.backgroundColor = [UIColor clearColor];
    } else {
        [UIApplication sharedApplication].statusBarHidden = YES;
        [self.navigationController.navigationBar setHidden:YES];
        self.view.backgroundColor = [UIColor blackColor];
    }
}
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    return YES;
}
@end
