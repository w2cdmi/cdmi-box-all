//
//  RemoteDataManager.h
//  OneMail
//
//  Created by cse  on 16/4/1.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DataCommon.h"
#import "RequestEntity.h"
#import "HttpService.h"
#import "TransportUploadTaskManager.h"
#import "TransportDownloadTaskManager.h"

@interface CloudLoginUserInfo : NSObject

@property(copy, nonatomic) NSString* domain;
@property(copy, nonatomic) NSString* loginName;
@property(copy, nonatomic) NSString* password;
@property(copy, nonatomic) NSString* deviceType;
@property(copy, nonatomic) NSString* deviceSN;
@property(copy, nonatomic) NSString* deviceOS;
@property(copy, nonatomic) NSString* deviceName;
@property(copy, nonatomic) NSString* deviceAddress;
@property(copy, nonatomic) NSString* deviceAgent;
@property(copy, nonatomic) NSString* clientVersion;

@end

@interface MailLoginUserInfo : NSObject

@property (nonatomic, copy) NSString *hostName;
@property (nonatomic, copy) NSString *port;
@property (nonatomic, copy) NSString *protocol;
@property (nonatomic, copy) NSString *address;
@property (nonatomic, copy) NSString *password;

@end

@interface RemoteDataManager : NSObject

@property (strong,nonatomic) HttpService *httpService;
@property (strong,nonatomic) TransportUploadTaskManager *uploadService;
@property (strong,nonatomic) TransportDownloadTaskManager *downloadService;
@property (strong,nonatomic) CloudLoginUserInfo *cloudLoginUserInfo;
@property (strong,nonatomic) MailLoginUserInfo *mailLoginUserInfo;

@property (copy,nonatomic,readonly,getter = getToken) NSString* token;
@property (copy,nonatomic,readonly) NSString* refreshToken;
@property (copy,nonatomic,readonly) NSString* userId;
@property (strong,nonatomic) NSDate* tokenInValidTime;

- (void) cloudLogin:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) ensureCloudLogin:(void(^)()) block failed:(HWRemoteFailedBlock)faildBlock;
- (void) ensureCloudLogin:(void(^)()) block failed:(HWRemoteFailedBlock)faildBlock completionQueue:(dispatch_queue_t)completionQueue;
- (void) cloudLogout:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) getServerAddress:(HWRemoteSuccessBlock)succeed failure:(HWRemoteFailedBlock)failed;
- (void) getEmailConfig:(HWRemoteSuccessBlock)succeed failure:(HWRemoteFailedBlock)failed;
- (void) mailLogin:(void(^)(NSError *error))completionHandler;
- (void) getDeclarationContent:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) setDeclarationSignStatus:(NSString*)declarationID succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) uploadUserHeadImage:(UIImage*)image success:(void(^)(NSData*))success failed:(void(^)(AFHTTPRequestOperation*,NSError*))failed;

@end

