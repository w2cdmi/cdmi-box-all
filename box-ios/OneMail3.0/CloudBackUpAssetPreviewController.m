//
//  CloudBackUpAssetPreviewController.m
//  OneMail
//
//  Created by CSE on 16/1/26.
//  Copyright © 2016年 cse. All rights reserved.
//
#import "CloudBackUpAssetPreviewController.h"
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "File+Remote.h"
#import "FileThumbnail.h"
#import "AssetBackUpFolder.h"
#import "AssetGroup.h"
#import "Asset.h"

@interface CloudBackUpAssetPreviewCell : UICollectionViewCell

@property (nonatomic,strong) File        *assetFile;
@property (nonatomic,strong) UIImageView *assetImageView;

- (void)reuse;

@end

@implementation CloudBackUpAssetPreviewCell

- (void)setAssetFile:(File *)assetFile {
    if (_assetFile != assetFile) {
        _assetFile = assetFile;
    }
}

- (void)reuse {
    if (!self.assetImageView) {
        self.assetImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height)];
        self.assetImageView.contentMode = UIViewContentModeScaleAspectFill;
        self.assetImageView.clipsToBounds = YES;
        [self.contentView addSubview:self.assetImageView];
    }
    self.assetFile = nil;
    self.assetImageView.image = nil;
}

@end

@interface CloudBackUpAssetPreviewController() <UICollectionViewDelegate,UICollectionViewDataSource>

@property (nonatomic, strong) UILabel          *assetTitleLabel;
@property (nonatomic, strong) UIButton         *assetBackButton;
@property (nonatomic, strong) File             *assetFolder;
@property (nonatomic, strong) NSMutableArray   *assetArray;
@property (nonatomic, strong) UICollectionView *assetCollectionView;

@end

@implementation CloudBackUpAssetPreviewController
- (id)initWithFile:(File *)file {
    self = [super init];
    if (self) {
        self.assetFolder = file;
    }
    return self;
}
- (void)viewDidLoad{
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.assetTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.assetTitleLabel.text = self.assetFolder.fileName;
    
    self.assetBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.assetBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.assetBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.assetBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.assetBackButton addTarget:self action:@selector(backUpBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarRect = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarRect = appDelegate.navigationController.navigationBar.frame;
    UICollectionViewFlowLayout *collectionViewFlowLayout = [[UICollectionViewFlowLayout alloc] init];
    collectionViewFlowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
    collectionViewFlowLayout.minimumInteritemSpacing = 5;
    collectionViewFlowLayout.minimumLineSpacing = 5;
    CGFloat collectionCellWidth = (CGRectGetWidth(self.view.frame)-5*5)/4.0f;
    collectionViewFlowLayout.itemSize = CGSizeMake(collectionCellWidth, collectionCellWidth);
    self.assetCollectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:collectionViewFlowLayout];
    self.assetCollectionView.delegate = self;
    self.assetCollectionView.dataSource = self;
    [self.assetCollectionView registerClass:[CloudBackUpAssetPreviewCell class] forCellWithReuseIdentifier:@"CloudBackUpAssetPreviewCell"];
    self.assetCollectionView.showsVerticalScrollIndicator = NO;
    self.assetCollectionView.backgroundColor = [UIColor clearColor];
    
    self.assetCollectionView.frame = CGRectMake(5, statusBarRect.size.height+navigationBarRect.size.height+5, CGRectGetWidth(self.view.frame)-2*5, CGRectGetHeight(self.view.frame)-(statusBarRect.size.height+navigationBarRect.size.height+5));
    [self.view addSubview:self.assetCollectionView];
    
    self.assetArray = [[NSMutableArray alloc] init];
    NSArray *subItems = self.assetFolder.subFileItems;
    for (File *subItem in subItems) {
        if (subItem.fileType.integerValue == TypeImage) {
            [self.assetArray addObject:subItem];
        }
    }
    [self.assetCollectionView reloadData];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.assetTitleLabel];
    [self.navigationController.navigationBar addSubview:self.assetBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.assetTitleLabel removeFromSuperview];
    [self.assetBackButton removeFromSuperview];
}

- (void)backUpBackButtonClick{
    [self.navigationController popViewControllerAnimated:YES];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.assetArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudBackUpAssetPreviewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"CloudBackUpAssetPreviewCell" forIndexPath:indexPath];
    [cell reuse];
    File *file = [self.assetArray objectAtIndex:indexPath.row];
    cell.assetFile = file;
    [FileThumbnail imageWithFile:file imageView:cell.assetImageView];
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath{


}

@end
