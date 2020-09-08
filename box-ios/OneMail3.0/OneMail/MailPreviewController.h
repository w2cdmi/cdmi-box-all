//
//  MailPreviewController.h
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Message;
typedef void (^replyBlock)();
@interface MailPreviewContentView : UIWebView <UIWebViewDelegate>

@property (nonatomic,strong) Message *message;

@end

@interface MailPreviewController : UIViewController
@property (nonatomic,strong) replyBlock block;
- (id)initWithMessage:(Message*)message;

@end
