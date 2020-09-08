//
//  ShareOperation.m
//  HttpService
//
//  Created by cse on 14-4-25.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import "ShareOperation.h"
#import "AppDelegate.h"

@interface ShareOperation ()

@property (nonatomic, strong)NSMutableDictionary *dic;

@end

@implementation ShareOperation

-(instancetype)init
{
    self = [super init];
    if (self) {
        _dic = [[NSMutableDictionary alloc]init];
    }
    return self;
}

-(void)doEntityRequst:(AFHTTPRequestOperationManager *)manage
        requestEntity:(RequestEntity*)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    switch (serviceType) {
        case ServiceShareAdd:
        {
            [self ShareAdd:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceShareReceiveList:
        {
            [self ShareReceiveList:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceShareSendList:
        {
            [self ShareSendList:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceShareUserList:
        {
            [self ShareUserList:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceShareDelete:
        {
            [self ShareDelete:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceLinksCreate:
        {
            [self LinksCreate:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceLinksRefresh:
        {
            [self LinksRefresh:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceLinksInfoList:
        {
            [self LinksList:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceLinksDelete:
        {
            [self LinksDelete:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceLinkInfo:
        {
            [self LinkInfo:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceLinksObject:
        {
            [self LinksObject:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
   
        default:
            break;
    }
}

-(void)ShareAdd:(AFHTTPRequestOperationManager*)manage
  requestEntity:(RequestEntity*)entity
    serviceType:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *,id, NSError *,ServiceType, ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    NSMutableDictionary *shareUser = [[NSMutableDictionary alloc] init];
    [shareUser setObject:entity.shareRecevierId forKey:@"id"];
    if (entity.shareRecevierType) {
        [shareUser setObject:entity.shareRecevierType forKey:@"type"];
    }
    [_dic setObject:shareUser forKey:@"sharedUser"];
    
    NSString* URLString = [NSString stringWithFormat:@"api/v2/shareships/%@/%@",entity.objectOwnerId,entity.objectId];
    [manage PUT:URLString parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)ShareReceiveList:(AFHTTPRequestOperationManager*)manage
          requestEntity:(RequestEntity*)entity
            serviceType:(ServiceType)serviceType
      completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
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
        NSMutableArray *order = [[NSMutableArray alloc]initWithObjects:orderItem, nil];
        [_dic setObject:order forKey:@"order"];
    }
    
    if (entity.thumbnailHeight && entity.thumbnailWidth) {
        NSMutableDictionary *thumbnail = [[NSMutableDictionary alloc]initWithObjectsAndKeys:entity.thumbnailHeight,@"height",entity.thumbnailWidth,@"width",nil];
        NSMutableArray *thumbnailArray = [[NSMutableArray alloc]initWithObjects:thumbnail, nil];
        [_dic setObject:thumbnailArray forKey:@"thumbnail"];
    }
    
    if (entity.listKeyword) {
        [_dic setObject:entity.listKeyword  forKey:@"keyword"];
    }
    
    NSString *URLString = [NSString stringWithFormat:@"api/v2/shares/received"];
    [manage POST:URLString parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}


-(void)ShareSendList:(AFHTTPRequestOperationManager*)manage
       requestEntity:(RequestEntity*)entity
         serviceType:(ServiceType)serviceType
   completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
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
        NSMutableDictionary *thumbnail = [[NSMutableDictionary alloc] initWithObjectsAndKeys:entity.thumbnailHeight,@"height",entity.thumbnailWidth,@"width",nil];
        NSMutableArray *thumbnailArray = [[NSMutableArray alloc] initWithObjects:thumbnail, nil];
        [_dic setObject:thumbnailArray forKey:@"thumbnail"];
    }
    if (entity.listKeyword) {
        [_dic setObject:entity.listKeyword forKey:@"keyword"];
    }
    
    NSString* URLString = [NSString stringWithFormat:@"api/v2/shares/distributed"];
    [manage POST:URLString parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}


-(void)ShareUserList:(AFHTTPRequestOperationManager*)manage
       requestEntity:(RequestEntity*)entity
         serviceType:(ServiceType)serviceType
   completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    if (!entity.listOffset) {
        entity.listOffset = [NSNumber numberWithLong:0];
    }
    if (!entity.listLimit) {
        entity.listLimit = [NSNumber numberWithLong:100];
    }
    NSString* URLString = [NSString stringWithFormat:@"api/v2/shareships/%@/%@?offset=%ld&limit=%ld",entity.objectOwnerId,entity.objectId,[entity.listOffset longValue],[entity.listLimit longValue]];
    [manage GET:URLString parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}



-(void)ShareDelete:(AFHTTPRequestOperationManager*)manage
     requestEntity:(RequestEntity*)entity
       serviceType:(ServiceType)serviceType
 completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    NSString *url;
    if (entity.shareRecevierId) {
        if (!entity.shareRecevierType) {
            [entity setShareRecevierType:@"user"];
        }
        url = [[NSString alloc]initWithFormat:@"api/v2/shareships/%@/%@?userId=%@&type=%@",entity.objectOwnerId,entity.objectId,entity.shareRecevierId,entity.shareRecevierType];
    } else {
        url = [[NSString alloc]initWithFormat:@"api/v2/shareships/%@/%@",entity.objectOwnerId,entity.objectId];
    }
    [manage DELETE:url parameters:nil
           success:^(AFHTTPRequestOperation *operation, id responseObject) {
               completionHandler(operation.response,responseObject,nil,serviceType,0);
           } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
               completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
           }];
}



-(void)LinksCreate:(AFHTTPRequestOperationManager*)manage
     requestEntity:(RequestEntity*)entity
       serviceType:(ServiceType)serviceType
 completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    if (!entity.linksAccess) {
        entity.linksAccess = @"object";
    }
    [_dic setObject:entity.linksAccess forKey:@"access"];
    if ([entity.linksAccess isEqualToString:@"object"]) {
        if (!entity.linksAccessCodeMode) {
            entity.linksAccessCodeMode = @"static";
        }
        [_dic setObject:entity.linksAccessCodeMode forKey:@"accessCodeMode"];
        if ([entity.linksAccessCodeMode isEqualToString:@"static"]) {
            if (entity.linksPlainAccessCode) {
                [_dic setObject:entity.linksPlainAccessCode forKey:@"plainAccessCode"];
            }
        }
        if ([entity.linksAccessCodeMode isEqualToString:@"mail"]) {
            NSDictionary *identities = [[NSDictionary alloc] initWithObjectsAndKeys:entity.linksAccessMail,@"identity", nil];
            [_dic setObject:identities forKey:@"identities"];
        }
    }
    if (!entity.linksRole) {
        entity.linksRole = @"viewer";
    }
    [_dic setObject:entity.linksRole forKey:@"role"];
    if (entity.linksEffectiveAt) {
        [_dic setObject:entity.linksEffectiveAt forKey:@"effectiveAt"];
    }
    if (entity.linksEffectiveAt && entity.linksExpireAt) {
        [_dic setObject:entity.linksEffectiveAt forKey:@"effectiveAt"];
        [_dic setObject:entity.linksExpireAt forKey:@"expireAt"];
    }
    
    NSString* url = [[NSString alloc]initWithFormat:@"api/v2/links/%@/%@",entity.objectOwnerId ,entity.objectId];
    [manage POST:url parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}



-(void)LinksList:(AFHTTPRequestOperationManager*)manage
   requestEntity:(RequestEntity*)entity
     serviceType:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    NSString* url = [[NSString alloc]initWithFormat:@"api/v2/nodes/%@/%@/links",entity.objectOwnerId ,entity.objectId];
    [manage GET:url parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)LinksRefresh:(AFHTTPRequestOperationManager*)manage
      requestEntity:(RequestEntity*)entity
        serviceType:(ServiceType)serviceType
  completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    if (!entity.linksAccess) {
        entity.linksAccess = @"object";
    }
    if ([entity.linksAccess isEqualToString:@"object"]) {
        if (!entity.linksAccessCodeMode) {
            entity.linksAccessCodeMode = @"static";
        }
        [_dic setObject:entity.linksAccessCodeMode forKey:@"accessCodeMode"];
        if ([entity.linksAccessCodeMode isEqualToString:@"static"]) {
            if (entity.linksPlainAccessCode) {
                [_dic setObject:entity.linksPlainAccessCode forKey:@"plainAccessCode"];
            }
        }
        if ([entity.linksAccessCodeMode isEqualToString:@"mail"]) {
            NSDictionary *identities = [[NSDictionary alloc] initWithObjectsAndKeys:entity.linksAccessMail,@"identity", nil];
            [_dic setObject:identities forKey:@"identities"];
        }
    }
    if (entity.linksRole) {
        [_dic setObject:entity.linksRole forKey:@"role"];
    }
    if (entity.linksEffectiveAt) {
        [_dic setObject:entity.linksEffectiveAt forKey:@"effectiveAt"];
    }
    if (entity.linksEffectiveAt && entity.linksExpireAt) {
        [_dic setObject:entity.linksEffectiveAt forKey:@"effectiveAt"];
        [_dic setObject:entity.linksExpireAt forKey:@"expireAt"];
    }
    NSString* url = [[NSString alloc]initWithFormat:@"api/v2/links/%@/%@?linkCode=%@",entity.objectOwnerId,entity.objectId,entity.linksId];
    [manage PUT:url parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)LinkInfo:(AFHTTPRequestOperationManager*)manage
  requestEntity:(RequestEntity*)entity
    serviceType:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    NSString* url = [[NSString alloc]initWithFormat:@"api/v2/links/%@/%@?linkCode=%@",entity.objectOwnerId ,entity.objectId,entity.linksId];
    [manage GET:url parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)LinksDelete:(AFHTTPRequestOperationManager*)manage
     requestEntity:(RequestEntity*)entity
       serviceType:(ServiceType)serviceType
 completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    NSString* url = [[NSString alloc]initWithFormat:@"api/v2/links/%@/%@?linkCode=%@",entity.objectOwnerId,entity.objectId,entity.linksId];
    [manage DELETE:url parameters:nil
           success:^(AFHTTPRequestOperation *operation, id responseObject) {
               completionHandler(operation.response,responseObject,nil,serviceType,0);
           } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
               completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
           }];
}

-(void)LinksObject:(AFHTTPRequestOperationManager*)manage
     requestEntity:(RequestEntity*)entity
       serviceType:(ServiceType)serviceType
 completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler{
    [manage.requestSerializer setValue:[NSString stringWithFormat:@"link,%@",entity.linksId] forHTTPHeaderField:@"Authorization"];
    [manage GET:@"api/v2/links/node" parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [manage.requestSerializer removeHTTPHeaderFieldForKey:@"Authorization"];
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [manage.requestSerializer removeHTTPHeaderFieldForKey:@"Authorization"];
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

@end
