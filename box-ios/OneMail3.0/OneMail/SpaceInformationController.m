//
//  SpaceInformationController.m
//  OneMail
//
//  Created by admin on 17/1/10.
//  Copyright © 2017年 cse. All rights reserved.
//

#import "SpaceInformationController.h"
#import "CommonFunction.h"
@interface SpaceInformationController ()
@property (nonatomic,strong) UIButton *backButton;
@end

@implementation SpaceInformationController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"查看详情";
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    self.backButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.backButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.backButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.backButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.backButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
    [self.navigationController.navigationBar addSubview:self.backButton];
    self.view.backgroundColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
    UIView *nameCell = [self setViewCell:@"名称" value:self.teamSpace.teamName];
    UIView *maxMemberCell = [self setViewCell:@"成员数配额" value:@"1000"];
    UIView *memberNumCell = [self setViewCell:@"当前成员数" value:self.teamSpace.teamMemberNum];
    UIView *spaceQuotaCell = [self setViewCell:@"空间配额" value:@"无限制"];
    NSString *spaceUsed = [CommonFunction pretySize:self.teamSpace.teamUsedSpace.longLongValue];
    UIView *usedSpaceCell = [self setViewCell:@"已用空间" value:spaceUsed];
    CGRect statusFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navFrame = self.navigationController.navigationBar.frame;
    nameCell.frame = CGRectMake(0, statusFrame.size.height + navFrame.size.height + 10, self.view.frame.size.width, 60);
    maxMemberCell.frame = CGRectMake(0, CGRectGetMaxY(nameCell.frame) + 10, self.view.frame.size.width, 60);
    memberNumCell.frame = CGRectMake(0, CGRectGetMaxY(maxMemberCell.frame), self.view.frame.size.width, 60);
    spaceQuotaCell.frame = CGRectMake(0, CGRectGetMaxY(memberNumCell.frame), self.view.frame.size.width, 60);
    usedSpaceCell.frame = CGRectMake(0, CGRectGetMaxY(spaceQuotaCell.frame), self.view.frame.size.width, 60);
    [self.view addSubview:nameCell];
    [self.view addSubview:maxMemberCell];
    [self.view addSubview:memberNumCell];
    [self.view addSubview:spaceQuotaCell];
    [self.view addSubview:usedSpaceCell];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
    // Do any additional setup after loading the view.
}
- (UIView *)setViewCell:(NSString *)type value:(NSString *)value{
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 60)];
    view.backgroundColor = [UIColor whiteColor];
    UIView *upLine = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 0.5)];
    upLine.backgroundColor = [UIColor colorWithRed:178/255.0f green:178/255.0f blue:178/255.0f alpha:1.0f];
    [view addSubview:upLine];
    UIView *tailLine = [[UIView alloc] initWithFrame:CGRectMake(0, view.frame.size.height - 1, self.view.frame.size.width, 0.5)];
    tailLine.backgroundColor = [UIColor colorWithRed:178/255.0f green:178/255.0f blue:178/255.0f alpha:1.0f];
    [view addSubview:tailLine];
    UILabel *typeLabel = [[UILabel alloc] initWithFrame:CGRectMake(40, 5, 200, 50)];
    [typeLabel setText:type];
    [typeLabel setTextAlignment:NSTextAlignmentLeft];
    [typeLabel setTextColor:[UIColor blackColor]];
    [typeLabel setFont:[UIFont systemFontOfSize:21]];
    [view addSubview:typeLabel];
    UILabel *valueLabel = [[UILabel alloc] initWithFrame:CGRectMake(self.view.frame.size.width - 240, 5, 200, 50)];
    [valueLabel setText:value];
    [valueLabel setTextAlignment:NSTextAlignmentRight];
    [valueLabel setTextColor:[CommonFunction colorWithString:@"666666" alpha:1.0f]];
    [valueLabel setFont:[UIFont systemFontOfSize:21]];
    [view addSubview:valueLabel];
    return view;
}
- (void)viewWillDisappear:(BOOL)animated{
    [self.backButton removeFromSuperview];
}
- (void)popViewController{
    [self.navigationController popViewControllerAnimated:YES];
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
