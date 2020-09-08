//
//  AssetGroup.h
//  OneMail
//
//  Created by cse  on 15/12/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface AssetGroup : NSManagedObject

@property (nonatomic, retain) NSString * groupURL;
@property (nonatomic, retain) NSString * groupKey;
@property (nonatomic, retain) NSString * groupName;
@property (nonatomic, retain) NSString * groupOwner;
@property (nonatomic, retain) NSNumber * groupBackUpFlag;

+ (AssetGroup*)insertGroupWithGroupInfo:(NSDictionary*)groupInfo;
+ (AssetGroup*)searchGroupWithGroupKey:(NSString*)key;
- (void)remove;

+ (NSArray*)getAllGroup;
+ (NSArray*)getBackupGroup;

- (void)saveGroupBackUpFlag:(NSNumber*)groupBackUpFlag;

@end
