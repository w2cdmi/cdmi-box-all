//
//  ContactUserAddRemarkView.m
//  OneMail
//
//  Created by cse  on 15/12/11.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "ContactUserAddRemarkView.h"
#import "AppDelegate.h"
#import "User.h"
#import "ContactUserViewController.h"

static const CGFloat RemarkAlertWidth      = 270.0f;
static const CGFloat RemarkTitleTop        = 20.0f;
static const CGFloat RemarkFieldViewTop    = 23.0f;
static const CGFloat RemarkFieldViewLeft   = 15.0f;
static const CGFloat RemarkFieldViewRight  = 15.0f;
static const CGFloat RemarkFieldViewHeight = 30.0f;
static const CGFloat RemarkFieldViewBottom = 15.0f;
static const CGFloat RemarkFieldLeft       = 10.0f;
static const CGFloat RemarkFieldRight      = 10.0f;
static const CGFloat RemarkFieldHeight     = 18.0f;
static const CGFloat RemarkControlHeight = 44.0f;

@interface ContactUserAddRemarkView ()<UITextFieldDelegate>

@property (nonatomic, strong) UIView *remarkAlertView;
@property (nonatomic, strong) UITextField *remarkField;

@end

@implementation ContactUserAddRemarkView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.remarkAlertView = [[UIView alloc] initWithFrame:CGRectZero];
        self.remarkAlertView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
        self.remarkAlertView.layer.cornerRadius = 4;
        self.remarkAlertView.layer.masksToBounds = YES;
        
        UIView *remarkSettingView = [[UIView alloc] initWithFrame:CGRectZero];
        remarkSettingView.backgroundColor = [UIColor clearColor];
        remarkSettingView.layer.borderWidth = 0.25;
        remarkSettingView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        
        UILabel *remarkTitleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        remarkTitleLabel.text = NSLocalizedString(@"ContactMenuAddRemark", nil);
        remarkTitleLabel.font = [UIFont systemFontOfSize:18.0f];
        remarkTitleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        remarkTitleLabel.textAlignment = NSTextAlignmentCenter;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:remarkTitleLabel limitSize:CGSizeMake(RemarkAlertWidth-RemarkFieldViewLeft-RemarkFieldViewRight, 1000)];
        remarkTitleLabel.frame = CGRectMake((RemarkAlertWidth-adjustTitleSize.width)/2, RemarkTitleTop, adjustTitleSize.width, adjustTitleSize.height);
        [remarkSettingView addSubview:remarkTitleLabel];
        
        UIView *remarkFieldView = [[UIView alloc] initWithFrame:CGRectMake(RemarkFieldViewLeft, CGRectGetMaxY(remarkTitleLabel.frame)+ RemarkFieldViewTop, RemarkAlertWidth-RemarkFieldViewLeft-RemarkFieldViewRight, RemarkFieldViewHeight)];
        remarkFieldView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        remarkFieldView.layer.borderWidth = 0.5;
        remarkFieldView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        remarkFieldView.layer.cornerRadius = 4;
        remarkFieldView.layer.masksToBounds = YES;
        [remarkSettingView addSubview:remarkFieldView];
        
        self.remarkField = [[UITextField alloc] initWithFrame:CGRectMake(RemarkFieldLeft, (RemarkFieldViewHeight-RemarkFieldHeight)/2, CGRectGetWidth(remarkFieldView.frame)-RemarkFieldLeft-RemarkFieldRight, RemarkFieldHeight)];
        self.remarkField.font = [UIFont systemFontOfSize:13.0f];
        self.remarkField.textColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
        self.remarkField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.remarkField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.remarkField.delegate = self;
        [remarkFieldView addSubview:self.remarkField];
        
        remarkSettingView.frame = CGRectMake(0, 0, RemarkAlertWidth, RemarkTitleTop+CGRectGetHeight(remarkTitleLabel.frame)+RemarkFieldViewTop+RemarkFieldViewHeight+RemarkFieldViewBottom);
        [self.remarkAlertView addSubview:remarkSettingView];
        
        UIView *RemarkControlView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(remarkSettingView.frame), RemarkAlertWidth, RemarkControlHeight)];
        RemarkControlView.backgroundColor = [UIColor clearColor];
        [self.remarkAlertView addSubview:RemarkControlView];
        UIButton *RemarkCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, RemarkAlertWidth/2, RemarkControlHeight)];
        RemarkCancelButton.backgroundColor = [UIColor clearColor];
        RemarkCancelButton.layer.borderWidth = 0.25;
        RemarkCancelButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [RemarkCancelButton setTitle:NSLocalizedString(@"Cancel", nil) forState:UIControlStateNormal];
        [RemarkCancelButton setTitleColor:[CommonFunction colorWithString:@"8e8e8e" alpha:1.0f] forState:UIControlStateNormal];
        [RemarkCancelButton addTarget:self action:@selector(cancel:) forControlEvents:UIControlEventTouchUpInside];
        [RemarkControlView addSubview:RemarkCancelButton];
        
        UIButton *RemarkConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(RemarkAlertWidth/2, 0, RemarkAlertWidth/2, RemarkControlHeight)];
        RemarkConfirmButton.backgroundColor = [UIColor clearColor];
        RemarkConfirmButton.layer.borderWidth = 0.25;
        RemarkConfirmButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [RemarkConfirmButton setTitle:NSLocalizedString(@"Confirm", nil) forState:UIControlStateNormal];
        [RemarkConfirmButton setTitleColor:[CommonFunction colorWithString:@"2e90e5" alpha:1.0f] forState:UIControlStateNormal];
        [RemarkConfirmButton addTarget:self action:@selector(confirm:) forControlEvents:UIControlEventTouchUpInside];
        [RemarkControlView addSubview:RemarkConfirmButton];
        
        self.remarkAlertView.bounds = CGRectMake(0, 0, RemarkAlertWidth, CGRectGetHeight(remarkSettingView.frame)+RemarkControlHeight);
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
        self.remarkAlertView.frame = CGRectMake((CGRectGetWidth(self.frame)-RemarkAlertWidth)/2, (CGRectGetHeight(self.frame)-CGRectGetHeight(self.remarkAlertView.frame))/2-statusBarFrame.size.height-navigationBarFrame.size.height, RemarkAlertWidth, CGRectGetHeight(self.remarkAlertView.frame));
        [self addSubview:self.remarkAlertView];
    }
    return self;
}

- (void)cancel:(UIButton*)sender {
    [self removeFromSuperview];
}

- (void)confirm:(UIButton*)sender {
    if (self.remarkField.text) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
        [ctx performBlockAndWait:^{
            User *shadow = (User*)[ctx objectWithID:self.user.objectID];
            shadow.userRemark = self.remarkField.text;
            [ctx save:nil];
        }];
        [self.userViewController refreshContactUserRemark:self.remarkField.text];
    }
    [self removeFromSuperview];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [textField becomeFirstResponder];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.remarkAlertView.frame;
    if (rect.origin.y+rect.size.height > (216+40)) {
        rect.origin.y = self.frame.size.height-20-44-216-40-rect.size.height;
        self.remarkAlertView.frame = rect;
    }
    [UIView commitAnimations];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self endEditing:YES];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.remarkAlertView.frame;
    rect.origin.y = (CGRectGetHeight(self.frame)-CGRectGetHeight(self.remarkAlertView.frame))/2-20-44;
    self.remarkAlertView.frame = rect;
    [UIView commitAnimations];
}
@end

