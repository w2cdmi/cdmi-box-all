//
//  SpaceOperation.h
//  OneBox
//
//  Created by cse on 15-2-13.
//  Copyright (c) 2015年 www.huawei.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RequestEntity.h"
#import "HttpConst.h"
#import "AFNetworking.h"
#import "ErrorFormat.h"

@interface SpaceOperation : NSObject

-(instancetype)init;
-(void)doRequestbyEntity:(AFHTTPRequestOperationManager *)manage
           requestEntity:(RequestEntity*)entity
             serviceType:(ServiceType)serviceType
       completionHandler:(void(^)(NSURLResponse *response,id responseObject, NSError *error, ServiceType, ErrorType errorType)) completionHandler;
@end
