//
//  CloudSpaceDetailViewController.h
//  OneMail
//
//  Created by cse  on 16/4/13.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;
@class TeamSpace;
@class CloudTitleView;

@interface CloudSpaceDetailViewController : UIViewController

@property (nonatomic, strong) TeamSpace *teamSpace;
@property (nonatomic, strong) CloudTitleView *cloudTitleView;
@property (nonatomic, strong) NSMutableArray *crumbFileIds;
@property (nonatomic, strong) NSMutableArray *crumbFileOwners;
@property (nonatomic, strong) NSMutableArray *crumbFileViewControllers;

- (id)initWithFile:(File*)file;

@end
