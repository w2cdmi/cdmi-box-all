//
//  CloudTitleView.h
//  OneMail
//
//  Created by cse  on 15/12/31.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum : NSUInteger {
    CloudTitleMySpaceStyle,
    CloudTitleShareWithMeStyle,
    CloudTitleTeamSapceStyle,
} CloudTitleViewStyle;

@interface CloudTitleView : UIView

@property (nonatomic, assign) CloudTitleViewStyle titleStyle;

- (id)initWithStyle:(CloudTitleViewStyle)titleViewStyle frame:(CGRect)frame;
- (void)setViewFrame:(CGRect)frame;

@end
