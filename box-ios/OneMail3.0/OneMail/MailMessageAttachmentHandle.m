//
//  MailMessageAttachmentHandle.m
//  OneMail
//
//  Created by cse  on 16/1/19.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "MailMessageAttachmentHandle.h"
#import "File+Remote.h"
#import "AppDelegate.h"

typedef void(^AttachmentHTMLCompletion)();

@interface MailMessageAttachmentHandle ()

@property (nonatomic, strong) NSArray *attachmentArray;
@property (nonatomic, strong) NSSet *callingObj;
@property (nonatomic, strong) NSMutableSet *succeededSet;
@property (nonatomic, strong) NSMutableSet *failedSet;
@property (nonatomic, copy) AttachmentHTMLCompletion HTMLCompletion;

@end

@implementation MailMessageAttachmentHandle

- (id)initWithAttachmentArray:(NSArray *)attachmentArray {
    self = [super init];
    if (self) {
        self.attachmentArray = [[NSArray alloc] initWithArray:attachmentArray];
        self.callingObj = [NSSet setWithArray:attachmentArray];
        self.succeededSet = [[NSMutableSet alloc] init];
        self.failedSet = [[NSMutableSet alloc] init];
    }
    return self;
}

- (void) onSuceess:(id) obj {
    if (obj) {
        [self.succeededSet addObject:obj];
        [self checkFinished];
    }
}

- (void) onFailed:(id) obj {
    if (obj) {
        [self.failedSet addObject:obj];
        [self checkFinished];
    }
}

- (void) checkFinished {
    NSMutableSet* tmpSet = [NSMutableSet setWithSet:self.callingObj];
    [tmpSet minusSet:self.failedSet];
    [tmpSet minusSet:self.succeededSet];
    if (tmpSet.count == 0) {
        if (self.HTMLCompletion) {
            self.HTMLCompletion();
        }
    }
}


- (void)generationAttachmentHTMLString {
    NSMutableString *attachmentHTMLString = [[NSMutableString alloc] init];
    
    __weak __block typeof(self) weak = self;
    self.HTMLCompletion = ^(){
        __strong typeof(weak) strong = weak;
        strong.handleCompletion(attachmentHTMLString);
    };
    
    if (self.attachmentArray.count == 0) {
        self.HTMLCompletion();
    }
    for (File *file in self.attachmentArray) {
        [MailMessageAttachmentHandle fileLinkHtmlStringWithFile:file completionBlock:^(NSString *fileLinkHtmlString) {
            if (fileLinkHtmlString) {
                [attachmentHTMLString appendString:fileLinkHtmlString];
                [self onSuceess:file];
            } else {
                [self onFailed:file];
            }
        }];
    }}

+ (void)fileLinkHtmlStringWithFile:(File *)file completionBlock:(void (^)(NSString *))block {
    if (!file) {
        block(nil);
    }
    NSMutableString *fileLinkHtmlString = [[NSMutableString alloc] init];
    [file fileLinkList:^(id retobj) {
        NSArray *fileLinksInfo = [retobj objectForKey:@"links"];
        if (fileLinksInfo.count == 0) {
            [file fileLinkCreate:nil succeed:^(id retobj) {
                NSString *filelink = [MailMessageAttachmentHandle shareLinkURL:[retobj objectForKey:@"id"]];
                [fileLinkHtmlString appendFormat:@"<a href='%@'>这是来自Onebox的文件:%@</a><br/>",filelink,file.fileName];
                block(fileLinkHtmlString);
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                block(nil);
            }];
        } else {
            NSDictionary *fileLinkInfo = [fileLinksInfo lastObject];
            NSString *filelink = [MailMessageAttachmentHandle shareLinkURL:[fileLinkInfo objectForKey:@"id"]];
            [fileLinkHtmlString appendFormat:@"<a href='%@'>这是来自Onebox的文件:%@</a><br/>",filelink,file.fileName];
            block(fileLinkHtmlString);
        }
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        block(nil);
    }];
}

+ (NSString*)shareLinkURL:(NSString*)linkId {
    AppDelegate *appDelegate =[UIApplication sharedApplication].delegate;
    NSString *loginBaseUrl = [appDelegate.remoteManager.httpService.loginBaseUrl absoluteString];
    if (![loginBaseUrl hasSuffix:@"/"]) {
        loginBaseUrl = [loginBaseUrl stringByAppendingString:@"/"];
    }
    return [NSString stringWithFormat:@"%@p/%@",loginBaseUrl,linkId];
}

@end
