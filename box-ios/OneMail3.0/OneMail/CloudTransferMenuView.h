//
//  CloudTransferMenuViewController.h
//  OneMail
//
//  Created by cse  on 16/1/25.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
@interface CloudTransferMenuCell : UITableViewCell

- (id) initWithTitle:(NSString*)title;

@end

@interface CloudTransferMenuView : UIView

@property (nonatomic, strong) NSArray* menuCells;

@end
