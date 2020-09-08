//
//  FolderOperation.h
//  HttpService
//
//  Created by cse on 14-3-29.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RequestEntity.h"
#import "AFNetworking.h"
#import "HttpConst.h"
#import "ErrorFormat.h"


@interface FolderOperation : NSObject

-(instancetype)init;
-(void)doEntityRequst:(AFHTTPRequestOperationManager *)manage
        requestEntity:(RequestEntity*)requestEntity
          serviceType:(ServiceType)serviceType
    completionHandler:(void(^)(NSURLResponse* response,id responseObject,NSError* error,ServiceType serviceType,ErrorType errorType)) completionHandler;
@end
