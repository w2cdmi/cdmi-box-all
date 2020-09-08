//
//  CloudFileVersionCell.m
//  OneMail
//
//  Created by cse  on 15/11/19.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define VersionLabelLeft 20
#define VersionLabelTop 11
#define VersionLabelRight 10
#define VersionLabelHeight 20
#define VersionLabelWidth 36

#define VersionSizeLabelTop 11
#define VersionSizeLabelRight 15
#define VersionSizeLabelHeight 22

#define VersionDateLabelTop 11
#define VersionDateLabelRight 10
#define VersionDateLabelHeight 22

#define VersionDownloadButtonTop 11
#define VersionDownloadButtonRight 15
#define VersionDownloadButtonHeight 22
#define VersionDownloadButtonWidth 22
#import "CloudFileVersionCell.h"
#import "AppDelegate.h"
#import "TransportVersionDownloadTaskHandle.h"
#import "TransportTask.h"
#import "CloudTransferViewController.h"
#import "CloudPreviewController.h"

@interface CloudFileVersionCell()
@property (nonatomic,strong) TransportVersionDownloadTaskHandle *taskHandle;
@property (nonatomic,strong) UILabel *versionSizeLabel;
@property (nonatomic,strong) UILabel *versionDateLabel;
@property (nonatomic,strong) UIButton *versionDownloadButton;
@end
@implementation CloudFileVersionCell
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        [self.imageView removeFromSuperview];
        [self.textLabel removeFromSuperview];
        [self.detailTextLabel removeFromSuperview];
        
        self.versionLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.versionLabel.backgroundColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f];
        self.versionLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        self.versionLabel.textAlignment = NSTextAlignmentCenter;
        self.versionLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:12.0f];
        self.versionLabel.layer.cornerRadius = VersionLabelHeight/2;
        self.versionLabel.layer.masksToBounds = YES;
        [self.contentView addSubview:self.versionLabel];
        
        self.versionSizeLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.versionSizeLabel.backgroundColor = [UIColor clearColor];
        self.versionSizeLabel.font = [UIFont systemFontOfSize:14.0f];
        self.versionSizeLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.versionSizeLabel.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.versionSizeLabel];
        
        self.versionDateLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.versionDateLabel.backgroundColor = [UIColor clearColor];
        self.versionDateLabel.font = [UIFont systemFontOfSize:14.0f];
        self.versionDateLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.versionDateLabel.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.versionDateLabel];
        
        self.versionDownloadButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.versionDownloadButton addTarget:self action:@selector(downloadVersion) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.versionDownloadButton];
    }
    return self;
}

- (void)layoutSubviews{
    [super layoutSubviews];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.versionLabel.frame = CGRectMake(VersionLabelLeft, VersionLabelTop,VersionLabelWidth , VersionLabelHeight);
    
    self.versionDateLabel.text = self.version.versionModifiedDateString;
    CGSize dateSize = [CommonFunction labelSizeWithLabel:self.versionDateLabel limitSize:CGSizeMake(1000, 1000)];
    self.versionDateLabel.frame = CGRectMake(CGRectGetMaxX(self.versionLabel.frame)+VersionLabelRight, VersionDateLabelTop, dateSize.width, VersionDateLabelHeight);
    
    self.versionSizeLabel.text = [CommonFunction pretySize:self.version.versionSize.longLongValue];
    CGSize sizeSize = [CommonFunction labelSizeWithLabel:self.versionSizeLabel limitSize:CGSizeMake(1000, 1000)];
    self.versionSizeLabel.frame = CGRectMake(CGRectGetMaxX(self.versionDateLabel.frame)+VersionDateLabelRight, VersionSizeLabelTop, sizeSize.width, VersionSizeLabelHeight);
    
    self.versionDownloadButton.frame = CGRectMake(CGRectGetWidth(self.frame) - VersionDownloadButtonRight - VersionDownloadButtonWidth, VersionDownloadButtonTop, VersionDownloadButtonWidth, VersionDownloadButtonHeight);
    NSString *versionDataLocalPath = [self.version versionDataLocalPath];
    if (versionDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:versionDataLocalPath]) {
        [self.versionDownloadButton setImage:[UIImage imageNamed:@"ic_radiobutton_on_press"] forState:UIControlStateNormal];
    } else {
        [self.versionDownloadButton setImage:[UIImage imageNamed:@"ic_transfer_download"] forState:UIControlStateNormal];
    }
}

- (void)setVersion:(Version *)version{
    _version = version;
}

- (void)downloadVersion{
    NSString *versionDataLocalPath = [self.version versionDataLocalPath];
    if (versionDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:versionDataLocalPath]) {
        CloudPreviewController *previewController = [[CloudPreviewController alloc] initWithVersion:self.version];
        [self.versionController.navigationController pushViewController:previewController animated:YES];
    } else {
        [self.version download];
        CloudTransferViewController *transferController = [[CloudTransferViewController alloc] initWithFile:nil];
        transferController.rootViewController = self.versionController;
        [self.versionController.navigationController pushViewController:transferController animated:YES];
    }
}

- (void)prepareForReuse {
    self.versionLabel.text = nil;
    self.versionLabel.backgroundColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f];
    self.versionDateLabel.text = nil;
    self.versionSizeLabel.text = nil;
}
@end
