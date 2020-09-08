//
//  FolderOperation.m
//  HttpService
//
//  Created by cse on 14-3-29.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import "FolderOperation.h"

@interface FolderOperation ()

@property (nonatomic, strong) NSMutableDictionary *dic;

@end

@implementation FolderOperation

-(instancetype)init
{
    self = [super init];
    if (self) {
        _dic = [[NSMutableDictionary alloc]init];
    }
    return self;
}

-(void)doEntityRequst:(AFHTTPRequestOperationManager *)manage
        requestEntity:(RequestEntity*)requestEntity
          serviceType:(ServiceType)serviceType
    completionHandler:(void (^)(NSURLResponse *, id, NSError *,ServiceType, ErrorType))completionHandler
{
    switch (serviceType) {
        case ServiceFolderList:
        {
            [self FolderList:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFolderCreate:
        {
            [self FolderCreate:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFolderInfo:
        {
            [self FolderGetInfo:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFolderRename:
        {
            [self FolderRename:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFolderDelete:
        {
            [self FolderDelete:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFolderCopy:
        {
            [self FolderCopy:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFolderMove:
        {
            [self FolderMove:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        default:
            break;
    }
}

-(void)FolderList:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
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
    
    if (entity.thumbnailHeight && entity.thumbnailWidth) {
        NSMutableDictionary *thumbnail = [[NSMutableDictionary alloc]initWithObjectsAndKeys:entity.thumbnailHeight,@"height",entity.thumbnailWidth,@"width",nil];
        NSMutableArray *thumbnailArray = [[NSMutableArray alloc]initWithObjects:thumbnail, nil];
        [_dic setObject:thumbnailArray forKey:@"thumbnail"];
    }
    
    NSString* URLString=[NSString stringWithFormat:@"api/v2/folders/%@/%@/items",entity.objectOwnerId,entity.objectId];
    [manage POST:URLString parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)FolderCreate:(AFHTTPRequestOperationManager*)manage
      requestEntity:(RequestEntity*)entity
        serviceType:(ServiceType)serviceType
  completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.objectName forKey:@"name"];
    [_dic setObject:entity.objectParentId forKey:@"parent"];
    if (entity.objectCreateAt) {
        [_dic setObject:entity.objectCreateAt forKey:@"contentCreatedAt"];
    }
    if (entity.objectModifiedAt) {
        [_dic setObject:entity.objectModifiedAt forKey:@"contentModifiedAt"];
    }
    NSString* URLString = [NSString stringWithFormat:@"api/v2/folders/%@/",entity.objectOwnerId];
    [manage POST:URLString parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)FolderGetInfo:(AFHTTPRequestOperationManager*)manage
       requestEntity:(RequestEntity*)entity
         serviceType:(ServiceType)serviceType
   completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    NSString* URLString=[NSString stringWithFormat:@"api/v2/folders/%@/%@/",entity.objectOwnerId,entity.objectId];
    [manage GET:URLString parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)FolderRename:(AFHTTPRequestOperationManager*)manage
      requestEntity:(RequestEntity*)entity
        serviceType:(ServiceType)serviceType
  completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.objectNewName forKey:@"name"];
    NSString* URLString=[NSString stringWithFormat:@"api/v2/folders/%@/%@",entity.objectOwnerId,entity.objectId];
    [manage PUT:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}



-(void)FolderDelete:(AFHTTPRequestOperationManager*)manage
      requestEntity:(RequestEntity*)entity
        serviceType:(ServiceType)serviceType
  completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    NSString* URLString=[NSString stringWithFormat:@"api/v2/folders/%@/%@",entity.objectOwnerId,entity.objectId];
    [manage DELETE:URLString parameters:nil
           success:^(AFHTTPRequestOperation *operation, id responseObject) {
               completionHandler(operation.response,responseObject,nil,serviceType,0);
           } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
               completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
           }];
}

-(void)FolderCopy:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.objectDestOwnerId forKey:@"destOwnerId"];
    [_dic setObject:entity.objectDestParentId forKey:@"destParent"];
    [_dic setObject:entity.objectAutoRename forKey:@"autoRename"];
    NSString *URLString = [NSString stringWithFormat:@"api/v2/folders/%@/%@/copy",entity.objectOwnerId,entity.objectId];
    [manage PUT:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)FolderMove:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.objectDestOwnerId forKey:@"destOwnerId"];
    [_dic setObject:entity.objectDestParentId forKey:@"destParent"];
    [_dic setObject:entity.objectAutoRename forKey:@"autoRename"];
    NSString* URLString = [NSString stringWithFormat:@"api/v2/folders/%@/%@/move",entity.objectOwnerId,entity.objectId];
    [manage PUT:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}
@end
