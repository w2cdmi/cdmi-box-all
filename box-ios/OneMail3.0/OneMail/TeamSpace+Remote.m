//
//  TeamSpace+Remote.m
//  OneMail
//
//  Created by cse  on 15/11/5.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TeamSpace+Remote.h"
#import "AppDelegate.h"

@implementation TeamSpace (Remote)

+ (void) spaceList:(NSString*)userId
           succeed:(HWRemoteSuccessBlock)succeed
            failed:(HWRemoteFailedBlock)failed
{
    if (!userId) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        ServiceType serviceCmd = ServiceSpaceList;
        RequestEntity *requestEntity = [[RequestEntity alloc] init];
        requestEntity.userId = [NSNumber numberWithLongLong:[userId longLongValue]];
        requestEntity.listOffset = @(0);
        requestEntity.listLimit = @(50);
        requestEntity.listField = @"createdAt";
        requestEntity.listDirection = @"ASC";
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                SERVICE_FAILED(@"SpaceList");
            } else {
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                NSPredicate *predicate = [NSPredicate predicateWithFormat:@"teamUserId = %@",appDelegate.localManager.userCloudId];
                NSEntityDescription *entity = [NSEntityDescription entityForName:@"TeamSpace" inManagedObjectContext:ctx];
                NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"teamId" ascending:YES];
                NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
                [fetchRequest setEntity:entity];
                [fetchRequest setPredicate:predicate];
                [fetchRequest setSortDescriptors:@[sort]];
                NSArray *localItems = [ctx executeFetchRequest:fetchRequest error:nil];
                NSArray *teamSpaceInfo = [responseObject objectForKey:@"memberships"];
                NSArray *remoteItems = [TeamSpace sortRemoteItemsFromResponse:teamSpaceInfo];
                [TeamSpace compareWithTeamInfo:remoteItems localItems:localItems context:ctx];
                [ctx performBlockAndWait:^{
                    if ([ctx hasChanges]) {
                        [ctx save:nil];
                    }
                }];
                SERVICE_SUCCEED(@"SpaceList");
            }
        }];
    } failed:failed];
}
+ (void) spaceCreate:(NSString *)teamName
             succeed:(HWRemoteSuccessBlock)succeed
              failed:(HWRemoteFailedBlock)failed{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        ServiceType serviceCmd = ServiceSpaceCreate;
        RequestEntity *requestEntity = [[RequestEntity alloc] init];
        requestEntity.spaceName = teamName;
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                SERVICE_FAILED(@"SpaceCreate");
            }
            else{
                SERVICE_SUCCEED(@"SpaceCreate");
            }
        }];
        
    } failed:failed];
}
- (void) spaceDelete:(HWRemoteSuccessBlock)succeed
              failed:(HWRemoteFailedBlock)failed{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *entity = [[RequestEntity alloc] init];
        entity.spaceId = @(self.teamId.integerValue);
        ServiceType serviceCmd = ServiceSpaceDelete;
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:entity serviceType:ServiceSpaceDelete completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                SERVICE_FAILED(@"SpaceDelete");
            }
            else{
                [self remove];
                SERVICE_SUCCEED(@"SpaceDelete");
            }
        }];
        
    } failed:failed];
}

- (void)spaceMemberAdd:(NSDictionary *)memberInfo
               succeed:(HWRemoteSuccessBlock)succeed
                failed:(HWRemoteFailedBlock)failed{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.remoteManager ensureCloudLogin:^{
        RequestEntity *entity = [[RequestEntity alloc] init];
        entity.spaceMemberUserId = [memberInfo objectForKey:@"userId"];
        entity.spaceTeamRole = @"member";
        entity.spaceRole = @"editor";
        entity.spaceId = self.teamId;
        [delegate.remoteManager.httpService setBaseURL:delegate.remoteManager.httpService.ufm];
        [delegate.remoteManager.httpService doEntityRequst:entity serviceType:ServiceSpaceMemberAdd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (!error) {
                SERVICE_SUCCEED(@"spaceMemberAdd");
            }
            else{
                SERVICE_FAILED(@"spaceMemberAdd");
            }
        }];
    } failed:failed];
}
- (void)spaceMemberDelete:(NSDictionary *)memberInfo
                  succeed:(HWRemoteSuccessBlock)succeed
                   failed:(HWRemoteFailedBlock)failed{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.remoteManager ensureCloudLogin:^{
        RequestEntity *entity = [[RequestEntity alloc] init];
        entity.spaceMemberId = [memberInfo objectForKey:@"memberId"];
        entity.spaceId = @(self.teamId.integerValue);
        [delegate.remoteManager.httpService setBaseURL:delegate.remoteManager.httpService.ufm];
        [delegate.remoteManager.httpService doEntityRequst:entity serviceType:ServiceSpaceMemberDelete completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (!error) {
                SERVICE_SUCCEED(@"spaceMemberDelete");
            }
            else{
                SERVICE_FAILED(@"spaceMemberDelete");
            }
        }];
    } failed:failed];
}

- (void)spaceMemberlist:(NSDictionary *)memberInfo
                succeed:(HWRemoteSuccessBlock)succeed
                 failed:(HWRemoteFailedBlock)failed{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.remoteManager ensureCloudLogin:^{
        RequestEntity *entity = [[RequestEntity alloc] init];
        entity.spaceId = @(self.teamId.integerValue);
        entity.listLimit = @(20);
        entity.listOffset = @(0);
        [delegate.remoteManager.httpService setBaseURL:delegate.remoteManager.httpService.ufm];
        [delegate.remoteManager.httpService doEntityRequst:entity serviceType:ServiceSpaceMemberList completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (!error) {
                
                SERVICE_SUCCEED(@"spaceMemberList");
            }
            else{
                SERVICE_FAILED(@"spaceMemberList");
            }
        }];
    } failed:failed];
}

- (void)spaceMemberUpdate:(NSDictionary *)memberInfo
                  succeed:(HWRemoteSuccessBlock)succeed
                   failed:(HWRemoteFailedBlock)failed{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate.remoteManager ensureCloudLogin:^{
        RequestEntity *entity = [[RequestEntity alloc] init];
        entity.spaceId = @(self.teamId.integerValue);
        entity.spaceMemberId = [memberInfo objectForKey:@"memberId"];
        entity.spaceRole = [memberInfo objectForKey:@"role"];
        entity.spaceTeamRole = [memberInfo objectForKey:@"teamRole"];
        [delegate.remoteManager.httpService setBaseURL:delegate.remoteManager.httpService.ufm];
        [delegate.remoteManager.httpService doEntityRequst:entity serviceType:ServiceSpaceMemberInfoUpdate completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (!error) {
                
                SERVICE_SUCCEED(@"spaceMemberList");
            }
            else{
                SERVICE_FAILED(@"spaceMemberList");
            }
        }];
    } failed:failed];
}

+ (NSArray*) sortRemoteItemsFromResponse:(NSArray*) response {
    NSMutableArray *remoteItems = [[NSMutableArray alloc] initWithArray:response];
    if ([remoteItems count] == 0) {
        return (NSArray*)remoteItems;
    }
    [remoteItems sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        NSDictionary* dic1 = obj1;
        NSDictionary* dic2 = obj2;
        
        NSString* iNode1 = [[dic1 objectForKey:@"teamId"] stringValue];
        NSString* iNode2 = [[dic2 objectForKey:@"teamId"] stringValue];
        
        return [iNode1 compare:iNode2];
    }];
    return (NSArray*)remoteItems;
}

+ (void) compareWithTeamInfo:(NSArray*)remoteItems localItems:(NSArray*)localItems context:(NSManagedObjectContext*)ctx {
    int rowOfLocal = 0 , rowOfRemote = 0;
    while (rowOfLocal < localItems.count && rowOfRemote < remoteItems.count) {
        if ([(TeamSpace*)localItems[rowOfLocal] sameWithTeamInfo:remoteItems[rowOfRemote]]) {
            rowOfLocal++;rowOfRemote++;
            continue;
        }
        int rowOfRemoteNext = rowOfRemote;
        for (; rowOfRemoteNext < remoteItems.count; ++rowOfRemoteNext) {
            if ([(TeamSpace*)localItems[rowOfLocal] sameWithTeamInfo:remoteItems[rowOfRemoteNext]]) {
                break;
            }
        }
        if (rowOfRemoteNext < remoteItems.count) {
            for (int rowOfRemoteAdd = rowOfLocal; rowOfRemoteAdd < rowOfRemoteNext; ++rowOfRemoteAdd) {
                [TeamSpace insertWithTeamInfo:remoteItems[rowOfRemoteAdd] context:ctx];
            }
            rowOfRemote = rowOfRemoteNext+1;
            rowOfLocal ++;
        } else {
            TeamSpace *teamSpace = (TeamSpace*)localItems[rowOfLocal];
            [teamSpace remove];
            rowOfLocal++;
        }
    }
    while (rowOfLocal < localItems.count) {
        TeamSpace *teamSpace = (TeamSpace*)localItems[rowOfLocal];
        [teamSpace remove];
        rowOfLocal++;
    }
    while (rowOfRemote < remoteItems.count) {
        [TeamSpace insertWithTeamInfo:remoteItems[rowOfRemote] context:ctx];
        rowOfRemote++;
    }
}

- (void)getMemberInfoWithId:(NSString *)userId succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    if (!userId) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc] init];
        requestEntity.spaceId = [NSNumber numberWithLongLong:[self.teamId longLongValue]];
        requestEntity.spaceMemberId = [NSNumber numberWithLongLong:[userId longLongValue]];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceSpaceMemberInfo completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                SERVICE_FAILED(@"Space Member Info");
            } else {
                SERVICE_SUCCEED(@"Space Member Info");
            }
        }];
    } failed:failed];
}

@end
