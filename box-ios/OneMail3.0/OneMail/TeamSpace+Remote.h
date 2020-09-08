//
//  TeamSpace+Remote.h
//  OneMail
//
//  Created by cse  on 15/11/5.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TeamSpace.h"
#import "DataCommon.h"

@interface TeamSpace (Remote)

+ (void) spaceList:(NSString*)userId succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
+ (void) spaceCreate:(NSString *)teamName
             succeed:(HWRemoteSuccessBlock)succeed
              failed:(HWRemoteFailedBlock)failed;
- (void) spaceDelete:(HWRemoteSuccessBlock)succeed
              failed:(HWRemoteFailedBlock)failed;
- (void)spaceMemberlist:(NSDictionary *)memberInfo
                succeed:(HWRemoteSuccessBlock)succeed
                 failed:(HWRemoteFailedBlock)failed;
- (void)spaceMemberDelete:(NSDictionary *)memberInfo
                  succeed:(HWRemoteSuccessBlock)succeed
                   failed:(HWRemoteFailedBlock)failed;
- (void)spaceMemberAdd:(NSDictionary *)memberInfo
               succeed:(HWRemoteSuccessBlock)succeed
                failed:(HWRemoteFailedBlock)failed;
- (void)spaceMemberUpdate:(NSDictionary *)memberInfo
                  succeed:(HWRemoteSuccessBlock)succeed
                   failed:(HWRemoteFailedBlock)failed;
- (void) getMemberInfoWithId:(NSString*)userId succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;

@end

