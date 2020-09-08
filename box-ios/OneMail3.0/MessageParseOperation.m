//
//  MessageParseOperation.m
//  OneMail
//
//  Created by cse  on 15/10/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MessageParseOperation.h"
#import "MessageParse.h"
#import "MessageIMAPSession.h"
#import "AppDelegate.h"
#import "User+Remote.h"
#import "File+Remote.h"
#import "Session.h"
#import "Message.h"
#import "Attachment.h"


typedef void (^MultiOperationCompletion)(NSSet* succeeded, NSSet* failed);

@interface MultiOperation : NSObject

@property (nonatomic, copy) MultiOperationCompletion completionBlock;
@property (nonatomic, strong) NSSet* callingObj;
@property (nonatomic, strong) NSMutableSet* succeededSet;
@property (nonatomic, strong) NSMutableSet* failedSet;
@property (nonatomic, assign) BOOL finished;

@end

@implementation MultiOperation

- (id) init {
    if (self = [super init]) {
        _succeededSet = [[NSMutableSet alloc] init];
        _failedSet = [[NSMutableSet alloc] init];
    }
    return self;
}

- (void) onSuceess:(id) obj {
    if (obj) {
        [_succeededSet addObject:obj];
        [self checkFinished];
    }
}

- (void) onFailed:(id) obj {
    if (obj) {
        [_failedSet addObject:obj];
        [self checkFinished];
    }
}

- (void) checkFinished {
    NSMutableSet* tmpSet = [NSMutableSet setWithSet:_callingObj];
    [tmpSet minusSet:_failedSet];
    [tmpSet minusSet:_succeededSet];
    if (tmpSet.count == 0) {
        _finished = YES;
        if (_completionBlock) {
            _completionBlock(_succeededSet, _failedSet);
        }
    }
}

@end


@interface MessageParseOperation ()

@property (nonatomic, strong) NSMutableArray *messagesInfo;
@property (nonatomic, strong) NSMutableArray *successMessageArray;

@end

@implementation MessageParseOperation

- (id)initWithMessagesInfo:(NSArray*)messagesInfo {
    self = [super init];
    if (self) {
        self.messagesInfo = [[NSMutableArray alloc] initWithArray:messagesInfo];
        self.successMessageArray = [[NSMutableArray alloc] init];
    }
    return self;
}

#pragma mark parse control
- (void)parseSuccess:(id)obj {
    if (obj) {
        [self.messagesInfo removeObject:obj];
        [self checkFinish];
    }
}

- (void)parseFailed:(id)obj {
    if (obj) {
        [self.messagesInfo removeObject:obj];
        [self checkFinish];
    }
}

- (void)checkFinish {
    if (self.messagesInfo.count == 0) {
        if (self.completionBlock) {
            self.completionBlock(self.successMessageArray);
        }
    } else {
        [self parseMesage:[self.messagesInfo objectAtIndex:0]];
    }
}

#pragma mark message parse
- (void)parseMesage:(NSDictionary*)messageInfo {
    NSString *folder = [messageInfo objectForKey:@"folder"];
    NSNumber *uidnum = [messageInfo objectForKey:@"uid"];
    uint32_t uid = uidnum.intValue;
    NSLog(@"%ld:%@-%@",self.messagesInfo.count,folder,uidnum.stringValue);
    MCOIMAPFetchContentOperation *contentOperation = [[MessageIMAPSession getSessionInstance] fetchMessageOperationWithFolder:folder uid:uid];
    [contentOperation start:^(NSError *error, NSData *data) {
        if (data) {
            [self parseMessageData:data folder:folder completion:^(Message *message) {
                if (message) {
                    [self relativeMessage:message completion:^(Session *session) {
                        if (session) {
                            [self.successMessageArray addObject:message.messageId];
                            [self parseSuccess:messageInfo];
                        } else {
                            [self parseFailed:messageInfo];
                        }
                    }];
                } else {
                    [self parseFailed:messageInfo];
                }
            }];
        } else {
            [self parseFailed:messageInfo];
            NSLog(@"data nil");
        }
    }];
}


- (void)parseMessageData:(NSData *)data folder:(NSString *)folder completion:(void(^)(Message *message))completion {
    MessageParse *messageParse = [[MessageParse alloc] init];
    NSDictionary *messageParseResult = [messageParse messageParse:data];
    Message *message = [messageParseResult objectForKey:@"Message"];
    NSArray *messageAttachments = [messageParseResult objectForKey:@"Attachments"];
    
    if (!message) {
        completion(nil);return;
    }
    
    if ([folder isEqualToString:@"INBOX"]) {
        [message saveMessageType:@(MessageReceive)];
    } else {
        [message saveMessageType:@(MessageSent)];
    }
    
    Attachment *messageImageAttachment;
    for (Attachment *attachment in messageAttachments) {
        if ([CommonFunction isImageResource:attachment.attachmentName]) {
            [CommonFunction imageCompressFromPath:[attachment attachmentDataLocalPath] toPath:[attachment attachmentCompressImagePath]];
            if (attachment.attachmentType.integerValue == AttachmentInLine && !messageImageAttachment) {
                messageImageAttachment = attachment;
            }
        }
    }
    if (messageImageAttachment) {
        [messageImageAttachment saveAttachmentDisplayFlag:@(1)];
        NSString *messageImageLocalPath = [message messageImageLocalPath];
        NSString *attachmentCompressImagePath = [messageImageAttachment attachmentCompressImagePath];
        if (attachmentCompressImagePath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentCompressImagePath]) {
            if (messageImageLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:messageImageLocalPath]) {
                [[NSFileManager defaultManager] removeItemAtPath:messageImageLocalPath error:nil];
            }
            [[NSFileManager defaultManager] copyItemAtPath:attachmentCompressImagePath toPath:messageImageLocalPath error:nil];
        }
    }
    
    NSArray *oneboxAttachmentArray = [self getOneboxAttachmentLink:message.messagePlainContent];
    if (oneboxAttachmentArray.count > 0) {
        [message saveMessageBody:[message.messagePlainContent componentsSeparatedByString:@"这是来自Onebox的文件"].firstObject];
        MultiOperation *operation = [[MultiOperation alloc] init];
        operation.completionBlock = ^(NSSet *success, NSSet *failed) {
            completion(message);
        };
        operation.callingObj = [NSSet setWithArray:oneboxAttachmentArray];
        for (NSString *oneboxAttachmentLink in oneboxAttachmentArray) {
            [self saveOneboxAttachment:oneboxAttachmentLink messageId:message.messageId completion:^(NSError *error) {
                if (error) {
                    [operation onFailed:oneboxAttachmentLink];
                } else {
                    [operation onSuceess:oneboxAttachmentLink];
                }
            }];
        }
    } else {
        completion(message);
    }
}

#pragma mark onebox attachment
- (NSArray *)getOneboxAttachmentLink:(NSString *)text{
    if (!text) {
        return nil;
    }
    NSArray *stringArray = [text componentsSeparatedByString:@"这是来自Onebox的文件"];
    NSMutableArray *linkArray = [[NSMutableArray alloc] init];
    for (NSString *substring in stringArray) {
        if ([stringArray indexOfObject:substring] == 0) {
            continue;
        }
        NSRange range1 = [substring rangeOfString:@"("];
        NSRange range2 = [substring rangeOfString:@")"];
        if (range1.length != 0) {
            NSString *link = [substring substringWithRange:NSMakeRange(range1.location + 1, range2.location - range1.location - 1)];
            [linkArray addObject:link];
        }
    }
    return linkArray;
}

- (void)saveOneboxAttachment:(NSString *)shareLink messageId:(NSString*)messageId completion:(void(^)(NSError *error))completion {
    NSString *oneboxAttachmentLinkId = [shareLink componentsSeparatedByString:@"/"].lastObject;
    [File fileLinkObject:oneboxAttachmentLinkId succeed:^(id retobj) {
        NSDictionary *fileInfo = [retobj objectForKey:@"file"];
        if (!fileInfo) {
            NSError *error = [[NSError alloc] initWithDomain:NSURLErrorDomain code:NSNotFound userInfo:@{NSLocalizedDescriptionKey:@"Failed to get link file"}];
            completion(error);
        } else {
            AppDelegate *delegate =[UIApplication sharedApplication].delegate;
            NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
            NSMutableDictionary *attachmentInfo = [[NSMutableDictionary alloc] init];
            [attachmentInfo setObject:oneboxAttachmentLinkId forKey:@"attachmentId"];
            [attachmentInfo setObject:messageId forKey:@"attachmentMessageId"];
            [attachmentInfo setObject:[fileInfo objectForKey:@"name"] forKey:@"attachmentName"];
            [attachmentInfo setObject:@([[fileInfo objectForKey:@"size"] longLongValue]) forKey:@"attachmentSize"];
            [attachmentInfo setObject:@(AttachmentOnebox) forKey:@"attachmentType"];
            [attachmentInfo setObject:@(1) forKey:@"attachmentUploadFlag"];
            [attachmentInfo setObject:[fileInfo objectForKey:@"id"] forKey:@"attachmentFileId"];
            [attachmentInfo setObject:[fileInfo objectForKey:@"ownedBy"] forKey:@"attachmentFileOwner"];
            [ctx performBlockAndWait:^{
                [Attachment attachmentInsertWithInfo:attachmentInfo ctx:ctx];
                [ctx save:nil];
                completion(nil);
            }];
        }
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        completion(error);
    }];
}

#pragma mark message+session
- (void)relativeMessage:(Message*)message completion:(void(^)(Session *session))completion {
    __block Session *session;
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    UserSetting *userSetting = [UserSetting defaultSetting];
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    
    if (message.messageSessionId) {
        session = [Session getSessionWithSessionId:message.messageSessionId ctx:nil];
    }
    
    if (!session) {
        if (message.messageReferenceId) {
            Message *referenceMessage = [Message getMessageWithMessageId:message.messageReferenceId ctx:nil];
            session = [Session getSessionWithSessionId:referenceMessage.messageSessionId ctx:nil];
        }
    }
    
    NSMutableArray *sessionUserArray = [[NSMutableArray alloc] init];
    NSMutableArray *sessionUserEmailArray = [[NSMutableArray alloc] init];
    [sessionUserEmailArray addObject:message.messageSender];
    [sessionUserEmailArray addObjectsFromArray:[message.messageReceiver componentsSeparatedByString:@","]];
    NSString *sessionUserString = [CommonFunction stringFromArray:sessionUserEmailArray];
    
    if (!session) {
        session = [Session getSessionWithSessionUsers:sessionUserString ctx:nil];
    }
    
    if (!session) {
        [ctx performBlockAndWait:^{
            session = [Session sessionInsertWithSessionId:[userSetting.emailNextSessionId stringValue] ctx:ctx];
            userSetting.emailNextSessionId = @(userSetting.emailNextSessionId.integerValue+1);
            [ctx save:nil];
        }];
    }

    if (session) {
        session = (Session*)[appDelegate.localManager.managedObjectContext objectWithID:session.objectID];
    } else {
        completion(nil);return;
    }
    
    [session saveSessionLastMessageId:message];
    
    [message saveMessageSessionId:session.sessionId];
    [message saveMessageOwner:session.sessionOwner];
    
    [sessionUserEmailArray removeObject:userSetting.emailAddress];
    
    session.sessionUsers = sessionUserString;
    [session saveSessionUsers:sessionUserString];
    
    MultiOperation *operation = [[MultiOperation alloc] init];
    operation.completionBlock = ^(NSSet *success,NSSet *failed){
        [session saveSessionTitle:[CommonFunction stringFromArray:sessionUserArray]];
        completion(session);
    };
    operation.callingObj = [NSSet setWithArray:sessionUserEmailArray];
    for (NSString *address in sessionUserEmailArray) {
        __block User *user = [User getUserWithUserEmail:address context:nil];
        if (user && user.userSingleId) {
            if (message.messageType.integerValue == MessageSent) {
                [user changeUserRecentContactFlag:@(1)];
            }
            [sessionUserArray addObject:user.userName];
            [operation onSuceess:address];
        } else {
            [User searchUser:address succeed:^(id retobj) {
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                user = [User getUserWithUserEmail:address context:nil];
                if (!user) {
                    [ctx performBlockAndWait:^{
                        user = [User userInsertWithEmail:address context:ctx];
                        [ctx save:nil];
                    }];
                }
                if (user) {
                    if (message.messageType.integerValue == MessageSent) {
                        [user changeUserRecentContactFlag:@(1)];
                    }
                    [sessionUserArray addObject:user.userName];
                    [operation onSuceess:address];
                } else {
                    [operation onFailed:address];
                }
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                [ctx performBlockAndWait:^{
                    user = [User userInsertWithEmail:address context:ctx];
                    [ctx save:nil];
                }];
                if (user) {
                    if (message.messageType.integerValue == MessageSent) {
                        [user changeUserRecentContactFlag:@(1)];
                    }
                    [sessionUserArray addObject:user.userName];
                    [operation onSuceess:address];
                } else {
                    [operation onFailed:address];
                }
            }];
        }
    }
}

@end
