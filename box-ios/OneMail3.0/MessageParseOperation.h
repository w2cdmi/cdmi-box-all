//
//  MessageParseOperation.h
//  OneMail
//
//  Created by cse  on 15/10/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Message;
@class Session;

typedef void (^MessageParseCompletion)(NSArray *successArray);

@interface MessageParseOperation : NSObject

@property (nonatomic, strong) MessageParseCompletion completionBlock;

- (id)initWithMessagesInfo:(NSArray*)messagesInfo;
- (void)parseMesage:(NSDictionary*)messageInfo;
- (void)parseMessageData:(NSData *)data folder:(NSString *)folder completion:(void(^)(Message *message))completion;
- (void)relativeMessage:(Message*)message completion:(void(^)(Session *session))completion;

@end
