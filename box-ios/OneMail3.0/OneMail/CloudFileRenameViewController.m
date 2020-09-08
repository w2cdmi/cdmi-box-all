//
//  CloudFileRenameViewController.m
//  OneMail
//
//  Created by cse  on 15/11/25.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define FileRenameViewTop 22
#define FileRenameViewHeight 68

#define FileRenameImageViewTop 6
#define FileRenameImageViewLeft 15
#define FileRenameImageViewRight 10
#define FileRenameImageViewWidth 56
#define FileRenameImageViewHeight 56

#define FileRenameTextFieldHeight 22
#define FileRenameTextFieldRight 15

#import "CloudFileRenameViewController.h"
#import "AppDelegate.h"
#import "File+Remote.h"
#import "FileThumbnail.h"
#import "UIAlertView+Blocks.h"
#import "TransportTask.h"
@interface CloudFileRenameViewController ()<UITextFieldDelegate>

@property (nonatomic, strong) UITextField *fileRenameTextField;

@end

@implementation CloudFileRenameViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = getLocalizedString(@"CloudRenameTitle", nil);
    self.view.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    
    UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc] initWithTitle:getLocalizedString(@"Confirm", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(fileRename)];
    self.navigationItem.leftBarButtonItem = leftItem;
    self.navigationItem.rightBarButtonItem = rightItem;
    [self.navigationItem.rightBarButtonItem setEnabled:NO];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    UIView *fileRenameView = [[UIView alloc] initWithFrame:CGRectMake(0, statusFrame.size.height+navigationFrame.size.height+FileRenameViewTop, CGRectGetWidth(self.view.frame), FileRenameViewHeight)];
    fileRenameView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    [self.view addSubview:fileRenameView];
    
    UIImageView *fileRenameImageView = [[UIImageView alloc] initWithFrame:CGRectMake(FileRenameImageViewLeft, FileRenameImageViewTop, FileRenameImageViewWidth, FileRenameImageViewHeight)];
    fileRenameImageView.backgroundColor = [UIColor clearColor];
    [FileThumbnail imageWithFile:self.file imageView:fileRenameImageView];
    [fileRenameView addSubview:fileRenameImageView];
    
    self.fileRenameTextField = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(fileRenameImageView.frame)+FileRenameImageViewRight, (FileRenameViewHeight-FileRenameTextFieldHeight)/2, CGRectGetWidth(fileRenameView.frame)-CGRectGetMaxX(fileRenameImageView.frame)-FileRenameImageViewRight-FileRenameTextFieldRight, FileRenameTextFieldHeight)];
    self.fileRenameTextField.backgroundColor = [UIColor clearColor];
    self.fileRenameTextField.text = self.file.fileName;
    self.fileRenameTextField.font = [UIFont systemFontOfSize:17.0f];
    self.fileRenameTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
    self.fileRenameTextField.textAlignment = NSTextAlignmentLeft;
    self.fileRenameTextField.delegate = self;
    [fileRenameView addSubview:self.fileRenameTextField];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.fileRenameTextField becomeFirstResponder];
    [self.fileRenameTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.fileRenameTextField resignFirstResponder];
    [self.fileRenameTextField removeTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)fileRename {
    if (([self.fileRenameTextField.text rangeOfString:@"/"].length +
         [self.fileRenameTextField.text rangeOfString:@"\\"].length)
        ||([self.fileRenameTextField.text hasPrefix:@"."])
        ||([self.fileRenameTextField.text hasSuffix:@"."])) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudNameIllegal", nil)];
        return;
    }
    if (self.file.transportTask && (self.file.transportTask.taskStatus.integerValue == TaskRunning)) {
        return;
    }
    [self.fileRenameTextField resignFirstResponder];
    NSString *folderCreate=  [self.fileRenameTextField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    
    [self.file fileRename:folderCreate succeed:^(id retobj) {
        NSString *dataLocalPath = self.file.fileDataLocalPath;
        NSString *cacheLocalPath = self.file.fileCacheLocalPath;
        NSString *thumbnailLocalPath = self.file.fileThumbnailLocalPath;
        NSString *compressImageLocalPath = self.file.fileCompressImagePath;
        [self.file saveFileName:folderCreate];
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        File *shadow = (File *)[delegate.localManager.managedObjectContext objectWithID:self.file.objectID];
        NSFileManager *manager = [NSFileManager defaultManager];
        if ([manager fileExistsAtPath:dataLocalPath]) {
            [manager moveItemAtPath:dataLocalPath toPath:shadow.fileDataLocalPath error:nil];
        }
        if ([manager fileExistsAtPath:cacheLocalPath]) {
            [manager moveItemAtPath:cacheLocalPath toPath:shadow.fileCacheLocalPath error:nil];
        }if ([manager fileExistsAtPath:thumbnailLocalPath]) {
            [manager moveItemAtPath:thumbnailLocalPath toPath:shadow.fileThumbnailLocalPath error:nil];
        }if ([manager fileExistsAtPath:compressImageLocalPath]) {
            [manager moveItemAtPath:compressImageLocalPath toPath:shadow.fileCompressImagePath error:nil];
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
        });
    }];
}

#pragma mark textFieldDelegate
- (void)textFieldDidChange:(UITextField*)textField {
    if ([textField.text isEqualToString:@""] || [textField.text isEqualToString:self.file.fileName]) {
        [self.navigationItem.rightBarButtonItem setEnabled:NO];
    } else {
        [self.navigationItem.rightBarButtonItem setEnabled:YES];
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
