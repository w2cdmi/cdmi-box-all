//
//  CloudFolderCreateViewController.m
//  OneMail
//
//  Created by cse  on 15/11/25.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define CreateFolderViewTop 22
#define CreateFolderViewHeight 68

#define CreateFolderImageViewTop 6
#define CreateFolderImageViewLeft 15
#define CreateFolderImageViewRight 10
#define CreateFolderImageViewWidth 56
#define CreateFolderImageViewHeight 56

#define CreateFolderTextFieldHeight 22
#define CreateFolderTextFieldRight 15

#import "CloudFolderCreateViewController.h"
#import "AppDelegate.h"
#import "File+Remote.h"
#import "TeamSpace+Remote.h"

@interface CloudFolderCreateViewController ()<UITextFieldDelegate>

@property (nonatomic, strong) UITextField *folderCreateTextField;

@end

@implementation CloudFolderCreateViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    if (self.file) {
        self.title = getLocalizedString(@"CloudCreateFolderTitle", nil);
    } else {
        self.title = getLocalizedString(@"CloudSpaceCreate", nil);
    }
    
    self.view.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    
    UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc] initWithTitle:getLocalizedString(@"Confirm", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(createFolder)];
    self.navigationItem.leftBarButtonItem = leftItem;
    self.navigationItem.rightBarButtonItem = rightItem;
    [self.navigationItem.rightBarButtonItem setEnabled:NO];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    UIView *folderCreateView = [[UIView alloc] initWithFrame:CGRectMake(0, statusFrame.size.height+navigationFrame.size.height+CreateFolderViewTop, CGRectGetWidth(self.view.frame), CreateFolderViewHeight)];
    folderCreateView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    [self.view addSubview:folderCreateView];
    
    
    UIImageView *folderCreateImageView = [[UIImageView alloc] initWithFrame:CGRectMake(CreateFolderImageViewLeft, CreateFolderImageViewTop, CreateFolderImageViewWidth, CreateFolderImageViewHeight)];
    folderCreateImageView.backgroundColor = [UIColor clearColor];
    if (self.file) {
        folderCreateImageView.image = [UIImage imageNamed:@"ic_list_folder"];
    } else {
        folderCreateImageView.image = [UIImage imageNamed:@"ic_team"];
    }
    [folderCreateView addSubview:folderCreateImageView];
    
    self.folderCreateTextField = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(folderCreateImageView.frame)+CreateFolderImageViewRight, (CreateFolderViewHeight-CreateFolderTextFieldHeight)/2, CGRectGetWidth(folderCreateView.frame)-CGRectGetMaxX(folderCreateImageView.frame)-CreateFolderImageViewRight-CreateFolderTextFieldRight, CreateFolderTextFieldHeight)];
    self.folderCreateTextField.backgroundColor = [UIColor clearColor];
    self.folderCreateTextField.text = nil;
    self.folderCreateTextField.placeholder = @"首文件名不应有./\\等特殊符";
    self.folderCreateTextField.font = [UIFont systemFontOfSize:17.0f];
    self.folderCreateTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    self.folderCreateTextField.textAlignment = NSTextAlignmentLeft;
    self.folderCreateTextField.delegate = self;
    [folderCreateView addSubview:self.folderCreateTextField];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.folderCreateTextField becomeFirstResponder];
    [self.folderCreateTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.folderCreateTextField resignFirstResponder];
    [self.folderCreateTextField removeTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)createFolder {
    if (([self.folderCreateTextField.text rangeOfString:@"/"].length +
         [self.folderCreateTextField.text rangeOfString:@"\\"].length)
        ||([self.folderCreateTextField.text hasPrefix:@"."])
        ||([self.folderCreateTextField.text hasSuffix:@"."])) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudNameIllegal", nil)];
        return;
    }
    if (![self.folderCreateTextField.text isEqualToString:@""]) {
        [self.folderCreateTextField resignFirstResponder];
        NSString *folderCreate=  [self.folderCreateTextField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        [self.navigationItem.rightBarButtonItem setEnabled:NO];
        if (self.file) {
            [self.file folderCreate:folderCreate succeed:^(id retobj) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.navigationController popViewControllerAnimated:YES];
                });
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
                    [self.navigationItem.rightBarButtonItem setEnabled:YES];
                });
            }];
        }
        else {
            [TeamSpace spaceCreate:folderCreate succeed:^(id retobj) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.navigationController popViewControllerAnimated:YES];
                });
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.navigationItem.rightBarButtonItem setEnabled:YES];
                    [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
                });
            }];
        
        
        }
        
    }
}

#pragma mark textFieldDelegate
- (void)textFieldDidChange:(UITextField*)textField {
    if (![textField.text isEqualToString:@""]) {
        [self.navigationItem.rightBarButtonItem setEnabled:YES];
    } else {
        [self.navigationItem.rightBarButtonItem setEnabled:NO];
    }
    if (textField.text.length > 246) {
        textField.text = [textField.text substringToIndex:246];
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudNameLimit", nil)];
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if (string.length == 0) {
        return YES;
    }
    NSInteger existedLength = textField.text.length;
    NSInteger selectedLength = range.length;
    NSInteger replaceLength = string.length;
    if (existedLength - selectedLength + replaceLength > 246) {
        return NO;
    }
    return YES;
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}
@end
