//
//  MailSessionViewController.m
//  OneMail
//
//  Created by cse  on 16/1/18.
//  Copyright (c) 2016年 cse. All rights reserved.
//
#define headerImageToTopDistance 6
#define headerImageToLeftDistance 15
#define headerImageToRightDistance 10
#define headerImageToButtomDistance 6
#define headerImageFrame 52
#define headerImageBGFrame 56
#define sessionHeadIconBackgroundWitdh 28
#define sessionHeadIconBackgroundHeight 28
#define sessionHeadIconWitdh 24
#define sessionHeadIconHeight 24
#define sessionHeadIconToTopDistance 2
#define sessionHeadIconToLeftDistance 2

#import "MailSessionViewController.h"
#import "MailMessageViewController.h"
#import "MailContactViewController.h"
#import "AppDelegate.h"
#import <CoreData/CoreData.h>
#import "Session.h"
#import "Message.h"
#import "MenuViewController.h"
#import "MessageLoadOperation.h"
#import "MessageParseOperation.h"
#import "MessageIMAPSession.h"
#import "AttachmentUploadOperation.h"
#import "User.h"
#import "UserThumbnail.h"
#import "UserSetting.h"

@interface MailSessionCell ()

@property (nonatomic, strong) UILabel     *mailSessionUsersLabel;
@property (nonatomic, strong) UILabel     *mailSessionMessageLabel;
@property (nonatomic, strong) UILabel     *mailSessionDateLabel;
@property (nonatomic, strong) UILabel     *mailSessionUnreadLabel;

@property (nonatomic, strong) NSDateFormatter *mailSessionDateDayFormatter;
@property (nonatomic, strong) NSDateFormatter *mailSessionDateTimeFormatter;

@property (nonatomic, strong) UIView      *mailSessionIconView;
@property (nonatomic, strong) UIImageView *mailSessionFirstUserIcon;
@property (nonatomic, strong) UIImageView *mailSessionSecondUserIcon;
@property (nonatomic, strong) UIImageView *mailSessionThirdUserIcon;
@property (nonatomic, strong) UIImageView *mailSessionFourthUserIcon;
@property (nonatomic, strong) UIImageView *mailSessionFirstIconBackground;
@property (nonatomic, strong) UIImageView *mailSessionSecondIconBackground;
@property (nonatomic, strong) UIImageView *mailSessionThirdIconBackground;
@property (nonatomic, strong) UIImageView *mailSessionFourthIconBackground;

@end

@implementation MailSessionCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.mailSessionIconView = [[UIView alloc] initWithFrame:CGRectMake(15, 6, 56, 56)];
        [self.contentView addSubview:self.mailSessionIconView];
        
        self.mailSessionUnreadLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.mailSessionIconView.frame)-18, 0, 18, 18)];
        self.mailSessionUnreadLabel.backgroundColor = [CommonFunction colorWithString:@"ff6b21" alpha:1.0f];
        self.mailSessionUnreadLabel.layer.cornerRadius = 18/2;
        self.mailSessionUnreadLabel.layer.masksToBounds = YES;
        self.mailSessionUnreadLabel.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        self.mailSessionUnreadLabel.font = [UIFont systemFontOfSize:12.0f];
        self.mailSessionUnreadLabel.textAlignment = NSTextAlignmentCenter;
        [self.mailSessionIconView addSubview:self.mailSessionUnreadLabel];
        
        self.mailSessionUsersLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.mailSessionUsersLabel];
        
        self.mailSessionMessageLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:15.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.mailSessionMessageLabel];
        
        self.mailSessionDateLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentRight];
        [self.contentView addSubview:self.mailSessionDateLabel];
        
        self.mailSessionDateDayFormatter = [[NSDateFormatter alloc] init];
        [self.mailSessionDateDayFormatter setDateFormat:@"MM/dd/yy"];
        
        self.mailSessionDateTimeFormatter = [[NSDateFormatter alloc] init];
        [self.mailSessionDateTimeFormatter setDateFormat:@"HH:mm"];
        
        [self initSessionIconView];
    }
    return self;
}

- (void)initSessionIconView {
    self.mailSessionFirstUserIcon = [[UIImageView alloc]initWithFrame:CGRectMake(1, 1, 26, 26)];
    self.mailSessionFirstIconBackground = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 28, 28)];
    self.mailSessionFirstIconBackground.layer.cornerRadius = 28/2;
    self.mailSessionFirstIconBackground.layer.masksToBounds = YES;
    self.mailSessionFirstUserIcon.layer.cornerRadius = 26/2;
    self.mailSessionFirstUserIcon.layer.masksToBounds = YES;
    self.mailSessionFirstIconBackground.image = [UIImage imageNamed:@"img_portrait_frame"];
    [self.mailSessionIconView addSubview:self.mailSessionFirstIconBackground];
    [self.mailSessionFirstIconBackground addSubview:self.mailSessionFirstUserIcon];
    
    self.mailSessionSecondUserIcon = [[UIImageView alloc] initWithFrame:CGRectMake(1, 1, 26, 26)];
    self.mailSessionSecondIconBackground = [[UIImageView alloc] initWithFrame:CGRectMake(28, 0, 28, 28)];
    self.mailSessionSecondIconBackground.layer.cornerRadius = 28/2;
    self.mailSessionSecondIconBackground.layer.masksToBounds = YES;
    self.mailSessionSecondUserIcon.layer.cornerRadius = 26/2;
    self.mailSessionSecondUserIcon.layer.masksToBounds = YES;
    self.mailSessionSecondIconBackground.image = [UIImage imageNamed:@"img_portrait_frame"];
    [self.mailSessionIconView addSubview:self.mailSessionSecondIconBackground];
    [self.mailSessionSecondIconBackground addSubview:self.mailSessionSecondUserIcon];
    
    self.mailSessionThirdUserIcon = [[UIImageView alloc] initWithFrame:CGRectMake(1, 1, 26, 26)];
    self.mailSessionThirdIconBackground = [[UIImageView alloc] initWithFrame:CGRectMake(0, 28, 28, 28)];
    self.mailSessionThirdIconBackground.layer.cornerRadius = 28/2;
    self.mailSessionThirdIconBackground.layer.masksToBounds = YES;
    self.mailSessionThirdUserIcon.layer.cornerRadius = 26/2;
    self.mailSessionThirdUserIcon.layer.masksToBounds = YES;
    self.mailSessionThirdIconBackground.image = [UIImage imageNamed:@"img_portrait_frame"];
    [self.mailSessionIconView addSubview:self.mailSessionThirdIconBackground];
    [self.mailSessionThirdIconBackground addSubview:self.mailSessionThirdUserIcon];
    
    self.mailSessionFourthUserIcon = [[UIImageView alloc] initWithFrame:CGRectMake(1, 1, 26, 26)];
    self.mailSessionFourthIconBackground = [[UIImageView alloc] initWithFrame:CGRectMake(28, 28, 28, 28)];
    self.mailSessionFourthIconBackground.layer.cornerRadius = 28/2;
    self.mailSessionFourthIconBackground.layer.masksToBounds = YES;
    self.mailSessionFourthUserIcon.layer.cornerRadius = 26/2;
    self.mailSessionFourthUserIcon.layer.masksToBounds = YES;
    self.mailSessionFourthIconBackground.image = [UIImage imageNamed:@"img_portrait_frame"];
    [self.mailSessionIconView addSubview:self.mailSessionFourthIconBackground];
    [self.mailSessionFourthIconBackground addSubview:self.mailSessionFourthUserIcon];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    NSMutableArray *sessionUserEmailAddresses  = [[NSMutableArray alloc] initWithArray:[self.session.sessionUsers componentsSeparatedByString:@","]];
    [sessionUserEmailAddresses removeObject:[[UserSetting defaultSetting] emailAddress]];
    [self refreshHeadImageViewFrame:sessionUserEmailAddresses];
    
    self.mailSessionUsersLabel.frame = CGRectMake(15+56+10, 11, CGRectGetWidth(self.frame)-15-56-10-15-50-10, 22);
    self.mailSessionUsersLabel.text = self.session.sessionTitle;
    
    self.mailSessionMessageLabel.frame = CGRectMake(CGRectGetMinX(self.mailSessionUsersLabel.frame), CGRectGetMaxY(self.mailSessionUsersLabel.frame)+4, CGRectGetWidth(self.frame)-15-56-10-15, 20);
    Message *lastMessage = [Message getMessageWithMessageId:self.session.sessionLastMessageId ctx:nil];
    self.mailSessionMessageLabel.text = lastMessage.messageTitle;
    
    self.mailSessionDateLabel.frame = CGRectMake(CGRectGetWidth(self.frame)-15-50, 11, 50, 22);
    NSString *dateDay = [self.mailSessionDateDayFormatter stringFromDate:[NSDate date]];
    Message *sessionLastMessage = [Message getMessageWithMessageId:self.session.sessionLastMessageId ctx:nil];
    NSString *sessionDateDay = [self.mailSessionDateDayFormatter stringFromDate:sessionLastMessage.messageSendDate];
    if ([sessionDateDay isEqualToString:dateDay]) {
        self.mailSessionDateLabel.text = [self.mailSessionDateTimeFormatter stringFromDate:sessionLastMessage.messageSendDate];
    } else {
        self.mailSessionDateLabel.text = sessionDateDay;
    }
    
    NSInteger mailSessionUnread = [self.session sessionUnreadMessageCount];
    if (mailSessionUnread > 0) {
        self.mailSessionUnreadLabel.hidden = NO;
        self.mailSessionUnreadLabel.text = [NSString stringWithFormat:@"%ld",(long)mailSessionUnread];
    } else {
        self.mailSessionUnreadLabel.hidden = YES;
    }
    [self.mailSessionIconView bringSubviewToFront:self.mailSessionUnreadLabel];
}

- (void)refreshHeadImageViewFrame:(NSArray*)userEmailArray {
    if (userEmailArray.count == 1) {
        self.mailSessionFirstIconBackground.frame = CGRectMake(0, 0, 56, 56);
        self.mailSessionFirstIconBackground.layer.cornerRadius = 56/2;
        self.mailSessionFirstIconBackground.layer.masksToBounds = YES;
        self.mailSessionFirstUserIcon.frame = CGRectMake(2, 2, 52, 52);
        self.mailSessionFirstUserIcon.layer.cornerRadius = 52/2;
        self.mailSessionFirstUserIcon.layer.masksToBounds = YES;
        User *user = [User getUserWithUserEmail:[userEmailArray objectAtIndex:0] context:nil];
        [UserThumbnail imageWithUser:user imageView:self.mailSessionFirstUserIcon refresh:NO];
        self.mailSessionFirstIconBackground.hidden = NO;
        self.mailSessionSecondIconBackground.hidden = YES;
        self.mailSessionThirdIconBackground.hidden = YES;
        self.mailSessionFourthIconBackground.hidden = YES;
    }
    if (userEmailArray.count == 2) {
        self.mailSessionFirstIconBackground.frame = CGRectMake((CGRectGetWidth(self.mailSessionIconView.frame)-28)/2, 0, 28, 28);
        self.mailSessionFirstIconBackground.layer.cornerRadius = 28/2;
        self.mailSessionFirstIconBackground.layer.masksToBounds = YES;
        self.mailSessionFirstUserIcon.frame = CGRectMake(1, 1, 26, 26);
        self.mailSessionFirstUserIcon.layer.cornerRadius = 26/2;
        self.mailSessionFirstUserIcon.layer.masksToBounds = YES;
        User *firstUser = [User getUserWithUserEmail:[[UserSetting defaultSetting] emailAddress] context:nil];
        [UserThumbnail imageWithUser:firstUser imageView:self.mailSessionFirstUserIcon refresh:NO];
        
        User *secondUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:0] context:nil];
        [UserThumbnail imageWithUser:secondUser imageView:self.mailSessionThirdUserIcon refresh:NO];
        
        User *thirdUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:1] context:nil];
        [UserThumbnail imageWithUser:thirdUser imageView:self.mailSessionFourthUserIcon refresh:NO];

        self.mailSessionFirstIconBackground.hidden = NO;
        self.mailSessionSecondIconBackground.hidden = YES;
        self.mailSessionThirdIconBackground.hidden = NO;
        self.mailSessionFourthIconBackground.hidden = NO;
    }
    if (userEmailArray.count > 2) {
        self.mailSessionFirstIconBackground.frame = CGRectMake(0, 0, 28, 28);
        self.mailSessionFirstIconBackground.layer.cornerRadius = 28/2;
        self.mailSessionFirstIconBackground.layer.masksToBounds = YES;
        self.mailSessionFirstUserIcon.frame = CGRectMake(1, 1, 26, 26);
        self.mailSessionFirstUserIcon.layer.cornerRadius = 26/2;
        self.mailSessionFirstUserIcon.layer.masksToBounds = YES;
        User *firstUser = [User getUserWithUserEmail:[[UserSetting defaultSetting] emailAddress] context:nil];
        [UserThumbnail imageWithUser:firstUser imageView:self.mailSessionFirstUserIcon refresh:NO];
        
        User *secondUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:0] context:nil];
        [UserThumbnail imageWithUser:secondUser imageView:self.mailSessionSecondUserIcon refresh:NO];
        
        User *thirdUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:1] context:nil];
        [UserThumbnail imageWithUser:thirdUser imageView:self.mailSessionThirdUserIcon refresh:NO];
        
        User *forthUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:2] context:nil];
        [UserThumbnail imageWithUser:forthUser imageView:self.mailSessionFourthUserIcon refresh:NO];

        self.mailSessionFirstIconBackground.hidden = NO;
        self.mailSessionSecondIconBackground.hidden = NO;
        self.mailSessionThirdIconBackground.hidden = NO;
        self.mailSessionFourthIconBackground.hidden = NO;
    }
}

- (void)refresh {
    [self layoutSubviews];
}

- (void)setSession:(Session *)session {
    _session = session;
    
}

- (void)prepareForReuse {
    [super prepareForReuse];
    self.mailSessionUsersLabel.text = nil;
    self.mailSessionMessageLabel.text = nil;
    self.mailSessionDateLabel.text = nil;
}

@end


@interface MailSessionViewController ()<NSFetchedResultsControllerDelegate,UITableViewDataSource,UITableViewDelegate>

@property (nonatomic, strong) UILabel                    *mailTitleLable;
@property (nonatomic, strong) UIView                     *mailIndicatorView;
@property (nonatomic, strong) UIButton                   *mailSettingButton;
@property (nonatomic, strong) UIButton                   *mailSearchButton;

@property (nonatomic, strong) UITableView                *mailSessionTableView;
@property (nonatomic, strong) NSFetchedResultsController *mailFetchController;
@property (nonatomic, strong) UIButton                   *mailAddButton;

@end

@implementation MailSessionViewController

- (id)init {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sessionOwner = %@",appDelegate.localManager.userCloudId];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        NSSortDescriptor *sortTopDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"sessionTopFlag" ascending:NO];
        NSSortDescriptor *sortDateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"sessionSyncDate" ascending:NO];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setPredicate:predicate];
        [request setEntity:entity];
        [request setSortDescriptors:@[sortTopDescriptor,sortDateDescriptor]];
        self.mailFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
        self.mailFetchController.delegate = self;
        [self performFetch];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mailLoad) name:@"mail.login.success" object:nil];
    };
    return self;
}

- (void)performFetch {
    NSError *error = NULL;
    if (![self.mailFetchController performFetch:&error]) {
        NSLog(@"Unresolved error %@,%@",error,[error userInfo]);
        abort();
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    
    self.mailTitleLable = [CommonFunction labelWithFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.mailTitleLable.text = NSLocalizedString(@"MailTitle", nil);
    
    self.mailSettingButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.mailSettingButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_nor"] forState:UIControlStateNormal];
    [self.mailSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_press"] forState:UIControlStateHighlighted];
    [self.mailSettingButton addTarget:self action:@selector(mailSettingButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.mailSearchButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
    self.mailSearchButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.mailSearchButton setImage:[UIImage imageNamed:@"ic_nav_search_nor"] forState:UIControlStateNormal];
    [self.mailSearchButton setImage:[UIImage imageNamed:@"ic_nav_search_press"] forState:UIControlStateHighlighted];
    [self.mailSearchButton addTarget:self action:@selector(mailSearchButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    self.mailSessionTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-49) style:UITableViewStylePlain];
    [self.mailSessionTableView registerClass:[MailSessionCell class] forCellReuseIdentifier:@"MailSessionCell"];
    self.mailSessionTableView.backgroundColor = [UIColor clearColor];
    self.mailSessionTableView.dataSource = self;
    self.mailSessionTableView.delegate = self;
    self.mailSessionTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.mailSessionTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.mailSessionTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.mailSessionTableView];
    
    CGRect tabBarFrame = appDelegate.mainTabBar.frame;
    self.mailAddButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-10-65, CGRectGetHeight(self.view.frame)-tabBarFrame.size.height-10-65, 65, 65)];
    self.mailAddButton.hidden = NO;
    [self.mailAddButton setImage:[UIImage imageNamed:@"btn_new_nor"] forState:UIControlStateNormal];
    [self.mailAddButton addTarget:self action:@selector(addNewSession) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.mailAddButton];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!userSetting.emailBinded.boolValue) {
        [self.navigationController popViewControllerAnimated:NO];
    } else {
        [self.navigationController.navigationBar addSubview:self.mailTitleLable];
        [self.navigationController.navigationBar addSubview:self.mailSettingButton];
        [self.navigationController.navigationBar addSubview:self.mailSearchButton];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabShow" object:nil];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.mailTitleLable removeFromSuperview];
    [self.mailSettingButton removeFromSuperview];
    [self.mailSearchButton removeFromSuperview];
}

- (void)mailSettingButtonClick {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (appDelegate.LeftSlideVC.closed) {
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshUserIcon];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshEmailAddress];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshTransferTaskCount];
        appDelegate.leftViewOpened = YES;
        [appDelegate.LeftSlideVC openLeftView];
    } else {
        appDelegate.leftViewOpened = NO;
        [appDelegate.LeftSlideVC closeLeftView];
    }
}
- (void)addNewSession{
    MailContactViewController *searchController = [[MailContactViewController alloc] init];
    [self.navigationController pushViewController:searchController animated:YES];
}
- (void)mailSearchButtonClick {
    
}
#pragma mark indicatorView
- (UIView*)mailIndicatorView {
    if (!_mailIndicatorView) {
        _mailIndicatorView = [[UIView alloc] initWithFrame:CGRectZero];
        
        UIActivityIndicatorView *indicatorView = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
        indicatorView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhite;
        [indicatorView startAnimating];
        [_mailIndicatorView addSubview:indicatorView];
        
        CGSize adjustLabelSize = [CommonFunction labelSizeWithString:NSLocalizedString(@"MailLoading", nil) font:[UIFont systemFontOfSize:18.0f]];
        UILabel *loadingLabel = [CommonFunction labelWithFrame:CGRectMake(0, 0, adjustLabelSize.width, 44) textFont:[UIFont systemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
        loadingLabel.text = NSLocalizedString(@"MailLoading", nil);
        [_mailIndicatorView addSubview:loadingLabel];
        
        _mailIndicatorView.bounds = CGRectMake(0, 0, CGRectGetWidth(indicatorView.frame)+CGRectGetWidth(loadingLabel.frame), 44);
        
        indicatorView.frame = CGRectMake(0, (CGRectGetHeight(self.mailIndicatorView.frame)-CGRectGetHeight(indicatorView.frame))/2, CGRectGetWidth(indicatorView.frame), CGRectGetHeight(indicatorView.frame));
        
        loadingLabel.frame = CGRectMake(CGRectGetWidth(indicatorView.frame), (CGRectGetHeight(self.mailIndicatorView.frame)-CGRectGetHeight(loadingLabel.frame))/2, CGRectGetWidth(loadingLabel.frame), CGRectGetHeight(loadingLabel.frame));
    }
    return _mailIndicatorView;
}

#pragma mark load mail message
- (void)mailLoad {
    MessageLoadOperation *operation = [[MessageLoadOperation alloc]init];
    operation.completionBlock = ^(NSMutableArray *inboxMessages, NSMutableArray *sentMessages){
        NSMutableArray *messageArray = [[NSMutableArray alloc] init];
        [messageArray addObjectsFromArray:inboxMessages];
        [messageArray addObjectsFromArray:sentMessages];
        messageArray = [NSMutableArray arrayWithArray:[messageArray sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
            NSDictionary *msgdic1 = obj1;
            NSDictionary *msgdic2 = obj2;
            NSDate *date1 = [msgdic1 objectForKey:@"date"];
            NSDate *date2 = [msgdic2 objectForKey:@"date"];
            return [date1 compare:date2];
        }]];
        [self mailParseWithMessageArray:messageArray];
    };
    [operation loadMessages:@"INBOX" completion:nil];
    [operation loadMessages:@"Sent Messages" completion:nil];
}

#pragma mark parse mail message
- (void)mailParseWithMessageArray:(NSArray*)messageArray {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (messageArray.count == 0) {
        if (userSetting.emailFirstLoad.boolValue) {
            userSetting.emailFirstLoad = @(0);
        }
        [self.mailSessionTableView reloadData];
        return;
    }
    if (self.mailTitleLable.superview) {
        self.mailTitleLable.hidden = YES;
    }
    self.navigationItem.titleView = self.mailIndicatorView;
    MessageParseOperation *operation = [[MessageParseOperation alloc] initWithMessagesInfo:messageArray];
    operation.completionBlock = ^(NSArray *messageArray) {
        [self.mailSessionTableView reloadData];
        self.navigationItem.titleView = nil;
        if (self.mailTitleLable.superview) {
            self.mailTitleLable.hidden = NO;
        }
        [self mailNotificationWithMessageArray:messageArray];
        userSetting.emailFirstLoad = @(0);
        if (userSetting.emailAutoArchiveAttchment) {
            [self backupMessageAttachment:messageArray];
        }
        [UIApplication sharedApplication].applicationIconBadgeNumber = [Message getMessageUnreadCount];
        [self mailMonitor];
    };
    [operation parseMesage:[messageArray objectAtIndex:0]];
}

#pragma mark mail monitor
- (void)mailMonitor {
    UserSetting *userSetting = [UserSetting defaultSetting];
    MessageLoadOperation *operation = [[MessageLoadOperation alloc] init];
    MessageIMAPSession *imapSession = [MessageIMAPSession getSessionInstance];
    if (!imapSession.InBoxIdleOperation) {
        imapSession.InBoxIdleOperation = [[MessageIMAPSession getSessionInstance] idleOperationWithFolder:@"INBOX" lastKnownUID:userSetting.emailLastInboxUid.intValue];
        NSLog(@"inbox lincese %d",userSetting.emailLastInboxUid.intValue);
        [imapSession.InBoxIdleOperation start:^(NSError *error) {
            [operation loadMessages:@"INBOX" completion:^(NSMutableArray *messageArray) {
                [self mailParseWithMessageArray:messageArray];
            }];
            imapSession.InBoxIdleOperation = nil;
            NSLog(@"inbox lincese %d over",userSetting.emailLastInboxUid.intValue);
        }];
    }
    if (!imapSession.SentBoxIdleOperation) {
        imapSession.SentBoxIdleOperation = [[MessageIMAPSession getSessionInstance] idleOperationWithFolder:@"Sent Messages" lastKnownUID:userSetting.emailLastSentBoxUid.intValue];
        NSLog(@"sendbox lincese %d",userSetting.emailLastSentBoxUid.intValue);
        [imapSession.SentBoxIdleOperation start:^(NSError *error) {
            if (error.code != 11) {
                [operation loadMessages:@"Sent Messages" completion:^(NSMutableArray *messageArray) {
                    [self mailParseWithMessageArray:messageArray];
                }];
                imapSession.SentBoxIdleOperation = nil;
                NSLog(@"sendbox lincese %d over",userSetting.emailLastSentBoxUid.intValue);
            }
        }];
    }
}

#pragma mark mail notification
- (void)mailNotificationWithMessageArray:(NSArray*)messageArray {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!userSetting.emailFirstLoad.boolValue) {
        for (NSString *messageId in messageArray) {
            Message *message = [Message getMessageWithMessageId:messageId ctx:nil];
            Session *session = [Session getSessionWithSessionId:message.messageSessionId ctx:nil];
            if (!session.sessionNotification.boolValue) {
                [CommonFunction noticification];
            }
        }
    }
}

#pragma mark attachment Backup
-(void)backupMessageAttachment:(NSArray*)messagesId {
    for (NSString *messageId in messagesId) {
        Message *message = [Message getMessageWithMessageId:messageId ctx:nil];
        AttachmentUploadOperation *attachmentUploadOperation = [[AttachmentUploadOperation alloc] init];
        attachmentUploadOperation.uploadComletion = ^(){};
        [attachmentUploadOperation attachmentsUploadWithMessage:message];
    }
}


#pragma mark tableView dataSource+delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.mailFetchController.fetchedObjects.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MailSessionCell *cell = (MailSessionCell*)[tableView dequeueReusableCellWithIdentifier:@"MailSessionCell"];
    if (!cell) {
        cell = [[MailSessionCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"MailSessionCell"];
    }
    Session *sesion = [self.mailFetchController.fetchedObjects objectAtIndex:indexPath.row];
    cell.session = sesion;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MailSessionCell *cell = (MailSessionCell*)[tableView cellForRowAtIndexPath:indexPath];
    [cell.session sessionUnreadMessageReset];
    [cell refresh];
    [UIApplication sharedApplication].applicationIconBadgeNumber = [Message getMessageUnreadCount];
    MailMessageViewController *messageViewController = [[MailMessageViewController alloc] initWithSession:cell.session];
    [self.navigationController pushViewController:messageViewController animated:YES];
}
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    return YES;
}
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        MailSessionCell *cell = (MailSessionCell*)[tableView cellForRowAtIndexPath:indexPath];
        Session *session = cell.session;
        [session removeSession];
    }
}
#pragma mark fetchedControler delegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    if ([[[UserSetting defaultSetting] emailFirstLoad] boolValue]) return;
    [self.mailSessionTableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    if ([[[UserSetting defaultSetting] emailFirstLoad] boolValue]) return;
    [self.mailSessionTableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    if ([[[UserSetting defaultSetting] emailFirstLoad] boolValue]) return;
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.mailSessionTableView insertRowsAtIndexPaths:[NSArray arrayWithObjects:newIndexPath, nil] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.mailSessionTableView deleteRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath, nil] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeUpdate: {
            [self.mailSessionTableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath, nil] withRowAnimation:UITableViewRowAnimationFade];
            MailSessionCell *sessionCell = (MailSessionCell*)[self.mailSessionTableView cellForRowAtIndexPath:indexPath];
            [sessionCell refresh];
        }
            break;
        case NSFetchedResultsChangeMove: {
            MailSessionCell *sessionCell = (MailSessionCell*)[self.mailSessionTableView cellForRowAtIndexPath:indexPath];
            [sessionCell refresh];
            [self.mailSessionTableView deleteRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath, nil] withRowAnimation:UITableViewRowAnimationFade];
            [self.mailSessionTableView insertRowsAtIndexPaths:[NSArray arrayWithObjects:newIndexPath, nil] withRowAnimation:UITableViewRowAnimationFade];
        }
            break;
        default:
            break;
    }
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    if ([[[UserSetting defaultSetting] emailFirstLoad] boolValue]) return;
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.mailSessionTableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.mailSessionTableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}

@end