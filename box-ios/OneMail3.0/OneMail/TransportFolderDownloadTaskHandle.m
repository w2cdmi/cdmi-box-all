//
//  TransportFolderDownloadTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/12/21.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportFolderDownloadTaskHandle.h"
#import "TransportTask.h"
#import "File.h"
#import "File+Remote.h"
#import "TransportFolderDownloadOperation.h"

@interface TransportFolderDownloadTaskHandle ()

@property int64_t currentTaskCompletedUnitCount;
@property (nonatomic, strong) TransportTaskHandle *currentTaskHandle;
@property (nonatomic, strong) File *deleteTaskFile;
@property (nonatomic, strong) TransportFolderDownloadOperation  *downloadOperation;

@end

@implementation TransportFolderDownloadTaskHandle

- (void)dealloc {
    self.downloadOperation = nil;
    NSLog(@"%@ taskHandle dealloc",self.transportTask.file.fileName);
    
}

- (void) resume {
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

- (void)doResume {
    [super resume];
    [self.transportTask.taskHandle running];
    if (!self.downloadOperation) {
        self.downloadOperation = [[TransportFolderDownloadOperation alloc]initWithFile:self.transportTask.file];
    }
    self.downloadOperation.fetchController.delegate = self.downloadOperation;
    [self.downloadOperation standBy];
    if (!self.taskProgress) {
        self.taskProgress = [[NSProgress alloc] init];
    }
    NSArray *subItems = [self.transportTask.file subItems];
    if (subItems.count == 0) {
        NSLog(@"%@任务下无子任务，任务成功",self.transportTask.file.fileName);
        [self success];
        return;
    }
    self.taskTotalUnitCount = [self totalUnitCountWithFile:self.transportTask.file];
    [self addObserver:self forKeyPath:@"taskTotalUnitCount" options:NSKeyValueObservingOptionNew context:nil];
    self.taskCompleteUnitCount = 0;
    for (File *subItem in subItems) {
        if ([subItem isFile]) {
            [self fileDownloadTaskAdd:subItem];
        } else {
            [self folderDownloadTaskAdd:subItem];
        }
    }
    NSLog(@"%@所有子任务添加完成",self.transportTask.file.fileName);
    [self.downloadOperation begin];
    [self.taskProgress setTotalUnitCount:self.taskTotalUnitCount];
    [self.taskProgress setCompletedUnitCount:self.taskCompleteUnitCount];
}

- (void)fileDownloadTaskAdd:(File*)file {
    if (!file.transportTask) {
        TransportTask *transportTask = [file downloadVisiable:NO force:YES];
        if (!transportTask) {
            self.taskTotalUnitCount = self.taskTotalUnitCount - file.fileSize.longLongValue;
        } else {
            NSLog(@"%@添加子任务%@",self.transportTask.file.fileName,file.fileName);
            [transportTask.taskHandle waiting];
        }
    } else {
        NSLog(@"%@子任务%@已存在",self.transportTask.file.fileName,file.fileName);
        if (file.transportTask.taskStatus.integerValue == TaskCancel) {
            self.taskTotalUnitCount = self.taskTotalUnitCount - file.fileSize.longLongValue;
            return;
        }
        if (file.transportTask.taskStatus.integerValue != TaskSucceed) {
            if (file.transportTask.taskHandle.taskProgress) {
                self.taskCompleteUnitCount = self.taskCompleteUnitCount + file.transportTask.taskHandle.taskProgress.completedUnitCount;
            } else {
                self.taskCompleteUnitCount = self.taskCompleteUnitCount + (unsigned long long)(file.transportTask.taskFraction.floatValue*file.fileSize.longLongValue);
            }
        } else {
            self.taskCompleteUnitCount = self.taskCompleteUnitCount + file.fileSize.longLongValue;
        }
    }
    if (file.transportTask.taskStatus.integerValue == TaskSuspend ||
        file.transportTask.taskStatus.integerValue == TaskFailed) {
        [file.transportTask.taskHandle waiting];
    }
}

- (void)folderDownloadTaskAdd:(File*)file {
    if (!file.transportTask) {
        TransportTask *transportTask = [file downloadVisiable:NO force:YES];
        if (!transportTask) {
            self.taskTotalUnitCount = self.taskTotalUnitCount - [self totalUnitCountWithFile:file];
            return;
        } else {
            NSLog(@"%@添加子任务%@",self.transportTask.file.fileName,file.fileName);
            [transportTask.taskHandle waiting];
        }
    } else {
        NSLog(@"%@子任务%@已存在",self.transportTask.file.fileName,file.fileName);
        if (file.transportTask.taskStatus.integerValue == TaskCancel) {
            self.taskTotalUnitCount = self.taskTotalUnitCount - [self totalTransportUnitCountWithFile:file];
            return;
        }
    }
    TransportFolderDownloadTaskHandle* taskHandle = (TransportFolderDownloadTaskHandle*)file.transportTask.taskHandle;
    if (!taskHandle.downloadOperation) {
        taskHandle.downloadOperation = [[TransportFolderDownloadOperation alloc]initWithFile:file];
    }
    [taskHandle.downloadOperation standBy];
    taskHandle.taskTotalUnitCount = [taskHandle totalUnitCountWithFile:file];
    taskHandle.taskCompleteUnitCount = 0;
    NSArray *subItems = [file subItems];
    for (File *subItem in subItems) {
        if ([subItem isFile]) {
            [taskHandle fileDownloadTaskAdd:subItem];
        } else {
            [taskHandle folderDownloadTaskAdd:subItem];
        }
    }
    self.taskTotalUnitCount = self.taskTotalUnitCount - ([self totalUnitCountWithFile:file] - taskHandle.taskTotalUnitCount);
    self.taskCompleteUnitCount = self.taskCompleteUnitCount + taskHandle.taskCompleteUnitCount;
    if (file.transportTask.taskStatus.integerValue == TaskSuspend ||
        file.transportTask.taskStatus.integerValue == TaskFailed) {
        [file.transportTask.taskHandle waiting];
    }
}

- (void)setDownloadOperation:(TransportFolderDownloadOperation *)downloadOperation {
    if (_downloadOperation == downloadOperation) {
        return;
    }
    if (_downloadOperation) {
        [_downloadOperation removeObserver:self forKeyPath:@"currentTaskFileId" context:nil];
        [_downloadOperation removeObserver:self forKeyPath:@"deleteTaskFileId" context:nil];
    }
    if (downloadOperation) {
        [downloadOperation addObserver:self forKeyPath:@"currentTaskFileId" options:NSKeyValueObservingOptionNew context:nil];
        [downloadOperation addObserver:self forKeyPath:@"deleteTaskFileId" options:NSKeyValueObservingOptionNew context:nil];
    }
    _downloadOperation = downloadOperation;
}

- (void)setCurrentTaskHandle:(TransportTaskHandle *)currentTaskHandle {
    if (_currentTaskHandle == currentTaskHandle) {
        return;
    }
    self.currentTaskCompletedUnitCount = 0;
    if (_currentTaskHandle) {
        [_currentTaskHandle removeObserver:self forKeyPath:@"taskFraction" context:nil];
    }
    if (currentTaskHandle) {
        if (currentTaskHandle.taskProgress) {
            self.currentTaskCompletedUnitCount = currentTaskHandle.taskProgress.completedUnitCount;
        } else {
            self.currentTaskCompletedUnitCount = (unsigned long long)(currentTaskHandle.transportTask.taskFraction.floatValue * currentTaskHandle.transportTask.file.fileSize.longLongValue);
        }
        [currentTaskHandle addObserver:self forKeyPath:@"taskFraction" options:NSKeyValueObservingOptionNew context:nil];
    }
    _currentTaskHandle = currentTaskHandle;
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.downloadOperation) {
        if([keyPath isEqual:@"currentTaskFileId"]) {
            self.currentTaskHandle = self.downloadOperation.currentTaskHandle;
        }
    }
    if (object == self.downloadOperation) {
        if ([keyPath isEqual:@"deleteTaskFileId"]) {
            self.deleteTaskFile = [File getFileWithFileId:self.downloadOperation.deleteTaskFileId fileOwner:self.transportTask.file.fileOwner];
            if (!self.deleteTaskFile.transportTask) {
                return;
            }
            if (self.deleteTaskFile.transportTask.taskStatus.integerValue == TaskCancel) {
                File *file = self.deleteTaskFile;
                TransportTask *transportTask = nil;
                unsigned long long deleteFileCompleteUnitCount = 0;
                if (self.deleteTaskFile.transportTask.taskHandle.taskProgress) {
                    deleteFileCompleteUnitCount = self.deleteTaskFile.transportTask.taskHandle.taskProgress.completedUnitCount;
                } else {
                    deleteFileCompleteUnitCount = (unsigned long long)(self.deleteTaskFile.transportTask.taskFraction.floatValue * [self totalTransportUnitCountWithFile:self.deleteTaskFile]);
                }
                do {
                    File *parentFile = [File getFileWithFileId:file.fileParent fileOwner:file.fileOwner];
                    transportTask = parentFile.transportTask;
                    transportTask.taskHandle.taskCompleteUnitCount -= deleteFileCompleteUnitCount;
                    transportTask.taskHandle.taskProgress.completedUnitCount -= deleteFileCompleteUnitCount;
                    transportTask.taskHandle.taskTotalUnitCount -= [self totalTransportUnitCountWithFile:self.deleteTaskFile];
                    transportTask.taskHandle.taskProgress.totalUnitCount -= [self totalTransportUnitCountWithFile:self.deleteTaskFile];
                    file = parentFile;
                } while (transportTask.taskRecoverable.integerValue != 1);
            } else {
                //self.taskCompleteUnitCount += [self totalTransportUnitCountWithFile:self.deleteTaskFile];
            }
        }
    }
    if (object == self.currentTaskHandle) {
        if ([keyPath isEqual:@"taskFraction"]) {
            self.taskCompleteUnitCount = self.taskCompleteUnitCount + (self.currentTaskHandle.taskProgress.completedUnitCount - self.currentTaskCompletedUnitCount);
            self.currentTaskCompletedUnitCount = self.currentTaskHandle.taskProgress.completedUnitCount;
            self.taskProgress.completedUnitCount = self.taskCompleteUnitCount;
            self.taskFraction = self.taskProgress.fractionCompleted;
        }
    }
}



- (void)suspend {
    [super suspend];
    if (self.downloadOperation) {
        self.downloadOperation.fetchController.delegate = nil;
    }
    NSArray *subItems = [self.transportTask.file subItems];
    for (File *subItem in subItems) {
        if (subItem.transportTask && subItem.transportTask.taskStatus.integerValue != TaskSucceed && subItem.transportTask.taskStatus.integerValue != TaskCancel) {
            [subItem.transportTask.taskHandle suspend];
        }
    }
}

- (void)cancel {
    [super cancel];
    if (self.downloadOperation) {
        self.downloadOperation.fetchController.delegate = nil;
        self.downloadOperation = nil;
    }
    if (self.currentTaskHandle) {
        self.currentTaskHandle = nil;
    }
    NSArray *subItems = [self.transportTask.file subItems];
    for (File *subItem in subItems) {
        if (subItem.transportTask &&
            subItem.transportTask.taskStatus.integerValue != TaskSucceed &&
            subItem.transportTask.taskStatus.integerValue != TaskCancel) {
            [subItem.transportTask.taskHandle suspend];
        }
    }
}

- (void)success {
    [super success];
    if (self.downloadOperation) {
        self.downloadOperation.fetchController.delegate = nil;
        self.downloadOperation = nil;
    }
    if (self.currentTaskHandle) {
        self.currentTaskHandle = nil;
    }
}

- (long long)totalUnitCountWithFile:(File*)file {
    if ([file isFile]) {
        return file.fileSize.longLongValue;
    } else {
        long long size = 0;
        NSArray *subItems = [file subItems];
        for (File *subItem in subItems) {
            if ([subItem isFile]) {
                size = size + subItem.fileSize.longLongValue;
            } else {
                size = size + [self totalUnitCountWithFile:subItem];
            }
        }
        return size;
    }
}

- (long long)totalTransportUnitCountWithFile:(File*)file {
    if ([file isFile]) {
        return file.fileSize.longLongValue;
    } else {
        long long size = 0;
        NSArray *subItems = [file subItems];
        for (File *subItem in subItems) {
            if (subItem.transportTask && subItem.transportTask.taskStatus.integerValue != TaskCancel) {
                if ([subItem isFile]) {
                    size = size + subItem.fileSize.longLongValue;
                } else {
                    size = size + [self totalTransportUnitCountWithFile:subItem];
                }
            }
        }
        return size;
    }
}

@end
