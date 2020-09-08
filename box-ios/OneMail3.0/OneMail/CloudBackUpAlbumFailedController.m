//
//  CloudBackUpAlbumFailedController.m
//  OneMail
//
//  Created by cse  on 16/3/29.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "CloudBackUpAlbumFailedController.h"
#import "CloudBackUpAssetFailedController.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "File+Remote.h"
#import "AssetBackUpFolder.h"
#import "AssetGroup.h"
#import "Asset.h"

@interface CloudBackUpAlbumFailedCollectionCell : UICollectionViewCell

@property (nonatomic, strong) UIImageView *backUpAlbumImageView;
@property (nonatomic, strong) UIImage     *backUpAlbumImage;
@property (nonatomic, strong) UILabel     *backUpAlbumNameLabel;
@property (nonatomic, strong) NSString    *backUpAlbumName;
@property (nonatomic, strong) UIButton    *backUpAlbumSelectButton;
@property (nonatomic, assign) BOOL         backUpAlbumSelect;

@property (nonatomic, strong) UICollectionView *collectionView;

@end

@implementation CloudBackUpAlbumFailedCollectionCell

- (void)setBackUpAlbumImage:(UIImage *)backUpAlbumImage {
    if (_backUpAlbumImage != backUpAlbumImage) {
        _backUpAlbumImage = backUpAlbumImage;
    };
    self.backUpAlbumImageView.image = backUpAlbumImage;
}

- (void)setBackUpAlbumName:(NSString *)backUpAlbumName {
    if (_backUpAlbumName != backUpAlbumName) {
        _backUpAlbumName = backUpAlbumName;
    }
    self.backUpAlbumNameLabel.text = backUpAlbumName;
}

- (void)setSelected {
    NSIndexPath *indexPath = [self.collectionView indexPathForCell:self];
    if (self.backUpAlbumSelectButton.isSelected) {
        self.backUpAlbumSelectButton.selected = NO;
        self.selected = NO;
        [self.collectionView deselectItemAtIndexPath:indexPath animated:YES];
    } else {
        self.backUpAlbumSelectButton.selected = YES;
        self.selected = YES;
        [self.collectionView selectItemAtIndexPath:indexPath animated:YES scrollPosition:UICollectionViewScrollPositionNone];
    }
}

- (void)reuse {
    if (!self.backUpAlbumImageView) {
        self.backUpAlbumImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.frame), CGRectGetWidth(self.frame))];
        self.backUpAlbumImageView.contentMode = UIViewContentModeScaleAspectFill;
        self.backUpAlbumImageView.clipsToBounds = YES;
        [self.contentView addSubview:self.backUpAlbumImageView];
    }
    if (!self.backUpAlbumNameLabel) {
        self.backUpAlbumNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(self.frame)-20, CGRectGetWidth(self.frame), 20)];
        self.backUpAlbumNameLabel.font = [UIFont systemFontOfSize:14.0f];
        self.backUpAlbumNameLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
        self.backUpAlbumNameLabel.textAlignment = NSTextAlignmentCenter;
        [self.contentView addSubview:self.backUpAlbumNameLabel];
    }
    if (!self.backUpAlbumSelectButton) {
        self.backUpAlbumSelectButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.frame)-30, 0, 30, 30)];
        self.backUpAlbumSelectButton.imageView.frame = CGRectMake(4, 4, 22, 22);
        [self.backUpAlbumSelectButton setImage:[UIImage imageNamed:@"ic_choice"] forState:UIControlStateNormal];
        [self.backUpAlbumSelectButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateSelected];
        [self.backUpAlbumSelectButton addTarget:self action:@selector(setSelected) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.backUpAlbumSelectButton];
    }
    self.backUpAlbumSelectButton.selected = NO;
    self.backUpAlbumImageView.image = nil;
    self.backUpAlbumNameLabel.text = nil;
}

@end

@interface CloudBackUpAlbumFailedController ()<UICollectionViewDataSource,UICollectionViewDelegate>

@property (nonatomic, strong) UILabel          *backUpTitleLabel;
@property (nonatomic, strong) UIButton         *backUpBackButton;
@property (nonatomic, strong) UIButton         *backUpConfirmButton;
@property (nonatomic, strong) NSMutableArray   *backUpFailedAlbums;
@property (nonatomic, strong) NSMutableArray   *backUpFailedGroup;
@property (nonatomic, strong) UICollectionView *backUpCollectionView;
@property (nonatomic, strong) NSMutableArray   *backUpFailedSelectedAlbums;

@property (nonatomic, strong) ALAssetsLibrary  *library;

@end

@implementation CloudBackUpAlbumFailedController
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
    
    self.backUpConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:getLocalizedString(@"Confirm", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
    self.backUpConfirmButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(self.backUpConfirmButton.frame), 0, CGRectGetWidth(self.backUpConfirmButton.frame), CGRectGetHeight(self.backUpConfirmButton.frame));
    [self.backUpConfirmButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"Confirm", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.backUpConfirmButton addTarget:self action:@selector(backUpConfirmButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.backUpFailedAlbums = [[NSMutableArray alloc] init];
    NSArray *assetFailedArray = [Asset getAllFailedAsset];
    for (Asset *asset in assetFailedArray) {
        AssetGroup *assetGroup = [AssetGroup searchGroupWithGroupKey:asset.assetAlbumKey];
        [self.backUpFailedAlbums addObject:assetGroup];
    }
    NSMutableArray *assetFailedAlbumsKey = [[NSMutableArray alloc] init];
    for (AssetGroup *assetGroup in self.backUpFailedAlbums) {
        [assetFailedAlbumsKey addObject:assetGroup.groupKey];
    }
    self.library = [[ALAssetsLibrary alloc] init];
    self.backUpFailedGroup = [[NSMutableArray alloc] init];
    self.backUpFailedSelectedAlbums = [[NSMutableArray alloc] init];
    
    UICollectionViewFlowLayout *collectionViewFlowLayout = [[UICollectionViewFlowLayout alloc] init];
    collectionViewFlowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
    collectionViewFlowLayout.minimumInteritemSpacing = 15;
    collectionViewFlowLayout.minimumLineSpacing = 17.5;
    CGFloat collectionCellWidth = (CGRectGetWidth(self.view.frame)-17.5*2-20*2)/3.0f;
    collectionViewFlowLayout.itemSize = CGSizeMake(collectionCellWidth, collectionCellWidth+10+20);
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarRect = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarRect = appDelegate.navigationController.navigationBar.frame;
    self.backUpCollectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:collectionViewFlowLayout];
    self.backUpCollectionView.delegate = self;
    self.backUpCollectionView.dataSource = self;
    [self.backUpCollectionView registerClass:[CloudBackUpAlbumFailedCollectionCell class] forCellWithReuseIdentifier:@"CloudBackUpAlbumFailedCollectionCell"];
    self.backUpCollectionView.showsVerticalScrollIndicator = NO;
    self.backUpCollectionView.backgroundColor = [UIColor clearColor];
    
    self.backUpCollectionView.frame = CGRectMake(20, statusBarRect.size.height+navigationBarRect.size.height+15, CGRectGetWidth(self.view.frame)-2*20, CGRectGetHeight(self.view.frame)-(statusBarRect.size.height+navigationBarRect.size.height+15));
    [self.view addSubview:self.backUpCollectionView];
    
    @autoreleasepool {
        [self.library enumerateGroupsWithTypes:ALAssetsGroupAll usingBlock:^(ALAssetsGroup *group, BOOL *stop) {
            if (group == nil) {
                [self.backUpCollectionView reloadData];
            } else {
                NSString *groupKey = [group valueForProperty:ALAssetsGroupPropertyPersistentID];
                if ([assetFailedAlbumsKey containsObject:groupKey]) {
                    [self.backUpFailedGroup addObject:group];
                }
            }
        } failureBlock:^(NSError *error) {
            if ([ALAssetsLibrary authorizationStatus] == ALAuthorizationStatusDenied) {
                NSString *errorMessage = getLocalizedString(@"BackupDeniedPrompt", nil);
                [[[UIAlertView alloc] initWithTitle:getLocalizedString(@"BackupDeniedTitle", nil) message:errorMessage delegate:nil cancelButtonTitle:getLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
            } else {
                NSString *errorMessage = [NSString stringWithFormat:@"Album Error: %@ - %@", [error localizedDescription], [error localizedRecoverySuggestion]];
                [[[UIAlertView alloc] initWithTitle:getLocalizedString(@"Error", nil) message:errorMessage delegate:nil cancelButtonTitle:getLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
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
    NSMutableArray *reBackUpAssetArray = [[NSMutableArray alloc] init];
    NSArray *selectedIndex = [self.backUpCollectionView indexPathsForSelectedItems];
    for (NSIndexPath *path in selectedIndex) {
        ALAssetsGroup *group = [self.backUpFailedGroup objectAtIndex:path.row];
        NSString *groupKey = [group valueForProperty:ALAssetsGroupPropertyPersistentID];
        NSArray *assetFailedArray = [Asset getFailedAssetWithAlbumKey:groupKey];
        [reBackUpAssetArray addObjectsFromArray:assetFailedArray];
    }
    for (Asset *asset in reBackUpAssetArray) {
        [asset reBackUp];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)reloadTableView {
    [self.backUpCollectionView reloadData];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.backUpFailedGroup.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudBackUpAlbumFailedCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"CloudBackUpAlbumFailedCollectionCell" forIndexPath:indexPath];
    [cell reuse];
    cell.collectionView = self.backUpCollectionView;
    ALAssetsGroup *group = [self.backUpFailedGroup objectAtIndex:indexPath.row];
    NSString *groupKey = [group valueForProperty:ALAssetsGroupPropertyPersistentID];
    NSArray *assetFailedArray = [Asset getFailedAssetWithAlbumKey:groupKey];
    cell.backUpAlbumImage = [UIImage imageWithCGImage:[group posterImage]];
    cell.backUpAlbumName = [NSString stringWithFormat:@"%@ (%ld)",[group valueForProperty:ALAssetsGroupPropertyName], (long)assetFailedArray.count];
    if (cell.isSelected) {
        cell.backUpAlbumSelectButton.selected = YES;
    } else {
        cell.backUpAlbumSelectButton.selected = NO;
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    ALAssetsGroup *group = [self.backUpFailedGroup objectAtIndex:indexPath.row];
    CloudBackUpAssetFailedController *assetFailedController = [[CloudBackUpAssetFailedController alloc] initWithAssetsGroup:group];
    assetFailedController.rootViewController = self.rootViewController;
    [self.navigationController pushViewController:assetFailedController animated:YES];
}


@end
