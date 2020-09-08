//
//  CloudFileVersionHeaderCell.m
//  OneMail
//
//  Created by cse  on 15/11/19.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define FileImageViewDistanceTop 3.5
#define FileImageViewDistanceButtom 14
#define FileImageViewDistanceLeft 15
#define FileImageViewDistanceRight 10
#define FileImageViewWidth 48
#define FileImageViewHeight 48

#define FileNameLabelDistanceTop 17.5
#define FileNameLabelHeight 20
#import "CloudFileVersionHeaderCell.h"
#import "FileThumbnail.h"
#import "AppDelegate.h"
@interface CloudFileVersionHeaderCell()
@property (nonatomic,strong) UIImageView *headerview;
@property (nonatomic,strong) UILabel *fileNameLabel;
@end
@implementation CloudFileVersionHeaderCell
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.headerview = [[UIImageView alloc] init];
        [self.contentView addSubview:self.headerview];
        
        self.fileNameLabel = [[UILabel alloc] init];
        [self.fileNameLabel setTextColor:[UIColor blackColor]];
        self.fileNameLabel.font = [UIFont systemFontOfSize:14];
        self.fileNameLabel.textAlignment = NSTextAlignmentCenter;
        [self.contentView addSubview:self.fileNameLabel];
    }
    return self;
}
- (void)layoutSubviews{
    self.headerview.frame = CGRectMake(FileImageViewDistanceLeft, FileImageViewDistanceTop, FileImageViewWidth, FileImageViewHeight);
    if (self.file) {
        [FileThumbnail imageWithFile:self.file imageView:self.headerview];
        self.fileNameLabel.text = self.file.fileName;
        self.fileNameLabel.frame = CGRectMake(CGRectGetMaxX(self.headerview.frame) + FileImageViewDistanceRight, FileNameLabelDistanceTop, [CommonFunction labelSizeWithLabel:self.fileNameLabel limitSize:CGSizeMake(1000, 20)].width, FileNameLabelHeight);
    }
}
- (void)setFile:(File *)file{
    _file = file;
    [self layoutIfNeeded];
}
@end
