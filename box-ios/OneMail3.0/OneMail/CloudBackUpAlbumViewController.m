//
//  CloudBackUpAlbumViewController.m
//  OneMail
//
//  Created by cse  on 15/12/1.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudBackUpAlbumViewController.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "File+Remote.h"
#import "AssetBackUpFolder.h"
#import "AssetGroup.h"
#import "Asset.h"

@interface CloudBackUpAlbumCollectionCell : UICollectionViewCell

@property (nonatomic, strong) UIImage     *backUpAlbumImage;
@property (nonatomic, strong) UIImageView *backUpAlbumImageView;
@property (nonatomic, strong) NSString    *backUpAlbumName;
@property (nonatomic, strong) UILabel     *backUpAlbumNameLabel;
@property (nonatomic, assign) BOOL         backUpAlbumSelected;
@property (nonatomic, strong) UIImageView *backUpAlbumAutoFlag;

- (void)reuse;

@end

@implementation CloudBackUpAlbumCollectionCell

- (void)setBackUpAlbumImage:(UIImage *)backUpAlbumImage {
    if (_backUpAlbumImage != backUpAlbumImage) {
        _backUpAlbumImage = backUpAlbumImage;
    };
    if (!self.backUpAlbumImageView) {
        self.backUpAlbumImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.frame), CGRectGetWidth(self.frame))];
        self.backUpAlbumImageView.contentMode = UIViewContentModeScaleAspectFill;
        self.backUpAlbumImageView.clipsToBounds = YES;
        [self.contentView addSubview:self.backUpAlbumImageView];
    }
    self.backUpAlbumImageView.image = backUpAlbumImage;
}

- (void)setBackUpAlbumName:(NSString *)backUpAlbumName {
    if (_backUpAlbumName != backUpAlbumName) {
        _backUpAlbumName = backUpAlbumName;
    }
    if (!self.backUpAlbumNameLabel) {
        self.backUpAlbumNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(self.frame)-20, CGRectGetWidth(self.frame), 20)];
        self.backUpAlbumNameLabel.font = [UIFont systemFontOfSize:14.0f];
        self.backUpAlbumNameLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
        self.backUpAlbumNameLabel.textAlignment = NSTextAlignmentCenter;
        [self.contentView addSubview:self.backUpAlbumNameLabel];
    }
    self.backUpAlbumNameLabel.text = backUpAlbumName;
}

- (void)setBackUpAlbumSelected:(BOOL)backUpAlbumSelected {
    if (_backUpAlbumSelected != backUpAlbumSelected) {
        _backUpAlbumSelected = backUpAlbumSelected;
    }
    if (!self.backUpAlbumAutoFlag) {
        self.backUpAlbumAutoFlag = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.backUpAlbumImageView.frame)-4-22, 4, 15, 15)];
        [self.backUpAlbumImageView addSubview:self.backUpAlbumAutoFlag];
    }
    if (backUpAlbumSelected) {
        self.backUpAlbumAutoFlag.image = [UIImage imageNamed:@"ic_checkbox_on_nor"];
    } else {
        self.backUpAlbumAutoFlag.image = [UIImage imageNamed:@"ic_choice"];
    }
}

- (void)reuse {
    self.backUpAlbumImageView.image = nil;
    self.backUpAlbumNameLabel.text = nil;
    self.backUpAlbumAutoFlag.image = nil;
}

@end


@interface CloudBackUpAlbumViewController ()<UICollectionViewDataSource,UICollectionViewDelegate>

@property (nonatomic, strong) UILabel          *backUpTitleLabel;
@property (nonatomic, strong) UIButton         *backUpBackButton;
@property (nonatomic, strong) UIButton         *backUpConfirmButton;
@property (nonatomic, strong) ALAssetsLibrary  *library;
@property (nonatomic, strong) NSMutableArray   *libraryAlbums;
@property (nonatomic, strong) UICollectionView *libraryCollectionView;
@property (nonatomic, strong) NSMutableArray   *librarySelectdAlbums;
@property (nonatomic, strong) NSMutableArray   *libraryDisSelectedAlbums;

@end

@implementation CloudBackUpAlbumViewController
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

    self.libraryAlbums = [[NSMutableArray alloc] init];
    self.library = [[ALAssetsLibrary alloc] init];
    self.librarySelectdAlbums = [[NSMutableArray alloc] init];
    self.libraryDisSelectedAlbums = [[NSMutableArray alloc] init];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"groupBackUpFlag = 1"];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"AssetGroup" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"groupName" ascending:YES];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    NSArray *backupGroup = [appDelegate.localManager.managedObjectContext executeFetchRequest:request error:nil];
    NSMutableArray *backupGroupKey = [[NSMutableArray alloc] init];
    for (AssetGroup *assetGroup in backupGroup) {
        [backupGroupKey addObject:assetGroup.groupKey];
    }
    
    UICollectionViewFlowLayout *collectionViewFlowLayout = [[UICollectionViewFlowLayout alloc] init];
    collectionViewFlowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
    collectionViewFlowLayout.minimumInteritemSpacing = 15;
    collectionViewFlowLayout.minimumLineSpacing = 17.5;
    CGFloat collectionCellWidth = (CGRectGetWidth(self.view.frame)-17.5*2-20*2)/3.0f;
    collectionViewFlowLayout.itemSize = CGSizeMake(collectionCellWidth, collectionCellWidth+10+20);
    
    CGRect statusBarRect = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarRect = appDelegate.navigationController.navigationBar.frame;
    self.libraryCollectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:collectionViewFlowLayout];
    self.libraryCollectionView.delegate = self;
    self.libraryCollectionView.dataSource = self;
    [self.libraryCollectionView registerClass:[CloudBackUpAlbumCollectionCell class] forCellWithReuseIdentifier:@"CloudBackUpAlbumCollectionCell"];
    self.libraryCollectionView.showsVerticalScrollIndicator = NO;
    self.libraryCollectionView.backgroundColor = [UIColor clearColor];

    self.libraryCollectionView.frame = CGRectMake(20, statusBarRect.size.height+navigationBarRect.size.height+15, CGRectGetWidth(self.view.frame)-2*20, CGRectGetHeight(self.view.frame)-(statusBarRect.size.height+navigationBarRect.size.height+15));
    [self.view addSubview:self.libraryCollectionView];
    
    @autoreleasepool {
        [self.library enumerateGroupsWithTypes:ALAssetsGroupAll usingBlock:^(ALAssetsGroup *group, BOOL *stop) {
            if (group == nil) {
                NSMutableArray *libraryAlbumsKeyArray = [[NSMutableArray alloc] init];
                for (ALAssetsGroup *group in self.libraryAlbums) {
                    [libraryAlbumsKeyArray addObject:[group valueForProperty:ALAssetsGroupPropertyPersistentID]];
                }
                NSArray *localAssetGroup = [AssetGroup getAllGroup];
                for (AssetGroup *group in localAssetGroup) {
                    if (![libraryAlbumsKeyArray containsObject:group.groupKey]) {
                        [group remove];
                    }
                }
                [self.libraryCollectionView reloadData];
            } else {
                NSString *groupName = [group valueForProperty:ALAssetsGroupPropertyName];
                NSString *groupType = [group valueForProperty:ALAssetsGroupPropertyType];
                NSString *groupKey = [group valueForProperty:ALAssetsGroupPropertyPersistentID];
                NSURL    *groupURL = [group valueForProperty:ALAssetsGroupPropertyURL];
                if ([[groupName lowercaseString] isEqualToString:@"camera roll"] && groupType.integerValue == ALAssetsGroupSavedPhotos) {
                    [self.libraryAlbums insertObject:group atIndex:0];
                } else {
                    [self.libraryAlbums addObject:group];
                }
                NSMutableDictionary *groupInfo = [[NSMutableDictionary alloc] init];
                [groupInfo setObject:groupKey forKey:@"groupKey"];
                [groupInfo setObject:groupName forKey:@"groupName"];
                [groupInfo setObject:groupURL.absoluteString forKey:@"groupURL"];
                [AssetGroup insertGroupWithGroupInfo:groupInfo];
                if ([backupGroupKey containsObject:groupKey]) {
                    [self.librarySelectdAlbums addObject:group];
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
    [self.libraryCollectionView reloadData];
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
    [self.libraryAlbums enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        ALAssetsGroup *group = obj;
        NSString *groupKey = [group valueForProperty:ALAssetsGroupPropertyPersistentID];
        AssetGroup *assetGroup = [AssetGroup searchGroupWithGroupKey:groupKey];
        if (!assetGroup) {
            return;
        }
        if ([self.librarySelectdAlbums containsObject:group]) {
            [assetGroup saveGroupBackUpFlag:@(1)];
        } else {
            [assetGroup saveGroupBackUpFlag:@(0)];
        }
        [self getAssetWithGroup:group assetGroup:assetGroup];
    }];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)getAssetWithGroup:(ALAssetsGroup*)group assetGroup:(AssetGroup*)assetGroup {
    [group enumerateAssetsUsingBlock:^(ALAsset *result, NSUInteger index, BOOL *stop) {
        if (!result) {
            return;
        }
        ALAssetRepresentation* representation = [result defaultRepresentation];
        NSURL *assetUrl = [representation url];
        NSMutableDictionary *assetInfo = [[NSMutableDictionary alloc] init];
        [assetInfo setObject:[group valueForProperty:ALAssetsGroupPropertyPersistentID] forKey:@"assetAlbumKey"];
        [assetInfo setObject:[group valueForProperty:ALAssetsGroupPropertyName] forKey:@"assetAlbumName"];
        [assetInfo setObject:[assetUrl absoluteString] forKey:@"assetUrl"];
        [assetInfo setObject:[representation filename] forKey:@"assetName"];
        Asset *asset = [Asset insertAssetWithAssetInfo:assetInfo];
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
        [ctx performBlockAndWait:^{
            Asset *shadow = (Asset*)[ctx objectWithID:asset.objectID];
            shadow.assetBackUpFlag = assetGroup.groupBackUpFlag;
            if (assetGroup.groupBackUpFlag.integerValue == 1) {
                NSString *assetThumbnailPath = [shadow assetThumbnailPath];
                if (assetThumbnailPath && [[NSFileManager defaultManager] fileExistsAtPath:assetThumbnailPath]) {
                    
                } else {
                    UIImage *assetThumbnail = [UIImage imageWithCGImage:result.thumbnail];
                    NSData *assetThumbnailData = UIImageJPEGRepresentation(assetThumbnail, 1);
                    [[NSFileManager defaultManager] createFileAtPath:assetThumbnailPath contents:assetThumbnailData attributes:nil];
                }
            }
            shadow.assetDate = [NSDate date];
            [ctx save:nil];
        }];
    }];
}


- (void)reloadTableView {
    [self.libraryCollectionView reloadData];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.libraryAlbums.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudBackUpAlbumCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"CloudBackUpAlbumCollectionCell" forIndexPath:indexPath];
    [cell reuse];
    ALAssetsGroup *group = [self.libraryAlbums objectAtIndex:indexPath.row];
    NSInteger assetsNumber = [group numberOfAssets];
    cell.backUpAlbumImage = [UIImage imageWithCGImage:[group posterImage]];
    cell.backUpAlbumName = [NSString stringWithFormat:@"%@ (%ld)",[group valueForProperty:ALAssetsGroupPropertyName], (long)assetsNumber];
    if ([self.librarySelectdAlbums containsObject:group]) {
        cell.backUpAlbumSelected = YES;
    } else {
        cell.backUpAlbumSelected = NO;
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    CloudBackUpAlbumCollectionCell *cell = (CloudBackUpAlbumCollectionCell*)[collectionView cellForItemAtIndexPath:indexPath];
    ALAssetsGroup *group = [self.libraryAlbums objectAtIndex:indexPath.row];
    if (cell.backUpAlbumSelected) {
        cell.backUpAlbumSelected = NO;
        if ([self.librarySelectdAlbums containsObject:group]) {
            [self.librarySelectdAlbums removeObject:group];
            [self.libraryDisSelectedAlbums addObject:group];
        }
    } else {
        cell.backUpAlbumSelected = YES;
        if (![self.librarySelectdAlbums containsObject:group]) {
            [self.librarySelectdAlbums addObject:group];
            [self.libraryDisSelectedAlbums removeObject:group];
        }
    }
}


@end
