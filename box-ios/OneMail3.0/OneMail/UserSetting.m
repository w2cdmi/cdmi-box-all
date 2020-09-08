//
//  UserSetting.m
//  Onebox
//
//  Created by cse  on 15/10/23.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#define ONE_MAIL_PROTOCOL_RECEIVE_KEY    @"Onebox_emailProtocolReceive"
#define ONE_MAIL_PORT_RECEIVE_KEY        @"Onebox_emailPortReceive"
#define ONE_MAIL_SERVER_RECEIVE_KEY      @"Onebox_emailServerReceive"
#define ONE_MAIL_PROTOCOL_SEND_KEY       @"Onebox_emailProtocolSend"
#define ONE_MAIL_PORT_SEND_KEY           @"Onebox_emailPortSend"
#define ONE_MAIL_SERVER_SEND_KEY         @"Onebox_emailServerSend"

#define ONE_MAIL_ADDRESS_KEY             @"Onebox_emailAddress"
#define ONE_MAIL_PASSWORD_KEY            @"Onebox_emailPassword"
#define ONE_MAIL_LASTINBOXUID_KEY        @"Onebox_emailLastInBoxUid"
#define ONE_MAIL_LASTSENTBOXUID_KEY      @"Onebox_emailLastSentBoxUid"
#define ONE_MAIL_NEXTSESSIONID_KEY       @"Onebox_emailNextSessionId"
#define ONE_MAIL_FIRSTLOAD_KEY           @"Onebox_emailFirstLoad"
#define ONE_MAIL_GETDEFAULTADDRESS       @"Onebox_emailGetDafaultAddress"
#define ONE_MAIL_MAILBINDED_KEY          @"Onebox_emailMailBinded"

#define ONE_MAIL_SCESERVE_KEY            @"Onebox_emailScnserveMode"
#define ONE_MAIL_SMART_KEY               @"Onebox_emailSmartMode"
#define ONE_MAIL_AUTOATTACHMENT_KEY      @"Onebox_emailAutoAttachment"
#define ONE_MAIL_WIFIATTACHMENT_KEY      @"Onebox_emailWiFiAttachment"
#define ONE_MAIL_RETENTIONTIME_KEY       @"Onebox_emailRetentionTime"
#define ONE_MAIL_NOTIFICATION_KEY        @"Onebox_emailNotification"
#define ONE_MAIL_SOUNDS_KEY              @"Onebox_emailSounds"
#define ONE_MAIL_VIBRATION_KEY           @"Onebox_emailVibration"
#define ONE_MAIL_DND_KEY                 @"Onebox_emailDND"
#define ONE_MAIL_DNDSTART_KEY            @"Onebox_emailDNDStart"
#define ONE_MAIL_DNDEND_KEY              @"Onebox_emailDNDEnd"

#define ONE_CLOUD_USERSINGLEID_KEY       @"Onebox_cloudUserSingleId"
#define ONE_CLOUD_USERCLOUDID_KEY        @"Onebox_cloudUserCloudId"
#define ONE_CLOUD_USERNAME_KEY           @"Onebox_cloudUserName"
#define ONE_CLOUD_USERLOGINNAME_KEY      @"Onebox_cloudUserLoginName"
#define ONE_CLOUD_USERACCOUNT_KEY        @"Onebox_cloudUserAccount"
#define ONE_CLOUD_USERPASSWORD_KEY       @"Onebox_cloudUserPassword"
#define ONE_CLOUD_USERDESCRIPTION_KEY    @"Onebox_cloudUserDescription"
#define ONE_CLOUD_USERICONPATH_KEY       @"Onebox_cloudUserIconPath"
#define ONE_CLOUD_USERFIRSTLOGIN_KEY     @"Onebox_cloudUserFirstLogin"

#define ONE_CLOUD_AUTOLOGIN_KEY          @"Onebox_cloudAutoLogin"
#define ONE_CLOUD_SERVICE_KEY            @"Onebox_cloudService"
#define ONE_CLOUD_SORTTYPE_KEY           @"Onebox_cloudSortType"
#define ONE_CLOUD_SORTNAMEORDER_KEY      @"Onebox_cloudSortNameOrder"
#define ONE_CLOUD_SORTTIMEORDER_KEY      @"Onebox_cloudSortTimeOrder"
#define ONE_CLOUD_WIFIPROMPT_KEY         @"Onebox_cloudWiFiPrompt"
#define ONE_CLOUD_LANGUAGE_KEY           @"Onebox_cloudLanguage"

#define ONE_CLOUD_ASSETBACKUPOPEN        @"Onebox_cloudAssetBackupOpen"
#define ONE_CLOUD_ASSETBACKUPWIFI        @"Onebox_cloudAssetBackupWifi"
/* 是否是第一次登录 key: com.huawei.onebox.isFirstLogin Value: NSNumber */
#define IS_FIRST_LOGIN_KEY         @"com.huawei.onebox.isFirstLogin"

#import "UserSetting.h"
#import "StringAES.h"

@interface UserSetting()
//@property (nonatomic, strong) HW_Main_CBB *cbb;
@end

@implementation UserSetting
@synthesize emailProtocolReceive = _emailProtocolReceive;
@synthesize emailPortReceive = _emailPortReceive;
@synthesize emailServerReceive = _emailServerReceive;
@synthesize emailProtocolSend = _emailProtocolSend;
@synthesize emailPortSend = _emailPortSend;
@synthesize emailServerSend = _emailServerSend;
@synthesize emailAddress = _emailAddress;
@synthesize emailPassword = _emailPassword;
@synthesize emailLastInboxUid = _emailLastInboxUid;
@synthesize emailLastSentBoxUid = _emailLastSentBoxUid;
@synthesize emailNextSessionId = _emailNextSessionId;
@synthesize emailFirstLoad = _emailFirstLoad;
@synthesize emailGetDefaultAddress = _emailGetDefaultAddress;
@synthesize emailBinded = _emailBinded;

@synthesize emailScnserveMode = _emailScnserveMode;
@synthesize emailSmartMode = _emailSmartMode;
@synthesize emailAutoArchiveAttchment = _emailAutoArchiveAttchment;
@synthesize emailWiFiArchiveAttchment = _emailWiFiArchiveAttchment;
@synthesize emailRetentionTime = _emailRetentionTime;
@synthesize emailNotification = _emailNotification;
@synthesize emailSounds = _emailSounds;
@synthesize emailVibration = _emailVibration;
@synthesize emailDND = _emailDND;
@synthesize emailDNDStart = _emailDNDStart;
@synthesize emailDNDEnd = _emailDNDEnd;

@synthesize cloudUserSingleId = _cloudUserSingleId;
@synthesize cloudUserCloudId = _cloudUserCloudId;
@synthesize cloudUserName = _cloudUserName;
@synthesize cloudUserLoginName = _cloudUserLoginName;
@synthesize cloudUserAccount = _cloudUserAccount;
@synthesize cloudUserPassword = _cloudUserPassword;
@synthesize cloudUserDescription = _cloudUserDepartment;
@synthesize cloudUserIconPath = _cloudUserIconPath;
@synthesize cloudUserFirstLogin = _cloudUserFirstLogin;

@synthesize cloudAutoLogin = _cloudAutoLogin;
@synthesize cloudService = _cloudService;
@synthesize cloudSortType = _cloudSortType;
@synthesize cloudSortNameOrder = _cloudSortNameOrder;
@synthesize cloudSortTimeOrder = _cloudSortTimeOrder;
@synthesize cloudWiFiPrompt = _cloudWiFiPrompt;
@synthesize cloudLanguage = _cloudLanguage;

@synthesize cloudAssetBackupOpen = _cloudAssetBackupOpen;
@synthesize cloudAssetBackupWifi = _cloudAssetBackupWifi;

static UserSetting *defaultSetting = nil;

+ (id)defaultSetting {
    @synchronized(self)
    {
        if (!defaultSetting)
            defaultSetting = [[UserSetting alloc] init];
    }
    return defaultSetting;
}

-(id)init {
    defaultSetting = [super init];
//    self.cbb = [[HW_Main_CBB alloc] init];
    if (defaultSetting) {
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PROTOCOL_RECEIVE_KEY] == nil) {
            defaultSetting.emailProtocolReceive = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PORT_RECEIVE_KEY] == nil) {
            defaultSetting.emailPortReceive = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SERVER_RECEIVE_KEY] == nil) {
            defaultSetting.emailServerReceive = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PROTOCOL_SEND_KEY] == nil) {
            defaultSetting.emailProtocolSend = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PORT_SEND_KEY] == nil) {
            defaultSetting.emailPortSend = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SERVER_SEND_KEY] == nil) {
            defaultSetting.emailServerSend = @"";
        }
        
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_ADDRESS_KEY] == nil) {
            defaultSetting.emailAddress = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PASSWORD_KEY] == nil) {
            defaultSetting.emailPassword = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_LASTINBOXUID_KEY] == nil) {
            defaultSetting.emailLastInboxUid = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_LASTSENTBOXUID_KEY] == nil) {
            defaultSetting.emailLastSentBoxUid = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_NEXTSESSIONID_KEY] == nil) {
            defaultSetting.emailNextSessionId = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_FIRSTLOAD_KEY] == nil) {
            defaultSetting.emailFirstLoad = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_GETDEFAULTADDRESS] == nil) {
            defaultSetting.emailGetDefaultAddress = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_MAILBINDED_KEY] == nil) {
            defaultSetting.emailBinded = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SCESERVE_KEY] == nil) {
            defaultSetting.emailScnserveMode = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SMART_KEY] == nil) {
            defaultSetting.emailSmartMode = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_AUTOATTACHMENT_KEY] == nil) {
            defaultSetting.emailAutoArchiveAttchment = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_WIFIATTACHMENT_KEY] == nil) {
            defaultSetting.emailWiFiArchiveAttchment = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_RETENTIONTIME_KEY] == nil) {
            defaultSetting.emailRetentionTime = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_NOTIFICATION_KEY] == nil) {
            defaultSetting.emailNotification = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SOUNDS_KEY] == nil) {
            defaultSetting.emailSounds = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_VIBRATION_KEY] == nil) {
            defaultSetting.emailVibration = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_DND_KEY] == nil) {
            defaultSetting.emailDND = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_DNDSTART_KEY] == nil) {
            defaultSetting.emailDNDStart = @"22:00";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_DNDEND_KEY] == nil) {
            defaultSetting.emailDNDEnd = @"08:00";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERSINGLEID_KEY] == nil) {
            defaultSetting.cloudUserSingleId = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERCLOUDID_KEY] == nil) {
            defaultSetting.cloudUserCloudId = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERNAME_KEY] == nil) {
            defaultSetting.cloudUserName = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERLOGINNAME_KEY] == nil) {
            defaultSetting.cloudUserLoginName = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERACCOUNT_KEY] == nil) {
            defaultSetting.cloudUserAccount = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERPASSWORD_KEY] == nil) {
            defaultSetting.cloudUserPassword = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERDESCRIPTION_KEY] == nil) {
            defaultSetting.cloudUserDescription = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERICONPATH_KEY] == nil) {
            defaultSetting.cloudUserIconPath = @"";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERFIRSTLOGIN_KEY] == nil) {
            defaultSetting.cloudUserFirstLogin = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_AUTOLOGIN_KEY] == nil) {
            defaultSetting.cloudAutoLogin = @(0);
        }
        //  http://box.csicloud.cn
        NSString *wanke = @"https://pan.vanke.com/";
        NSString *zhong = @"https://pan.storbox.cn/login";
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SERVICE_KEY] == nil) {
            defaultSetting.cloudService = zhong;
            
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SORTTYPE_KEY] == nil) {
            defaultSetting.cloudSortType = @"fileSortNameKey";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SORTNAMEORDER_KEY] == nil) {
            defaultSetting.cloudSortNameOrder = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SORTTIMEORDER_KEY] == nil) {
            defaultSetting.cloudSortTimeOrder = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_WIFIPROMPT_KEY] == nil) {
            defaultSetting.cloudWiFiPrompt = @(1);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_LANGUAGE_KEY] == nil) {
            defaultSetting.cloudLanguage = @"system";
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_ASSETBACKUPOPEN] == nil) {
            defaultSetting.cloudAssetBackupOpen = @(0);
        }
        if ([[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_ASSETBACKUPWIFI] == nil) {
            defaultSetting.cloudAssetBackupWifi = @(1);
        }
    }
    return defaultSetting;
}

- (void)logout {
    defaultSetting.emailAddress = @"";
    defaultSetting.emailPassword = @"";
    defaultSetting.emailLastInboxUid = @(0);
    defaultSetting.emailLastSentBoxUid = @(0);
    defaultSetting.emailNextSessionId = @(0);
    defaultSetting.emailFirstLoad = @(1);
    defaultSetting.emailGetDefaultAddress = @(1);
    defaultSetting.emailBinded = @(0);
    
    defaultSetting.emailScnserveMode = @(0);
    defaultSetting.emailSmartMode = @(1);
    defaultSetting.emailAutoArchiveAttchment = @(0);
    defaultSetting.emailWiFiArchiveAttchment = @(1);
    defaultSetting.emailRetentionTime = @(1);
    
    defaultSetting.emailNotification = @(1);
    defaultSetting.emailSounds = @(1);
    defaultSetting.emailVibration = @(1);
    defaultSetting.emailDND = @(0);
    defaultSetting.emailDNDStart = @"23:00";
    defaultSetting.emailDNDEnd = @"08:00";
    
    defaultSetting.cloudUserSingleId = @(0);
    defaultSetting.cloudUserCloudId = @(0);
    defaultSetting.cloudUserName = @"";
    defaultSetting.cloudUserLoginName = @"";
    defaultSetting.cloudUserAccount = @"";
    defaultSetting.cloudUserPassword = @"";
    defaultSetting.cloudUserDescription = @"";
    defaultSetting.cloudUserIconPath = @"";
    defaultSetting.cloudUserFirstLogin = @(1);
    
    defaultSetting.cloudAutoLogin = @(0);
    defaultSetting.cloudSortType = @"fileSortNameKey";
    defaultSetting.cloudSortNameOrder = @(1);
    defaultSetting.cloudSortTimeOrder = @(1);
    defaultSetting.cloudWiFiPrompt = @(1);
    defaultSetting.cloudLanguage = @"system";
}

/** 是否是第一次登录 */
- (NSNumber *)isFirstLogin {
    return [[NSUserDefaults standardUserDefaults] objectForKey:IS_FIRST_LOGIN_KEY];
}

- (void)setIsFirstLogin:(NSNumber *)isFirstLogin {
    [[NSUserDefaults standardUserDefaults] setObject:isFirstLogin forKey:IS_FIRST_LOGIN_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailProtocolReceive {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PROTOCOL_RECEIVE_KEY];
}

- (void)setEmailProtocolReceive:(NSString *)emailProtocolReceive {
    [[NSUserDefaults standardUserDefaults] setObject:emailProtocolReceive forKey:ONE_MAIL_PROTOCOL_RECEIVE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailPortReceive {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PORT_RECEIVE_KEY];
}

- (void)setEmailPortReceive:(NSString *)emailPortReceive {
    [[NSUserDefaults standardUserDefaults] setObject:emailPortReceive forKey:ONE_MAIL_PORT_RECEIVE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailServerReceive {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SERVER_RECEIVE_KEY];
}

- (void)setEmailServerReceive:(NSString *)emailServerReceive {
    [[NSUserDefaults standardUserDefaults] setObject:emailServerReceive forKey:ONE_MAIL_SERVER_RECEIVE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailProtocolSend {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PROTOCOL_SEND_KEY];
}

- (void)setEmailProtocolSend:(NSString *)emailProtocolSend {
    [[NSUserDefaults standardUserDefaults] setObject:emailProtocolSend forKey:ONE_MAIL_PROTOCOL_SEND_KEY];
}

- (NSString*)emailPortSend {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PORT_SEND_KEY];
}

- (void)setEmailPortSend:(NSString *)emailPortSend {
    [[NSUserDefaults standardUserDefaults] setObject:emailPortSend forKey:ONE_MAIL_PORT_SEND_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailServerSend {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SERVER_SEND_KEY];
}

- (void)setEmailServerSend:(NSString *)emailServerSend {
    [[NSUserDefaults standardUserDefaults] setObject:emailServerSend forKey:ONE_MAIL_SERVER_SEND_KEY];
    [[NSUserDefaults standardUserDefaults]synchronize];
}

- (NSString*)emailAddress {
//    return [self.cbb cbbDecrypt:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_ADDRESS_KEY]];
    return [StringAES AEStoString:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_ADDRESS_KEY]];
}

- (void)setEmailAddress:(NSString *)emailAddress {
//    [[NSUserDefaults standardUserDefaults] setObject:[self.cbb cbbEncrypt:emailAddress] forKey:ONE_MAIL_ADDRESS_KEY];
    [[NSUserDefaults standardUserDefaults] setObject:[StringAES stringtoAES:emailAddress] forKey:ONE_MAIL_ADDRESS_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailPassword {
//    return [self.cbb cbbDecrypt:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PASSWORD_KEY]];
    return [StringAES AEStoString:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_PASSWORD_KEY]];
}

- (void)setEmailPassword:(NSString *)emailPassword {
//    [[NSUserDefaults standardUserDefaults] setObject:[self.cbb cbbEncrypt:emailPassword] forKey:ONE_MAIL_PASSWORD_KEY];
    [[NSUserDefaults standardUserDefaults] setObject:[StringAES stringtoAES:emailPassword] forKey:ONE_MAIL_PASSWORD_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber *)emailLastInboxUid {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_LASTINBOXUID_KEY];
}

- (void)setEmailLastInboxUid:(NSNumber *)emailLastInboxUid {
    [[NSUserDefaults standardUserDefaults] setObject:emailLastInboxUid forKey:ONE_MAIL_LASTINBOXUID_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailLastSentBoxUid {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_LASTSENTBOXUID_KEY];
}

- (void)setEmailLastSentBoxUid:(NSNumber *)emailLastSentBoxUid {
    [[NSUserDefaults standardUserDefaults] setObject:emailLastSentBoxUid forKey:ONE_MAIL_LASTSENTBOXUID_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailNextSessionId {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_NEXTSESSIONID_KEY];
}

- (void)setEmailNextSessionId:(NSNumber *)emailNextSessionId {
    [[NSUserDefaults standardUserDefaults] setObject:emailNextSessionId forKey:ONE_MAIL_NEXTSESSIONID_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailFirstLoad {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_FIRSTLOAD_KEY];
}

-(void)setEmailFirstLoad:(NSNumber *)emailFirstLoad {
    [[NSUserDefaults standardUserDefaults]setObject:emailFirstLoad forKey:ONE_MAIL_FIRSTLOAD_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailGetDefaultAddress {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_GETDEFAULTADDRESS];
}

- (void)setEmailGetDefaultAddress:(NSNumber *)emailGetDefaultAddress {
    [[NSUserDefaults standardUserDefaults] setObject:emailGetDefaultAddress forKey:ONE_MAIL_GETDEFAULTADDRESS];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)emailBinded {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_MAILBINDED_KEY];
}

-(void)setEmailBinded:(NSNumber *)emailBinded {
    [[NSUserDefaults standardUserDefaults] setObject:emailBinded forKey:ONE_MAIL_MAILBINDED_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailScnserveMode {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SCESERVE_KEY];
}

-(void)setEmailScnserveMode:(NSNumber *)emailScnserveMode {
    [[NSUserDefaults standardUserDefaults] setObject:emailScnserveMode forKey:ONE_MAIL_SCESERVE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)emailSmartMode {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SMART_KEY];
}

-(void)setEmailSmartMode:(NSNumber *)emailSmartMode {
    [[NSUserDefaults standardUserDefaults] setObject:emailSmartMode forKey:ONE_MAIL_SMART_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)emailAutoArchiveAttchment {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_AUTOATTACHMENT_KEY];
}

-(void)setEmailAutoArchiveAttchment:(NSNumber *)emailAutoArchiveAttchment {
    [[NSUserDefaults standardUserDefaults] setObject:emailAutoArchiveAttchment forKey:ONE_MAIL_AUTOATTACHMENT_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)emailWiFiArchiveAttchment {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_WIFIATTACHMENT_KEY];
}

-(void)setEmailWiFiArchiveAttchment:(NSNumber *)emailWiFiArchiveAttchment {
    [[NSUserDefaults standardUserDefaults] setObject:emailWiFiArchiveAttchment forKey:ONE_MAIL_WIFIATTACHMENT_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailRetentionTime {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_RETENTIONTIME_KEY];
}

- (void)setEmailRetentionTime:(NSNumber*)emailRetentionTime
{
    [[NSUserDefaults standardUserDefaults] setObject:emailRetentionTime forKey:ONE_MAIL_RETENTIONTIME_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailNotification {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_NOTIFICATION_KEY];
}

- (void)setEmailNotification:(NSNumber *)emailNotification {
    [[NSUserDefaults standardUserDefaults] setObject:emailNotification forKey:ONE_MAIL_NOTIFICATION_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailSounds {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_SOUNDS_KEY];
}

- (void)setEmailSounds:(NSNumber *)emailSounds {
    [[NSUserDefaults standardUserDefaults] setObject:emailSounds forKey:ONE_MAIL_SOUNDS_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailVibration {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_VIBRATION_KEY];
}

- (void)setEmailVibration:(NSNumber *)emailVibration {
    [[NSUserDefaults standardUserDefaults] setObject:emailVibration forKey:ONE_MAIL_VIBRATION_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)emailDND {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_DND_KEY];
}

- (void)setEmailDND:(NSNumber *)emailDND {
    [[NSUserDefaults standardUserDefaults] setObject:emailDND forKey:ONE_MAIL_DND_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailDNDStart {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_DNDSTART_KEY];
}

- (void)setEmailDNDStart:(NSString *)emailDNDStart {
    [[NSUserDefaults standardUserDefaults] setObject:emailDNDStart forKey:ONE_MAIL_DNDSTART_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)emailDNDEnd {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_MAIL_DNDEND_KEY];
}

- (void)setEmailDNDEnd:(NSString *)emailDNDEnd {
    [[NSUserDefaults standardUserDefaults] setObject:emailDNDEnd forKey:ONE_MAIL_DNDEND_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)cloudWiFiPrompt {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_WIFIPROMPT_KEY];
}

- (void)setCloudWiFiPrompt:(NSNumber *)cloudWiFiPrompt {
    [[NSUserDefaults standardUserDefaults] setObject:cloudWiFiPrompt forKey:ONE_CLOUD_WIFIPROMPT_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)cloudAutoLogin {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_AUTOLOGIN_KEY];
}

- (void)setCloudAutoLogin:(NSNumber *)cloudAutoLogin {
    [[NSUserDefaults standardUserDefaults] setObject:cloudAutoLogin forKey:ONE_CLOUD_AUTOLOGIN_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)cloudLanguage {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_LANGUAGE_KEY];
}

- (void)setCloudLanguage:(NSString *)cloudLanguage {
    [[NSUserDefaults standardUserDefaults] setObject:cloudLanguage forKey:ONE_CLOUD_LANGUAGE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSString*)cloudService {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SERVICE_KEY];
}

-(void)setCloudService:(NSString *)cloudService {
    [[NSUserDefaults standardUserDefaults] setObject:cloudService forKey:ONE_CLOUD_SERVICE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)cloudUserFirstLogin {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERFIRSTLOGIN_KEY];
}

-(void)setCloudUserFirstLogin:(NSNumber *)cloudUserFirstLogin {
    [[NSUserDefaults standardUserDefaults] setObject:cloudUserFirstLogin forKey:ONE_CLOUD_USERFIRSTLOGIN_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSString*)cloudUserAccount {
//    NSString *utf8Account = [self.cbb cbbDecrypt:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERACCOUNT_KEY]];
    NSString *utf8Account = [StringAES AEStoString:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERACCOUNT_KEY]];
    return [utf8Account stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
}

-(void)setCloudUserAccount:(NSString *)cloudUserAccount {
   cloudUserAccount = [cloudUserAccount stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
//    NSString *encryptString = [self.cbb cbbEncrypt:cloudUserAccount];
//    NSString *utf8Account = [StringAES AEStoString:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERACCOUNT_KEY]];
    NSString *encryptString = [StringAES stringtoAES:cloudUserAccount];
    if (encryptString) {
//        [[NSUserDefaults standardUserDefaults] setObject:[self.cbb cbbEncrypt:cloudUserAccount] forKey:ONE_CLOUD_USERACCOUNT_KEY];
        [[NSUserDefaults standardUserDefaults] setObject:[StringAES stringtoAES:cloudUserAccount] forKey:ONE_CLOUD_USERACCOUNT_KEY];
    } else {
        [[NSUserDefaults standardUserDefaults] setObject:@"" forKey:ONE_CLOUD_USERACCOUNT_KEY];
    }
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSString*)cloudUserPassword {
//    return [self.cbb cbbDecrypt:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERPASSWORD_KEY]];
    return [StringAES AEStoString:[[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERPASSWORD_KEY]];
}

-(void)setCloudUserPassword:(NSString *)cloudUserPassword {
//    NSString *encryptString = [self.cbb cbbEncrypt:cloudUserPassword];
    NSString *encryptString = [StringAES stringtoAES:cloudUserPassword];
    if (encryptString) {
//        [[NSUserDefaults standardUserDefaults] setObject:[self.cbb cbbEncrypt:cloudUserPassword] forKey:ONE_CLOUD_USERPASSWORD_KEY];
        [[NSUserDefaults standardUserDefaults] setObject:[StringAES stringtoAES:cloudUserPassword] forKey:ONE_CLOUD_USERPASSWORD_KEY];
    } else {
        [[NSUserDefaults standardUserDefaults] setObject:nil forKey:ONE_CLOUD_USERPASSWORD_KEY];
    }
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)cloudUserSingleId {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERSINGLEID_KEY];
}

- (void)setCloudUserSingleId:(NSNumber *)cloudUserSingleId {
    [[NSUserDefaults standardUserDefaults] setObject:cloudUserSingleId forKey:ONE_CLOUD_USERSINGLEID_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)cloudUserCloudId {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERCLOUDID_KEY];
}

-(void)setCloudUserCloudId:(NSNumber *)cloudUserCloudId {
    [[NSUserDefaults standardUserDefaults] setObject:cloudUserCloudId forKey:ONE_CLOUD_USERCLOUDID_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSString*)cloudUserDescription {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERDESCRIPTION_KEY];
}

-(void)setCloudUserDescription:(NSString *)cloudUserDescription {
    [[NSUserDefaults standardUserDefaults] setObject:cloudUserDescription forKey:ONE_CLOUD_USERDESCRIPTION_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)cloudUserIconPath {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERICONPATH_KEY];
}

- (void)setCloudUserIconPath:(NSString *)cloudUserIconPath {
    [[NSUserDefaults standardUserDefaults] setObject:cloudUserIconPath forKey:ONE_CLOUD_USERICONPATH_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSString*)cloudSortType {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SORTTYPE_KEY];
}

-(void)setCloudSortType:(NSString *)cloudSortType {
    [[NSUserDefaults standardUserDefaults] setObject:cloudSortType forKey:ONE_CLOUD_SORTTYPE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)cloudSortNameOrder {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SORTNAMEORDER_KEY];
}

-(void)setCloudSortNameOrder:(NSNumber *)cloudSortNameOrder {
    [[NSUserDefaults standardUserDefaults] setObject:cloudSortNameOrder forKey:ONE_CLOUD_SORTNAMEORDER_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

-(NSNumber*)cloudSortTimeOrder {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_SORTTIMEORDER_KEY];
}

-(void)setCloudSortTimeOrder:(NSNumber *)cloudSortTimeOrder {
    [[NSUserDefaults standardUserDefaults] setObject:cloudSortTimeOrder forKey:ONE_CLOUD_SORTTIMEORDER_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString*)cloudUserName {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERNAME_KEY];
}

- (void)setCloudUserName:(NSString *)cloudUserName {
    [[NSUserDefaults standardUserDefaults] setObject:cloudUserName forKey:ONE_CLOUD_USERNAME_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSString *)cloudUserLoginName {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_USERLOGINNAME_KEY];
}

- (void)setCloudUserLoginName:(NSString *)cloudUserLoginName {
    [[NSUserDefaults standardUserDefaults] setObject:cloudUserLoginName forKey:ONE_CLOUD_USERLOGINNAME_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)cloudAssetBackupOpen {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_ASSETBACKUPOPEN];
}

- (void)setCloudAssetBackupOpen:(NSNumber *)cloudAssetBackupOpen {
    [[NSUserDefaults standardUserDefaults] setObject:cloudAssetBackupOpen forKey:ONE_CLOUD_ASSETBACKUPOPEN];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSNumber*)cloudAssetBackupWifi {
    return [[NSUserDefaults standardUserDefaults] objectForKey:ONE_CLOUD_ASSETBACKUPWIFI];
}

- (void)setCloudAssetBackupWifi:(NSNumber *)cloudAssetBackupWifi {
    [[NSUserDefaults standardUserDefaults] setObject:cloudAssetBackupWifi forKey:ONE_CLOUD_ASSETBACKUPWIFI];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

@end
