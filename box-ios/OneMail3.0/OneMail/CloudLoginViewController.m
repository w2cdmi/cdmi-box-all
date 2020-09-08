//
//  CloudLoginViewController.m
//  OneBox
//
//  Created by cse on 15/5/20.
//  Copyright (c) 2015年 www.huawei.com. All rights reserved.
//

#import "AppDelegate.h"
#import "CloudLoginViewController.h"
#import "MailLoginViewController.h"
#import "MainViewController.h"
#import "CloudViewController.h"
#import "UIView+Toast.h"
#import "MenuViewController.h"
#import "HW_Main_CBB.h"
#import "MRProgressOverlayView.h"
#import "CloudLoginDeclarationViewController.h"
#import "CloudLoginSecurityAlertView.h"
#import "CloudLoginServerAddressView.h"
#import "User.h"


@interface CloudLoginViewController ()<UITextFieldDelegate>

@property (nonatomic, strong) UITextField             *cloudLoginAccountTextField;
@property (nonatomic, strong) UITextField             *cloudLoginPasswordTextField;
@property (nonatomic, strong) UITextField             *cloudLoginIdentifyCodeTextField;
@property (nonatomic, strong) UITextField             *cloudLoginHighLightTextField;
@property (nonatomic, strong) UIButton                *cloudLoginAutoControlButton;
@property (nonatomic, strong) UIView                  *cloudLoginIdentifyView;
@property (nonatomic, strong) UIView                  *cloudLoginAutoControlView;
@property (nonatomic, strong) UIButton                *cloudLoginButton;
@property (nonatomic, strong) UIActivityIndicatorView *cloudLoginActivityIndicator;
@property (nonatomic, strong) UIView                  *cloudLoginDeclarationView;
@property (strong, nonatomic) UIButton *loginAddressSetBtn;
@property (nonatomic, assign) NSInteger loginFailedFlag;

@end

@implementation CloudLoginViewController

- (id)init {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.loginFailedFlag = 0;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];

//    self.view.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    self.view.backgroundColor = [UIColor whiteColor];
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    UIImageView *cloudLoginLogoImageView = [[UIImageView alloc] initWithFrame:CGRectMake((CGRectGetWidth(self.view.frame)/2-50), statusBarFrame.size.height+70, 100, 100)];
    cloudLoginLogoImageView.image = [UIImage imageNamed:@"storbox"];
    [self.view addSubview:cloudLoginLogoImageView];
    
    UIView *cloudLoginAccountView = [self textFieldViewWithFrame:CGRectMake(30, CGRectGetMaxY(cloudLoginLogoImageView.frame)+35, CGRectGetWidth(self.view.frame)-30*2, 44)];
    [self.view addSubview:cloudLoginAccountView];
    self.cloudLoginAccountTextField = [self textFieldWithFrame:CGRectMake(10, 11, CGRectGetWidth(cloudLoginAccountView.frame)-10*2, 22)];
    self.cloudLoginAccountTextField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudLoginUserName", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"999999" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:16.0f]}];
    [cloudLoginAccountView addSubview:self.cloudLoginAccountTextField];
    self.cloudLoginAccountTextField.tag = 1745;
    
    UIView *cloudLoginPasswordView = [self textFieldViewWithFrame:CGRectMake(CGRectGetMinX(cloudLoginAccountView.frame), CGRectGetMaxY(cloudLoginAccountView.frame)+10, CGRectGetWidth(cloudLoginAccountView.frame), 44)];
    [self.view addSubview:cloudLoginPasswordView];
    self.cloudLoginPasswordTextField = [self textFieldWithFrame:CGRectMake(10, 11, CGRectGetWidth(cloudLoginPasswordView.frame)-10*2, 22)];
    self.cloudLoginPasswordTextField.secureTextEntry = YES;
    self.cloudLoginPasswordTextField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudLoginPassword", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"999999" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:16.0f]}];
    [cloudLoginPasswordView addSubview:self.cloudLoginPasswordTextField];
    self.cloudLoginPasswordTextField.tag = 1746;
    
    self.cloudLoginAutoControlView = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMinX(cloudLoginPasswordView.frame), CGRectGetMaxY(cloudLoginPasswordView.frame)+15, CGRectGetWidth(cloudLoginPasswordView.frame), 22)];
    [self.view addSubview:self.cloudLoginAutoControlView];
    
    self.cloudLoginAutoControlButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 22, 22)];
    [self.cloudLoginAutoControlButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    [self.cloudLoginAutoControlButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateSelected];
    [self.cloudLoginAutoControlButton addTarget:self action:@selector(cloudLoginAutoControl:) forControlEvents:UIControlEventTouchUpInside];
    UserSetting * userSetting = [UserSetting defaultSetting];
    if ([userSetting.cloudAutoLogin boolValue]) {
        self.cloudLoginAutoControlButton.selected = YES;
    } else {
        self.cloudLoginAutoControlButton.selected = NO;
    }
    [self.cloudLoginAutoControlView addSubview:self.cloudLoginAutoControlButton];
    
    UILabel *cloudLoginAutoControlLabel = [CommonFunction labelWithFrame:CGRectMake(CGRectGetMaxX(self.cloudLoginAutoControlButton.frame)+5, 0, CGRectGetWidth(self.cloudLoginAutoControlView.frame)-CGRectGetWidth(self.cloudLoginAutoControlButton.frame)-5, 22) textFont:[UIFont systemFontOfSize:16.0f] textColor:[UIColor blackColor] textAlignment:NSTextAlignmentLeft];
    cloudLoginAutoControlLabel.text = getLocalizedString(@"CloudLoginAutomaticaly", nil);
    [self.cloudLoginAutoControlView addSubview:cloudLoginAutoControlLabel];
    
//    self.loginAddressSetBtn = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetMaxX(self.cloudLoginAutoControlButton.frame)+210, CGRectGetMaxY(self.cloudLoginAutoControlButton.frame)-23, 22, 22)];
//    self.loginAddressSetBtn = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetMaxX(self.cloudLoginAutoControlButton) + 20,CGRectGetWidth(self.cloudLoginAutoControlView.frame)-CGRectGetWidth(self.cloudLoginAutoControlButton.frame)-5, 22, 22)];
//    [self.loginAddressSetBtn setImage:[UIImage imageNamed:@"https"] forState:UIControlStateNormal];
//    [self.loginAddressSetBtn addTarget:self action:@selector(showServerAddressView) forControlEvents:UIControlEventTouchUpInside];
//    [self.cloudLoginAutoControlView addSubview:self.loginAddressSetBtn];
    
    self.cloudLoginButton.frame = CGRectMake(CGRectGetMinX(self.cloudLoginAutoControlView.frame), CGRectGetMaxY(self.cloudLoginAutoControlView.frame)+20, CGRectGetWidth(self.cloudLoginAutoControlView.frame), 44);
    [self.view addSubview:self.cloudLoginButton];
    
    
    CGRect cloudLoginButtonTitleFrame = self.cloudLoginButton.titleLabel.frame;
    self.cloudLoginActivityIndicator.frame = CGRectMake(CGRectGetMinX(cloudLoginButtonTitleFrame)-8-20, CGRectGetMinY(cloudLoginButtonTitleFrame), 20, cloudLoginButtonTitleFrame.size.height);
    
    UIButton *cloudLoginForget = [[UIButton alloc] initWithFrame:CGRectMake(30, CGRectGetHeight(self.view.frame)-49-20, CGRectGetWidth(self.view.frame)-30*2, 20)];
    [cloudLoginForget setTitle:getLocalizedString(@"CloudLoginForget", nil) forState:UIControlStateNormal];
    [cloudLoginForget setTitleColor:[CommonFunction colorWithString:@"2e90e5" alpha:1.0f] forState:UIControlStateNormal];
    [cloudLoginForget addTarget:self action:@selector(showPassWordForgetView) forControlEvents:UIControlEventTouchUpInside];
//    [self.view addSubview:cloudLoginForget];
    
    [NSTimer scheduledTimerWithTimeInterval:0.1f target:self selector:@selector(autoLogin) userInfo:nil repeats:NO];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[UIApplication sharedApplication] setStatusBarHidden:NO];
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault];
    self.navigationController.navigationBarHidden = YES;
    self.cloudLoginButton.enabled = YES;
    [self hideCloudLoginIdentifyView];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.cloudAutoLogin.boolValue) {
        self.cloudLoginAccountTextField.text = userSetting.cloudUserAccount;
        self.cloudLoginPasswordTextField.text = userSetting.cloudUserPassword;
        [self.cloudLoginAutoControlButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        self.cloudLoginAccountTextField.text = nil;
        self.cloudLoginPasswordTextField.text = nil;
        [self.cloudLoginAutoControlButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    }
}

#pragma mark loginIdentifyView
- (UIView*)cloudLoginIdentifyView {
    if (!_cloudLoginIdentifyView) {
        _cloudLoginIdentifyView = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMinX(self.cloudLoginAutoControlView.frame), CGRectGetMinY(self.cloudLoginAutoControlView.frame), CGRectGetWidth(self.view.frame)-30-30, 44)];
        UIView *cloudLoginIdentifyCodeView = [self textFieldViewWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.cloudLoginIdentifyView.frame)-10-110, CGRectGetHeight(self.cloudLoginIdentifyView.frame))];
        [_cloudLoginIdentifyView addSubview:cloudLoginIdentifyCodeView];
        self.cloudLoginIdentifyCodeTextField = [self textFieldWithFrame:CGRectMake(10, (CGRectGetHeight(cloudLoginIdentifyCodeView.frame)-22)/2, CGRectGetWidth(cloudLoginIdentifyCodeView.frame)-10-10, 22)];
        [cloudLoginIdentifyCodeView addSubview:self.cloudLoginIdentifyCodeTextField];
        UIImageView *cloudLoginIdentifyImageView = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(cloudLoginIdentifyCodeView.frame)+10, 0, 110, CGRectGetHeight(cloudLoginIdentifyCodeView.frame))];
        [_cloudLoginIdentifyView addSubview:cloudLoginIdentifyImageView];
    }
    return _cloudLoginIdentifyView;
}

- (void)showCloudLoginIdentifyView {
    [self.view addSubview:self.cloudLoginIdentifyView];
    self.cloudLoginAutoControlView.frame = CGRectMake(CGRectGetMinX(self.cloudLoginIdentifyView.frame), CGRectGetMaxY(self.cloudLoginIdentifyView.frame)+22, CGRectGetWidth(self.cloudLoginAutoControlView.frame), CGRectGetHeight(self.cloudLoginAutoControlView.frame));
}

- (void)hideCloudLoginIdentifyView {
    [self.cloudLoginIdentifyView removeFromSuperview];
    self.cloudLoginAutoControlView.frame = CGRectMake(CGRectGetMinX(self.cloudLoginIdentifyView.frame), CGRectGetMinY(self.cloudLoginIdentifyView.frame), CGRectGetWidth(self.cloudLoginAutoControlView.frame), CGRectGetHeight(self.cloudLoginAutoControlView.frame));
}

#pragma mark TextFieldView+TextField
- (UIView*)textFieldViewWithFrame:(CGRect)frame {
    UIView *textFieldView = [[UIView alloc]initWithFrame:frame];
    textFieldView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    textFieldView.layer.cornerRadius = 4;
    textFieldView.layer.masksToBounds = YES;
    textFieldView.layer.borderWidth = 0.5;
    textFieldView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    
    return textFieldView;
}

- (UITextField*)textFieldWithFrame:(CGRect)frame {
    UITextField *textField = [[UITextField alloc]initWithFrame:frame];
    textField.backgroundColor = [UIColor clearColor];
    textField.text = nil;
    textField.font = [UIFont systemFontOfSize:16.0f];
    textField.textColor = [UIColor blackColor];
    textField.delegate = self;
    textField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    
    return textField;
}


- (void)textNormal:(UITextField*)textField {
    if (self.cloudLoginHighLightTextField == textField) {
        self.cloudLoginHighLightTextField = nil;
    }
    textField.superview.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
}

- (void)textHighLight:(UITextField*)textField {
    self.cloudLoginHighLightTextField = textField;
    textField.superview.layer.borderColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f].CGColor;
}

#pragma mark autoLoginControl
- (void)cloudLoginAutoControl:(UIButton*)sender {
    UserSetting * userSetting = [UserSetting defaultSetting];
    if ([userSetting.cloudAutoLogin boolValue]) {
        sender.selected = NO;
        userSetting.cloudAutoLogin = [NSNumber numberWithBool:NO];
    } else {
        sender.selected = YES;
        userSetting.cloudAutoLogin = [NSNumber numberWithBool:YES];
    }
}

#pragma mark loginButton
- (UIButton*)cloudLoginButton {
    if (!_cloudLoginButton) {
        _cloudLoginButton = [[UIButton alloc] initWithFrame:CGRectZero];
        _cloudLoginButton.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
        _cloudLoginButton.titleLabel.font = [UIFont systemFontOfSize:20.0f];
        [_cloudLoginButton setTitle:getLocalizedString(@"CloudLoginButtonTitle", nil) forState:UIControlStateNormal];
        [_cloudLoginButton setTitleColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] forState:UIControlStateNormal];
        _cloudLoginButton.layer.cornerRadius = 4;
        _cloudLoginButton.layer.masksToBounds = YES;
        [_cloudLoginButton addTarget:self action:@selector(handLogin) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudLoginActivityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectZero];
        self.cloudLoginActivityIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhite;
        [_cloudLoginButton addSubview:self.cloudLoginActivityIndicator];
    }
    return _cloudLoginButton;
}

-(void)autoLogin {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if ([userSetting.cloudAutoLogin boolValue]) {
        if (![userSetting.cloudUserAccount isEqualToString:@""] &&
            ![userSetting.cloudUserPassword isEqualToString:@""]) {
            self.cloudLoginAccountTextField.text = userSetting.cloudUserAccount;
            self.cloudLoginPasswordTextField.text = userSetting.cloudUserPassword;
            
            [self cloudLogin];
        }
    }
}

- (void)handLogin {
//        self.cloudLoginAccountTextField.text = @"bojiexian";
//        self.cloudLoginPasswordTextField.text = @"pas@123a";
//    self.cloudLoginAccountTextField.text = @"jianghua";
//    self.cloudLoginPasswordTextField.text = @"huawei@123";
        self.cloudLoginAccountTextField.text = @"jianghua@storbox.cn";
        self.cloudLoginPasswordTextField.text = @"huawei@123";
//            self.cloudLoginAccountTextField.text = @"chinasoft/jianghua001@chinasofti.com";
//            self.cloudLoginPasswordTextField.text = @"csibox@123";
    
//    self.cloudLoginAccountTextField.text = @"v-longwz";
//    self.cloudLoginPasswordTextField.text = @"62pzGWPV";
    [self cloudLogin];
}

- (void)cloudLogin {
    
    UserSetting *userSetting = [UserSetting defaultSetting];
    if ([self.cloudLoginAccountTextField.text isEqualToString:@""] ||
        [self.cloudLoginPasswordTextField.text isEqualToString:@""]){
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudLoginNone", nil)];
        });
        return;
    }
    
    NSMutableArray *cloudLoginAccountMessage = [[NSMutableArray alloc] initWithArray:[self.cloudLoginAccountTextField.text componentsSeparatedByString:@"/"]];
    if (cloudLoginAccountMessage.count == 1) {
        [cloudLoginAccountMessage insertObject:@"" atIndex:0];
    }
    if (cloudLoginAccountMessage.count != 2) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudLoginFailedPrompt", nil)];
        });
        return;
    }
    
    AppDelegate * appDelegate = [[UIApplication sharedApplication] delegate];
    if (!appDelegate.remoteManager) {
        appDelegate.remoteManager = [[RemoteDataManager alloc]init];
    }
    
//[cloudLoginAccountMessage objectAtIndex:1]
    appDelegate.remoteManager.httpService.loginBaseUrl = [[NSURL alloc]initWithString:userSetting.cloudService];
    [appDelegate.remoteManager.cloudLoginUserInfo setDomain:[cloudLoginAccountMessage objectAtIndex:0]];
    [appDelegate.remoteManager.cloudLoginUserInfo setLoginName:[cloudLoginAccountMessage objectAtIndex:1]];
    [appDelegate.remoteManager.cloudLoginUserInfo setPassword:self.cloudLoginPasswordTextField.text];
    
    self.cloudLoginButton.enabled = NO;
    if(![appDelegate hasNetwork]){
        self.cloudLoginButton.enabled = YES;
        if ([userSetting.cloudUserAccount isEqualToString:self.cloudLoginAccountTextField.text] &&
            [userSetting.cloudUserPassword isEqualToString:self.cloudLoginPasswordTextField.text]) {
            [self loginSucess:nil];
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[[UIAlertView alloc]initWithTitle:getLocalizedString(@"InternetOfflineTitle", nil) message:nil delegate:nil cancelButtonTitle:getLocalizedString(@"Confirm", nil) otherButtonTitles:nil, nil] show];
            });
        }
    } else {
        [self.cloudLoginActivityIndicator startAnimating];
        
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"Login", nil) duration:2 position:nil];
        [appDelegate.remoteManager cloudLogin:^(id retobj){
            [self.cloudLoginActivityIndicator stopAnimating];
            NSString *cloudUserId = [[retobj objectForKey:@"cloudUserId"] stringValue];
            if (![userSetting.cloudUserCloudId isEqual:@(0)] &&
                ![userSetting.cloudUserCloudId.stringValue isEqualToString:cloudUserId]) {
                userSetting.cloudUserFirstLogin = @(1);
                userSetting.emailGetDefaultAddress = @(1);
            }
            userSetting.cloudUserSingleId = [retobj objectForKey:@"userId"];
            userSetting.cloudUserCloudId = [retobj objectForKey:@"cloudUserId"];
            
            userSetting.cloudUserAccount = self.cloudLoginAccountTextField.text;
            userSetting.cloudUserPassword = self.cloudLoginPasswordTextField.text;
            userSetting.cloudUserLoginName = [retobj objectForKey:@"loginName"];
            if (userSetting.emailGetDefaultAddress.boolValue) {
                userSetting.emailGetDefaultAddress = @(0);
                if ([retobj objectForKey:@"email"]) {
                    userSetting.emailAddress = [retobj objectForKey:@"email"];
                    userSetting.emailPassword = self.cloudLoginPasswordTextField.text;
                }
            }
            userSetting.cloudUserName = [retobj objectForKey:@"name"];
            if (!userSetting.cloudUserName || [userSetting.cloudUserName isEqualToString:@""]) {
                userSetting.cloudUserName = userSetting.cloudUserLoginName;
            }
            
            [self showDeclarationView:[[retobj objectForKey:@"needDeclaration"] boolValue] completionBlock:^{
                if ([[retobj objectForKey:@"needChangePassword"] boolValue]) {
                    [self showSecurityAlertView];
                } else {
                    [self loginSucess:[retobj objectForKey:@"lastAccessTerminal"]];
                }
            }];
        } failed:^(NSURLRequest* request, NSURLResponse* response, NSError* error,int ErrorType){
            [self.cloudLoginActivityIndicator stopAnimating];
            self.loginFailedFlag = 0;
            if (self.loginFailedFlag >= 2) {}
            dispatch_async(dispatch_get_main_queue(), ^{
                self.cloudLoginButton.enabled = YES;
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                if (httpResponse.statusCode == 401) {
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudLoginIncorrectPrompt", nil)];
                } else if (httpResponse.statusCode == 403) {
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudLoginLockedPrompt", nil)];
                } else {
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudLoginFailedPrompt", nil)];
                }
            });
        }];
    }
}

- (void)showDeclarationView:(BOOL)cloudNeedDeclaration completionBlock:(void(^)())block {
    if (cloudNeedDeclaration) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        [appDelegate.remoteManager getDeclarationContent:^(id retobj) {
            CloudLoginDeclarationViewController *declarationView = [[CloudLoginDeclarationViewController alloc] init];
            declarationView.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
            declarationView.confirmBlock = ^(){
                block();
            };
            declarationView.cloudLoginDeclarationString = [retobj objectForKey:@"declaration"];
            declarationView.cloudLoginDeclarationId = [retobj objectForKey:@"id"];
            [self.navigationController presentViewController:declarationView animated:YES completion:^{}];
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudLoginFailedPrompt", nil)];
            });
        }];
    } else {
        block();
    }
}

- (void)showSecurityAlertView {
    CloudLoginSecurityAlertView *securityAlertView = [[CloudLoginSecurityAlertView alloc] initWithFrame:self.view.frame];
    [self.view addSubview:securityAlertView];
}

- (void)showServerAddressView {
    CloudLoginServerAddressView *serverAddressView = [[CloudLoginServerAddressView alloc] initWithFrame:self.view.frame];
    [self.view addSubview:serverAddressView];
}

- (void)showPassWordForgetView {
    [[UIApplication sharedApplication].keyWindow makeToast:@"请联系管理员"];
}

- (void)loginSucess:(NSDictionary*)lastAccessTerminal {
    AppDelegate * appDelegate = [[UIApplication sharedApplication] delegate];
    UserSetting * userSetting = [UserSetting defaultSetting];

    appDelegate.localManager.userSingleId = [userSetting.cloudUserSingleId stringValue];
    appDelegate.localManager.userCloudId = [userSetting.cloudUserCloudId stringValue];
    appDelegate.uploadOperation = [[TransportUploadOperation alloc] init];
    appDelegate.downloadOperation = [[TransportDownloadOperation alloc] init];
    appDelegate.backUpAssetOperation = [[AssetBackUpOperation alloc] init];
    [appDelegate.networkReachability startMonitoring];
    appDelegate.startMonitor = YES;
    [self createLocalUserDirectory];
    if (lastAccessTerminal) {
        [self showLastLoginNotice:lastAccessTerminal];
    }
    

    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
    if (userSetting.cloudUserSingleId && userSetting.cloudUserSingleId.integerValue != 0) {
        [userInfo setObject:userSetting.cloudUserSingleId forKey:@"userId"];
    }
    if (userSetting.cloudUserCloudId && userSetting.cloudUserCloudId.integerValue != 0) {
        [userInfo setObject:userSetting.cloudUserCloudId forKey:@"cloudUserId"];
    }
    if (userSetting.cloudUserLoginName && ![userSetting.cloudUserLoginName isEqualToString:@""]) {
        [userInfo setObject:userSetting.cloudUserLoginName forKey:@"loginName"];
    }
    if (userSetting.cloudUserName && ![userSetting.cloudUserName isEqualToString:@""]) {
        [userInfo setObject:userSetting.cloudUserName forKey:@"name"];
    } else {
        [userInfo setObject:userSetting.cloudUserLoginName forKey:@"name"];
        userSetting.cloudUserName = userSetting.cloudUserLoginName;
    }
    if (userSetting.cloudUserDescription && ![userSetting.cloudUserDescription isEqualToString:@""]) {
        [userInfo setObject:userSetting.cloudUserDescription forKey:@"description"];
    }
    if (userSetting.emailAddress && ![userSetting.emailAddress isEqualToString:@""]) {
//        [userInfo setObject:userSetting.emailAddress forKey:@"email"];
    }
    [ctx performBlockAndWait:^{
        [User userInsertWithInfo:userInfo context:ctx];
        [ctx save:nil];
    }];
    
    
    if (!appDelegate.LeftSlideVC) {
//        MainViewController *mainTabVC = [[MainViewController alloc] init];
        CloudViewController *cloudTabVC = [[CloudViewController alloc] init];
        MenuViewController *menuVC = [[MenuViewController alloc]init];
        appDelegate.LeftSlideVC = [[LeftSlideViewController alloc]initWithLeftView:menuVC andMainView:cloudTabVC];
        appDelegate.leftViewOpened = NO;
    }
    [appDelegate.LeftSlideVC setPanEnabled:NO];
    [appDelegate.navigationController pushViewController:appDelegate.LeftSlideVC animated:YES];
    
    if (userSetting.cloudAssetBackupOpen.boolValue) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Onebox.backup.autoStart" object:nil];
    }
}

- (void)createLocalUserDirectory {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSURL* docURL = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
    if (docURL) {
        NSString* docDirectory = [[docURL.path stringByAppendingPathComponent:@"downloads"] stringByAppendingPathComponent:appDelegate.localManager.userCloudId];
        BOOL isDirectory;
        if (![[NSFileManager defaultManager] fileExistsAtPath:docDirectory isDirectory:&isDirectory] || !isDirectory) {
            [[NSFileManager defaultManager] createDirectoryAtPath:docDirectory withIntermediateDirectories:YES attributes:nil error:nil];
        }
        appDelegate.localManager.userDataPath = docDirectory;
    }
}

- (void)showLastLoginNotice:(NSDictionary*)lastAccessTerminal {
    if (!lastAccessTerminal) {
        return;
    }
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-M-dd H:mm"];
    NSString *cloudLastAccessDate = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:[[lastAccessTerminal objectForKey:@"lastAccessAt"] doubleValue]/1000]];
    NSString *cloudLastAccessIP = [lastAccessTerminal objectForKey:@"lastAccessIP"];
    NSString *cloudLastDeviceType = [lastAccessTerminal objectForKey:@"deviceType"];
    if (!cloudLastAccessDate || !cloudLastAccessIP || !cloudLastDeviceType) {
        return;
    } else {
        NSString *cloudLastAccessDateString = [NSString stringWithFormat:getLocalizedString(@"CloudLoginLastTime", nil),cloudLastAccessDate];
        NSString *cloudLastAccessIPString = [NSString stringWithFormat:getLocalizedString(@"CloudLoginIPAddress", nil),cloudLastAccessIP];
        NSString *cloudLastDeviceTypeString = [NSString stringWithFormat:getLocalizedString(@"CloudLoginClientType", nil),cloudLastDeviceType];
        NSString *cloudLastLoginInfo = [NSString stringWithFormat:@"%@%@%@",cloudLastAccessDateString,cloudLastAccessIPString,cloudLastDeviceTypeString];
        [[UIApplication sharedApplication].keyWindow makeToast:cloudLastLoginInfo duration:1.5 position:@"center" title:getLocalizedString(@"CloudLoginLastNotice", nil)];
    }
}

#pragma mark TextFieldDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [self textNormal:self.cloudLoginHighLightTextField];
    [self textHighLight:textField];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = (rect.size.height-216-40) - CGRectGetMaxY(self.cloudLoginIdentifyView.frame);
    if(rect.origin.y < 0)
        self.view.frame = rect;
    [UIView commitAnimations];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self.view endEditing:YES];
    [self textNormal:self.cloudLoginHighLightTextField];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = 0;
    self.view.frame = rect;
    [UIView commitAnimations];
}
@end
