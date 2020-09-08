//
//  DataCommon.h
//  OneBox
//
//  Created by yemingxing on 7/9/14.
//  Copyright (c) 2014 www.huawei.com. All rights reserved.
//

#ifndef OneBox_DataCommon_h
#define OneBox_DataCommon_h

#import <Foundation/Foundation.h>

#define READ_BUFFER_SIZE 16 * 1024

typedef void (^HWRemoteSuccessBlock)(id retobj);
typedef void (^HWRemoteFailedBlock)(NSURLRequest* request, NSURLResponse* response, NSError* error,int errorType);


#define SERVICE_FAILED(args) \
if (failed) {\
NSLog(@"%@",error);\
failed(nil,nil,error,errorType);\
}\

//[SNLog Log:LError :@"%@ Failed, Number:%d, nserror:%@", args, errorType,error];\



#define SERVICE_SUCCEED(args) \
if(succeed) {\
succeed(responseObject);\
}

//[SNLog Log:LDebug :@"Suceed to %@ params:%@", args, responseObject];\

#endif
