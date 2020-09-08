//
//  CloudFileTableViewCell.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudFileTableViewCell.h"
#import "FileThumbnail.h"
#import "File.h"
#import "File+Remote.h"
#import "TransportTaskHandle.h"
#import "AppDelegate.h"

@interface CloudFileTableViewCell ()

@property (nonatomic, strong) UIView *fileThumbnailView;
@property (nonatomic, strong) UIImageView *fileImageView;
@property (nonatomic, strong) UILabel *fileTitleLable;
@property (nonatomic, strong) UILabel *fileTimeLable;
@property (nonatomic, strong) UILabel *fileSizeLable;
@property (nonatomic, strong) UIButton *fileCheckBox;
@property (nonatomic, strong) UIImageView *fileDownloadImageView;

@property (nonatomic, strong) NSDateFormatter *fileDateFormatter;

@end

@implementation CloudFileTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self.imageView removeFromSuperview];
        [self.textLabel removeFromSuperview];
        [self.detailTextLabel removeFromSuperview];
        
        self.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        
        self.fileDateFormatter =[[NSDateFormatter alloc] init];
        [self.fileDateFormatter setDateFormat:@"YYYY/MM/dd HH:mm:ss"];
        [self.fileDateFormatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:8 * 3600]];
        
        self.fileThumbnailView = [[UIView alloc] initWithFrame:CGRectZero];
        [self.contentView addSubview:self.fileThumbnailView];
        
        self.fileImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self.fileThumbnailView addSubview:self.fileImageView];
        
        self.fileTitleLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.fileTitleLable.font = [UIFont systemFontOfSize:17.0f];
        self.fileTitleLable.textAlignment = NSTextAlignmentLeft;
        self.fileTitleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        [self.contentView addSubview:self.fileTitleLable];
        
        self.fileTimeLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.fileTimeLable.font = [UIFont systemFontOfSize:15.0f];
        self.fileTimeLable.textAlignment = NSTextAlignmentLeft;
        self.fileTimeLable.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        [self.contentView addSubview:self.fileTimeLable];
        
        self.fileSizeLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.fileSizeLable.font = [UIFont systemFontOfSize:10.0f];
        self.fileSizeLable.textAlignment = NSTextAlignmentRight;
        self.fileSizeLable.textColor = [CommonFunction colorWithString:@"999999" alpha:1.0f];
        [self.contentView addSubview:self.fileSizeLable];
        
        self.fileCheckBox = [[UIButton alloc] initWithFrame:CGRectZero];
        self.fileCheckBox.hidden = YES;
        [self.contentView addSubview:self.fileCheckBox];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    
    self.fileThumbnailView.frame = CGRectMake(15, 6, 56, 56);
    self.fileImageView.frame = CGRectMake(4, 4, 48, 48);
    [FileThumbnail imageWithFile:self.file imageView:self.fileImageView];
    
    if (self.fileSelectState) {
        self.fileTitleLable.frame = CGRectMake(CGRectGetMaxX(self.fileThumbnailView.frame)+10, 11, CGRectGetWidth(self.frame)-CGRectGetMaxX(self.fileThumbnailView.frame)-10-15-22-15, 22);
    } else {
        self.fileTitleLable.frame = CGRectMake(CGRectGetMaxX(self.fileThumbnailView.frame)+10, 11, CGRectGetWidth(self.frame)-CGRectGetMaxX(self.fileThumbnailView.frame)-10-15, 22);
    }
    self.fileTitleLable.text = self.file.fileName;
    
    if ([self.file isFolder]) {
        self.fileSizeLable.frame = CGRectZero;
    } else {
        CGSize adjustSizeSize = [CommonFunction labelSizeWithString:[CommonFunction pretySize:self.file.fileSize.longLongValue] font:[UIFont systemFontOfSize:12.0f]];
        self.fileSizeLable.text = [CommonFunction pretySize:self.file.fileSize.longLongValue];
        if (self.fileSelectState) {
            self.fileSizeLable.frame = CGRectMake(CGRectGetWidth(self.frame)-15-22-15-adjustSizeSize.width, CGRectGetMaxY(self.fileTitleLable.frame)+4, adjustSizeSize.width, 20);
        } else {
            self.fileSizeLable.frame = CGRectMake(CGRectGetWidth(self.frame)-15-adjustSizeSize.width, CGRectGetMaxY(self.fileTitleLable.frame)+4, adjustSizeSize.width, 20);
        }
    }
    

    self.fileTimeLable.frame = CGRectMake(CGRectGetMaxX(self.fileThumbnailView.frame)+10, CGRectGetMaxY(self.fileTitleLable.frame)+4, CGRectGetWidth(self.fileTitleLable.frame)-CGRectGetWidth(self.fileSizeLable.frame), 20);
    self.fileTimeLable.text = [self.fileDateFormatter stringFromDate:self.file.fileModifiedDate];
    
    self.fileCheckBox.frame = CGRectMake(CGRectGetWidth(self.frame)-15-22, (CGRectGetHeight(self.frame)-22)/2, 22, 22);
    
    if (self.file.fileType.integerValue == TypeImage) {
        NSString *fileDataLocalPath = [self.file fileDataLocalPath];
        if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
            if (!self.fileDownloadImageView.superview) {
                [self.fileThumbnailView addSubview:self.fileDownloadImageView];
            }
        } else {
            if (self.fileDownloadImageView.superview) {
                [self.fileDownloadImageView removeFromSuperview];
            }
        }
    }
}

- (void) setFileSelected:(BOOL)fileSelected {
    if (_fileSelected != fileSelected) {
        _fileSelected = fileSelected;
    }
    if (fileSelected) {
        [self.fileCheckBox setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.fileCheckBox setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    }
}

- (void)setFileSelectState:(BOOL)fileSelectState {
    if (_fileSelectState != fileSelectState) {
        _fileSelectState = fileSelectState;
    }
    if (fileSelectState) {
        self.fileCheckBox.hidden = NO;
    } else {
        self.fileCheckBox.hidden = YES;
    }
    [self layoutSubviews];
}

- (void)setFile:(File *)file {
    if (_file != file) {
        _file = file;
    }
}

- (void)refresh{
    [self layoutSubviews];
}

- (UIImageView*)fileDownloadImageView {
    if (!_fileDownloadImageView) {
        _fileDownloadImageView = [[UIImageView alloc] initWithFrame:CGRectMake(56-((56-48)/2-1+22), (56-48)/2-1, 22, 22)];
        _fileDownloadImageView.image = [UIImage imageNamed:@"ic_list_upload"];//ic_list_image_download,ic_transfer_upload
    }
    return _fileDownloadImageView;
}

- (void)prepareForReuse {
    self.fileImageView.image = nil;
    self.fileTimeLable.text = nil;
    self.fileTitleLable.text = nil;
    self.fileSizeLable.text = nil;
    [self.fileDownloadImageView removeFromSuperview];
}

@end
