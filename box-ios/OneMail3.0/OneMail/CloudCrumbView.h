//
//  CloudCrumbView.h
//  OneMail
//
//  Created by cse  on 15/12/17.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CloudCrumbView : UIView

@property (nonatomic, strong) UIButton *mainCrumbButton;
@property (nonatomic, strong) UINavigationController *navigationController;
@property (nonatomic, strong) NSArray *viewControllers;

- (id)initWithFiles:(NSArray *)fileIds fileOwners:(NSArray *)fileOwners;

@end
