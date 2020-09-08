//
//  TransportTaskHandle.m
//  OneMail
//
//  Created by cse  on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportTaskHandle.h"
#import "TransportTask.h"
#import "AppDelegate.h"
#import "File.h"

@implementation TransportTaskHandle
- (id) initWithTranportTask:(TransportTask *)transportTask {
    self = [super init];
    if (self) {
        _transportTask = transportTask;
    }
    return self;
}

- (void) setTaskProgress:(NSProgress *)taskProgress {
    if (_taskProgress != taskProgress) {
        if (_taskProgress) {
            [_taskProgress removeObserver:self forKeyPath:@"fractionCompleted"];
            [_taskProgress removeObserver:self forKeyPath:@"totalUnitCount"];
        }
        if (taskProgress) {
            [taskProgress addObserver:self forKeyPath:@"fractionCompleted" options:NSKeyValueObservingOptionNew context:nil];
            [taskProgress addObserver:self forKeyPath:@"totalUnitCount" options:NSKeyValueObservingOptionNew context:nil];
            _taskFraction = taskProgress.fractionCompleted;
            _taskTotalUnitCount = taskProgress.totalUnitCount;
        }
        _taskProgress = taskProgress;
    }
}


- (void) observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    if (object == _taskProgress) {
        if([keyPath isEqual:@"fractionCompleted"]) {
            self.taskFraction = self.taskProgress.fractionCompleted;
            if (_transportTask.taskStatus.integerValue != TaskSucceed &&
                _transportTask.taskStatus.integerValue != TaskSuspend &&
                _transportTask.taskStatus.integerValue != TaskCancel) {
                [_transportTask.taskHandle running];
            }
        }
        if ([keyPath isEqual:@"totalUnitCount"]) {
            self.taskTotalUnitCount = self.taskProgress.totalUnitCount;
        }
    } else {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}

- (void) initting {
    if (self.transportTask.taskStatus.integerValue == TaskInitialing) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务初始化",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务初始化",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskInitialing];
}

- (void) resume {
    if (self.transportTask.file) {
        NSLog(@"%@任务启动",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务启动",self.transportTask.version.versionFileName);
    }
}

- (void) running {
    if (self.transportTask.taskStatus.integerValue == TaskRunning) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务传输",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务传输",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskRunning];
}

- (void) waiting {
    if (self.transportTask.taskStatus.integerValue == TaskWaitting) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务等待",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务等待",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskWaitting];
}

- (void) suspend {
    if (self.transportTask.taskStatus.integerValue == TaskSuspend) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务暂停",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务暂停",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskSuspend];
    [self.transportTask saveTransportFraction:(float)self.taskProgress.fractionCompleted];
}

- (void) success {
    if (self.transportTask.taskStatus.integerValue == TaskSucceed) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务成功",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务成功",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskSucceed];
}

- (void) failed {
    if (self.transportTask.taskStatus.integerValue == TaskFailed) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务失败",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务失败",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskFailed];
}

- (void) cancel {
    if (self.transportTask.taskStatus.integerValue == TaskCancel) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务取消",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务取消",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskCancel];
    [self.transportTask saveTransportFraction:(float)self.taskProgress.fractionCompleted];
}

- (void) waitNetwork {
    if (self.transportTask.taskStatus.integerValue == TaskWaitNetwork) {
        return;
    }
    if (self.transportTask.file) {
        NSLog(@"%@任务等待网络",self.transportTask.file.fileName);
    }
    if (self.transportTask.version) {
        NSLog(@"%@任务等待网络",self.transportTask.version.versionFileName);
    }
    [self.transportTask changeTransportStatus:TaskWaitNetwork];
}



- (void) dealloc {
    if (_taskProgress) {
        [_taskProgress removeObserver:self forKeyPath:@"fractionCompleted"];
        _taskProgress = nil;
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

- (void)savePath:(NSString *)fromPath toPath:(NSString *)toPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:appDelegate.localManager.userDataPath isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:appDelegate.localManager.userDataPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    if (toPath && [[NSFileManager defaultManager] fileExistsAtPath:toPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:toPath error:nil];
    }
    if (fromPath && [[NSFileManager defaultManager] fileExistsAtPath:fromPath]) {
        [[NSFileManager defaultManager] moveItemAtPath:fromPath toPath:toPath error:nil];
    }
}

@end
