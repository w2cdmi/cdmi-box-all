//
//  TransportUploadTaskManager.h
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"

@class TransportTask;
@interface TransportUploadTaskManager : NSObject

@property (nonatomic, strong) AFSecurityPolicy *securityPolicy;

- (id) initWithBaseURL:(NSURL *) baseURL sessionConfiguration:(NSURLSessionConfiguration *) configuration;
- (void) setMaxConcurrentOperationCount:(NSInteger) count;
- (void) setTimeoutInterval:(NSTimeInterval)time;
- (void) invalidateSessionCancelingTasks:(BOOL) b;
- (void) setHttpRequestHeaderWithToken:(NSString *)token;

- (void) uploadWithTask:(TransportTask*)transportTask
              preUpload:(void (^)(id, NSString *))preUploadCallback
           taskProgress:(void (^)(NSURLSessionUploadTask *, NSProgress *))taskProgressCallback
      completionHandler:(void (^)(AFHTTPRequestOperation *, NSError *))completionHandler;

- (void)uploadHeadImage:(UIImage*)image
      compleitonHandler:(void (^)(AFHTTPRequestOperation *operation, NSError *error))completionHandler;
@end
