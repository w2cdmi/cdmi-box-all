//
//  User+Remote.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "User+Remote.h"
#import "AppDelegate.h"
#import "UserSearchOperation.h"

@implementation User (Remote)

+ (void) searchUser:(NSString*) keyword
            succeed:(HWRemoteSuccessBlock) succeed
             failed:(HWRemoteFailedBlock) failed {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *search=[[RequestEntity alloc]init];
        [search setUserSearchType:@"auto"];
        [search setUserSearchKeyword:keyword];
        [search setListOffset:@(0)];
        [search setListLimit:@(50)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
        [appDelegate.remoteManager.httpService doEntityRequst:search serviceType:ServiceUserSearch completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                SERVICE_FAILED(@"SearchADUser");
            } else {
                NSArray *userInfos = [responseObject objectForKey:@"users"];
                UserSearchOperation *userSearchOperation = [[UserSearchOperation alloc] init];
                [userSearchOperation userSearchResultOperation:userInfos completion:^{
                    SERVICE_SUCCEED(@"SearchADUser");
                }];
//                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
//                [ctx performBlockAndWait:^{
//                    for (NSDictionary *userInfo in userInfos) {
//                        if ([[userInfo objectForKey:@"status"] isEqualToString:@"nonesystemuser"] &&
//                            [userInfo objectForKey:@"loginName"]) {
//                            [User availableUsersWithUserLoginName:[userInfo objectForKey:@"loginName"] succeed:^(id retobj) {
//                                
//                            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
//                                
//                            }];
//                        }
//                        if (![userInfo objectForKey:@"id"]) {
//                            continue;
//                        }
//                        User *user = [User getUserWithUserSingleId:[[userInfo objectForKey:@"id"] stringValue] context:nil];
//                        if (!user) {
//                            user = [User userInsertWithInfo:userInfo context:ctx];
//                        }
//                    }
//                    [ctx save:nil];
//                }];
            }
        }];
    } failed:failed];
}

- (void) getUserInfo:(HWRemoteSuccessBlock) succeed
              failed:(HWRemoteFailedBlock) failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [[appDelegate remoteManager] ensureCloudLogin:^{
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
        RequestEntity *requestEntity = [[RequestEntity alloc] init];
        [requestEntity setUserId:@(self.userSingleId.integerValue)];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceUserInfo completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"GetUserInfo");
            } else {
            succeed:SERVICE_SUCCEED(@"GetUserInfo");
            }
        }];
    } failed:failed];
}


+ (void) getUserInfo:(NSString*) userSingleId
             context:(NSManagedObjectContext*) ctx
             succeed:(HWRemoteSuccessBlock) succeed
              failed:(HWRemoteFailedBlock) failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [[appDelegate remoteManager] ensureCloudLogin:^{
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
        RequestEntity *requestEntity = [[RequestEntity alloc] init];
        [requestEntity setUserId:@(userSingleId.integerValue)];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceUserInfo completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"GetUserInfo");
            } else {
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                [ctx performBlock:^{
                    User *user = [User getUserWithUserSingleId:userSingleId context:nil];
                    if (!user) {
                        [User userInsertWithInfo:responseObject context:ctx];
                    }
                    [ctx save:nil];
                }];
            succeed:SERVICE_SUCCEED(@"GetUserInfo");
            }
        }];
    } failed:failed];
}

+ (void) availableUsersWithUserLoginName:(NSString*)userLginName
                                 succeed:(HWRemoteSuccessBlock)succeed
                                  failed:(HWRemoteFailedBlock)failed {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *createUser=[[RequestEntity alloc]init];
        [createUser setUserLoginName:userLginName];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
        [appDelegate.remoteManager.httpService doEntityRequst:createUser serviceType:ServiceUserCreate completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failure:SERVICE_FAILED(@"createAccount");
            } else {
            succeed:SERVICE_SUCCEED(@"createAccount");
            }
        }];
    } failed:failed];
}

- (void) availableUser:(HWRemoteSuccessBlock)succeed
                failed:(HWRemoteFailedBlock)failed {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *createUser=[[RequestEntity alloc]init];
        [createUser setUserLoginName:self.userLoginName];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
        [appDelegate.remoteManager.httpService doEntityRequst:createUser serviceType:ServiceUserCreate completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failure:SERVICE_FAILED(@"createAccount");
            } else {
                self.userCloudId = [responseObject objectForKey:@"userId"];
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                [ctx performBlockAndWait:^{
                    User *user = (User*)[ctx objectWithID:self.objectID];
                    if (!user) {
                        [User userInsertWithInfo:responseObject context:ctx];
                    }
                    [ctx save:nil];
                }];
            succeed:SERVICE_SUCCEED(@"createAccount");
            }
        }];
    } failed:failed];
}



- (void) availableUsers:(HWRemoteSuccessBlock)succeed
                 failed:(HWRemoteFailedBlock)failed {
    if ([self.userSystemStatus isEqualToString:@"nonesystemuser"]) {
        AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
        [appDelegate.remoteManager ensureCloudLogin:^{
            RequestEntity *createUser=[[RequestEntity alloc]init];
            [createUser setUserLoginName:self.userName];
            [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
            [appDelegate.remoteManager.httpService doEntityRequst:createUser serviceType:ServiceUserCreate completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
                if (error) {
                failure:SERVICE_FAILED(@"createAccount");
                }else{
                    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                    [ctx performBlockAndWait:^{
                        User *shadow = (User*)[ctx objectWithID:self.objectID];
                        [shadow setUserInfo:responseObject];
                        [ctx save:nil];
                    }];
                     succeed:SERVICE_SUCCEED(@"createAccount");
                }
            }];
        } failed:failed];
    }
    else if ([self.userSystemStatus isEqualToString:@"enable"]) {
        succeed(nil);
    }
    else if ([self.userSystemStatus isEqualToString:@"disable"]) {
        failed(nil,nil,nil,0);
    }
    else{
        failed(nil,nil,nil,0);
    }
}

- (void)getUserHeadIcon:(HWRemoteSuccessBlock)succeed
                 failed:(HWRemoteFailedBlock)failed {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *createUser=[[RequestEntity alloc]init];
        [createUser setUserId:@(self.userSingleId.integerValue)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
        [appDelegate.remoteManager.httpService doEntityRequst:createUser serviceType:ServiceUserHeadIcon completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failure:SERVICE_FAILED(@"createAccount");
            } else {
                User *user = [User getUserWithUserSingleId:appDelegate.localManager.userSingleId context:nil];
                [user saveUserHeadIcon:responseObject];
            succeed:SERVICE_SUCCEED(@"createAccount");
            }
        }];
    } failed:failed];
}

@end

