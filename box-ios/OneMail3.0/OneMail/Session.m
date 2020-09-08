//
//  Session.m
//  OneMail
//
//  Created by cse  on 16/1/18.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "Session.h"
#import "Message.h"
#import "AppDelegate.h"


@implementation Session

@dynamic sessionId;
@dynamic sessionLastMessageId;
@dynamic sessionSyncDate;
@dynamic sessionNotification;
@dynamic sessionScreenName;
@dynamic sessionTitle;
@dynamic sessionTopDate;
@dynamic sessionTopFlag;
@dynamic sessionUsers;
@dynamic sessionOwner;

+ (Session *)getSessionWithSessionId:(NSString *)sessionId ctx:(NSManagedObjectContext*)ctx {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!ctx) {
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sessionId = %@ AND sessionOwner = %@",sessionId,appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"sessionSyncDate" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [[ctx executeFetchRequest:request error:nil] lastObject];
}

+ (Session *)getSessionWithSessionUsers:(NSString *)sessionUsers ctx:(NSManagedObjectContext *)ctx {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if (!ctx) {
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sessionUsers = %@ AND sessionOwner = %@",sessionUsers,appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Session" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"sessionSyncDate" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [[ctx executeFetchRequest:request error:nil] lastObject];
}

+ (Session *)sessionInsertWithSessionId:(NSString*)sessionId ctx:(NSManagedObjectContext *)ctx {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    Session *session = [Session getSessionWithSessionId:sessionId ctx:ctx];
    
    if (!session) {
        session = [NSEntityDescription insertNewObjectForEntityForName:@"Session" inManagedObjectContext:ctx];
        session.sessionId = sessionId;
        session.sessionNotification = @(0);
        session.sessionScreenName = @(0);
        session.sessionTopDate = nil;
        session.sessionTopFlag = @(0);
        session.sessionOwner = appDelegate.localManager.userCloudId;
    }
    return session;
}

- (NSInteger)sessionMessageCount {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"messageSessionId = %@ AND messageOwner = %@",self.sessionId,self.sessionOwner];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"messageReceiveDate" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    NSArray *array = [ctx executeFetchRequest:request error:nil];
    return array.count;
}

- (NSInteger)sessionUnreadMessageCount {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"messageSessionId = %@ AND messageOwner = %@ AND messageReadFlag = %@",self.sessionId,self.sessionOwner,@(0)];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"messageReceiveDate" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    NSArray *array = [ctx executeFetchRequest:request error:nil];
    return array.count;
}

- (void)sessionUnreadMessageReset {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"messageSessionId = %@ AND messageOwner = %@ AND messageReadFlag = %@",self.sessionId,self.sessionOwner,@(0)];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"messageReceiveDate" ascending:NO];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    NSArray *array = [ctx executeFetchRequest:request error:nil];
    for (Message *message in array) {
        [message saveMessageRead:@(1)];
    }
    return;
}

- (void)removeSession {
    NSArray *messages = [Message getMessagesWithSessionId:self.sessionId ctx:self.managedObjectContext];
    for (Message *message in messages) {
        Message *shadow = (Message*)[self.managedObjectContext objectWithID:message.objectID];
        [shadow removeMessage];
    }
    [self.managedObjectContext deleteObject:self];
}

- (void)saveSessionLastMessageId:(Message*)message {
    if ([self.sessionLastMessageId isEqualToString:message.messageId]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Session *shadow = (Session*)[ctx objectWithID:self.objectID];
        shadow.sessionLastMessageId = message.messageId;
        shadow.sessionSyncDate = message.messageReceiveDate;
        [ctx save:nil];
    }];
}

- (void)saveSessionNotification:(NSNumber *)sessionNotification {
    if (self.sessionNotification.integerValue == sessionNotification.integerValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Session *shadow = (Session*)[ctx objectWithID:self.objectID];
        shadow.sessionNotification = sessionNotification;
        [ctx save:nil];
    }];
}

- (void)saveSessionScreenName:(NSNumber *)sessionScreenName {
    if (self.sessionScreenName.integerValue == sessionScreenName.integerValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Session *shadow = (Session*)[ctx objectWithID:self.objectID];
        shadow.sessionScreenName = sessionScreenName;
        [ctx save:nil];
    }];
}

- (void)saveSessionTitle:(NSString *)sessionTitle {
    if ([self.sessionTitle isEqualToString:sessionTitle]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Session *shadow = (Session*)[ctx objectWithID:self.objectID];
        shadow.sessionTitle = sessionTitle;
        [ctx save:nil];
    }];
}

- (void)saveSessionTopFlag:(NSNumber *)sessionTopFlag {
    if (self.sessionTopFlag.integerValue == sessionTopFlag.integerValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Session *shadow = (Session*)[ctx objectWithID:self.objectID];
        shadow.sessionTopFlag = sessionTopFlag;
        [ctx save:nil];
    }];
}

- (void)saveSessionUsers:(NSString *)sessionUsers {
    if ([self.sessionUsers isEqualToString:sessionUsers]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Session *shadow = (Session*)[ctx objectWithID:self.objectID];
        shadow.sessionUsers = sessionUsers;
        [ctx save:nil];
    }];
}




@end
