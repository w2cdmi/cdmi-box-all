//
//  TransportAttachmentUploadTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportAttachmentUploadTaskHandle.h"
#import "Attachment.h"
#import "TransportTask.h"
#import "File.h"
#import "AppDelegate.h"
#import "CloudPreviewController.h"
#import "UIAlertView+Blocks.h"
#import "UIImage+fixOrientation.h"
@interface TransportAttachmentUploadTaskHandle ()

@property (nonatomic, strong) Attachment *attachment;

@end

@implementation TransportAttachmentUploadTaskHandle

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

- (void) doResume {
    if (self.sessionTask && (self.sessionTask.state == NSURLSessionTaskStateSuspended ||
                             self.sessionTask.state == NSURLSessionTaskStateRunning)) {
        [self.sessionTask resume];
    } else {
        self.attachment = [Attachment getAttachmentWithAttachmentId:self.transportTask.file.fileAttachmentId ctx:nil];
        NSString *attachmentDataLocalPath = [self.attachment attachmentDataLocalPath];
        if ([self hadCacheFile:attachmentDataLocalPath]) {
            NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
            if (fileCacheLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileCacheLocalPath]) {
                [[NSFileManager defaultManager] removeItemAtPath:fileCacheLocalPath error:nil];
            }
            [[NSFileManager defaultManager] copyItemAtPath:attachmentDataLocalPath toPath:fileCacheLocalPath error:nil];
            [self uploadFileWithNetworkStatus];
        } else {
            return;
        }
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
//        if (!appDelegate.wifiNetwork && [UserSetting defaultSetting].cloudWiFiPrompt.boolValue) {
//            [UIAlertView showAlertViewWithTitle:nil message:NSLocalizedString(@"CloudUploadWIFIPrompt", nil) cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
//                [self imageFixOrientation:^{
//                    if ([self hadCacheFile:[self.transportTask.file fileCacheLocalPath]]) {
//                        [self uploadAttachment];
//                    }
//                }];
//            } onCancel:^{
//                [self waitNetwork];
//            }];
//        } else {
            [self imageFixOrientation:^{
                if ([self hadCacheFile:[self.transportTask.file fileCacheLocalPath]]) {
                    [self uploadAttachment];
                }
            }];
        //}
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [self failed];
    }];
}

- (void) uploadAttachment {
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

- (void)imageFixOrientation:(void(^)())block {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_LOW, 0), ^{
        File *file = self.transportTask.file;
        if ([CommonFunction isImageResource:file.fileName]) {
            UIImage *image = [UIImage imageWithContentsOfFile:file.fileCacheLocalPath];
            image = [image fixOrientation];
            NSData *date = UIImageJPEGRepresentation(image, 1);
            [[NSFileManager defaultManager] removeItemAtPath:file.fileCacheLocalPath error:nil];
            [[NSFileManager defaultManager] createFileAtPath:file.fileCacheLocalPath contents:date attributes:nil];
            if (block) {
                block();
            }
        } else {
            if (block) {
                block();
            }
        }
    });
}

- (BOOL) isPhoto {
    NSString* extension = [[self.transportTask.file.fileName pathExtension] lowercaseString];
    if ([extension isEqual:@"jpeg"]||[extension isEqual:@"jpg"]||
        [extension isEqual:@"png"]||[extension isEqual:@"bmp"]||
        [extension isEqual:@"gif"]||[extension isEqual:@"tiff"]||
        [extension isEqual:@"raw"]||[extension isEqual:@"ppm"]||
        [extension isEqual:@"pgm"]||[extension isEqual:@"pbm"]||
        [extension isEqual:@"pnm"]||[extension isEqual:@"webp"]) {
        return YES;
    }
    return NO;
}

@end
