//
//  TransportDataDownloadTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/11/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportDataDownloadTaskHandle.h"
#import "TransportTask.h"
#import "File.h"
#import "AppDelegate.h"
#import <CommonCrypto/CommonCrypto.h>
#import "CloudPreviewController.h"
#import "UIAlertView+Blocks.h"
@implementation TransportDataDownloadTaskHandle

- (void) resume {
    [self initting];
    switch (self.transportTask.taskStatus.integerValue) {
        case TaskInitialing:
        case TaskRunning:
        case TaskSuspend:
        case TaskWaitting:
        case TaskFailed:
            [self doResume];
            break;
        case TaskCancel:
            if (self.transportTask.taskRecoverable.integerValue == 1) {
                [self doResume];
            }
        default:
            break;
    }
    
}

- (void) doResume {
    [super resume];
    if (self.requestOperation) {
        [self.requestOperation resume];
    } else {
        [self fileDownloadWithTask];
    }
}

- (void)suspend {
    [super suspend];
    NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
    if (fileCacheLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileCacheLocalPath]) {
        NSDictionary *fileAttribute = [[NSFileManager defaultManager] attributesOfItemAtPath:fileCacheLocalPath error:nil];
        if (!fileAttribute) {
            [self failed];
        } else {
            [self.requestOperation pauseWithOffset:[NSNumber numberWithUnsignedLongLong:[fileAttribute fileSize]]];
        }
    }
}

- (void)cancel {
    [super cancel];
    [self.requestOperation cancel];
}

-(void)fileDownloadWithTask {
    NSString *fileDataLocalPath = [self.transportTask.file fileDataLocalPath];
    if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
        [self success];
        self.taskFraction = 1.0f;
        return;
    }
    [self downloadFileWithNetworkStatus];
}

- (void)downloadFileWithNetworkStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [self downloadFile];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [self failed];
    }];
//    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
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
//                [self downloadFile];
//            } onCancel:^{}];
//        } else {
//            [self downloadFile];
//        }
//    }
//    [self downloadFile];
}

- (void) downloadFile {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager.downloadService downloadWithTask:self.transportTask taskProgress:^(AFHTTPRequestOperation *downloadOperation, NSProgress *taskProgress) {
        self.requestOperation = downloadOperation;
        self.taskProgress = taskProgress;
    } completionHandler:^(NSURLResponse *response, NSError *error) {
        NSLog(@"%@%@",response,error);
        if (error) {
            
            if (self.taskProgress) {
                [self.transportTask saveTransportFraction:(float)self.taskProgress.fractionCompleted];
            }
            if (self.transportTask.taskStatus.integerValue != TaskCancel) {
                [self failed];
            }
        } else {
            NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
            NSString *fileDataLocalPath = [self.transportTask.file fileDataLocalPath];
            if ([self hadCacheFile:fileCacheLocalPath]) {
                [self savePath:fileCacheLocalPath toPath:fileDataLocalPath];
            }
            
            if ([CloudPreviewController isSupportedImage:fileDataLocalPath]) {
                [self.transportTask.file fileCompressImage];
            }
            
            [self success];
        }
    }];
}

- (NSURL*) resumeFileURL:(NSURL*) requestUrl {
    if (!requestUrl) {
        return nil;
    }
    NSString* urlStr = requestUrl.absoluteString;
    NSFileManager* fileManager =[NSFileManager defaultManager];
    NSURL* userCache  = [[fileManager URLsForDirectory:NSCachesDirectory inDomains:NSUserDomainMask] lastObject];
    if (userCache) {
        return [userCache URLByAppendingPathComponent:[self hashFileName:urlStr] isDirectory:NO];
    }
    return nil;
}

- (NSString*) hashFileName:(NSString*) str {
    const char* cstr = [str UTF8String];
    static unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5(cstr, (CC_LONG)strlen(cstr), digest);
    NSMutableString* outMd5 = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH];
    for (int i=0; i<CC_MD5_DIGEST_LENGTH; ++i) {
        [outMd5 appendFormat:@"%02x", digest[i]];
    }
    [outMd5 appendString:@".tmp"];
    return outMd5;
}

@end
