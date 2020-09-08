//
//  ShareOperation.h
//  HttpService
//
//  Created by cse on 14-4-25.
//  Copyright (c) 2014年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RequestEntity.h"
#import "HttpConst.h"
#import "AFNetworking.h"
#import "ErrorFormat.h"

@interface ShareOperation : NSObject

-(instancetype)init;
-(void)doEntityRequst:(AFHTTPRequestOperationManager *)manage
        requestEntity:(RequestEntity*)entity
          serviceType:(ServiceType)serviceType
    completionHandler:(void(^)(NSURLResponse *response,id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType)) completionHandler;
@end
