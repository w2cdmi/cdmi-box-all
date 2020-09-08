//
//  MessageCacheOperation.m
//  OneMail
//
//  Created by cse on 15/11/17.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "MessageCacheOperation.h"
#import "UserSetting.h"
#import "AppDelegate.h"
#import "Session.h"
#import "Message.h"

@implementation MessageCacheOperation

+ (BOOL)shouldCleanCache:(Message *)message{
    NSDate *dateNow = [NSDate date];
    NSDate *messageDate = message.messageSendDate;
    NSTimeInterval timeInterval = [dateNow timeIntervalSinceDate:messageDate];
    long retentInterval = 0;
    if ([UserSetting defaultSetting].emailRetentionTime.intValue == 2) {
        retentInterval = 90 * 1440 * 60;
    } else if ([UserSetting defaultSetting].emailRetentionTime.intValue == 1){
        retentInterval = 30 * 1440 * 60;
    } else if ([UserSetting defaultSetting].emailRetentionTime.intValue == 0){
        retentInterval = 7 * 1440 * 60;
    }
    if (timeInterval > retentInterval ) {
        return YES;
    }
    return NO;
}
+ (NSArray *)getExpiredMessages:(NSManagedObjectContext *)ctx{
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Message" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [NSSortDescriptor sortDescriptorWithKey:@"messageId" ascending:YES];
    [request setEntity:entity];
    [request setSortDescriptors:@[sort]];
    NSArray *allMessages = [ctx executeFetchRequest:request error:nil];
    NSMutableArray *timeOffMessages = [[NSMutableArray alloc] init];
    for (Message *message in allMessages) {
        if ([MessageCacheOperation shouldCleanCache:message]) {
            [timeOffMessages addObject:message];
        }
    }
    return timeOffMessages;
}
+ (void)cleanExpiredMessages{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
    [ctx performBlock:^{
        NSArray *TimeOffMessages = [MessageCacheOperation getExpiredMessages:ctx];
        for (Message *message in TimeOffMessages) {
            Session *session = [Session getSessionWithSessionId:message.messageSessionId ctx:ctx];
            NSInteger sessionMessageCount = [session sessionMessageCount];
            if (sessionMessageCount == 1) {
                [session removeSession];
            } else {
                [message removeMessage];
            }
        }
        [ctx save:nil];
    }];
}
@end