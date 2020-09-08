//
//  FileOperation.m
//  HttpService
//
//  Created by cse on 14-3-29.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import "FileOperation.h"

@interface FileOperation ()

@property (nonatomic, strong) NSMutableDictionary *dic;

@end

@implementation FileOperation

-(instancetype)init
{
    self = [super init];
    if (self) {
        _dic = [[NSMutableDictionary alloc]init];
    }
    return self;
}
-(void)doEntityRequest:(AFHTTPRequestOperationManager *)manage
         requestEntity:(RequestEntity *)requestEntity
           serviceType:(ServiceType)serviceType
     completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    switch (serviceType) {
        case ServiceFileInfo:
        {
            [self FileContent:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFileDelete:
        {
            [self FileDelete:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFileRename:
        {
            [self FileRename:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
            
        case ServiceFileCopy:
        {
            [self FileCopy:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFileMove:
        {
            [self FileMove:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFileVersionList:
        {
            [self FileVersions:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFileThumbnail:
        {
            [self FileThumbnail:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFileSearch:
        {
            [self FileSearch:manage requestEntity:requestEntity serviceType:serviceType completionHandler:completionHandler];
        }
            
        default:
            break;
    }
    
}

-(void)FileContent:(AFHTTPRequestOperationManager*)manage
     requestEntity:(RequestEntity*)entity
       serviceType:(ServiceType)serviceType
 completionHandler:(void (^)(NSURLResponse *, id, NSError *,ServiceType, ErrorType))completionHandler
{
    NSString *URLString = [NSString stringWithFormat:@"api/v2/files/%@/%@",entity.objectOwnerId,entity.objectId];
    [manage GET:URLString parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)FileDelete:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void (^)(NSURLResponse *, id, NSError *,ServiceType, ErrorType))completionHandler
{
    NSString *URLString = [NSString stringWithFormat:@"api/v2/files/%@/%@",entity.objectOwnerId,entity.objectId];
    [manage DELETE:URLString parameters:nil
           success:^(AFHTTPRequestOperation *operation, id responseObject) {
               completionHandler(operation.response,responseObject,nil,serviceType,0);
           } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
               completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
           }];
}

-(void)FileVersions:(AFHTTPRequestOperationManager*)manage
      requestEntity:(RequestEntity*)entity
        serviceType:(ServiceType)serviceType
  completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    if (!entity.listOffset) {
        entity.listOffset = [NSNumber numberWithLong:0];
    }
    if (!entity.listLimit) {
        entity.listLimit = [NSNumber numberWithLong:100];
    }
    NSString *URLString = [NSString stringWithFormat:@"api/v2/files/%@/%@/versions?offset=%ld&limit=%ld",entity.objectOwnerId,entity.objectId,[entity.listOffset longValue],[entity.listLimit longValue]];
    [manage GET:URLString parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)FileCopy:(AFHTTPRequestOperationManager*)manage
  requestEntity:(RequestEntity*)entity
    serviceType:(ServiceType)serviceType
completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.objectDestOwnerId forKey:@"destOwnerId"];
    [_dic setObject:entity.objectDestParentId forKey:@"destParent"];
    [_dic setObject:entity.objectAutoRename forKey:@"autoRename"];
    NSString *URLString=[NSString stringWithFormat:@"api/v2/files/%@/%@/copy",entity.objectOwnerId,entity.objectId];
    [manage PUT:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}


-(void)FileMove:(AFHTTPRequestOperationManager*)manage
  requestEntity:(RequestEntity*)entity
    serviceType:(ServiceType)serviceType
completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.objectDestOwnerId forKey:@"destOwnerId"];
    [_dic setObject:entity.objectDestParentId forKey:@"destParent"];
    [_dic setObject:entity.objectAutoRename forKey:@"autoRename"];
    NSString* URLString = [NSString stringWithFormat:@"api/v2/files/%@/%@/move",entity.objectOwnerId,entity.objectId];
    [manage PUT:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}


-(void)FileThumbnail:(AFHTTPRequestOperationManager*)manage
       requestEntity:(RequestEntity*)entity
         serviceType:(ServiceType)serviceType
   completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    NSString *URLString=[NSString stringWithFormat:@"api/v2/files/%@/%@/thumbUrl?height=%ld&wight=%ld",entity.objectOwnerId,entity.objectId,[entity.thumbnailHeight longValue],[entity.thumbnailWidth longValue]];
    [manage GET:URLString parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}


-(void)FileRename:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.objectNewName forKey:@"name"];
    NSString *URLString=[NSString stringWithFormat:@"api/v2/files/%@/%@",entity.objectOwnerId,entity.objectId];
    [manage PUT:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

-(void)FileSearch:(AFHTTPRequestOperationManager*)manage
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
    [_dic setObject:entity.objectSearchWord forKey:@"name"];
    
    if (entity.thumbnailWidth && entity.thumbnailHeight) {
        NSMutableDictionary *thumbnail = [[NSMutableDictionary alloc]initWithObjectsAndKeys:entity.thumbnailHeight,@"height",entity.thumbnailWidth,@"width",nil];
        NSMutableArray *thumbnailArray = [[NSMutableArray alloc]initWithObjects:thumbnail, nil];
        [_dic setObject:thumbnailArray forKey:@"thumbnail"];
    }

    NSString *URLString=[NSString stringWithFormat:@"api/v2/nodes/%@/search",entity.objectOwnerId];
    [manage.requestSerializer setValue:[NSString stringWithFormat:@"%@",[NSDate date]] forHTTPHeaderField:@"Date"];
    [manage POST:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}
@end
