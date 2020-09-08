//
//  LocalDataManager.h
//  OneMail
//
//  Created by cse  on 16/4/1.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
@interface LocalDataManager : NSObject

@property(copy, nonatomic, readonly) NSString* userSingleId;
@property(copy, nonatomic, readonly) NSString* userCloudId;
@property(copy, nonatomic, readonly) NSString* userDataPath;

- (NSManagedObjectModel*)   managedObjectModel;
- (NSManagedObjectContext*) managedObjectContext;
- (NSManagedObjectContext*) backgroundObjectContext;
- (NSManagedObjectContext*) memoryObjectContext;
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator;

- (id)init;
- (void)setUserSingleId:(NSString *)userSingleId;
- (void)setUserCloudId:(NSString *)userCloudId;
- (void)setUserDataPath:(NSString *)userDataPath;

//- (void) resumeAllTask;
//- (void) suspendAllTask;
//- (void) cancelAllDownloadTask;

@end

