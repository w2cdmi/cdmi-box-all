//
//  TransportBackUpAssetUploadTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/11/27.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportBackUpAssetUploadTaskHandle.h"
#import "AppDelegate.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <AVFoundation/AVFoundation.h>
#import <AssetsLibrary/ALAsset.h>
#import "File.h"
#import "TransportTask.h"
#import "Asset.h"
#import "CloudPreviewController.h"
#import "UIAlertView+Blocks.h"
@interface TransportBackUpAssetUploadTaskHandle ()

@property (nonatomic, strong) ALAsset *asset;
@property (nonatomic, strong) NSURL *assetUrl;
@property (nonatomic, strong) AVAssetExportSession* exportSession;
@property (atomic) BOOL assetExporting;
@property (atomic) BOOL taskCanceled;

@end
@implementation TransportBackUpAssetUploadTaskHandle

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
        if (self.asset) {
            [self getAssetCache:^{
                [self uploadFileWithNetworkStatus];
            }];
        } else {
            NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
            if ([self hadCacheFile:fileCacheLocalPath]) {
                [self uploadFileWithNetworkStatus];
            } else {
                [self getUploadAsset:^{
                    [self uploadFileWithNetworkStatus];
                }];
            }
        }
    }
}

- (void) getUploadAsset:(void(^)())getAssetCompletionHandle {
    NSURL *assetUrl;
    if (self.transportTask.file.fileAlbumUrl) {
        assetUrl = [NSURL URLWithString:self.transportTask.file.fileAlbumUrl];
    }
    if (self.transportTask.file.relationAsset) {
        assetUrl = [NSURL URLWithString:self.transportTask.file.relationAsset.assetUrl];
    }
    if (assetUrl) {
        ALAssetsLibrary* lib = [[ALAssetsLibrary alloc] init];
        [lib assetForURL:assetUrl resultBlock:^(ALAsset *asset) {
            if (asset) {
                self.asset = asset;
                [self getAssetCache:^{
                    getAssetCompletionHandle();
                }];
            } else {
                [self failed];
            }
        } failureBlock:^(NSError *error) {
            [self failed];
        }];
    } else {
        [self failed];
    }
}

- (void) getAssetCache:(void(^)())getCacheCompletionHandle {
    [self exportAsset:self.asset completionHandler:^(NSError * error) {
        self.assetExporting = NO;
        self.asset = nil;
        if (error) {
            if ([error.domain isEqual:AVFoundationErrorDomain] && error.code
                == -11853) {
                [self cancel];
            } else {
                if (error.code == AVErrorOperationInterrupted) {
                    [self resume];
                } else {
                    [self failed];
                }
            }
        } else {
            NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
            if (![self hadCacheFile:fileCacheLocalPath]) {
                [self failed];
            } else {
                getCacheCompletionHandle();
            }
        }
    }];
}

- (void) cancel {
    _taskCanceled = YES;
    if (self.transportTask.taskStatus.integerValue == TaskSucceed) {
        return;
    }
    [super cancel];
    if (self.sessionTask) {
        [self.sessionTask cancel];
    }
    if (self.exportSession) {
        [self.exportSession cancelExport];
    }
}

- (void) suspend {
    [super suspend];
    if (self.sessionTask && self.sessionTask.state == NSURLSessionTaskStateRunning) {
        [self.sessionTask suspend];
    }
    if (self.exportSession) {
        [self.exportSession cancelExport];
    }
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

- (void)uploadFileWithNetworkStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [self uploadAsset];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [self failed];
        [self backUpAssetFailed];
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
//                [self uploadAsset];
//            } onCancel:^{}];
//        } else {
//            [self uploadAsset];
//        }
//    }
//    [self uploadAsset];
}

- (void) uploadAsset {
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
            [self backUpAssetFailed];
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
            
            if ([CloudPreviewController isSupportedImage:self.transportTask.file.fileName]) {
                [self.transportTask.file fileCompressImage];
            }
            
            [self success];
            [self backUpAssetSuccess];
        }
    }];
}

/*从本地图像集中导出文件，如果是视频文件，会先进行最大不失真的压缩*/
- (void) exportAsset:(ALAsset*) asset completionHandler:(void (^)(NSError*))completionHandler
{
    if (self.assetExporting) {
        return;
    }
    self.assetExporting = YES;
    ALAssetRepresentation *assetRep = [asset defaultRepresentation];
    NSString* assetType = [asset valueForProperty:ALAssetPropertyType];
    NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
    NSError* error = nil;
    
    if ([[NSFileManager defaultManager] fileExistsAtPath:fileCacheLocalPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:fileCacheLocalPath error:&error];
        if (error) {
            completionHandler(error);
            return;
        }
    }
    [[NSFileManager defaultManager] createFileAtPath:fileCacheLocalPath contents:nil attributes:nil];
    
    if ([assetType isEqual:ALAssetTypePhoto]) {
        NSFileHandle* handler = [NSFileHandle fileHandleForWritingToURL:[NSURL URLWithString:fileCacheLocalPath] error:&error];
        if (error) {
            completionHandler(error);
            return;
        }
        uint8_t buffer[READ_BUFFER_SIZE] = {0};
        long long offset = 0;
        NSUInteger nRead = 0;
        do {
            nRead = [assetRep getBytes:buffer fromOffset:offset length:(sizeof(buffer) / sizeof(uint8_t)) error:&error];
            offset += nRead;
            [handler writeData:[NSData dataWithBytes:buffer length:nRead]];
            if (error) {
                completionHandler(error);
                [handler closeFile];
                return;
            }
        } while(nRead > 0 && !self.taskCanceled);
        [handler closeFile];
        completionHandler(nil);
    } else {
        AVURLAsset* avAsset = [AVURLAsset assetWithURL:assetRep.url];
        NSArray* compatiblePresets = [AVAssetExportSession exportPresetsCompatibleWithAsset:avAsset];
        NSString* quality = nil;
        
        if (![compatiblePresets containsObject:AVAssetExportPresetMediumQuality]) {
            quality = [compatiblePresets lastObject];
        } else {
            quality = AVAssetExportPresetMediumQuality;
        }
        
        if (quality) {
            self.exportSession = [[AVAssetExportSession alloc] initWithAsset:avAsset presetName:quality];
            self.exportSession.outputURL = [NSURL fileURLWithPath:fileCacheLocalPath];
            self.exportSession.outputFileType = AVFileTypeQuickTimeMovie;
            if ([[NSFileManager defaultManager] fileExistsAtPath:fileCacheLocalPath]) {
                [[NSFileManager defaultManager] removeItemAtPath:fileCacheLocalPath error:nil];
            }
            __weak __block typeof(self) weakSelf = self;
            [self.exportSession exportAsynchronouslyWithCompletionHandler:^{
                __strong typeof(self) strongSelf = weakSelf;
                switch (strongSelf.exportSession.status) {
                    case AVAssetExportSessionStatusCompleted:
                        completionHandler(nil);
                        break;
                    case AVAssetExportSessionStatusCancelled:
                        completionHandler( [NSError errorWithDomain:AVFoundationErrorDomain code:-11853 userInfo:@{NSLocalizedDescriptionKey:@"Opearion cancelled"}]);
                        break;
                    case AVAssetExportSessionStatusFailed:
                        completionHandler(strongSelf.exportSession.error);
                        break;
                    default:
                        completionHandler(strongSelf.exportSession.error);
                        break;
                }
            }];
        } else {
            error = [NSError errorWithDomain:AVFoundationErrorDomain code:AVErrorOperationNotSupportedForAsset userInfo:@{NSLocalizedDescriptionKey:@"No quality found for export!"}];
            completionHandler(error);
            return;
        }
    }
}


- (void)backUpAssetSuccess {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlock:^{
        Asset *shadow = (Asset*)[ctx objectWithID:self.transportTask.file.relationAsset.objectID];
        shadow.assetUploadFlag = @(1);
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Onebox.backup.success" object:nil];
        [ctx save:nil];
    }];
}

- (void)backUpAssetFailed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlock:^{
        Asset *shadow = (Asset*)[ctx objectWithID:self.transportTask.file.relationAsset.objectID];
        shadow.assetBackUpFailedFlag = @(1);
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Onebox.backup.failed" object:nil];
        [ctx save:nil];
    }];
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

