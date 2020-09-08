//
//  MailForwardViewController.h
//  OneMail
//
//  Created by CSE on 15/12/1.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Session;

@interface MailForwardSessionCell : UITableViewCell

@property (nonatomic, strong) Session *session;

@end

@class Message;

@interface MailForwardViewController : UIViewController

- (id)initWithForwardMessage:(Message *)forwardMessage;
- (id)initWithShareLink:(NSString *)shareLink;
@end
