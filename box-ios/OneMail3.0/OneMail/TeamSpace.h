//
//  TeamSpace.h
//  OneMail
//
//  Created by cse  on 15/11/5.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class File;

@interface TeamSpace : NSManagedObject

@property (nonatomic, retain) NSString * teamSortTimeKey;
@property (nonatomic, retain) NSDate   * teamDate;
@property (nonatomic, retain) NSString * teamMemberNum;
@property (nonatomic, retain) NSString * teamId;
@property (nonatomic, retain) NSString * teamName;
@property (nonatomic, retain) NSString * teamOwner;
@property (nonatomic, retain) NSString * teamOwnerName;
@property (nonatomic, retain) NSString * teamUserId;
@property (nonatomic, retain) NSString * teamRelationId;
@property (nonatomic, retain) NSString * teamRole;
@property (nonatomic, retain) NSString * role;
@property (nonatomic, retain) NSNumber * teamUsedSpace;
@property (nonatomic, retain) File     * teamFile;

+ (TeamSpace*) insertWithTeamInfo:(NSDictionary*)teamInfo context:(NSManagedObjectContext*)ctx;
- (BOOL) sameWithTeamInfo:(NSDictionary*)teamInfo;
- (void) remove;

@end
