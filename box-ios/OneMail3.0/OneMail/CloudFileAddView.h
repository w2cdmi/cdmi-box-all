//
//  CloudFileAddView.h
//  OneMail
//
//  Created by cse  on 15/12/12.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CloudFileAddView : UIView

@property (assign, nonatomic) SEL uploadAction;
@property (assign, nonatomic) SEL createFolderAction;

@property (nonatomic, strong) UIViewController *fileViewController;

- (void)showView;

@end
