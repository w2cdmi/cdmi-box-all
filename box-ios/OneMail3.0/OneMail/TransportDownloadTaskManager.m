//
//  TransportDownloadTaskManager.m
//  OneMail
//
//  Created by cse  on 15/11/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportDownloadTaskManager.h"
#import <CommonCrypto/CommonCrypto.h>
#import "AppDelegate.h"
#import "TransportTask.h"
#import "File.h"

@interface TransportDownloadTaskManager ()

@property (nonatomic, strong) AFHTTPSessionManager* sessionManager;
@property (nonatomic, strong) AFHTTPRequestOperationManager* operationManager;
@property (nonatomic, strong) NSURLSessionConfiguration* config;
@property (nonatomic, strong) NSString *token;

@end

@implementation TransportDownloadTaskManager
- (id)initWithBaseURL:(NSURL *)baseURL sessionConfiguration:(NSURLSessionConfiguration *)configuration {
    if (self = [super init]) {
        //AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSMutableArray *Array = [[NSMutableArray alloc]init];
        [Array addObject:@"text/plain"];
        [Array addObject:@"text/html"];
        [Array addObject:@"text/json"];
        [Array addObject:@"application/json"];
        [Array addObject:@"text/javascript"];
        [Array addObject:@"image/png"];
        [Array addObject:@"image/jpeg"];
        NSSet* accepts = [NSSet setWithArray:Array];
        _sessionManager = [[AFHTTPSessionManager alloc] initWithBaseURL:baseURL sessionConfiguration:configuration];
        _sessionManager.responseSerializer.acceptableContentTypes = accepts;
        //_sessionManager.completionQueue = appDelegate.download_queue;
        
        _operationManager = [[AFHTTPRequestOperationManager alloc] initWithBaseURL:baseURL];
        _operationManager.responseSerializer.acceptableContentTypes = accepts;
        _operationManager.requestSerializer = [AFJSONRequestSerializer serializer];
        //_operationManager.completionQueue = appDelegate.download_queue;
        _config = configuration;
    }
    return self;
}

- (void) invalidateSessionCancelingTasks:(BOOL) b {
    [_sessionManager invalidateSessionCancelingTasks:NO];
}

- (void) setMaxConcurrentOperationCount:(NSInteger) count {
    [_sessionManager.operationQueue setMaxConcurrentOperationCount:count];
    [_operationManager.operationQueue setMaxConcurrentOperationCount:count];}

- (void) setTimeoutInterval:(NSTimeInterval)time {
    [_sessionManager.requestSerializer setTimeoutInterval:time];
    [_operationManager.requestSerializer setTimeoutInterval:time];
}

- (void)setHttpRequestHeaderWithToken:(NSString *)token {
    [_operationManager.requestSerializer setAuthorizationHeaderFieldWithToken:token];
    [_sessionManager.requestSerializer setAuthorizationHeaderFieldWithToken:token];
    [self.config  setHTTPAdditionalHeaders:@{@"Authorization": token}];
    self.token = token;
}

- (void)setSecurityPolicy:(AFSecurityPolicy *)securityPolicy {
    _securityPolicy = securityPolicy;
    _sessionManager.securityPolicy = securityPolicy;
    _operationManager.securityPolicy = securityPolicy;
}

- (void) downloadTaskCancel {
    [_operationManager.operationQueue cancelAllOperations];
}

- (void)downloadWithTask:(TransportTask *)transportTask
            taskProgress:(void (^)(AFHTTPRequestOperation *, NSProgress *))taskProgressCallback
       completionHandler:(void (^)(NSURLResponse *, NSError *))completionHandler {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString* fileId = transportTask.file.fileId;
    NSString* fileName = transportTask.file.fileName;
    NSString* fileOwner = transportTask.file.fileOwner;
    NSNumber* fileSize = transportTask.file.fileSize;
    
    [_operationManager setBaseURL:appDelegate.remoteManager.httpService.ufm];
    [_operationManager GET:[NSString stringWithFormat:@"api/v2/files/%@/%@/url",fileOwner,fileId] parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSString* fileObjectId = [operation.response.allHeaderFields objectForKey:@"objectId"];
        NSString* fileDownloadPath = [[NSString alloc]initWithFormat:@"api/v2/files/%@/%@/%@/contents",fileOwner,fileId,fileObjectId];
        NSURL* fileDownloadURL = [_operationManager.baseURL URLByAppendingPathComponent:fileDownloadPath];
        
        NSError *error = nil;
        NSString *fileDestinationPath = [transportTask.file fileCacheLocalPath];
        if (!fileDestinationPath) {
            error = [NSError errorWithDomain:NSCocoaErrorDomain code:NSFileNoSuchFileError userInfo:@{NSLocalizedDescriptionKey:[NSString stringWithFormat:@"%@ destination path is nil", fileName]}];
            completionHandler(nil,error);
        }
        NSProgress *progress = [NSProgress progressWithTotalUnitCount:fileSize.longLongValue];
        NSString *range = nil;
        unsigned long long completedUnitCount = 0;
        if ([[NSFileManager defaultManager] fileExistsAtPath:fileDestinationPath]) {
            NSDictionary *fileAttribute = [[NSFileManager defaultManager] attributesOfItemAtPath:fileDestinationPath error:&error];
            if (error) {
                completionHandler(nil,error);
            } else {
                progress.completedUnitCount = [fileAttribute fileSize];
                completedUnitCount = [fileAttribute fileSize];
                range = [NSString stringWithFormat:@"bytes=%llu-",[fileAttribute fileSize]];
            }
        } else {
            [[NSFileManager defaultManager] createFileAtPath:fileDestinationPath contents:nil attributes:nil];
            progress.completedUnitCount = 0;
            range = [NSString stringWithFormat:@"bytes=0-"];
        }
        
        NSMutableURLRequest *request = [_operationManager.requestSerializer requestWithMethod:@"GET" URLString:fileDownloadURL.absoluteString parameters:nil error:nil];
        [request setValue:range forHTTPHeaderField:@"Range"];
        AFHTTPRequestOperation *downloadOperation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
        downloadOperation.securityPolicy = _operationManager.securityPolicy;
        downloadOperation.completionQueue = _operationManager.completionQueue;
        
        downloadOperation.outputStream = [NSOutputStream outputStreamToFileAtPath:fileDestinationPath append:YES];
        taskProgressCallback(downloadOperation,progress);
        
        [downloadOperation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation.response,nil);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(nil,error);
        }];
        
        [downloadOperation setDownloadProgressBlock:^(NSUInteger bytesRead, NSInteger totalBytesRead, NSInteger totalBytesExpectedToRead) {
            progress.completedUnitCount = completedUnitCount+totalBytesRead;
        }];
        
        [_operationManager.operationQueue addOperation:downloadOperation];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        completionHandler(operation.response,error);
    }];
}

//- (void)downloadWithTask:(TransportTask *)transportTask
//            taskProgress:(void (^)(NSURLSessionDownloadTask *, NSProgress *))taskProgressCallback
//       completionHandler:(void (^)(NSURLResponse *, NSString *, NSError *))completionHandler {
//    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
//    NSString* fileId = transportTask.file.fileId;
//    NSString* fileName = transportTask.file.fileName;
//    NSString* fileOwner = transportTask.file.fileOwner;
//    
//    [_operationManager setBaseURL:appDelegate.remoteManager.httpService.ufm];
//    [_operationManager GET:[NSString stringWithFormat:@"api/v2/files/%@/%@/url",fileOwner,fileId] parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
//        NSString* fileObjectId = [operation.response.allHeaderFields objectForKey:@"objectId"];
//        NSString* fileDownloadPath = [NSString stringWithFormat:@"api/v2/files/%@/%@/%@/contents",fileOwner,fileId ,fileObjectId];
//        NSURL* fileDownloadURL = [_operationManager.baseURL URLByAppendingPathComponent:fileDownloadPath];
//        NSURL* fileResumeDataURL = [self resumeFileURL:fileDownloadURL];
//        
//        NSProgress* progress;
//        NSURLSessionDownloadTask* fileDownloadTask;
//        if ([[NSFileManager defaultManager] fileExistsAtPath:fileResumeDataURL.path]) {
//            NSData *fileResumeData = [NSData dataWithContentsOfURL:fileResumeDataURL];
//            [[NSFileManager defaultManager] removeItemAtURL:fileResumeDataURL error:nil];
//            fileDownloadTask = [_sessionManager downloadTaskWithResumeData:fileResumeData progress:&progress destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
//                return [[targetPath URLByDeletingLastPathComponent] URLByAppendingPathComponent:fileName];
//            } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
//                completionHandler(response,filePath.path,error);
//            }];
//        } else {
//            NSMutableURLRequest* fileDownloadRequest = [NSMutableURLRequest requestWithURL:fileDownloadURL];
//            [fileDownloadRequest setValue:self.token forHTTPHeaderField:@"Authorization"];
//            fileDownloadTask = [_sessionManager downloadTaskWithRequest:fileDownloadRequest progress:&progress destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
//                return [[targetPath URLByDeletingLastPathComponent] URLByAppendingPathComponent:fileName];
//            } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
//                completionHandler(response,filePath.path,error);
//            }];
//        }
//        taskProgressCallback(fileDownloadTask, progress);
//    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
//        completionHandler(operation.response, nil, error);
//    }];
//}

- (void)downloadVersionWithTask:(TransportTask *)transportTask
                   taskProgress:(void (^)(AFHTTPRequestOperation *, NSProgress *))taskProgressCallback
              completionHandler:(void (^)(NSURLResponse *, NSError *))completionHandler {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *versionId = transportTask.version.versionId;
    NSString* versionFileName = transportTask.version.versionFileName;
    NSString* versionFileOwner = transportTask.version.versionOwner;
    NSNumber* versionFileSize = transportTask.version.versionSize;
    NSString *versionFileObjectId = transportTask.version.versionObjectId;
    [_operationManager setBaseURL:appDelegate.remoteManager.httpService.ufm];
        NSString* versionFileDownloadPath = [[NSString alloc]initWithFormat:@"api/v2/files/%@/%@/%@/contents",versionFileOwner,versionId,versionFileObjectId];
        NSURL* versionFileDownloadURL = [_operationManager.baseURL URLByAppendingPathComponent:versionFileDownloadPath];
        NSError *error = nil;
        NSString *versionFileDestinationPath = [transportTask.version versionCacheLocalPath];
        if (!versionFileDownloadPath) {
            error = [NSError errorWithDomain:NSCocoaErrorDomain code:NSFileNoSuchFileError userInfo:@{NSLocalizedDescriptionKey:[NSString stringWithFormat:@"%@ destination path is nil", versionFileName]}];
            completionHandler(nil,error);
        }
        
        NSProgress *progress = [NSProgress progressWithTotalUnitCount:versionFileSize.longLongValue];
        NSString *range = nil;
        unsigned long long completedUnitCount = 0;
        if ([[NSFileManager defaultManager] fileExistsAtPath:versionFileDestinationPath]) {
            NSDictionary *fileAttribute = [[NSFileManager defaultManager] attributesOfItemAtPath:versionFileDestinationPath error:&error];
            if (error) {
                completionHandler(nil,error);
            } else {
                progress.completedUnitCount = [fileAttribute fileSize];
                completedUnitCount = [fileAttribute fileSize];
                range = [NSString stringWithFormat:@"bytes=%llu-",[fileAttribute fileSize]];
            }
        } else {
            progress.completedUnitCount = 0;
            range = [NSString stringWithFormat:@"bytes=0-"];
        }
        NSMutableURLRequest *request = [_operationManager.requestSerializer requestWithMethod:@"GET" URLString:versionFileDownloadURL.absoluteString parameters:nil error:nil];
        [request setValue:range forHTTPHeaderField:@"Range"];
        AFHTTPRequestOperation *downloadOperation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
        downloadOperation.securityPolicy = _operationManager.securityPolicy;
        downloadOperation.completionQueue = _operationManager.completionQueue;
        
        downloadOperation.outputStream = [NSOutputStream outputStreamToFileAtPath:versionFileDestinationPath append:YES];
        taskProgressCallback(downloadOperation,progress);
        
        [downloadOperation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
            NSLog(@"%@ download success",versionFileName);
            completionHandler(operation.response,nil);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            NSLog(@"%@ download failed",versionFileName);
            completionHandler(nil,error);
        }];
        
        [downloadOperation setDownloadProgressBlock:^(NSUInteger bytesRead, NSInteger totalBytesRead, NSInteger totalBytesExpectedToRead) {
            progress.completedUnitCount = completedUnitCount+totalBytesRead;
            NSLog(@"%g",progress.fractionCompleted);
        }];
        
        [_operationManager.operationQueue addOperation:downloadOperation];
}

//- (void)downloadVersionWithTask:(TransportTask *)transportTask
//                   taskProgress:(void (^)(NSURLSessionDownloadTask *, NSProgress *))taskProgressCallback
//              completionHandler:(void (^)(NSURLResponse *, NSString *, NSError *))completionHandler{
//    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
//    [_operationManager setBaseURL:appDelegate.remoteManager.httpService.ufm];
//    NSString* fileDownloadPath = [NSString stringWithFormat:@"api/v2/files/%@/%@/%@/contents",transportTask.version.versionOwner,transportTask.version.versionFileId ,transportTask.version.versionObjectId];
//    NSURL* versionDownloadURL = [_operationManager.baseURL URLByAppendingPathComponent:fileDownloadPath];
//    NSURL* versionResumeDataURL = [self resumeFileURL:versionDownloadURL];
//    NSString *fileName = transportTask.version.versionFileName;
//    NSProgress* progress;
//    NSURLSessionDownloadTask* fileDownloadTask;
//    if ([[NSFileManager defaultManager] fileExistsAtPath:versionResumeDataURL.path]) {
//        NSData *fileResumeData = [NSData dataWithContentsOfURL:versionDownloadURL];
//        [[NSFileManager defaultManager] removeItemAtURL:versionDownloadURL error:nil];
//        fileDownloadTask = [_sessionManager downloadTaskWithResumeData:fileResumeData progress:&progress destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
//            return [[targetPath URLByDeletingLastPathComponent] URLByAppendingPathComponent:fileName];
//        } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
//            completionHandler(response,filePath.path,error);
//        }];
//    } else {
//        NSMutableURLRequest* fileDownloadRequest = [NSMutableURLRequest requestWithURL:versionDownloadURL];
//        [fileDownloadRequest setValue:self.token forHTTPHeaderField:@"Authorization"];
//        fileDownloadTask = [_sessionManager downloadTaskWithRequest:fileDownloadRequest progress:&progress destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
//            return [[targetPath URLByDeletingLastPathComponent] URLByAppendingPathComponent:fileName];
//        } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
//            completionHandler(response,filePath.path,error);
//        }];
//    }
//    taskProgressCallback(fileDownloadTask, progress);
//}

- (NSURL*) resumeFileURL:(NSURL*) requestUrl {
    if (!requestUrl) {
        return nil;
    }
    NSString* urlStr = requestUrl.absoluteString;
    NSFileManager* fileManager =[NSFileManager defaultManager];
    NSURL* userCache  = [[fileManager URLsForDirectory:NSCachesDirectory inDomains:NSUserDomainMask] lastObject];
    if (userCache) {
        return [userCache URLByAppendingPathComponent:[self hashFileName:urlStr] isDirectory:NO];
    }
    return nil;
}

- (NSString*) hashFileName:(NSString*) str {
    const char* cstr = [str UTF8String];
    static unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5(cstr, (CC_LONG)strlen(cstr), digest);
    NSMutableString* outMd5 = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH];
    for (int i=0; i<CC_MD5_DIGEST_LENGTH; ++i) {
        [outMd5 appendFormat:@"%02x", digest[i]];
    }
    [outMd5 appendString:@".tmp"];
    return outMd5;
}

- (void)downloadHeadImageWithUserId:(NSString *)userCloudId
                  completionHandler:(void (^)(NSURLResponse *, NSData *, NSError *))compleitonHandler {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *url = [NSString stringWithFormat:@"api/v2/users/image?id=%@",userCloudId];
    [_operationManager setBaseURL:appDelegate.remoteManager.httpService.uam];
    [_operationManager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        compleitonHandler(operation.response,responseObject,nil);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        compleitonHandler(operation.response,nil,error);
    }];
}



@end
