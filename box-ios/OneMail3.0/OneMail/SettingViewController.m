//
//  SettingViewController.m
//  OneMail
//
//  Created by cse  on 15/12/5.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "SettingViewController.h"
#import "AppDelegate.h"
#import "MenuViewController.h"
#import "File.h"
#import "TeamSpace.h"
#import "TransportTask.h"
#import "SettingEmailAddressViewController.h"
#import "SettingEmailSyncViewController.h"
#import "SettingEmailRemindViewController.h"
#import "CloudBackUpViewController.h"
#import "CloudBackupOpenViewController.h"
#import "SettingAboutViewController.h"
#import "CloudTransferViewController.h"

@interface SettingViewController ()<UIActionSheetDelegate>

@property (nonatomic, strong) UILabel         *settingTitleLabel;
@property (nonatomic, strong) UIButton        *settingBackButton;

@property (nonatomic, strong) UIView          *settingFilesHeader;
@property (nonatomic, strong) UITableViewCell *settingCacheCell;
@property (nonatomic, strong) UIView          *settingMailsHeader;
@property (nonatomic, strong) UITableViewCell *settingEmailCell;
@property (nonatomic, strong) UITableViewCell *settingSyncCell;
@property (nonatomic, strong) UITableViewCell *settingRemindCell;
@property (nonatomic, strong) UIView          *settingNetworkHeader;
@property (nonatomic, strong) UITableViewCell *settingWiFiCell;
@property (nonatomic, strong) UITableViewCell *settingBackupCell;
@property (nonatomic, strong) UITableViewCell *settingAutoLoginCell;
@property (nonatomic, strong) UITableViewCell *settingLanguageCell;
@property (nonatomic, strong) UITableViewCell *settingAboutCell;
@property (nonatomic, strong) UIView          *settingLogoutView;

@property (nonatomic, strong) UILabel         *settingCacheSizeLabel;
@property (nonatomic, strong) UILabel         *settingEmailAddressLabel;
@property (nonatomic, strong) UILabel         *settingLanguageLabel;

@end

@implementation SettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.settingTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.settingTitleLabel.text = getLocalizedString(@"SettingTitle", nil);
    
    self.settingBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.settingBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.settingBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.settingBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.settingBackButton addTarget:self action:@selector(settingBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.showsVerticalScrollIndicator = NO;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    [self.tableView reloadData];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.settingTitleLabel];
    [self.navigationController.navigationBar addSubview:self.settingBackButton];
    
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailBinded.boolValue) {
        self.settingEmailAddressLabel.text = userSetting.emailAddress;
    } else {
        self.settingEmailAddressLabel.text = getLocalizedString(@"UserMailNoBound", nil);
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.settingTitleLabel removeFromSuperview];
    [self.settingBackButton removeFromSuperview];
}

- (void)settingBackButtonClick {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (appDelegate.leftViewOpened) {
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshUserIcon];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshEmailAddress];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshTransferTaskCount];
        [appDelegate.LeftSlideVC openLeftView];
    }
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

#pragma mark Files Setting

- (UIView*)settingFilesHeader {
    if (!_settingFilesHeader) {
        _settingFilesHeader = [CommonFunction tableViewHeaderWithTitle:@"Files"];
        //_settingFilesHeader = [self settingHeaderWithTitle:@"Files"];
    }
    return _settingFilesHeader;
}

- (UITableViewCell*)settingCacheCell {
//    if (!_settingCacheCell) {
        _settingCacheCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *cacheTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        cacheTitle.text = getLocalizedString(@"SettingFileClearCache", nil);
        cacheTitle.font = [UIFont systemFontOfSize:17.0f];
        cacheTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        cacheTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:cacheTitle limitSize:CGSizeMake(1000, 1000)];
        cacheTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingCacheCell.contentView addSubview:cacheTitle];
        
        self.settingCacheSizeLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.settingCacheSizeLabel.text = [self cacheSize];
        self.settingCacheSizeLabel.font = [UIFont systemFontOfSize:14.0f];
        self.settingCacheSizeLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.settingCacheSizeLabel.textAlignment = NSTextAlignmentRight;
        CGSize adjustCacheSize = [CommonFunction labelSizeWithLabel:self.settingCacheSizeLabel limitSize:CGSizeMake(1000, 1000)];
        self.settingCacheSizeLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustCacheSize.width, 11, adjustCacheSize.width, 22);
        [_settingCacheCell.contentView addSubview:self.settingCacheSizeLabel];
//    }
    return _settingCacheCell;
}

- (void)cacheSizePrompt {
    UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:getLocalizedString(@"SettingFileClearCachePrompt", nil) delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"Confirm", nil), nil];
    actionSheet.tag = 10001;
    [actionSheet showInView:self.view];
}

- (NSString*) cacheSize {
    unsigned long long cacheSize = 0;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSError *error = nil;
    NSArray *paths = [[NSFileManager defaultManager] subpathsOfDirectoryAtPath:appDelegate.localManager.userDataPath error:nil];
    for (NSString* path in paths) {
        NSString* subPath = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:path];
        NSDictionary *dictionary = [[NSFileManager defaultManager] attributesOfItemAtPath:subPath error: &error];
        if ([[dictionary objectForKey:NSFileType] isEqualToString:NSFileTypeRegular]) {
            cacheSize += [[dictionary objectForKey:NSFileSize] unsignedLongLongValue];
        }
    }
    if (cacheSize > 0) {
        return [CommonFunction pretySize:cacheSize];
    } else {
        return @"0.00KB";
    }
}

- (void) clearCacheMemory {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext* ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlock:^{
        NSError* error = nil;
        NSMutableArray *fileArray = [[NSMutableArray alloc] init];
        NSMutableArray *taskArray = [[NSMutableArray alloc] init];
        
        NSPredicate *myFilePredicate = [NSPredicate predicateWithFormat:@"(fileOwner = %@ OR fileShareUser = %@) AND transportTask != nil",appDelegate.localManager.userCloudId,appDelegate.localManager.userCloudId];
        NSEntityDescription *myFileEntity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
        NSFetchRequest *myFileRequest = [[NSFetchRequest alloc] init];
        [myFileRequest setPredicate:myFilePredicate];
        [myFileRequest setEntity:myFileEntity];
        NSArray *myFileArray = [ctx executeFetchRequest:myFileRequest error:&error];
        [fileArray addObjectsFromArray:myFileArray];
        
        NSPredicate *myTeamPredicate = [NSPredicate predicateWithFormat:@"teamUserId = %@",appDelegate.localManager.userCloudId];
        NSEntityDescription *myTeamEntity = [NSEntityDescription entityForName:@"TeamSpace" inManagedObjectContext:ctx];
        NSFetchRequest *myTeamRequest = [[NSFetchRequest alloc] init];
        [myTeamRequest setPredicate:myTeamPredicate];
        [myTeamRequest setEntity:myTeamEntity];
        NSArray *myTeamArray = [ctx executeFetchRequest:myTeamRequest error:&error];
        for (TeamSpace *teamSpace in myTeamArray) {
            NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileOwner = %@ AND transportTask != nil",teamSpace.teamId];
            NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
            NSFetchRequest *request = [[NSFetchRequest alloc] init];
            [request setPredicate:predicate];
            [request setEntity:entity];
            NSArray *array = [ctx executeFetchRequest:request error:&error];
            [fileArray addObjectsFromArray:array];
        }
        
        NSPredicate *ghostTaskPredicate = [NSPredicate predicateWithFormat:@"file = NULL AND version = NULL"];
        NSEntityDescription *ghostTaskEntity = [NSEntityDescription entityForName:@"TransportTask" inManagedObjectContext:ctx];
        NSFetchRequest *ghostTaskRequest = [[NSFetchRequest alloc] init];
        [ghostTaskRequest setPredicate:ghostTaskPredicate];
        [ghostTaskRequest setEntity:ghostTaskEntity];
        NSArray *ghostTaskArray = [ctx executeFetchRequest:ghostTaskRequest error:nil];
        [taskArray addObjectsFromArray:ghostTaskArray];
        
        for (File *file in fileArray) {
            if (file.transportTask) {
                [file.transportTask remove];
                file.transportTask = nil;
            }
        }
        
        for (TransportTask *task in taskArray) {
            [task remove];
        }
        
        [ctx save:&error];
        if (error) {
            NSLog(@"%@", error);
        }
    }];
    
    NSError* error = nil;
    [[NSFileManager defaultManager] removeItemAtPath:appDelegate.localManager.userDataPath error:&error];
    if (error) {
        [SNLog Log:LFatal :@"Failed to clear user data ! %@", error];
    } else {
        //[[NSNotificationCenter defaultCenter] postNotificationName:kFileCacheEvent object:self.userDataPath];
    }
    [[NSFileManager defaultManager] createDirectoryAtPath:appDelegate.localManager.userDataPath withIntermediateDirectories:YES attributes:nil error:nil];
}

#pragma mark Mails Setting
- (UIView*)settingMailsHeader {
    if (!_settingMailsHeader) {
        _settingMailsHeader = [self settingHeaderWithTitle:@"Mails"];
    }
    return _settingMailsHeader;
}

- (UITableViewCell*)settingEmailCell {
//    if (!_settingEmailCell) {
        _settingEmailCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *emailTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        emailTitle.text = getLocalizedString(@"SettingMailAddressTitle", nil);
        emailTitle.font = [UIFont systemFontOfSize:17.0f];
        emailTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        emailTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:emailTitle limitSize:CGSizeMake(1000, 1000)];
        emailTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingEmailCell.contentView addSubview:emailTitle];
        
        self.settingEmailAddressLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        if ([UserSetting defaultSetting].emailAddress) {
            self.settingEmailAddressLabel.text = [UserSetting defaultSetting].emailAddress;
        } else {
            self.settingEmailAddressLabel.text = getLocalizedString(@"UserMailNoBound", nil);
        }
        self.settingEmailAddressLabel.font = [UIFont systemFontOfSize:14.0f];
        self.settingEmailAddressLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.settingEmailAddressLabel.textAlignment = NSTextAlignmentRight;
        CGFloat emaliAddressMaxWidth = CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(emailTitle.frame)-15;
        CGSize adjustAddressSize = [CommonFunction labelSizeWithLabel:self.settingEmailAddressLabel limitSize:CGSizeMake(1000, 1000)];
        emaliAddressMaxWidth = MIN(emaliAddressMaxWidth, adjustAddressSize.width);
        self.settingEmailAddressLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-emaliAddressMaxWidth, 11, emaliAddressMaxWidth, 22);
        [_settingEmailCell.contentView addSubview:self.settingEmailAddressLabel];
//    }
    return _settingEmailCell;
}

- (UITableViewCell*)settingSyncCell {
    if (!_settingSyncCell) {
        _settingSyncCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *syncTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        syncTitle.text = getLocalizedString(@"SettingMailSyncTitle", nil);
        syncTitle.font = [UIFont systemFontOfSize:17.0f];
        syncTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        syncTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:syncTitle limitSize:CGSizeMake(1000, 1000)];
        syncTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingSyncCell.contentView addSubview:syncTitle];
    }
    return _settingSyncCell;
}

- (UITableViewCell*)settingRemindCell {
    if (!_settingRemindCell) {
        _settingRemindCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *remindTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        remindTitle.text = getLocalizedString(@"SettingMailRemainTitle", nil);
        remindTitle.font = [UIFont systemFontOfSize:17.0f];
        remindTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        remindTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:remindTitle limitSize:CGSizeMake(1000, 1000)];
        remindTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingRemindCell.contentView addSubview:remindTitle];
    }
    return _settingRemindCell;
}

#pragma mark Network Setting

- (UIView*)settingNetworkHeader {
    if (!_settingNetworkHeader) {
        _settingNetworkHeader = [CommonFunction tableViewHeaderWithTitle:@"Network"];
        //_settingNetworkHeader = [self settingHeaderWithTitle:@"Network"];
    }
    return _settingNetworkHeader;
}

- (UITableViewCell*)settingWiFiCell {
//    if (!_settingWiFiCell) {
        _settingWiFiCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *WiFiTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        WiFiTitle.text = getLocalizedString(@"SettingNetworkWiFiNotification", nil);
        WiFiTitle.font = [UIFont systemFontOfSize:17.0f];
        WiFiTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        WiFiTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:WiFiTitle limitSize:CGSizeMake(1000, 1000)];
        WiFiTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingWiFiCell.contentView addSubview:WiFiTitle];
        
        UISwitch *WiFiSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (88-72)/2, 51, 36)];
        if ([UserSetting defaultSetting].cloudWiFiPrompt.boolValue) {
            [WiFiSwitch setOn:YES];
        } else {
            [WiFiSwitch setOn:NO];
        }
        [WiFiSwitch addTarget:self action:@selector(WiFiSwitchSelect:) forControlEvents:UIControlEventTouchUpInside];
        [_settingWiFiCell.contentView addSubview:WiFiSwitch];
//    }
    return _settingWiFiCell;
}

- (void)WiFiSwitchSelect:(id)sender {
    UISwitch *WiFiSwicth = (UISwitch*)sender;
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.cloudWiFiPrompt.boolValue) {
        userSetting.cloudWiFiPrompt = @(0);
        [WiFiSwicth setOn:NO animated:YES];
    } else {
        userSetting.cloudWiFiPrompt = @(1);
        [WiFiSwicth setOn:YES animated:YES];
    }
}

#pragma mark Backup Setting

- (UITableViewCell*)settingBackupCell {
//    if (!_settingBackupCell) {
        _settingBackupCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *backupTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        backupTitle.text = getLocalizedString(@"BackupTitle", nil);
        backupTitle.font = [UIFont systemFontOfSize:17.0f];
        backupTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        backupTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:backupTitle limitSize:CGSizeMake(1000, 1000)];
        backupTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingBackupCell.contentView addSubview:backupTitle];
//    }
    return _settingBackupCell;
}

#pragma mark AutoLogin Setting

- (UITableViewCell*)settingAutoLoginCell {
//    if (!_settingAutoLoginCell) {
        _settingAutoLoginCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *autoLoginTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        autoLoginTitle.text = getLocalizedString(@"SettingAutoLoginTitle", nil);
        autoLoginTitle.font = [UIFont systemFontOfSize:17.0f];
        autoLoginTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        autoLoginTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:autoLoginTitle limitSize:CGSizeMake(1000, 1000)];
        autoLoginTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingAutoLoginCell.contentView addSubview:autoLoginTitle];
        
        UISwitch *autoLoginSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (88-72)/2, 51, 36)];
        if ([UserSetting defaultSetting].cloudAutoLogin.boolValue) {
            [autoLoginSwitch setOn:YES];
        } else {
            [autoLoginSwitch setOn:NO];
        }
        [autoLoginSwitch addTarget:self action:@selector(autoLoginSwitchSelect:) forControlEvents:UIControlEventTouchUpInside];
        [_settingAutoLoginCell.contentView addSubview:autoLoginSwitch];
//    }
    return _settingAutoLoginCell;
}

- (void)autoLoginSwitchSelect:(id)sender {
    UISwitch *autoLoginSwicth = (UISwitch*)sender;
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.cloudAutoLogin.boolValue) {
        userSetting.cloudAutoLogin = @(0);
        [autoLoginSwicth setOn:NO animated:YES];
    } else {
        userSetting.cloudAutoLogin = @(1);
        [autoLoginSwicth setOn:YES animated:YES];
    }
}


#pragma mark Language Setting

- (UITableViewCell*)settingLanguageCell {
//    if (!_settingLanguageCell) {
        _settingLanguageCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *languageTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        languageTitle.text = getLocalizedString(@"SettingLanguage", nil);
        languageTitle.font = [UIFont systemFontOfSize:17.0f];
        languageTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        languageTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:languageTitle limitSize:CGSizeMake(1000, 1000)];
        languageTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingLanguageCell.contentView addSubview:languageTitle];
        
        self.settingLanguageLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        if ([[UserSetting defaultSetting].cloudLanguage isEqualToString:@"zh-Hans"]) {
            self.settingLanguageLabel.text = getLocalizedString(@"SettingLanguageChineseSimplified", nil);
        } else if ([[UserSetting defaultSetting].cloudLanguage isEqualToString:@"en"]) {
            self.settingLanguageLabel.text = getLocalizedString(@"SettingLanguageEnglish", nil);
        } else {
            self.settingLanguageLabel.text = getLocalizedString(@"SettingLanguageSystem", nil);
        }
        self.settingLanguageLabel.font = [UIFont systemFontOfSize:14.0f];
        self.settingLanguageLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.settingLanguageLabel.textAlignment = NSTextAlignmentRight;
        CGSize adjustLanguageSize = [CommonFunction labelSizeWithLabel:self.settingLanguageLabel limitSize:CGSizeMake(1000, 1000)];
        self.settingLanguageLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustLanguageSize.width, 11, adjustLanguageSize.width, 22);
        [_settingLanguageCell.contentView addSubview:self.settingLanguageLabel];
//    }
    return _settingLanguageCell;
}

- (void)settingLanguage {
    UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"SettingLanguageChineseSimplified", nil),getLocalizedString(@"SettingLanguageEnglish", nil), getLocalizedString(@"SettingLanguageSystem", nil), nil];
    actionSheet.tag = 10002;
    [actionSheet showInView:self.view];
}

#pragma mark About Setting

- (UITableViewCell*)settingAboutCell {
//    if (!_settingAboutCell) {
        _settingAboutCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *aboutTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        aboutTitle.text = getLocalizedString(@"SettingAboutTitle", nil);
        aboutTitle.font = [UIFont systemFontOfSize:17.0f];
        aboutTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        aboutTitle.textAlignment = NSTextAlignmentLeft;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:aboutTitle limitSize:CGSizeMake(1000, 1000)];
        aboutTitle.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
        [_settingAboutCell.contentView addSubview:aboutTitle];
//    }
    return _settingAboutCell;
}

#pragma mark Logout Setting

- (UIView*)settingLogoutView {
//    if (!_settingLogoutView) {
        _settingLogoutView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 88)];
        _settingLogoutView.backgroundColor = [UIColor clearColor];
        UIButton *logoutButton = [[UIButton alloc] initWithFrame:CGRectMake(30, 22, CGRectGetWidth(self.view.frame)-30-30, 44)];
        logoutButton.layer.cornerRadius = 4;
        logoutButton.layer.masksToBounds = YES;
        [logoutButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"SettingLogout", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:20.0f]}] forState:UIControlStateNormal];
        logoutButton.backgroundColor = [CommonFunction colorWithString:@"fc5043" alpha:1.0f];
        [logoutButton addTarget:self action:@selector(logoutSelect) forControlEvents:UIControlEventTouchUpInside];
        [_settingLogoutView addSubview:logoutButton];
//    }
    return _settingLogoutView;
}

- (void)logoutSelect {
    UIActionSheet *logoutSheet = [[UIActionSheet alloc] initWithTitle:getLocalizedString(@"SettingLogoutPrompt", nil) delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"Confirm", nil), nil];
    logoutSheet.tag = 10003;
    [logoutSheet showInView:self.view];
}


- (void)logout{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [CloudTransferViewController pauseAllTransferTask];
    [[UserSetting defaultSetting] logout];
    [appDelegate.LeftSlideVC.leftTableview removeFromSuperview];
    appDelegate.LeftSlideVC = nil;
    [appDelegate.localManager.managedObjectContext save:nil];
    [appDelegate.navigationController popToRootViewControllerAnimated:YES];
    
    [appDelegate.remoteManager cloudLogout:^(id retobj) {
        [appDelegate.networkReachability stopMonitoring];
        appDelegate.startMonitor = NO;
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {

    }];
}

#pragma mark ActionSheet Delegate 

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (actionSheet.tag == 10001) {
        if (buttonIndex == 0) {
            [self clearCacheMemory];
            self.settingCacheSizeLabel.text = [self cacheSize];
        }
    } else if (actionSheet.tag == 10002) {
        UserSetting *userSetting = [UserSetting defaultSetting];
        if (buttonIndex == 0) {
            userSetting.cloudLanguage = @"zh-Hans";
            self.settingLanguageLabel.text = getLocalizedString(@"SettingLanguageChineseSimplified", nil);
        } else if (buttonIndex == 1) {
            userSetting.cloudLanguage = @"en";
            self.settingLanguageLabel.text = getLocalizedString(@"SettingLanguageEnglish", nil);
        } else if (buttonIndex == 2) {
            userSetting.cloudLanguage = @"system";
            self.settingLanguageLabel.text = getLocalizedString(@"SettingLanguageSystem", nil);
        }
        [self.tableView reloadData];
        self.settingTitleLabel.text = getLocalizedString(@"SettingTitle", nil);
        [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.LocalizedChange" object:nil];
    } else if (actionSheet.tag == 10003) {
        if (buttonIndex == 0) {
            [self logout];
        }
    }
}

#pragma mark TableView Delegate+Data Source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    //    return 6;
    return 5;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    //    if (section == 0) {
    //        return 1;
    //    } else if (section == 1) {
    //        return 3;
    //    } else if (section == 2) {
    //        return 1;
    //    } else if (section == 3) {
    //        return 1;
    //    } else if (section == 4) {
    //        return 1;
    //    } else {
    //        return 2;
    //    }
    if (section == 0) {
        return 1;
    } else if (section == 1) {
        return 1;
    } else if (section == 2) {
        return 1;
    } else if (section == 3) {
        return 1;
    } else{
        return 2;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 22.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    //    if (section == 5) {
    //        return CGRectGetHeight(self.settingLogoutView.frame);
    //    }
    if (section == 4) {
        return CGRectGetHeight(self.settingLogoutView.frame);
    }
    return 0.1f;
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    //    if (section == 0) {
    //        return self.settingFilesHeader;
    //    } else if (section == 1) {
    //        return self.settingMailsHeader;
    //    } else if (section == 2) {
    //        return self.settingNetworkHeader;
    //    } else {
    //        return nil;
    //    }
    if (section == 0) {
        return self.settingFilesHeader;
    } else if (section == 1) {
        return self.settingNetworkHeader;
    } else {
        return nil;
    }
}

- (UIView*)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    //    if (section == 5) {
    //        return self.settingLogoutView;
    //    }
    if (section == 4) {
        return self.settingLogoutView;
    }
    return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 44;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    //    if (indexPath.section == 0) {
    //        return self.settingCacheCell;
    //    } else if (indexPath.section == 1) {
    //        if (indexPath.row == 0) {
    //            return self.settingEmailCell;
    //        } else if (indexPath.row == 1) {
    //            return self.settingSyncCell;
    //        } else {
    //            return self.settingRemindCell;
    //        }
    //    } else if (indexPath.section == 2) {
    //        return self.settingWiFiCell;
    //    } else if (indexPath.section == 3) {
    //        return self.settingBackupCell;
    //    } else if (indexPath.section == 4) {
    //        return self.settingAutoLoginCell;
    //    } else {
    //        if (indexPath.row == 0) {
    //            return self.settingLanguageCell;
    //        } else {
    //            return self.settingAboutCell;
    //        }
    //    }
    if (indexPath.section == 0) {
        return self.settingCacheCell;
    } else if (indexPath.section == 1) {
        return self.settingWiFiCell;
    } else if (indexPath.section == 2) {
        return self.settingBackupCell;
    } else if (indexPath.section == 3) {
        return self.settingAutoLoginCell;
    } else{
        if (indexPath.row == 0) {
            return self.settingLanguageCell;
//            return self.settingSyncCell;
        } else {
            return self.settingAboutCell;
        }
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    //    if (indexPath.section == 0) {
    //        [self cacheSizePrompt];
    //    } else if (indexPath.section == 1) {
    //        if (indexPath.row == 0) {
    //            SettingEmailAddressViewController *emailAddress = [[SettingEmailAddressViewController alloc] init];
    //            [self.navigationController pushViewController:emailAddress animated:YES];
    //        } else if (indexPath.row == 1) {
    //            SettingEmailSyncViewController *emailSync = [[SettingEmailSyncViewController alloc] init];
    //            [self.navigationController pushViewController:emailSync animated:YES];
    //        } else {
    //            SettingEmailRemindViewController *emailRemind = [[SettingEmailRemindViewController alloc] init];
    //            [self.navigationController pushViewController:emailRemind animated:YES];
    //        }
    //    } else if (indexPath.section == 3) {
    //        if ([UserSetting defaultSetting].cloudAssetBackupOpen.boolValue) {
    //            CloudBackUpViewController *backupView = [[CloudBackUpViewController alloc] init];
    //            backupView.settingViewController = self;
    //            [self.navigationController pushViewController:backupView animated:YES];
    //        } else {
    //            CloudBackupOpenViewController *backupOpenView = [[CloudBackupOpenViewController alloc] init];
    //            backupOpenView.settingViewController = self;
    //            [self.navigationController pushViewController:backupOpenView animated:YES];
    //        }
    //    } else if (indexPath.section == 5) {
    //        if (indexPath.row == 0) {
    //            [self settingLanguage];
    //        } else {
    //            SettingAboutViewController *aboutView = [[SettingAboutViewController alloc] init];
    //            [self.navigationController pushViewController:aboutView animated:YES];
    //        }
    //    }
    //
    //    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (indexPath.section == 0) {
        [self cacheSizePrompt];
    } else if (indexPath.section == 2) {
        if ([UserSetting defaultSetting].cloudAssetBackupOpen.boolValue) {
            CloudBackUpViewController *backupView = [[CloudBackUpViewController alloc] init];
            backupView.settingViewController = self;
            [self.navigationController pushViewController:backupView animated:YES];
        } else {
            CloudBackupOpenViewController *backupOpenView = [[CloudBackupOpenViewController alloc] init];
            backupOpenView.settingViewController = self;
            [self.navigationController pushViewController:backupOpenView animated:YES];
        }
    } else if (indexPath.section == 4) {
        if (indexPath.row == 0) {
            [self settingLanguage];
        } else {
            SettingAboutViewController *aboutView = [[SettingAboutViewController alloc] init];
            [self.navigationController pushViewController:aboutView animated:YES];
        }
    }
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}


@end
