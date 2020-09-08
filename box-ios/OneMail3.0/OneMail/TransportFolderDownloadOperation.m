//
//  TransportFolderDownloadOperation.m
//  OneMail
//
//  Created by cse  on 15/12/21.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportFolderDownloadOperation.h"
#import "TransportFolderDownloadTaskHandle.h"
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "TransportTask.h"
#import "TransportTaskHandle.h"
#import "File.h"

@interface TransportFolderDownloadOperation ()<NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, assign) BOOL standByState;

@end

@implementation TransportFolderDownloadOperation
-(id)initWithFile:(File *)file {
    self = [super init];
    if (self) {
        self.file = file;
        self.currentTaskFileId = nil;
        self.standByState = YES;
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"NOT(taskStatus IN %@) AND file.fileOwner = %@ AND file.fileParent = %@ AND (taskType IN %@)",@[@(TaskSucceed),@(TaskCancel)],self.file.fileOwner,self.file.fileId,@[@(TaskFileDownload),@(TaskFolderDownload)]];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"TransportTask" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        NSSortDescriptor *statusDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"taskStatus" ascending:YES];
        NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"taskCreatedDate" ascending:NO];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setPredicate:predicate];
        [request setEntity:entity];
        [request setSortDescriptors:@[statusDescriptor,dateDescriptor]];
        self.fetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
        self.fetchController.delegate = self;
        NSError *error = NULL;
        if (![self.fetchController performFetch:&error]) {
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
            abort();
        }
    }
    return self;
}

- (void)begin {
    self.standByState = NO;
    if (self.fetchController.fetchedObjects.count == 0) {
        [self.file.transportTask.taskHandle success];
        return;
    }
    TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
    if (taskFirst.taskStatus.integerValue == TaskWaitting &&
        self.file.transportTask.taskStatus.integerValue == TaskRunning) {
        self.currentTaskHandle = taskFirst.taskHandle;
        self.currentTaskFileId = taskFirst.file.fileId;
        [taskFirst.taskHandle resume];
    }
}

- (void)standBy {
    self.standByState = YES;
}

- (void)dealloc {
    NSLog(@"%@文件夹下载成功，operation释放",self.file.fileName);
}

- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {

}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {

}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    if (self.standByState) {
        return;
    }
    switch (type) {
        case NSFetchedResultsChangeInsert: {
            TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
            if (taskFirst.taskStatus.integerValue == TaskWaitting &&
                self.file.transportTask.taskStatus.integerValue == TaskRunning) {
                self.currentTaskHandle = taskFirst.taskHandle;
                self.currentTaskFileId = taskFirst.file.fileId;
                [taskFirst.taskHandle resume];
            }
        }
            break;
        case NSFetchedResultsChangeDelete: {
            TransportTask *deleteTask = (TransportTask*)anObject;
            self.deleteTaskHandle = deleteTask.taskHandle;
            self.deleteTaskFileId = deleteTask.file.fileId;
            if (self.fetchController.fetchedObjects.count > 0) {
                TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
                if (taskFirst.taskStatus.integerValue == TaskWaitting &&
                    self.file.transportTask.taskStatus.integerValue == TaskRunning) {
                    self.currentTaskHandle = taskFirst.taskHandle;
                    self.currentTaskFileId = taskFirst.file.fileId;
                    [taskFirst.taskHandle resume];
                } else {
                    if (taskFirst.taskStatus.integerValue == TaskSuspend) {
                        [self.file.transportTask.taskHandle suspend];
                    }
                    if (taskFirst.taskStatus.integerValue == TaskFailed) {
                        [self.file.transportTask.taskHandle failed];
                    }
                    if (taskFirst.taskStatus.integerValue == TaskRunning) {
                        [self.file.transportTask.taskHandle running];
                    }
                }
            } else {
                [self.file.transportTask.taskHandle success];
            }
        }
            break;
        case NSFetchedResultsChangeUpdate: {
            TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
            if (self.file.transportTask.taskStatus.integerValue == TaskWaitting) {
                
            }
            if (self.file.transportTask.taskStatus.integerValue == TaskRunning) {
                if (taskFirst.taskStatus.integerValue == TaskWaitting) {
                    self.currentTaskHandle = taskFirst.taskHandle;
                    self.currentTaskFileId = taskFirst.file.fileId;
                    [taskFirst.taskHandle resume];
                } else {
                    if (taskFirst.taskStatus.integerValue == TaskSuspend) {
                        [self.file.transportTask.taskHandle suspend];
                    }
                    if (taskFirst.taskStatus.integerValue == TaskFailed) {
                        [self.file.transportTask.taskHandle failed];
                    }
                    if (taskFirst.taskStatus.integerValue == TaskRunning) {
                        [self.file.transportTask.taskHandle running];
                    }
                }
            }
            if (self.file.transportTask.taskStatus.integerValue == TaskSuspend ||
                self.file.transportTask.taskStatus.integerValue == TaskFailed) {
                self.currentTaskHandle = taskFirst.taskHandle;
                self.currentTaskFileId = taskFirst.file.fileId;
                if (taskFirst.taskStatus.integerValue == TaskWaitting) {
                    self.currentTaskHandle = taskFirst.taskHandle;
                    self.currentTaskFileId = taskFirst.file.fileId;
                    [taskFirst.taskHandle resume];
                    [self.file.transportTask.taskHandle running];
                }
            }
        }
            break;
        case NSFetchedResultsChangeMove: {

        }
            break;
        default:
            break;
    }
    
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    
}
@end

