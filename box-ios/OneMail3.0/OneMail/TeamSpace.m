//
//  TeamSpace.m
//  OneMail
//
//  Created by cse  on 15/11/5.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "TeamSpace.h"
#import "AppDelegate.h"
#import "File.h"


@implementation TeamSpace

@dynamic teamSortTimeKey;
@dynamic teamDate;
@dynamic teamMemberNum;
@dynamic teamId;
@dynamic teamName;
@dynamic teamOwner;
@dynamic teamOwnerName;
@dynamic teamUserId;
@dynamic teamRelationId;
@dynamic teamFile;
@dynamic teamRole;
@dynamic role;
@dynamic teamUsedSpace;

+ (TeamSpace*) insertWithTeamInfo:(NSDictionary*)teamInfo context:(NSManagedObjectContext*)ctx {
    NSDictionary *teamDetailInfo = [teamInfo objectForKey:@"teamspace"];
    TeamSpace *teamSpace = [TeamSpace searchWithTeamInfo:teamDetailInfo context:ctx];
    if (!teamSpace) {
        teamSpace = [NSEntityDescription insertNewObjectForEntityForName:@"TeamSpace" inManagedObjectContext:ctx];
    }
    if (teamSpace) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        teamSpace.teamUserId = appDelegate.localManager.userCloudId;
        teamSpace.teamId = [[teamDetailInfo objectForKey:@"id"] stringValue];
        teamSpace.teamRole = [teamInfo objectForKey:@"teamRole"];
        teamSpace.role = [teamInfo objectForKey:@"role"];
        teamSpace.teamRelationId = [[teamInfo objectForKey:@"id"] stringValue];
        [teamSpace setTeamInfo:[teamInfo objectForKey:@"teamspace"]];
    }
    return teamSpace;
}

+ (TeamSpace*) searchWithTeamInfo:(NSDictionary*)teamInfo context:(NSManagedObjectContext*)ctx{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *teamId = [[teamInfo objectForKey:@"id"] stringValue];
    NSString *teamUserId = appDelegate.localManager.userCloudId;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"teamId = %@ AND teamUserId = %@",teamId,teamUserId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"TeamSpace" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    return [[ctx executeFetchRequest:request error:nil] lastObject];
}



- (BOOL) sameWithTeamInfo:(NSDictionary*)teamInfo {
    NSNumber *teamId = [teamInfo objectForKey:@"teamId"];
    if (![teamId.stringValue isEqual:self.teamId]) {
        return NO;
    }
    self.teamRole = [teamInfo objectForKey:@"teamRole"];
    self.role = [teamInfo objectForKey:@"role"];
    NSDictionary *teamDetailInfo = [teamInfo objectForKey:@"teamspace"];
    [self setTeamInfo:teamDetailInfo];
    return YES;
}

- (void)setTeamInfo:(NSDictionary*)teamInfo {
    self.teamDate = [NSDate dateWithTimeIntervalSince1970:[[teamInfo objectForKey:@"createdAt"] doubleValue]/1000];
    self.teamMemberNum = [[teamInfo objectForKey:@"curNumbers"] stringValue];
    self.teamName = [teamInfo objectForKey:@"name"];
    self.teamOwner = [[teamInfo objectForKey:@"ownedBy"] stringValue];
    self.teamOwnerName = [teamInfo objectForKey:@"ownedByUserName"];
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy/MM"];
    self.teamSortTimeKey = [formatter stringFromDate:self.teamDate];
    self.teamUsedSpace = [teamInfo objectForKey:@"spaceUsed"];
    File *teamFile = [File rootTeamSpaceFolder:self.teamId name:self.teamName];
    File *shadowFile = (File*)[self.managedObjectContext objectWithID:teamFile.objectID];
    self.teamFile = shadowFile;
    shadowFile.teamSpace = self;
}

- (void)remove {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileId = %@ AND fileOwner = %@",@"0",self.teamId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:appDelegate.localManager.managedObjectContext];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    File *file = [[appDelegate.localManager.managedObjectContext executeFetchRequest:request error:nil] lastObject];
    [file fileRemove:nil];
    [self.managedObjectContext deleteObject:self];
}
@end
