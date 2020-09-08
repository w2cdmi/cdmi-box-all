//
//  CloudBackUpAssetPreviewController.h
//  OneMail
//
//  Created by CSE on 16/1/26.
//  Copyright © 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>

@class File;

@interface CloudBackUpAssetPreviewController : UIViewController

- (id)initWithFile:(File*)file;

@end
