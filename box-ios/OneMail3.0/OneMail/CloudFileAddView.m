//
//  CloudFileAddView.m
//  OneMail
//
//  Created by cse  on 15/12/12.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudFileAddView.h"
#import "AppDelegate.h"

@interface CloudFileAddView ()

@property (nonatomic, strong) UIButton *closeButton;

@property (nonatomic, strong) UIButton    *uploadAssetButton;
@property (nonatomic, strong) UIView      *uploadAssetView;
@property (nonatomic, assign) CGRect       uploadAssetViewShowFrame;
@property (nonatomic, assign) CGRect       uploadAssetViewHideFrame;
@property (nonatomic, strong) UIImageView *uploadAssetImageView;
@property (nonatomic, assign) CGRect       uploadAssetImageViewShowFrame;
@property (nonatomic, assign) CGRect       uploadAssetImageViewHideFrame;
@property (nonatomic, strong) UIView      *uploadAssetTitleView;
@property (nonatomic, assign) CGRect       uploadAssetTitleViewShowFrame;
@property (nonatomic, assign) CGRect       uploadAssetTitleViewHideFrame;
@property (nonatomic, strong) UILabel      *uploadAssetTitleLabel;

@property (nonatomic, strong) UIButton    *createFolderButton;
@property (nonatomic, strong) UIView      *createFolderView;
@property (nonatomic, assign) CGRect       createFolderViewShowFrame;
@property (nonatomic, assign) CGRect       createFolderViewHideFrame;
@property (nonatomic, strong) UIImageView *createFolderImageView;
@property (nonatomic, assign) CGRect       createFolderImageViewShowFrame;
@property (nonatomic, assign) CGRect       createFolderImageViewHideFrame;
@property (nonatomic, strong) UIView      *createFolderTitleView;
@property (nonatomic, assign) CGRect       createFolderTitleViewShowFrame;
@property (nonatomic, assign) CGRect       createFolderTitleViewHideFrame;
@property (nonatomic, strong) UILabel     *createFolderTitleLabel;

@end

@implementation CloudFileAddView

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.LocalizedChange" object:nil];
}

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
         [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(layoutSubView) name:@"com.huawei.onemail.LocalizedChange" object:nil];
         [self layoutSubView];
    }
    return self;
}

- (void)layoutSubView {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect tabBarFrame = appDelegate.mainTabBar.frame;
    
    self.closeButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.frame)-10-65, CGRectGetHeight(self.frame)-tabBarFrame.size.height-10-65, 65, 65)];
    self.closeButton.layer.shadowColor = [UIColor blackColor].CGColor;
    self.closeButton.layer.shadowOpacity = 0.1;
    self.closeButton.layer.shadowOffset = CGSizeMake(0, 5);
    self.closeButton.layer.shadowRadius = 2;
    [self.closeButton addTarget:self action:@selector(hideView) forControlEvents:UIControlEventTouchUpInside];//
    [self.closeButton setImage:[UIImage imageNamed:@"btn_new_nor"] forState:UIControlStateNormal];
    //        [self.closeButton setImage:[UIImage imageNamed:@"btn_new_press"] forState:UIControlStateSelected];
    
    self.createFolderTitleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    //        self.createFolderTitleLabel.font = [UIFont systemFontOfSize:15.0f];
    self.createFolderTitleLabel.font = [UIFont boldSystemFontOfSize:15.0f];
    self.createFolderTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.createFolderTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.createFolderTitleLabel.layer.shadowOpacity = 0.9;
    self.createFolderTitleLabel.text = getLocalizedString(@"CloudFileCreateFolder", nil);
    CGSize createFolderTitleLabelSize = [CommonFunction labelSizeWithLabel:self.createFolderTitleLabel limitSize:CGSizeMake(1000, 1000)];
    self.createFolderTitleLabel.frame = CGRectMake(15, 7, createFolderTitleLabelSize.width, 22);
    
    self.createFolderTitleView = [[UIView alloc] initWithFrame:CGRectZero];
    self.createFolderTitleView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:0.0f];
    self.createFolderTitleView.layer.cornerRadius = 4;
    self.createFolderTitleView.layer.shadowColor = [UIColor blackColor].CGColor;
    self.createFolderTitleView.layer.shadowOpacity = 0.8;
    self.createFolderTitleView.layer.shadowOffset = CGSizeMake(0, 2);
    self.createFolderTitleView.layer.shadowRadius = 2;
    self.createFolderTitleViewShowFrame = CGRectMake(15, (54-36)/2, 10+CGRectGetWidth(self.createFolderTitleLabel.frame)+10, 36);
    self.createFolderTitleViewHideFrame = CGRectZero;
    self.createFolderTitleView.frame = self.createFolderTitleViewHideFrame;
    
    self.createFolderView = [[UIView alloc] initWithFrame:CGRectZero];
    self.createFolderViewShowFrame = CGRectMake(CGRectGetWidth(self.frame)-15-54-20-self.createFolderTitleViewShowFrame.size.width, CGRectGetHeight(self.frame)-tabBarFrame.size.height-10-65-10-54, self.createFolderTitleViewShowFrame.size.width+20+54, 54);
    self.createFolderViewHideFrame = CGRectMake(CGRectGetWidth(self.frame)-10-65/2, CGRectGetHeight(self.frame)-tabBarFrame.size.height-10-65/2, 0, 0);
    [self.createFolderView addSubview:self.createFolderTitleView];
    self.createFolderView.frame = self.createFolderViewHideFrame;
    
    self.createFolderImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"btn_new_folder_nor"] highlightedImage:[UIImage imageNamed:@"btn_new_folder_press"]];
    self.createFolderImageViewShowFrame = CGRectMake(self.createFolderViewShowFrame.size.width-54, 0, 54, 54);
    self.createFolderImageViewHideFrame = CGRectZero;
    self.createFolderImageView.frame = self.createFolderImageViewHideFrame;
    [self.createFolderView addSubview:self.createFolderImageView];
    
    self.createFolderButton = [[UIButton alloc] initWithFrame:CGRectZero];
    [self.createFolderButton addTarget:self action:@selector(createFolder) forControlEvents:UIControlEventTouchUpInside];
    self.createFolderButton.backgroundColor = [UIColor clearColor];
    self.createFolderButton.frame = self.createFolderView.frame;
    
    self.uploadAssetTitleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    self.uploadAssetTitleLabel.font = [UIFont boldSystemFontOfSize:15.0f];
    self.uploadAssetTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.uploadAssetTitleLabel.layer.shadowOpacity = 0.9;
    self.uploadAssetTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.uploadAssetTitleLabel.text = getLocalizedString(@"CloudFileUploadFile", nil);
    CGSize uploadAssetTitleLabelSize = [CommonFunction labelSizeWithLabel:self.uploadAssetTitleLabel limitSize:CGSizeMake(1000, 1000)];
    self.uploadAssetTitleLabel.frame = CGRectMake(15, 7, uploadAssetTitleLabelSize.width, 22);
    
    self.uploadAssetTitleView = [[UIView alloc] initWithFrame:CGRectZero];
    self.uploadAssetTitleView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:0.0f];
    self.uploadAssetTitleView.layer.cornerRadius = 4;
    self.uploadAssetTitleView.layer.shadowColor = [UIColor blackColor].CGColor;
    self.uploadAssetTitleView.layer.shadowOpacity = 0.8;
    self.uploadAssetTitleView.layer.shadowOffset = CGSizeMake(0, 2);
    self.uploadAssetTitleView.layer.shadowRadius = 2;
    self.uploadAssetTitleViewShowFrame = CGRectMake(15, (54-36)/2, 10+CGRectGetWidth(self.uploadAssetTitleLabel.frame)+10, 36);
    self.uploadAssetTitleViewHideFrame = CGRectZero;
    self.uploadAssetTitleView.frame = self.uploadAssetTitleViewHideFrame;
    
    self.uploadAssetView = [[UIView alloc] initWithFrame:CGRectZero];
    self.uploadAssetViewShowFrame = CGRectMake(CGRectGetWidth(self.frame)-15-54-20-self.uploadAssetTitleViewShowFrame.size.width, CGRectGetHeight(self.frame)-tabBarFrame.size.height-10-65-10-54-10-54, self.uploadAssetTitleViewShowFrame.size.width+20+54, 54);
    self.uploadAssetViewHideFrame = CGRectMake(CGRectGetWidth(self.frame)-10-65/2, CGRectGetHeight(self.frame)-tabBarFrame.size.height-10-65/2, 0, 0);
    [self.uploadAssetView addSubview:self.uploadAssetTitleView];
    self.uploadAssetView.frame = self.uploadAssetViewHideFrame;
    
    self.uploadAssetImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"btn_upload_files_nor"] highlightedImage:[UIImage imageNamed:@"btn_upload_files_press"]];
    self.uploadAssetImageViewShowFrame = CGRectMake(self.uploadAssetViewShowFrame.size.width-54, 0, 54, 54);
    self.uploadAssetImageViewHideFrame = CGRectZero;
    self.uploadAssetImageView.frame = self.uploadAssetImageViewHideFrame;
    [self.uploadAssetView addSubview:self.uploadAssetImageView];
    
    self.uploadAssetButton = [[UIButton alloc] initWithFrame:CGRectZero];
    [self.uploadAssetButton addTarget:self action:@selector(uploadFile) forControlEvents:UIControlEventTouchUpInside];
    self.uploadAssetButton.backgroundColor = [UIColor clearColor];
    self.uploadAssetButton.frame = self.uploadAssetView.frame;
    
    [self addSubview:self.uploadAssetView];
    [self addSubview:self.uploadAssetButton];
    [self addSubview:self.createFolderView];
    [self addSubview:self.createFolderButton];
    [self addSubview:self.closeButton];
}

- (void)showView {
    [[UIApplication sharedApplication].keyWindow.rootViewController.view addSubview:self];
    [UIView animateWithDuration:0.4 delay:0.1 options:UIViewAnimationOptionTransitionCrossDissolve animations:^{
        self.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.9];
        self.closeButton.transform = CGAffineTransformMakeRotation(M_PI_4);
        self.uploadAssetView.frame = self.uploadAssetViewShowFrame;
        self.uploadAssetTitleView.frame = self.uploadAssetTitleViewShowFrame;
        self.uploadAssetImageView.frame = self.uploadAssetImageViewShowFrame;
        self.uploadAssetButton.frame = self.uploadAssetView.frame;
        self.createFolderView.frame = self.createFolderViewShowFrame;
        self.createFolderTitleView.frame = self.createFolderTitleViewShowFrame;
        self.createFolderImageView.frame = self.createFolderImageViewShowFrame;
        self.createFolderButton.frame = self.createFolderView.frame;
    } completion:^(BOOL finished) {
        [self.uploadAssetTitleView addSubview:self.uploadAssetTitleLabel];
        [self.createFolderTitleView addSubview:self.createFolderTitleLabel];
    }];
//    [UIView animateWithDuration:0.5 animations:^{
//        self.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.5];
//        self.closeButton.transform = CGAffineTransformMakeRotation(M_PI_4);
//        self.uploadAssetView.frame = self.uploadAssetViewShowFrame;
//        self.uploadAssetTitleView.frame = self.uploadAssetTitleViewShowFrame;
//        self.uploadAssetImageView.frame = self.uploadAssetImageViewShowFrame;
//        self.uploadAssetButton.frame = self.uploadAssetView.frame;
//        self.createFolderView.frame = self.createFolderViewShowFrame;
//        self.createFolderTitleView.frame = self.createFolderTitleViewShowFrame;
//        self.createFolderImageView.frame = self.createFolderImageViewShowFrame;
//        self.createFolderButton.frame = self.createFolderView.frame;
//    } completion:^(BOOL finished) {
//        [self.uploadAssetTitleView addSubview:self.uploadAssetTitleLabel];
//        [self.createFolderTitleView addSubview:self.createFolderTitleLabel];
//    }];
}

- (void)hideView {
    [UIView animateWithDuration:0.5 animations:^{
        [self.uploadAssetTitleLabel removeFromSuperview];
        [self.createFolderTitleLabel removeFromSuperview];
        self.closeButton.transform = CGAffineTransformMakeRotation(0);
        self.uploadAssetView.frame = self.uploadAssetViewHideFrame;
        self.uploadAssetTitleView.frame = self.uploadAssetTitleViewHideFrame;
        self.uploadAssetImageView.frame = self.uploadAssetImageViewHideFrame;
        self.uploadAssetButton.frame = self.uploadAssetView.frame;
        self.createFolderView.frame = self.createFolderViewHideFrame;
        self.createFolderTitleView.frame = self.createFolderTitleViewHideFrame;
        self.createFolderImageView.frame = self.createFolderImageViewHideFrame;
        self.createFolderButton.frame = self.createFolderView.frame;
    } completion:^(BOOL finished) {
        [self removeFromSuperview];
    }];
}

- (void)uploadFile {
    [UIView animateWithDuration:0.3 animations:^{
        [self.uploadAssetTitleLabel removeFromSuperview];
        [self.createFolderTitleLabel removeFromSuperview];
        self.closeButton.transform = CGAffineTransformMakeRotation(0);
        self.uploadAssetView.frame = self.uploadAssetViewHideFrame;
        self.uploadAssetTitleView.frame = self.uploadAssetTitleViewHideFrame;
        self.uploadAssetImageView.frame = self.uploadAssetImageViewHideFrame;
        self.uploadAssetButton.frame = self.uploadAssetView.frame;
        self.createFolderView.frame = self.createFolderViewHideFrame;
        self.createFolderTitleView.frame = self.createFolderTitleViewHideFrame;
        self.createFolderImageView.frame = self.createFolderImageViewHideFrame;
        self.createFolderButton.frame = self.createFolderView.frame;
    } completion:^(BOOL finished) {
        [self removeFromSuperview];
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [self.fileViewController performSelector:self.uploadAction];
    }];
}

- (void)createFolder {
    [UIView animateWithDuration:0.3 animations:^{
        [self.uploadAssetTitleLabel removeFromSuperview];
        [self.createFolderTitleLabel removeFromSuperview];
        self.closeButton.transform = CGAffineTransformMakeRotation(0);
        self.uploadAssetView.frame = self.uploadAssetViewHideFrame;
        self.uploadAssetTitleView.frame = self.uploadAssetTitleViewHideFrame;
        self.uploadAssetImageView.frame = self.uploadAssetImageViewHideFrame;
        self.uploadAssetButton.frame = self.uploadAssetView.frame;
        self.createFolderView.frame = self.createFolderViewHideFrame;
        self.createFolderTitleView.frame = self.createFolderTitleViewHideFrame;
        self.createFolderImageView.frame = self.createFolderImageViewHideFrame;
        self.createFolderButton.frame = self.createFolderView.frame;
    } completion:^(BOOL finished) {
        [self removeFromSuperview];
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [self.fileViewController performSelector:self.createFolderAction];
    }];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self hideView];
}

@end
