//
//  ErrorFormat.h
//  HttpService
//
//  Created by cse on 7/3/14.
//  Copyright (c) 2014 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HttpConst.h"
#import "AFHTTPRequestOperation.h"

@interface ErrorFormat : NSObject<NSObject>

+(ErrorType)format:(NSError *)error :(AFHTTPRequestOperation *)operation;
@end
