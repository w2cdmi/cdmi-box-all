//
//  HttpService.m
//  HttpService
//
//  Created by cse on 14-3-29.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import "HttpService.h"

@implementation HttpService

-(instancetype)initWithBaseURL:(NSURL*)url
{
    self=[super init];
    NSMutableArray *Array = [[NSMutableArray alloc]initWithObjects:@"text/plain",@"text/html",@"text/json",@"application/json",@"text/javascript",@"image/png",@"image/jpeg",nil];
    
    if (self.userOperation == nil) {
        self.userOperation = [[UserOperation alloc]init];
    }
    if (self.fileOperation == nil) {
        self.fileOperation = [[FileOperation alloc]init];
    }
    if (self.folderOperation == nil) {
        self.folderOperation = [[FolderOperation alloc]init];
    }
    if (self.shareOperation == nil) {
        self.shareOperation=[[ShareOperation alloc]init];
    }
    if (self.spaceOperation == nil) {
        self.spaceOperation = [[SpaceOperation alloc] init];
    }
    if (self.manager==nil) {
        self.loginBaseUrl=url;
        self.manager=[[AFHTTPRequestOperationManager alloc]initWithBaseURL:url];
        self.manager.requestSerializer=[AFJSONRequestSerializer serializer];
        [self.manager.responseSerializer setAcceptableContentTypes:[NSSet setWithArray:Array]];
    }
    return self;
}

-(void)doEntityRequst:(RequestEntity *)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void (^)(NSURLResponse *, id, NSError *, ServiceType, ErrorType))completionHandler
{
    switch (serviceType) {
        case ServiceUserLogin:
        case ServiceUserLogout:
        case ServiceUserTokenRefresh:
        case ServiceUserServerAddress:
        case ServiceUserCreate:
        case ServiceUserInfo:
        case ServiceUserSearch:
        case ServiceUserSendEmail:
        case ServiceUserEmailMessageSet:
        case ServiceUserEmailMessageGet:
        case ServiceUserSystermLinksOption:
        case ServiceUserEmailConfig:
        case ServiceClientCheckCode:
        case ServiceClientInfo:
        case ServiceUserDeclarationContent:
        case ServiceUserDeclarationStatus:
        case ServiceUserHeadIcon:
        {
            [_userOperation doRequestbyEntity:_manager requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFileInfo:
        case ServiceFileVersionList:
        case ServiceFileDelete:
        case ServiceFileRename:
        case ServiceFileMove:
        case ServiceFileCopy:
        case ServiceFileSearch:
        case ServiceFileThumbnail:
        {
            [_fileOperation doEntityRequest:_manager requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceFolderList:
        case ServiceFolderCreate:
        case ServiceFolderDelete:
        case ServiceFolderRename:
        case ServiceFolderMove:
        case ServiceFolderCopy:
        case ServiceFolderInfo:
        {
            [_folderOperation doEntityRequst:_manager requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceShareAdd:
        case ServiceShareReceiveList:
        case ServiceShareDelete:
        case ServiceShareUserList:
        case ServiceShareSendList:
        case ServiceLinksInfoList:
        case ServiceLinksCreate:
        case ServiceLinksDelete:
        case ServiceLinksRefresh:
        case ServiceLinkInfo:
        case ServiceLinksObject:
        {
            [_shareOperation doEntityRequst:_manager requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
            break;
        case ServiceSpaceList:
        case ServiceSpaceCreate:
        case ServiceSpaceDelete:
        case ServiceSpaceInfo:
        case ServiceSpaceUpdate:
        case ServiceSpaceMemberList:
        case ServiceSpaceMemberAdd:
        case ServiceSpaceMemberDelete:
        case ServiceSpaceMemberInfo:
        case ServiceSpaceMemberInfoUpdate:
        {
            [_spaceOperation doRequestbyEntity:_manager requestEntity:entity serviceType:serviceType completionHandler:completionHandler];
        }
        default:
            break;
    }
}

-(void)setBaseURL:(NSURL *)url
{
    if ([[url path] length] > 0 && ![[url absoluteString] hasSuffix:@"/"]) {
        url = [url URLByAppendingPathComponent:@""];
    }
    self.manager.baseURL = url;
}

-(void)setHttpRequestHeaderWithToken:(NSString *)token
{
    [_manager.requestSerializer setAuthorizationHeaderFieldWithToken:token];
    
}
-(void)setTimeoutInterval:(NSTimeInterval)time
{
    [_manager.requestSerializer setTimeoutInterval:time];
}

-(void)setCachePolicy:(NSURLRequestCachePolicy )cachePolice
{
    [_manager.requestSerializer setCachePolicy:cachePolice];
}
-(void)setMaxConcurrentOperationCount:(NSInteger)counts
{
    [_manager.operationQueue setMaxConcurrentOperationCount:counts];
}
@end

