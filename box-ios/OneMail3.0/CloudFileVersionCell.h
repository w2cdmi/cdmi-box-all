//
//  CloudFileVersionCell.h
//  OneMail
//
//  Created by cse  on 15/11/19.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define VersionCellHeight 44
#import <UIKit/UIKit.h>

@class Version;

@interface CloudFileVersionCell : UITableViewCell

@property (nonatomic,strong) UILabel *versionLabel;
@property (nonatomic,strong) Version *version;
@property (nonatomic,strong) UIViewController *versionController;

@end
