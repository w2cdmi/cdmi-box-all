//
//  MailMessageViewController.m
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailMessageViewController.h"
#import <CoreData/CoreData.h>
#import <AVFoundation/AVFoundation.h>

#import "AppDelegate.h"
#import "Message.h"
#import "Session.h"
#import "TransportTask.h"
#import "File+Remote.h"

#import "MailSessionViewController.h"
#import "MailMessageTableViewCell.h"
#import "MailMessageInputView.h"
#import "MailManagerViewController.h"

#import "MessageSend.h"
#import "MessageIMAPSession.h"

#import "MailCloudAttachmentAddViewController.h"
#import "MailLocalAttachmentAddViewController.h"
#import "MailMessageAttachmentHandle.h"
#import "AttachmentUploadOperation.h"

#import "CloudPreviewController.h"
#import "MailAttachmentPreviewController.h"
@interface MailMessageViewController ()<UITableViewDataSource,UITableViewDelegate,NSFetchedResultsControllerDelegate,UITextFieldDelegate,MailMessageCellDelegate>

@property (nonatomic, strong) UILabel                  *mailMessageTitleLabel;
@property (nonatomic, strong) UIButton                 *mailMessageBackButton;
@property (nonatomic, strong) UIButton                 *mailMessageSettingButton;

@property (nonatomic, strong) UITableView              *mailMessageTableView;
@property (nonatomic, strong) MailMessageTableViewCell *mailMessageTableViewCell;

@property (nonatomic, strong) MailMessageInputView     *mailMessageInputView;

@property (nonatomic, strong) NSMutableArray           *mailAttachmentArray;

@property (nonatomic, strong) Message                  *mailMessageHistoryMessage;

@property (nonatomic, assign) BOOL                      mailMessageAttachmentState;
@property (nonatomic, assign) BOOL                      mailMessageSelectedState;
@property (nonatomic, assign) BOOL                      mailMessageResponseHistoryFlag;

@property (nonatomic, strong) NSFetchedResultsController *mailMessageFetchController;

@end

@implementation MailMessageViewController

- (id)initWithSession:(Session *)session{
    self = [super init];
    if (self) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        Session *shadow = (Session *)[delegate.localManager.managedObjectContext objectWithID:session.objectID];
        self.session = shadow;
        self.mailMessageAttachmentState = NO;
        self.mailMessageSelectedState = NO;
        self.mailMessageResponseHistoryFlag = NO;
        self.mailAttachmentArray = [[NSMutableArray alloc] init];
        self.mailMessageTableViewCell = [[MailMessageTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];

        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"messageSessionId = %@ AND messageOwner = %@",self.session.sessionId,appDelegate.localManager.userCloudId];
        NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"messageReceiveDate" ascending:YES];
        NSArray *sortdescription = @[sort];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setEntity:entity];
        [request setPredicate:predicate];
        [request setSortDescriptors:sortdescription];
        self.mailMessageFetchController = [[NSFetchedResultsController alloc]initWithFetchRequest:request managedObjectContext:ctx sectionNameKeyPath:nil cacheName:nil];
        [self.mailMessageFetchController setDelegate:self];
        [self performFetch];

        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(keyboardChange:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(keyboardChange:) name:UIKeyboardWillHideNotification object:nil];
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(tableViewScrollToBottom) name:UIKeyboardDidShowNotification object:nil];
    }
    return self;
}

- (void)performFetch {
    NSError *error = NULL;
    if (![self.mailMessageFetchController performFetch:&error]) {
        NSLog(@"Unresolved error %@,%@",error,[error userInfo]);
        abort();
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.mailMessageTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailMessageTitleLabel.text = self.session.sessionTitle;
    
    self.mailMessageBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailMessageBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailMessageBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailMessageBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailMessageBackButton addTarget:self action:@selector(mailMessageBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.mailMessageSettingButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
    self.mailMessageSettingButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailMessageSettingButton setImage:[UIImage imageNamed:@"ic_nav_group_nor"] forState:UIControlStateNormal];
    [self.mailMessageSettingButton setImage:[UIImage imageNamed:@"ic_nav_group_press"] forState:UIControlStateHighlighted];
    [self.mailMessageSettingButton addTarget:self action:@selector(mailMessageSettingButtonClick) forControlEvents:UIControlEventTouchUpInside];

    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    self.mailMessageTableView = [[UITableView alloc]initWithFrame:CGRectZero style:UITableViewStylePlain];
    [self.mailMessageTableView registerClass:[MailMessageTableViewCell class] forCellReuseIdentifier:@"MailMessageTableViewCell"];
    self.mailMessageTableView.backgroundColor = [UIColor clearColor];
    self.mailMessageTableView.dataSource = self;
    self.mailMessageTableView.delegate = self;
    self.mailMessageTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.mailMessageTableView.scrollEnabled = YES;
    self.mailMessageTableView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49);
    [self.view addSubview:self.mailMessageTableView];
    [self tableViewScrollToBottom];

    self.mailMessageInputView = [[MailMessageInputView alloc]initWithMailChat:self];
    self.mailMessageInputView.mailMessageInputTextField.delegate = self;
    [self.mailMessageInputView.mailMessageInputTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    [self.mailMessageInputView.mailAttachmentAddButton addTarget:self action:@selector(mailMessageAddAttachment) forControlEvents:UIControlEventTouchUpInside];
    [self.mailMessageInputView.mailMessageSendButton addTarget:self action:@selector(mailMessageSend) forControlEvents:UIControlEventTouchUpInside];
    [self.mailMessageInputView.mailAttachmentLocalFileAddButton addTarget:self action:@selector(addLocalFiles) forControlEvents:UIControlEventTouchUpInside];
    [self.mailMessageInputView.mailAttachmentCloudFileAddButton addTarget:self action:@selector(addCloudFiles) forControlEvents:UIControlEventTouchUpInside];
    self.mailMessageInputView.frame = CGRectMake(0, CGRectGetHeight(self.view.frame)-49, CGRectGetWidth(self.view.frame), 49+215);
    [self.view addSubview:self.mailMessageInputView];

    UITapGestureRecognizer *tableViewGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(mailMessageTableViewGestureHandle)];
    tableViewGesture.numberOfTapsRequired = 1;
    tableViewGesture.cancelsTouchesInView = NO;
    [self.mailMessageTableView addGestureRecognizer:tableViewGesture];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailMessageTitleLabel removeFromSuperview];
    [self.mailMessageBackButton removeFromSuperview];
    [self.mailMessageSettingButton removeFromSuperview];
    self.mailMessageFetchController.delegate = nil;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.mailMessageTitleLabel.text = self.session.sessionTitle;
    [self.navigationController.navigationBar addSubview:self.mailMessageTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailMessageBackButton];
    [self.navigationController.navigationBar addSubview:self.mailMessageSettingButton];
    self.mailMessageFetchController.delegate = self;
    [self setMailMessageSendButtonState];
    [[NSNotificationCenter defaultCenter]postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)mailMessageBackButtonClick {
    if (!self.session.sessionLastMessageId) {
        [self.session removeSession];
    } else {
        [self.session sessionUnreadMessageReset];
        [self.sessionCell refresh];
    }
    UIViewController *viewcontroller = self.navigationController.viewControllers[1];
    [self.navigationController popToViewController:viewcontroller animated:YES];
}

- (void)mailMessageSettingButtonClick {
    //MailContactManageController *manageVC = [[MailContactManageController alloc] initWithSession:self.session];
    //[self.navigationController pushViewController:manageVC animated:YES];
    MailManagerViewController *mailManager = [[MailManagerViewController alloc] initWithSession:self.session];
    [self.navigationController pushViewController:mailManager animated:YES];
}

- (void)setMailMessageSendButtonState {
    [self.mailMessageInputView setMailMessageSendButtonDisable];
    if (self.mailMessageInputView.mailMessageInputTextField.text &&
        ![self.mailMessageInputView.mailMessageInputTextField.text isEqualToString:@""]) {
        [self.mailMessageInputView setMailMessageSendButtonEnable];
    }
    if (self.mailAttachmentArray.count > 0) {
        [self.mailMessageInputView setMailMessageSendButtonEnable];
    }
}

#pragma mark tableView delegate
- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.mailMessageFetchController.fetchedObjects.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    Message *message = [self.mailMessageFetchController objectAtIndexPath:indexPath];
    self.mailMessageTableViewCell.session = self.session;
    if (indexPath.row == 0) {
        self.mailMessageTableViewCell.lastMessage = nil;
    } else {
        NSIndexPath *lastIndexPath = [NSIndexPath indexPathForRow:indexPath.row-1 inSection:indexPath.section];
        self.mailMessageTableViewCell.lastMessage = [self.mailMessageFetchController objectAtIndexPath:lastIndexPath];
    }
    self.mailMessageTableViewCell.message = message;
    return [self.mailMessageTableViewCell cellHeight];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MailMessageTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"MailMessageTableViewCell"];
    if (!cell) {
        cell = [[MailMessageTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"MailMessageTableViewCell"];
    }
    Message *message = [self.mailMessageFetchController objectAtIndexPath:indexPath];
    if (indexPath.row == 0) {
        cell.lastMessage = nil;
    } else {
        NSIndexPath *lastIndexPath = [NSIndexPath indexPathForRow:indexPath.row-1 inSection:indexPath.section];
        cell.lastMessage = [self.mailMessageFetchController objectAtIndexPath:lastIndexPath];
    }
    cell.delegate = self;
    cell.message = message;
    cell.session = self.session;
    cell.mainViewController = self;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.mailMessageSelectedState) {
        Message *message = [self.mailMessageFetchController objectAtIndexPath:indexPath];
        self.mailMessageHistoryMessage = message;
        MailMessageTableViewCell *cell = (MailMessageTableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
        cell.isSelected = YES;
    } else {
        Message *message = [self.mailMessageFetchController objectAtIndexPath:indexPath];
        AttachmentUploadOperation *attachmentUploadOperation = [[AttachmentUploadOperation alloc] init];
        attachmentUploadOperation.uploadComletion = ^(){};
        [attachmentUploadOperation attachmentsUploadWithMessage:message];
    }
}

- (void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath{
    if (self.mailMessageSelectedState) {
        MailMessageTableViewCell *cell = (MailMessageTableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
        cell.isSelected = NO;
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    if (self.mailMessageAttachmentState) {
        [self mailMessageInputAttachmentHide];
    }
    if ([self.mailMessageInputView.mailMessageInputTextField isFirstResponder]) {
        [self.mailMessageInputView.mailMessageInputTextField resignFirstResponder];
    }
}

- (void)tableViewScrollToBottom {
    id<NSFetchedResultsSectionInfo> sectionInfo = [[self.mailMessageFetchController sections]objectAtIndex:0];
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:[sectionInfo numberOfObjects]-1 inSection:0];
    if (self.mailMessageFetchController.fetchedObjects.count != 0) {
        [self.mailMessageTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
}

#pragma mark inputView state
- (void)mailMessageTableViewGestureHandle {
    if (self.mailMessageAttachmentState) {
        [self mailMessageInputAttachmentHide];
    }
    if ([self.mailMessageInputView.mailMessageInputTextField isFirstResponder]) {
        [self.mailMessageInputView.mailMessageInputTextField resignFirstResponder];
        self.mailMessageInputView.mailMessageInputTextField.placeholder = @"";
        self.mailMessageHistoryMessage = nil;
        self.mailMessageResponseHistoryFlag = NO;
    }
}

- (void)mailMessageInputAttachmentShow {
    self.mailMessageAttachmentState = YES;
    if ([self.mailMessageInputView.mailMessageInputTextField isFirstResponder]) {
        [self.mailMessageInputView.mailMessageInputTextField resignFirstResponder];
    } else {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:0.25f];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
        CGRect inputNewFrame = self.mailMessageInputView.frame;
        inputNewFrame.origin.y = CGRectGetHeight(self.view.frame)-49-215;
        self.mailMessageInputView.frame = inputNewFrame;
        CGRect chatNewFrame = self.mailMessageTableView.frame;
        chatNewFrame.size.height = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49-215;
        self.mailMessageTableView.frame = chatNewFrame;
        [self tableViewScrollToBottom];
        [UIView commitAnimations];
    }
}

- (void)mailMessageInputAttachmentHide {
    self.mailMessageAttachmentState = NO;
    if ([self.mailMessageInputView.mailMessageInputTextField isFirstResponder]) {
        [self.mailMessageInputView.mailMessageInputTextField resignFirstResponder];
    } else {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:0.25f];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
        CGRect inputNewFrame = self.mailMessageInputView.frame;
        inputNewFrame.origin.y = CGRectGetHeight(self.view.frame)-49;
        self.mailMessageInputView.frame = inputNewFrame;
        CGRect chatNewFrame = self.mailMessageTableView.frame;
        chatNewFrame.size.height = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49;
        self.mailMessageTableView.frame = chatNewFrame;
        [self tableViewScrollToBottom];
        [UIView commitAnimations];
    }
}

#pragma mark textViewDelegate
- (void)textFieldDidChange:(UITextField*)textField {
    [self setMailMessageSendButtonState];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [textField becomeFirstResponder];
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [textField resignFirstResponder];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (self.mailMessageAttachmentState) {
        [self mailMessageInputAttachmentHide];
    }
    if ([self.mailMessageInputView.mailMessageInputTextField isFirstResponder]) {
        [self.mailMessageInputView.mailMessageInputTextField resignFirstResponder];
    }
    return YES;
}

#pragma mark add attachment
- (void)mailMessageAddAttachment {
    [self mailMessageInputAttachmentShow];
}

- (void)addCloudFiles {
    MailCloudAttachmentAddViewController *attachmentAddViewController = [[MailCloudAttachmentAddViewController alloc] initWithFile:[File rootMyFolder]];
    attachmentAddViewController.completion = ^(NSArray *attachmentArray,BOOL comfirm) {
        [self.mailAttachmentArray addObjectsFromArray:attachmentArray];
        if (self.mailAttachmentArray.count > 0) {
            self.mailMessageInputView.mailAttachmentAddedButton.hidden = NO;
            self.mailMessageInputView.mailAttachmentAddedLabel.text = [NSString stringWithFormat:@"%ld",(unsigned long)self.mailAttachmentArray.count];
        } else {
            self.mailMessageInputView.mailAttachmentAddedButton.hidden = YES;
        }
        [self setMailMessageSendButtonState];
    };
    attachmentAddViewController.rootViewController = self;
    [self.navigationController pushViewController:attachmentAddViewController animated:YES];
}

- (void)addLocalFiles {
    MailLocalAttachmentAddViewController *attachmentAddViewController = [[MailLocalAttachmentAddViewController alloc] init];
    attachmentAddViewController.completion = ^(NSArray* succeededAssetArray, NSArray* failedAssetArray) {
        [self.mailAttachmentArray addObjectsFromArray:succeededAssetArray];
        if (self.mailAttachmentArray.count > 0) {
            self.mailMessageInputView.mailAttachmentAddedButton.hidden = NO;
            self.mailMessageInputView.mailAttachmentAddedLabel.text = [NSString stringWithFormat:@"%ld",(unsigned long)self.mailAttachmentArray.count];
        } else {
            self.mailMessageInputView.mailAttachmentAddedButton.hidden = YES;
        }
        [self setMailMessageSendButtonState];
        [self.navigationController popToViewController:self animated:YES];
    };
    attachmentAddViewController.rootViewController = self;
    [self.navigationController pushViewController:attachmentAddViewController animated:YES];
}

- (void)previewAttachment:(Attachment *)attachment {
    mailAttachmentPreviewController *preview = [[mailAttachmentPreviewController alloc] initWithAttachment:attachment];
    [self.navigationController pushViewController:preview animated:YES];
}

#pragma mark respond history
- (void)respondHistoryMessage:(Message *)message{
    self.mailMessageHistoryMessage = message;
    self.mailMessageResponseHistoryFlag = YES;
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    User *user = [User getUserWithUserEmail:message.messageSender context:delegate.localManager.managedObjectContext];
    self.mailMessageInputView.mailMessageInputTextField.placeholder = [NSString stringWithFormat:@"回复:%@",user.userName];
    [self.mailMessageInputView.mailMessageInputTextField becomeFirstResponder];
}

#pragma mark sendMessage
- (void)mailMessageSend {
    MailMessageAttachmentHandle *attachmentHandle = [[MailMessageAttachmentHandle alloc] initWithAttachmentArray:self.mailAttachmentArray];
    attachmentHandle.handleCompletion = ^(NSString *attachmentHTMLString) {
        
        NSMutableDictionary *messageInfo = [[NSMutableDictionary alloc] init];

        Message *lastMessage;
        NSString *messageTitle;
        NSMutableString *messageMessage;
        if (self.mailMessageInputView.mailMessageInputTextField.text &&
            ![self.mailMessageInputView.mailMessageInputTextField.text isEqualToString:@""]) {
            messageMessage = [[NSMutableString alloc] initWithString:self.mailMessageInputView.mailMessageInputTextField.text];
        }
        if (self.mailMessageResponseHistoryFlag && self.mailMessageHistoryMessage) {
            lastMessage = self.mailMessageHistoryMessage;
            messageTitle = [NSString stringWithFormat:@"回复:%@",lastMessage.messageTitle];
            if (messageMessage) {
                [messageMessage appendString:[self.mailMessageHistoryMessage getMessageContent]];
            } else {
                messageMessage = [[NSMutableString alloc] initWithString:[self.mailMessageHistoryMessage getMessageContent]];
            }
        } else {
            lastMessage = [self.mailMessageFetchController.fetchedObjects lastObject];
            messageTitle = [NSString stringWithFormat:@"%@",lastMessage.messageTitle];
        }

        if (messageTitle) {
            [messageInfo setObject:messageTitle forKey:@"messageTitle"];
        }
        if (messageMessage) {
            [messageInfo setObject:messageMessage forKey:@"messageBody"];
        }
        if (lastMessage.messageId) {
            [messageInfo setObject:lastMessage.messageId forKey:@"messageReferenceId"];
        }
        
        UserSetting *userSetting = [UserSetting defaultSetting];
        [messageInfo setObject:userSetting.emailAddress forKey:@"messageSender"];
        
        NSMutableArray *messageReceiverArray = [[NSMutableArray alloc] initWithArray:[self.session.sessionUsers componentsSeparatedByString:@","]];
        [messageReceiverArray removeObject:userSetting.emailAddress];
        for (NSString *userEmail in messageReceiverArray) {
            User *user = [User getUserWithUserEmail:userEmail context:nil];
            [user changeUserRecentContactFlag:@(1)];
        }
        
        [messageInfo setObject:messageReceiverArray forKey:@"messageReceiver"];
        
        [messageInfo setObject:self.session.sessionId forKey:@"messageSessionId"];
        
        if (attachmentHTMLString) {
            [messageInfo setObject:attachmentHTMLString forKey:@"messageAttachmentHTMLString"];
        }
        
        [[MessageSend shareMessageSend] sendMessage:messageInfo];
        
        self.mailMessageHistoryMessage = nil;
        self.mailMessageResponseHistoryFlag = NO;
        [self.mailAttachmentArray removeAllObjects];
        self.mailMessageInputView.mailMessageInputTextField.text = nil;
        self.mailMessageInputView.mailMessageInputTextField.placeholder = nil;
        self.mailMessageInputView.mailAttachmentAddedButton.hidden = YES;
        [self setMailMessageSendButtonState];
    };
    [attachmentHandle generationAttachmentHTMLString];
}

//#pragma mark keyboardDelegate
-(void)keyboardChange:(NSNotification *)notification {

    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    NSDictionary *userInfo = [notification userInfo];
    NSTimeInterval animationDuration;
    UIViewAnimationCurve animationCurve;
    CGRect ketBoardFrame;

    [[userInfo objectForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&animationCurve];
    [[userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&animationDuration];
    [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] getValue:&ketBoardFrame];

    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:animationDuration];
    [UIView setAnimationCurve:animationCurve];

    [self.view layoutIfNeeded];

    if (notification.name == UIKeyboardWillHideNotification) {
        if (self.mailMessageAttachmentState) {
            CGRect inputNewFrame = self.mailMessageInputView.frame;
            inputNewFrame.origin.y = CGRectGetHeight(self.view.frame)-49-215;
            self.mailMessageInputView.frame = inputNewFrame;
            CGRect chatNewFrame = self.mailMessageTableView.frame;
            chatNewFrame.size.height = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49-215;
            self.mailMessageTableView.frame = chatNewFrame;
        } else {
            CGRect inputNewFrame = self.mailMessageInputView.frame;
            inputNewFrame.origin.y = CGRectGetHeight(self.view.frame)-49;
            self.mailMessageInputView.frame = inputNewFrame;
            CGRect chatNewFrame = self.mailMessageTableView.frame;
            chatNewFrame.size.height = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49;
            self.mailMessageTableView.frame = chatNewFrame;
        }
    } else if (notification.name == UIKeyboardWillShowNotification) {
        CGRect inputNewFrame = self.mailMessageInputView.frame;
        inputNewFrame.origin.y = CGRectGetHeight(self.view.frame)-ketBoardFrame.size.height-49;
        self.mailMessageInputView.frame = inputNewFrame;
        CGRect chatNewFrame = self.mailMessageTableView.frame;
        chatNewFrame.size.height = CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49-ketBoardFrame.size.height;
        self.mailMessageTableView.frame = chatNewFrame;
    }
    [self tableViewScrollToBottom];
    [UIView commitAnimations];
}


#pragma mark NSFetchedResultsControllerDelegate

- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.mailMessageTableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    [self.mailMessageTableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {

    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.mailMessageTableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(tableViewScrollToBottom) userInfo:nil repeats:NO];

            break;
        case NSFetchedResultsChangeDelete:
            [self.mailMessageTableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeUpdate:

            break;
        case NSFetchedResultsChangeMove:
            [self.mailMessageTableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            [self.mailMessageTableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
        default:
            break;
    }
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    if (self.mailMessageFetchController != controller) {
        return ;
    }
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.mailMessageTableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.mailMessageTableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
        default:
            break;
    }
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end

