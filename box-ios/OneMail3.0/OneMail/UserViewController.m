//
//  UserViewController.m
//  OneMail
//
//  Created by cse  on 15/12/9.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "UserViewController.h"
#import "AppDelegate.h"
#import "MenuViewController.h"
#import "User.h"
#import "User+Remote.h"
#import "AppDelegate.h"
#import <ImageIO/ImageIO.h>

@interface UserViewController ()<UIActionSheetDelegate,UIImagePickerControllerDelegate,UINavigationControllerDelegate,UIAlertViewDelegate>

@property (nonatomic, strong) UILabel         *userTitleLabel;
@property (nonatomic, strong) UIButton        *userBackButton;
@property (nonatomic, strong) UITableViewCell *userPhotoCell;
@property (nonatomic, strong) UIImageView     *userImageView;
@property (nonatomic, strong) UITableViewCell *userLoginNameCell;
@property (nonatomic, strong) UITableViewCell *userNameCell;
@property (nonatomic, strong) UITableViewCell *userEmailCell;
@property (nonatomic, strong) UITableViewCell *userDescriptionCell;

@end



@implementation UserViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    
    self.userTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont systemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.userTitleLabel.text = getLocalizedString(@"UserTitle", nil);
    
    self.userBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.userBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.userBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.userBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.userBackButton addTarget:self action:@selector(userBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.showsVerticalScrollIndicator = NO;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    [self.tableView reloadData];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.userTitleLabel];
    [self.navigationController.navigationBar addSubview:self.userBackButton];
    [UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleLightContent;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.userTitleLabel removeFromSuperview];
    [self.userBackButton removeFromSuperview];
}

- (void)userBackButtonClick {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (appDelegate.leftViewOpened) {
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshUserIcon];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshEmailAddress];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshTransferTaskCount];
        [appDelegate.LeftSlideVC openLeftView];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (UITableViewCell*)userPhotoCell {
    if (!_userPhotoCell) {
        _userPhotoCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _userPhotoCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _userPhotoCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        _userImageView = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-65, 12.5, 65, 65)];
        _userImageView.layer.cornerRadius = 65/2;
        _userImageView.layer.masksToBounds = YES;
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        User *user = [User getUserWithUserSingleId:appDelegate.localManager.userSingleId context:nil];
        NSString *userHeadIconPath = [user userHeadIconPath];
        if (userHeadIconPath && [[NSFileManager defaultManager] fileExistsAtPath:userHeadIconPath]) {
            _userImageView.image = [UIImage imageWithContentsOfFile:userHeadIconPath];
        } else {
            _userImageView.image = [UIImage imageNamed:@"img_portrait_default"];
        }
        [_userPhotoCell.contentView addSubview:_userImageView];
        
        UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, (90-22)/2, CGRectGetWidth(self.view.frame)-15-10-65-15, 22)];
        titleLabel.text = getLocalizedString(@"UserPhoto", nil);
        titleLabel.font = [UIFont systemFontOfSize:17.0f];
        titleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLabel.textAlignment = NSTextAlignmentLeft;
        [_userPhotoCell.contentView addSubview:titleLabel];
    }
    return _userPhotoCell;
}

- (UITableViewCell*)userCellWithTitle:(NSString*)title message:(NSString*)message {
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    
    UILabel *titleLable = [[UILabel alloc] initWithFrame:CGRectZero];
    titleLable.text = title;
    titleLable.font = [UIFont systemFontOfSize:17.0f];
    titleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    titleLable.textAlignment = NSTextAlignmentLeft;
    CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:titleLable limitSize:CGSizeMake(1000, 1000)];
    titleLable.frame = CGRectMake(15, 11, adjustTitleSize.width, 22);
    [cell.contentView addSubview:titleLable];
    
    UILabel *messageLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(titleLable.frame)+10, 11, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(titleLable.frame)-10-15, 22)];
    messageLabel.text = message;
    messageLabel.font = [UIFont systemFontOfSize:14.0f];
    messageLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    messageLabel.textAlignment = NSTextAlignmentRight;
    [cell.contentView addSubview:messageLabel];
    
    return cell;
}

- (UITableViewCell*)userLoginNameCell {
    if (!_userLoginNameCell) {
        _userLoginNameCell = [self userCellWithTitle:getLocalizedString(@"UserLoginName", nil) message:[UserSetting defaultSetting].cloudUserLoginName];
    }
    return _userLoginNameCell;
}

- (UITableViewCell*)userNameCell {
    if (!_userNameCell) {
        _userNameCell = [self userCellWithTitle:getLocalizedString(@"UserName", nil) message:[UserSetting defaultSetting].cloudUserName];
    }
    return _userNameCell;
}

- (UITableViewCell*)userEmailCell {
    if (!_userEmailCell) {
        if ([UserSetting defaultSetting].emailBinded.boolValue) {
            _userEmailCell = [self userCellWithTitle:getLocalizedString(@"UserMail", nil) message:[UserSetting defaultSetting].emailAddress];
        } else {
            _userEmailCell = [self userCellWithTitle:getLocalizedString(@"UserMail", nil) message:getLocalizedString(@"UserMailNoBound", nil)];
        }
    }
    return _userEmailCell;
}

- (UITableViewCell*)userDescriptionCell {
    if (!_userDescriptionCell) {
        _userDescriptionCell = [self userCellWithTitle:getLocalizedString(@"UserDescription", nil) message:[UserSetting defaultSetting].cloudUserDescription];
    }
    return _userDescriptionCell;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    //    if (section == 0) {
    //        return 1;
    //    } else {
    //        return 4;
    //    }
    if (section == 0) {
        return 1;
    } else {
        return 3;
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
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return 90.0f;
    } else {
        return 44.0f;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return self.userPhotoCell;
    } else {
        //        if (indexPath.row == 0) {
        //            return self.userLoginNameCell;
        //        } else if (indexPath.row == 1) {
        //            return self.userNameCell;
        //        } else if (indexPath.row == 2) {
        //            return self.userEmailCell;
        //        } else {
        //            return self.userDescriptionCell;
        //        }
        if (indexPath.row == 0) {
            return self.userLoginNameCell;
        } else if (indexPath.row == 1) {
            return self.userNameCell;
        } else{
            return self.userDescriptionCell;
        }
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        UIActionSheet *actionSheet= [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"UserCamera", nil),getLocalizedString(@"UserPhotoLibrary", nil),nil];
        [actionSheet showInView:self.view];
    }
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {
        [self carema];
    }else if (buttonIndex == 1){
        [self libraryPhoto];
    }
}

-(void)carema {
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        UIImagePickerController *picker = [[UIImagePickerController alloc] init];
        picker.delegate = self;
        picker.allowsEditing = YES;
        picker.sourceType = UIImagePickerControllerSourceTypeCamera;
        [self presentViewController:picker animated:YES completion:^{}];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"UserCameraPrompt", nil)];
        });
    }
}

-(void)libraryPhoto {
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]) {
        UIImagePickerController *picker = [[UIImagePickerController alloc] init];
        picker.delegate = self;
        picker.allowsEditing = YES;
        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentViewController:picker animated:YES completion:^{
            [UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleDefault;
        }];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"UserPhotoPrompt", nil)];
        });
    }
}

-(void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    UIImage *orginal = [info objectForKey:UIImagePickerControllerEditedImage];
    UIImage *pressedImage = [self compressUserHeaderImage:orginal];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [appDelegate.remoteManager uploadUserHeadImage:pressedImage success:^(NSData *data) {
            NSLog(@"%@",data);
            self.userImageView.image = pressedImage;
        } failed:^(AFHTTPRequestOperation *operation,NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
            });
        }];
    });
    [self dismissViewControllerAnimated:YES completion:^{
        [UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleLightContent;
    }];
}

-(UIImage*)compressUserHeaderImage:(UIImage*)image {
    UIImage *imageSmall;
    CGImageSourceRef imageSource = NULL;
    CGImageRef         thumbnail = NULL;
    imageSource = CGImageSourceCreateWithData((__bridge CFDataRef)UIImageJPEGRepresentation(image, 1.0f), NULL);
    CFStringRef imageSourceType = CGImageSourceGetType(imageSource);
    if (imageSource == NULL)
    {
        return nil;
    }
    if (imageSourceType == NULL)
    {
        CFRelease(imageSource);
        return nil;
    }
    NSDictionary *options = [[NSDictionary alloc] initWithObjectsAndKeys:
                             [NSNumber numberWithBool:YES], (NSString *)kCGImageSourceCreateThumbnailFromImageAlways,
                             [NSNumber numberWithLong:100*2], (NSString *)kCGImageSourceThumbnailMaxPixelSize,
                             nil];
    thumbnail = CGImageSourceCreateThumbnailAtIndex(imageSource, 0, (__bridge CFDictionaryRef)options);
    CFRelease(imageSource);
    if (thumbnail == NULL) {
        return nil;
    }
    imageSmall = [UIImage imageWithCGImage:thumbnail];
    CGImageRelease(thumbnail);
    CGSize imageOriginalRect = imageSmall.size;
    CGRect rect;
    int width = imageOriginalRect.width;
    int height = imageOriginalRect.height;
    if (width >= height) {
        rect = CGRectMake((width-height)/2, 0, height, height);
    } else {
        rect = CGRectMake(0, (height-width)/2, width, width);
    }
    UIImage *newImage = [self imageFromImage:imageSmall inRect:rect];
    return newImage;
}

- (UIImage *)imageFromImage:(UIImage *)image inRect:(CGRect )rect
{
    CGImageRef sourceImageRef = [image CGImage];
    CGImageRef newImageRef = CGImageCreateWithImageInRect(sourceImageRef, rect);
    UIImage *newImage = [UIImage imageWithCGImage:newImageRef];
    CFRelease(newImageRef);
    return newImage;
}
@end
