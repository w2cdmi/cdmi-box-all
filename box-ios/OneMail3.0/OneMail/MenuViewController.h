//
//  MenuViewController.h
//  OneMail
//
//  Created by cse  on 15/12/8.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MenuViewController : UIViewController

@property (nonatomic, strong) UITableView *menuTableView;

- (void)refreshUserIcon;
- (void)refreshEmailAddress;
- (void)refreshTransferTaskCount;

@end
