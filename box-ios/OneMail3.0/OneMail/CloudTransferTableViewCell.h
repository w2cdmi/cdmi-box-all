//
//  CloudTransferTableViewCell.h
//  OneMail
//
//  Created by cse  on 15/11/13.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TransportTask;
@class CloudTransferViewController;

@interface CloudTransferTableViewCell : UITableViewCell

@property (nonatomic, strong) TransportTask *transportTask;
@property (nonatomic, strong) CloudTransferViewController *transferViewController;

- (void)refreshSize;

@end
