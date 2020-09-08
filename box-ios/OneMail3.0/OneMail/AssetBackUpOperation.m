//
//  AssetBackUpOperation.m
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "AssetBackUpOperation.h"
#import <CoreData/CoreData.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import <Photos/Photos.h>
#import "Asset.h"
#import "AssetGroup.h"
#import "AppDelegate.h"
#import "File+Remote.h"
#import "TransportTask.h"
#import "TransportTaskHandle.h"
#import "AssetBackUpFolder.h"
#import "CloudBackUpViewController.h"
#import "UIAlertView+Blocks.h"

@interface AssetBackUpOperation ()<NSFetchedResultsControllerDelegate>

@property (nonatomic, assign) BOOL backUpStatus;
@property (nonatomic, assign) BOOL backUpPause;
@property (nonatomic, strong) NSFetchedResultsController *fetchController;

@end

@implementation AssetBackUpOperation

-(id)init {
    self = [super init];
    if (self) {
        
        self.backUpStatus = NO;
        self.backUpAsset = nil;
        self.backUpTotalCount = 0;
        self.backUpPause = NO;
        
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"assetBackUpFlag = %@ AND assetUploadFlag = %@ AND assetBackUpFailedFlag = %@ AND assetOwner = %@",@(1),@(0),@(0),appDelegate.localManager.userCloudId];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"Asset" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"assetDate" ascending:NO];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setPredicate:predicate];
        [request setEntity:entity];
        [request setSortDescriptors:@[dateDescriptor]];
        self.fetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
        self.fetchController.delegate = self;
        NSError *error = NULL;
        if (![self.fetchController performFetch:&error]) {
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
            abort();
        }
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(assetBackupSuccess) name:@"Onebox.backup.success" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(assetBackupFailed) name:@"Onebox.backup.failed" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(assetAutoBackup) name:@"Onebox.backup.autoStart" object:nil];
    }
    return self;
}

- (NSUInteger) backUpRemainCount {
    return self.fetchController.fetchedObjects.count;
}

- (BOOL) backUpPauseStatus {
    return self.backUpPause;
}

- (void) backUpControl {
    if (self.backUpPause) {
        [self assetBackupContinue];
    } else {
        [self assetBackupPause];
    }
}


- (void) assetBackupBegin {
    if (self.backUpStatus) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    BOOL hasNetWork = appDelegate.hasNetwork;
    if (!hasNetWork) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudNoneNetworkPrompt", nil)];
        });
    } else {
        BOOL WiFiNetWork = appDelegate.wifiNetwork;
        UserSetting *userSetting = [UserSetting defaultSetting];
        if (!WiFiNetWork && userSetting.cloudAssetBackupWifi.integerValue == 1) {
            [UIAlertView showAlertViewWithTitle:nil message:getLocalizedString(@"CloudUploadWIFIPrompt", nil) cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                [self doBackup];
            } onCancel:^{}];
        } else {
            [self doBackup];
        }
    }
}

- (void) assetBackupPause {
    self.backUpPause = YES;
}

- (void) assetBackupContinue {
    self.backUpPause = NO;
    [self assetBackupBegin];
}

- (void) assetBackupCancel {
    self.backUpStatus = NO;
    Asset *asset = [self.fetchController.fetchedObjects objectAtIndex:0];
    TransportTaskHandle *taskHandle = asset.relationFile.transportTask.taskHandle;
    [taskHandle suspend];
}

- (void) assetBackupSuccess {
    self.backUpStatus = NO;
    [self.backUpViewController refreshBackUpStatusRemain];
}

- (void) assetBackupFailed {
    self.backUpStatus = NO;
    [self.backUpViewController refreshBackUpStatusRemain];
}

- (void) assetTotalBackupSuccess {
    self.backUpStatus = NO;
    self.backUpTotalCount = 0;
}

- (void) doBackup {
    self.backUpStatus = YES;
    if (self.fetchController.fetchedObjects.count == 0) {
        self.backUpStatus = NO;
        return;
    }
    Asset *asset = [self.fetchController.fetchedObjects objectAtIndex:0];
    self.backUpAsset = asset;
    [self.backUpViewController refreshBackUpStatusImage];
    AssetBackUpFolder *assetBackUpFolder = [[AssetBackUpFolder alloc] init];
    assetBackUpFolder.completionBlock = ^(File *assetFolder){
        TransportTaskHandle *taskHandle = [assetFolder backUpAssets:asset force:YES];
        [taskHandle resume];
    };
    
    assetBackUpFolder.failedBlock = ^(){
        self.backUpStatus = NO;
    };
    [assetBackUpFolder cheakAssetFolderWithKey:asset.assetAlbumKey name:asset.assetAlbumName];
}

- (void) assetAutoBackup {
    NSArray *backupGroup = [AssetGroup getBackupGroup];
    NSMutableArray *backupGroupsKey = [[NSMutableArray alloc] initWithCapacity:backupGroup.count];
    for (AssetGroup *group in backupGroup) {
        [backupGroupsKey addObject:group.groupKey];
    }
    //ios8以后的相册获取
    //列出所有相册智能相册
    PHFetchResult *smartAlbums = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeAlbum subtype:PHAssetCollectionSubtypeAlbumRegular options:nil];
    //列出所有用户创建的相册
    PHFetchResult *userAblums = [PHCollectionList fetchTopLevelUserCollectionsWithOptions:nil];
    //获取所有相册资源的集合，并按资源的创建时间排序
    PHFetchOptions *options = [[PHFetchOptions alloc]init];
    options.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"creationDate" ascending:YES]];
    PHFetchResult *assetFetchResults = [PHAsset fetchAssetsWithOptions:options];
    //在资源的集合中获取第一个集合，并获取其中的图片
    PHCachingImageManager *imageManager = [[PHCachingImageManager alloc]init];
    PHAsset *asset = assetFetchResults[0];
    [imageManager requestImageForAsset:asset targetSize:CGSizeMake(20, 30) contentMode:PHImageContentModeAspectFill options:nil resultHandler:^(UIImage * _Nullable result, NSDictionary * _Nullable info) {
        
    }];
    
    
    //ios7以前相册获取
    ALAssetsLibrary *assetsLibrary = [[ALAssetsLibrary alloc] init];
    [assetsLibrary enumerateGroupsWithTypes:ALAssetsGroupAll usingBlock:^(ALAssetsGroup *group, BOOL *stop) {
        if (group == nil) {
            [self assetBackupBegin];
            return;
        }
        NSString *groupKey = [group valueForProperty:ALAssetsGroupPropertyPersistentID];
        if ([backupGroupsKey containsObject:groupKey]) {
            [self getAssetWithGroup:group asserLibary:assetsLibrary];
        }
    } failureBlock:^(NSError *error) {
        if ([ALAssetsLibrary authorizationStatus] == ALAuthorizationStatusDenied) {
            NSString *errorMessage = getLocalizedString(@"BackupDeniedPrompt", nil);
            [[[UIAlertView alloc] initWithTitle:getLocalizedString(@"BackupDeniedTitle", nil) message:errorMessage delegate:nil cancelButtonTitle:getLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
            
        } else {
            NSString *errorMessage = [NSString stringWithFormat:@"Album Error: %@ - %@", [error localizedDescription], [error localizedRecoverySuggestion]];
            [[[UIAlertView alloc] initWithTitle:getLocalizedString(@"Error", nil) message:errorMessage delegate:nil cancelButtonTitle:getLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
        }
    }];
}

- (void) getAssetWithGroup:(ALAssetsGroup*)group asserLibary:(ALAssetsLibrary*)library {
    [group enumerateAssetsUsingBlock:^(ALAsset *result, NSUInteger index, BOOL *stop) {
        if (!result) {
            return;
        }
        ALAssetRepresentation* representation = [result defaultRepresentation];
        NSURL *assetUrl = [representation url];
        NSMutableDictionary *assetInfo = [[NSMutableDictionary alloc] init];
        [assetInfo setObject:[group valueForProperty:ALAssetsGroupPropertyPersistentID] forKey:@"assetAlbumKey"];
        [assetInfo setObject:[group valueForProperty:ALAssetsGroupPropertyName] forKey:@"assetAlbumName"];
        [assetInfo setObject:[assetUrl absoluteString] forKey:@"assetUrl"];
        [assetInfo setObject:[representation filename] forKey:@"assetName"];
        Asset *asset = [Asset insertAssetWithAssetInfo:assetInfo];
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
        [ctx performBlockAndWait:^{
            Asset *shadow = (Asset*)[ctx objectWithID:asset.objectID];
            shadow.assetBackUpFlag = @(1);
            NSString *assetThumbnailPath = [shadow assetThumbnailPath];
            if (assetThumbnailPath && [[NSFileManager defaultManager] fileExistsAtPath:assetThumbnailPath]) {
                
            } else {
                UIImage *assetThumbnail = [UIImage imageWithCGImage:result.thumbnail];
                NSData *assetThumbnailData = UIImageJPEGRepresentation(assetThumbnail, 1);
                [[NSFileManager defaultManager] createFileAtPath:assetThumbnailPath contents:assetThumbnailData attributes:nil];
                shadow.assetDate = [NSDate date];
            }
            [ctx save:nil];
        }];
    }];
}

#pragma mark NSFetchResultsControllerDelegate
- (void) controllerWillChangeContent:(NSFetchedResultsController *)controller {
   
}

- (void) controllerDidChangeContent:(NSFetchedResultsController *)controller {
    
}

- (void) controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    switch (type) {
        case NSFetchedResultsChangeInsert: {
            self.backUpTotalCount++;
            if (self.backUpStatus) {
                return;
            } else {
                if (self.backUpPause) {
                    return;
                }
                [self assetBackupBegin];
            }
        }
            break;
        case NSFetchedResultsChangeDelete: {
            if (self.fetchController.fetchedObjects.count > 0) {
                if (self.backUpAsset.assetBackUpFlag.integerValue == 1) {
                    self.backUpAsset = nil;
                } else {
                    self.backUpTotalCount--;
                }
                if (self.backUpStatus) {
                    return;
                } else {
                    if (self.backUpPause) {
                        return;
                    }
                    [self assetBackupBegin];
                }
            } else {
                self.backUpStatus = NO;
                [self assetTotalBackupSuccess];
            }
        }
            break;
        case NSFetchedResultsChangeUpdate:
            
            break;
        case NSFetchedResultsChangeMove:
            
            break;
        default:
            break;
    }
    
}

- (void) controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    
}

@end
