//
//  MenuViewController.m
//  OneMail
//
//  Created by cse  on 15/12/8.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MenuViewController.h"
#import "AppDelegate.h"
#import "User+Remote.h"
#import "MainViewController.h"
#import "CloudViewController.h"
#import "UserViewController.h"
#import "CloudTransferViewController.h"
#import "ContactViewController.h"
#import "SettingViewController.h"
#import "UserThumbnail.h"

#import <ShareSDK/ShareSDK.h>
#import <ShareSDKUI/ShareSDK+SSUI.h>
#import <ShareSDKUI/SSUIShareActionSheetStyle.h>

@interface MenuViewController ()<UITableViewDataSource,UITableViewDelegate>

@property (nonatomic, strong) UITableViewCell *userIconCell;
@property (nonatomic, strong) UIImageView *userIconImageView;
@property (nonatomic, strong) UILabel *userEmailLabel;

@property (nonatomic, strong) UITableViewCell *transferTaskCell;
@property (nonatomic, strong) UILabel *transferTaskCountLabel;

@property (nonatomic, strong) UITableViewCell *contactCell;
@property (nonatomic, strong) UITableViewCell *settingCell;

@end

@implementation MenuViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.LocalizedChange" object:nil];
}

-(void)viewDidLoad {
    [super viewDidLoad];
    
    UIImageView *backgroundView = [[UIImageView alloc] initWithFrame:self.view.frame];
    backgroundView.image = [UIImage imageNamed:@"bg_1"];
    [self.view addSubview:backgroundView];
    
    self.menuTableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStylePlain];
    self.menuTableView.backgroundColor = [UIColor clearColor];
    self.menuTableView.dataSource = self;
    self.menuTableView.delegate = self;
    self.menuTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.menuTableView.scrollEnabled = NO;
    [self.menuTableView reloadData];
    [self.view addSubview:self.menuTableView];
    
    UIPanGestureRecognizer *handle = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handLeftPan:)];
    [handle setMinimumNumberOfTouches:1];
    [handle setMaximumNumberOfTouches:1];
    [self.menuTableView addGestureRecognizer:handle];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshTitle) name:@"com.huawei.onemail.LocalizedChange" object:nil];
}

- (void)refreshTitle{
    
    [self.menuTableView reloadData];
    
}

- (void)handLeftPan:(UIScreenEdgePanGestureRecognizer *)sender {
    
    UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)sender;
    CGPoint point = [pan translationInView:self.view];
    if (point.x < 0) {
        if (sender.state == UIGestureRecognizerStateEnded) {
            
            AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
            if (appDelegate.LeftSlideVC.closed) {
                [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshUserIcon];
                [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshEmailAddress];
                [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshTransferTaskCount];
                appDelegate.leftViewOpened = YES;
                [appDelegate.LeftSlideVC openLeftView];
            } else {
                appDelegate.leftViewOpened = NO;
                [appDelegate.LeftSlideVC closeLeftView];
            }
        }
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleLightContent;
}

- (UITableViewCell*)userIconCell {
    if (!_userIconCell) {
        _userIconCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _userIconCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _userIconCell.backgroundColor = [UIColor clearColor];
        
        UIImageView *background = [[UIImageView alloc] initWithFrame:CGRectMake(15, 44, 65, 65)];
        background.image = [UIImage imageNamed:@"img_portrait_frame"];
        background.layer.shadowOpacity = 0.9;
        [background.layer setShadowColor:[[UIColor whiteColor]CGColor]];
        [background.layer setShadowOffset:CGSizeMake(1  , 1)];
        [_userIconCell.contentView addSubview:background];
        
        self.userIconImageView = [[UIImageView alloc] initWithFrame:CGRectMake((65-61)/2 -1.9, (65-61)/2 - 1.9, 65, 65)];
        self.userIconImageView.layer.cornerRadius = 65/2;
        self.userIconImageView.layer.masksToBounds = YES;
        [self refreshUserIcon];
        [background addSubview:self.userIconImageView];
        
        UILabel *nameLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(background.frame)+25, CGRectGetMinY(background.frame), CGRectGetWidth(self.view.frame)-CGRectGetMaxX(background.frame)-15-68-15-22-5, 40)];
        nameLabel.text = [UserSetting defaultSetting].cloudUserName;
        nameLabel.font = [UIFont systemFontOfSize:20.0f];
        nameLabel.textColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        nameLabel.textAlignment = NSTextAlignmentLeft;
        [_userIconCell.contentView addSubview:nameLabel];
        
//        UILabel *userNameLable = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(nameLabel.frame), CGRectGetMaxY(nameLabel.frame)+5, CGRectGetWidth(nameLabel.frame), 15)];
//        userNameLable.text = [NSString stringWithFormat:getLocalizedString(@"UserNameTitle", nil),[UserSetting defaultSetting].cloudUserLoginName];
//        userNameLable.font = [UIFont systemFontOfSize:12.0f];
//        userNameLable.textColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f];
//        userNameLable.textAlignment = NSTextAlignmentLeft;
//        [_userIconCell.contentView addSubview:userNameLable];
        
        self.userEmailLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(nameLabel.frame)-20, CGRectGetMaxY(nameLabel.frame), CGRectGetWidth(nameLabel.frame)+150, 15)];
        //        userEmailLabel.text = [NSString stringWithFormat:getLocalizedString(@"UserMailTitle", nil),[UserSetting defaultSetting].emailAddress];
        [self refreshEmailAddress];
        self.userEmailLabel.font = [UIFont systemFontOfSize:10.0f];
        self.userEmailLabel.textColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        self.userEmailLabel.textAlignment = NSTextAlignmentLeft;
        [_userIconCell.contentView addSubview:self.userEmailLabel];
        
        UIButton *detailButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetMaxX(nameLabel.frame)+5, 0, 22,22)];
        detailButton.centerY = background.centerY;
        [detailButton setImage:[UIImage imageNamed:@"ic_menu_enter_nor"] forState:UIControlStateNormal];
//        [_userIconCell.contentView addSubview:detailButton];
        
        UIView *separatorLine = [[UIView alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(background.frame)+17, CGRectGetWidth(self.view.frame)-15-68-15, 1)];
        separatorLine.backgroundColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
//        [_userIconCell addSubview:separatorLine];
    }
    return _userIconCell;
}

- (void)refreshUserIcon {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    User *user = [User getUserWithUserSingleId:appDelegate.localManager.userSingleId context:nil];
    [UserThumbnail imageWithUser:user imageView:self.userIconImageView refresh:YES];
}

- (void)refreshEmailAddress {
//    UserSetting *userSetting = [UserSetting defaultSetting];
//    if (userSetting.emailBinded.boolValue) {
//        self.userEmailLabel.text = [NSString stringWithFormat:getLocalizedString(@"UserMailTitle", nil),userSetting.emailAddress];
//    } else {
//        self.userEmailLabel.text = [NSString stringWithFormat:getLocalizedString(@"UserMailTitle", nil),getLocalizedString(@"UserMailNoBound", nil)];
//    }
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!userSetting.emailBinded.boolValue) {
        self.userEmailLabel.text = [NSString stringWithFormat:getLocalizedString(@"UserMailTitle", nil),userSetting.emailAddress];
    } else {
        self.userEmailLabel.text = [NSString stringWithFormat:getLocalizedString(@"UserMailTitle", nil),getLocalizedString(@"UserMailNoBound", nil)];
    }
}

- (UITableViewCell*)menuCellWithImage:(UIImage*)image title:(NSString*)title {
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [UIColor clearColor];
    
    UIImageView *cellImageView = [[UIImageView alloc] initWithFrame:CGRectMake(19, 20, 29, 29)];
    cellImageView.image = image;
    [cell.contentView addSubview:cellImageView];
    
    CGSize adjustSize = [CommonFunction labelSizeWithString:title font:[UIFont systemFontOfSize:16.0f] limitSize:CGSizeMake(CGRectGetWidth(self.view.frame)-CGRectGetMaxX(cellImageView.frame)-10-68-15, 22)];
    UILabel *cellTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(cellImageView.frame)+10, 0, adjustSize.width*3, 22)];
    cellTitleLabel.centerY = cellImageView.centerY;
    cellTitleLabel.text = title;
    cellTitleLabel.font = [UIFont systemFontOfSize:20.0f];
    cellTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    cellTitleLabel.textAlignment = NSTextAlignmentLeft;
    [cell.contentView addSubview:cellTitleLabel];
    
    if ([title isEqualToString:getLocalizedString(@"CloudTransferTaskTitle", nil)]) {
        self.transferTaskCountLabel.frame = CGRectMake(CGRectGetMaxX(cellTitleLabel.frame)+8, 0, CGRectGetWidth(self.transferTaskCountLabel.frame), CGRectGetHeight(self.transferTaskCountLabel.frame));
        self.transferTaskCountLabel.centerY = cellTitleLabel.centerY;
        [cell.contentView addSubview:self.transferTaskCountLabel];
    }
    return cell;
}

- (UITableViewCell*)transferTaskCell {
//    if (!_transferTaskCell) {
        _transferTaskCell = [self menuCellWithImage:[UIImage imageNamed:@"ic_menu_transfer_task_nor"] title:getLocalizedString(@"CloudTransferTaskTitle", nil)];
//    }
    return _transferTaskCell;
}

- (UILabel*)transferTaskCountLabel {
    if (!_transferTaskCountLabel) {
        _transferTaskCountLabel = [CommonFunction labelWithFrame:CGRectMake(0, 0, 18, 18) textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        _transferTaskCountLabel.layer.cornerRadius = 18/2;
        _transferTaskCountLabel.layer.masksToBounds = YES;
        _transferTaskCountLabel.backgroundColor = [CommonFunction colorWithString:@"ff6b21" alpha:1.0f];
    }
    return _transferTaskCountLabel;
}

- (void)refreshTransferTaskCount {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (appDelegate.transferTaskCount > 0) {
        self.transferTaskCountLabel.hidden = NO;
        self.transferTaskCountLabel.text = [NSString stringWithFormat:@"%ld",(long)appDelegate.transferTaskCount];
    } else {
        self.transferTaskCountLabel.hidden = YES;
    }
}

- (UITableViewCell*)contactCell {
//    if (!_contactCell) {
        _contactCell = [self menuCellWithImage:[UIImage imageNamed:@"ic_send_shares"] title:getLocalizedString(@"CloudShareApp", nil)];
//    }
    return _contactCell;
}

- (UITableViewCell*)settingCell {
//    if (!_settingCell) {
        _settingCell = [self menuCellWithImage:[UIImage imageNamed:@"ic_menu_settings_nor"] title:getLocalizedString(@"SettingTitle",nil)];
//    }
    return _settingCell;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    //    return 4;
    return 4;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        return 127.0f;
    } else {
        return 54.0f;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
        if (indexPath.row == 0) {
            return self.userIconCell;
        } else if (indexPath.row == 1) {
            return self.transferTaskCell;
        } else if (indexPath.row == 2) {
            return self.contactCell;
        } else {
            return self.settingCell;
        }
//    if (indexPath.row == 0) {
//        return self.userIconCell;
//    } else if (indexPath.row == 1) {
//        return self.transferTaskCell;
//    } else {
//        return self.settingCell;
//    }
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.LeftSlideVC closeLeftView];
    MainViewController *mainVC = (MainViewController*)appDelegate.LeftSlideVC.mainVC;
    UIViewController *selectedView = mainVC.selectedViewController;
    if ([selectedView isKindOfClass:[CloudViewController class]]) {
        CloudViewController *cloudViewController = (CloudViewController*)selectedView;
        selectedView = cloudViewController.selectedViewController;
    }
        if (indexPath.row == 0) {
            UserViewController *userView = [[UserViewController alloc] init];
            [(UINavigationController*)selectedView pushViewController:userView animated:YES];
        } else if (indexPath.row == 1) {
            CloudTransferViewController *transferView = [[CloudTransferViewController alloc] initWithFile:nil];
            transferView.rootViewController = [(UINavigationController*)selectedView topViewController];
            [(UINavigationController*)selectedView pushViewController:transferView animated:YES];
        } else if (indexPath.row == 2) {
//            ContactViewController *contactView = [[ContactViewController alloc] init];
//            [(UINavigationController*)selectedView pushViewController:contactView animated:YES];
            NSArray *imageArray = @[[UIImage imageNamed:@"logo120_120-2"]];//
            if (imageArray) {
                NSMutableDictionary *shareParams = [NSMutableDictionary dictionary];
                NSString *link = @"http://fir.im/z6hp";
                [shareParams SSDKSetupShareParamsByText:@"分享链接:http://fir.im/z6hp,点击分享,即可获取链接"
                                                 images:nil
                                                    url: [NSURL URLWithString:link]
                                                  title:@"分享应用到您的好友"
                                                   type:SSDKContentTypeAuto];
                
                [SSUIShareActionSheetStyle setShareActionSheetStyle:ShareActionSheetStyleSimple];
                [ShareSDK showShareActionSheet:nil items:nil shareParams:shareParams onShareStateChanged:^(SSDKResponseState state, SSDKPlatformType platformType, NSDictionary *userData, SSDKContentEntity *contentEntity, NSError *error, BOOL end) {
                    switch (state) {
                        case SSDKResponseStateSuccess:
                        {
                            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"分享成功"
                                                                                message:nil
                                                                               delegate:nil
                                                                      cancelButtonTitle:@"确定"
                                                                      otherButtonTitles:nil];
                            [alertView show];
                            
                            break;
                        }
                        case SSDKResponseStateFail:
                        {
                            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"分享失败"
                                                                            message:[NSString stringWithFormat:@"%@",error]
                                                                           delegate:nil
                                                                  cancelButtonTitle:@"OK"
                                                                  otherButtonTitles:nil, nil];
                            [alert show];
                        }
                        default:
                            break;
                    }
                }];
            }

            
        } else if (indexPath.row == 3) {
            SettingViewController *settingView = [[SettingViewController alloc] init];
            [(UINavigationController*)selectedView pushViewController:settingView animated:YES];
        }
//    if (indexPath.row == 0) {
//        UserViewController *userView = [[UserViewController alloc] init];
//        [(UINavigationController*)selectedView pushViewController:userView animated:YES];
//    } else if (indexPath.row == 1) {
//        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
//        appDelegate.transferTaskCount = 0;
//        CloudTransferViewController *transferView = [[CloudTransferViewController alloc] initWithFile:nil];
//        transferView.rootViewController = [(UINavigationController*)selectedView topViewController];
//        [(UINavigationController*)selectedView pushViewController:transferView animated:YES];
//    } else if (indexPath.row == 2) {
//        SettingViewController *settingView = [[SettingViewController alloc] init];
//        [(UINavigationController*)selectedView pushViewController:settingView animated:YES];
//    }
}

@end
