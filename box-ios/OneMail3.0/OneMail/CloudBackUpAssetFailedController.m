//
//  CloudBackUpAssetFailedController.m
//  OneMail
//
//  Created by cse  on 16/3/29.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "CloudBackUpAssetFailedController.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import "AppDelegate.h"
#import "File+Remote.h"
#import "FileMultiOperation.h"
#import "UIAlertView+Blocks.h"
#import "Asset.h"

@interface CloudBackUpAssetFailedCollectionCell : UICollectionViewCell

@property (nonatomic, strong) ALAsset     *asset;
@property (nonatomic, strong) UIImageView *assetImageView;
@property (nonatomic, strong) UIButton    *assetSelectButton;

@property (nonatomic, strong) UICollectionView *assetCollectionView;

@end

@implementation CloudBackUpAssetFailedCollectionCell

- (void)setAsset:(ALAsset *)asset {
    if (_asset != asset) {
        _asset = asset;
    }
    self.assetImageView.image = [UIImage imageWithCGImage:self.asset.thumbnail];
}

- (void)setSelected {
    NSIndexPath *indexPath = [self.assetCollectionView indexPathForCell:self];
    if (self.assetSelectButton.isSelected) {
        self.assetSelectButton.selected = NO;
        self.selected = NO;
        [self.assetCollectionView deselectItemAtIndexPath:indexPath animated:YES];
    } else {
        self.assetSelectButton.selected = YES;
        self.selected = YES;
        [self.assetCollectionView selectItemAtIndexPath:indexPath animated:YES scrollPosition:UICollectionViewScrollPositionNone];
    }
}

- (void)reuse {
    if (!self.assetImageView) {
        self.assetImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.frame),CGRectGetHeight(self.frame))];
        self.assetImageView.contentMode = UIViewContentModeScaleAspectFill;
        self.assetImageView.clipsToBounds = YES;
        [self.contentView addSubview:self.assetImageView];
    }
    if (!self.assetSelectButton) {
        self.assetSelectButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.frame)-30, 0, 30, 30)];
        self.assetSelectButton.imageView.frame = CGRectMake(4, 4, 22, 22);
        [self.assetSelectButton setImage:[UIImage imageNamed:@"ic_choice"] forState:UIControlStateNormal];
        [self.assetSelectButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateSelected];
        [self.assetSelectButton addTarget:self action:@selector(setSelected) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.assetSelectButton];
    }
    self.assetSelectButton.selected = NO;
    self.assetImageView.image = nil;
}
@end

@interface CloudBackUpAssetFailedController ()<UICollectionViewDataSource,UICollectionViewDelegate>

@property (nonatomic, strong) ALAssetsGroup    *assetsGroup;
@property (nonatomic, strong) UILabel          *backUpTitleLabel;
@property (nonatomic, strong) UIButton         *backUpBackButton;
@property (nonatomic, strong) UIButton         *backUpConfirmButton;
@property (nonatomic, strong) UICollectionView *backUpCollectionView;
@property (nonatomic, strong) NSMutableArray   *backUpFailedAssetArray;
@property (nonatomic, strong) NSMutableArray   *backUpSelectAssetArray;

@end

@implementation CloudBackUpAssetFailedController

- (id)initWithAssetsGroup:(ALAssetsGroup *)assetsGroup {
    self = [super init];
    if (self) {
        self.assetsGroup = assetsGroup;
    }
    return self;
}

- (void)viewDidLoad {
    
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.backUpTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.backUpTitleLabel.text = getLocalizedString(@"BackupAlbumSelectTitle", nil);
    
    self.backUpBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.backUpBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.backUpBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.backUpBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.backUpBackButton addTarget:self action:@selector(backUpBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.backUpConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:getLocalizedString(@"Backup", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
    self.backUpConfirmButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(self.backUpConfirmButton.frame), 0, CGRectGetWidth(self.backUpConfirmButton.frame), CGRectGetHeight(self.backUpConfirmButton.frame));
    [self.backUpConfirmButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"Backup", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.backUpConfirmButton addTarget:self action:@selector(backUpConfirmButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    UICollectionViewFlowLayout *collectionViewFlowLayout = [[UICollectionViewFlowLayout alloc] init];
    collectionViewFlowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
    collectionViewFlowLayout.minimumInteritemSpacing = 5;
    collectionViewFlowLayout.minimumLineSpacing = 5;
    CGFloat collectionCellWidth = (CGRectGetWidth(self.view.frame)-5*5)/4.0f;
    collectionViewFlowLayout.itemSize = CGSizeMake(collectionCellWidth, collectionCellWidth);
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarRect = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarRect = appDelegate.navigationController.navigationBar.frame;
    self.backUpCollectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:collectionViewFlowLayout];
    self.backUpCollectionView.delegate = self;
    self.backUpCollectionView.dataSource = self;
    [self.backUpCollectionView registerClass:[CloudBackUpAssetFailedCollectionCell class] forCellWithReuseIdentifier:@"CloudBackUpAssetFailedCollectionCell"];
    self.backUpCollectionView.showsVerticalScrollIndicator = NO;
    self.backUpCollectionView.backgroundColor = [UIColor clearColor];
    self.backUpCollectionView.allowsSelection = YES;
    self.backUpCollectionView.allowsMultipleSelection = YES;
    
    self.backUpCollectionView.frame = CGRectMake(5, statusBarRect.size.height+navigationBarRect.size.height+5, CGRectGetWidth(self.view.frame)-2*5, CGRectGetHeight(self.view.frame)-(statusBarRect.size.height+navigationBarRect.size.height+5));
    [self.view addSubview:self.backUpCollectionView];
    
    NSArray *failedAssets = [Asset getFailedAssetWithAlbumKey:[self.assetsGroup valueForProperty:ALAssetsGroupPropertyPersistentID]];
    NSMutableArray *failedAssetsName = [[NSMutableArray alloc] init];
    for (Asset *asset in failedAssets) {
        [failedAssetsName addObject:asset.assetName];
    }
    self.backUpFailedAssetArray = [[NSMutableArray alloc] init];
    self.backUpSelectAssetArray = [[NSMutableArray alloc] init];
    @autoreleasepool {
        [self.assetsGroup enumerateAssetsUsingBlock:^(ALAsset *result, NSUInteger index, BOOL *stop) {
            if (result == nil) {
                [self.backUpCollectionView reloadData];
            } else {
                ALAssetRepresentation *representation = [result defaultRepresentation];
                NSString *assetName = [representation filename];
                if ([failedAssetsName containsObject:assetName]) {
                    [self.backUpFailedAssetArray addObject:result];
                }
            }
        }];
    }
}


- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.backUpTitleLabel];
    [self.navigationController.navigationBar addSubview:self.backUpBackButton];
    [self.navigationController.navigationBar addSubview:self.backUpConfirmButton];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadTableView) name:ALAssetsLibraryChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
    [self.backUpCollectionView reloadData];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.backUpTitleLabel removeFromSuperview];
    [self.backUpBackButton removeFromSuperview];
    [self.backUpConfirmButton removeFromSuperview];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:ALAssetsLibraryChangedNotification object:nil];
}

- (void)backUpBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)backUpConfirmButtonClick {
    NSArray *failedAssets = [Asset getFailedAssetWithAlbumKey:[self.assetsGroup valueForProperty:ALAssetsGroupPropertyPersistentID]];
    NSMutableArray *reBackUpAssetArray = [[NSMutableArray alloc] init];
    NSMutableArray *reBackUpAssetNameArray = [[NSMutableArray alloc] init];
    NSArray *selectedIndex = [self.backUpCollectionView indexPathsForSelectedItems];
    for (NSIndexPath *path in selectedIndex) {
        ALAsset *asset = [self.backUpFailedAssetArray objectAtIndex:path.row];
        ALAssetRepresentation *representation = [asset defaultRepresentation];
        [reBackUpAssetNameArray addObject:representation.filename];
    }
    for (Asset *asset in failedAssets) {
        if ([reBackUpAssetNameArray containsObject:asset.assetName]) {
            [reBackUpAssetArray addObject:asset];
        }
    }
    for (Asset *asset in reBackUpAssetArray) {
        [asset reBackUp];
    }
    [self.navigationController popToViewController:self.rootViewController animated:YES];
}


- (void)reloadTableView {
    [self.backUpCollectionView reloadData];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.backUpFailedAssetArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudBackUpAssetFailedCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"CloudBackUpAssetFailedCollectionCell" forIndexPath:indexPath];
    [cell reuse];
    cell.assetCollectionView = self.backUpCollectionView;
    cell.asset = [self.backUpFailedAssetArray objectAtIndex:indexPath.row];
    if (cell.isSelected) {
        cell.assetSelectButton.selected = YES;
    } else {
        cell.assetSelectButton.selected = NO;
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {

}


@end
