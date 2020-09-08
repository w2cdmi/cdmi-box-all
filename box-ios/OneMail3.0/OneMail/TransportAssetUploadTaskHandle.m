//
//  TransportAssetUploadTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportAssetUploadTaskHandle.h"
#import "AppDelegate.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <AVFoundation/AVFoundation.h>
#import <AssetsLibrary/ALAsset.h>
#import "File.h"
#import "TransportTask.h"
#import "CloudPreviewController.h"
#import "UIAlertView+Blocks.h"
#import "UIImage+fixOrientation.h"
#import "FileMD5Hash.h"

@interface TransportAssetUploadTaskHandle ()

@property (nonatomic, strong) ALAsset *asset;
@property (nonatomic, strong) NSURL *assetUrl;
@property (nonatomic, strong) AVAssetExportSession* exportSession;
@property (atomic) BOOL assetExporting;
@property (atomic) BOOL taskCanceled;

@end
@implementation TransportAssetUploadTaskHandle

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

- (void) cancel {
    if (self.transportTask.taskStatus.integerValue == TaskSucceed) {
        return;
    }
    _taskCanceled = YES;
    [super cancel];
    if (self.sessionTask) {
        [self.sessionTask cancel];
    }
    if (self.exportSession) {
        [self.exportSession cancelExport];
    }
}

- (void) suspend {
    if (self.transportTask.taskStatus.integerValue == TaskSucceed) {
        return;
    }
    [super suspend];
    if (self.sessionTask && self.sessionTask.state == NSURLSessionTaskStateRunning) {
        [self.sessionTask suspend];
    }
    if (self.exportSession) {
        [self.exportSession cancelExport];
    }
}

- (void) getUploadAsset:(void(^)())getAssetCompletionHandle {
    NSURL *assetUrl = [NSURL URLWithString:self.transportTask.file.fileAlbumUrl];
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
        NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
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
            [[NSFileManager defaultManager] removeItemAtPath:fileCacheLocalPath error:nil];
        } else {
            if (![self hadCacheFile:fileCacheLocalPath]) {
                [self failed];
            } else {
                getCacheCompletionHandle();
            }
        }
    }];
}

- (void)uploadFileWithNetworkStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [self imageFixOrientation:^{
            NSString *fileCacheLocalPath = [self.transportTask.file fileCacheLocalPath];
            if ([self hadCacheFile:fileCacheLocalPath]) {
                NSString *fileMD5 = [FileMD5Hash computeMD5HashOfFileInPath:fileCacheLocalPath];
                [self.transportTask.file saveFileMD5:fileMD5];
                [self uploadAsset];
            }
        }];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [self failed];
    }];
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
            if ([CloudPreviewController isSupportedImage:self.transportTask.file.fileName]) {
                [self.transportTask.file fileCompressImage];
            }
            [self success];
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
    if (fileCacheLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileCacheLocalPath]) {
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

- (void)imageFixOrientation:(void(^)())block {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_LOW, 0), ^{
        File *file = self.transportTask.file;
        if ([CommonFunction isImageResource:file.fileName]) {
            UIImage *image = [UIImage imageWithContentsOfFile:file.fileCacheLocalPath];
            image = [image fixOrientation];
            NSData *date = UIImageJPEGRepresentation(image, 0.8);
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
