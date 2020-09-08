//
//  MailContactViewController.m
//  OneMail
//
//  Created by cse  on 16/1/26.
//  Copyright (c) 2016年 cse. All rights reserved.
//

//
//  mailContactUserViewController.m
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailContactViewController.h"
#import "AppDelegate.h"
#import <CoreData/CoreData.h>
#import "File+Remote.h"
#import "User+Remote.h"
#import "MailMessageViewController.h"
#import "Message.h"
#import "MessageSend.h"
@implementation MailContactUserListCard

- (id)initWithUserEmail:(NSString *)userEmail {
    self = [super init];
    if (self) {
        self.userEmail = userEmail;
        User *user = [User getUserWithUserEmail:userEmail context:nil];
        self.layer.cornerRadius = 4;
        self.layer.masksToBounds = YES;
        self.backgroundColor = [CommonFunction colorWithString:@"e5e5e5" alpha:1.0f];
        CGSize adjustNameSize = [CommonFunction labelSizeWithString:user.userName font:[UIFont systemFontOfSize:14.0f]];
        self.frame = CGRectMake(0, 0, 10+adjustNameSize.width+10, 36);
        [self setAttributedTitle:[[NSAttributedString alloc] initWithString:user.userName attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"000000" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
        [self addTarget:self action:@selector(deleteUser) forControlEvents:UIControlEventTouchUpInside];
    }
    return self;
}

- (void)deleteUser {
    if ([self.delegate respondsToSelector:@selector(deleteListUser:)]) {
        [self.delegate deleteListUser:self.userEmail];
    }
}

@end



@interface MailContactUserSearchCell ()

@property (nonatomic, strong) UIImageView *mailContactUserIconView;
@property (nonatomic, strong) UILabel     *mailContactUserNameLabel;
@property (nonatomic, strong) UILabel     *mailContactUserEmailLabel;
@property (nonatomic, strong) UIButton    *mailContactUserCheckButton;

@end

@implementation MailContactUserSearchCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UIImageView *cloudFileShareUserBackgroundImageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 5.5, 44, 44)];
        cloudFileShareUserBackgroundImageView.image = [UIImage imageNamed:@"img_user_frame"];
        [self.contentView addSubview:cloudFileShareUserBackgroundImageView];
        
        self.mailContactUserIconView = [[UIImageView alloc] initWithFrame:CGRectMake(2, 2, 40, 40)];
        [cloudFileShareUserBackgroundImageView addSubview:self.mailContactUserIconView];
        
        self.mailContactUserNameLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.mailContactUserNameLabel];
        
        self.mailContactUserEmailLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.mailContactUserEmailLabel];
        
        self.mailContactUserCheckButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.mailContactUserCheckButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_transfer_delete_nor"] forState:UIControlStateNormal];
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_transfer_delete_press"] forState:UIControlStateHighlighted];
        [self.mailContactUserCheckButton addTarget:self action:@selector(shareUserControl) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.mailContactUserCheckButton];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    User *user = [User getUserWithUserEmail:self.userEmail context:nil];
    
    self.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    
    self.mailContactUserIconView.image = [UIImage imageNamed:@"img_user_default"];
    
    self.mailContactUserNameLabel.text = user.userName;
    self.mailContactUserNameLabel.frame = CGRectMake(15+44+10, 8, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 20);
    
    self.mailContactUserEmailLabel.text = user.userEmail;
    self.mailContactUserEmailLabel.frame = CGRectMake(15+44+10, CGRectGetMaxY(self.mailContactUserNameLabel.frame)+4, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 15);
    
    self.mailContactUserCheckButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-42,(CGRectGetHeight(self.frame)-42)/2, 42, 42);
    
    if (_selectState) {
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    }
}

- (void)setSelectState:(BOOL)selectState {
    _selectState = selectState;
    if (_selectState) {
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    }
}

- (void)shareUserControl {
    if (self.selectState) {
        self.selectState = NO;
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
        if ([self.delegate respondsToSelector:@selector(deleteMailUser:)]) {
            [self.delegate deleteMailUser:self.userEmail];
        }
    } else {
        self.selectState = YES;
        [self.mailContactUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
        if ([self.delegate respondsToSelector:@selector(addMailUser:)]) {
            [self.delegate addMailUser:self.userEmail];
        }
    }
    
}

- (void)prepareForReuse {
    self.mailContactUserIconView.image = nil;
    self.mailContactUserNameLabel.text = nil;
    self.mailContactUserEmailLabel.text = nil;
}

@end



@interface MailContactViewController ()<UITextFieldDelegate,UITableViewDataSource,UITableViewDelegate,MailContactUserSearchDelegate,MailContactUserListCardDelegate>

@property (nonatomic, strong) Session *session;
@property (nonatomic, strong) NSMutableArray  *mailContactUserArray;
@property (nonatomic, strong) NSMutableArray  *mailContactUserCardArray;

@property (nonatomic, strong) UILabel         *mailContactSearchTitleLabel;
@property (nonatomic, strong) UIButton        *mailContactSearchBackButton;
@property (nonatomic, strong) UIButton        *mailContactSearchConfirmButton;

@property (nonatomic, strong) UIView          *mailContactSearchTextView;
@property (nonatomic, strong) UIScrollView    *mailContactSearchUserListView;
@property (nonatomic, strong) UIImageView     *mailContactSearchIconView;
@property (nonatomic, strong) UITextField     *mailContactSearchTextField;
@property (nonatomic, strong) UIButton        *mailContactSearchClearButton;
@property (nonatomic, strong) UIView          *mailContactSearchBackgroundView;
@property (nonatomic, strong) UIView          *mailContactSearchIndicatorView;
@property (nonatomic, strong) UIActivityIndicatorView *mailContactSearchIndicator;

@property (nonatomic, strong) UITableView     *mailContactSearchTableView;
@property (nonatomic, strong) NSArray         *mailContactSearchUserArray;
@property (nonatomic, strong) NSArray         *mailContactRecentContactUserArray;
@property (nonatomic, strong) NSMutableArray  *mailContactMyContactUserArray;
@property (nonatomic, strong) UITableViewCell *mailContactRecentContactDisplayCell;

@property (nonatomic, assign) BOOL             mailContactSearchState;
@property (nonatomic, assign) BOOL             mailContactRecentContactShow;
@property (nonatomic, assign) BOOL             mailContactMyContactShow;
@property (nonatomic, assign) BOOL             mailContactRecentContactDisplayState;

@property (nonatomic,strong) Message         *forwardMessage;
@property (nonatomic,strong) NSString        *shareLink;
@end

@implementation MailContactViewController
- (id)initWithMessage:(Message *)forwardMessage{
    if (self = [self init]) {
        self.forwardMessage = forwardMessage;
    }
    return self;
}
- (id)initWithShareLink:(NSString *)shareLink{
    if (self = [self init]) {
        self.shareLink = shareLink;
    }
    return self;
}
- (id)init{
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        
        self.mailContactSearchState = NO;
        self.mailContactRecentContactDisplayState = NO;
    
        self.mailContactUserArray = [[NSMutableArray alloc] init];
        self.mailContactUserCardArray = [[NSMutableArray alloc] init];
        self.mailContactRecentContactUserArray = [User getRecentContactUsers:nil];
        if (self.mailContactRecentContactUserArray.count > 0) {
            self.mailContactRecentContactShow = YES;
        }
        
        self.mailContactMyContactUserArray = [NSMutableArray arrayWithArray:[User getMyContactUsers:nil]];
        [self.mailContactMyContactUserArray removeObjectsInArray:self.mailContactRecentContactUserArray];
        if (self.mailContactMyContactUserArray.count > 0) {
            self.mailContactMyContactShow = YES;
        }
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.mailContactSearchTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.mailContactSearchTitleLabel.font = [UIFont boldSystemFontOfSize:18.0f];
    self.mailContactSearchTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.mailContactSearchTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.mailContactSearchTitleLabel.text = NSLocalizedString(@"CloudSharedUserAddTitle", nil);
    
    self.mailContactSearchBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailContactSearchBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailContactSearchBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailContactSearchBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailContactSearchBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    
    CGSize adjustLabelSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"Confirm", nil) font:[UIFont systemFontOfSize:17.0f]];
    self.mailContactSearchConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15- adjustLabelSize.width, 0, adjustLabelSize.width, 44)];
    [self.mailContactSearchConfirmButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"Confirm", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.mailContactSearchConfirmButton addTarget:self action:@selector(confirm) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:self.mailContactSearchTextView];
    
    self.mailContactSearchTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    [self.mailContactSearchTableView registerClass:[MailContactUserSearchCell class] forCellReuseIdentifier:@"CloudFileShareUserSearchCell"];
    self.mailContactSearchTableView.backgroundColor = [UIColor clearColor];
    self.mailContactSearchTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.mailContactSearchTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.mailContactSearchTableView.delegate = self;
    self.mailContactSearchTableView.dataSource = self;
    self.mailContactSearchTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.mailContactSearchTableView];
    [self.mailContactSearchTableView reloadData];
    
    [self searchTextViewUserListStatus];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.mailContactSearchTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailContactSearchBackButton];
    [self.navigationController.navigationBar addSubview:self.mailContactSearchConfirmButton];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailContactSearchTitleLabel removeFromSuperview];
    [self.mailContactSearchBackButton removeFromSuperview];
    [self.mailContactSearchConfirmButton removeFromSuperview];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)confirm {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (self.mailContactUserArray.count == 0) {
        return;
    }
    NSMutableArray *contactNameArray = [[NSMutableArray alloc] init];
    for (NSString *userEmail in self.mailContactUserArray) {
        User *user = [User getUserWithUserEmail:userEmail context:nil];
        [contactNameArray addObject:user.userName];
    }
    NSString *contactEmailString = [CommonFunction stringFromArray:contactNameArray];
    [self.mailContactUserArray addObject:userSetting.emailAddress];
    NSString *sessionUserString = [CommonFunction stringFromArray:self.mailContactUserArray];
    __block Session *session = [Session getSessionWithSessionUsers:sessionUserString ctx:nil];
    
    if (!session) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
        [ctx performBlockAndWait:^{
            session = [Session sessionInsertWithSessionId:[userSetting.emailNextSessionId stringValue] ctx:ctx];
            userSetting.emailNextSessionId = @(userSetting.emailNextSessionId.integerValue+1);
            session.sessionUsers = sessionUserString;
            session.sessionTitle = contactEmailString;
            [ctx save:nil];
        }];
    }
    if (self.forwardMessage) {
        [[MessageSend shareMessageSend] forwardMessage:self.forwardMessage Session:session];
        NSInteger index = [self.navigationController.viewControllers indexOfObject:self];
        UIViewController *viewController = [self.navigationController.viewControllers objectAtIndex:index - 2];
        [self.navigationController popToViewController:viewController animated:YES];
    }else if (self.shareLink){
        [[MessageSend shareMessageSend] forwardShareLink:self.shareLink Session:session];
        NSInteger index = [self.navigationController.viewControllers indexOfObject:self];
        UIViewController *viewController = [self.navigationController.viewControllers objectAtIndex:index - 2];
        [self.navigationController popToViewController:viewController animated:YES];
    }

    else{
    MailMessageViewController *mailChatViewController =[[MailMessageViewController alloc] initWithSession:session];
    [self.navigationController pushViewController:mailChatViewController animated:YES];
    }
}

#pragma mark cloudFileShareUserSearchDelegate
- (void)addMailUser:(NSString *)userEmail {
    if (![self.mailContactUserArray containsObject:userEmail]) {
        MailContactUserListCard *card = [[MailContactUserListCard alloc] initWithUserEmail:userEmail];
        card.delegate = self;
        [self.mailContactUserCardArray addObject:card];
        [self.mailContactUserArray addObject:userEmail];
        [self searchTextViewUserListStatus];
    }
}

- (void)deleteMailUser:(NSString *)userEmail {
    if ([self.mailContactUserArray containsObject:userEmail]) {
        MailContactUserListCard *removeCard;
        for (MailContactUserListCard *card in self.mailContactUserCardArray) {
            if ([card.userEmail isEqualToString:userEmail]) {
                removeCard = card;break;
            }
        }
        [self.mailContactUserCardArray removeObject:removeCard];
        [self.mailContactUserArray removeObject:userEmail];
        [self searchTextViewUserListStatus];
    }
}

#pragma mark search textField
- (UIView*)mailContactSearchBackgroundView {
    if (!_mailContactSearchBackgroundView) {
        _mailContactSearchBackgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.mailContactSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailContactSearchTextView.frame))];
        _mailContactSearchBackgroundView.backgroundColor = [UIColor clearColor];
    }
    return _mailContactSearchBackgroundView;
}

- (void)mailContactSearchBackgroundViewShow {
    if (!self.mailContactSearchBackgroundView.superview) {
        [self.view addSubview:self.mailContactSearchBackgroundView];
    }
}

- (void)mailContactSearchBackgroundViewHide {
    if (self.mailContactSearchBackgroundView.superview) {
        [self.mailContactSearchBackgroundView removeFromSuperview];
    }
}

- (UIView*)mailContactSearchIndicatorView {
    if (!_mailContactSearchIndicatorView) {
        _mailContactSearchIndicatorView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.mailContactSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailContactSearchTextView.frame))];
        _mailContactSearchIndicatorView.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.3];
        UIActivityIndicatorView *mailContactSearchIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake((CGRectGetWidth(_mailContactSearchIndicatorView.frame)-50)/2, (CGRectGetHeight(_mailContactSearchIndicatorView.frame)-50)/2, 50, 50)];
        mailContactSearchIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        [mailContactSearchIndicator startAnimating];
        [_mailContactSearchIndicatorView addSubview:mailContactSearchIndicator];
    }
    return _mailContactSearchIndicatorView;
}

- (void)mailContactSearchIndicatorViewShow {
    if (!self.mailContactSearchIndicator.superview) {
        [self.mailContactSearchTextView addSubview:self.mailContactSearchIndicator];
        self.mailContactSearchIconView.hidden = YES;
    }
}

- (void)mailContactSearchIndicatorViewHide {
    if (self.mailContactSearchIndicator.superview) {
        [self.mailContactSearchIndicator removeFromSuperview];
        self.mailContactSearchIconView.hidden = NO;
    }
}

- (UIView*)mailContactSearchTextView {
    if (!_mailContactSearchTextView) {
        _mailContactSearchTextView = [[UIView alloc] initWithFrame:CGRectZero];
        _mailContactSearchTextView.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
        _mailContactSearchTextView.layer.borderWidth = 0.5;
        _mailContactSearchTextView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        
        self.mailContactSearchUserListView = [[UIScrollView alloc] initWithFrame:CGRectZero];
        self.mailContactSearchUserListView.showsHorizontalScrollIndicator = NO;
        [_mailContactSearchTextView addSubview:self.mailContactSearchUserListView];
        
        self.mailContactSearchIconView = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.mailContactSearchIconView.image = [UIImage imageNamed:@"ic_contact_search_nor"];
        [_mailContactSearchTextView addSubview:self.mailContactSearchIconView];
        
        self.mailContactSearchIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectZero];
        self.mailContactSearchIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
        [self.mailContactSearchIndicator startAnimating];
        
        self.mailContactSearchClearButton = [[UIButton alloc] initWithFrame:CGRectZero];
        self.mailContactSearchClearButton.imageView.frame = CGRectZero;
        [self.mailContactSearchClearButton setImage:[UIImage imageNamed:@"ic_contact_clear_nor"] forState:UIControlStateNormal];
        self.mailContactSearchClearButton.hidden = YES;
        [self.mailContactSearchClearButton addTarget:self action:@selector(searchTextFieldClear) forControlEvents:UIControlEventTouchUpInside];
        [_mailContactSearchTextView addSubview:self.mailContactSearchClearButton];
        
        self.mailContactSearchTextField = [[UITextField alloc] initWithFrame:CGRectZero];
        [self.mailContactSearchTextField setAttributedPlaceholder:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"Search", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"999999" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}]];
        self.mailContactSearchTextField.font = [UIFont systemFontOfSize:14.0f];
        self.mailContactSearchTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.mailContactSearchTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.mailContactSearchTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.mailContactSearchTextField.delegate = self;
        self.mailContactSearchTextField.returnKeyType = UIReturnKeySearch;
        [self.mailContactSearchTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [_mailContactSearchTextView addSubview:self.mailContactSearchTextField];
    }
    return _mailContactSearchTextView;
}

- (void)searchTextFieldClear {
    self.mailContactSearchTextField.text = nil;
    [self.mailContactSearchTextField becomeFirstResponder];
}

- (void)searchTextViewUserListStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    if (self.mailContactUserArray.count > 0) {
        self.mailContactSearchTextView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 8+36+10+22+11);
        self.mailContactSearchUserListView.frame = CGRectMake(0, 8, CGRectGetWidth(self.mailContactSearchTextView.frame), 36);
        self.mailContactSearchIconView.frame = CGRectMake(15, CGRectGetMaxY(self.mailContactSearchUserListView.frame)+10, 22, 22);
        self.mailContactSearchIndicator.frame = self.mailContactSearchIconView.frame;
        self.mailContactSearchTextField.frame = CGRectMake(15+22+4, CGRectGetMaxY(self.mailContactSearchUserListView.frame)+10, CGRectGetWidth(self.view.frame)-15-22-4-4-44-4, 22);
        self.mailContactSearchClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-44, CGRectGetMaxY(self.mailContactSearchUserListView.frame), 44, 44);
        self.mailContactSearchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.mailContactSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailContactSearchTextView.frame));
    } else {
        self.mailContactSearchTextView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 44);
        self.mailContactSearchUserListView.frame = CGRectZero;
        self.mailContactSearchIconView.frame = CGRectMake(15, 11, 22, 22);
        self.mailContactSearchIndicator.frame = self.mailContactSearchIconView.frame;
        self.mailContactSearchTextField.frame = CGRectMake(15+22+4, 11, CGRectGetWidth(self.view.frame)-15-22-4-4-44-4, 22);
        self.mailContactSearchClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44);
        self.mailContactSearchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.mailContactSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailContactSearchTextView.frame));
    }
    [self cloudFileShareUserList];
    
}

- (void)cloudFileShareUserList {
    CGFloat start_x = 15;
    for (UIView *view in self.mailContactSearchUserListView.subviews) {
        [view removeFromSuperview];
    }
    for (MailContactUserListCard *card in self.mailContactUserCardArray) {
        CGRect rect = card.frame;
        rect.origin.x = start_x;
        rect.origin.y = 0;
        start_x = start_x + rect.size.width + 10;
        card.frame = rect;
        [self.mailContactSearchUserListView addSubview:card];
    }
    CGFloat end_x = start_x > CGRectGetWidth(self.view.frame) ? start_x : CGRectGetWidth(self.view.frame);
    self.mailContactSearchUserListView.contentSize = CGSizeMake(end_x, 0);
    [self.mailContactSearchUserListView setContentOffset:CGPointMake(end_x-CGRectGetWidth(self.view.frame), 0) animated:YES];
}

- (void)deleteListUser:(NSString *)userEmail {
    if ([self.mailContactUserArray containsObject:userEmail]) {
        MailContactUserListCard *removeCard;
        for (MailContactUserListCard *card in self.mailContactUserCardArray) {
            if ([card.userEmail isEqualToString:userEmail]) {
                removeCard = card;break;
            }
        }
        [self.mailContactUserCardArray removeObject:removeCard];
        [self searchTextViewUserListStatus];
        
        for (MailContactUserSearchCell *cell in self.mailContactSearchTableView.visibleCells) {
            if ([cell.userEmail isEqualToString:userEmail]) {
                cell.selectState = NO;
            }
        }
    }
}

#pragma mark textFiled delegate
- (void)textFieldDidChange:(UITextField*)textField {
    if (textField.text.length == 0) {
        self.mailContactSearchState = NO;
        self.mailContactSearchClearButton.hidden = YES;
    } else {
        self.mailContactSearchState = YES;
        self.mailContactSearchClearButton.hidden = NO;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [self mailContactSearchBackgroundViewShow];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [self mailContactSearchBackgroundViewHide];
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [self mailContactSearchBackgroundViewHide];
    if ([textField.text isEqualToString:@""]) {
        return;
    }
    [self mailContactSearchIndicatorViewShow];
    [User searchUser:textField.text succeed:^(id retobj) {
        [self mailContactSearchIndicatorViewHide];
        self.mailContactSearchUserArray = [User getUserHasEmailArrayWithKey:textField.text context:nil];
        [self.mailContactSearchTableView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        [self mailContactSearchIndicatorViewHide];
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"ContactSearchFailed", nil)];
        });
    }];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self mailContactSearchBackgroundViewHide];
    [self.mailContactSearchTextField resignFirstResponder];
}

#pragma maek tableView display cell
- (UITableViewCell*)mailContactRecentContactDisplayCell {
    if (!_mailContactRecentContactDisplayCell) {
        _mailContactRecentContactDisplayCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _mailContactRecentContactDisplayCell.textLabel.frame = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 55);
        _mailContactRecentContactDisplayCell.textLabel.font = [UIFont systemFontOfSize:15.0f];
        _mailContactRecentContactDisplayCell.textLabel.textColor = [CommonFunction colorWithString:@"008be3" alpha:1.0f];
        _mailContactRecentContactDisplayCell.textLabel.textAlignment = NSTextAlignmentCenter;
        _mailContactRecentContactDisplayCell.textLabel.text = NSLocalizedString(@"More", nil);
    }
    return _mailContactRecentContactDisplayCell;
}

#pragma mark tableView delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.mailContactSearchState) {
        if (self.mailContactSearchUserArray.count == 0) {
            return 0;
        } else {
            return 1;
        }
    } else {
        NSInteger sectionNumber = 0;
        if (self.mailContactRecentContactShow) {
            sectionNumber = sectionNumber + 1;
        }
        if (self.mailContactMyContactShow) {
            sectionNumber = sectionNumber + 1;
        }
        return sectionNumber;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.mailContactSearchState) {
        return self.mailContactSearchUserArray.count;
    } else {
        if (section == 0) {
            if (self.mailContactRecentContactShow) {
                if (self.mailContactRecentContactUserArray.count > 5 && !self.mailContactRecentContactDisplayState) {
                    return 6;
                } else {
                    return self.mailContactRecentContactUserArray.count;
                }
            } else {
                return self.mailContactMyContactUserArray.count;
            }
        }
        if (section == 1) {
            return self.mailContactMyContactUserArray.count;
        }
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 55.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 22.0f;
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (self.mailContactSearchState) {
        return [CommonFunction tableViewHeaderWithTitle:NSLocalizedString(@"ContactSearchTitle", nil)];
    } else {
        if (section == 0) {
            if (self.mailContactRecentContactShow) {
                return [CommonFunction tableViewHeaderWithTitle:NSLocalizedString(@"ContactRecentTitle", nil)];
            } else {
                return [CommonFunction tableViewHeaderWithTitle:NSLocalizedString(@"ContactAllTitle", nil)];
            }
        }
        if (section == 1) {
            return [CommonFunction tableViewHeaderWithTitle:NSLocalizedString(@"ContactAllTitle", nil)];
        }
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MailContactUserSearchCell *cell = (MailContactUserSearchCell*)[tableView dequeueReusableCellWithIdentifier:@"mailContactUserSearchCell"];
    if (!cell) {
        cell = [[MailContactUserSearchCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"mailContactUserSearchCell"];
    }
    if (self.mailContactSearchState) {
        User *user = [self.mailContactSearchUserArray objectAtIndex:indexPath.row];
        cell.userEmail = user.userEmail;
    } else {
        if (indexPath.section == 0) {
            if (self.mailContactRecentContactShow) {
                if (indexPath.row == 5 && !self.mailContactRecentContactDisplayState) {
                    return self.mailContactRecentContactDisplayCell;
                } else {
                    User *user = [self.mailContactRecentContactUserArray objectAtIndex:indexPath.row];
                    cell.userEmail = user.userEmail;
                }
            } else {
                User *user = [self.mailContactRecentContactUserArray objectAtIndex:indexPath.row];
                cell.userEmail = user.userEmail;
            }
        }
        if (indexPath.section == 1) {
            User *user = [self.mailContactRecentContactUserArray objectAtIndex:indexPath.row];
            cell.userEmail = user.userEmail;
        }
    }
    if ([self.mailContactUserArray containsObject:cell.userEmail]) {
        cell.selectState = YES;
    } else {
        cell.selectState = NO;
    }
    cell.delegate = self;
    return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        if (indexPath.row + 1 == [tableView numberOfRowsInSection:indexPath.section]) {
            if (indexPath.section + 1 == [tableView numberOfSections]) {
                [cell setSeparatorInset:UIEdgeInsetsZero];
            } else {
                [cell setSeparatorInset:UIEdgeInsetsMake(0, CGRectGetWidth(self.view.frame)/2, 0, CGRectGetWidth(self.view.frame)/2)];
            }
        } else {
            [cell setSeparatorInset:UIEdgeInsetsMake(0, 15, 0, 15)];
        }
    }
//    float systemVersion = [[UIDevice currentDevice] systemVersion].floatValue;
//    if (systemVersion >= 8.4) {
//        if ([cell respondsToSelector:@selector(setPreservesSuperviewLayoutMargins:)]) {
//            [cell setPreservesSuperviewLayoutMargins:NO];
//        }
//        if ([cell respondsToSelector:@selector(setLayoutMargins:)]) {
//            [cell setLayoutMargins:UIEdgeInsetsZero];
//        }
//    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    if (cell == self.mailContactRecentContactDisplayCell) {
        self.mailContactRecentContactDisplayState = YES;
        [self.mailContactSearchTableView reloadData];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

@end
