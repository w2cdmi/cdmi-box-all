//
//  MailManagerUserViewController.m
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailManagerUserViewController.h"
#import "AppDelegate.h"
#import <CoreData/CoreData.h>
#import "File+Remote.h"
#import "User+Remote.h"


@implementation MailManagerUserListCard

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
    if ([self.delegate respondsToSelector:@selector(deleteMailListUser:)]) {
        [self.delegate deleteMailListUser:self.userEmail];
    }
}

@end



@interface MailManagerUserSearchCell ()

@property (nonatomic, strong) UIImageView *mailManagerUserIconView;
@property (nonatomic, strong) UILabel     *mailManagerUserNameLabel;
@property (nonatomic, strong) UILabel     *mailManagerUserEmailLabel;
@property (nonatomic, strong) UIButton    *mailManagerUserCheckButton;

@end

@implementation MailManagerUserSearchCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UIImageView *cloudFileShareUserBackgroundImageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 5.5, 44, 44)];
        cloudFileShareUserBackgroundImageView.image = [UIImage imageNamed:@"img_user_frame"];
        [self.contentView addSubview:cloudFileShareUserBackgroundImageView];
        
        self.mailManagerUserIconView = [[UIImageView alloc] initWithFrame:CGRectMake(2, 2, 40, 40)];
        [cloudFileShareUserBackgroundImageView addSubview:self.mailManagerUserIconView];
        
        self.mailManagerUserNameLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.mailManagerUserNameLabel];
        
        self.mailManagerUserEmailLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.mailManagerUserEmailLabel];
        
        self.mailManagerUserCheckButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.mailManagerUserCheckButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_transfer_delete_nor"] forState:UIControlStateNormal];
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_transfer_delete_press"] forState:UIControlStateHighlighted];
        [self.mailManagerUserCheckButton addTarget:self action:@selector(shareUserControl) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.mailManagerUserCheckButton];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    User *user = [User getUserWithUserEmail:self.userEmail context:nil];
    
    self.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    
    self.mailManagerUserIconView.image = [UIImage imageNamed:@"img_user_default"];
    
    self.mailManagerUserNameLabel.text = user.userName;
    self.mailManagerUserNameLabel.frame = CGRectMake(15+44+10, 8, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 20);
    
    self.mailManagerUserEmailLabel.text = user.userEmail;
    self.mailManagerUserEmailLabel.frame = CGRectMake(15+44+10, CGRectGetMaxY(self.mailManagerUserNameLabel.frame)+4, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 15);
    
    self.mailManagerUserCheckButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-42,(CGRectGetHeight(self.frame)-42)/2, 42, 42);
    
    if (_selectState) {
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    }
}

- (void)setSelectState:(BOOL)selectState {
    _selectState = selectState;
    if (_selectState) {
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
    }
}

- (void)shareUserControl {
    if (self.selectState) {
        self.selectState = NO;
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_select_off_nor"] forState:UIControlStateNormal];
        if ([self.delegate respondsToSelector:@selector(deleteMailUser:)]) {
            [self.delegate deleteMailUser:self.userEmail];
        }
    } else {
        self.selectState = YES;
        [self.mailManagerUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
        if ([self.delegate respondsToSelector:@selector(addMailUser:)]) {
            [self.delegate addMailUser:self.userEmail];
        }
    }
    
}

- (void)prepareForReuse {
    self.mailManagerUserIconView.image = nil;
    self.mailManagerUserNameLabel.text = nil;
    self.mailManagerUserEmailLabel.text = nil;
}

@end



@interface MailManagerUserViewController ()<UITextFieldDelegate,UITableViewDataSource,UITableViewDelegate,MailManagerUserSearchDelegate,MailManagerUserListCardDelegate>

@property (nonatomic, strong) Session *session;
@property (nonatomic, strong) NSMutableArray  *mailManagerUserArray;
@property (nonatomic, strong) NSMutableArray  *mailManagerUserCardArray;

@property (nonatomic, strong) UILabel         *mailManagerSearchTitleLabel;
@property (nonatomic, strong) UIButton        *mailManagerSearchBackButton;
@property (nonatomic, strong) UIButton        *mailManagerSearchConfirmButton;

@property (nonatomic, strong) UIView          *mailManagerSearchTextView;
@property (nonatomic, strong) UIScrollView    *mailManagerSearchUserListView;
@property (nonatomic, strong) UIImageView     *mailManagerSearchIconView;
@property (nonatomic, strong) UITextField     *mailManagerSearchTextField;
@property (nonatomic, strong) UIButton        *mailManagerSearchClearButton;
@property (nonatomic, strong) UIView          *mailManagerSearchBackgroundView;
@property (nonatomic, strong) UIView          *mailManagerSearchIndicatorView;
@property (nonatomic, strong) UIActivityIndicatorView *mailManagerSearchIndicator;

@property (nonatomic, strong) UITableView     *mailManagerSearchTableView;
@property (nonatomic, strong) NSArray         *mailManagerSearchUserArray;
@property (nonatomic, strong) NSArray         *mailManagerRecentContactUserArray;
@property (nonatomic, strong) NSMutableArray  *mailManagerMyContactUserArray;
@property (nonatomic, strong) UITableViewCell *mailManagerRecentContactDisplayCell;

@property (nonatomic, assign) BOOL             mailManagerSearchState;
@property (nonatomic, assign) BOOL             mailManagerRecentContactShow;
@property (nonatomic, assign) BOOL             mailManagerMyContactShow;
@property (nonatomic, assign) BOOL             mailManagerRecentContactDisplayState;

@end

@implementation MailManagerUserViewController

- (id)initWithSession:(Session *)session {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.session = session;
        
        self.mailManagerSearchState = NO;
        self.mailManagerRecentContactDisplayState = NO;
        
        NSArray *userArray = [session.sessionUsers componentsSeparatedByString:@","];
        self.mailManagerUserArray = [[NSMutableArray alloc] initWithArray:userArray];
        [self.mailManagerUserArray removeObject:[[UserSetting defaultSetting] emailAddress]];
        self.mailManagerUserCardArray = [[NSMutableArray alloc] init];
        for (NSString *userEmail in self.mailManagerUserArray) {
            MailManagerUserListCard *card = [[MailManagerUserListCard alloc] initWithUserEmail:userEmail];
            card.delegate = self;
            [self.mailManagerUserCardArray addObject:card];
        }
        
        self.mailManagerRecentContactUserArray = [User getRecentContactUsers:nil];
        if (self.mailManagerRecentContactUserArray.count > 0) {
            self.mailManagerRecentContactShow = YES;
        }
        
        self.mailManagerMyContactUserArray = [NSMutableArray arrayWithArray:[User getMyContactUsers:nil]];
        [self.mailManagerMyContactUserArray removeObjectsInArray:self.mailManagerRecentContactUserArray];
        if (self.mailManagerMyContactUserArray.count > 0) {
            self.mailManagerMyContactShow = YES;
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
    
    self.mailManagerSearchTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.mailManagerSearchTitleLabel.font = [UIFont boldSystemFontOfSize:18.0f];
    self.mailManagerSearchTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.mailManagerSearchTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.mailManagerSearchTitleLabel.text = NSLocalizedString(@"CloudSharedUserAddTitle", nil);
    
    self.mailManagerSearchBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailManagerSearchBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailManagerSearchBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailManagerSearchBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailManagerSearchBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    
    CGSize adjustLabelSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"Confirm", nil) font:[UIFont systemFontOfSize:17.0f]];
    self.mailManagerSearchConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15- adjustLabelSize.width, 0, adjustLabelSize.width, 44)];
    [self.mailManagerSearchConfirmButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"Confirm", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.mailManagerSearchConfirmButton addTarget:self action:@selector(confirm) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:self.mailManagerSearchTextView];
    
    self.mailManagerSearchTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    [self.mailManagerSearchTableView registerClass:[MailManagerUserSearchCell class] forCellReuseIdentifier:@"CloudFileShareUserSearchCell"];
    self.mailManagerSearchTableView.backgroundColor = [UIColor clearColor];
    self.mailManagerSearchTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.mailManagerSearchTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.mailManagerSearchTableView.delegate = self;
    self.mailManagerSearchTableView.dataSource = self;
    self.mailManagerSearchTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.mailManagerSearchTableView];
    [self.mailManagerSearchTableView reloadData];
    
    [self searchTextViewUserListStatus];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.mailManagerSearchTitleLabel];
    [self.navigationController.navigationBar addSubview:self.mailManagerSearchBackButton];
    [self.navigationController.navigationBar addSubview:self.mailManagerSearchConfirmButton];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailManagerSearchTitleLabel removeFromSuperview];
    [self.mailManagerSearchBackButton removeFromSuperview];
    [self.mailManagerSearchConfirmButton removeFromSuperview];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)confirm {
    if ([self.delegate respondsToSelector:@selector(completeMailUsers:)]) {
        [self.delegate completeMailUsers:(NSArray*)self.mailManagerUserArray];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark cloudFileShareUserSearchDelegate
- (void)addMailUser:(NSString *)userEmail {
    if (![self.mailManagerUserArray containsObject:userEmail]) {
        MailManagerUserListCard *card = [[MailManagerUserListCard alloc] initWithUserEmail:userEmail];
        card.delegate = self;
        [self.mailManagerUserCardArray addObject:card];
        [self.mailManagerUserArray addObject:userEmail];
        [self searchTextViewUserListStatus];
    }
}

- (void)deleteMailUser:(NSString *)userEmail {
    if ([self.mailManagerUserArray containsObject:userEmail]) {
        MailManagerUserListCard *removeCard;
        for (MailManagerUserListCard *card in self.mailManagerUserCardArray) {
            if ([card.userEmail isEqualToString:userEmail]) {
                removeCard = card;break;
            }
        }
        [self.mailManagerUserCardArray removeObject:removeCard];
        [self.mailManagerUserArray removeObject:userEmail];
        [self searchTextViewUserListStatus];
    }
}

#pragma mark search textField
- (UIView*)mailManagerSearchBackgroundView {
    if (!_mailManagerSearchBackgroundView) {
        _mailManagerSearchBackgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.mailManagerSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailManagerSearchTextView.frame))];
        _mailManagerSearchBackgroundView.backgroundColor = [UIColor clearColor];
    }
    return _mailManagerSearchBackgroundView;
}

- (void)mailManagerSearchBackgroundViewShow {
    if (!self.mailManagerSearchBackgroundView.superview) {
        [self.view addSubview:self.mailManagerSearchBackgroundView];
    }
}

- (void)mailManagerSearchBackgroundViewHide {
    if (self.mailManagerSearchBackgroundView.superview) {
        [self.mailManagerSearchBackgroundView removeFromSuperview];
    }
}

- (UIView*)mailManagerSearchIndicatorView {
    if (!_mailManagerSearchIndicatorView) {
        _mailManagerSearchIndicatorView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.mailManagerSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailManagerSearchTextView.frame))];
        _mailManagerSearchIndicatorView.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.3];
        UIActivityIndicatorView *mailManagerSearchIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake((CGRectGetWidth(_mailManagerSearchIndicatorView.frame)-50)/2, (CGRectGetHeight(_mailManagerSearchIndicatorView.frame)-50)/2, 50, 50)];
        mailManagerSearchIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        [mailManagerSearchIndicator startAnimating];
        [_mailManagerSearchIndicatorView addSubview:mailManagerSearchIndicator];
    }
    return _mailManagerSearchIndicatorView;
}

- (void)mailManagerSearchIndicatorViewShow {
    if (!self.mailManagerSearchIndicator.superview) {
        [self.mailManagerSearchTextView addSubview:self.mailManagerSearchIndicator];
        self.mailManagerSearchIconView.hidden = YES;
    }
}

- (void)mailManagerSearchIndicatorViewHide {
    if (self.mailManagerSearchIndicator.superview) {
        [self.mailManagerSearchIndicator removeFromSuperview];
        self.mailManagerSearchIconView.hidden = NO;
    }
}

- (UIView*)mailManagerSearchTextView {
    if (!_mailManagerSearchTextView) {
        _mailManagerSearchTextView = [[UIView alloc] initWithFrame:CGRectZero];
        _mailManagerSearchTextView.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
        _mailManagerSearchTextView.layer.borderWidth = 0.5;
        _mailManagerSearchTextView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        
        self.mailManagerSearchUserListView = [[UIScrollView alloc] initWithFrame:CGRectZero];
        self.mailManagerSearchUserListView.showsHorizontalScrollIndicator = NO;
        [_mailManagerSearchTextView addSubview:self.mailManagerSearchUserListView];
        
        self.mailManagerSearchIconView = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.mailManagerSearchIconView.image = [UIImage imageNamed:@"ic_contact_search_nor"];
        [_mailManagerSearchTextView addSubview:self.mailManagerSearchIconView];
        
        self.mailManagerSearchIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectZero];
        self.mailManagerSearchIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
        [self.mailManagerSearchIndicator startAnimating];
        
        self.mailManagerSearchClearButton = [[UIButton alloc] initWithFrame:CGRectZero];
        self.mailManagerSearchClearButton.imageView.frame = CGRectZero;
        [self.mailManagerSearchClearButton setImage:[UIImage imageNamed:@"ic_contact_clear_nor"] forState:UIControlStateNormal];
        self.mailManagerSearchClearButton.hidden = YES;
        [self.mailManagerSearchClearButton addTarget:self action:@selector(searchTextFieldClear) forControlEvents:UIControlEventTouchUpInside];
        [_mailManagerSearchTextView addSubview:self.mailManagerSearchClearButton];
        
        self.mailManagerSearchTextField = [[UITextField alloc] initWithFrame:CGRectZero];
        [self.mailManagerSearchTextField setAttributedPlaceholder:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"Search", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"999999" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}]];
        self.mailManagerSearchTextField.font = [UIFont systemFontOfSize:14.0f];
        self.mailManagerSearchTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.mailManagerSearchTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.mailManagerSearchTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.mailManagerSearchTextField.delegate = self;
        self.mailManagerSearchTextField.returnKeyType = UIReturnKeySearch;
        [self.mailManagerSearchTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [_mailManagerSearchTextView addSubview:self.mailManagerSearchTextField];
    }
    return _mailManagerSearchTextView;
}

- (void)searchTextFieldClear {
    self.mailManagerSearchTextField.text = nil;
    [self.mailManagerSearchTextField becomeFirstResponder];
}

- (void)searchTextViewUserListStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    if (self.mailManagerUserArray.count > 0) {
        self.mailManagerSearchTextView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 8+36+10+22+11);
        self.mailManagerSearchUserListView.frame = CGRectMake(0, 8, CGRectGetWidth(self.mailManagerSearchTextView.frame), 36);
        self.mailManagerSearchIconView.frame = CGRectMake(15, CGRectGetMaxY(self.mailManagerSearchUserListView.frame)+10, 22, 22);
        self.mailManagerSearchIndicator.frame = self.mailManagerSearchIconView.frame;
        self.mailManagerSearchTextField.frame = CGRectMake(15+22+4, CGRectGetMaxY(self.mailManagerSearchUserListView.frame)+10, CGRectGetWidth(self.view.frame)-15-22-4-4-44-4, 22);
        self.mailManagerSearchClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-44, CGRectGetMaxY(self.mailManagerSearchUserListView.frame), 44, 44);
        self.mailManagerSearchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.mailManagerSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailManagerSearchTextView.frame));
    } else {
        self.mailManagerSearchTextView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 44);
        self.mailManagerSearchUserListView.frame = CGRectZero;
        self.mailManagerSearchIconView.frame = CGRectMake(15, 11, 22, 22);
        self.mailManagerSearchIndicator.frame = self.mailManagerSearchIconView.frame;
        self.mailManagerSearchTextField.frame = CGRectMake(15+22+4, 11, CGRectGetWidth(self.view.frame)-15-22-4-4-44-4, 22);
        self.mailManagerSearchClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44);
        self.mailManagerSearchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.mailManagerSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.mailManagerSearchTextView.frame));
    }
    [self cloudFileShareUserList];
    
}

- (void)cloudFileShareUserList {
    CGFloat start_x = 15;
    for (UIView *view in self.mailManagerSearchUserListView.subviews) {
        [view removeFromSuperview];
    }
    for (MailManagerUserListCard *card in self.mailManagerUserCardArray) {
        CGRect rect = card.frame;
        rect.origin.x = start_x;
        rect.origin.y = 0;
        start_x = start_x + rect.size.width + 10;
        card.frame = rect;
        [self.mailManagerSearchUserListView addSubview:card];
    }
    CGFloat end_x = start_x > CGRectGetWidth(self.view.frame) ? start_x : CGRectGetWidth(self.view.frame);
    self.mailManagerSearchUserListView.contentSize = CGSizeMake(end_x, 0);
    [self.mailManagerSearchUserListView setContentOffset:CGPointMake(end_x-CGRectGetWidth(self.view.frame), 0) animated:YES];
}

- (void)deleteMailListUser:(NSString *)userEmail {
    if ([self.mailManagerUserArray containsObject:userEmail]) {
        MailManagerUserListCard *removeCard;
        for (MailManagerUserListCard *card in self.mailManagerUserCardArray) {
            if ([card.userEmail isEqualToString:userEmail]) {
                removeCard = card;break;
            }
        }
        [self.mailManagerUserCardArray removeObject:removeCard];
        [self searchTextViewUserListStatus];
        
        for (MailManagerUserSearchCell *cell in self.mailManagerSearchTableView.visibleCells) {
            if ([cell.userEmail isEqualToString:userEmail]) {
                cell.selectState = NO;
            }
        }
    }
}

#pragma mark textFiled delegate
- (void)textFieldDidChange:(UITextField*)textField {
    if (textField.text.length == 0) {
        self.mailManagerSearchState = NO;
        self.mailManagerSearchClearButton.hidden = YES;
    } else {
        self.mailManagerSearchState = YES;
        self.mailManagerSearchClearButton.hidden = NO;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [self mailManagerSearchBackgroundViewShow];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [self mailManagerSearchBackgroundViewHide];
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [self mailManagerSearchBackgroundViewHide];
    if ([textField.text isEqualToString:@""]) {
        return;
    }
    [self mailManagerSearchIndicatorViewShow];
    [User searchUser:textField.text succeed:^(id retobj) {
        [self mailManagerSearchIndicatorViewHide];
        self.mailManagerSearchUserArray = [User getUserHasEmailArrayWithKey:textField.text context:nil];
        [self.mailManagerSearchTableView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        [self mailManagerSearchIndicatorViewHide];
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"ContactSearchFailed", nil)];
        });
    }];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self mailManagerSearchBackgroundViewHide];
    [self.mailManagerSearchTextField resignFirstResponder];
}

#pragma maek tableView display cell
- (UITableViewCell*)mailManagerRecentContactDisplayCell {
    if (!_mailManagerRecentContactDisplayCell) {
        _mailManagerRecentContactDisplayCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _mailManagerRecentContactDisplayCell.textLabel.frame = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 55);
        _mailManagerRecentContactDisplayCell.textLabel.font = [UIFont systemFontOfSize:15.0f];
        _mailManagerRecentContactDisplayCell.textLabel.textColor = [CommonFunction colorWithString:@"008be3" alpha:1.0f];
        _mailManagerRecentContactDisplayCell.textLabel.textAlignment = NSTextAlignmentCenter;
        _mailManagerRecentContactDisplayCell.textLabel.text = NSLocalizedString(@"More", nil);
    }
    return _mailManagerRecentContactDisplayCell;
}

#pragma mark tableView delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.mailManagerSearchState) {
        if (self.mailManagerSearchUserArray.count == 0) {
            return 0;
        } else {
            return 1;
        }
    } else {
        NSInteger sectionNumber = 0;
        if (self.mailManagerRecentContactShow) {
            sectionNumber = sectionNumber + 1;
        }
        if (self.mailManagerMyContactShow) {
            sectionNumber = sectionNumber + 1;
        }
        return sectionNumber;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.mailManagerSearchState) {
        return self.mailManagerSearchUserArray.count;
    } else {
        if (section == 0) {
            if (self.mailManagerRecentContactShow) {
                if (self.mailManagerRecentContactUserArray.count > 5 && !self.mailManagerRecentContactDisplayState) {
                    return 6;
                } else {
                    return self.mailManagerRecentContactUserArray.count;
                }
            } else {
                return self.mailManagerMyContactUserArray.count;
            }
        }
        if (section == 1) {
            return self.mailManagerMyContactUserArray.count;
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
    if (self.mailManagerSearchState) {
        return [CommonFunction tableViewHeaderWithTitle:NSLocalizedString(@"ContactSearchTitle", nil)];
    } else {
        if (section == 0) {
            if (self.mailManagerRecentContactShow) {
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
    MailManagerUserSearchCell *cell = (MailManagerUserSearchCell*)[tableView dequeueReusableCellWithIdentifier:@"MailManagerUserSearchCell"];
    if (!cell) {
        cell = [[MailManagerUserSearchCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"MailManagerUserSearchCell"];
    }
    if (self.mailManagerSearchState) {
        User *user = [self.mailManagerSearchUserArray objectAtIndex:indexPath.row];
        cell.userEmail = user.userEmail;
    } else {
        if (indexPath.section == 0) {
            if (self.mailManagerRecentContactShow) {
                if (indexPath.row == 5 && !self.mailManagerRecentContactDisplayState) {
                    return self.mailManagerRecentContactDisplayCell;
                } else {
                    User *user = [self.mailManagerRecentContactUserArray objectAtIndex:indexPath.row];
                    cell.userEmail = user.userEmail;
                }
            } else {
                User *user = [self.mailManagerRecentContactUserArray objectAtIndex:indexPath.row];
                cell.userEmail = user.userEmail;
            }
        }
        if (indexPath.section == 1) {
            User *user = [self.mailManagerRecentContactUserArray objectAtIndex:indexPath.row];
            cell.userEmail = user.userEmail;
        }
    }
    if ([self.mailManagerUserArray containsObject:cell.userEmail]) {
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
    if (cell == self.mailManagerRecentContactDisplayCell) {
        self.mailManagerRecentContactDisplayState = YES;
        [self.mailManagerSearchTableView reloadData];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

@end
