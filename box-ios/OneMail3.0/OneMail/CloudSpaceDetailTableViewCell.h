//
//  CloudSpaceDetailTableViewCell.h
//  OneMail
//
//  Created by cse  on 16/4/13.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;

@interface CloudSpaceDetailTableViewCell : UITableViewCell

@property (nonatomic, strong) File *file;
@property (nonatomic, assign) BOOL fileSelectState;
@property (nonatomic, assign) BOOL fileSelected;

- (void)refresh;

@end
