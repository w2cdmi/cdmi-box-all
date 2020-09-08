//
//  MailCloudAttachmentAddViewController.m
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailCloudAttachmentAddViewController.h"
#import "AppDelegate.h"
#import "File+Remote.h"
#import "FileThumbnail.h"
#import "CloudPreviewController.h"

#pragma mark cloudFile tableViewCell
@protocol MailCloudAttchmentAddDelegate <NSObject>

@required

- (void)mailCloudAttchmentSelecte:(File*)file;

@end

@interface MailCloudAttachmentAddCell : UITableViewCell

@property (nonatomic, strong) File *file;
@property (nonatomic, assign) BOOL  fileSelected;

@property (nonatomic, strong) UIView      *fileThumbnailView;
@property (nonatomic, strong) UIImageView *fileImageView;
@property (nonatomic, strong) UILabel     *fileTitleLable;
@property (nonatomic, strong) UILabel     *fileTimeLable;
@property (nonatomic, strong) UILabel     *fileSizeLable;
@property (nonatomic, strong) UIButton    *fileCheckBox;
@property (nonatomic, strong) UIImageView *fileDownloadImageView;

@property (nonatomic, weak)id<MailCloudAttchmentAddDelegate>delegate;

@property (nonatomic, strong) NSDateFormatter *fileDateFormatter;

@end

@implementation MailCloudAttachmentAddCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        
        self.fileDateFormatter =[[NSDateFormatter alloc] init];
        [self.fileDateFormatter setDateFormat:@"YYYY/MM/dd HH:mm:ss"];
        [self.fileDateFormatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
        
        self.fileThumbnailView = [[UIView alloc] initWithFrame:CGRectZero];
        [self.contentView addSubview:self.fileThumbnailView];
        
        self.fileImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self.fileThumbnailView addSubview:self.fileImageView];
        
        self.fileTitleLable = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.fileTitleLable];
        
        self.fileTimeLable = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.fileTimeLable];
        
        self.fileSizeLable = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"999999" alpha:1.0f] textAlignment:NSTextAlignmentRight];
        [self.contentView addSubview:self.fileSizeLable];
        
        self.fileCheckBox = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.fileCheckBox addTarget:self action:@selector(mailCloudAttchmentSelecte) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.fileCheckBox];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    
    self.fileThumbnailView.frame = CGRectMake(15, 6, 56, 56);
    self.fileImageView.frame = CGRectMake(4, 4, 48, 48);
    [FileThumbnail imageWithFile:self.file imageView:self.fileImageView];
    
    self.fileTitleLable.frame = CGRectMake(CGRectGetMaxX(self.fileThumbnailView.frame)+10, 11, CGRectGetWidth(self.frame)-CGRectGetMaxX(self.fileThumbnailView.frame)-10-15-22-15, 22);
    self.fileTitleLable.text = self.file.fileName;
    
    if ([self.file isFolder]) {
        self.fileSizeLable.frame = CGRectZero;
    } else {
        CGSize adjustSizeSize = [CommonFunction labelSizeWithString:[CommonFunction pretySize:self.file.fileSize.longLongValue] font:[UIFont systemFontOfSize:12.0f]];
        self.fileSizeLable.text = [CommonFunction pretySize:self.file.fileSize.longLongValue];
        self.fileSizeLable.frame = CGRectMake(CGRectGetWidth(self.frame)-15-22-15-adjustSizeSize.width, CGRectGetMaxY(self.fileTitleLable.frame)+4, adjustSizeSize.width, 20);
    }
    
    self.fileTimeLable.frame = CGRectMake(CGRectGetMaxX(self.fileThumbnailView.frame)+10, CGRectGetMaxY(self.fileTitleLable.frame)+4, CGRectGetWidth(self.fileTitleLable.frame)-CGRectGetWidth(self.fileSizeLable.frame), 20);
    self.fileTimeLable.text = [self.fileDateFormatter stringFromDate:self.file.fileModifiedDate];
    
    self.fileCheckBox.frame = CGRectMake(CGRectGetWidth(self.frame)-5-42, (CGRectGetHeight(self.frame)-42)/2, 42, 42);
    self.fileCheckBox.imageView.frame = CGRectMake(10, 10, 22, 22);
    
    if (self.file.fileType.integerValue == TypeImage) {
        NSString *fileDataLocalPath = [self.file fileDataLocalPath];
        if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
            if (!self.fileDownloadImageView.superview) {
                [self.fileThumbnailView addSubview:self.fileDownloadImageView];
            }
        } else {
            if (self.fileDownloadImageView.superview) {
                [self.fileDownloadImageView removeFromSuperview];
            }
        }
    }
}

- (UIImageView*)fileDownloadImageView {
    if (!_fileDownloadImageView) {
        _fileDownloadImageView = [[UIImageView alloc] initWithFrame:CGRectMake(56-((56-48)/2-1+22), (56-48)/2-1, 22, 22)];
        _fileDownloadImageView.image = [UIImage imageNamed:@"ic_list_image_download"];
    }
    return _fileDownloadImageView;
}

- (void) setFileSelected:(BOOL)fileSelected {
    if (_fileSelected != fileSelected) {
        _fileSelected = fileSelected;
    }
    if (fileSelected) {
        [self.fileCheckBox setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.fileCheckBox setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    }
}

- (void)setFile:(File *)file {
    if (_file != file) {
        _file = file;
    }
}

- (void)mailCloudAttchmentSelecte {
    self.fileSelected = !self.fileSelected;
    if ([self.delegate respondsToSelector:@selector(mailCloudAttchmentSelecte:)]) {
        [self.delegate mailCloudAttchmentSelecte:self.file];
    }
}
@end


#pragma mark cloudFile viewController
@interface MailCloudAttachmentAddViewController ()<UITableViewDataSource,UITableViewDelegate,MailCloudAttchmentAddDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) NSArray *subFileArray;

@property (nonatomic, strong) UILabel *mailAttachmentAddTitleLabel;
@property (nonatomic, strong) UIButton *mailAttachmentAddBackButton;

@property (nonatomic, strong) UITableView *mailAttachmentAddTableView;

@property (nonatomic, strong) NSMutableArray *mailAttachmentAddArray;

@end

@implementation MailCloudAttachmentAddViewController

- (id)initWithFile:(File*)file {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.file = file;
        self.subFileArray = [[NSArray alloc] initWithArray:[file subItems]];
        self.mailAttachmentAddArray = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.mailAttachmentAddTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailAttachmentAddTitleLabel.text = NSLocalizedString(@"MailChatAttAttachment", nil);
    
    self.mailAttachmentAddBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailAttachmentAddBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailAttachmentAddBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailAttachmentAddBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailAttachmentAddBackButton addTarget:self action:@selector(mailAttachmentAddBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    UIView *mailCloudAttachmentAddControl = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(self.view.frame)-49, CGRectGetWidth(self.view.frame), 49)];
    mailCloudAttachmentAddControl.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
    mailCloudAttachmentAddControl.layer.borderWidth = 0.5;
    mailCloudAttachmentAddControl.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
    [self.view addSubview:mailCloudAttachmentAddControl];
    
    UIButton *mailCloudAttachmentCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(mailCloudAttachmentAddControl.frame)/2, 49)];
    UILabel *mailCloudAttachmentCancelLabel = [CommonFunction labelWithFrame:CGRectMake(15, 0, CGRectGetWidth(mailCloudAttachmentCancelButton.frame)-15, CGRectGetHeight(mailCloudAttachmentCancelButton.frame)) textFont:[UIFont systemFontOfSize:16.0f] textColor:[CommonFunction colorWithString:@"008be8" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
    mailCloudAttachmentCancelLabel.text = NSLocalizedString(@"Cancel",nil);
    [mailCloudAttachmentCancelButton addSubview:mailCloudAttachmentCancelLabel];
    [mailCloudAttachmentCancelButton addTarget:self action:@selector(cancel) forControlEvents:UIControlEventTouchUpInside];
    [mailCloudAttachmentAddControl addSubview:mailCloudAttachmentCancelButton];
    
    UIButton *mailCloudAttachmentConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(mailCloudAttachmentAddControl.frame)/2, 0, CGRectGetWidth(mailCloudAttachmentAddControl.frame)/2, 49)];
    UILabel *mailCloudAttachmentConfirmLabel = [CommonFunction labelWithFrame:CGRectMake(0, 0, CGRectGetWidth(mailCloudAttachmentCancelButton.frame)-15, CGRectGetHeight(mailCloudAttachmentCancelButton.frame)) textFont:[UIFont systemFontOfSize:16.0f] textColor:[CommonFunction colorWithString:@"008be8" alpha:1.0f] textAlignment:NSTextAlignmentRight];
    mailCloudAttachmentConfirmLabel.text = NSLocalizedString(@"Confirm",nil);
    [mailCloudAttachmentConfirmButton addSubview:mailCloudAttachmentConfirmLabel];
    [mailCloudAttachmentConfirmButton addTarget:self action:@selector(confirm) forControlEvents:UIControlEventTouchUpInside];
    [mailCloudAttachmentAddControl addSubview:mailCloudAttachmentConfirmButton];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    self.mailAttachmentAddTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49) style:UITableViewStylePlain];
    [self.mailAttachmentAddTableView registerClass:[MailCloudAttachmentAddCell class] forCellReuseIdentifier:@"mailCloudAttachmentAddCell"];
    self.mailAttachmentAddTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.mailAttachmentAddTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.mailAttachmentAddTableView.delegate = self;
    self.mailAttachmentAddTableView.dataSource = self;
    self.mailAttachmentAddTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.mailAttachmentAddTableView];
    [self reloadDataSource];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.mailAttachmentAddTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailAttachmentAddBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailAttachmentAddTitleLabel removeFromSuperview];
    [self.mailAttachmentAddBackButton removeFromSuperview];
}

- (void)mailAttachmentAddBackButtonClick {
    self.completion(self.mailAttachmentAddArray,NO);
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)cancel {
    [self.navigationController popToViewController:self.rootViewController animated:YES];
}

- (void)confirm {
    self.completion(self.mailAttachmentAddArray,YES);
    [self.navigationController popToViewController:self.rootViewController animated:YES];
}

#pragma mark mail attachment select
- (void)mailCloudAttchmentSelecte:(File *)file {
    if ([self.mailAttachmentAddArray containsObject:file]) {
        [self.mailAttachmentAddArray removeObject:file];
    } else {
        [self.mailAttachmentAddArray addObject:file];
    }
}

#pragma mark tableview delegate+datasource
- (void)reloadDataSource {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        __block __weak typeof(self) weak = self;
        [self.file folderUpdate:^(id retobj) {
            
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                __strong typeof(weak) strong = weak;
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                if (httpResponse.statusCode == 404) {
                    [strong.file fileRemove:nil];
                    [strong.navigationController popToRootViewControllerAnimated:YES];
                }
                [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudFileLoadFailedPrompt", nil)];
            });
        }];
    });
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.subFileArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MailCloudAttachmentAddCell *cell = [tableView dequeueReusableCellWithIdentifier:@"MailCloudAttachmentAddCell"];
    if (!cell) {
        cell = [[MailCloudAttachmentAddCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"MailCloudAttachmentAddCell"];
    }
    cell.file = [self.subFileArray objectAtIndex:indexPath.row];
    cell.delegate = self;
    if ([self.mailAttachmentAddArray containsObject:cell.file]) {
        cell.fileSelected = YES;
    } else {
        cell.fileSelected = NO;
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MailCloudAttachmentAddCell *cell = (MailCloudAttachmentAddCell*)[tableView cellForRowAtIndexPath:indexPath];
    File *file = cell.file;
    if (file.isFolder) {
        if (!cell.fileSelected) {
            MailCloudAttachmentAddViewController *attachmentViewController = [[MailCloudAttachmentAddViewController alloc] initWithFile:file];
            attachmentViewController.completion = ^(NSArray *attachmentArray,BOOL comfirm) {
                [self.mailAttachmentAddArray addObjectsFromArray:attachmentArray];
                if (comfirm) {
                    self.completion(self.mailAttachmentAddArray,comfirm);
                }
            };
            attachmentViewController.rootViewController = self.rootViewController;
            [self.navigationController pushViewController:attachmentViewController animated:YES];
        }
    } else {
        CloudPreviewController *previewController = [[CloudPreviewController alloc] initWithFile:file];
        [self.navigationController pushViewController:previewController animated:YES];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}
@end
