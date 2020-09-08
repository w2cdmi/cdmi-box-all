//
//  MessageIMAPSession.m
//  OneMail
//
//  Created by cse  on 15/10/31.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MessageIMAPSession.h"

static MessageIMAPSession *messageImapSession = nil;
@implementation MessageIMAPSession

+ (MessageIMAPSession *)getSessionInstance {
    @synchronized(self) {
        if (messageImapSession == nil) {
            messageImapSession = [[MessageIMAPSession alloc]init];
        }
    }
    return messageImapSession;
}

- (id)init {
    self = [super init];
    if (self) {

    }
    return self;
}

+ (void)clearSessionInstance {
    messageImapSession = nil;
}

@end
