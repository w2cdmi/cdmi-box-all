//
//  CloudFileShareLinkSettingViewController.h
//  OneMail
//
//  Created by cse  on 15/11/24.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;
@class CloudFileShareLinkInfo;

@interface CloudFileShareLinkSettingViewController : UIViewController

- (id)initWithShareLinkInfo:(CloudFileShareLinkInfo*)cloudFileShareLinkInfo shareLinkFile:(File*)file;

@end
