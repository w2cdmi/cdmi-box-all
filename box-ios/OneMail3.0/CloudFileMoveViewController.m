//
//  CloudFileMoveViewController.m
//  OneMail
//

//  Created by cse on 15/11/13.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudFileMoveViewController.h"
#import "File+Remote.h"
#import "AppDelegate.h"
#import "FileMultiOperation.h"
#import "MRProgress.h"
#import "UIAlertView+Blocks.h"
#import "CloudFileTableViewCell.h"
#import "File+Remote.h"
#import "File.h"
#import "TransportTask.h"
@interface CloudFileMoveViewController() <NSFetchedResultsControllerDelegate,UITableViewDataSource,UITableViewDelegate>

@property (nonatomic,strong) File *file;
@property (nonatomic,strong) NSArray *sourceFiles;
@property (nonatomic,strong) NSArray *sourceFilesOwner;
@property (nonatomic,strong) NSFetchedResultsController *fetchController;

@end

@implementation CloudFileMoveViewController

- (void)dealloc {
    NSLog(@"%@ moveViewController dealloc",self.file.fileName);
}

- (id)initWithSourceFiles:(NSArray *)sourceFiles filesOwner:(NSArray *)sourceFilesOwner rootFile:(File *)file {
    if (self = [super init]) {
        if (file) {
            self.file = file;
        } else {
            self.file = [File rootMyFolder];
        }

        self.sourceFiles = [NSArray arrayWithArray:sourceFiles];
        self.sourceFilesOwner = [NSArray arrayWithArray:sourceFilesOwner];
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
        NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileParent = %@ AND fileOwner = %@ AND fileType = 0 AND NOT(fileId IN %@)", self.file.fileId, self.file.fileOwner, self.sourceFiles];
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
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.scrollEnabled = YES;
    self.tableView.tableFooterView = [[UIView alloc] init];
    [self reloadFolderDataSource];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)selectDoneButton {
    NSString *firstFileID = self.sourceFiles.firstObject;
    NSString *firstFileOwner = self.sourceFilesOwner.firstObject;
    File *firstFile = [File getFileWithFileId:firstFileID fileOwner:firstFileOwner];
    if ([self.file.fileId isEqualToString:firstFile.parent.fileId] && [self.file.fileOwner isEqualToString:firstFile.parent.fileOwner]) {
        [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"目标文件夹未改变", nil)];
        return;
    }
    [self.navigationController popToViewController:self.rootViewController animated:YES];
    FileMultiOperation *multiOperation = [[FileMultiOperation alloc]init];
    multiOperation.completionBlock = ^(NSSet *succeed, NSSet *failed){
        if (succeed.count == 0) {
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
        } else {
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationSuccess", nil)];
        }
    };
    multiOperation.callingObj = [NSSet setWithArray:self.sourceFiles];
    for (NSString *fileId in self.sourceFiles) {
        NSUInteger index = [self.sourceFiles indexOfObject:fileId];
        NSString* fileOwner = [self.sourceFilesOwner objectAtIndex:index];
        File *file = [File getFileWithFileId:fileId fileOwner:fileOwner];
        if (file.transportTask && file.transportTask.taskStatus.integerValue == TaskRunning){
            continue;
        }
        NSString *fileOrignalParent = file.fileParent;
        if ([file.fileOwner isEqualToString:self.file.fileOwner]) {
            [file fileMove:self.file autoRename:NO succeed:^(id retobj) {
                [self moveDataHandleWithFile:file orignalParent:fileOrignalParent newName:file.fileName copy:NO];
                [multiOperation onSuceess:file.fileId];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                if (httpResponse.statusCode == 409) {
                    [UIAlertView showAlertViewWithTitle:[NSString stringWithFormat:getLocalizedString(@"CloudFileMovePrompt", nil),self.file.fileName,file.fileName] message:getLocalizedString(@"CloudFileMoveRenamePrompt", nil) cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                        [file fileMove:self.file autoRename:YES succeed:^(id retobj) {
                            [self moveDataHandleWithFile:file orignalParent:fileOrignalParent newName:[retobj objectForKey:@"name"] copy:NO];
                            [multiOperation onSuceess:file.fileId];
                        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                            [multiOperation onFailed:file.fileId];
                        }];
                    } onCancel:^{}];
                } else {
                    [multiOperation onFailed:file.fileId];
                }
            }];
        } else {
            [file fileCopy:self.file autoRename:NO succeed:^(id retobj) {
                [self moveDataHandleWithFile:file orignalParent:fileOrignalParent newName:file.fileName copy:YES];
                [multiOperation onSuceess:file.fileId];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
                if (httpResponse.statusCode == 409) {
                    [UIAlertView showAlertViewWithTitle:[NSString stringWithFormat:getLocalizedString(@"CloudFileMovePrompt", nil),self.file.fileName,file.fileName] message:getLocalizedString(@"CloudFileMoveRenamePrompt", nil) cancelButtonTitle:getLocalizedString(@"Cancel", nil) otherButtonTitles:@[getLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
                        [file fileCopy:self.file autoRename:YES succeed:^(id retobj) {
                            [self moveDataHandleWithFile:file orignalParent:fileOrignalParent newName:[retobj objectForKey:@"name"] copy:YES];
                            [multiOperation onSuceess:file.fileId];
                        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                            [multiOperation onFailed:file.fileId];
                        }];
                    } onCancel:^{}];
                } else {
                    [multiOperation onFailed:file.fileId];
                }
            }];
        }
    }
}
- (void)moveDataHandleWithFile:(File*)file orignalParent:(NSString*)orignalParent newName:(NSString*)newName copy:(BOOL)copy {
    if (!newName) return;
    
    NSString *fileDataLocalPath = [file fileDataLocalPath];
    NSString *fileOrignalDataPath = [[fileDataLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, orignalParent, file.fileName]];
    NSString *fileNewDataPath = [[fileDataLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, self.file.fileId, newName]];
    
    NSString *fileThumbnailLocalPath = [file fileThumbnailLocalPath];
    NSString *fileOrignalThumbnailPath = [[fileThumbnailLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, orignalParent, file.fileName]];
    NSString *fileNewThumbnailPath = [[fileThumbnailLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, self.file.fileId, newName]];
    
    NSString *fileCacheLocalPath = [file fileCacheLocalPath];
    NSString *fileOrignalCachePath = [[fileCacheLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, orignalParent, file.fileName]];
    NSString *fileNewCachePath = [[fileCacheLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, self.file.fileId, newName]];
    
    NSString *fileCompressLocalPath = [file fileCompressImagePath];
    NSString *fileOrignalCompressPath = [[fileCompressLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, orignalParent, file.fileName]];
    NSString *fileNewCompressPath = [[fileCompressLocalPath stringByDeletingLastPathComponent] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", file.fileOwner, self.file.fileId, newName]];
    
    if (fileOrignalDataPath && [[NSFileManager defaultManager] fileExistsAtPath:fileOrignalDataPath]) {
        if (copy) {
            [[NSFileManager defaultManager] copyItemAtPath:fileOrignalDataPath toPath:fileNewDataPath error:nil];
        } else {
            [[NSFileManager defaultManager] moveItemAtPath:fileOrignalDataPath toPath:fileNewDataPath error:nil];
        }
    }
    
    if (fileOrignalThumbnailPath && [[NSFileManager defaultManager] fileExistsAtPath:fileOrignalThumbnailPath]) {
        if (copy) {
            [[NSFileManager defaultManager] copyItemAtPath:fileOrignalThumbnailPath toPath:fileNewThumbnailPath error:nil];
        } else {
            [[NSFileManager defaultManager] moveItemAtPath:fileOrignalThumbnailPath toPath:fileNewThumbnailPath error:nil];
        }
    }
    
    if (fileOrignalCachePath && [[NSFileManager defaultManager] fileExistsAtPath:fileOrignalCachePath]) {
        if (copy) {
            [[NSFileManager defaultManager] copyItemAtPath:fileOrignalCachePath toPath:fileNewCachePath error:nil];
        } else {
            [[NSFileManager defaultManager] moveItemAtPath:fileOrignalCachePath toPath:fileNewCachePath error:nil];
        }
    }
    
    if (fileOrignalCompressPath && [[NSFileManager defaultManager] fileExistsAtPath:fileOrignalCompressPath]) {
        if (copy) {
            [[NSFileManager defaultManager] copyItemAtPath:fileOrignalCompressPath toPath:fileNewCompressPath error:nil];
        } else {
            [[NSFileManager defaultManager] moveItemAtPath:fileOrignalCompressPath toPath:fileNewCompressPath error:nil];
        }
    }
}

- (void) selectBackButton {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void) selectCancelButton {
    [self.navigationController popToViewController:self.rootViewController animated:YES];
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

#pragma mark tableView dataSource+delegate

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
        cell.file = [self.fetchController.fetchedObjects objectAtIndex:indexPath.row];

    return cell;
}



- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    CloudFileTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    
    File *file = cell.file;
//    File *file = [self.fetchController.fetchedObjects objectAtIndex:indexPath.row];
    CloudFileMoveViewController *fileMove = [[CloudFileMoveViewController alloc] initWithSourceFiles:self.sourceFiles filesOwner:self.sourceFilesOwner rootFile:file];
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
