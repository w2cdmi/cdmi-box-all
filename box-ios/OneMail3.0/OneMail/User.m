//
//  User.m
//  OneMail
//
//  Created by cse  on 15/11/2.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "User.h"
#import "AppDelegate.h"


@implementation User

@dynamic userSortTimeKey;
@dynamic userEmail;
@dynamic userRecentContactFlag;
@dynamic userSystemStatus;
@dynamic userName;
@dynamic userLoginName;
@dynamic userCloudId;
@dynamic userDescription;
@dynamic userSortNameKey;
@dynamic userMyContactFlag;
@dynamic userRemark;
@dynamic userSingleId;
@dynamic userPhone;

+ (User *)getUserWithUserCloudId:(NSString *)userCloudId context:(NSManagedObjectContext*)context {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userCloudId = %@",userCloudId];
    return [User getUserWithPredicate:predicate context:context];
}

+ (User *)getUserWithUserSingleId:(NSString *)userSingleId context:(NSManagedObjectContext*)context {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userSingleId = %@",userSingleId];
    return [User getUserWithPredicate:predicate context:context];
}

+ (User *)getUserWithUserEmail:(NSString *)userEmail context:(NSManagedObjectContext *)context {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userEmail = %@",userEmail];
    return [User getUserWithPredicate:predicate context:context];
}

+ (User *)getUserWithUserLoginName:(NSString *)userLoginName context:(NSManagedObjectContext*)context {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userLoginName = %@",userLoginName];
    return [User getUserWithPredicate:predicate context:context];
}

+ (User *)getUserWithPredicate:(NSPredicate*)predicate context:(NSManagedObjectContext *)context{
    if (!context) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        context = appDelegate.localManager.managedObjectContext;
    }
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:context];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"userSortTimeKey" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    return [[context executeFetchRequest:request error:nil] lastObject];
}

+ (NSArray *)getUserArrayWithKey:(NSString *)keyWord context:(NSManagedObjectContext *)context{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!context) {
        context = appDelegate.localManager.managedObjectContext;
    }
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userEmail contains[cd] %@ OR userName contains[cd] %@ OR userLoginName contains [cd] %@",keyWord,keyWord,keyWord];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:context];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"userSortTimeKey" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    NSArray *userArray = [context executeFetchRequest:request error:nil];
    NSMutableArray *userMutableArray = [[NSMutableArray alloc] init];
    for (User *user in userArray) {
        if (![user.userCloudId isEqualToString: appDelegate.localManager.userCloudId]) {
            [userMutableArray addObject:user];
        }
    }
    NSArray *result = [[NSArray alloc] initWithArray:(NSArray*)userMutableArray];
    return result;
}

+ (NSArray*)getUserHasEmailArrayWithKey:(NSString *)keyWord context:(NSManagedObjectContext *)context {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!context) {
        context = appDelegate.localManager.managedObjectContext;
    }
    NSArray *userArray = [User getUserArrayWithKey:keyWord context:context];
    NSMutableArray *userMutableArray = [[NSMutableArray alloc] init];
    for (User *user in userArray) {
        if (user.userEmail) {
            [userMutableArray addObject:user];
        }
    }
    NSArray *result = [[NSArray alloc] initWithArray:(NSArray*)userMutableArray];
    return result;
}

+ (NSArray *)getRecentContactUsers:(NSManagedObjectContext *)context {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!context) {
        context = appDelegate.localManager.managedObjectContext;
    }
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userRecentContactFlag = %@",@(1)];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:context];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"userSortTimeKey" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    return [context executeFetchRequest:request error:nil];
}

+ (NSArray *)getMyContactUsers:(NSManagedObjectContext *)context {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!context) {
        context = appDelegate.localManager.managedObjectContext;
    }
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userMyContactFlag = %@",@(1)];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:context];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"userSortTimeKey" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    return [context executeFetchRequest:request error:nil];
}

+ (User*)userInsertWithInfo:(NSDictionary *)userInfo context:(NSManagedObjectContext *)context {
    NSString *userSingleId = [userInfo objectForKey:@"id"]?[[userInfo objectForKey:@"id"] stringValue]:[[userInfo objectForKey:@"userId"] stringValue];
    User *user;
    if (userSingleId) {
        user = [User getUserWithUserSingleId:userSingleId context:context];
    }
    if (!user) {
        user = [NSEntityDescription insertNewObjectForEntityForName:@"User" inManagedObjectContext:context];
    }
    user.userSingleId = userSingleId;
    user.userMyContactFlag = @(0);
    user.userRecentContactFlag = @(0);
    user.userRemark = nil;
    user.userPhone = nil;
    [user setUserInfo:userInfo];
    return user;
}

- (void)setUserInfo:(NSDictionary *)userInfo {
    self.userCloudId = [[userInfo objectForKey:@"cloudUserId"] stringValue];
    if ([userInfo objectForKey:@"name"] && ![self.userName isEqualToString:[userInfo objectForKey:@"name"]]) {
        self.userName = [userInfo objectForKey:@"name"];
        self.userSortNameKey = [self sortNameKey:[userInfo objectForKey:@"name"]];
    }
    self.userLoginName = [userInfo objectForKey:@"loginName"];
    if ([userInfo objectForKey:@"email"] && ![self.userEmail isEqualToString:[userInfo objectForKey:@"email"]]) {
        self.userEmail = [userInfo objectForKey:@"email"];
    }
    self.userDescription = [userInfo objectForKey:@"description"];
    self.userSystemStatus = [userInfo objectForKey:@"status"];
    self.userSortTimeKey = [NSDate date];
    if ([userInfo objectForKey:@"phone"]) {
        self.userPhone = [userInfo objectForKey:@"phone"];
    }
}

- (NSString*) sortNameKey:(NSString*)userName {
    NSMutableString* fname = [NSMutableString stringWithString:userName];
    CFRange range = CFRangeMake(0, 1);
    BOOL bSuccess = CFStringTransform((__bridge CFMutableStringRef)fname, &range, kCFStringTransformToLatin, NO);
    if (bSuccess) {
        bSuccess = CFStringTransform((__bridge CFMutableStringRef)fname, &range, kCFStringTransformStripCombiningMarks, NO);
    }
    NSString* firstLetter = nil;
    if (bSuccess && range.length > 0) {
        NSRange nsRange = NSMakeRange(range.location, 1);
        firstLetter = [[fname substringWithRange:nsRange] uppercaseString];
    }
    if (firstLetter && ([firstLetter compare:@"A"] < 0 || [firstLetter compare:@"Z"] > 0)) {
        firstLetter = @"#";
    }
    return firstLetter?firstLetter:@"#";
}

+ (User*)userInsertWithEmail:(NSString *)userEmail context:(NSManagedObjectContext *)context {
    User *user = [User getUserWithUserEmail:userEmail context:context];
    if (!user) {
        user = [NSEntityDescription insertNewObjectForEntityForName:@"User" inManagedObjectContext:context];
    }
    user.userName = [userEmail componentsSeparatedByString:@"@"].firstObject;
    user.userEmail = userEmail;
    return user;
}

- (void)userRemove{
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:self.managedObjectContext];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sessionUsers = %@",self.userEmail];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"sessionId" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    NSArray *sessions = [self.managedObjectContext executeFetchRequest:request error:nil];
    if (sessions.count > 0) {
        for (Session *session in sessions) {
            [session removeSession];
        }
    }
    [self.managedObjectContext deleteObject:self];
}

- (NSString*)userHeadIconPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *headIconDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"HeadIcon"];
    BOOL isDerictory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:headIconDirectory isDirectory:&isDerictory] || !isDerictory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:headIconDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [headIconDirectory stringByAppendingPathComponent:self.userSingleId];
}

- (void)saveUserHeadIcon:(NSData*)data {
    NSString *userHeadIconPath = [self userHeadIconPath];
    if ([[NSFileManager defaultManager] fileExistsAtPath:userHeadIconPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:userHeadIconPath error:nil];
    }
    [[NSFileManager defaultManager] createFileAtPath:userHeadIconPath contents:data attributes:nil];

}

- (void)saveUserRemarks:(NSString *)remarks {
    if ([self.userRemark isEqualToString:remarks]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    [ctx performBlockAndWait:^{
        User *shadow = (User*)[ctx objectWithID:self.objectID];
        shadow.userRemark = remarks;
        [ctx save:nil];
    }];
}

- (void)saveuserPhone:(NSString *)phone {
    if ([self.userPhone isEqualToString:phone]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    [ctx performBlockAndWait:^{
        User *shadow = (User*)[ctx objectWithID:self.objectID];
        shadow.userPhone = phone;
        [ctx save:nil];
    }];
}

- (void)saveUserEmail:(NSString *)email {
    if ([self.userEmail isEqualToString:email]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    [ctx performBlockAndWait:^{
        User *shadow = (User*)[ctx objectWithID:self.objectID];
        shadow.userEmail = email;
        [ctx save:nil];
    }];
}

- (void)changeUserRecentContactFlag:(NSNumber *)userRecentContactFlag {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    
    if (self.userRecentContactFlag.integerValue == userRecentContactFlag.integerValue) {
        [ctx performBlockAndWait:^{
            User *shadow = (User*)[ctx objectWithID:self.objectID];
            shadow.userSortTimeKey = [NSDate date];
            [ctx save:nil];
        }];
    } else {
        NSArray *recentContactArray = [User getRecentContactUsers:nil];
        [ctx performBlockAndWait:^{
            if (recentContactArray.count >= 20) {
                User *lastRecentContactUser = recentContactArray.lastObject;
                User *lastShadow = (User*)[ctx objectWithID:lastRecentContactUser.objectID];
                lastShadow.userRecentContactFlag = @(0);
            }
            User *shadow = (User*)[ctx objectWithID:self.objectID];
            shadow.userRecentContactFlag = @(1);
            shadow.userSortTimeKey = [NSDate date];
            [ctx save:nil];
        }];
    }
}

- (void)changeUserMyContactFlag:(NSNumber *)userMyContactFlag {
    if (self.userMyContactFlag.integerValue == userMyContactFlag.integerValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    [ctx performBlockAndWait:^{
        User *shadow = (User*)[ctx objectWithID:self.objectID];
        shadow.userMyContactFlag = userMyContactFlag;
        [ctx save:nil];
    }];
}

@end
