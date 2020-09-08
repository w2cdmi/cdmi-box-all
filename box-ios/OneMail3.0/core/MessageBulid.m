//
//  MessageBulid.m
//  OneMail
//
//  Created by cse  on 15/10/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MessageBulid.h"

@implementation MessageBulid

+ (void)buildMessageWithInfo:(NSDictionary *)messageInfo messageData:(EmlData)messageDataBlock {
    NSString *messageTitle = [messageInfo objectForKey:@"messageTitle"];
    NSString *messageBody = [messageInfo objectForKey:@"messageBody"];
    NSArray  *messageReceiver = [messageInfo objectForKey:@"messageReceiver"];
    NSString *messageSender= [messageInfo objectForKey:@"messageSender"];
    NSArray  *messageAttachmentsInline = [messageInfo objectForKey:@"messageInlineAttachmentsInfo"];
    NSArray  *messageAttachments = [messageInfo objectForKey:@"messageAttachmentsInfo"];
    NSString *messageReferenceId = [messageInfo objectForKey:@"messageReferenceId"];
    NSString *messageAttachmentHTMLString = [messageInfo objectForKey:@"messageAttachmentHTMLString"];
    MCOMessageBuilder *builder = [[MCOMessageBuilder alloc] init];
    if (messageReferenceId) {
        builder.header.references = @[messageReferenceId];
    }
    MCOAddress *fromAddress = [MCOAddress addressWithDisplayName:nil mailbox:messageSender];
    
    NSMutableArray *addressArray = [[NSMutableArray alloc] init];
    for (NSString *address in messageReceiver) {
        MCOAddress *toAddress = [MCOAddress addressWithDisplayName:nil mailbox:address];
        [addressArray addObject:toAddress];
    }
    
    [[builder header] setFrom:fromAddress];
    [[builder header] setTo:addressArray];
    
    if (messageTitle) {
        [[builder header] setSubject:[NSString stringWithCString:[messageTitle UTF8String] encoding:NSUTF8StringEncoding]];
    }
    
    if (messageBody) {
        if (messageAttachmentHTMLString) {
            NSMutableString *content = [[NSMutableString alloc] init];
            [content appendFormat:@"%@ <br/> %@",messageBody,messageAttachmentHTMLString];
            [builder setHTMLBody:content];
        }
        else{
            [builder setHTMLBody:messageBody];
        }
        
    } else {
        if (messageAttachmentHTMLString) {
            [builder setHTMLBody:messageAttachmentHTMLString];
        }
    }
    for (NSDictionary *attachmentInfo in messageAttachmentsInline) {
        MCOAttachment *attachment = [MCOAttachment attachmentWithData:[attachmentInfo objectForKey:@"attachmentData"] filename:[attachmentInfo objectForKey:@"attachmentName"]];
        if ([attachmentInfo objectForKey:@"attachmentContentId"]) {
            attachment.contentID = [attachmentInfo objectForKey:@"attachmentContentId"];
        }
        attachment.inlineAttachment = YES;
        [builder addRelatedAttachment:attachment];
    }
    for (NSDictionary *attachmentInfo in messageAttachments) {
        MCOAttachment *attachment = [MCOAttachment attachmentWithData:[attachmentInfo objectForKey:@"attachmentData"] filename:[attachmentInfo objectForKey:@"attachmentName"]];
        if ([attachmentInfo objectForKey:@"attachmentContentId"]) {
            attachment.contentID = [attachmentInfo objectForKey:@"attachmentContentId"];
        }
        attachment.inlineAttachment = NO;
        [builder addRelatedAttachment:attachment];
    }
    
    NSData * messageData = [builder data];
    if (messageDataBlock) {
        messageDataBlock(messageData);
    }
}

@end
