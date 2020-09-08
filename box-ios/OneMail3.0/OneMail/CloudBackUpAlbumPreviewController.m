//
//  CloudBackUpAlbumPreviewController.m
//  OneMail
//
//  Created by cse  on 16/1/26.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "CloudBackUpAlbumPreviewController.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "File+Remote.h"
#import "FileThumbnail.h"
#import "AssetBackUpFolder.h"
#import "AssetGroup.h"
#import "Asset.h"
#import "CloudBackUpAssetPreviewController.h"
@interface CloudBackUpAlbumPreviewCell : UICollectionViewCell

@property (nonatomic, strong) File        *backUpFolder;
@property (nonatomic, strong) UIImage     *backUpAlbumImage;
@property (nonatomic, strong) NSString    *backUpAlbumName;
@property (nonatomic, strong) UIImageView *backUpAlbumImageView;
@property (nonatomic, strong) UILabel     *backUpAlbumNameLabel;

@end


@implementation CloudBackUpAlbumPreviewCell

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
    self.backUpAlbumImageView.image = nil;
    self.backUpAlbumNameLabel.text = nil;
}

@end

@interface CloudBackUpAlbumPreviewController ()<UICollectionViewDataSource,UICollectionViewDelegate>

@property (nonatomic, strong) UILabel          *backUpTitleLabel;
@property (nonatomic, strong) UIButton         *backUpBackButton;
@property (nonatomic, strong) UICollectionView *backUpCollectionView;
@property (nonatomic, strong) NSArray          *backUpFolderArray;

@end

@implementation CloudBackUpAlbumPreviewController
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.backUpTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.backUpTitleLabel.text = getLocalizedString(@"BackupAlbumPreviewTitle", nil);
    
    self.backUpBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.backUpBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.backUpBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.backUpBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.backUpBackButton addTarget:self action:@selector(backUpBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
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
    [self.backUpCollectionView registerClass:[CloudBackUpAlbumPreviewCell class] forCellWithReuseIdentifier:@"CloudBackUpAlbumPreviewCell"];
    self.backUpCollectionView.showsVerticalScrollIndicator = NO;
    self.backUpCollectionView.backgroundColor = [UIColor clearColor];
    
    self.backUpCollectionView.frame = CGRectMake(20, statusBarRect.size.height+navigationBarRect.size.height+15, CGRectGetWidth(self.view.frame)-2*20, CGRectGetHeight(self.view.frame)-(statusBarRect.size.height+navigationBarRect.size.height+15));
    [self.view addSubview:self.backUpCollectionView];
    
    self.backUpFolderArray = nil;
    File *file = [File assetMainFolder];
    [file folderRansack:^(id retobj) {
        self.backUpFolderArray = [file subFolderItems];
        [self reloadTableView];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        self.backUpFolderArray = [file subFolderItems];
        [self reloadTableView];
    }];
}


- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.backUpTitleLabel];
    [self.navigationController.navigationBar addSubview:self.backUpBackButton];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadTableView) name:ALAssetsLibraryChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.backUpTitleLabel removeFromSuperview];
    [self.backUpBackButton removeFromSuperview];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:ALAssetsLibraryChangedNotification object:nil];
}

- (void)reloadTableView {
    [self.backUpCollectionView reloadData];
}

- (void)backUpBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.backUpFolderArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudBackUpAlbumPreviewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"CloudBackUpAlbumPreviewCell" forIndexPath:indexPath];
    [cell reuse];
    File *file = [self.backUpFolderArray objectAtIndex:indexPath.row];
    cell.backUpFolder = file;
    cell.backUpAlbumName = [NSString stringWithFormat:@"%@ (%ld)",file.fileName, (long)[file.subFileItems count]];
    File *firstImageFile = [self headerImageFileWithFolder:file];
    [FileThumbnail headerImageWithFolder:firstImageFile imageView:cell.backUpAlbumImageView];
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    File *file = [self.backUpFolderArray objectAtIndex:indexPath.row];
    CloudBackUpAssetPreviewController *assetPreviewController = [[CloudBackUpAssetPreviewController alloc] initWithFile:file];
    [self.navigationController pushViewController:assetPreviewController animated:YES];
}

- (File*)headerImageFileWithFolder:(File*)file  {
    File *firstImageFile = nil;
    NSArray *subItems = file.subFileItems;
    for (File *file in subItems) {
        if (file.fileType.integerValue == TypeImage) {
            firstImageFile = file;
        }
    }
    return firstImageFile;
}

@end
