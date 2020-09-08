//
//  ContactUserViewController.h
//  OneMail
//
//  Created by cse  on 15/12/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class User;

@interface ContactUserViewController : UITableViewController

- (id)initWithUser:(User*)user;
- (void)refreshContactUserRemark:(NSString*)remark;

@end
