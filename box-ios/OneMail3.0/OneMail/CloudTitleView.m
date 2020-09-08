//
//  CloudTitleView.m
//  OneMail
//
//  Created by cse  on 15/12/31.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudTitleView.h"
#import "AppDelegate.h"

@interface CloudTitleView ()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIView *mySpacePoint;
@property (nonatomic, strong) UIView *shareWithMePoint;
@property (nonatomic, strong) UIView *teamSpacePoint;

@end

@implementation CloudTitleView

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.LocalizedChange" object:nil];
}

- (id)initWithStyle:(CloudTitleViewStyle)titleViewStyle frame:(CGRect)frame{
    self = [super init];
    if (self) {
        self.frame = frame;
        self.titleStyle = titleViewStyle;
        self.titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.titleLabel.font = [UIFont boldSystemFontOfSize:18.0f];
        self.titleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        self.titleLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:self.titleLabel];
        
        self.mySpacePoint = [[UIView alloc] init];
        self.mySpacePoint.bounds = CGRectMake(0, 0, 5, 5);
        self.mySpacePoint.layer.cornerRadius = 5/2;
        self.mySpacePoint.layer.masksToBounds = YES;
        self.mySpacePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:0.5];
//        [self addSubview:self.mySpacePoint];
        
        self.shareWithMePoint = [[UIView alloc] init];
        self.shareWithMePoint.bounds = CGRectMake(0, 0, 5, 5);
        self.shareWithMePoint.layer.cornerRadius = 5/2;
        self.shareWithMePoint.layer.masksToBounds = YES;
        self.shareWithMePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:0.5f];
//        [self addSubview:self.shareWithMePoint];
        
        self.teamSpacePoint = [[UIView alloc] init];
        self.teamSpacePoint.bounds = CGRectMake(0, 0, 5, 5);
        self.teamSpacePoint.layer.cornerRadius = 5/2;
        self.teamSpacePoint.layer.masksToBounds = YES;
        self.teamSpacePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:0.5f];
//        [self addSubview:self.teamSpacePoint];
        
        switch (titleViewStyle) {
            case CloudTitleMySpaceStyle:
                self.titleLabel.text = getLocalizedString(@"CloudFileTitle", nil);
                self.mySpacePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
                break;
            case CloudTitleShareWithMeStyle:
                self.titleLabel.text = getLocalizedString(@"CloudShareTitle", nil);
                self.shareWithMePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
                break;
            case CloudTitleTeamSapceStyle:
                self.titleLabel.text = getLocalizedString(@"CloudTeamSpaceTitle", nil);
                self.teamSpacePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
                break;
            default:
                break;
        }
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshTitle) name:@"com.huawei.onemail.LocalizedChange" object:nil];
    }
    return self;
}

- (void)refreshTitle{
    switch (self.titleStyle) {
        case CloudTitleMySpaceStyle:
            self.titleLabel.text = getLocalizedString(@"CloudFileTitle",nil);
            self.mySpacePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
            break;
        case CloudTitleShareWithMeStyle:
            self.titleLabel.text = getLocalizedString(@"CloudShareTitle",nil);
            self.shareWithMePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
            break;
        case CloudTitleTeamSapceStyle:
            self.titleLabel.text = getLocalizedString(@"CloudTeamSpaceTitle",nil);
            self.teamSpacePoint.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
            break;
        default:
            break;
    }
}

- (void)setViewFrame:(CGRect)frame {
    self.frame = frame;
    self.titleLabel.frame = CGRectMake(0, 0, frame.size.width, 24);
    self.mySpacePoint.frame = CGRectMake((frame.size.width-5)/2-5/2-10, CGRectGetMaxY(self.titleLabel.frame)+1, 5, 5);
    self.shareWithMePoint.frame = CGRectMake((frame.size.width-5)/2, CGRectGetMaxY(self.titleLabel.frame)+1, 5, 5);
    self.teamSpacePoint.frame = CGRectMake((frame.size.width-5)/2+5/2+10, CGRectGetMaxY(self.titleLabel.frame)+1, 5, 5);
}


@end
