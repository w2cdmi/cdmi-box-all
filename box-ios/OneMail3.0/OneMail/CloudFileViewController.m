//
//  CloudFileViewController.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#import "CloudFileViewController.h"
#import "CloudFileTableViewCell.h"
#import "AppDelegate.h"
#import "MenuViewController.h"
#import "File+Remote.h"
#import "TransportTask.h"
#import "UIView+Toast.h"
#import "TransportTaskHandle.h"
#import "UIAlertView+Blocks.h"
#import "FileMultiOperation.h"
#import "CloudTabBarButton.h"
#import "CloudTabBarView.h"
#import "CloudPreviewController.h"
#import "CloudAlbumViewController.h"
#import "CloudFileMoveViewController.h"
#import "CloudTeamMoveViewController.h"
#import "CloudFileShareInfoViewController.h"
#import "CloudFolderCreateViewController.h"
#import "CloudFileHandleMenuView.h"
#import "CloudFileMoreMenuView.h"
#import "CloudFileRenameViewController.h"
#import "CloudFileVersionController.h"
#import "CloudFileAddView.h"
#import "CloudCrumbView.h"
#import "CloudTitleView.h"
#import "MJRefresh.h"

@interface CloudFileViewController ()<UITableViewDelegate,UITableViewDataSource,NSFetchedResultsControllerDelegate,UIActionSheetDelegate,UITextFieldDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) NSFetchedResultsController *cloudFileFetchController;
@property (nonatomic, strong) UITableView                *cloudFileTableView;
@property (nonatomic, strong) CloudCrumbView             *cloudFileCrumbView;
@property (nonatomic, strong) UIRefreshControl           *cloudFileRefreshController;
@property (nonatomic, assign) BOOL                        cloudFileLoadState;

@property (nonatomic, strong) NSFetchedResultsController *cloudSearchFetchController;
@property (nonatomic, strong) UITableView                *cloudSearchTableView;
@property (nonatomic, strong) CloudCrumbView             *cloudSearchCrumbView;
@property (nonatomic, strong) UIView                     *cloudSearchNavigationBarView;
@property (nonatomic, strong) UITextField                *cloudSearchNavigationBarTextField;
@property (nonatomic, strong) UIView                     *cloudSearchBackgroundView;
@property (nonatomic, assign) BOOL                        cloudSearchState;
@property (nonatomic, strong) UIActivityIndicatorView    *cloudSearchNavifationBarActivityIndicator;
@property (nonatomic, strong) UIImageView                *cloudSearchNavifationBarImageView;

@property (nonatomic, strong) UIButton                   *cloudSettingButton;
@property (nonatomic, strong) UIButton                   *cloudSearchButton;
@property (nonatomic, strong) UIButton                   *cloudSearchCancelButton;
@property (nonatomic, strong) UIButton                   *cloudSelectAllButton;
@property (nonatomic, strong) UIButton                   *cloudSelectCancelButton;
@property (nonatomic, strong) UIButton                   *cloudBackButton;
@property (nonatomic, strong) UIButton                   *cloudMenuButton;

@property (nonatomic, strong) CloudFileMoreMenuView      *cloudFileMoreMenu;

@property (nonatomic, strong) UIButton                   *cloudFileAddButton;
@property (nonatomic, strong) CloudFileAddView           *cloudFileAddView;

@property (nonatomic, strong) NSMutableArray             *cloudSelectedArray;
@property (nonatomic, assign) BOOL                        cloudSelectState;
@property (nonatomic, assign) BOOL                        cloudSelectAllState;

@property (nonatomic, strong) UIView                     *cloudFileHandleTabBarView;
@property (nonatomic, strong) CloudFileHandleMenuView    *cloudFileHandleMenu;
@property (nonatomic, strong) UIButton                   *cloudFileDownloadButton;
@property (nonatomic, strong) UIButton                   *cloudFileShareButton;
@property (nonatomic, strong) UIButton                   *cloudFileDeleteButton;
@property (nonatomic, strong) UIButton                   *cloudFileMoreButton;

@property (nonatomic, strong) UIView                     *cloudFileNullView;
@end

@implementation CloudFileViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.LocalizedChange" object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.popUpLayer" object:nil];
    
}

- (id)initWithFile:(File*)file {
    self = [super init];
    if (self) {
        self.file = file;
        self.cloudSelectedArray = [[NSMutableArray alloc] init];
        self.cloudSelectAllState = NO;
        self.cloudSelectState = NO;
        self.cloudSearchState = NO;
        self.cloudFileLoadState = NO;
        
        self.cloudSettingButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        self.cloudSettingButton.hidden = YES;
        self.cloudSettingButton.imageView.frame = CGRectMake(11, 11, 22, 22);
        [self.cloudSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_nor"] forState:UIControlStateNormal];
        [self.cloudSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_press"] forState:UIControlStateHighlighted];
        [self.cloudSettingButton addTarget:self action:@selector(cloudSettingButtonClick) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudSearchButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        self.cloudSearchButton.hidden = YES;
        self.cloudSearchButton.imageView.frame = CGRectMake(11, 11, 22, 22);
        [self.cloudSearchButton setImage:[UIImage imageNamed:@"ic_nav_search_nor"] forState:UIControlStateNormal];
        [self.cloudSearchButton setImage:[UIImage imageNamed:@"ic_nav_search_press"] forState:UIControlStateHighlighted];
        [self.cloudSearchButton addTarget:self action:@selector(cloudSearchButtonClick) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudSearchCancelButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        self.cloudSearchCancelButton.hidden = YES;
        self.cloudSearchCancelButton.imageView.frame = CGRectMake(11, 11, 44, 44);
        [self.cloudSearchCancelButton setTitle:getLocalizedString(@"Undo", nil) forState:UIControlStateNormal];
//        [self.cloudSearchCancelButton setImage:[UIImage imageNamed:@"ic_menu_enter_nor"] forState:UIControlStateNormal];
//        [self.cloudSearchCancelButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
        [self.cloudSearchCancelButton addTarget:self action:@selector(cloudSearchCancelButtonClick) forControlEvents:UIControlEventTouchUpInside];
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
        self.cloudBackButton.imageView.frame = CGRectMake(11, 11, 44, 44);
//        [self.cloudBackButton setTitle:getLocalizedString(@"undo", nil) forState:UIControlStateNormal];
        [self.cloudBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
//        [self.cloudBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
        [self.cloudBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
        
        self.cloudMenuButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        self.cloudMenuButton.hidden = YES;
        self.cloudMenuButton.imageView.frame = CGRectMake(11, 11, 22, 22);
        [self.cloudMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_nor"] forState:UIControlStateNormal];
        [self.cloudMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_press"] forState:UIControlStateHighlighted];
        [self.cloudMenuButton addTarget:self action:@selector(cloudMenuButtonClick) forControlEvents:UIControlEventTouchUpInside];
        
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        UserSetting *userSetting = [UserSetting defaultSetting];
        
        NSPredicate *preficate = [NSPredicate predicateWithFormat:@"fileId != NULL AND fileParent = %@ AND fileOwner = %@",file.fileId,file.fileOwner];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:userSetting.cloudSortType ascending:userSetting.cloudSortNameOrder.boolValue];
        NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"fileModifiedDate" ascending:NO];
        if ([userSetting.cloudSortType isEqualToString:@"fileSortTimeKey"]) {
            sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:userSetting.cloudSortType ascending:userSetting.cloudSortTimeOrder.boolValue];
            dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"fileModifiedDate" ascending:userSetting.cloudSortTimeOrder.boolValue];
        }
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        [fetchRequest setPredicate:preficate];
        [fetchRequest setEntity:entity];
        [fetchRequest setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor,dateDescriptor,nil]];
        [fetchRequest setFetchBatchSize:20];
        _cloudFileFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:[sortDescriptor key] cacheName:nil];
        [_cloudFileFetchController setDelegate:self];
        [self performFetch];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(localizedChange) name:@"com.huawei.onemail.LocalizedChange" object:nil];
        
         [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(popUpLayer) name:@"com.huawei.onemail.popUpLayer" object:nil];
    }
    return self;
}

- (void)popUpLayer {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
         [self setupRefresh];
    });
  
}

- (void)localizedChange{
    _cloudFileHandleMenu = nil;
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
    if (![self.cloudFileFetchController performFetch:&error]) {
        NSLog(@"Unresolved error %@,%@",error,[error userInfo]);
        abort();
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    self.view.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];//3592E2
    
    [self.cloudTitleView setViewFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    [self.navigationController.navigationBar addSubview:self.cloudTitleView];
    
    self.cloudSettingButton.frame = CGRectMake(4, 0, CGRectGetWidth(self.cloudSettingButton.frame), CGRectGetHeight(self.cloudSettingButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSettingButton];
    
    self.cloudSearchButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-CGRectGetWidth(self.cloudMenuButton.frame)-CGRectGetWidth(self.cloudSearchButton.frame), 0, CGRectGetWidth(self.cloudSearchButton.frame), CGRectGetHeight(self.cloudSearchButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSearchButton];
    
    self.cloudSearchCancelButton.frame = CGRectMake(267, 0, CGRectGetWidth(self.cloudSearchCancelButton.frame), CGRectGetHeight(self.cloudSearchCancelButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSearchCancelButton];
    
    self.cloudSelectAllButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(self.cloudSelectAllButton.frame), 0, CGRectGetWidth(self.cloudSelectAllButton.frame), CGRectGetHeight(self.cloudSelectAllButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSelectAllButton];
    
    self.cloudSelectCancelButton.frame = CGRectMake(15, 0, CGRectGetWidth(self.cloudSelectCancelButton.frame), CGRectGetHeight(self.cloudSelectCancelButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudSelectCancelButton];
    
    self.cloudBackButton.frame = CGRectMake(4, 0, CGRectGetWidth(self.cloudBackButton.frame), CGRectGetHeight(self.cloudBackButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudBackButton];
    
    self.cloudMenuButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-4-CGRectGetWidth(self.cloudMenuButton.frame), 0, CGRectGetWidth(self.cloudMenuButton.frame), CGRectGetHeight(self.cloudMenuButton.frame));
    [self.navigationController.navigationBar addSubview:self.cloudMenuButton];
    
    if ([self.file isCloudRoot]) {
        self.cloudSettingButton.hidden = NO;
        self.cloudBackButton.hidden = YES;
    } else {
        self.cloudSettingButton.hidden = YES;
        self.cloudBackButton.hidden = NO;
    }
    if ([[self.crumbFileIds objectAtIndex:0] isEqualToString:@"search"]) {
        self.cloudMenuButton.hidden = NO;
        self.cloudSearchButton.hidden = YES;
    } else {
        self.cloudMenuButton.hidden = NO;
        self.cloudSearchButton.hidden = NO;
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
    self.cloudFileCrumbView = [[CloudCrumbView alloc] initWithFiles:(NSArray*)self.crumbFileIds fileOwners:(NSArray*)self.crumbFileOwners];
    self.cloudFileCrumbView.backgroundColor = [UIColor whiteColor];
    self.cloudFileCrumbView.navigationController = self.navigationController;
    self.cloudFileCrumbView.viewControllers = self.crumbFileViewControllers;
    self.cloudFileCrumbView.frame = CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.cloudFileCrumbView.frame), CGRectGetHeight(self.cloudFileCrumbView.frame));
    [self.view addSubview:self.cloudFileCrumbView];
    
    self.cloudFileTableView = [[UITableView alloc]initWithFrame:self.view.frame style:UITableViewStylePlain];
    [self.cloudFileTableView registerClass:[CloudFileTableViewCell class] forCellReuseIdentifier:@"CloudFileTableViewCell"];
    self.cloudFileTableView.backgroundColor = [UIColor clearColor];
    self.cloudFileTableView.dataSource = self;
    self.cloudFileTableView.delegate = self;
    self.cloudFileTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.cloudFileTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.cloudFileTableView.sectionIndexBackgroundColor = [UIColor clearColor];
    self.cloudFileTableView.sectionIndexTrackingBackgroundColor = [UIColor clearColor];
    self.cloudFileTableView.sectionIndexColor = [UIColor colorWithRed:126/255.0f green:126/255.0f blue:128/255.0f alpha:1.0f];
    self.cloudFileTableView.tableFooterView = [[UIView alloc] init];
    self.cloudFileTableView.hidden = NO;
    [self.view addSubview:self.cloudFileTableView];
    
    UILongPressGestureRecognizer *fileMultiSelectGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(cloudGestureSelect:)];
    [self.cloudFileTableView addGestureRecognizer:fileMultiSelectGesture];
    
    //添加下拉刷新控件
    [self setupRefresh];
//    self.cloudFileTableView.tableHeaderView = self.cloudFileRefreshController;
    
    CGRect tableViewFrame = self.cloudFileTableView.frame;
    tableViewFrame.origin.y = tableViewFrame.origin.y+CGRectGetHeight(self.cloudFileCrumbView.frame)-CGRectGetHeight(self.cloudFileRefreshController.frame);
    tableViewFrame.size.height = tableViewFrame.size.height+CGRectGetHeight(self.cloudFileRefreshController.frame)-CGRectGetHeight(self.cloudFileCrumbView.frame);
    self.cloudFileTableView.frame = tableViewFrame;
    
    self.cloudSearchTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height+CGRectGetHeight(self.cloudFileCrumbView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-CGRectGetHeight(self.cloudFileCrumbView.frame)) style:UITableViewStylePlain];
    [self.cloudSearchTableView registerClass:[CloudFileTableViewCell class] forCellReuseIdentifier:@"CloudFileTableViewCell"];
    self.cloudSearchTableView.backgroundColor = [UIColor clearColor];
    self.cloudSearchTableView.delegate = self;
    self.cloudSearchTableView.dataSource = self;
    self.cloudSearchTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.cloudSearchTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.cloudSearchTableView.tableFooterView = [[UIView alloc] init];
    self.cloudSearchTableView.hidden = YES;
    [self.view addSubview:self.cloudSearchTableView];
    UILongPressGestureRecognizer *searchMultiSelectGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(cloudGestureSelect:)];
    [self.cloudSearchTableView addGestureRecognizer:searchMultiSelectGesture];
    
    self.cloudFileAddView = [[CloudFileAddView alloc] initWithFrame:self.view.frame];
    self.cloudFileAddView.fileViewController = self;
    self.cloudFileAddView.uploadAction = @selector(fileUpload);
    self.cloudFileAddView.createFolderAction = @selector(fileCreateFolder);
    
    CGRect tabBarFrame = appDelegate.mainTabBar.frame;
    self.cloudFileAddButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-10-65, CGRectGetHeight(self.view.frame)-tabBarFrame.size.height-10-65, 65, 65)];
    self.cloudFileAddButton.hidden = NO;
    [self.cloudFileAddButton setImage:[UIImage imageNamed:@"btn_new_nor"] forState:UIControlStateNormal];
    [self.cloudFileAddButton addTarget:self action:@selector(showFileAddView) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.cloudFileAddButton];
    [self.view bringSubviewToFront:self.cloudFileCrumbView];
    [self reloadDataSource];
    
    if (self.cloudTitleView.titleStyle == CloudTitleMySpaceStyle) {
        UISwipeGestureRecognizer *handleRevognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handSwipeRight:)];
        handleRevognizer.direction = UISwipeGestureRecognizerDirectionRight;
        [self.view addGestureRecognizer:handleRevognizer];
    }
//    if (self.cloudTitleView.titleStyle == CloudTitleTeamSapceStyle) {
//        UISwipeGestureRecognizer *handleRevognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handSwipe:)];
//        handleRevognizer.direction = UISwipeGestureRecognizerDirectionRight;
//        [self.view addGestureRecognizer:handleRevognizer];
//    }
}

- (void)handSwipeRight:(UISwipeGestureRecognizer*)sender {
    if (self.cloudSelectState) {
        return;
    }
    [self cloudSettingButtonClick];
}


- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    self.cloudTitleView.hidden = YES;
    self.cloudSettingButton.hidden = YES;
    self.cloudSearchButton.hidden = YES;
    self.cloudSearchCancelButton.hidden = YES;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = YES;
    self.cloudFileAddButton.hidden = YES;
    if (self.cloudSearchNavigationBarView.superview) {
        [self.cloudSearchNavigationBarView removeFromSuperview];
    }
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.cloudTitleView.hidden = NO;
    self.cloudSettingButton.hidden = YES;
    self.cloudSearchButton.hidden = YES;
    self.cloudSearchCancelButton.hidden = YES;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = NO;
    self.cloudFileAddButton.hidden = NO;
    if ([self.file isCloudRoot]) {
        self.cloudSettingButton.hidden = NO;
        self.cloudBackButton.hidden = YES;
    } else {
        self.cloudSettingButton.hidden = YES;
        self.cloudBackButton.hidden = NO;
    }
    if ([[self.crumbFileIds objectAtIndex:0] isEqualToString:@"search"]) {
        self.cloudMenuButton.hidden = NO;
        self.cloudSearchButton.hidden = YES;
    } else {
        self.cloudMenuButton.hidden = NO;
        self.cloudSearchButton.hidden = NO;
    }
    if (self.cloudSearchState == YES) {
        self.cloudTitleView.hidden = YES;
        self.cloudSettingButton.hidden = YES;
        self.cloudSearchCancelButton.hidden = NO;
        self.cloudSearchButton.hidden = YES;
        self.cloudMenuButton.hidden = YES;
        self.cloudBackButton.hidden = YES;
        self.cloudFileAddButton.hidden = YES;
        [self.navigationController.navigationBar addSubview:self.cloudSearchNavigationBarView];
    }
    [[UIApplication sharedApplication]setStatusBarStyle:UIStatusBarStyleLightContent];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabShow" object:nil];
    [self reloadDataSource];
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
        
        UILabel * cloudFileNullTitle = [[UILabel alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(cloudFileNullImage.frame) + 10, self.view.frame.size.width - 30, 20)];
        cloudFileNullTitle.text = getLocalizedString(@"CloudNoFile", nil);
        cloudFileNullTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1];
        cloudFileNullTitle.font = [UIFont systemFontOfSize:15];
        cloudFileNullTitle.textAlignment = NSTextAlignmentCenter;
        [_cloudFileNullView addSubview:cloudFileNullTitle];
        
        UILabel *cloudFileNullPrompt = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:14] textColor:[CommonFunction colorWithString:@"666666" alpha:1] textAlignment:NSTextAlignmentCenter];
        cloudFileNullPrompt.text = getLocalizedString(@"CloudNoFileNotification", nil);
        cloudFileNullPrompt.numberOfLines = 0;
        CGSize size = [CommonFunction labelSizeWithLabel:cloudFileNullPrompt limitSize:CGSizeMake(self.view.frame.size.width - 30, 1000)];
        cloudFileNullPrompt.frame = CGRectMake(20, CGRectGetMaxY(cloudFileNullTitle.frame) + 10, size.width,size.height);

        [_cloudFileNullView addSubview:cloudFileNullPrompt];
        
        [self.view addSubview:_cloudFileNullView];
    }
    return _cloudFileNullView;
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
   [self.navigationController popViewControllerAnimated:YES];
     [self reloadDataSource];
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
    NSFetchRequest *request = self.cloudFileFetchController.fetchRequest;
    [request setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor, nameSortDescripter, nil]];
    self.cloudFileFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:[appDelegate.localManager managedObjectContext] sectionNameKeyPath:[sortDescriptor key] cacheName:nil];
    self.cloudFileFetchController.delegate = self;
    [self performFetch];
    [self.cloudFileTableView reloadData];
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
    NSFetchRequest* request = self.cloudFileFetchController.fetchRequest;
    [request setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor,nameSortDescriptor,nil]];
    self.cloudFileFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:[appDelegate.localManager managedObjectContext] sectionNameKeyPath:[sortDescriptor key] cacheName:nil];
    self.cloudFileFetchController.delegate = self;
    [self performFetch];
    [self.cloudFileTableView reloadData];
}

#pragma mark UIRefreshViewController
//- (UIRefreshControl*)cloudFileRefreshController {
//    if (!_cloudFileRefreshController) {
//        UIRefreshControl *refreshController = [[UIRefreshControl alloc] init];
//        refreshController.attributedTitle = [[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudFileRefreshing", nil)];
//        [refreshController addTarget:self action:@selector(refreshView:) forControlEvents:UIControlEventValueChanged];
//        _cloudFileRefreshController = refreshController;
//    }
//    return _cloudFileRefreshController;
//}

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
    self.cloudFileTableView.mj_header = header;
}

- (void)loadNewData
{
    __weak UITableView *tableView = self.cloudFileTableView;
    
    if (self.cloudSelectState) {
        [tableView.mj_header endRefreshing];
        return;
    }
    [self reloadDataSource];
    
}
#pragma mark TableView Delegate + DataSource

- (void)reloadDataSource {
    if (self.cloudFileLoadState) {
        return;
    } else {
        self.cloudFileLoadState = YES;
    }
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        __block __weak typeof(self) weakSelf = self;
        __weak UITableView *tableView = self.cloudFileTableView;
        [self.file folderUpdate:^(id retobj) {
            dispatch_async(dispatch_get_main_queue(), ^{
                __strong typeof(weakSelf) strongSelf = weakSelf;
                strongSelf.cloudFileLoadState = NO;
                
                if ([tableView.mj_header isRefreshing]) {
                    [tableView.mj_header endRefreshing];
                }
                
                if (self.cloudFileFetchController.fetchedObjects.count == 0) {
                    self.cloudFileNullView.hidden = NO;
                } else {
                    self.cloudFileNullView.hidden = YES;
                }
                
            });
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                __strong typeof(weakSelf) strongSelf = weakSelf;
                strongSelf.cloudFileLoadState = NO;
                if ([tableView.mj_header isRefreshing]) {
                    [tableView.mj_header endRefreshing];
                }
                
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileLoadFailedPrompt", nil)];
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                if (httpResponse.statusCode == 404) {
                    [strongSelf.file fileRemove:nil];
                    [strongSelf.navigationController popToRootViewControllerAnimated:YES];
                }
                
                if (self.cloudFileFetchController.fetchedObjects.count == 0) {
                    self.cloudFileNullView.hidden = NO;
                } else {
                    self.cloudFileNullView.hidden = YES;
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
    if (self.cloudFileTableView == tableView) {
        NSMutableArray *titleOfSection = [[NSMutableArray alloc] init];
        for (int i = 0; i < [self.cloudFileFetchController sections].count; i++) {
            [titleOfSection addObject:[[[self.cloudFileFetchController sections] objectAtIndex:i] name]];
        }
        if (titleOfSection.copy > 0) {
            [titleOfSection insertObject:UITableViewIndexSearch atIndex:0];
        }
        return titleOfSection;
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index {
    if (title == UITableViewIndexSearch) {
        [self.cloudFileTableView scrollRectToVisible:self.searchDisplayController.searchBar.frame animated:NO];
        return -1;
    }
    return index - 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.cloudFileTableView == tableView) {
        if ([self.cloudFileFetchController sections].count > 0) {
            tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        } else {
            tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        }
        return [self.cloudFileFetchController sections].count;
    } else {
        if ([self.cloudSearchFetchController sections].count > 0) {
            tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        } else {
            tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        }
        return 1;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.cloudFileTableView == tableView) {
        id<NSFetchedResultsSectionInfo> sectionInfo = [[self.cloudFileFetchController sections] objectAtIndex:section];
        return [sectionInfo numberOfObjects];
    } else {
        return self.cloudSearchFetchController.fetchedObjects.count;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (self.cloudFileTableView == tableView) {
        return 22.0f;
    } else {
        return 0.1f;
    }
}

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (self.cloudFileTableView == tableView) {
        id<NSFetchedResultsSectionInfo> sectionInfo = [[self.cloudFileFetchController sections] objectAtIndex:section];
        return [CommonFunction tableViewHeaderWithTitle:[sectionInfo name]];
    } else {
        return nil;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSFetchedResultsController *fetchController = nil;
    if (self.cloudFileTableView == tableView) {
        fetchController = self.cloudFileFetchController;
    } else {
        fetchController = self.cloudSearchFetchController;
    }
    CloudFileTableViewCell *cell = (CloudFileTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"CloudFileTableViewCell"];
    if (cell == nil) {
        cell = [[CloudFileTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudFileTableViewCell"];
    }
    File *file = [fetchController objectAtIndexPath:indexPath];
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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSFetchedResultsController *fetchController = nil;
    if (self.cloudFileTableView == tableView) {
        fetchController = self.cloudFileFetchController;
    } else {
        fetchController = self.cloudSearchFetchController;
    }
    CloudFileTableViewCell *cell = (CloudFileTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
    File *file = [fetchController objectAtIndexPath:indexPath];
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
            if (self.cloudSearchState) {
                CloudFileViewController *child = [[CloudFileViewController alloc] initWithFile:file];
                child.cloudTitleView = self.cloudTitleView;
                child.crumbFileIds = [NSMutableArray arrayWithObject:@"search"];
                child.crumbFileOwners = [NSMutableArray arrayWithObject:[NSString stringWithFormat:@"%lu",(unsigned long)self.cloudSearchFetchController.fetchedObjects.count]];
                child.crumbFileViewControllers = [NSMutableArray arrayWithObject:self];
                [self.navigationController pushViewController:child animated:YES];
            } else {
                CloudFileViewController *child = [[CloudFileViewController alloc] initWithFile:file];
                child.cloudTitleView = self.cloudTitleView;
                child.crumbFileIds = self.crumbFileIds;
                child.crumbFileOwners = self.crumbFileOwners;
                child.crumbFileViewControllers = self.crumbFileViewControllers;
                [self.navigationController pushViewController:child animated:NO];
            }
        } else {
            CloudPreviewController *previewController = [[CloudPreviewController alloc] initWithFile:file];
            [self.navigationController pushViewController:previewController animated:YES];
            
        }
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger sectionMax = [tableView numberOfSections];
    NSInteger rowNumOfSectionMax = [tableView numberOfRowsInSection:sectionMax-1];
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        if (indexPath.section == sectionMax-1 && indexPath.row == rowNumOfSectionMax-1) {
            [cell setSeparatorInset:UIEdgeInsetsZero];
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

- (NSArray *)tableView:(UITableView *)tableView editActionsForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewRowAction *action0 = [UITableViewRowAction rowActionWithStyle:UITableViewRowActionStyleNormal title:@"删除" handler:^(UITableViewRowAction * _Nonnull action, NSIndexPath * _Nonnull indexPath) {
        [UIAlertView showAlertViewWithTitle:getLocalizedString(@"CloudFileDeletePrompt", nil) message:@"" cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
            if (buttonIndex == 0) {
                File *file = [self.cloudFileFetchController objectAtIndexPath:indexPath];
                if (file.transportTask) {
                    [file.transportTask.taskHandle suspend];
                }
                [file fileRemove:^(id retobj) {
                    [file fileRemove:nil];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self cloudEndSelect:nil];
                    });
                } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                    if (httpResponse.statusCode == 404) {
                        [file fileRemove:nil];
                    } else {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self cloudEndSelect:nil];
                        });
                    }
                }];
            }
        } onCancel:^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self cloudEndSelect:nil];
            });
        }];
    }];
    action0.backgroundColor = [UIColor redColor];
    
    UITableViewRowAction *action1 = [UITableViewRowAction rowActionWithStyle:UITableViewRowActionStyleNormal title:@"分享" handler:^(UITableViewRowAction * _Nonnull action, NSIndexPath * _Nonnull indexPath) {
        
        if (self.cloudSelectedArray.count == 0) {
            File *file = [self.cloudFileFetchController objectAtIndexPath:indexPath];
            CloudFileShareInfoViewController *shareInfoViewController = [[CloudFileShareInfoViewController alloc] initWithFile:file];
            [self.navigationController pushViewController:shareInfoViewController animated:YES];
        } else {
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileShareSinglePrompt", nil)];
        }
        [self cloudEndSelect:nil];
    }];
    action1.backgroundColor = [UIColor orangeColor];
    
    UITableViewRowAction *action2 = [UITableViewRowAction rowActionWithStyle:UITableViewRowActionStyleNormal title:@"收藏" handler:^(UITableViewRowAction * _Nonnull action, NSIndexPath * _Nonnull indexPath) {
        
    }];
    action2.backgroundColor = [UIColor lightGrayColor];
    //    action2.backgroundEffect = [UIBlurEffect effectWithStyle:UIBlurEffectStyleExtraLight];
    
    if (!self.cloudSelectState) {
        return @[action0,action1];
    } else {
        return 0;
    }
}

#pragma mark Multi Select
- (void)cloudGestureSelect:(UIGestureRecognizer *)gesture{
    if (self.cloudSelectState) {
        return;
    }
    if (gesture.state == UIGestureRecognizerStateBegan) {
        [self cloudBeginSelect];
        CGPoint location = [gesture locationInView:self.cloudFileTableView];
        NSIndexPath *indexPath = [self.cloudFileTableView indexPathForRowAtPoint:location];
        NSFetchedResultsController *fetchController = nil;
        UITableView *tableView = nil;
        if (self.cloudSearchState == NO) {
            fetchController = self.cloudFileFetchController;
            tableView = self.cloudFileTableView;
        } else {
            fetchController = self.cloudSearchFetchController;
            tableView = self.cloudSearchTableView;
        }
        CloudFileTableViewCell *cell = (CloudFileTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
        File *file = [fetchController objectAtIndexPath:indexPath];
        if (cell.fileSelected == YES) {
            [self.cloudSelectedArray addObject:file];
        }
        
        
        self.title = [NSString stringWithFormat:getLocalizedString(@"CloudFileMultiSelectTitle", nil),self.cloudSelectedArray.count];
    }
}
- (void)cloudBeginSelect{
    self.cloudSelectState = YES;
    self.cloudTitleView.hidden = YES;
    self.cloudSettingButton.hidden = YES;
    self.cloudSearchButton.hidden = YES;
    self.cloudSearchCancelButton.hidden = YES;
    self.cloudSelectAllButton.hidden = NO;
    self.cloudSelectCancelButton.hidden = NO;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = YES;
    self.cloudFileAddButton.hidden = YES;
    
    self.title = [NSString stringWithFormat:getLocalizedString(@"CloudFileMultiSelectTitle", nil),self.cloudSelectedArray.count];
    [self showSelectTabBarView];
    if (self.cloudSearchState) {
        [self.cloudSearchNavigationBarView removeFromSuperview];
        [self.cloudSearchTableView reloadData];
    } else {
        [self.cloudFileTableView reloadData];
    }
}

- (void)cloudEndSelect:(id)sender {
    self.cloudSelectState = NO;
    self.cloudSelectAllState = NO;
    [self.cloudSelectedArray removeAllObjects];
    self.cloudTitleView.hidden = YES;
    self.cloudSettingButton.hidden = YES;
    self.cloudSearchButton.hidden = YES;
    self.cloudSearchCancelButton.hidden = YES;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = YES;
    self.cloudFileAddButton.hidden = YES;
    
    self.title = nil;
    [self hideSelectTabBarView];
    if (self.cloudSearchState) {
        self.cloudSearchCancelButton.hidden = NO;
        [self.navigationController.navigationBar addSubview:self.cloudSearchNavigationBarView];
        [self.cloudSearchTableView reloadData];
    } else {
        self.cloudTitleView.hidden = NO;
        self.cloudSearchButton.hidden = NO;
        self.cloudMenuButton.hidden = NO;
        self.cloudFileAddButton.hidden = NO;
        if ([self.file isCloudRoot]) {
            self.cloudSettingButton.hidden = NO;
        } else {
            self.cloudBackButton.hidden = NO;
        }
        [self.cloudFileTableView reloadData];
    }
}

- (void)cloudSelectAll {
    [self.cloudSelectedArray removeAllObjects];
    if (self.cloudSelectAllState) {
        self.cloudSelectAllState = NO;
    } else {
        self.cloudSelectAllState = YES;
        if (self.cloudSearchState) {
            [self.cloudSelectedArray addObjectsFromArray:self.cloudSearchFetchController.fetchedObjects];
        } else {
            [self.cloudSelectedArray addObjectsFromArray:self.cloudFileFetchController.fetchedObjects];
        }
    }
    if (self.cloudSearchState) {
        [self.cloudSearchTableView reloadData];
    } else {
        [self.cloudFileTableView reloadData];
    }
    self.title = [NSString stringWithFormat:getLocalizedString(@"CloudFileMultiSelectTitle", nil),self.cloudSelectedArray.count];
}

- (UIView*)cloudFileHandleTabBarView {
    if (!_cloudFileHandleTabBarView) {
        CGRect tabBarFrame = self.tabBarController.tabBar.frame;
        _cloudFileHandleTabBarView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(tabBarFrame)+CGRectGetHeight(tabBarFrame), CGRectGetWidth(tabBarFrame), CGRectGetHeight(tabBarFrame))];
        _cloudFileHandleTabBarView.backgroundColor = [UIColor colorWithRed:255.0/255.0 green:255.0/255.0 blue:255.0/255.0 alpha:1];
        [[UIApplication sharedApplication].keyWindow.rootViewController.view addSubview:_cloudFileHandleTabBarView];
        
        self.cloudFileDownloadButton.frame = CGRectMake(0, 0, tabBarFrame.size.width/4, tabBarFrame.size.height);
        [_cloudFileHandleTabBarView addSubview:self.cloudFileDownloadButton];
        
        self.cloudFileShareButton.frame = CGRectMake(tabBarFrame.size.width/4, 0, tabBarFrame.size.width/4, tabBarFrame.size.height);
        [_cloudFileHandleTabBarView addSubview:self.cloudFileShareButton];
        
        self.cloudFileDeleteButton.frame = CGRectMake(tabBarFrame.size.width/4*2, 0, tabBarFrame.size.width/4, tabBarFrame.size.height);
        [_cloudFileHandleTabBarView addSubview:self.cloudFileDeleteButton];
        
        self.cloudFileMoreButton.frame = CGRectMake(tabBarFrame.size.width/4*3, 0, tabBarFrame.size.width/4, tabBarFrame.size.height);
        [_cloudFileHandleTabBarView addSubview:self.cloudFileMoreButton];
    }
    return _cloudFileHandleTabBarView;
}

- (UIButton*)cloudFileDownloadButton {
    if (!_cloudFileDownloadButton) {
        _cloudFileDownloadButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [_cloudFileDownloadButton setImage:[UIImage imageNamed:@"ic_tab_download_nor"] forState:UIControlStateNormal];
        [_cloudFileDownloadButton setImage:[UIImage imageNamed:@"ic_tab_download_sel"] forState:UIControlStateSelected];
        [_cloudFileDownloadButton setTitleColor:[UIColor colorWithRed:0 green:139/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
        [_cloudFileDownloadButton addTarget:self action:@selector(fileDownload:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cloudFileDownloadButton;
}

- (UIButton*)cloudFileShareButton {
    if (!_cloudFileShareButton) {
        _cloudFileShareButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [_cloudFileShareButton setImage:[UIImage imageNamed:@"ic_tab_share_nor"] forState:UIControlStateNormal];
        [_cloudFileShareButton setImage:[UIImage imageNamed:@"ic_tab_share_sel"] forState:UIControlStateSelected];
        [_cloudFileShareButton setTitleColor:[UIColor colorWithRed:0 green:139/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
        [_cloudFileShareButton addTarget:self action:@selector(fileShare:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cloudFileShareButton;
}

- (UIButton*)cloudFileDeleteButton {
    if (!_cloudFileDeleteButton) {
        _cloudFileDeleteButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [_cloudFileDeleteButton setImage:[UIImage imageNamed:@"ic_tab_delete_nor"] forState:UIControlStateNormal];
        [_cloudFileDeleteButton setImage:[UIImage imageNamed:@"ic_tab_delete_sel"] forState:UIControlStateSelected];
        [_cloudFileDeleteButton setTitleColor:[UIColor colorWithRed:0 green:139/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
        [_cloudFileDeleteButton addTarget:self action:@selector(fileDelete:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cloudFileDeleteButton;
}

- (UIButton*)cloudFileMoreButton {
    if (!_cloudFileMoreButton) {
        _cloudFileMoreButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [_cloudFileMoreButton setImage:[UIImage imageNamed:@"ic_tab_more_nor"] forState:UIControlStateNormal];
        [_cloudFileMoreButton setImage:[UIImage imageNamed:@"ic_tab_more_sel"] forState:UIControlStateSelected];
        [_cloudFileMoreButton setTitleColor:[UIColor colorWithRed:0 green:139/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
        [_cloudFileMoreButton addTarget:self action:@selector(showMoreButtonView) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cloudFileMoreButton;
}

- (CloudFileHandleMenuView*)cloudFileHandleMenu {
    if (!_cloudFileHandleMenu) {
        _cloudFileHandleMenu = [[CloudFileHandleMenuView alloc] initWithFrame:self.view.frame];
        _cloudFileHandleMenu.fileViewController = self;
        _cloudFileHandleMenu.viewControlButton = self.cloudFileMoreButton;
        CloudFileHandleMenuCell *storgeToTeamSpaceMenuCell = [[CloudFileHandleMenuCell alloc] initWithTitle:getLocalizedString(@"CloudFileTransmitTeamSpace", nil) target:self action:@selector(fileSaveToTeamSpace)];
        CloudFileHandleMenuCell *moveMenuCell = [[CloudFileHandleMenuCell alloc] initWithTitle:getLocalizedString(@"CloudFileMove", nil) target:self action:@selector(fileMove)];
        CloudFileHandleMenuCell *renameMenuCell = [[CloudFileHandleMenuCell alloc] initWithTitle:getLocalizedString(@"CloudFileRename", nil) target:self action:@selector(fileRename)];
        CloudFileHandleMenuCell *viewVersionMenuCell = [[CloudFileHandleMenuCell alloc] initWithTitle:getLocalizedString(@"CloudFileVersion", nil) target:self action:@selector(fileVersion)];
        [_cloudFileHandleMenu setMenuCells:@[storgeToTeamSpaceMenuCell,moveMenuCell,renameMenuCell,viewVersionMenuCell]];
    }
    return _cloudFileHandleMenu;
}

- (void)showMoreButtonView {
    if (self.cloudFileHandleMenu.hidden) {
        self.cloudFileMoreButton.selected = YES;
        self.cloudFileHandleMenu.hidden = NO;
    } else {
        self.cloudFileMoreButton.selected = NO;
        self.cloudFileHandleMenu.hidden = YES;
    }
}

- (void)showSelectTabBarView {
    self.cloudFileDownloadButton.selected = NO;
    self.cloudFileShareButton.selected = NO;
    self.cloudFileDeleteButton.selected = NO;
    self.cloudFileMoreButton.selected = NO;
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
    [UIView setAnimationDuration:0.3f];
    CGRect tabBarFrame = self.tabBarController.tabBar.frame;
    tabBarFrame.origin.y = tabBarFrame.origin.y + 0.5;
    self.cloudFileHandleTabBarView.frame = tabBarFrame;
    [UIView commitAnimations];
}

- (void)hideSelectTabBarView {
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
    [UIView setAnimationDuration:0.3f];
    CGRect tabBarFrame = self.tabBarController.tabBar.frame;
    tabBarFrame.origin.y = tabBarFrame.origin.y + tabBarFrame.size.height;
    self.cloudFileHandleTabBarView.frame = tabBarFrame;
    [UIView commitAnimations];
}

#pragma mark File Operation

- (void)fileDownload:(UIButton*)button {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    button.selected = YES;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    BOOL hasNetWork = appDelegate.hasNetwork;
    if (!hasNetWork) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudNoneNetworkPrompt", nil)];
        });
    } else {
        BOOL WiFiNetWork = appDelegate.wifiNetwork;
        UserSetting *userSetting = [UserSetting defaultSetting];
        if (!WiFiNetWork && userSetting.cloudAssetBackupWifi.integerValue == 1) {
            [UIAlertView showAlertViewWithTitle:nil message:getLocalizedString(@"CloudDownloadWIFIPrompt", nil) cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                [self doDownloading:YES];
            } onCancel:^{
                [self doDownloading:NO];
            }];
        } else {
            [self doDownloading:YES];
        }
    }
    [self cloudEndSelect:nil];
}

- (void)doDownloading:(BOOL)force {
    
    for (File *file in self.cloudSelectedArray) {
        if ([file isFile]) {
            [file downloadVisiable:YES force:force];
            [[UIApplication sharedApplication].keyWindow makeToast:[NSString stringWithFormat:getLocalizedString(@"CloudDownloadAddSuccessPrompt", nil)]];
        } else {
            [file folderRansack:^(id retobj) {
                [file downloadVisiable:YES force:force];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                NSLog(@"%@",error);
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:[NSString stringWithFormat:getLocalizedString(@"CloudDownloadAddFailedPrompt", nil)]];
                });
            }];
        }
    }
}


//- (void)fileShare1:(UIButton*)button {
//    if (self.cloudSelectedArray.count == 0) {
//        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
//        return;
//    }
//    [self fileEditPremission:^(BOOL permission) {
//        if (permission) {
//            button.selected = YES;
//            if (self.cloudSelectedArray.count == 1) {
//                CloudFileShareInfoViewController *shareInfoViewController = [[CloudFileShareInfoViewController alloc] initWithFile:self.cloudSelectedArray.firstObject];
//                dispatch_async(dispatch_get_main_queue(), ^{
//                    [self cloudEndSelect:nil];
//                    [self.navigationController pushViewController:shareInfoViewController animated:YES];
//                });
//            } else {
//                dispatch_async(dispatch_get_main_queue(), ^{
//                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileShareSinglePrompt", nil)];
//                    [self cloudEndSelect:nil];
//                });
//            }
//        } else {
//            dispatch_async(dispatch_get_main_queue(), ^{
//                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"用户无权限", nil)];
//                [self cloudEndSelect:nil];
//            });
//        }
//    }];
//}



- (void)fileShare:(UIButton*)button {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    button.selected = YES;
    if (self.cloudSelectedArray.count == 1) {
        CloudFileShareInfoViewController *shareInfoViewController = [[CloudFileShareInfoViewController alloc] initWithFile:self.cloudSelectedArray.firstObject];
        [self.navigationController pushViewController:shareInfoViewController animated:YES];
    } else {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileShareSinglePrompt", nil)];
    }
    [self cloudEndSelect:nil];
   
}

- (void)fileDelete:(UIButton*)button {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    button.selected = YES;
    [UIAlertView showAlertViewWithTitle:getLocalizedString(@"CloudFileDeletePrompt", nil) message:@"" cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
        if (buttonIndex == 0) {
            for (File *file in self.cloudSelectedArray) {
                if (file.transportTask) {
                    [file.transportTask.taskHandle suspend];
                }
                [file fileRemove:^(id retobj) {
                    [file fileRemove:nil];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self cloudEndSelect:nil];
                    });
                } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                    if (httpResponse.statusCode == 404) {
                        [file fileRemove:nil];
                    } else {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
                        });
                    }
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self cloudEndSelect:nil];
                    });
                }];
            }
        }
    } onCancel:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            [self cloudEndSelect:nil];
        });
    }];
}

- (void)fileSaveToTeamSpace {
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
    CloudTeamMoveViewController *fileMoveVC = [[CloudTeamMoveViewController alloc] initWithSourceFiles:multiSelectedFileId filesOwner:multiSelectedFileOwner];
    fileMoveVC.rootViewController = self;
    [self.navigationController pushViewController:fileMoveVC animated:YES];
    [self cloudEndSelect:nil];
}

- (void)fileSaveToMySpace {
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
    CloudFileMoveViewController *fileMove = [[CloudFileMoveViewController alloc] initWithSourceFiles:multiSelectedFileId filesOwner:multiSelectedFileOwner rootFile:[File rootMyFolder]];
    fileMove.rootViewController = self;
    [self.navigationController pushViewController:fileMove animated:YES];
    [self cloudEndSelect:nil];
}

- (void)fileMove {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    if ((self.cloudSearchState == YES) && (self.cloudSelectedArray.count > 1)) {
        return;
    }
    NSMutableArray *multiSelectedFileId = [[NSMutableArray alloc] initWithCapacity:self.cloudSelectedArray.count];
    NSMutableArray *multiSelectedFileOwner = [[NSMutableArray alloc] initWithCapacity:self.cloudSelectedArray.count];
    for (File *file in self.cloudSelectedArray) {
        [multiSelectedFileId addObject:file.fileId];
        [multiSelectedFileOwner addObject:file.fileOwner];
    }
    CloudFileMoveViewController *fileMove = [[CloudFileMoveViewController alloc] initWithSourceFiles:multiSelectedFileId filesOwner:multiSelectedFileOwner rootFile:[File rootFolderWithOwner:self.file.fileOwner]];
    fileMove.rootViewController = self;
    [self.navigationController pushViewController:fileMove animated:YES];
    [self cloudEndSelect:nil];
}

- (void)fileRename {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    if (self.cloudSelectedArray.count != 1) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileRenameSinglePrompt", nil)];
        return;
    }
    CloudFileRenameViewController *renameFileController = [[CloudFileRenameViewController alloc] init];
    renameFileController.file = self.cloudSelectedArray.firstObject;
    [self.navigationController pushViewController:renameFileController animated:YES];
    [self cloudEndSelect:nil];
}

- (void)fileVersion {
    if (self.cloudSelectedArray.count == 0) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileNoneOperatePrompt", nil)];
        return;
    }
    if (self.cloudSelectedArray.count != 1) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudFileVersionSinglePrompt", nil)];
        return;
    }
    CloudFileVersionController *versionController = [[CloudFileVersionController alloc] initWithFile:self.cloudSelectedArray.firstObject];
    [self.navigationController pushViewController:versionController animated:YES];
    [self cloudEndSelect:nil];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0 && actionSheet.tag == 10001) {
        [self fileSaveToTeamSpace];
    } else if (buttonIndex == 1 && actionSheet.tag == 10001) {
        [self fileMove];
    } else if (buttonIndex == 2 && actionSheet.tag == 10001) {
        [self fileRename];
    } else if (buttonIndex == 3 && actionSheet.tag == 10001) {
        [self fileVersion];
    }
}

#pragma mark addFile
- (void)showFileAddView {
    [self.cloudFileAddView showView];
}

- (void)fileUpload {
    CloudAlbumViewController *albumController = [[CloudAlbumViewController alloc] init];
    albumController.rootViewController = self;
    albumController.uploadTargetFolderId = self.file.fileId;
    albumController.uploadTargetFolderOwner = self.file.fileOwner;
    [self.navigationController pushViewController:albumController animated:YES];
}

- (void)fileCreateFolder {
    CloudFolderCreateViewController *creatFolderVC = [[CloudFolderCreateViewController alloc]init];
    creatFolderVC.file = self.file;
    [self.navigationController pushViewController:creatFolderVC animated:NO];
}

#pragma mark UISearchBarDelegate
- (void)cloudSearchButtonClick {
    self.cloudSearchState = YES;
    self.cloudTitleView.hidden = YES;
    self.cloudSettingButton.hidden = YES;
    self.cloudSearchButton.hidden = YES;
    self.cloudSearchCancelButton.hidden = NO;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = YES;
    self.cloudFileAddButton.hidden = YES;
    
    CGRect searchViewFrame = self.cloudSearchNavigationBarView.frame;
    CGRect searchViewStartFrame = searchViewFrame;
    searchViewStartFrame.origin.x = CGRectGetWidth(self.view.frame)-(15+22+2);
    searchViewStartFrame.size.width = 0;
    self.cloudSearchNavigationBarView.frame = searchViewStartFrame;
    self.cloudSearchNavigationBarTextField.text = nil;
    [self.navigationController.navigationBar addSubview:self.cloudSearchNavigationBarView];
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.3];
    self.cloudSearchNavigationBarView.frame = searchViewFrame;
    [UIView commitAnimations];
}

- (void)cloudSearchCancelButtonClick {
    [self.cloudSearchNavifationBarActivityIndicator stopAnimating];
    self.cloudSearchNavifationBarImageView.hidden = NO;
    self.cloudSearchState = NO;
    [self.cloudSearchNavigationBarView removeFromSuperview];
    [self.cloudSearchCrumbView removeFromSuperview];
    self.cloudFileCrumbView.hidden = NO;
    
    self.cloudTitleView.hidden = NO;
    self.cloudSettingButton.hidden = YES;
    self.cloudSearchButton.hidden = NO;
    self.cloudSearchCancelButton.hidden = YES;
    self.cloudSelectAllButton.hidden = YES;
    self.cloudSelectCancelButton.hidden = YES;
    self.cloudBackButton.hidden = YES;
    self.cloudMenuButton.hidden = NO;
    self.cloudFileAddButton.hidden = NO;
    if ([self.file isCloudRoot]) {
        self.cloudSettingButton.hidden = NO;
    } else {
        self.cloudBackButton.hidden = NO;
    }
    self.cloudSearchTableView.hidden = YES;
    self.cloudFileTableView.hidden = NO;
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabShow" object:nil];
}

- (UIView*)cloudSearchNavigationBarView {
    if (!_cloudSearchNavigationBarView) {//15+22+2
        _cloudSearchNavigationBarView = [[UIView alloc] initWithFrame:CGRectMake(10, 4, CGRectGetWidth(self.navigationController.navigationBar.frame)-15-22-2-35, 36)];
        _cloudSearchNavigationBarView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1];
        _cloudSearchNavigationBarView.layer.cornerRadius = 4;
        _cloudSearchNavigationBarView.layer.masksToBounds = YES;
        _cloudSearchNavigationBarView.layer.borderWidth = 0.5;
        _cloudSearchNavigationBarView.layer.borderColor = [UIColor clearColor].CGColor;
        self.cloudSearchNavifationBarImageView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 7, 22, 22)];
        self.cloudSearchNavifationBarImageView.image = [UIImage imageNamed:@"ic_contact_corporate_search_nor"];
        [_cloudSearchNavigationBarView addSubview:self.cloudSearchNavifationBarImageView];
        
        self.cloudSearchNavigationBarTextField = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(self.cloudSearchNavifationBarImageView.frame)+4, 7, CGRectGetWidth(_cloudSearchNavigationBarView.frame)-CGRectGetMaxX(self.cloudSearchNavifationBarImageView.frame)-4-35,22)];
        self.cloudSearchNavigationBarTextField.backgroundColor = [UIColor clearColor];
        self.cloudSearchNavigationBarTextField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:getLocalizedString(@"Search", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"bedefe" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}];
        self.cloudSearchNavigationBarTextField.font = [UIFont systemFontOfSize:15.0f];
        self.cloudSearchNavigationBarTextField.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.cloudSearchNavigationBarTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.cloudSearchNavigationBarTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.cloudSearchNavigationBarTextField.delegate = self;
        self.cloudSearchNavigationBarTextField.returnKeyType = UIReturnKeySearch;
        [_cloudSearchNavigationBarView addSubview:self.cloudSearchNavigationBarTextField];
        
        self.cloudSearchNavifationBarActivityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:self.cloudSearchNavifationBarImageView.frame];
        self.cloudSearchNavifationBarActivityIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhite;
        self.cloudSearchNavifationBarActivityIndicator.backgroundColor = [UIColor clearColor];
        [_cloudSearchNavigationBarView addSubview:self.cloudSearchNavifationBarActivityIndicator];
    }
    return _cloudSearchNavigationBarView;
}

- (UIView*)cloudSearchBackgroundView {
    if (!_cloudSearchBackgroundView) {
        _cloudSearchBackgroundView = [[UIView alloc] initWithFrame:self.view.frame];
        _cloudSearchBackgroundView.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.5];
    }
    return _cloudSearchBackgroundView;
}

- (void)showSearchBackgroundView {
    [self.view addSubview:self.cloudSearchBackgroundView];
}

- (void)hideSearchBackgroundView {
    [self.cloudSearchBackgroundView removeFromSuperview];
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event{
    [self.cloudSearchNavigationBarTextField resignFirstResponder];
}
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [self showSearchBackgroundView];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [self hideSearchBackgroundView];
    self.cloudFileTableView.hidden = YES;
    self.cloudSearchTableView.hidden = NO;
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [self hideSearchBackgroundView];
    NSString *keyWord = [textField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    textField.text = keyWord;
    if (keyWord.length < 1) {
        return;
    }
    self.cloudFileAddButton.hidden = YES;
    self.cloudSearchNavifationBarImageView.hidden = YES;
    
    self.cloudSearchFetchController = nil;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileName contains[cd] %@ AND fileOwner = %@ AND fileId != %@ AND fileId != %@",textField.text,self.file.fileOwner,@"0",@"-0"];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"fileModifiedDate" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    [request setFetchBatchSize:20];
    self.cloudSearchFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
    self.cloudSearchFetchController.delegate = self;
    [self.cloudSearchFetchController performFetch:nil];
    
    self.cloudFileCrumbView.hidden = YES;
    [self.cloudSearchCrumbView removeFromSuperview];
    NSArray *searchCrumbFileIds = [NSArray arrayWithObject:@"search"];
    NSArray *searchCrumbFileOwners = [NSArray arrayWithObject:[NSString stringWithFormat:@"%lu",(unsigned long)self.cloudSearchFetchController.fetchedObjects.count]];
    NSArray *searchCrumbFileViewControllers = [NSArray arrayWithObject:self];
    self.cloudSearchCrumbView = [[CloudCrumbView alloc] initWithFiles:searchCrumbFileIds fileOwners:searchCrumbFileOwners];
    self.cloudSearchCrumbView.navigationController = self.navigationController;
    self.cloudSearchCrumbView.viewControllers = searchCrumbFileViewControllers;
    self.cloudSearchCrumbView.backgroundColor = [UIColor whiteColor];
    self.cloudSearchCrumbView.frame = self.cloudFileCrumbView.frame;
    [self.view addSubview:self.cloudSearchCrumbView];
    
    self.cloudSearchTableView.delegate = self;
    self.cloudSearchTableView.dataSource = self;
    [self.cloudSearchTableView reloadData];
    
    [self.cloudSearchNavifationBarActivityIndicator startAnimating];
    [File fileSearch:textField.text resourceOwner:self.file.fileOwner succeed:^(id retobj) {
        [self.cloudSearchNavifationBarActivityIndicator stopAnimating];
        self.cloudSearchNavifationBarImageView.hidden = NO;
        
        [self.cloudSearchCrumbView removeFromSuperview];
        NSArray *searchCrumbFileIds = [NSArray arrayWithObject:@"search"];
        NSArray *searchCrumbFileOwners = [NSArray arrayWithObject:[NSString stringWithFormat:@"%lu",(unsigned long)self.cloudSearchFetchController.fetchedObjects.count]];
        NSArray *searchCrumbFileViewControllers = [NSArray arrayWithObject:self];
        self.cloudSearchCrumbView = [[CloudCrumbView alloc] initWithFiles:searchCrumbFileIds fileOwners:searchCrumbFileOwners];
        self.cloudSearchCrumbView.backgroundColor = [UIColor whiteColor];
        self.cloudSearchCrumbView.navigationController = self.navigationController;
        self.cloudSearchCrumbView.viewControllers = searchCrumbFileViewControllers;
        self.cloudSearchCrumbView.frame = self.cloudFileCrumbView.frame;
        [self.view addSubview:self.cloudSearchCrumbView];
        if (self.cloudSearchTableView) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
        }
        
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        [self.cloudSearchNavifationBarActivityIndicator stopAnimating];
        self.cloudSearchNavifationBarImageView.hidden = NO;
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"ContactSearchFailed", nil)];
    }];
}

#pragma mark NSFetchedResultsControllerDelegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    UITableView *tableView;
    if (controller == self.cloudSearchFetchController) {
        tableView = self.cloudSearchTableView;
    } else {
        tableView = self.cloudFileTableView;
    }
    [tableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    UITableView *tableView;
    if (controller == self.cloudSearchFetchController) {
        tableView = self.cloudSearchTableView;
    } else {
        tableView = self.cloudFileTableView;
    }
    [tableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    CloudFileTableViewCell *cell = (CloudFileTableViewCell *)[self.cloudFileTableView cellForRowAtIndexPath:indexPath];
    UITableView *tableView;
    if (controller == self.cloudSearchFetchController) {
        tableView = self.cloudSearchTableView;
    } else {
        tableView = self.cloudFileTableView;
    }
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeUpdate:
            [cell refresh];
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
    UITableView *tableView;
    if (controller == self.cloudSearchFetchController) {
        tableView = self.cloudSearchTableView;
    } else {
        tableView = self.cloudFileTableView;
    }
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
