//
//  MessageLoadOperation.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^MessageLoadCompletion)(NSMutableArray *inboxMessages, NSMutableArray *sentMessages);

@interface MessageLoadOperation : NSObject

@property (nonatomic, strong) MessageLoadCompletion completionBlock;
@property (nonatomic, strong) NSMutableArray *inboxMessages;
@property (nonatomic, strong) NSMutableArray *sentMessages;

- (void)loadMessages:(NSString *)folder completion:(void(^)(NSMutableArray *messageInfos))completionBlock;

@end
