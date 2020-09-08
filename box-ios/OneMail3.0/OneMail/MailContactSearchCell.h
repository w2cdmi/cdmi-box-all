//
//  MailContactSearchCell.h
//  OneMail
//
//  Created by cse  on 15/11/3.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class User;

@protocol MailContactSearchDelegate <NSObject>

- (void) selectMailContactUser:(User *)user;
- (void) deselectMailContactUser:(User *)user;

@end

@interface MailContactSearchCell : UITableViewCell

@property (nonatomic, strong) User *user;
@property (nonatomic, assign) BOOL contactSelected;
@property (nonatomic, assign) BOOL isNeedAdd;
@property (nonatomic, assign) id <MailContactSearchDelegate> delegate;

@end
