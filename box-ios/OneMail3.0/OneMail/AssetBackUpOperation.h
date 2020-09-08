//
//  AssetBackUpOperation.h
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Asset;
@class CloudBackUpViewController;

@interface AssetBackUpOperation : NSObject

@property (nonatomic, strong) Asset *backUpAsset;
@property (nonatomic, assign) NSInteger backUpTotalCount;

@property (nonatomic, weak) CloudBackUpViewController *backUpViewController;

- (void) backUpControl;
- (BOOL) backUpPauseStatus;
- (NSUInteger) backUpRemainCount;

@end
