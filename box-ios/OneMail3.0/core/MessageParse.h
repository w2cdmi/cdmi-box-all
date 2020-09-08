//
//  MessageParse.h
//  OneMail
//
//  Created by cse  on 15/10/31.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MailCore/MailCore.h>

@interface MessageParse : NSObject <MCOHTMLRendererDelegate>

- (NSDictionary *)messageParse:(NSData *)emlData;

@end
