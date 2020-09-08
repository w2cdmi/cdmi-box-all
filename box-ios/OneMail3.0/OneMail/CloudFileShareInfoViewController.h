//
//  CloudFileShareInfoViewController.h
//  OneMail
//
//  Created by cse  on 15/11/24.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class User;
@class File;
@class CloudFileShareLinkInfo;

@protocol CloudFileShareWithDelegate <NSObject>

@required

- (void)deleteShareUser:(User*)user;

@end

@interface CloudFileShareWithCell : UITableViewCell
@property (nonatomic, strong) User *user;
@property (nonatomic, weak) id<CloudFileShareWithDelegate> delegate;
@end


@protocol CloudFileShareLinkDelegate <NSObject>

@required

- (void)deleteShareLink:(CloudFileShareLinkInfo*)cloudFileShareLinkInfo;
- (void)editShareLink:(CloudFileShareLinkInfo*)cloudFileShareLinkInfo;
- (void)pushViewController:(UIViewController *)viewController;
@end

@interface CloudFileShareLinkCell : UITableViewCell
@property (nonatomic, strong) CloudFileShareLinkInfo *cloudFileShareLinkInfo;
@property (nonatomic, strong) File *file;
@property (nonatomic, weak) id<CloudFileShareLinkDelegate>delegate;
@end


@interface CloudFileShareInfoViewController : UIViewController
- (id)initWithFile:(File*)file;
@end
