//
//  Message.h
//  OneMail
//
//  Created by cse  on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

typedef enum : NSUInteger {
    MessageReceive = 0,
    MessageSent
} MessageType;

@interface Message : NSManagedObject

@property (nonatomic, retain) NSString * messageHTMLContent;
@property (nonatomic, retain) NSString * messageId;
@property (nonatomic, retain) NSString * messagePlainContent;
@property (nonatomic, retain) NSNumber * messageReadFlag;
@property (nonatomic, retain) NSDate   * messageReceiveDate;
@property (nonatomic, retain) NSString * messageReceiver;
@property (nonatomic, retain) NSString * messageReferenceId;
@property (nonatomic, retain) NSString * messageSender;
@property (nonatomic, retain) NSDate   * messageSendDate;
@property (nonatomic, retain) NSString * messageSessionId;
@property (nonatomic, retain) NSString * messageTitle;
@property (nonatomic, retain) NSNumber * messageType;
@property (nonatomic, retain) NSString * messageOwner;

+ (Message*)getMessageWithMessageId:(NSString *)messageId ctx:(NSManagedObjectContext*)ctx;
+ (NSArray*)getMessagesWithSessionId:(NSString *)sessionId ctx:(NSManagedObjectContext*)ctx;
+ (Message*)messageInsertWithInfo:(NSDictionary*)messageInfo ctx:(NSManagedObjectContext*)ctx;
- (void)removeMessage;
- (NSString*)messageImageLocalPath;

- (NSString *)getMessageContent;

- (void)saveMessageSessionId:(NSString*)messageSessionId;
- (void)saveMessageType:(NSNumber*)messageType;
- (void)saveMessageOwner:(NSString*)messageOwner;
- (void)saveMessageBody:(NSString*)messageBody;
- (void)saveMessageRead:(NSNumber*)messageReadFlag;

+ (NSInteger)getMessageUnreadCount;

@end
