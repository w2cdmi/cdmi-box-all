//
//  SettingEmailSyncViewController.m
//  OneMail
//
//  Created by cse  on 15/12/7.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "SettingEmailSyncViewController.h"
#import "AppDelegate.h"
#import "MessageCacheOperation.h"

@interface SettingEmailSyncViewController ()<UIActionSheetDelegate>

@property (nonatomic, strong) UILabel     *mailTitleLabel;
@property (nonatomic, strong) UIButton    *mailBackButton;

@property (nonatomic, strong) UIView          *syncModeHeader;
@property (nonatomic, strong) UITableViewCell *scnserveModeCell;
@property (nonatomic, strong) UITableViewCell *smartModeCell;
@property (nonatomic, strong) UIView          *syncAttachmentHeader;
@property (nonatomic, strong) UITableViewCell *archiveAutoCell;
@property (nonatomic, strong) UITableViewCell *archiveWiFiCell;
@property (nonatomic, strong) UITableViewCell *retentionTimeCell;

@property (nonatomic, strong) UIButton *scnserveButton;
@property (nonatomic, strong) UIButton *smartButton;
@property (nonatomic, strong) UISwitch *autoArchiveAttachment;
@property (nonatomic, strong) UISwitch *wifiArchiveAttachment;
@property (nonatomic, strong) UILabel  *retentionTimeLabel;

@end

@implementation SettingEmailSyncViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.mailTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailTitleLabel.text = NSLocalizedString(@"SettingMailSyncTitle", nil);
    
    self.mailBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailBackButton addTarget:self action:@selector(mailBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    [self.tableView reloadData];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.mailTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailTitleLabel removeFromSuperview];
    [self.mailBackButton removeFromSuperview];
}

- (void)mailBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (UIView*)settingHeaderWithTitle:(NSString*)title {
    UIView *settingHeader = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 22)];
    settingHeader.backgroundColor = [UIColor clearColor];
    if (title) {
        UILabel *settingLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 0, CGRectGetWidth(settingHeader.frame)-15-15, CGRectGetHeight(settingHeader.frame))];
        settingLabel.text = title;
        settingLabel.font = [UIFont boldSystemFontOfSize:12.0f];
        settingLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        settingLabel.textAlignment = NSTextAlignmentLeft;
        [settingHeader addSubview:settingLabel];
    }
    return settingHeader;
}

#pragma mark Mode Setting
- (UIView*)syncModeHeader {
    if (!_syncModeHeader) {
        _syncModeHeader = [self settingHeaderWithTitle:NSLocalizedString(@"SettingMailSyncModeTitle", nil)];
    }
    return _syncModeHeader;
}

- (UITableViewCell*)scnserveModeCell {
    if (!_scnserveModeCell) {
        _scnserveModeCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _scnserveModeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-22-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailSyncModeSaving", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_scnserveModeCell.contentView addSubview:titleLable];
        
        UILabel *promptLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        promptLabel.text = NSLocalizedString(@"SettingMailSyncModeSavingPrompt", nil);
        promptLabel.font = [UIFont systemFontOfSize:14.0f];
        promptLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        promptLabel.textAlignment = NSTextAlignmentLeft;
        promptLabel.numberOfLines = 0;
        CGSize adjustPromptSize = [CommonFunction labelSizeWithLabel:promptLabel limitSize:CGSizeMake(CGRectGetWidth(titleLable.frame), 1000)];
        promptLabel.frame = CGRectMake(15, CGRectGetMaxY(titleLable.frame)+4, CGRectGetWidth(titleLable.frame), MAX(adjustPromptSize.height, 20));
        [_scnserveModeCell.contentView addSubview:promptLabel];
        
        _scnserveModeCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 11+CGRectGetHeight(titleLable.frame)+4+CGRectGetHeight(promptLabel.frame)+11);
        
        self.scnserveButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-22, (CGRectGetHeight(_scnserveModeCell.frame)-22)/2, 22, 22)];
        if ([UserSetting defaultSetting].emailScnserveMode.boolValue) {
            [self.scnserveButton setImage:[UIImage imageNamed:@"ic_radiobutton_on_nor"] forState:UIControlStateNormal];
        } else {
            [self.scnserveButton setImage:[UIImage imageNamed:@"ic_radiobutton_off_nor"] forState:UIControlStateNormal];
        }
        [self.scnserveButton addTarget:self action:@selector(scnserveModeSelect) forControlEvents:UIControlEventTouchUpInside];
        [_scnserveModeCell.contentView addSubview:self.scnserveButton];
    }
    return _scnserveModeCell;
}

- (void)scnserveModeSelect {
    UserSetting *userSetting = [UserSetting defaultSetting];
    userSetting.emailScnserveMode = @(1);
    userSetting.emailSmartMode = @(0);
    [self.scnserveButton setImage:[UIImage imageNamed:@"ic_radiobutton_on_nor"] forState:UIControlStateNormal];
    [self.smartButton setImage:[UIImage imageNamed:@"ic_radiobutton_off_nor"] forState:UIControlStateNormal];
}

- (UITableViewCell*)smartModeCell {
    if (!_smartModeCell) {
        _smartModeCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _smartModeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-22-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailSyncModeSmart", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_smartModeCell.contentView addSubview:titleLable];
        
        UILabel *promptLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        promptLabel.text = NSLocalizedString(@"SettingMailSyncModeSmartPrompt", nil);
        promptLabel.font = [UIFont systemFontOfSize:14.0f];
        promptLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        promptLabel.textAlignment = NSTextAlignmentLeft;
        promptLabel.numberOfLines = 0;
        CGSize adjustPromptSize = [CommonFunction labelSizeWithLabel:promptLabel limitSize:CGSizeMake(CGRectGetWidth(titleLable.frame), 1000)];
        promptLabel.frame = CGRectMake(15, CGRectGetMaxY(titleLable.frame)+4, CGRectGetWidth(titleLable.frame), MAX(adjustPromptSize.height, 20));
        [_smartModeCell.contentView addSubview:promptLabel];
        
        _smartModeCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 11+CGRectGetHeight(titleLable.frame)+4+CGRectGetHeight(promptLabel.frame)+11);
        
        self.smartButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-22, (CGRectGetHeight(_smartModeCell.frame)-22)/2, 22, 22)];
        if ([UserSetting defaultSetting].emailSmartMode.boolValue) {
            [self.smartButton setImage:[UIImage imageNamed:@"ic_radiobutton_on_nor"] forState:UIControlStateNormal];
        } else {
            [self.smartButton setImage:[UIImage imageNamed:@"ic_radiobutton_off_nor"] forState:UIControlStateNormal];
        }
        [self.smartButton addTarget:self action:@selector(smartModeSelect) forControlEvents:UIControlEventTouchUpInside];
        
        [_smartModeCell.contentView addSubview:self.smartButton];
    }
    return _smartModeCell;
}

- (void)smartModeSelect {
    UserSetting *userSetting = [UserSetting defaultSetting];
    userSetting.emailScnserveMode = @(0);
    userSetting.emailSmartMode = @(1);
    [self.scnserveButton setImage:[UIImage imageNamed:@"ic_radiobutton_off_nor"] forState:UIControlStateNormal];
    [self.smartButton setImage:[UIImage imageNamed:@"ic_radiobutton_on_nor"] forState:UIControlStateNormal];
}

#pragma mark Attachment Setting
- (UIView*)syncAttachmentHeader {
    if (!_syncAttachmentHeader) {
        _syncAttachmentHeader = [self settingHeaderWithTitle:NSLocalizedString(@"SettingMailSyncAttachmentTitle", nil)];
    }
    return _syncAttachmentHeader;
}

- (UITableViewCell*)archiveAutoCell {
    if (!_archiveAutoCell) {
        _archiveAutoCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _archiveAutoCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _archiveAutoCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailSyncAttachmentAutoArchive", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_archiveAutoCell.contentView addSubview:titleLable];
        
        self.autoArchiveAttachment = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (CGRectGetHeight(_archiveAutoCell.frame)-36)/2, 51, 36)];
        if ([UserSetting defaultSetting].emailAutoArchiveAttchment.boolValue) {
            [self.autoArchiveAttachment setOn:YES];
        } else {
            [self.autoArchiveAttachment setOn:NO];
        }
        [self.autoArchiveAttachment addTarget:self action:@selector(autoArchiveAttachment) forControlEvents:UIControlEventTouchUpInside];
        [_archiveAutoCell.contentView addSubview:self.autoArchiveAttachment];
    }
    return _archiveAutoCell;
}

- (void)autoArchiveSelect {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailAutoArchiveAttchment.boolValue) {
        userSetting.emailAutoArchiveAttchment = @(0);
        [self.autoArchiveAttachment setOn:NO animated:YES];
    } else {
        userSetting.emailAutoArchiveAttchment = @(1);
        [self.autoArchiveAttachment setOn:YES animated:YES];
    }
}

- (UITableViewCell*)archiveWiFiCell {
    if (!_archiveWiFiCell) {
        _archiveWiFiCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _archiveWiFiCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _archiveWiFiCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailSyncAttachmentWiFiArchive", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_archiveWiFiCell.contentView addSubview:titleLable];
        
        self.wifiArchiveAttachment = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (CGRectGetHeight(_archiveWiFiCell.frame)-36)/2, 51, 36)];
        if ([UserSetting defaultSetting].emailWiFiArchiveAttchment.boolValue) {
            [self.wifiArchiveAttachment setOn:YES];
        } else {
            [self.wifiArchiveAttachment setOn:NO];
        }
        [self.wifiArchiveAttachment addTarget:self action:@selector(WiFiArchiveSelect) forControlEvents:UIControlEventTouchUpInside];
        [_archiveWiFiCell.contentView addSubview:self.wifiArchiveAttachment];
    }
    return _archiveWiFiCell;
}

- (void)WiFiArchiveSelect {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailWiFiArchiveAttchment.boolValue) {
        userSetting.emailWiFiArchiveAttchment = @(0);
        [self.wifiArchiveAttachment setOn:NO animated:YES];
    } else {
        userSetting.emailWiFiArchiveAttchment = @(1);
        [self.wifiArchiveAttachment setOn:YES animated:YES];
    }
}

#pragma mark Retention 
- (UITableViewCell*)retentionTimeCell {
    if (!_retentionTimeCell) {
        _retentionTimeCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _retentionTimeCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _retentionTimeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.retentionTimeLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        if ([UserSetting defaultSetting].emailRetentionTime.intValue == 0) {
            self.retentionTimeLabel.text = NSLocalizedString(@"SettingMailSyncMailOneWeek", nil);
        } else if ([UserSetting defaultSetting].emailRetentionTime.intValue == 1) {
            self.retentionTimeLabel.text = NSLocalizedString(@"SettingMailSyncMailOneMonth", nil);
        } else {
            self.retentionTimeLabel.text = NSLocalizedString(@"SettingMailSyncMailThreeMonth", nil);
        }
        self.retentionTimeLabel.font = [UIFont systemFontOfSize:14.0f];
        self.retentionTimeLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.retentionTimeLabel.textAlignment = NSTextAlignmentRight;
        CGSize adjustTimeSize = [CommonFunction labelSizeWithLabel:self.retentionTimeLabel limitSize:CGSizeMake(1000, 1000)];
        self.retentionTimeLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustTimeSize.width, 11, adjustTimeSize.width, 22);
        [_retentionTimeCell.contentView addSubview:self.retentionTimeLabel];
    
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-CGRectGetWidth(self.retentionTimeLabel.frame)-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailSyncMailKeepTime", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_retentionTimeCell.contentView addSubview:titleLable];
    }
    return _retentionTimeCell;
}

- (void)retentionTimeChoose {
    UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"SettingMailSyncMailKeepPrompt", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedString(@"SettingMailSyncMailOneWeek", nil), NSLocalizedString(@"SettingMailSyncMailOneMonth", nil), NSLocalizedString(@"SettingMailSyncMailThreeMonth", nil), nil];
    [actionSheet showInView:self.view];
}

- (void)refreshRetentionTime {
    CGSize adjustTimeSize = [CommonFunction labelSizeWithLabel:self.retentionTimeLabel limitSize:CGSizeMake(1000, 1000)];
    self.retentionTimeLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustTimeSize.width, 11, adjustTimeSize.width, 22);
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (buttonIndex == 0) {
        userSetting.emailRetentionTime = @(0);
        self.retentionTimeLabel.text = NSLocalizedString(@"SettingMailSyncMailOneWeek", nil);
        [MessageCacheOperation cleanExpiredMessages];
    } else if (buttonIndex == 1) {
        userSetting.emailRetentionTime = @(1);
        self.retentionTimeLabel.text = NSLocalizedString(@"SettingMailSyncMailOneMonth", nil);
        [MessageCacheOperation cleanExpiredMessages];
    } else if (buttonIndex == 2) {
        userSetting.emailRetentionTime = @(2);
        self.retentionTimeLabel.text = NSLocalizedString(@"SettingMailSyncMailThreeMonth", nil);
        [MessageCacheOperation cleanExpiredMessages];
    }
    [self refreshRetentionTime];
}

#pragma mark TableView Delegate + Data Source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 2;
    } else if (section == 1) {
        return 2;
    } else {
        return 1;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 22.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return self.scnserveModeCell.frame.size.height;
        } else {
            return self.smartModeCell.frame.size.height;
        }
    } else if (indexPath.section == 1) {
        if (indexPath.row == 0) {
            return self.archiveAutoCell.frame.size.height;
        } else {
            return self.archiveWiFiCell.frame.size.height;
        }
    } else {
        return self.retentionTimeCell.frame.size.height;
    }
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return self.syncModeHeader;
    } else if (section == 1) {
        return self.syncAttachmentHeader;
    } else {
        return nil;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return self.scnserveModeCell;
        } else {
            return self.smartModeCell;
        }
    } else if (indexPath.section == 1) {
        if (indexPath.row == 0) {
            return self.archiveAutoCell;
        } else {
            return self.archiveWiFiCell;
        }
    } else {
        return self.retentionTimeCell;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            [self scnserveModeSelect];
        } else {
            [self smartModeSelect];
        }
    } else if (indexPath.section == 1) {
        if (indexPath.row == 0) {
            [self autoArchiveSelect];
        } else {
            [self WiFiArchiveSelect];
        }
    } else {
        [self retentionTimeChoose];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
}

@end
