//
//  CloudAssetViewController.m
//  OneMail
//
//  Created by cse  on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudAssetViewController.h"
#import "AppDelegate.h"
#import "CloudAssetCollectionCell.h"
#import "File.h"
#import "File+Remote.h"
#import "TransportTask.h"
#import "CloudTransferViewController.h"
#import "FileMultiOperation.h"
#import "TransportLocalAttachmentUploadTaskHandle.h"
#import "AttachmentBackupFolder.h"
#import "CloudUploadTargetFolderViewController.h"
#import "UIAlertView+Blocks.h"
#import "CloudFileViewController.h"
#import "MJRefresh.h"


@interface CloudAssetViewController ()<UITableViewDataSource,UITableViewDelegate,UICollectionViewDataSource,UICollectionViewDelegate>

@property (nonatomic, strong) UILabel          *cloudAssetTitleLabel;
@property (nonatomic, strong) UIButton         *cloudAssetCancelButton;
@property (nonatomic, strong) UIButton         *cloudAssetConfirmButton;

@property (nonatomic, strong) UITableViewCell  *cloudAssetCollectionCell;
@property (nonatomic, strong) UICollectionView *cloudAssetCollectionView;

@property (nonatomic, strong) UITableViewCell  *cloudAssetUploadTargetCell;
@property (nonatomic, strong) UIButton         *cloudAssetUploadTargetButton;
@property (nonatomic, strong) UILabel          *cloudAssetUploadTargetLabel;

@property (nonatomic, strong) UITableViewCell  *cloudAssetUploadControlCell;
@property (nonatomic, strong) UIButton         *cloudAssetUploadConfirmButton;
@property (nonatomic, strong) UILabel          *cloudAssetUploadConfirmLabel;

@property (nonatomic, strong) NSMutableArray  *cloudAssetArray;
@property (nonatomic, strong) NSMutableArray  *cloudAssetSelectedArray;
@property (nonatomic, assign) BOOL            cloudAssetSelectedAll;

@property (nonatomic, strong) NSMutableArray  *uploadTargetFolderIdArray;
@property (nonatomic, strong) NSMutableArray  *uploadTargetFolderOwnerArray;

@end
@implementation CloudAssetViewController

- (void)setAssetsGroup:(ALAssetsGroup *)assetsGroup {
    if (_assetsGroup!=assetsGroup) {
        _assetsGroup = assetsGroup;
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.cloudAssetTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.cloudAssetTitleLabel.font = [UIFont systemFontOfSize:18.0f];
    self.cloudAssetTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.cloudAssetTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.cloudAssetTitleLabel.text = getLocalizedString(@"CloudUploadTitle", nil);
    
    self.cloudAssetCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.cloudAssetCancelButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudAssetCancelButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.cloudAssetCancelButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.cloudAssetCancelButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    
    CGSize adjustLabelSize = [CommonFunction labelSizeWithString:getLocalizedString(@"SelectAll", nil) font:[UIFont systemFontOfSize:17.0f]];
    self.cloudAssetConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-adjustLabelSize.width, 0, adjustLabelSize.width, 44)];
    [self.cloudAssetConfirmButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"SelectAll", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.cloudAssetConfirmButton addTarget:self action:@selector(assetsSelectAll) forControlEvents:UIControlEventTouchUpInside];
    
    UITableView *assetsTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    [assetsTableView setDataSource:self];
    [assetsTableView setDelegate:self];
    [assetsTableView setSeparatorStyle:UITableViewCellSeparatorStyleSingleLine];
    [assetsTableView setSeparatorInset:UIEdgeInsetsZero];
    [assetsTableView setSeparatorColor:[CommonFunction colorWithString:@"d9d9d9" alpha:1.0f]];
    [assetsTableView setScrollEnabled:NO];
    [assetsTableView setFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame))];
    [self.view addSubview:assetsTableView];
    [assetsTableView reloadData];
    
    self.cloudAssetArray = [[NSMutableArray alloc] init];
    self.cloudAssetSelectedArray = [[NSMutableArray alloc] init];
    self.uploadTargetFolderIdArray = [[NSMutableArray alloc] init];
    self.uploadTargetFolderOwnerArray = [[NSMutableArray alloc] init];
    self.cloudAssetSelectedAll = NO;
    
    @autoreleasepool {
        [self.assetsGroup enumerateAssetsUsingBlock:^(ALAsset *result, NSUInteger index, BOOL *stop) {
            if (result == nil) {
                [self.cloudAssetCollectionView reloadData];
            } else {
                [self.cloudAssetArray addObject:result];
            }
        }];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.cloudAssetTitleLabel];
    [self.navigationController.navigationBar addSubview:self.cloudAssetCancelButton];
    [self.navigationController.navigationBar addSubview:self.cloudAssetConfirmButton];

    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [self.cloudAssetTitleLabel removeFromSuperview];
    [self.cloudAssetCancelButton removeFromSuperview];
    [self.cloudAssetConfirmButton removeFromSuperview];
    
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)cancel {
    [self.navigationController popToViewController:self.rootViewController animated:YES];
}

- (void)confirm {
    if (self.cloudAssetSelectedArray.count == 0) {
        [self cancel];return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    BOOL hasNetWork = appDelegate.hasNetwork;
    if (!hasNetWork) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudNoneNetworkPrompt", nil)];
            [self.navigationController popToViewController:self.rootViewController animated:YES];
        });
    } else {
        BOOL WiFiNetWork = appDelegate.wifiNetwork;
        UserSetting *userSetting = [UserSetting defaultSetting];
        if (!WiFiNetWork && userSetting.cloudWiFiPrompt.integerValue == 1) {
            [UIAlertView showAlertViewWithTitle:nil message:getLocalizedString(@"CloudUploadWIFIPrompt", nil) cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                [self doUploadingWithForce:YES];
            } onCancel:^{
                [self doUploadingWithForce:NO];
            }];
        } else {
            [self doUploadingWithForce:YES];
        }
    }
}

- (void)doUploadingWithForce:(BOOL)force {
    File *rootFile = [File getFileWithFileId:self.uploadTargetFolderId fileOwner:self.uploadTargetFolderOwner];
//    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        for (ALAsset *asset in self.cloudAssetSelectedArray) {
            [rootFile uploadAsset:asset force:YES];
        }
//    });
    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudUploadAddSuccessPrompt", nil)];
    [self.navigationController popToViewController:self.rootViewController animated:YES];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.popUpLayer" object:nil];
}


- (UITableViewCell*)cloudAssetCollectionCell {
    if (!_cloudAssetCollectionCell) {
        UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
        
        UICollectionViewFlowLayout *assetsCollectionViewFlowLayout = [[UICollectionViewFlowLayout alloc] init];
        assetsCollectionViewFlowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
        assetsCollectionViewFlowLayout.minimumInteritemSpacing = 5;
        assetsCollectionViewFlowLayout.minimumLineSpacing = 5;
        CGFloat assetCollectionCellWidth = (CGRectGetWidth(self.view.frame)-5*5)/4.0f;
        assetsCollectionViewFlowLayout.itemSize = CGSizeMake(assetCollectionCellWidth, assetCollectionCellWidth);
        
        self.cloudAssetCollectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:assetsCollectionViewFlowLayout];
        self.cloudAssetCollectionView.delegate = self;
        self.cloudAssetCollectionView.dataSource = self;
        [self.cloudAssetCollectionView registerClass:[CloudAssetCollectionCell class] forCellWithReuseIdentifier:@"CloudAssetCollectionCell"];
        self.cloudAssetCollectionView.showsVerticalScrollIndicator = NO;
        self.cloudAssetCollectionView.backgroundColor = [UIColor clearColor];
        
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
        self.cloudAssetCollectionView.frame = CGRectMake(5, 5, CGRectGetWidth(self.view.frame)-2*5, CGRectGetHeight(self.view.frame)-29-49-statusBarFrame.size.height-navigationFrame.size.height-5);
        [cell.contentView addSubview:self.cloudAssetCollectionView];
        
        _cloudAssetCollectionCell = cell;
    }
    return _cloudAssetCollectionCell;
}

- (UITableViewCell*)cloudAssetUploadTargetCell {
    if (!_cloudAssetUploadTargetCell) {
        UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
        
        UIView *uploadTargetView = [[UIView alloc] initWithFrame:CGRectMake(15, 0, CGRectGetWidth(self.view.frame)-15-15, 29)];
        UILabel *uploadTargetLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        uploadTargetLabel.font = [UIFont systemFontOfSize:12.0f];
        uploadTargetLabel.text = getLocalizedString(@"CloudUploadTo", nil);
        uploadTargetLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        uploadTargetLabel.textAlignment = NSTextAlignmentLeft;
        CGSize labelAdjustSize = [CommonFunction labelSizeWithLabel:uploadTargetLabel limitSize:CGSizeMake(1000, 1000)];
        uploadTargetLabel.frame = CGRectMake(0, (29-20)/2, ceil(labelAdjustSize.width), 20);
        [uploadTargetView addSubview:uploadTargetLabel];
        
        self.cloudAssetUploadTargetButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(uploadTargetLabel.frame), CGRectGetMinY(uploadTargetLabel.frame), CGRectGetWidth(uploadTargetView.frame)-CGRectGetWidth(uploadTargetLabel.frame), 20)];
        self.cloudAssetUploadTargetLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.cloudAssetUploadTargetButton.frame), CGRectGetHeight(self.cloudAssetUploadTargetButton.frame))];
        self.cloudAssetUploadTargetLabel.font = [UIFont systemFontOfSize:12.0f];
        self.cloudAssetUploadTargetLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0f];
        self.cloudAssetUploadTargetLabel.textAlignment = NSTextAlignmentLeft;
        self.cloudAssetUploadTargetLabel.text = [self uploadTargetDestination];
        [self.cloudAssetUploadTargetButton addSubview:self.cloudAssetUploadTargetLabel];
        [self.cloudAssetUploadTargetButton addTarget:self action:@selector(uploadTargetChange) forControlEvents:UIControlEventTouchUpInside];
        [uploadTargetView addSubview:self.cloudAssetUploadTargetButton];
        [cell.contentView addSubview:uploadTargetView];
        
        _cloudAssetUploadTargetCell = cell;
    }
    return _cloudAssetUploadTargetCell;
}

- (NSString*)uploadTargetDestination {
    NSString *destination = [[NSString alloc] init];
    NSString *folderId = self.uploadTargetFolderId;
    NSString *folderOwner = self.uploadTargetFolderOwner;
    [self.uploadTargetFolderIdArray removeAllObjects];
    [self.uploadTargetFolderOwnerArray removeAllObjects];
    do {
        [self.uploadTargetFolderIdArray insertObject:folderId atIndex:0];
        [self.uploadTargetFolderOwnerArray insertObject:folderOwner atIndex:0];
        File *file = [File getFileWithFileId:folderId fileOwner:folderOwner];
        destination = [file.fileName stringByAppendingPathComponent:destination];
        folderId = file.fileParent;
        folderOwner = file.parent.fileOwner;
    } while (folderId);
    return destination;
}

- (void)uploadTargetChange {
    CloudUploadTargetFolderConfirm cloudUploadTargetFolderConfirm = ^(NSString *uploadTargetFolderPath, NSString *uploadTargetFolderId, NSString *uploadTargetFolderOwner){
        self.uploadTargetFolderId = uploadTargetFolderId;
        self.uploadTargetFolderOwner = uploadTargetFolderOwner;
        self.cloudAssetUploadTargetLabel.text = [self uploadTargetDestination];
        [self.navigationController popToViewController:self animated:YES];
    };
    
    for (NSString *fileId in self.uploadTargetFolderIdArray) {
        NSUInteger index = [self.uploadTargetFolderIdArray indexOfObject:fileId];
        if (index+1 == self.uploadTargetFolderIdArray.count) {
            break;
        }
        NSString *fileOwner = [self.uploadTargetFolderOwnerArray objectAtIndex:index];
        File *file = [File getFileWithFileId:fileId fileOwner:fileOwner];
        CloudUploadTargetFolderViewController *uploadTargetFolderViewController = [[CloudUploadTargetFolderViewController alloc] initWithFile:file uploadTargetFolderPath:@"/"];
        uploadTargetFolderViewController.cloudUploadTargetFolderConfirm = cloudUploadTargetFolderConfirm;
        uploadTargetFolderViewController.rootViewController = self;
        [self.navigationController pushViewController:uploadTargetFolderViewController animated:NO];
    }
    NSString *lastFolderId = [self.uploadTargetFolderIdArray lastObject];
    NSString *lastFolderOwner = [self.uploadTargetFolderOwnerArray lastObject];
    File *file = [File getFileWithFileId:lastFolderId fileOwner:lastFolderOwner];
    CloudUploadTargetFolderViewController *uploadTargetFolderViewController = [[CloudUploadTargetFolderViewController alloc] initWithFile:file uploadTargetFolderPath:@"/"];
    uploadTargetFolderViewController.cloudUploadTargetFolderConfirm = cloudUploadTargetFolderConfirm;
    uploadTargetFolderViewController.rootViewController = self;
    [self.navigationController pushViewController:uploadTargetFolderViewController animated:YES];
}

- (UITableViewCell*)cloudAssetUploadControlCell {
    if (!_cloudAssetUploadControlCell) {
        UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
        
        UIButton *cloudAssetUploadCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame)/2, 49)];
        UILabel *cloudAssetUploadCancelLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 0, CGRectGetWidth(cloudAssetUploadCancelButton.frame)-15, CGRectGetHeight(cloudAssetUploadCancelButton.frame))];
        cloudAssetUploadCancelLabel.font = [UIFont systemFontOfSize:16.0f];
        cloudAssetUploadCancelLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0f];
        cloudAssetUploadCancelLabel.textAlignment = NSTextAlignmentLeft;
        cloudAssetUploadCancelLabel.text = getLocalizedString(@"Cancel",nil);
        [cloudAssetUploadCancelButton addSubview:cloudAssetUploadCancelLabel];
        [cloudAssetUploadCancelButton addTarget:self action:@selector(cancel) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:cloudAssetUploadCancelButton];
        
        self.cloudAssetUploadConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetMaxX(cloudAssetUploadCancelButton.frame), 0, CGRectGetWidth(self.view.frame)/2, 49)];
        self.cloudAssetUploadConfirmLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.cloudAssetUploadConfirmButton.frame)-15, CGRectGetHeight(self.cloudAssetUploadConfirmButton.frame))];
        self.cloudAssetUploadConfirmLabel.font = [UIFont boldSystemFontOfSize:16.0f];
        self.cloudAssetUploadConfirmLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0f];
        self.cloudAssetUploadConfirmLabel.textAlignment = NSTextAlignmentRight;
        self.cloudAssetUploadConfirmLabel.text = getLocalizedString(@"Confirm",nil);
        [self.cloudAssetUploadConfirmButton addSubview:self.cloudAssetUploadConfirmLabel];
        [self.cloudAssetUploadConfirmButton addTarget:self action:@selector(confirm) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:self.cloudAssetUploadConfirmButton];
        
        _cloudAssetUploadControlCell = cell;
    }
    return _cloudAssetUploadControlCell;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.cloudAssetArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudAssetCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"CloudAssetCollectionCell" forIndexPath:indexPath];
    [cell reuse];
    cell.asset = [self.cloudAssetArray objectAtIndex:indexPath.row];
    if ([self.cloudAssetSelectedArray containsObject:cell.asset]) {
        cell.assetSelect = YES;
    } else {
        cell.assetSelect = NO;
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudAssetCollectionCell *cell = (CloudAssetCollectionCell*)[collectionView cellForItemAtIndexPath:indexPath];
    if (cell.assetSelect) {
        cell.assetSelect = NO;
        if ([self.cloudAssetSelectedArray containsObject:cell.asset]) {
            [self.cloudAssetSelectedArray removeObject:cell.asset];
        }
    } else {
        cell.assetSelect = YES;
        if (![self.cloudAssetSelectedArray containsObject:cell.asset]) {
            [self.cloudAssetSelectedArray addObject:cell.asset];
        }
    }
    self.cloudAssetUploadConfirmLabel.text = [NSString stringWithFormat:getLocalizedString(@"CloudUploadNumButton", nil),(unsigned long)self.cloudAssetSelectedArray.count];
}

- (void)assetsSelectAll {
    [self.cloudAssetSelectedArray removeAllObjects];
    if (self.cloudAssetSelectedAll) {
        self.cloudAssetSelectedAll = NO;
        self.cloudAssetUploadConfirmLabel.text = getLocalizedString(@"CloudUploadButton", nil);
    } else {
        self.cloudAssetSelectedAll = YES;
        [self.cloudAssetSelectedArray addObjectsFromArray:self.cloudAssetArray];
        self.cloudAssetUploadConfirmLabel.text = [NSString stringWithFormat:getLocalizedString(@"CloudUploadNumButton", nil),(unsigned long)self.cloudAssetSelectedArray.count];
    }
    [self.cloudAssetCollectionView reloadData];
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 3;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
        return CGRectGetHeight(tableView.frame)-29-49-statusBarFrame.size.height-navigationFrame.size.height;
    } else if (indexPath.row == 1) {
        return 29;
    } else if (indexPath.row == 2) {
        return 49;
    } else {
        return 0.0f;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        return self.cloudAssetCollectionCell;
    } else if (indexPath.row == 1) {
        return self.cloudAssetUploadTargetCell;
    } else {
        return self.cloudAssetUploadControlCell;
    }
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        [cell setSeparatorInset:UIEdgeInsetsZero];
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

@end
