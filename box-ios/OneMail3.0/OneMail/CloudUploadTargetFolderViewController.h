//
//  CloudUploadTargetFolderViewController.h
//  OneMail
//
//  Created by cse  on 15/11/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^CloudUploadTargetFolderConfirm)(NSString *uploadTargetFolderPath, NSString *uploadTargetFolderId, NSString *uploadTargetFolderOwner);

@class File;

@interface CloudUploadTargetFolderViewController : UITableViewController

@property (nonatomic, strong) CloudUploadTargetFolderConfirm cloudUploadTargetFolderConfirm;
@property (nonatomic, strong) UIViewController *rootViewController;

- (id)initWithFile:(File*)file uploadTargetFolderPath:(NSString*)path;

@end
