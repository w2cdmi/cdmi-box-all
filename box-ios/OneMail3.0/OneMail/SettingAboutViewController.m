//
//  SettingAboutViewController.m
//  OneMail
//
//  Created by cse  on 15/12/8.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "SettingAboutViewController.h"
#import "AppDelegate.h"
#import "HttpService.h"
#import "AFNetworking.h"
#import "UIAlertView+Blocks.h"
#import <CommonCrypto/CommonCrypto.h>

@interface SettingAboutViewController ()

@property (nonatomic, strong) UILabel     *mailTitleLabel;
@property (nonatomic, strong) UIButton    *mailBackButton;

@property (nonatomic, strong) NSString *clientDownloadPath;
@property (nonatomic, strong) NSString *clientVersionInfo;
@property (nonatomic, strong) NSString *clientVersion;

@end

@implementation SettingAboutViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.mailTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailTitleLabel.text = getLocalizedString(@"SettingAboutTitle", nil);
    
    self.mailBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailBackButton addTarget:self action:@selector(mailBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    UIImageView *aboutLogo = [[UIImageView alloc] initWithFrame:CGRectMake((CGRectGetWidth(self.view.frame)-120)/2, statusBarFrame.size.height+navigationBarFrame.size.height+35, 120, 120)];
    aboutLogo.image = [UIImage imageNamed:@"logo120_120-3"];
    [self.view addSubview:aboutLogo];
    
//    UILabel *aboutVersionTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(aboutLogo.frame)+5, CGRectGetWidth(self.view.frame)-15-15, 20)];
//    NSString *boundleVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:(__bridge NSString*)kCFBundleVersionKey];
//    aboutVersionTitleLabel.text = boundleVersion;
//    aboutVersionTitleLabel.font = [UIFont systemFontOfSize:14.0f];
//    aboutVersionTitleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
//    aboutVersionTitleLabel.textAlignment = NSTextAlignmentCenter;
//    [self.view addSubview:aboutVersionTitleLabel];
    
    UILabel *aboutVersionTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(aboutLogo.frame)+5, CGRectGetWidth(self.view.frame)-15-15, 20)];
    aboutVersionTitleLabel.lineBreakMode = NSLineBreakByWordWrapping;
    aboutVersionTitleLabel.numberOfLines = 0;
    //    NSString *boundleVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:(__bridge NSString*)kCFBundleVersionKey];
    aboutVersionTitleLabel.text = [NSString stringWithFormat:@"聚数科技成都有限公司 \n"];
    aboutVersionTitleLabel.font = [UIFont systemFontOfSize:12.0f];
    aboutVersionTitleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    aboutVersionTitleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:aboutVersionTitleLabel];
    
    UILabel *aboutVersion = [[UILabel alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(aboutVersionTitleLabel.frame)+5, CGRectGetWidth(self.view.frame)-15-15, 20)];
    aboutVersion.lineBreakMode = NSLineBreakByWordWrapping;
    aboutVersion.numberOfLines = 0;
    NSString *boundleVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:(__bridge NSString*)kCFBundleVersionKey];
    aboutVersion.text = [NSString stringWithFormat:@"%@",boundleVersion];
    aboutVersion.font = [UIFont systemFontOfSize:12.0f];
    aboutVersion.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    aboutVersion.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:aboutVersion];
    
    
    UIButton *aboutUpdateButton = [[UIButton alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(aboutVersionTitleLabel.frame)+45, CGRectGetWidth(self.view.frame), 44)];
    aboutUpdateButton.layer.borderWidth = 0.5f;
    aboutUpdateButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    aboutUpdateButton.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    
    UILabel *aboutUpdateLable = [[UILabel alloc] initWithFrame:CGRectZero];
    aboutUpdateLable.text = getLocalizedString(@"SettingAboutVersionUpdate", nil);
    aboutUpdateLable.font = [UIFont systemFontOfSize:17.0f];
    aboutUpdateLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    aboutUpdateLable.textAlignment = NSTextAlignmentLeft;
    CGSize adjustLabelSize = [CommonFunction labelSizeWithLabel:aboutUpdateLable limitSize:CGSizeMake(1000, 1000)];
    aboutUpdateLable.frame = CGRectMake(15, 11, adjustLabelSize.width, 22);
//    [aboutUpdateButton addSubview:aboutUpdateLable];
    
    UIView *aboutUpdateNotice = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(aboutUpdateLable.frame)+5, (CGRectGetHeight(aboutUpdateButton.frame)-8)/2, 8, 8)];
    aboutUpdateNotice.hidden = YES;
    aboutUpdateNotice.layer.cornerRadius = 4;
    aboutUpdateNotice.layer.masksToBounds = YES;
    aboutUpdateNotice.backgroundColor = [CommonFunction colorWithString:@"fc5043" alpha:1.0f];
//    [aboutUpdateButton addSubview:aboutUpdateNotice];
    
    UILabel *aboutVersionLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    aboutVersionLabel.font = [UIFont systemFontOfSize:14.0f];
    aboutVersionLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    aboutVersionLabel.textAlignment = NSTextAlignmentRight;
    aboutVersionLabel.frame = CGRectMake(CGRectGetMaxX(aboutUpdateNotice.frame)+5, 11, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(aboutUpdateNotice.frame)-5-15, 22);
    [aboutUpdateButton addSubview:aboutVersionLabel];
    [aboutUpdateButton addTarget:self action:@selector(checkClient) forControlEvents:UIControlEventTouchUpInside];
    
//    [self.view addSubview:aboutUpdateButton];
    
    UILabel *aboutPowerLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    aboutPowerLabel.lineBreakMode = NSLineBreakByWordWrapping;
    aboutPowerLabel.numberOfLines = 0;
    aboutPowerLabel.text = @"聚数科技成都有限公司 版权所有 \n Copyright @ 2014-2017 chengdu Storbox. \n All Right Reserved.";
    aboutPowerLabel.font = [UIFont systemFontOfSize:14.0f];
    aboutPowerLabel.textColor = [CommonFunction colorWithString:@"999999" alpha:1.0f];
    aboutPowerLabel.textAlignment = NSTextAlignmentCenter;
    aboutPowerLabel.numberOfLines = 0;
    CGSize adjustPowerSize = [CommonFunction labelSizeWithLabel:aboutPowerLabel limitSize:CGSizeMake(CGRectGetWidth(self.view.frame)-15-15,1000)];
    aboutPowerLabel.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame)-15-15, MAX(20, adjustPowerSize.height));
    aboutPowerLabel.frame = CGRectMake(15, CGRectGetHeight(self.view.frame)-30-CGRectGetHeight(aboutPowerLabel.frame), CGRectGetWidth(self.view.frame)-15-15, CGRectGetHeight(aboutPowerLabel.frame));
    [self.view addSubview:aboutPowerLabel];
    
    UIButton *aboutTermsButton = [[UIButton alloc] initWithFrame:CGRectMake(15, CGRectGetHeight(self.view.frame)-30-CGRectGetHeight(aboutPowerLabel.frame)-8-20, CGRectGetWidth(self.view.frame)-15-15, 20)];
    [aboutTermsButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"SettingAboutPolicy", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be0" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
    [self.view addSubview:aboutTermsButton];
    
    [appDelegate.remoteManager ensureCloudLogin:^{
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.loginBaseUrl];
        [appDelegate.remoteManager.httpService doEntityRequst:nil serviceType:ServiceClientInfo completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"SettingAboutClientInfoFailedPrompt", nil)];
                });
            } else {
                self.clientDownloadPath = [responseObject objectForKey:@"downloadUrl"];
                self.clientVersionInfo = [responseObject objectForKey:@"versionInfo"];
                self.clientVersion = [[self.clientVersionInfo componentsSeparatedByString:@"="] lastObject];
                aboutVersionLabel.text = self.clientVersion;
                if (self.clientVersion && ![boundleVersion isEqual:self.clientVersion]) {
                    aboutUpdateNotice.hidden = NO;
                }
            }
        }];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"SettingAboutClientInfoFailedPrompt", nil)];
        });
    }];
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.mailTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailTitleLabel removeFromSuperview];
    [self.mailBackButton removeFromSuperview];
}

- (void)mailBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)checkClient {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.loginBaseUrl];
        RequestEntity *entity = [[RequestEntity alloc] init];
        [entity setClientType:@"IOS"];
        [entity setClientVersion:self.clientVersion];
        [appDelegate.remoteManager.httpService doEntityRequst:entity serviceType:ServiceClientCheckCode completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"SettingAboutClientCheckFailed", nil)];
                });
            } else {
                NSString *feactureCode = [responseObject objectForKey:@"featurecode"];
                [self clientDownload:^(NSString *clientDestination) {
                    NSString *clientSHA256String = [self fileSha256Hash:clientDestination];
                    if ([feactureCode isEqualToString:clientSHA256String]) {
                        [UIAlertView showAlertViewWithTitle:getLocalizedString(@"SettingAboutClientCheckSuccess", nil) message:getLocalizedString(@"SettingAboutClientUpdatePrompt",nil) cancelButtonTitle:getLocalizedString(@"Cancel",nil) otherButtonTitles:@[getLocalizedString(@"Confirm",nil)] onDismiss:^(int buttonIndex) {
                            [self clientUpdate];
                        } onCancel:^{
                            
                        }];
                    }
                }];
            }
        }];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"SettingAboutClientCheckFailed", nil)];
        });
    }];
}

- (void)clientDownload:(void(^)(NSString *))completion {
    NSURL* docURL = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
    NSString *destination = [docURL.path stringByAppendingPathComponent:@"client"];
    if ([[NSFileManager defaultManager] fileExistsAtPath:destination]) {
        [[NSFileManager defaultManager] removeItemAtPath:destination error:nil];
    }
    
    AFHTTPRequestSerializer *requestSerializer = [AFJSONRequestSerializer serializer];
    NSMutableURLRequest *request = [requestSerializer requestWithMethod:@"GET" URLString:self.clientDownloadPath parameters:nil error:nil];
    AFHTTPRequestOperation *operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    operation.securityPolicy.allowInvalidCertificates = YES;
    operation.outputStream = [NSOutputStream outputStreamToFileAtPath:destination append:YES];
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        completion(destination);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"SettingAboutClientCheckFailed", nil)];
        });
    }];
    [operation start];
}

- (void)clientUpdate {
    NSString *clientPlistPath = [[self.clientDownloadPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:@"ios.plist"];
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"itms-services://?action=download-manifest&url=%@",clientPlistPath]];
    [[UIApplication sharedApplication] openURL:url];
    exit(0);
}

- (NSString*) fileSha256Hash:(NSString*) filePath
{
    // Declare needed variables
    CFStringRef result = NULL;
    CFReadStreamRef readStream = NULL;
    if (!filePath) {
        return nil;
    }
    // Get the file URL
    CFURLRef fileURL =
    CFURLCreateWithFileSystemPath(kCFAllocatorDefault,
                                  (CFStringRef)filePath,
                                  kCFURLPOSIXPathStyle,
                                  (Boolean)false);
    if (!fileURL) goto done;
    
    // Create and open the read stream
    readStream = CFReadStreamCreateWithFile(kCFAllocatorDefault,
                                            (CFURLRef)fileURL);
    if (!readStream) goto done;
    bool didSucceed = (bool)CFReadStreamOpen(readStream);
    if (!didSucceed) goto done;
    
    // Initialize the hash object
    CC_SHA256_CTX hashObject;
    CC_SHA256_Init(&hashObject);
    
    size_t chunkSizeForReadingData = READ_BUFFER_SIZE;
    
    // Feed the data to the hash object
    bool hasMoreData = true;
    
    while (hasMoreData) {
        uint8_t buffer[chunkSizeForReadingData];
        CFIndex readBytesCount = CFReadStreamRead(readStream,
                                                  (UInt8 *)buffer,
                                                  (CFIndex)sizeof(buffer));
        if (readBytesCount == -1) break;
        if (readBytesCount == 0) {
            hasMoreData = false;
            continue;
        }
        CC_SHA256_Update(&hashObject,
                       (const void *)buffer,
                       (CC_LONG)readBytesCount);
    }
    
    // Check if the read operation succeeded
    didSucceed = !hasMoreData;
    
    // Compute the hash digest
    unsigned char digest[CC_SHA256_DIGEST_LENGTH];
    CC_SHA256_Final(digest, &hashObject);
    
    // Abort if the read operation failed
    if (!didSucceed) goto done;
    
    // Compute the string result
    char hash[2 * sizeof(digest) + 1];
    for (size_t i = 0; i < sizeof(digest); ++i) {
        snprintf(hash + (2 * i), 3, "%02x", (int)(digest[i]));
    }
    result = CFStringCreateWithCString(kCFAllocatorDefault,
                                       (const char *)hash,
                                       kCFStringEncodingUTF8);
done:
    
    if (readStream) {
        CFReadStreamClose(readStream);
        CFRelease(readStream);
    }
    if (fileURL) {
        CFRelease(fileURL);
    }
    NSString* sha256 = CFBridgingRelease(result);
    return sha256;
    
}

@end
