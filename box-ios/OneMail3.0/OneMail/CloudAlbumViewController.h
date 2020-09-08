//
//  CloudAlbumViewController.h
//  OneMail
//
//  Created by cse  on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CloudAlbumTableViewCell : UITableViewCell

@property (nonatomic, strong) NSString *albumTitle;
@property (nonatomic, strong) UIImage *albumImage;

@end

@interface CloudAlbumViewController : UITableViewController

@property (nonatomic, strong) UIViewController *rootViewController;
@property (nonatomic, strong) NSString *uploadTargetFolderId;
@property (nonatomic, strong) NSString *uploadTargetFolderOwner;

@end
