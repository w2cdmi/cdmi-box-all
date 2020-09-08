//
//  MailManagerUserViewController.h
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class Session;
@class Message;
@protocol MailContactUserListCardDelegate <NSObject>

@required

- (void)deleteListUser:(NSString*)userCloudId;

@end

@interface MailContactUserListCard : UIButton

@property (nonatomic, strong) NSString *userEmail;
@property (nonatomic, weak) id<MailContactUserListCardDelegate> delegate;

- (id) initWithUserEmail:(NSString*)userEmail;

@end



@protocol MailContactUserSearchDelegate <NSObject>

@required

- (void)addMailUser:(NSString*)userEmail;
- (void)deleteMailUser:(NSString*)userEmail;

@end

@interface MailContactUserSearchCell : UITableViewCell

@property (nonatomic, strong) NSString *userEmail;
@property (nonatomic, assign) BOOL selectState;
@property (nonatomic, weak) id<MailContactUserSearchDelegate> delegate;

@end

@protocol MailContactUserDelegate <NSObject>

@required

- (void)completeMailUsers:(NSArray*)userEmailArray;

@end

@interface MailContactViewController : UIViewController
- (id)initWithMessage:(Message *)forwardMessage;
- (id)initWithShareLink:(NSString *)shareLink;
@property (nonatomic, weak) id<MailContactUserDelegate> delegate;

@end

