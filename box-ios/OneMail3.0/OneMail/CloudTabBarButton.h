//
//  CloudTabBarButton.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CloudTabBarButton : UIButton

@property (nonatomic,assign) CGSize limitSize;
@property (nonatomic,strong) NSString *titleString;

- (void)refreshButtonFrame;

@end
