//
//  MailMessageUserViewController.m
//  OneMail
//
//  Created by cse  on 16/1/29.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailMessageUserViewController.h"
#import "AppDelegate.h"
#import "User.h"

@interface MailMessageUserViewController ()<UIAlertViewDelegate,UITextFieldDelegate>

@property (nonatomic, strong) User *user;
@property (nonatomic, strong) UITableViewCell *contactUserIconCell;
@property (nonatomic, strong) UITableViewCell *contactUserRemarkCell;
@property (nonatomic, strong) UITableViewCell *contactUserDescriptionCell;
@property (nonatomic, strong) UITableViewCell *contactUserPhoneCell;

@property (nonatomic, strong) UILabel *contactUserRemarkLabel;
@property (nonatomic, strong) UILabel *contactUserPhoneLabel;

@end

@implementation MailMessageUserViewController

- (id)initWithUser:(User*)user {
    self = [super init];
    if (self) {
        self.user = user;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = NSLocalizedString(@"ContactDetail", nil);
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    self.view.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.separatorInset = UIEdgeInsetsMake(0, 15, 0, 15);
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.scrollEnabled = NO;
    [self.tableView reloadData];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    
}

- (UITableViewCell*)contactUserIconCell {
    if (!_contactUserIconCell) {
        _contactUserIconCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _contactUserIconCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _contactUserIconCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UIImageView *iconBackground = [[UIImageView alloc] initWithFrame:CGRectMake(15, 12, 56, 56)];
        iconBackground.image = [UIImage imageNamed:@"img_user_frame"];
        [_contactUserIconCell.contentView addSubview:iconBackground];
        
        UIImageView *icon = [[UIImageView alloc] initWithFrame:CGRectMake(2, 2, 52, 52)];
        NSString *userHeadIconPath = [self.user userHeadIconPath];
        if (userHeadIconPath && [[NSFileManager defaultManager] fileExistsAtPath:userHeadIconPath]) {
            icon.image = [UIImage imageWithContentsOfFile:userHeadIconPath];
        } else {
            icon.image = [UIImage imageNamed:@"img_user_default"];
        }
        [iconBackground addSubview:icon];
        
        UILabel *nameLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(iconBackground.frame)+15, 21, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(iconBackground.frame)-15-15, 20)];
        nameLabel.text = self.user.userName;
        nameLabel.font = [UIFont systemFontOfSize:15.0f];
        nameLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        nameLabel.textAlignment = NSTextAlignmentLeft;
        [_contactUserIconCell.contentView addSubview:nameLabel];
        
        UILabel *emailLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(nameLabel.frame), CGRectGetMaxY(nameLabel.frame)+4, CGRectGetWidth(nameLabel.frame), 15)];
        emailLabel.text = self.user.userEmail;
        emailLabel.font = [UIFont systemFontOfSize:12.0f];
        emailLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        emailLabel.textAlignment = NSTextAlignmentLeft;
        [_contactUserIconCell.contentView addSubview:emailLabel];
    }
    return _contactUserIconCell;
}

- (UITableViewCell*)contactCellWithTitle:(NSString*)title message:(NSString*)message {
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    cell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    titleLabel.font = [UIFont systemFontOfSize:15.0f];
    titleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    
    CGSize adjustDescriptionSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"UserDescription", nil) font:[UIFont systemFontOfSize:15.0f]];
    CGSize adjustRemarkSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"UserRemark", nil) font:[UIFont systemFontOfSize:15.0f]];
    CGSize adjustPhoneSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"UserPhoneNumber", nil) font:[UIFont systemFontOfSize:15.0f]];
    titleLabel.frame = CGRectMake(15, 12, MAX(MAX(MAX(adjustDescriptionSize.width, adjustRemarkSize.width), adjustPhoneSize.width), 56) , 20);
    titleLabel.text = title;
    [cell.contentView addSubview:titleLabel];
    
    UILabel *messageLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(titleLabel.frame)+15, 12, CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(titleLabel.frame)-15-15, 20)];
    messageLabel.text = message;
    messageLabel.font = [UIFont systemFontOfSize:15.0f];
    messageLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    messageLabel.textAlignment = NSTextAlignmentLeft;
    [cell.contentView addSubview:messageLabel];
    
    if ([title isEqualToString:NSLocalizedString(@"UserRemark", nil)]) {
        self.contactUserRemarkLabel = messageLabel;
    }
    if ([title isEqualToString:NSLocalizedString(@"UserPhoneNumber", nil)]) {
        self.contactUserPhoneLabel = messageLabel;
    }
    
    return cell;
}

- (void)refreshContactUserRemark:(NSString*)remark {
    self.contactUserRemarkLabel.text = remark;
}

- (UITableViewCell*)contactUserRemarkCell {
    if (!_contactUserRemarkCell) {
        _contactUserRemarkCell = [self contactCellWithTitle:NSLocalizedString(@"UserRemark", nil) message:self.user.userRemark];
    }
    return _contactUserRemarkCell;
}

- (UITableViewCell*)contactUserDescriptionCell {
    if (!_contactUserDescriptionCell) {
        _contactUserDescriptionCell = [self contactCellWithTitle:NSLocalizedString(@"UserDescription", nil) message:self.user.userDescription];
    }
    return _contactUserDescriptionCell;
}

- (UITableViewCell*)contactUserPhoneCell {
    if (!_contactUserPhoneCell) {
        _contactUserPhoneCell = [self contactCellWithTitle:NSLocalizedString(@"UserPhoneNumber", nil) message:self.user.userPhone];
    }
    return _contactUserPhoneCell;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 1;
    } else {
        return 3;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return 0.1;
    } else {
        return 22;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return 80;
    } else {
        return 44;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.section == 0) {
        return self.contactUserIconCell;
    } else {
        if (indexPath.row == 0) {
            return self.contactUserRemarkCell;
        } else if (indexPath.row == 1) {
            return self.contactUserDescriptionCell;
        } else {
            return self.contactUserPhoneCell;
        }
    }
}

@end
