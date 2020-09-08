//
//  MailMessageInputView.m
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailMessageInputView.h"
#import "MailMessageViewController.h"
#import "AppDelegate.h"

@implementation MailMessageInputButton

- (id)init {
    self = [super init];
    if (self) {
        self.buttonImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self addSubview:self.buttonImageView];
        
        self.buttonTitleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.buttonTitleLabel.font = [UIFont systemFontOfSize:12.0f];
        self.buttonTitleLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.buttonTitleLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:self.buttonTitleLabel];
    }
    return self;
}

- (void)setButtonFrame:(CGRect)frame {
    self.frame = frame;
    self.buttonImageView.frame = CGRectMake((CGRectGetWidth(frame)-59)/2, 0, 59, 59);
    self.buttonTitleLabel.frame = CGRectMake(0, CGRectGetMaxY(self.buttonImageView.frame)+4, CGRectGetWidth(frame), 15);
}


@end



@implementation MailMessageInputView

- (id)initWithMailChat:(MailMessageViewController*)mailMessageViewController {
    self = [super init];
    if (self) {
        self.mailMessageViewController = mailMessageViewController;
        
        self.mailMessageInputBackground = [[UIView alloc] initWithFrame:CGRectZero];
        self.mailMessageInputBackground.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
        self.mailMessageInputBackground.layer.borderWidth = 0.5;
        self.mailMessageInputBackground.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;

        self.mailMessageInputView = [[UIView alloc] initWithFrame:CGRectZero];
        self.mailMessageInputView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        self.mailMessageInputView.layer.borderWidth = 0.5;
        self.mailMessageInputView.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
        self.mailMessageInputView.layer.cornerRadius = 4;
        self.mailMessageInputView.layer.masksToBounds = YES;
        [self.mailMessageInputBackground addSubview:self.mailMessageInputView];

        self.mailMessageInputTextField = [[UITextField alloc] initWithFrame:CGRectZero];
        self.mailMessageInputTextField.font = [UIFont systemFontOfSize:17.0f];
        self.mailMessageInputTextField.returnKeyType = UIReturnKeyDone;
        self.mailMessageInputTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.mailMessageInputTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.mailMessageInputTextField.text = nil;
        [self.mailMessageInputView addSubview:self.mailMessageInputTextField];

        self.mailAttachmentAddButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.mailAttachmentAddButton setImage:[UIImage imageNamed:@"ic_add_nor"] forState:UIControlStateNormal];
        [self.mailAttachmentAddButton setImage:[UIImage imageNamed:@"ic_add_press"] forState:UIControlStateHighlighted];
        //[self.mailMessageInputView addSubview:self.mailAttachmentAddButton];
        [self.mailMessageInputBackground addSubview:self.mailAttachmentAddButton];
        
        self.mailAttachmentAddedButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 36, 36)];
        self.mailAttachmentAddedButton.hidden = YES;
        self.mailAttachmentAddedLabel = [CommonFunction labelWithFrame:CGRectMake(7, 7, 22, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        self.mailAttachmentAddedLabel.backgroundColor = [CommonFunction colorWithString:@"fc5043" alpha:1.0f];
        self.mailAttachmentAddedLabel.layer.cornerRadius = 11;
        self.mailAttachmentAddedLabel.layer.masksToBounds = YES;
        [self.mailAttachmentAddedButton addSubview:self.mailAttachmentAddedLabel];
        [self.mailMessageInputView addSubview:self.mailAttachmentAddedButton];

        self.mailMessageSendButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.mailMessageInputBackground addSubview:self.mailMessageSendButton];

        self.mailAttachmentAddView = [[UIView alloc] initWithFrame:CGRectZero];
        self.mailAttachmentAddView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];

        self.mailAttachmentPhotoAddButton = [[MailMessageInputButton alloc] init];
        self.mailAttachmentPhotoAddButton.buttonImageView.image = [UIImage imageNamed:@"ic_add_photo_nor"];
        self.mailAttachmentPhotoAddButton.buttonTitleLabel.text = NSLocalizedString(@"MailChatPhoto", nil);
        [self.mailAttachmentAddView addSubview:self.mailAttachmentPhotoAddButton];

        self.mailAttachmentVideoAddButton = [[MailMessageInputButton alloc] init];
        self.mailAttachmentVideoAddButton.buttonImageView.image = [UIImage imageNamed:@"ic_add_video_nor"];
        self.mailAttachmentVideoAddButton.buttonTitleLabel.text = NSLocalizedString(@"MailChatVideo", nil);
        [self.mailAttachmentAddView addSubview:self.mailAttachmentVideoAddButton];

        self.mailAttachmentAudioAddButton = [[MailMessageInputButton alloc] init];
        self.mailAttachmentAudioAddButton.buttonImageView.image = [UIImage imageNamed:@"ic_add_audio_nor"];
        self.mailAttachmentAudioAddButton.buttonTitleLabel.text = NSLocalizedString(@"MailChatAudio", nil);
        [self.mailAttachmentAddView addSubview:self.mailAttachmentAudioAddButton];

        self.mailAttachmentLocalFileAddButton = [[MailMessageInputButton alloc] init];
        self.mailAttachmentLocalFileAddButton.buttonImageView.image = [UIImage imageNamed:@"ic_add_local_file_nor"];
        self.mailAttachmentLocalFileAddButton.buttonTitleLabel.text = NSLocalizedString(@"MailChatLocal", nil);
        [self.mailAttachmentAddView addSubview:self.mailAttachmentLocalFileAddButton];

        self.mailAttachmentCloudFileAddButton = [[MailMessageInputButton alloc] init];
        self.mailAttachmentCloudFileAddButton.buttonImageView.image = [UIImage imageNamed:@"ic_add_cloud_file_nor"];
        self.mailAttachmentCloudFileAddButton.buttonTitleLabel.text = NSLocalizedString(@"MailChatCloud", nil);
        [self.mailAttachmentAddView addSubview:self.mailAttachmentCloudFileAddButton];

//        self.historyView = [[UIView alloc] initWithFrame:CGRectZero];
//        self.historyView.hidden = YES;
//
//        self.historyLabel = [[UILabel alloc] initWithFrame:CGRectZero];
//        self.historyLabel.textAlignment = NSTextAlignmentLeft;
//        self.historyLabel.font = [UIFont systemFontOfSize:14];
//        self.historyLabel.textColor = [UIColor blackColor];
//        [self.historyView addSubview:self.historyLabel];
//
//        self.historyButton = [[UIButton alloc] initWithFrame:CGRectZero];
//        [self.historyButton setTitle:NSLocalizedString(@"Cancel", nil) forState:UIControlStateNormal];
//        [self.historyButton setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
//        self.historyButton.titleLabel.font = [UIFont systemFontOfSize:14];
//        [self.historyView addSubview:self.historyButton];
//
//        [self.historyView addSubview:self.historyButton];


        self.mailAttachmentShowButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.mailAttachmentShowButton setTitleColor:[UIColor colorWithRed:0 green:136/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
        self.mailAttachmentShowButton.titleLabel.font = [UIFont boldSystemFontOfSize:15];
        [self.mailMessageInputView addSubview:self.mailAttachmentShowButton];
    }

    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.mailMessageInputBackground.frame = CGRectMake(0, 0, CGRectGetWidth(self.frame), 49);
    if (!self.mailMessageInputBackground.superview) {
        [self addSubview:self.mailMessageInputBackground];
    }
    self.mailMessageSendButton.frame = CGRectMake(CGRectGetWidth(self.mailMessageInputBackground.frame)-44, 0, 44, 49);
    self.mailMessageSendButton.imageView.frame = CGRectMake(7.5, 10, 29, 29);
    
    self.mailAttachmentAddButton.frame = CGRectMake(CGRectGetWidth(self.mailMessageInputBackground.frame)-44-44, 0, 49, 49);
    self.mailAttachmentAddButton.imageView.frame = CGRectMake(7.5, 10, 29, 29);
    self.mailMessageInputView.frame = CGRectMake(15, (CGRectGetHeight(self.mailMessageInputBackground.frame)-36)/2, CGRectGetWidth(self.mailMessageInputBackground.frame)-15-44-44, 36);
    //self.mailAttachmentAddButton.frame = CGRectMake(CGRectGetWidth(self.mailMessageInputView.frame)-38, 0, 38, 36);
    //self.mailAttachmentAddButton.imageView.frame = CGRectMake(8, 7, 22, 22);
    //self.mailAttachmentAddedButton.frame = CGRectMake(CGRectGetWidth(self.mailMessageInputView.frame)-38-36, 0, 36, 36);
    self.mailAttachmentAddedButton.frame = CGRectMake(CGRectGetWidth(self.mailMessageInputView.frame)-36, 0, 36, 36);
    self.mailMessageInputTextField.frame = CGRectMake(10, (CGRectGetHeight(self.mailMessageInputView.frame)-22)/2, CGRectGetWidth(self.mailMessageInputView.frame)-10-8-22-8, 22);

    self.mailAttachmentAddView.frame = CGRectMake(0, CGRectGetMaxY(self.mailMessageInputBackground.frame), CGRectGetWidth(self.frame), 215);
    if (!self.mailAttachmentAddView.superview) {
        [self addSubview:self.mailAttachmentAddView];
    }
    CGFloat buttonImageGap = (CGRectGetWidth(self.frame)-59*4)/5;
    CGFloat buttonWidth = 59+buttonImageGap;
    CGFloat buttonHeight = 59+4+15;
    [self.mailAttachmentPhotoAddButton setButtonFrame:CGRectMake(buttonImageGap/2, 20, buttonWidth, buttonHeight)];
    [self.mailAttachmentVideoAddButton setButtonFrame:CGRectMake(CGRectGetMaxX(self.mailAttachmentPhotoAddButton.frame), 20, buttonWidth, buttonHeight)];
    [self.mailAttachmentAudioAddButton setButtonFrame:CGRectMake(CGRectGetMaxX(self.mailAttachmentVideoAddButton.frame), 20, buttonWidth, buttonHeight)];
    [self.mailAttachmentLocalFileAddButton setButtonFrame:CGRectMake(CGRectGetMaxX(self.mailAttachmentAudioAddButton.frame), 20, buttonWidth, buttonHeight)];
    [self.mailAttachmentCloudFileAddButton setButtonFrame:CGRectMake(buttonImageGap/2, CGRectGetMaxY(self.mailAttachmentPhotoAddButton.frame)+12, buttonWidth, buttonHeight)];

//    self.historyView.frame = CGRectMake(0, -HistoryButtonHeight, CGRectGetWidth(self.frame), HistoryButtonHeight);
//    if (!self.historyView.superview) {
//        [self addSubview:self.historyView];
//    }
//    self.historyLabel.frame = CGRectMake(HistoryLabelDistanceLeft, HistoryLabelDistanceTop, self.frame.size.width - HistoryLabelDistanceRight - HistoryLabelDistanceLeft - HistoryButtonWidth - HistoryButtonDisTanceRight, HistoryButtonHeight);
//    self.historyButton.frame = CGRectMake(self.frame.size.width - HistoryButtonDisTanceRight - HistoryButtonWidth, HistoryButtonDistanceTop, HistoryButtonWidth, HistoryButtonHeight);

    self.mailAttachmentShowButton.frame = CGRectMake(CGRectGetMaxX(self.mailMessageInputTextField.frame) + 8, (36 - 22) /2, 22, 22);
}

- (void)setMailMessageSendButtonEnable {
    [self.mailMessageSendButton setImage:[UIImage imageNamed:@"ic_send_nor"] forState:UIControlStateNormal];
    self.mailMessageSendButton.enabled = YES;
}

- (void)setMailMessageSendButtonDisable {
    [self.mailMessageSendButton setImage:[UIImage imageNamed:@"ic_send_press"] forState:UIControlStateNormal];
    self.mailMessageSendButton.enabled = NO;
}
@end


