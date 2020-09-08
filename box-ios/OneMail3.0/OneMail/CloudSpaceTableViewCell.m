//
//  CloudSpaceTableViewCell.m
//  OneMail
//
//  Created by cse  on 15/11/5.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudSpaceTableViewCell.h"
#import "TeamSpace.h"
#import "AppDelegate.h"

@interface CloudSpaceTableViewCell ()

@property (nonatomic, strong) UIView *fileThumbnailView;
@property (nonatomic, strong) UIImageView *fileImageView;
@property (nonatomic, strong) UILabel *fileTitleLable;
@property (nonatomic, strong) UILabel *fileOwnerLable;
@property (nonatomic, strong) UILabel *fileMemberLable;

@end
@implementation CloudSpaceTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self.imageView removeFromSuperview];
        [self.textLabel removeFromSuperview];
        [self.detailTextLabel removeFromSuperview];
        
        self.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        
        self.fileThumbnailView = [[UIView alloc] initWithFrame:CGRectZero];
        [self.contentView addSubview:self.fileThumbnailView];
        
        self.fileImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self.fileThumbnailView addSubview:self.fileImageView];
        
        self.fileTitleLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.fileTitleLable.font = [UIFont systemFontOfSize:17.0f];
        self.fileTitleLable.textAlignment = NSTextAlignmentLeft;
        self.fileTitleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        [self.contentView addSubview:self.fileTitleLable];
        
        self.fileOwnerLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.fileOwnerLable.font = [UIFont systemFontOfSize:12.0f];
        self.fileOwnerLable.textAlignment = NSTextAlignmentLeft;
        self.fileOwnerLable.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        [self.contentView addSubview:self.fileOwnerLable];
        
        self.fileMemberLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.fileMemberLable.font = [UIFont systemFontOfSize:12.0f];
        self.fileMemberLable.textAlignment = NSTextAlignmentLeft;
        self.fileMemberLable.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        [self.contentView addSubview:self.fileMemberLable];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.fileThumbnailView.frame = CGRectMake(15, 6, 56, 56);
    self.fileImageView.frame = CGRectMake((56-48)/2, (56-48)/2, 48, 48);
    self.fileImageView.image = [UIImage imageNamed:@"ic_team"];
    self.fileTitleLable.frame = CGRectMake(CGRectGetMaxX(self.fileThumbnailView.frame)+10, 11, CGRectGetWidth(self.frame)-CGRectGetMaxX(self.fileThumbnailView.frame)-10-15, 22);
    self.fileTitleLable.text = self.teamSpace.teamName;

    self.fileOwnerLable.text = [NSString stringWithFormat:getLocalizedString(@"CloudTeamSpaceOwner", nil),self.teamSpace.teamOwnerName];
    CGSize adjustOwnerSize = [CommonFunction labelSizeWithLabel:self.fileOwnerLable limitSize:CGSizeMake(1000, 1000)];
    self.fileOwnerLable.frame = CGRectMake(CGRectGetMaxX(self.fileThumbnailView.frame)+10, CGRectGetMaxY(self.fileTitleLable.frame)+4, adjustOwnerSize.width, MAX(adjustOwnerSize.height, 20));
    
    self.fileMemberLable.text = [NSString stringWithFormat:getLocalizedString(@"CloudTeamSpaceMember", nil),self.teamSpace.teamMemberNum];
    CGSize adjustMemberSize = [CommonFunction labelSizeWithLabel:self.fileMemberLable limitSize:CGSizeMake(1000, 1000)];
    self.fileMemberLable.frame = CGRectMake(CGRectGetWidth(self.frame)-15-adjustMemberSize.width, CGRectGetMinY(self.fileOwnerLable.frame), adjustMemberSize.width, CGRectGetHeight(self.fileOwnerLable.frame));
}

- (void)setTeamSpace:(TeamSpace *)teamSpace {
    if (_teamSpace != teamSpace) {
        _teamSpace = teamSpace;
    }
}

@end
