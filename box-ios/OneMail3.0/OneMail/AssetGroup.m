//
//  AssetGroup.m
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "AssetGroup.h"
#import "AppDelegate.h"
#import "Asset.h"

@implementation AssetGroup

@dynamic groupURL;
@dynamic groupKey;
@dynamic groupName;
@dynamic groupOwner;
@dynamic groupBackUpFlag;

+ (AssetGroup*)insertGroupWithGroupInfo:(NSDictionary*)groupInfo {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString* groupKey = [groupInfo objectForKey:@"groupKey"];
    NSString* groupName = [groupInfo objectForKey:@"groupName"];
    NSString* groupURL = [groupInfo objectForKey:@"groupURL"];
    __block AssetGroup *assetGroup = [AssetGroup searchGroupWithGroupKey:groupKey];
    if (!assetGroup) {
        NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
        [ctx performBlockAndWait:^{
            assetGroup = [NSEntityDescription insertNewObjectForEntityForName:@"AssetGroup" inManagedObjectContext:ctx];
            assetGroup.groupKey = groupKey;
            assetGroup.groupOwner = appDelegate.localManager.userCloudId;
            assetGroup.groupName = groupName;
            assetGroup.groupURL = groupURL;
            assetGroup.groupBackUpFlag = @(0);
            [ctx save:nil];
        }];
        if (assetGroup) {
            AssetGroup *mainAssetGroup = (AssetGroup*)[appDelegate.localManager.managedObjectContext objectWithID:assetGroup.objectID];
            return mainAssetGroup;
        }
    }
    return assetGroup;
}

- (void)remove {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSArray *assetArray = [Asset getAssetWithAlbumKey:self.groupKey];
    for (Asset *asset in assetArray) {
        [asset remove];
    }
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        AssetGroup *shadow = (AssetGroup*)[ctx objectWithID:self.objectID];
        [ctx deleteObject:shadow];
        [ctx save:nil];
    }];
}

+ (AssetGroup*)searchGroupWithGroupKey:(NSString*)key {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"groupKey = %@ AND groupOwner = %@",key,appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"AssetGroup" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.predicate = predicate;
    request.entity = entity;
    NSArray *resultArray = [ctx executeFetchRequest:request error:nil];
    return [resultArray lastObject];
}

+ (NSArray*)getAllGroup {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"groupOwner = %@",appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"AssetGroup" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.predicate = predicate;
    request.entity = entity;
    return [ctx executeFetchRequest:request error:nil];
}

+ (NSArray*)getBackupGroup {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"groupBackUpFlag = %@ AND groupOwner = %@",@(1),appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"AssetGroup" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.predicate = predicate;
    request.entity = entity;
    return [ctx executeFetchRequest:request error:nil];
}

- (void)saveGroupBackUpFlag:(NSNumber*)groupBackUpFlag {
    if (self.groupBackUpFlag.boolValue == groupBackUpFlag.boolValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        AssetGroup *shadow = (AssetGroup*)[ctx objectWithID:self.objectID];
        shadow.groupBackUpFlag = groupBackUpFlag;
        [ctx save:nil];
    }];
}

@end
