//
//  CloudFileMoveViewController.h
//  OneMail
//
//  Created by cse on 15/11/13.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;
@interface CloudFileMoveViewController : UITableViewController

@property (nonatomic,strong) UIViewController *rootViewController;

- (id)initWithSourceFiles:(NSArray *)sourceFiles filesOwner:(NSArray*)sourceFilesOwner rootFile:(File *)file;

@end
