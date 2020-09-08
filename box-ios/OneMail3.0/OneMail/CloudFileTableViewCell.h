//
//  CloudFileTableViewCell.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;

@interface CloudFileTableViewCell : UITableViewCell

@property (nonatomic, strong) File *file;
@property (nonatomic, assign) BOOL fileSelectState;
@property (nonatomic, assign) BOOL fileSelected;

- (void)refresh;

@end
