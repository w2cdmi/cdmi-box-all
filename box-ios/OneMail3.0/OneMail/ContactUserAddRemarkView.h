//
//  ContactUserAddRemarkView.h
//  OneMail
//
//  Created by cse  on 15/12/11.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class User;
@class ContactUserViewController;

@interface ContactUserAddRemarkView : UIView

@property (nonatomic, strong) User *user;
@property (nonatomic, strong) ContactUserViewController *userViewController;

- (id)initWithFrame:(CGRect)frame;

@end
