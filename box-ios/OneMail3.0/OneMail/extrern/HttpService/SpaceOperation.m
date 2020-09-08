//
//  SpaceOperation.m
//  OneBox
//
//  Created by cse on 15-2-13.
//  Copyright (c) 2015年 www.huawei.com. All rights reserved.
//

#import "SpaceOperation.h"

@interface SpaceOperation ()

@property (nonatomic, strong)NSMutableDictionary *dic;

@end

@implementation SpaceOperation

-(instancetype)init
{
    self = [super init];
    if (self) {
        _dic = [[NSMutableDictionary alloc] init];
    }
    return self;
}

-(void)doRequestbyEntity:(AFHTTPRequestOperationManager *)manage
           requestEntity:(RequestEntity *)entity
             serviceType:(ServiceType)serviceType
       completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    switch (serviceType) {
        case ServiceSpaceListAll:
            [self SpaceListAll:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceList:
            [self SpaceList:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceCreate:
            [self SpaceCreate:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceDelete:
            [self SpaceDelete:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceInfo:
            [self SpaceInfo:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceUpdate:
            [self SpaceInfoUpdate:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceMemberAdd:
            [self SpaceMemberAdd:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceMemberDelete:
            [self SpaceMemberDelete:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceMemberInfo:
            [self SpaceMemberInfo:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceMemberList:
            [self SpaceMemberList:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        case ServiceSpaceMemberInfoUpdate:
            [self SpaceMemberUpdate:manage requestEntity:entity service:serviceType completionHandler:completionHandler];
            break;
            
        default:
            break;
    }
    
}

-(void)SpaceListAll:(AFHTTPRequestOperationManager *)manage
      requestEntity:(RequestEntity *)entity
            service:(ServiceType)serviceType
  completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    if (entity.listKeyword) {
        [_dic setObject:entity.listKeyword forKey:@"keyword"];
    }
    if (entity.listOffset) {
        [_dic setObject:entity.listOffset forKey:@"offset"];
    }
    if (entity.listLimit) {
        [_dic setObject:entity.listLimit forKey:@"limit"];
    }
    if (entity.listField) {
        NSMutableDictionary *orderItem = [[NSMutableDictionary alloc]initWithObjectsAndKeys:entity.listField,@"field", nil];
        if (entity.listDirection) {
            [orderItem setObject:entity.listDirection forKey:@"direction"];
        }
        NSMutableArray *order = [[NSMutableArray alloc] initWithObjects:orderItem, nil];
        [_dic setObject:order forKey:@"order"];
    }
    
    [manage POST:@"api/v2/teamspaces/all" parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)SpaceList:(AFHTTPRequestOperationManager *)manage
   requestEntity:(RequestEntity *)entity
         service:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.userId forKey:@"userId"];
    if (entity.listOffset) {
        [_dic setObject:entity.listOffset forKey:@"offset"];
    }
    if (entity.listLimit) {
        [_dic setObject:entity.listLimit forKey:@"limit"];
    }
    if (entity.listField) {
        NSMutableDictionary *orderItem = [[NSMutableDictionary alloc] initWithObjectsAndKeys:entity.listField,@"field", nil];
        if (entity.listDirection) {
            [orderItem setObject:entity.listDirection forKey:@"direction"];
        }
        NSMutableArray *order = [[NSMutableArray alloc] initWithObjects:orderItem, nil];
        [_dic setObject:order forKey:@"order"];
    }
    
    [manage POST:@"api/v2/teamspaces/items" parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)SpaceCreate:(AFHTTPRequestOperationManager *)manage
     requestEntity:(RequestEntity *)entity
           service:(ServiceType)serviceType
 completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.spaceName forKey:@"name"];
    if (entity.spaceDescription) {
        [_dic setObject:entity.spaceDescription forKey:@"description"];
    }
    if (entity.spaceQuota) {
        [_dic setObject:entity.spaceQuota forKey:@"spaceQuota"];
    }
    if (entity.spaceStatus) {
        [_dic setObject:entity.spaceStatus forKey:@"status"];
    }
    if (entity.spaceMaxVersions) {
        [_dic setObject:entity.spaceMaxVersions forKey:@"maxVersions"];
    }
    if (entity.spaceMaxMembers) {
        [_dic setObject:entity.spaceMaxMembers forKey:@"maxMembers"];
    }
    
    [manage POST:@"api/v2/teamspaces" parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)SpaceInfoUpdate:(AFHTTPRequestOperationManager *)manage
         requestEntity:(RequestEntity *)entity
               service:(ServiceType)serviceType
     completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.spaceName forKey:@"name"];
    if (entity.spaceDescription) {
        [_dic setObject:entity.spaceDescription forKey:@"description"];
    }
    if (entity.spaceQuota) {
        [_dic setObject:entity.spaceQuota forKey:@"spaceQuota"];
    }
    if (entity.spaceStatus) {
        [_dic setObject:entity.spaceStatus forKey:@"status"];
    }
    if (entity.spaceMaxVersions) {
        [_dic setObject:entity.spaceMaxVersions forKey:@"maxVersions"];
    }
    if (entity.spaceMaxMembers) {
        [_dic setObject:entity.spaceMaxMembers forKey:@"maxMembers"];
    }
    
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@",entity.spaceId];
    
    [manage PUT:url parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)SpaceInfo:(AFHTTPRequestOperationManager *)manage
   requestEntity:(RequestEntity *)entity
         service:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@",entity.spaceId];
    [manage GET:url parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)SpaceDelete:(AFHTTPRequestOperationManager *)manage
     requestEntity:(RequestEntity *)entity
           service:(ServiceType)serviceType
 completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@",entity.spaceId];
    [manage DELETE:url parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)SpaceMemberAdd:(AFHTTPRequestOperationManager *)manage
        requestEntity:(RequestEntity *)entity
              service:(ServiceType)serviceType
    completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.spaceTeamRole forKey:@"teamRole"];
    
    NSMutableDictionary *member = [[NSMutableDictionary alloc] init];
    [member setObject:entity.spaceMemberUserId forKey:@"id"];
    if (entity.spaceMemberType) {
        [member setObject:entity.spaceMemberType forKey:@"type"];
    }
    [_dic setObject:member forKey:@"member"];
    
    if (entity.spaceRole) {
        [_dic setObject:entity.spaceRole forKey:@"role"];
    }
    
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@/memberships",entity.spaceId];
    [manage POST:url parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         }
         failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)SpaceMemberInfo:(AFHTTPRequestOperationManager *)manage
         requestEntity:(RequestEntity *)entity
               service:(ServiceType)serviceType
     completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@/memberships/%@",entity.spaceId,entity.spaceMemberId];
    [manage GET:url parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)SpaceMemberUpdate:(AFHTTPRequestOperationManager *)manage
           requestEntity:(RequestEntity *)entity
                 service:(ServiceType)serviceType
       completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    if (entity.spaceTeamRole) {
        [_dic setObject:entity.spaceTeamRole forKey:@"teamRole"];
    }
    if (entity.spaceRole) {
        [_dic setObject:entity.spaceRole forKey:@"role"];
    }
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@/memberships/%@",entity.spaceId,entity.spaceMemberId];
    [manage PUT:url parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)SpaceMemberList:(AFHTTPRequestOperationManager *)manage
         requestEntity:(RequestEntity *)entity
               service:(ServiceType)serviceType
     completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    if (entity.spaceTeamRole) {
        [_dic setObject:entity.spaceTeamRole forKey:@"teamRole"];
    }
    if (entity.listKeyword) {
        [_dic setObject:entity.listKeyword forKey:@"keyword"];
    }
    if (entity.listOffset) {
        [_dic setObject:entity.listOffset forKey:@"offset"];
    }
    if (entity.listLimit) {
        [_dic setObject:entity.listLimit forKey:@"limit"];
    }
    if (entity.listField) {
        NSMutableDictionary *orderItem = [[NSMutableDictionary alloc] initWithObjectsAndKeys:entity.listField,@"field", nil];
        if (entity.listDirection) {
            [orderItem setObject:entity.listDirection forKey:@"direction"];
        }
        NSMutableArray *order = [[NSMutableArray alloc] initWithObjects:orderItem, nil];
        [_dic setObject:order forKey:@"order"];
    }
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@/memberships/items",entity.spaceId];
    [manage POST:url parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)SpaceMemberDelete:(AFHTTPRequestOperationManager *)manage
           requestEntity:(RequestEntity *)entity
                 service:(ServiceType)serviceType
       completionHandler:(void(^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    NSString *url = [NSString stringWithFormat:@"api/v2/teamspaces/%@/memberships/%@",entity.spaceId,entity.spaceMemberId];
    [manage DELETE:url parameters:nil
           success:^(AFHTTPRequestOperation *operation, id responseObject) {
               completionHandler(operation.response,responseObject,nil,serviceType,0);
           } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
               completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
           }];
}

-(void)setDateHeader:(AFHTTPRequestOperationManager*)manage {
    NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"EEE, dd MMM YYYY HH: mm: ss"];
    [formatter setTimeZone:[NSTimeZone timeZoneWithName:@"GMT"]];
    NSString *time = [formatter stringFromDate:[NSDate date]];
    time = [time stringByAppendingString:@" GMT"];
    [manage.requestSerializer setValue:time forHTTPHeaderField:@"Date"];
}
@end
