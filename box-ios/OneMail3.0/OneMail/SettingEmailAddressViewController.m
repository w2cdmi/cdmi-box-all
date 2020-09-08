//
//  SettingEmailAddressViewController.m
//  OneMail
//
//  Created by cse  on 15/12/7.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "SettingEmailAddressViewController.h"
#import "AppDelegate.h"
#import "MessageIMAPSession.h"
#import "MessageLoadOperation.h"
#import "Session.h"

static const CGFloat TitleLabelTop    = 50.0f;
static const CGFloat TitleLabelLeft   = 30.0f;
static const CGFloat TitleLabelRight  = 30.0f;
static const CGFloat TitleLabelBottom = 5.0f;
static const CGFloat TitleLabelHeight = 20.0f;
static const CGFloat FieldViewHeight  = 36.0f;
static const CGFloat FieldHeight      = 22.0f;
static const CGFloat FieldLeft        = 10.0f;
static const CGFloat FieldRight       = 10.0f;
static const CGFloat ButtonTop        = 30.0f;
static const CGFloat ButtonHeight     = 44.0f;

@interface SettingEmailAddressViewController ()<UITextFieldDelegate,UIActionSheetDelegate>

@property (nonatomic, strong) UILabel     *mailTitleLabel;
@property (nonatomic, strong) UIButton    *mailBackButton;
@property (nonatomic, strong) UITextField *mailAddressField;
@property (nonatomic, strong) UITextField *mailPasswordField;

@end

@implementation SettingEmailAddressViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.mailTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailTitleLabel.text = NSLocalizedString(@"SettingMailAddressTitle", nil);
    
    self.mailBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailBackButton addTarget:self action:@selector(mailBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    
    UILabel *addressTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(TitleLabelLeft, statusBarFrame.size.height+navigationFrame.size.height+TitleLabelTop, CGRectGetWidth(self.view.frame)-TitleLabelLeft-TitleLabelRight, TitleLabelHeight)];
    addressTitleLabel.text = NSLocalizedString(@"MailLoginAddress", nil);
    addressTitleLabel.font = [UIFont systemFontOfSize:14.0f];
    addressTitleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    addressTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:addressTitleLabel];
    
    UIView *addressFieldView = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMinX(addressTitleLabel.frame), CGRectGetMaxY(addressTitleLabel.frame)+TitleLabelBottom, CGRectGetWidth(addressTitleLabel.frame), FieldViewHeight)];
    addressFieldView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    addressFieldView.layer.borderWidth = 0.5f;
    addressFieldView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    addressFieldView.layer.cornerRadius = 4;
    addressFieldView.layer.masksToBounds = YES;
    [self.view addSubview:addressFieldView];
    
    self.mailAddressField = [[UITextField alloc] initWithFrame:CGRectMake(FieldLeft, (FieldViewHeight-FieldHeight)/2, CGRectGetWidth(addressFieldView.frame)-FieldLeft-FieldRight, FieldHeight)];
    self.mailAddressField.backgroundColor = [UIColor clearColor];
    self.mailAddressField.tag = 10001;
    self.mailAddressField.text = [UserSetting defaultSetting].emailAddress;
    self.mailAddressField.font = [UIFont systemFontOfSize:16.0f];
    self.mailAddressField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    self.mailAddressField.textAlignment = NSTextAlignmentLeft;
    self.mailAddressField.delegate = self;
    [self textNormal:self.mailAddressField];
    [addressFieldView addSubview:self.mailAddressField];
    
    UILabel *passwordTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(addressTitleLabel.frame), CGRectGetMaxY(addressFieldView.frame)+10, CGRectGetWidth(addressTitleLabel.frame), CGRectGetHeight(addressTitleLabel.frame))];
    passwordTitleLabel.text = NSLocalizedString(@"MailLoginPassword", nil);
    passwordTitleLabel.font = [UIFont systemFontOfSize:14.0f];
    passwordTitleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    passwordTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:passwordTitleLabel];
    
    UIView *passwordFieldView = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMinX(passwordTitleLabel.frame), CGRectGetMaxY(passwordTitleLabel.frame)+TitleLabelBottom, CGRectGetWidth(passwordTitleLabel.frame), FieldViewHeight)];
    passwordFieldView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    passwordFieldView.layer.borderWidth = 0.5f;
    passwordFieldView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    passwordFieldView.layer.cornerRadius = 4;
    passwordFieldView.layer.masksToBounds = YES;
    [self.view addSubview:passwordFieldView];
    
    self.mailPasswordField = [[UITextField alloc] initWithFrame:CGRectMake(FieldLeft, (FieldViewHeight-FieldHeight)/2, CGRectGetWidth(passwordFieldView.frame)-FieldLeft-FieldRight, FieldHeight)];
    self.mailPasswordField.backgroundColor = [UIColor clearColor];
    self.mailPasswordField.tag = 10002;
    self.mailPasswordField.secureTextEntry = YES;
    self.mailPasswordField.font = [UIFont systemFontOfSize:16.0f];
    self.mailPasswordField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    self.mailPasswordField.textAlignment = NSTextAlignmentLeft;
    [self textNormal:self.mailAddressField];
    self.mailPasswordField.delegate = self;
    [passwordFieldView addSubview:self.mailPasswordField];
    
    UIButton *changeButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetMinX(passwordFieldView.frame), CGRectGetMaxY(passwordFieldView.frame)+ButtonTop, CGRectGetWidth(passwordFieldView.frame), ButtonHeight)];
    changeButton.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
    changeButton.layer.cornerRadius = 4;
    changeButton.layer.masksToBounds = YES;
    [changeButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"MailLoginChange", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:20.0f]}] forState:UIControlStateNormal];
    [changeButton addTarget:self action:@selector(handChangeSheet) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:changeButton];
    
    UIButton *deleteButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetMinX(passwordFieldView.frame), CGRectGetMaxY(changeButton.frame)+10, CGRectGetWidth(passwordFieldView.frame), ButtonHeight)];
    deleteButton.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
    deleteButton.layer.cornerRadius = 4;
    deleteButton.layer.masksToBounds = YES;
    [deleteButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"MailLoginDelete", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:20.0f]}] forState:UIControlStateNormal];
    [deleteButton addTarget:self action:@selector(handDeleteSheet) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:deleteButton];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.mailTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailTitleLabel removeFromSuperview];
    [self.mailBackButton removeFromSuperview];
}

- (void)mailBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)textNormal:(UITextField*)textField {
    textField.superview.layer.borderColor  = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
}

- (void)textHighLight:(UITextField*)textField {
    textField.superview.layer.borderColor  = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f].CGColor;
}

-(void)textFieldDidBeginEditing:(UITextField *)textField
{
    if (10001 == textField.tag)
    {
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

- (void)handChangeSheet {
    [self touchesBegan:nil withEvent:nil];
    UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"SettingMailAddressChangePrompt", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedString(@"Confirm", nil), nil];
    actionSheet.tag = 10003;
    [actionSheet showInView:self.view];
}

- (void)handDeleteSheet {
    [self touchesBegan:nil withEvent:nil];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!userSetting.emailBinded.boolValue) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"MailLoginNone", nil)];
        return;
    }
    UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"SettingMailAddressDeletePrompt", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedString(@"Confirm", nil), nil];
    actionSheet.tag = 10004;
    [actionSheet showInView:self.view];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (actionSheet.tag == 10003 && buttonIndex == 0) {
        [self handChange];
    }
    if (actionSheet.tag == 10004 && buttonIndex == 0) {
        [self handLogout:actionSheet];
    }
}


- (void)handChange {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!self.mailAddressField.text || !self.mailPasswordField.text) {
        return;
    }
    if (![[self class] checkMailAccountForm:self.mailAddressField.text]) {
        return;
    }
    if (!userSetting.emailBinded.boolValue) {
        [self handLogin];
    } else {
        [self handLogout:nil];
        [self handLogin];
    }
}

-(void) handLogin {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    UserSetting *userSetting = [UserSetting defaultSetting];
    [appDelegate.remoteManager.mailLoginUserInfo setAddress:self.mailAddressField.text];
    [appDelegate.remoteManager.mailLoginUserInfo setPassword:self.mailPasswordField.text];
    [appDelegate.remoteManager mailLogin:^(NSError *error) {
        if (!error) {
            userSetting.emailBinded = @(1);
            userSetting.emailAddress = self.mailAddressField.text;
            userSetting.emailPassword = self.mailPasswordField.text;
            userSetting.emailFirstLoad = @(1);
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationSuccess", nil)];
                [self.navigationController popViewControllerAnimated:YES];
            });
        } else {
            userSetting.emailBinded = @(0);
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
            });
        }
    }];
}

- (void)handLogout:(id)sender{
    UserSetting *userSetting = [UserSetting defaultSetting];
    MessageIMAPSession *imapSession = [MessageIMAPSession getSessionInstance];
    [imapSession cancelAllOperations];
    userSetting.emailAddress = nil;
    userSetting.emailPassword = nil;
    userSetting.emailLastInboxUid = @(0);
    userSetting.emailLastSentBoxUid = @(0);
    userSetting.emailNextSessionId = @(1);
    userSetting.emailFirstLoad = @(1);
    userSetting.emailBinded = @(0);
    if (sender) {
        self.mailAddressField.text = nil;
        self.mailPasswordField.text = nil;
    }
    [self DeleteAllMailData];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)DeleteAllMailData{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *backgroundObjectContext = delegate.localManager.backgroundObjectContext;
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:backgroundObjectContext];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"sessionId" ascending:YES];
    NSArray *sortdescription = @[sort];
    [request setEntity:entity];
    [request setSortDescriptors:sortdescription];
    NSArray *Sessions = [backgroundObjectContext executeFetchRequest:request error:nil];
    for (Session *session in Sessions) {
        [session removeSession];
    }
    
    [backgroundObjectContext save:nil];
    
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


+(BOOL)checkMailAccountForm:(NSString*)emailAccount {
    NSString *emailRegex = @"[A-Z0-9a-z]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,4}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",emailRegex];
    return [emailTest evaluateWithObject:emailAccount];
}


@end