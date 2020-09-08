//
//  Asset.m
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "Asset.h"
#import "AppDelegate.h"

@implementation Asset

@dynamic assetAlbumKey;
@dynamic assetAlbumName;
@dynamic assetName;
@dynamic assetOwner;
@dynamic assetUrl;
@dynamic assetUploadFlag;
@dynamic assetBackUpFlag;
@dynamic assetBackUpFailedFlag;
@dynamic assetDate;
@dynamic relationFile;

+ (Asset*) searchAssetWithAlbumKey:(NSString*)albumKey assetName:(NSString*)assetName {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"assetAlbumKey = %@ AND assetName = %@ AND assetOwner = %@",albumKey,assetName,appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Asset" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.predicate = predicate;
    request.entity = entity;
    return [[ctx executeFetchRequest:request error:nil] lastObject];
}

+ (Asset*) insertAssetWithAssetInfo:(NSDictionary*)assetInfo {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *assetAlbumKey = [assetInfo objectForKey:@"assetAlbumKey"];
    NSString *assetAlbumName = [assetInfo objectForKey:@"assetAlbumName"];
    NSString *assetName = [assetInfo objectForKey:@"assetName"];
    NSString *assetUrl = [assetInfo objectForKey:@"assetUrl"];
    __block Asset *asset = [Asset searchAssetWithAlbumKey:assetAlbumKey assetName:assetName];
    if (!asset) {
        NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
        [ctx performBlockAndWait:^{
            asset = [NSEntityDescription insertNewObjectForEntityForName:@"Asset" inManagedObjectContext:ctx];
            asset.assetAlbumKey = assetAlbumKey;
            asset.assetAlbumName = assetAlbumName;
            asset.assetName = assetName;
            asset.assetUrl = assetUrl;
            asset.assetOwner = appDelegate.localManager.userCloudId;
            asset.assetUploadFlag = @(0);
            asset.assetBackUpFlag = @(0);
            asset.assetBackUpFailedFlag = @(0);
            asset.assetDate = [NSDate date];
            [ctx save:nil];
        }];
        if (asset) {
            Asset *mainAsset = (Asset*)[appDelegate.localManager.managedObjectContext objectWithID:asset.objectID];
            return mainAsset;
        }
    }
    return asset;
}

- (void) remove {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Asset *shadow = (Asset*)[ctx objectWithID:self.objectID];
        [ctx deleteObject:shadow];
        [ctx save:nil];
    }];
}

+ (NSArray*) getAssetWithAlbumKey:(NSString *)albumKey {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"assetAlbumKey = %@ AND assetOwner = %@",albumKey,appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Asset" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.predicate = predicate;
    request.entity = entity;
    return [ctx executeFetchRequest:request error:nil];
}

+ (NSArray*) getAllFailedAsset {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"assetBackUpFlag = %@ AND assetBackUpFailedFlag = %@ AND assetOwner = %@",@(1),@(1),appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Asset" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.predicate = predicate;
    request.entity = entity;
    return [ctx executeFetchRequest:request error:nil];
}

+ (NSArray*) getFailedAssetWithAlbumKey:(NSString *)albumKey {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"assetAlbumKey = %@ AND assetBackUpFlag = %@ AND assetBackUpFailedFlag = %@ AND assetOwner = %@",albumKey,@(1),@(1),appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Asset" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.predicate = predicate;
    request.entity = entity;
    return [ctx executeFetchRequest:request error:nil];
}

- (NSString*) assetThumbnailPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *assetDirctory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Backup"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:assetDirctory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:assetDirctory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *assetThumbnailDirectory = [assetDirctory stringByAppendingPathComponent:@"Thumbnail"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:assetThumbnailDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:assetThumbnailDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [assetThumbnailDirectory stringByAppendingPathComponent:self.assetName];
}

- (void) reBackUp {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    [ctx performBlockAndWait:^{
        Asset *shadow = (Asset*)[ctx objectWithID:self.objectID];
        shadow.assetBackUpFailedFlag = @(0);
        [ctx save:nil];
    }];
}

@end
