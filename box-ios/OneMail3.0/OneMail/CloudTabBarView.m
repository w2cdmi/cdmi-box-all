//
//  CloudTabBarView.m
//  OneMail
//
//  Created by cse  on 15/11/9.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#define CellLabelDistanceTop 11
#define CellLabelDistanceBottom 11
#define CellLabelHeight 22
#define CellHeight (CellLabelHeight+CellLabelDistanceTop+CellLabelDistanceBottom)

#import "CloudTabBarView.h"
#import "AppDelegate.h"

@interface CloudTabBarView ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UITableView *cloudTabBarTable;
@property (nonatomic, strong) UITableViewCell *cloudMySpaceCell;
@property (nonatomic, strong) UITableViewCell *cloudTeamSpaceCell;
@property (nonatomic, strong) UITableViewCell *cloudShareSpaceCell;


@end

static CloudTabBarView *cloudTabBarView = nil;

@implementation CloudTabBarView

+ (CloudTabBarView *)getTabBarView {
    @synchronized(self) {
        if (cloudTabBarView == nil) {
            cloudTabBarView = [[CloudTabBarView alloc]initWithFrame:CGRectZero];
        }
    }
    return cloudTabBarView;
}

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect navigantionFrame = appDelegate.navigationController.navigationBar.frame;
        self.frame = CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height);
        self.backgroundColor = [UIColor colorWithRed:0.0f/255.0f green:0.0f/255.0f blue:0.0f/255.0f alpha:0.30f];
        
        self.cloudMySpaceCell = [self cellWithString:getLocalizedString(@"CloudFileTitle", nil)];
        self.cloudTeamSpaceCell = [self cellWithString:getLocalizedString(@"CloudTeamSpaceTitle", nil)];
        self.cloudShareSpaceCell = [self cellWithString:getLocalizedString(@"CloudShareTitle", nil)];
        
        self.cloudTabBarTable = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.cloudTabBarTable.frame = CGRectMake(0, statusBarFrame.size.height+navigantionFrame.size.height, [UIScreen mainScreen].bounds.size.width, 3*CellHeight);
        self.cloudTabBarTable.delegate = self;
        self.cloudTabBarTable.dataSource = self;
        self.cloudTabBarTable.scrollEnabled = NO;
        [self addSubview:self.cloudTabBarTable];
        [self.cloudTabBarTable reloadData];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hideTabBarTable) name:@"oneMail.Hide.TabBarTable" object:nil];
    }
    return self;
}

- (void)hideTabBarTable {
    CloudTabBarView *cloudTabBarView = [CloudTabBarView getTabBarView];
    if (cloudTabBarView.superview) {
        [cloudTabBarView removeFromSuperview];
    }
}

- (UITableViewCell*)cellWithString:(NSString*)string {
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
    cell.frame = CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, CellHeight);
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectZero];
    label.font = [UIFont systemFontOfSize:17.0f];
    label.textColor = [UIColor blackColor];
    label.textAlignment = NSTextAlignmentCenter;
    label.text = string;
    label.frame = CGRectMake(0, CellLabelDistanceTop, CGRectGetWidth(cell.frame), CellLabelHeight);
    [cell addSubview:label];
    return cell;
}

#pragma mark tableView
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 3;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return CellHeight;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        return self.cloudMySpaceCell;
    } else if (indexPath.row == 1) {
        return self.cloudTeamSpaceCell;
    } else if (indexPath.row == 2) {
        return self.cloudShareSpaceCell;
    } else {
        return nil;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Show.MySpace" object:nil];
    } else if (indexPath.row == 1) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Show.TeamSpace" object:nil];
    } else if (indexPath.row == 2) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Show.ShareSpace" object:nil];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger sectionMax = [tableView numberOfSections];
    NSInteger rowNumOfSectionMax = [tableView numberOfRowsInSection:sectionMax-1];
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        if (indexPath.section == sectionMax-1 && indexPath.row == rowNumOfSectionMax-1) {
            [cell setSeparatorInset:UIEdgeInsetsMake(0, CGRectGetWidth(cell.frame)/2, 0, CGRectGetWidth(cell.frame)/2)];
        } else {
            [cell setSeparatorInset:UIEdgeInsetsMake(0, 15, 0, 15)];
        }
    }
//    float systemVersion = [[UIDevice currentDevice] systemVersion].floatValue;
//    if (systemVersion >= 8.4) {
//        if ([cell respondsToSelector:@selector(setPreservesSuperviewLayoutMargins:)]) {
//            [cell setPreservesSuperviewLayoutMargins:NO];
//        }
//        if ([cell respondsToSelector:@selector(setLayoutMargins:)]) {
//            [cell setLayoutMargins:UIEdgeInsetsZero];
//        }
//    }
}

- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesEnded:touches withEvent:event];
    if ([event touchesForView:self]) {
        [self hideTabBarTable];
    }
}
@end
