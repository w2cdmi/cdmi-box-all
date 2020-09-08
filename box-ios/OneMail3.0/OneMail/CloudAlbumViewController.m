//
//  CloudAlbumViewController.m
//  OneMail
//
//  Created by cse  on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudAlbumViewController.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import "AppDelegate.h"
#import "CloudAssetViewController.h"
#import "File+Remote.h"
#import "UIAlertView+Blocks.h"

@implementation CloudAlbumTableViewCell

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


@interface CloudAlbumViewController ()<UINavigationControllerDelegate,UIImagePickerControllerDelegate>

@property (nonatomic, strong) ALAssetsLibrary *library;
@property (nonatomic, strong) NSMutableArray  *cloudAlbumGroups;
@property (nonatomic, strong) UILabel         *cloudAlbumTitleLabel;
@property (nonatomic, strong) UIButton        *cloudAlbumBackButton;
@property (nonatomic, strong) UIButton        *cloudAlbumCameraButton;

@end

@implementation CloudAlbumViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.cloudAlbumTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.cloudAlbumTitleLabel.font = [UIFont systemFontOfSize:18.0f];
    self.cloudAlbumTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.cloudAlbumTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.cloudAlbumTitleLabel.text = getLocalizedString(@"CloudUploadTitle", nil);
    
    self.cloudAlbumBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.cloudAlbumBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudAlbumBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.cloudAlbumBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.cloudAlbumBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    
    self.cloudAlbumCameraButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
    self.cloudAlbumCameraButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudAlbumCameraButton setImage:[UIImage imageNamed:@"ic_nav_camera_nor"] forState:UIControlStateNormal];
    [self.cloudAlbumCameraButton setImage:[UIImage imageNamed:@"ic_nav_camera_press"] forState:UIControlStateHighlighted];
    [self.cloudAlbumCameraButton addTarget:self action:@selector(cameraTakePhoto) forControlEvents:UIControlEventTouchUpInside];

    self.view.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0];
    [self.tableView registerClass:[CloudAlbumTableViewCell class] forCellReuseIdentifier:@"CloudAlbumTableViewCell"];
    [self.tableView setDataSource:self];
    [self.tableView setDelegate:self];
    [self.tableView setSeparatorColor:[CommonFunction colorWithString:@"d9d9d9" alpha:1.0f]];
    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleSingleLine];
    [self.tableView setScrollEnabled:YES];
    [self.tableView setFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame))];
    [self.tableView setTableFooterView:[[UIView alloc] init]];
    
    self.cloudAlbumGroups = [[NSMutableArray alloc] init];
    self.library = [[ALAssetsLibrary alloc] init];
    @autoreleasepool {
        [self.library enumerateGroupsWithTypes:ALAssetsGroupAll usingBlock:^(ALAssetsGroup *group, BOOL *stop) {
            if (group == nil) {
                [self.tableView reloadData];
            } else {
                NSString *groupName = [group valueForProperty:ALAssetsGroupPropertyName];
                NSString *groupType = [group valueForProperty:ALAssetsGroupPropertyType];
                if ([[groupName lowercaseString] isEqualToString:@"camera roll"] && groupType.integerValue == ALAssetsGroupSavedPhotos) {
                    [self.cloudAlbumGroups insertObject:group atIndex:0];
                } else {
                    [self.cloudAlbumGroups addObject:group];
                }
            }
        } failureBlock:^(NSError *error) {
            if ([ALAssetsLibrary authorizationStatus] == ALAuthorizationStatusDenied) {
                NSString *errorMessage = getLocalizedString(@"CloudUploadDeniedPrompt", nil);
                [[[UIAlertView alloc] initWithTitle:getLocalizedString(@"CloudUploadDeniedTitle", nil) message:errorMessage delegate:nil cancelButtonTitle:getLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
                
            } else {
                NSString *errorMessage = [NSString stringWithFormat:@"Album Error: %@ - %@", [error localizedDescription], [error localizedRecoverySuggestion]];
                [[[UIAlertView alloc] initWithTitle:getLocalizedString(@"Error", nil) message:errorMessage delegate:nil cancelButtonTitle:getLocalizedString(@"OK", nil) otherButtonTitles:nil] show];
            }
        }];
    }
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.cloudAlbumTitleLabel];
    [self.navigationController.navigationBar addSubview:self.cloudAlbumBackButton];
    [self.navigationController.navigationBar addSubview:self.cloudAlbumCameraButton];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadTableView) name:ALAssetsLibraryChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
    [self.tableView reloadData];
}

- (void)reloadTableView {
    [self.tableView reloadData];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.cloudAlbumTitleLabel removeFromSuperview];
    [self.cloudAlbumBackButton removeFromSuperview];
    [self.cloudAlbumCameraButton removeFromSuperview];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:ALAssetsLibraryChangedNotification object:nil];
}


#pragma mark camera
- (void)cameraTakePhoto {
    UIImagePickerControllerSourceType sourceType = UIImagePickerControllerSourceTypeCamera;
    if ([UIImagePickerController isSourceTypeAvailable:sourceType]) {
        UIImagePickerController *picker = [[UIImagePickerController alloc] init];
        picker.delegate = self;
        picker.sourceType = sourceType;
        [self presentViewController:picker animated:YES completion:nil];
    }
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    [self dismissViewControllerAnimated:YES completion:^{
        [self.navigationController popViewControllerAnimated:NO];
    }];
    UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
    __strong typeof(self) strong = self;
    [self addAssetImage:image album:@"Onebox" completion:^(ALAsset *asset) {
        //__strong typeof(weak) strong = weak;
        if (asset) {
            [strong doUploadingWithAsset:asset];
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudUploadAddSuccessPrompt", nil)];
            });
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudUploadAddFailedPrompt", nil)];
            });
        }
    }];
}

- (void)addAssetImage:(UIImage*)image album:(NSString*)albumName completion:(void(^)(ALAsset *asset))completion {
    __block ALAssetsGroup *album = nil;
    __block BOOL albumExist = NO;
    @autoreleasepool {
        [self.library enumerateGroupsWithTypes:ALAssetsGroupAlbum usingBlock:^(ALAssetsGroup *group, BOOL *stop) {
            if (group == nil) {
                if (!albumExist) {
                    [self.library addAssetsGroupAlbumWithName:albumName resultBlock:^(ALAssetsGroup *group) {
                        album = group;
                    } failureBlock:^(NSError *error) {
                        completion(nil);
                    }];
                }
            } else {
                NSString *groupName = [group valueForProperty:ALAssetsGroupPropertyName];
                if ([albumName isEqualToString:groupName]) {
                    albumExist = YES; album = group; stop = false;
                }
            }
        } failureBlock:^(NSError *error) {
            completion(nil);
        }];
        
        [self.library writeImageToSavedPhotosAlbum:image.CGImage orientation:(ALAssetOrientation)image.imageOrientation completionBlock:^(NSURL *assetURL, NSError *error) {
            if (!error) {
                [self.library assetForURL:assetURL resultBlock:^(ALAsset *asset) {
                    [album addAsset:asset];
                    completion(asset);
                } failureBlock:^(NSError *error) {
                    completion(nil);
                }];
            } else {
                completion(nil);
            }
        }];
    }
}

- (void)doUploadingWithAsset:(ALAsset*)asset {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    BOOL WiFiNetWork = appDelegate.wifiNetwork;
    File *file = [File getFileWithFileId:self.uploadTargetFolderId fileOwner:self.uploadTargetFolderOwner];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!WiFiNetWork && userSetting.cloudAssetBackupWifi.integerValue == 1) {
        [UIAlertView showAlertViewWithTitle:nil message:getLocalizedString(@"CloudUploadWIFIPrompt", nil) cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
            [file uploadAsset:asset force:YES];
        } onCancel:^{
            [file uploadAsset:asset force:NO];
        }];
    } else {
        [file uploadAsset:asset force:YES];
    }
}

#pragma mark tableView dataSource + delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.cloudAlbumGroups.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 55.0f;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudAlbumTableViewCell *cell = (CloudAlbumTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"CloudAlbumTableViewCell"];
    if (cell == nil) {
        cell = [[CloudAlbumTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"CloudAlbumTableViewCell"];
    }
    ALAssetsGroup *album = (ALAssetsGroup*)[self.cloudAlbumGroups objectAtIndex:indexPath.row];
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
        if (indexPath.row == self.cloudAlbumGroups.count) {
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
    CloudAssetViewController *assetViewController = [[CloudAssetViewController alloc] init];
    assetViewController.rootViewController = self.rootViewController;
    assetViewController.uploadTargetFolderId = self.uploadTargetFolderId;
    assetViewController.uploadTargetFolderOwner = self.uploadTargetFolderOwner;
    [assetViewController setAssetsGroup:[self.cloudAlbumGroups objectAtIndex:indexPath.row]];
    [self.navigationController pushViewController:assetViewController animated:YES];
}

@end
