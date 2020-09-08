//
//  CloudBackupOpenViewController.m
//  OneMail
//
//  Created by cse  on 15/12/13.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudBackupOpenViewController.h"
#import "AppDelegate.h"
#import "CloudBackUpViewController.h"
#import "AssetBackUpFolder.h"
#import "UIView+Toast.h"

@interface CloudBackupOpenViewController ()

@end

@implementation CloudBackupOpenViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = getLocalizedString(@"BackupTitle", nil);
    self.view.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake((CGRectGetWidth(self.view.frame)-300)/2, statusBarFrame.size.height+navigationBarFrame.size.height+35, 300, 160)];
    imageView.image = [UIImage imageNamed:@"photo_album_backup"];
    [self.view addSubview:imageView];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    titleLabel.text = getLocalizedString(@"BackupDescription", nil);
    titleLabel.font = [UIFont systemFontOfSize:14.0f];
    titleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.numberOfLines = 0;
    CGSize adjustSize = [CommonFunction labelSizeWithLabel:titleLabel limitSize:CGSizeMake(CGRectGetWidth(self.view.frame)-15-15, 1000)];
    titleLabel.frame = CGRectMake(15, CGRectGetMaxY(imageView.frame), CGRectGetWidth(self.view.frame)-15-15, MAX(adjustSize.height, 20));
    [self.view addSubview:titleLabel];
    
    UIButton *enableButton = [[UIButton alloc] initWithFrame:CGRectMake(30, CGRectGetMaxY(titleLabel.frame)+30, CGRectGetWidth(self.view.frame)-30-30, 44)];
    enableButton.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
    enableButton.layer.cornerRadius = 4;
    enableButton.layer.masksToBounds = YES;
    [enableButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"BackupAlbumOpenTitle", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:18.0f]}] forState:UIControlStateNormal];
    [enableButton addTarget:self action:@selector(assetBackupOpen) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:enableButton];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)assetBackupOpen {
    AssetBackUpFolder *assetBackUpFolder = [[AssetBackUpFolder alloc] init];
    assetBackUpFolder.completionBlock = ^(File *assetFolder){
        [UserSetting defaultSetting].cloudAssetBackupOpen = @(1);
        CloudBackUpViewController *backupView = [[CloudBackUpViewController alloc] init];
        backupView.settingViewController = self.settingViewController;
        [self.navigationController pushViewController:backupView animated:YES];
    };
    assetBackUpFolder.failedBlock = ^(){
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"创建相册备份文件夹失败", nil)];
        });
    };
    [assetBackUpFolder cheakAssetFolderWithKey:nil name:@"BackUp"];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
