//
//  ContactUserMenuView.m
//  OneMail
//
//  Created by cse  on 15/12/11.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#define CellHeight 44
#define CellTextLabelLeft 15
#define CellTextLabelHeight 20
#define CellTextLabelRight 15

#define TableViewRight 5
#define TableViewMinWidth 140

#import "ContactUserMenuView.h"
#import "AppDelegate.h"
#import "ContactUserViewController.h"

@interface ContactUserMenuView ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic, strong) UITableView *tableView;
@end

@implementation ContactUserMenuView
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
        self.tableView.layer.borderWidth = 0.5;
        self.tableView.layer.borderColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f].CGColor;
        self.tableView.layer.cornerRadius = 4;
        self.tableView.layer.masksToBounds = YES;
        self.tableView.clipsToBounds = NO;
        self.tableView.layer.shadowColor = [UIColor blackColor].CGColor;
        self.tableView.layer.shadowOpacity = 0.3;
        self.tableView.layer.shadowOffset = CGSizeMake(-4, 4);
        self.tableView.layer.shadowRadius = 4;
        self.tableView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.tableView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [self addSubview:self.tableView];
    }
    return self;
}

- (void)setMenuCells:(NSArray *)menuCells {
    _menuCells = menuCells;
    CGFloat maxLabelWidth = 0;
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectZero];
    label.font = [UIFont systemFontOfSize:15.0f];
    for (ContactUserMenuCell *cell in menuCells) {
        label.text = cell.textLabel.text;
        CGSize adjustSize = [CommonFunction labelSizeWithLabel:label limitSize:CGSizeMake(1000, 1000)];
        maxLabelWidth = MAX(maxLabelWidth, adjustSize.width);
    }
    self.tableView.bounds = CGRectMake(0, 0, MAX(TableViewMinWidth, CellTextLabelLeft+maxLabelWidth+CellTextLabelRight), menuCells.count*CellHeight);
    CGRect statusFrame = [UIApplication sharedApplication].statusBarFrame;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    self.tableView.frame = CGRectMake(CGRectGetWidth(self.frame)-TableViewRight-CGRectGetWidth(self.tableView.frame), statusFrame.size.height+navigationFrame.size.height, CGRectGetWidth(self.tableView.frame), CGRectGetHeight(self.tableView.frame));
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
    ContactUserMenuCell*cell = (ContactUserMenuCell*)[tableView cellForRowAtIndexPath:indexPath];
    if ([self.userViewController respondsToSelector:cell.action]) {
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [self.userViewController performSelector:cell.action withObject:nil];
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

@implementation ContactUserMenuCell

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
    self.textLabel.frame = CGRectMake(CellTextLabelLeft, (CellHeight-CellTextLabelHeight)/2, CGRectGetWidth(tableView.frame)-CellTextLabelLeft-CellTextLabelRight, CellTextLabelHeight);
    self.textLabel.font = [UIFont systemFontOfSize:15.0f];
    self.textLabel.textAlignment = NSTextAlignmentLeft;
    
    self.backgroundColor = [UIColor clearColor];
}

@end

