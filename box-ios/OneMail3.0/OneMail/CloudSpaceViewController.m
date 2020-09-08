//
//  CloudSpaceViewController.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define FileTableViewHeaderHeight 22

#import "CloudSpaceViewController.h"
#import "CloudSpaceTableViewCell.h"
#import "CloudSpaceDetailViewController.h"
#import "AppDelegate.h"
#import "MenuViewController.h"
#import "TeamSpace.h"
#import "TeamSpace+Remote.h"
#import "CloudTitleView.h"
#import "CloudFolderCreateViewController.h"
#import "UIAlertView+Blocks.h"
#import "SpaceInformationController.h"
#import "SpaceUsersManageViewController.h"
#import "CloudChangeOwnerViewController.h"
#import "MJRefresh.h"

@interface CloudSpaceViewController ()<UITableViewDelegate,UITableViewDataSource,NSFetchedResultsControllerDelegate,UIActionSheetDelegate>

@property (nonatomic, strong) NSFetchedResultsController *cloudSpaceFetchController;
@property (nonatomic, strong) UIButton                   *cloudSettingButton;
@property (nonatomic, strong) UIButton                   *cloudAddSpaceButton;
@property (nonatomic, strong) TeamSpace                  *selectedTeam;
@property (nonatomic, strong) UIView                     *cloudFileNullView;
@property (nonatomic, assign) BOOL                        cloudSelectState;

@end

@implementation CloudSpaceViewController

- (id)init {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        
        NSPredicate *preficate = [NSPredicate predicateWithFormat:@"teamUserId = %@",appDelegate.localManager.userCloudId];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"TeamSpace" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        
        NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"teamDate" ascending:NO];
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        [fetchRequest setPredicate:preficate];
        [fetchRequest setEntity:entity];
        [fetchRequest setSortDescriptors:@[dateDescriptor]];
        [fetchRequest setFetchBatchSize:20];
        self.cloudSpaceFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
        [self.cloudSpaceFetchController setDelegate:self];
        [self performFetch];
        [self reloadDataSource];
    }
    return self;
}

- (void)performFetch
{
    NSError *error = NULL;
    if (![self.cloudSpaceFetchController performFetch:&error]) {
        [SNLog Log:LFatal :@"Unresolved error %@, %@",error, [error userInfo]];
        abort();
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:self action:nil];
    self.view.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    
    [self.cloudTitleView setViewFrame:CGRectMake(44+44+4, 7, CGRectGetWidth(self.view.frame)-(44+44+4)*2, 24)];
    [self.navigationController.navigationBar addSubview:self.cloudTitleView];
    
    self.cloudSettingButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.cloudSettingButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_nor"] forState:UIControlStateNormal];
    [self.cloudSettingButton setImage:[UIImage imageNamed:@"ic_nav_menu_press"] forState:UIControlStateHighlighted];
    [self.cloudSettingButton addTarget:self action:@selector(cloudSettingButtonClick) forControlEvents:UIControlEventTouchUpInside];
    [self.navigationController.navigationBar addSubview:self.cloudSettingButton];
    
    self.cloudAddSpaceButton = [[UIButton alloc] initWithFrame:CGRectMake(self.view.frame.size.width - 48, 0, 44, 44)];
    self.cloudAddSpaceButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.cloudAddSpaceButton setImage:[UIImage imageNamed:@"ic_add_teamspace_nor"] forState:UIControlStateNormal];
    [self.cloudAddSpaceButton setImage:[UIImage imageNamed:@"ic_add_teamspace_nor"] forState:UIControlStateHighlighted];
    [self.cloudAddSpaceButton addTarget:self action:@selector(addSpace) forControlEvents:UIControlEventTouchUpInside];
    [self.navigationController.navigationBar addSubview:self.cloudAddSpaceButton];
    
    [self.tableView registerClass:[CloudSpaceTableViewCell class] forCellReuseIdentifier:@"CloudSpaceTableViewCell"];
    [self.tableView setDataSource:self];
    [self.tableView setDelegate:self];
    [self.tableView setSeparatorColor:[CommonFunction colorWithString:@"d9d9d9" alpha:1.0f]];
    [self.tableView setScrollEnabled:YES];
    [self.tableView setFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame))];
    [self.tableView setTableFooterView:[[UIView alloc] init]];
    
    UISwipeGestureRecognizer *rightHandleRevognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handSwipe:)];
    rightHandleRevognizer.direction = UISwipeGestureRecognizerDirectionRight;
//    [self.view addGestureRecognizer:rightHandleRevognizer];
    
    
    //添加控件
    [self setupRefresh];
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
        cloudFileNullTitle.text = getLocalizedString(@"CloudTeamSpace", nil);
        cloudFileNullTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1];
        cloudFileNullTitle.font = [UIFont systemFontOfSize:15];
        cloudFileNullTitle.textAlignment = NSTextAlignmentCenter;
        [_cloudFileNullView addSubview:cloudFileNullTitle];
        
//        UILabel *cloudFileNullPrompt = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:14] textColor:[CommonFunction colorWithString:@"666666" alpha:1] textAlignment:NSTextAlignmentCenter];
//        cloudFileNullPrompt.text = getLocalizedString(@"CloudNoFileNotification", nil);
//        cloudFileNullPrompt.numberOfLines = 0;
//        CGSize size = [CommonFunction labelSizeWithLabel:cloudFileNullPrompt limitSize:CGSizeMake(self.view.frame.size.width - 30, 1000)];
//        cloudFileNullPrompt.frame = CGRectMake(20, CGRectGetMaxY(cloudFileNullTitle.frame) + 10, size.width,size.height);
//        
//        [_cloudFileNullView addSubview:cloudFileNullPrompt];
        
        [self.view addSubview:_cloudFileNullView];
    }
    return _cloudFileNullView;
}

- (void)setupRefresh
{
    //    self.cloudFileTableView.tableHeaderView = self.cloudFileRefreshController;
    
    MJRefreshNormalHeader *header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(loadNewData)];
    header.automaticallyChangeAlpha = YES;
    [header beginRefreshing];
    header.stateLabel.hidden = YES;
    self.tableView.mj_header = header;
}

- (void)loadNewData
{
    __weak UITableView *tableView = self.tableView;
    
    if (self.cloudSelectState) {
        [tableView.mj_header endRefreshing];
        return;
    }
    [self reloadDataSource];
}


- (void)handSwipe:(UISwipeGestureRecognizer*)sender {
    if (sender.direction == UISwipeGestureRecognizerDirectionRight) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"oneMail.Show.ShareSpace" object:nil];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.cloudTitleView.hidden = NO;
    self.cloudSettingButton.hidden = NO;
    self.cloudAddSpaceButton.hidden = NO;
    [[UIApplication sharedApplication]setStatusBarStyle:UIStatusBarStyleLightContent];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabShow" object:nil];
    [self reloadDataSource];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    self.cloudTitleView.hidden = YES;
    self.cloudSettingButton.hidden = YES;
    self.cloudAddSpaceButton.hidden = YES;
}

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

- (void)addSpace{
    CloudFolderCreateViewController *createController = [[CloudFolderCreateViewController alloc] init];
    [self.navigationController pushViewController:createController animated:NO];
}

- (void)longPressFunction:(id)sender{
    UIGestureRecognizer *gesture = sender;
    if (gesture.state == UIGestureRecognizerStateEnded) {
        CloudSpaceTableViewCell *cell = (CloudSpaceTableViewCell *)[gesture view];
        TeamSpace *space = cell.teamSpace;
        self.selectedTeam = space;
        NSString *dissolveTitle;
        if ([self.selectedTeam.teamOwner isEqualToString:self.selectedTeam.teamUserId]) {
            dissolveTitle =  getLocalizedString(@"CloudTeamSpaceDissolve", nil);
        }
        else{
            dissolveTitle = getLocalizedString(@"CloudTeamSpaceExit", nil);
        }
        UIActionSheet *sheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"CloudTeamSpaceManage", nil),getLocalizedString(@"CloudTeamSpaceAppoint", nil),getLocalizedString(@"CloudTeamSpaceDetail", nil),dissolveTitle, nil];
        [sheet showInView:self.view];
    }
}

#pragma actionSheetDelegate
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    switch (buttonIndex) {
        case 0:
            [self memberManage];break;
        case 1:
            [self changeOwner];break;
        case 2:
            [self detailInformation];
            break;
        case 3:
            [self dissolveSpace];
            break;
        default:
            break;
    }
}

#pragma TeamSpaceAction
- (void)memberManage{
    SpaceUsersManageViewController *usersManageController = [[SpaceUsersManageViewController alloc] initWithSpace:self.selectedTeam];
    [self.navigationController pushViewController:usersManageController animated:NO];
}
- (void)changeOwner{
    if ([self permisson] == false) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"用户无权限", nil)];
        });
        return;
    }
    CloudChangeOwnerViewController *changOwnerController = [[CloudChangeOwnerViewController alloc] initWithSpace:self.selectedTeam];
    [self.navigationController pushViewController:changOwnerController animated:NO];
}
- (void)detailInformation{
    SpaceInformationController *detailViewController = [[SpaceInformationController alloc] init];
    detailViewController.teamSpace = self.selectedTeam;
    [self.navigationController pushViewController:detailViewController animated:YES];
}
- (void)dissolveSpace{
    if ([self permisson] == false) {
        //        dispatch_async(dispatch_get_main_queue(), ^{
        //            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"用户无权限", nil)];
        //        });
        NSDictionary *dic = [NSDictionary dictionaryWithObject:self.selectedTeam.teamRelationId forKey:@"memberId"];
        [self.selectedTeam spaceMemberDelete:dic succeed:^(id retobj) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"退出成功", nil)];
            });
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"退出失败", nil)];
            });
            
        }];
    }
    else{
        [UIAlertView showAlertViewWithTitle:NSLocalizedString(@"CloudTeamSpaceDeletePrompt", nil) message:nil cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
            [self.selectedTeam spaceDelete:^(id retobj) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"删除成功", nil)];
                });
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"删除失败", nil)];
                });
            }];
        } onCancel:^{
            
        }];
    }
}
#pragma permissionCheck
- (BOOL)permisson{
    NSString *userCloudId = [UserSetting defaultSetting].cloudUserCloudId.stringValue;
    return [self.selectedTeam.teamOwner isEqualToString:userCloudId];
}


#pragma mark tableView dataSource + delegate
- (void)reloadDataSource {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [TeamSpace spaceList:appDelegate.localManager.userCloudId succeed:^(id retobj) {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.tableView.scrollEnabled = YES;
            if ([self.tableView.mj_header isRefreshing]) {
                [self.tableView.mj_header endRefreshing];
            }
            
//            [self.refreshControl endRefreshing];
            
            if (self.cloudSpaceFetchController.fetchedObjects.count == 0) {
                self.cloudFileNullView.hidden = NO;
            } else {
                self.cloudFileNullView.hidden = YES;
            }
            
            [self.tableView reloadData];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.tableView.scrollEnabled = YES;
            [self.refreshControl endRefreshing];
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudTeamSpaceFailedPrompt", nil)];
            if ([self.tableView.mj_header isRefreshing]) {
                [self.tableView.mj_header endRefreshing];
            }
            if (self.cloudSpaceFetchController.fetchedObjects.count == 0) {
                self.cloudFileNullView.hidden = NO;
            } else {
                self.cloudFileNullView.hidden = YES;
            }
        });
    }];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.cloudSpaceFetchController.fetchedObjects.count == 0) {
        tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    } else {
        tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    }
    return self.cloudSpaceFetchController.fetchedObjects.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.0f;
}

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return nil;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudSpaceTableViewCell *cell = (CloudSpaceTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"CloudSpaceTableViewCell"];
    if (cell == nil) {
        cell = [[CloudSpaceTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudSpaceTableViewCell"];
    }
    TeamSpace *teamSpace = [self.cloudSpaceFetchController.fetchedObjects objectAtIndex:indexPath.row];
    cell.teamSpace = teamSpace;
    UILongPressGestureRecognizer *recognizer = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressFunction:)];
    [cell addGestureRecognizer:recognizer];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    TeamSpace *teamSpace = [self.cloudSpaceFetchController objectAtIndexPath:indexPath];
    CloudSpaceDetailViewController *fileViewController = [[CloudSpaceDetailViewController alloc] initWithFile:teamSpace.teamFile];
    fileViewController.teamSpace = teamSpace;
    fileViewController.cloudTitleView = self.cloudTitleView;
    [self.navigationController pushViewController:fileViewController animated:YES];
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

#pragma mark NSFetchedResultsControllerDelegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeUpdate:
            
            break;
        case NSFetchedResultsChangeMove:
            [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}

-(void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.tableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.tableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}

@end
