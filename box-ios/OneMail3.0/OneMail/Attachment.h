//
//  Attachment.h
//  OneMail
//
//  Created by cse  on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import <UIKit/UIKit.h>
#import "Message.h"

typedef enum : NSUInteger {
    AttachmentInLine = 1,
    AttachmentNormal,
    AttachmentOnebox,
} AttachmentType;

@class File;

@interface Attachment : NSManagedObject

@property (nonatomic, retain) NSString * attachmentId;
@property (nonatomic, retain) NSString * attachmentFileId;
@property (nonatomic, retain) NSString * attachmentFileOwner;
@property (nonatomic, retain) NSString * attachmentMessageId;
@property (nonatomic, retain) NSString * attachmentName;
@property (nonatomic, retain) NSNumber * attachmentSize;
@property (nonatomic, retain) NSNumber * attachmentType;
@property (nonatomic, retain) NSNumber * attachmentUploadFlag;
@property (nonatomic, retain) NSNumber * attachmentDisplayFlag;

+ (Attachment*)getAttachmentWithAttachmentId:(NSString*)attachmentId ctx:(NSManagedObjectContext*)ctx;
+ (NSArray*)getAttachmentWithMessageId:(NSString*)messageId ctx:(NSManagedObjectContext*)ctx;
+ (NSArray*)getAttachmentWithoutDisplayWithMessageId:(NSString*)messageId ctx:(NSManagedObjectContext *)ctx;
+ (Attachment*)attachmentInsertWithInfo:(NSDictionary*)attachmentInfo ctx:(NSManagedObjectContext*)ctx;
- (void)removeAttachment;
+ (void)imageWithResourceThumbnail:(Attachment*)attachment imageView:(UIImageView*)imageView;

- (NSString*)attachmentDataLocalPath;
+ (NSString*)attachmentDataCachePathWithId:(NSString*)attachmentId name:(NSString*)attachmentName;
- (NSString*)attachmentThumbnailLocalPath;
- (NSString*)attachmentCompressImagePath;

- (void)saveAttachmentDisplayFlag:(NSNumber*)displayFlag;

@end
