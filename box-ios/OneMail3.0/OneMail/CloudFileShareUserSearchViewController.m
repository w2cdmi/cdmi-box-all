//
//  CloudFileShareUserSearchViewController.m
//  OneMail
//
//  Created by cse  on 16/1/13.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "CloudFileShareUserSearchViewController.h"
#import "AppDelegate.h"
#import <CoreData/CoreData.h>
#import "File+Remote.h"
#import "User+Remote.h"
#import "CloudFileShareActionHandle.h"

@interface CloudFileShareUserListCard ()

@property (nonatomic, strong) User *user;

@end

@implementation CloudFileShareUserListCard

- (id)initWithUserCloudId:(NSString *)userCloudId {
    self = [super init];
    if (self) {
        self.userCloudId = userCloudId;
        self.user = [User getUserWithUserCloudId:userCloudId context:nil];
        self.layer.cornerRadius = 4;
        self.layer.masksToBounds = YES;
        self.backgroundColor = [CommonFunction colorWithString:@"e5e5e5" alpha:1.0f];
        CGSize adjustNameSize = [CommonFunction labelSizeWithString:self.user.userName font:[UIFont systemFontOfSize:14.0f]];
        self.frame = CGRectMake(0, 0, 10+adjustNameSize.width+10, 36);
        [self setAttributedTitle:[[NSAttributedString alloc] initWithString:self.user.userName attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"000000" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
        [self addTarget:self action:@selector(deleteUser) forControlEvents:UIControlEventTouchUpInside];
    }
    return self;
}

- (void)deleteUser {
    if ([self.delegate respondsToSelector:@selector(deleteListShareUser:)]) {
        [self.delegate deleteListShareUser:self.userCloudId];
    }
}

@end



@interface CloudFileShareUserSearchCell ()

@property (nonatomic, strong) UIImageView *cloudFileShareUserIconView;
@property (nonatomic, strong) UILabel     *cloudFileShareUserNameLabel;
@property (nonatomic, strong) UILabel     *cloudFileShareUserEmailLabel;
@property (nonatomic, strong) UIButton    *cloudFileShareUserCheckButton;

@end

@implementation CloudFileShareUserSearchCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UIImageView *cloudFileShareUserBackgroundImageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 5.5, 44, 44)];
        cloudFileShareUserBackgroundImageView.image = [UIImage imageNamed:@"img_user_frame"];
        [self.contentView addSubview:cloudFileShareUserBackgroundImageView];
        
        self.cloudFileShareUserIconView = [[UIImageView alloc] initWithFrame:CGRectMake(2, 2, 40, 40)];
        [cloudFileShareUserBackgroundImageView addSubview:self.cloudFileShareUserIconView];
        
        self.cloudFileShareUserNameLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.cloudFileShareUserNameLabel];
        
        self.cloudFileShareUserEmailLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.cloudFileShareUserEmailLabel];
        
        self.cloudFileShareUserCheckButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.cloudFileShareUserCheckButton.imageView.frame = CGRectMake(10, 10, 22, 22);
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_transfer_delete_nor"] forState:UIControlStateNormal];
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_transfer_delete_press"] forState:UIControlStateHighlighted];
        [self.cloudFileShareUserCheckButton addTarget:self action:@selector(shareUserControl) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.cloudFileShareUserCheckButton];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    
    self.cloudFileShareUserIconView.image = [UIImage imageNamed:@"img_user_default"];
    
    self.cloudFileShareUserNameLabel.text = self.user.userName;
    self.cloudFileShareUserNameLabel.frame = CGRectMake(15+44+10, 8, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 20);
    
    self.cloudFileShareUserEmailLabel.text = self.user.userDescription;
    self.cloudFileShareUserEmailLabel.frame = CGRectMake(15+44+10, CGRectGetMaxY(self.cloudFileShareUserNameLabel.frame)+4, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 15);
    
    self.cloudFileShareUserCheckButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-42,(CGRectGetHeight(self.frame)-42)/2, 42, 42);
    
    if (_selectState) {
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_off_nor"] forState:UIControlStateNormal];
    }
}

- (void)setSelectState:(BOOL)selectState {
    _selectState = selectState;
    if (_selectState) {
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_choice"] forState:UIControlStateNormal];
    }
}

- (void)shareUserControl {
    if (self.selectState) {
        self.selectState = NO;
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_choice"] forState:UIControlStateNormal];
        if ([self.delegate respondsToSelector:@selector(deleteShareUser:)]) {
            [self.delegate deleteShareUser:self.user];
        }
    } else {
        self.selectState = YES;
        [self.cloudFileShareUserCheckButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
        if ([self.delegate respondsToSelector:@selector(addShareUser:)]) {
            [self.delegate addShareUser:self.user];
        }
    }
    
}

- (void)prepareForReuse {
    self.cloudFileShareUserIconView.image = nil;
    self.cloudFileShareUserNameLabel.text = nil;
    self.cloudFileShareUserEmailLabel.text = nil;
}

@end



@interface CloudFileShareUserSearchViewController ()<UITextFieldDelegate,UITextViewDelegate,UITableViewDataSource,UITableViewDelegate,CloudFileShareUserSearchDelegate,CloudFileShareUserListCardDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) NSMutableArray  *cloudFileShareUserArray;
@property (nonatomic, strong) NSMutableArray  *cloudFileShareUserNewArray;
@property (nonatomic, strong) NSMutableArray  *cloudFileShareUserCardArray;

@property (nonatomic, strong) UILabel         *cloudFileShareSearchTitleLabel;
@property (nonatomic, strong) UIButton        *cloudFileShareSearchBackButton;
@property (nonatomic, strong) UIButton        *cloudFileShareSearchConfirmButton;

@property (nonatomic, strong) UIView          *cloudFileShareSearchTextView;
@property (nonatomic, strong) UIScrollView    *cloudFileShareSearchUserListView;
@property (nonatomic, strong) UIImageView     *cloudFileShareSearchIconView;
@property (nonatomic, strong) UITextField     *cloudFileShareSearchTextField;
@property (nonatomic, strong) UIButton        *cloudFileShareSearchClearButton;
@property (nonatomic, strong) UIView          *cloudFileShareSearchBackgroundView;
@property (nonatomic, strong) UIView          *cloudFileShareSearchIndicatorView;
@property (nonatomic, strong) UIActivityIndicatorView *cloudFileShareSearchIndicator;

@property (nonatomic, strong) UITableView     *cloudFileShareSearchTableView;
@property (nonatomic, strong) NSMutableArray  *cloudFileShareSearchUserArray;
@property (nonatomic, strong) NSMutableArray  *cloudFileShareRecentContactUserArray;
@property (nonatomic, strong) NSMutableArray  *cloudFileShareMyContactUserArray;
@property (nonatomic, strong) UITableViewCell *cloudFileShareRecentContactDisplayCell;

@property (nonatomic, strong) UIView          *cloudFileShareMessageInputView;
@property (nonatomic, strong) NSString        *cloudFileShareMessage;

@property (nonatomic, assign) BOOL             cloudFileShareSearchState;
@property (nonatomic, assign) BOOL             cloudFileShareRecentContactShow;
@property (nonatomic, assign) BOOL             cloudFileShareMyContactShow;
@property (nonatomic, assign) BOOL             cloudFileShareRecentContactDisplayState;

@end

@implementation CloudFileShareUserSearchViewController

- (id)initWithShareUsers:(NSArray *)shareUserArray file:(File *)file {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.file = file;
        
        self.cloudFileShareSearchState = NO;
        self.cloudFileShareRecentContactDisplayState = NO;
        
        self.cloudFileShareUserArray = [[NSMutableArray alloc] initWithArray:shareUserArray];
        self.cloudFileShareUserNewArray = [[NSMutableArray alloc] initWithArray:shareUserArray];
        self.cloudFileShareUserCardArray = [[NSMutableArray alloc] init];
        for (NSString *userCloudId in self.cloudFileShareUserNewArray) {
            CloudFileShareUserListCard *card = [[CloudFileShareUserListCard alloc] initWithUserCloudId:userCloudId];
            card.delegate = self;
            [self.cloudFileShareUserCardArray addObject:card];
        }
        
        NSMutableArray *recentContactUser = [[NSMutableArray alloc] initWithArray:[User getRecentContactUsers:nil]];
        self.cloudFileShareRecentContactUserArray = [[NSMutableArray alloc] initWithArray:recentContactUser];
        for (User *user in recentContactUser) {
            if (!user.userCloudId && !user.userLoginName) {
                [self.cloudFileShareRecentContactUserArray removeObject:user];
            }
        }
        if (self.cloudFileShareRecentContactUserArray.count > 0) {
            self.cloudFileShareRecentContactShow = YES;
        }
        
        NSMutableArray *myContactUsers = [[NSMutableArray alloc] initWithArray:[User getMyContactUsers:nil]];
        self.cloudFileShareMyContactUserArray = [[NSMutableArray alloc] initWithArray:myContactUsers];
        for (User *user in myContactUsers) {
            if (!user.userCloudId && !user.userLoginName) {
                [self.cloudFileShareMyContactUserArray removeObject:user];
            }
        }
        [self.cloudFileShareMyContactUserArray removeObjectsInArray:self.cloudFileShareRecentContactUserArray];
        if (self.cloudFileShareMyContactUserArray.count > 0) {
            self.cloudFileShareMyContactShow = YES;
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
    
    self.cloudFileShareSearchTitleLabel = [[UILabel alloc] initWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    self.cloudFileShareSearchTitleLabel.font = [UIFont boldSystemFontOfSize:18.0f];
    self.cloudFileShareSearchTitleLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.cloudFileShareSearchTitleLabel.textAlignment = NSTextAlignmentCenter;
    self.cloudFileShareSearchTitleLabel.text = getLocalizedString(@"CloudSharedUserAddTitle", nil);
    
    self.cloudFileShareSearchBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.cloudFileShareSearchBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudFileShareSearchBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.cloudFileShareSearchBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.cloudFileShareSearchBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    
    CGSize adjustLabelSize = [CommonFunction labelSizeWithString:getLocalizedString(@"Confirm", nil) font:[UIFont systemFontOfSize:17.0f]];
    self.cloudFileShareSearchConfirmButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15- adjustLabelSize.width, 0, adjustLabelSize.width, 44)];
    [self.cloudFileShareSearchConfirmButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"Confirm", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.cloudFileShareSearchConfirmButton addTarget:self action:@selector(confirm) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:self.cloudFileShareSearchTextView];
    
    self.cloudFileShareSearchTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    [self.cloudFileShareSearchTableView registerClass:[CloudFileShareUserSearchCell class] forCellReuseIdentifier:@"CloudFileShareUserSearchCell"];
    self.cloudFileShareSearchTableView.backgroundColor = [UIColor clearColor];
    self.cloudFileShareSearchTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.cloudFileShareSearchTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.cloudFileShareSearchTableView.delegate = self;
    self.cloudFileShareSearchTableView.dataSource = self;
    self.cloudFileShareSearchTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.cloudFileShareSearchTableView];
    [self.cloudFileShareSearchTableView reloadData];
    
    [self searchTextViewUserListStatus];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self.navigationController.navigationBar addSubview:self.cloudFileShareSearchTitleLabel];
    [self.navigationController.navigationBar addSubview:self.cloudFileShareSearchBackButton];
    [self.navigationController.navigationBar addSubview:self.cloudFileShareSearchConfirmButton];

    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.cloudFileShareSearchTitleLabel removeFromSuperview];
    [self.cloudFileShareSearchBackButton removeFromSuperview];
    [self.cloudFileShareSearchConfirmButton removeFromSuperview];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)confirm {
    NSMutableSet *searchUserSet = [[NSMutableSet alloc] initWithArray:self.cloudFileShareUserNewArray];
    NSMutableSet *sharedUserSet = [[NSMutableSet alloc] initWithArray:self.cloudFileShareUserArray];
    
    NSMutableSet *addShareUserSet = [NSMutableSet setWithSet:searchUserSet];
    [addShareUserSet minusSet:sharedUserSet];
    
    if (addShareUserSet.count > 0) {
        [self.view addSubview:self.cloudFileShareMessageInputView];
    } else {
        [self sendShareRequestWithMessage:nil];
    }
}

#pragma mark share request
- (void)sendShareRequestWithMessage:(NSString*)message {
    CloudFileShareActionHandle *shareHandle = [[CloudFileShareActionHandle alloc] init];
    shareHandle.shareFile = self.file;
    shareHandle.sharedUserArray = self.cloudFileShareUserArray;
    shareHandle.searchUserArray = self.cloudFileShareUserNewArray;
    shareHandle.searchView = self;
    shareHandle.descriptionMessage = message;
    [shareHandle sendShareRequest:^{
        [self.navigationController popViewControllerAnimated:YES];
    }];
}

#pragma mark cloudFileShareUserSearchDelegate
- (void)addShareUser:(User *)user {
    if (!user.userCloudId) {
        [user availableUser:^(id retobj) {
            [self addShareUserHandleWithUser:user];
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudSharedUserAddFailedPrompt", nil)];
            });
        }];
    } else {
        [self addShareUserHandleWithUser:user];
    }
}

- (void)addShareUserHandleWithUser:(User*)user {
    if (![self.cloudFileShareUserNewArray containsObject:user.userCloudId]) {
        CloudFileShareUserListCard *card = [[CloudFileShareUserListCard alloc] initWithUserCloudId:user.userCloudId];
        card.delegate = self;
        [self.cloudFileShareUserCardArray addObject:card];
        [self.cloudFileShareUserNewArray addObject:user.userCloudId];
        [self searchTextViewUserListStatus];
    }
}

- (void)deleteShareUser:(User *)user {
    if ([self.cloudFileShareUserNewArray containsObject:user.userCloudId]) {
        CloudFileShareUserListCard *removeCard;
        for (CloudFileShareUserListCard *card in self.cloudFileShareUserCardArray) {
            if ([card.userCloudId isEqualToString:user.userCloudId]) {
                removeCard = card;break;
            }
        }
        [self.cloudFileShareUserCardArray removeObject:removeCard];
        [self.cloudFileShareUserNewArray removeObject:user.userCloudId];
        [self searchTextViewUserListStatus];
    }
}

#pragma mark search textField
- (UIView*)cloudFileShareSearchBackgroundView {
    if (!_cloudFileShareSearchBackgroundView) {
        _cloudFileShareSearchBackgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.cloudFileShareSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.cloudFileShareSearchTextView.frame))];
        _cloudFileShareSearchBackgroundView.backgroundColor = [UIColor clearColor];
    }
    return _cloudFileShareSearchBackgroundView;
}

- (void)cloudFileShareSearchBackgroundViewShow {
    if (!self.cloudFileShareSearchBackgroundView.superview) {
        [self.view addSubview:self.cloudFileShareSearchBackgroundView];
    }
}

- (void)cloudFileShareSearchBackgroundViewHide {
    if (self.cloudFileShareSearchBackgroundView.superview) {
        [self.cloudFileShareSearchBackgroundView removeFromSuperview];
    }
}

- (UIView*)cloudFileShareSearchIndicatorView {
    if (!_cloudFileShareSearchIndicatorView) {
        _cloudFileShareSearchIndicatorView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.cloudFileShareSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.cloudFileShareSearchTextView.frame))];
        _cloudFileShareSearchIndicatorView.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.3];
        UIActivityIndicatorView *cloudFileShareSearchIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake((CGRectGetWidth(_cloudFileShareSearchIndicatorView.frame)-50)/2, (CGRectGetHeight(_cloudFileShareSearchIndicatorView.frame)-50)/2, 50, 50)];
        cloudFileShareSearchIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        [cloudFileShareSearchIndicator startAnimating];
        [_cloudFileShareSearchIndicatorView addSubview:cloudFileShareSearchIndicator];
    }
    return _cloudFileShareSearchIndicatorView;
}

- (void)cloudFileShareSearchIndicatorViewShow {
    if (!self.cloudFileShareSearchIndicator.superview) {
        [self.cloudFileShareSearchTextView addSubview:self.cloudFileShareSearchIndicator];
        self.cloudFileShareSearchIconView.hidden = YES;
    }
}

- (void)cloudFileShareSearchIndicatorViewHide {
    if (self.cloudFileShareSearchIndicator.superview) {
        [self.cloudFileShareSearchIndicator removeFromSuperview];
        self.cloudFileShareSearchIconView.hidden = NO;
    }
}

- (UIView*)cloudFileShareSearchTextView {
    if (!_cloudFileShareSearchTextView) {
        _cloudFileShareSearchTextView = [[UIView alloc] initWithFrame:CGRectZero];
        _cloudFileShareSearchTextView.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
        _cloudFileShareSearchTextView.layer.borderWidth = 0.5;
        _cloudFileShareSearchTextView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        
        self.cloudFileShareSearchUserListView = [[UIScrollView alloc] initWithFrame:CGRectZero];
        self.cloudFileShareSearchUserListView.showsHorizontalScrollIndicator = NO;
        [_cloudFileShareSearchTextView addSubview:self.cloudFileShareSearchUserListView];
        
        self.cloudFileShareSearchIconView = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.cloudFileShareSearchIconView.image = [UIImage imageNamed:@"ic_contact_search_nor"];
        [_cloudFileShareSearchTextView addSubview:self.cloudFileShareSearchIconView];
        
        self.cloudFileShareSearchIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectZero];
        self.cloudFileShareSearchIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
        [self.cloudFileShareSearchIndicator startAnimating];
        
        self.cloudFileShareSearchClearButton = [[UIButton alloc] initWithFrame:CGRectZero];
        self.cloudFileShareSearchClearButton.imageView.frame = CGRectZero;
        [self.cloudFileShareSearchClearButton setImage:[UIImage imageNamed:@"ic_contact_clear_nor"] forState:UIControlStateNormal];
        self.cloudFileShareSearchClearButton.hidden = YES;
        [self.cloudFileShareSearchClearButton addTarget:self action:@selector(searchTextFieldClear) forControlEvents:UIControlEventTouchUpInside];
        [_cloudFileShareSearchTextView addSubview:self.cloudFileShareSearchClearButton];
        
        self.cloudFileShareSearchTextField = [[UITextField alloc] initWithFrame:CGRectZero];
        [self.cloudFileShareSearchTextField setAttributedPlaceholder:[[NSAttributedString alloc] initWithString:getLocalizedString(@"Search", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"999999" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}]];
        self.cloudFileShareSearchTextField.font = [UIFont systemFontOfSize:14.0f];
        self.cloudFileShareSearchTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.cloudFileShareSearchTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.cloudFileShareSearchTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.cloudFileShareSearchTextField.delegate = self;
        self.cloudFileShareSearchTextField.returnKeyType = UIReturnKeySearch;
        [self.cloudFileShareSearchTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [_cloudFileShareSearchTextView addSubview:self.cloudFileShareSearchTextField];
    }
    return _cloudFileShareSearchTextView;
}

- (void)searchTextFieldClear {
    self.cloudFileShareSearchTextField.text = nil;
    [self.cloudFileShareSearchTextField becomeFirstResponder];
}

- (void)searchTextViewUserListStatus {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    if (self.cloudFileShareUserNewArray.count > 0) {
        self.cloudFileShareSearchTextView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 8+36+10+22+11);
        self.cloudFileShareSearchUserListView.frame = CGRectMake(0, 8, CGRectGetWidth(self.cloudFileShareSearchTextView.frame), 36);
        self.cloudFileShareSearchIconView.frame = CGRectMake(15, CGRectGetMaxY(self.cloudFileShareSearchUserListView.frame)+10, 22, 22);
        self.cloudFileShareSearchIndicator.frame = self.cloudFileShareSearchIconView.frame;
        self.cloudFileShareSearchTextField.frame = CGRectMake(15+22+4, CGRectGetMaxY(self.cloudFileShareSearchUserListView.frame)+10, CGRectGetWidth(self.view.frame)-15-22-4-4-44-4, 22);
        self.cloudFileShareSearchClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-44, CGRectGetMaxY(self.cloudFileShareSearchUserListView.frame), 44, 44);
        self.cloudFileShareSearchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.cloudFileShareSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.cloudFileShareSearchTextView.frame));
    } else {
        self.cloudFileShareSearchTextView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 44);
        self.cloudFileShareSearchUserListView.frame = CGRectZero;
        self.cloudFileShareSearchIconView.frame = CGRectMake(15, 11, 22, 22);
        self.cloudFileShareSearchIndicator.frame = self.cloudFileShareSearchIconView.frame;
        self.cloudFileShareSearchTextField.frame = CGRectMake(15+22+4, 11, CGRectGetWidth(self.view.frame)-15-22-4-4-44-4, 22);
        self.cloudFileShareSearchClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44);
        self.cloudFileShareSearchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.cloudFileShareSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.cloudFileShareSearchTextView.frame));
    }
    [self cloudFileShareUserList];

}

- (void)cloudFileShareUserList {
    CGFloat start_x = 15;
    for (UIView *view in self.cloudFileShareSearchUserListView.subviews) {
        [view removeFromSuperview];
    }
    for (CloudFileShareUserListCard *card in self.cloudFileShareUserCardArray) {
        CGRect rect = card.frame;
        rect.origin.x = start_x;
        rect.origin.y = 0;
        start_x = start_x + rect.size.width + 10;
        card.frame = rect;
        [self.cloudFileShareSearchUserListView addSubview:card];
    }
    CGFloat end_x = start_x > CGRectGetWidth(self.view.frame) ? start_x : CGRectGetWidth(self.view.frame);
    self.cloudFileShareSearchUserListView.contentSize = CGSizeMake(end_x, 0);
    [self.cloudFileShareSearchUserListView setContentOffset:CGPointMake(end_x-CGRectGetWidth(self.view.frame), 0) animated:YES];
}

- (void)deleteListShareUser:(NSString *)userCloudId {
    if ([self.cloudFileShareUserNewArray containsObject:userCloudId]) {
        CloudFileShareUserListCard *removeCard;
        for (CloudFileShareUserListCard *card in self.cloudFileShareUserCardArray) {
            if ([card.userCloudId isEqualToString:userCloudId]) {
                removeCard = card;break;
            }
        }
        [self.cloudFileShareUserCardArray removeObject:removeCard];
        [self.cloudFileShareUserNewArray removeObject:userCloudId];
        [self searchTextViewUserListStatus];
        
        for (CloudFileShareUserSearchCell *cell in self.cloudFileShareSearchTableView.visibleCells) {
            if ([cell.user.userCloudId isEqualToString:userCloudId]) {
                cell.selectState = NO;
            }
        }
    }
}

#pragma mark messageInput
- (UIView*)cloudFileShareMessageInputView {
    if (!_cloudFileShareMessageInputView) {
        _cloudFileShareMessageInputView = [[UIView alloc] initWithFrame:CGRectMake(0,0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame))];
        _cloudFileShareMessageInputView.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.3f];
        
        UIView *messageAlert = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 270, 163)];
        messageAlert.frame = CGRectMake((CGRectGetWidth(_cloudFileShareMessageInputView.frame)-CGRectGetWidth(messageAlert.frame))/2, ((CGRectGetHeight(_cloudFileShareMessageInputView.frame)-CGRectGetHeight(messageAlert.frame))/2), CGRectGetWidth(messageAlert.frame), CGRectGetHeight(messageAlert.frame));
        messageAlert.backgroundColor = [UIColor whiteColor];
        messageAlert.layer.cornerRadius = 15;
        [_cloudFileShareMessageInputView addSubview:messageAlert];
        
        CGSize titleAdjustSize = [CommonFunction labelSizeWithString:getLocalizedString(@"CloudShareMessageTitle", nil) font:[UIFont systemFontOfSize:16.5f]];
        UILabel *messageTitle = [CommonFunction labelWithFrame:CGRectMake(0, 22, CGRectGetWidth(messageAlert.frame), ceil(titleAdjustSize.height)) textFont:[UIFont systemFontOfSize:16.5f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        messageTitle.text = getLocalizedString(@"CloudShareMessageTitle", nil);
        [messageAlert addSubview:messageTitle];
        
        UIButton *cancelBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(messageAlert.frame)-44, CGRectGetWidth(messageAlert.frame)/2, 44)];
        [cancelBtn setTitle:getLocalizedString(@"Cancel", nil) forState:UIControlStateNormal];
        [cancelBtn setTitleColor:[CommonFunction colorWithString:@"3282c4" alpha:1.0f] forState:UIControlStateNormal];
        cancelBtn.titleLabel.font = [UIFont systemFontOfSize:16.5f];
        [cancelBtn addTarget:self action:@selector(messageInputCancel) forControlEvents:UIControlEventTouchUpInside];
        [messageAlert addSubview:cancelBtn];
        
        UIButton *confirmBtn = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(messageAlert.frame)/2, CGRectGetHeight(messageAlert.frame)-44, CGRectGetWidth(messageAlert.frame)/2, 44)];
        [confirmBtn setTitle:getLocalizedString(@"Done", nil) forState:UIControlStateNormal];
        [confirmBtn setTitleColor:[CommonFunction colorWithString:@"3282c4" alpha:1.0f] forState:UIControlStateNormal];
        confirmBtn.titleLabel.font = [UIFont systemFontOfSize:16.5f];
        [confirmBtn addTarget:self action:@selector(messageInputConfirm) forControlEvents:UIControlEventTouchUpInside];
        [messageAlert addSubview:confirmBtn];
        
        UITextView *messageText = [[UITextView alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(messageTitle.frame)+15, 240, CGRectGetHeight(messageAlert.frame)-CGRectGetMaxY(messageTitle.frame)-15-12-44)];
        messageText.layer.borderWidth = 1;
        messageText.layer.borderColor = [CommonFunction colorWithString:@"c8c7cc" alpha:1.0f].CGColor;
        [messageAlert addSubview:messageText];
        messageText.delegate = self;
        
        UIView *line1 = [[UIView alloc] initWithFrame:CGRectZero];
        line1.frame = CGRectMake(0, CGRectGetMinY(cancelBtn.frame), CGRectGetWidth(messageAlert.frame), 1);
        line1.backgroundColor = [CommonFunction colorWithString:@"c8c7cc" alpha:1.0f];
        [messageAlert addSubview:line1];
        
        UIView *line2 = [[UIView alloc] initWithFrame:CGRectZero];
        line2.frame = CGRectMake(CGRectGetWidth(messageAlert.frame)/2, CGRectGetMinY(cancelBtn.frame), 1, 44);
        line2.backgroundColor = [CommonFunction colorWithString:@"c8c7cc" alpha:1.0f];
        [messageAlert addSubview:line2];
    }
    return _cloudFileShareMessageInputView;
}

- (void)messageInputCancel {
    [self.cloudFileShareMessageInputView removeFromSuperview];
}

- (void)messageInputConfirm {
    [self.cloudFileShareMessageInputView removeFromSuperview];
    [self sendShareRequestWithMessage:self.cloudFileShareMessage];
    self.cloudFileShareMessage = nil;
}

#pragma mark textView delegate
- (void)textViewDidBeginEditing:(UITextView *)textView {
    UIView *messageAlert = [textView superview];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = (rect.size.height-216-40) - CGRectGetMaxY(messageAlert.frame);
    if(rect.origin.y < 0) self.view.frame = rect;
    [UIView commitAnimations];
}

- (void)textViewDidEndEditing:(UITextView *)textView {
    self.cloudFileShareMessage = textView.text;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    if (text.length == 0) {
        return YES;
    }
    NSInteger existedLength = textView.text.length;
    NSInteger selectedLength = range.length;
    NSInteger replaceLength = text.length;
    if (existedLength - selectedLength + replaceLength > 2000) {
        return NO;
    }
    return YES;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self.view endEditing:YES];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3];
    CGRect rect = self.view.frame;
    rect.origin.y = 0;
    self.view.frame = rect;
    [UIView commitAnimations];
}

#pragma mark textFiled delegate
- (void)textFieldDidChange:(UITextField*)textField {
    if (textField.text.length == 0) {
        self.cloudFileShareSearchState = NO;
        self.cloudFileShareSearchClearButton.hidden = YES;
        [self.cloudFileShareSearchTableView reloadData];
    } else {
        self.cloudFileShareSearchState = YES;
        self.cloudFileShareSearchClearButton.hidden = NO;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [self cloudFileShareSearchBackgroundViewShow];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [self cloudFileShareSearchBackgroundViewHide];
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [self cloudFileShareSearchBackgroundViewHide];
    if ([textField.text isEqualToString:@""]) {
        self.cloudFileShareSearchState = NO;
        [self.cloudFileShareSearchTableView reloadData];
        return;
    }
    [self cloudFileShareSearchIndicatorViewShow];
    [User searchUser:textField.text succeed:^(id retobj) {
        [self cloudFileShareSearchIndicatorViewHide];
        NSMutableArray *searchContactUser = [[NSMutableArray alloc] initWithArray:[User getUserArrayWithKey:textField.text context:nil]];
        self.cloudFileShareSearchUserArray = [[NSMutableArray alloc] initWithArray:searchContactUser];
        for (User *user in searchContactUser) {
            if (!user.userCloudId && !user.userLoginName) {
                [self.cloudFileShareSearchUserArray removeObject:user];
            }
        }
        [self.cloudFileShareSearchTableView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        [self cloudFileShareSearchIndicatorViewHide];
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"ContactSearchFailed", nil)];
        });
    }];
}

//- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
//    [self cloudFileShareSearchBackgroundViewHide];
//    [self.cloudFileShareSearchTextField resignFirstResponder];
//}

#pragma maek tableView display cell
- (UITableViewCell*)cloudFileShareRecentContactDisplayCell {
    if (!_cloudFileShareRecentContactDisplayCell) {
        _cloudFileShareRecentContactDisplayCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        UILabel *titleLabel = [CommonFunction labelWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 55) textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"008be3" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        titleLabel.text = getLocalizedString(@"More", nil);
        [_cloudFileShareRecentContactDisplayCell addSubview:titleLabel];
    }
    return _cloudFileShareRecentContactDisplayCell;
}

#pragma mark tableView delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.cloudFileShareSearchState) {
        if (self.cloudFileShareSearchUserArray.count == 0) {
            return 0;
        } else {
            return 1;
        }
    } else {
        NSInteger sectionNumber = 0;
        if (self.cloudFileShareRecentContactShow) {
            sectionNumber = sectionNumber + 1;
        }
        if (self.cloudFileShareMyContactShow) {
            sectionNumber = sectionNumber + 1;
        }
        return sectionNumber;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.cloudFileShareSearchState) {
        return self.cloudFileShareSearchUserArray.count;
    } else {
        if (section == 0) {
            if (self.cloudFileShareRecentContactShow) {
                if (self.cloudFileShareRecentContactUserArray.count > 5 && !self.cloudFileShareRecentContactDisplayState) {
                    return 6;
                } else {
                    return self.cloudFileShareRecentContactUserArray.count;
                }
            } else {
                return self.cloudFileShareMyContactUserArray.count;
            }
        }
        if (section == 1) {
            return self.cloudFileShareMyContactUserArray.count;
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
    if (self.cloudFileShareSearchState) {
        return [CommonFunction tableViewHeaderWithTitle:getLocalizedString(@"ContactSearchTitle", nil)];
    } else {
        if (section == 0) {
            if (self.cloudFileShareRecentContactShow) {
                return [CommonFunction tableViewHeaderWithTitle:getLocalizedString(@"ContactRecentTitle", nil)];
            } else {
                return [CommonFunction tableViewHeaderWithTitle:getLocalizedString(@"ContactAllTitle", nil)];
            }
        }
        if (section == 1) {
            return [CommonFunction tableViewHeaderWithTitle:getLocalizedString(@"ContactAllTitle", nil)];
        }
    }
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudFileShareUserSearchCell *cell = (CloudFileShareUserSearchCell*)[tableView dequeueReusableCellWithIdentifier:@"CloudFileShareUserSearchCell"];
    if (!cell) {
        cell = [[CloudFileShareUserSearchCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"CloudFileShareUserSearchCell"];
    }
    if (self.cloudFileShareSearchState) {
        cell.user = [self.cloudFileShareSearchUserArray objectAtIndex:indexPath.row];
    } else {
        if (indexPath.section == 0) {
            if (self.cloudFileShareRecentContactShow) {
                if (indexPath.row == 5 && !self.cloudFileShareRecentContactDisplayState) {
                    return self.cloudFileShareRecentContactDisplayCell;
                } else {
                    cell.user = [self.cloudFileShareRecentContactUserArray objectAtIndex:indexPath.row];
                }
            } else {
                cell.user = [self.cloudFileShareMyContactUserArray objectAtIndex:indexPath.row];
            }
        }
        if (indexPath.section == 1) {
            cell.user = [self.cloudFileShareMyContactUserArray objectAtIndex:indexPath.row];
        }
    }
    if ([self.cloudFileShareUserNewArray containsObject:cell.user.userCloudId]) {
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
    if (cell == self.cloudFileShareRecentContactDisplayCell) {
        self.cloudFileShareRecentContactDisplayState = YES;
        [self.cloudFileShareSearchTableView reloadData];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

@end
