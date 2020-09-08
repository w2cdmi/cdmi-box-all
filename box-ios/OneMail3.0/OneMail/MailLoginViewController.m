//
//  MailLoginViewController.m
//  OneMail
//
//  Created by cse  on 15/11/25.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MailLoginViewController.h"
#import "MailSessionViewController.h"
#import "AppDelegate.h"
#import "MenuViewController.h"
#import "UIView+Toast.h"
#import "User.h"

@interface MailLoginViewController ()<UITextFieldDelegate>

@property (nonatomic, strong) UILabel     *mailLoginTitleLabel;
@property (nonatomic, strong) UIButton    *mailSettingButton;
@property (nonatomic, strong) UITextField *mailAddressField;
@property (nonatomic, strong) UITextField *mailPasswordField;
@property (nonatomic, strong) UIButton    *mailLoginButton;
@property (nonatomic, strong) MailSessionViewController *mailSessionViewController;

@end

@implementation MailLoginViewController
- (id)init {
    self = [super init];
    if (self) {
        self.mailSessionViewController = [[MailSessionViewController alloc] init];
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        UserSetting *userSetting = [UserSetting defaultSetting];
        [appDelegate.remoteManager getEmailConfig:^(id retobj) {
            NSArray *mailAttributes = [retobj objectForKey:@"attributes"];
            for (NSDictionary *attribute in mailAttributes) {
                if ([[attribute objectForKey:@"name"] isEqualToString:@"portReceive"]) {
                    userSetting.emailPortReceive = [attribute objectForKey:@"value"];
                } else if ([[attribute objectForKey:@"name"] isEqualToString:@"portSend"]) {
                    userSetting.emailPortSend = [attribute objectForKey:@"value"];
                } else if ([[attribute objectForKey:@"name"] isEqualToString:@"protocolReceive"]) {
                    userSetting.emailProtocolReceive = [attribute objectForKey:@"value"];
                } else if ([[attribute objectForKey:@"name"] isEqualToString:@"protocolSend"]) {
                    userSetting.emailProtocolSend = [attribute objectForKey:@"value"];
                } else if ([[attribute objectForKey:@"name"] isEqualToString:@"serverReceive"]) {
                    userSetting.emailServerReceive = [attribute objectForKey:@"value"];
                } else if ([[attribute objectForKey:@"name"] isEqualToString:@"serverSend"]) {
                    userSetting.emailServerSend = [attribute objectForKey:@"value"];
                }
            }
            [self autoLogin];
        } failure:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"MailServerPrompt", nil)];
            });
        }];
        
    }
    return self;
}

- (void) viewDidLoad {
    [super viewDidLoad];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.mailLoginTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailLoginTitleLabel.text = NSLocalizedString(@"MailTitle", nil);
    
    self.mailSettingButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailSettingButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_nor"] forState:UIControlStateNormal];
    [self.mailSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_press"] forState:UIControlStateHighlighted];
    [self.mailSettingButton addTarget:self action:@selector(mailSettingButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    UILabel *mailAddressPrompt = [CommonFunction labelWithFrame:CGRectMake(30, statusBarFrame.size.height+navigationBarFrame.size.height+25, CGRectGetWidth(self.view.frame)-30-30, 20) textFont:[UIFont systemFontOfSize:14.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
    mailAddressPrompt.text = NSLocalizedString(@"MailLoginAddress", nil);
    [self.view addSubview:mailAddressPrompt];
    
    UIView *mailAddressView = [[UIView alloc]initWithFrame:CGRectMake(CGRectGetMinX(mailAddressPrompt.frame), CGRectGetMaxY(mailAddressPrompt.frame)+5, CGRectGetWidth(mailAddressPrompt.frame), 36)];
    mailAddressView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    mailAddressView.layer.cornerRadius = 4;
    mailAddressView.layer.masksToBounds = YES;
    mailAddressView.layer.borderWidth = 0.5;
    mailAddressView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    [self.view addSubview:mailAddressView];
    
    self.mailAddressField = [[UITextField alloc]initWithFrame:CGRectMake(10, 7, CGRectGetWidth(mailAddressView.frame)-10-10, 22)];
    self.mailAddressField.text = nil;
    self.mailAddressField.font = [UIFont systemFontOfSize:16.0f];
    self.mailAddressField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    self.mailAddressField.textAlignment = NSTextAlignmentLeft;
    self.mailAddressField.delegate = self;
    self.mailAddressField.tag = 10001;
    [mailAddressView addSubview:self.mailAddressField];
    [self textNormal:self.mailAddressField];
    
    UILabel *mailPasswordPrompt = [CommonFunction labelWithFrame:CGRectMake(30, CGRectGetMaxY(mailAddressView.frame)+10, CGRectGetWidth(self.view.frame)-30-30, 20) textFont:[UIFont systemFontOfSize:14.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
    mailPasswordPrompt.text = NSLocalizedString(@"MailLoginPassword", nil);
    [self.view addSubview:mailPasswordPrompt];
    
    UIView *mailPasswordView = [[UIView alloc]initWithFrame:CGRectMake(CGRectGetMinX(mailPasswordPrompt.frame), CGRectGetMaxY(mailPasswordPrompt.frame)+5, CGRectGetWidth(mailPasswordPrompt.frame), 36)];
    mailPasswordView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    mailPasswordView.layer.cornerRadius = 4;
    mailPasswordView.layer.masksToBounds = YES;
    mailPasswordView.layer.borderWidth = 0.5;
    mailPasswordView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    [self.view addSubview:mailPasswordView];
    
    self.mailPasswordField = [[UITextField alloc]initWithFrame:CGRectMake(10, 7, CGRectGetWidth(mailPasswordView.frame)-10-10, 22)];
    self.mailPasswordField.text = nil;
    self.mailPasswordField.font = [UIFont systemFontOfSize:16.0f];
    self.mailPasswordField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    self.mailPasswordField.textAlignment = NSTextAlignmentLeft;
    self.mailPasswordField.delegate = self;
    self.mailPasswordField.tag = 10002;
    [mailPasswordView addSubview:self.mailPasswordField];
    [self textNormal:self.mailPasswordField];
    
    self.mailLoginButton = [[UIButton alloc] initWithFrame:CGRectMake(30, CGRectGetMaxY(mailPasswordView.frame)+30, CGRectGetWidth(self.view.frame)-30-30, 44)];
    self.mailLoginButton.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
    self.mailLoginButton.layer.cornerRadius = 4;
    self.mailLoginButton.layer.masksToBounds = YES;
    [self.mailLoginButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"MailLogin", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:20.0f]}] forState:UIControlStateNormal];
    [self.mailLoginButton addTarget:self action:@selector(handLogin) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.mailLoginButton];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.mailLoginTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailSettingButton];
    
    [UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleLightContent;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabShow" object:nil];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!userSetting.emailBinded.boolValue) {
        self.mailAddressField.text = nil;
        self.mailPasswordField.text = nil;
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailLoginTitleLabel removeFromSuperview];
    [self.mailSettingButton removeFromSuperview];
}

- (void)mailSettingButtonClick {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (appDelegate.LeftSlideVC.closed) {
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshUserIcon];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshEmailAddress];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshTransferTaskCount];
        appDelegate.leftViewOpened = YES;
        [appDelegate.LeftSlideVC openLeftView];
    } else {
        appDelegate.leftViewOpened = NO;
        [appDelegate.LeftSlideVC closeLeftView];
    }
}

#pragma mark mail autoLogin
-(void) autoLogin {
    self.mailLoginButton.enabled = NO;
    UserSetting *userSetting = [UserSetting defaultSetting];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (userSetting.emailAddress && userSetting.emailPassword) {
        self.mailAddressField.text = userSetting.emailAddress;
        if (!userSetting.emailPassword) {
            self.mailPasswordField.text = userSetting.cloudUserPassword;
        } else {
            self.mailPasswordField.text = userSetting.emailPassword;
        }
        [appDelegate.remoteManager.mailLoginUserInfo setAddress:self.mailAddressField.text];
        [appDelegate.remoteManager.mailLoginUserInfo setPassword:self.mailPasswordField.text];
        [appDelegate.remoteManager.mailLoginUserInfo setHostName:userSetting.emailServerReceive];
        [appDelegate.remoteManager.mailLoginUserInfo setPort:userSetting.emailPortReceive];
        [appDelegate.remoteManager mailLogin:^(NSError *error) {
            if (!error) {
                userSetting.emailBinded = @(1);
                self.mailLoginButton.enabled = YES;
                [self.navigationController pushViewController:self.mailSessionViewController animated:NO];
                [[NSNotificationCenter defaultCenter] postNotificationName:@"mail.login.success" object:nil];
            } else {
                userSetting.emailBinded = @(0);
                self.mailLoginButton.enabled = YES;
            }
        }];
    } else {
        self.mailLoginButton.enabled = YES;
    }
}

#pragma mark mail handLogin
-(void)handLogin {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    UserSetting *userSetting = [UserSetting defaultSetting];
    if ([userSetting.emailServerReceive isEqualToString:@""] ||
        [userSetting.emailProtocolReceive isEqualToString:@""] ||
        [userSetting.emailPortReceive isEqualToString:@""]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"MailServerPrompt", nil)];
        });
    }
    if (!self.mailAddressField.text || !self.mailPasswordField.text) {
        return;
    }
    if (![CommonFunction checkMailAccountForm:self.mailAddressField.text]) {
        return;
    }
    self.mailLoginButton.enabled = NO;
    [appDelegate.remoteManager.mailLoginUserInfo setAddress:self.mailAddressField.text];
    [appDelegate.remoteManager.mailLoginUserInfo setPassword:self.mailPasswordField.text];
    [appDelegate.remoteManager.mailLoginUserInfo setHostName:userSetting.emailServerReceive];
    [appDelegate.remoteManager.mailLoginUserInfo setPort:userSetting.emailPortReceive];
    [appDelegate.remoteManager mailLogin:^(NSError *error) {
        if (!error) {
            self.mailLoginButton.enabled = YES;
            userSetting.emailBinded = @(1);
            userSetting.emailAddress = self.mailAddressField.text;
            userSetting.emailPassword = self.mailPasswordField.text;
            User *user = [User getUserWithUserSingleId:appDelegate.localManager.userSingleId context:nil];
            [user saveUserEmail:self.mailAddressField.text];
            [self.navigationController pushViewController:self.mailSessionViewController animated:NO];
            [[NSNotificationCenter defaultCenter] postNotificationName:@"mail.login.success" object:nil];
        } else {
            self.mailLoginButton.enabled = YES;
            userSetting.emailBinded = @(0);
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
            });
        }
    }];
}

#pragma mark textField state
- (void)textNormal:(UITextField*)textField {
    textField.superview.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
}

- (void)textHighLight:(UITextField*)textField {
    textField.superview.layer.borderColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f].CGColor;
}

#pragma mark textField delegate
-(void)textFieldDidBeginEditing:(UITextField *)textField {
    if (10001 == textField.tag) {
        [self textHighLight:self.mailAddressField];
        [self textNormal:self.mailPasswordField];
    } else {
        [self textHighLight:self.mailPasswordField];
        [self textNormal:self.mailAddressField];
    }
    
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = (rect.size.height-216-40) - CGRectGetMaxY(self.mailPasswordField.frame);
    if(rect.origin.y < 0)
        self.view.frame = rect;
    [UIView commitAnimations];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self.mailAddressField resignFirstResponder];
    [self.mailPasswordField resignFirstResponder];
    [self textNormal:self.mailAddressField];
    [self textNormal:self.mailPasswordField];
    
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = 0;
    self.view.frame = rect;
    [UIView commitAnimations];
}

@end