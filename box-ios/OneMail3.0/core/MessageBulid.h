//
//  MessageBulid.h
//  OneMail
//
//  Created by cse  on 15/10/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MailCore/MailCore.h>

typedef void(^EmlData)(NSData *emlData);

@interface MessageBulid : NSObject

+ (void)buildMessageWithInfo:(NSDictionary *)messageInfo messageData:(EmlData)messageDataBlock;

@end
