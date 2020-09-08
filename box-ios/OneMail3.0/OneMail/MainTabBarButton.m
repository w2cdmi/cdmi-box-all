//
//  MainTabBarButton.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MainTabBarButton.h"

@implementation MainTabBarButton
- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        
    }
    return self;
}

- (void)layoutSubviews{
    [super layoutSubviews];
    self.imageView.frame = CGRectMake(0, 0, 29, 29);
    CGRect imageFrame;
    imageFrame.origin.x = self.frame.size.width/2 - self.imageView.frame.size.width/2;
    imageFrame.origin.y = 4;
    imageFrame.size.width = self.imageView.frame.size.width;
    imageFrame.size.height = self.imageView.frame.size.height;
    self.imageView.frame = imageFrame;
    
    CGRect newFrame;
    newFrame.origin.x = 0;
    newFrame.origin.y = self.imageView.frame.size.height+4;
    newFrame.size.width = self.frame.size.width;
    newFrame.size.height = self.frame.size.height - self.imageView.frame.size.height - 4*2;
    self.titleLabel.frame = newFrame;
//    self.titleLabel.font = [UIFont fontWithName:@"Heiti SC" size:12.0f];
    self.titleLabel.font = [UIFont systemFontOfSize:12.0f];
    [self setTitleColor:[UIColor colorWithRed:146/255.0f green:146/255.0f blue:146/255.0f alpha:1.0f] forState:UIControlStateNormal];
    [self setTitleColor:[UIColor colorWithRed:51/255.0f green:130/255.0f blue:197/255.0f alpha:1.0f] forState:UIControlStateSelected];
    self.titleLabel.textAlignment = NSTextAlignmentCenter;
}
@end
