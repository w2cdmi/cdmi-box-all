//
//  TransportUploadOperation.m
//  OneMail
//
//  Created by cse  on 15/11/15.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportUploadOperation.h"
#import <CoreData/CoreData.h>
#import "TransportTask.h"
#import "AppDelegate.h"
#import "TransportTaskHandle.h"
#import "TransportAssetUploadTaskHandle.h"
#import "TransportAttachmentUploadTaskHandle.h"

@interface TransportUploadOperation ()<NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) NSFetchedResultsController *fetchController;

@end

@implementation TransportUploadOperation

-(id)init {
    self = [super init];
    if (self) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"taskStatus != %@ AND taskOwner = %@ AND (taskType = %@ OR taskType = %@)",@(TaskSucceed),appDelegate.localManager.userCloudId,@(TaskAssetUpload),@(TaskAttachmentUpload)];
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
        if (taskFirst.taskStatus.integerValue == TaskWaitting) {
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
        case NSFetchedResultsChangeUpdate:
            if (self.fetchController.fetchedObjects.count > 0) {
                TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
                if (taskFirst.taskStatus.integerValue == TaskWaitting) {
                    [taskFirst.taskHandle resume];
                }
            }
            break;
        case NSFetchedResultsChangeMove:
                if (self.fetchController.fetchedObjects.count > 0) {
                    TransportTask *taskFirst = [self.fetchController.fetchedObjects objectAtIndex:0];
                    if (taskFirst.taskStatus.integerValue == TaskWaitting) {
                        [taskFirst.taskHandle resume];
                    }
                }
            
            break;
        default:
            break;
    }
    
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    
}
@end
