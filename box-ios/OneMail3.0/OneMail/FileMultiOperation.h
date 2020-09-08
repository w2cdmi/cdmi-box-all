//
//  FileMultiOperation.h
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^FileMultiCompletion)(NSSet* succeeded, NSSet* failed);

@interface FileMultiOperation : NSObject

@property (nonatomic, copy) FileMultiCompletion completionBlock;
@property (nonatomic, strong) NSSet* callingObj;
@property (nonatomic, readonly, getter = getSucceedObj) NSSet* succeedObj;
@property (nonatomic, readonly, getter = getFailedObj) NSSet* failedObj;
@property (nonatomic, readonly) BOOL finished;

- (void) onSuceess:(id) obj;
- (void) onFailed:(id) obj;

@end
