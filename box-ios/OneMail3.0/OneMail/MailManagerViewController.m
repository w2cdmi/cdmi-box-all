//
//  MailManagerViewController.m
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailManagerViewController.h"
#import "Session.h"
#import "User.h"
#import "AppDelegate.h"
#import "MailManagerUserViewController.h"

@interface MailManagerUserCell ()

@property (nonatomic, strong) UIImageView *contactHeaderBackground;
@property (nonatomic, strong) UIImageView *contactHeaderImage;
@property (nonatomic, strong) UILabel *contactNameLabel;
@property (nonatomic, strong) UIImageView *contactHeaderDeleteIcon;

@property (nonatomic, assign) BOOL contactDeleteState;

@end

@implementation MailManagerUserCell

- (void)setUserEmail:(NSString *)userEmail {
    _userEmail = userEmail;
    User *user = [User getUserWithUserEmail:userEmail context:nil];
    
    self.contentView.backgroundColor = [UIColor clearColor];
    
    self.contactHeaderBackground = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 56, 56)];
    self.contactHeaderBackground.image = [UIImage imageNamed:@"img_user_frame"];
    [self.contentView addSubview:self.contactHeaderBackground];
    
    self.contactHeaderImage = [[UIImageView alloc] initWithFrame:CGRectMake(2, 2, 52, 52)];
    self.contactHeaderImage.image = [UIImage imageNamed:@"img_user_default"];
    [self.contactHeaderBackground addSubview:self.contactHeaderImage];
    
    self.contactHeaderDeleteIcon = [[UIImageView alloc] initWithFrame:CGRectMake(56-18, 0, 18, 18)];
    self.contactHeaderDeleteIcon.image = [UIImage imageNamed:@"ic_delete_group_member_small"];
    [self.contactHeaderBackground addSubview:self.contactHeaderDeleteIcon];
    self.contactHeaderDeleteIcon.hidden = YES;
    
    self.contactNameLabel = [CommonFunction labelWithFrame:CGRectMake(0, CGRectGetMaxY(self.contactHeaderBackground.frame)+5, CGRectGetWidth(self.frame), 15) textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.contactNameLabel.text = user.userName;
    [self.contentView addSubview:self.contactNameLabel];
}

- (void)setContactDeleteState:(BOOL)contactDeleteState {
    _contactDeleteState = contactDeleteState;
    if (contactDeleteState) {
        self.contactHeaderDeleteIcon.hidden = NO;
    } else {
        self.contactHeaderDeleteIcon.hidden = YES;
    }
}

- (void)reuse{
    self.userEmail = nil;
    self.contactDeleteState = NO;
    self.contactHeaderDeleteIcon = nil;
    self.contactHeaderBackground = nil;
    self.contactHeaderImage = nil;
    self.contactNameLabel = nil;
    for (UIView *view in self.contentView.subviews) {
        [view removeFromSuperview];
    }
}

@end


@interface MailManagerViewController ()<UICollectionViewDataSource,UICollectionViewDelegate,UITableViewDelegate,UITableViewDataSource,MailManagerUserDelegate>

@property (nonatomic, strong) Session *session;

@property (nonatomic, strong) UILabel *mailManagerTitle;
@property (nonatomic, strong) UIButton *mailManagerBackButton;

@property (nonatomic, strong) UITableView *mailManagerTableView;
@property (nonatomic, strong) UICollectionView *mailManagerCollectionView;
@property (nonatomic, strong) UITableViewCell *mailManagerCollectionCell;
@property (nonatomic, strong) UITableViewCell *mailManagerStickyCell;
@property (nonatomic, strong) UITableViewCell *mailManagerNotificationCell;
@property (nonatomic, strong) UITableViewCell *mailManagerScreenNameCell;

@property (nonatomic, assign) BOOL mailManagerUserDeleteState;

@end

@implementation MailManagerViewController

- (id)initWithSession:(Session *)session {
    self = [super init];
    if (self) {
        self.session = session;
        self.mailManagerUserDeleteState = NO;
        
        NSArray *userArray = [session.sessionUsers componentsSeparatedByString:@","];
        self.sessionUserArray = [[NSMutableArray alloc] initWithArray:userArray];
        [self.sessionUserArray removeObject:[[UserSetting defaultSetting] emailAddress]];
        
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.mailManagerTitle = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailManagerTitle.text = self.session.sessionTitle;
    
    self.mailManagerBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailManagerBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailManagerBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.mailManagerBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.mailManagerBackButton addTarget:self action:@selector(mailManagerBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    self.mailManagerTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height) style:UITableViewStyleGrouped];
    self.mailManagerTableView.delegate = self;
    self.mailManagerTableView.dataSource = self;
    self.mailManagerTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.mailManagerTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    [self.view addSubview:self.mailManagerTableView];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.mailManagerTitle];
    [self.navigationController.navigationBar addSubview:self.mailManagerBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
    [self mailManagerUserChange];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailManagerTitle removeFromSuperview];
    [self.mailManagerBackButton removeFromSuperview];
}

- (void)mailManagerBackButtonClick {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)mailManagerUserChange {
    NSMutableArray *mailManagerUserNameArray = [[NSMutableArray alloc] init];
    for (NSString *userEmail in self.sessionUserArray) {
        User *user = [User getUserWithUserEmail:userEmail context:nil];
        [mailManagerUserNameArray addObject:user.userName];
    }
    NSString *mailUserNameString = [CommonFunction stringFromArray:(NSArray*)mailManagerUserNameArray];
    [self.session saveSessionTitle:mailUserNameString];
    
    [self.sessionUserArray addObject:[[UserSetting defaultSetting] emailAddress]];
    NSString *mailUserString = [CommonFunction stringFromArray:(NSArray*)self.sessionUserArray];
    [self.session saveSessionUsers:mailUserString];
    [self.sessionUserArray removeObject:[[UserSetting defaultSetting] emailAddress]];
    
    [self setMailManagerCollectionViewFrame];
    [self.mailManagerCollectionView reloadData];
    [self setMailManagerCollectionCellFrame];
    [self.mailManagerTableView reloadData];
    
    self.mailManagerTitle.text = self.session.sessionTitle;
}

- (UITableViewCell*)mailManagerCollectionCell {
    if (!_mailManagerCollectionCell) {
        _mailManagerCollectionCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _mailManagerCollectionCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _mailManagerCollectionCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        [_mailManagerCollectionCell addSubview:self.mailManagerCollectionView];
    }
    return _mailManagerCollectionCell;
}

- (UICollectionView*)mailManagerCollectionView {
    if (!_mailManagerCollectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.itemSize = CGSizeMake(56, 76);
        flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
        flowLayout.minimumInteritemSpacing = floor((CGRectGetWidth(self.view.frame)-4*56)/5);
        flowLayout.minimumLineSpacing = 20;
        
        _mailManagerCollectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _mailManagerCollectionView.backgroundColor = [UIColor clearColor];
        _mailManagerCollectionView.delegate = self;
        _mailManagerCollectionView.dataSource = self;
        [_mailManagerCollectionView registerClass:[MailManagerUserCell class] forCellWithReuseIdentifier:@"MailManagerUserCell"];
    }
    return _mailManagerCollectionView;
}

- (void)setMailManagerCollectionViewFrame {
    UICollectionViewFlowLayout *flowLayout = (UICollectionViewFlowLayout*)self.mailManagerCollectionView.collectionViewLayout;
    NSInteger lineNumber = ceil(((float)self.sessionUserArray.count+2)/4);
    CGFloat collectionWidth = 4*flowLayout.itemSize.width+3*flowLayout.minimumInteritemSpacing;
    CGFloat collectionHeight = lineNumber*flowLayout.itemSize.height+(lineNumber-1)*flowLayout.minimumLineSpacing;
    self.mailManagerCollectionView.frame = CGRectMake((CGRectGetWidth(self.view.frame)-collectionWidth)/2, 15, collectionWidth, collectionHeight);
}

- (void)setMailManagerCollectionCellFrame {
    self.mailManagerCollectionCell.frame = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.mailManagerCollectionView.frame)+15+15);
}

- (UITableViewCell*)mailManagerStickyCell {
    if (!_mailManagerStickyCell) {
        _mailManagerStickyCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _mailManagerStickyCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _mailManagerStickyCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        UISwitch *switchButton = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, 4, 51, 36)];
        [switchButton setOn:self.session.sessionTopFlag.boolValue];
        [switchButton addTarget:self action:@selector(mailStickyChange:) forControlEvents:UIControlEventValueChanged];
        [_mailManagerStickyCell addSubview:switchButton];
        
        UILabel *titleLabel = [CommonFunction labelWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        titleLabel.text = NSLocalizedString(@"MailStickyTop", nil);
        [_mailManagerStickyCell addSubview:titleLabel];
    }
    return _mailManagerStickyCell;
}

- (void)mailStickyChange:(UISwitch*)switchButton {
    [self.session saveSessionTopFlag:@(switchButton.on)];
}

- (UITableViewCell*)mailManagerNotificationCell {
    if (!_mailManagerNotificationCell) {
        _mailManagerNotificationCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _mailManagerNotificationCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _mailManagerNotificationCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        UISwitch *switchButton = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, 4, 51, 36)];
        [switchButton setOn:self.session.sessionNotification.boolValue];
        [switchButton addTarget:self action:@selector(mailNotificationChange:) forControlEvents:UIControlEventValueChanged];
        [_mailManagerNotificationCell addSubview:switchButton];
        
        UILabel *titleLabel = [CommonFunction labelWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        titleLabel.text = NSLocalizedString(@"MailNotifications", nil);
        [_mailManagerNotificationCell addSubview:titleLabel];
    }
    return _mailManagerNotificationCell;
}

- (void)mailNotificationChange:(UISwitch*)switchButton {
    [self.session saveSessionNotification:@(switchButton.on)];
}

- (UITableViewCell*)mailManagerScreenNameCell {
    if (!_mailManagerScreenNameCell) {
        _mailManagerScreenNameCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _mailManagerScreenNameCell.selectionStyle = UITableViewCellSelectionStyleNone;
        _mailManagerScreenNameCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        UISwitch *switchButton = [[UISwitch alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-15-51, 4, 51, 36)];
        [switchButton setOn:self.session.sessionScreenName.boolValue];
        [switchButton addTarget:self action:@selector(mailScreenNameChange:) forControlEvents:UIControlEventValueChanged];
        [_mailManagerScreenNameCell addSubview:switchButton];
        
        UILabel *titleLabel = [CommonFunction labelWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.view.frame)-15-15-51-10, 22) textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        titleLabel.text = NSLocalizedString(@"MailScreenName", nil);
        [_mailManagerScreenNameCell addSubview:titleLabel];
    }
    return _mailManagerScreenNameCell;
}

- (void)mailScreenNameChange:(UISwitch*)switchButton {
    [self.session saveSessionScreenName:@(switchButton.on)];
}

#pragma mark collection delegate
- (MailManagerUserCell*)mailContactPlusCell:(MailManagerUserCell*)cell {
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 56, 56)];
    if (self.mailManagerUserDeleteState) {
        [button setImage:[UIImage imageNamed:@"ic_add_group_member_press"] forState:UIControlStateNormal];
    } else {
        [button setImage:[UIImage imageNamed:@"ic_add_group_member_nor"] forState:UIControlStateNormal];
        [button setImage:[UIImage imageNamed:@"ic_add_group_member_press"] forState:UIControlStateHighlighted];
        [button addTarget:self action:@selector(addMailUser) forControlEvents:UIControlEventTouchUpInside];
    }
    [cell.contentView addSubview:button];
    return cell;
}

- (void)addMailUser {
    MailManagerUserViewController *userViewController = [[MailManagerUserViewController alloc] initWithSession:self.session];
    userViewController.delegate = self;
    [self.navigationController pushViewController:userViewController animated:YES];
}

- (void)completeMailUsers:(NSArray *)userEmailArray {
    [self.sessionUserArray removeAllObjects];
    [self.sessionUserArray addObjectsFromArray:userEmailArray];
    [self mailManagerUserChange];
}

- (MailManagerUserCell*)mailContactMinusCell:(MailManagerUserCell*)cell {
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 56, 56)];
    [button setImage:[UIImage imageNamed:@"ic_delete_group_member_nor"] forState:UIControlStateNormal];
    [button setImage:[UIImage imageNamed:@"ic_delete_group_member_press"] forState:UIControlStateHighlighted];
    [button addTarget:self action:@selector(deleteMailUser) forControlEvents:UIControlEventTouchUpInside];
    [cell.contentView addSubview:button];
    return cell;
}

- (void)deleteMailUser {
    if (self.mailManagerUserDeleteState) {
        self.mailManagerUserDeleteState = NO;
    } else {
        self.mailManagerUserDeleteState = YES;
    }
    [self.mailManagerCollectionView reloadData];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    if (self.sessionUserArray.count < 2) {
        return self.sessionUserArray.count + 1;
    } else {
        return self.sessionUserArray.count + 2;
    }
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    MailManagerUserCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"MailManagerUserCell" forIndexPath:indexPath];
    [cell reuse];
    if (indexPath.row < self.sessionUserArray.count) {
        cell.userEmail = [self.sessionUserArray objectAtIndex:indexPath.row];
        cell.contactDeleteState = self.mailManagerUserDeleteState;
    } else if (indexPath.row == self.sessionUserArray.count) {
        cell = [self mailContactPlusCell:cell];
    } else if (indexPath.row == self.sessionUserArray.count+1) {
        cell = [self mailContactMinusCell:cell];
    } else {
        cell = nil;
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    MailManagerUserCell *cell = (MailManagerUserCell*)[collectionView cellForItemAtIndexPath:indexPath];
    if (cell.contactDeleteState) {
        [self.sessionUserArray removeObject:cell.userEmail];
        if (self.sessionUserArray.count < 2) {
            self.mailManagerUserDeleteState = NO;
        }
        [self mailManagerUserChange];
    }
    
    [collectionView deselectItemAtIndexPath:indexPath animated:YES];
}

#pragma mark tableView delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 1) {
        return 3;
    } else {
        return 1;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return 0.1f;
    } else {
        return 20.0f;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.section == 0) {
        return self.mailManagerCollectionCell.frame.size.height;
    } else {
        return 44;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return self.mailManagerCollectionCell;
    } else {
        if (indexPath.row == 0) {
            return self.mailManagerStickyCell;
        } else if (indexPath.row == 1) {
            return self.mailManagerNotificationCell;
        } else if (indexPath.row == 2) {
            return self.mailManagerScreenNameCell;
        }
    }
    return nil;
}
@end
