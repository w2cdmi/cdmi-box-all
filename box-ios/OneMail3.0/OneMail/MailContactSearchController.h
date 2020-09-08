//
//  MailContactSearchController.h
//  OneMail
//
//  Created by cse  on 15/11/3.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Message.h"
@class Session;

@interface MailContactSearchController : UIViewController
- (id)initWithMessage:(Message *)forwardMessage;
- (id)initWithSession:(Session *)session;

@end
