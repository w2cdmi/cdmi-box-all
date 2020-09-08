//
//  CloudFileHandleMenuView.m
//  OneMail
//
//  Created by cse on 15/11/24.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define CellHeight 44

#define CellTextLabelLeft 30
#define CellTextLabelRight 30

#define TableViewRight 10
#define TableViewMinWidth 160

#import "CloudFileHandleMenuView.h"
#import "AppDelegate.h"

@interface CloudFileHandleMenuView ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic, strong) UITableView *tableView;
@end

@implementation CloudFileHandleMenuView
- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
        [rootViewController.view addSubview:self];
        self.hidden = YES;
        
        self.tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.tableView.scrollEnabled = NO;
        self.tableView.delegate = self;
        self.tableView.dataSource = self;
        self.tableView.separatorInset = UIEdgeInsetsZero;
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
        self.tableView.layer.borderWidth = 1;
        self.tableView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        self.tableView.layer.cornerRadius = 12;
        self.tableView.layer.masksToBounds = YES;
        self.tableView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.tableView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [self addSubview:self.tableView];
        [self.tableView reloadData];
    }
    return self;
}

- (void)setViewControlButton:(UIButton *)viewControlButton {
    _viewControlButton = viewControlButton;
}

- (void)setMenuCells:(NSArray *)menuCells {
    _menuCells = menuCells;
    CGFloat maxLabelWidth = 0;
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectZero];
    label.font = [UIFont systemFontOfSize:15.0f];
    for (CloudFileHandleMenuCell *cell in menuCells) {
        label.text = cell.textLabel.text;
        CGSize adjustSize = [CommonFunction labelSizeWithLabel:label limitSize:CGSizeMake(1000, 1000)];
        maxLabelWidth = MAX(maxLabelWidth, adjustSize.width);
    }
    self.tableView.bounds = CGRectMake(0, 0, MAX(TableViewMinWidth, CellTextLabelLeft+maxLabelWidth+CellTextLabelRight), menuCells.count*CellHeight);
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect tabBarFrame = appDelegate.mainTabBar.frame;
    self.tableView.frame = CGRectMake(CGRectGetWidth(self.frame)-TableViewRight-CGRectGetWidth(self.tableView.frame), CGRectGetHeight(self.frame)-CGRectGetHeight(tabBarFrame)-CGRectGetHeight(self.tableView.frame), CGRectGetWidth(self.tableView.frame), CGRectGetHeight(self.tableView.frame));
    [self.tableView reloadData];
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
    return CellHeight;
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [_menuCells objectAtIndex:indexPath.row];
}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    self.hidden = YES;
    CloudFileHandleMenuCell *cell = (CloudFileHandleMenuCell*)[tableView cellForRowAtIndexPath:indexPath];
    if ([self.fileViewController respondsToSelector:cell.action]) {
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [self.fileViewController performSelector:cell.action withObject:nil];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesEnded:touches withEvent:event];
    if ([event touchesForView:self]) {
        self.hidden = YES;
        self.viewControlButton.selected = NO;
    }
}

@end

@implementation CloudFileHandleMenuCell

- (id) initWithTitle:(NSString*) title target:(id) target action:(SEL) action {
    if (self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil]) {
        self.target = target;
        self.action = action;
        self.textLabel.text = title;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    UITableView *tableView = (UITableView*)self.superview;
    self.textLabel.frame = CGRectMake(0, 0, CGRectGetWidth(tableView.frame), CellHeight);
    self.textLabel.font = [UIFont systemFontOfSize:17.0f];
    self.textLabel.textAlignment = NSTextAlignmentCenter;
    self.textLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0f];
    
    self.backgroundColor = [UIColor clearColor];
}

@end