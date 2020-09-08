//
//  SpaceUsersManageViewController.m
//  OneMail
//
//  Created by admin on 17/1/10.
//  Copyright © 2017年 cse. All rights reserved.
//

#import "SpaceUsersManageViewController.h"
#import "CommonFunction.h"
#import "User.h"
#import "TeamSpace+Remote.h"
#import "UIView+Toast.h"
#import "User+Remote.h"
#import "FileMultiOperation.h"
#import "MJRefresh.h"
#import "UserSetting.h"
@interface SpaceUsersManageCell ()
@property (nonatomic, strong) UIImageView *SpaceUserIconView;
@property (nonatomic, strong) UILabel     *SpaceUserNameLabel;
@property (nonatomic, strong) UILabel     *SpaceUserRoleLabel;
@property (nonatomic, strong) UIButton    *SpaceUserDeleteButton;
@end
@implementation SpaceUsersManageCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        UIImageView *SpaceUserBackgroundImageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 5.5, 44, 44)];
        SpaceUserBackgroundImageView.image = [UIImage imageNamed:@"img_user_frame"];
        [self.contentView addSubview:SpaceUserBackgroundImageView];
        
        self.SpaceUserIconView = [[UIImageView alloc] initWithFrame:CGRectMake(2, 2, 40, 40)];
        [SpaceUserBackgroundImageView addSubview:self.SpaceUserIconView];
        
        self.SpaceUserNameLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.SpaceUserNameLabel];
        
        self.SpaceUserRoleLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.SpaceUserRoleLabel];
        
        self.SpaceUserDeleteButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 42, 42)];
        self.SpaceUserDeleteButton.imageView.frame = CGRectMake(10, 10, 22, 22);
            [self.contentView addSubview:self.SpaceUserDeleteButton];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
    self.SpaceUserIconView.image = [UIImage imageNamed:@"img_user_default"];
    if (self.ship) {
        self.SpaceUserNameLabel.text = self.ship.userName?self.ship.userName:@"系统";
        self.SpaceUserNameLabel.frame = CGRectMake(15+44+10, 8, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 20);
        NSString *role;
        if ([self.ship.teamRole isEqualToString:@"admin"]) {
            role = @"拥有者";
        }
        else{
            role = [MemberShip pretyName:self.ship.role];
        }
        self.SpaceUserRoleLabel.text = role;
        self.SpaceUserRoleLabel.frame = CGRectMake(15+44+10, CGRectGetMaxY(self.SpaceUserNameLabel.frame)+4, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 15);
        
        self.SpaceUserDeleteButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-42,(CGRectGetHeight(self.frame)-42)/2, 42, 42);
        [self.SpaceUserDeleteButton addTarget:self action:@selector(spaceUserDelete) forControlEvents:UIControlEventTouchUpInside];
        [self.SpaceUserDeleteButton setImage:[UIImage imageNamed:@"ic_transfer_delete_nor"] forState:UIControlStateNormal];
        [self.SpaceUserDeleteButton setImage:[UIImage imageNamed:@"ic_transfer_delete_press"] forState:UIControlStateHighlighted];
    }
    if (self.user) {
        self.SpaceUserNameLabel.text = self.user.userName;
        self.SpaceUserNameLabel.frame = CGRectMake(15+44+10, 8, CGRectGetWidth(self.frame)-15-44-10-5-42-5, 20);
        self.SpaceUserDeleteButton.frame = CGRectMake(CGRectGetWidth(self.frame)-5-42,(CGRectGetHeight(self.frame)-42)/2, 42, 42);
        [self.SpaceUserDeleteButton addTarget:self action:@selector(spaceUserAdd) forControlEvents:UIControlEventTouchUpInside];
        [self.SpaceUserDeleteButton setImage:[UIImage imageNamed:@"ic_add_group_member_nor"] forState:UIControlStateNormal];
    }
    if (self.isChangeOwer == true) {
        self.SpaceUserDeleteButton.hidden = YES;
    }
}
- (void)spaceUserDelete{
    [self.SpaceUserDeleteButton setEnabled:NO];
    if ([self.delegate respondsToSelector:@selector(spaceDeleteUser:completion:)]) {
        [self.delegate spaceDeleteUser:self.ship completion:^(BOOL result) {
            [self.SpaceUserDeleteButton setEnabled:YES];
        }];
    }
}
- (void)spaceUserAdd{
    [self.SpaceUserDeleteButton setEnabled:NO];
    if ([self.delegate respondsToSelector:@selector(spaceAddUser:completion:)]) {
        [self.delegate spaceAddUser:self.user completion:^(BOOL result) {
            if (result) {
                [self.SpaceUserDeleteButton setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
            }
            else{
                [self.SpaceUserDeleteButton setEnabled:YES];
            }
        }];
    }
}
- (void)prepareForReuse{
    for (UIGestureRecognizer *gesture in self.gestureRecognizers) {
        [self removeGestureRecognizer:gesture];
    }
    self.SpaceUserIconView.image = nil;
    self.SpaceUserNameLabel.text = nil;
    self.SpaceUserRoleLabel.text = nil;
    [self.SpaceUserDeleteButton setImage:nil forState:UIControlStateNormal];
    [self.SpaceUserDeleteButton setEnabled:YES];
    self.isChangeOwer = false;
}
@end
@interface SpaceUsersManageViewController () <UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,SpaceUserChange,UIActionSheetDelegate>
@property (nonatomic,strong) UITableView *spaceUsersTableview;
@property (nonatomic,strong) UITableView *searchTableView;
@property (nonatomic,strong) TeamSpace   *space;
@property (nonatomic,strong) NSArray *memberShips;
@property (nonatomic,strong) NSMutableArray *searchUsers;
@property (nonatomic,strong) UIView *SpaceUserSearchTextView;
@property (nonatomic,strong) UIImageView *SpaceUserSearchIconView;
@property (nonatomic,strong) UIActivityIndicatorView *SpaceUserSearchIndicator;
@property (nonatomic,strong) UIButton *SpaceUserSearchClearButton;
@property (nonatomic,strong) UITextField *SpaceUserSearchTextField;
@property (nonatomic,strong) UIButton *spaceUsersDeleteButton;
@property (nonatomic,strong) MemberShip *selectedShip;
@end

@implementation SpaceUsersManageViewController

- (id)initWithSpace:(TeamSpace *)space{
    if (self = [super init]) {
        self.space = space;
        self.searchTableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.searchTableView.delegate = self;
        self.searchTableView.dataSource = self;
        self.spaceUsersTableview = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.spaceUsersTableview.delegate = self;
        self.spaceUsersTableview.dataSource = self;
        self.spaceUsersTableview.backgroundColor = [UIColor clearColor];
        self.spaceUsersTableview.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
        self.spaceUsersTableview.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        self.spaceUsersTableview.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
        self.searchTableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
        [self.spaceUsersTableview registerClass:[SpaceUsersManageCell class] forCellReuseIdentifier:@"spaceUsers"];
        [self.spaceUsersTableview registerClass:[SpaceUsersManageCell class] forCellReuseIdentifier:@"SearchUsers"];
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"成员管理";
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationFrame = self.navigationController.navigationBar.frame;
    [self.view addSubview:self.SpaceUserSearchTextView];
    self.SpaceUserSearchTextView.frame = CGRectMake(0, statusBarFrame.size.height+navigationFrame.size.height, CGRectGetWidth(self.view.frame), 44);
    self.SpaceUserSearchIconView.frame = CGRectMake(15, 11, 22, 22);
    self.SpaceUserSearchIndicator.frame = self.SpaceUserSearchIconView.frame;
    self.SpaceUserSearchTextField.frame = CGRectMake(15+22+4, 11, CGRectGetWidth(self.view.frame)-15-22-4-4-44-4, 22);
    self.SpaceUserSearchClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44);
    self.spaceUsersTableview.frame = CGRectMake(0, CGRectGetMaxY(self.SpaceUserSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.SpaceUserSearchTextView.frame));
    self.spaceUsersTableview.frame = CGRectMake(0, CGRectGetMaxY(self.SpaceUserSearchTextView.frame), self.view.frame.size.width, self.view.frame.size.height - CGRectGetMaxY(self.SpaceUserSearchTextView.frame));
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 50)];
    [footerView addSubview:self.spaceUsersDeleteButton];
    self.spaceUsersTableview.tableFooterView = footerView;
    self.searchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.SpaceUserSearchTextView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-CGRectGetMaxY(self.SpaceUserSearchTextView.frame));
    self.searchTableView.frame = CGRectMake(0, CGRectGetMaxY(self.SpaceUserSearchTextView.frame), self.view.frame.size.width, self.view.frame.size.height - CGRectGetMaxY(self.SpaceUserSearchTextView.frame));
    [self.view addSubview:self.spaceUsersTableview];
    [self.view addSubview:self.searchTableView];
    self.searchTableView.hidden = YES;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
    [self reloadData];
    [self setupRefresh];
    // Do any additional setup after loading the view.
}
- (void)setupRefresh
{
    MJRefreshNormalHeader *header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(reloadData)];
    header.automaticallyChangeAlpha = YES;
    [header beginRefreshing];
    header.stateLabel.hidden = YES;
    self.spaceUsersTableview.mj_header = header;
}
- (void)reloadData{
    [self.space spaceMemberlist:nil succeed:^(id retobj) {
        NSDictionary *shipsInfo = retobj;
        self.memberShips = [MemberShip getShipsFromInfo:shipsInfo];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.spaceUsersTableview.mj_header isRefreshing]) {
                [self.spaceUsersTableview.mj_header endRefreshing];
            }
            [self.spaceUsersTableview reloadData];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.spaceUsersTableview.mj_header isRefreshing]) {
                [self.spaceUsersTableview.mj_header endRefreshing];
            }
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
        });
    }];
}
- (void)spaceSearchIndicatorViewShow {
    if (!self.SpaceUserSearchIndicator.superview) {
        [self.SpaceUserSearchTextView addSubview:self.SpaceUserSearchIndicator];
        self.SpaceUserSearchIconView.hidden = YES;
    }
}

- (void)spaceSearchIndicatorViewHide {
    if (self.SpaceUserSearchIndicator.superview) {
        [self.SpaceUserSearchIndicator removeFromSuperview];
        self.SpaceUserSearchIconView.hidden = NO;
    }
}

- (UIView*)SpaceUserSearchTextView {
    if (!_SpaceUserSearchTextView) {
        _SpaceUserSearchTextView = [[UIView alloc] initWithFrame:CGRectZero];
        _SpaceUserSearchTextView.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
        _SpaceUserSearchTextView.layer.borderWidth = 0.5;
        _SpaceUserSearchTextView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        
        self.SpaceUserSearchIconView = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.SpaceUserSearchIconView.image = [UIImage imageNamed:@"ic_contact_search_nor"];
        [_SpaceUserSearchTextView addSubview:self.SpaceUserSearchIconView];
        
        self.SpaceUserSearchIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectZero];
        self.SpaceUserSearchIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
        [self.SpaceUserSearchIndicator startAnimating];
        
        self.SpaceUserSearchClearButton = [[UIButton alloc] initWithFrame:CGRectZero];
        self.SpaceUserSearchClearButton.imageView.frame = CGRectZero;
        [self.SpaceUserSearchClearButton setImage:[UIImage imageNamed:@"ic_contact_clear_nor"] forState:UIControlStateNormal];
        self.SpaceUserSearchClearButton.hidden = YES;
        [self.SpaceUserSearchClearButton addTarget:self action:@selector(searchTextFieldClear) forControlEvents:UIControlEventTouchUpInside];
        [_SpaceUserSearchTextView addSubview:self.SpaceUserSearchClearButton];
        
        self.SpaceUserSearchTextField = [[UITextField alloc] initWithFrame:CGRectZero];
        [self.SpaceUserSearchTextField setAttributedPlaceholder:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"Search", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"999999" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}]];
        self.SpaceUserSearchTextField.font = [UIFont systemFontOfSize:14.0f];
        self.SpaceUserSearchTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.SpaceUserSearchTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.SpaceUserSearchTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.SpaceUserSearchTextField.delegate = self;
        self.SpaceUserSearchTextField.returnKeyType = UIReturnKeySearch;
        [self.SpaceUserSearchTextField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [_SpaceUserSearchTextView addSubview:self.SpaceUserSearchTextField];
    }
    return _SpaceUserSearchTextView;
}
- (UIButton*)spaceUsersDeleteButton {
    if (!_spaceUsersDeleteButton) {
        _spaceUsersDeleteButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), 44)];
        _spaceUsersDeleteButton.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        _spaceUsersDeleteButton.layer.borderWidth = 0.5;
        _spaceUsersDeleteButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        [_spaceUsersDeleteButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"CloudShareDeleteAll", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"008be8" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:15.0f]}] forState:UIControlStateNormal];
        [_spaceUsersDeleteButton addTarget:self action:@selector(deleteShipsAll) forControlEvents:UIControlEventTouchUpInside];
    }
    return _spaceUsersDeleteButton;
}

- (void)searchTextFieldClear {
    self.SpaceUserSearchTextField.text = nil;
    self.spaceUsersTableview.hidden = NO;
    self.searchTableView.hidden = YES;
    self.SpaceUserSearchClearButton.hidden = YES;
    [self.SpaceUserSearchTextField becomeFirstResponder];
}
- (void)deleteShipsAll{
    if ([self permisson] == false) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"用户无权限", nil)];
        return;
    }
    NSMutableArray *canDeletedShips = [[NSMutableArray alloc] init];
    for (MemberShip *ship in self.memberShips) {
        if (![ship.teamRole isEqualToString:@"admin"]) {
            [canDeletedShips addObject:ship];
        }
    }
    FileMultiOperation *operation = [[FileMultiOperation alloc] init];
    operation.callingObj = [NSSet setWithArray:canDeletedShips];
    operation.completionBlock = ^(NSSet* succeeded, NSSet* failed){
        if (succeeded.count == canDeletedShips.count) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:@"删除成功"];
            });
        }
        [self reloadData];
    };
    for (MemberShip *ship in canDeletedShips) {
        [self.space spaceMemberDelete:[NSDictionary dictionaryWithObject:ship.ShipId forKey:@"memberId"] succeed:^(id retobj) {
            [operation onSuceess:ship];
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            [operation onFailed:ship];
        }];
    }
}
#pragma mark textFiled delegate
- (void)textFieldDidChange:(UITextField*)textField {
    if (textField.text.length == 0) {
//        self.cloudFileShareSearchState = NO;
        self.spaceUsersTableview.hidden = NO;
        self.searchTableView.hidden = YES;
        self.SpaceUserSearchClearButton.hidden = YES;
//        [self.cloudFileShareSearchTableView reloadData];
    } else {
//        self.cloudFileShareSearchState = YES;
        self.SpaceUserSearchClearButton.hidden = NO;
    }
}
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    //[self cloudFileShareSearchBackgroundViewShow];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    self.spaceUsersTableview.hidden = YES;
    self.searchTableView.hidden = NO;
    //[self cloudFileShareSearchBackgroundViewHide];
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
   // [self cloudFileShareSearchBackgroundViewHide];
    if ([textField.text isEqualToString:@""]) {
//        self.cloudFileShareSearchState = NO;
        [self.searchTableView reloadData];
        return;
    }
    [self spaceSearchIndicatorViewShow];
    [self.searchUsers removeAllObjects];
    [User searchUser:textField.text succeed:^(id retobj) {
        [self spaceSearchIndicatorViewHide];
        NSMutableArray *searchContactUser = [[NSMutableArray alloc] initWithArray:[User getUserArrayWithKey:textField.text context:nil]];
        self.searchUsers = [[NSMutableArray alloc] initWithArray:searchContactUser];
        for (User *user in searchContactUser) {
            if (!user.userCloudId && !user.userLoginName) {
                [self.searchUsers removeObject:user];
            }
            for (MemberShip *ship in self.memberShips) {
                if ([ship.userId isEqualToString:user.userCloudId]) {
                    [self.searchUsers removeObject:user];
                }
            }
        }
        [self.searchTableView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        [self spaceSearchIndicatorViewHide];
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"ContactSearchFailed", nil)];
        });
    }];
}
#pragma spaceUserChangeDelegate
- (void)spaceAddUser:(User *)user completion:(userChangeBlock)block{
    if ([self permisson] == false) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"用户无权限", nil)];
        block(NO);
        return;
    }
    NSDictionary *userInfo = [NSDictionary dictionaryWithObject:user.userCloudId forKey:@"userId"];
    [self.space spaceMemberAdd:userInfo succeed:^(id retobj) {
        block(YES);
        dispatch_async(dispatch_get_main_queue(), ^{
            [self reloadData];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        block(NO);
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
        });
    }];
}
- (void)spaceDeleteUser:(MemberShip *)ship completion:(userChangeBlock)block{
    if ([self permisson] == false) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"用户无权限", nil)];
        block(NO);
        return;
    }
    NSDictionary *userInfo = [NSDictionary dictionaryWithObject:ship.ShipId forKey:@"memberId"];
    [self.space spaceMemberDelete:userInfo succeed:^(id retobj) {
        block(YES);
        dispatch_async(dispatch_get_main_queue(), ^{
            [self reloadData];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        block(NO);
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
        });
    }];
}
#pragma gesture target
- (void)changeAuthority:(UIGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateEnded) {
        SpaceUsersManageCell *cell = (SpaceUsersManageCell *)sender.view;
        self.selectedShip = cell.ship;
        UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedString(@"编辑者", nil),NSLocalizedString(@"管理者", nil),NSLocalizedString(@"可上传可查看", nil),NSLocalizedString(@"上传者", nil),NSLocalizedString(@"查看者", nil),NSLocalizedString(@"预览者", nil), nil];
        [sheet showInView:self.view];
    }
}
#pragma actionsheetDelegate
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    if ([self permisson] == false) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"用户无权限", nil)];
        return;
    }
    NSString *authority;
    switch (buttonIndex) {
        case 0:
            authority = @"editor";
            break;
        case 1:
            authority = @"auther";
            break;
        case 2:
            authority = @"uploadAndView";
            break;
        case 3:
            authority = @"uploader";
            break;
        case 4:
            authority = @"viewer";
            break;
        case 5:
            authority = @"previewer";
            break;
        default:
            break;
    }
    if (!authority) {
        return;
    }
    NSDictionary *dic = [NSDictionary dictionaryWithObjectsAndKeys:self.selectedShip.ShipId,@"memberId",self.selectedShip.teamRole,@"teamRole",authority,@"role", nil];
    [self.space spaceMemberUpdate:dic succeed:^(id retobj) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self reloadData];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
        });
    }];
}
#pragma permissionCheck
- (bool)permisson{
    NSString *userCloudId = [UserSetting defaultSetting].cloudUserCloudId.stringValue;
    for (MemberShip *ship in self.memberShips) {
        if ([ship.userId isEqualToString:userCloudId]) {
            if ([ship.role isEqualToString:@"auther"]) {
                return true;
            }
        }
    }
    return false;
}
#pragma tableview Datasource + Delegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (tableView == self.searchTableView) {
        return self.searchUsers.count;
    }
    else{
    return self.memberShips.count;
    }
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    SpaceUsersManageCell *cell;
    if (tableView == self.searchTableView) {
        cell = [tableView dequeueReusableCellWithIdentifier:@"SearchUsers"];
        if (!cell) {
            cell = [[SpaceUsersManageCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"SearchUsers"];
        }
    }
    else{
        cell = [tableView dequeueReusableCellWithIdentifier:@"spaceUsers"];
    if (!cell) {
        cell = [[SpaceUsersManageCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"spaceUsers"];
    }
    }
    if (tableView == self.searchTableView) {
        cell.user = self.searchUsers[indexPath.row];
    }
    else{
       cell.ship = self.memberShips[indexPath.row];
        UILongPressGestureRecognizer *longGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(changeAuthority:)];
        [cell addGestureRecognizer:longGesture];
        
    }
    cell.delegate = self;
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 55.0f;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
