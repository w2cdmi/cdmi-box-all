//
//  CloudTransferViewController.m
//  OneMail
//
//  Created by cse  on 15/11/12.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudTransferViewController.h"
#import "CloudTransferTableViewCell.h"
#import <CoreData/CoreData.h>
#import "AppDelegate.h"
#import "MenuViewController.h"
#import "TransportTask.h"
#import "File.h"
#import "CloudTransferMenuView.h"

@interface CloudTransferViewController ()<NSFetchedResultsControllerDelegate,UITableViewDelegate,UITableViewDataSource,UIActionSheetDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) UILabel  *transferTitleLabel;
@property (nonatomic, strong) UIButton *transferBackButton;
@property (nonatomic, strong) UIButton *transferMenuButton;
@property (nonatomic, strong) UIButton *transferClearButton;

@property (nonatomic, strong) UIView   *transferSegmentView;
@property (nonatomic, strong) UIButton *transferingButton;
@property (nonatomic, strong) UILabel  *transferingButtonLabel;
@property (nonatomic, strong) UIButton *transferredButton;
@property (nonatomic, strong) UILabel  *transferredButtonLabel;
@property (nonatomic, strong) UIView   *transferButtonHighLight;

@property (nonatomic, strong) CloudTransferMenuView *transferMenuView;

@property (nonatomic, strong) UITableView *transferTableView;
@property (nonatomic, strong) NSFetchedResultsController *fetchedResultsController;
@property (nonatomic, strong) UIView   *cloudFileNullView;

@end

@implementation CloudTransferViewController

- (id)initWithFile:(File *)file {
    self = [super init];
    if (self) {
        self.file = file;
        
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"TransportTask" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        NSSortDescriptor *statusDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"taskStatus" ascending:YES];
        NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"taskCreatedDate" ascending:NO];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setEntity:entity];
        [request setSortDescriptors:@[statusDescriptor,dateDescriptor]];
        [request setFetchBatchSize:20];
        _fetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
        _fetchedResultsController.delegate = self;
        
        self.cloudFileNullView.hidden = NO;
        
        [self showTransfering];
    }
    return self;
}

- (void)performFetch {
    NSError *error = NULL;
    if (![self.fetchedResultsController performFetch:&error]) {
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.transferTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    if (self.file) {
        self.transferTitleLabel.text = self.file.fileName;
    } else {
        self.transferTitleLabel.text = getLocalizedString(@"CloudTransferTitle", nil);
    }
    
    self.transferBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.transferBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.transferBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.transferBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.transferBackButton addTarget:self action:@selector(transferBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.transferMenuButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)-4-44, 0, 44, 44)];
    self.transferMenuButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.transferMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_nor"] forState:UIControlStateNormal];
    [self.transferMenuButton setImage:[UIImage imageNamed:@"ic_nav_more_press"] forState:UIControlStateHighlighted];
    [self.transferMenuButton addTarget:self action:@selector(transferMenuButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.transferClearButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, MAX(44, [CommonFunction labelSizeWithString:getLocalizedString(@"CloudTransferClearAll", nil) font:[UIFont systemFontOfSize:17.0f]].width), 44)];
    self.transferClearButton.frame = CGRectMake(CGRectGetWidth(self.view.frame)-15-CGRectGetWidth(self.transferClearButton.frame), 0, CGRectGetWidth(self.transferClearButton.frame), CGRectGetHeight(self.transferClearButton.frame));
    [self.transferClearButton setAttributedTitle:[[NSAttributedString alloc] initWithString:getLocalizedString(@"CloudTransferClearAll", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"ffffff" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:17.0f]}] forState:UIControlStateNormal];
    [self.transferClearButton addTarget:self action:@selector(transferClearButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
    
    self.transferSegmentView = [[UIView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height, CGRectGetWidth(self.view.frame), 44)];
    self.transferSegmentView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    self.transferSegmentView.layer.borderWidth = 0.5;
    self.transferSegmentView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    [self.view addSubview:self.transferSegmentView];
    
    self.transferingButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.view.frame)/2, 44)];
    self.transferingButton.tag = 10001;
    self.transferingButton.selected = NO;
    [self.transferingButton addTarget:self action:@selector(showTransfering) forControlEvents:UIControlEventTouchUpInside];
    self.transferingButtonLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 11, CGRectGetWidth(self.transferingButton.frame)-15-10, CGRectGetHeight(self.transferingButton.frame)-11-11)];
    self.transferingButtonLabel.text = getLocalizedString(@"CloudTransferingTitle", nil);
    self.transferingButtonLabel.textAlignment = NSTextAlignmentCenter;
    [self.transferingButton addSubview:self.transferingButtonLabel];
    UIView *transferingButtonMidLine = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.transferingButton.frame)-0.5/2, (44-24)/2, 0.5/2, 24)];
    transferingButtonMidLine.backgroundColor = [CommonFunction colorWithString:@"cccccc" alpha:1.0f];
    [self.transferingButton addSubview:transferingButtonMidLine];
    [self.transferSegmentView addSubview:self.transferingButton];
    
    self.transferredButton = [[UIButton alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.view.frame)/2, 0, CGRectGetWidth(self.view.frame)/2, 44)];
    self.transferredButton.tag = 10002;
    self.transferredButton.selected = NO;
    [self.transferredButton addTarget:self action:@selector(showTransfed) forControlEvents:UIControlEventTouchUpInside];
    self.transferredButtonLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 11, CGRectGetWidth(self.transferredButton.frame)-15-10, CGRectGetHeight(self.transferredButton.frame)-11-11)];
    self.transferredButtonLabel.text = getLocalizedString(@"CloudTransferredTitle", nil);
    self.transferredButtonLabel.textAlignment = NSTextAlignmentCenter;
    [self.transferredButton addSubview:self.transferredButtonLabel];
    UIView *transfedButtonMidLine = [[UIView alloc] initWithFrame:CGRectMake(0, (44-24)/2, 0.5/2, 24)];
    transfedButtonMidLine.backgroundColor = [CommonFunction colorWithString:@"cccccc" alpha:1.0f];
    [self.transferredButton addSubview:transfedButtonMidLine];
    [self.transferSegmentView addSubview:self.transferredButton];
    
    self.transferButtonHighLight = [[UIView alloc] initWithFrame:CGRectMake(0, 44-3, CGRectGetWidth(self.view.frame)/2, 3)];
    self.transferButtonHighLight.backgroundColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
    [self buttonHighLightState:self.transferingButton];
    [self buttonNormalState:self.transferredButton];
    
    self.transferTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height+navigationBarFrame.size.height+CGRectGetHeight(self.transferSegmentView.frame), CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame)-statusBarFrame.size.height-navigationBarFrame.size.height-CGRectGetHeight(self.transferSegmentView.frame)) style:UITableViewStylePlain];
    self.transferTableView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.transferTableView registerClass:[CloudTransferTableViewCell class] forCellReuseIdentifier:@"CloudTransferTableViewCell"];
    self.transferTableView.dataSource = self;
    self.transferTableView.delegate = self;
    self.transferTableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.transferTableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.transferTableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.transferTableView];
    
    UISwipeGestureRecognizer *handleRevognizer1 = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handSwipeRight)];
    handleRevognizer1.direction = UISwipeGestureRecognizerDirectionRight;
    [self.view addGestureRecognizer:handleRevognizer1];
    
    
    UISwipeGestureRecognizer *handleRevognizer2 = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handSwipeLeft)];
    handleRevognizer2.direction = UISwipeGestureRecognizerDirectionLeft;
    [self.view addGestureRecognizer:handleRevognizer2];

    
}

- (void)handSwipeRight
{
    [self showTransfed];
}


- (void)handSwipeLeft
{
    [self showTransfering];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.transferTitleLabel];
    [self.navigationController.navigationBar addSubview:self.transferBackButton];
    [self.navigationController.navigationBar addSubview:self.transferMenuButton];
    [self.navigationController.navigationBar addSubview:self.transferClearButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.transferTitleLabel removeFromSuperview];
    [self.transferBackButton removeFromSuperview];
    [self.transferMenuButton removeFromSuperview];
    [self.transferClearButton removeFromSuperview];
}

- (UIView *)cloudFileNullView{
    if (!_cloudFileNullView) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
        CGRect navigationBarFrame = delegate.navigationController.navigationBar.frame;
        _cloudFileNullView = [[UIView alloc] initWithFrame:CGRectMake(0, statusBarFrame.size.height + navigationBarFrame.size.height   + 50, self.view.frame.size.width, self.view.frame.size.height - statusBarFrame.size.height - navigationBarFrame.size.height)];
        _cloudFileNullView.hidden = YES;
        
        UIImageView *cloudFileNullImage = [[UIImageView alloc] initWithFrame:CGRectMake((_cloudFileNullView.frame.size.width - 96) / 2,100, 96, 96)];
        cloudFileNullImage.image = [UIImage imageNamed:@"ic_menu_transfer_task_nor"];
        [_cloudFileNullView addSubview:cloudFileNullImage];
        
        UILabel * cloudFileNullTitle = [[UILabel alloc] initWithFrame:CGRectMake(15, CGRectGetMaxY(cloudFileNullImage.frame) + 10, self.view.frame.size.width - 30, 20)];
        cloudFileNullTitle.text = getLocalizedString(@"CloudNoTransferTask", nil);
        cloudFileNullTitle.textColor = [CommonFunction colorWithString:@"000000" alpha:1];
        cloudFileNullTitle.font = [UIFont systemFontOfSize:15];
        cloudFileNullTitle.textAlignment = NSTextAlignmentCenter;
        [_cloudFileNullView addSubview:cloudFileNullTitle];
        
        UILabel *cloudFileNullPrompt = [CommonFunction labelWithFrame:CGRectZero textFont:[UIFont systemFontOfSize:14] textColor:[CommonFunction colorWithString:@"666666" alpha:1] textAlignment:NSTextAlignmentCenter];
        //        cloudFileNullPrompt.text = @"You can click on the 'Upload' to add the files here";
        cloudFileNullPrompt.numberOfLines = 0;
        CGSize size = [CommonFunction labelSizeWithLabel:cloudFileNullPrompt limitSize:CGSizeMake(self.view.frame.size.width - 30, 1000)];
        cloudFileNullPrompt.frame = CGRectMake(70, CGRectGetMaxY(cloudFileNullTitle.frame) + 10, size.width,size.height);
        
        [_cloudFileNullView addSubview:cloudFileNullPrompt];
        
        [self.view addSubview:_cloudFileNullView];
    }
    return _cloudFileNullView;
}

- (void)transferBackButtonClick {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!self.file && appDelegate.leftViewOpened) {
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshUserIcon];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshEmailAddress];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshTransferTaskCount];
        [appDelegate.LeftSlideVC openLeftView];
    }
    [self.navigationController popToViewController:self.rootViewController animated:YES];
}

- (void)transferMenuButtonClick {
    if (self.transferMenuView.hidden) {
        self.transferMenuView.hidden = NO;
    } else {
        self.transferMenuView.hidden = YES;
    }
}

- (void)transferClearButtonClick {
    if (self.fetchedResultsController.fetchedObjects.count > 0) {
        UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:getLocalizedString(@"CloudTransferClearPrompt", nil) delegate:self cancelButtonTitle:getLocalizedString(@"Cancel", nil) destructiveButtonTitle:nil otherButtonTitles:getLocalizedString(@"Confirm", nil), nil];
        [actionSheet showInView:self.view];
    }
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {
        NSArray *taskArray = [[NSArray alloc] initWithArray:self.fetchedResultsController.fetchedObjects];
        for (TransportTask *task in taskArray) {
            [task remove];
        }
    }
}

#pragma mark transferMenuView

- (CloudTransferMenuView*)transferMenuView {
    if (!_transferMenuView) {
        _transferMenuView = [[CloudTransferMenuView alloc] initWithFrame:self.view.frame];
        
        CloudTransferMenuCell *resumeCell = [[CloudTransferMenuCell alloc] initWithTitle:getLocalizedString(@"CloudTransferResumeAll", nil)];
        CloudTransferMenuCell *pauseCell = [[CloudTransferMenuCell alloc] initWithTitle:getLocalizedString(@"CloudTransferPauseAll", nil)];
        CloudTransferMenuCell *cancelCell = [[CloudTransferMenuCell alloc] initWithTitle:getLocalizedString(@"CloudTransferCancelAll", nil)];
        [_transferMenuView setMenuCells:@[resumeCell,pauseCell,cancelCell]];
    }
    return _transferMenuView;
}
#pragma mark highLight state
- (void)buttonNormalState:(UIButton*)button {
    if (button.tag == 10001) {
        self.transferingButtonLabel.font = [UIFont systemFontOfSize:17.0];
        self.transferingButtonLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
    } else {
        self.transferredButtonLabel.font = [UIFont systemFontOfSize:17.0];
        self.transferredButtonLabel.textColor = [CommonFunction colorWithString:@"333333" alpha:1.0f];
    }
}

- (void)buttonHighLightState:(UIButton*)button {
    if (self.transferButtonHighLight.superview) {
        [self.transferButtonHighLight removeFromSuperview];
    }
    if (button.tag == 10001) {
        self.transferMenuButton.hidden = NO;
        self.transferClearButton.hidden = YES;
        self.transferingButtonLabel.font = [UIFont boldSystemFontOfSize:17.0f];
        self.transferingButtonLabel.textColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
        [self.transferingButton addSubview:self.transferButtonHighLight];
    } else {
        self.transferMenuButton.hidden = YES;
        self.transferClearButton.hidden = NO;
        self.transferredButtonLabel.font = [UIFont boldSystemFontOfSize:17.0f];
        self.transferredButtonLabel.textColor = [CommonFunction colorWithString:@"2e90e5" alpha:1.0f];
        [self.transferredButton addSubview:self.transferButtonHighLight];
    }
}

#pragma mark show transfering
- (void)showTransfering {
    if (self.transferingButton.selected) {
        return;
    }
    [self buttonHighLightState:self.transferingButton];
    [self buttonNormalState:self.transferredButton];
    self.transferingButton.selected = YES;
    self.transferredButton.selected = NO;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSPredicate *predicate;
    if (self.file) {
        predicate = [NSPredicate predicateWithFormat:@"taskStatus!=%@ AND taskStatus!=%@ AND file.fileOwner=%@ AND file.fileParent=%@",@(TaskSucceed),@(TaskCancel),self.file.fileOwner,self.file.fileId];
    } else {
        predicate = [NSPredicate predicateWithFormat:@"taskStatus!=%@ AND taskStatus!=%@ AND taskOwner=%@ AND taskRecoverable=%@",@(TaskSucceed),@(TaskCancel),appDelegate.localManager.userCloudId,@(1)];
    }
    NSFetchRequest *request = self.fetchedResultsController.fetchRequest;
    [request setPredicate:predicate];
    [self performFetch];
    [self.transferTableView reloadData];
    
    if (self.fetchedResultsController.fetchedObjects.count > 0) {
        self.cloudFileNullView.hidden = YES;
    }
}

#pragma mark show transfered
- (void)showTransfed {
    if (self.transferredButton.selected) {
        return;
    }
    [self buttonNormalState:self.transferingButton];
    [self buttonHighLightState:self.transferredButton];
    self.transferredButton.selected = YES;
    self.transferingButton.selected = NO;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSPredicate *predicate;
    if (self.file) {
        predicate = [NSPredicate predicateWithFormat:@"taskStatus=%@ AND file.fileOwner=%@ AND file.fileParent=%@",@(TaskSucceed),self.file.fileOwner,self.file.fileId];
    } else {
        predicate = [NSPredicate predicateWithFormat:@"taskStatus=%@ AND taskOwner=%@ AND taskRecoverable=%@",@(TaskSucceed),appDelegate.localManager.userCloudId,@(1)];
    }
    NSFetchRequest *request = self.fetchedResultsController.fetchRequest;
    [request setPredicate:predicate];
    [self performFetch];
    [self.transferTableView reloadData];
    
    if(self.fetchedResultsController.fetchedObjects.count > 0) {
        self.cloudFileNullView.hidden = YES;
    }
}

#pragma mark tableView dataSource+delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.fetchedResultsController.fetchedObjects.count > 0) {
        tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    } else {
        tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    return self.fetchedResultsController.fetchedObjects.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    TransportTask *transportTask = (TransportTask*)[self.fetchedResultsController objectAtIndexPath:indexPath];
    CloudTransferTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CloudTransferTableViewCell"];
    if (!cell) {
        cell = [[CloudTransferTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudTransferTableViewCell"];
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.transportTask = transportTask;
    cell.transferViewController = self;
    [cell refreshSize];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudTransferTableViewCell *cell = (CloudTransferTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
    if ([cell.transportTask.file isFolder]) {
        CloudTransferViewController *transferView = [[CloudTransferViewController alloc] initWithFile:cell.transportTask.file];
        transferView.rootViewController = self;
        transferView.parentTransferCell = cell;
        [self.navigationController pushViewController:transferView animated:YES];
    }
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([cell respondsToSelector:@selector(setSeparatorInset:)]) {
        [cell setSeparatorInset:UIEdgeInsetsMake(0, 15, 0, 15)];
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

#pragma mark NSFetchedResultsController delegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.transferTableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    [self.transferTableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.transferTableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationBottom];
            break;
        case NSFetchedResultsChangeDelete:
            [self.transferTableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationTop];
            break;
        case NSFetchedResultsChangeUpdate:
            
            break;
        case NSFetchedResultsChangeMove:
            [self.transferTableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationTop];
            [self.transferTableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationBottom];
            break;
        default:
            break;
    }
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.transferTableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.transferTableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}

#pragma mark task control
+ (void)resumeAllTransferTask {
    NSArray *taskPauseArray = [TransportTask getAllTaskWithTaskStatus:@(TaskSuspend) ctx:nil];
    NSArray *taskFailedArray = [TransportTask getAllTaskWithTaskStatus:@(TaskFailed) ctx:nil];
    NSArray *taskWaittingNetworkArray = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitNetwork) ctx:nil];
    NSMutableArray *taskArray = [[NSMutableArray alloc] init];
    [taskArray addObjectsFromArray:taskPauseArray];
    [taskArray addObjectsFromArray:taskFailedArray];
    [taskArray addObjectsFromArray:taskWaittingNetworkArray];
    for (TransportTask *task in taskArray) {
        [task.taskHandle waiting];
    }
}

+ (void)pauseAllTransferTask {
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.uploadOperation closeOperation];
    [delegate.downloadOperation closeOperation];
    NSArray *taskInitArray = [TransportTask getAllTaskWithTaskStatus:@(TaskInitialing) ctx:nil];
    NSArray *taskRuningArray = [TransportTask getAllTaskWithTaskStatus:@(TaskRunning) ctx:nil];
    NSArray *taskWaittingArray = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitting) ctx:nil];
    NSArray *taskWaittingNetworkArray = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitNetwork) ctx:nil];
    NSMutableArray *taskArray = [[NSMutableArray alloc] init];
    [taskArray addObjectsFromArray:taskInitArray];
    [taskArray addObjectsFromArray:taskRuningArray];
    [taskArray addObjectsFromArray:taskWaittingArray];
    [taskArray addObjectsFromArray:taskWaittingNetworkArray];
    for (TransportTask *task in taskArray) {
        [task.taskHandle suspend];
    }
    [delegate.uploadOperation openOperation];
    [delegate.downloadOperation openOperation];
}

+ (void)cancelAllTransferTask {
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.uploadOperation closeOperation];
    [delegate.downloadOperation closeOperation];
    NSArray *taskInitArray = [TransportTask getAllTaskWithTaskStatus:@(TaskInitialing) ctx:nil];
    NSArray *taskRuningArray = [TransportTask getAllTaskWithTaskStatus:@(TaskRunning) ctx:nil];
    NSArray *taskWaittingArray = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitting) ctx:nil];
    NSArray *taskPauseArray = [TransportTask getAllTaskWithTaskStatus:@(TaskSuspend) ctx:nil];
    NSArray *taskFailedArray = [TransportTask getAllTaskWithTaskStatus:@(TaskFailed) ctx:nil];
    NSArray *taskWaittingNetworkArray = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitNetwork) ctx:nil];
    NSMutableArray *taskArray = [[NSMutableArray alloc] init];
    [taskArray addObjectsFromArray:taskInitArray];
    [taskArray addObjectsFromArray:taskRuningArray];
    [taskArray addObjectsFromArray:taskWaittingArray];
    [taskArray addObjectsFromArray:taskPauseArray];
    [taskArray addObjectsFromArray:taskFailedArray];
    [taskArray addObjectsFromArray:taskWaittingNetworkArray];
    for (TransportTask *task in taskArray) {
        [task.taskHandle cancel];
    }
    [delegate.uploadOperation openOperation];
    [delegate.downloadOperation openOperation];
}

+ (void)waitNetworkAllTransferTask {
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.uploadOperation closeOperation];
    [delegate.downloadOperation closeOperation];
    NSArray *taskInitArray = [TransportTask getAllTaskWithTaskStatus:@(TaskInitialing) ctx:nil];
    NSArray *taskRuningArray = [TransportTask getAllTaskWithTaskStatus:@(TaskRunning) ctx:nil];
    NSArray *taskWaittingArray = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitting) ctx:nil];
    NSArray *taskFailedArray = [TransportTask getAllTaskWithTaskStatus:@(TaskFailed) ctx:nil];
    
    NSMutableArray *taskArray = [[NSMutableArray alloc] init];
    [taskArray addObjectsFromArray:taskInitArray];
    [taskArray addObjectsFromArray:taskRuningArray];
    [taskArray addObjectsFromArray:taskWaittingArray];
    [taskArray addObjectsFromArray:taskFailedArray];
    
    for (TransportTask *task in taskArray) {
        [task.taskHandle waitNetwork];
    }
    [delegate.uploadOperation openOperation];
    [delegate.downloadOperation openOperation];
}

+ (void)waitAllTransferTask {
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.uploadOperation closeOperation];
    [delegate.downloadOperation closeOperation];
    NSArray *taskWaittingArray = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitNetwork) ctx:nil];
    
    for (TransportTask *task in taskWaittingArray) {
        [task.taskHandle waiting];
    }
    [delegate.uploadOperation openOperation];
    [delegate.downloadOperation openOperation];
}

@end
