//
//  AssetBackUpFolder.m
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "AssetBackUpFolder.h"
#import "File+Remote.h"
#import "AppDelegate.h"

@interface AssetBackUpFolder ()

@property (nonatomic, strong) File *assetMainFolder;
@property (nonatomic, strong) NSString *albumKey;
@property (nonatomic, strong) NSString *albumName;

@end

@implementation AssetBackUpFolder

- (id) init {
    self = [super init];
    if (self) {

    }
    return self;
}

- (void) cheakAssetFolderWithKey:(NSString *)albumKey name:(NSString *)albumName {
    self.albumKey = albumKey;
    self.albumName = albumName;
    [self getAssetFolderWithKey:nil name:@"BackUp" parent:[File rootMyFolder]];
}

- (void) folderCreateSucceed:(NSString*)key name:(NSString*)name file:(File*)file {
    if (!key) {
        self.assetMainFolder = file;
        if (!self.albumKey) {
            self.completionBlock(file);
        } else {
            [self getAssetFolderWithKey:self.albumKey name:self.albumName parent:self.assetMainFolder];
        }
    } else {
        self.completionBlock(file);
    }
}

- (void) getAssetFolderWithKey:(NSString*)key name:(NSString*)name parent:(File*)parentFile {
    File *assetFolder;
    if (!key) {
        assetFolder = [File assetMainFolder];
        if (assetFolder) {
            [self folderCreateSucceed:key name:name file:assetFolder];return;
        }
    } else {
        assetFolder = [parentFile assetFolderWithKey:key];
        if (assetFolder) {
            [self folderCreateSucceed:key name:name file:assetFolder];return;
        }
        assetFolder = [parentFile assetFolderWithName:name];
        if (assetFolder) {
            [self setAssetFolderKey:key file:assetFolder];
            [self folderCreateSucceed:key name:name file:assetFolder];return;
        }
    }
    [self createAssetFolderWithKey:key name:name parent:parentFile completion:^(File *file) {
        if (file) {
            [self folderCreateSucceed:key name:name file:file];
        } else {
            self.failedBlock();
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
        }
    }];
    
}

- (void) setAssetFolderKey:(NSString*)key file:(File*)file {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlock:^{
        File *shadow = (File*)[ctx objectWithID:file.objectID];
        shadow.fileAlbumFolderKey = key;
        [ctx save:nil];
    }];
}

- (void) createAssetFolderWithKey:(NSString*)key name:(NSString*)name parent:(File*)parentFile completion:(void(^)(File *file))completionBlock {
    [parentFile folderCreate:name succeed:^(id retobj) {
        File *file =[parentFile assetFolderWithName:name];
        [file saveFileAlbumFolderKey:key];
        completionBlock(file);
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
        if (httpResponse.statusCode == 409) {
            [parentFile folderReload:^(id retobj) {
                File *file = [parentFile assetFolderWithName:name];
                if ([file isFile]) {
                    NSString *rename = [name stringByAppendingString:@"(1)"];
                    [self createAssetFolderWithKey:key name:rename parent:parentFile completion:completionBlock];
                } else {
                    [file saveFileAlbumFolderKey:key];
                    completionBlock(file);
                }
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                completionBlock(nil);
            }];
        } else {
            completionBlock(nil);
        }
    }];
}

@end
