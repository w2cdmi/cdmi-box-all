//
//  CloudFileVersionController.m
//  OneMail
//
//  Created by cse  on 15/11/19.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define FileInfoViewHeight 68
#define FileInfoViewTop 22
#define FileInfoViewBottom 22
#define FileImageViewWidth 56
#define FileImageViewHeight 56
#define FileImageViewTop 6
#define FileImageViewLeft 15
#define FileImageViewRight 10
#define FileNameLabelHeight 22
#define FileNameLabelRight 15

#import "CloudFileVersionController.h"
#import "CloudFileVersionCell.h"
#import "AppDelegate.h"
#import "CloudFileVersionHeaderCell.h"
#import "FileThumbnail.h"
#import "File+Remote.h"

@interface CloudFileVersionController() <UITableViewDataSource,UITableViewDelegate,NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) NSMutableArray *fileVersionArray;
@property (nonatomic, strong) UIView *fileInfoView;
@property (nonatomic, strong) UITableView *fileVersionTableView;

@end

@implementation CloudFileVersionController
- (id)initWithFile:(File *)file{
    if (self = [super init]) {
        self.file = file;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = getLocalizedString(@"CloudVersionTitle", nil);
    self.view.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = appDelegate.navigationController.navigationBar.frame;
    self.fileInfoView.frame = CGRectMake(0, statusBarFrame.size.height+navigationFrame.size.height+FileInfoViewTop,CGRectGetWidth(self.fileInfoView.frame), CGRectGetHeight(self.fileInfoView.frame));
    [self.view addSubview:self.fileInfoView];
    
    self.fileVersionTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    [self.fileVersionTableView registerClass:[CloudFileVersionCell class] forCellReuseIdentifier:@"CloudFileVersionCell"];
    self.fileVersionTableView.delegate = self;
    self.fileVersionTableView.dataSource = self;
    [self.view addSubview:self.fileVersionTableView];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.file fileVersionList:^(id retobj) {
        self.fileVersionArray = retobj;
        [self refreshVersionTableViewFrame];
        [self.fileVersionTableView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [self refreshVersionTableViewFrame];
        [self.fileVersionTableView reloadData];
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudVersionFailedPrompt", nil)];
    }];
}

- (UIView*)fileInfoView {
    if (!_fileInfoView) {
        _fileInfoView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), FileInfoViewHeight)];
        _fileInfoView.backgroundColor = [UIColor whiteColor];
        UIImageView *fileImageView = [[UIImageView alloc] initWithFrame:CGRectMake(FileImageViewLeft, FileImageViewTop, FileImageViewWidth, FileImageViewHeight)];
        [FileThumbnail imageWithFile:self.file imageView:fileImageView];
        [_fileInfoView addSubview:fileImageView];
        UILabel *fileNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(fileImageView.frame)+FileImageViewRight, (FileInfoViewHeight-FileNameLabelHeight)/2, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(fileImageView.frame)-FileImageViewRight-FileNameLabelRight, FileNameLabelHeight)];
        fileNameLabel.font = [UIFont systemFontOfSize:18.0f];
        fileNameLabel.textColor = [UIColor blackColor];
        fileNameLabel.textAlignment = NSTextAlignmentLeft;
        fileNameLabel.text = self.file.fileName;
        [_fileInfoView addSubview:fileNameLabel];
    }
    return _fileInfoView;
}

- (void)refreshVersionTableViewFrame {
    CGFloat maxHeight = CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.fileInfoView.frame)-FileInfoViewBottom;
    CGFloat tableViewHeight = VersionCellHeight*self.fileVersionArray.count;
    if (tableViewHeight > maxHeight) {
        self.fileVersionTableView.frame = CGRectMake(0, CGRectGetMaxY(self.fileInfoView.frame)+FileInfoViewBottom, CGRectGetWidth(self.view.frame), maxHeight);
        self.fileVersionTableView.scrollEnabled = YES;
    } else {
        self.fileVersionTableView.frame = CGRectMake(0, CGRectGetMaxY(self.fileInfoView.frame)+FileInfoViewBottom, CGRectGetWidth(self.view.frame), tableViewHeight);
        self.fileVersionTableView.scrollEnabled = NO;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (self.fileVersionArray.count > 0) {
        tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    } else {
        tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    return self.fileVersionArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    CloudFileVersionCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CloudFileVersionCell"];
    if (!cell) {
        cell = [[CloudFileVersionCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"CloudFileVersionCell"];
    }
    NSManagedObjectID *objectID = [self.fileVersionArray objectAtIndex:indexPath.row];
    Version *version = [Version getVersionWithObjectID:objectID];
    cell.version = version;
    cell.versionController = self;
    cell.versionLabel.text = [NSString stringWithFormat:@"V%ld",(unsigned long)(self.fileVersionArray.count -indexPath.row)];
    if (indexPath.row == 0) {
        cell.versionLabel.backgroundColor = [CommonFunction colorWithString:@"2d90e5" alpha:1.0f];
    }
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return VersionCellHeight;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger sectionMax = [tableView numberOfSections];
    NSInteger rowNumOfSectionMax = [tableView numberOfRowsInSection:sectionMax-1];
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        if (indexPath.section == sectionMax-1 && indexPath.row == rowNumOfSectionMax-1) {
            [cell setSeparatorInset:UIEdgeInsetsZero];
        } else {
            [cell setSeparatorInset:UIEdgeInsetsMake(0, 15, 0, 15)];
        }
    }
//    float systemVersion = [[UIDevice currentDevice] systemVersion].floatValue;
//    if (systemVersion >= 8.4) {
//        if ([cell respondsToSelector:@selector(setPreservesSuperviewLayoutMargins:)]) {
//            [cell setPreservesSuperviewLayoutMargins:NO];
//        }
//        if ([cell respondsToSelector:@selector(setLayoutMargins:)]) {
//            [cell setLayoutMargins:UIEdgeInsetsZero];
//        }
//    }
}

@end
