//
//  Message.m
//  OneMail
//
//  Created by cse  on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "Message.h"
#import "AppDelegate.h"
#import "Attachment.h"
#import "Session.h"
#define MAXSIZE 1024*1024*1024

@implementation Message

@dynamic messageHTMLContent;
@dynamic messageId;
@dynamic messagePlainContent;
@dynamic messageReadFlag;
@dynamic messageReceiveDate;
@dynamic messageReceiver;
@dynamic messageReferenceId;
@dynamic messageSender;
@dynamic messageSendDate;
@dynamic messageSessionId;
@dynamic messageTitle;
@dynamic messageType;
@dynamic messageOwner;

- (NSString *)getMessageContent {
    NSMutableString *content = [[NSMutableString alloc] init];
    [content appendFormat:@"<hr />"];
    
    [content appendFormat:@"<b>发件人:</b>%@<br/>",self.messageSender];
    [content appendFormat:@"<b>收件人:</b>%@<br/>",self.messageReceiver];
    
    NSDateFormatter *dateFormatter =[[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"YYYY/MM/dd HH:mm"];
    NSString* date = [dateFormatter stringFromDate:[NSDate date]];
    [content appendFormat:@"<b>发送时间:</b>%@<br/>",date];
    
    [content appendFormat:@"<b>主题:</b>%@<br/>",self.messageTitle];
    [content appendFormat:@"<br/>"];
    [content appendFormat:@"<body>%@<body/>",self.messageHTMLContent];
    
    return content;
}

+ (Message *)getMessageWithMessageId:(NSString *)messageId ctx:(NSManagedObjectContext*)ctx {
    if (!ctx) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"messageId = %@",messageId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"messageId" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [[ctx executeFetchRequest:request error:nil] lastObject];
}

+ (NSArray*)getMessagesWithSessionId:(NSString *)sessionId ctx:(NSManagedObjectContext *)ctx {
    if (!ctx) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"messageSessionId = %@",sessionId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"messageId" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [ctx executeFetchRequest:request error:nil];
}

+ (Message*)messageInsertWithInfo:(NSDictionary *)messageInfo ctx:(NSManagedObjectContext *)ctx {
    NSString *messageId = [messageInfo objectForKey:@"messageId"];
    Message *message = [Message getMessageWithMessageId:messageId ctx:ctx];
    if (!message) {
        message = [NSEntityDescription insertNewObjectForEntityForName:@"Message" inManagedObjectContext:ctx];
    }
    message.messageId = messageId;
    message.messageReferenceId = [messageInfo objectForKey:@"messageReferenceId"];
    message.messageSessionId = [messageInfo objectForKey:@"messageSessionId"];
    message.messageSender = [messageInfo objectForKey:@"messageSender"];
    message.messageSendDate = [messageInfo objectForKey:@"messageSenderDate"];
    message.messageReceiver = [messageInfo objectForKey:@"messageReceiver"];
    message.messageReceiveDate = [NSDate date];
    message.messagePlainContent = [messageInfo objectForKey:@"messagePlainContent"];
    message.messageHTMLContent = [messageInfo objectForKey:@"messageHTMLContent"];
    message.messageTitle = [messageInfo objectForKey:@"messageTitle"];
    message.messageType = [messageInfo objectForKey:@"messageType"];
    if (message.messageType.integerValue == MessageSent) {
        message.messageReadFlag = @(1);
    } else {
        if ([[[UserSetting defaultSetting] emailFirstLoad] boolValue]) {
            message.messageReadFlag = @(1);
        } else {
            message.messageReadFlag = @(0);
        }
    }
    
    NSString *messageImagePath = [messageInfo objectForKey:@"messageImagePath"];
    NSString *messageImageLocalPath = [message messageImageLocalPath];
    if (messageImagePath && [[NSFileManager defaultManager] fileExistsAtPath:messageImagePath]) {
        if (messageImageLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:messageImageLocalPath]) {
            [[NSFileManager defaultManager] removeItemAtPath:messageImageLocalPath error:nil];
        }
        [[NSFileManager defaultManager] moveItemAtPath:messageImagePath toPath:messageImageLocalPath error:nil];
    }
    return message;
}

- (void)removeMessage {
    NSArray *attachments = [Attachment getAttachmentWithMessageId:self.messageId ctx:self.managedObjectContext];
    for (Attachment *attachment in attachments) {
        [attachment removeAttachment];
    }
    Session *session = [Session getSessionWithSessionId:self.messageSessionId ctx:self.managedObjectContext];
    if ([session.sessionLastMessageId isEqualToString:self.messageId]) {
        session.sessionLastMessageId = self.messageReferenceId;
    }
    [self.managedObjectContext deleteObject:self];
}

- (NSString*)messageImageLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *messageDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Message"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:messageDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:messageDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *messageImageDirectory = [messageDirectory stringByAppendingString:@"Image"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:messageImageDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:messageImageDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [messageImageDirectory stringByAppendingPathComponent:self.messageId];
}

- (void)saveMessageSessionId:(NSString *)messageSessionId {
    if ([self.messageSessionId isEqualToString:messageSessionId]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Message *shadow = (Message*)[ctx objectWithID:self.objectID];
        shadow.messageSessionId = messageSessionId;
        [ctx save:nil];
    }];
}

- (void)saveMessageType:(NSNumber *)messageType {
    if (self.messageType.boolValue == messageType.boolValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Message *shadow = (Message*)[ctx objectWithID:self.objectID];
        shadow.messageType = messageType;
        [ctx save:nil];
        
    }];
}

- (void)saveMessageOwner:(NSString *)messageOwner {
    if ([self.messageOwner isEqualToString: messageOwner]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Message *shadow = (Message*)[ctx objectWithID:self.objectID];
        shadow.messageOwner = messageOwner;
        [ctx save:nil];
    }];
}

- (void)saveMessageBody:(NSString *)messageBody {
    if ([self.messagePlainContent isEqualToString: messageBody]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Message *shadow = (Message*)[ctx objectWithID:self.objectID];
        shadow.messagePlainContent = messageBody;
        [ctx save:nil];
    }];
}

- (void)saveMessageRead:(NSNumber *)messageReadFlag {
    if (self.messageReadFlag.boolValue == messageReadFlag.boolValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Message *shadow = (Message*)[ctx objectWithID:self.objectID];
        shadow.messageReadFlag = messageReadFlag;
        [ctx save:nil];
    }];
}

+ (NSInteger)getMessageUnreadCount {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *prediecte = [NSPredicate predicateWithFormat:@"messageReadFlag = %@ AND messageOwner = %@",@(0),appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"messageReceiveDate" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:prediecte];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    NSArray *array = [ctx executeFetchRequest:request error:nil];
    return array.count;
}

@end
