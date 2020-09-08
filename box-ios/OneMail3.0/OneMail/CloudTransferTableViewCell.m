//
//  CloudTransferTableViewCell.m
//  OneMail
//
//  Created by cse  on 15/11/13.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudTransferTableViewCell.h"
#import "CloudTransferViewController.h"
#import "TransportTask.h"
#import "TransportTaskHandle.h"
#import "File.h"
#import "FileThumbnail.h"
#import "Version.h"
#import "AppDelegate.h"

@interface CloudTransferTableViewCell () <UIActionSheetDelegate>

@property (nonatomic, strong) UIView         *transferImageBackground;
@property (nonatomic, strong) UIImageView    *transferImageView;
@property (nonatomic, strong) UIImageView    *transferImageStatus;
@property (nonatomic, strong) UILabel        *transferTitleLabel;
@property (nonatomic, strong) UILabel        *transferSizeLabel;
@property (nonatomic, strong) UILabel        *transferStatusLabel;
@property (nonatomic, strong) UIButton       *transferFunctionButton;
@property (nonatomic, strong) UIButton       *transferDeleteButton;
@property (nonatomic, strong) UIProgressView *transferProgressView;

@end

@implementation CloudTransferTableViewCell
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        [self.imageView removeFromSuperview];
        [self.textLabel removeFromSuperview];
        [self.detailTextLabel removeFromSuperview];
        
        self.transferImageBackground = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 56, 56)];
        self.transferImageBackground.backgroundColor = [UIColor whiteColor];
        [self.contentView addSubview:self.transferImageBackground];
        
        self.transferImageView = [[UIImageView alloc] initWithFrame:CGRectMake(4, 4, 48, 48)];
        self.transferImageView.backgroundColor = [UIColor clearColor];
        [self.transferImageBackground addSubview:self.transferImageView];
        
        self.transferImageStatus = [[UIImageView alloc] initWithFrame:CGRectMake(56-20, 56-20, 20, 20)];
        self.transferImageStatus.backgroundColor = [UIColor clearColor];
        [self.transferImageBackground addSubview:self.transferImageStatus];
        
        self.transferTitleLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.transferTitleLabel];
        
        self.transferSizeLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.transferSizeLabel];
        
        self.transferStatusLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"333333" alpha:1.0f] textAlignment:NSTextAlignmentRight];
        [self.contentView addSubview:self.transferStatusLabel];
        
        self.transferFunctionButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.transferFunctionButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.transferFunctionButton addTarget:self action:@selector(taskControl) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.transferFunctionButton];
        
        self.transferDeleteButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.transferDeleteButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.transferDeleteButton setImage:[UIImage imageNamed:@"ic_transfer_delete_nor"] forState:UIControlStateNormal];
        [self.transferDeleteButton setImage:[UIImage imageNamed:@"ic_transfer_delete_press"] forState:UIControlStateHighlighted];
        [self.transferDeleteButton addTarget:self action:@selector(deleteTask) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.transferDeleteButton];
        
        self.transferProgressView = [[UIProgressView alloc] initWithProgressViewStyle:UIProgressViewStyleDefault];
        self.transferProgressView.progressTintColor = [CommonFunction colorWithString:@"6fcc31" alpha:1.0f];
        self.transferProgressView.trackTintColor = [UIColor clearColor];
        [self.contentView addSubview:self.transferProgressView];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.transferImageBackground.frame = CGRectMake(15, (CGRectGetHeight(self.frame)-CGRectGetHeight(self.transferImageBackground.frame))/2, CGRectGetWidth(self.transferImageBackground.frame), CGRectGetHeight(self.transferImageBackground.frame));
    self.transferDeleteButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-CGRectGetWidth(self.transferDeleteButton.frame), (CGRectGetHeight(self.frame)-CGRectGetHeight(self.transferDeleteButton.frame))/2, CGRectGetWidth(self.transferDeleteButton.frame), CGRectGetHeight(self.transferDeleteButton.frame));
    self.transferFunctionButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-CGRectGetWidth(self.transferDeleteButton.frame)-CGRectGetWidth(self.transferFunctionButton.frame), (CGRectGetHeight(self.frame)-CGRectGetHeight(self.transferFunctionButton.frame))/2, CGRectGetWidth(self.transferFunctionButton.frame), CGRectGetHeight(self.transferFunctionButton.frame));
    self.transferTitleLabel.frame = CGRectMake(CGRectGetMaxX(self.transferImageBackground.frame)+10, 11, CGRectGetMinX(self.transferFunctionButton.frame)-CGRectGetMaxX(self.transferImageBackground.frame)-10, 22);
    self.transferSizeLabel.frame = CGRectMake(CGRectGetMinX(self.transferTitleLabel.frame), CGRectGetMaxY(self.transferTitleLabel.frame)+4, CGRectGetWidth(self.transferTitleLabel.frame)/2, 20);
    self.transferStatusLabel.frame = CGRectMake(CGRectGetMaxX(self.transferSizeLabel.frame), CGRectGetMaxY(self.transferTitleLabel.frame)+4, CGRectGetWidth(self.transferTitleLabel.frame)/2, 20);
    self.transferProgressView.frame = CGRectMake(15, CGRectGetHeight(self.frame)-2, CGRectGetWidth(self.frame)-30, 4);
    
    if (self.transportTask.taskType.integerValue == TaskVersionDownload) {
        [self.transferImageStatus setImage:[UIImage imageNamed:@"ic_transfer_download"]];
        [FileThumbnail imageWithVersion:self.transportTask.version imageView:self.transferImageView];
        self.transferTitleLabel.text = self.transportTask.version.versionFileName;
    } else {
        if (self.transportTask.taskType.integerValue == TaskFileDownload ||
            self.transportTask.taskType.integerValue == TaskFolderDownload) {
            [self.transferImageStatus setImage:[UIImage imageNamed:@"ic_transfer_download"]];
        } else {
            [self.transferImageStatus setImage:[UIImage imageNamed:@"ic_transfer_upload"]];
        }
        [FileThumbnail imageWithFile:self.transportTask.file imageView:self.transferImageView];
        self.transferTitleLabel.text = self.transportTask.file.fileName;
    }
    [self refreshSize];
    [self transferStatus];
}

- (void)refreshFraction {
    if (self.transportTask.taskStatus.integerValue == TaskRunning) {
        NSInteger fractionComplete = (NSInteger)floor(self.transportTask.taskHandle.taskFraction*100);
        self.transferStatusLabel.text = [NSString stringWithFormat:@"%ld%@",(long)fractionComplete,@"%"];
    }
}

- (void)refreshProgress {
    [self.transferProgressView setProgress:self.transportTask.taskHandle.taskFraction];
}

- (void)refreshSize {
    if (self.transportTask.taskType.integerValue == TaskVersionDownload) {
        self.transferSizeLabel.text = [CommonFunction pretySize:self.transportTask.version.versionSize.longLongValue];
    } else {
        if ([self.transportTask.file isFile]) {
            self.transferSizeLabel.text = [CommonFunction pretySize:self.transportTask.file.fileSize.longLongValue];
        } else {
            self.transferSizeLabel.text = [CommonFunction pretySize:[self folderSizeWithFolder:self.transportTask.file]];
        }
    }
}

- (long long)folderSizeWithFolder:(File*)file {
    long long size = 0;
    NSArray *subItems = [file subItems];
    for (File *subItem in subItems) {
        if (subItem.transportTask && subItem.transportTask.taskStatus.integerValue != TaskCancel) {
            if ([subItem isFile]) {
                size = size + subItem.fileSize.longLongValue;
            } else {
                size = size + [self folderSizeWithFolder:subItem];
            }
        }
    }
    return size;
}

- (void)setTransportTask:(TransportTask *)transportTask {
    if (_transportTask != transportTask) {
        if (_transportTask) {
            [_transportTask.taskHandle removeObserver:self forKeyPath:@"taskFraction"];
            [_transportTask.taskHandle removeObserver:self forKeyPath:@"taskTotalUnitCount"];
            [_transportTask removeObserver:self forKeyPath:@"taskStatus"];
        }
        _transportTask = transportTask;
        if (transportTask) {
            [self.transferProgressView setProgress:transportTask.taskFraction.floatValue animated:NO];
            [transportTask.taskHandle addObserver:self forKeyPath:@"taskFraction" options:NSKeyValueObservingOptionNew context:nil];
            [transportTask.taskHandle addObserver:self forKeyPath:@"taskTotalUnitCount" options:NSKeyValueObservingOptionNew context:nil];
            [transportTask addObserver:self forKeyPath:@"taskStatus" options:NSKeyValueObservingOptionNew context:nil];
        }
    }
}

- (void)dealloc {
    if (_transportTask) {
        [_transportTask.taskHandle removeObserver:self forKeyPath:@"taskFraction"];
        [_transportTask.taskHandle removeObserver:self forKeyPath:@"taskTotalUnitCount"];
        [_transportTask removeObserver:self forKeyPath:@"taskStatus"];
    }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.transportTask.taskHandle) {
        if ([keyPath isEqual:@"taskFraction"]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self refreshProgress];
                [self refreshFraction];
            });
        }
        if ([keyPath isEqual:@"taskTotalUnitCount"]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self refreshSize];
            });
        }
    }
    if (object == self.transportTask) {
        if ([keyPath isEqual:@"taskStatus"]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self transferStatus];
            });
        }
    }
}

- (void)transferStatus {
    switch (self.transportTask.taskStatus.integerValue) {
        case TaskInitialing:
            self.transferFunctionButton.hidden = NO;
            self.transferProgressView.hidden = NO;
            [self.transferProgressView setProgress:[self.transportTask.taskFraction floatValue]];
            self.transferStatusLabel.text = getLocalizedString(@"CloudTransferStatusInitialing", nil);
            self.transferStatusLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_pause_nor"] forState:UIControlStateNormal];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_pause_press"] forState:UIControlStateHighlighted];
            break;
        case TaskRunning:
            self.transferFunctionButton.hidden = NO;
            self.transferProgressView.hidden = NO;
            self.transferStatusLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_pause_nor"] forState:UIControlStateNormal];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_pause_press"] forState:UIControlStateHighlighted];
            [self refreshFraction];
            [self refreshProgress];
            break;
        case TaskWaitting:
            self.transferFunctionButton.hidden = NO;
            self.transferProgressView.hidden = NO;
            [self.transferProgressView setProgress:[self.transportTask.taskFraction floatValue]];
            self.transferStatusLabel.text = getLocalizedString(@"CloudTransferStatusWaitting", nil);
            self.transferStatusLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_pause_nor"] forState:UIControlStateNormal];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_pause_press"] forState:UIControlStateHighlighted];
            break;
        case TaskSuspend:
            self.transferFunctionButton.hidden = NO;
            self.transferProgressView.hidden = NO;
            [self.transferProgressView setProgress:[self.transportTask.taskFraction floatValue]];
            self.transferStatusLabel.text = getLocalizedString(@"CloudTransferStatusPaused", nil);
            self.transferStatusLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_start_nor"] forState:UIControlStateNormal];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_start_press"] forState:UIControlStateHighlighted];
            break;
        case TaskSucceed:
            self.transferFunctionButton.hidden = YES;
            self.transferProgressView.hidden = YES;
            //self.transferStatusLabel.text = getLocalizedString(@"CloudTransferStatusSucceed", nil);
            self.transferStatusLabel.text = nil;
            self.transferStatusLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
            break;
        case TaskCancel:
            self.transferFunctionButton.hidden = NO;
            self.transferProgressView.hidden = NO;
            self.transferStatusLabel.text = getLocalizedString(@"CloudTransferStatusCancel", nil);
            self.transferStatusLabel.textColor =  [CommonFunction colorWithString:@"333333" alpha:1.0f];;
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_start_nor"] forState:UIControlStateNormal];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_start_press"] forState:UIControlStateHighlighted];
            break;
        case TaskFailed:
            self.transferFunctionButton.hidden = NO;
            self.transferProgressView.hidden = NO;
            [self.transferProgressView setProgress:[self.transportTask.taskFraction floatValue]];
            self.transferStatusLabel.text = getLocalizedString(@"CloudTransferStatusFailed", nil);
            self.transferStatusLabel.textColor = [CommonFunction colorWithString:@"fc5043" alpha:1.0f];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_retry_nor"] forState:UIControlStateNormal];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_retry_press"] forState:UIControlStateHighlighted];
            break;
        case TaskWaitNetwork:
            self.transferFunctionButton.hidden = NO;
            self.transferProgressView.hidden = NO;
            [self.transferProgressView setProgress:[self.transportTask.taskFraction floatValue]];
            self.transferStatusLabel.text = getLocalizedString(@"CloudTransferStatusWaitNetwork", nil);
            self.transferStatusLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_start_nor"] forState:UIControlStateNormal];
            [self.transferFunctionButton setImage:[UIImage imageNamed:@"ic_transfer_start_press"] forState:UIControlStateHighlighted];
            break;
            
        default:
            break;
    }
}

- (void)deleteTask {
    [[[UIActionSheet alloc]initWithTitle:getLocalizedString(@"CloudTransferDeletePrompt",nil)
                                delegate:self
                       cancelButtonTitle:getLocalizedString(@"Cancel", nil)
                  destructiveButtonTitle:getLocalizedString(@"Confirm", nil)
                       otherButtonTitles:nil, nil] showInView:self];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {
        [self.transportTask.taskHandle cancel];
        if (self.transferViewController.parentTransferCell) {
            [self.transferViewController.parentTransferCell refreshSize];
        }
        if (self.transportTask.taskRecoverable.boolValue) {
            AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
            NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
            [ctx performBlockAndWait:^{
                TransportTask *shadow = (TransportTask*)[ctx objectWithID:self.transportTask.objectID];
                [shadow remove];
                [ctx save:nil];
            }];
        }
    }
}
- (void)taskControl {
    if (self.transportTask.taskStatus.integerValue == TaskFailed) {
        [self.transportTask.taskHandle waiting];
    } else if (self.transportTask.taskStatus.integerValue == TaskSuspend) {
        [self.transportTask.taskHandle waiting];
    } else if (self.transportTask.taskStatus.integerValue == TaskWaitNetwork){
        [self.transportTask.taskHandle waiting];
    } else {
        [self.transportTask.taskHandle suspend];
    }
}

@end
