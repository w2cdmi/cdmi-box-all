//
//  User+Remote.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "User.h"
#import "DataCommon.h"

@interface User (Remote)

+ (void) searchUser:(NSString*) keyword succeed:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;

- (void) getUserInfo:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;
+ (void) getUserInfo:(NSString*) userSingleId context:(NSManagedObjectContext*) ctx succeed:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;

- (void) availableUsers:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) availableUser:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
+ (void) availableUsersWithUserLoginName:(NSString*)userLginName succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;

- (void) getUserHeadIcon:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;

@end

