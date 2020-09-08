//
//  CloudTeamMoveViewController.m
//  OneMail
//
//  Created by cse on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudTeamMoveViewController.h"
#import "AppDelegate.h"
#import "CloudSpaceTableViewCell.h"
#import "CloudFileMoveViewController.h"

@interface CloudTeamMoveViewController() <NSFetchedResultsControllerDelegate,UITableViewDataSource,UITableViewDelegate>

@property (nonatomic,strong) NSArray *sourceFiles;
@property (nonatomic,strong) NSArray *sourceFilesOwner;
@property (nonatomic,strong) NSFetchedResultsController *fetchController;

@end

@implementation CloudTeamMoveViewController
- (id)initWithSourceFiles:(NSArray *)sourceFiles filesOwner:(NSArray *)sourceFilesOwner{
    if (self = [super init]) {
        self.sourceFiles = [NSArray arrayWithArray:sourceFiles];
        self.sourceFilesOwner = [NSArray arrayWithArray:sourceFilesOwner];
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"teamUserId = %@",appDelegate.localManager.userCloudId];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"TeamSpace" inManagedObjectContext:ctx];
        NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"teamDate" ascending:NO];
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        [fetchRequest setPredicate:predicate];
        [fetchRequest setEntity:entity];
        [fetchRequest setSortDescriptors:@[sortDescriptor]];
        [fetchRequest setFetchBatchSize:20];
        self.fetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:ctx sectionNameKeyPath:nil cacheName:nil];
        self.fetchController.delegate = self;
        [self performFetch];
    }
    return self;
}

- (void)performFetch {
    NSError *error = NULL;
    if (![self.fetchController performFetch:&error]) {
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = getLocalizedString(@"CloudTeamSpaceTitle", nil);
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    [self.tableView registerClass:[CloudSpaceTableViewCell class] forCellReuseIdentifier:@"CloudSpaceTableViewCell"];
    [self.tableView setDataSource:self];
    [self.tableView setDelegate:self];
    [self.tableView setSeparatorColor:[UIColor colorWithRed:200/255.0f green:199/255.0f blue:204/255.0f alpha:1.0f]];
    [self.tableView setScrollEnabled:YES];
    [self setExtraCellLineHidden:self.tableView];
    [self reloadDataSource];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[UIApplication sharedApplication]setStatusBarStyle:UIStatusBarStyleLightContent];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark DataSource
- (void)reloadDataSource {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    __block __weak typeof(self) weakSelf = self;
    [TeamSpace spaceList:appDelegate.localManager.userCloudId succeed:^(id retobj) {
        __strong typeof(self) strongSelf = weakSelf;
        dispatch_async(dispatch_get_main_queue(), ^{
            strongSelf.tableView.scrollEnabled = YES;
            [strongSelf.refreshControl endRefreshing];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            __strong typeof(self) strongSelf = weakSelf;
            strongSelf.tableView.scrollEnabled = YES;
            [strongSelf.refreshControl endRefreshing];
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudTeamSpaceFailedPrompt", nil)];
        });
    }];
}

- (void)setExtraCellLineHidden:(UITableView*)tableView {
    UIView *clearView = [[UIView alloc] init];
    clearView.backgroundColor = [UIColor clearColor];
    [tableView setTableFooterView:clearView];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.fetchController.fetchedObjects.count > 0) {
        tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    } else {
        tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    return self.fetchController.fetchedObjects.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 68.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.0f;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudSpaceTableViewCell *cell = (CloudSpaceTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"CloudSpaceTableViewCell"];
    if (cell == nil) {
        cell = [[CloudSpaceTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudSpaceTableViewCell"];
    }
    TeamSpace *teamSpace = [self.fetchController objectAtIndexPath:indexPath];
    cell.teamSpace = teamSpace;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    TeamSpace *teamSpace = [self.fetchController objectAtIndexPath:indexPath];
    CloudFileMoveViewController *fileMove = [[CloudFileMoveViewController alloc] initWithSourceFiles:self.sourceFiles filesOwner:self.sourceFilesOwner rootFile:teamSpace.teamFile];
    fileMove.rootViewController = self.rootViewController;
    [self.navigationController pushViewController:fileMove animated:YES];
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
