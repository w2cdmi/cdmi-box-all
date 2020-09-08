//
//  AttachmentUploadOperation.h
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Message;
@class Attachment;

typedef void (^AttachmentUploadCompletion)();
@interface AttachmentUploadOperation : NSObject

@property (nonatomic, copy) AttachmentUploadCompletion uploadComletion;

- (void)attachmentsUploadWithMessage:(Message*)message;
- (void)attachmentUploadWithAttachment:(Attachment*)attachment;
@end
