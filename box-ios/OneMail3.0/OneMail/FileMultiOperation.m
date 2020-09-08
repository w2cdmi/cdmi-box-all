//
//  FileMultiOperation.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "FileMultiOperation.h"

@interface FileMultiOperation()

@property (atomic, strong) NSMutableSet* succeededSet;
@property (atomic, strong) NSMutableSet* failedSet;

@end

@implementation FileMultiOperation

- (id) init {
    if (self = [super init]) {
        _succeededSet = [[NSMutableSet alloc] init];
        _failedSet = [[NSMutableSet alloc] init];
    }
    return self;
}

- (void) onSuceess:(id) obj {
    if (obj) {
        [_succeededSet addObject:obj];
        [self checkFinished];
    }
}

- (void) onFailed:(id) obj {
    if (obj) {
        [_failedSet addObject:obj];
        [self checkFinished];
    }
}

- (void) checkFinished {
    NSMutableSet* tmpSet = [NSMutableSet setWithSet:_callingObj];
    [tmpSet minusSet:_failedSet];
    [tmpSet minusSet:_succeededSet];
    if (tmpSet.count == 0) {
        _finished = YES;
        if (_completionBlock) {
            _completionBlock(_succeededSet, _failedSet);
        }
    }
}

- (NSSet*) getSucceedObj {
    return _succeededSet;
}

- (NSSet*) getFailedObj {
    return _failedSet;
}
@end
