//
//  TransportTask.m
//  OneMail
//
//  Created by cse  on 15/10/26.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportTask.h"
#import "File.h"
#import "Version.h"
#import "AppDelegate.h"
#import "TransportAttachmentUploadTaskHandle.h"
#import "TransportAssetUploadTaskHandle.h"
#import "TransportDataDownloadTaskHandle.h"
#import "TransportFolderDownloadTaskHandle.h"
#import "TransportFilePreviewTaskHandle.h"
#import "TransportLocalAttachmentUploadTaskHandle.h"
#import "TransportVersionDownloadTaskHandle.h"
#import "TransportCameraPhotoUploadTaskHandle.h"
#import "TransportBackUpAssetUploadTaskHandle.h"

@implementation TransportTask

@dynamic taskCreatedDate;
@dynamic taskStatus;
@dynamic taskType;
@dynamic taskRecoverable;
@dynamic taskLoadPath;
@dynamic taskFraction;
@dynamic taskOwner;
@dynamic file;
@dynamic version;
@synthesize taskHandle=_taskHandle;

- (void) remove {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = [appDelegate.localManager managedObjectContext];
    TransportTask *mainSelf = (TransportTask*)[ctx objectWithID:self.objectID];
    [mainSelf.taskHandle cancel];
    NSLog(@"%@ transportTask delete",mainSelf.file.fileName);
    [mainSelf.file deleteFileTransportTask];
    [self.managedObjectContext deleteObject:self];
}

- (TransportTaskHandle*) taskHandle {
    [self willAccessValueForKey:@"taskHandle"];
    if (nil == _taskHandle) {
        if (self.taskType.integerValue == TaskAssetUpload) {
            _taskHandle = [[TransportAssetUploadTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskAttachmentUpload) {
            _taskHandle = [[TransportAttachmentUploadTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskFileDownload) {
            _taskHandle = [[TransportDataDownloadTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskFolderDownload) {
            _taskHandle = [[TransportFolderDownloadTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskFilePreview) {
            _taskHandle = [[TransportFilePreviewTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskLocalAttachmentUpload) {
            _taskHandle = [[TransportLocalAttachmentUploadTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskVersionDownload) {
            _taskHandle = [[TransportVersionDownloadTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskAssetBackUpload) {
            _taskHandle = [[TransportBackUpAssetUploadTaskHandle alloc] initWithTranportTask:self];
        }
        if (self.taskType.integerValue == TaskCamerPhotoUpload) {
            _taskHandle = [[TransportCameraPhotoUploadTaskHandle alloc] initWithTranportTask:self];
        }
        AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
        if (self.managedObjectContext != appDelegate.localManager.managedObjectContext) {
            [SNLog Log:LError :@"Task handle create from non-main context %@", self.file.fileName];
        }
    }
    [self didAccessValueForKey:@"taskHandle"];
    return _taskHandle;
}

+ (TransportTask*) taskInsertWithFile:(File*)file type:(NSInteger)taskType recoverable:(BOOL)recoverable force:(BOOL)force {
    if (!file) return nil;
    if (file.transportTask) {
        if (file.transportTask.taskType.integerValue == taskType) {
            file.transportTask.taskRecoverable = @(recoverable);
            file.transportTask.taskCreatedDate = [NSDate date];
            if (!force) {
                file.transportTask.taskStatus = @(TaskWaitNetwork);
            }
            return file.transportTask;
        } else {
            [file.transportTask remove];
            file.transportTask = nil;
        }
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    TransportTask *transportTask = [NSEntityDescription insertNewObjectForEntityForName:@"TransportTask" inManagedObjectContext:file.managedObjectContext];
    transportTask.taskCreatedDate = [NSDate date];
    if (!force) {
        transportTask.taskStatus = @(TaskWaitNetwork);
    } else {
        transportTask.taskStatus = @(TaskWaitting);
    }
    transportTask.taskType = @(taskType);
    transportTask.taskFraction = @(0);
    transportTask.taskRecoverable = @(recoverable);
    transportTask.taskOwner = appDelegate.localManager.userCloudId;
    transportTask.file = file;
    file.transportTask = transportTask;
    return transportTask;
}

+ (TransportTask*) taskInsertWithVersion:(Version*)version type:(NSInteger)taskType {
    if (!version) return nil;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (version.transportTask) {
        if (version.transportTask.taskType.integerValue == TaskAttachmentUpload) {
            return version.transportTask;
        } else {
            [version.transportTask remove];
            version.transportTask = nil;
        }
    }
    TransportTask *transportTask = [NSEntityDescription insertNewObjectForEntityForName:@"TransportTask" inManagedObjectContext:version.managedObjectContext];
    transportTask.taskCreatedDate = [NSDate date];
    transportTask.taskStatus = @(TaskWaitting);
    transportTask.taskType = @(taskType);
    transportTask.taskFraction = @(0);
    transportTask.taskRecoverable = @(1);
    transportTask.taskOwner = appDelegate.localManager.userCloudId;
    transportTask.version = version;
    version.transportTask = transportTask;
    return transportTask;
}

- (NSString*)stringWithStatus:(TransportTaskStatus)taskStatus {
    if (taskStatus == 1) {
        return @"running";
    } else if (taskStatus == 2) {
        return @"initing";
    } else if (taskStatus == 3) {
        return @"waitting";
    } else if (taskStatus == 4) {
        return @"suspend";
    } else if (taskStatus == 5) {
        return @"success";
    } else if (taskStatus == 6) {
        return @"cancel";
    } else {
        return @"failed";
    }
}

+ (NSArray*)getAllTaskWithTaskStatus:(NSNumber *)taskStatus ctx:(NSManagedObjectContext *)ctx {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!ctx) {
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"taskStatus = %@ AND taskRecoverable = %@ AND taskOwner = %@",taskStatus,@(1),appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"TransportTask" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"taskCreatedDate" ascending:YES];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    return [ctx executeFetchRequest:request error:nil];
}

- (void)changeTransportStatus:(TransportTaskStatus)taskStatus {
    if (self.isFault) {
        return;
    }
    if (self.taskStatus.integerValue == taskStatus) {
        return;
    }
    self.taskStatus = @(taskStatus);
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (taskStatus == TaskSucceed && [self.file isFolder]) {
        NSString *folderDataLocalPath = [self.file fileDataLocalPath];
        BOOL isDirectory;
        if (![[NSFileManager defaultManager] fileExistsAtPath:folderDataLocalPath isDirectory:&isDirectory] || !isDirectory) {
            [[NSFileManager defaultManager] createDirectoryAtPath:folderDataLocalPath withIntermediateDirectories:YES attributes:nil error:nil];
        }
    }
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        TransportTask *shadow = (TransportTask*)[ctx objectWithID:self.objectID];
        shadow.taskStatus = @(taskStatus);
        shadow.taskCreatedDate = [NSDate date];
        [ctx save:nil];
    }];
}

- (void)saveTransportFileId:(NSString*)fileId {
    if (self.file.fileId == fileId) {
        return;
    }
    self.file.fileId = fileId;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        TransportTask *shadow = (TransportTask*)[ctx objectWithID:self.objectID];
        shadow.file.fileId = fileId;
        shadow.file.fileModifiedDate = [NSDate date];
        [ctx save:nil];
    }];
}

- (void)saveTransportLoadPath:(NSString*)path {
    if (self.taskLoadPath == path) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        TransportTask *shadow = (TransportTask*)[ctx objectWithID:self.objectID];
        shadow.taskLoadPath = path;
        [ctx save:nil];
    }];
}

- (void)saveTransportFraction:(float)fraction {
    if (self.isFault) {
        return;
    }
    if ([self.taskFraction doubleValue] == fraction) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        TransportTask *shadow = (TransportTask*)[ctx objectWithID:self.objectID];
        shadow.taskFraction = [NSNumber numberWithFloat:fraction];
        [ctx save:nil];
    }];
}

@end
