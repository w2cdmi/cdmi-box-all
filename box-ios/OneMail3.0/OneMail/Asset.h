//
//  Asset.h
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class File;

@interface Asset : NSManagedObject

@property (nonatomic, retain) NSString * assetAlbumKey;
@property (nonatomic, retain) NSString * assetAlbumName;
@property (nonatomic, retain) NSString * assetName;
@property (nonatomic, retain) NSString * assetOwner;
@property (nonatomic, retain) NSString * assetUrl;
@property (nonatomic, retain) NSDate   * assetDate;
@property (nonatomic, retain) NSNumber * assetUploadFlag;
@property (nonatomic, retain) NSNumber * assetBackUpFlag;
@property (nonatomic, retain) NSNumber * assetBackUpFailedFlag;
@property (nonatomic, retain) File *relationFile;

+ (Asset*) insertAssetWithAssetInfo:(NSDictionary*)assetInfo;
- (void) remove;

+ (NSArray*) getAssetWithAlbumKey:(NSString*)albumKey;

+ (NSArray*) getAllFailedAsset;
+ (NSArray*) getFailedAssetWithAlbumKey:(NSString*)albumKey;

- (void)reBackUp;

- (NSString*) assetThumbnailPath;

@end
