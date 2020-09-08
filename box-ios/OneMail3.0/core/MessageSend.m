//
//  MessageSend.m
//  OneMail
//
//  Created by cse  on 15/10/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MessageSend.h"
#import "AppDelegate.h"
#import "MCOSMTPSession.h"
#import "MessageIMAPSession.h"
#import "MessageBulid.h"
#import "MessageParseOperation.h"
#import "Attachment.h"
#import "Message.h"
#import "Session.h"

@interface MessageSend()

@property (nonatomic, strong) NSMutableArray *sendQueue;
@property (nonatomic, assign) BOOL mailSending;

@end

static MessageSend *messageSend = nil;

@implementation MessageSend

+ (MessageSend *)shareMessageSend {
    if (!messageSend) {
        messageSend = [[MessageSend alloc] init];
    }
    return messageSend;
}

- (id)init {
    if (self = [super init]) {
        self.mailSending = NO;
        self.sendQueue = [[NSMutableArray alloc] init];
        [self addObserver:self forKeyPath:@"mailSending" options:NSKeyValueObservingOptionNew context:nil];
    }
    return self;
}

- (void)forwardMessage:(Message *)forwardMessage Session:(Session *)forwardSession{
    NSMutableDictionary *messageInfo = [[NSMutableDictionary alloc] init];
    UserSetting *userSetting = [UserSetting defaultSetting];
    NSString *substring;
    if (forwardMessage.messagePlainContent.length > 2) {
        substring = [forwardMessage.messagePlainContent substringToIndex:3];
    }
    if (substring && (![substring isEqualToString:@"转发:"])) {
        NSMutableString *mailbody = [NSMutableString stringWithFormat:@"<b>转发:<b/><br/>%@",forwardMessage.messageHTMLContent];
        [messageInfo setObject:mailbody forKey:@"messageBody"];
        [messageInfo setObject:[NSString stringWithFormat:@"转发:%@",forwardMessage.messageTitle] forKey:@"mailTitle"];
    }
    else{
        [messageInfo setObject:forwardMessage.messageHTMLContent forKey:@"messageBody"];
        [messageInfo setObject:forwardMessage.messageTitle forKey:@"messageTitle"];
    }
    [messageInfo setObject:userSetting.emailAddress forKey:@"messageSender"];
    [messageInfo setObject:forwardSession.sessionId forKey:@"messageSessionId"];
    NSMutableArray *messageReceiverArray = [[NSMutableArray alloc] initWithArray:[forwardSession.sessionUsers componentsSeparatedByString:@","]];
    [messageReceiverArray removeObject:userSetting.emailAddress];
    [messageInfo setObject:messageReceiverArray forKey:@"messageReceiver"];
    
    NSArray *attachments = [Attachment getAttachmentWithMessageId:forwardMessage.messageId ctx:nil];
    NSMutableArray *attachmentsInfo = [[NSMutableArray alloc] init];
    NSMutableArray *inlineAttachmentsInfo = [[NSMutableArray alloc] init];
    for (Attachment *attachment in attachments) {
        NSString *attachmentDataLocalPath = [attachment attachmentDataLocalPath];
        NSData *attachmentData = [NSData dataWithContentsOfFile:attachmentDataLocalPath];
        if (!attachmentData) {
            continue;
        }
        if (attachment.attachmentType.integerValue == AttachmentInLine) {
            NSDictionary *attachmentInfo = [NSDictionary dictionaryWithObjectsAndKeys:attachmentData,@"attachmentData",attachment.attachmentName,@"attachmentName",attachment.attachmentId,@"attachmentContentId", nil];
            [inlineAttachmentsInfo addObject:attachmentInfo];
            
        }
        if (attachment.attachmentType.integerValue == AttachmentNormal) {
            NSDictionary *attachmentInfo = [NSDictionary dictionaryWithObjectsAndKeys:attachmentData,@"attachmentData",attachment.attachmentName,@"attachmentName",attachment.attachmentId,@"attachmentContentId", nil];
            [attachmentsInfo addObject:attachmentInfo];
        }
    }
    if (attachmentsInfo.count > 0) {
        [messageInfo setObject:attachmentsInfo forKey:@"messageAttachmentsInfo"];
    }
    if (inlineAttachmentsInfo.count > 0) {
        [messageInfo setObject:inlineAttachmentsInfo forKey:@"messageInlineAttachmentsInfo"];
    }
    [self sendMessage:messageInfo];
}
- (void)forwardShareLink:(NSString *)shareLink Session:(Session *)forwardSession{
    NSMutableDictionary *messageInfo = [[NSMutableDictionary alloc] init];
    UserSetting *userSetting = [UserSetting defaultSetting];
    [messageInfo setObject:[shareLink componentsSeparatedByString:@","].lastObject forKey:@"messageBody"];
    [messageInfo setObject:[shareLink componentsSeparatedByString:@","].firstObject forKey:@"messageTitle"];
    [messageInfo setObject:userSetting.emailAddress forKey:@"messageSender"];
    [messageInfo setObject:forwardSession.sessionId forKey:@"messageSessionId"];
    NSMutableArray *messageReceiverArray = [[NSMutableArray alloc] initWithArray:[forwardSession.sessionUsers componentsSeparatedByString:@","]];
    [messageReceiverArray removeObject:userSetting.emailAddress];
    [messageInfo setObject:messageReceiverArray forKey:@"messageReceiver"];
    [self sendMessage:messageInfo];
}

- (void)sendMessage:(NSDictionary *)messageInfo {
    [MessageBulid buildMessageWithInfo:messageInfo messageData:^(NSData *messageData) {
        MessageParseOperation *parseOperation = [[MessageParseOperation alloc] initWithMessagesInfo:@[messageInfo]];
        [parseOperation parseMessageData:messageData folder:@"Sent Messages" completion:^(Message *message) {
            NSDictionary *dataInfo = [NSDictionary dictionaryWithObjectsAndKeys:message.messageId,@"messageId",messageData,@"emlData", nil];
            if (self.mailSending) {
                [self.sendQueue addObject:dataInfo];
            } else {
                [self sendData:dataInfo];
            }
            [parseOperation relativeMessage:message completion:^(Session *session) {
                
            }];
        }];
    }];
}

- (void)sendData:(NSDictionary *)dataInfo {
    self.mailSending = YES;
    NSData *emlData = [dataInfo objectForKey:@"emlData"];
    UserSetting *userSetting = [UserSetting defaultSetting];
    MCOSMTPSession *smtpSession = [[MCOSMTPSession alloc] init];
    smtpSession.hostname = userSetting.emailServerSend;
    smtpSession.port = userSetting.emailPortSend.intValue;
    smtpSession.username = userSetting.emailAddress;
    smtpSession.password = userSetting.emailPassword;
    smtpSession.authType = MCOAuthTypeSASLLogin;
    smtpSession.connectionType = MCOConnectionTypeClear;
    [smtpSession setCheckCertificateEnabled:NO];
    MCOSMTPSendOperation *sendOperation = [smtpSession sendOperationWithData:emlData];
    [sendOperation start:^(NSError *error) {
        if (!error) {
            MessageIMAPSession *imapSession = [MessageIMAPSession getSessionInstance];
            [imapSession.SentBoxIdleOperation interruptIdle];
            MCOIMAPAppendMessageOperation * op = [imapSession appendMessageOperationWithFolder:@"Sent Messages" messageData:emlData flags:MCOMessageFlagNone];
            [op start:^(NSError * error, uint32_t createdUID) {
                if (!error) {
                    NSLog(@"成功保存到发件箱");
                }
            }];
        } else {
            [[UIApplication sharedApplication].keyWindow makeToast:@"发送失败"];
        }
        self.mailSending = NO;
    }];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context{
    NSString *mailSending = [change objectForKey:@"new"];
    if (mailSending.integerValue == 0) {
        if (self.sendQueue.count > 0) {
            [self sendData:self.sendQueue.firstObject];
            [self.sendQueue removeObject:self.sendQueue.firstObject];
        }
    }
}

@end
