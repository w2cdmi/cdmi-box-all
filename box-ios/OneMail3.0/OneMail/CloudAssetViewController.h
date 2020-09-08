//
//  CloudAssetViewController.h
//  OneMail
//
//  Created by cse  on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>

@interface CloudAssetViewController : UIViewController

@property (nonatomic, strong) ALAssetsGroup *assetsGroup;
@property (nonatomic, strong) UIViewController *rootViewController;
@property (nonatomic, strong) NSString *uploadTargetFolderId;
@property (nonatomic, strong) NSString *uploadTargetFolderOwner;

@end
