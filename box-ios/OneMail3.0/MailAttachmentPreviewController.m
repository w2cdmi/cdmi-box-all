//
//  MailAttachmentPreviewControllerViewController.m
//  OneMail
//
//  Created by Jason on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailAttachmentPreviewController.h"
#import "AppDelegate.h"
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import "TransportFilePreviewTaskHandle.h"
#import "TransportTask.h"
#import "File.h"
#import "File+Remote.h"
#import "FileThumbnail.h"
#import "UIAlertView+Blocks.h"
@interface mailAttachmentPreviewController () <UIWebViewDelegate>
@property (nonatomic,strong) File                           *file;
@property (nonatomic,strong) UIView                         *previewResource;
@property (nonatomic,strong) UIImageView                    *previewImageView;
@property (nonatomic,strong) UIWebView                      *previewWebView;
@property (nonatomic,strong) AVAudioPlayer                  *previewAudioPlayer;
@property (nonatomic,strong) MPMoviePlayerController        *previewVideoPlayer;
@property (nonatomic,strong) UIActivityIndicatorView        *previewIndicator;
@property (nonatomic,strong) NSString                       *attachmentPath;
@property (nonatomic,strong) TransportFilePreviewTaskHandle *taskHandle;
@property (nonatomic,strong) UIView                         *previewLoading;
@property (nonatomic,strong) UIImageView                    *previewLoadingThumbnail;
@property (nonatomic,strong) UIProgressView                 *previewLoadingProgress;
@property (nonatomic,strong) UILabel                        *previewLoadingTitle;

@property (nonatomic,strong) UIButton                       *previewBackButton;
@property (nonatomic,strong) UILabel                        *previewTitleLabel;
@end

@implementation mailAttachmentPreviewController
- (id)initWithAttachment:(Attachment *)attachment{
    if (self = [super init]) {
        self.attachment = attachment;
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.previewTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.previewTitleLabel.font = [UIFont boldSystemFontOfSize:18.0f];
    self.previewTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.previewTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.previewTitleLabel.text = self.attachment.attachmentName;
    
    self.previewBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.previewBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.previewBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.previewBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.previewBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    NSString *attachmentDataLocalPath = [self.attachment attachmentDataLocalPath];
    if (attachmentDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentDataLocalPath]) {
        self.attachmentPath = self.attachment.attachmentDataLocalPath;
        [self previewAttachmentShow];
    }
    else{
            if (self.attachment.attachmentFileId && self.attachment.attachmentFileOwner) {
                File *file = [File getFileWithFileId:self.attachment.attachmentFileId fileOwner:self.attachment.attachmentFileOwner];
                if (file) {
                    self.file = file;
                    NSString *fileDataLocalPath = [file fileDataLocalPath];
                    if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
                        self.attachmentPath = fileDataLocalPath;
                        [self previewAttachmentShow];
                    } else {
                        [self previewFileLoading];
                    }
                }
                else{
                    [self getFileInfo];
                }
            } else {
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudPreviewAttachmentNoFound", nil)];
            }
    }
    // Do any additional setup after loading the view.
}
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.previewTitleLabel];
    [self.navigationController.navigationBar addSubview:self.previewBackButton];
}
- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self.previewTitleLabel removeFromSuperview];
    [self.previewBackButton removeFromSuperview];
}
- (void)getFileInfo{
    AppDelegate *appdelegate = [UIApplication sharedApplication].delegate;
    NSNumber *fileId = [NSNumber numberWithInteger:self.attachment.attachmentFileId.integerValue];
    NSNumber *fileOwner = [NSNumber numberWithInteger:self.attachment.attachmentFileOwner.integerValue];
    NSDictionary *fileInfo = [NSDictionary dictionaryWithObjectsAndKeys:fileId,@"id",fileOwner,@"ownedBy",@(1),@"type", nil];
    [appdelegate.remoteManager ensureCloudLogin:^{
        [File getFileContent:fileInfo succeed:^(id retobj) {
            NSManagedObjectContext *backGroundContext = appdelegate.localManager.backgroundObjectContext;
            [backGroundContext performBlockAndWait:^{
                [File fileInsertWithInfo:retobj context:backGroundContext];
                [backGroundContext save:nil];
                [self previewFileLoading];
            }];
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
            });
        }];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudPreviewDownloadFailedPrompt", nil)];
        });
    }];
}
- (void)previewFileLoading{
    [self.view addSubview:self.previewLoading];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!self.file) {
        self.file = [File getFileWithFileId:self.attachment.attachmentFileId fileOwner:self.attachment.attachmentFileOwner];
    }
    [appDelegate.remoteManager ensureCloudLogin:^{
        if (!appDelegate.wifiNetwork && [UserSetting defaultSetting].cloudWiFiPrompt.boolValue) {
            [UIAlertView showAlertViewWithTitle:nil message:NSLocalizedString(@"CloudDownloadWIFIPrompt", nil) cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
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
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudPreviewDownloadFailedPrompt", nil)];
        });
    }];
}
- (void)doLoadingWithForce:(BOOL)force {
    self.taskHandle = (TransportFilePreviewTaskHandle*)[self.file previewWithForce:YES];
    [self.taskHandle addObserver:self forKeyPath:@"taskFraction" options:NSKeyValueObservingOptionNew context:nil];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager.downloadService downloadWithTask:self.taskHandle.transportTask taskProgress:^(AFHTTPRequestOperation *downloadOperation, NSProgress *taskProgress) {
        self.taskHandle.requestOperation = downloadOperation;
        self.taskHandle.taskProgress = taskProgress;
    } completionHandler:^(NSURLResponse *response, NSError *error) {
        if (error) {
            if (((NSHTTPURLResponse*)response).statusCode == 404) {
                [self.taskHandle.transportTask.taskHandle failed];
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudPreviewFileNoFound", nil)];
            } else if ([error.domain isEqual:((__bridge NSString*)kCFErrorDomainCFNetwork)] || [error.domain isEqual:NSURLErrorDomain] || [error.domain isEqual:AFNetworkingErrorDomain]) {
                [self.taskHandle.transportTask.taskHandle failed];
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudPreviewDownloadFailedPrompt", nil)];
            } else {
                [self.taskHandle.transportTask.taskHandle failed];
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudPreviewDownloadFailedPrompt", nil)];
            }
        } else {
            NSString *fileCacheLocalPath = [self.file fileCacheLocalPath];
            NSString *fileDataLocalPath = [self.file fileDataLocalPath];
            self.attachmentPath = fileDataLocalPath;
            [self.taskHandle savePath:fileCacheLocalPath toPath:fileDataLocalPath];
            [self.taskHandle.transportTask.taskHandle success];
            if ([mailAttachmentPreviewController isSupportedImage:self.file.fileName]) {
                [self.file fileCompressImage];
            }
            [self.previewLoading removeFromSuperview];
            [self previewAttachmentShow];
        }
    }];
}
- (UIImageView *)previewImageView{
    if (!_previewImageView) {
        _previewImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self.view addSubview:_previewImageView];
    }
    return _previewImageView;
}
- (UIWebView *)previewWebView{
    if (!_previewWebView) {
        _previewWebView = [[UIWebView alloc] initWithFrame:self.view.frame];
    }
    return _previewWebView;
}
- (void) previewAttachmentShow {
    if ([[self class] isSupportedVideo:self.attachment.attachmentName]) {
        [self previewVideo];
    } else if ([[self class] isSupportedImage:self.attachment.attachmentName]) {
        [self previewImage];
    } else if ([[self class] isSupportedAudio:self.attachment.attachmentName]) {
        [self previewAudio];
    } else {
        [self previewOther];
    }
}
- (UIView*)previewLoading {
    if (!_previewLoading) {
        
        CGFloat statusBarHeight = [UIApplication sharedApplication].statusBarFrame.size.height;
        CGFloat navBarHeight = self.navigationController.navigationBar.frame.size.height;
        _previewLoading = [[UIView alloc] initWithFrame:CGRectMake(0, statusBarHeight + navBarHeight, self.view.size.width,self.view.size.height - statusBarHeight - navBarHeight)];
        _previewLoading.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1];
        _previewLoading.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
        
        _previewLoadingThumbnail = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 96, 96)];
        _previewLoadingThumbnail.frame = CGRectMake((CGRectGetWidth(_previewLoading.frame)-CGRectGetWidth(_previewLoadingThumbnail.frame))/2, 104, CGRectGetWidth(_previewLoadingThumbnail.frame), CGRectGetHeight(_previewLoadingThumbnail.frame));
        [FileThumbnail imageWithFile:self.file imageView:_previewLoadingThumbnail];
        [_previewLoading addSubview:_previewLoadingThumbnail];
        
        _previewLoadingProgress = [[UIProgressView alloc] initWithProgressViewStyle:UIProgressViewStyleDefault];
        _previewLoadingProgress.frame = CGRectMake(0, 0, self.view.size.width, 4);
        _previewLoadingProgress.progressTintColor = [CommonFunction colorWithString:@"6fcc31" alpha:1];
        _previewLoadingProgress.trackTintColor = [CommonFunction colorWithString:@"cccccc" alpha:1];
        [_previewLoading addSubview:_previewLoadingProgress];
        
        _previewLoadingTitle = [[UILabel alloc] initWithFrame:CGRectMake(15, 104 + 96 + 10, self.view.size.width - 30, 20)];
        if (self.attachment) {
            _previewLoadingTitle.text = self.attachment.attachmentName;
        }
        _previewLoadingTitle.font = [UIFont systemFontOfSize:15];
        _previewLoadingTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1];
        _previewLoadingTitle.textAlignment = NSTextAlignmentCenter;
        [self.previewLoading addSubview:_previewLoadingTitle];
    }
    return _previewLoading;
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
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context{
    if (object == self.taskHandle) {
        TransportTaskHandle *taskHandle = object;
        if ([keyPath isEqual:@"taskFraction"]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.previewLoadingProgress setProgress:taskHandle.taskFraction];
            });
        }
    }
}
#pragma mark Back Button Click
- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}
#pragma mark image
- (void) previewImage {
    UIImage *image = [UIImage imageWithContentsOfFile:self.attachmentPath];
    CGSize imageSize = image.size;
    self.previewImageView.image = image;
    CGFloat imageScale = imageSize.height / imageSize.width;
    CGFloat viewScale = self.view.frame.size.height / self.view.frame.size.width;
    if (imageScale > viewScale) {
        self.previewImageView.size = CGSizeMake(self.view.frame.size.height / imageScale, self.view.frame.size.height);
    }
    else{
        self.previewImageView.size = CGSizeMake(self.view.frame.size.width, self.view.frame.size.width * imageScale);
    }
    self.previewImageView.center = self.view.center;
}

#pragma mark audio
- (void) previewAudio {
    NSString *dataLocalPath;
        dataLocalPath = self.attachmentPath;
    self.previewAudioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:[NSURL URLWithString:dataLocalPath] error:nil];
    self.previewAudioPlayer.volume = 1.0f;
    self.previewAudioPlayer.pan = 0.0f;
    self.previewAudioPlayer.numberOfLoops = 1;
    [self.previewAudioPlayer prepareToPlay];
    [self.previewAudioPlayer play];
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
    
    if (self.attachmentPath) {
        [self.previewVideoPlayer setContentURL:[NSURL fileURLWithPath:self.attachmentPath]];
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
    //self.navigationController.navigationBar.hidden = YES;
    //[[UIApplication sharedApplication].statusBarHidden = YES;
    
    NSData* previewData = nil;
    CFStringRef fileExtension = nil;
    if (self.attachmentPath) {
        previewData = [NSData dataWithContentsOfFile:self.attachmentPath options:NSDataReadingMappedIfSafe error:nil];
        fileExtension = (__bridge CFStringRef)[self.attachment.attachmentName pathExtension];
    }
    if (!previewData || !fileExtension) {
        return;
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
    
    [self.previewWebView loadData:previewData MIMEType:(__bridge NSString *)(mimeType) textEncodingName:@"UTF-8" baseURL:nil];
    CFBridgingRelease(mimeType);
    
    [self.previewResource addSubview:self.previewWebView];
    [self.previewResource bringSubviewToFront:self.previewWebView];
    [self.view addSubview:self.previewResource];
}
#pragma mark webview delegate
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
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
