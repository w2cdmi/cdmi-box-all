//
//  STMCOPOPSession.m
//  StoryClient
//
//  Created by mengxianzhi on 15-5-20.
//  Copyright (c) 2015年 LiuQi. All rights reserved.
//

#import "STMCOPOPSession.h"
//#import "STEmailData+Oper.h"
static STMCOPOPSession *popSession = nil;

@implementation STMCOPOPSession

+ (STMCOPOPSession *)getSessionInstanct{
    @synchronized(self) {
        if (popSession == nil) {
            popSession = [[STMCOPOPSession alloc]init];
        }
    }
    return popSession;
}

- (void)clearSesstionInstance{
    popSession = nil;
}

- (void)loadAccountWithUsername:(NSString *)username password:(NSString *)password hostname:(NSString *)hostname loadUserResultBlock:(STLoadUserResult)loadUserResultBlock{
    self.popSession = [[self class] getSessionInstanct];
    self.popSession.hostname = hostname;
    self.popSession.port = 110;
    self.popSession.username = username;
    self.popSession.password = password;

    MCOPOPOperation * op = [self.popSession checkAccountOperation];
    [op start:^(NSError * error) {
        
        NSLog(@"%@",error);
        if (error == nil) {
            if (loadUserResultBlock) {
                loadUserResultBlock(YES);
            }
        }else{
            if (loadUserResultBlock) {
                loadUserResultBlock(NO);
            }
        }
    }];
    
}

- (void)loadLastNMessages{
    MCOPOPFetchMessagesOperation *fetch = [self.popSession fetchMessagesOperation];
    [fetch start:^(NSError *error, NSArray *message){
//        if (error == nil) {
//            if ([message count] > 0) {
//                for (MCOPOPMessageInfo *messageInfo in message) {
//                    MCOPOPFetchHeaderOperation * op = [self.popSession fetchHeaderOperationWithIndex:messageInfo.index];
//                    [op start:^(NSError * error, MCOMessageHeader * header) {
//                        NSString *name = [header extraHeaderValueForName:@"Content-Type"];
//                        NSDate *date = header.date;
//                        NSString *title = header.subject;
//                        int messageUid = messageInfo.index;
//                        NSString *from = header.from.nonEncodedRFC822String;
//                        if ([name hasPrefix:@"multipart/mixed"] || [name hasPrefix:@"multipart/related"]) {
//                            NSLog(@"主题 : %@ ,有附件",title);
//                        }else{
//                            NSLog(@"主题 : %@ ,无附件",title);
//                        }
//                    }];
//                }
//            }
//        }
    }];
}

- (NSString *)dataFormatString:(NSDate *)date{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:SS"];
    NSString* timeStr = [formatter stringFromDate:date];
    return timeStr;
}

@end
