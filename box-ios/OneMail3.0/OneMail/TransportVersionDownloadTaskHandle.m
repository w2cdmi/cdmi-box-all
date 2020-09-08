//
//  TransportVersionDownloadTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/11/19.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportVersionDownloadTaskHandle.h"
#import "TransportTask.h"
#import "File.h"
#import "AppDelegate.h"
#import <CommonCrypto/CommonCrypto.h>
#import "CloudPreviewController.h"
#import "UIAlertView+Blocks.h"
@implementation TransportVersionDownloadTaskHandle
- (void) resume {
    switch (self.transportTask.taskStatus.integerValue) {
        case TaskInitialing:
        case TaskRunning:
        case TaskSuspend:
        case TaskWaitting:
        case TaskFailed:
            [self doResume];
            break;
        default:
            break;
    }
}

- (void) doResume {
    if (self.requestOperation) {
        [self.requestOperation resume];
    } else {
        [self versionDownloadWithTask];
    }
}

- (void)suspend {
    [super suspend];
}

- (void)cancel {
    [super cancel];
}

-(void)versionDownloadWithTask {
    NSString *versionDataLocalPath = [self.transportTask.version versionDataLocalPath];
    if (versionDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:versionDataLocalPath]) {
        [self success];
        self.taskFraction = 1.0f;
        return;
    }
    [self downloadVersionWithNetworkStatus];
}


- (void)downloadVersionWithNetworkStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [self downloadVersion];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [self failed];
    }];
//    BOOL hasNetWork = appDelegate.hasNetwork;
//    if (!hasNetWork) {
//        dispatch_async(dispatch_get_main_queue(), ^{
//            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudNoneNetworkPrompt", nil)];
//        });
//    } else {
//        BOOL WiFiNetWork = appDelegate.wifiNetwork;
//        UserSetting *userSetting = [UserSetting defaultSetting];
//        if (!WiFiNetWork && userSetting.cloudAssetBackupWifi.integerValue == 1) {
//            [UIAlertView showAlertViewWithTitle:nil message:NSLocalizedString(@"CloudDownloadWIFIPrompt", nil) cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
//                [self downloadVersion];
//            } onCancel:^{}];
//        } else {
//            [self downloadVersion];
//        }
//    }
//    [self downloadVersion];
}

- (void) downloadVersion {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager.downloadService downloadVersionWithTask:self.transportTask taskProgress:^(AFHTTPRequestOperation *downloadOperation, NSProgress *taskProgress) {
        self.requestOperation = downloadOperation;
        self.taskProgress = taskProgress;
    } completionHandler:^(NSURLResponse *response, NSError *error) {
        if (error) {
            NSLog(@"%@",error);
            [self failed];
            if (self.taskProgress) {
                [self.transportTask saveTransportFraction:(float)self.taskProgress.fractionCompleted];
            }
        } else {
            NSString *versionCacheLocalPath = [self.transportTask.version versionCacheLocalPath];
            NSString *versionDataLocalPath = [self.transportTask.version versionDataLocalPath];
            if ([self hadCacheFile:versionCacheLocalPath]) {
                [self savePath:versionCacheLocalPath toPath:versionDataLocalPath];
            }
            
            if ([CloudPreviewController isSupportedImage:versionDataLocalPath]) {
                [self.transportTask.version versionCompressImage];
            }
            
            [self success];
        }
    }];
}

@end
