//
//  TransportUploadTaskManager.m
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TransportUploadTaskManager.h"
#import <CommonCrypto/CommonCrypto.h>
#import "AppDelegate.h"
#import "UIImage+fixOrientation.h"
#import "FileMD5Hash.h"
#import "TransportTask.h"
#import "File.h"


@interface TransportUploadTaskManager ()

@property (nonatomic, strong) AFHTTPSessionManager* sessionManager;
@property (nonatomic, strong) AFHTTPRequestOperationManager* operationManager;
@property (nonatomic, strong) NSURLSessionConfiguration* config;

@end

@implementation TransportUploadTaskManager
- (id) initWithBaseURL:(NSURL *) baseURL sessionConfiguration:(NSURLSessionConfiguration *) configuration {
    if (self = [super init]) {
        //AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSMutableArray *Array = [[NSMutableArray alloc]init];
        [Array addObject:@"text/plain"];
        [Array addObject:@"text/html"];
        [Array addObject:@"text/json"];
        [Array addObject:@"application/json"];
        [Array addObject:@"text/javascript"];
        [Array addObject:@"image/png"];
        [Array addObject:@"multipart/form-data"];
        NSSet* accepts = [NSSet setWithArray:Array];
        _sessionManager = [[AFHTTPSessionManager alloc] initWithBaseURL:baseURL sessionConfiguration:configuration];
        _sessionManager.responseSerializer.acceptableContentTypes = accepts;
        //_sessionManager.completionQueue = appDelegate.upload_queue;
        
        _operationManager = [[AFHTTPRequestOperationManager alloc] initWithBaseURL:baseURL];
        _operationManager.responseSerializer.acceptableContentTypes = accepts;
        _operationManager.requestSerializer = [AFJSONRequestSerializer serializer];
        //_operationManager.completionQueue = appDelegate.upload_queue;
        
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
    
}

- (void)setSecurityPolicy:(AFSecurityPolicy *)securityPolicy {
    _securityPolicy = securityPolicy;
    _sessionManager.securityPolicy = securityPolicy;
    _operationManager.securityPolicy = securityPolicy;
}

- (void) uploadWithTask:(TransportTask*)transportTask
              preUpload:(void (^)(id, NSString *))preUploadCallback
           taskProgress:(void (^)(NSURLSessionUploadTask *, NSProgress *))taskProgressCallback
      completionHandler:(void (^)(AFHTTPRequestOperation *, NSError *))completionHandler {
    __block NSError* error;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *fileCacheLocalPath = [transportTask.file fileCacheLocalPath];
    NSString* fileName = transportTask.file.fileName;
    NSString* fileUploadPath = transportTask.taskLoadPath;
    NSString* fileParent = transportTask.file.fileParent;
    NSString* fileId = transportTask.file.fileId;
    NSString* fileOwner = transportTask.file.fileOwner;
    if (!fileCacheLocalPath || ![[NSFileManager defaultManager] fileExistsAtPath:fileCacheLocalPath]) {
        error = [NSError errorWithDomain:NSCocoaErrorDomain code:NSFileNoSuchFileError userInfo:@{NSLocalizedDescriptionKey:[NSString stringWithFormat:@"upload failed file:%@ parent:%@", fileCacheLocalPath, fileParent]}];
        completionHandler(nil, error);
    }
    NSDictionary* fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:fileCacheLocalPath error:&error];
    if (error) {
        completionHandler(nil,error);
        return;
    }
    NSNumber* fileSize = [fileAttributes objectForKey:NSFileSize];
    NSString* fileMD5 = [FileMD5Hash computeMD5HashOfFileInPath:fileCacheLocalPath];
    NSString* fileBlockMD5= [FileMD5Hash computeBlockMD5HashOfFileInPath:fileCacheLocalPath fileSize:(NSUInteger)[fileSize longLongValue]];
    
    if (!fileUploadPath) {
        if (!fileParent || !fileCacheLocalPath || !fileMD5) {
            error = [NSError errorWithDomain:NSCocoaErrorDomain code:NSFileNoSuchFileError userInfo:@{NSLocalizedDescriptionKey:[NSString stringWithFormat:@"upload failed file:%@ parent:%@ md5:%@", fileCacheLocalPath, fileParent, fileMD5]}];
            completionHandler(nil,error);
            return;
        }
        NSDictionary* param;
        if (fileBlockMD5) {
            param = @{@"name":fileName, @"parent":fileParent, @"size":fileSize, @"md5":fileMD5, @"blockMD5":fileBlockMD5};
        } else {
            param = @{@"name":fileName, @"parent":fileParent, @"size":fileSize, @"md5":fileMD5};
        }
        [_operationManager setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [_operationManager PUT:[NSString stringWithFormat:@"api/v2/files/%@/", fileOwner] parameters:param success:^(AFHTTPRequestOperation *operation, id responseObject) {
            NSString* fileId = [[responseObject objectForKey:@"fileId"] stringValue];
            NSString* fileUploadPath = [responseObject objectForKey:@"uploadUrl"];
            if (fileId && fileUploadPath) {
                preUploadCallback(fileId,fileUploadPath);
                [_operationManager PUT:[NSString stringWithFormat:@"%@?parts",fileUploadPath] parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
                    NSMutableURLRequest* request = [_operationManager.requestSerializer requestWithMethod:@"PUT" URLString:[NSString stringWithFormat:@"%@?partId=1",fileUploadPath] parameters:nil error:&error];
                    if (error) {
                        completionHandler(nil,error);
                        return;
                    } else {
                        NSProgress* progress;
                        NSURLSessionUploadTask* task = [_sessionManager uploadTaskWithRequest:request fromFile:[NSURL fileURLWithPath:fileCacheLocalPath] progress:&progress completionHandler:^(NSURLResponse *response, id responseObject, NSError *error) {
                            if (error) {
                                completionHandler(nil,error);
                                return;
                            }
                            NSMutableArray *part = [[NSMutableArray alloc]init];
                            [part addObject:[[NSDictionary alloc]initWithObjectsAndKeys:[NSNumber numberWithInt:1],@"partId",nil]];
                            NSDictionary *parmater = [[NSDictionary alloc]initWithObjectsAndKeys:part,@"parts",nil];
                            [_operationManager PUT:[NSString stringWithFormat:@"%@?commit=true",fileUploadPath] parameters:parmater success:^(AFHTTPRequestOperation *operation, id responseObject) {
                                completionHandler(operation,error);
                            } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                                completionHandler(operation,error);
                            }];
                        }];
                        taskProgressCallback(task,progress);
                    }
                } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                    completionHandler(operation,error);
                }];
            } else {
                preUploadCallback(responseObject,nil);
                completionHandler(operation,nil);
            }
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation,error);
        }];
    } else {
        [_operationManager GET:fileUploadPath parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
            NSMutableArray *parts = [[NSMutableArray alloc]initWithArray:[responseObject objectForKey:@"parts"]];
            NSMutableArray *partsArray = [[NSMutableArray alloc]initWithCapacity:[parts count]+1];
            NSUInteger totalBytesSend = 0;
            if ([parts count] > 0) {
                long lastSize = [[[parts lastObject] objectForKey:@"size"] longValue];
                if (lastSize < 5*1024*1024) {
                    [parts removeLastObject];
                }
            }
            if ([parts count] == 0) {
                [transportTask saveTransportLoadPath:nil];
                [self uploadWithTask:transportTask preUpload:preUploadCallback taskProgress:taskProgressCallback completionHandler:completionHandler];
                return;
            }
            for (int i = 0; i<[parts count]; i++) {
                totalBytesSend = totalBytesSend + [[[parts objectAtIndex:i] objectForKey:@"size"] longValue];
                [partsArray addObject:[[NSDictionary alloc]initWithObjectsAndKeys:[NSNumber numberWithInt:i+1],@"partId",nil]];
            }
            NSRange range = NSMakeRange((NSUInteger)totalBytesSend, (NSUInteger)[fileSize longValue]-(NSUInteger)totalBytesSend);
            if (0 == range.length) {
                NSDictionary *parmater = [[NSDictionary alloc]initWithObjectsAndKeys:partsArray,@"parts",nil];
                [_operationManager PUT:[NSString stringWithFormat:@"%@?commit=true",fileUploadPath] parameters:parmater success:^(AFHTTPRequestOperation *operation, id responseObject) {
                    completionHandler(operation,error);
                } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                    completionHandler(operation,error);
                }];
            } else {
                [partsArray addObject:[[NSDictionary alloc]initWithObjectsAndKeys:@(parts.count+1),@"partId",nil]];
                NSDictionary *parmater = [[NSDictionary alloc]initWithObjectsAndKeys:partsArray,@"parts",nil];
                NSURL *fileURLNew = [self resumeUploadDataWithURL:[NSURL URLWithString:fileCacheLocalPath] range:range error:error];
                if (error) {
                    completionHandler(nil,error);
                    return;
                }
                NSMutableURLRequest *request = [_operationManager.requestSerializer requestWithMethod:@"PUT" URLString:[NSString stringWithFormat:@"%@?partId=%ld",fileUploadPath,(long)(parts.count+1)] parameters:nil error:&error];
                if (error) {
                    completionHandler(nil,error);
                    return;
                } else {
                    NSProgress* progress;
                    NSURLSessionUploadTask* task = [_sessionManager uploadTaskWithRequest:request fromFile:[NSURL fileURLWithPath:fileURLNew.path] progress:&progress completionHandler:^(NSURLResponse *response, id responseObject, NSError *error) {
                        if (error) {
                            completionHandler(nil,error);
                            return;
                        }
                        [SNLog Log:LInfo :@"%@ upload success partId:%ld",fileName,[parts count]+1];
                        [_operationManager PUT:[NSString stringWithFormat:@"%@?commit=true",fileUploadPath] parameters:parmater success:^(AFHTTPRequestOperation *operation, id responseObject) {
                            completionHandler(operation,error);
                        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                            completionHandler(operation,error);
                        }];
                    }];
                    taskProgressCallback(task, progress);
                }
            }
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            if ([operation.response statusCode] == 403) {
                NSString *refreshToken_url=[NSString stringWithFormat:@"api/v2/files/%@/%@/refreshurl",fileOwner,fileId];
                NSDictionary *parmater=[[NSDictionary alloc]initWithObjectsAndKeys:fileUploadPath,@"uploadUrl", nil];
                [_operationManager setBaseURL:appDelegate.remoteManager.httpService.ufm];
                [_operationManager PUT:refreshToken_url parameters:parmater success:^(AFHTTPRequestOperation *operation, id responseObject){
                    NSString* newURL = [responseObject objectForKey:@"uploadUrl"];
                    preUploadCallback(fileId,newURL);
                    [self uploadWithTask:transportTask preUpload:preUploadCallback taskProgress:taskProgressCallback completionHandler:completionHandler];
                } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                    if (operation.response.statusCode == 404) {
                        [transportTask saveTransportLoadPath:nil];
                        [self uploadWithTask:transportTask preUpload:preUploadCallback taskProgress:taskProgressCallback completionHandler:completionHandler];
                    } else {
                        completionHandler(operation,error);
                    }
                }];
            }else if([operation.response statusCode] == 404){
                [transportTask saveTransportLoadPath:nil];
                [self uploadWithTask:transportTask preUpload:preUploadCallback taskProgress:taskProgressCallback completionHandler:completionHandler];
            }else if ([operation.response statusCode] == 412){
                [transportTask saveTransportLoadPath:nil];
                [self uploadWithTask:transportTask preUpload:preUploadCallback taskProgress:taskProgressCallback completionHandler:completionHandler];
            } else {
                completionHandler(operation,error);
            }
        }];
    }
}

+ (BOOL) photoFile:(TransportTask*)task {
    NSString* extension = [[task.file.fileName pathExtension] lowercaseString];
    if ([extension isEqual:@"jpeg"]||[extension isEqual:@"jpg"]||
        [extension isEqual:@"png"]||[extension isEqual:@"bmp"]||
        [extension isEqual:@"gif"]||[extension isEqual:@"tiff"]||
        [extension isEqual:@"raw"]||[extension isEqual:@"ppm"]||
        [extension isEqual:@"pgm"]||[extension isEqual:@"pbm"]||
        [extension isEqual:@"pnm"]||[extension isEqual:@"webp"]) {
        return YES;
    }
    return NO;
}

-(NSURL*)resumeUploadDataWithURL:(NSURL*)inFileURL range:(NSRange)range error:(NSError*)error {
    error = nil;
    NSFileHandle *inFileHandle = [NSFileHandle fileHandleForReadingAtPath:inFileURL.path];
    NSString *outFilePath = [[inFileURL absoluteString] stringByAppendingString:@".tmp"];
    NSURL *outFileURL = [NSURL URLWithString:outFilePath];
    if ([[NSFileManager defaultManager] fileExistsAtPath:outFileURL.path]) {
        [[NSFileManager defaultManager] removeItemAtPath:outFileURL.path error:&error];
        if (error) {
            return nil;
        }
    }
    [[NSFileManager defaultManager] createFileAtPath:outFileURL.path contents:nil attributes:nil];
    NSFileHandle *outFileHandle = [NSFileHandle fileHandleForWritingAtPath:outFileURL.path];
    if (inFileHandle && outFileHandle) {
        [outFileHandle truncateFileAtOffset:0];
        [inFileHandle seekToFileOffset:range.location];
        NSData *data = [inFileHandle readDataToEndOfFile];
        [outFileHandle writeData:data];
        [inFileHandle closeFile];
        [outFileHandle closeFile];
        return outFileURL;
    } else {
        return nil;
    }
}

- (void)uploadHeadImage:(UIImage*)image compleitonHandler:(void (^)(AFHTTPRequestOperation *operation, NSError *error))completionHandler{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [_operationManager setBaseURL:appDelegate.remoteManager.httpService.uam];
    NSData *data = UIImageJPEGRepresentation(image, 1.0f);
    if (data) {
        NSDictionary *dic = [NSDictionary dictionaryWithObject:data forKey:@"fileInputStream"];
        [_operationManager PUT:[NSString stringWithFormat:@"api/v2/users/image"] parameters:dic success:^(AFHTTPRequestOperation *operation, id responseObject) {
            completionHandler(operation,nil);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            completionHandler(operation,error);
        }];
    }
}

@end
