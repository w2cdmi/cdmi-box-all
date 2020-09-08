//
//  ContactViewController.m
//  OneMail
//
//  Created by cse  on 15/12/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "ContactViewController.h"
#import "ContactTableViewCell.h"
#import "AppDelegate.h"
#import "MenuViewController.h"
#import <CoreData/CoreData.h>
#import "ContactAddViewController.h"
#import "ContactUserViewController.h"

@interface ContactViewController ()<NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) UILabel         *contactTitleLabel;
@property (nonatomic, strong) UIButton        *contactBackButton;
@property (nonatomic, strong) UITableViewCell *contactAddCell;
@property (nonatomic, strong) UITableViewCell *contactGrounpCell;
@property (nonatomic, strong) UIView          *contactCountView;
@property (nonatomic, strong) UILabel         *contactCountLabel;
@property (nonatomic, strong) NSFetchedResultsController *fetchController;

@end

@implementation ContactViewController

- (id)init {
    self = [super init];
    if (self) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userCloudId !=%@ AND userMyContactFlag = 1 AND userName != %@",appDelegate.localManager.userCloudId,@"admin"];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
        NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"userSortNameKey" ascending:YES];
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        [request setPredicate:predicate];
        [request setEntity:entity];
        [request setSortDescriptors:@[sort]];
        self.fetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:[sort key] cacheName:nil];
        self.fetchController.delegate = self;
        NSError *error = NULL;
        if (![self.fetchController performFetch:&error]) {
            NSLog(@"Unresolved error %@,%@",error,[error userInfo]);
            abort();
        }
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
    UIView *backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
    backgroundView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    [self.view addSubview:backgroundView];
    
    self.contactTitleLabel = [CommonFunction labelWithFrame:CGRectMake(44+4, 7, CGRectGetWidth(self.view.frame)-(44+4)*2, 24) textFont:[UIFont boldSystemFontOfSize:18.0f] textColor:[CommonFunction colorWithString:@"ffffff" alpha:1.0f] textAlignment:NSTextAlignmentCenter];
    self.contactTitleLabel.text = NSLocalizedString(@"ContactTitle", nil);
    
    self.contactBackButton = [[UIButton alloc] initWithFrame:CGRectMake(4, 0, 44, 44)];
    self.contactBackButton.imageView.frame = CGRectMake(11, 11, 22, 22);
    [self.contactBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
    [self.contactBackButton setImage:[UIImage imageNamed:@"ic_nav_back_press"] forState:UIControlStateHighlighted];
    [self.contactBackButton addTarget:self action:@selector(contactBackButtonClick) forControlEvents:UIControlEventTouchUpInside];
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStylePlain];
    [self.tableView registerClass:[ContactTableViewCell class] forCellReuseIdentifier:@"ContactTableViewCell"];
    self.tableView.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    self.tableView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.separatorInset = UIEdgeInsetsMake(0, 15, 0, 15);
    self.tableView.showsVerticalScrollIndicator = NO;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.tableFooterView = self.contactCountView;
    [self.tableView reloadData];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self refreshContactCount];
    [self.navigationController.navigationBar addSubview:self.contactTitleLabel];
    [self.navigationController.navigationBar addSubview:self.contactBackButton];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onemail.mainTabHide" object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.contactTitleLabel removeFromSuperview];
    [self.contactBackButton removeFromSuperview];
}

- (void)contactBackButtonClick {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (appDelegate.leftViewOpened) {
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshUserIcon];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshEmailAddress];
        [(MenuViewController*)appDelegate.LeftSlideVC.leftVC refreshTransferTaskCount];
        [appDelegate.LeftSlideVC openLeftView];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UITableViewCell*)contactAddCell {
    if (!_contactAddCell) {
        _contactAddCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _contactAddCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 5.5, 44, 44)];
        imageView.image = [UIImage imageNamed:@"img_contact_new"];
        [_contactAddCell.contentView addSubview:imageView];
        
        UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(imageView.frame)+10, 17.5, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(imageView.frame)-10-15, 20)];
        titleLabel.text = NSLocalizedString(@"ContactAddTitle", nil);
        titleLabel.font = [UIFont systemFontOfSize:15.0f];
        titleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLabel.textAlignment = NSTextAlignmentLeft;
        [_contactAddCell.contentView addSubview:titleLabel];
    }
    return _contactAddCell;
}

- (UITableViewCell*)contactGrounpCell {
    if (!_contactGrounpCell) {
        _contactGrounpCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        _contactGrounpCell.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 5.5, 44, 44)];
        imageView.image = [UIImage imageNamed:@"img_contact_group"];
        [_contactGrounpCell.contentView addSubview:imageView];
        
        UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(imageView.frame)+10, 17.5, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(imageView.frame)-10-15, 20)];
        titleLabel.text = NSLocalizedString(@"ContactGroupTitle", nil);
        titleLabel.font = [UIFont systemFontOfSize:15.0f];
        titleLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        titleLabel.textAlignment = NSTextAlignmentLeft;
        [_contactGrounpCell.contentView addSubview:titleLabel];
    }
    return _contactGrounpCell;
}

- (UIView*)contactCountView {
    if (!_contactCountView) {
        _contactCountView = [[UIView alloc] init];
        _contactCountLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 10, CGRectGetWidth(self.view.frame)-15-15, 15)];
        _contactCountLabel.text = [NSString stringWithFormat:NSLocalizedString(@"ContactCountLabel", nil),(unsigned long)self.fetchController.fetchedObjects.count];
        _contactCountLabel.font = [UIFont systemFontOfSize:12.0f];
        _contactCountLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        _contactCountLabel.textAlignment = NSTextAlignmentCenter;
        [_contactCountView addSubview:_contactCountLabel];
    }
    return _contactCountView;
}

- (void)refreshContactCount {
    self.contactCountLabel.text = [NSString stringWithFormat:NSLocalizedString(@"ContactCountLabel", nil),(unsigned long)self.fetchController.fetchedObjects.count];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1 + [self.fetchController sections].count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 2;
    } else {
        if ([self.fetchController sections].count > 0) {
            id<NSFetchedResultsSectionInfo> sectionInfo = [[self.fetchController sections] objectAtIndex:section-1];
            return [sectionInfo numberOfObjects];
        }
        return 0;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 55.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return 0.1f;
    } else {
        return 22.0f;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1f;
}

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return nil;
    } else {
        if ([self.fetchController sections].count > 0) {
            id<NSFetchedResultsSectionInfo> sectionInfo = [[self.fetchController sections] objectAtIndex:section-1];
            return [sectionInfo name];
        }
        return nil;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return self.contactAddCell;
        } else {
            return self.contactGrounpCell;
        }
    } else {
        ContactTableViewCell *cell = (ContactTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"ContactTableViewCell"];
        if (!cell) {
            cell = [[ContactTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"ContactTableViewCell"];
        }
        NSIndexPath *indexPathNew = [NSIndexPath indexPathForRow:indexPath.row inSection:indexPath.section-1];
        User *user = [self.fetchController objectAtIndexPath:indexPathNew];
        cell.user = user;
        cell.searchingState = NO;
        return cell;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            ContactAddViewController *contactAddView = [[ContactAddViewController alloc] init];
            [self.navigationController pushViewController:contactAddView animated:YES];
        }
    } else {
        ContactTableViewCell *cell = (ContactTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
        ContactUserViewController *userView = [[ContactUserViewController alloc] initWithUser:cell.user];
        [self.navigationController pushViewController:userView animated:YES];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

#pragma mark NSFetchedResultsControllerDelegate
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView beginUpdates];
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    [self.tableView endUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    NSIndexPath *indexPathAdjust = [NSIndexPath indexPathForRow:indexPath.row inSection:indexPath.section+1];
    NSIndexPath *newIndexPathAdjust = [NSIndexPath indexPathForRow:newIndexPath.row inSection:newIndexPath.section+1];
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPathAdjust] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPathAdjust] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeUpdate:
            
            break;
        case NSFetchedResultsChangeMove:
            [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPathAdjust] withRowAnimation:UITableViewRowAnimationFade];
            [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPathAdjust] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}

-(void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type {
    switch (type) {
        case NSFetchedResultsChangeInsert:
            [self.tableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex+1] withRowAnimation:UITableViewRowAnimationFade];
            break;
        case NSFetchedResultsChangeDelete:
            [self.tableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex+1] withRowAnimation:UITableViewRowAnimationFade];
            break;
        default:
            break;
    }
}




@end
