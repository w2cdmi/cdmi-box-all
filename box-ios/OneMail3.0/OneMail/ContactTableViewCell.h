//
//  ContactTableViewCell.h
//  OneMail
//
//  Created by cse  on 15/12/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class User;

@interface ContactTableViewCell : UITableViewCell

@property (nonatomic, strong) User *user;
@property (nonatomic, assign) BOOL searchingState;

@end
