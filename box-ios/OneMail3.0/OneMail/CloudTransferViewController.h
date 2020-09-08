//
//  CloudTransferViewController.h
//  OneMail
//
//  Created by cse  on 15/11/12.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;
@class CloudTransferTableViewCell;

@interface CloudTransferViewController : UIViewController

@property (nonatomic, strong) UIViewController *rootViewController;
@property (nonatomic, strong) CloudTransferTableViewCell *parentTransferCell;

- (id)initWithFile:(File*)file;

+ (void) resumeAllTransferTask;
+ (void) pauseAllTransferTask;
+ (void) cancelAllTransferTask;
+ (void) waitNetworkAllTransferTask;
+ (void) waitAllTransferTask;

@end
