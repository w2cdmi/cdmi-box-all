//
//  MailContactSearchController.m
//  OneMail
//
//  Created by cse  on 15/11/3.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define SessionUserScrollViewHeight 54
#define UsersViewDistanceLeft 15
#define UsersCellDistanceRight 10
#define UsersCellDistanceTop 8

#define SearchContactTableCellHeight 55

#define SearchContactBarHeight  44

#import "MailContactSearchController.h"
#import <CoreData/CoreData.h>
#import "Session.h"
#import "User.h"
#import "User+Remote.h"
#import "AppDelegate.h"
#import "MailContactNameCard.h"
#import "MailMessageViewController.h"
#import "MailContactSearchCell.h"
//#import "MailContactSearchMoreCell.h"
#import "MessageSend.h"
@interface MailContactSearchController ()<UITextFieldDelegate,UITableViewDelegate,UITableViewDataSource,MailContactSearchDelegate>

@property (nonatomic, strong) Session *session;
@property (nonatomic, strong) NSArray *originalSessionUsers;
@property (nonatomic, strong) NSMutableArray *sessionUsers;
@property (nonatomic, strong) NSMutableArray *sessionNameCard;
@property (nonatomic, strong) UIScrollView *sessionUserScrollView;
@property (nonatomic, strong) UISearchBar *searchContactBar;
@property (nonatomic, strong) UITableView *mailSessionContactTableView;
@property (nonatomic, assign) BOOL mailSessionRecentContactShow;
@property (nonatomic, assign) BOOL mailSessionMyContactShow;
@property (nonatomic, strong) NSMutableArray *mailSessionRecentContactArray;
@property (nonatomic, strong) NSMutableArray * mailSessionMyContactArray;
@property (nonatomic, strong) NSMutableArray * mailSessionSearchArray;
@property (atomic) BOOL isSearching;
@property (atomic) BOOL isShowMoreContact;
@property (nonatomic, strong) Message *forwardMessage;
@property (nonatomic, strong) UITableViewCell *MailContactSearchMoreCell;
@property (nonatomic, strong) UIView *mailSessionSearchView;
@property (nonatomic, strong) UITextField *mailSessionSearchTextField;
@property (nonatomic, strong) UIView *mailSessionSearchBackgroundView;
@property (nonatomic, strong) UIButton *mailSessionSearchClearButton;
@end

@implementation MailContactSearchController
- (id)initWithMessage:(Message *)forwardMessage{
    if (self = [self initWithSession:nil]) {
        self.forwardMessage = forwardMessage;
    }
    return self;
}
- (id)initWithSession:(Session *)session {
    self = [super init];
    if (self) {
        self.session = session;
        self.isSearching = NO;
        self.isShowMoreContact = NO;
        self.sessionUsers = [[NSMutableArray alloc] init];
        if (self.session) {
            NSMutableArray *userEmails = [NSMutableArray arrayWithArray:[session.sessionUsers componentsSeparatedByString:@","]];
            for (NSString *email in userEmails) {
                User *user = [User getUserWithUserEmail:email context:nil];
                if (user) {
                    [self.sessionUsers addObject:user];
                }
            }
        }
        self.originalSessionUsers = [NSArray arrayWithArray:self.sessionUsers];
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        self.mailSessionMyContactArray = [NSMutableArray arrayWithArray:[User getMyContactUsers:delegate.localManager.managedObjectContext]];
        if (self.mailSessionMyContactArray.count > 0) {
            self.mailSessionMyContactShow = YES;
        }
        self.mailSessionRecentContactArray = [NSMutableArray arrayWithArray:[User getRecentContactUsers:delegate.localManager.managedObjectContext]];
        if (self.mailSessionRecentContactArray.count > 0) {
            self.mailSessionRecentContactShow = YES;
        }
        self.mailSessionSearchArray = [[NSMutableArray alloc] init];
    }
    return self;
}


- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.isShowMoreContact = NO;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = NSLocalizedString(@"ContactTitle", nil);
    self.navigationItem.hidesBackButton = YES;
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Confirm", nil) style:UIBarButtonItemStylePlain target:self action:@selector(pushViewController)];
    self.sessionUserScrollView = [[UIScrollView alloc] initWithFrame:CGRectZero];
    self.automaticallyAdjustsScrollViewInsets = NO;
    [self setScrollViewFrame];
    self.sessionUserScrollView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:self.sessionUserScrollView];
    [self.view addSubview:self.mailSessionSearchView];
    self.mailSessionContactTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.mailSessionSearchView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailSessionSearchView.frame)) style:UITableViewStylePlain];
    [self.mailSessionContactTableView registerClass:[MailContactSearchCell class] forCellReuseIdentifier:@"MailContactSearchCell"];
    self.mailSessionContactTableView.delegate = self;
    self.mailSessionContactTableView.dataSource = self;
    self.mailSessionContactTableView.separatorColor = [UIColor clearColor];
    [self.view addSubview:self.mailSessionContactTableView];
    self.sessionNameCard = [[NSMutableArray alloc] init];
    for (User *user in self.sessionUsers) {
        MailContactNameCard *contactNameCard = [[MailContactNameCard alloc] initWithName:user.userName];
        [self.sessionNameCard addObject:contactNameCard];
    }
    [self LayoutNameCards];
}
- (UIView*)mailSessionSearchView {
    if (!_mailSessionSearchView) {
        _mailSessionSearchView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.sessionUserScrollView.frame), CGRectGetWidth(self.view.frame), 44)];
        _mailSessionSearchView.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1];
        _mailSessionSearchView.layer.borderWidth = 0.5;
        _mailSessionSearchView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 11, 22, 22)];
        imageView.image = [UIImage imageNamed:@"ic_contact_search_nor"];
        [_mailSessionSearchView addSubview:imageView];
        
        self.mailSessionSearchClearButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
        self.mailSessionSearchClearButton.imageView.frame = CGRectMake(11, 11, 22, 22);
        [self.mailSessionSearchClearButton setImage:[UIImage imageNamed:@"ic_transfer_delete_nor"] forState:UIControlStateNormal];
        self.mailSessionSearchClearButton.hidden = YES;
        [_mailSessionSearchView addSubview:self.mailSessionSearchClearButton];
        [self.mailSessionSearchClearButton addTarget:self action:@selector(clearText) forControlEvents:UIControlEventTouchUpInside];
        self.mailSessionSearchTextField = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(imageView.frame)+4, 11, CGRectGetWidth(_mailSessionSearchView.frame)-CGRectGetMaxX(imageView.frame)-4-15 - 22,22)];
        //        self.mailSessionSearchTextField.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1];
        self.mailSessionSearchTextField.placeholder = @"Search";
        self.mailSessionSearchTextField.font = [UIFont systemFontOfSize:14.0f];
        self.mailSessionSearchTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.mailSessionSearchTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.mailSessionSearchTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.mailSessionSearchTextField.delegate = self;
        self.mailSessionSearchTextField.returnKeyType = UIReturnKeySearch;
        [self.mailSessionSearchTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [_mailSessionSearchView addSubview:self.mailSessionSearchTextField];
    }
    return _mailSessionSearchView;
}
- (void)clearText{
    self.mailSessionSearchTextField.text = @"";
    self.isSearching = NO;
    self.mailSessionSearchClearButton.hidden = YES;
    [self.mailSessionContactTableView reloadData];
}
- (void)setScrollViewFrame{
    CGRect statusRect = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationRect = self.navigationController.navigationBar.frame;
    CGFloat ScrollViewHeight;
    if (self.sessionUsers.count > 0) {
        ScrollViewHeight = SessionUserScrollViewHeight;
        self.sessionUserScrollView.hidden = NO;
    }
    else{
        ScrollViewHeight = 0;
        self.sessionUserScrollView.hidden = YES;
    }
    self.sessionUserScrollView.frame = CGRectMake(0, CGRectGetHeight(statusRect)+CGRectGetHeight(navigationRect), CGRectGetWidth(self.view.frame), ScrollViewHeight);
    _mailSessionSearchView.frame = CGRectMake(0, CGRectGetMaxY(self.sessionUserScrollView.frame), CGRectGetWidth(self.navigationController.navigationBar.frame), 44);
    self.mailSessionContactTableView.frame = CGRectMake(0, CGRectGetMaxY(self.mailSessionSearchView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailSessionSearchView.frame));
}
- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)pushViewController {
    NSMutableArray *sessionUsersArray = [[NSMutableArray alloc] initWithArray:self.sessionUsers];
    if (sessionUsersArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"ContactNonePrompt", nil)];
        return;
    }
    NSArray *Users = [self GetArrayString:sessionUsersArray];
    
    NSString *sessionUsers = [CommonFunction stringFromArray:Users];
    UserSetting *userSetting = [UserSetting defaultSetting];
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
    if (self.originalSessionUsers.count>0) {
        [ctx performBlockAndWait:^{
            Session *shadow = (Session*)[ctx objectWithID:self.session.objectID];
            shadow.sessionUsers = sessionUsers;
            [ctx save:nil];
        }];
        [self.navigationController popViewControllerAnimated:YES];
    }
    else{
        Session * newSession = [Session getSessionWithSessionUsers:sessionUsers ctx:ctx];
        if (!newSession) {
            newSession = [NSEntityDescription insertNewObjectForEntityForName:@"Session" inManagedObjectContext:ctx];
            newSession.sessionId = [NSString stringWithFormat:@"%@",userSetting.emailNextSessionId];
            userSetting.emailNextSessionId = @(userSetting.emailNextSessionId.integerValue+1);
            newSession.sessionNotification = @(0);
            newSession.sessionSyncDate = [NSDate date];
            newSession.sessionUsers = sessionUsers;
            [ctx save:nil];
        }
        if (self.forwardMessage) {
            [[MessageSend shareMessageSend] forwardMessage:self.forwardMessage Session:newSession];
            NSInteger index = [self.navigationController.viewControllers indexOfObject:self];
            UIViewController *viewController = [self.navigationController.viewControllers objectAtIndex:index - 2];
            [self.navigationController popToViewController:viewController animated:YES];
        }
        else{
            MailMessageViewController *mailChatViewController =[[MailMessageViewController alloc] initWithSession:newSession];
            [self.navigationController pushViewController:mailChatViewController animated:YES];
        }
    }
}

- (void)LayoutNameCards {
    CGFloat start_x = UsersViewDistanceLeft;
    for (UIView *view in self.sessionUserScrollView.subviews) {
        [view removeFromSuperview];
    }
    for (MailContactNameCard *contactNameCard in self.sessionNameCard) {
        CGRect rect = contactNameCard.frame;
        rect.origin.x = start_x;
        start_x = start_x + rect.size.width + UsersCellDistanceRight;
        rect.origin.y = UsersCellDistanceTop;
        contactNameCard.frame = rect;
        [self.sessionUserScrollView addSubview:contactNameCard];
    }
    CGFloat end_x = start_x > self.view.frame.size.width ? start_x:self.view.frame.size.width;
    self.sessionUserScrollView.contentSize = CGSizeMake(end_x, 0);
}

#pragma mark tableview delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    if (self.isSearching) {
        if (self.mailSessionRecentContactArray.count > 0) {
            tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
            return 1;
        } else {
            tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
            return 0;
        }
    } else {
        NSInteger sessionCount = 0;
        if (self.mailSessionRecentContactShow == YES) {
            sessionCount++;
        }
        if (self.mailSessionMyContactShow == YES) {
            sessionCount++;
        }
        if (sessionCount > 0) {
            tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        }
        else{
            tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        }
        return sessionCount;
    }
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    if (self.isSearching) {
        return NSLocalizedString(@"ContactSearchTitle", nil);
    } else {
        if (section == 0) {
            if (self.mailSessionRecentContactShow == YES) {
                return NSLocalizedString(@"ContactRecentTitle", nil);
            }
            else{
                return NSLocalizedString(@"ContactAllTitle", nil);
            }
        }
        if (section == 1) {
            return NSLocalizedString(@"ContactAllTitle", nil);
        }
    }
    return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return SearchContactTableCellHeight;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (self.isSearching) {
        return self.mailSessionSearchArray.count;
    } else {
        if (section == 0) {
            if (self.mailSessionRecentContactShow == YES) {
                if (self.isShowMoreContact == NO && self.mailSessionRecentContactArray.count > 5) {
                    return 6;
                }
                else{
                    return self.mailSessionRecentContactArray.count;
                }
            }
            else{
                return self.mailSessionMyContactArray.count;
            }
        } else {
            return self.mailSessionMyContactArray.count;
        }
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSArray *usersArray = nil;
    if (self.isSearching) {
        usersArray = self.mailSessionSearchArray;
    } else {
        if (indexPath.section == 0) {
            if (self.mailSessionRecentContactShow == YES) {
                usersArray = self.mailSessionRecentContactArray;
            }
            else{
                usersArray = self.mailSessionMyContactArray;
            }
        } else {
            usersArray = self.mailSessionMyContactArray;
        }
    }
    if (!self.isShowMoreContact && !self.isSearching && indexPath.section == 0 && indexPath.row == 5 && self.mailSessionRecentContactShow == YES) {
        return self.MailContactSearchMoreCell;
    } else {
        MailContactSearchCell *cell = [tableView dequeueReusableCellWithIdentifier:@"MailContactSearchCell"];
        if (!cell) {
            cell = [[MailContactSearchCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"MailContactSearchCell"];
        }
        User *user = [usersArray objectAtIndex:indexPath.row];
        cell.user = user;
        cell.delegate = self;
        if ([self.sessionUsers containsObject:user]) {
            cell.contactSelected = YES;
        } else {
            cell.contactSelected = NO;
        }
        return cell;
    }
}

- (UITableViewCell *)MailContactSearchMoreCell{
    if (!_MailContactSearchMoreCell) {
        _MailContactSearchMoreCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"more"];
        if (_MailContactSearchMoreCell) {
            UIButton *contactMore = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, _MailContactSearchMoreCell.frame.size.height)];
            [contactMore setTitle:NSLocalizedString(@"ContactMore", nil) forState:UIControlStateNormal];
            [contactMore setTitleColor:[UIColor colorWithRed:0 green:139/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
            contactMore.titleLabel.font = [UIFont systemFontOfSize:15];
            contactMore.titleLabel.textAlignment = NSTextAlignmentCenter;
            [_MailContactSearchMoreCell.contentView addSubview:contactMore];
            [contactMore addTarget:self action:@selector(showMoreContact) forControlEvents:UIControlEventTouchUpInside];
            
        }
    }
    return _MailContactSearchMoreCell;
}




#pragma mark searchBar delegate
- (void)textFieldDidChange:(UITextField *)textField{
    if ([textField.text isEqualToString:@""]) {
        self.isSearching = NO;
        [self.mailSessionContactTableView reloadData];
    }
    else{
        self.mailSessionSearchClearButton.hidden = NO;
    }
}
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.isSearching = YES;
    [self.mailSessionSearchArray removeAllObjects];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    if ([textField.text isEqualToString:@""]) {
        return;
    }
    [User searchUser:textField.text succeed:^(id retobj) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(userEmail contains[cd] %@ OR userName contains[cd] %@ OR userLoginName contains [cd] %@) AND userCloudId != %@",textField.text,textField.text,textField.text,delegate.localManager.userCloudId];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:delegate.localManager.managedObjectContext];
        NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"userSortTimeKey" ascending:YES];
        [request setSortDescriptors:@[sort]];
        [request setEntity:entity];
        [request setPredicate:predicate];
        self.mailSessionSearchArray = [NSMutableArray arrayWithArray:[delegate.localManager.managedObjectContext executeFetchRequest:request error:nil]];
        [self.mailSessionContactTableView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"ContactSearchFailed", nil)];
    }];
}

#pragma mark MailContactSearch delegate
- (void) selectMailContactUser:(User *)user {
    if (![self.sessionUsers containsObject:user]) {
        NSString *name = user.userName;
        MailContactNameCard *namecard = [[MailContactNameCard alloc] initWithName:name];
        [self.sessionUsers addObject:user];
        [self.sessionNameCard addObject:namecard];
        [self LayoutNameCards];
        [self setScrollViewFrame];
    }
}

- (void) deselectMailContactUser:(User *)user {
    MailContactNameCard *tempcard;
    for (MailContactNameCard *namecard in self.sessionNameCard) {
        if ([namecard.cardName isEqualToString:user.userName]) {
            tempcard = namecard;
        }
    }
    [self.sessionUsers removeObject:user];
    [self.sessionNameCard removeObject:tempcard];
    [self LayoutNameCards];
    [self setScrollViewFrame];
}

- (void)showMoreContact {
    self.isShowMoreContact = YES;
    [self.mailSessionContactTableView reloadData];
}



- (NSArray *)GetArrayString:(NSArray *)userSource{
    NSMutableArray *result = [[NSMutableArray alloc] init];
    for (User *user in userSource) {
        if (user.userEmail) {
            [result addObject:user.userEmail];
        }
    }
    UserSetting *userSetting = [UserSetting defaultSetting];
    [result addObject:userSetting.emailAddress];
    return result;
}
@end
