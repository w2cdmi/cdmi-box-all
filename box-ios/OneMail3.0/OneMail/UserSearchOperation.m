//
//  UserSearchOperation.m
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "UserSearchOperation.h"
#import "User+Remote.h"
#import "AppDelegate.h"

typedef void(^UserOperationCompletion)();

@interface UserSearchOperation ()

@property (nonatomic, copy) UserOperationCompletion operationCompletion;
@property (nonatomic, strong) NSSet *userCallingObj;
@property (nonatomic, strong) NSMutableSet *userSuccessObj;
@property (nonatomic, strong) NSMutableSet *userFailedObj;
@end

@implementation UserSearchOperation

- (id) init {
    if (self = [super init]) {
        _userSuccessObj = [[NSMutableSet alloc] init];
        _userFailedObj = [[NSMutableSet alloc] init];
    }
    return self;
}

- (void) onSuceess:(id) obj {
    if (obj) {
        [_userSuccessObj addObject:obj];
        [self checkFinished];
    }
}

- (void) onFailed:(id) obj {
    if (obj) {
        [_userFailedObj addObject:obj];
        [self checkFinished];
    }
}

- (void) checkFinished {
    NSMutableSet* tmpSet = [NSMutableSet setWithSet:_userCallingObj];
    [tmpSet minusSet:_userFailedObj];
    [tmpSet minusSet:_userSuccessObj];
    if (tmpSet.count == 0) {
        if (_operationCompletion) {
            _operationCompletion();
        }
    }
}


- (void)userSearchResultOperation:(NSArray *)usersInfo completion:(void (^)())completion {
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    
    self.userCallingObj = [NSSet setWithArray:usersInfo];
    self.operationCompletion = ^(){
        completion();
    };
    if (usersInfo.count == 0) {
        self.operationCompletion();
    }
    NSMutableArray *userEnable = [[NSMutableArray alloc] init];
    NSMutableArray *userDisable = [[NSMutableArray alloc] init];
    NSMutableArray *userNone = [[NSMutableArray alloc] init];
    NSMutableArray *userGhost = [[NSMutableArray alloc] init];
    
    for (NSDictionary *userInfo in usersInfo) {
        NSString *userStatus = [userInfo objectForKey:@"status"];
        if ([userStatus isEqualToString:@"enable"]) {
            [userEnable addObject:userInfo];
        } else if ([userStatus isEqualToString:@"disable"]) {
            [userDisable addObject:userInfo];
        } else if ([userStatus isEqualToString:@"nonesystemuser"]) {
            [userNone addObject:userInfo];
        } else {
            [userGhost addObject:userInfo];
        }
    }
    
    for (NSDictionary *userInfo in userEnable) {
        __block User *user = [User getUserWithUserSingleId:[[userInfo objectForKey:@"id"] stringValue] context:nil];
        if (!user) {
            NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
            [ctx performBlockAndWait:^{
                user = [User userInsertWithInfo:userInfo context:ctx];
                [ctx save:nil];
            }];
        }
        if (user) {
            [self onSuceess:userInfo];
        } else {
            [self onFailed:userInfo];
        }
    }
    
    for (NSDictionary *userInfo in userDisable) {
        __block User *user = [User getUserWithUserSingleId:[[userInfo objectForKey:@"id"] stringValue] context:nil];
        if (!user) {
            NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
            [ctx performBlockAndWait:^{
                user = [User userInsertWithInfo:userInfo context:ctx];
                [ctx save:nil];
            }];
        }
        if (user) {
            [self onSuceess:userInfo];
        } else {
            [self onFailed:userInfo];
        }
    }
    
    for (NSDictionary *userInfo in userNone) {
        NSString *userLoginName = [userInfo objectForKey:@"loginName"];
        if (userLoginName) {
            [User availableUsersWithUserLoginName:userLoginName succeed:^(id retobj) {
                __block User *user = [User getUserWithUserSingleId:[[retobj objectForKey:@"id"] stringValue] context:nil];
                if (!user) {
                    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                    [ctx performBlockAndWait:^{
                        user = [User userInsertWithInfo:retobj context:ctx];
                        [ctx save:nil];
                    }];
                }
                if (user) {
                    [self onSuceess:userInfo];
                } else {
                    [self onFailed:userInfo];
                }
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                [self onFailed:userInfo];
            }];
        } else {
            [self onFailed:userInfo];
        }
    }
    
    for (NSDictionary *userInfo in userGhost) {
        [self onFailed:userInfo];
    }
}
@end
