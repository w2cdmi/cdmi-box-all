//
//  Session.h
//  OneMail
//
//  Created by cse  on 16/1/18.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Message;

@interface Session : NSManagedObject

@property (nonatomic, retain) NSString * sessionId;
@property (nonatomic, retain) NSString * sessionLastMessageId;
@property (nonatomic, retain) NSDate   * sessionSyncDate;
@property (nonatomic, retain) NSNumber * sessionNotification;
@property (nonatomic, retain) NSNumber * sessionScreenName;
@property (nonatomic, retain) NSString * sessionTitle;
@property (nonatomic, retain) NSDate   * sessionTopDate;
@property (nonatomic, retain) NSNumber * sessionTopFlag;
@property (nonatomic, retain) NSString * sessionUsers;
@property (nonatomic, retain) NSString * sessionOwner;


+ (Session *)getSessionWithSessionId:(NSString *)sessionId ctx:(NSManagedObjectContext*)ctx;
+ (Session *)getSessionWithSessionUsers:(NSString *)sessionUsers ctx:(NSManagedObjectContext*)ctx;
+ (Session *)sessionInsertWithSessionId:(NSString*)sessionId ctx:(NSManagedObjectContext *)ctx;

- (NSInteger)sessionMessageCount;
- (NSInteger)sessionUnreadMessageCount;
- (void)sessionUnreadMessageReset;

- (void)removeSession;

- (void)saveSessionLastMessageId:(Message*)message;
- (void)saveSessionTopFlag:(NSNumber*)sessionTopFlag;
- (void)saveSessionNotification:(NSNumber*)sessionNotification;
- (void)saveSessionScreenName:(NSNumber*)sessionScreenName;
- (void)saveSessionTitle:(NSString*)sessionTitle;
- (void)saveSessionUsers:(NSString*)sessionUsers;

@end
