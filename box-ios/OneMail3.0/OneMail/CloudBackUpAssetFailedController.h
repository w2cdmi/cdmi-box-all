//
//  CloudBackUpAssetFailedController.h
//  OneMail
//
//  Created by cse  on 16/3/29.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>

@interface CloudBackUpAssetFailedController : UIViewController

@property (nonatomic, strong)UIViewController *rootViewController;

- (id)initWithAssetsGroup:(ALAssetsGroup *)assetsGroup;

@end
