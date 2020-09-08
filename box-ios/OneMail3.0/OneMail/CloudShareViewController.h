//
//  CloudShareViewController.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;
@class CloudTitleView;

@interface CloudShareViewController : UIViewController

@property (nonatomic, strong) CloudTitleView *cloudTitleView;
@property (nonatomic, strong) NSMutableArray *crumbFileIds;
@property (nonatomic, strong) NSMutableArray *crumbFileOwners;
@property (nonatomic, strong) NSMutableArray *crumbFileViewControllers;

- (id)initWithFile:(File*)file;

@end
