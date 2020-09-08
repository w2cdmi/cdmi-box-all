//
//  MailLocalAttachmentAddViewController.m
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailLocalAttachmentAddViewController.h"
#import "AppDelegate.h"
#import "File+Remote.h"
#import "AttachmentBackupFolder.h"
#import "TransportTask.h"
#import "File.h"
#import "TransportLocalAttachmentUploadTaskHandle.h"
#import "UIAlertView+Blocks.h"

#pragma mark asset collectionView cell
@interface MailLocalAssetAttachmentAddCell : UICollectionViewCell

@property (nonatomic, strong) ALAsset     *asset;
@property (nonatomic, assign) BOOL         assetSelect;
@property (nonatomic, strong) UIImageView *assetImageView;
@property (nonatomic, strong) UIImageView *assetSelectView;

@end

@implementation MailLocalAssetAttachmentAddCell

- (void)setAsset:(ALAsset *)asset {
    if (_asset != asset) {
        _asset = asset;
    }
    if (!self.assetImageView) {
        self.assetImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.frame),CGRectGetHeight(self.frame))];
        [self.contentView addSubview:self.assetImageView];
    }
    self.assetImageView.image = [UIImage imageWithCGImage:self.asset.thumbnail];
}

- (void)setAssetSelect:(BOOL)assetSelect {
    if (_assetSelect != assetSelect) {
        _assetSelect = assetSelect;
    }
    if (!self.assetSelectView) {
        self.assetSelectView = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.assetImageView.frame)-22-4, 4, 22, 22)];
        self.assetSelectView.backgroundColor = [UIColor clearColor];
        [self.assetImageView addSubview:self.assetSelectView];
    }
    if (assetSelect) {
        self.assetSelectView.image = [UIImage imageNamed:@"ic_checkbox_on_nor"];
    } else {
        self.assetSelectView.image = [UIImage imageNamed:@"ic_select_off_nor"];
    }
}

- (void)reuse {
    self.assetImageView.image = nil;
    self.assetSelectView.image = nil;
}
@end

#pragma mark asset viewController

typedef void (^AssetArchiveCompletion)(NSArray* succeeded, NSArray* failed);

@interface MailLocalAssetAddViewController ()<UICollectionViewDataSource,UICollectionViewDelegate>

@property (nonatomic, strong) UILabel          *mailAttachmentAddTitleLabel;
@property (nonatomic, strong) UIButton         *mailAttachmentAddBackButton;
@property (nonatomic, strong) UICollectionView *mailAttachmentAddCollectionView;
@property (nonatomic, strong) NSMutableArray   *mailAssetArray;
@property (nonatomic, strong) NSMutableArray   *mailAssetSelectArray;
@property (nonatomic, strong) NSMutableArray   *mailAssetTaskArray;
@property (nonatomic, strong) NSMutableArray   *mailAssetTaskSuccessArray;
@property (nonatomic, strong) NSMutableArray   *mailAssetTaskFailedArray;
@property (nonatomic, copy) AssetArchiveCompletion archiveCompletion;
@end

@implementation MailLocalAssetAddViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.mailAttachmentAddTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.mailAttachmentAddTitleLabel.font = [UIFont systemFontOfSize:18.0f];
    self.mailAttachmentAddTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.mailAttachmentAddTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.mailAttachmentAddTitleLabel.text = [self.assetsGroup valueForProperty:ALAssetsGroupPropertyName];
    
    self.mailAttachmentAddBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailAttachmentAddBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailAttachmentAddBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailAttachmentAddBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailAttachmentAddBackButton addTarget:self action:@selector(mailAttachmentAddBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    UIView *mailLocalAttachmentAddControl = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(self.view.frame)-49, CGRectGetWidth(self.view.frame), 49)];
    mailLocalAttachmentAddControl.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
    mailLocalAttachmentAddControl.layer.borderWidth = 0.5;
    mailLocalAttachmentAddControl.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
    [self.view addSubview:mailLocalAttachmentAddControl];
    
    UIButton *mailLocalAttachmentCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(mailLocalAttachmentAddControl.frame)/2, 49)];
    UILabel *mailLocalAttachmentCancelLabel = [CommonFunction labelWithFrame:CGRectMake(15, 0, CGRectGetWidth(mailLocalAttachmentCancelButton.frame)-15, CGRectGetHeight(mailLocalAttachmentCancelButton.frame)) textFont:[UIFont systemFontOfSize:16.0f] textColor:[CommonFunction colorWithString:@"008be8" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
    mailLocalAttachmentCancelLabel.text = NSLocalizedString(@"Cancel",nil);
    [mailLocalAttachmentCancelButton addSubview:mailLocalAttachmentCancelLabel];
    [mailLocalAttachmentCancelButton addTarget:self action:@selector(cancel) forControlEvents:UIControlEventTouchUpInside];
    [mailLocalAttachmentAddControl addSubview:mailLocalAttachmentCancelButton];
    
    UIButton *mailLocalAttachmentConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(mailLocalAttachmentAddControl.frame)/2, 0, CGRectGetWidth(mailLocalAttachmentAddControl.frame)/2, 49)];
    UILabel *mailLocalAttachmentConfirmLabel = [CommonFunction labelWithFrame:CGRectMake(0, 0, CGRectGetWidth(mailLocalAttachmentConfirmButton.frame)-15, CGRectGetHeight(mailLocalAttachmentConfirmButton.frame)) textFont:[UIFont systemFontOfSize:16.0f] textColor:[CommonFunction colorWithString:@"008be8" alpha:1.0f] textAlignment:NSTextAlignmentRight];
    mailLocalAttachmentConfirmLabel.text = NSLocalizedString(@"Confirm",nil);
    [mailLocalAttachmentConfirmButton addSubview:mailLocalAttachmentConfirmLabel];
    [mailLocalAttachmentConfirmButton addTarget:self action:@selector(confirm) forControlEvents:UIControlEventTouchUpInside];
    [mailLocalAttachmentAddControl addSubview:mailLocalAttachmentConfirmButton];
    
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    UICollectionViewFlowLayout *assetsCollectionViewFlowLayout = [[UICollectionViewFlowLayout alloc] init];
    assetsCollectionViewFlowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
    assetsCollectionViewFlowLayout.minimumInteritemSpacing = 5;
    assetsCollectionViewFlowLayout.minimumLineSpacing = 5;
    CGFloat assetCollectionCellWidth = (CGRectGetWidth(self.view.frame)-5*5)/4.0f;
    assetsCollectionViewFlowLayout.itemSize = CGSizeMake(assetCollectionCellWidth, assetCollectionCellWidth);
    
    self.mailAttachmentAddCollectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(5, statusBarFrame.size.height+navigationBarFrame.size.height+5, CGRectGetWidth(self.view.frame)-2*5, CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-5-49) collectionViewLayout:assetsCollectionViewFlowLayout];
    self.mailAttachmentAddCollectionView.backgroundColor = [UIColor clearColor];
    self.mailAttachmentAddCollectionView.delegate = self;
    self.mailAttachmentAddCollectionView.dataSource = self;
    [self.mailAttachmentAddCollectionView registerClass:[MailLocalAssetAttachmentAddCell class] forCellWithReuseIdentifier:@"MailLocalAssetAttachmentAddCell"];
    self.mailAttachmentAddCollectionView.showsVerticalScrollIndicator = NO;
    [self.view addSubview:self.mailAttachmentAddCollectionView];
    
    self.mailAssetArray = [[NSMutableArray alloc] init];
    self.mailAssetSelectArray = [[NSMutableArray alloc] init];
    
    @autoreleasepool {
        [self.assetsGroup enumerateAssetsUsingBlock:^(ALAsset *result, NSUInteger index, BOOL *stop) {
            if (result == nil) {
                [self.mailAttachmentAddCollectionView reloadData];
            } else {
                [self.mailAssetArray addObject:result];
            }
        }];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.mailAttachmentAddTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailAttachmentAddBackButton];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [self.mailAttachmentAddTitleLabel removeFromSuperview];
    [self.mailAttachmentAddBackButton removeFromSuperview];
}

- (void)mailAttachmentAddBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)cancel {
    [self.navigationController popToViewController:self.rootViewController animated:YES];
}

- (void)confirm {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    BOOL hasNetWork = appDelegate.hasNetwork;
    if (!hasNetWork) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudNoneNetworkPrompt", nil)];
        });
    } else {
        BOOL WiFiNetWork = appDelegate.wifiNetwork;
        UserSetting *userSetting = [UserSetting defaultSetting];
        if (!WiFiNetWork && userSetting.cloudAssetBackupWifi.integerValue == 1) {
            [UIAlertView showAlertViewWithTitle:nil message:NSLocalizedString(@"CloudUploadWIFIPrompt", nil) cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                [self doUploadingWithForce:YES];
            } onCancel:^{}];
        } else {
            [self doUploadingWithForce:YES];
        }
    }
}

- (void)doUploadingWithForce:(BOOL)force {
    self.mailAssetTaskArray = [[NSMutableArray alloc] init];
    self.mailAssetTaskSuccessArray = [[NSMutableArray alloc] init];
    self.mailAssetTaskFailedArray = [[NSMutableArray alloc] init];
    AttachmentBackupFolder *attachmentBackupFolder = [[AttachmentBackupFolder alloc] init];
    attachmentBackupFolder.completionBlock = ^(File *attachmentFolder) {
        if (!attachmentFolder) {
            self.completion(nil,nil);
        }
        for (ALAsset *asset in self.mailAssetSelectArray) {
            TransportTask *transportTask = [attachmentFolder localAttachmentUpload:asset force:YES];
            if (transportTask) {
                [self.mailAssetTaskArray addObject:transportTask];
            }
        }
        if (self.mailAssetTaskArray.count == 0) {
            self.completion(nil,nil);
        }
        
        self.archiveCompletion = self.completion;
        
        TransportTask *transortTask = [self.mailAssetTaskArray firstObject];
        [transortTask.taskHandle resume];
        [transortTask addObserver:self forKeyPath:@"taskStatus" options:NSKeyValueObservingOptionNew context:nil];
    };
    [attachmentBackupFolder cheakAttachmentFolderWithType:@(1) date:[NSDate date]];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (![object isKindOfClass:[TransportTask class]] || ![keyPath isEqualToString:@"taskStatus"]) {
        return;
    }
    TransportTask *transportTask = object;
    if (transportTask.taskStatus.integerValue == TaskFailed ||
        transportTask.taskStatus.integerValue == TaskSucceed) {
        if (transportTask.taskStatus.integerValue == TaskFailed) {
            [self.mailAssetTaskFailedArray addObject:transportTask.file];
        }
        if (transportTask.taskStatus.integerValue == TaskSucceed) {
            [self.mailAssetTaskSuccessArray addObject:transportTask.file];
        }
        [transportTask removeObserver:self forKeyPath:@"taskStatus" context:nil];
        [self.mailAssetTaskArray removeObject:transportTask];
        if (self.mailAssetTaskArray.count > 0) {
            TransportTask *nextTask = [self.mailAssetTaskArray objectAtIndex:0];
            [nextTask.taskHandle resume];
            [nextTask addObserver:self forKeyPath:@"taskStatus" options:NSKeyValueObservingOptionNew context:nil];
        } else {
            self.archiveCompletion(self.mailAssetTaskSuccessArray,self.mailAssetTaskFailedArray);
        }
    }
}


- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.mailAssetArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    MailLocalAssetAttachmentAddCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"MailLocalAssetAttachmentAddCell" forIndexPath:indexPath];
    [cell reuse];
    cell.asset = [self.mailAssetArray objectAtIndex:indexPath.row];
    if ([self.mailAssetSelectArray containsObject:cell.asset]) {
        cell.assetSelect = YES;
    } else {
        cell.assetSelect = NO;
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    MailLocalAssetAttachmentAddCell *cell = (MailLocalAssetAttachmentAddCell*)[collectionView cellForItemAtIndexPath:indexPath];
    if (cell.assetSelect) {
        cell.assetSelect = NO;
        if ([self.mailAssetSelectArray containsObject:cell.asset]) {
            [self.mailAssetSelectArray removeObject:cell.asset];
        }
    } else {
        cell.assetSelect = YES;
        if (![self.mailAssetSelectArray containsObject:cell.asset]) {
            [self.mailAssetSelectArray addObject:cell.asset];
        }
    }
}


@end


#pragma mark album tableView cell
@interface MailLocalAlbumAttachmentAddCell : UITableViewCell

@property (nonatomic, strong) NSString *albumTitle;
@property (nonatomic, strong) UIImage *albumImage;

@end

@implementation MailLocalAlbumAttachmentAddCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        
    }
    return self;
}

- (void)setAlbumTitle:(NSString *)albumTitle {
    if (_albumTitle != albumTitle) {
        _albumTitle = albumTitle;
    }
}

- (void)setAlbumImage:(UIImage *)albumImage {
    if (_albumImage != albumImage) {
        _albumImage = albumImage;
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.imageView.image = self.albumImage;
    self.imageView.frame = CGRectMake(19, 3.5, 48, 48);
    
    self.textLabel.text = self.albumTitle;
    self.textLabel.font = [UIFont systemFontOfSize:17.0f];
    self.textLabel.textAlignment = NSTextAlignmentLeft;
    self.textLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    self.textLabel.frame = CGRectMake(CGRectGetMaxX(self.imageView.frame)+14, 16.5, CGRectGetWidth(self.frame)-19-CGRectGetWidth(self.imageView.frame)-14-11, 22);
    
    self.contentView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
}

@end

#pragma mark album viewController
@interface MailLocalAttachmentAddViewController ()<UINavigationControllerDelegate,UIImagePickerControllerDelegate,UITableViewDataSource,UITableViewDelegate>

@property (nonatomic, strong) ALAssetsLibrary *library;
@property (nonatomic, strong) NSMutableArray  *albumGroups;
@property (nonatomic, strong) UILabel         *mailAttachmentAddTitleLabel;
@property (nonatomic, strong) UIButton        *mailAttachmentAddBackButton;
@property (nonatomic, strong) UITableView     *mailAttachmentAddTableView;

@end

@implementation MailLocalAttachmentAddViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.mailAttachmentAddTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.mailAttachmentAddTitleLabel.font = [UIFont systemFontOfSize:18.0f];
    self.mailAttachmentAddTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.mailAttachmentAddTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.mailAttachmentAddTitleLabel.text = NSLocalizedString(@"MailChatAttAttachment", nil);
    
    self.mailAttachmentAddBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailAttachmentAddBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailAttachmentAddBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailAttachmentAddBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailAttachmentAddBackButton addTarget:self action:@selector(mailAttachmentAddBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    self.mailAttachmentAddTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height) style:UITableViewStylePlain];
    [self.mailAttachmentAddTableView registerClass:[MailLocalAlbumAttachmentAddCell class] forCellReuseIdentifier:@"MailLocalAlbumAttachmentAddCell"];
    self.mailAttachmentAddTableView.delegate = self;
    self.mailAttachmentAddTableView.dataSource = self;
    self.mailAttachmentAddTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.mailAttachmentAddTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.mailAttachmentAddTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.mailAttachmentAddTableView];
    
    self.albumGroups = [[NSMutableArray alloc] init];
    self.library = [[ALAssetsLibrary alloc] init];
    @autoreleasepool {
        [self.library enumerateGroupsWithTypes:ALAssetsGroupAll usingBlock:^(ALAssetsGroup *group, BOOL *stop) {
            if (group == nil) {
                [self.mailAttachmentAddTableView reloadData];
            } else {
                NSString *groupName = [group valueForProperty:ALAssetsGroupPropertyName];
                NSString *groupType = [group valueForProperty:ALAssetsGroupPropertyType];
                if ([[groupName lowercaseString] isEqualToString:@"camera roll"] && groupType.integerValue == ALAssetsGroupSavedPhotos) {
                    [self.albumGroups insertObject:group atIndex:0];
                } else {
                    [self.albumGroups addObject:group];
                }
            }
        } failureBlock:^(NSError *error) {
            if ([ALAssetsLibrary authorizationStatus] == ALAuthorizationStatusDenied) {
                NSString *errorMessage = NSLocalizedString(@"CloudUploadDeniedPrompt", nil);
                [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"CloudUploadDeniedTitle", nil) message:errorMessage delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
                
            } else {
                NSString *errorMessage = [NSString stringWithFormat:@"Album Error: %@ - %@", [error localizedDescription], [error localizedRecoverySuggestion]];
                [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:errorMessage delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
            }
        }];
    }
}

- (void)mailAttachmentAddBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.mailAttachmentAddTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailAttachmentAddBackButton];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadTableView) name:ALAssetsLibraryChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
    [self.mailAttachmentAddTableView reloadData];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailAttachmentAddTitleLabel removeFromSuperview];
    [self.mailAttachmentAddBackButton removeFromSuperview];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:ALAssetsLibraryChangedNotification object:nil];
}


- (void)reloadTableView {
    [self.mailAttachmentAddTableView reloadData];
}

#pragma mark tableView dataSource + delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.albumGroups.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 55.0f;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MailLocalAlbumAttachmentAddCell *cell = (MailLocalAlbumAttachmentAddCell*)[tableView dequeueReusableCellWithIdentifier:@"MailLocalAlbumAttachmentAddCell"];
    if (cell == nil) {
        cell = [[MailLocalAlbumAttachmentAddCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"MailLocalAlbumAttachmentAddCell"];
    }
    ALAssetsGroup *album = (ALAssetsGroup*)[self.albumGroups objectAtIndex:indexPath.row];
    NSInteger albumAssetsNumber = [album numberOfAssets];
    
    cell.albumTitle = [NSString stringWithFormat:@"%@ (%ld)",[album valueForProperty:ALAssetsGroupPropertyName], (long)albumAssetsNumber];
    UIImage* image = [UIImage imageWithCGImage:[album posterImage]];
    image = [self resize:image to:CGSizeMake(48, 48)];
    cell.albumImage = image;
    return cell;
}

- (UIImage *)resize:(UIImage *)image to:(CGSize)newSize {
    UIGraphicsBeginImageContextWithOptions(newSize, NO, 0.0);
    [image drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        if (indexPath.row == self.albumGroups.count) {
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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MailLocalAssetAddViewController *assetViewController = [[MailLocalAssetAddViewController alloc] init];
    assetViewController.completion = self.completion;
    assetViewController.rootViewController = self.rootViewController;
    [assetViewController setAssetsGroup:[self.albumGroups objectAtIndex:indexPath.row]];
    [self.navigationController pushViewController:assetViewController animated:YES];
}

@end
