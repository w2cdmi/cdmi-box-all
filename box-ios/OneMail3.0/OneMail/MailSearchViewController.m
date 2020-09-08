////
////  MailSearchViewController.m
////  OneMail
////
////  Created by cse  on 15/11/30.
////  Copyright (c) 2015年 cse. All rights reserved.
////
//
//#import "MailSearchViewController.h"
//#import "AppDelegate.h"
//#import <CoreData/CoreData.h>
//#import "MailSessionTableViewCell.h"
//#import "Session.h"
//#import "Message.h"
//typedef enum : NSUInteger {
//    SearchAll = 1,
//    SearchAddresser,
//    SearchTitle,
//    SearchContent,
//} SearchType;
//@interface MailSearchViewController ()<UITextFieldDelegate,NSFetchedResultsControllerDelegate,UITableViewDataSource,UITableViewDelegate>
//
//@property (nonatomic, strong) UITextField *searchTextField;
//@property (nonatomic, strong) NSNumber *searchType;
//@property (nonatomic, strong) NSFetchedResultsController *searchFetchController;
//@property (nonatomic, strong) UITableView *searchResultTableView;
//@end
//
//@implementation MailSearchViewController
//
//- (void)viewDidLoad {
//    [super viewDidLoad];
//    self.searchType = @(SearchAll);
//    UIButton *searchBackButton = [[UIButton alloc] initWithFrame:CGRectMake(15, 11, 22, 22)];
//    [searchBackButton setImage:[UIImage imageNamed:@"ic_nav_back_nor"] forState:UIControlStateNormal];
//    [searchBackButton addTarget:self action:@selector(popViewController) forControlEvents:UIControlEventTouchUpInside];
//    
//    UIView *searchBarView = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(searchBackButton.frame)+2, 4, CGRectGetWidth(self.view.frame)-CGRectGetMaxX(searchBackButton.frame)-2-15, 36)];
//    searchBarView.backgroundColor = [CommonFunction colorWithString:@"ffffff" alpha:0.3];
//    searchBarView.layer.borderWidth = 0.5;
//    searchBarView.layer.borderColor = [CommonFunction colorWithString:@"1a5080" alpha:1.0f].CGColor;
//    searchBarView.layer.cornerRadius = 4;
//    searchBarView.layer.masksToBounds = YES;
//    UIImageView *searchImageView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 7, 22, 22)];
//    searchImageView.image = [UIImage imageNamed:@"ic_nav_search_nor"];
//    [searchBarView addSubview:searchImageView];
//    
//    self.searchTextField = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(searchImageView.frame)+4, 7, CGRectGetWidth(searchBarView.frame)-CGRectGetMaxX(searchImageView.frame)-4-10, 22)];
//    self.searchTextField.font = [UIFont systemFontOfSize:14.0f];
//    self.searchTextField.textColor = [CommonFunction colorWithString:@"ffffff" alpha:1.0f];
//    self.searchTextField.textAlignment = NSTextAlignmentLeft;
//    self.searchTextField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Search" attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"bedefa" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}];
//    [searchBarView addSubview:self.searchTextField];
//    
//    [self.navigationController.navigationBar addSubview:searchBackButton];
//    [self.navigationController.navigationBar addSubview:searchBarView];
//    
//    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
//    CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
//    CGRect navigationBarFrame = appDelegate.navigationController.navigationBar.frame;
//    UILabel *searchTitleLable = [[UILabel alloc] initWithFrame:CGRectMake((CGRectGetWidth(self.view.frame)-272)/2, statusBarFrame.size.height+navigationBarFrame.size.height+14, 272, 25)];
//    searchTitleLable.text = @"Search Mail";
//    searchTitleLable.font = [UIFont systemFontOfSize:18.0f];
//    searchTitleLable.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
//    searchTitleLable.textAlignment = NSTextAlignmentCenter;
//    [self.view addSubview:searchTitleLable];
//    
//    UIButton *searchAddresser = [self searchTypeButtonWithImage:[UIImage imageNamed:@"ic_search_addresser"] title:@"Addresser"];
//    UIButton *searchTitle = [self searchTypeButtonWithImage:[UIImage imageNamed:@"ic_search_title"] title:@"Title"];
//    UIButton *searchContent = [self searchTypeButtonWithImage:[UIImage imageNamed:@"ic_search_content"] title:@"Content"];
//    
//    searchAddresser.frame = CGRectMake(CGRectGetMinX(searchTitleLable.frame), 15, CGRectGetWidth(searchAddresser.frame), CGRectGetHeight(searchAddresser.frame));
//    searchTitle.frame = CGRectMake(CGRectGetMaxX(searchAddresser.frame)+10, 15, CGRectGetWidth(searchAddresser.frame), CGRectGetHeight(searchAddresser.frame));
//    searchContent.frame = CGRectMake(CGRectGetMaxX(searchTitle.frame)+10, 15, CGRectGetWidth(searchTitle.frame), CGRectGetHeight(searchTitle.frame));
//    
//    self.searchResultTableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStylePlain];
//    self.searchResultTableView.dataSource = self;
//    self.searchResultTableView.delegate = self;
//    [self.searchResultTableView registerClass:[MailSessionTableViewCell class] forCellReuseIdentifier:@"MailSessionTableViewCell"];
//    
//}
//
//- (void)popViewController {
//    if (self.searchResultTableView.superview) {
//        [self.searchResultTableView removeFromSuperview];
//        self.searchFetchController = nil;
//    } else {
//        [self.navigationController popViewControllerAnimated:YES];
//    }
//}
//
//- (UIButton*)searchTypeButtonWithImage:(UIImage*)image title:(NSString*)title {
//    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 84, 85)];
//    button.backgroundColor = [UIColor clearColor];
//    UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake((CGRectGetWidth(button.frame)-44)/2, 0, 44, 44)];
//    imageView.image = image;
//    [button addSubview:imageView];
//    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(button.frame)-36, CGRectGetWidth(button.frame), 36)];
//    label.font = [UIFont systemFontOfSize:12.0f];
//    label.text = title;
//    label.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
//    label.textAlignment = NSTextAlignmentCenter;
//    [button addSubview:label];
//    return button;
//}
//
//- (void)searchAddresserOperation {
//    [self.searchTextField becomeFirstResponder];
//    self.searchType = @(SearchAddresser);
//}
//
//- (void)searchTitleOperation {
//    [self.searchTextField becomeFirstResponder];
//    self.searchType = @(SearchTitle);
//}
//
//- (void)searchContentOperation {
//    [self.searchTextField becomeFirstResponder];
//    self.searchType = @(SearchContent);
//}
//
//- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
//    if (self.searchType.integerValue == SearchAll) {
//        return NO;
//    } else {
//        return YES;
//    }
//}
//- (void)textFieldDidBeginEditing:(UITextField *)textField {
//    
//}
//
//- (void)textFieldDidEndEditing:(UITextField *)textField {
//    AppDelegate *appDelagte = [UIApplication sharedApplication].delegate;
//    NSPredicate *predicate = nil;
//    NSEntityDescription *entity = nil;
//    NSSortDescriptor *sort = nil;
//    if (self.searchType.integerValue == SearchAddresser) {
//        predicate = [NSPredicate predicateWithFormat:@"sessionUsers contains[cd]",textField.text];
//        entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:appDelagte.localManager.managedObjectContext];
//        sort = [NSSortDescriptor sortDescriptorWithKey:@"sessionLastRefresh" ascending:YES];
//    }
//    if (self.searchType.integerValue == SearchTitle) {
//        predicate = [NSPredicate predicateWithFormat:@"sessionTitle contains[cd]",textField.text];
//        entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:appDelagte.localManager.managedObjectContext];
//        sort = [NSSortDescriptor sortDescriptorWithKey:@"sessionLastRefresh" ascending:YES];
//    }
//    if (self.searchType.integerValue == SearchContent) {
//        predicate = [NSPredicate predicateWithFormat:@"messageTextContent contains[cd]",textField.text];
//        entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:appDelagte.localManager.managedObjectContext];
//        sort = [NSSortDescriptor sortDescriptorWithKey:@"messageDate" ascending:YES];
//    }
//    
//    NSFetchRequest *request = [[NSFetchRequest alloc] init];
//    request.predicate = predicate;
//    request.entity = entity;
//    request.sortDescriptors = @[sort];
//    _searchFetchController = [[NSFetchedResultsController alloc] initWithFetchRequest:request managedObjectContext:appDelagte.localManager.managedObjectContext sectionNameKeyPath:nil cacheName:nil];
//    _searchFetchController.delegate = self;
//    NSError *error = NULL;
//    if (![_searchFetchController performFetch:&error]) {
//        NSLog(@"Unresolved error %@,%@",error,[error userInfo]);
//        abort();
//    }
//    [self.searchResultTableView addSubview:self.view];
//    [self.searchResultTableView reloadData];
//}
//
//- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
//    return 1;
//}
//
//- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
//    if (self.searchFetchController.fetchedObjects.count == 0) {
//        tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
//    } else {
//        tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
//    }
//    return self.searchFetchController.fetchedObjects.count;
//}
//
//- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
//    return 0.0f;
//}
//
//- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
//    return 68.0f;
//}
//
//- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
//    MailSessionTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"MailSessionTabelViewCell"];
//    if (!cell) {
//        cell = [[MailSessionTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"MailSessionTableViewCell"];
//    }
//    if (self.searchType.integerValue == SearchContent) {
//        Message *message = [self.searchFetchController.fetchedObjects objectAtIndex:indexPath.row];
//        cell.session = [Session getSessionWithMessageId:message.messageId];
//    } else {
//        cell.session = [self.searchFetchController.fetchedObjects objectAtIndex:indexPath.row];
//    }
//    return cell;
//}
//@end
