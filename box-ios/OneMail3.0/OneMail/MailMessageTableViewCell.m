//
//  MailMessageTableViewCell.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#define MaxContentViewWidth ([UIScreen mainScreen].bounds.size.width-10-44-8-44-10)

#import "MailMessageTableViewCell.h"
#import "AppDelegate.h"
#import "Attachment.h"
#import "Session.h"
#import "Message.h"
#import "File.h"
#import "User.h"
#import "UserThumbnail.h"
#import "MailMessageUserViewController.h"

#import "MailForwardViewController.h"
#import "MailPreviewController.h"
#import "UIAlertView+Blocks.h"
#import "UIBezierPath+BasicShapes.h"
@interface MailMessageAttachmentCell ()

@property (nonatomic, strong) UIImageView *attachImageView;
@property (nonatomic, strong) UILabel *attachTitleLable;
@property (nonatomic, strong) UILabel *attachSizeLable;

@end

@implementation MailMessageAttachmentCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.backgroundColor = [UIColor clearColor];
        self.contentView.backgroundColor = [UIColor clearColor];
        
        self.attachImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self.contentView addSubview:self.attachImageView];
        
        self.attachTitleLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.attachTitleLable.font = [UIFont systemFontOfSize:15.0f];
        self.attachTitleLable.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.attachTitleLable];
        
        self.attachSizeLable = [[UILabel alloc] initWithFrame:CGRectZero];
        self.attachSizeLable.font = [UIFont systemFontOfSize:12.0f];
        self.attachSizeLable.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.attachSizeLable];
    }
    return self;
    
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.attachImageView.frame = CGRectMake(0, 4, 48, 48);
    
    NSString *attachmentThumbnailLocalPath = [self.attachment attachmentThumbnailLocalPath];
    if (attachmentThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentThumbnailLocalPath]) {
        self.attachImageView.image = [UIImage imageWithContentsOfFile:attachmentThumbnailLocalPath];
    }
    
    if (self.attachment.attachmentFileId && self.attachment.attachmentFileOwner) {
        File *file = [File getFileWithFileId:self.attachment.attachmentFileId fileOwner:self.attachment.attachmentFileOwner];
        NSString *fileThumbnailPath = [file fileThumbnailLocalPath];
        if ([[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailPath]) {
            self.attachImageView.image = [UIImage imageWithContentsOfFile:fileThumbnailPath];
            if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentThumbnailLocalPath]) {
                [[NSFileManager defaultManager] moveItemAtPath:fileThumbnailPath toPath:attachmentThumbnailLocalPath error:nil];
            }
        }
    }
    
    if (!self.attachImageView.image) {
        self.attachImageView.image = [CommonFunction thumbnailWithFileName:_attachment.attachmentName];
    }
    
    UIView *tableView = [self superview];
    CGFloat maxLableWidth = CGRectGetWidth(tableView.frame)-48-8;
    
    self.attachTitleLable.frame = CGRectMake(CGRectGetMaxX(self.attachImageView.frame)+8, CGRectGetMinY(self.attachImageView.frame)+5, maxLableWidth, 20);
    self.attachTitleLable.text = _attachment.attachmentName;
    
    self.attachSizeLable.frame = CGRectMake(CGRectGetMaxX(self.attachImageView.frame)+8, CGRectGetMaxY(self.attachTitleLable.frame)+4, maxLableWidth, 15);
    self.attachSizeLable.text = [CommonFunction pretySize:[_attachment.attachmentSize longLongValue]];
    
    Message *message = [Message getMessageWithMessageId:self.attachment.attachmentMessageId ctx:nil];
    if (message.messageType.integerValue == MessageSent) {
        self.attachTitleLable.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        self.attachSizeLable.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    } else {
        self.attachTitleLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.attachSizeLable.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    }
}

@end



@interface MailMessageTableViewCell() <UIGestureRecognizerDelegate,UITableViewDataSource,UITableViewDelegate,UIActionSheetDelegate>
@property (nonatomic, strong) UIView *webViewBackGroundView;

@property (nonatomic, strong) UIView *messageTimeView;
@property (nonatomic, strong) UILabel *messageTimeLable;
@property (nonatomic, assign) BOOL messageTimeShow;

@property (nonatomic, strong) UIImageView *messageUserIconBackground;
@property (nonatomic, strong) UIImageView *messageUserIcon;

@property (nonatomic, strong) UIView *messageContentView;

@property (nonatomic, strong) UILabel *messageContentTitleLabel;
@property (nonatomic, assign) BOOL messageTitleShow;

@property (nonatomic, strong) UILabel *messageContentMessageLable;
@property (nonatomic, assign) BOOL messageMessageShow;

@property (nonatomic, strong) UIImageView *messageContentImageView;
@property (nonatomic, assign) BOOL messageImageShow;

@property (nonatomic, strong) UITableView *messageContentAttachmentTableView;
@property (nonatomic, strong) NSArray *messageContentAttachmentArray;
@property (nonatomic, assign) BOOL messageAttachmentShow;

@property (nonatomic, strong) UIView *messageEmptyPromptView;
@property (nonatomic, strong) UILabel *messageEmptyPromptLabel;
@property (nonatomic, assign) BOOL messageEmptyPromptShow;

@property (nonatomic, strong) UIImageView *isDownloadFailView;
@property (nonatomic, strong) UIButton *CellCheckBox;

@property (nonatomic, strong) CAShapeLayer *maskBorderLayer;
@end

static UIView *originView;

@implementation MailMessageTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        
        self.messageTimeView = [[UIView alloc] initWithFrame:CGRectZero];
        self.messageTimeView.layer.cornerRadius = 4;
        self.messageTimeView.backgroundColor = [CommonFunction colorWithString:@"cccccc" alpha:1.0f];
        self.messageTimeLable = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        [self.messageTimeView addSubview:self.messageTimeLable];
        [self.contentView addSubview:self.messageTimeView];
        
        self.messageUserIconBackground = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.messageUserIconBackground.bounds = CGRectMake(0, 0, 44, 44);
        self.messageUserIconBackground.image = [UIImage imageNamed:@"img_portrait_frame"];
        self.messageUserIcon = [[UIImageView alloc]initWithFrame:CGRectZero];
        self.messageUserIcon.frame = CGRectMake(2, 2, 40, 40);
        self.messageUserIcon.layer.cornerRadius = 40/2;
        self.messageUserIcon.layer.masksToBounds = YES;
        [self.messageUserIconBackground addSubview:self.messageUserIcon];
        [self.contentView addSubview:self.messageUserIconBackground];
        
        self.messageContentView = [[UIView alloc] initWithFrame:CGRectZero];
        self.messageContentView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        self.messageContentView.layer.cornerRadius = 4;
//        self.messageContentView.layer.masksToBounds = YES;
        self.messageContentView.layer.borderWidth = 0.5f;
        self.messageContentView.layer.borderColor = [CommonFunction colorWithString:@"cccccc" alpha:1.0f].CGColor;
        [self.contentView addSubview:self.messageContentView];
        self.maskBorderLayer = [CAShapeLayer layer];
        [self.messageContentView.layer addSublayer:self.maskBorderLayer];
        
        self.messageContentTitleLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:17.0f] textColor:nil textAlignment:NSTextAlignmentLeft];
        self.messageContentTitleLabel.numberOfLines = 2;
        [self.messageContentView addSubview:self.messageContentTitleLabel];
        
        self.messageContentMessageLable = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:nil textAlignment:NSTextAlignmentJustified];
        self.messageContentMessageLable.numberOfLines = 0;
        [self.messageContentView addSubview:self.messageContentMessageLable];
        
        self.messageContentImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.messageContentImageView.bounds = CGRectMake(0, 0, 60, 60);
        [self.messageContentView addSubview:self.messageContentImageView];
        
        self.messageContentAttachmentTableView = [[UITableView alloc] initWithFrame:CGRectZero];
        self.messageContentAttachmentTableView.backgroundColor = [UIColor clearColor];
        self.messageContentAttachmentTableView.delegate = self;
        self.messageContentAttachmentTableView.dataSource = self;
        self.messageContentAttachmentTableView.scrollEnabled = NO;
        self.messageContentAttachmentTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        [self.messageContentView addSubview:self.messageContentAttachmentTableView];
        
        self.messageEmptyPromptView = [[UIView alloc] initWithFrame:CGRectZero];
        self.messageEmptyPromptView.layer.cornerRadius = 4;
        self.messageEmptyPromptView.backgroundColor = [CommonFunction colorWithString:@"cccccc" alpha:1.0f];
        self.messageEmptyPromptLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        [self.messageEmptyPromptView addSubview:self.messageEmptyPromptLabel];
        [self.contentView addSubview:self.messageEmptyPromptView];
        
        UITapGestureRecognizer *userGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:nil];
        userGesture.numberOfTapsRequired = 1;
        [self.messageUserIcon addGestureRecognizer:userGesture];
        
        UITapGestureRecognizer *contentGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(contentGesture:)];
        contentGesture.numberOfTapsRequired= 2;
        [self.messageContentView addGestureRecognizer:contentGesture];
        
        UILongPressGestureRecognizer *responseGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(responseGesture:)];
        responseGesture.minimumPressDuration = 1;
        [self.messageContentView addGestureRecognizer:responseGesture];
    }
    return self;
}

- (void)setMessage:(Message *)message {
    if (_message != message) {
        _message = message;
    }
}

- (void)userGesture:(UITapGestureRecognizer *)tapGesture {
    User *user = [User getUserWithUserEmail:self.message.messageSender context:nil];
    MailMessageUserViewController *userViewController = [[MailMessageUserViewController alloc] initWithUser:user];
    [self.mainViewController.navigationController pushViewController:userViewController animated:YES];
}

- (void)contentGesture:(UITapGestureRecognizer *)tapGesture {
    MailPreviewController *mailPreview = [[MailPreviewController alloc] initWithMessage:self.message];
    mailPreview.block = ^(){
        [self.delegate respondHistoryMessage:self.message];
    };
    [self.mainViewController.navigationController pushViewController:mailPreview animated:YES];
}

- (void)responseGesture:(UILongPressGestureRecognizer *)tapGesture {
    if (tapGesture.state == UIGestureRecognizerStateBegan) {
        UIActionSheet *responseSheet = [[UIActionSheet alloc] initWithTitle:self.message.messageSender delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedString(@"MailReplyTitle", nil),NSLocalizedString(@"MailTransmitTitle", nil),NSLocalizedString(@"MailDeleteTitle", nil),nil];
        responseSheet.tag = 10001;
        [responseSheet showInView:self];
    }
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {
        [self.delegate respondHistoryMessage:self.message];
    } else if(buttonIndex == 1) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        Message *shadow = (Message *)[delegate.localManager.managedObjectContext objectWithID:self.message.objectID];
        MailForwardViewController *forwardViewController = [[MailForwardViewController alloc] initWithForwardMessage:shadow];
        [self.mainViewController.navigationController pushViewController:forwardViewController animated:YES];
    } else if(buttonIndex == 2) {
        [UIAlertView showAlertViewWithTitle:nil message:NSLocalizedString(@"MailDeletePrompt", nil) cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
            AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
            NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
            [ctx performBlockAndWait:^{
                Message *shadow = (Message*)[ctx objectWithID:self.message.objectID];
                [shadow removeMessage];
                [ctx save:nil];
            }];
        } onCancel:^{}];
    }
}

- (void)hideWebView:(UIGestureRecognizer *)tap {
    UIWebView *webView = (UIWebView *)[tap.view viewWithTag:1];
    [UIView animateWithDuration:0.3 animations:^{
        webView.frame = [originView convertRect:originView.bounds toView:[UIApplication sharedApplication].keyWindow];
    } completion:^(BOOL finished) {
        [self.webViewBackGroundView removeFromSuperview];
    }];
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    if (!self.message) return;
    CGFloat messageContentViewHeight = 10;
    
    if (self.message.messageTitle) {
        if (self.lastMessage.messageTitle) {
            self.messageTitleShow = ![self.message.messageTitle isEqualToString:self.lastMessage.messageTitle];
        } else {
            self.messageTitleShow = YES;
        }
    } else {
        self.messageTitleShow = NO;
    }
    //self.messageTitleShow = ![self.message.messageTitle isEqualToString:self.lastMessage.messageTitle ];
    if (self.messageTitleShow && self.message.messageTitle && ![self.message.messageTitle isEqualToString:@""]) {
        self.messageContentTitleLabel.hidden = NO;
        self.messageContentTitleLabel.text = self.message.messageTitle;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:self.messageContentTitleLabel limitSize:CGSizeMake(MaxContentViewWidth-12-12, 1000)];
        self.messageContentTitleLabel.frame = CGRectMake(12, messageContentViewHeight, adjustTitleSize.width, MIN(adjustTitleSize.height, 48));
        messageContentViewHeight += CGRectGetHeight(self.messageContentTitleLabel.frame);
    } else {
        self.messageContentTitleLabel.hidden = YES;
        self.messageContentTitleLabel.frame = CGRectZero;
    }
    
    NSString *messageImagePath = [self.message messageImageLocalPath];
    if (messageImagePath && [[NSFileManager defaultManager] fileExistsAtPath:messageImagePath]) {
        self.messageImageShow = YES;
    } else {
        self.messageImageShow = NO;
    }
    self.messageMessageShow = (self.message.messagePlainContent.length > 0);
    if (self.messageMessageShow && self.messageImageShow) {
        self.messageContentImageView.hidden = NO;
        self.messageContentMessageLable.hidden = NO;
        self.messageContentImageView.image = [UIImage imageWithContentsOfFile:messageImagePath];
        self.messageContentMessageLable.text = self.message.messagePlainContent;
        CGSize adjustMessageSize = [CommonFunction labelSizeWithLabel:self.messageContentMessageLable limitSize:CGSizeMake(MaxContentViewWidth-12-12-60-8, 1000)];
        if (self.messageTitleShow) {
            self.messageContentImageView.frame = CGRectMake(12, messageContentViewHeight+4, 60, 60);
            
            self.messageContentMessageLable.frame = CGRectMake(CGRectGetMaxX(self.messageContentImageView.frame)+8, messageContentViewHeight+4, MaxContentViewWidth-CGRectGetMaxX(self.messageContentImageView.frame)-8-12, MIN(60, adjustMessageSize.height));
            
            messageContentViewHeight += 4 + 60;
        } else {
            self.messageContentImageView.frame = CGRectMake(12, messageContentViewHeight, 60, 60);
            
            self.messageContentMessageLable.frame = CGRectMake(CGRectGetMaxX(self.messageContentImageView.frame)+8, messageContentViewHeight, MaxContentViewWidth-CGRectGetMaxX(self.messageContentImageView.frame)-8-12, MIN(60, adjustMessageSize.height));
            
            messageContentViewHeight += 60;
        }
    }
    
    if (!self.messageMessageShow && self.messageImageShow) {
        self.messageContentImageView.hidden = NO;
        self.messageContentMessageLable.hidden = YES;
        self.messageContentMessageLable.frame = CGRectZero;
        self.messageContentImageView.image = [UIImage imageWithContentsOfFile:messageImagePath];
        if (self.messageTitleShow) {
            self.messageContentImageView.frame = CGRectMake(12, messageContentViewHeight+4, MaxContentViewWidth-12-12, MaxContentViewWidth-12-12);
            messageContentViewHeight += 4 + MaxContentViewWidth-12-12;
        } else {
            self.messageContentImageView.frame = CGRectMake(12, messageContentViewHeight, MaxContentViewWidth-12-12, MaxContentViewWidth-12-12);
            messageContentViewHeight += MaxContentViewWidth-12-12;
        }
    }
    
    if (self.messageMessageShow && !self.messageImageShow) {
        self.messageContentImageView.hidden = YES;
        self.messageContentMessageLable.hidden = NO;
        self.messageContentImageView.frame = CGRectZero;
        NSUInteger messageContentMessageLength = [self.message.messagePlainContent length];
        if (messageContentMessageLength > 140) {
            self.messageContentMessageLable.text = [self.message.messagePlainContent substringToIndex:140];
            self.messageContentMessageLable.text = [self.messageContentMessageLable.text stringByAppendingString:@"..."];
        } else {
            self.messageContentMessageLable.text = self.message.messagePlainContent;
        }
        CGSize adjustMessageSize = [CommonFunction labelSizeWithLabel:self.messageContentMessageLable limitSize:CGSizeMake(MaxContentViewWidth-12-12, 1000)];
        if (self.messageTitleShow) {
            self.messageContentMessageLable.frame = CGRectMake(12, messageContentViewHeight+4, adjustMessageSize.width, adjustMessageSize.height);
            messageContentViewHeight += 4 + CGRectGetHeight(self.messageContentMessageLable.frame);
        } else {
            self.messageContentMessageLable.frame = CGRectMake(12, messageContentViewHeight, adjustMessageSize.width, adjustMessageSize.height);
            messageContentViewHeight += CGRectGetHeight(self.messageContentMessageLable.frame);
        }
    }
    
    if (!self.messageMessageShow && !self.messageImageShow) {
        self.messageContentImageView.hidden = YES;
        self.messageContentMessageLable.hidden = YES;
        self.messageContentImageView.frame = CGRectZero;
        self.messageContentMessageLable.frame = CGRectZero;
    }
    
    self.messageContentAttachmentArray = [Attachment getAttachmentWithoutDisplayWithMessageId:self.message.messageId ctx:nil];
    if (self.messageContentAttachmentArray.count == 0) {
        self.messageAttachmentShow = NO;
    } else {
        self.messageAttachmentShow = YES;
    }
    if (self.messageAttachmentShow) {
        self.messageContentAttachmentTableView.hidden = NO;
        if (self.messageTitleShow || self.messageImageShow || self.messageMessageShow) {
            self.messageContentAttachmentTableView.frame = CGRectMake(12, messageContentViewHeight+12, [self maxWidthOfAttachmentTableView], 56*self.messageContentAttachmentArray.count);
            messageContentViewHeight += 10 + CGRectGetHeight(self.messageContentAttachmentTableView.frame) + 10;
        } else {
            self.messageContentAttachmentTableView.frame = CGRectMake(12, messageContentViewHeight, [self maxWidthOfAttachmentTableView], 56*self.messageContentAttachmentArray.count);
            messageContentViewHeight += CGRectGetHeight(self.messageContentAttachmentTableView.frame) + 10;
        }
        [self.messageContentAttachmentTableView reloadData];
    } else {
        self.messageContentAttachmentTableView.hidden = YES;
        self.messageContentAttachmentTableView.frame = CGRectZero;
        messageContentViewHeight += 12;
    }
    
    if (self.lastMessage) {
        self.messageTimeShow = [self minuteOffSetStart:self.message.messageSendDate end:self.lastMessage.messageSendDate];
    } else {
        self.messageTimeShow = YES;
    }
    if (self.messageTimeShow) {
        self.messageTimeView.hidden = NO;
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
        [dateFormatter setDateFormat:@"MM/dd/yy HH:mm"];
        self.messageTimeLable.text = [dateFormatter stringFromDate:self.message.messageSendDate];
        CGSize adjustTimeSize = [CommonFunction labelSizeWithLabel:self.messageTimeLable limitSize:CGSizeMake(CGRectGetWidth(self.frame)-10-10-10-10, 20)];
        self.messageTimeLable.frame = CGRectMake(10, 0, adjustTimeSize.width, 20);
        self.messageTimeView.bounds = CGRectMake(0, 0, 10+adjustTimeSize.width+10, 20);
        self.messageTimeView.frame = CGRectMake((CGRectGetWidth(self.frame)-CGRectGetWidth(self.messageTimeView.frame))/2, 9, CGRectGetWidth(self.messageTimeView.frame), CGRectGetHeight(self.messageTimeView.frame));
    } else {
        self.messageTimeView.hidden = YES;
        self.messageTimeView.frame = CGRectZero;
    }
    
    if (!self.messageTitleShow && !self.messageImageShow && !self.messageMessageShow && !self.messageAttachmentShow) {
        self.messageEmptyPromptShow = YES;
    } else {
        self.messageEmptyPromptShow = NO;
    }
    if (self.messageEmptyPromptShow) {
        self.messageEmptyPromptView.hidden = NO;
        self.messageUserIconBackground.hidden = YES;
        self.messageContentView.hidden = YES;
        self.messageUserIconBackground.frame = CGRectZero;
        self.messageContentView.frame = CGRectZero;
        self.messageEmptyPromptLabel.text = [NSString stringWithFormat:NSLocalizedString(@"MailEmptyPrompt", nil),self.message.messageSender];
        CGSize adjustEmptySize = [CommonFunction labelSizeWithLabel:self.messageEmptyPromptLabel limitSize:CGSizeMake(CGRectGetWidth(self.frame)-10-10-10-10, 20)];
        self.messageEmptyPromptLabel.frame = CGRectMake(10, 0, adjustEmptySize.width, 20);
        self.messageEmptyPromptView.bounds = CGRectMake(0, 0, 10+adjustEmptySize.width+10, 20);
        if (self.messageTimeShow) {
            self.messageEmptyPromptView.frame = CGRectMake((CGRectGetWidth(self.frame)-CGRectGetWidth(self.messageEmptyPromptView.frame))/2, CGRectGetMaxY(self.messageTimeView.frame)+14, CGRectGetWidth(self.messageEmptyPromptView.frame), CGRectGetHeight(self.messageEmptyPromptView.frame));
        } else {
            self.messageEmptyPromptView.frame = CGRectMake((CGRectGetWidth(self.frame)-CGRectGetWidth(self.messageEmptyPromptView.frame))/2, 9, CGRectGetWidth(self.messageEmptyPromptView.frame), CGRectGetHeight(self.messageEmptyPromptView.frame));
        }
    } else {
        self.messageEmptyPromptView.hidden = YES;
        self.messageUserIconBackground.hidden = NO;
        self.messageContentView.hidden = NO;
        self.messageEmptyPromptView.frame = CGRectZero;
        CGFloat messageContentViewWidth;
        if (self.messageImageShow && self.messageMessageShow) {
            messageContentViewWidth = MAX(CGRectGetWidth(self.messageContentTitleLabel.frame), MAX(CGRectGetWidth(self.messageContentMessageLable.frame)+CGRectGetWidth(self.messageContentImageView.frame), CGRectGetWidth(self.messageContentAttachmentTableView.frame)))+12+12;
        } else {
            messageContentViewWidth = MAX(CGRectGetWidth(self.messageContentTitleLabel.frame), MAX(CGRectGetWidth(self.messageContentImageView.frame), MAX(CGRectGetWidth(self.messageContentMessageLable.frame), CGRectGetWidth(self.messageContentAttachmentTableView.frame))))+12+12;
        }
        User *user = [User getUserWithUserEmail:self.message.messageSender context:nil];
        [UserThumbnail imageWithUser:user imageView:self.messageUserIcon refresh:YES];
        if (self.message.messageType.intValue == 1) {
            //            self.messageContentView.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:0.85f];
            self.messageContentView.layer.borderColor = [UIColor clearColor].CGColor;
            self.messageContentTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
            self.messageContentMessageLable.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
            if (self.messageTimeShow) {
                self.messageUserIconBackground.frame = CGRectMake(CGRectGetWidth(self.frame)-10-44, CGRectGetMaxY(self.messageTimeView.frame)+14, 44, 44);
                self.messageContentView.frame = CGRectMake(CGRectGetWidth(self.frame)-10-44-8-messageContentViewWidth, CGRectGetMaxY(self.messageTimeView.frame)+14, messageContentViewWidth, messageContentViewHeight);
            } else {
                self.messageUserIconBackground.frame = CGRectMake(CGRectGetWidth(self.frame)-10-44, 9, 44, 44);
                self.messageContentView.frame = CGRectMake(CGRectGetWidth(self.frame)-10-44-8-messageContentViewWidth, 9, messageContentViewWidth, messageContentViewHeight);
            }
            UIBezierPath *rightPath = [UIBezierPath bubbleShape:self.messageContentView.bounds direct:2];
            [self setMaskWithPath:rightPath withBorderColor:[CommonFunction colorWithString:@"2e90e5" alpha:1.0f] borderWidth:0.5 backGroundColor:[CommonFunction colorWithString:@"2e90e5" alpha:0.85f]];
        } else {
            //            self.messageContentView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
            self.messageContentView.layer.borderColor = [UIColor clearColor].CGColor;
            self.messageContentTitleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
            self.messageContentMessageLable.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
            if (self.messageTimeShow) {
                self.messageUserIconBackground.frame = CGRectMake(10, CGRectGetMaxY(self.messageTimeView.frame)+14, 44, 44);
                self.messageContentView.frame = CGRectMake(10+44+8, CGRectGetMaxY(self.messageTimeView.frame)+14, messageContentViewWidth, messageContentViewHeight);
            } else {
                self.messageUserIconBackground.frame = CGRectMake(10, 9, 44, 44);
                self.messageContentView.frame = CGRectMake(CGRectGetMaxX(self.messageUserIconBackground.frame)+8, 9, messageContentViewWidth, messageContentViewHeight);
            }
            UIBezierPath *leftPath = [UIBezierPath bubbleShape:self.messageContentView.bounds direct:1];
            [self setMaskWithPath:leftPath withBorderColor:[CommonFunction colorWithString:@"cccccc" alpha:1.0f] borderWidth:0.5 backGroundColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f]];
            
        }
    }
}
- (void)setMaskWithPath:(UIBezierPath*)path withBorderColor:(UIColor*)borderColor borderWidth:(float)borderWidth backGroundColor:(UIColor *)backGroundColor{
    if (borderColor && borderWidth>0) {
        self.maskBorderLayer.path = [path CGPath];
        self.maskBorderLayer.fillColor = [backGroundColor CGColor];
        self.maskBorderLayer.strokeColor = [borderColor CGColor];
        self.maskBorderLayer.lineWidth = borderWidth;
        self.maskBorderLayer.cornerRadius = 8;
        //        self.maskBorderLayer.masksToBounds = YES;
    }
}
- (void)refreshUI{
    self.isDownloadFailView.hidden = NO;
}

- (CGFloat)cellHeight {
    [super layoutSubviews];
    if (!self.message) {
        return 0.0f;
    }
    
    CGFloat messageCellViewHeight = 0;
    CGFloat messageContentViewHeight = 10;
    
    if (self.message.messageTitle) {
        if (self.lastMessage.messageTitle) {
            self.messageTitleShow = ![self.message.messageTitle isEqualToString:self.lastMessage.messageTitle];
        } else {
            self.messageTitleShow = YES;
        }
    } else {
        self.messageTitleShow = NO;
    }
    //self.messageTitleShow = ![self.message.messageTitle isEqualToString:self.lastMessage.messageTitle ];
    if (self.messageTitleShow && self.message.messageTitle && ![self.message.messageTitle isEqualToString:@""]) {
        self.messageContentTitleLabel.text = self.message.messageTitle;
        CGSize adjustTitleSize = [CommonFunction labelSizeWithLabel:self.messageContentTitleLabel limitSize:CGSizeMake(MaxContentViewWidth-12-12, 48)];
        messageContentViewHeight += adjustTitleSize.height;
    }
    
    NSString *messageImagePath = [self.message messageImageLocalPath];
    if (messageImagePath && [[NSFileManager defaultManager] fileExistsAtPath:messageImagePath]) {
        self.messageImageShow = YES;
    } else {
        self.messageImageShow = NO;
    }
    self.messageMessageShow = (self.message.messagePlainContent.length > 0);
    if (self.messageMessageShow && self.messageImageShow) {
        if (self.messageTitleShow) {
            messageContentViewHeight += 4 + 60;
        } else {
            messageContentViewHeight += 60;
        }
    }
    
    if (!self.messageMessageShow && self.messageImageShow) {
        if (self.messageTitleShow) {
            messageContentViewHeight += 4 + MaxContentViewWidth-12-12;
        } else {
            messageContentViewHeight += MaxContentViewWidth-12-12;
        }
    }
    
    if (self.messageMessageShow && !self.messageImageShow) {
        NSUInteger messageContentMessageLength = [self.message.messagePlainContent length];
        if (messageContentMessageLength > 140) {
            self.messageContentMessageLable.text = [self.message.messagePlainContent substringToIndex:140];
            self.messageContentMessageLable.text = [self.messageContentMessageLable.text stringByAppendingString:@"..."];
        } else {
            self.messageContentMessageLable.text = self.message.messagePlainContent;
        }
        CGSize adjustMessageSize = [CommonFunction labelSizeWithLabel:self.messageContentMessageLable limitSize:CGSizeMake(MaxContentViewWidth-12-12, 1000)];
        if (self.messageTitleShow) {
            messageContentViewHeight += 4 + adjustMessageSize.height;
        } else {
            messageContentViewHeight += adjustMessageSize.height;
        }
    }
    
    self.messageContentAttachmentArray = [Attachment getAttachmentWithoutDisplayWithMessageId:self.message.messageId ctx:nil];
    if (self.messageContentAttachmentArray.count == 0) {
        self.messageAttachmentShow = NO;
    } else {
        self.messageAttachmentShow = YES;
    }
    if (self.messageAttachmentShow) {
        if (self.messageTitleShow || self.messageImageShow || self.messageMessageShow) {
            messageContentViewHeight += 12 + 48*self.messageContentAttachmentArray.count + 10;
        } else {
            messageContentViewHeight += 48*self.messageContentAttachmentArray.count + 10;
        }
    } else {
        messageContentViewHeight += 12;
    }
    
    if (self.lastMessage) {
        self.messageTimeShow = [self minuteOffSetStart:self.message.messageSendDate end:self.lastMessage.messageSendDate];
    } else {
        self.messageTimeShow = YES;
    }
    if (self.messageTimeShow) {
        messageCellViewHeight += 20;
    }
    
    if (!self.messageTitleShow && !self.messageImageShow && !self.messageMessageShow && !self.messageAttachmentShow) {
        self.messageEmptyPromptShow = YES;
    } else {
        self.messageEmptyPromptShow = NO;
    }
    if (self.messageEmptyPromptShow) {
        if (self.messageTimeShow) {
            messageCellViewHeight += 14 + 20 + 20;
        } else {
            messageCellViewHeight += 20 + 20;
        }
    } else {
        if (self.messageTimeShow) {
            messageCellViewHeight += 14 + messageContentViewHeight + 20;
        } else {
            messageCellViewHeight += messageContentViewHeight + 20;
        }
    }
    return messageCellViewHeight;
}


- (BOOL)minuteOffSetStart:(NSDate*)start end:(NSDate*)end {
    NSTimeInterval offset = [start timeIntervalSinceDate:end];
    if (fabs(offset) > 5*60) {
        return YES;
    }
    return NO;
}

- (CGFloat)maxWidthOfAttachmentTableView {
    CGFloat maxWidth = 0;
    for (Attachment *attachment in self.messageContentAttachmentArray) {
        CGSize adjustTitleLabelSize = [CommonFunction labelSizeWithString:attachment.attachmentName font:[UIFont systemFontOfSize:15.0f]];
        CGSize adjustSizeLabelSize = [CommonFunction labelSizeWithString:[CommonFunction pretySize:attachment.attachmentSize.longLongValue] font:[UIFont systemFontOfSize:12.0f]];
        CGFloat adjustLabelWidth = MAX(adjustTitleLabelSize.width, adjustSizeLabelSize.width);
        adjustLabelWidth = MIN(adjustLabelWidth, MaxContentViewWidth-12-48-8-12);
        if (maxWidth < adjustLabelWidth) {
            maxWidth = adjustLabelWidth;
        }
    }
    return maxWidth+8+48;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSArray *attachmentArray = [Attachment getAttachmentWithMessageId:self.message.messageId ctx:nil];
    return attachmentArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 56.0f;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MailMessageAttachmentCell *cell = [[MailMessageAttachmentCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    Attachment *attachment = [self.messageContentAttachmentArray objectAtIndex:indexPath.row];
    cell.attachment = attachment;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    Attachment *attachment = [self.messageContentAttachmentArray objectAtIndex:indexPath.row];
    if (self.delegate && [self.delegate respondsToSelector:@selector(previewAttachment:)]) {
        [self.delegate previewAttachment:attachment];
    }
}

- (void)prepareForReuse {
    [super prepareForReuse];
    self.messageUserIcon.image = nil;
}

@end
