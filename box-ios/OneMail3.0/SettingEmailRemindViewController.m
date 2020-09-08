//
//  SettingEmailRemindViewController.m
//  OneMail
//
//  Created by cse  on 15/12/7.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "SettingEmailRemindViewController.h"
#import "AppDelegate.h"

@interface SettingEmailRemindViewController ()

@property (nonatomic, strong) UILabel     *mailTitleLabel;
@property (nonatomic, strong) UIButton    *mailBackButton;

@property (nonatomic, strong) UIView *remindHeader;
@property (nonatomic, strong) UITableViewCell *notificationCell;
@property (nonatomic, strong) UITableViewCell *soundCell;
@property (nonatomic, strong) UITableViewCell *vibrateCell;
@property (nonatomic, strong) UIView *disturbHeader;
@property (nonatomic, strong) UITableViewCell *disturbCell;
@property (nonatomic, strong) UITableViewCell *disturbStartCell;
@property (nonatomic, strong) UITableViewCell *disturbEndCell;
@property (nonatomic, strong) UIDatePicker    *disturbDatePicker;

@property (nonatomic, strong) UISwitch *notificationSwitch;
@property (nonatomic, strong) UISwitch *soundsSwitch;
@property (nonatomic, strong) UISwitch *vibrateSwitch;
@property (nonatomic, strong) UISwitch *DNDSwitch;
@property (nonatomic, strong) UILabel  *DNDStartLabel;
@property (nonatomic, strong) UILabel  *DNDEndLabel;

@property (nonatomic, assign) CGFloat tableViewHeight;

@end

@implementation SettingEmailRemindViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.mailTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailTitleLabel.text = NSLocalizedString(@"SettingMailRemainTitle", nil);
    
    self.mailBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailBackButton addTarget:self action:@selector(mailBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    self.tableViewHeight = statusBarFrame.size.height + navigationBarFrame.size.height;
    self.tableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.tableFooterView = self.disturbDatePicker;
    
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

- (UIView*)remindHeader {
    if (!_remindHeader) {
        _remindHeader = [self settingHeaderWithTitle:NSLocalizedString(@"SettingMailRemainHeader", nil)];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_remindHeader.frame);
    }
    return _remindHeader;
}

- (UITableViewCell*)notificationCell {
    if (!_notificationCell) {
        _notificationCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _notificationCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _notificationCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailRemainNotification", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_notificationCell.contentView addSubview:titleLable];
        
        self.notificationSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (CGRectGetHeight(_notificationCell.frame)-36)/2, 51, 36)];
        if ([UserSetting defaultSetting].emailNotification.boolValue) {
            [self.notificationSwitch setOn:YES];
        } else {
            [self.notificationSwitch setOn:NO];
        }
        [self.notificationSwitch addTarget:self action:@selector(notificationSelect) forControlEvents:UIControlEventTouchUpInside];
        [_notificationCell.contentView addSubview:self.notificationSwitch];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_notificationCell.frame);
    }
    return _notificationCell;
}

- (void)notificationSelect {
    [self disturbDatePickerHidden];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailNotification.boolValue) {
        userSetting.emailNotification = @(0);
        [self.notificationSwitch setOn:NO animated:YES];
    } else {
        userSetting.emailNotification = @(1);
        [self.notificationSwitch setOn:YES animated:YES];
    }
}

- (UITableViewCell*)soundCell {
    if (!_soundCell) {
        _soundCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _soundCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _soundCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailRemainSound", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_soundCell.contentView addSubview:titleLable];
        
        self.soundsSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (CGRectGetHeight(_soundCell.frame)-36)/2, 51, 36)];
        if ([UserSetting defaultSetting].emailSounds.boolValue) {
            [self.soundsSwitch setOn:YES];
        } else {
            [self.soundsSwitch setOn:NO];
        }
        [self.soundsSwitch addTarget:self action:@selector(soundSelect) forControlEvents:UIControlEventTouchUpInside];
        [_soundCell.contentView addSubview:self.soundsSwitch];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_soundCell.frame);
    }
    return _soundCell;
}

- (void)soundSelect {
    [self disturbDatePickerHidden];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailSounds.boolValue) {
        userSetting.emailSounds = @(0);
        [self.soundsSwitch setOn:NO animated:YES];
    } else {
        userSetting.emailSounds = @(1);
        [self.soundsSwitch setOn:YES animated:YES];
    }
}

- (UITableViewCell*)vibrateCell {
    if (!_vibrateCell) {
        _vibrateCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _vibrateCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _vibrateCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailRemainShake", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_vibrateCell.contentView addSubview:titleLable];
        
        self.vibrateSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (CGRectGetHeight(_vibrateCell.frame)-36)/2, 51, 36)];
        if ([UserSetting defaultSetting].emailVibration.boolValue) {
            [self.vibrateSwitch setOn:YES];
        } else {
            [self.vibrateSwitch setOn:NO];
        }
        [self.vibrateSwitch addTarget:self action:@selector(vibrateSelect) forControlEvents:UIControlEventTouchUpInside];
        [_vibrateCell.contentView addSubview:self.vibrateSwitch];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_vibrateCell.frame);
    }
    return _vibrateCell;
}

- (void)vibrateSelect {
    [self disturbDatePickerHidden];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailVibration.boolValue) {
        userSetting.emailVibration = @(0);
        [self.vibrateSwitch setOn:NO animated:YES];
    } else {
        userSetting.emailVibration = @(1);
        [self.vibrateSwitch setOn:YES animated:YES];
    }
}

- (UIView*)disturbHeader {
    if (!_disturbHeader) {
        _disturbHeader = [self settingHeaderWithTitle:nil];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_disturbHeader.frame);
    }
    return _disturbHeader;
}

- (UITableViewCell*)disturbCell {
    if (!_disturbCell) {
        _disturbCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _disturbCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailRemainDisturbe", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_disturbCell.contentView addSubview:titleLable];
        
        UILabel *promptLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        promptLabel.text = NSLocalizedString(@"SettingMailRemainDisturbePrompt", nil);
        promptLabel.font = [UIFont systemFontOfSize:14.0f];
        promptLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        promptLabel.textAlignment = NSTextAlignmentLeft;
        promptLabel.numberOfLines = 0;
        CGSize adjustPromptSize = [CommonFunction labelSizeWithLabel:promptLabel limitSize:CGSizeMake(CGRectGetWidth(self.view.frame)-15-15, 1000)];
        promptLabel.frame = CGRectMake(15, CGRectGetMaxY(titleLable.frame)+4, CGRectGetWidth(titleLable.frame), MAX(adjustPromptSize.height, 20));
        [_disturbCell.contentView addSubview:promptLabel];
        
        _disturbCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 11+CGRectGetHeight(titleLable.frame)+4+CGRectGetHeight(promptLabel.frame)+11);
        
        self.DNDSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, (44-36)/2, 51, 36)];
        if ([UserSetting defaultSetting].emailDND.boolValue) {
            [self.DNDSwitch setOn:YES];
        } else {
            [self.DNDSwitch setOn:NO];
        }
        [self.DNDSwitch addTarget:self action:@selector(DNDSelect) forControlEvents:UIControlEventTouchUpInside];
        [_disturbCell.contentView addSubview:self.DNDSwitch];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_disturbCell.frame);
    }
    return _disturbCell;
}

- (void)DNDSelect {
    [self disturbDatePickerHidden];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailDND.boolValue) {
        userSetting.emailDND = @(0);
        [self.DNDSwitch setOn:NO animated:YES];
        self.DNDStartLabel.hidden = YES;
        self.DNDEndLabel.hidden = YES;
    } else {
        userSetting.emailDND = @(1);
        [self.DNDSwitch setOn:YES animated:YES];
        self.DNDStartLabel.hidden = NO;
        self.DNDEndLabel.hidden = NO;
    }
}

- (UITableViewCell*)disturbStartCell {
    if (!_disturbStartCell) {
        _disturbStartCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _disturbStartCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _disturbStartCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailRemainDisturbeStart", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_disturbStartCell.contentView addSubview:titleLable];
        
        self.DNDStartLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.DNDStartLabel.text = [UserSetting defaultSetting].emailDNDStart;
        self.DNDStartLabel.font = [UIFont systemFontOfSize:14.0f];
        self.DNDStartLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.DNDStartLabel.textAlignment = NSTextAlignmentRight;
        CGSize adjustSize = [CommonFunction labelSizeWithLabel:self.DNDStartLabel limitSize:CGSizeMake(1000, 1000)];
        self.DNDStartLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustSize.width, 11, adjustSize.width, 22);
        if ([UserSetting defaultSetting].emailDND.boolValue) {
            self.DNDStartLabel.hidden = NO;
        } else {
            self.DNDStartLabel.hidden = YES;
        }
        [_disturbStartCell addSubview:self.DNDStartLabel];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_disturbStartCell.frame);
    }
    return _disturbStartCell;
}

- (UITableViewCell*)disturbEndCell {
    if (!_disturbEndCell) {
        _disturbEndCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _disturbEndCell.bounds = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44);
        _disturbEndCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22)];
        titleLable.text = NSLocalizedString(@"SettingMailRemainDisturbeEnd", nil);
        titleLable.font = [UIFont systemFontOfSize:17.0f];
        titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLable.textAlignment = NSTextAlignmentLeft;
        [_disturbEndCell.contentView addSubview:titleLable];
        
        self.DNDEndLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.DNDEndLabel.text = [UserSetting defaultSetting].emailDNDEnd;
        self.DNDEndLabel.font = [UIFont systemFontOfSize:14.0f];
        self.DNDEndLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.DNDEndLabel.textAlignment = NSTextAlignmentRight;
        CGSize adjustSize = [CommonFunction labelSizeWithLabel:self.DNDEndLabel limitSize:CGSizeMake(1000, 1000)];
        self.DNDEndLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustSize.width, 11, adjustSize.width, 22);
        if ([UserSetting defaultSetting].emailDND.boolValue) {
            self.DNDEndLabel.hidden = NO;
        } else {
            self.DNDEndLabel.hidden = YES;
        }
        [_disturbEndCell addSubview:self.DNDEndLabel];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_disturbEndCell.frame);
    }
    return _disturbEndCell;
}

- (UIDatePicker *)disturbDatePicker {
    if (!_disturbDatePicker) {
        _disturbDatePicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 216)];
        _disturbDatePicker.datePickerMode = UIDatePickerModeCountDownTimer;
        _disturbDatePicker.minuteInterval = 1;
        _disturbDatePicker.hidden = YES;
        NSString *strLanguage = [[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] objectAtIndex:0];
        NSLocale *locale;
        if ([strLanguage isEqualToString:@"zh-Hans"]) {
            locale = [[NSLocale alloc]initWithLocaleIdentifier:@"zh-Hans"];
        } else {
            locale = [[NSLocale alloc]initWithLocaleIdentifier:@"en"];
        }
        _disturbDatePicker.locale = locale;
        [_disturbDatePicker addTarget:self action:@selector(disturbDatePick:) forControlEvents:UIControlEventValueChanged];
        self.tableViewHeight = self.tableViewHeight + CGRectGetHeight(_disturbDatePicker.frame);
    }
    return _disturbDatePicker;
}

- (void)disturbDatePick:(UIDatePicker*)sender {
    NSDate *selectedDate = sender.date;
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"HH:mm";
    NSString *dateString = [formatter stringFromDate:selectedDate];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (sender.tag == 10001) {
        self.DNDStartLabel.text = dateString;
        userSetting.emailDNDStart = dateString;
    }
    if (sender.tag == 10002) {
        self.DNDEndLabel.text = dateString;
        userSetting.emailDNDEnd = dateString;
    }
}

- (void)disturbDatePickerShow {
    self.disturbDatePicker.hidden = NO;
    [UIView beginAnimations:nil context:nil];
    CGRect tableViewFrame  = self.tableView.frame;
    if (self.tableViewHeight > [UIScreen mainScreen].bounds.size.height) {
        tableViewFrame.origin.y = [UIScreen mainScreen].bounds.size.height - self.tableViewHeight;
        tableViewFrame.size.height = self.tableViewHeight;
    }
    self.tableView.frame = tableViewFrame;
    [UIView setAnimationDuration:0.3f];
    [UIView commitAnimations];
}

- (void)disturbDatePickerHidden {
    self.disturbDatePicker.hidden = YES;
    [UIView beginAnimations:nil context:nil];
    CGRect tableViewFrame = self.tableView.frame;
    tableViewFrame.origin.y = 0;
    tableViewFrame.size.height = [UIScreen mainScreen].bounds.size.height;
    self.tableView.frame = tableViewFrame;
    [UIView setAnimationDuration:0.3f];
    [UIView commitAnimations];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 3;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 22.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1 && indexPath.row == 0) {
        return self.disturbCell.frame.size.height;
    } else {
        return 44.0f;
    }
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return self.remindHeader;
    } else {
        return self.disturbHeader;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return self.notificationCell;
        } else if (indexPath.row == 1) {
            return self.soundCell;
        } else {
            return self.vibrateCell;
        }
    } else {
        if (indexPath.row == 0) {
            return self.disturbCell;
        } else if (indexPath.row == 1) {
            return self.disturbStartCell;
        } else {
            return self.disturbEndCell;
        }
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        [self disturbDatePickerHidden];
    } else if (indexPath.section == 1) {
        if (indexPath.row == 0) {
            [self disturbDatePickerHidden];
        } else if (indexPath.row == 1) {
            if (self.DNDSwitch.on) {
                self.disturbDatePicker.tag = 10001;
                [self disturbDatePickerShow];
            }
        } else if (indexPath.row == 2) {
            if (self.DNDSwitch.on) {
                self.disturbDatePicker.tag = 10002;
                [self disturbDatePickerShow];
            }
        }
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    self.disturbDatePicker.hidden = YES;
}

@end
