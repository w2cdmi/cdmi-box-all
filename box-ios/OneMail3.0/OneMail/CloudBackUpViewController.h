//
//  CloudBackUpViewController.h
//  OneMail
//
//  Created by cse  on 15/11/30.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class SettingViewController;

@interface CloudBackUpViewController : UIViewController

@property (nonatomic, strong) SettingViewController *settingViewController;

- (void)refreshBackUpStatusImage;
- (void)refreshBackUpStatusRemain;

@end
