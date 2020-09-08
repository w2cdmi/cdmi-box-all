//
//  AttachmentBackupFolder.m
//  OneMail
//
//  Created by cse  on 15/10/29.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "AttachmentBackupFolder.h"
#import "File.h"
#import "File+Remote.h"
#import "AppDelegate.h"
#import "Message.h"

@interface AttachmentBackupFolder ()

@property (nonatomic, strong) File *file;
@property (nonatomic, strong) File *Attachment;
@property (nonatomic, strong) File *AttachmentSend;
@property (nonatomic, strong) File *AttachmentReceive;
@property (nonatomic, strong) File *AttachmentSendWithDate;
@property (nonatomic, strong) File *AttachmentReceiveWithDate;

@property (nonatomic, strong) NSDateFormatter *dateFormatter;
@property (nonatomic, strong) NSMutableArray *attachmentFolders;

@property (nonatomic, strong) NSNumber *messageType;
@property (nonatomic, strong) NSDate *messageDate;

@end

@implementation AttachmentBackupFolder

- (id) init {
    self = [super init];
    if (self) {
        self.dateFormatter = [[NSDateFormatter alloc] init];
        [self.dateFormatter setDateFormat:@"yyyy-MM-dd"];
       
    }
    return self;
}

- (void) cheakAttachmentFolderWithType:(NSNumber*)messageType date:(NSDate*)messageDate {
    self.messageType = messageType;
    self.messageDate = messageDate;
    [self getAttachmentFolderWithString:@"Attachment" parent:[File rootMyFolder]];
}

- (void) folderCreateSucceed:(NSString*)attachString file:(File*)file {
    if ([attachString isEqualToString:@"Attachment"]) {
        self.Attachment = file;
        self.completionBlock(file);
//        if (self.messageType.integerValue == MessageReceive) {
//            [self getAttachmentFolderWithString:@"AttachmentReceive" parent:self.Attachment];
//        } else {
//            [self getAttachmentFolderWithString:@"AttachmentSend" parent:self.Attachment];
//        }
    }
//    else if ([attachString isEqualToString:@"AttachmentReceive"]) {
//        self.AttachmentReceive = file;
//        [self getAttachmentFolderWithString:[self.dateFormatter stringFromDate:self.messageDate] parent:self.AttachmentReceive];
//    } else if ([attachString isEqualToString:@"AttachmentSend"]) {
//        self.AttachmentSend = file;
//        [self getAttachmentFolderWithString:[self.dateFormatter stringFromDate:self.messageDate] parent:self.AttachmentSend];
//    } else if ([attachString isEqualToString:[self.dateFormatter stringFromDate:self.messageDate]]){
//        self.completionBlock(file);
//    }
}

- (void)getAttachmentFolderWithString:(NSString*)attachString parent:(File*)parentFile {
    File *attachmentFolder;
    attachmentFolder = [parentFile attachmentFolderWithTag:attachString];
    if (attachmentFolder) {
        [self folderCreateSucceed:attachString file:attachmentFolder];return;
    }
    attachmentFolder = [parentFile attachmentFolderWithName:attachString];
    if (attachmentFolder) {
        [self setAttachmentFolderTagWithString:attachString file:attachmentFolder];
        [self folderCreateSucceed:attachString file:attachmentFolder];return;
    }
    [self createAttachmentFolderWithString:attachString parent:parentFile completion:^(File *file) {
        if (file) {
            [self folderCreateSucceed:attachString file:file];
        } else {
            [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"OperationFailed", nil)];
        }
    }];
}

- (void)createAttachmentFolderWithString:(NSString*)attachString parent:(File*)parentFile completion:(void(^)(File *file))completionBlock {
    [parentFile folderCreate:attachString succeed:^(id retobj) {
        File *file =[parentFile attachmentFolderWithName:attachString];
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
        [ctx performBlock:^{
            File *shadow = (File*)[ctx objectWithID:file.objectID];
            shadow.fileAttachmentFolderTag = attachString;
            [ctx save:nil];
        }];
        completionBlock(file);
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
        if (httpResponse.statusCode == 409) {
            [parentFile folderReload:^(id retobj) {
                File *file = [parentFile attachmentFolderWithName:attachString];
                if ([file isFile]) {
                    NSString *rename = [attachString stringByAppendingString:@"(1)"];
                    [self createAttachmentFolderWithString:rename parent:parentFile completion:completionBlock];
                } else {
                    completionBlock(file);
                }
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                completionBlock(nil);
            }];
        } else {
            completionBlock(nil);
        }
    }];
}

- (void)setAttachmentFolderTagWithString:(NSString*)attachString file:(File*)file {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    [ctx performBlock:^{
        File *shadow = (File*)[ctx objectWithID:file.objectID];
        shadow.fileAttachmentFolderTag = attachString;
        [ctx save:nil];
    }];
}

@end
