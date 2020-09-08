//
//  CloudViewController.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudViewController.h"
#import "CloudTabBarButton.h"
#import "File.h"
#import "CloudFileViewController.h"
#import "CloudShareViewController.h"
#import "CloudSpaceViewController.h"
#import "AppDelegate.h"
#import "CloudTabBarButton.h"
#import "CloudTitleView.h"
#import "MainTabBarButton.h"
@interface CloudViewController ()

@property (nonatomic, strong) UIButton *selectedBtn;
@property (nonatomic, strong) UIView *myTabBar;
@property (nonatomic, strong) MainTabBarButton *myFileButton;
@property (nonatomic, strong) MainTabBarButton *mySpaceButton;
@property (nonatomic, strong) MainTabBarButton *myShareButton;
@end

@implementation CloudViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"com.huawei.onemail.LocalizedChange" object:nil];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.view.backgroundColor = [UIColor whiteColor];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
     CGRect tabBar = self.tabBar.frame;
    [self.tabBar removeFromSuperview];
    self.myTabBar = [[UIView alloc]init];
    self.myTabBar.frame = tabBar;
    self.myTabBar.backgroundColor = [UIColor colorWithRed:255.0/255.0 green:255.0/255.0 blue:255.0/255.0 alpha:1];
    [self.view addSubview:self.myTabBar];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    appDelegate.mainTabBar = self.myTabBar;
    
    UITabBarItem *item = [UITabBarItem appearance];
    NSMutableDictionary *attrs = [NSMutableDictionary dictionary];
    attrs[NSFontAttributeName] = [UIFont systemFontOfSize:19];
    attrs[NSForegroundColorAttributeName] = [UIColor grayColor];

    NSMutableDictionary *selectedAttrs = [NSMutableDictionary dictionary];
    selectedAttrs[NSFontAttributeName] = [UIFont systemFontOfSize:12];
    selectedAttrs[NSForegroundColorAttributeName] = [UIColor darkGrayColor];
 
    [item setTitleTextAttributes:attrs forState:UIControlStateNormal];
    [item setTitleTextAttributes:selectedAttrs forState:UIControlStateSelected];
    
    _myFileButton = [[MainTabBarButton alloc]init];
    [_myFileButton setTitle:getLocalizedString(@"CloudFileTitle", nil) forState:UIControlStateNormal];
//    _myFileButton.titleLabel.font = [UIFont systemFontOfSize:22];
    [_myFileButton setImage:[UIImage imageNamed:@"ic_tab_my_space_nor"] forState:UIControlStateNormal];
    [_myFileButton setImage:[UIImage imageNamed:@"ic_tab_my_space_sel"] forState:UIControlStateSelected];
    _myFileButton.frame = CGRectMake(0, 0, CGRectGetWidth(self.myTabBar.frame)/3, CGRectGetHeight(self.myTabBar.frame));
    _myFileButton.tag = 0;
    [_myFileButton addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.myTabBar addSubview:_myFileButton];
    
    _mySpaceButton = [[MainTabBarButton alloc]init];
    [_mySpaceButton setTitle:getLocalizedString(@"CloudTeamSpaceTitle", nil) forState:UIControlStateNormal];
//    _myShareButton.titleLabel.font = [UIFont systemFontOfSize:12];
    [_mySpaceButton setImage:[UIImage imageNamed:@"ic_tab_team_space_nor"] forState:UIControlStateNormal];
    [_mySpaceButton setImage:[UIImage imageNamed:@"ic_tab_team_space_sel"] forState:UIControlStateSelected];
    _mySpaceButton.frame = CGRectMake(CGRectGetWidth(self.myTabBar.frame)/3 * 2, 0, CGRectGetWidth(self.myTabBar.frame)/3, CGRectGetHeight(self.myTabBar.frame));
    _mySpaceButton.tag = 1;
    [_mySpaceButton addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.myTabBar addSubview:_mySpaceButton];
    
    _myShareButton = [[MainTabBarButton alloc]init];
    [_myShareButton setTitle:getLocalizedString(@"CloudShareTitle", nil) forState:UIControlStateNormal];
//    _myShareButton.titleLabel.font = [UIFont systemFontOfSize:12];
    [_myShareButton setImage:[UIImage imageNamed:@"ic_tab_share_nor"] forState:UIControlStateNormal];
    [_myShareButton setImage:[UIImage imageNamed:@"ic_tab_share_sel"] forState:UIControlStateSelected];
    _myShareButton.frame = CGRectMake(CGRectGetWidth(self.myTabBar.frame)/3, 0, CGRectGetWidth(self.myTabBar.frame)/3, CGRectGetHeight(self.myTabBar.frame));
    _myShareButton.tag = 2;
    [_myShareButton addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.myTabBar addSubview:_myShareButton];
    
    UIView *topLineView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.myTabBar.frame.size.width, 0.5)];
    topLineView.backgroundColor = [UIColor colorWithRed:178/255.0f green:178/255.0f blue:178/255.0f alpha:1.0f];
    [self.myTabBar addSubview:topLineView];
    self.selectedBtn = _myFileButton;
    self.selectedBtn.selected = YES;

    CloudFileViewController *myFile = [[CloudFileViewController alloc] initWithFile:[File rootMyFolder]];
    myFile.cloudTitleView = [[CloudTitleView alloc] initWithStyle:CloudTitleMySpaceStyle frame:CGRectZero];
    CloudShareViewController *shareFile = [[CloudShareViewController alloc] initWithFile:[File rootReceivedShareFolder]];
    shareFile.cloudTitleView = [[CloudTitleView alloc] initWithStyle:CloudTitleShareWithMeStyle frame:CGRectZero];
    CloudSpaceViewController *spaceFile = [[CloudSpaceViewController alloc] init];
    spaceFile.cloudTitleView = [[CloudTitleView alloc] initWithStyle:CloudTitleTeamSapceStyle frame:CGRectZero];
    
    NSArray *views = @[myFile,spaceFile,shareFile];
    NSMutableArray *viewControllers = [NSMutableArray arrayWithCapacity:3];
    for (UIViewController *viewController in views) {
        UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:viewController];
        [nav.navigationBar setTintColor:[UIColor whiteColor]];
        [nav.navigationBar setBarTintColor:[UIColor colorWithRed:53/255.0f green:146/255.0f blue:226/255.0f alpha:1.0f]];
        [nav.navigationBar setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], NSForegroundColorAttributeName, nil ]];
        [viewControllers addObject:nav];
    }
    
    self.viewControllers = viewControllers;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showMySpace) name:@"oneMail.Show.MySpace" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showTeamSpace) name:@"oneMail.Show.TeamSpace" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showShareSpace) name:@"oneMail.Show.ShareSpace" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshTitle) name:@"com.huawei.onemail.LocalizedChange" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mainTabHid) name:@"com.huawei.onemail.mainTabHide" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mainTabShow) name:@"com.huawei.onemail.mainTabShow" object:nil];
}

- (void)refreshTitle{
    if (_myFileButton && _myShareButton && _mySpaceButton) {
        [_myFileButton setTitle:getLocalizedString(@"CloudFileTitle", nil) forState:UIControlStateNormal];
        [_mySpaceButton setTitle:getLocalizedString(@"CloudTeamSpaceTitle", nil) forState:UIControlStateNormal];
        [_myShareButton setTitle:getLocalizedString(@"CloudShareTitle", nil) forState:UIControlStateNormal];
    }
}


- (void)mainTabShow {
    self.myTabBar.hidden = NO;
}

- (void)mainTabHid {
    self.myTabBar.hidden = YES;
}

- (void)clickBtn:(UIButton*)button {
    if (self.selectedBtn != button) {
        self.selectedBtn.selected = NO;
        self.selectedBtn = button;
        self.selectedBtn.selected = YES;
    }
    if (self.selectedIndex != button.tag) {
        self.selectedIndex = button.tag;
    }
}

- (void)showMySpace {
    self.selectedIndex = 0;
    self.selectedBtn.selected = NO;
    self.selectedBtn = self.myFileButton;
    self.selectedBtn.selected = YES;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Hide.TabBarTable" object:nil];
}

- (void)showTeamSpace {
    self.selectedIndex = 1;
    self.selectedBtn.selected = NO;
    self.selectedBtn = self.mySpaceButton;
    self.selectedBtn.selected = YES;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Hide.TabBarTable" object:nil];
}

- (void)showShareSpace {
    self.selectedIndex = 2;
    self.selectedBtn.selected = NO;
    self.selectedBtn = self.myShareButton;
    self.selectedBtn.selected = YES;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Hide.TabBarTable" object:nil];
}

@end
