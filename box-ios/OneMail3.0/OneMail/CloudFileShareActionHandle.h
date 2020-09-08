//
//  CloudFileShareActionHandle.h
//  OneMail
//
//  Created by cse  on 16/1/15.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "DataCommon.h"

@class File;

typedef void (^ShareCompletion)(File* file, NSSet* shareSucceeded, NSSet* shareFailed, NSSet* cancelSucceeded, NSSet* cancelFailed);

@interface CloudFileShareOperationHandle : NSObject
@property (nonatomic, copy) ShareCompletion completionBlock;
@property (nonatomic, strong) File* file;
@property (nonatomic, strong) NSSet* callingObj;
@property (nonatomic, readonly, getter = getShareSucceedObj)  NSSet* ShareSucceedObj;
@property (nonatomic, readonly, getter = getShareFailedObj)   NSSet* ShareFailedObj;
@property (nonatomic, readonly, getter = getCancelSucceedObj) NSSet* CancelSucceedObj;
@property (nonatomic, readonly, getter = getCancelFailedObj)  NSSet* CancelFailedObj;

@property (nonatomic, readonly) BOOL finished;

- (void) onShareSuceess:(id) obj;
- (void) onShareFailed:(id) obj;
- (void) onCancelSuceess:(id) obj;
- (void) onCancelFailed:(id) obj;

@end

@interface CloudFileShareActionHandle : NSObject

@property (nonatomic, strong) File *shareFile;                   /*需要共享文件*/
@property (nonatomic, strong) NSMutableArray *sharedUserArray;   /*已经共享的用户*/
@property (nonatomic, strong) NSMutableArray *searchUserArray;   /*希望共享的用户*/
@property (nonatomic, copy)   NSString *descriptionMessage;
@property (nonatomic, weak)   UIViewController *searchView;

- (void)sendShareRequest:(void(^)())block;

@end

