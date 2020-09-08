//
//  TransportTask.h
//  OneMail
//
//  Created by cse  on 15/10/26.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
typedef enum : NSUInteger {
    TaskFileDownload = 1,
    TaskFolderDownload,
    TaskFilePreview,
    TaskVersionDownload,
    TaskAssetUpload,
    TaskAssetBackUpload,
    TaskAttachmentUpload,
    TaskCamerPhotoUpload,
    TaskLocalAttachmentUpload,
} TransportTaskType;

typedef enum : NSUInteger {
    TaskSucceed = 1,
    TaskRunning,
    TaskInitialing,
    TaskWaitting,
    TaskWaitNetwork,
    TaskSuspend,
    TaskCancel,
    TaskFailed,
} TransportTaskStatus;

@class File;
@class TransportTaskHandle;
@class Version;
@interface TransportTask : NSManagedObject

@property (nonatomic, retain) NSDate * taskCreatedDate;
@property (nonatomic, retain) NSNumber * taskStatus;
@property (nonatomic, retain) NSNumber * taskType;
@property (nonatomic, retain) NSNumber * taskRecoverable;
@property (nonatomic, retain) NSString * taskLoadPath;
@property (nonatomic, retain) NSNumber * taskFraction;
@property (nonatomic, retain) NSString * taskOwner;
@property (nonatomic, retain) File *file;
@property (nonatomic, retain) Version *version;
@property (nonatomic, strong) TransportTaskHandle *taskHandle;

- (void) remove;
+ (TransportTask*) taskInsertWithFile:(File*)file type:(NSInteger)taskType recoverable:(BOOL)recoverable force:(BOOL)force;
+ (TransportTask*) taskInsertWithVersion:(Version*)version type:(NSInteger)taskType;

+ (NSArray*) getAllTaskWithTaskStatus:(NSNumber*)taskStatus ctx:(NSManagedObjectContext*)ctx;

- (void)changeTransportStatus:(TransportTaskStatus)taskStatus;
- (void)saveTransportFileId:(NSString*)fileId;
- (void)saveTransportLoadPath:(NSString*)path;
- (void)saveTransportFraction:(float)fraction;

- (NSString*)stringWithStatus:(TransportTaskStatus)taskStatus;
@end
