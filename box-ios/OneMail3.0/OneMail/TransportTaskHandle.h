//
//  TransportTaskHandle.h
//  OneMail
//
//  Created by cse  on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"

@class TransportTask;
@interface TransportTaskHandle : NSObject

@property float taskFraction;
@property int64_t taskTotalUnitCount;
@property int64_t taskCompleteUnitCount;
@property (strong, nonatomic) NSProgress *taskProgress;
@property (strong, nonatomic) TransportTask *transportTask;
@property (weak, nonatomic) NSURLSessionTask *sessionTask;
@property (weak, nonatomic) AFHTTPRequestOperation *requestOperation;

- (id) initWithTranportTask:(TransportTask*)transportTask;

- (void) initting;
- (void) resume;
- (void) running;
- (void) waiting;
- (void) suspend;
- (void) success;
- (void) failed;
- (void) cancel;
- (void) waitNetwork;

- (BOOL)hadCacheFile:(NSString*)cachePath;
- (void)savePath:(NSString*)fromPath toPath:(NSString*)toPath;

@end
