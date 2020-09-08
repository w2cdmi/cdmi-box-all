//
//  TransportCameraPhotoUploadTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/11/30.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportCameraPhotoUploadTaskHandle.h"
#import "TransportTask.h"
#import "AppDelegate.h"
#import "File.h"
#import "CloudPreviewController.h"
#import "UIAlertView+Blocks.h"
@implementation TransportCameraPhotoUploadTaskHandle
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
        default:
            break;
    }
}

- (void)doResume {
    if (self.sessionTask && (self.sessionTask.state == NSURLSessionTaskStateSuspended ||
                             self.sessionTask.state == NSURLSessionTaskStateRunning)) {
        [self.sessionTask resume];
    } else {
        [self uploadFileWithNetworkStatus];
    }
}

- (void) cancel {
    if (self.transportTask.taskStatus.integerValue == TaskSucceed) {
        return;
    }
    [super cancel];
    if (self.sessionTask) {
        [self.sessionTask cancel];
    }
}

- (void) suspend {
    [super suspend];
    if (self.sessionTask && self.sessionTask.state == NSURLSessionTaskStateRunning) {
        [self.sessionTask suspend];
    }
}

- (void)uploadFileWithNetworkStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [self uploadPhoto];
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
//            [UIAlertView showAlertViewWithTitle:nil message:NSLocalizedString(@"CloudUploadWIFIPrompt", nil) cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
//                [self uploadPhoto];
//            } onCancel:^{}];
//        } else {
//            [self uploadPhoto];
//        }
//    }
//    [self uploadPhoto];
}

- (void)uploadPhoto {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager.uploadService uploadWithTask:self.transportTask preUpload:^(id obj, NSString *loadPath) {
        if (loadPath) {
            [self.transportTask saveTransportLoadPath:loadPath];
        }
        if ([obj isKindOfClass:[NSString class]]) {
            [self.transportTask saveTransportFileId:obj];
        } else {
            [self.transportTask saveTransportFileId:[[obj objectForKey:@"id"] stringValue]];
        }
    } taskProgress:^(NSURLSessionUploadTask *sessionTask, NSProgress *taskProgress) {
        self.taskProgress = taskProgress;
        self.sessionTask = sessionTask;
        [sessionTask resume];
    } completionHandler:^(AFHTTPRequestOperation *operation, NSError *error) {
        if (error) {
            NSLog(@"%@",error);
            [self failed];
            if (self.taskProgress) {
                [self.transportTask saveTransportFraction:(float)self.taskProgress.fractionCompleted];
            }
        } else {
            self.transportTask.taskLoadPath = nil;
            self.transportTask.file.fileModifiedDate = [NSDate date];
            NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
            NSString *fileDataLocalPath = [self.transportTask.file fileDataLocalPath];
            if ([self hadCacheFile:fileCacheLocalPath]) {
                NSString *outFilePath = [fileCacheLocalPath stringByAppendingString:@".tmp"];
                if ([[NSFileManager defaultManager] fileExistsAtPath:outFilePath]) {
                    [[NSFileManager defaultManager] removeItemAtPath:outFilePath error:nil];
                }
                [self savePath:fileCacheLocalPath toPath:fileDataLocalPath];
            }

            if ([CloudPreviewController isSupportedImage:fileDataLocalPath]) {
                [self.transportTask.file fileCompressImage];
            }
            
            [self success];
        }
    }];
}

- (BOOL) hadCacheFile:(NSString*)cachePath {
    BOOL isDirectory;
    BOOL fileExist;
    if (cachePath) {
        fileExist = [[NSFileManager defaultManager] fileExistsAtPath:cachePath isDirectory:&isDirectory];
        return fileExist && !isDirectory;
    } else {
        return NO;
    }
}
@end
