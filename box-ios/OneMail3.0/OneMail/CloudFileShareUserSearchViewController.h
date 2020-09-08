//
//  CloudFileShareUserSearchViewController.h
//  OneMail
//
//  Created by cse  on 16/1/13.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class File;
@class User;

@protocol CloudFileShareUserListCardDelegate <NSObject>

@required

- (void)deleteListShareUser:(NSString*)userCloudId;

@end

@interface CloudFileShareUserListCard : UIButton

@property (nonatomic, strong) NSString *userCloudId;
@property (nonatomic, weak) id<CloudFileShareUserListCardDelegate> delegate;

- (id) initWithUserCloudId:(NSString*)userCloudId;

@end



@protocol CloudFileShareUserSearchDelegate <NSObject>

@required

- (void)addShareUser:(User*)user;
- (void)deleteShareUser:(User*)user;

@end

@interface CloudFileShareUserSearchCell : UITableViewCell

@property (nonatomic, strong) User *user;
@property (nonatomic, assign) BOOL selectState;
@property (nonatomic, weak) id<CloudFileShareUserSearchDelegate> delegate;

@end



@interface CloudFileShareUserSearchViewController : UIViewController

- (id)initWithShareUsers:(NSArray*)shareUserArray file:(File*)file;

@end
