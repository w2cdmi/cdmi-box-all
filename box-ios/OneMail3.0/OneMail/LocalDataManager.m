//
//  LocalDataManager.m
//  OneMail
//
//  Created by cse  on 16/4/1.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "LocalDataManager.h"
#import "AFNetworkReachabilityManager.h"

@interface LocalDataManager()

@property(strong, nonatomic) NSManagedObjectModel* managedObjectModel;
@property(strong, nonatomic) NSPersistentStoreCoordinator* persistentStoreCoordinator;
@property(strong, nonatomic) NSManagedObjectContext* managedObjectContext;

@end

@implementation LocalDataManager

- (id)init {
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachablilityChanged:) name: AFNetworkingReachabilityDidChangeNotification object: nil];
    }
    return self;
}

- (void)reachablilityChanged:(NSNotification *)note {
    NSDictionary *curReach = (NSDictionary *)[note userInfo];
    AFNetworkReachabilityStatus status = [[curReach objectForKey:AFNetworkingReachabilityNotificationStatusItem] integerValue];
    if (status == AFNetworkReachabilityStatusReachableViaWiFi || status == AFNetworkReachabilityStatusReachableViaWWAN) {
        //UIAlertView *alert = [[UIAlertView alloc]initWithTitle:NSLocalizedString(@"InternetConnectionResume",nil) message:nil delegate:nil cancelButtonTitle:NSLocalizedString(@"Confirm", nil) otherButtonTitles:nil, nil];
        //[alert show];
        //[SNLog Log:LInfo :@"Try to resume all transport when network avaliable"];
        //[self resumeAllTask];
    }
}

- (void)setUserSingleId:(NSString *)userSingleId {
    _userSingleId = userSingleId;
}

- (void)setUserCloudId:(NSString *)userCloudId {
    _userCloudId = userCloudId;
}

- (void)setUserDataPath:(NSString *)userDataPath {
    _userDataPath = userDataPath;
}


- (NSManagedObjectContext*) managedObjectContext {
    if (_managedObjectContext != nil) {
        return _managedObjectContext;
    }
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (coordinator != nil) {
        _managedObjectContext = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSMainQueueConcurrencyType];
        [_managedObjectContext setPersistentStoreCoordinator:coordinator];
    }
    return _managedObjectContext;
}

- (NSManagedObjectContext*) backgroundObjectContext {
    NSManagedObjectContext* _backgroundCtx = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSPrivateQueueConcurrencyType];
    _backgroundCtx.parentContext = [self managedObjectContext];
    return _backgroundCtx;
}

- (NSManagedObjectModel *)managedObjectModel {
    if (_managedObjectModel != nil) {
        return _managedObjectModel;
    }
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"OneMail" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

- (NSPersistentStoreCoordinator *)persistentStoreCoordinator {
    if (_persistentStoreCoordinator != nil) {
        return _persistentStoreCoordinator;
    }
    NSError* error;
    NSFileManager* fileManager = [NSFileManager defaultManager];
    NSURL* docUrl = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
    NSURL* userDataHome = [docUrl URLByAppendingPathComponent:@"sqlite"];
    if (![fileManager fileExistsAtPath:userDataHome.path]) {
        if (![fileManager createDirectoryAtURL:userDataHome withIntermediateDirectories:NO attributes:nil error:&error]) {
            return nil;
        }
    }
    NSURL *storeURL = [userDataHome URLByAppendingPathComponent:@"OneMailV1.0.sqlite"];
    
    _persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    
    NSDictionary *options = [NSDictionary dictionaryWithObjectsAndKeys:
                             [NSNumber numberWithBool:YES], NSMigratePersistentStoresAutomaticallyOption,
                             [NSNumber numberWithBool:YES], NSInferMappingModelAutomaticallyOption, nil];
    
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:options error:&error]) {
        abort();
    }
    return _persistentStoreCoordinator;
}

- (NSManagedObjectContext*) memoryObjectContext {
    NSManagedObjectContext* memoryContext = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSMainQueueConcurrencyType];
    [memoryContext setPersistentStoreCoordinator:[self memoryPersistentStroeCoordinator]];
    return memoryContext;
}

- (NSPersistentStoreCoordinator*)memoryPersistentStroeCoordinator {
    NSPersistentStoreCoordinator* persistentSotreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    [persistentSotreCoordinator addPersistentStoreWithType:NSInMemoryStoreType configuration:nil URL:nil options:nil error:nil];
    return persistentSotreCoordinator;
}

@end

