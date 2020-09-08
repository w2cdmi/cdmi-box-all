//
//  CloudLoadingViewController.m
//  OneMail
//
//  Created by hua on 16/11/25.
//  Copyright © 2016年 cse. All rights reserved.
//

#import "CloudLoadingViewController.h"
#import "AppDelegate.h"
#import "CloudLoginViewController.h"

@interface CloudLoadingViewController ()<UIScrollViewDelegate>

@property (strong, nonatomic) UIScrollView *leadScrollView;
@property (strong, nonatomic) UIImageView *page1;
@property (strong, nonatomic) UIImageView *page2;
@property (strong, nonatomic) UIImageView *page3;
@property (strong, nonatomic) UIButton *button;
@property (strong, nonatomic) UIPageControl *leadPageControl;

@end

int pageCount = 3;

@implementation CloudLoadingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
    }
    return self;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    [[UIApplication sharedApplication] setStatusBarHidden:NO];
    [[UIApplication sharedApplication] setStatusBarStyle: UIStatusBarStyleLightContent];
    
    self.view.frame = [UIScreen mainScreen].bounds;
    CGFloat viewWidth = self.view.frame.size.width;
    CGFloat viewHeight = self.view.frame.size.height;
    
    _leadPageControl.numberOfPages = pageCount;
    _leadPageControl.currentPage = 0;
    
    _page1 = [[UIImageView alloc] initWithFrame:CGRectZero];
    _page2 = [[UIImageView alloc] initWithFrame:CGRectZero];
    _page3 = [[UIImageView alloc] initWithFrame:CGRectZero];
    _page1.image = [UIImage imageNamed:NSLocalizedString(@"loading_1", nil)];
    _page2.image = [UIImage imageNamed:NSLocalizedString(@"loading_3",nil)];
    _page3.image = [UIImage imageNamed:NSLocalizedString(@"loading_2",nil)];
    
    _button = [[UIButton alloc] initWithFrame:CGRectZero];
    _button.bounds = CGRectMake(0, 0, viewWidth*3/4, 50);
    _button.backgroundColor = [UIColor lightGrayColor];
    _button.frame = CGRectMake((viewWidth-CGRectGetWidth(_button.bounds))/2+viewWidth*2, viewHeight-160-CGRectGetHeight(_button.bounds), CGRectGetWidth(_button.bounds), CGRectGetHeight(_button.bounds));
    [_button setTitle:getLocalizedString(@"LoginInitiateExperience", nil) forState:UIControlStateNormal];
    [_button setTitle:getLocalizedString(@"LoginInitiateExperience", nil) forState:UIControlStateHighlighted];
    [_button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [_button setTitleColor:[UIColor blackColor] forState:UIControlStateHighlighted];
    _button.backgroundColor = [UIColor whiteColor];
    [_button addTarget:self action:@selector(onEnter:) forControlEvents:UIControlEventTouchUpInside];
    
    _leadScrollView = [[UIScrollView alloc] initWithFrame:CGRectZero];
    _leadScrollView.frame = [UIScreen mainScreen].bounds;
    _leadScrollView.contentSize = CGSizeMake(viewWidth*3, viewHeight);
    _leadScrollView.pagingEnabled = YES;
    
    _page1.frame = CGRectMake(0, 0, viewWidth, viewHeight);
    _page2.frame = CGRectMake(viewWidth, 0, viewWidth, viewHeight);
    _page3.frame = CGRectMake(viewWidth*2, 0, viewWidth, viewHeight);
    
    _leadPageControl = [[UIPageControl alloc] initWithFrame:CGRectMake(0, self.view.frame.size.height - 70, self.view.frame.size.width, 37)];
    _leadPageControl.tintColor = [UIColor whiteColor];
    [_leadScrollView addSubview:_page1];
    [_leadScrollView addSubview:_page2];
    [_leadScrollView addSubview:_page3];
    [_leadScrollView addSubview:_button];
    
    [self.view addSubview:_leadScrollView];
    [self.view addSubview:_leadPageControl];
    _leadScrollView.delegate = self;
}

-(void) viewDidAppear:(BOOL)animated {
    _leadScrollView.contentSize = CGSizeMake(self.view.frame.size.width * pageCount,0);
    [super viewDidAppear:animated];
}

- (void)onEnter:(id)sender {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CloudLoginViewController *login = [[CloudLoginViewController alloc] initWithNibName:nil bundle:nil];
    [appDelegate.navigationController setViewControllers:@[login] animated:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    CGFloat pageWidth = scrollView.frame.size.width;
    int page = floor((scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    self.leadPageControl.currentPage = page;
}



@end
