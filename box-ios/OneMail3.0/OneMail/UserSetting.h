//
//  UserSetting.h
//  OneMail
//
//  Created by cse  on 15/10/23.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UserSetting : NSObject

//email service setting
@property (nonatomic, copy)   NSString* emailProtocolReceive;
@property (nonatomic, copy)   NSString* emailPortReceive;
@property (nonatomic, copy)   NSString* emailServerReceive;
@property (nonatomic, copy)   NSString* emailProtocolSend;
@property (nonatomic, copy)   NSString* emailPortSend;
@property (nonatomic, copy)   NSString* emailServerSend;

//email account setting
@property (nonatomic, copy)   NSString* emailAddress;
@property (nonatomic, copy)   NSString* emailPassword;
@property (nonatomic, strong) NSNumber* emailLastInboxUid;
@property (nonatomic, strong) NSNumber* emailLastSentBoxUid;
@property (nonatomic, strong) NSNumber* emailNextSessionId;
@property (nonatomic, strong) NSNumber* emailFirstLoad;
@property (nonatomic, strong) NSNumber* emailGetDefaultAddress;
@property (nonatomic, strong) NSNumber* emailBinded;

//email mode setting
@property (nonatomic, strong) NSNumber* emailScnserveMode;
@property (nonatomic, strong) NSNumber* emailSmartMode;
@property (nonatomic, strong) NSNumber* emailAutoArchiveAttchment;
@property (nonatomic, strong) NSNumber* emailWiFiArchiveAttchment;
@property (nonatomic, strong) NSNumber* emailRetentionTime;
@property (nonatomic, strong) NSNumber* emailNotification;
@property (nonatomic, strong) NSNumber* emailSounds;
@property (nonatomic, strong) NSNumber* emailVibration;
@property (nonatomic, strong) NSNumber* emailDND;
@property (nonatomic, copy)   NSString* emailDNDStart;
@property (nonatomic, copy)   NSString* emailDNDEnd;

@property (nonatomic, strong) NSNumber* cloudUserSingleId;
@property (nonatomic, strong) NSNumber* cloudUserCloudId;
@property (nonatomic, copy)   NSString* cloudUserName;
@property (nonatomic, copy)   NSString* cloudUserLoginName;
@property (nonatomic, copy)   NSString* cloudUserAccount;
@property (nonatomic, copy)   NSString* cloudUserPassword;
@property (nonatomic, copy)   NSString* cloudUserDescription;
@property (nonatomic, copy)   NSString* cloudUserIconPath;
@property (nonatomic, strong) NSNumber* cloudUserFirstLogin;

@property (nonatomic, strong) NSNumber* cloudAutoLogin;
@property (nonatomic, copy)   NSString* cloudService;
@property (nonatomic, copy)   NSString* cloudSortType;
@property (nonatomic, strong) NSNumber* cloudSortNameOrder;
@property (nonatomic, strong) NSNumber* cloudSortTimeOrder;
@property (nonatomic, strong) NSNumber* cloudWiFiPrompt;
@property (nonatomic, strong) NSString* cloudLanguage;

@property (nonatomic, strong) NSNumber* cloudAssetBackupOpen;
@property (nonatomic, strong) NSNumber* cloudAssetBackupWifi;

@property(strong, nonatomic) NSNumber* isFirstLogin;//首次登陆


+ (instancetype)defaultSetting;
- (void)logout;

@end
