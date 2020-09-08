//
//  TransportDownloadTaskManager.h
//  OneMail
//
//  Created by cse  on 15/11/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"
#import "Version.h"
@class TransportTask;

@interface TransportDownloadTaskManager : NSObject

@property (nonatomic, strong) AFSecurityPolicy *securityPolicy;

- (id) initWithBaseURL:(NSURL *) baseURL sessionConfiguration:(NSURLSessionConfiguration *) configuration;
- (void) setMaxConcurrentOperationCount:(NSInteger) count;
- (void) setTimeoutInterval:(NSTimeInterval)time;
- (void) invalidateSessionCancelingTasks:(BOOL) b;
- (void) setHttpRequestHeaderWithToken:(NSString *)token;

- (void) downloadTaskCancel;

- (void) downloadWithTask:(TransportTask *)transportTask
             taskProgress:(void(^)(AFHTTPRequestOperation*, NSProgress*))taskProgressCallback
        completionHandler:(void(^)(NSURLResponse*, NSError*))completionHandler;

//- (void) downloadWithTask:(TransportTask*)transportTask
//             taskProgress:(void(^)(NSURLSessionDownloadTask*, NSProgress*)) taskProgressCallback
//        completionHandler:(void(^)(NSURLResponse*, NSString*, NSError*))completionHandler;

- (void)downloadVersionWithTask:(TransportTask *)transportTask
                   taskProgress:(void (^)(AFHTTPRequestOperation *, NSProgress *))taskProgressCallback
              completionHandler:(void (^)(NSURLResponse *, NSError *))completionHandler;

//- (void)downloadVersionWithTask:(TransportTask *)transportTask
//                   taskProgress:(void (^)(NSURLSessionDownloadTask *, NSProgress *))taskProgressCallback
//              completionHandler:(void (^)(NSURLResponse *, NSString *, NSError *))completionHandler;

- (void)downloadHeadImageWithUserId:(NSString*)userCloudId
                  completionHandler:(void(^)(NSURLResponse*,NSData*,NSError*))compleitonHandler;
@end
