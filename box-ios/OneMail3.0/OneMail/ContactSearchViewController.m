//
//  ContactSearchViewController.m
//  OneMail
//
//  Created by cse  on 15/12/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "ContactSearchViewController.h"
#import "AppDelegate.h"
#import "ContactTableViewCell.h"
#import <CoreData/CoreData.h>
#import "User+Remote.h"
#import "ContactUserViewController.h"

@interface ContactSearchViewController ()<UITableViewDataSource,UITableViewDelegate,UITextFieldDelegate,NSFetchedResultsControllerDelegate>

@property (nonatomic, strong) UIView      *searchView;
@property (nonatomic, strong) UITextField *searchTextField;
@property (nonatomic, strong) UITableView *searchTabelView;
@property (nonatomic, strong) UIView      *searchBackgroundView;
@property (nonatomic, strong) NSFetchedResultsController *searchFetchController;

@end

@implementation ContactSearchViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ic_nav_back_nor"] style:UIBarButtonItemStylePlain target:self action:@selector(popViewController)];
    self.view.backgroundColor = [CommonFunction colorWithString:@"fafafa" alpha:1.0f];
    
    self.searchTabelView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStylePlain];
    [self.searchTabelView registerClass:[ContactTableViewCell class] forCellReuseIdentifier:@"ContactTableViewCell"];
    self.searchTabelView.backgroundColor = [UIColor clearColor];
    self.searchTabelView.separatorColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f];
    self.searchTabelView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.searchTabelView.separatorInset = UIEdgeInsetsMake(0, 15, 0, 15);
    self.searchTabelView.layer.borderWidth = 0.5f;
    self.searchTabelView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    self.searchTabelView.delegate = self;
    self.searchTabelView.dataSource = self;
    [self setExtraCellLineHidden:self.searchTabelView];
    [self.view addSubview:self.searchTabelView];
}

- (void)popViewController {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:self.searchView];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.searchView removeFromSuperview];
}

- (UIView*)searchView {
    if (!_searchView) {
        _searchView = [[UIView alloc] initWithFrame:CGRectMake(15+22+2, 4, CGRectGetWidth(self.navigationController.navigationBar.frame)-15-22-2-15, 36)];
        _searchView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:0.3];
        _searchView.layer.cornerRadius = 4;
        _searchView.layer.masksToBounds = YES;
        _searchView.layer.borderWidth = 0.5;
        _searchView.layer.borderColor = [CommonFunction colorWithString:@"1a5080" alpha:1.0f].CGColor;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 7, 22, 22)];
        imageView.image = [UIImage imageNamed:@"ic_contact_corporate_search_nor"];
        [_searchView addSubview:imageView];
        
        self.searchTextField = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(imageView.frame)+4, 7, CGRectGetWidth(_searchView.frame)-CGRectGetMaxX(imageView.frame)-4-10,22)];
        self.searchTextField.backgroundColor = [UIColor clearColor];
        self.searchTextField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"ContactSearchTitle", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"bedefe" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}];
        self.searchTextField.font = [UIFont systemFontOfSize:14.0f];
        self.searchTextField.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
        self.searchTextField.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
        self.searchTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.searchTextField.delegate = self;
        self.searchTextField.returnKeyType = UIReturnKeySearch;
        [_searchView addSubview:self.searchTextField];
    }
    return _searchView;
}

- (UIView*)searchBackgroundView {
    if (!_searchBackgroundView) {
        _searchBackgroundView = [[UIView alloc] initWithFrame:self.view.frame];
        _searchBackgroundView.backgroundColor = [CommonFunction colorWithString:@"000000" alpha:0.3];
    }
    return _searchBackgroundView;
}

- (void)searchBackgroundViewShow {
    [self.view addSubview:self.searchBackgroundView];
}

- (void)searchBackgroundViewHide {
    [self.searchBackgroundView removeFromSuperview];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    [self searchBackgroundViewShow];
    self.searchFetchController = nil;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"userSortNameKey" ascending:YES];
    NSFetchRequest *reqeust = [[NSFetchRequest alloc] init];
    [reqeust setEntity:entity];
    [reqeust setSortDescriptors:@[sort]];
    [reqeust setFetchBatchSize:20];
    self.searchFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:reqeust managedObjectContext:appDelegate.localManager.managedObjectContext sectionNameKeyPath:[sort key] cacheName:nil];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [self searchBackgroundViewHide];
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [self searchBackgroundViewHide];
    if ([textField.text isEqualToString:@""]) {
        return;
    }
    [User searchUser:textField.text succeed:^(id retobj) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(userEmail contains[cd] %@ OR userName contains[cd] %@ OR userLoginName contains [cd] %@) AND userCloudId != %@",textField.text,textField.text,textField.text,delegate.localManager.userCloudId];
        NSFetchRequest *request = self.searchFetchController.fetchRequest;
        [request setPredicate:predicate];
        [self.searchFetchController performFetch:nil];
        [self.searchTabelView reloadData];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"ContactSearchFailed", nil)];
    }];
}

- (void)setExtraCellLineHidden:(UITableView*)tableView {
    UIView *clearView = [[UIView alloc] init];
    clearView.backgroundColor = [UIColor clearColor];
    clearView.layer.borderWidth = 0.5;
    clearView.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
    [tableView setTableFooterView:clearView];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return [self.searchFetchController sections].count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if ([self.searchFetchController sections].count > 0) {
        id<NSFetchedResultsSectionInfo> sectionInfo = [[self.searchFetchController sections] objectAtIndex:section];
            return [sectionInfo numberOfObjects];
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 55.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 22.0f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.1f;
}

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if ([self.searchFetchController sections].count > 0) {
        id<NSFetchedResultsSectionInfo> sectionInfo = [[self.searchFetchController sections] objectAtIndex:section];
        return [sectionInfo name];
    }
    return nil;
    
}
- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (self.searchTabelView == tableView) {
        id<NSFetchedResultsSectionInfo> sectionInfo = [[self.searchFetchController sections] objectAtIndex:section];
        return [CommonFunction tableViewHeaderWithTitle:[sectionInfo name]];
    } else {
        return nil;
    }
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    ContactTableViewCell *cell = (ContactTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"ContactTableViewCell"];
    if (!cell) {
        cell = [[ContactTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"ContactTableViewCell"];
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    User *user = [self.searchFetchController objectAtIndexPath:indexPath];
    cell.user = user;
    cell.searchingState = YES;
    return cell;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
//    ContactTableViewCell *cell = (ContactTableViewCell*)[tableView cellForRowAtIndexPath:indexPath];
//    ContactUserViewController *userView = [[ContactUserViewController alloc] initWithUser:cell.user];
//    [self.navigationController pushViewController:userView animated:YES];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

@end
