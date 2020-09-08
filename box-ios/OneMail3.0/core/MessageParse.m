//
//  MessageParse.m
//  OneMail
//
//  Created by cse  on 15/10/31.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MessageParse.h"
#import "AppDelegate.h"
#import "Attachment.h"
#import "Message.h"

@implementation MessageParse

- (NSDictionary*)messageParse:(NSData *)emlData {
    MCOMessageParser *messageParse = [[MCOMessageParser alloc] initWithData:emlData];
    
    NSMutableDictionary *messageInfo = [[NSMutableDictionary alloc] init];
    NSMutableArray *messageAttachments = [[NSMutableArray alloc] init];
    
    [messageInfo setObject:messageParse.header.messageID forKey:@"messageId"];
    
    if (messageParse.header.references) {
        [messageInfo setObject:messageParse.header.references.lastObject forKey:@"messageReferenceId"];
    }
    
    [messageInfo setObject:messageParse.header.from.mailbox forKey:@"messageSender"];
    if (![CommonFunction checkMailAccountForm:messageParse.header.from.mailbox]) {
        return nil;
    }
    
    NSMutableArray *messageReceiverArray = [[NSMutableArray alloc] init];
    for (MCOAddress *address in messageParse.header.to) {
        if (![messageReceiverArray containsObject:address.mailbox]) {
            [messageReceiverArray addObject:address.mailbox];
        }
    }
    for (MCOAddress *address in messageParse.header.cc) {
        if (![messageReceiverArray containsObject:address.mailbox]) {
            [messageReceiverArray addObject:address.mailbox];
        }
    }
    if (messageReceiverArray.count > 0) {
        [messageInfo setObject:[CommonFunction stringFromArray:messageReceiverArray] forKey:@"messageReceiver"];
    }
    
    NSString *mailTitle = messageParse.header.subject;
    if (!mailTitle || [mailTitle isEqualToString:@""] || [mailTitle isEqual:@"(null)"]) {
        mailTitle = [messageParse plainTextBodyRenderingAndStripWhitespace:NO];
        mailTitle = [mailTitle stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        mailTitle = [mailTitle componentsSeparatedByString:@"\n"].firstObject;
        mailTitle = [mailTitle stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    }
    if (mailTitle && ![mailTitle isEqualToString:@""]) {
        [messageInfo setObject:mailTitle forKey:@"messageTitle"];
    }
    
    if (messageParse.htmlBodyRendering) {
        [messageInfo setObject:messageParse.htmlBodyRendering forKey:@"messageHTMLContent"];
    }

    NSString *plainText;
    if (messageParse.plainTextBodyRendering) {
        plainText = [messageParse.plainTextBodyRendering componentsSeparatedByString:@"发件人:"].firstObject;//plainText去掉历史邮件
        //plainText = [plainText componentsSeparatedByString:@"这是来自Onebox的文件"].firstObject;//plainText去掉链接
        if (plainText) {
            [messageInfo setObject:plainText forKey:@"messagePlainContent"];
        }
    }
    
    [messageInfo setObject:messageParse.header.date forKey:@"messageSenderDate"];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    __block Message *message;
    [ctx performBlockAndWait:^{
        message = [Message messageInsertWithInfo:messageInfo ctx:ctx];
        [ctx save:nil];
    }];
    if (message) {
        message = (Message*)[appDelegate.localManager.managedObjectContext objectWithID:message.objectID];
    } else {
        return nil;
    }
    
    for (MCOAttachment *inlineattachmentParse in messageParse.htmlInlineAttachments) {
        Attachment *attachment = [self attachmentParse:inlineattachmentParse message:messageParse];
        if (attachment) {
            [messageAttachments addObject:attachment];
        }
    }
    for (MCOAttachment *attachmentParse in messageParse.attachments) {
        Attachment *attachment = [self attachmentParse:attachmentParse message:messageParse];
        if (attachment) {
            [messageAttachments addObject:attachment];
        }
    }
    
    NSDictionary *result = @{@"Message":message,@"Attachments":messageAttachments};
    return result;
}

- (Attachment*)attachmentParse:(MCOAttachment *)attachmentParse message:(MCOMessageParser*)messageParse {
    NSMutableDictionary *attachmentInfo = [[NSMutableDictionary alloc] init];

    if (attachmentParse.contentID) {
        [attachmentInfo setObject:attachmentParse.contentID forKey:@"attachmentId"];
    } else {
        return nil;
    }
    
    NSData *attachmentData = attachmentParse.data;
    
    [attachmentInfo setObject:attachmentParse.filename forKey:@"attachmentName"];
    [attachmentInfo setObject:@(attachmentData.length) forKey:@"attachmentSize"];
    
    [attachmentInfo setObject:messageParse.header.messageID forKey:@"attachmentMessageId"];
    
    if (attachmentParse.inlineAttachment) {
        [attachmentInfo setObject:@(AttachmentInLine) forKey:@"attachmentType"];
    } else {
        [attachmentInfo setObject:@(AttachmentNormal) forKey:@"attachmentType"];
    }
    
    [attachmentInfo setObject:@(0) forKey:@"attachmentUploadFlag"];
    
    NSString *attachmentCachePath = [Attachment attachmentDataCachePathWithId:attachmentParse.contentID name:attachmentParse.filename];
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentCachePath]) {
        [[NSFileManager defaultManager] createFileAtPath:attachmentCachePath contents:attachmentData attributes:nil];
    }
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    __block Attachment *attachment;
    [ctx performBlockAndWait:^{
        attachment = [Attachment attachmentInsertWithInfo:attachmentInfo ctx:ctx];
        [ctx save:nil];
    }];
    if (attachment) {
        return (Attachment*)[appDelegate.localManager.managedObjectContext objectWithID:attachment.objectID];
    } else {
        return nil;
    }
}

#pragma mark MCOHTMLRendererDelegate
- (BOOL) MCOAbstractMessage:(MCOAbstractMessage *)msg canPreviewPart:(MCOAbstractPart *)part{
    if (part.inlineAttachment == YES) {
        return YES;
    }
    return NO;
}
- (BOOL) MCOAbstractMessage:(MCOAbstractMessage *)msg shouldShowPart:(MCOAbstractPart *)part{
    return YES;
    
}
- (NSString *) MCOAbstractMessage:(MCOAbstractMessage *)msg templateForImage:(MCOAbstractPart *)header{
    MCOAttachment *attachment = (MCOAttachment *)header;
    NSData *data = [attachment data];
    NSString *cachepath = [NSString stringWithFormat:@"/Users/cse/Desktop/"];
    NSString *name = attachment.filename;
    NSString *path = [cachepath stringByAppendingPathComponent:name];
    [data writeToFile:path atomically:YES];
    return [NSString stringWithFormat:@"<img src = \"%@\"'/>",path];
}

@end
