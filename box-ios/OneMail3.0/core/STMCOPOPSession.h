//
//  STMCOPOPSession.h
//  StoryClient
//
//  Created by mengxianzhi on 15-5-20.
//  Copyright (c) 2015年 LiuQi. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MailCore/MailCore.h>
typedef void(^STLoadUserResult)(BOOL isSuccess);
typedef void(^STMessageArray)(NSArray *messageList);

@protocol STMCOPOPSessionDelgate <NSObject>

- (void)popRefreshMailResultStatus:(NSString *)code;
- (void)popRefreshMailisMoreMail:(BOOL)isMoreMain;

@end

@interface STMCOPOPSession : MCOPOPSession
@property (nonatomic, strong) MCOPOPSession *popSession;
@property (nonatomic, strong) MCOPOPFetchMessagesOperation *popMessagesFetchOp;
@property (weak, nonatomic) id<STMCOPOPSessionDelgate> popSessionDelgate;

- (void)loadLastNMessages;

- (void)clearSesstionInstance;

+ (STMCOPOPSession *)getSessionInstanct;

- (void)loadAccountWithUsername:(NSString *)username
                       password:(NSString *)password
                       hostname:(NSString *)hostname
                       loadUserResultBlock:(STLoadUserResult)loadUserResultBlock;


@end
