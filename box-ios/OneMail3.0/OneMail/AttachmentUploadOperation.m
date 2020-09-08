//
//  AttachmentUploadOperation.m
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "AttachmentUploadOperation.h"
#import "File.h"
#import "File+Remote.h"
#import "Attachment.h"
#import "Message.h"
#import "TransportTaskHandle.h"
#import "AttachmentBackupFolder.h"

@interface AttachmentUploadOperation ()

@property (nonatomic, strong) NSMutableArray *attachments;
@property (nonatomic, strong) File *attachmentFolder;
@end

@implementation AttachmentUploadOperation

- (void)attachmentsUploadWithMessage:(Message*)message {
    self.attachments = [[NSMutableArray alloc] initWithArray:[Attachment getAttachmentWithMessageId:message.messageId ctx:nil]];
    if (self.attachments.count == 0) {
        if (self.uploadComletion) {
            self.uploadComletion();return;
        }
    }
    AttachmentBackupFolder *attachmentBackupFolder = [[AttachmentBackupFolder alloc] init];
    attachmentBackupFolder.completionBlock = ^(File *attachmentFolder) {
        self.attachmentFolder = attachmentFolder;
        [self upload:self.attachments.firstObject];
    };
    [attachmentBackupFolder cheakAttachmentFolderWithType:message.messageType date:message.messageSendDate];
}

- (void)attachmentUploadWithAttachment:(Attachment*)attachment {
    self.attachments = [[NSMutableArray alloc] initWithObjects:attachment,nil];
    if (!attachment) {
        if (self.uploadComletion) {
            self.uploadComletion();
        }
    }
    Message *message = [Message getMessageWithMessageId:attachment.attachmentMessageId ctx:nil];
    AttachmentBackupFolder *attachmentBackupFolder = [[AttachmentBackupFolder alloc] init];
    attachmentBackupFolder.completionBlock = ^(File *attachmentFolder) {
        self.attachmentFolder = attachmentFolder;
        [self upload:self.attachments.firstObject];
    };
    [attachmentBackupFolder cheakAttachmentFolderWithType:message.messageType date:message.messageSendDate];
}

- (void)upload:(Attachment*)attachment {
    [self.attachmentFolder uploadAttachment:attachment force:YES];
    [self attachmentResume:attachment];
}

- (void)attachmentResume:(Attachment*)attachment {
    [self.attachments removeObject:attachment];
    if (self.attachments.count > 0) {
        [self upload:self.attachments.firstObject];
    } else {
        if (self.uploadComletion) {
            self.uploadComletion();
        }
    }
}
@end
