//
//  User.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "Session.h"

@interface User : NSManagedObject

@property (nonatomic, retain) NSString * userSingleId;
@property (nonatomic, retain) NSString * userCloudId;
@property (nonatomic, retain) NSString * userName;
@property (nonatomic, retain) NSString * userLoginName;
@property (nonatomic, retain) NSString * userEmail;
@property (nonatomic, retain) NSString * userDescription;
@property (nonatomic, retain) NSString * userRemark;
@property (nonatomic, retain) NSString * userPhone;
@property (nonatomic, retain) NSString * userSystemStatus;
@property (nonatomic, retain) NSString * userSortNameKey;
@property (nonatomic, retain) NSDate   * userSortTimeKey;
@property (nonatomic, retain) NSNumber * userMyContactFlag;
@property (nonatomic, retain) NSNumber * userRecentContactFlag;


+ (User *)getUserWithUserSingleId:(NSString *)userSingleId context:(NSManagedObjectContext*)context;
+ (User *)getUserWithUserCloudId:(NSString *)userCloudId context:(NSManagedObjectContext*)context;
+ (User *)getUserWithUserEmail:(NSString *)userEmail context:(NSManagedObjectContext*)context;
+ (User *)getUserWithUserLoginName:(NSString *)userLoginName context:(NSManagedObjectContext*)context;

+ (NSArray *)getMyContactUsers:(NSManagedObjectContext *)context;
+ (NSArray *)getRecentContactUsers:(NSManagedObjectContext*)context;
+ (NSArray *)getUserArrayWithKey:(NSString*)keyWord context:(NSManagedObjectContext*)context;
+ (NSArray *)getUserHasEmailArrayWithKey:(NSString *)keyWord context:(NSManagedObjectContext *)context;

+ (User *)userInsertWithInfo:(NSDictionary*)userInfo context:(NSManagedObjectContext*)context;
+ (User *)userInsertWithEmail:(NSString*)userEmail context:(NSManagedObjectContext*)context;
- (void)setUserInfo:(NSDictionary *)userInfo;
- (void)userRemove;

- (NSString*)userHeadIconPath;
- (void)saveUserHeadIcon:(NSData*)data;

- (void)saveUserRemarks:(NSString*)remarks;
- (void)saveuserPhone:(NSString*)phone;
- (void)saveUserEmail:(NSString*)email;

- (void)changeUserRecentContactFlag:(NSNumber *)userRecentContactFlag;
- (void)changeUserMyContactFlag:(NSNumber *)userMyContactFlag;
@end
