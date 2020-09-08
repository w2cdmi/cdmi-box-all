//
//  ShareViewController.h
//  ShareExtention
//
//  Created by hua on 17/3/20.
//  Copyright © 2017年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Social/Social.h>
//#import "AppDelegate.h"

@interface ShareViewController : SLComposeServiceViewController

@property (nonatomic, strong) NSString *uploadTargetFolderId;
@property (nonatomic, strong) NSString *uploadTargetFolderOwner;


@end
