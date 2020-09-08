//
//  MailSessionViewController.h
//  OneMail
//
//  Created by cse  on 16/1/18.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Session;

@interface MailSessionCell : UITableViewCell

@property (nonatomic, strong) Session *session;

- (void) refresh;

@end

@interface MailSessionViewController : UIViewController

- (void)mailMonitor;

@end
