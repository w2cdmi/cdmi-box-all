//
//  ContactUserMenuView.h
//  OneMail
//
//  Created by cse  on 15/12/11.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ContactUserMenuCell : UITableViewCell

@property (weak, nonatomic) id target;
@property (assign, nonatomic) SEL action;

- (id) initWithTitle:(NSString*) title target:(id) target action:(SEL) action;

@end

@class ContactUserViewController;
@interface ContactUserMenuView : UIView

@property (nonatomic, strong) NSArray* menuCells;
@property (nonatomic, strong) ContactUserViewController *userViewController;

@end
