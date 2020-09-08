//
//  CloudChangeOwnerViewController.m
//  OneMail
//
//  Created by admin on 17/1/12.
//  Copyright © 2017年 cse. All rights reserved.
//

#import "CloudChangeOwnerViewController.h"
#import "SpaceUsersManageViewController.h"
#import "CommonFunction.h"
#import "TeamSpace+Remote.h"
#import "UIView+Toast.h"
#import "MJRefresh.h"
@interface CloudChangeOwnerViewController () <UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong) TeamSpace *space;
@property (nonatomic,strong) NSArray *memberShips;
@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) MemberShip *selectedShip;
@end

@implementation CloudChangeOwnerViewController
- (id)initWithSpace:(TeamSpace *)space{
    if (self = [super init]) {
        _space = space;
        self.tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        self.tableView.delegate = self;
        self.tableView.dataSource = self;
        [self.tableView registerClass:[SpaceUsersManageCell class] forCellReuseIdentifier:@"changeOwner"];
        self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"更换拥有者";
    self.tableView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
    [self.view addSubview:self.tableView];
    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Confirm", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(confirm)];
    self.navigationItem.rightBarButtonItem = rightItem;
    [self setupRefresh];
    [self reloadData];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
    // Do any additional setup after loading the view.
}
- (void)setupRefresh
{
    MJRefreshNormalHeader *header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(reloadData)];
    header.automaticallyChangeAlpha = YES;
    [header beginRefreshing];
    header.stateLabel.hidden = YES;
    self.tableView.mj_header = header;
}
- (void)confirm{
    if (!self.selectedShip) {
        return;
    }
    NSDictionary *dic = [NSDictionary dictionaryWithObjectsAndKeys:self.selectedShip.ShipId,@"memberId",@"admin",@"teamRole",self.selectedShip.role,@"role",nil];
    [self.space spaceMemberUpdate:dic succeed:^(id retobj) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
        });
    }];
}
- (void)reloadData{
    [self.space spaceMemberlist:nil succeed:^(id retobj) {
        NSDictionary *shipsInfo = retobj;
        self.memberShips = [MemberShip getShipsFromInfo:shipsInfo];
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.tableView.mj_header isRefreshing]) {
                [self.tableView.mj_header endRefreshing];
            }
            [self.tableView reloadData];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.tableView.mj_header isRefreshing]) {
                [self.tableView.mj_header endRefreshing];
            }
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"OperationFailed", nil)];
        });
    }];
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.memberShips.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    SpaceUsersManageCell *cell = [tableView dequeueReusableCellWithIdentifier:@"changeOwner"];
    if (!cell) {
        cell = [[SpaceUsersManageCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"changeOwner"];
    }
    cell.ship = self.memberShips[indexPath.row];
    cell.isChangeOwer = true;
    cell.selectionStyle = UITableViewCellSelectionStyleBlue;
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    self.selectedShip = self.memberShips[indexPath.row];
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
