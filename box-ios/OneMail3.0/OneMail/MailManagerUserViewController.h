//
//  MailManagerUserViewController.h
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Session;

@protocol MailManagerUserListCardDelegate <NSObject>

@required

- (void)deleteMailListUser:(NSString*)userCloudId;

@end

@interface MailManagerUserListCard : UIButton

@property (nonatomic, strong) NSString *userEmail;
@property (nonatomic, weak) id<MailManagerUserListCardDelegate> delegate;

- (id) initWithUserEmail:(NSString*)userEmail;

@end



@protocol MailManagerUserSearchDelegate <NSObject>

@required

- (void)addMailUser:(NSString*)userEmail;
- (void)deleteMailUser:(NSString*)userEmail;

@end

@interface MailManagerUserSearchCell : UITableViewCell

@property (nonatomic, strong) NSString *userEmail;
@property (nonatomic, assign) BOOL selectState;
@property (nonatomic, weak) id<MailManagerUserSearchDelegate> delegate;

@end

@protocol MailManagerUserDelegate <NSObject>

@required

- (void)completeMailUsers:(NSArray*)userEmailArray;

@end

@interface MailManagerUserViewController : UIViewController

@property (nonatomic, weak) id<MailManagerUserDelegate> delegate;
- (id)initWithSession:(Session*)session;

@end

