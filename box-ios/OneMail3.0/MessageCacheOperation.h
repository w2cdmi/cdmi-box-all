//
//  MessageCacheOperation.h
//  OneMail
//
//  Created by cse on 15/11/17.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#import <Foundation/Foundation.h>

@interface MessageCacheOperation : NSObject

+ (void)cleanExpiredMessages;

@end

