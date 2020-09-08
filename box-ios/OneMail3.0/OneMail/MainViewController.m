//
//  MainViewController.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MainViewController.h"
#import "MainTabBarButton.h"
#import "CloudFileViewController.h"
#import "MailLoginViewController.h"
#import "CloudViewController.h"
#import "AppDelegate.h"

@interface MainViewController ()

@property (nonatomic, strong) UIButton *selectedBtn;

@end

@implementation MainViewController

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
//    self.myTabBar.backgroundColor = [UIColor whiteColor];
    self.myTabBar.backgroundColor = [UIColor colorWithRed:255.0/255.0 green:255.0/255.0 blue:255.0/255.0 alpha:0.1];
    [self.view addSubview:self.myTabBar];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    appDelegate.mainTabBar = self.myTabBar;
    
    
    //邮件会话界面
//    MailLoginViewController *myMailVC = [[MailLoginViewController alloc]init];
//    MainTabBarButton *myMailButton = [[MainTabBarButton alloc]init];
//    [myMailButton setTitle:NSLocalizedString(@"MailTitle", nil) forState:UIControlStateNormal];
//    [myMailButton setImage:[UIImage imageNamed:@"ic_tab_mail_nor"] forState:UIControlStateNormal];
//    [myMailButton setImage:[UIImage imageNamed:@"ic_tab_mail_sel"] forState:UIControlStateSelected];
//    myMailButton.frame = CGRectMake(0, 0, CGRectGetWidth(self.myTabBar.frame)/2, CGRectGetHeight(self.myTabBar.frame));
//    myMailButton.tag = 0;
//    [myMailButton addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
//    [self.myTabBar addSubview:myMailButton];
    
    //我的文件界面
    CloudViewController *myFileVC = [[CloudViewController alloc]init];
    MainTabBarButton *myFileButton = [[MainTabBarButton alloc]init];
    [myFileButton setTitle:getLocalizedString(@"CloudTitle", nil) forState:UIControlStateNormal];
    [myFileButton setImage:[UIImage imageNamed:@"ic_tab_my_space_nor"] forState:UIControlStateNormal];
    [myFileButton setImage:[UIImage imageNamed:@"ic_tab_my_space_sel"] forState:UIControlStateSelected];
    myFileButton.frame = CGRectMake(CGRectGetWidth(self.myTabBar.frame)/2, 0, CGRectGetWidth(self.myTabBar.frame)/2, CGRectGetHeight(self.myTabBar.frame));
    myFileButton.tag = 1;
    [myFileButton addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.myTabBar addSubview:myFileButton];
    
//    myMailButton.selected = YES;
    self.selectedBtn = myFileButton;
//    NSArray *views = @[myMailVC,myFileVC];
    NSArray *views = @[myFileVC];
    NSMutableArray *viewControllers = [NSMutableArray arrayWithCapacity:2];
    for (UIViewController *viewController in views) {
        if ([viewController isKindOfClass:[CloudViewController class]]) {
            [viewControllers addObject:viewController];
        }
        if ([viewController isKindOfClass:[MailLoginViewController class]]) {
            UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:viewController];
            [nav.navigationBar setTintColor:[UIColor whiteColor]];
            [nav.navigationBar setBarTintColor:[UIColor colorWithRed:50/255.0f green:130/255.0f blue:196/255.0f alpha:1.0f]];
            [nav.navigationBar setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], NSForegroundColorAttributeName, nil ]];
            [viewControllers addObject:nav];
        }
    }
    
    UIView *topLineView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.myTabBar.frame.size.width, 0.5)];
    topLineView.backgroundColor = [UIColor colorWithRed:178/255.0f green:178/255.0f blue:178/255.0f alpha:1.0f];
    [self.myTabBar addSubview:topLineView];
    
    self.viewControllers = viewControllers;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mainTabHid) name:@"com.huawei.onemail.mainTabHide" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mainTabShow) name:@"com.huawei.onemail.mainTabShow" object:nil];
}

- (void)mainTabShow {
    self.myTabBar.hidden = NO;
}

- (void)mainTabHid {
    self.myTabBar.hidden = YES;
}

-(void)dealloc {
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"com.huawei.onemail.mainTabHide" object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"com.huawei.onemail.mainTabShow" object:nil];
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

@end
