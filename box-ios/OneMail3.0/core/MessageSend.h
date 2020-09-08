//
//  MessageSend.h
//  OneMail
//
//  Created by cse  on 15/10/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Session;
@class Message;

@interface MessageSend : NSObject

+ (MessageSend *)shareMessageSend;
- (void)sendMessage:(NSDictionary*)messageInfo;
- (void)forwardMessage:(Message *)forwardMessage Session:(Session *)session;
- (void)forwardShareLink:(NSString *)shareLink Session:(Session *)forwardSession;
@end
