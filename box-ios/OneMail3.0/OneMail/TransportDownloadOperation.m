//
//  TransportDownloadOperation.m
//  OneMail
//
//  Created by cse  on 15/11/17.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportDownloadOperation.h"
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "TransportTask.h"
#import "TransportTaskHandle.h"

@interface TransportDownloadOperation ()<NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) NSFetchedResultsController *fetchController;

@end

@implementation TransportDownloadOperation
-(id)init {
    self = [super init];
    if (self) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"taskStatus != %@ AND taskRecoverable = %@ AND taskOwner = %@ AND (taskType = %@ OR taskType = %@ OR taskType = %@)",@(TaskSucceed),@(1),appDelegate.localManager.userCloudId,@(TaskFileDownload),@(TaskFolderDownload),@(TaskVersionDownload)];
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
- (void)closeOperation{
    self.fetchController.delegate = nil;
}

- (void)openOperation{
    self.fetchController.delegate = self;
}

- (void)startOperation {
    if (self.fetchController.fetchedObjects.count > 0) {
        TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
        if (taskFirst && taskFirst.taskStatus.integerValue == TaskWaitting) {
            [taskFirst.taskHandle resume];
        }
    }
}

- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {

}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
 
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    switch (type) {
        case NSFetchedResultsChangeInsert: {
            TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
            if (taskFirst.taskStatus.integerValue == TaskWaitting) {
                [taskFirst.taskHandle resume];
            }
        }
            break;
        case NSFetchedResultsChangeDelete:
            if (self.fetchController.fetchedObjects.count > 0) {
                TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
                if (taskFirst.taskStatus.integerValue == TaskWaitting) {
                    [taskFirst.taskHandle resume];
                }
            }
            break;
        case NSFetchedResultsChangeUpdate: {
            TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
            if (taskFirst.taskStatus.integerValue == TaskWaitting) {
                [taskFirst.taskHandle resume];
            }
        }
            break;
        case NSFetchedResultsChangeMove:
//            if (self.fetchController.fetchedObjects.count > 0) {
//                TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
//                if (taskFirst.taskStatus.integerValue == TaskWaitting) {
//                    [taskFirst.taskHandle resume];
//                }
//            }
            
            break;
        default:
            break;
    }
    
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    
}
@end
