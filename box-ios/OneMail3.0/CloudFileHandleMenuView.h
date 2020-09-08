//
//  CloudFileHandleMenuView.h
//  OneMail
//
//  Created by cse on 15/11/24.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
@interface CloudFileHandleMenuCell : UITableViewCell

@property (weak, nonatomic) id target;
@property (assign, nonatomic) SEL action;

- (id) initWithTitle:(NSString*) title target:(id) target action:(SEL) action;

@end

@interface CloudFileHandleMenuView : UIView

@property (nonatomic, strong) NSArray  *menuCells;
@property (nonatomic, strong) UIButton *viewControlButton;
@property (nonatomic, strong) UIViewController *fileViewController;

@end
