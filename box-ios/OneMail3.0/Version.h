//
//  Version.h
//  OneMail
//
//  Created by cse  on 15/11/19.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "TransportTaskHandle.h"
@class TransportTask;

@interface Version : NSManagedObject

@property (nonatomic, retain) NSString * versionId;
@property (nonatomic, retain) NSString * versionFileId;
@property (nonatomic, retain) NSString * versionFileName;
@property (nonatomic, retain) NSString * versionOwner;
@property (nonatomic, retain) NSDate * versionModifiedDate;
@property (nonatomic, retain) NSString * versionModifiedDateString;
@property (nonatomic, retain) NSNumber * versionSize;
@property (nonatomic, retain) NSString * versionObjectId;
@property (nonatomic, retain) TransportTask *transportTask;

- (void)setVersion:(NSDictionary *)versionInfo;
+ (Version *)getVersionWithVersionIdAndFileId:(NSString*)versionObjectId FileId:(NSString *)fileId;
+ (Version *)getVersionWithObjectID:(NSManagedObjectID*)objectID;
- (TransportTaskHandle*)download;

- (NSString*)versionCacheLocalPath;
- (NSString*)versionDataLocalPath;
- (NSString*)versionCompressImagePath;
- (void) versionCompressImage;

@end
