//
//  CloudCrumbView.m
//  OneMail
//
//  Created by cse  on 15/12/17.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudCrumbView.h"
#import "File.h"
#import "AppDelegate.h"

@interface CloudCrumbView()
@property (nonatomic,strong) UIScrollView *scrollView;
@end

@implementation CloudCrumbView

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.LocalizedChange" object:nil];
}

- (id)initWithFiles:(NSArray *)fileIds fileOwners:(NSArray *)fileOwners {
    self = [super init];
    if (self) {
        self.frame = CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 29);
        self.layer.borderColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f].CGColor;
        self.layer.borderWidth = 0.5;
        if (fileIds.count == 1) {
            self.mainCrumbButton = [self crumbButtonWithFileId:[fileIds objectAtIndex:0] fileOwner:[fileOwners objectAtIndex:0] lastButton:YES];
        } else {
            self.mainCrumbButton = [self crumbButtonWithFileId:[fileIds objectAtIndex:0] fileOwner:[fileOwners objectAtIndex:0] lastButton:NO];
        }
        self.mainCrumbButton.tag = 0;
        self.mainCrumbButton.frame = CGRectMake(0, 0, CGRectGetWidth(self.mainCrumbButton.frame), CGRectGetHeight(self.mainCrumbButton.frame));
        [self.mainCrumbButton addTarget:self action:@selector(buttonSelect:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:self.mainCrumbButton];
        
        CGFloat orginalX = 0.0f;
        UIScrollView *crumbScrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.mainCrumbButton.frame), 0, CGRectGetWidth(self.frame)-CGRectGetWidth(self.mainCrumbButton.frame), CGRectGetHeight(self.frame))];
        crumbScrollView.scrollEnabled = YES;
        crumbScrollView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        for (NSString *fileId in fileIds) {
            if ([fileId isEqualToString:[fileIds objectAtIndex:0]]) {
                continue;
            }
            BOOL isLastButton = NO;
            if ([fileIds indexOfObject:fileId] == fileIds.count-1) {
                isLastButton = YES;
            }
            NSUInteger index = [fileIds indexOfObject:fileId];
            NSString *fileOwner = [fileOwners objectAtIndex:index];
            UIButton *crumbButton = [self crumbButtonWithFileId:fileId fileOwner:fileOwner lastButton:isLastButton];
            crumbButton.tag = (NSInteger)[fileIds indexOfObject:fileId];
            crumbButton.frame = CGRectMake(orginalX, 0, CGRectGetWidth(crumbButton.frame), CGRectGetHeight(crumbButton.frame));
            [crumbButton addTarget:self action:@selector(buttonSelect:) forControlEvents:UIControlEventTouchUpInside];
            [crumbScrollView addSubview:crumbButton];
            orginalX = orginalX + CGRectGetWidth(crumbButton.frame);
        }
        crumbScrollView.contentSize = CGSizeMake(orginalX, CGRectGetHeight(self.frame));
        if (orginalX > crumbScrollView.frame.size.width) {
            crumbScrollView.contentOffset = CGPointMake(orginalX-crumbScrollView.frame.size.width, 0);
        }
        crumbScrollView.bounces = NO;
        crumbScrollView.showsHorizontalScrollIndicator = NO;
        [self addSubview:crumbScrollView];
//        self.scrollView.contentSize = CGSizeMake(orginalX, CGRectGetHeight(self.frame));
//        if (orginalX > self.scrollView.frame.size.width) {
//            self.scrollView.contentOffset = CGPointMake(orginalX-self.scrollView.frame.size.width, 0);
//        }
//        self.scrollView.bounces = NO;
//        self.scrollView.showsHorizontalScrollIndicator = NO;
//        [self addSubview:self.scrollView];
    }
    [self refreshCrumbTitle];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshCrumbTitle) name:@"com.huawei.onemail.LocalizedChange" object:nil];
    return self;
}

- (void)refreshCrumbTitle{
    UILabel *label;
    UIImageView *imageView;
    for (UIView *view in self.mainCrumbButton.subviews) {
        if ([[view class] isSubclassOfClass:[UILabel class]]) {
            label = (UILabel *)view;
        }
        else{
            imageView = (UIImageView *)view;
        }
    }
    NSString *text = label.text;
    if ([text isEqualToString:@"My Space"] || [text isEqualToString:@"个人空间"]) {
        label.text = getLocalizedString(@"CloudFileTitle", nil);
    }
    if ([text isEqualToString:@"Search"]|| [text isEqualToString:@"搜索"]) {
        label.text = getLocalizedString(@"CloudFileSearch", nil);
    }
    if ([text isEqualToString:@"Share With Me"]|| [text isEqualToString:@"收到的共享"]) {
        label.text = getLocalizedString(@"CloudShareTitle", nil);
    }
    label.font = [UIFont systemFontOfSize:8.0f];
    label.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
    label.textAlignment = NSTextAlignmentCenter;
    CGSize adjustNameSize = [CommonFunction labelSizeWithLabel:label limitSize:CGSizeMake(1000, 1000)];
    label.frame = CGRectMake(15, (CGRectGetHeight(self.frame)-adjustNameSize.height)/2, MIN(130, adjustNameSize.width), adjustNameSize.height);
    
    self.mainCrumbButton.frame = CGRectMake(self.mainCrumbButton.frame.origin.x, self.mainCrumbButton.frame.origin.y, CGRectGetWidth(label.frame)+30, CGRectGetHeight(self.frame));
    imageView.frame = CGRectMake(CGRectGetWidth(self.mainCrumbButton.frame)-8, 0, 8, CGRectGetHeight(self.frame));
    self.scrollView.frame = CGRectMake(CGRectGetWidth(self.mainCrumbButton.frame), 0, CGRectGetWidth(self.frame)-CGRectGetWidth(self.mainCrumbButton.frame), CGRectGetHeight(self.frame));
}

- (UIButton*)crumbButtonWithFileId:(NSString*)fileId fileOwner:(NSString*)fileOwner lastButton:(BOOL)isLastButton {
    UIButton *crumbButton = [[UIButton alloc] initWithFrame:CGRectZero];
    crumbButton.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    UILabel *fileNameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    if ([fileId isEqualToString:@"search"]) {
        fileNameLabel.text = [NSString stringWithFormat:@"Search(%@)",fileOwner];
    } else {
        File *file = [File getFileWithFileId:fileId fileOwner:fileOwner];
        fileNameLabel.text = file.fileName;
    }

    fileNameLabel.font = [UIFont systemFontOfSize:12.0f];
    fileNameLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
    fileNameLabel.textAlignment = NSTextAlignmentCenter;
    CGSize adjustNameSize = [CommonFunction labelSizeWithLabel:fileNameLabel limitSize:CGSizeMake(1000, 1000)];
    fileNameLabel.frame = CGRectMake(15, (CGRectGetHeight(self.frame)-adjustNameSize.height)/2, MIN(130, adjustNameSize.width), adjustNameSize.height);
    [crumbButton addSubview:fileNameLabel];
    
    crumbButton.bounds = CGRectMake(0, 0, CGRectGetWidth(fileNameLabel.frame)+30, CGRectGetHeight(self.frame));
    
    UIImageView *crumbImageView = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetWidth(crumbButton.frame)-8, 0, 8, CGRectGetHeight(self.frame))];
    if (isLastButton) {
        crumbImageView.image = [UIImage imageNamed:@"crumbs_white_nor"];
    } else {
        crumbImageView.image = [UIImage imageNamed:@"crumbs_white_nor"];
    }
    [crumbButton addSubview:crumbImageView];
    return crumbButton;
}

- (void)buttonSelect:(UIButton*)button {
    UIViewController *viewController = [self.viewControllers objectAtIndex:button.tag];
    [self.navigationController popToViewController:viewController animated:NO];
}

@end
