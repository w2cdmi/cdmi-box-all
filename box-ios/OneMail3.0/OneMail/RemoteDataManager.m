//
//  RemoteDataManager.m
//  OneMail
//
//  Created by cse  on 16/4/1.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "RemoteDataManager.h"
#import "KeychainItemWrapper.h"
#import "MessageIMAPSession.h"
#import "UserSetting.h"
#import "AppDelegate.h"
#import "User.h"
#import "TransportTask.h"
#import "AFSecurityPolicy.h"

@implementation CloudLoginUserInfo

@end

@implementation MailLoginUserInfo

@end

@implementation RemoteDataManager

@synthesize token = _token;

-(id)init {
    self = [super init];
    if (self) {
        self.cloudLoginUserInfo = [self buildCloudLoginInfo];
        self.mailLoginUserInfo = [self buildMailLoginInfo];
    }
    return self;
}

- (HttpService*) httpService {
    if (!_httpService) {
        _httpService = [[HttpService alloc]initWithBaseURL:nil];
        [_httpService.manager.operationQueue setMaxConcurrentOperationCount:1];
        _httpService.manager.securityPolicy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeNone];
        _httpService.manager.securityPolicy.allowInvalidCertificates = YES;
        [_httpService.manager.requestSerializer setTimeoutInterval:10];
        [_httpService.manager.requestSerializer setValue:@"Onebox-iOS" forHTTPHeaderField:@"User-Agent"];
    }
    return _httpService;
}

- (TransportUploadTaskManager*) uploadService {
    if (!_uploadService) {
        _uploadService = [[TransportUploadTaskManager alloc] initWithBaseURL:self.httpService.manager.baseURL sessionConfiguration:[NSURLSessionConfiguration backgroundSessionConfiguration:@"com.huawei.onebox.uploadservice"]];
        AFSecurityPolicy *securityPolicy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeNone];
        securityPolicy.allowInvalidCertificates = YES;
        _uploadService.securityPolicy = securityPolicy;
        [_uploadService setMaxConcurrentOperationCount:1];
        [_uploadService setTimeoutInterval:10];
    }
    return _uploadService;
}

- (TransportDownloadTaskManager*) downloadService {
    if (!_downloadService) {
        _downloadService = [[TransportDownloadTaskManager alloc] initWithBaseURL:self.httpService.manager.baseURL sessionConfiguration:[NSURLSessionConfiguration backgroundSessionConfiguration:@"com.huawei.onebox.downloadservice"]];
        AFSecurityPolicy *securityPolicy = [AFSecurityPolicy policyWithPinningMode:AFSSLPinningModeNone];
        securityPolicy.allowInvalidCertificates = YES;
        _downloadService.securityPolicy = securityPolicy;
        [_downloadService setMaxConcurrentOperationCount:1];
        [_downloadService setTimeoutInterval:10];
    }
    return _downloadService;
}

- (CloudLoginUserInfo*) buildCloudLoginInfo {
    UIDevice* device = [UIDevice currentDevice];
    CloudLoginUserInfo* userLogin = [[CloudLoginUserInfo alloc] init];
    
    userLogin.deviceType = @"ios";
    userLogin.deviceName = device.model;
    userLogin.deviceOS = [NSString stringWithFormat:@"%@%@",  device.systemName, device.systemVersion];
    userLogin.clientVersion=[NSString stringWithFormat:@"%@", device.systemVersion];
//    KeychainItemWrapper * wrapper = [[KeychainItemWrapper alloc] initWithIdentifier:nil accessGroup:nil];
//    userLogin.deviceSN=[wrapper objectForKey:(__bridge id)(kSecValueData)];
    userLogin.deviceSN = [[NSUUID UUID] UUIDString];
    //CFStringRef ipAddress = NULL;
    struct ifaddrs *interfaces = NULL;
    struct ifaddrs *temp_addr = NULL;
    int success = 0;
    success = getifaddrs(&interfaces);
    if (success == 0) {
        temp_addr = interfaces;
        while(temp_addr != NULL) {
            if(temp_addr->ifa_addr->sa_family == AF_INET) {
                if(strncasecmp(temp_addr->ifa_name, "en0", 3) == 0) {
                    CFStringRef ipAddress = CFStringCreateWithCString(kCFAllocatorDefault,inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr),kCFStringEncodingUTF8);
                    userLogin.deviceAddress = (__bridge NSString*)ipAddress;
                    if (NULL != ipAddress) CFRelease(ipAddress);
                    printf("%s:%s", temp_addr->ifa_name, inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr));
                }
            }
            temp_addr = temp_addr->ifa_next;
        }
    }
    freeifaddrs(interfaces);
    
    return userLogin;
}

-(void)getServerAddress:(HWRemoteSuccessBlock)succeed failure:(HWRemoteFailedBlock)failed {
    [self.httpService setBaseURL:self.httpService.loginBaseUrl];
    RequestEntity *requestEntity=[[RequestEntity alloc]init];
    [requestEntity setUserServerType:@"uam"];
    [self.httpService doEntityRequst:requestEntity serviceType:ServiceUserServerAddress completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
        if (error) {
        failed:SERVICE_FAILED(@"Get Server Address");
        } else {
            NSURL *uamUrl = [NSURL URLWithString:[responseObject objectForKey:@"serverUrl"]];
            if ([[uamUrl path] length] > 0 && ![[uamUrl absoluteString] hasSuffix:@"/"]) {
                uamUrl = [uamUrl URLByAppendingPathComponent:@""];
            }
            if (![uamUrl isKindOfClass:[NSURL class]]) {
                failed(nil,nil,nil,0);return;
            }
            [self.httpService setUam:uamUrl];
            [requestEntity setUserServerType:@"ufm"];
            [self.httpService doEntityRequst:requestEntity serviceType:ServiceUserServerAddress completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
                if (error) {
                failed:SERVICE_FAILED(@"Get Servcer Address");
                } else {
                    NSURL *ufmUrl = [NSURL URLWithString:[responseObject objectForKey:@"serverUrl"]];
                    if ([[ufmUrl path] length] > 0 && ![[ufmUrl absoluteString] hasSuffix:@"/"]) {
                        ufmUrl = [ufmUrl URLByAppendingPathComponent:@""];
                    }
                    if (![ufmUrl isKindOfClass:[NSURL class]]) {
                        failed(nil,nil,nil,0);return;
                    }
                    [self.httpService setUfm:ufmUrl];
                succeed:SERVICE_SUCCEED(@"Get Server Address");
                }
            }];
        }
    }];
}

- (void) ensureCloudLogin:(void(^)()) block failed:(HWRemoteFailedBlock)faildBlock {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!appDelegate.hasNetwork) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudNoneNetworkPrompt", nil)];
        });
    } else {
        if ([self getToken]) {
            if (block) {
                block();
            }
        } else {
            [self cloudLogin:^(id retobj) {
                if (block) {
                    block();
                }
            } failed:^(NSURLRequest* request, NSURLResponse* response, NSError* error,int ErrorType) {
                if (faildBlock) {
                    faildBlock(request, response, error, ErrorType);
                }
            }];
        }
    }
}


- (void) ensureCloudLogin:(void(^)()) block failed:(HWRemoteFailedBlock)faildBlock completionQueue:(dispatch_queue_t)completionQueue {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!appDelegate.hasNetwork) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication].keyWindow makeToast:NSLocalizedString(@"CloudNoneNetworkPrompt", nil)];
        });
        faildBlock(nil,nil,nil,0);
    } else {
        if ([self getToken]) {
            if (block) {
                dispatch_async(completionQueue, ^{
                    block();
                });
            }
        } else {
            [self cloudLogin:^(id retobj) {
                if (block) {
                    dispatch_async(completionQueue, ^{
                        block();
                    });
                }
            } failed:^(NSURLRequest* request, NSURLResponse* response, NSError* error,int ErrorType) {
                if (faildBlock) {
                    dispatch_async(completionQueue, ^{
                        faildBlock(request, response, error, ErrorType);
                    });
                }
            }];
        }
    }
}

- (NSString*) getToken {
    NSDate* now = [NSDate date];
    if ([now timeIntervalSinceDate:self.tokenInValidTime] >= 1000) {
        return nil;
    }
    return _token;
}

- (void)cloudLogin:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    RequestEntity *requestEntity=[[RequestEntity alloc]init];
    [requestEntity setX_client_version:self.cloudLoginUserInfo.clientVersion];
    [requestEntity setX_device_name:self.cloudLoginUserInfo.deviceName];
    [requestEntity setX_device_os:self.cloudLoginUserInfo.deviceOS];
    [requestEntity setX_device_sn:self.cloudLoginUserInfo.deviceSN];
    [requestEntity setX_device_type:self.cloudLoginUserInfo.deviceType];
    [requestEntity setUserDomain:self.cloudLoginUserInfo.domain];
    [requestEntity setUserLoginName:self.cloudLoginUserInfo.loginName];
    [requestEntity setUserPassword:self.cloudLoginUserInfo.password];
    [self.httpService setBaseURL:self.httpService.loginBaseUrl];
    [self.httpService doEntityRequst:requestEntity serviceType:ServiceUserLogin completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
        if (error) {
            [SNLog Log:LInfo :@"Login Failed,Error:%@",error];
        failed:SERVICE_FAILED(@"Login");
        } else {
            _userId = [NSString stringWithFormat:@"%@",[[responseObject objectForKey:@"cloudUserId"] stringValue]];
            _token = [responseObject objectForKey:@"token"];
            _refreshToken=[responseObject objectForKey:@"refreshToken"];
            NSNumber *toExpiredAt= [responseObject objectForKey:@"timeout"];
            self.tokenInValidTime = [NSDate dateWithTimeIntervalSinceNow:toExpiredAt.doubleValue / 1000.0f];
            [self.httpService setHttpRequestHeaderWithToken:_token];
            [self.uploadService setHttpRequestHeaderWithToken:_token];
            [self.downloadService setHttpRequestHeaderWithToken:_token];
            [self getServerAddress:^(id retobj) {
            succeed:SERVICE_SUCCEED(@"Login");
            } failure:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
            failed:SERVICE_FAILED(@"Login");
            }];
        }
    }];
}

- (void)cloudLogout:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    NSMutableDictionary *parametes = [[NSMutableDictionary alloc]init];
    [parametes setValue:_token forKey:@"token"];
    [self.httpService setBaseURL:self.httpService.uam];
    [self.httpService doEntityRequst:nil serviceType:ServiceUserLogout completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
        if (error) {
            [SNLog Log:LInfo :@"Logout Failed,Error:%@",error];
        failed:SERVICE_FAILED(@"Logout");
        } else {
            [SNLog Log:LInfo :@"Logout Success"];
        succeed:SERVICE_SUCCEED(@"Logout");
        }
    }];
}


- (MailLoginUserInfo*) buildMailLoginInfo {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (!_mailLoginUserInfo) {
        _mailLoginUserInfo = [[MailLoginUserInfo alloc] init];
    }
    _mailLoginUserInfo.hostName = userSetting.emailServerReceive;
    _mailLoginUserInfo.port = userSetting.emailPortReceive;
    _mailLoginUserInfo.protocol = userSetting.emailProtocolReceive;
    return _mailLoginUserInfo;
}

-(void) getEmailConfig:(HWRemoteSuccessBlock)succeed failure:(HWRemoteFailedBlock)failed {
    [self.httpService setBaseURL:self.httpService.uam];
    [self.httpService doEntityRequst:nil serviceType:ServiceUserEmailConfig completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
        if (error) {
        failed:SERVICE_FAILED(@"mail_login")
        } else {
        succeed:SERVICE_SUCCEED(@"mail_login")
        }
    }];
}

-(void)mailLogin:(void(^)(NSError *error))completionHandler {
    [MessageIMAPSession clearSessionInstance];
    MessageIMAPSession *imapSession = [MessageIMAPSession getSessionInstance];
    imapSession.hostname = self.mailLoginUserInfo.hostName;
    imapSession.port = self.mailLoginUserInfo.port.intValue;
    imapSession.username = self.mailLoginUserInfo.address;
    imapSession.password= self.mailLoginUserInfo.password;
    imapSession.connectionType = MCOConnectionTypeClear;
    imapSession.checkCertificateEnabled = NO;
    imapSession.connectionLogger = ^(void * connectionID, MCOConnectionLogType type, NSData * data) {
        if (type != MCOConnectionLogTypeSentPrivate) {
            
        }
    };
    MCOIMAPOperation *checkAccountOP = [imapSession checkAccountOperation];
    [checkAccountOP start:^(NSError *error) {
        completionHandler(error);
    }];
}

- (void) getDeclarationContent:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    RequestEntity *requestEntity=[[RequestEntity alloc]init];
    [requestEntity setX_device_type:self.cloudLoginUserInfo.deviceType];
    [self.httpService setBaseURL:self.httpService.uam];
    [self.httpService doEntityRequst:requestEntity serviceType:ServiceUserDeclarationContent completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
        if (error) {
        failed:SERVICE_FAILED(@"getPrivacyProtocolContent");
        } else {
        succeed:SERVICE_SUCCEED(@"getPrivacyProtocolContent");
        }
    }];
    
}
- (void) setDeclarationSignStatus:(NSString*)declarationID succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    RequestEntity *requestEntity = [[RequestEntity alloc]init];
    [requestEntity setUserDeclarationID:declarationID];
    [self.httpService setBaseURL:self.httpService.uam];
    [self.httpService doEntityRequst:requestEntity serviceType:ServiceUserDeclarationStatus completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
        if (error) {
        failed:SERVICE_FAILED(@"setDeclarationStatus");
        } else {
        succeed:SERVICE_SUCCEED(@"setDeclarationStatus");
        }
    }];
}

//CFStringRef getIPAddress() {
//    CFStringRef address = NULL;
//    struct ifaddrs *interfaces = NULL;
//    struct ifaddrs *temp_addr = NULL;
//    int success = 0;
//    success = getifaddrs(&interfaces);
//    if (success == 0) {
//        temp_addr = interfaces;
//        while(temp_addr != NULL) {
//            if(temp_addr->ifa_addr->sa_family == AF_INET) {
//                if(strncasecmp(temp_addr->ifa_name, "en0", 3) == 0) {
//                    address = CFStringCreateWithCString(kCFAllocatorDefault,inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr),kCFStringEncodingUTF8);
//                    printf("%s:%s", temp_addr->ifa_name, inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr));
//                }
//            }
//            temp_addr = temp_addr->ifa_next;
//        }
//    }
//    freeifaddrs(interfaces);
//    return address;
//}

- (void) uploadUserHeadImage:(UIImage*)image success:(void(^)(NSData*))success failed:(void(^)(AFHTTPRequestOperation*,NSError*))failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    appDelegate.uploadHeadImage = YES;
    [self.uploadService uploadHeadImage:image compleitonHandler:^(AFHTTPRequestOperation *operation, NSError *error) {
        appDelegate.uploadHeadImage = NO;
        if (!error) {
            User *user = [User getUserWithUserSingleId:appDelegate.localManager.userSingleId context:nil];
            NSData *data = UIImageJPEGRepresentation(image, 1.0f);
            [user saveUserHeadIcon:data];
            success(data);
        } else {
            failed(operation,error);
        }
    }];
}

@end

