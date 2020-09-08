//
//  CloudFileShareActionHandle.m
//  OneMail
//
//  Created by cse  on 16/1/15.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "CloudFileShareActionHandle.h"
#import "File+Remote.h"
#import "User.h"
#import "User+Remote.h"
#import "AppDelegate.h"

@interface CloudFileShareOperationHandle()

@property (atomic, strong) NSMutableSet* shareSucceededSet;
@property (atomic, strong) NSMutableSet* shareFailedSet;
@property (atomic, strong) NSMutableSet* cancelSucceededSet;
@property (atomic, strong) NSMutableSet* cancelFailedSet;

@end

@implementation CloudFileShareOperationHandle
- (id) init {
    if (self = [super init]) {
        _shareSucceededSet = [[NSMutableSet alloc] init];
        _shareFailedSet = [[NSMutableSet alloc] init];
        _cancelSucceededSet = [[NSMutableSet alloc] init];
        _cancelFailedSet = [[NSMutableSet alloc] init];
    }
    return self;
}

- (void) onShareSuceess:(id)obj {
    if (obj) {
        [_shareSucceededSet addObject:obj];
        [self checkFinished];
    }
}

- (void) onShareFailed:(id)obj {
    if (obj) {
        [_shareFailedSet addObject:obj];
        [self checkFinished];
    }
}

- (void) onCancelSuceess:(id)obj {
    if (obj) {
        [_cancelSucceededSet addObject:obj];
        [self checkFinished];
    }
}

- (void) onCancelFailed:(id)obj {
    if (obj) {
        [_cancelFailedSet addObject:obj];
        [self checkFinished];
    }
}

- (void) checkFinished {
    NSMutableSet* tmpSet = [NSMutableSet setWithSet:_callingObj];
    [tmpSet minusSet:_shareSucceededSet];
    [tmpSet minusSet:_shareFailedSet];
    [tmpSet minusSet:_cancelSucceededSet];
    [tmpSet minusSet:_cancelFailedSet];
    if (tmpSet.count == 0) {
        _finished = YES;
        if (_completionBlock) {
            _completionBlock(_file, _shareSucceededSet, _shareFailedSet, _cancelSucceededSet, _cancelFailedSet);
        }
    }
}

- (NSSet*) getShareSucceedObj {
    return _shareSucceededSet;
}

- (NSSet*) getShareFailedObj {
    return _shareFailedSet;
}

- (NSSet*) getCancelSucceedObj {
    return _cancelSucceededSet;
}

- (NSSet*) getCancelFailedObj {
    return _cancelFailedSet;
}

@end



@interface CloudFileShareActionHandle ()

@property (nonatomic, strong) NSMutableSet *sharedUserSet;      /*已经共享用户*/
@property (nonatomic, strong) NSMutableSet *addShareUserSet;    /*新增的共享用户*/
@property (nonatomic, strong) NSMutableSet *deleteShareUserSet; /*取消的共享用户*/
@property (nonatomic, strong) NSMutableSet *usersCallingObj;    /*新增共享用户与取消的共享用户总和*/

@end

@implementation CloudFileShareActionHandle
- (void)sendShareRequest:(void (^)())block {
    
    NSMutableSet *searchUserSet = [[NSMutableSet alloc] initWithArray:self.searchUserArray];
    
    self.sharedUserSet = [[NSMutableSet alloc] initWithArray:self.sharedUserArray];
    
    self.addShareUserSet = [NSMutableSet setWithSet:searchUserSet];
    [self.addShareUserSet minusSet:self.sharedUserSet];
    
    self.deleteShareUserSet = [NSMutableSet setWithSet:self.sharedUserSet];
    [self.deleteShareUserSet minusSet:searchUserSet];
    
    self.usersCallingObj = [[NSMutableSet alloc] init];
    for (User *user in self.addShareUserSet) {
        [self.usersCallingObj addObject:user];
    }
    
    for (User *user in self.deleteShareUserSet) {
        [self.usersCallingObj addObject:user];
    }
    
    if ([self.usersCallingObj count] == 0) {
        block();return;
    }
    
    if (self.descriptionMessage) {
        
    }
    
    CloudFileShareOperationHandle* shareHandle = [[CloudFileShareOperationHandle alloc]init];
    shareHandle.file = self.shareFile;
    shareHandle.callingObj = self.usersCallingObj;
    shareHandle.completionBlock = ^(File* file, NSSet* shareSucceed, NSSet* shareFailed, NSSet* cancelSucceed, NSSet* cancelFailed){
        dispatch_async(dispatch_get_main_queue(), ^{
            if (shareSucceed.count == 0 && cancelSucceed.count == 0) {
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
            } else {
                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationSuccess", nil)];
            }
        });
        block();
    };
    
    for (NSString *userCloudId in self.addShareUserSet) {
        User *user = [User getUserWithUserCloudId:userCloudId context:nil];
//        [user availableUsers:^(id retobj) {
//            [self.shareFile fileShare:user message:self.descriptionMessage succeed:^(id retobj) {
//                dispatch_async(dispatch_get_main_queue(), ^{
//                    [shareHandle onShareSuceess:userCloudId];/*共享成功*/
//                });
//            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
//                dispatch_async(dispatch_get_main_queue(), ^{
//                    [shareHandle onShareFailed:userCloudId];/*共享失败*/
//                });
//            }];
//        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
//            dispatch_async(dispatch_get_main_queue(), ^{
//                [shareHandle onShareFailed:userCloudId];/*共享失败，用户开户失败*/
//            });
//        }];
        [self.shareFile fileShare:user message:self.descriptionMessage succeed:^(id retobj) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [shareHandle onShareSuceess:userCloudId];/*共享成功*/
            });
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [shareHandle onShareFailed:userCloudId];/*共享失败*/
            });
        }];
    }
    
    for (NSString *userCloudId in self.deleteShareUserSet) {
        User *user = [User getUserWithUserCloudId:userCloudId context:nil];
        [self.shareFile fileShareCancel:user succeed:^(id retobj) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [shareHandle onCancelSuceess:userCloudId];/*取消共享成功*/
            });
        } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [shareHandle onCancelFailed:userCloudId];/*取消共享失败*/
            });
        }];
    }
}

@end

