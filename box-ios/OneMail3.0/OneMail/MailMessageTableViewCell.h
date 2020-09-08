//
//  MailMessageTableViewCell.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Attachment;
@class Session;
@class Message;

@protocol MailMessageCellDelegate<NSObject>

- (void)previewAttachment:(Attachment*)attachment;
- (void)respondHistoryMessage:(Message *)historyMessage;

@end

@interface MailMessageAttachmentCell : UITableViewCell

@property (nonatomic, strong) Attachment *attachment;

@end

@interface MailMessageTableViewCell : UITableViewCell

@property (nonatomic, strong) Message *message;
@property (nonatomic, strong) Message *lastMessage;
@property (nonatomic, strong) Session *session;
@property (nonatomic, strong) UIViewController *mainViewController;
@property (nonatomic, assign) id<MailMessageCellDelegate> delegate;
@property (nonatomic, assign) BOOL isSelected;

- (void)refreshUI;
- (CGFloat)cellHeight;

@end
