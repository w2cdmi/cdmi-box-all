//
//  MailAttachmentPreviewControllerViewController.h
//  OneMail
//
//  Created by Jason on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Attachment.h"
@interface mailAttachmentPreviewController : UIViewController
@property (nonatomic,strong) Attachment *attachment;
- (id)initWithAttachment:(Attachment *)attachment;
@end
