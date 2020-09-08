//
//  SessionRelativeOperation.m
//  OneMail
//
//  Created by cse  on 16/1/20.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "SessionRelativeOperation.h"
#import "Session.h"
#import "Message.h"
#import "User+Remote.h"
#import "AppDelegate.h"

typedef void (^SessionUserCompletion)(NSSet* succeeded, NSSet* failed);

@interface SessionUserOperation : NSObject

@property (nonatomic, copy) SessionUserCompletion completionBlock;
@property (nonatomic, strong) NSSet* callingObj;
@property (nonatomic, strong) NSMutableSet* succeededSet;
@property (nonatomic, strong) NSMutableSet* failedSet;
@property (nonatomic, assign) BOOL finished;

@end

@implementation SessionUserOperation

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

@end


@interface SessionRelativeOperation ()

@property (nonatomic, strong) NSArray        *sessionMessageArray;

@end

@implementation SessionRelativeOperation

- (id)init {
    self = [super init];
    if (self) {
        
    }
    return self;
}

- (void)relativeSessionWithMessageArray:(NSArray *)messages {
    self.sessionMessageArray = [[NSArray alloc] initWithArray:messages];
    
}

- (void)relativeSessionWithHeaderMessage:(Message*)message completion:(void(^)())completion {
    
    BOOL messageHeaderTag = NO;
    if (!message.messageReferenceId) {
        messageHeaderTag = YES;
    } else {
        Message *lastMessage = [Message getMessageWithMessageId:message.messageReferenceId ctx:nil];
        if (!lastMessage) {
            messageHeaderTag = YES;
        }
    }
    
    if (!messageHeaderTag) return;
    
    __block Session *session;
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    UserSetting *userSetting = [UserSetting defaultSetting];
    
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        session = [Session sessionInsertWithSessionId:[userSetting.emailNextSessionId stringValue] ctx:ctx];
        [ctx save:nil];
    }];
    
    if (session) {
        session = (Session*)[appDelegate.localManager.managedObjectContext objectWithID:session.objectID];
    } else {
        completion();return;
    }
    
    NSMutableArray *sessionUserArray = [[NSMutableArray alloc] init];
    NSMutableArray *sessionUserEmailArray = [[NSMutableArray alloc] initWithArray:[message.messageReceiver componentsSeparatedByString:@","]];
    [sessionUserEmailArray addObject:message.messageSender];
    [sessionUserEmailArray addObjectsFromArray:[message.messageReceiver componentsSeparatedByString:@","]];
    NSString *sessionUserString = [CommonFunction stringFromArray:sessionUserEmailArray];
    [session saveSessionLastMessageId:message];
    
    [message saveMessageSessionId:session.sessionId];
    [message saveMessageOwner:session.sessionOwner];
    
    [sessionUserEmailArray removeObject:userSetting.emailAddress];
    session.sessionTitle = [CommonFunction stringFromArray:sessionUserEmailArray];
    
    session.sessionUsers = sessionUserString;
    [session saveSessionUsers:sessionUserString];
    
    SessionUserOperation *operation = [[SessionUserOperation alloc] init];
    operation.completionBlock = ^(NSSet *success,NSSet *failed){
        [session saveSessionTitle:[CommonFunction stringFromArray:sessionUserArray]];
        completion(session);
    };
    operation.callingObj = [NSSet setWithArray:sessionUserEmailArray];
    for (NSString *address in sessionUserEmailArray) {
        __block User *user = [User getUserWithUserEmail:address context:nil];
        if (user && user.userSingleId) {
            [user changeUserRecentContactFlag:@(1)];
            [sessionUserArray addObject:user.userName];
            [operation onSuceess:address];
        } else {
            [User searchUser:address succeed:^(id retobj) {
                user = [User getUserWithUserEmail:address context:nil];
                [user changeUserRecentContactFlag:@(1)];
                [sessionUserArray addObject:user.userName];
                [operation onSuceess:address];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                [ctx performBlockAndWait:^{
                    user = [User userInsertWithEmail:address context:ctx];
                    [ctx save:nil];
                }];
                if (user) {
                    [sessionUserArray addObject:user.userName];
                    [user changeUserRecentContactFlag:@(1)];
                    [operation onSuceess:address];
                } else {
                    [operation onFailed:address];
                }
            }];
        }
    }
}

@end
