//
//  CloudFileShareLinkInfo.m
//  OneMail
//
//  Created by cse  on 16/1/18.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "CloudFileShareLinkInfo.h"
#import "AppDelegate.h"

@implementation CloudFileShareLinkInfo

- (id)initWithInfo:(NSDictionary*)info
{
    self = [super init];
    if (self) {
        self.shareLinkId = [info objectForKey:@"id"];
        self.shareLinkUrl = [self shareLinkURL:[info objectForKey:@"id"]];
        self.shareLinkRole = [info objectForKey:@"role"];
        self.shareLinkExtracCode = [info objectForKey:@"plainAccessCode"];
        self.shareLinkEffectiveAt = [info objectForKey:@"effectiveAt"];
        self.shareLinkExpireAt = [info objectForKey:@"expireAt"];
        //self.shareLinkExtracCodeMode = [info objectForKey:@"accessCodeMode"];
        //self.shareLinkIdentities = [info objectForKey:@"indentities"];
    }
    return self;
}

- (NSString*)shareLinkURL:(NSString*)linkId
{
    AppDelegate *appDelelegate=[UIApplication sharedApplication].delegate;
    NSString *loginBaseUrl = [appDelelegate.remoteManager.httpService.loginBaseUrl absoluteString];
    if (![loginBaseUrl hasSuffix:@"/"]) {
        loginBaseUrl = [loginBaseUrl stringByAppendingString:@"/"];
    }
    return [NSString stringWithFormat:@"%@p/%@",loginBaseUrl,linkId];
}

@end

