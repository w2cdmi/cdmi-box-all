//
//  UserOperation.m
//  HttpService
//
//  Created by cse on 14-3-29.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import "UserOperation.h"

@interface UserOperation ()

@property (nonatomic, strong) NSMutableDictionary *dic;

@end

@implementation UserOperation

-(instancetype)init
{
    self = [super init];
    if (self) {
        _dic=[[NSMutableDictionary alloc]init];
    }
    return self;
}

-(void)doRequestbyEntity:(AFHTTPRequestOperationManager *)manage
           requestEntity:(RequestEntity *)entity
             serviceType:(ServiceType)serviceType
       completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    switch (serviceType) {
        case ServiceUserLogin:
        {
            [self UserLogin:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserServerAddress:
        {
            [self UserServerAddress:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserTokenRefresh:
        {
            [self UserRefreshToken:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserLogout:
        {
            [self UserLogout:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserSearch:
        {
            [self UserSearch:manage requestEntity:entity serviceType:ServiceUserSearch completionHandler:completionHandler];
        }
            break;
        case ServiceUserCreate:
        {
            [self UserCreate:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserUpdate:
        {

        }
            break;
        case ServiceUserInfo:
        {
            [self UserInfo:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserDelete:
        {

        }
            break;
        case ServiceUserSendEmail:
        {
            [self UserSendEmail:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserEmailMessageSet:
        {
            [self SetMailMessage:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserEmailMessageGet:
        {
            [self GetMailMessage:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserSystermLinksOption:
        {
            [self GetShareLinksOption:manage RequestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserEmailConfig:
        {
            [self UserRequestMailConfig:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceClientCheckCode:
        {
            [self GetCheckCode:manage requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceClientInfo:
        {
            [self GetClientInfo:manage requesetEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserDeclarationContent:
        {
            [self GetDeclarationContent:manage requesetEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserDeclarationStatus:
        {
            [self PutDeclarationStatus:manage requesetEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceUserHeadIcon:
        {
            [self getUserHeadIcon:manage requesetEntity:entity serviceType:ServiceUserHeadIcon completionHandler:completionHandler];
        }
            break;
        default:
            break;
    }
}

-(void)GetShareLinksOption:(AFHTTPRequestOperationManager *)manage
             RequestEntity:(RequestEntity*)entity
               serviceType:(ServiceType)serviceType
         completionHandler:(void(^)(NSURLResponse *,id,NSError *,ServiceType,ErrorType))completionHandler
{
    NSString *URLString = [NSString stringWithFormat:@"api/v2/config?option=%@",@"linkAccessKeyRule"];
    [manage GET:URLString parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}


-(void)UserLogin:(AFHTTPRequestOperationManager*)manage
   requestEntity:(RequestEntity*)entity
     serviceType:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *,id , NSError *,ServiceType ,ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.userLoginName forKey:@"loginName"];
    [_dic setObject:entity.userPassword forKey:@"password"];
    if (entity.userDomain) {
        [_dic setObject:entity.userDomain forKey:@"domain"];
    }
    if (entity.userAppId) {
        [_dic setObject:entity.userAppId forKey:@"appId"];
    }

    [manage.requestSerializer setValue:entity.x_device_sn forHTTPHeaderField:@"x-device-sn"];
    [manage.requestSerializer setValue:entity.x_device_os forHTTPHeaderField:@"x-device-os"];
    [manage.requestSerializer setValue:entity.x_device_name forHTTPHeaderField:@"x-device-name"];
    [manage.requestSerializer setValue:entity.x_device_type forHTTPHeaderField:@"x-device-type"];
    [manage.requestSerializer setValue:entity.x_client_version forHTTPHeaderField:@"x-client-version"];
    [manage.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Content－Type"];
    
    [manage POST:@"api/v2/login"  parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             [manage.requestSerializer removeHTTPHeaderFieldForKey:@"x-device-sn"];
             [manage.requestSerializer removeHTTPHeaderFieldForKey:@"x-device-os"];
             [manage.requestSerializer removeHTTPHeaderFieldForKey:@"x-device-name"];
             [manage.requestSerializer removeHTTPHeaderFieldForKey:@"x-device-type"];
             [manage.requestSerializer removeHTTPHeaderFieldForKey:@"x-client-version"];
             [manage.requestSerializer removeHTTPHeaderFieldForKey:@"Content－Type"];
             
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         }failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
             NSLog(@"error:%@",error);
         }];
}


-(void)UserServerAddress:(AFHTTPRequestOperationManager*)manage
           requestEntity:(RequestEntity*)entity
             serviceType:(ServiceType)serviceType
       completionHandler:(void(^)(NSURLResponse *,id , NSError *,ServiceType ,ErrorType)) completionHandler
{
    NSString *URLString = [NSString stringWithFormat:@"api/v2/serverurl?type=%@",entity.userServerType];
    [manage GET:URLString parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)UserRefreshToken:(AFHTTPRequestOperationManager*)manage
          requestEntity:(RequestEntity*)entity
            serviceType:(ServiceType)serviceType
      completionHandler:(void(^)(NSURLResponse *,id , NSError *,ServiceType ,ErrorType)) completionHandler
{
    [manage PUT:@"api/v2/token" parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}

-(void)UserLogout:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *,id , NSError *,ServiceType ,ErrorType)) completionHandler
{
    [manage DELETE:@"api/v2/token" parameters:nil
           success:^(AFHTTPRequestOperation *operation, id responseObject) {
               completionHandler(operation.response,responseObject,nil,serviceType,0);
           } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
               completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
           }];
}

-(void)UserSearch:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.userSearchKeyword forKey:@"keyword"];
    if (entity.userSearchType) {
        [_dic setObject:entity.userSearchType forKey:@"type"];
    }
    if (entity.userAppId) {
        [_dic setObject:entity.userAppId forKey:@"appId"];
    }
    if (entity.listOffset) {
        [_dic setObject:entity.listOffset forKey:@"offset"];
    }
    if (entity.listLimit) {
        [_dic setObject:entity.listLimit forKey:@"limit"];
    }
    NSLog(@"%@",_dic);
    [manage POST:@"api/v2/users/search" parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             NSLog(@"%@",responseObject);
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)UserCreate:(AFHTTPRequestOperationManager*)manage
    requestEntity:(RequestEntity*)entity
      serviceType:(ServiceType)serviceType
completionHandler:(void(^)(NSURLResponse *,id , NSError *,ServiceType ,ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.userLoginName forKey:@"loginName"];
    NSString *URLString = [NSString stringWithFormat:@"api/v2/users/ldapuser"];
    
    [manage POST:URLString parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)UserInfo:(AFHTTPRequestOperationManager*)manage
     requestEntity:(RequestEntity*)entity
       serviceType:(ServiceType)serviceType
 completionHandler:(void(^)(NSURLResponse *,id , NSError *, ServiceType,ErrorType)) completionHandler
{
    NSString *URLString = [NSString stringWithFormat:@"api/v2/users/%@",entity.userId];
    [manage GET:URLString parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}


-(void)UserSendEmail:(AFHTTPRequestOperationManager*)manage
       requestEntity:(RequestEntity*)entity
         serviceType:(ServiceType)serviceType
   completionHandler:(void(^)(NSURLResponse *,id , NSError *,ServiceType ,ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.emailType forKey:@"type"];
    [_dic setObject:entity.emailRecevier forKey:@"mailTo"];
    
    NSMutableArray *params = [[NSMutableArray alloc]init];
    if ([entity.emailType isEqualToString:@"share"]) {
        if (entity.emailMessage && ![entity.emailMessage isEqualToString:@""]) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"message" forKey:@"name"];
            [param setObject:entity.emailMessage forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailObjectName) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"nodeName" forKey:@"name"];
            [param setObject:entity.emailObjectName forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailSender) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"sender" forKey:@"name"];
            [param setObject:entity.emailSender forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailObjectType) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"type" forKey:@"name"];
            [param setObject:entity.emailObjectType forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailObjectOwnerId) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"ownerId" forKey:@"name"];
            [param setObject:entity.emailObjectOwnerId forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailObjectId) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"nodeId" forKey:@"name"];
            [param setObject:entity.emailObjectId forKey:@"value"];
            [params addObject:param];
        }
    }
    
    if ([entity.emailType isEqualToString:@"link"]) {
        if (entity.emailMessage) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"message" forKey:@"name"];
            [param setObject:entity.emailMessage forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailObjectName) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"nodeName" forKey:@"name"];
            [param setObject:entity.emailObjectName forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailSender) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"sender" forKey:@"name"];
            [param setObject:entity.emailSender forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailLinkAccessCode) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"plainAccessCode" forKey:@"name"];
            [param setObject:entity.emailLinkAccessCode forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailLinkStart) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"start" forKey:@"name"];
            [param setObject:entity.emailLinkStart forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailLinkEnd) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"end" forKey:@"name"];
            [param setObject:entity.emailLinkEnd forKey:@"value"];
            [params addObject:param];
        }
        if (entity.emailLinkUrl) {
            NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
            [param setObject:@"linkUrl" forKey:@"name"];
            [param setObject:entity.emailLinkUrl forKey:@"value"];
            [params addObject:param];
        }
    }
    [_dic setObject:params forKey:@"params"];
    
    NSString *URLString = [NSString stringWithFormat:@"api/v2/mail"];
    [manage POST:URLString parameters:_dic
         success:^(AFHTTPRequestOperation *operation, id responseObject) {
             completionHandler(operation.response,responseObject,nil,serviceType,0);
         } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
             completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
         }];
}

-(void)SetMailMessage:(AFHTTPRequestOperationManager*)manage
        requestEntity:(RequestEntity*)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.emailType forKey:@"source"];
    [_dic setObject:entity.emailMessage forKey:@"message"];
    NSString* url = [[NSString alloc] initWithFormat:@"api/v2/mailmsgs/%@/%@",entity.objectOwnerId,entity.objectId];
    [manage PUT:url parameters:_dic
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}
-(void)GetMailMessage:(AFHTTPRequestOperationManager*)manage
        requestEntity:(RequestEntity*)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    NSString* url = [[NSString alloc] initWithFormat:@"api/v2/mailmsgs/%@/%@?source=%@",entity.objectOwnerId,entity.objectId,entity.emailType];
    [manage GET:url parameters:nil
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,responseObject,nil,serviceType,0);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
        }];
}


-(void)UserRequestMailConfig:(AFHTTPRequestOperationManager*)manage
               requestEntity:(RequestEntity*)entity
                 serviceType:(ServiceType)serviceType
           completionHandler:(void(^)(NSURLResponse *,id , NSError *,ServiceType ,ErrorType)) completionHandler
{
    [manage GET:@"api/v2/accounts/attributes" parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

-(void)GetCheckCode:(AFHTTPRequestOperationManager*)manage
        requestEntity:(RequestEntity*)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler {
    NSString* url = [[NSString alloc] initWithFormat:@"api/v2/client/featurecode/%@/%@",entity.clientType,entity.clientVersion];
    [manage GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

- (void)GetDeclarationContent:(AFHTTPRequestOperationManager*)manage
               requesetEntity:(RequestEntity*)entity
                  serviceType:(ServiceType)serviceType
            completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    [manage GET:@"api/v2/declaration/ios" parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

- (void)PutDeclarationStatus:(AFHTTPRequestOperationManager*)manage
              requesetEntity:(RequestEntity*)entity
                 serviceType:(ServiceType)serviceType
           completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:entity.userDeclarationID forKey:@"id"];
    [manage PUT:@"api/v2/declaration/sign" parameters:_dic success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

- (void)getUserHeadIcon:(AFHTTPRequestOperationManager*)manage
         requesetEntity:(RequestEntity*)entity
            serviceType:(ServiceType)serviceType
      completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    NSString *url = [NSString stringWithFormat:@"api/v2/users/image?id=%@",entity.userId];
    [manage GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

- (void)GetClientInfo:(AFHTTPRequestOperationManager*)manage
       requesetEntity:(RequestEntity*)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void(^)(NSURLResponse *,id ,NSError *,ServiceType, ErrorType)) completionHandler
{
    [_dic removeAllObjects];
    [_dic setObject:@"ios" forKey:@"clientType"];
    [manage POST:@"api/v2/client/info" parameters:_dic success:^(AFHTTPRequestOperation *operation, id responseObject) {
        completionHandler(operation.response,responseObject,nil,serviceType,0);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,nil,error,serviceType,[ErrorFormat format:error :operation]);
    }];
}

@end
