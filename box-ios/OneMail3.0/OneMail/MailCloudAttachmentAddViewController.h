//
//  MailCloudAttachmentAddViewController.h
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void (^MailCloudAttachmentAddedCompletion)(NSArray *attachmentArray, BOOL comfirm);

@class File;

@interface MailCloudAttachmentAddViewController : UIViewController

@property (nonatomic, strong) UIViewController *rootViewController;
@property (nonatomic, copy) MailCloudAttachmentAddedCompletion completion;

- (id)initWithFile:(File*)file;

@end
