//
//  AttachmentBackupFolder.h
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "File.h"

typedef void (^AttachmentFolderBlock)(File *AttachmentWithDate);

@interface AttachmentBackupFolder : NSObject

@property (nonatomic,copy) AttachmentFolderBlock completionBlock;

- (void) cheakAttachmentFolderWithType:(NSNumber*)messageType date:(NSDate*)messageDate;

@end