//
//  CloudTeamMoveViewController.h
//  OneMail
//
//  Created by cse on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "File.h"
#import "TeamSpace.h"
#import "TeamSpace+Remote.h"

@interface CloudTeamMoveViewController : UITableViewController

@property (nonatomic,strong) UIViewController *rootViewController;

- (id)initWithSourceFiles:(NSArray *)sourcefiles filesOwner:(NSArray *)sourceFilesOwner;

@end
