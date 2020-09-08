//
//  CloudLoginSecurityAlertView.m
//  OneMail
//
//  Created by cse  on 15/11/27.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudLoginSecurityAlertView.h"
#import "AppDelegate.h"

static const CGFloat SecurityAlertWidth    = 270.0f;
static const CGFloat SecurityTitleTop      = 20.0f;
static const CGFloat SecurityTitleBottom   = 15.0f;
static const CGFloat SecurityTitleLeft     = 15.0f;
static const CGFloat SecurityTitleRight    = 15.0f;
static const CGFloat SecurityControlHeight = 44.0f;

@implementation CloudLoginSecurityAlertView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.5];
        
        UIView *securityAlertView = [[UIView alloc] initWithFrame:CGRectZero];
        securityAlertView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
        securityAlertView.layer.cornerRadius = 4;
        securityAlertView.layer.masksToBounds = YES;
        UIView *securityTitleView = [[UIView alloc] initWithFrame:CGRectZero];
        securityTitleView.backgroundColor = [UIColor clearColor];
        securityTitleView.layer.borderWidth = 0.25;
        securityTitleView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        UILabel *securityTitleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        securityTitleLabel.text = getLocalizedString(@"CloudSecurityMessage", nil);
        securityTitleLabel.font = [UIFont systemFontOfSize:14.0f];
        securityTitleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        securityTitleLabel.textAlignment = NSTextAlignmentCenter;
        securityTitleLabel.numberOfLines = 0;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:securityTitleLabel limitSize:CGSizeMake(SecurityAlertWidth-SecurityTitleLeft-SecurityTitleRight, 1000)];
        securityTitleLabel.frame = CGRectMake((SecurityAlertWidth-adjustTitleSize.width)/2, SecurityTitleTop, adjustTitleSize.width, adjustTitleSize.height);
        [securityTitleView addSubview:securityTitleLabel];
        securityTitleView.frame = CGRectMake(0, 0, SecurityAlertWidth, SecurityTitleTop+adjustTitleSize.height+SecurityTitleBottom);
        [securityAlertView addSubview:securityTitleView];
        
        UIView *securityControlView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(securityTitleView.frame), SecurityAlertWidth, SecurityControlHeight)];
        securityControlView.backgroundColor = [UIColor clearColor];
        [securityAlertView addSubview:securityControlView];
        UIButton *securityCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, SecurityAlertWidth/2, SecurityControlHeight)];
        securityCancelButton.backgroundColor = [UIColor clearColor];
        securityCancelButton.layer.borderWidth = 0.25;
        securityCancelButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [securityCancelButton setTitle:getLocalizedString(@"Cancel", nil) forState:UIControlStateNormal];
        
        [securityCancelButton setTitleColor:[CommonFunction colorWithString:@"8e8e8e" alpha:1.0f] forState:UIControlStateNormal];
        [securityCancelButton addTarget:self action:@selector(cancel:) forControlEvents:UIControlEventTouchUpInside];
        [securityControlView addSubview:securityCancelButton];
        UIButton *securityConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(SecurityAlertWidth/2, 0, SecurityAlertWidth/2, SecurityControlHeight)];
        securityConfirmButton.backgroundColor = [UIColor clearColor];
        securityConfirmButton.layer.borderWidth = 0.25;
        securityConfirmButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [securityConfirmButton setTitle:getLocalizedString(@"Confirm", nil) forState:UIControlStateNormal];
        [securityConfirmButton setTitleColor:[CommonFunction colorWithString:@"2e90e5" alpha:1.0f] forState:UIControlStateNormal];
        [securityConfirmButton addTarget:self action:@selector(confirm:) forControlEvents:UIControlEventTouchUpInside];
        [securityControlView addSubview:securityConfirmButton];
        
        securityAlertView.bounds = CGRectMake(0, 0, SecurityAlertWidth, CGRectGetHeight(securityTitleView.frame)+SecurityControlHeight);
        securityAlertView.frame = CGRectMake((CGRectGetWidth(self.frame)-CGRectGetWidth(securityAlertView.frame))/2, (CGRectGetHeight(self.frame)-CGRectGetHeight(securityAlertView.frame))/2, CGRectGetWidth(securityAlertView.frame), CGRectGetHeight(securityAlertView.frame));
        [self addSubview:securityAlertView];
    }
    return self;
}

- (void)cancel:(UIButton*)sender {
    [self removeFromSuperview];
}

- (void)confirm:(UIButton*)sender {
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[UserSetting defaultSetting].cloudService]];
    [self removeFromSuperview];
}

@end
