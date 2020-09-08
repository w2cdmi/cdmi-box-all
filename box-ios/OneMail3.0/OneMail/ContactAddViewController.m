//
//  ContactAddViewController.m
//  OneMail
//
//  Created by cse  on 15/12/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "ContactAddViewController.h"
#import "AppDelegate.h"
#import "User.h"
#import "ContactSearchViewController.h"

@interface ContactAddViewController ()

@property (nonatomic, strong) UILabel     *contactTitleLabel;
@property (nonatomic, strong) UIButton    *contactBackButton;
@property (nonatomic, strong) UIButton    *contactAddButton;
@property (nonatomic, strong) UITextField *nameTextField;
@property (nonatomic, strong) UITextField *emailTextField;
@property (nonatomic, strong) UITextField *phoneTextField;
@property (nonatomic, strong) UITextField *remarkTextField;

@end

@implementation ContactAddViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.contactTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.contactTitleLabel.text = NSLocalizedString(@"ContactAddTitle", nil);
    
    self.contactBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.contactBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.contactBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.contactBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.contactBackButton addTarget:self action:@selector(contactBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.contactAddButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:NSLocalizedString(@"Confirm", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
    self.contactAddButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(self.contactAddButton.frame), 0, CGRectGetWidth(self.contactAddButton.frame), CGRectGetHeight(self.contactAddButton.frame));
    [self.contactAddButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"Confirm", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.contactAddButton addTarget:self action:@selector(contactAddButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    UIButton *contactSearchButton = [[UIButton alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 44)];
    contactSearchButton.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    contactSearchButton.layer.borderWidth = 0.5;
    contactSearchButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    [contactSearchButton addTarget:self action:@selector(contactSearch) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:contactSearchButton];
    
    UIImageView *contactSearchImage = [[UIImageView alloc] initWithFrame:CGRectMake(15, (CGRectGetHeight(contactSearchButton.frame)-22)/2, 22, 22)];
    contactSearchImage.image = [UIImage imageNamed:@"ic_contact_corporate_search_nor"];
    [contactSearchButton addSubview:contactSearchImage];
    
    UILabel *contactSearchLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(contactSearchImage.frame)+4, (CGRectGetHeight(contactSearchButton.frame)-22)/2, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(contactSearchImage.frame)-4-15, 22)];
    contactSearchLabel.text = NSLocalizedString(@"ContactSearchEnterpriseTitle", nil);
    contactSearchLabel.font = [UIFont systemFontOfSize:14.0f];
    contactSearchLabel.textColor = [CommonFunction colorWithString:@"008be8" alpha:1.0f];
    contactSearchLabel.textAlignment = NSTextAlignmentLeft;
    [contactSearchButton addSubview:contactSearchLabel];
    
    UILabel *nameLabel = [self contactTile:NSLocalizedString(@"ContactNameTitle", nil) frame:CGRectMake(15, CGRectGetMaxY(contactSearchButton.frame)+15, CGRectGetWidth(self.view.frame)-15-15, 20)];
    [self.view addSubview:nameLabel];
    UIView *nameView = [self contactTextFieldBackgroundWithFrame:CGRectMake(CGRectGetMinX(nameLabel.frame), CGRectGetMaxY(nameLabel.frame)+5, CGRectGetWidth(nameLabel.frame), 36)];
    [self.view addSubview:nameView];
    self.nameTextField = [self contactTextFieldWithFrame:CGRectMake(10, (CGRectGetHeight(nameView.frame)-22)/2, CGRectGetWidth(nameView.frame)-10-10, 22)];
    self.nameTextField.placeholder = NSLocalizedString(@"ContactMandatory", nil);
    [nameView addSubview:self.nameTextField];
    
    UILabel *emailLabel = [self contactTile:NSLocalizedString(@"ContactEmailTitle", nil) frame:CGRectMake(CGRectGetMinX(nameLabel.frame), CGRectGetMaxY(nameView.frame)+10, CGRectGetWidth(self.view.frame)-15-15, 20)];
    [self.view addSubview:emailLabel];
    UIView *emailView = [self contactTextFieldBackgroundWithFrame:CGRectMake(CGRectGetMinX(emailLabel.frame), CGRectGetMaxY(emailLabel.frame)+5, CGRectGetWidth(emailLabel.frame), 36)];
    [self.view addSubview:emailView];
    self.emailTextField = [self contactTextFieldWithFrame:CGRectMake(10, (CGRectGetHeight(emailView.frame)-22)/2, CGRectGetWidth(emailView.frame)-10-10, 22)];
    self.emailTextField.placeholder = NSLocalizedString(@"ContactMandatory", nil);
    [emailView addSubview:self.emailTextField];
    
    UILabel *phoneLabel = [self contactTile:NSLocalizedString(@"ContactMobliePhone", nil) frame:CGRectMake(CGRectGetMinX(emailLabel.frame), CGRectGetMaxY(emailView.frame)+10, CGRectGetWidth(self.view.frame)-15-15, 20)];
    [self.view addSubview:phoneLabel];
    UIView *phoneView = [self contactTextFieldBackgroundWithFrame:CGRectMake(CGRectGetMinX(phoneLabel.frame), CGRectGetMaxY(phoneLabel.frame)+5, CGRectGetWidth(phoneLabel.frame), 36)];
    [self.view addSubview:phoneView];
    self.phoneTextField = [self contactTextFieldWithFrame:CGRectMake(10, (CGRectGetHeight(phoneView.frame)-22)/2, CGRectGetWidth(phoneView.frame)-10-10, 22)];
    self.phoneTextField.keyboardType = UIKeyboardTypePhonePad;
    [phoneView addSubview:self.phoneTextField];
    
    UILabel *remarkLabel = [self contactTile:NSLocalizedString(@"ContactRemark", nil) frame:CGRectMake(CGRectGetMinX(phoneLabel.frame), CGRectGetMaxY(phoneView.frame)+10, CGRectGetWidth(self.view.frame)-15-15, 20)];
    [self.view addSubview:remarkLabel];
    UIView *remarkView = [self contactTextFieldBackgroundWithFrame:CGRectMake(CGRectGetMinX(remarkLabel.frame), CGRectGetMaxY(remarkLabel.frame)+5, CGRectGetWidth(remarkLabel.frame), 36)];
    [self.view addSubview:remarkView];
    self.remarkTextField = [self contactTextFieldWithFrame:CGRectMake(10, (CGRectGetHeight(remarkView.frame)-22)/2, CGRectGetWidth(remarkView.frame)-10-10, 22)];
    [remarkView addSubview:self.remarkTextField];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.contactTitleLabel];
    [self.navigationController.navigationBar addSubview:self.contactBackButton];
    [self.navigationController.navigationBar addSubview:self.contactAddButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.contactTitleLabel removeFromSuperview];
    [self.contactBackButton removeFromSuperview];
    [self.contactAddButton removeFromSuperview];
}

- (void)contactBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)contactAddButtonClick {
    if (([self.emailTextField.text isEqualToString:@""]) || ([self.nameTextField.text isEqualToString:@""])) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"ContactAddFailedPrompt", nil)];
        return;
    }
    if (![[self class] checkMailAccountForm:self.emailTextField.text]) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"MailFormatIncorrect", nil)];
        return;
    }
    if (self.phoneTextField.text.length != 11) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"PhoneFormatIncorrect", nil)];
        return;
    }
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
        [userInfo setObject:self.nameTextField.text forKey:@"name"];
        [userInfo setObject:self.emailTextField.text forKey:@"email"];
        [userInfo setObject:self.phoneTextField.text forKey:@"phone"];
        if (self.remarkTextField.text) {
            [userInfo setObject:self.remarkTextField.text forKey:@"description"];
        }
        User *user = [User userInsertWithInfo:userInfo context:ctx];
        user.userMyContactFlag = @(1);
        [ctx save:nil];
    }];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)contactSearch {
    ContactSearchViewController *contactSearchView = [[ContactSearchViewController alloc] init];
    [self.navigationController pushViewController:contactSearchView animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UILabel*)contactTile:(NSString*)title frame:(CGRect)frame{
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:frame];
    titleLabel.text = title;
    titleLabel.font = [UIFont systemFontOfSize:14.0f];
    titleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    return titleLabel;
}

- (UITextField*)contactTextFieldWithFrame:(CGRect)frame {
    UITextField *textField = [[UITextField alloc] initWithFrame:frame];
    textField.font = [UIFont systemFontOfSize:16.0f];
    textField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    textField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    return textField;
}

- (UIView*)contactTextFieldBackgroundWithFrame:(CGRect)frame {
    UIView *view = [[UIView alloc] initWithFrame:frame];
    view.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    view.layer.borderWidth = 0.5;
    view.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    view.layer.cornerRadius = 4;
    view.layer.masksToBounds = YES;
    return view;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event{
    [self.nameTextField resignFirstResponder];
    [self.emailTextField resignFirstResponder];
    [self.phoneTextField resignFirstResponder];
    [self.remarkTextField resignFirstResponder];
}

+(BOOL)checkMailAccountForm:(NSString*)emailAccount {
    NSString *emailRegex = @"[A-Z0-9a-z]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,4}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",emailRegex];
    return [emailTest evaluateWithObject:emailAccount];
}


@end
