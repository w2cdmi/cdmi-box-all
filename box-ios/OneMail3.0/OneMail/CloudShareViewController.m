//
//  CloudShareViewController.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudShareViewController.h"
#import "CloudShareTableViewCell.h"
#import "CloudFileViewController.h"
#import "AppDelegate.h"
#import "MenuViewController.h"
#import "File.h"
#import "File+Remote.h"
#import "MRProgressOverlayView.h"
#import "UIView+Toast.h"
#import "CloudTabBarButton.h"
#import "CloudTitleView.h"
#import "CloudFileMoreMenuView.h"
#import "CloudCrumbView.h"
#import "CloudPreviewController.h"
#import "CloudFileMoveViewController.h"
#import "TransportTask.h"
#import "CloudFileViewController.h"
#import "CloudFileTableViewCell.h"
#import "UIAlertView+Blocks.h"
#import "MJRefresh.h"

@interface CloudShareViewController ()<UITableViewDelegate,UITableViewDataSource,NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) NSFetchedResultsController *cloudShareFetchController;
@property (nonatomic, strong) UITableView                *cloudShareTableView;
@property (nonatomic, strong) CloudCrumbView             *cloudShareCrumbView;
@property (nonatomic, strong) UIRefreshControl           *cloudShareRefreshController;
@property (nonatomic, assign) BOOL                        cloudShareLoadState;

@property (nonatomic, strong) UIButton                   *cloudSettingButton;
@property (nonatomic, strong) UIButton                   *cloudSelectAllButton;
@property (nonatomic, strong) UIButton                   *cloudSelectCancelButton;
@property (nonatomic, strong) UIButton                   *cloudBackButton;
@property (nonatomic, strong) UIButton                   *cloudMenuButton;

@property (nonatomic, strong) CloudFileMoreMenuView      *cloudFileMoreMenu;

@property (nonatomic, strong) UIView                     *cloudSelectTabBarView;
@property (nonatomic, strong) NSMutableArray             *cloudSelectedArray;
@property (nonatomic, assign) BOOL                        cloudSelectState;
@property (nonatomic, assign) BOOL                        cloudSelectAllState;

@property (nonatomic, strong) UIView                     *cloudFileNullView;


@end

@implementation CloudShareViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.LocalizedChange" object:nil];
//    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.popUpLayer1" object:nil];
}

- (id)initWithFile:(File *)file {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.file = file;
        self.cloudSelectAllState = NO;
        self.cloudSelectState = NO;
        self.cloudShareLoadState = NO;
        self.cloudSelectedArray = [[NSMutableArray alloc] init];
        
        self.cloudSettingButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        self.cloudSettingButton.hidden = YES;
        self.cloudSettingButton.imageView.frame = CGRectMake(11, 11, 22, 22);
        [self.cloudSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_nor"] forState:UIControlStateNormal];
        [self.cloudSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_press"] forState:UIControlStateHighlighted];
        [self.cloudSettingButton addTarget:self action:@selector(cloudSettingButtonClick) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudSelectAllButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:getLocalizedString(@"SelectAll", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
        self.cloudSelectAllButton.hidden = YES;
        self.cloudSelectAllButton.titleEdgeInsets = UIEdgeInsetsMake(0, CGRectGetWidth(self.cloudSelectAllButton.frame)-[CommonFunction labelSizeWithString:getLocalizedString(@"SelectAll", nil) font:[UIFont systemFontOfSize:17.0f]].width, 0, 0);
        [self.cloudSelectAllButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"SelectAll", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
        [self.cloudSelectAllButton addTarget:self action:@selector(cloudSelectAll) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudSelectCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:getLocalizedString(@"Cancel", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
        self.cloudSelectCancelButton.hidden = YES;
        self.cloudSelectCancelButton.titleEdgeInsets = UIEdgeInsetsMake(0, CGRectGetWidth(self.cloudSelectCancelButton.frame)-[CommonFunction labelSizeWithString:getLocalizedString(@"Cancel", nil) font:[UIFont systemFontOfSize:17.0f]].width, 0, 0);
        [self.cloudSelectCancelButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"Cancel", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
        [self.cloudSelectCancelButton addTarget:self action:@selector(cloudEndSelect:) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudBackButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        self.cloudBackButton.hidden = YES;
        self.cloudBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
        [self.cloudBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
        [self.cloudBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
        [self.cloudBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudMenuButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        self.cloudMenuButton.hidden = YES;
        self.cloudMenuButton.imageView.frame = CGRectMake(11, 11, 22, 22);
        [self.cloudMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_nor"] forState:UIControlStateNormal];
        [self.cloudMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_press"] forState:UIControlStateHighlighted];
        [self.cloudMenuButton addTarget:self action:@selector(cloudMenuButtonClick) forControlEvents:UIControlEventTouchUpInside];
        
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        UserSetting *userSetting = [UserSetting defaultSetting];
        
        NSPredicate *preficate;
        if ([self.file isShareRoot]) {
            preficate = [NSPredicate predicateWithFormat:@"fileId != %@ AND fileShareUser = %@",@"-0",appDelegate.localManager.userCloudId];
        } else {
            preficate = [NSPredicate predicateWithFormat:@"fileId != NULL AND fileParent = %@ AND fileOwner = %@",file.fileId,file.fileOwner];
        }
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:userSetting.cloudSortType ascending:YES];
        NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"fileModifiedDate" ascending:NO];
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        [fetchRequest setPredicate:preficate];
        [fetchRequest setEntity:entity];
        [fetchRequest setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor,dateDescriptor,nil]];
        [fetchRequest setFetchBatchSize:20];
        self.cloudShareFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:[sortDescriptor key] cacheName:nil];
        [self.cloudShareFetchController setDelegate:self];
        [self performFetch];
         [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(localizedChange) name:@"com.huawei.onemail.LocalizedChange" object:nil];
        
//        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(popUpLayer) name:@"com.huawei.onemail.popUpLayer1" object:nil];
    
    }
    return self;
}

- (void)popUpLayer {
    [self reloadDataSource];
}

- (void)localizedChange{
    _cloudFileMoreMenu = nil;
    self.cloudSelectAllButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:getLocalizedString(@"SelectAll", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectAllButton.titleEdgeInsets = UIEdgeInsetsMake(0, CGRectGetWidth(self.cloudSelectAllButton.frame)-[CommonFunction labelSizeWithString:getLocalizedString(@"SelectAll", nil) font:[UIFont systemFontOfSize:17.0f]].width, 0, 0);
    [self.cloudSelectAllButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"SelectAll", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.cloudSelectAllButton addTarget:self action:@selector(cloudSelectAll) forControlEvents:UIControlEventTouchUpInside];
    self.cloudSelectAllButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(self.cloudSelectAllButton.frame), 0, CGRectGetWidth(self.cloudSelectAllButton.frame), CGRectGetHeight(self.cloudSelectAllButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSelectAllButton];
    
    self.cloudSelectCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:getLocalizedString(@"Cancel", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudSelectCancelButton.titleEdgeInsets = UIEdgeInsetsMake(0, CGRectGetWidth(self.cloudSelectCancelButton.frame)-[CommonFunction labelSizeWithString:getLocalizedString(@"Cancel", nil) font:[UIFont systemFontOfSize:17.0f]].width, 0, 0);
    [self.cloudSelectCancelButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"Cancel", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.cloudSelectCancelButton addTarget:self action:@selector(cloudEndSelect:) forControlEvents:UIControlEventTouchUpInside];
    self.cloudSelectCancelButton.frame = CGRectMake(15, 0, CGRectGetWidth(self.cloudSelectCancelButton.frame), CGRectGetHeight(self.cloudSelectCancelButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSelectCancelButton];
    
}


- (void)performFetch {
    NSError *error = NULL;
    if (![self.cloudShareFetchController performFetch:&error]) {
        NSLog(@"Unresolved error %@,%@",error,[error userInfo]);
        abort();
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    self.view.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    
    [self.cloudTitleView setViewFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    [self.navigationController.navigationBar addSubview:self.cloudTitleView];
    
    self.cloudSettingButton.frame = CGRectMake(4, 0, CGRectGetWidth(self.cloudSettingButton.frame), CGRectGetHeight(self.cloudSettingButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSettingButton];
    
    self.cloudSelectAllButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(self.cloudSelectAllButton.frame), 0, CGRectGetWidth(self.cloudSelectAllButton.frame), CGRectGetHeight(self.cloudSelectAllButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSelectAllButton];
    
    self.cloudSelectCancelButton.frame = CGRectMake(15, 0, CGRectGetWidth(self.cloudSelectCancelButton.frame), CGRectGetHeight(self.cloudSelectCancelButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSelectCancelButton];
    
    self.cloudBackButton.frame = CGRectMake(4, 0, CGRectGetWidth(self.cloudBackButton.frame), CGRectGetHeight(self.cloudBackButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudBackButton];
    
    self.cloudMenuButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-CGRectGetWidth(self.cloudMenuButton.frame), 0, CGRectGetWidth(self.cloudMenuButton.frame), CGRectGetHeight(self.cloudMenuButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudMenuButton];
    
    if ([self.file isShareRoot]) {
        self.cloudSettingButton.hidden = NO;
        self.cloudBackButton.hidden = YES;
    } else {
        self.cloudSettingButton.hidden = YES;
        self.cloudBackButton.hidden = NO;
    }
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    if (!self.crumbFileIds) {
        self.crumbFileIds = [NSMutableArray arrayWithObject:self.file.fileId];
        self.crumbFileOwners = [NSMutableArray arrayWithObject:self.file.fileOwner];
        self.crumbFileViewControllers = [NSMutableArray arrayWithObject:self];
    } else {
        [self.crumbFileIds addObject:self.file.fileId];
        [self.crumbFileOwners addObject:self.file.fileOwner];
        [self.crumbFileViewControllers addObject:self];
    }
    self.cloudShareCrumbView = [[CloudCrumbView alloc] initWithFiles:(NSArray*)self.crumbFileIds fileOwners:(NSArray*)self.crumbFileOwners];
    self.cloudShareCrumbView.backgroundColor = [UIColor whiteColor];
    self.cloudShareCrumbView.navigationController = self.navigationController;
    self.cloudShareCrumbView.viewControllers = self.crumbFileViewControllers;
    self.cloudShareCrumbView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.cloudShareCrumbView.frame), CGRectGetHeight(self.cloudShareCrumbView.frame));
    [self.view addSubview:self.cloudShareCrumbView];
    
    self.cloudShareTableView = [[UITableView alloc]initWithFrame:self.view.frame style:UITableViewStylePlain];
    if ([self.file isShareRoot]) {
        [self.cloudShareTableView registerClass:[CloudShareTableViewCell class] forCellReuseIdentifier:@"CloudShareTableViewCell"];
    } else {
        [self.cloudShareTableView registerClass:[CloudFileTableViewCell class] forCellReuseIdentifier:@"CloudFileTableViewCell"];
    }

    self.cloudShareTableView.backgroundColor = [UIColor clearColor];
    self.cloudShareTableView.dataSource = self;
    self.cloudShareTableView.delegate = self;
    self.cloudShareTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.cloudShareTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.cloudShareTableView.sectionIndexBackgroundColor = [UIColor clearColor];
    self.cloudShareTableView.sectionIndexTrackingBackgroundColor = [UIColor clearColor];
    self.cloudShareTableView.sectionIndexColor = [UIColor colorWithRed:126/255.0f green:126/255.0f blue:128/255.0f alpha:1.0f];
    //添加下拉刷新控件
    [self setupRefresh];
//    self.cloudShareTableView.tableHeaderView = self.cloudShareRefreshController;
    self.cloudShareTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.cloudShareTableView];
    
    UILongPressGestureRecognizer *fileMultiSelectGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(cloudGestureSelect:)];
    [self.cloudShareTableView addGestureRecognizer:fileMultiSelectGesture];
    
    
    CGRect tableViewFrame = self.cloudShareTableView.frame;
    tableViewFrame.origin.y = tableViewFrame.origin.y+CGRectGetHeight(self.cloudShareCrumbView.frame)-CGRectGetHeight(self.cloudShareRefreshController.frame);
    tableViewFrame.size.height = tableViewFrame.size.height+CGRectGetHeight(self.cloudShareRefreshController.frame)-CGRectGetHeight(self.cloudShareCrumbView.frame);
    self.cloudShareTableView.frame = tableViewFrame;
    
    [self.view bringSubviewToFront:self.cloudShareCrumbView];
    [self reloadDataSource];
    
    UISwipeGestureRecognizer *leftHandleRevognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handSwipe:)];
    leftHandleRevognizer.direction = UISwipeGestureRecognizerDirectionLeft;
    [self.view addGestureRecognizer:leftHandleRevognizer];
    
    UISwipeGestureRecognizer *rightHandleRevognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handSwipe:)];
    rightHandleRevognizer.direction = UISwipeGestureRecognizerDirectionRight;
    [self.view addGestureRecognizer:rightHandleRevognizer];
}

- (void)handSwipe:(UISwipeGestureRecognizer*)sender {
    if (self.cloudSelectState) {
        return;
    }
    if (sender.direction == UISwipeGestureRecognizerDirectionLeft) {
       [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Show.TeamSpace" object:nil];
    }
    if (sender.direction == UISwipeGestureRecognizerDirectionRight) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Show.MySpace" object:nil];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.cloudTitleView.hidden = YES;
    self.cloudSettingButton.hidden = YES;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = YES;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.cloudTitleView.hidden = NO;
    self.cloudSettingButton.hidden = YES;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = NO;
    if ([self.file isShareRoot]) {
        self.cloudSettingButton.hidden = NO;
        self.cloudBackButton.hidden = YES;
    } else {
        self.cloudSettingButton.hidden = YES;
        self.cloudBackButton.hidden = NO;
    }
    [[UIApplication sharedApplication]setStatusBarStyle:UIStatusBarStyleLightContent];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabShow" object:nil];
    [self reloadDataSource];
}

#pragma mark Crumb View
- (void)setCrumbFileIds:(NSMutableArray *)crumbFileIds {
    _crumbFileIds = [NSMutableArray arrayWithArray:crumbFileIds];
}

- (void)setCrumbFileOwners:(NSArray *)crumbFileOwners {
    _crumbFileOwners = [NSMutableArray arrayWithArray:crumbFileOwners];
}

- (void)setCrumbFileViewControllers:(NSMutableArray *)crumbFileViewControllers {
    _crumbFileViewControllers = [NSMutableArray arrayWithArray:crumbFileViewControllers];
}

#pragma mark Back Button Click
- (void)popViewController {
    [self.navigationController popViewControllerAnimated:NO];
}

#pragma mark Setting Button Click
- (void)cloudSettingButtonClick {
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

#pragma mark Menu Button Click
- (void)cloudMenuButtonClick {
    if (self.cloudFileMoreMenu.hidden) {
        self.cloudFileMoreMenu.hidden = NO;
    } else {
        self.cloudFileMoreMenu.hidden = YES;
    }
}

- (UIView *)cloudFileNullView{
    if (!_cloudFileNullView) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        CGRect navigationBarFrame = delegate.navigationController.navigationBar.frame;
        _cloudFileNullView = [[UIView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height + navigationBarFrame.size.height, self.view.frame.size.width, self.view.frame.size.height - statusBarFrame.size.height - navigationBarFrame.size.height - 250)];
        _cloudFileNullView.hidden = YES;
        
        UIImageView *cloudFileNullImage = [[UIImageView alloc] initWithFrame:CGRectMake((_cloudFileNullView.frame.size.width - 96) / 2,100, 96, 96)];
        cloudFileNullImage.image = [UIImage imageNamed:@"ic_null_files"];
        [_cloudFileNullView addSubview:cloudFileNullImage];
        
        UILabel * cloudFileNullTitle = [[UILabel alloc] initWithFrame:CGRectMake(20, CGRectGetMaxY(cloudFileNullImage.frame) + 10, self.view.frame.size.width - 30, 20)];
        cloudFileNullTitle.text = getLocalizedString(@"CloudNoFile", nil);
        cloudFileNullTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1];
        cloudFileNullTitle.font = [UIFont systemFontOfSize:15];
        cloudFileNullTitle.textAlignment = NSTextAlignmentCenter;
        [_cloudFileNullView addSubview:cloudFileNullTitle];
        
        UILabel *cloudFileNullPrompt = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:14] textColor:[CommonFunction colorWithString:@"666666" alpha:1] textAlignment:NSTextAlignmentCenter];
//        cloudFileNullPrompt.text = getLocalizedString(@"CloudNoFileNotification", nil);
        cloudFileNullPrompt.numberOfLines = 0;
        CGSize size = [CommonFunction labelSizeWithLabel:cloudFileNullPrompt limitSize:CGSizeMake(self.view.frame.size.width - 30, 1000)];
        cloudFileNullPrompt.frame = CGRectMake(20, CGRectGetMaxY(cloudFileNullTitle.frame) + 10, size.width,size.height);
        
        [_cloudFileNullView addSubview:cloudFileNullPrompt];
        
        [self.view addSubview:_cloudFileNullView];
    }
    return _cloudFileNullView;
}

- (CloudFileMoreMenuView*)cloudFileMoreMenu {
    if (!_cloudFileMoreMenu) {
        _cloudFileMoreMenu = [[CloudFileMoreMenuView alloc] initWithFrame:self.view.frame];
        _cloudFileMoreMenu.fileViewController = self;
        
        CloudFileMoreMenuCell *multiSelect = [[CloudFileMoreMenuCell alloc] initWithImage:[UIImage imageNamed:@"ic_popmenu_multiple_select_nor"] title:getLocalizedString(@"CloudFileMoreSelect", nil) target:self action:@selector(cloudBeginSelect)];
        CloudFileMoreMenuCell *sortByName = [[CloudFileMoreMenuCell alloc] initWithImage:[UIImage imageNamed:@"ic_popmenu_sort_by_name_nor"] title:getLocalizedString(@"CloudFileMoreSortName", nil) target:self action:@selector(switchSortByName)];
        CloudFileMoreMenuCell *sortByTime = [[CloudFileMoreMenuCell alloc] initWithImage:[UIImage imageNamed:@"ic_popmenu_sort_by_time_nor"] title:getLocalizedString(@"CloudFileMoreSortTime", nil) target:self action:@selector(switchSortByTime)];
        [_cloudFileMoreMenu setMenuCells:@[multiSelect,sortByName,sortByTime]];
    }
    return _cloudFileMoreMenu;
}

- (void)switchSortByName {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    UserSetting *userSetting = [UserSetting defaultSetting];
    if ([userSetting.cloudSortType isEqualToString:@"fileSortNameKey"]) {
        if (userSetting.cloudSortNameOrder.boolValue) {
            userSetting.cloudSortNameOrder = @(0);
        } else {
            userSetting.cloudSortNameOrder = @(1);
        }
    } else {
        userSetting.cloudSortType = @"fileSortNameKey";
    }
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:userSetting.cloudSortType ascending:userSetting.cloudSortNameOrder.boolValue];
    NSSortDescriptor *nameSortDescripter = [NSSortDescriptor sortDescriptorWithKey:@"fileModifiedDate" ascending:NO];
    NSFetchRequest *request = self.cloudShareFetchController.fetchRequest;
    [request setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor, nameSortDescripter, nil]];
    self.cloudShareFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:[appDelegate.localManager managedObjectContext] sectionNameKeyPath:[sortDescriptor key] cacheName:nil];
    self.cloudShareFetchController.delegate = self;
    [self performFetch];
    [self.cloudShareTableView reloadData];
}

-(void)switchSortByTime {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    UserSetting *userSetting = [UserSetting defaultSetting];
    if ([userSetting.cloudSortType isEqualToString:@"fileSortTimeKey"]) {
        if (userSetting.cloudSortTimeOrder.boolValue) {
            userSetting.cloudSortTimeOrder = @(0);
        } else {
            userSetting.cloudSortTimeOrder = @(1);
        }
    } else {
        userSetting.cloudSortType = @"fileSortTimeKey";
    }
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:userSetting.cloudSortType ascending:userSetting.cloudSortTimeOrder.boolValue];
    NSSortDescriptor *nameSortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"fileModifiedDate" ascending:userSetting.cloudSortTimeOrder.boolValue];
    NSFetchRequest* request = self.cloudShareFetchController.fetchRequest;
    [request setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor,nameSortDescriptor,nil]];
    self.cloudShareFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:[appDelegate.localManager managedObjectContext] sectionNameKeyPath:[sortDescriptor key] cacheName:nil];
    self.cloudShareFetchController.delegate = self;
    [self performFetch];
    [self.cloudShareTableView reloadData];
}

#pragma mark UIRefreshViewController
//- (UIRefreshControl*)cloudShareRefreshController {
//    if (!_cloudShareRefreshController) {
//        UIRefreshControl *refreshController = [[UIRefreshControl alloc] init];
//        [refreshController addTarget:self action:@selector(refreshView:) forControlEvents:UIControlEventValueChanged];
//        _cloudShareRefreshController = refreshController;
//    }
//    return _cloudShareRefreshController;
//}
//
//- (void)refreshView:(UIRefreshControl*)refreshController {
//    if (self.cloudSelectState) {
//        [refreshController endRefreshing];
//        return;
//    }
//    if (self.file.fileSyncDate) {
//        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
//        [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
//        NSString *lastUpdated = [NSString stringWithFormat:getLocalizedString(@"CloudFileRefreshPrompt", nil),[formatter stringFromDate:self.file.fileSyncDate]];
//        refreshController.attributedTitle = [[NSAttributedString alloc] initWithString:lastUpdated];
//    } else {
//        refreshController.attributedTitle = [[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudFileRefreshing", nil)];
//    }
//    [self reloadDataSource];
//}

- (void)setupRefresh
{
    //    self.cloudFileTableView.tableHeaderView = self.cloudFileRefreshController;
    
    MJRefreshNormalHeader *header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(loadNewData)];
    header.automaticallyChangeAlpha = YES;
    [header beginRefreshing];
    header.stateLabel.hidden = YES;
    self.cloudShareTableView.mj_header = header;
}

- (void)loadNewData
{
    __weak UITableView *tableView = self.cloudShareTableView;
    
    if (self.cloudSelectState) {
        [tableView.mj_header endRefreshing];
        return;
    }
    [self reloadDataSource];
}

#pragma mark TableView Delegate + DataSource
- (void)reloadDataSource {
    if (self.cloudShareLoadState) {
        return;
    } else {
        self.cloudShareLoadState = YES;
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        __block __weak typeof(self) weakSelf = self;
        __weak UITableView *tableView = self.cloudShareTableView;
        [self.file folderReload:^(id retobj) {
            dispatch_async(dispatch_get_main_queue(), ^{
                __strong typeof(weakSelf) strongSelf = weakSelf;
                
                strongSelf.cloudShareLoadState = NO;
                if ([tableView.mj_header isRefreshing]) {
                    [tableView.mj_header endRefreshing];
                }

                if (self.cloudShareFetchController.fetchedObjects.count == 0) {
                    self.cloudFileNullView.hidden = NO;
                } else {
                    self.cloudFileNullView.hidden = YES;
                }
            });
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                __strong typeof(weakSelf) strongSelf = weakSelf;
                strongSelf.cloudShareLoadState = NO;
                if ([tableView.mj_header isRefreshing]) {
                    [tableView.mj_header endRefreshing];
                }

                if (self.cloudShareFetchController.fetchedObjects.count == 0) {
                    self.cloudFileNullView.hidden = NO;
                } else {
                    self.cloudFileNullView.hidden = YES;
                }
                           
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileLoadFailedPrompt", nil)];
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                if (httpResponse.statusCode == 404) {
                    [strongSelf.file fileRemove:nil];
                    [strongSelf.navigationController popToRootViewControllerAnimated:YES];
                }
            });
        }];
    });
}

- (NSArray*)sectionIndexTitlesForTableView:(UITableView *)tableView {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (![userSetting.cloudSortType isEqualToString:@"sortNameKey"]) {
        return nil;
    }
    if (self.cloudShareTableView == tableView) {
        NSMutableArray *titleOfSection = [[NSMutableArray alloc] init];
        for (int i = 0; i < [self.cloudShareFetchController sections].count; i++) {
            [titleOfSection addObject:[[[self.cloudShareFetchController sections] objectAtIndex:i] name]];
        }
        if (titleOfSection.copy > 0) {
            [titleOfSection insertObject:UITableViewIndexSearch atIndex:0];
        }
        return titleOfSection;
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index {
    return index - 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if ([self.cloudShareFetchController sections].count > 0) {
        tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    } else {
        tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    return [self.cloudShareFetchController sections].count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    id<NSFetchedResultsSectionInfo> sectionInfo = [[self.cloudShareFetchController sections] objectAtIndex:section];
    return [sectionInfo numberOfObjects];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 22.0f;
}
- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (self.cloudShareTableView == tableView) {
        id<NSFetchedResultsSectionInfo> sectionInfo = [[self.cloudShareFetchController sections] objectAtIndex:section];
        return [CommonFunction tableViewHeaderWithTitle:[sectionInfo name]];
    } else {
        return nil;
    }
}


- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([self.file isShareRoot]) {
        CloudShareTableViewCell *cell = (CloudShareTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"CloudShareTableViewCell"];
        if (cell == nil) {
            cell = [[CloudShareTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudShareTableViewCell"];
        }
        File *file = [self.cloudShareFetchController objectAtIndexPath:indexPath];
        cell.file = file;
        cell.fileSelectState = self.cloudSelectState;
        if ([self.cloudSelectedArray containsObject:file]) {
            cell.fileSelected = YES;
        } else {
            cell.fileSelected = NO;
        }
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        return cell;
    } else {
        CloudFileTableViewCell *cell = (CloudFileTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"CloudFileTableViewCell"];
        if (cell == nil) {
            cell = [[CloudFileTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudFileTableViewCell"];
        }
        File *file = [self.cloudShareFetchController objectAtIndexPath:indexPath];
        cell.file = file;
        cell.fileSelectState = self.cloudSelectState;
        if ([self.cloudSelectedArray containsObject:file]) {
            cell.fileSelected = YES;
        } else {
            cell.fileSelected = NO;
        }
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        return cell;
    }

}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudShareTableViewCell *cell = (CloudShareTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
    File *file = [self.cloudShareFetchController objectAtIndexPath:indexPath];
    [file saveFileShareNewFlag:@(0)];
    if (self.cloudSelectState) {
        if ([self.cloudSelectedArray containsObject:file]) {
            cell.fileSelected = NO;
            [self.cloudSelectedArray removeObject:file];
        } else {
            cell.fileSelected = YES;
            [self.cloudSelectedArray addObject:file];
        }
        self.title = [NSString stringWithFormat:getLocalizedString(@"CloudFileMultiSelectTitle", nil),self.cloudSelectedArray.count];
    } else {
        if ([file isFolder]) {
            CloudShareViewController *child = [[CloudShareViewController alloc] initWithFile:file];
            child.cloudTitleView = self.cloudTitleView;
            child.crumbFileIds = self.crumbFileIds;
            child.crumbFileOwners = self.crumbFileOwners;
            child.crumbFileViewControllers = self.crumbFileViewControllers;
            [self.navigationController pushViewController:child animated:NO];
        } else {
            CloudPreviewController *previewController = [[CloudPreviewController alloc] initWithFile:file];
            [self.navigationController pushViewController:previewController animated:YES];
        }
    }
}

#pragma mark Multi Select
- (void)cloudGestureSelect:(UIGestureRecognizer*)gesture {
    if (self.cloudSelectState) {
        return;
    }
    if (gesture.state == UIGestureRecognizerStateBegan) {
        [self cloudBeginSelect];
        CGPoint location = [gesture locationInView:self.cloudShareTableView];
        NSIndexPath *indexPath = [self.cloudShareTableView indexPathForRowAtPoint:location];
        CloudFileTableViewCell *cell = (CloudFileTableViewCell*)[self.cloudShareTableView cellForRowAtIndexPath:indexPath];
        File *file = [self.cloudShareFetchController objectAtIndexPath:indexPath];
        if (cell.fileSelected == YES) {
            [self.cloudSelectedArray addObject:file];
        }
        
        
        self.title = [NSString stringWithFormat:getLocalizedString(@"CloudFileMultiSelectTitle", nil),self.cloudSelectedArray.count];
    }
    
}
- (void)cloudBeginSelect {
    self.cloudSelectState = YES;
    self.cloudTitleView.hidden = YES;
    self.cloudSettingButton.hidden = YES;
    self.cloudSelectAllButton.hidden = NO;
    self.cloudSelectCancelButton.hidden = NO;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = YES;
    self.title = [NSString stringWithFormat:getLocalizedString(@"CloudFileMultiSelectTitle", nil),self.cloudSelectedArray.count];
    [self showSelectTabBarView];
    [self.cloudShareTableView reloadData];
}

- (void)cloudEndSelect:(id)sender {
    self.cloudSelectState = NO;
    self.cloudSelectAllState = NO;
    [self.cloudSelectedArray removeAllObjects];
    self.cloudTitleView.hidden = NO;
    self.cloudSettingButton.hidden = YES;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = NO;
    
    self.title = nil;
    [self hideSelectTabBarView];

    if ([self.file isShareRoot]) {
        self.cloudSettingButton.hidden = NO;
    } else {
        self.cloudBackButton.hidden = NO;
    }
    [self.cloudShareTableView reloadData];
}

- (void)cloudSelectAll {
    [self.cloudSelectedArray removeAllObjects];
    if (self.cloudSelectAllState) {
        self.cloudSelectAllState = NO;
    } else {
        self.cloudSelectAllState = YES;
        [self.cloudSelectedArray addObjectsFromArray:self.cloudShareFetchController.fetchedObjects];
    }
    self.title = [NSString stringWithFormat:getLocalizedString(@"CloudFileMultiSelectTitle", nil),self.cloudSelectedArray.count];
    [self.cloudShareTableView reloadData];
}

- (UIView*)cloudSelectTabBarView {
    if (!_cloudSelectTabBarView) {
        CGRect tabBarFrame = self.tabBarController.tabBar.frame;
        _cloudSelectTabBarView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(tabBarFrame)+CGRectGetHeight(tabBarFrame), CGRectGetWidth(tabBarFrame), CGRectGetHeight(tabBarFrame))];
        _cloudSelectTabBarView.backgroundColor = [UIColor colorWithRed:255.0/255.0 green:255.0/255.0 blue:255.0/255.0 alpha:1];
        [[UIApplication sharedApplication].keyWindow.rootViewController.view addSubview:_cloudSelectTabBarView];
        
        UIButton *fileSaveButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, tabBarFrame.size.width/2, tabBarFrame.size.height)];
        [fileSaveButton setImage:[UIImage imageNamed:@"ic_tab_copy_move_hover"] forState:UIControlStateNormal];
        [fileSaveButton setImage:[UIImage imageNamed:@"ic_tab_copy_move_hover"] forState:UIControlStateHighlighted];
        [fileSaveButton addTarget:self action:@selector(fileSave) forControlEvents:UIControlEventTouchUpInside];
        [_cloudSelectTabBarView addSubview:fileSaveButton];
        
        UIButton *fileDeleteButton = [[UIButton alloc] initWithFrame:CGRectMake(tabBarFrame.size.width/2, 0, tabBarFrame.size.width/2, tabBarFrame.size.height)];
        [fileDeleteButton setImage:[UIImage imageNamed:@"ic_tab_delete_nor"] forState:UIControlStateNormal];
        [fileDeleteButton setImage:[UIImage imageNamed:@"ic_tab_delete_press"] forState:UIControlStateHighlighted];
        [fileDeleteButton addTarget:self action:@selector(fileDelete) forControlEvents:UIControlEventTouchUpInside];
        [_cloudSelectTabBarView addSubview:fileDeleteButton];
        
        if (![self.file isShareRoot]) {
            fileDeleteButton.frame = CGRectZero;
            fileSaveButton.frame = CGRectMake(0, 0, tabBarFrame.size.width, tabBarFrame.size.height);
        }
    }
    return _cloudSelectTabBarView;
}

- (void)showSelectTabBarView {
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
    [UIView setAnimationDuration:0.3f];
    CGRect tabBarFrame = self.tabBarController.tabBar.frame;
    tabBarFrame.origin.y = tabBarFrame.origin.y + 0.5;
    self.cloudSelectTabBarView.frame = tabBarFrame;
    [UIView commitAnimations];
}

- (void)hideSelectTabBarView {
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3f];
    CGRect tabBarFrame = self.tabBarController.tabBar.frame;
    tabBarFrame.origin.y = tabBarFrame.origin.y + tabBarFrame.size.height;
    self.cloudSelectTabBarView.frame = tabBarFrame;
    [UIView commitAnimations];
}

#pragma mark File Operation
- (void)fileSave {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    NSMutableArray *multiSelectedFileId = [[NSMutableArray alloc] initWithCapacity:self.cloudSelectedArray.count];
    NSMutableArray *multiSelectedFileOwner = [[NSMutableArray alloc] initWithCapacity:self.cloudSelectedArray.count];
    for (File *file in self.cloudSelectedArray) {
        [multiSelectedFileId addObject:file.fileId];
        [multiSelectedFileOwner addObject:file.fileOwner];
    }
    CloudFileMoveViewController *fileMove = [[CloudFileMoveViewController alloc] initWithSourceFiles:multiSelectedFileId filesOwner:multiSelectedFileOwner rootFile:nil];
    fileMove.rootViewController = self;
    [self.navigationController pushViewController:fileMove animated:YES];
    [self cloudEndSelect:nil];
}

- (void)fileDelete {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    [UIAlertView showAlertViewWithTitle:getLocalizedString(@"CloudFileDeletePrompt", nil) message:@"" cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
        for (File *file in self.cloudSelectedArray) {
            if (file.transportTask) {
                [file.transportTask.taskHandle suspend];
            }
            [file fileRemove:^(id retobj) {
                [file fileRemove:nil];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                if (httpResponse.statusCode == 404) {
                    [file fileRemove:nil];
                } else {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
                    });
                }
            }];
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [self cloudEndSelect:nil];
        });
    } onCancel:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            [self cloudEndSelect:nil];
        });
    }];
}

#pragma mark NSFetchedResultsControllerDelegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.cloudShareTableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    [self.cloudShareTableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    UITableView *tableView = self.cloudShareTableView;
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeUpdate: {
            CloudShareTableViewCell *cell = (CloudShareTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
            [cell refresh];
        }
            break;
        case NSFetchedResultsChangeMove:
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            [tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}

-(void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    UITableView *tableView = self.cloudShareTableView;
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [tableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [tableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}

@end
