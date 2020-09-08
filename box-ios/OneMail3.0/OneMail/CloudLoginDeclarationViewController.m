//
//  CloudLoginDeclarationViewController.m
//  OneMail
//
//  Created by cse  on 15/11/25.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#import "CloudLoginDeclarationViewController.h"
#import "AppDelegate.h"

@interface CloudLoginDeclarationViewController ()<UITextFieldDelegate>

@end

@implementation CloudLoginDeclarationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    
    UIView *declarationTitleView = [[UIView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height, CGRectGetWidth(self.view.frame), navigationFrame.size.height)];
    declarationTitleView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:declarationTitleView];
    
    UILabel *declarationTitleLabel = [[UILabel alloc]initWithFrame:CGRectZero];
    declarationTitleLabel.text = getLocalizedString(@"CloudDeclarationTitle", nil);
    declarationTitleLabel.font = [UIFont systemFontOfSize:18.0f];
    declarationTitleLabel.textColor = [UIColor blackColor];
    declarationTitleLabel.textAlignment = NSTextAlignmentCenter;
    declarationTitleLabel.backgroundColor = [UIColor clearColor];
    CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:declarationTitleLabel limitSize:CGSizeMake(1000, 1000)];
    declarationTitleLabel.frame = CGRectMake((CGRectGetWidth(declarationTitleView.frame)-adjustTitleSize.width)/2, (CGRectGetHeight(declarationTitleView.frame)-adjustTitleSize.height)/2, adjustTitleSize.width, adjustTitleSize.height);
    [declarationTitleView addSubview:declarationTitleLabel];
    
    UITextField *declarationTextField = [[UITextField alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationFrame.size.height-49)];
    declarationTextField.font = [UIFont systemFontOfSize:15.0f];
    declarationTextField.text = self.cloudLoginDeclarationString;
    declarationTextField.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    declarationTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    declarationTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentTop;
    declarationTextField.layer.borderWidth = 0.5;
    declarationTextField.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    declarationTextField.delegate = self;
    [self.view addSubview:declarationTextField];
    
    UIView *declarationControlView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(self.view.frame)-49, CGRectGetWidth(self.view.frame), 49)];
    declarationControlView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:declarationControlView];
    
    UILabel *declarationCancelLabel = [[UILabel alloc]initWithFrame: CGRectZero];
    declarationCancelLabel.font = [UIFont systemFontOfSize:16.0f];
    declarationCancelLabel.text = getLocalizedString(@"CloudDeclarationDisagree", nil);
    declarationCancelLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0];
    declarationCancelLabel.textAlignment = NSTextAlignmentLeft;
    CGSize adjustCancelLabelSize = [CommonFunction labelSizeWithLabel:declarationCancelLabel limitSize:CGSizeMake(1000, 1000)];
    declarationCancelLabel.frame = CGRectMake(15, 0, adjustCancelLabelSize.width, 49);
    UIButton *declarationCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 15+adjustCancelLabelSize.width, 49)];
    [declarationCancelButton addSubview:declarationCancelLabel];
    [declarationCancelButton addTarget:self action:@selector(cancel:) forControlEvents:UIControlEventTouchUpInside];
    [declarationControlView addSubview:declarationCancelButton];
    
    UILabel *declarationConfirmLabel = [[UILabel alloc]initWithFrame:CGRectZero];
    declarationConfirmLabel.font = [UIFont systemFontOfSize:16.0f];
    declarationConfirmLabel.text = getLocalizedString(@"CloudDeclarationAgree", nil);
    declarationConfirmLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0];
    declarationConfirmLabel.textAlignment = NSTextAlignmentRight;
    CGSize adjustConfirmLabelSize = [CommonFunction labelSizeWithLabel:declarationConfirmLabel limitSize:CGSizeMake(1000, 1000)];
    declarationConfirmLabel.frame = CGRectMake(0, 0, adjustConfirmLabelSize.width, 49);
    UIButton *declarationConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(declarationControlView.frame)-15-CGRectGetWidth(declarationConfirmLabel.frame), 0, adjustConfirmLabelSize.width+15, 49)];
    [declarationConfirmButton addSubview:declarationConfirmLabel];
    [declarationConfirmButton addTarget:self action:@selector(confirm:) forControlEvents:UIControlEventTouchUpInside];
    [declarationControlView addSubview:declarationConfirmButton];
}

- (void)cancel:(UIButton*)sender {
    [self dismissViewControllerAnimated:YES completion:^{

    }];
}

- (void)confirm:(UIButton*)sender {
    sender.enabled = NO;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager setDeclarationSignStatus:self.cloudLoginDeclarationId succeed:^(id retobj) {
        sender.enabled = YES;
        self.confirmBlock();
        [self dismissViewControllerAnimated:NO completion:^{}];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            sender.enabled = YES;
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudDeclarationFailed", nil)];
        });
    }];
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    return NO;
}
@end
