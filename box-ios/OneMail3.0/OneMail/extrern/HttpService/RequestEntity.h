//
//  RequestEntity.h
//  OneBox
//
//  Created by cse on 8/30/14.
//  Copyright (c) 2014 www.huawei.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RequestEntity : NSObject
@property (nonatomic,copy)   NSString *x_device_sn;
@property (nonatomic,copy)   NSString *x_device_os;
@property (nonatomic,copy)   NSString *x_device_name;
@property (nonatomic,copy)   NSString *x_device_type;
@property (nonatomic,copy)   NSString *x_client_version;

@property (nonatomic,copy)   NSNumber *userId;
@property (nonatomic,copy)   NSString *userDomain;
@property (nonatomic,copy)   NSString *userLoginName;
@property (nonatomic,copy)   NSString *userPassword;
@property (nonatomic,copy)   NSString *userAppId;
@property (nonatomic,copy)   NSString *userServerType;
@property (nonatomic,copy)   NSString *userSearchKeyword;
@property (nonatomic,copy)   NSString *userSearchType;
@property (nonatomic,copy)   NSString *userDeclarationID;

@property (nonatomic,copy)   NSString *emailType;
@property (nonatomic,strong) NSArray  *emailRecevier;
@property (nonatomic,copy)   NSString *emailSender;
@property (nonatomic,copy)   NSString *emailMessage;
@property (nonatomic,copy)   NSString *emailObjectName;
@property (nonatomic,strong) NSNumber *emailObjectType;
@property (nonatomic,strong) NSNumber *emailObjectOwnerId;
@property (nonatomic,strong) NSNumber *emailObjectId;
@property (nonatomic,copy)   NSString *emailLinkAccessCode;
@property (nonatomic,strong) NSNumber *emailLinkStart;
@property (nonatomic,strong) NSNumber *emailLinkEnd;
@property (nonatomic,copy)   NSString *emailLinkUrl;
@property (nonatomic,copy)   NSString *emailServerName;
@property (nonatomic,copy)   NSString *emailProtocol;
@property (nonatomic,copy)   NSString *emailPort;

@property (nonatomic,copy)   NSString *clientType;
@property (nonatomic,copy)   NSString *clientVersion;

@property (nonatomic,strong) NSNumber *listOffset;
@property (nonatomic,strong) NSNumber *listLimit;
@property (nonatomic,copy)   NSString *listField;
@property (nonatomic,copy)   NSString *listDirection;
@property (nonatomic,copy)   NSString *listKeyword;

@property (nonatomic,strong) NSNumber *objectId;
@property (nonatomic,copy)   NSString *objectName;
@property (nonatomic,strong) NSNumber *objectOwnerId;
@property (nonatomic,strong) NSNumber *objectParentId;

@property (nonatomic,strong) NSNumber *objectCreateAt;
@property (nonatomic,strong) NSNumber *objectModifiedAt;

@property (nonatomic,strong) NSNumber *thumbnailHeight;
@property (nonatomic,strong) NSNumber *thumbnailWidth;

@property (nonatomic,strong) NSNumber *objectDestOwnerId;
@property (nonatomic,strong) NSNumber *objectDestParentId;
@property (nonatomic,copy)   NSNumber *objectAutoRename;

@property (nonatomic,copy)   NSString *objectNewName;

@property (nonatomic,copy)   NSString *objectSearchWord;

@property (nonatomic,strong) NSNumber *shareRecevierId;
@property (nonatomic,copy)   NSString *shareRecevierType;

@property (nonatomic,copy)   NSString *linksId;
@property (nonatomic,copy)   NSString *linksAccess;
@property (nonatomic,copy)   NSString *linksAccessCodeMode;
@property (nonatomic,copy)   NSString *linksAccessMail;
@property (nonatomic,copy)   NSString *linksPlainAccessCode;
@property (nonatomic,copy)   NSString *linksEffectiveAt;
@property (nonatomic,copy)   NSString *linksExpireAt;
@property (nonatomic,copy)   NSString *linksRole;
@property (nonatomic,copy)   NSString *linksOption;

@property (nonatomic,strong) NSNumber *spaceId;
@property (nonatomic,copy)   NSString *spaceName;
@property (nonatomic,copy)   NSString *spaceDescription;
@property (nonatomic,strong) NSNumber *spaceQuota;
@property (nonatomic,strong) NSNumber *spaceStatus;
@property (nonatomic,strong) NSNumber *spaceMaxVersions;
@property (nonatomic,strong) NSNumber *spaceMaxMembers;
@property (nonatomic,copy)   NSString *spaceMemberType;
@property (nonatomic,strong) NSNumber *spaceMemberUserId;
@property (nonatomic,strong) NSNumber *spaceMemberId;
@property (nonatomic,copy)   NSString *spaceTeamRole;
@property (nonatomic,copy)   NSString *spaceRole;


@end
