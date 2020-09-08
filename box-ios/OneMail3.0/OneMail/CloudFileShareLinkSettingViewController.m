//
//  CloudFileShareLinkSettingViewController.m
//  OneMail
//
//  Created by cse  on 15/11/24.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudFileShareLinkSettingViewController.h"
#import "AppDelegate.h"
#import "CloudFileShareLinkInfo.h"
#import "File+Remote.h"

@interface FileShareLinkAuthorityButton : UIButton

@property (nonatomic, strong) UIImageView *buttonImageView;
@property (nonatomic, strong) UILabel *buttonLabel;
@property (nonatomic, assign) BOOL buttonSelected;

@end

@implementation FileShareLinkAuthorityButton

+ (FileShareLinkAuthorityButton*)authorityButton:(CGRect)frame title:(NSString*)title {
    FileShareLinkAuthorityButton *button = [[FileShareLinkAuthorityButton alloc] initWithFrame:frame];
    button.buttonImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ic_choice"]];
    button.buttonImageView.frame = CGRectMake(0, 11, 22, 22);
    [button addSubview:button.buttonImageView];
    button.buttonLabel = [[UILabel alloc] initWithFrame:CGRectMake(22+8, 11, frame.size.width-22-8, 22)];
    button.buttonLabel.text = title;
    button.buttonLabel.font = [UIFont systemFontOfSize:14.0f];
    button.buttonLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    button.buttonLabel.textAlignment = NSTextAlignmentLeft;
    button.buttonSelected = NO;
    [button addSubview:button.buttonLabel];
    return button;
}

- (void)clickButton:(UIButton*)sender {
    if (self.buttonSelected) {
        [self disSelectButton];
    } else {
        [self selectButton];
    }
}

- (void)selectButton {
    self.buttonSelected = YES;
    self.buttonImageView.image = [UIImage imageNamed:@"ic_checkbox_on_nor"];
}
- (void)disSelectButton {
    self.buttonSelected = NO;
    self.buttonImageView.image = [UIImage imageNamed:@"ic_choice"];
}

@end

typedef enum {
    ExtractionNone = 0,
    ExtractionCommon,
    ExtractionCustom,
}ExtractionType;

@interface CloudFileShareLinkSettingViewController ()<UIActionSheetDelegate,UITableViewDataSource,UITableViewDelegate,UITextFieldDelegate>

@property (nonatomic, strong) File* file;
@property (nonatomic, strong) CloudFileShareLinkInfo   *cloudFileShareLinkInfo;

@property (nonatomic, strong) UILabel         *cloudFileShareLinkTitleLabel;
@property (nonatomic, strong) UIButton        *cloudFileShareLinkBackButton;
@property (nonatomic, strong) UIButton        *cloudFileShareLinkConfirmButton;

@property (nonatomic, strong) UITableView     *cloudFileShareLinkTableView;

@property (nonatomic, strong) UITableViewCell *cloudFileShareLinkAddressCell;

@property (nonatomic, strong) UITableViewCell *cloudFileShareLinkAuthorityCell;

@property (nonatomic, strong) UITableViewCell *cloudFileShareLinkExtractionTypeCell;
@property (nonatomic, assign) BOOL             cloudFileShareLinkExtractionCustomPermission;
@property (nonatomic, strong) UIButton        *cloudFileShareLinkExtractionTypeButton;
@property (nonatomic, assign) ExtractionType   cloudFileShareLinkExtractionType;

@property (nonatomic, strong) UITableViewCell *cloudFileShareLinkExtractionCodeCell;
@property (nonatomic, strong) UITextField     *cloudFileShareLinkExtractionCode;
@property (nonatomic, strong) UIButton        *cloudFileShareLinkExtractionCodeRefreshButton;

@property (nonatomic, strong) UITableViewCell *cloudFileShareLinkPeriodCell;
@property (nonatomic, strong) UIButton        *cloudFileShareLinkPeriodTypeButton;
@property (nonatomic, strong) UITableViewCell *cloudFileShareLinkPeriodStartCell;
@property (nonatomic, strong) UIButton        *cloudFileShareLinkPeriodStartButton;
@property (nonatomic, strong) UITableViewCell *cloudFileShareLinkPeriodEndCell;
@property (nonatomic, strong) UIButton        *cloudFileShareLinkPeriodEndButton;
@property (nonatomic, strong) UIDatePicker    *cloudFileShareLinkPeriodDatePicker;
@property (nonatomic, assign) BOOL             cloudFileShareLinkPeriodCustom;

@property (nonatomic, strong) FileShareLinkAuthorityButton *fileShareLinkAuthorityDownload;
@property (nonatomic, strong) FileShareLinkAuthorityButton *fileShareLinkAuthorityPreview;
@property (nonatomic, strong) FileShareLinkAuthorityButton *fileShareLinkAuthorityUpload;
@end

@implementation CloudFileShareLinkSettingViewController
- (id)initWithShareLinkInfo:(CloudFileShareLinkInfo *)cloudFileShareLinkInfo shareLinkFile:(File *)file {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.file = file;
        self.cloudFileShareLinkInfo = cloudFileShareLinkInfo;
        if (self.cloudFileShareLinkInfo.shareLinkExpireAt) {
            self.cloudFileShareLinkPeriodCustom = YES;
        } else {
            self.cloudFileShareLinkPeriodCustom = NO;
        }
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.cloudFileShareLinkTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.cloudFileShareLinkTitleLabel.text = getLocalizedString(@"CloudShareLinkSettingTitle", nil);
    
    self.cloudFileShareLinkBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.cloudFileShareLinkBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudFileShareLinkBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.cloudFileShareLinkBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.cloudFileShareLinkBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    
    CGSize adjustLabelSize = [CommonFunction labelSizeWithString:getLocalizedString(@"Confirm", nil) font:[UIFont systemFontOfSize:17.0f]];
    self.cloudFileShareLinkConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15- adjustLabelSize.width, 0, adjustLabelSize.width, 44)];
    [self.cloudFileShareLinkConfirmButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"Confirm", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.cloudFileShareLinkConfirmButton addTarget:self action:@selector(confirm) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    self.cloudFileShareLinkTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusFrame.size.height+navigationFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusFrame.size.height-navigationFrame.size.height) style:UITableViewStyleGrouped];
    self.cloudFileShareLinkTableView.dataSource = self;
    self.cloudFileShareLinkTableView.delegate = self;
    self.cloudFileShareLinkTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.cloudFileShareLinkTableView.separatorInset = UIEdgeInsetsZero;
    self.cloudFileShareLinkTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.cloudFileShareLinkTableView.tableFooterView = self.cloudFileShareLinkPeriodDatePicker;
    [self.view addSubview:self.cloudFileShareLinkTableView];
    [self.cloudFileShareLinkTableView reloadData];
    
    self.cloudFileShareLinkExtractionCustomPermission = NO;
    [self.file fileLinkOption:^(id retobj) {
        NSDictionary *valueString = [retobj lastObject];
        if ([[valueString objectForKey:@"value"] isEqualToString:@"simple"]) {
            self.cloudFileShareLinkExtractionCustomPermission = YES;
        }
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        
    }];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.cloudFileShareLinkTitleLabel];
    [self.navigationController.navigationBar addSubview:self.cloudFileShareLinkBackButton];
    [self.navigationController.navigationBar addSubview:self.cloudFileShareLinkConfirmButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.cloudFileShareLinkTitleLabel removeFromSuperview];
    [self.cloudFileShareLinkBackButton removeFromSuperview];
    [self.cloudFileShareLinkConfirmButton removeFromSuperview];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];

    [self.file fileLinkDelete:self.cloudFileShareLinkInfo.shareLinkId success:^(id retobj) {
        
//        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationSuccess", nil)];
        
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
    }];
}

- (void)confirm {
    self.cloudFileShareLinkInfo.shareLinkRole = [self fileShareLinkAuthorityRole];
    self.cloudFileShareLinkInfo.shareLinkExtracCode = self.cloudFileShareLinkExtractionCode.text;
    NSMutableDictionary *fileNewLinkInfo = [[NSMutableDictionary alloc] init];
    [fileNewLinkInfo setObject:self.cloudFileShareLinkInfo.shareLinkId forKey:@"id"];
    [fileNewLinkInfo setObject:[self fileShareLinkAuthorityRole] forKey:@"role"];
    if (self.cloudFileShareLinkInfo.shareLinkExtracCode) {
        [fileNewLinkInfo setObject:self.cloudFileShareLinkInfo.shareLinkExtracCode forKey:@"PlainAccessCode"];
    }
    if (self.cloudFileShareLinkInfo.shareLinkEffectiveAt) {
        [fileNewLinkInfo setObject:self.cloudFileShareLinkInfo.shareLinkEffectiveAt forKey:@"EffectiveAt"];
    }
    if (self.cloudFileShareLinkInfo.shareLinkExpireAt) {
        [fileNewLinkInfo setObject:self.cloudFileShareLinkInfo.shareLinkExpireAt forKey:@"ExpireAt"];
        NSDate *effectiveAt = [NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkEffectiveAt longLongValue]/1000];
        NSDate *expireAt = [NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkExpireAt longLongValue]/1000];
        if ([effectiveAt compare:expireAt] == NSOrderedDescending) {
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudShareLinkPeriodFailedPrompt", nil)];
            return;
        }
    }
    [self.file fileLinkRefresh:fileNewLinkInfo succeed:^(id retobj) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationSuccess", nil)];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
    }];
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark tableview delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 4.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 22.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1f;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 1;
    } else if (section == 1) {
        return 1;
    } else if (section == 2) {
        return 2;
    } else {
        if (self.cloudFileShareLinkPeriodCustom) {
            return 3;
        } else {
            return 1;
        }
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1) {
        return 88.0f;
    } else {
        return 44.0f;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return self.cloudFileShareLinkAddressCell;
    } else if (indexPath.section == 1) {
        return self.cloudFileShareLinkAuthorityCell;
    } else if (indexPath.section == 2) {
        if (indexPath.row == 0) {
            return self.cloudFileShareLinkExtractionTypeCell;
        } else {
            return self.cloudFileShareLinkExtractionCodeCell;
        }
    } else {
        if (self.cloudFileShareLinkPeriodCustom) {
            if (indexPath.row == 0) {
                return self.fileShareLinkPeriodCell;
            } else if (indexPath.row == 1) {
                return self.fileShareLinkPeriodStartCell;
            } else {
                return self.fileShareLinkPeriodEndCell;
            }
        } else {
            return self.fileShareLinkPeriodCell;
        }
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 2) {
        if (indexPath.row == 0) {
            [self cloudFileShareLinkExtractionTypeSelect];
        }
        if (indexPath.row == 1) {
            if (self.cloudFileShareLinkExtractionType == ExtractionCustom) {
                [self cloudFileShareLinkExtractionCodeCustom];
            }
            if (self.cloudFileShareLinkExtractionType == ExtractionCommon) {
                [self cloudFileShareLinkExtractionCodeRefresh];
            }
        }
    }
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

#pragma mark ShareLink Address
- (UITableViewCell*)cloudFileShareLinkAddressCell {
    if (!_cloudFileShareLinkAddressCell) {
        _cloudFileShareLinkAddressCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _cloudFileShareLinkAddressCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareLinkAddressCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        CGSize adjustTitleSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareLinkTitle", nil) font:[UIFont systemFontOfSize:17.0f]];
        UILabel *shareLinkTitle = [CommonFunction labelWithFrame:CGRectMake(15, 11, adjustTitleSize.width, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        shareLinkTitle.text = getLocalizedString(@"CloudShareLinkTitle", nil);;
        [_cloudFileShareLinkAddressCell.contentView addSubview:shareLinkTitle];
        
        UILabel *shareLinkAddress = [CommonFunction labelWithFrame:CGRectMake(15+CGRectGetWidth(shareLinkTitle.frame)+10, 11, CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(shareLinkTitle.frame)-10-15, 22) textFont:[UIFont systemFontOfSize:13.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentRight];
        shareLinkAddress.text = self.cloudFileShareLinkInfo.shareLinkUrl;
        [_cloudFileShareLinkAddressCell.contentView addSubview:shareLinkAddress];
    }
    return _cloudFileShareLinkAddressCell;
}

#pragma mark ShareLink Authority
- (UITableViewCell*)cloudFileShareLinkAuthorityCell {
    if (!_cloudFileShareLinkAuthorityCell) {
        _cloudFileShareLinkAuthorityCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _cloudFileShareLinkAuthorityCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareLinkAuthorityCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        CGSize adjustTitleSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareLinkAccess", nil) font:[UIFont systemFontOfSize:17.0f]];
        UILabel *authorityTitle = [CommonFunction labelWithFrame:CGRectMake(15, 11, adjustTitleSize.width, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        authorityTitle.text = getLocalizedString(@"CloudShareLinkAccess", nil);
        [_cloudFileShareLinkAuthorityCell.contentView addSubview:authorityTitle];
        
        self.fileShareLinkAuthorityDownload.frame = CGRectMake(15, CGRectGetMaxY(authorityTitle.frame)+9, CGRectGetWidth(self.fileShareLinkAuthorityDownload.frame), CGRectGetHeight(self.fileShareLinkAuthorityDownload.frame));
        [_cloudFileShareLinkAuthorityCell.contentView addSubview:self.fileShareLinkAuthorityDownload];
        
        self.fileShareLinkAuthorityPreview.frame = CGRectMake(CGRectGetMaxX(self.fileShareLinkAuthorityDownload.frame)+5, CGRectGetMaxY(authorityTitle.frame)+9, CGRectGetWidth(self.fileShareLinkAuthorityPreview.frame), CGRectGetHeight(self.fileShareLinkAuthorityPreview.frame));
        [_cloudFileShareLinkAuthorityCell.contentView addSubview:self.fileShareLinkAuthorityPreview];
        
        self.fileShareLinkAuthorityUpload.frame = CGRectMake(CGRectGetMaxX(self.fileShareLinkAuthorityPreview.frame)+5, CGRectGetMaxY(authorityTitle.frame)+9, CGRectGetWidth(self.fileShareLinkAuthorityUpload.frame), CGRectGetHeight(self.fileShareLinkAuthorityUpload.frame));
        [self fileShareLinkAuthorityWithRole:self.cloudFileShareLinkInfo.shareLinkRole];
        [_cloudFileShareLinkAuthorityCell.contentView addSubview:self.fileShareLinkAuthorityUpload];
    }
    return _cloudFileShareLinkAuthorityCell;
}

- (FileShareLinkAuthorityButton*)fileShareLinkAuthorityDownload {
    if (!_fileShareLinkAuthorityDownload) {
        CGFloat authorityButtonWidth = (CGRectGetWidth(self.view.frame)-15*2-5*2)/3;
        _fileShareLinkAuthorityDownload = [FileShareLinkAuthorityButton authorityButton:CGRectMake(0, 0, authorityButtonWidth, 44) title:getLocalizedString(@"CloudShareLinkAccessDownload", nil)];
        [_fileShareLinkAuthorityDownload addTarget:_fileShareLinkAuthorityDownload action:@selector(clickButton:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _fileShareLinkAuthorityDownload;
}

- (FileShareLinkAuthorityButton*)fileShareLinkAuthorityPreview {
    if (!_fileShareLinkAuthorityPreview) {
        CGFloat authorityButtonWidth = (CGRectGetWidth(self.view.frame)-15*2-5*2)/3;
        _fileShareLinkAuthorityPreview = [FileShareLinkAuthorityButton authorityButton:CGRectMake(0, 0, authorityButtonWidth, 44) title:getLocalizedString(@"CloudShareLinkAccessPreview", nil)];
        [_fileShareLinkAuthorityPreview addTarget:_fileShareLinkAuthorityPreview action:@selector(clickButton:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _fileShareLinkAuthorityPreview;
}

- (FileShareLinkAuthorityButton*)fileShareLinkAuthorityUpload {
    if (!_fileShareLinkAuthorityUpload) {
        CGFloat authorityButtonWidth = (CGRectGetWidth(self.view.frame)-15*2-5*2)/3;
        _fileShareLinkAuthorityUpload = [FileShareLinkAuthorityButton authorityButton:CGRectMake(0, 0, authorityButtonWidth, 44) title:getLocalizedString(@"CloudShareLinkAccessUpload", nil)];
        if (self.file.isFolder) {
            [_fileShareLinkAuthorityUpload addTarget:_fileShareLinkAuthorityUpload action:@selector(clickButton:) forControlEvents:UIControlEventTouchUpInside];
        }
    }
    return _fileShareLinkAuthorityUpload;
}

- (void)fileShareLinkAuthorityWithRole:(NSString*)role {
    if ([role isEqualToString:@"uploader"]) {
        [self.fileShareLinkAuthorityUpload selectButton];
    }
    if ([role isEqualToString:@"previewer"]) {
        [self.fileShareLinkAuthorityPreview selectButton];
    }
    if ([role isEqualToString:@"viewer"]) {
        [self.fileShareLinkAuthorityPreview selectButton];
        [self.fileShareLinkAuthorityDownload selectButton];
    }
    if ([role isEqualToString:@"uploadAndView"]) {
        [self.fileShareLinkAuthorityPreview selectButton];
        [self.fileShareLinkAuthorityDownload selectButton];
        [self.fileShareLinkAuthorityUpload selectButton];
    }
}

- (NSString*)fileShareLinkAuthorityRole {
    int roleCount = 0;
    if (self.fileShareLinkAuthorityUpload.buttonSelected) {roleCount += 1;}
    if (self.fileShareLinkAuthorityPreview.buttonSelected) {roleCount += 2;}
    if (self.fileShareLinkAuthorityDownload.buttonSelected) {roleCount += 4;}
    switch (roleCount) {
        case 1:
            return @"uploader";
            break;
        case 2:
            return @"previewer";
            break;
        case 4:
        case 6:
            return @"viewer";
            break;
        case 3:
        case 5:
        case 7:
            return @"uploadAndView";
            break;
        default:
            return nil;
            break;
    }
}

#pragma mark ShareLink Extraction Code
- (UITableViewCell*)cloudFileShareLinkExtractionTypeCell {
    if (!_cloudFileShareLinkExtractionTypeCell) {
        _cloudFileShareLinkExtractionTypeCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _cloudFileShareLinkExtractionTypeCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareLinkExtractionTypeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        CGSize adjustTitleSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareLinkExtraction", nil) font:[UIFont systemFontOfSize:17.0f]];
        UILabel *extractionTypeTitle = [CommonFunction labelWithFrame:CGRectMake(15, 11, adjustTitleSize.width, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        extractionTypeTitle.text = getLocalizedString(@"CloudShareLinkExtraction", nil);
        [_cloudFileShareLinkExtractionTypeCell.contentView addSubview:extractionTypeTitle];
        
        self.cloudFileShareLinkExtractionTypeButton = [[UIButton alloc] initWithFrame:CGRectZero];
        if (self.cloudFileShareLinkInfo.shareLinkExtracCode) {
            self.cloudFileShareLinkExtractionType = ExtractionCommon;
            [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionCommon", nil)];
        } else {
            self.cloudFileShareLinkExtractionType =ExtractionNone;
            [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionNone", nil)];
        }
        [self.cloudFileShareLinkExtractionTypeButton addTarget:self action:@selector(cloudFileShareLinkExtractionTypeSelect) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareLinkExtractionTypeCell.contentView addSubview:self.cloudFileShareLinkExtractionTypeButton];
    }
    return _cloudFileShareLinkExtractionTypeCell;
}

- (void)cloudFileShareLinkExtractionButtonWithString:(NSString*)buttonString {
    CGSize adjustButtonSize = [CommonFunction labelSizeWithString:buttonString font:[UIFont systemFontOfSize:14.0f]];
    self.cloudFileShareLinkExtractionTypeButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustButtonSize.width, 0, adjustButtonSize.width, 44);
    [self.cloudFileShareLinkExtractionTypeButton setAttributedTitle:[[NSAttributedString alloc] initWithString:buttonString attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"666666" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
}

- (void)cloudFileShareLinkExtractionTypeSelect {
    UIActionSheet *sheet;
    if (self.cloudFileShareLinkExtractionCustomPermission) {
        sheet = [[UIActionSheet alloc] initWithTitle:getLocalizedString(@"CloudShareLinkExtractionType", nil) delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"CloudShareLinkExtractionNone", nil),getLocalizedString(@"CloudShareLinkExtractionCommon", nil),getLocalizedString(@"CloudShareLinkExtractionCustom", nil),nil];
        sheet.tag = 10000;
    } else {
        sheet = [[UIActionSheet alloc] initWithTitle:getLocalizedString(@"CloudShareLinkExtractionType", nil) delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"CloudShareLinkExtractionNone", nil),getLocalizedString(@"CloudShareLinkExtractionCommon", nil), nil];
        sheet.tag = 10001;
    }
    [sheet showInView:self.view];
}

- (UITableViewCell*)cloudFileShareLinkExtractionCodeCell {
    if (!_cloudFileShareLinkExtractionCodeCell) {
        _cloudFileShareLinkExtractionCodeCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _cloudFileShareLinkExtractionCodeCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareLinkExtractionCodeCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        CGSize adjustLabelSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareLinkRefrshButton", nil) font:[UIFont systemFontOfSize:14.0f]];
        self.cloudFileShareLinkExtractionCodeRefreshButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustLabelSize.width, 0, adjustLabelSize.width, 44)];
        [self.cloudFileShareLinkExtractionCodeRefreshButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudShareLinkRefrshButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be8" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
        [self.cloudFileShareLinkExtractionCodeRefreshButton addTarget:self action:@selector(cloudFileShareLinkExtractionCodeRefresh) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareLinkExtractionCodeCell.contentView addSubview:self.cloudFileShareLinkExtractionCodeRefreshButton];
        
        _cloudFileShareLinkExtractionCode = [[UITextField alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-adjustLabelSize.width-15-10, 22)];
        _cloudFileShareLinkExtractionCode.font = [UIFont systemFontOfSize:17.0f];
        _cloudFileShareLinkExtractionCode.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        _cloudFileShareLinkExtractionCode.textAlignment = NSTextAlignmentLeft;
        _cloudFileShareLinkExtractionCode.returnKeyType = UIReturnKeyDone;
        _cloudFileShareLinkExtractionCode.delegate = self;
        [_cloudFileShareLinkExtractionCodeCell.contentView addSubview:_cloudFileShareLinkExtractionCode];
        
        if (self.cloudFileShareLinkInfo.shareLinkExtracCode) {
            self.cloudFileShareLinkExtractionCode.text = self.cloudFileShareLinkInfo.shareLinkExtracCode;
            self.cloudFileShareLinkExtractionCode.enabled = NO;
            self.cloudFileShareLinkExtractionCodeRefreshButton.hidden = NO;
        } else {
            self.cloudFileShareLinkExtractionCode.text = nil;
            self.cloudFileShareLinkExtractionCode.enabled = NO;
            self.cloudFileShareLinkExtractionCodeRefreshButton.hidden = YES;
        }
    }
    return _cloudFileShareLinkExtractionCodeCell;
}

- (void)cloudFileShareLinkExtractionCodeCustom {
    [self.cloudFileShareLinkExtractionCode becomeFirstResponder];
}

- (void)cloudFileShareLinkExtractionCodeRefresh {
    _cloudFileShareLinkExtractionCode.text = [self getRandom8TakeCode];
    [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionCommon", nil)];;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = self.navigationController.navigationBar.frame;
    rect.origin.y = (rect.size.height-216-40) - (statusBarFrame.size.height+navigationBarFrame.size.height+22+CGRectGetHeight(self.cloudFileShareLinkAddressCell.frame)+22+CGRectGetHeight(self.cloudFileShareLinkAuthorityCell.frame)+22+CGRectGetHeight(self.cloudFileShareLinkExtractionTypeCell.frame)+CGRectGetHeight(self.cloudFileShareLinkExtractionCodeCell.frame));
    if(rect.origin.y < 0)
        self.view.frame = rect;
    [UIView commitAnimations];
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = 0;
    self.view.frame = rect;
    [UIView commitAnimations];
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if (string.length == 0) {
        return YES;
    }
    NSInteger existedLength = textField.text.length;
    NSInteger selectedLength = range.length;
    NSInteger replaceLength = string.length;
    if (existedLength - selectedLength + replaceLength > 20) {
        return NO;
    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = 0;
    self.view.frame = rect;
    [UIView commitAnimations];
    return YES;
}

#pragma mark ShareLink Period
- (UITableViewCell*)fileShareLinkPeriodCell {
    if (!_cloudFileShareLinkPeriodCell) {
        _cloudFileShareLinkPeriodCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _cloudFileShareLinkPeriodCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareLinkPeriodCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        CGSize adjustTitleSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareLinkPeriod", nil) font:[UIFont systemFontOfSize:17.0f]];
        UILabel *periodTitle = [CommonFunction labelWithFrame:CGRectMake(15, 11, adjustTitleSize.width, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        periodTitle.text = getLocalizedString(@"CloudShareLinkPeriod", nil);
        [_cloudFileShareLinkPeriodCell.contentView addSubview:periodTitle];
        
        _cloudFileShareLinkPeriodTypeButton = [[UIButton alloc] initWithFrame:CGRectZero];
        if (self.cloudFileShareLinkPeriodCustom) {
            [self cloudFileShareLinkPeriodButtonWithString:getLocalizedString(@"CloudShareLinkPeriodCustom", nil)];
        } else {
            [self cloudFileShareLinkPeriodButtonWithString:getLocalizedString(@"CloudShareLinkPeriodForever", nil)];
        }
        [_cloudFileShareLinkPeriodTypeButton addTarget:self action:@selector(cloudFileShareLinkPeriodType:) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareLinkPeriodCell.contentView addSubview:_cloudFileShareLinkPeriodTypeButton];
    }
    return _cloudFileShareLinkPeriodCell;
}

- (void)cloudFileShareLinkPeriodButtonWithString:(NSString*)buttonString {
    CGSize adjustButtonSize = [CommonFunction labelSizeWithString:buttonString font:[UIFont systemFontOfSize:14.0f]];
    self.cloudFileShareLinkPeriodTypeButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustButtonSize.width, 0, adjustButtonSize.width, 44);
    [self.cloudFileShareLinkPeriodTypeButton setAttributedTitle:[[NSAttributedString alloc] initWithString:buttonString attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"666666" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
}

- (void)cloudFileShareLinkPeriodType:(UIButton*)button {
    UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:getLocalizedString(@"CloudShareLinkPeriodType", nil) delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"CloudShareLinkPeriodCustom", nil),getLocalizedString(@"CloudShareLinkPeriodForever", nil), nil];
    sheet.tag = 10002;
    [sheet showInView:self.view];
}

- (UITableViewCell*)fileShareLinkPeriodStartCell {
    if (!_cloudFileShareLinkPeriodStartCell) {
        _cloudFileShareLinkPeriodStartCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _cloudFileShareLinkPeriodStartCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareLinkPeriodStartCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        CGSize adjustTitleSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareLinkPeriodStart", nil) font:[UIFont systemFontOfSize:17.0f]];
        UILabel *periodStartTitle = [CommonFunction labelWithFrame:CGRectMake(15, 11, adjustTitleSize.width, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        periodStartTitle.text = getLocalizedString(@"CloudShareLinkPeriodStart", nil);
        [_cloudFileShareLinkPeriodStartCell.contentView addSubview:periodStartTitle];
        
        self.cloudFileShareLinkPeriodStartButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self cloudFileShareLinkPeriodRefresh:self.cloudFileShareLinkPeriodStartButton];
        [self.cloudFileShareLinkPeriodStartButton addTarget:self action:@selector(cloudFileShareLinkPeriodCustom:) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareLinkPeriodStartCell.contentView addSubview:self.cloudFileShareLinkPeriodStartButton];
    }
    return _cloudFileShareLinkPeriodStartCell;
}

- (UITableViewCell*)fileShareLinkPeriodEndCell {
    if (!_cloudFileShareLinkPeriodEndCell) {
        _cloudFileShareLinkPeriodEndCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _cloudFileShareLinkPeriodEndCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _cloudFileShareLinkPeriodEndCell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        CGSize adjustTitleSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareLinkPeriodEnd", nil) font:[UIFont systemFontOfSize:17.0f]];
        UILabel *periodEndTitle = [CommonFunction labelWithFrame:CGRectMake(15, 11, adjustTitleSize.width, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        periodEndTitle.text = getLocalizedString(@"CloudShareLinkPeriodEnd", nil);
        [_cloudFileShareLinkPeriodEndCell.contentView addSubview:periodEndTitle];
        
        self.cloudFileShareLinkPeriodEndButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self cloudFileShareLinkPeriodRefresh:self.cloudFileShareLinkPeriodEndButton];
        [self.cloudFileShareLinkPeriodEndButton addTarget:self action:@selector(cloudFileShareLinkPeriodCustom:) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareLinkPeriodEndCell.contentView addSubview:self.cloudFileShareLinkPeriodEndButton];
    }
    return _cloudFileShareLinkPeriodEndCell;
}

- (void)cloudFileShareLinkPeriodCustom:(UIButton*)button {
    if (button == self.cloudFileShareLinkPeriodStartButton) {
        self.cloudFileShareLinkPeriodDatePicker.tag = 10003;
        [self cloudFileShareLinkPeriodDatePickerShow];
        self.cloudFileShareLinkPeriodDatePicker.date = [NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkEffectiveAt longLongValue]/1000];
    }
    if (button == self.cloudFileShareLinkPeriodEndButton) {
        self.cloudFileShareLinkPeriodDatePicker.tag = 10004;
        [self cloudFileShareLinkPeriodDatePickerShow];
        self.cloudFileShareLinkPeriodDatePicker.date = [NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkExpireAt longLongValue]/1000];
    }
}

- (void)cloudFileShareLinkPeriodRefresh:(UIButton*)button {
    NSString *buttonString = nil;
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy/MM/dd HH:mm"];
    if (button == self.cloudFileShareLinkPeriodStartButton) {
        if (self.cloudFileShareLinkInfo.shareLinkEffectiveAt) {
            buttonString = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkEffectiveAt longLongValue]/1000]];
        } else {
            NSDate *date = [NSDate date];
            self.cloudFileShareLinkInfo.shareLinkEffectiveAt = [NSNumber numberWithLongLong:[date timeIntervalSince1970]*1000];
            buttonString = [dateFormatter stringFromDate:date];
        }
    }
    if (button == self.cloudFileShareLinkPeriodEndButton) {
        if (self.cloudFileShareLinkInfo.shareLinkExpireAt) {
            buttonString = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:[self.cloudFileShareLinkInfo.shareLinkExpireAt longLongValue]/1000]];
        } else {
            NSDate *date = [NSDate date];
            self.cloudFileShareLinkInfo.shareLinkExpireAt = [NSNumber numberWithLongLong:[date timeIntervalSince1970]*1000];
            buttonString = [dateFormatter stringFromDate:date];
        }
    }
    if (buttonString) {
        CGSize adjustStringSize = [CommonFunction labelSizeWithString:buttonString font:[UIFont systemFontOfSize:14.0f]];
        button.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-adjustStringSize.width, 0, adjustStringSize.width, 44);
        [button setAttributedTitle:[[NSAttributedString alloc] initWithString:buttonString attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"666666" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
    }
}

#pragma mark ShareLink Period DatePicker
- (UIDatePicker *)cloudFileShareLinkPeriodDatePicker {
    if (!_cloudFileShareLinkPeriodDatePicker) {
        _cloudFileShareLinkPeriodDatePicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 216)];
        _cloudFileShareLinkPeriodDatePicker.datePickerMode = UIDatePickerModeDateAndTime;
        _cloudFileShareLinkPeriodDatePicker.minuteInterval = 1;
        _cloudFileShareLinkPeriodDatePicker.hidden = YES;
        NSString *strLanguage = [[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] objectAtIndex:0];
        NSLocale *locale;
        if ([strLanguage isEqualToString:@"zh-Hans"]) {
            locale = [[NSLocale alloc]initWithLocaleIdentifier:@"zh-Hans"];
        } else {
            locale = [[NSLocale alloc]initWithLocaleIdentifier:@"en"];
        }
        _cloudFileShareLinkPeriodDatePicker.locale = locale;
        [_cloudFileShareLinkPeriodDatePicker addTarget:self action:@selector(cloudFileShareLinkPeriodDatePick:) forControlEvents:UIControlEventValueChanged];
    }
    return _cloudFileShareLinkPeriodDatePicker;
}

- (void)cloudFileShareLinkPeriodDatePick:(UIDatePicker*)sender {
    NSDate *selectedDate = sender.date;
    if (sender.tag == 10003) {
        self.cloudFileShareLinkInfo.shareLinkEffectiveAt = [NSNumber numberWithLongLong:[selectedDate timeIntervalSince1970]*1000];
        [self cloudFileShareLinkPeriodRefresh:self.cloudFileShareLinkPeriodStartButton];
    }
    if (sender.tag == 10004) {
        if ([selectedDate compare:[NSDate date]] == NSOrderedAscending) {
            [sender setDate:[NSDate date] animated:YES];
        } else {
            self.cloudFileShareLinkInfo.shareLinkExpireAt = [NSNumber numberWithLongLong:[selectedDate timeIntervalSince1970]*1000];
            [self cloudFileShareLinkPeriodRefresh:self.cloudFileShareLinkPeriodEndButton];
        }
    }
}

- (void)cloudFileShareLinkPeriodDatePickerShow {
    self.cloudFileShareLinkPeriodDatePicker.hidden = NO;
    [UIView beginAnimations:nil context:nil];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    CGFloat tableViewHeight = 22+CGRectGetHeight(self.cloudFileShareLinkAddressCell.frame)+22+CGRectGetHeight(self.cloudFileShareLinkAuthorityCell.frame)+22+CGRectGetHeight(self.cloudFileShareLinkExtractionTypeCell.frame)+CGRectGetHeight(self.cloudFileShareLinkExtractionCodeCell.frame)+22+CGRectGetHeight(self.fileShareLinkPeriodCell.frame)+CGRectGetHeight(self.fileShareLinkPeriodStartCell.frame)+CGRectGetHeight(self.fileShareLinkPeriodEndCell.frame)+CGRectGetHeight(self.cloudFileShareLinkPeriodDatePicker.frame);
    CGRect tableViewFrame  = self.cloudFileShareLinkTableView.frame;
    if (tableViewHeight > [UIScreen mainScreen].bounds.size.height-statusBarFrame.size.height-navigationFrame.size.height) {
        tableViewFrame.origin.y = [UIScreen mainScreen].bounds.size.height-statusBarFrame.size.height-navigationFrame.size.height-tableViewHeight;
        tableViewFrame.size.height = tableViewHeight;
    }
    self.cloudFileShareLinkTableView.frame = tableViewFrame;
    [UIView setAnimationDuration:0.3f];
    [UIView commitAnimations];
}

- (void)cloudFileShareLinkPeriodDatePickerHide {
    self.cloudFileShareLinkPeriodDatePicker.hidden = YES;
    [UIView beginAnimations:nil context:nil];
    CGRect tableViewFrame = self.cloudFileShareLinkTableView.frame;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    tableViewFrame.origin.y = statusBarFrame.size.height+navigationFrame.size.height;
    tableViewFrame.size.height = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationFrame.size.height;
    self.cloudFileShareLinkTableView.frame = tableViewFrame;
    [UIView setAnimationDuration:0.3f];
    [UIView commitAnimations];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self cloudFileShareLinkPeriodDatePickerHide];
}

#pragma mark UIActionSheet Delegate
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (actionSheet.tag == 10000) {
        if (buttonIndex == 0) {
            [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionNone", nil)];
            self.cloudFileShareLinkExtractionCode.text = nil;
            self.cloudFileShareLinkExtractionCode.enabled = NO;
            self.cloudFileShareLinkExtractionCodeRefreshButton.hidden = YES;
        } else if (buttonIndex == 1) {
            [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionCommon", nil)];
            self.cloudFileShareLinkExtractionCode.enabled = NO;
            self.cloudFileShareLinkExtractionCodeRefreshButton.hidden = NO;
            self.cloudFileShareLinkExtractionCode.text = [self getRandom8TakeCode];
        } else if (buttonIndex == 2) {
            [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionCustom", nil)];
            self.cloudFileShareLinkExtractionCode.enabled = YES;
            self.cloudFileShareLinkExtractionCodeRefreshButton.hidden = YES;
            [self cloudFileShareLinkExtractionCodeCustom];
        }
    }
    if (actionSheet.tag == 10001) {
        if (buttonIndex == 0) {
            [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionNone", nil)];
            self.cloudFileShareLinkExtractionCode.text = nil;
            self.cloudFileShareLinkExtractionCode.enabled = NO;
            self.cloudFileShareLinkExtractionCodeRefreshButton.hidden = YES;
        } else if (buttonIndex == 1) {
            [self cloudFileShareLinkExtractionButtonWithString:getLocalizedString(@"CloudShareLinkExtractionCommon", nil)];
            self.cloudFileShareLinkExtractionCode.enabled = NO;
            self.cloudFileShareLinkExtractionCodeRefreshButton.hidden = NO;
            self.cloudFileShareLinkExtractionCode.text = [self getRandom8TakeCode];
        }
    }
    if (actionSheet.tag == 10002) {
        if (buttonIndex == 0) {
            self.cloudFileShareLinkPeriodCustom = YES;
            [self cloudFileShareLinkPeriodButtonWithString:getLocalizedString(@"CloudShareLinkPeriodCustom", nil)];
        } else if (buttonIndex == 1) {
            self.cloudFileShareLinkPeriodCustom = NO;
            self.cloudFileShareLinkInfo.shareLinkEffectiveAt = nil;
            self.cloudFileShareLinkInfo.shareLinkExpireAt = nil;
            [self cloudFileShareLinkPeriodButtonWithString:getLocalizedString(@"CloudShareLinkPeriodForever", nil)];
        
            [self cloudFileShareLinkPeriodDatePickerHide];
            
            
            
        }
        [self.cloudFileShareLinkTableView reloadData];
    }
}

-(NSString *)getRandom8TakeCode {
    NSArray *changeArray = [[NSArray alloc] initWithObjects:@"0",@"1",@"2",@"3",@"4",@"5",@"6",@"7",@"8",@"9",@"A",@"B",@"C",@"D",@"E",@"F",@"G",@"H",@"I",@"J",@"K",@"L",@"M",@"N",@"O",@"P",@"Q",@"R",@"S",@"T",@"U",@"V",@"W",@"X",@"Y",@"Z",@"a",@"b",@"c",@"d",@"e",@"f",@"g",@"h",@"i",@"j",@"k",@"l",@"m",@"n",@"o",@"p",@"q",@"r",@"s",@"t",@"u",@"v",@"w",@"x",@"y",@"z",@"!",@"@",@"#",@"$",@"^",@"&",@"*",@"-",@"+",nil];
    NSArray *specailArray=[[NSArray alloc]initWithObjects:@"!",@"@",@"#",@"$",@"^",@"&",@"*",@"-",@"+", nil];
    NSMutableString *changeString = [[NSMutableString alloc] initWithCapacity:8];
    
    NSInteger specialIndex=arc4random()%(7);
    NSInteger specialArrayIndex=arc4random()%([specailArray count] - 1);
    for(int i = 0; i < 8; i++)
    {
        if (i==specialIndex) {
            changeString = (NSMutableString *)[changeString stringByAppendingString:[specailArray objectAtIndex:specialArrayIndex]];
            continue;
        }
        NSInteger index = arc4random() % ([changeArray count] - 1);
        changeString = (NSMutableString *)[changeString stringByAppendingString:[changeArray objectAtIndex:index]];
    }
    return changeString;
}

@end

