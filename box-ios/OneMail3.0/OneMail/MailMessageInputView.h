//
//  MailMessageInputView.h
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MailMessageViewController;

@interface MailMessageInputButton : UIButton

@property (nonatomic, strong) UIImageView *buttonImageView;
@property (nonatomic, strong) UILabel     *buttonTitleLabel;

- (void)setButtonFrame:(CGRect)frame;

@end

@interface MailMessageInputView : UIView

@property (nonatomic, strong) UIView                 *mailMessageInputBackground;
@property (nonatomic, strong) UIView                 *mailMessageInputView;
@property (nonatomic, strong) UITextField            *mailMessageInputTextField;
@property (nonatomic, strong) UIButton               *mailMessageSendButton;
@property (nonatomic, strong) UIButton               *mailAttachmentAddButton;
@property (nonatomic, strong) UIButton               *mailAttachmentAddedButton;
@property (nonatomic, strong) UILabel                *mailAttachmentAddedLabel;

@property (nonatomic, strong) UIView                 *mailAttachmentAddView;
@property (nonatomic, strong) MailMessageInputButton *mailAttachmentPhotoAddButton;
@property (nonatomic, strong) MailMessageInputButton *mailAttachmentVideoAddButton;
@property (nonatomic, strong) MailMessageInputButton *mailAttachmentAudioAddButton;
@property (nonatomic, strong) MailMessageInputButton *mailAttachmentLocalFileAddButton;
@property (nonatomic, strong) MailMessageInputButton *mailAttachmentCloudFileAddButton;
@property (nonatomic, strong) UIButton               *mailAttachmentShowButton;

@property (nonatomic, strong) MailMessageViewController *mailMessageViewController;


@property (nonatomic, strong) UIView *historyView;
@property (nonatomic, strong) UILabel *historyLabel;
@property (nonatomic, strong) UIButton *historyButton;

- (id)initWithMailChat:(MailMessageViewController*)mailMessageViewController;
- (void)setMailMessageSendButtonEnable;
- (void)setMailMessageSendButtonDisable;

@end

