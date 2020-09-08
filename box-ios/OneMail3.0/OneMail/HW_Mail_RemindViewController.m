//
//  HW_Mail_RemindViewController.m
//  OneMail
//
//  Created by Jason on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#define headerViewHeight 22
#define leftSideDistance 15
#define rightSideDistance 15
#define switchWidth 51
#define switchHeight 36
#define singleLineCellHeight 44
#define doubleLineCellHeight 68
#define titleLabelWidth 200
#define labelTopDistance 11
#define titileLabelHeight 22
#define labelToLabelDistance 4


#import "HW_Mail_RemindViewController.h"
#import "UserSetting.h"

@interface HW_Mail_RemindViewController ()<UITableViewDataSource,UITableViewDelegate>

@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, strong) UIView *mailHeaderView;
@property (nonatomic, strong) UITableViewCell *noticeCell;
@property (nonatomic, strong) UITableViewCell *voiceCell;
@property (nonatomic, strong) UITableViewCell *shakeCell;
@property (nonatomic, strong) UITableViewCell *noDisturbSetCell;
@property (nonatomic, strong) UITableViewCell *noDisturbStartTimeCell;
@property (nonatomic, strong) UITableViewCell *noDisturbEndTimeCell;

@property (nonatomic, strong) UIDatePicker *datePicker;
@property (nonatomic, strong) UILabel *startTimeLabel;
@property (nonatomic, strong) UILabel *endTimeLabel;
@property (nonatomic, strong) UISwitch *noDisturbSwitch;


@end

@implementation HW_Mail_RemindViewController


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.title = NSLocalizedString(@"SettingRemainTitle", nil);
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(gotoSettingVC)];
        self.tableView = [[UITableView alloc]initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.tableView.delegate = self;
        self.tableView.dataSource = self;
        self.tableView.frame = self.view.bounds;
        self.tableView.scrollEnabled = NO;
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        self.tableView.separatorColor = [UIColor colorWithRed:217/255.0f green:217/255.0f blue:217/255.0f alpha:1.0f];
        self.tableView.backgroundColor = [UIColor colorWithRed:240/255.0f green:240/255.0f blue:240/255.0f alpha:1.0f];
        [self.view addSubview: self.tableView];
        self.tableView.tableFooterView = self.datePicker;
    }
    return self;
}

- (void)gotoSettingVC {
    if (self.noDisturbSwitch.isOn) {
        if ([self.startTimeLabel.text compare: self.endTimeLabel.text] == NSOrderedDescending) {
            [[[UIAlertView alloc]initWithTitle:NSLocalizedString(@"SettingMailRemainTimePrompt", nil)
                                       message:nil
                                      delegate:nil
                             cancelButtonTitle:NSLocalizedString(@"Confirm", nil)
                             otherButtonTitles:nil]show];
            return;
        }
    }
    [self.navigationController popViewControllerAnimated:YES];
}


- (void)chooseDate:(UIDatePicker*)sender
{
    NSDate *selectedDate = sender.date;
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"HH:mm";
    NSString *dateString = [formatter stringFromDate:selectedDate];
    UserSetting *userSet = [UserSetting defaultSetting];
    if (sender.tag == 101) {
        _startTimeLabel.text = dateString;
        userSet.emailNoDisturbStartTime = dateString;
    }
    
    if (sender.tag == 102) {
        _endTimeLabel.text = dateString;
        userSet.emailNoDisturbEndTime = dateString;
        
    }
    
}

- (UIDatePicker *)datePicker
{
    if (!_datePicker) {
        _datePicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.tableView.frame), 0)];
        _datePicker.datePickerMode = UIDatePickerModeCountDownTimer;
        _datePicker.minuteInterval = 1;
        [_datePicker addTarget:self action:@selector(chooseDate:) forControlEvents:UIControlEventValueChanged];
        //[_datePicker setMinimumDate:[NSDate date]];
        _datePicker.hidden = YES;
        NSString *strLanguage = [[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] objectAtIndex:0];
        NSLocale *locale;
        if ([strLanguage isEqualToString:@"zh-Hans"]) {
            locale = [[NSLocale alloc]initWithLocaleIdentifier:@"zh-Hans"];
        } else {
            locale = [[NSLocale alloc]initWithLocaleIdentifier:@"en"];
        }
        _datePicker.locale = locale;
    }
    return _datePicker;
}



- (UIView *)mailHeaderView
{
    if (!_mailHeaderView) {
        UILabel *HeaderLabel = [[UILabel alloc]init];
        HeaderLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:12.0f];
        HeaderLabel.text = NSLocalizedString(@"SettingMailRemainHeader", nil);
        HeaderLabel.textAlignment = NSTextAlignmentLeft;
        HeaderLabel.textColor = [UIColor colorWithRed:102/255.0f green:102/255.0f blue:102/255.0f alpha:1.0f];
        HeaderLabel.backgroundColor = [UIColor clearColor];
        HeaderLabel.frame = CGRectMake(leftSideDistance, 0, titleLabelWidth, headerViewHeight);
        
        _mailHeaderView = [[UIView alloc]initWithFrame:CGRectZero];
        _mailHeaderView.backgroundColor = [UIColor colorWithRed:240/255.0f green:240/255.0f blue:240/255.0f alpha:1.0f];
        _mailHeaderView.frame = CGRectMake(0, 0, CGRectGetWidth(self.tableView.frame), headerViewHeight);
        [_mailHeaderView addSubview:HeaderLabel];
        
    }
    return _mailHeaderView;
}

- (UITableViewCell *)noticeCell
{
    if (!_noticeCell) {
        _noticeCell = [[UITableViewCell alloc]initWithFrame:CGRectZero];
        _noticeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _noticeCell.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [_noticeCell.imageView removeFromSuperview];
        [_noticeCell.detailTextLabel removeFromSuperview];
        [_noticeCell.textLabel removeFromSuperview];
        
        UILabel *cellHeaderLabel = [[UILabel alloc]initWithFrame:CGRectZero];
        cellHeaderLabel.textAlignment = NSTextAlignmentLeft;
        cellHeaderLabel.textColor = [UIColor blackColor];
        cellHeaderLabel.font = [UIFont systemFontOfSize:17];
        cellHeaderLabel.text = NSLocalizedString(@"SettingMailRemainNotification", nil);
        CGSize headerlabelSize = [self adjustSize:cellHeaderLabel];
        cellHeaderLabel.frame = CGRectMake(leftSideDistance, 0, ceil(headerlabelSize.width), singleLineCellHeight);
        [_noticeCell.contentView addSubview:cellHeaderLabel];
        
        UISwitch *switchBtn = [[UISwitch alloc]init];
        switchBtn.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-rightSideDistance-switchWidth, (singleLineCellHeight-switchHeight)/2+2, switchWidth, switchHeight);
        [switchBtn setOn:[[UserSetting defaultSetting].emailNoticeBar boolValue]];
        [switchBtn addTarget:self action:@selector(isOpenNoticeBar:) forControlEvents:UIControlEventValueChanged];
        [_noticeCell.contentView addSubview:switchBtn];
        
    }
    return _noticeCell;
    
}

- (UITableViewCell *)voiceCell
{
    if (!_voiceCell) {
        _voiceCell = [[UITableViewCell alloc]initWithFrame:CGRectZero];
        _voiceCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _voiceCell.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [_voiceCell.imageView removeFromSuperview];
        [_voiceCell.detailTextLabel removeFromSuperview];
        [_voiceCell.textLabel removeFromSuperview];
        
        UILabel *cellHeaderLabel = [[UILabel alloc]initWithFrame:CGRectZero];
        cellHeaderLabel.textAlignment = NSTextAlignmentLeft;
        cellHeaderLabel.textColor = [UIColor blackColor];
        cellHeaderLabel.font = [UIFont systemFontOfSize:17];
        cellHeaderLabel.text = NSLocalizedString(@"SettingMailRemainSound", nil);
        CGSize headerlabelSize = [self adjustSize:cellHeaderLabel];
        cellHeaderLabel.frame = CGRectMake(leftSideDistance, 0, ceil(headerlabelSize.width), singleLineCellHeight);
        [_voiceCell.contentView addSubview:cellHeaderLabel];
        
        UISwitch *switchBtn = [[UISwitch alloc]init];
        switchBtn.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-rightSideDistance-switchWidth, (singleLineCellHeight-switchHeight)/2+2, switchWidth, switchHeight);
        [switchBtn setOn:[[UserSetting defaultSetting].emailVoiceNotice boolValue]];
        [switchBtn addTarget:self action:@selector(isOpenVoiceNotice:) forControlEvents:UIControlEventValueChanged];
        [_voiceCell.contentView addSubview:switchBtn];
        
    }
    return _voiceCell;
    
}

- (UITableViewCell *)shakeCell
{
    if (!_shakeCell) {
        _shakeCell = [[UITableViewCell alloc]initWithFrame:CGRectZero];
        _shakeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _shakeCell.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [_shakeCell.imageView removeFromSuperview];
        [_shakeCell.detailTextLabel removeFromSuperview];
        [_shakeCell.textLabel removeFromSuperview];
        
        UILabel *cellHeaderLabel = [[UILabel alloc]initWithFrame:CGRectZero];
        cellHeaderLabel.textAlignment = NSTextAlignmentLeft;
        cellHeaderLabel.textColor = [UIColor blackColor];
        cellHeaderLabel.font = [UIFont systemFontOfSize:17];
        cellHeaderLabel.text = NSLocalizedString(@"SettingMailRemainShake", nil);
        CGSize headerlabelSize = [self adjustSize:cellHeaderLabel];
        cellHeaderLabel.frame = CGRectMake(leftSideDistance, 0, ceil(headerlabelSize.width), singleLineCellHeight);
        [_shakeCell.contentView addSubview:cellHeaderLabel];
        
        UISwitch *switchBtn = [[UISwitch alloc]init];
        switchBtn.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-rightSideDistance-switchWidth, (singleLineCellHeight-switchHeight)/2+2, switchWidth, switchHeight);
        [switchBtn setOn:[[UserSetting defaultSetting].emailShakeNotice boolValue]];
        [switchBtn addTarget:self action:@selector(isOpenShakeNotice:) forControlEvents:UIControlEventValueChanged];
        [_shakeCell.contentView addSubview:switchBtn];
        
    }
    return _shakeCell;
    
}

- (UITableViewCell *)noDisturbSetCell
{
    if (!_noDisturbSetCell) {
        _noDisturbSetCell = [[UITableViewCell alloc]initWithFrame:CGRectZero];
        _noDisturbSetCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _noDisturbSetCell.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [_noDisturbSetCell.imageView removeFromSuperview];
        [_noDisturbSetCell.detailTextLabel removeFromSuperview];
        [_noDisturbSetCell.textLabel removeFromSuperview];
        
        UILabel *titileLabel = [[UILabel alloc]init];
        titileLabel.font = [UIFont systemFontOfSize:17];
        titileLabel.textColor = [UIColor blackColor];
        titileLabel.textAlignment = NSTextAlignmentLeft;
        titileLabel.text = NSLocalizedString(@"SettingMailRemainDisturbe", nil);
        CGSize labelSize = [self adjustSize:titileLabel];
        titileLabel.frame = CGRectMake(leftSideDistance, labelTopDistance, labelSize.width, titileLabelHeight);
        [_noDisturbSetCell.contentView addSubview:titileLabel];
        
        _noDisturbSwitch = [[UISwitch alloc]init];
        _noDisturbSwitch.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-rightSideDistance-switchWidth, (singleLineCellHeight-switchHeight)/2+2, switchWidth, switchHeight);
        [_noDisturbSwitch setOn:[[UserSetting defaultSetting].emailNoMessageDisturb boolValue]];
        [_noDisturbSwitch addTarget:self action:@selector(isOpenNoMessageDisturb:) forControlEvents:UIControlEventValueChanged];
        [_noDisturbSetCell.contentView addSubview:_noDisturbSwitch];
        
        
        UILabel *textLabel = [[UILabel alloc]init];
        textLabel.font = [UIFont systemFontOfSize:14];
        textLabel.textColor = [UIColor colorWithRed:102/255.0f green:102/255.0f blue:102/255.0f alpha:1.0f];
        textLabel.textAlignment = NSTextAlignmentLeft;
        textLabel.text = NSLocalizedString(@"SettingMailRemainDisturbePrompt", nil);
        CGSize textLabelSize = [self adjustSize:textLabel];
        textLabel.frame = CGRectMake(leftSideDistance, CGRectGetMaxY(titileLabel.frame)+labelToLabelDistance, textLabelSize.width, titileLabelHeight);
        [_noDisturbSetCell.contentView addSubview:textLabel];
    }
    return _noDisturbSetCell;
}



- (UITableViewCell *)noDisturbStartTimeCell
{
    if (!_noDisturbStartTimeCell) {
        _noDisturbStartTimeCell = [[UITableViewCell alloc]initWithFrame:CGRectZero];
        _noDisturbStartTimeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _noDisturbStartTimeCell.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [_noDisturbStartTimeCell.imageView removeFromSuperview];
        [_noDisturbStartTimeCell.detailTextLabel removeFromSuperview];
        [_noDisturbStartTimeCell.textLabel removeFromSuperview];
        
        UILabel *cellHeaderLabel = [[UILabel alloc]initWithFrame:CGRectZero];
        cellHeaderLabel.textAlignment = NSTextAlignmentLeft;
        cellHeaderLabel.textColor = [UIColor blackColor];
        cellHeaderLabel.font = [UIFont systemFontOfSize:17];
        cellHeaderLabel.text = NSLocalizedString(@"SettingMailRemainDisturbeStart", nil);
        CGSize headerlabelSize = [self adjustSize:cellHeaderLabel];
        cellHeaderLabel.frame = CGRectMake(leftSideDistance, 0, ceil(headerlabelSize.width), singleLineCellHeight);
        [_noDisturbStartTimeCell.contentView addSubview:cellHeaderLabel];
        
        _startTimeLabel = [[UILabel alloc]init];
        _startTimeLabel.textAlignment = NSTextAlignmentRight;
        _startTimeLabel.font = [UIFont systemFontOfSize:14];
        _startTimeLabel.textColor = [UIColor colorWithRed:102/255.0f green:102/255.0f blue:102/255.0f alpha:1.0f];
        _startTimeLabel.text = [UserSetting defaultSetting].emailNoDisturbStartTime;
        //CGSize detailLabelSize = [self adjustSize:_startTimeLabel];
        _startTimeLabel.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-rightSideDistance-100, 0, 100, singleLineCellHeight);
        [_noDisturbStartTimeCell.contentView addSubview:_startTimeLabel];
        
    }
    return _noDisturbStartTimeCell;
}

- (UITableViewCell *)noDisturbEndTimeCell
{
    if (!_noDisturbEndTimeCell) {
        _noDisturbEndTimeCell = [[UITableViewCell alloc]initWithFrame:CGRectZero];
        _noDisturbEndTimeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _noDisturbEndTimeCell.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        [_noDisturbEndTimeCell.imageView removeFromSuperview];
        [_noDisturbEndTimeCell.detailTextLabel removeFromSuperview];
        [_noDisturbEndTimeCell.textLabel removeFromSuperview];
        
        UILabel *cellHeaderLabel = [[UILabel alloc]initWithFrame:CGRectZero];
        cellHeaderLabel.textAlignment = NSTextAlignmentLeft;
        cellHeaderLabel.textColor = [UIColor blackColor];
        cellHeaderLabel.font = [UIFont systemFontOfSize:17];
        cellHeaderLabel.text = NSLocalizedString(@"SettingMailRemainDisturbeEnd", nil);
        CGSize headerlabelSize = [self adjustSize:cellHeaderLabel];
        cellHeaderLabel.frame = CGRectMake(leftSideDistance, 0, ceil(headerlabelSize.width), singleLineCellHeight);
        [_noDisturbEndTimeCell.contentView addSubview:cellHeaderLabel];
        
        _endTimeLabel = [[UILabel alloc]init];
        _endTimeLabel.textAlignment = NSTextAlignmentRight;
        _endTimeLabel.font = [UIFont systemFontOfSize:14];
        _endTimeLabel.textColor = [UIColor colorWithRed:102/255.0f green:102/255.0f blue:102/255.0f alpha:1.0f];
        _endTimeLabel.text = [UserSetting defaultSetting].emailNoDisturbEndTime;
        //CGSize detailLabelSize = [self adjustSize:_endTimeLabel];
        _endTimeLabel.frame = CGRectMake([UIScreen mainScreen].bounds.size.width-rightSideDistance-100, 0, 100, singleLineCellHeight);
        [_noDisturbEndTimeCell.contentView addSubview:_endTimeLabel];
        
    }
    return _noDisturbEndTimeCell;
}

- (void)isOpenNoticeBar:(id)sender
{
    UISwitch *swithcBtn = (UISwitch *)sender;
    UserSetting *userSetting = [UserSetting defaultSetting];
    userSetting.emailNoticeBar = [NSNumber numberWithBool:swithcBtn.on];
}

- (void)isOpenVoiceNotice:(id)sender
{
    UISwitch *swithcBtn = (UISwitch *)sender;
    UserSetting *userSetting = [UserSetting defaultSetting];
    userSetting.emailVoiceNotice = [NSNumber numberWithBool:swithcBtn.on];
    
}

- (void)isOpenShakeNotice:(id)sender
{
    UISwitch *swithcBtn = (UISwitch *)sender;
    UserSetting *userSetting = [UserSetting defaultSetting];
    userSetting.emailShakeNotice = [NSNumber numberWithBool:swithcBtn.on];
    
}

- (void)isOpenNoMessageDisturb:(id)sender
{
    UISwitch *swithcBtn = (UISwitch *)sender;
    UserSetting *userSetting = [UserSetting defaultSetting];
    userSetting.emailNoMessageDisturb = [NSNumber numberWithBool:swithcBtn.on];
}


- (CGSize)adjustSize:(UILabel*)lable
{
    CGSize size = CGSizeMake(1000, 1000);
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    NSDictionary *attributes = @{NSFontAttributeName:lable.font,NSParagraphStyleAttributeName:paragraphStyle.copy};
    return [lable.text boundingRectWithSize:size options:NSStringDrawingUsesLineFragmentOrigin attributes:attributes context:nil].size;
}



- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

#pragma mark tableView dateSource+delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0) {
        return 3;
    } else if (section == 1) {
        return 3;
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 22;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        return 44;
    } else if (indexPath.section == 1) {
        if (indexPath.row == 0) {
            return 68;
        } else {
            return 44;
        }
    }
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return self.noticeCell;
        } else if (indexPath.row == 1){
            return self.voiceCell;
        } else if (indexPath.row == 2){
            return self.shakeCell;
        }
    } else if (indexPath.section == 1){
        if (indexPath.row == 0) {
            return self.noDisturbSetCell;
        } else if (indexPath.row == 1){
            return self.noDisturbStartTimeCell;
        } else if (indexPath.row == 2) {
            return self.noDisturbEndTimeCell;
        }
    }
    return nil;
    
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        return self.mailHeaderView;
    }
    return nil;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 1) {
        if (indexPath.row == 1) {
            _datePicker.tag = 101;
            if (_datePicker.hidden) {
                _datePicker.hidden = NO;
            } else {
                _datePicker.hidden = YES;
            }
            
        }
        if (indexPath.row == 2) {
            _datePicker.tag = 102;
            if (_datePicker.hidden) {
                _datePicker.hidden = NO;
            } else {
                _datePicker.hidden = YES;
            }
        }
    }
    
}
- (void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    // [tableView deselectRowAtIndexPath:indexPath animated:NO];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger sectionMax = [tableView numberOfSections];
    NSInteger rowNumOfSectionMax = [tableView numberOfRowsInSection:sectionMax-1];
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        if (indexPath.section == sectionMax-1 && indexPath.row == rowNumOfSectionMax-1) {
            [cell setSeparatorInset:UIEdgeInsetsZero];
        } else {
            [cell setSeparatorInset:UIEdgeInsetsMake(0, 15, 0, 15)];
        }
    }
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
