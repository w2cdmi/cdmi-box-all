//
//  CloudUploadTargetFolderViewController.m
//  OneMail
//
//  Created by cse  on 15/11/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudUploadTargetFolderViewController.h"
#import <CoreData/CoreData.h>
#import "File+Remote.h"
#import "AppDelegate.h"
#import "CloudFileTableViewCell.h"
@interface CloudUploadTargetFolderViewController ()<NSFetchedResultsControllerDelegate,UITableViewDataSource,UITableViewDelegate>

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) NSFetchedResultsController *fetchController;
@property (nonatomic, strong) NSString *uploadTargetFolderPath;

@end

@implementation CloudUploadTargetFolderViewController
- (id)initWithFile:(File *)file uploadTargetFolderPath:(NSString *)path {
    if (self = [super init]) {
        self.file = file;
        self.uploadTargetFolderPath = [path stringByAppendingPathComponent:self.file.fileName];
        
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
        NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileParent = %@ AND fileOwner = %@ AND fileType = 0", self.file.fileId, self.file.fileOwner];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"fileSortNameKey" ascending:YES];
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

- (void)viewDidLoad{
    [super viewDidLoad];
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(selectBackButton)];
    UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc] initWithTitle:getLocalizedString(@"Cancel", nil) style:UIBarButtonItemStylePlain target:self action:@selector(selectCancelButton)];
    UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:getLocalizedString(@"Confirm", nil) style:UIBarButtonItemStylePlain target:self action:@selector(selectDoneButton)];
    self.navigationItem.leftBarButtonItems = @[backButton,cancelButton];
    self.navigationItem.rightBarButtonItem = doneButton;
    self.title = self.file.fileName;
    [self.tableView registerClass:[CloudFileTableViewCell class] forCellReuseIdentifier:@"CloudFileTableViewCell"];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    [self.tableView setSeparatorColor:[UIColor colorWithRed:200/255.0f green:199/255.0f blue:204/255.0f alpha:1.0f]];
    [self.tableView setScrollEnabled:YES];
    [self setExtraCellLineHidden:self.tableView];
    [self reloadFolderDataSource];
}

- (void) selectBackButton {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void) selectCancelButton {
    [self.navigationController popToViewController:self.rootViewController animated:YES];
}

- (void) selectDoneButton {
    if (self.cloudUploadTargetFolderConfirm) {
        self.cloudUploadTargetFolderConfirm(self.uploadTargetFolderPath,self.file.fileId,self.file.fileOwner);
    }
}

#pragma mark tableViewDataSourceLoad
- (void)reloadFolderDataSource {
    __block __weak typeof(self) weakSelf = self;
    [self.file folderReload:^(id retobj) {
        __strong typeof(self) strongSelf = weakSelf;
        dispatch_async(dispatch_get_main_queue(), ^{
            [strongSelf performFetch];
            strongSelf.tableView.scrollEnabled = YES;
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        __strong typeof(self) strongSelf = weakSelf;
        dispatch_async(dispatch_get_main_queue(), ^{
            [strongSelf performFetch];
            strongSelf.tableView.scrollEnabled = YES;
            NSHTTPURLResponse *httpResponse=(NSHTTPURLResponse *)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
            if (httpResponse.statusCode == 404) {
                [self.navigationController popToRootViewControllerAnimated:YES];
            }
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

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudFileTableViewCell *cell = (CloudFileTableViewCell *)[tableView dequeueReusableCellWithIdentifier:@"CloudFileTableViewCell"];
    if (cell == nil) {
        cell = [[CloudFileTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"CloudFileTableViewCell"];
    }
    cell.file = [self.fetchController objectAtIndexPath:indexPath];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    File *file = [self.fetchController.fetchedObjects objectAtIndex:indexPath.row];
    CloudUploadTargetFolderViewController *uploadTargetFolderViewController = [[CloudUploadTargetFolderViewController alloc] initWithFile:file uploadTargetFolderPath:self.uploadTargetFolderPath];
    uploadTargetFolderViewController.rootViewController = self.rootViewController;
    uploadTargetFolderViewController.cloudUploadTargetFolderConfirm = self.cloudUploadTargetFolderConfirm;
    [self.navigationController pushViewController:uploadTargetFolderViewController animated:YES];
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

#pragma mark NSFetchedResultsController delegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    CloudFileTableViewCell *cell = nil;
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeDelete:
            cell = (CloudFileTableViewCell*)[self.tableView cellForRowAtIndexPath:indexPath];
            cell.file = nil;
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

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
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
