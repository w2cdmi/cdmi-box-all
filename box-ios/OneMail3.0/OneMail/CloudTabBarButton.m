//
//  CloudTabBarButton.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudTabBarButton.h"
#import "CloudTabBarView.h"

@interface CloudTabBarButton ()

@property (nonatomic, strong) UILabel *title;
@property (nonatomic, strong) UIImageView *image;

@end

static CloudTabBarButton *cloudTabBarButton = nil;

@implementation CloudTabBarButton

//+ (CloudTabBarButton *)getButton {
//    @synchronized(self) {
//        if (cloudTabBarButton == nil) {
//            cloudTabBarButton = [[CloudTabBarButton alloc]init];
//        }
//    }
//    return cloudTabBarButton;
//}

- (id)init {
    self = [super init];
    if (self) {
        
        self.image = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.image.image = [UIImage imageNamed:@"ic_nav_dropdown_nor"];
        self.title = [[UILabel alloc] initWithFrame:CGRectZero];
        self.title.font = [UIFont systemFontOfSize:18.0f];
        self.title.textColor = [UIColor whiteColor];
        
        self.limitSize = CGSizeMake(1000, 24);
        
        [self addTarget:self action:@selector(showTabBarTable) forControlEvents:UIControlEventTouchUpInside];
    }
    return self;
}

- (void)showTabBarTable {
    CloudTabBarView *cloudTabBarView = [CloudTabBarView getTabBarView];
    if (cloudTabBarView.superview) {
        [cloudTabBarView removeFromSuperview];
    } else {
        [[UIApplication sharedApplication].keyWindow.rootViewController.view addSubview:cloudTabBarView];
    }
}

- (void)refreshButtonFrame {
    if (!self.image.superview) {
        [self addSubview:self.image];
    }
    self.image.frame = CGRectMake(0, 0, 22, 22);
    
    if (!self.title.superview) {
        [self addSubview:self.title];
    }
    CGSize titleLimitSize = CGSizeMake(self.limitSize.width-(5+CGRectGetWidth(self.image.frame))*2, self.limitSize.height);
    CGSize adjustSize = [self adjustSize:self.title limitSize:titleLimitSize];
    self.title.frame = CGRectMake(0, 0, ceil(adjustSize.width), ceil(self.limitSize.height));
    
    CGRect newFrame = CGRectZero;
    newFrame.size.width = self.title.frame.size.width + (5 + self.image.frame.size.width)*2;
    newFrame.size.height = self.title.frame.size.height;
    self.frame = newFrame;
    
    self.title.frame = CGRectMake(self.image.frame.size.width+5, 0, self.title.frame.size.width, self.title.frame.size.height);
    self.image.frame = CGRectMake(CGRectGetMaxX(self.title.frame)+5, (CGRectGetHeight(self.frame)-CGRectGetHeight(self.image.frame))/2, self.image.frame.size.width, self.image.frame.size.height);
}

- (void)setTitleString:(NSString *)titleString {
    if (![_titleString isEqualToString:titleString]) {
        _titleString = titleString;
        self.title.text = titleString;
    }
}

- (CGSize)adjustSize:(UILabel*)label limitSize:(CGSize)size {
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    NSDictionary *attributes = @{NSFontAttributeName:label.font,NSParagraphStyleAttributeName:paragraphStyle.copy};
    return [label.text boundingRectWithSize:size options:NSStringDrawingUsesLineFragmentOrigin attributes:attributes context:nil].size;
}

@end
