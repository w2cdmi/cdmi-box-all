//
//  CloudBackUpViewController.m
//  OneMail
//
//  Created by cse  on 15/11/30.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudBackUpViewController.h"
#import "AppDelegate.h"
#import "CloudBackUpAlbumViewController.h"
#import <CoreData/CoreData.h>
#import "Asset.h"
#import "File.h"
#import "CloudFileViewController.h"
#import "CloudBackUpAlbumPreviewController.h"
#import "CloudBackUpAlbumFailedController.h"

@interface CloudBackUpViewController ()<UITableViewDelegate,UITableViewDataSource,NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) AssetBackUpOperation *backUpOperation;

@property (nonatomic, strong) UILabel         *backUpTitleLabel;
@property (nonatomic, strong) UIButton        *backUpBackButton;

@property (nonatomic, strong) UITableView     *backUpTableView;
@property (nonatomic, strong) UITableViewCell *backUpStatusCell;
@property (nonatomic, strong) UITableViewCell *backUpSelectCell;
@property (nonatomic, strong) UITableViewCell *backUpAutoCell;
@property (nonatomic, strong) UIView          *backUpControlView;

@property (nonatomic, strong) UIImageView     *backUpAssetDefaultImageView;
@property (nonatomic, strong) UIImageView     *backUpAssetImageView;
@property (nonatomic, strong) UILabel         *backUpStatusLabel;
@property (nonatomic, strong) UIView          *backUpStatusProgress;
@property (nonatomic, strong) UILabel         *backUpStatusRemain;
@property (nonatomic, strong) UIButton        *backUpStatusButton;
@property (nonatomic, strong) UIButton        *backUpAssetFailedButton;

@property (nonatomic, assign) NSInteger        backUpTotalCount;

@end

@implementation CloudBackUpViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.backUpTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.backUpTitleLabel.text = getLocalizedString(@"BackupTitle", nil);
    
    self.backUpBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.backUpBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.backUpBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.backUpBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.backUpBackButton addTarget:self action:@selector(backUpBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    self.backUpOperation = appDelegate.backUpAssetOperation;
    self.backUpOperation.backUpViewController = self;
    
    CGRect statusBarRect = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarRect = appDelegate.navigationController.navigationBar.frame;
    self.backUpTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarRect.size.height+navigationBarRect.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarRect.size.height-navigationBarRect.size.height) style:UITableViewStyleGrouped];
    self.backUpTableView.dataSource = self;
    self.backUpTableView.delegate = self;
    self.backUpTableView.scrollEnabled = YES;
    self.backUpTableView.showsVerticalScrollIndicator = NO;
    UIView *backUpTableHeaderView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.backUpTableView.frame), 0.01f)];
    self.backUpTableView.tableHeaderView = backUpTableHeaderView;
    [self.backUpTableView reloadData];
    [self.view addSubview:self.backUpTableView];
    
    [self.backUpOperation addObserver:self forKeyPath:@"backUpTotalCount" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.backUpTotalCount = self.backUpOperation.backUpTotalCount;
    [self.navigationController.navigationBar addSubview:self.backUpTitleLabel];
    [self.navigationController.navigationBar addSubview:self.backUpBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.backUpTitleLabel removeFromSuperview];
    [self.backUpBackButton removeFromSuperview];
}

- (void)backUpBackButtonClick {
    [self.navigationController popToViewController:(UIViewController*)self.settingViewController animated:YES];
}

- (void)dealloc {
    [self.backUpOperation removeObserver:self forKeyPath:@"backUpTotalCount"];
}

#pragma mark BakUpStatus
- (UITableViewCell*)backUpStatusCell {
    if (!_backUpStatusCell) {
        _backUpStatusCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _backUpStatusCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _backUpStatusCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        self.backUpAssetDefaultImageView = [[UIImageView alloc] initWithFrame:CGRectMake((CGRectGetWidth(self.view.frame)-300)/2, 35, 300, 160)];
        self.backUpAssetDefaultImageView.image = [UIImage imageNamed:@"photo_album_backup"];
        [_backUpStatusCell.contentView addSubview:self.backUpAssetDefaultImageView];
        
        self.backUpAssetImageView = [[UIImageView alloc] initWithFrame:CGRectMake((CGRectGetWidth(self.view.frame)-160)/2, 35, 160, 160)];
        [_backUpStatusCell.contentView addSubview:self.backUpAssetImageView];
        Asset *asset = self.backUpOperation.backUpAsset;
        if (asset) {
            NSString *assetThumbnailPath = [asset assetThumbnailPath];
            UIImage *assetImage = [UIImage imageWithContentsOfFile:assetThumbnailPath];
            [self.backUpAssetImageView setImage:assetImage];
        }
        
        self.backUpStatusLabel = [CommonFunction labelWithFrame:CGRectMake(15, 215, CGRectGetWidth(self.view.frame)-15*2, 22) textFont:[UIFont systemFontOfSize:16.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        [_backUpStatusCell.contentView addSubview:self.backUpStatusLabel];
        
        UIView *backUpProgressBackground = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMinX(self.backUpStatusLabel.frame), CGRectGetMaxY(self.backUpStatusLabel.frame)+10, CGRectGetWidth(self.backUpStatusLabel.frame), 4)];
        backUpProgressBackground.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        backUpProgressBackground.layer.masksToBounds = YES;
        [_backUpStatusCell.contentView addSubview:backUpProgressBackground];
        
        self.backUpStatusProgress = [[UIView alloc] initWithFrame:CGRectMake(-CGRectGetWidth(backUpProgressBackground.frame), 0, CGRectGetWidth(backUpProgressBackground.frame), CGRectGetHeight(backUpProgressBackground.frame))];
        self.backUpStatusProgress.backgroundColor = [CommonFunction colorWithString:@"6fcc31" alpha:1.0f];
        [backUpProgressBackground addSubview:self.backUpStatusProgress];
        
        self.backUpStatusRemain = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:14.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self refreshBackUpStatusRemain];
        [_backUpStatusCell.contentView addSubview:self.backUpStatusRemain];
        
        self.backUpStatusButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.backUpStatusButton addTarget:self action:@selector(backUpStatusContoller) forControlEvents:UIControlEventTouchUpInside];
        [self refreshBackUpStatusButton];
        [_backUpStatusCell.contentView addSubview:self.backUpStatusButton];
        
        self.backUpAssetFailedButton = [[UIButton alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(self.backUpStatusLabel.frame)+10, CGRectGetWidth(self.view.frame)-15-15, 20)];
        [self refreshBackUpAssetFailedButton];
        [self.backUpAssetFailedButton addTarget:self action:@selector(showBackUpFailedAlbum) forControlEvents:UIControlEventTouchUpInside];
        [_backUpStatusCell.contentView addSubview:self.backUpAssetFailedButton];
        
        [self refreshBackUpStatus];
    }
    return _backUpStatusCell;
}

- (void)refreshBackUpStatus {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.backUpTotalCount != 0) {
            self.backUpAssetDefaultImageView.hidden = YES;
            self.backUpAssetImageView.hidden = NO;
            self.backUpAssetFailedButton.hidden = YES;
            self.backUpStatusLabel.text = getLocalizedString(@"BackupStatusRunning", nil);
            self.backUpStatusProgress.hidden = NO;
            self.backUpStatusProgress.superview.hidden = NO;
            self.backUpStatusRemain.hidden = NO;
            self.backUpStatusButton.hidden = NO;
        } else {
            self.backUpAssetDefaultImageView.hidden = NO;
            self.backUpAssetImageView.hidden = YES;
            self.backUpAssetFailedButton.hidden = NO;
            self.backUpStatusLabel.text = getLocalizedString(@"BackupStatusComplete", nil);
            self.backUpStatusProgress.hidden = YES;
            self.backUpStatusProgress.superview.hidden = YES;
            self.backUpStatusRemain.hidden = YES;
            self.backUpStatusButton.hidden = YES;
            [self refreshBackUpAssetFailedButton];
        }
    });
}

- (void)refreshBackUpStatusImage {
    dispatch_async(dispatch_get_main_queue(), ^{
        Asset *asset = self.backUpOperation.backUpAsset;
        NSString *assetThumbanilPath = [asset assetThumbnailPath];
        UIImage *image = [UIImage imageWithContentsOfFile:assetThumbanilPath];
        [self.backUpAssetImageView setImage:image];
    });
}

- (void)refreshBackUpStatusRemain {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUInteger backupRemain = [self.backUpOperation backUpRemainCount];
        self.backUpStatusRemain.text = [NSString stringWithFormat:getLocalizedString(@"BackupRemainNotice", nil),backupRemain,self.backUpTotalCount];
        CGSize adjustRemainSize = [CommonFunction labelSizeWithLabel:self.backUpStatusRemain limitSize:CGSizeMake(1000, 1000)];
        self.backUpStatusRemain.frame = CGRectMake(CGRectGetMinX(self.backUpStatusLabel.frame), CGRectGetMaxY(self.backUpStatusLabel.frame)+10+4+8, adjustRemainSize.width, 20);
        
        [self refreshBackUpProgress];
    });
}

- (void)refreshBackUpProgress {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.backUpTotalCount == 0) {
            return;
        }
        NSUInteger backupRemain = [self.backUpOperation backUpRemainCount];
        CGRect pregressFrame = self.backUpStatusProgress.frame;
        pregressFrame.origin.x = - (pregressFrame.size.width - ((float)self.backUpTotalCount - (float)backupRemain)/(float)self.backUpTotalCount * pregressFrame.size.width);
        self.backUpStatusProgress.frame = pregressFrame;
    });
}

- (void)refreshBackUpStatusButton {
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectZero];
    if ([self.backUpOperation backUpPauseStatus]) {
        label.text = getLocalizedString(@"BackupStatusContinue", nil);
    } else {
        label.text = getLocalizedString(@"BackupStatusPause", nil);
    }
    label.font = [UIFont systemFontOfSize:14.0f];
    CGSize adjustLabelSize = [CommonFunction labelSizeWithLabel:label limitSize:CGSizeMake(1000, 1000)];
    self.backUpStatusButton.frame = CGRectMake(CGRectGetMaxX(self.backUpStatusLabel.frame)-adjustLabelSize.width, CGRectGetMaxY(self.backUpStatusLabel.frame)+10+4+8, adjustLabelSize.width, 20);
    [self.backUpStatusButton setAttributedTitle:[[NSAttributedString alloc] initWithString:label.text attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be8" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
}

- (void)refreshBackUpAssetFailedButton {
    NSArray *assetFailedArray = [Asset getAllFailedAsset];
    if (assetFailedArray.count > 0) {
        self.backUpAssetFailedButton.hidden = NO;
        [self.backUpAssetFailedButton setAttributedTitle:[[NSAttributedString alloc] initWithString:[NSString stringWithFormat:getLocalizedString(@"BackupFailedNotice", nil),assetFailedArray.count] attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"fc5043" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
    } else {
        self.backUpAssetFailedButton.hidden = YES;
    }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.backUpOperation) {
        if ([keyPath isEqualToString:@"backUpTotalCount"]) {
            self.backUpTotalCount = self.backUpOperation.backUpTotalCount;
            [self refreshBackUpStatus];
            [self refreshBackUpStatusRemain];
            [self refreshBackUpProgress];
        }
    }
}

- (void)backUpStatusContoller {
    [self.backUpOperation backUpControl];
    [self refreshBackUpStatusButton];
}

-(void)showBackUpFailedAlbum {
    CloudBackUpAlbumFailedController *cloudBackUpAlbumFailedController = [[CloudBackUpAlbumFailedController alloc] init];
    cloudBackUpAlbumFailedController.rootViewController = self;
    [self.navigationController pushViewController:cloudBackUpAlbumFailedController animated:YES];
}

#pragma mark BackUpSelect
- (UITableViewCell*)backUpSelectCell {
    if (!_backUpSelectCell) {
        _backUpSelectCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _backUpSelectCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _backUpSelectCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        UILabel *backUpSelectLable = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15*2, 22)];
        backUpSelectLable.text = getLocalizedString(@"BackupAlbumSelectTitle", nil);
        backUpSelectLable.font = [UIFont systemFontOfSize:18.0f];
        backUpSelectLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        backUpSelectLable.textAlignment = NSTextAlignmentLeft;
        [_backUpSelectCell addSubview:backUpSelectLable];
    }
    return _backUpSelectCell;
}

#pragma mark BackUpAuto
- (UITableViewCell*)backUpAutoCell {
    if (!_backUpAutoCell) {
        _backUpAutoCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _backUpAutoCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _backUpAutoCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        UISwitch *backUpAutoSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 51, 36)];
        [backUpAutoSwitch setOn:[UserSetting defaultSetting].cloudAssetBackupWifi.boolValue];
        backUpAutoSwitch.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(backUpAutoSwitch.frame), (44-CGRectGetHeight(backUpAutoSwitch.frame))/2, CGRectGetWidth(backUpAutoSwitch.frame), CGRectGetHeight(backUpAutoSwitch.frame));
        [_backUpAutoCell addSubview:backUpAutoSwitch];
        
        UILabel *backUpAutoLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetMinX(backUpAutoSwitch.frame)-10-15, 22)];
        backUpAutoLabel.text = getLocalizedString(@"BackupWIFINoticeTitle", nil);
        backUpAutoLabel.font = [UIFont systemFontOfSize:18.0f];
        backUpAutoLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        backUpAutoLabel.textAlignment = NSTextAlignmentLeft;
        [_backUpAutoCell addSubview:backUpAutoLabel];
    }
    return _backUpAutoCell;
}

- (void)assetBackupWifi:(UISwitch*)backupWifiSwitch {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.cloudAssetBackupWifi.boolValue) {
        userSetting.cloudAssetBackupWifi = @(0);
        [backupWifiSwitch setOn:NO animated:YES];
    } else {
        userSetting.cloudAssetBackupWifi = @(1);
        [backupWifiSwitch setOn:YES animated:YES];
    }
}

#pragma mark BackUpControl
- (UIView*)backUpControlView {
    if (!_backUpControlView) {
        _backUpControlView = [[UIView alloc] init];
        _backUpControlView.backgroundColor = [UIColor clearColor];
        
        UIButton *backUpAlbumPreviewButton = [[UIButton alloc] initWithFrame:CGRectMake(30, 22, CGRectGetWidth(self.view.frame)-30*2, 44)];
        backUpAlbumPreviewButton.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        backUpAlbumPreviewButton.layer.borderWidth = 0.5;
        backUpAlbumPreviewButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        backUpAlbumPreviewButton.layer.cornerRadius = 4;
        backUpAlbumPreviewButton.layer.masksToBounds = YES;
        UILabel *backUpAlbumPreviewLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, CGRectGetWidth(backUpAlbumPreviewButton.frame)-10*2, CGRectGetHeight(backUpAlbumPreviewButton.frame))];
        backUpAlbumPreviewLabel.text = getLocalizedString(@"BackupAlbumViewTitle", nil);
        backUpAlbumPreviewLabel.font = [UIFont systemFontOfSize:18.0f];
        backUpAlbumPreviewLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        backUpAlbumPreviewLabel.textAlignment = NSTextAlignmentCenter;
        [backUpAlbumPreviewButton addSubview:backUpAlbumPreviewLabel];
        [backUpAlbumPreviewButton addTarget:self action:@selector(assetBackupView) forControlEvents:UIControlEventTouchUpInside];
        [_backUpControlView addSubview:backUpAlbumPreviewButton];
        
        UIButton *backUpCloseButton = [[UIButton alloc] initWithFrame:CGRectMake(30, CGRectGetMaxY(backUpAlbumPreviewButton.frame)+10, CGRectGetWidth(self.view.frame)-30*2, 44)];
        backUpCloseButton.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        backUpCloseButton.layer.borderWidth = 0.5;
        backUpCloseButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        backUpCloseButton.layer.cornerRadius = 4;
        backUpCloseButton.layer.masksToBounds = YES;
        UILabel *backUpCloseLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, CGRectGetWidth(backUpCloseButton.frame)-10*2, CGRectGetHeight(backUpCloseButton.frame))];
        backUpCloseLabel.text = getLocalizedString(@"BackupAlbumCloseTitle", nil);
        backUpCloseLabel.font = [UIFont systemFontOfSize:18.0f];
        backUpCloseLabel.textColor = [CommonFunction colorWithString:@"fc5043" alpha:1.0f];
        backUpCloseLabel.textAlignment = NSTextAlignmentCenter;
        [backUpCloseButton addSubview:backUpCloseLabel];
        [backUpCloseButton addTarget:self action:@selector(assetBackupClose) forControlEvents:UIControlEventTouchUpInside];
        [_backUpControlView addSubview:backUpCloseButton];
        
        _backUpControlView.frame = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 22+CGRectGetHeight(backUpAlbumPreviewButton.frame)+10+CGRectGetHeight(backUpCloseButton.frame)+22);
    }
    return _backUpControlView;
}

- (void)assetBackupView {
    CloudBackUpAlbumPreviewController *albumPreviewController = [[CloudBackUpAlbumPreviewController alloc] init];
    [self.navigationController pushViewController:albumPreviewController animated:YES];
}

- (void)assetBackupClose {
    [UserSetting defaultSetting].cloudAssetBackupOpen = @(0);
    [self.navigationController popToViewController:(UIViewController*)self.settingViewController animated:YES];
}

#pragma mark TableView dataSource+delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 1;
    } else {
        return 2;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return 0.1f;
    } else {
        return 22.0f;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    if (section == 0) {
        return 0.1f;
    } else {
        return CGRectGetHeight(self.backUpControlView.frame);
    }
}

- (UIView*)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    if (section == 1) {
        return self.backUpControlView;
    }
    return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return 289;
    } else {
        return 44;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return self.backUpStatusCell;
    } else {
        if (indexPath.row == 0) {
            return self.backUpSelectCell;
        } else {
            return self.backUpAutoCell;
        }
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 1 && indexPath.row == 0) {
        CloudBackUpAlbumViewController *albumViewController = [[CloudBackUpAlbumViewController alloc] init];
        [self.navigationController pushViewController:albumViewController animated:YES];
    }
}

@end
