//
//  CloudFileShareLinkInfo.h
//  OneMail
//
//  Created by cse  on 16/1/18.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CloudFileShareLinkInfo : NSObject

@property (nonatomic, strong) NSString *shareLinkId;
@property (nonatomic, strong) NSString *shareLinkUrl;
@property (nonatomic, strong) NSString *shareLinkExtracCode;
@property (nonatomic, strong) NSNumber *shareLinkEffectiveAt;
@property (nonatomic, strong) NSNumber *shareLinkExpireAt;
@property (nonatomic, strong) NSString *shareLinkRole;
@property (nonatomic, strong) NSString *shareLinkExtracCodeMode;
//@property (nonatomic, strong) NSMutableDictionary *shareLinkIdentities;
- (id)initWithInfo:(NSDictionary*)info;
@end
