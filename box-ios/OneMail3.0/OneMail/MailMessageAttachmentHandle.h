//
//  MailMessageAttachmentHandle.h
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void(^AttachmentHandleCompletion)(NSString *attamentHTMLString);

@interface MailMessageAttachmentHandle : NSObject

@property (nonatomic, copy) AttachmentHandleCompletion handleCompletion;

- (id)initWithAttachmentArray:(NSArray*)attachmentArray;
- (void)generationAttachmentHTMLString;

@end
