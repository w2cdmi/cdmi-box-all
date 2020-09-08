//
//  MessageIMAPSession.h
//  OneMail
//
//  Created by cse  on 15/10/31.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MCOIMAPSession.h"
#import <MailCore/MailCore.h>

@interface MessageIMAPSession : MCOIMAPSession <MCOHTMLRendererIMAPDelegate>

@property (nonatomic,strong) MCOIMAPIdleOperation *SentBoxIdleOperation;
@property (nonatomic,strong) MCOIMAPIdleOperation *InBoxIdleOperation;

+ (MessageIMAPSession *)getSessionInstance;
+ (void)clearSessionInstance;

@end
