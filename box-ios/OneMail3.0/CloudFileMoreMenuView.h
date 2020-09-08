//
//  CloudFileMoreMenuView.h
//  OneMail
//
//  Created by cse on 15/11/24.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CloudFileMoreMenuCell : UITableViewCell

@property (weak, nonatomic) id target;
@property (assign, nonatomic) SEL action;

- (id) initWithImage:(UIImage*)image title:(NSString*) title target:(id) target action:(SEL) action;

@end

@class CloudFileViewController;
@interface CloudFileMoreMenuView : UIView

@property (nonatomic, strong) NSArray* menuCells;
@property (nonatomic, strong) UIViewController *fileViewController;

@end
