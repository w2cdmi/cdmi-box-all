//
//  TransportFolderDownloadOperation.h
//  OneMail
//
//  Created by cse  on 15/12/21.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class File;
@class TransportTaskHandle;

@interface TransportFolderDownloadOperation : NSObject<NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) TransportTaskHandle *currentTaskHandle;
@property (nonatomic, strong) NSString *currentTaskFileId;
@property (nonatomic, strong) TransportTaskHandle *deleteTaskHandle;
@property (nonatomic, strong) NSString *deleteTaskFileId;
@property (nonatomic, strong) NSFetchedResultsController *fetchController;

- (id)initWithFile:(File*)file;
- (void)begin;
- (void)standBy;

@end
