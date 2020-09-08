//
//  CloudFileShareInfoViewController.m
//  OneMail
//
//  Created by cse  on 15/11/24.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudFileShareInfoViewController.h"
#import "AppDelegate.h"
#import "File.h"
#import "User.h"
#import "File+Remote.h"
#import "FileThumbnail.h"
#import "CloudFileShareLinkInfo.h"
#import "CloudFileShareLinkSettingViewController.h"
#import "CloudFileShareUserSearchViewController.h"
#import "UIAlertView+Blocks.h"
//#import "shareSDKOperation.h"
#import <ShareSDK/ShareSDK.h>
#import <ShareSDKUI/ShareSDK+SSUI.h>
#import <ShareSDKUI/SSUIShareActionSheetStyle.h>
#import "MailForwardViewController.h"


#pragma mark CloudFileShareWithCell
@interface CloudFileShareWithCell ()

@property (nonatomic, strong) UIImageView *cloudFileShareUserIconView;
@property (nonatomic, strong) UILabel     *cloudFileShareUserNameLabel;
@property (nonatomic, strong) UILabel     *cloudFileShareUserDetailLabel;
@property (nonatomic, strong) UIButton    *cloudFileShareUserDeleteButton;

@end

@implementation CloudFileShareWithCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UIImageView *cloudFileShareUserBackgroundImageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 5.5, 44, 44)];
//
        cloudFileShareUserBackgroundImageView.image = [UIImage imageNamed:@"img_user_frame"];
        [self.contentView addSubview:cloudFileShareUserBackgroundImageView];
        
        self.cloudFileShareUserIconView = [[UIImageView alloc] initWithFrame:CGRectMake(2, 2, 40, 40)];
        [cloudFileShareUserBackgroundImageView addSubview:self.cloudFileShareUserIconView];
        
        self.cloudFileShareUserNameLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.cloudFileShareUserNameLabel];
        
        self.cloudFileShareUserDetailLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.cloudFileShareUserDetailLabel];
        //imageNamed:@"ic_transfer_delete_nor"]
        self.cloudFileShareUserDeleteButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.cloudFileShareUserDeleteButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.cloudFileShareUserDeleteButton setImage:[UIImage imageNamed:@"ic_transfer_delete_nor"]  forState:UIControlStateNormal];
        [self.cloudFileShareUserDeleteButton setImage:[UIImage imageNamed:@"ic_transfer_delete_press"] forState:UIControlStateHighlighted];
        [self.cloudFileShareUserDeleteButton addTarget:self action:@selector(deletShareUser) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.cloudFileShareUserDeleteButton];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.cloudFileShareUserIconView.image = [UIImage imageNamed:@"img_user_default"];
    
    self.cloudFileShareUserNameLabel.text = self.user.userName;
    self.cloudFileShareUserNameLabel.frame = CGRectMake(15+44+10, 8, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 20);
    
    self.cloudFileShareUserDetailLabel.text = self.user.userDescription;
    self.cloudFileShareUserDetailLabel.frame = CGRectMake(15+44+10, CGRectGetMaxY(self.cloudFileShareUserNameLabel.frame)+4, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 15);
    
    self.cloudFileShareUserDeleteButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-42,(CGRectGetHeight(self.frame)-42)/2, 42, 42);
}

-(void)setUser:(User *)user {
    if (_user != user) {
        _user = user;
    }
}

- (void)prepareForReuse {
    [super prepareForReuse];
    self.cloudFileShareUserIconView.image = nil;
    self.cloudFileShareUserNameLabel.text = nil;
    self.cloudFileShareUserDetailLabel.text = nil;
}

- (void)deletShareUser {
    if ([self.delegate respondsToSelector:@selector(deleteShareUser:)]) {
        [self.delegate deleteShareUser:self.user];
    }
}

@end


#pragma mark CloudFileShareLinkCell
@interface CloudFileShareLinkCell () <UIActionSheetDelegate>

@property (nonatomic, strong) UIView   *cloudFileShareLinkView;
@property (nonatomic, strong) UILabel  *cloudFileShareLinkAddressLabel;
@property (nonatomic, strong) UILabel  *cloudFileShareLinkAccessTitleLabel;
@property (nonatomic, strong) UILabel  *cloudFileShareLinkAccessLabel;
@property (nonatomic, strong) UILabel  *cloudFileShareLinkCodeTitleLabel;
@property (nonatomic, strong) UILabel  *cloudFileShareLinkCodeLabel;
@property (nonatomic, strong) UILabel  *cloudFileShareLinkPeriodTitleLabel;
@property (nonatomic, strong) UILabel  *cloudFileShareLinkPeriodLabel;

@property (nonatomic, strong) UIButton *cloudFileShareLinkSendButton;
@property (nonatomic, strong) UIButton *cloudFileShareLinkEditButton;
@property (nonatomic, strong) UIButton *cloudFileShareLinkDeleteButton;

@end

@implementation CloudFileShareLinkCell
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.cloudFileShareLinkView = [[UIView alloc] initWithFrame:CGRectZero];
        self.cloudFileShareLinkView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [self.contentView addSubview:self.cloudFileShareLinkView];
        
        self.cloudFileShareLinkAddressLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkAddressLabel];
        
        self.cloudFileShareLinkAccessTitleLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkAccessTitleLabel];
        
        self.cloudFileShareLinkAccessLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkAccessLabel];
        
        self.cloudFileShareLinkCodeTitleLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkCodeTitleLabel];
        
        self.cloudFileShareLinkCodeLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkCodeLabel];
        
        self.cloudFileShareLinkPeriodTitleLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkPeriodTitleLabel];
        
        self.cloudFileShareLinkPeriodLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkPeriodLabel];
        
        self.cloudFileShareLinkSendButton = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.cloudFileShareLinkSendButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.cloudFileShareLinkSendButton setImage:[UIImage imageNamed:@"ic_share_link_share_nor"] forState:UIControlStateNormal];
        [self.cloudFileShareLinkSendButton setImage:[UIImage imageNamed:@"ic_share_link_share_press"] forState:UIControlStateSelected];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkSendButton];
        [self.cloudFileShareLinkSendButton addTarget:self action:@selector(sendShareLink) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudFileShareLinkEditButton = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.cloudFileShareLinkEditButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.cloudFileShareLinkEditButton setImage:[UIImage imageNamed:@"ic_share_link_edit_nor"] forState:UIControlStateNormal];
        [self.cloudFileShareLinkEditButton setImage:[UIImage imageNamed:@"ic_share_link_edit_press"] forState:UIControlStateSelected];
        [self.cloudFileShareLinkEditButton addTarget:self action:@selector(editShareLink) forControlEvents:UIControlEventTouchUpInside];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkEditButton];
        
        self.cloudFileShareLinkDeleteButton = [[UIButton alloc]initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.cloudFileShareLinkDeleteButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.cloudFileShareLinkDeleteButton setImage:[UIImage imageNamed:@"ic_share_link_delete_nor"] forState:UIControlStateNormal];
        [self.cloudFileShareLinkDeleteButton setImage:[UIImage imageNamed:@"ic_share_link_delete_press"] forState:UIControlStateNormal];
        [self.cloudFileShareLinkDeleteButton addTarget:self action:@selector(deletShareLink) forControlEvents:UIControlEventTouchUpInside];
        [self.cloudFileShareLinkView addSubview:self.cloudFileShareLinkDeleteButton];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    if (self.cloudFileShareLinkInfo.shareLinkExtracCode) {
        self.cloudFileShareLinkView.frame = CGRectMake(0, 22, CGRectGetWidth(self.frame), 110+19);
    } else {
        self.cloudFileShareLinkView.frame = CGRectMake(0, 22, CGRectGetWidth(self.frame), 110);
    }
    self.cloudFileShareLinkAddressLabel.text = self.cloudFileShareLinkInfo.shareLinkUrl;
    self.cloudFileShareLinkAddressLabel.frame = CGRectMake(15, 10, CGRectGetWidth(self.frame)-15-15, 20);
    
    self.cloudFileShareLinkAccessTitleLabel.text = getLocalizedString(@"CloudShareLinkAccess", nil);
    CGSize accessTitleLabelSize = [CommonFunction labelSizeWithLabel:self.cloudFileShareLinkAccessTitleLabel limitSize:CGSizeMake(1000, 1000)];
    self.cloudFileShareLinkAccessTitleLabel.frame = CGRectMake(15, CGRectGetMaxY(self.cloudFileShareLinkAddressLabel.frame)+8, accessTitleLabelSize.width, 15);
    
    self.cloudFileShareLinkAccessLabel.text = [self roleString:self.cloudFileShareLinkInfo.shareLinkRole];
    CGSize accessLabelSize = [CommonFunction labelSizeWithLabel:self.cloudFileShareLinkAccessLabel limitSize:CGSizeMake(1000, 1000)];
    self.cloudFileShareLinkAccessLabel.frame = CGRectMake(CGRectGetMaxX(self.cloudFileShareLinkAccessTitleLabel.frame), CGRectGetMaxY(self.cloudFileShareLinkAddressLabel.frame)+8, accessLabelSize.width, 15);
    
    CGFloat cloudFileShareLinkPeriodTitleLabelOriginY = CGRectGetMaxY(self.cloudFileShareLinkAccessTitleLabel.frame)+4;
    if (self.cloudFileShareLinkInfo.shareLinkExtracCode) {
        self.cloudFileShareLinkCodeTitleLabel.text = getLocalizedString(@"CloudShareLinkExtraction", nil);
        CGSize extractionTitleLabelSize = [CommonFunction labelSizeWithLabel:self.cloudFileShareLinkCodeTitleLabel limitSize:CGSizeMake(1000, 1000)];
        self.cloudFileShareLinkCodeTitleLabel.frame = CGRectMake(CGRectGetMinX(self.cloudFileShareLinkAccessTitleLabel.frame), CGRectGetMaxY(self.cloudFileShareLinkAccessTitleLabel.frame)+4, extractionTitleLabelSize.width, 15);
        
        self.cloudFileShareLinkCodeLabel.text = self.cloudFileShareLinkInfo.shareLinkExtracCode;
        CGSize extractionLabelSize = [CommonFunction labelSizeWithLabel:self.cloudFileShareLinkCodeLabel limitSize:CGSizeMake(1000, 1000)];
        self.cloudFileShareLinkCodeLabel.frame = CGRectMake(CGRectGetMaxX(self.cloudFileShareLinkCodeTitleLabel.frame), CGRectGetMinY(self.cloudFileShareLinkCodeTitleLabel.frame), extractionLabelSize.width, 15);
        cloudFileShareLinkPeriodTitleLabelOriginY= CGRectGetMaxY(self.cloudFileShareLinkCodeTitleLabel.frame)+4;
    } else {
        self.cloudFileShareLinkCodeLabel.frame  = CGRectZero;
        self.cloudFileShareLinkCodeTitleLabel.frame = CGRectZero;
    }
    
    self.cloudFileShareLinkPeriodTitleLabel.text = getLocalizedString(@"CloudShareLinkPeriod", nil);
    CGSize periodValidityTitleLabelSize = [CommonFunction labelSizeWithLabel:self.cloudFileShareLinkPeriodTitleLabel limitSize:CGSizeMake(1000, 1000)];
    self.cloudFileShareLinkPeriodTitleLabel.frame = CGRectMake(15, cloudFileShareLinkPeriodTitleLabelOriginY, periodValidityTitleLabelSize.width, 15);
    
    self.cloudFileShareLinkPeriodLabel.text = [self periodValidityTime];
    CGSize periodValidityLabelSize = [CommonFunction labelSizeWithLabel:self.cloudFileShareLinkPeriodLabel limitSize:CGSizeMake(1000, 1000)];
    self.cloudFileShareLinkPeriodLabel.frame = CGRectMake(CGRectGetMaxX(self.cloudFileShareLinkPeriodTitleLabel.frame), CGRectGetMinY(self.cloudFileShareLinkPeriodTitleLabel.frame), periodValidityLabelSize.width, 15);
    
    self.cloudFileShareLinkDeleteButton.frame = CGRectMake(CGRectGetWidth(self.cloudFileShareLinkView.frame)-5-42, CGRectGetHeight(self.cloudFileShareLinkView.frame)-42, 42, 42);
    self.cloudFileShareLinkEditButton.frame = CGRectMake(CGRectGetMinX(self.cloudFileShareLinkDeleteButton.frame)-10-42, CGRectGetHeight(self.cloudFileShareLinkView.frame)-42, 42, 42);
    self.cloudFileShareLinkSendButton.frame = CGRectMake(CGRectGetMinX(self.cloudFileShareLinkEditButton.frame)-10-42, CGRectGetHeight(self.cloudFileShareLinkView.frame)-42, 42, 42);
}


- (NSString*)roleString:(NSString*)role {
    if (!role) {
        return nil;
    }
    if ([role isEqualToString:@"uploader"]) {
        return getLocalizedString(@"CloudShareLinkUploader", nil);
    }
    if ([role isEqualToString:@"previewer"]) {
        return getLocalizedString(@"CloudShareLinkPreviewer", nil);
    }
    if ([role isEqualToString:@"viewer"]) {
        return getLocalizedString(@"CloudShareLinkViewer", nil);
    }
    if ([role isEqualToString:@"uploadAndView"]) {
        return getLocalizedString(@"CloudShareLinkUploadAndView", nil);
    }
    return nil;
}

- (NSString*)periodValidityTime {
    NSDateFormatter *dateForm = [[NSDateFormatter alloc] init];
    [dateForm setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *dateFromStr = nil;
    NSString *dateToStr = nil;
    NSString *datePeriodValid = nil;
    if (self.cloudFileShareLinkInfo.shareLinkEffectiveAt) {
        dateFromStr = [dateForm stringFromDate:[NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkEffectiveAt longLongValue]/1000]];
    }
    if (self.cloudFileShareLinkInfo.shareLinkExpireAt) {
        dateToStr = [dateForm stringFromDate:[NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkExpireAt longLongValue]/1000]];
    }
    if (dateFromStr && dateToStr) {
        datePeriodValid = [NSString stringWithFormat:@"%@-%@",dateFromStr,dateToStr];
    }
    if (!dateFromStr && !dateToStr) {
        datePeriodValid = getLocalizedString(@"CloudShareLinkPeriodForever", nil);
    }
    if (dateFromStr && !dateToStr) {
        datePeriodValid = [NSString stringWithFormat:@"%@-%@",dateFromStr,getLocalizedString(@"CloudShareLinkPeriodForever", nil)];
    }
    return datePeriodValid;
}

- (NSString*)periodValidityDays {
    NSDateFormatter *dateForm = [[NSDateFormatter alloc] init];
    [dateForm setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *dateFromStr = [dateForm stringFromDate:[NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkEffectiveAt longLongValue]/1000]];
    NSString *dateToStr = nil;
    if (self.cloudFileShareLinkInfo.shareLinkExpireAt) {
        dateToStr = [dateForm stringFromDate:[NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkExpireAt longLongValue]/1000]];
    } else {
        return getLocalizedString(@"CloudShareLinkPeriodForever", nil);
    }
    NSDate *fromDate;
    NSDate *toDate;
    NSCalendar *gregorian = [[NSCalendar alloc]initWithCalendarIdentifier:NSGregorianCalendar];
    [gregorian rangeOfUnit:NSDayCalendarUnit startDate:&fromDate interval:NULL forDate:[dateForm dateFromString:dateFromStr]];
    [gregorian rangeOfUnit:NSDayCalendarUnit startDate:&toDate interval:NULL forDate:[dateForm dateFromString:dateToStr]];
    NSDateComponents *dayComponents = [gregorian components:NSDayCalendarUnit fromDate:fromDate toDate:toDate options:0];
    if (dayComponents.day >1) {
        return [NSString stringWithFormat:@"%ld days",(long)dayComponents.day];
    } else {
        return [NSString stringWithFormat:@"%ld day",(long)dayComponents.day];
    }
    
}

- (void)setCloudFileShareLinkInfo:(CloudFileShareLinkInfo *)cloudFileShareLinkInfo {
    if (_cloudFileShareLinkInfo != cloudFileShareLinkInfo) {
        _cloudFileShareLinkInfo = cloudFileShareLinkInfo;
    }
}

- (void)prepareForReuse {
    [super prepareForReuse];
    self.cloudFileShareLinkAddressLabel.text = nil;
    self.cloudFileShareLinkAccessLabel.text = nil;
    self.cloudFileShareLinkCodeLabel.text = nil;
    self.cloudFileShareLinkPeriodLabel.text = nil;
}

- (void)deletShareLink {
    if ([self.delegate respondsToSelector:@selector(deleteShareLink:)]) {
        [self.delegate deleteShareLink:self.cloudFileShareLinkInfo];
    }
}

- (void)editShareLink {
    if ([self.delegate respondsToSelector:@selector(editShareLink:)]) {
        [self.delegate editShareLink:self.cloudFileShareLinkInfo];
    }
}
- (void)sendShareLink{
    UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"CloudShareLinkSendToOtherApp", nil), nil];
    [sheet showInView:self.contentView];
}
- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex{
//    if (buttonIndex == 0) {
//        MailForwardViewController *forwardViewController = [[MailForwardViewController alloc] initWithShareLink:[NSString stringWithFormat:@"%@,%@",self.file.fileName,self.cloudFileShareLinkInfo.shareLinkUrl]];
//        [self.delegate pushViewController:forwardViewController];
//    }
//    if (buttonIndex == 1) {
    NSArray *imageArray = @[[UIImage imageNamed:@"120x120_whitebg"]];//
    if (imageArray) {
        NSMutableDictionary *shareParams = [NSMutableDictionary dictionary];
        
        [shareParams SSDKSetupShareParamsByText:@"分享链接"
                                         images:imageArray
                                            url:[NSURL URLWithString:self.cloudFileShareLinkInfo.shareLinkUrl]
                                          title:self.file.fileName
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

//    }
}

@end


#pragma CloudFileShareInfoViewController
@interface CloudFileShareInfoViewController ()<UITableViewDataSource,UITableViewDelegate,UIScrollViewDelegate,CloudFileShareWithDelegate,CloudFileShareLinkDelegate>

@property (nonatomic, strong) File *file;

@property (nonatomic, strong) UILabel      *cloudFileShareTitleLabel;
@property (nonatomic, strong) UIButton     *cloudFileShareBackButton;
@property (nonatomic, strong) UIButton     *cloudFileShareUserAddButton;
@property (nonatomic, strong) UIButton     *cloudFileShareLinkAddButton;

@property (nonatomic, strong) UIView       *cloudFileShareSegmentView;
@property (nonatomic, strong) UIButton     *cloudFileShareWithButton;
@property (nonatomic, strong) UIButton     *cloudFileShareLinkButton;
@property (nonatomic, strong) UIView       *cloudFileShareHighLightView;

@property (nonatomic, strong) UIView       *cloudFileInfoView;

@property (nonatomic, strong) UIScrollView *cloudFileShareScrollView;

@property (nonatomic, strong) UILabel      *cloudFileShareWithPromptLabel;
@property (nonatomic, strong) UITableView  *cloudFileShareWithTableView;
@property (nonatomic, strong) UIButton     *cloudFileShareWithDeleteButton;

@property (nonatomic, strong) UITableView  *cloudFileShareLinkTableView;
@property (nonatomic, strong) UILabel      *cloudFileShareLinkCreatePromptLabel;
@property (nonatomic, strong) UILabel      *cloudFileShareLinkPermissionPromptLabel;

@property (nonatomic, strong) NSMutableArray *cloudFileShareWithUsers;
@property (nonatomic, strong) NSMutableArray *cloudFileShareLinkInfos;

@end

@implementation CloudFileShareInfoViewController

- (id)initWithFile:(File*)file {
    self = [super init];
    if (self) {
        self.file = file;
        self.cloudFileShareWithUsers = [[NSMutableArray alloc] init];
        self.cloudFileShareLinkInfos = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.cloudFileShareTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.cloudFileShareTitleLabel.font = [UIFont boldSystemFontOfSize:18.0f];
    self.cloudFileShareTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.cloudFileShareTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.cloudFileShareTitleLabel.text = getLocalizedString(@"CloudShareTitle", nil);
    
    self.cloudFileShareBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.cloudFileShareBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudFileShareBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.cloudFileShareBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.cloudFileShareBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    
    self.cloudFileShareUserAddButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
    self.cloudFileShareUserAddButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudFileShareUserAddButton setImage:[UIImage imageNamed:@"ic_nav_share_with_sb_nor"] forState:UIControlStateNormal];
    [self.cloudFileShareUserAddButton setImage:[UIImage imageNamed:@"ic_nav_share_with_sb_press"] forState:UIControlStateHighlighted];
    [self.cloudFileShareUserAddButton addTarget:self action:@selector(addShareUser) forControlEvents:UIControlEventTouchUpInside];
    
    self.cloudFileShareLinkAddButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
    self.cloudFileShareLinkAddButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudFileShareLinkAddButton setImage:[UIImage imageNamed:@"ic_nav_share_link_create_nor"] forState:UIControlStateNormal];
    [self.cloudFileShareLinkAddButton setImage:[UIImage imageNamed:@"ic_nav_share_link_create_press"] forState:UIControlStateHighlighted];
    [self.cloudFileShareLinkAddButton addTarget:self action:@selector(addShareLink) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:self.cloudFileShareSegmentView];
    [self.view addSubview:self.cloudFileInfoView];
    [self.view addSubview:self.cloudFileShareScrollView];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.cloudFileShareTitleLabel];
    [self.navigationController.navigationBar addSubview:self.cloudFileShareBackButton];
    [self.navigationController.navigationBar addSubview:self.cloudFileShareLinkAddButton];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if ([self.file.fileOwner isEqualToString:appDelegate.localManager.userCloudId]) {
        [self.navigationController.navigationBar addSubview:self.cloudFileShareUserAddButton];
    }
    
    if (self.cloudFileShareWithButton.selected) {
        self.cloudFileShareUserAddButton.hidden = NO;
        self.cloudFileShareLinkAddButton.hidden = YES;
    }
    if (self.cloudFileShareLinkButton.selected) {
        self.cloudFileShareUserAddButton.hidden = YES;
        self.cloudFileShareLinkAddButton.hidden = NO;
    }
    
    [self loadShareWith];
    [self loadShareLink];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.cloudFileShareTitleLabel removeFromSuperview];
    [self.cloudFileShareBackButton removeFromSuperview];
    [self.cloudFileShareUserAddButton removeFromSuperview];
    [self.cloudFileShareLinkAddButton removeFromSuperview];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark segmentView 
- (UIView*)cloudFileShareSegmentView {
    if (!_cloudFileShareSegmentView) {
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
        
        _cloudFileShareSegmentView = [[UIView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationFrame.size.height, CGRectGetWidth(self.view.frame), 44)];
        _cloudFileShareSegmentView.layer.borderWidth = 0.5;
        _cloudFileShareSegmentView.layer.borderColor = [CommonFunction colorWithString:@"cccccc" alpha:1.0f].CGColor;
        
        self.cloudFileShareWithButton = [[UIButton alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)/2-15-10, 22)];
        self.cloudFileShareWithButton.selected = YES;
        self.cloudFileShareWithButton.tag = 10001;
        self.cloudFileShareWithButton.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        [self.cloudFileShareWithButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareWithButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be0" alpha:1.0f],NSFontAttributeName:[UIFont boldSystemFontOfSize:17.0f]}] forState:UIControlStateNormal];
        [self.cloudFileShareWithButton addTarget:self action:@selector(showShareWith) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareSegmentView addSubview:self.cloudFileShareWithButton];
        
        self.cloudFileShareLinkButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)/2+10, 11, CGRectGetWidth(self.view.frame)/2-10-15, 22)];
        self.cloudFileShareLinkButton.selected = NO;
        self.cloudFileShareLinkButton.tag = 10002;
        self.cloudFileShareLinkButton.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        [self.cloudFileShareLinkButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareLinkButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"333333" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
        [self.cloudFileShareLinkButton addTarget:self action:@selector(showShareLink) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareSegmentView addSubview:self.cloudFileShareLinkButton];
        
        UIView *cloudFileShareSegmentMidLine = [[UIView alloc] initWithFrame:CGRectMake((CGRectGetWidth(_cloudFileShareSegmentView.frame)-1)/2, (CGRectGetHeight(_cloudFileShareSegmentView.frame)-24)/2, 1, 24)];
        cloudFileShareSegmentMidLine.backgroundColor = [CommonFunction colorWithString:@"d0d0d0" alpha:1.0f];
        [_cloudFileShareSegmentView addSubview:cloudFileShareSegmentMidLine];
        
        self.cloudFileShareHighLightView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(_cloudFileShareSegmentView.frame)-3, CGRectGetWidth(_cloudFileShareSegmentView.frame)/2, 3)];
        self.cloudFileShareHighLightView.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
        [_cloudFileShareSegmentView addSubview:self.cloudFileShareHighLightView];
    }
    return _cloudFileShareSegmentView;
}

#pragma mark fileInfo
- (UIView*)cloudFileInfoView {
    if (!_cloudFileInfoView) {
        _cloudFileInfoView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.cloudFileShareSegmentView.frame), CGRectGetWidth(self.view.frame), 68)];
        _cloudFileInfoView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileInfoView.layer.borderWidth = 0.5;
        _cloudFileInfoView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        
        UIImageView *cloudFileImageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, (68-56)/2, 56, 56)];
        [FileThumbnail imageWithFile:self.file imageView:cloudFileImageView];
        [_cloudFileInfoView addSubview:cloudFileImageView];
        
        UILabel *cloudFileTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(15+CGRectGetWidth(cloudFileImageView.frame)+10, 11, CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(cloudFileImageView.frame)-10-15, 22)];
        cloudFileTitleLabel.font = [UIFont systemFontOfSize:17.0f];
        cloudFileTitleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        cloudFileTitleLabel.textAlignment = NSTextAlignmentLeft;
        cloudFileTitleLabel.text = self.file.fileName;
        [_cloudFileInfoView addSubview:cloudFileTitleLabel];
        
        UILabel *cloudFileTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(15+CGRectGetWidth(cloudFileImageView.frame)+10, 11+CGRectGetHeight(cloudFileTitleLabel.frame)+4, CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(cloudFileImageView.frame)-10-15, 20)];
        cloudFileTimeLabel.font = [UIFont systemFontOfSize:15.0f];
        cloudFileTimeLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        cloudFileTimeLabel.textAlignment = NSTextAlignmentLeft;
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        NSTimeZone * timezone = [NSTimeZone timeZoneForSecondsFromGMT:0];//直接指定时区
//        NSTimeZone * timezone1 = [NSTimeZone timeZoneWithAbbreviation:@"UTC"];//直接指定时区
        
        [dateFormatter setTimeZone:timezone];//这里指定不指定时区并没有什么用
        [dateFormatter setDateFormat:@"yyyy-M-dd H:mm"];
        NSString *modifiedDateStr = [dateFormatter stringFromDate:self.file.fileModifiedDate];
        cloudFileTimeLabel.text = modifiedDateStr;
        [_cloudFileInfoView addSubview:cloudFileTimeLabel];
    }
    return _cloudFileInfoView;
}

#pragma mark scrollView
- (UIScrollView*)cloudFileShareScrollView {
    if (!_cloudFileShareScrollView) {
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
        _cloudFileShareScrollView = [[UIScrollView alloc]initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationFrame.size.height+CGRectGetHeight(self.cloudFileShareSegmentView.frame)+CGRectGetHeight(self.cloudFileInfoView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationFrame.size.height-CGRectGetHeight(self.cloudFileShareSegmentView.frame)-CGRectGetHeight(self.cloudFileInfoView.frame))];
        _cloudFileShareScrollView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        _cloudFileShareScrollView.delegate = self;
        _cloudFileShareScrollView.bounces = NO;
        _cloudFileShareScrollView.contentSize = CGSizeMake(CGRectGetWidth(self.view.frame)*2, 0);
        _cloudFileShareScrollView.showsHorizontalScrollIndicator = NO;
        _cloudFileShareScrollView.pagingEnabled = YES;
        [self.view addSubview:_cloudFileShareScrollView];
        
        UIView *cloudFileShareWithHeaderView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 22)];
        cloudFileShareWithHeaderView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        self.cloudFileShareWithPromptLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 0, CGRectGetWidth(self.view.frame)-15-15, 22)];
        self.cloudFileShareWithPromptLabel.font = [UIFont systemFontOfSize:12.0f];
        self.cloudFileShareWithPromptLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.cloudFileShareWithPromptLabel.textAlignment = NSTextAlignmentLeft;
        NSString *text;
        UserSetting *userSetting = [UserSetting defaultSetting];
        if (self.file.fileOwner.integerValue != userSetting.cloudUserCloudId.integerValue) {
            text = [NSString stringWithFormat:@"团队空间文件无法进行共享"];
        }
        else{
            text = [NSString stringWithFormat:getLocalizedString(@"CloudShareWithSpecified", nil),(unsigned long)self.cloudFileShareWithUsers.count];
        }
        self.cloudFileShareWithPromptLabel.text = text;
        [_cloudFileShareScrollView addSubview:self.cloudFileShareWithPromptLabel];
        self.cloudFileShareWithTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.cloudFileShareWithTableView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [self.cloudFileShareWithTableView registerClass:[CloudFileShareWithCell class] forCellReuseIdentifier:@"CloudFileShareWithCell"];
        self.cloudFileShareWithTableView.dataSource = self;
        self.cloudFileShareWithTableView.delegate = self;
        self.cloudFileShareWithTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
        [_cloudFileShareScrollView addSubview:self.cloudFileShareWithTableView];
        
        self.cloudFileShareLinkTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.cloudFileShareLinkTableView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [self.cloudFileShareLinkTableView registerClass:[CloudFileShareLinkCell class] forCellReuseIdentifier:@"CloudFileShareLinkCell"];
        self.cloudFileShareLinkTableView.dataSource = self;
        self.cloudFileShareLinkTableView.delegate = self;
        self.cloudFileShareLinkTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        [_cloudFileShareScrollView addSubview:self.cloudFileShareLinkTableView];
        [_cloudFileShareScrollView addSubview:self.cloudFileShareLinkCreatePromptLabel];
        [_cloudFileShareScrollView addSubview:self.cloudFileShareLinkPermissionPromptLabel];
    }
    return _cloudFileShareScrollView;
}

#pragma mark ShareWith
- (void)loadShareWith {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (![self.file.fileOwner isEqualToString:appDelegate.localManager.userCloudId]) {
        [self refreshShareWithTableView];
        return;
    }
    [self.file fileShareUser:^(id retobj) {
        [self.cloudFileShareWithUsers removeAllObjects];
        [self.cloudFileShareWithUsers addObjectsFromArray:retobj];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self refreshShareWithTableView];
            [self.cloudFileShareWithTableView reloadData];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self refreshShareWithTableView];
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudShareWithLoadFailedPrompt", nil)];
        });
    }];
}

- (void)showShareWith {
    if (self.cloudFileShareWithButton.selected) {
        return;
    }
    self.cloudFileShareWithButton.selected = YES;
    self.cloudFileShareLinkButton.selected = NO;
    
    self.cloudFileShareUserAddButton.hidden = NO;
    self.cloudFileShareLinkAddButton.hidden = YES;
    
    self.cloudFileShareHighLightView.frame = CGRectMake(0, CGRectGetHeight(self.cloudFileShareSegmentView.frame)-3, CGRectGetWidth(self.cloudFileShareSegmentView.frame)/2, 3);
    
    [self.cloudFileShareWithButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareWithButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be0" alpha:1.0f],NSFontAttributeName:[UIFont boldSystemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.cloudFileShareLinkButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareLinkButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"333333" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    
    [self.cloudFileShareScrollView setContentOffset:CGPointMake(0, 0) animated:YES];
}

- (void)refreshShareWithTableView {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    CGFloat maxShareWithTableHeight = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationFrame.size.height-CGRectGetHeight(self.cloudFileShareSegmentView.frame)-CGRectGetHeight(self.cloudFileInfoView.frame)-22-CGRectGetHeight(self.cloudFileShareWithDeleteButton.frame);
    CGFloat shareWithTableHeight = self.cloudFileShareWithUsers.count*55;
    self.cloudFileShareWithTableView.frame = CGRectMake(0, 22, CGRectGetWidth(self.view.frame), MIN(shareWithTableHeight, maxShareWithTableHeight));
    self.cloudFileShareWithDeleteButton.frame = CGRectMake(0, CGRectGetMaxY(self.cloudFileShareWithTableView.frame), CGRectGetWidth(self.cloudFileShareWithDeleteButton.frame), CGRectGetHeight(self.cloudFileShareWithDeleteButton.frame));
    NSString *text;
    if ([self.file.fileOwner isEqualToString:appDelegate.localManager.userCloudId]) {
        text = [NSString stringWithFormat:getLocalizedString(@"CloudShareWithSpecified", nil),(unsigned long)self.cloudFileShareWithUsers.count];
        self.cloudFileShareWithPromptLabel.textAlignment = NSTextAlignmentLeft;
    } else {
        text = [NSString stringWithFormat:getLocalizedString(@"CloudShareWithSpecifiedPrompt", nil)];
        self.cloudFileShareWithPromptLabel.textAlignment = NSTextAlignmentCenter;
    }
    self.cloudFileShareWithPromptLabel.text = text;
    if (self.cloudFileShareWithUsers.count == 0) {
        self.cloudFileShareWithDeleteButton.hidden = YES;
    } else {
        self.cloudFileShareWithDeleteButton.hidden = NO;
    }
}

- (UIButton*)cloudFileShareWithDeleteButton {
    if (!_cloudFileShareWithDeleteButton) {
        _cloudFileShareWithDeleteButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44)];
        _cloudFileShareWithDeleteButton.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareWithDeleteButton.layer.borderWidth = 0.5;
        _cloudFileShareWithDeleteButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [_cloudFileShareWithDeleteButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareCancel", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be8" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:15.0f]}] forState:UIControlStateNormal];
        [_cloudFileShareWithDeleteButton addTarget:self action:@selector(deleteAllShareUser) forControlEvents:UIControlEventTouchUpInside];
        [self.cloudFileShareScrollView addSubview:_cloudFileShareWithDeleteButton];
    }
    return _cloudFileShareWithDeleteButton;
}

- (void) deleteAllShareUser {
    [self.file fileShareCancel:^(id retobj) {
        [self.cloudFileShareWithUsers removeAllObjects];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self refreshShareWithTableView];
            [self.cloudFileShareWithTableView reloadData];
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationSuccess", nil)];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
        });
    }];
}

- (void)deleteShareUser:(User *)user {
    [self.file fileShareCancel:user succeed:^(id retobj) {
        [self.cloudFileShareWithUsers removeObject:user.userCloudId];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self refreshShareWithTableView];
            [self.cloudFileShareWithTableView reloadData];
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CancelShare", nil)];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
        });
    }];
}

- (void)addShareUser {
    CloudFileShareUserSearchViewController *searchViewController = [[CloudFileShareUserSearchViewController alloc] initWithShareUsers:self.cloudFileShareWithUsers file:self.file];
    [self.navigationController pushViewController:searchViewController animated:YES];
}

#pragma mark ShareLink
- (void)loadShareLink {
    [self.file fileLinkList:^(id retobj) {
        [self.cloudFileShareLinkInfos removeAllObjects];
        NSArray *shareLinkInfos = [retobj objectForKey:@"links"];
        for (NSDictionary *shareLinkInfoDic in shareLinkInfos) {
            CloudFileShareLinkInfo *cloudFileShareLinkInfo = [[CloudFileShareLinkInfo alloc]initWithInfo:shareLinkInfoDic];
            [self.cloudFileShareLinkInfos addObject:cloudFileShareLinkInfo];
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [self refreshShareLinkTableView];
            [self.cloudFileShareLinkTableView reloadData];
        });
        [self refreshShareLinkTableView];
        [self.cloudFileShareLinkTableView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self refreshShareLinkTableView];
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudShareLinkLoadFailedPrompt", nil)];
        });
    }];
}

- (void)showShareLink {
    if (self.cloudFileShareLinkButton.selected) {
        return;
    }
    self.cloudFileShareWithButton.selected = NO;
    self.cloudFileShareLinkButton.selected = YES;
    
    self.cloudFileShareUserAddButton.hidden = YES;
    self.cloudFileShareLinkAddButton.hidden = NO;
    
    self.cloudFileShareHighLightView.frame = CGRectMake(CGRectGetWidth(self.cloudFileShareSegmentView.frame)/2, CGRectGetHeight(self.cloudFileShareSegmentView.frame)-3, CGRectGetWidth(self.cloudFileShareSegmentView.frame)/2, 3);
    
    [self.cloudFileShareWithButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareWithButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"333333" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.cloudFileShareLinkButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareLinkButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be0" alpha:1.0f],NSFontAttributeName:[UIFont boldSystemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    
    [self.cloudFileShareScrollView setContentOffset:CGPointMake(CGRectGetWidth(self.view.frame), 0) animated:YES];
    [self refreshShareLinkTableView];
}

- (void)refreshShareLinkTableView {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    CGFloat maxShareLinkTableHeight = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationFrame.size.height-CGRectGetHeight(self.cloudFileShareSegmentView.frame)-CGRectGetHeight(self.cloudFileInfoView.frame)-10-CGRectGetHeight(self.cloudFileShareLinkCreatePromptLabel.frame)-3-CGRectGetHeight(self.cloudFileShareLinkPermissionPromptLabel.frame);
    CGFloat tableViewHeightOffset = 0;
    for (CloudFileShareLinkInfo *shareLinkInfo in self.cloudFileShareLinkInfos) {
        if (shareLinkInfo.shareLinkExtracCode) {
            tableViewHeightOffset = tableViewHeightOffset + 19.0f;
        }
    }
    CGFloat shareLinkTableHeight = self.cloudFileShareLinkInfos.count*132+tableViewHeightOffset;
    self.cloudFileShareLinkTableView.frame = CGRectMake(CGRectGetWidth(self.view.frame), 0, CGRectGetWidth(self.view.frame), MIN(shareLinkTableHeight, maxShareLinkTableHeight));
    self.cloudFileShareLinkCreatePromptLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)+15, CGRectGetMaxY(self.cloudFileShareLinkTableView.frame)+10, CGRectGetWidth(self.view.frame)-15-15, 18);
    self.cloudFileShareLinkPermissionPromptLabel.frame = CGRectMake(CGRectGetWidth(self.view.frame)+15, CGRectGetMaxY(self.cloudFileShareLinkCreatePromptLabel.frame)+3, CGRectGetWidth(self.view.frame)-15-15, 18);
}

- (UILabel*)cloudFileShareLinkCreatePromptLabel {
    if (!_cloudFileShareLinkCreatePromptLabel) {
        _cloudFileShareLinkCreatePromptLabel = [[UILabel alloc]initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)+15, 10, CGRectGetWidth(self.view.frame)-15-15,18)];
        _cloudFileShareLinkCreatePromptLabel.font = [UIFont systemFontOfSize:12.0f];
        _cloudFileShareLinkCreatePromptLabel.textColor = [CommonFunction colorWithString:@"999999" alpha:1.0f];
        _cloudFileShareLinkCreatePromptLabel.textAlignment = NSTextAlignmentCenter;
        _cloudFileShareLinkCreatePromptLabel.text = getLocalizedString(@"CloudShareLinkNumPrompt", nil);
    }
    return _cloudFileShareLinkCreatePromptLabel;
}

- (UILabel*)cloudFileShareLinkPermissionPromptLabel {
    if (!_cloudFileShareLinkPermissionPromptLabel) {
        _cloudFileShareLinkPermissionPromptLabel = [[UILabel alloc]initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)+15, 10+15+3, CGRectGetWidth(self.view.frame)-15-15,18)];
        _cloudFileShareLinkPermissionPromptLabel.font = [UIFont systemFontOfSize:12.0f];
        _cloudFileShareLinkPermissionPromptLabel.textColor = [CommonFunction colorWithString:@"999999" alpha:1.0f];
        _cloudFileShareLinkPermissionPromptLabel.textAlignment = NSTextAlignmentCenter;
        _cloudFileShareLinkPermissionPromptLabel.text = getLocalizedString(@"CloudShareLinkPermissonPrompt", nil);
    }
    return _cloudFileShareLinkPermissionPromptLabel;
}

- (void)editShareLink:(CloudFileShareLinkInfo *)cloudFileShareLinkInfo {
    CloudFileShareLinkSettingViewController *fileShareLinkSettingViewController = [[CloudFileShareLinkSettingViewController alloc] initWithShareLinkInfo:cloudFileShareLinkInfo shareLinkFile:self.file];
    [self.navigationController pushViewController:fileShareLinkSettingViewController animated:YES];
}

- (void)deleteShareLink:(CloudFileShareLinkInfo *)cloudFileShareLinkInfo {
    [UIAlertView showAlertViewWithTitle:getLocalizedString(@"CloudShareLinkDeletePrompt", nil) message:nil cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
        [self.file fileLinkDelete:cloudFileShareLinkInfo.shareLinkId success:^(id retobj) {
            [self.cloudFileShareLinkInfos removeObject:cloudFileShareLinkInfo];
            dispatch_async(dispatch_get_main_queue(), ^{
                [self refreshShareLinkTableView];
                [self.cloudFileShareLinkTableView reloadData];
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationSuccess", nil)];
            });
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
            });
        }];
    } onCancel:^{
        
    }];
}

- (void)addShareLink {
    [self.file fileLinkCreate:nil succeed:^(id retobj) {
        CloudFileShareLinkSettingViewController *linkSettingViewController = [[CloudFileShareLinkSettingViewController alloc]initWithShareLinkInfo:[[CloudFileShareLinkInfo alloc] initWithInfo:retobj] shareLinkFile:self.file];
        [self.navigationController pushViewController:linkSettingViewController animated:YES];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
    }];
}
- (void)pushViewController:(UIViewController *)viewController{
    [self.navigationController pushViewController:viewController animated:YES];
    

}
#pragma mark tableViewDelegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (tableView == self.cloudFileShareWithTableView) {
        if (self.cloudFileShareWithUsers.count == 0 || self.cloudFileShareWithUsers.count == 1) {
            self.cloudFileShareWithTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        } else {
            self.cloudFileShareWithTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        }
        return self.cloudFileShareWithUsers.count;
    }
    if (tableView == self.cloudFileShareLinkTableView) {
        return self.cloudFileShareLinkInfos.count;
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1f;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView == self.cloudFileShareWithTableView) {
        return 55.0f;
    }
    if (tableView == self.cloudFileShareLinkTableView) {
        CloudFileShareLinkInfo*shareLinkInfo = [self.cloudFileShareLinkInfos objectAtIndex:indexPath.row];
        if (shareLinkInfo.shareLinkExtracCode) {
            return 132.0+19.0f;
        } else {
            return 132.0f;
        }
    }
    return 0.0f;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView == self.cloudFileShareWithTableView) {
        CloudFileShareWithCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CloudFileShareWithCell"];
        if (!cell) {
            cell = [[CloudFileShareWithCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudFileShareWithCell"];
        }
        NSString *userCloudId = [self.cloudFileShareWithUsers objectAtIndex:indexPath.row];
        User *user = [User getUserWithUserCloudId:userCloudId context:nil];
        cell.user = user;
        cell.delegate = self;
        return cell;
    }
    if (tableView == self.cloudFileShareLinkTableView) {
        CloudFileShareLinkCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CloudFileShareLinkCell"];
        if (!cell) {
            cell = [[CloudFileShareLinkCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudFileShareLinkCell"];
        }
        cell.cloudFileShareLinkInfo = [self.cloudFileShareLinkInfos objectAtIndex:indexPath.row];
        cell.file = self.file;
        cell.delegate = self;
        return cell;
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView == self.cloudFileShareLinkTableView) {
        return;
    }
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        if (indexPath.row+1 == [tableView numberOfRowsInSection:indexPath.section]) {
            [cell setSeparatorInset:UIEdgeInsetsMake(0, CGRectGetWidth(self.view.frame)/2, 0, CGRectGetWidth(self.view.frame)/2)];
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

#pragma mark scrollView delegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    if (scrollView !=self.cloudFileShareScrollView) {
        return;
    }
    CGPoint offSetRect = scrollView.contentOffset;
    if (offSetRect.x>=CGRectGetWidth(self.view.frame)) {
        [self showShareLink];
    } else if (offSetRect.x<=0){
        [self showShareWith];
    }
}

@end
