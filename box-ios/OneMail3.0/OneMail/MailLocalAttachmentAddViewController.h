//
//  MailLocalAttachmentAddViewController.h
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>

typedef void (^MailLocalAssetAddedCompletion)(NSArray* succeededAssetArray, NSArray* failedAssetArray);

@interface MailLocalAssetAddViewController : UIViewController

@property (nonatomic, strong) ALAssetsGroup *assetsGroup;
@property (nonatomic, strong) UIViewController *rootViewController;
@property (nonatomic, copy) MailLocalAssetAddedCompletion completion;

@end


typedef void (^MailLocalAttachmentAddedCompletion)(NSArray* succeededAssetArray, NSArray* failedAssetArray);

@interface MailLocalAttachmentAddViewController : UIViewController

@property (nonatomic, strong) UIViewController *rootViewController;
@property (nonatomic, copy) MailLocalAttachmentAddedCompletion completion;

@end
