//
//  MailPreviewController.m
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailPreviewController.h"
#import "Attachment.h"
#import "Message.h"
#import "User.h"
#import "CommonFunction.h"
#import "AppDelegate.h"
#import "CloudFileMoreMenuView.h"
#import "UIAlertView+Blocks.h"
#import "MailForwardViewController.h"
@implementation MailPreviewContentView

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.delegate = self;
        self.hidden = YES;
    }
    return self;
}

- (void)loadHTMLString:(NSString *)string baseURL:(NSURL *)baseURL {
    NSString *html = [NSString  stringWithFormat:@"<html><head><title></title><script Type = 'text/javascript'>function imageElements(){\
                      var imageNodes = document.getElementsByTagName('img');\
                      return [].slice.call(imageNodes);\
                      };\
                      \
                      function findCIDImageURL(){\
                      var images = imageElements();\
                      var imgLinks = [];\
                      for(var i = 0;i < images.length;i++){\
                      var url = images[i].getAttribute('src');\
                      imgLinks.push(url);\
                      }\
                      return JSON.stringify(imgLinks);\
                      }\
                      function replaceImage(info){\
                      var images = imageElements();\
                      images[info.Count].setAttribute('src',info.LocalPathKey);\
                      }\
                      function ResizeImages(){\
                      var myimg;\
                      for(i = 0;i < document.images.length;i++){\
                      myimg = document.images[i];\
                      if(myimg.width > 345){\
                      myimg.width = 345;\
                      }\
                      }\
                      }\
                      </script></head><body>%@</body></html>",string];
    [super loadHTMLString:html baseURL:baseURL];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    NSString *result = [webView stringByEvaluatingJavaScriptFromString:@"findCIDImageURL()"];
    if (result) {
        [self loadImages:result];
    }
}

- (void)loadImages:(NSString *)result {
    NSData *data = [result dataUsingEncoding:NSUTF8StringEncoding];
    NSError *error = nil;
    NSArray *imagesURLStrings = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
    NSArray *attachments = [Attachment getAttachmentWithMessageId:self.message.messageId ctx:nil];
    for (NSString *urlString in imagesURLStrings) {
        for (Attachment *inlineattachment in attachments) {
            NSString *src = [NSString stringWithFormat:@"cid:%@",inlineattachment.attachmentId];
            if ([src isEqualToString:urlString]) {
                NSString *attachmentDataLocalPath = [inlineattachment attachmentDataLocalPath];
                NSURL *url = [NSURL fileURLWithPath:attachmentDataLocalPath];
                NSDictionary *args = @{@"Count":[NSNumber numberWithInteger:[imagesURLStrings indexOfObject:urlString]],@"LocalPathKey":url.absoluteString};
                NSData *json = [NSJSONSerialization dataWithJSONObject:args options:0 error:nil];
                NSString *jsonString = [[NSString alloc] initWithData:json encoding:NSUTF8StringEncoding];
                NSString *replaceScript = [NSString stringWithFormat:@"replaceImage(%@)",jsonString];
                [self stringByEvaluatingJavaScriptFromString:replaceScript];
            }
        }
    }
    [self performSelector:@selector(ResizeImage) withObject:nil afterDelay:0.5];
}

- (void)ResizeImage {
    [self stringByEvaluatingJavaScriptFromString:@"ResizeImages()"];
    self.hidden = NO;
}

@end


@interface MailPreviewController ()<UIWebViewDelegate>

@property (nonatomic, strong) Message *message;
@property (nonatomic, strong) UILabel *mailPreviewTitle;
@property (nonatomic, strong) UIButton *mailPreviewBackButton;
@property (nonatomic, strong) UIButton *mailPreviewMenuButton;
@property (nonatomic, strong) UIView *mailPreviewHeaderView;
@property (nonatomic, strong) MailPreviewContentView *mailPreviewContentView;
@property (nonatomic, strong) CloudFileMoreMenuView *mailPreviewMoreMenu;
@end

@implementation MailPreviewController

- (id)initWithMessage:(Message *)message {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.message = message;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.mailPreviewTitle = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailPreviewTitle.text = NSLocalizedString(@"MailShowTitle", nil);
    
    self.mailPreviewBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailPreviewBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailPreviewBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailPreviewBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailPreviewBackButton addTarget:self action:@selector(mailPreviewBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.mailPreviewMenuButton = [[UIButton alloc] initWithFrame:CGRectMake(self.view.frame.size.width - 48, 0, 44, 44)];
    self.mailPreviewMenuButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailPreviewMenuButton setImage:[UIImage imageNamed:@"ic_nav_menu_nor"] forState:UIControlStateNormal];
    [self.mailPreviewMenuButton setImage:[UIImage imageNamed:@"ic_nav_menu_press"] forState:UIControlStateHighlighted];
    [self.mailPreviewMenuButton addTarget:self action:@selector(mailPreviewMenuButtonClick) forControlEvents:UIControlEventTouchUpInside];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    self.mailPreviewHeaderView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.mailPreviewHeaderView.frame), CGRectGetHeight(self.mailPreviewHeaderView.frame));
    [self.view addSubview:self.mailPreviewHeaderView];
    
    self.mailPreviewContentView = [[MailPreviewContentView alloc] initWithFrame:CGRectMake(15, statusBarFrame.size.height+navigationBarFrame.size.height+CGRectGetHeight(self.mailPreviewHeaderView.frame)+15, CGRectGetWidth(self.view.frame)-15-15, CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-CGRectGetHeight(self.mailPreviewHeaderView.frame)-15-15)];
    [self.view addSubview:self.mailPreviewContentView];
    self.mailPreviewContentView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.mailPreviewContentView.scrollView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.mailPreviewContentView.message = self.message;
    [self.mailPreviewContentView loadHTMLString:self.message.messageHTMLContent baseURL:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.mailPreviewTitle];
    [self.navigationController.navigationBar addSubview:self.mailPreviewBackButton];
    [self.navigationController.navigationBar addSubview:self.mailPreviewMenuButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailPreviewTitle removeFromSuperview];
    [self.mailPreviewBackButton removeFromSuperview];
    [self.mailPreviewMenuButton removeFromSuperview];
}

- (void)mailPreviewBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}
- (void)mailPreviewMenuButtonClick {
    if (self.mailPreviewMoreMenu.hidden) {
        self.mailPreviewMoreMenu.hidden = NO;
    } else {
        self.mailPreviewMoreMenu.hidden = YES;
    }
}

- (CloudFileMoreMenuView*)mailPreviewMoreMenu {
    if (!_mailPreviewMoreMenu) {
        _mailPreviewMoreMenu = [[CloudFileMoreMenuView alloc] initWithFrame:self.view.frame];
        _mailPreviewMoreMenu.fileViewController = self;
        
        CloudFileMoreMenuCell *mailDelete = [[CloudFileMoreMenuCell alloc] initWithImage:[UIImage imageNamed:@""] title:NSLocalizedString(@"MailDeleteTitle", nil) target:self action:@selector(MailDelete)];
        CloudFileMoreMenuCell *mailTransmit = [[CloudFileMoreMenuCell alloc] initWithImage:[UIImage imageNamed:@""] title:NSLocalizedString(@"MailTransmitTitle", nil) target:self action:@selector(MailTransmit)];
        CloudFileMoreMenuCell *mailReply = [[CloudFileMoreMenuCell alloc] initWithImage:[UIImage imageNamed:@""] title:NSLocalizedString(@"MailReplyTitle", nil) target:self action:@selector(MailReply)];
        [_mailPreviewMoreMenu setMenuCells:@[mailDelete,mailTransmit,mailReply]];
    }
    return _mailPreviewMoreMenu;
}
- (void)MailDelete{
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
- (void)MailTransmit{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    Message *shadow = (Message *)[delegate.localManager.managedObjectContext objectWithID:self.message.objectID];
    MailForwardViewController *forwardViewController = [[MailForwardViewController alloc] initWithForwardMessage:shadow];
    [self.navigationController pushViewController:forwardViewController animated:YES];
}
- (void)MailReply{
    if (self.block) {
        self.block();
    }
    [self.navigationController popViewControllerAnimated:YES];
}
- (UIView*)mailPreviewHeaderView {
    if (!_mailPreviewHeaderView) {
        _mailPreviewHeaderView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 78)];
        _mailPreviewHeaderView.layer.borderWidth = 0.5;
        _mailPreviewHeaderView.layer.borderColor = [CommonFunction colorWithString:@"bbbbbb" alpha:1.0f].CGColor;
        
        CGSize subjectLabelSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"MailShowSubjectTitle", nil) font:[UIFont systemFontOfSize:15.0f]];
        CGSize fromLabelSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"MailShowFromTitle", nil) font:[UIFont systemFontOfSize:15.0f]];
        CGFloat labelWidth = MAX(subjectLabelSize.width, fromLabelSize.width);
        UILabel *subjectTitleLabel = [CommonFunction labelWithFrame:CGRectMake(15, 20, labelWidth, 20) textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        subjectTitleLabel.text = NSLocalizedString(@"MailShowSubjectTitle", nil);
        UILabel *subjectLabel = [CommonFunction labelWithFrame:CGRectMake(15+labelWidth+10, 20, CGRectGetWidth(self.view.frame)-15-labelWidth-10-15, 20) textFont:[UIFont boldSystemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        subjectLabel.text = self.message.messageTitle;
        [_mailPreviewHeaderView addSubview:subjectTitleLabel];
        [_mailPreviewHeaderView addSubview:subjectLabel];
        
        UILabel *fromTitleLabel = [CommonFunction labelWithFrame:CGRectMake(15, CGRectGetMaxY(subjectTitleLabel.frame)+8, labelWidth, 20) textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        fromTitleLabel.text = NSLocalizedString(@"MailShowFromTitle", nil);
        
        User *sendUser = [User getUserWithUserEmail:self.message.messageSender context:nil];
        CGSize fromNameLabelSize = [CommonFunction labelSizeWithString:sendUser.userName font:[UIFont boldSystemFontOfSize:15.0f]];
        UILabel *fromNameLabel = [CommonFunction labelWithFrame:CGRectMake(15+labelWidth+10, CGRectGetMaxY(subjectTitleLabel.frame)+8, MIN(CGRectGetWidth(self.view.frame)-15-labelWidth-10-15, fromNameLabelSize.width) , 20) textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        fromNameLabel.text = sendUser.userName;
        UILabel *fromEmailLabel = [CommonFunction labelWithFrame:CGRectMake(CGRectGetMaxX(fromNameLabel.frame), CGRectGetMaxY(subjectTitleLabel.frame)+8, MAX(CGRectGetWidth(self.view.frame)-CGRectGetMaxX(fromNameLabel.frame)-15, 0), 20) textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        fromEmailLabel.text = [NSString stringWithFormat:@"(%@)",self.message.messageSender];
        [_mailPreviewHeaderView addSubview:fromTitleLabel];
        [_mailPreviewHeaderView addSubview:fromNameLabel];
        [_mailPreviewHeaderView addSubview:fromEmailLabel];
    }
    return _mailPreviewHeaderView;
}

@end
