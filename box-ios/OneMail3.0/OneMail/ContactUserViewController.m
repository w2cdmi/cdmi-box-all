//
//  ContactUserViewController.m
//  OneMail
//
//  Created by cse  on 15/12/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "ContactUserViewController.h"
#import "AppDelegate.h"
#import "User.h"
#import "ContactUserMenuView.h"
#import "ContactUserAddRemarkView.h"

@interface ContactUserViewController ()<UIAlertViewDelegate,UITextFieldDelegate>

@property (nonatomic, strong) User *user;
@property (nonatomic, strong) UILabel         *contactTitleLabel;
@property (nonatomic, strong) UIButton        *contactBackButton;
@property (nonatomic, strong) UIButton        *contactMenuButton;

@property (nonatomic, strong) UITableViewCell *contactUserIconCell;
@property (nonatomic, strong) UITableViewCell *contactUserRemarkCell;
@property (nonatomic, strong) UITableViewCell *contactUserDescriptionCell;
@property (nonatomic, strong) UITableViewCell *contactUserPhoneCell;

@property (nonatomic, strong) UILabel *contactUserRemarkLabel;
@property (nonatomic, strong) UILabel *contactUserPhoneLabel;

@property (nonatomic, strong) UIView *contactUserSendMailView;

@property (nonatomic, strong) ContactUserMenuView *userMenuView;

@end

@implementation ContactUserViewController

- (id)initWithUser:(User*)user {
    self = [super init];
    if (self) {
        self.user = user;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.contactTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.contactTitleLabel.text = NSLocalizedString(@"ContactDetail", nil);
    
    self.contactBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.contactBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.contactBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.contactBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.contactBackButton addTarget:self action:@selector(contactBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.contactMenuButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
    self.contactMenuButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.contactMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_nor"] forState:UIControlStateNormal];
    [self.contactMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_press"] forState:UIControlStateHighlighted];
    [self.contactMenuButton addTarget:self action:@selector(contactMenuButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.separatorInset = UIEdgeInsetsMake(0, 15, 0, 15);
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.scrollEnabled = NO;
    self.tableView.tableFooterView = self.contactUserSendMailView;
    [self.tableView reloadData];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.contactTitleLabel];
    [self.navigationController.navigationBar addSubview:self.contactBackButton];
    [self.navigationController.navigationBar addSubview:self.contactMenuButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.contactTitleLabel removeFromSuperview];
    [self.contactBackButton removeFromSuperview];
    [self.contactMenuButton removeFromSuperview];
}

- (void)contactBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)contactMenuButtonClick {
    if (self.userMenuView.hidden) {
        self.userMenuView.hidden = NO;
    } else {
        self.userMenuView.hidden = YES;
    }
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

- (UIView*)contactUserSendMailView {
    if (!_contactUserSendMailView) {
        _contactUserSendMailView = [[UIView alloc] init];
        
        UIButton *contactSendMailButton = [[UIButton alloc] initWithFrame:CGRectMake(20, 20, CGRectGetWidth(self.view.frame)-20-20, 44)];
        contactSendMailButton.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [contactSendMailButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"UserSendEmail", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be8" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:18.0f]}] forState:UIControlStateNormal];
        contactSendMailButton.layer.borderWidth = 0.5;
        contactSendMailButton.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
        contactSendMailButton.layer.cornerRadius = 4;
        contactSendMailButton.layer.masksToBounds = YES;
        [_contactUserSendMailView addSubview:contactSendMailButton];
    }
    return _contactUserSendMailView;
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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1 && indexPath.row == 2) {
        if (!self.contactUserPhoneLabel.text || [self.contactUserPhoneLabel.text isEqualToString:@""]) {
            return;
        }
        NSString *string = [NSString stringWithFormat:@"telprompt://%@",self.contactUserPhoneLabel.text];
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:string]];
    }
}

- (ContactUserMenuView*)userMenuView {
    if (!_userMenuView) {
        _userMenuView = [[ContactUserMenuView alloc] initWithFrame:self.view.frame];
        _userMenuView.userViewController = self;
        
        ContactUserMenuCell *addRemarks = [[ContactUserMenuCell alloc] initWithTitle:NSLocalizedString(@"ContactMenuAddRemark", nil) target:self action:@selector(contactUserAddRemarks)];
        ContactUserMenuCell *userPhone = [[ContactUserMenuCell alloc] initWithTitle:NSLocalizedString(@"ContactMenuPhone", nil) target:self action:@selector(contactUserPhone)];
        ContactUserMenuCell *delete = [[ContactUserMenuCell alloc] initWithTitle:NSLocalizedString(@"ContactMenuDelete", nil) target:self action:@selector(contactUserDelete)];
        [_userMenuView setMenuCells:@[addRemarks,userPhone,delete]];
    }
    return _userMenuView;
}

- (void)contactUserAddRemarks {
    ContactUserAddRemarkView *userAddRemarkView = [[ContactUserAddRemarkView alloc] initWithFrame:self.view.frame];
    userAddRemarkView.user = self.user;
    userAddRemarkView.userViewController = self;
    [self.view addSubview:userAddRemarkView];
}

- (void)contactUserPhone {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"ContactMenuPhone", nil) message:nil delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:NSLocalizedString(@"Confirm", nil), nil];
    [alertView setAlertViewStyle:UIAlertViewStylePlainTextInput];
    UITextField *alertTextField = [alertView textFieldAtIndex:0];
    alertTextField.text = self.contactUserPhoneLabel.text;
    alertTextField.returnKeyType = UIReturnKeyDone;
    alertTextField.keyboardType = UIKeyboardTypePhonePad;
    alertTextField.delegate = self;
    [alertView show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    if (buttonIndex == 1) {
        UITextField *textfield = [alertView textFieldAtIndex:0];
        if (textfield.text.length != 11) {
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"PhoneFormatIncorrect", nil)];
        } else {
            self.contactUserPhoneLabel.text = textfield.text;
            [self.user saveuserPhone:textfield.text];
        }
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if (range.location >= 11) {
        return NO;
    }
    if (string.length+range.location > 11) {
        return NO;
    }
    return YES;
}

- (void)contactUserDelete {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        User *shadow = (User*)[ctx objectWithID:self.user.objectID];
        shadow.userMyContactFlag = @(0);
        [ctx save:nil];
    }];
    [self.navigationController popViewControllerAnimated:YES];
}




@end
