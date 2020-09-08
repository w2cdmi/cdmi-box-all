//
//  MessageLoadOperation.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MessageLoadOperation.h"
#import "MessageIMAPSession.h"
#import "UserSetting.h"

@interface MessageLoadOperation ()

@property (atomic) BOOL inboxFinish;
@property (atomic) BOOL sentFinish;

@end

@implementation MessageLoadOperation

- (id)init {
    self = [super init];
    if (self) {
        self.inboxFinish = NO;
        self.sentFinish = NO;
    }
    return self;
}

- (void)checkFinish {
    if (self.inboxFinish && self.sentFinish) {
        if (self.completionBlock) {
            self.completionBlock(self.inboxMessages,self.sentMessages);
        }
    }
}

- (void)loadMessages:(NSString *)folder completion:(void (^)(NSMutableArray *))completionBlock {
    
    UserSetting *usersettting = [UserSetting defaultSetting];
    MessageIMAPSession *imapSession = [MessageIMAPSession getSessionInstance];
    
    MCOIMAPMessagesRequestKind requestKind = (MCOIMAPMessagesRequestKind)
    (MCOIMAPMessagesRequestKindHeaders | MCOIMAPMessagesRequestKindStructure |
     MCOIMAPMessagesRequestKindInternalDate | MCOIMAPMessagesRequestKindHeaderSubject |
     MCOIMAPMessagesRequestKindFlags);
    
    MCOIMAPFolderInfoOperation *FolderInfo = [imapSession folderInfoOperation:folder];
    [FolderInfo start:^(NSError *error, MCOIMAPFolderInfo *info) {
        uint32_t start_uid = 0;
        if ([folder isEqualToString:@"INBOX"]) {
            start_uid = usersettting.emailLastInboxUid.intValue;
        }
        if ([folder isEqualToString:@"Sent Messages"]) {
            start_uid = usersettting.emailLastSentBoxUid.intValue;
        }
         
        MCOIMAPFetchMessagesOperation *fetchMessageOP = [imapSession fetchMessagesOperationWithFolder:folder requestKind:requestKind uids:[MCOIndexSet indexSetWithRange:MCORangeMake(start_uid, info.uidNext)]];
        [fetchMessageOP start:^(NSError *error, NSArray *messages, MCOIndexSet *vanishedMessages) {
            NSMutableArray *messageInfos = [[NSMutableArray alloc] init];
            for (MCOIMAPMessage *message in messages) {
                NSDictionary *messageInfo = [NSDictionary dictionaryWithObjectsAndKeys:@(message.uid),@"uid",folder,@"folder",message.header.date,@"date", nil];
                [messageInfos  addObject:messageInfo];
            }
            MCOIMAPMessage *lastmessage = messages.lastObject;
            if ([folder isEqualToString:@"INBOX"]) {
                self.inboxFinish = YES;
                self.inboxMessages = messageInfos;
                if (lastmessage) {
                    usersettting.emailLastInboxUid = [NSNumber numberWithInt:lastmessage.uid + 1];
                }
            }
            if ([folder isEqualToString:@"Sent Messages"]) {
                self.sentFinish = YES;
                self.sentMessages = messageInfos;
                if (lastmessage) {
                    usersettting.emailLastSentBoxUid = [NSNumber numberWithInt:lastmessage.uid + 1];
                }
            }
            if (completionBlock) {
                completionBlock(messageInfos);
            }
            [self checkFinish];
        }];
    }];
}

@end
