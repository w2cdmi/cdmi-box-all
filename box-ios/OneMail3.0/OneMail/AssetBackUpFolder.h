//
//  AssetBackUpFolder.h
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;

typedef void (^AssetFolderBlock)(File *assetFolder);
typedef void (^AssetFailedBlock)();

@interface AssetBackUpFolder : NSObject

@property (nonatomic,copy) AssetFolderBlock completionBlock;
@property (nonatomic,copy) AssetFailedBlock failedBlock;

- (void) cheakAssetFolderWithKey:(NSString*)albumKey name:(NSString*)albumName;

@end
