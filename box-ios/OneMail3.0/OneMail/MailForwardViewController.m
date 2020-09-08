//
//  MailForwardViewController.m
//  OneMail
//
//  Created by CSE on 15/12/1.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define mailAddBtnWidth 65
#define mailAddBtnHeight 65
#define mailAddBtnDistanceRight 10
#define mailAddBtnDistanceBottom 10
#import "MailForwardViewController.h"
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "Session.h"
#import "MessageSend.h"
#import "MailContactViewController.h"
#import "Message.h"
#import "User.h"
#import "UserThumbnail.h"

@interface MailForwardSessionCell ()

@property (nonatomic, strong) UILabel     *mailSessionUsersLabel;

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

@implementation MailForwardSessionCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.mailSessionIconView = [[UIView alloc] initWithFrame:CGRectMake(15, 6, 56, 56)];
        [self.contentView addSubview:self.mailSessionIconView];
        
        self.mailSessionUsersLabel = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:17.0f] textColor:[CommonFunction colorWithString:@"000000" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
        [self.contentView addSubview:self.mailSessionUsersLabel];
        
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
    
    self.mailSessionUsersLabel.frame = CGRectMake(15+56+10, (CGRectGetHeight(self.frame)-22)/2, CGRectGetWidth(self.frame)-15-56-10-15, 22);
    self.mailSessionUsersLabel.text = self.session.sessionTitle;
}

- (void)refreshHeadImageViewFrame:(NSArray*)userEmailArray {
    if (userEmailArray.count == 1) {
        self.mailSessionFirstIconBackground.frame = CGRectMake(0, 0, 56, 56);
        self.mailSessionFirstIconBackground.layer.cornerRadius = 56/2;
        self.mailSessionFirstUserIcon.frame = CGRectMake(2, 2, 52, 52);
        self.mailSessionFirstUserIcon.layer.cornerRadius = 52/2;
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
        self.mailSessionFirstUserIcon.frame = CGRectMake(1, 1, 26, 26);
        self.mailSessionFirstUserIcon.layer.cornerRadius = 26/2;
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
        self.mailSessionFirstUserIcon.frame = CGRectMake(1, 1, 26, 26);
        self.mailSessionFirstUserIcon.layer.cornerRadius = 26/2;
        User *firstUser = [User getUserWithUserEmail:[[UserSetting defaultSetting] emailAddress] context:nil];
        [UserThumbnail imageWithUser:firstUser imageView:self.mailSessionFirstUserIcon refresh:NO];
        
        User *secondUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:0] context:nil];
        [UserThumbnail imageWithUser:secondUser imageView:self.mailSessionSecondUserIcon refresh:NO];
        
        User *thirdUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:1] context:nil];
        [UserThumbnail imageWithUser:thirdUser imageView:self.mailSessionThirdUserIcon refresh:NO];
        
        User *forthUser = [User getUserWithUserEmail:[userEmailArray objectAtIndex:2] context:nil];
        [UserThumbnail imageWithUser:forthUser imageView:self.mailSessionFourthUserIcon refresh:NO];
        self.mailSessionFourthUserIcon.image = [UIImage imageWithContentsOfFile:forthUser.userHeadIconPath];

        self.mailSessionFirstIconBackground.hidden = NO;
        self.mailSessionSecondIconBackground.hidden = NO;
        self.mailSessionThirdIconBackground.hidden = NO;
        self.mailSessionFourthIconBackground.hidden = NO;
    }
}

- (void)setSession:(Session *)session {
    _session = session;
}

@end

@interface MailForwardViewController() <UITableViewDataSource,UITableViewDelegate,UIAlertViewDelegate,NSFetchedResultsControllerDelegate>
@property (nonatomic, strong) NSFetchedResultsController *mailFetchedResultsController;
@property (nonatomic, strong) UIButton *mailAddBtn;
@property (nonatomic, strong) UITableView *mailTableView;
@property (nonatomic, strong) Message *forwardMessage;
@property (nonatomic, strong) Session *forwardSession;
@property (nonatomic, strong) NSString *shareLink;
@end

@implementation MailForwardViewController
- (id)initWithForwardMessage:(Message *)forwardMessage{
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
    if (self = [super init]) {
        AppDelegate *appdelegate = [UIApplication sharedApplication].delegate;
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:appdelegate.localManager.managedObjectContext];
        NSSortDescriptor *topDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"sessionTopFlag" ascending:NO];
        NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"sessionSyncDate" ascending:NO];
        NSFetchRequest *request = [[NSFetchRequest alloc]init];
        [request setEntity:entity];
        [request setSortDescriptors:@[topDescriptor,dateDescriptor]];
        [request setFetchBatchSize:20];
        self.mailFetchedResultsController = [[NSFetchedResultsController alloc]initWithFetchRequest:request managedObjectContext:appdelegate.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
        [self.mailFetchedResultsController setDelegate:self];
        [self performFetch];
    }
    return self;
}

- (void)performFetch {
    NSError *error = NULL;
    if (![self.mailFetchedResultsController performFetch:&error]) {
        NSLog(@"Unresolved error %@,%@",error,[error userInfo]);
        abort();
    }
}

- (void)viewDidLoad{
    [super viewDidLoad];
    
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.title = NSLocalizedString(@"MailForwardTitle", nil);
    UIBarButtonItem *leftButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Cancel", nil) style:UIBarButtonItemStylePlain target:self action:@selector(cancelForward)];
    self.navigationItem.leftBarButtonItem = leftButton;
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    self.mailTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height) style:UITableViewStylePlain];
    [self.mailTableView registerClass:[MailForwardSessionCell class] forCellReuseIdentifier:@"MailForwardSessionCell"];
    [self.mailTableView setDataSource:self];
    [self.mailTableView setDelegate:self];
    [self.mailTableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    [self.mailTableView setScrollEnabled:YES];
    [self.mailTableView setAllowsMultipleSelectionDuringEditing:YES];
    [self.view addSubview:self.mailTableView];
    
    self.mailAddBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.mailAddBtn setImage:[UIImage imageNamed:@"btn_new_nor"] forState:UIControlStateNormal];
    [self.mailAddBtn setImage:[UIImage imageNamed:@"btn_new_press"] forState:UIControlStateHighlighted];
    [self.mailAddBtn setFrame:CGRectMake(CGRectGetWidth(self.view.frame) - mailAddBtnWidth - mailAddBtnDistanceRight, CGRectGetHeight(self.view.frame) - mailAddBtnHeight - mailAddBtnDistanceBottom - 49 , mailAddBtnWidth, mailAddBtnHeight)];
    [self.view addSubview:self.mailAddBtn];
    [self.mailAddBtn addTarget:self action:@selector(addSession) forControlEvents:UIControlEventTouchUpInside];

}

- (void)cancelForward{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)addSession{
    MailContactViewController *searchController;
    if (self.forwardMessage) {
        searchController = [[MailContactViewController alloc] initWithMessage:self.forwardMessage];
    }
    if (self.shareLink) {
        searchController = [[MailContactViewController alloc] initWithShareLink:self.shareLink];
    }
    [self.navigationController pushViewController:searchController animated:YES];

}

#pragma mark tableViewDelegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.mailFetchedResultsController.fetchedObjects.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    MailForwardSessionCell *cell = (MailForwardSessionCell *)[tableView dequeueReusableCellWithIdentifier:@"MailForwardSessionCell"];
    if (cell == nil) {
        cell = [[MailForwardSessionCell alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"MailForwardSessionCell"];
    }
    Session *session = [self.mailFetchedResultsController objectAtIndexPath:indexPath];
    cell.session = session;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    MailForwardSessionCell *cell = (MailForwardSessionCell *)[tableView cellForRowAtIndexPath:indexPath];
    self.forwardSession = cell.session;
    UIAlertView *alertview = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"MailForwardPrompt", nil) message:nil delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:NSLocalizedString(@"Confirm", nil), nil];
    [alertview show];
}

#pragma mark alertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    if (buttonIndex == 1) {
        MessageSend *sender = [MessageSend shareMessageSend];
        if (self.forwardMessage) {
            [sender forwardMessage:self.forwardMessage Session:self.forwardSession];
        }
        if (self.shareLink) {
            [sender forwardShareLink:self.shareLink Session:self.forwardSession];
        }
        [self.navigationController popViewControllerAnimated:YES];

    }
}
@end
