//
//  CloudLoginServerAddressView.m
//  OneMail
//
//  Created by cse  on 15/11/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudLoginServerAddressView.h"
#import "AppDelegate.h"

static const CGFloat ServerAlertWidth      = 270.0f;
static const CGFloat ServerTitleTop        = 20.0f;
static const CGFloat ServerFieldViewTop    = 23.0f;
static const CGFloat ServerFieldViewLeft   = 15.0f;
static const CGFloat ServerFieldViewRight  = 15.0f;
static const CGFloat ServerFieldViewHeight = 30.0f;
static const CGFloat ServerFieldViewBottom = 15.0f;
static const CGFloat ServerFieldLeft       = 10.0f;
static const CGFloat ServerFieldRight      = 10.0f;
static const CGFloat ServerFieldHeight     = 18.0f;
static const CGFloat ServerControlHeight = 44.0f;

@interface CloudLoginServerAddressView ()<UITextFieldDelegate>

@property (nonatomic, strong) UIView *serverAlertView;
@property (nonatomic, strong) UITextField *serverField;

@end

@implementation CloudLoginServerAddressView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.5];
        self.serverAlertView = [[UIView alloc] initWithFrame:CGRectZero];
        self.serverAlertView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
        self.serverAlertView.layer.cornerRadius = 4;
        self.serverAlertView.layer.masksToBounds = YES;
        UIView *serverSettingView = [[UIView alloc] initWithFrame:CGRectZero];
        serverSettingView.backgroundColor = [UIColor clearColor];
        serverSettingView.layer.borderWidth = 0.25;
        serverSettingView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        UILabel *serverTitleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        serverTitleLabel.text = getLocalizedString(@"CloudLoginServerAddress", nil);
        serverTitleLabel.font = [UIFont systemFontOfSize:18.0f];
        serverTitleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        serverTitleLabel.textAlignment = NSTextAlignmentCenter;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:serverTitleLabel limitSize:CGSizeMake(ServerAlertWidth-ServerFieldViewLeft-ServerFieldViewRight, 1000)];
        serverTitleLabel.frame = CGRectMake((ServerAlertWidth-adjustTitleSize.width)/2, ServerTitleTop, adjustTitleSize.width, adjustTitleSize.height);
        [serverSettingView addSubview:serverTitleLabel];
        UIView *serverFieldView = [[UIView alloc] initWithFrame:CGRectMake(ServerFieldViewLeft, CGRectGetMaxY(serverTitleLabel.frame)+ ServerFieldViewTop, ServerAlertWidth-ServerFieldViewLeft-ServerFieldViewRight, ServerFieldViewHeight)];
        serverFieldView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        serverFieldView.layer.borderWidth = 0.5;
        serverFieldView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        serverFieldView.layer.cornerRadius = 4;
        serverFieldView.layer.masksToBounds = YES;
        [serverSettingView addSubview:serverFieldView];
        self.serverField = [[UITextField alloc] initWithFrame:CGRectMake(ServerFieldLeft, (ServerFieldViewHeight-ServerFieldHeight)/2, CGRectGetWidth(serverFieldView.frame)-ServerFieldLeft-ServerFieldRight, ServerFieldHeight)];
        self.serverField.text = [UserSetting defaultSetting].cloudService;
        self.serverField.font = [UIFont systemFontOfSize:13.0f];
        self.serverField.textColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
        self.serverField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.serverField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.serverField.delegate = self;
        [serverFieldView addSubview:self.serverField];
        
        serverSettingView.frame = CGRectMake(0, 0, ServerAlertWidth, ServerTitleTop+CGRectGetHeight(serverTitleLabel.frame)+ServerFieldViewTop+ServerFieldViewHeight+ServerFieldViewBottom);
        [self.serverAlertView addSubview:serverSettingView];
        
        UIView *serverControlView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(serverSettingView.frame), ServerAlertWidth, ServerControlHeight)];
        serverControlView.backgroundColor = [UIColor clearColor];
        [self.serverAlertView addSubview:serverControlView];
        UIButton *serverCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, ServerAlertWidth/2, ServerControlHeight)];
        serverCancelButton.backgroundColor = [UIColor clearColor];
        serverCancelButton.layer.borderWidth = 0.25;
        serverCancelButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [serverCancelButton setTitle:getLocalizedString(@"Cancel", nil) forState:UIControlStateNormal];
        [serverCancelButton setTitleColor:[CommonFunction colorWithString:@"8e8e8e" alpha:1.0f] forState:UIControlStateNormal];
        [serverCancelButton addTarget:self action:@selector(cancel:) forControlEvents:UIControlEventTouchUpInside];
        [serverControlView addSubview:serverCancelButton];
        UIButton *serverConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(ServerAlertWidth/2, 0, ServerAlertWidth/2, ServerControlHeight)];
        serverConfirmButton.backgroundColor = [UIColor clearColor];
        serverConfirmButton.layer.borderWidth = 0.25;
        serverConfirmButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [serverConfirmButton setTitle:getLocalizedString(@"Confirm", nil) forState:UIControlStateNormal];
        [serverConfirmButton setTitleColor:[CommonFunction colorWithString:@"2e90e5" alpha:1.0f] forState:UIControlStateNormal];
        [serverConfirmButton addTarget:self action:@selector(confirm:) forControlEvents:UIControlEventTouchUpInside];
        [serverControlView addSubview:serverConfirmButton];
        
        self.serverAlertView.bounds = CGRectMake(0, 0, ServerAlertWidth, CGRectGetHeight(serverSettingView.frame)+ServerControlHeight);
        self.serverAlertView.frame = CGRectMake((CGRectGetWidth(self.frame)-ServerAlertWidth)/2, (CGRectGetHeight(self.frame)-CGRectGetHeight(self.serverAlertView.frame))/2, ServerAlertWidth, CGRectGetHeight(self.serverAlertView.frame));
        [self addSubview:self.serverAlertView];
    }
    return self;
}

- (void)cancel:(UIButton*)sender {
    [self removeFromSuperview];
}

- (void)confirm:(UIButton*)sender {
    UserSetting *userSetting = [UserSetting defaultSetting];
    userSetting.cloudService = self.serverField.text;
    [self removeFromSuperview];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [textField becomeFirstResponder];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.serverAlertView.frame;
    if (rect.origin.y+rect.size.height > (216+40)) {
        rect.origin.y = self.frame.size.height-216-40-rect.size.height;
        self.serverAlertView.frame = rect;
    }
    [UIView commitAnimations];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self endEditing:YES];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.serverAlertView.frame;
    rect.origin.y = (CGRectGetHeight(self.frame)-CGRectGetHeight(self.serverAlertView.frame))/2;
    self.serverAlertView.frame = rect;
    [UIView commitAnimations];
}
@end

