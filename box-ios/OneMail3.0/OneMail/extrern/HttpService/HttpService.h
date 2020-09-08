//
//  HttpService.h
//  HttpService
//
//  Created by cse on 14-3-29.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RequestEntity.h"
#import "HttpConst.h"
#import "UserOperation.h"
#import "FileOperation.h"
#import "FolderOperation.h"
#import "AFNetworking.h"
#import "ShareOperation.h"
#import "SpaceOperation.h"
#import "ErrorFormat.h"


@interface HttpService : NSObject

@property(nonatomic,strong)UserOperation *userOperation;
@property(nonatomic,strong)FileOperation *fileOperation;
@property(nonatomic,strong)FolderOperation *folderOperation;
@property(nonatomic,strong)ShareOperation *shareOperation;
@property(nonatomic,strong)SpaceOperation *spaceOperation;
@property(nonatomic,strong)AFHTTPRequestOperationManager *manager;

@property(nonatomic,strong)NSURL *loginBaseUrl;
@property(nonatomic,strong)NSURL *uam;
@property(nonatomic,strong)NSURL *ufm;

-(instancetype)initWithBaseURL:(NSURL *)url;
-(void)doEntityRequst:(RequestEntity*)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void (^)(NSURLResponse *response,id responseObject,NSError *error,ServiceType serviceType,ErrorType errorType)) completionHandler;
-(void)setBaseURL:(NSURL*)url;
-(void)setHttpRequestHeaderWithToken:(NSString *)token;
-(void)setCachePolicy:(NSURLRequestCachePolicy )cachePolice;
-(void)setTimeoutInterval:(NSTimeInterval )time;
-(void)setMaxConcurrentOperationCount:(NSInteger)counts;
@end
