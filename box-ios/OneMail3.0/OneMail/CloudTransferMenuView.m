//
//  CloudTransferMenuViewController.m
//  OneMail
//
//  Created by cse  on 16/1/25.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "CloudTransferMenuView.h"
#import "AppDelegate.h"
#import "CloudTransferViewController.h"
@implementation CloudTransferMenuCell

- (id) initWithTitle:(NSString*)title {
    if (self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil]) {
        self.textLabel.text = title;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.textLabel.frame = CGRectMake(0, 0, CGRectGetWidth(self.frame), CGRectGetHeight(self.frame));
    self.textLabel.font = [UIFont systemFontOfSize:17.0f];
    self.textLabel.textAlignment = NSTextAlignmentCenter;
    self.textLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0f];
    
    self.backgroundColor = [UIColor clearColor];
}

@end


@interface CloudTransferMenuView ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *transferMenuTableView;

@end

@implementation CloudTransferMenuView
- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
        [rootViewController.view addSubview:self];
        self.hidden = YES;
        
        self.transferMenuTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.transferMenuTableView.scrollEnabled = NO;
        self.transferMenuTableView.delegate = self;
        self.transferMenuTableView.dataSource = self;
        self.transferMenuTableView.separatorInset = UIEdgeInsetsZero;
        self.transferMenuTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        self.transferMenuTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
        self.transferMenuTableView.layer.borderWidth = 1;
        self.transferMenuTableView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        self.transferMenuTableView.layer.cornerRadius = 12;
        self.transferMenuTableView.layer.masksToBounds = YES;
        self.transferMenuTableView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.transferMenuTableView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [self addSubview:self.transferMenuTableView];
    }
    return self;
}

- (void)setMenuCells:(NSArray *)menuCells {
    _menuCells = menuCells;
    CGFloat maxLabelWidth = 0;
    for (CloudTransferMenuCell *cell in menuCells) {
        CGSize adjustSize = [CommonFunction labelSizeWithString:cell.textLabel.text font:[UIFont systemFontOfSize:15.0f]];
        maxLabelWidth = MAX(maxLabelWidth, adjustSize.width);
    }
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    self.transferMenuTableView.bounds = CGRectMake(0, 0, MAX(160, 30+maxLabelWidth+30), menuCells.count*44);
    self.transferMenuTableView.frame = CGRectMake(CGRectGetWidth(self.frame)-10-CGRectGetWidth(self.transferMenuTableView.frame), statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.transferMenuTableView.frame), CGRectGetHeight(self.transferMenuTableView.frame));
    [self.transferMenuTableView reloadData];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return _menuCells.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 44.0f;
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [_menuCells objectAtIndex:indexPath.row];
}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    self.hidden = YES;
    if (indexPath.row == 0) {
        [CloudTransferViewController resumeAllTransferTask];
    }
    if (indexPath.row == 1) {
        [CloudTransferViewController pauseAllTransferTask];
    }
    if (indexPath.row == 2) {
        [CloudTransferViewController cancelAllTransferTask];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesEnded:touches withEvent:event];
    if ([event touchesForView:self]) {
        self.hidden = YES;
    }
}

@end