//
//  MailMessageViewController.h
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Session;
@class MailSessionCell;

@interface MailMessageViewController : UIViewController

@property (nonatomic,strong) Session *session;
@property (nonatomic,strong) MailSessionCell *sessionCell;

- (id)initWithSession:(Session *)session;

@end
