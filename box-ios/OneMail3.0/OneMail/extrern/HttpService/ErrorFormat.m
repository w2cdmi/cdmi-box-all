//
//  ErrorFormat.m
//  HttpService
//
//  Created by cse on 7/3/14.
//  Copyright (c) 2014 cse. All rights reserved.
//

#import "ErrorFormat.h"


@implementation ErrorFormat

+(ErrorType)format:(NSError *)error :(AFHTTPRequestOperation *)operation
{
    if (![operation.responseObject isKindOfClass:[NSDictionary class]]) {
        return UnkownError;
    }
    NSString *codeInfo;
    codeInfo=[[error.userInfo objectForKey:@"NSLocalizedDescription"] lowercaseString];
    if ([codeInfo isEqualToString:@"the request timed out."]||[codeInfo isEqualToString:@"请求超时。"]) {
        return TimeOut;
    }
    if ([codeInfo isEqualToString:@"the requested url was not found on this server."]) {
        return NoFound;
    }
    NSHTTPURLResponse *httpResponse=(NSHTTPURLResponse *)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
    codeInfo=[operation.responseObject objectForKey:@"code"]?[operation.responseObject objectForKey:@"code"]:nil;
    if (!codeInfo) {
        return UnkownError;
    }
    switch (httpResponse.statusCode) {
        case 400:
        {
            if ([codeInfo isEqualToString:@"BadRequest"]) {
                return  BadRequest;
            }
            else if ([codeInfo isEqualToString:@"InvalidParameter"]) {
                return InvalidParameter;
            }
            else if ([codeInfo isEqualToString:@"InvalidPart"]) {
                return  InvalidPart;
            }
            else if ([codeInfo isEqualToString:@"InvalidRange"]) {
                return  InvalidRange;
            }
            else if ([codeInfo isEqualToString:@"InvalidTeamRole"]) {
                return  InvalidTeamRole;
            }
            else if ([codeInfo isEqualToString:@"InvalidRegion"]) {
                return  InvalidRegion;
            }
            else if ([codeInfo isEqualToString:@"InvalidPermissonRole"]) {
                return  InvalidPermissonRole;
            }
            else if ([codeInfo isEqualToString:@"InvalidFileType"]){
                return InvalidFileType;
            }
            else if ([codeInfo isEqualToString:@"UnmatchedDownloadUrl"]){
                return UnmatchedDownloadUrl;
            }
        }
            
        case 401:
        {
            if ([codeInfo isEqualToString:@"Unauthorized"]) {
                return  Unauthorized;
            }
            else if ([codeInfo isEqualToString:@"ClientUnauthorized"]) {
                return  ClientUnauthorized;
            }
        }
            
        case 403:
        {
            if ([codeInfo isEqualToString:@"Forbidden"]) {
                return  Forbidden;
            }
            else if ([codeInfo isEqualToString:@"UserLocked"]) {
                return  UserLocked;
            }
            else if ([codeInfo isEqualToString:@"InvalidSpaceStatus"]){
                return InvalidSpaceStatus;
            }
            else if ([codeInfo isEqualToString:@"SourceForbidden"]){
                return SourceForbidden;
            }
            else if ([codeInfo isEqualToString:@"DestForbidden"]){
                return DestForbidden;
            }
        }
            
        case 404:
        {
            if ([codeInfo isEqualToString:@"NoFound"]) {
                return  NoFound;
            }
            else if ([codeInfo isEqualToString:@"NoSuchUser"]) {
                return  NoSuchUser;
            }
            else if ([codeInfo isEqualToString:@"NoSuchNode"]) {
                return  NoSuchNode;
            }
            else if ([codeInfo isEqualToString:@"NoSuchItem"]) {
                return NoSuchItem;
            }
            else if ([codeInfo isEqualToString:@"NoSuchFolder"]) {
                return  NoSuchFolder;
            }
            else if ([codeInfo isEqualToString:@"NoSuchFile"]) {
                return  NoSuchFile;
            }
            else if ([codeInfo isEqualToString:@"NoSuchVersion"]) {
                return  NoSuchVersion;
            }
            else if ([codeInfo isEqualToString:@"NoSuchToken"]) {
                return  NoSuchToken;
            }
            else if ([codeInfo isEqualToString:@"NoSuchLink"]) {
                return  NoSuchLink;
            }
            else if ([codeInfo isEqualToString:@"NoSuchShare"]) {
                return NoSuchShare;
            }
            else if ([codeInfo isEqualToString:@"NoSuchRegion"]) {
                return  NoSuchRegion;
            }
            else if ([codeInfo isEqualToString:@"NoSuchParent"]) {
                return NoSuchParent;
            }
            else if ([codeInfo isEqualToString:@"NoSuchApplication"]) {
                return NoSuchApplication;
            }
            else if ([codeInfo isEqualToString:@"LinkNoEffective"]) {
                return  LinkNoEffective;
            }
            else if ([codeInfo isEqualToString:@"LinkExpired"]) {
                return  LinkExpired;
            }
            else if ([codeInfo isEqualToString:@"NoSuchSource"]) {
                return  NoSuchSource;
            }
            else if ([codeInfo isEqualToString:@"NoSuchDest"]) {
                return  NoSuchDest;
            }
            else if ([codeInfo isEqualToString:@"NoThumbnail"]) {
                return  NoThumbnail;
            }
            else if ([codeInfo isEqualToString:@"AbnormalTeamStatus"]) {
                return  AbnormalTeamStatus;
            }
            else if ([codeInfo isEqualToString:@"NoSuchTeamspace"]) {
                return NoSuchTeamspace;
            }
            else if ([codeInfo isEqualToString:@"NoSuchACL"]) {
                return NoSuchACL;
            }
            
        }
            
        case 405:
        {
            if ([codeInfo isEqualToString:@"MethodNotAllowed"]) {
                return  MethodNotAllowed;
            }
            else if ([codeInfo isEqualToString:@"InvalidProtocol"]) {
                return InvalidProtocol;
            }
        }
            
        case 409:
        {
            if ([codeInfo isEqualToString:@"Conflict"]) {
                return  Conflict;
            }
            else if ([codeInfo isEqualToString:@"ConflictRegion"]) {
                return ConflictRegion;
            }
            else if ([codeInfo isEqualToString:@"RegionConflict"]) {
                return RegionConflict;
            }
            else if ([codeInfo isEqualToString:@"ConflictUser"]) {
                return ConflictUser;
            }
            else if ([codeInfo isEqualToString:@"RepeatNameConflict"]) {
                return RepeatNameConflict;
            }
            else if ([codeInfo isEqualToString:@"SubFolderConflict"]) {
                return SubFolderConflict;
            }
            else if ([codeInfo isEqualToString:@"SameParentConflict"]) {
                return SameParentConflict;
            }
            else if ([codeInfo isEqualToString:@"LinkExistedConflict"]) {
                return LinkExistedConflict;
            }
            else if ([codeInfo isEqualToString:@"ExistMemberConflict"]) {
                return ExistMemberConflict;
            }
            else if ([codeInfo isEqualToString:@"ExistTeamspaceConflict"]) {
                return ExistTeamspaceConflict;
            }
            else if ([codeInfo isEqualToString:@"AsyncNodesConflict"]) {
                return AsyncNodesConflict;
            }
            else if ([codeInfo isEqualToString:@"OutOfQuota"]) {
                return OutOfQuota;
            }
        }
            
        case 412:
        {
            if ([codeInfo isEqualToString:@"TooManyRequest"]) {
                return  TooManyRequest;
            }
            else if ([codeInfo isEqualToString:@"PreconditionFailed"]) {
                return  PreconditionFailed;
            }
        }
            
        case 417:
        {
            if ([codeInfo isEqualToString:@"TransCommitFailed"]) {
                return  TransCommitFailed;
            }
            else if ([codeInfo isEqualToString:@"TransRollbackError"]) {
                return  TransRollbackError;;
            }
        }
            
        case 500:
        {
            if ([codeInfo isEqualToString:@"InternalServerError"]) {
                return  InternalServerError;
            }
        }
            
        case 507:
        {
            if ([codeInfo isEqualToString:@"InsufficientStorage"]) {
                return  InsufficientStorage;
            }
        }
            
        default :
        {
            return UnkownError;
        }
            break;
            
    }
    return UnkownError;
}



-(ErrorType)format:(NSError *)error
{
    NSString *codeInfo=[[error.userInfo objectForKey:@"NSLocalizedDescription"] lowercaseString];
    NSHTTPURLResponse *httpResponse=(NSHTTPURLResponse *)[error.userInfo objectForKeyedSubscript:@"AFNetworkingOperationFailingURLResponseErrorKey"];
    if (!codeInfo||!httpResponse)
    {
        return UnkownError;
    }
    switch (httpResponse.statusCode) {
        case 400:
            if ([codeInfo rangeOfString:@"badrequest"].location!=NSNotFound ||[codeInfo rangeOfString:@"bad request"].location!=NSNotFound) {
                return  BadRequest;
            }
            else  if ([codeInfo rangeOfString:@"invalidparameter"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid parameter"].location!=NSNotFound) {
                return InvalidParameter;
            }
            else   if ([codeInfo rangeOfString:@"invalidpart"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid part"].location!=NSNotFound) {
                return  InvalidPart;
            }
            else  if ([codeInfo rangeOfString:@"invalidrange"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid range"].location!=NSNotFound) {
                return  InvalidRange;
            }
            else   if ( [codeInfo rangeOfString:@"invalidteamrole"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid teamrole"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid team role"].location!=NSNotFound) {
                return  InvalidTeamRole;
            }
            else    if ( [codeInfo rangeOfString:@"invalidregion"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid region"].location!=NSNotFound) {
                return  InvalidRegion;
            }
            else  if ([codeInfo rangeOfString:@"invalidpermissonrole"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid permissonrole"].location!=NSNotFound||[codeInfo rangeOfString:@"invalid permisson role"].location!=NSNotFound) {
                return  InvalidPermissonRole;
            }
            break;
            
        case 401:
        {
            if ([codeInfo rangeOfString:@"unauthorized"].location!=NSNotFound) {
                return  Unauthorized;
            }
            else  if ( [codeInfo rangeOfString:@"clientunauthorized"].location!=NSNotFound||[codeInfo rangeOfString:@"client unauthorized"].location!=NSNotFound) {
                return  ClientUnauthorized;
            }
        }
            break;
        case 403:
        {
            if ([codeInfo rangeOfString:@"forbidden"].location!=NSNotFound) {
                return  Forbidden;
            }
            else  if ([codeInfo rangeOfString:@"userlocked"].location!=NSNotFound||[codeInfo rangeOfString:@"user locked"].location!=NSNotFound) {
                return  UserLocked;
            }
        }
            break;
        case 404:
        {
            if ([codeInfo rangeOfString:@"notfound"].location!=NSNotFound||[codeInfo rangeOfString:@"not found"].location!=NSNotFound) {
                return  NoFound;
            }
            else  if ([codeInfo rangeOfString:@"nosuchuser"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchuser"].location!=NSNotFound||[codeInfo rangeOfString:@"no such user"].location!=NSNotFound) {
                return  NoSuchUser;
            }
            else   if ([codeInfo rangeOfString:@"nosuchnode"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchnode"].location!=NSNotFound||[codeInfo rangeOfString:@"no such node"].location!=NSNotFound) {
                return  NoSuchNode;
            }
            else  if ( [codeInfo rangeOfString:@"nosuchfolder"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchfolder"].location!=NSNotFound||[codeInfo rangeOfString:@"no such folder"].location!=NSNotFound) {
                return  NoSuchFolder;
            }
            else  if ([codeInfo rangeOfString:@"nosuchfile"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchfile"].location!=NSNotFound||[codeInfo rangeOfString:@"no such file"].location!=NSNotFound) {
                return  NoSuchFile;
            }
            else   if ([codeInfo rangeOfString:@"nosuchversion"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchversion"].location!=NSNotFound||[codeInfo rangeOfString:@"no such version"].location!=NSNotFound) {
                return  NoSuchVersion;
            }
            else  if ([codeInfo rangeOfString:@"nosuchtoken"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchtoken"].location!=NSNotFound||[codeInfo rangeOfString:@"no such token"].location!=NSNotFound) {
                return  NoSuchToken;
            }
            else   if ( [codeInfo rangeOfString:@"nosuchlink"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchlink"].location!=NSNotFound||[codeInfo rangeOfString:@"no such link"].location!=NSNotFound) {
                return  NoSuchLink;
            }
            else    if ( [codeInfo rangeOfString:@"nosuchregion"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchregion"].location!=NSNotFound||[codeInfo rangeOfString:@"no such region"].location!=NSNotFound) {
                return  NoSuchRegion;
            }
            else  if ( [codeInfo rangeOfString:@"linknoteffective"].location!=NSNotFound||[codeInfo rangeOfString:@"link noteffective"].location!=NSNotFound||[codeInfo rangeOfString:@"link not effective"].location!=NSNotFound) {
                return  LinkNoEffective;
            }
            else  if ( [codeInfo rangeOfString:@"linkexpired"].location!=NSNotFound||[codeInfo rangeOfString:@"link expired"].location!=NSNotFound) {
                return  LinkExpired;
            }
            else   if ( [codeInfo rangeOfString:@"nosuchsource"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchsource"].location!=NSNotFound||[codeInfo rangeOfString:@"no such source"].location!=NSNotFound) {
                return  NoSuchSource;
            }
            else  if ( [codeInfo rangeOfString:@"nosuchdest"].location!=NSNotFound||[codeInfo rangeOfString:@"no suchdest"].location!=NSNotFound||[codeInfo rangeOfString:@"no such dest"].location!=NSNotFound) {
                return  NoSuchDest;
            }
            else   if ( [codeInfo rangeOfString:@"nothumbnail"].location!=NSNotFound||[codeInfo rangeOfString:@"no thumbanil"].location!=NSNotFound) {
                return  NoThumbnail;
            }
            else    if ( [codeInfo rangeOfString:@"abnormalteamstatus"].location!=NSNotFound||[codeInfo rangeOfString:@"abnormal teamstatus"].location!=NSNotFound||[codeInfo rangeOfString:@"abnormal team status"].location!=NSNotFound||[codeInfo rangeOfString:@"ab normal team status"].location!=NSNotFound) {
                return  AbnormalTeamStatus;
            }
        }
            break;
        case 405:
        {
            if ( [codeInfo rangeOfString:@"methodnotallowed"].location!=NSNotFound||[codeInfo rangeOfString:@"method notallowed"].location!=NSNotFound||[codeInfo rangeOfString:@"method not allowed"].location!=NSNotFound) {
                return  MethodNotAllowed;
            }
        }
            break;
        case 409:
        {
            if ( [codeInfo rangeOfString:@"confilictregion"].location!=NSNotFound||[codeInfo rangeOfString:@"conflict region"].location!=NSNotFound) {
                return  ConflictRegion;
            }
            else   if ( [codeInfo rangeOfString:@"conflictuser"].location!=NSNotFound||[codeInfo rangeOfString:@"conflict user"].location!=NSNotFound) {
                return  ConflictUser;
            }
            else    if ([codeInfo     rangeOfString:@"conflict"].location!=NSNotFound) {
                return  Conflict;
            }
        }
            break;
        case 412:
        {
            if ([codeInfo rangeOfString:@"toomanyrequest"].location!=NSNotFound||[codeInfo rangeOfString:@"too manyrequest"].location!=NSNotFound||[codeInfo rangeOfString:@"too many request"].location!=NSNotFound) {
                return  TooManyRequest;
            }
            else   if ( [codeInfo rangeOfString:@"preconditionfailed"].location!=NSNotFound||[codeInfo rangeOfString:@"pre conditionfailed"].location!=NSNotFound||[codeInfo rangeOfString:@"precondition failed"].location!=NSNotFound||[codeInfo rangeOfString:@"pre condition failed"].location!=NSNotFound) {
                return  PreconditionFailed;
            }
        }
            break;
        case 417:
        {
            if ( [codeInfo rangeOfString:@"transcommiterror"].location!=NSNotFound||[codeInfo rangeOfString:@"trans commiterror"].location!=NSNotFound||[codeInfo rangeOfString:@"trans commit error"].location!=NSNotFound) {
                return  TransCommitFailed;
            }
            else   if ([codeInfo rangeOfString:@"transrollbackerror"].location!=NSNotFound||[codeInfo rangeOfString:@"trans rollbackerror"].location!=NSNotFound||[codeInfo rangeOfString:@"trans rollback error"].location!=NSNotFound) {
                return  TransRollbackError;;
                
            }
        }
            break;
        case 500:
        {
            if ( [codeInfo rangeOfString:@"internalservererror"].location!=NSNotFound||[codeInfo rangeOfString:@"internal servererror"].location!=NSNotFound||[codeInfo rangeOfString:@"internal server error"].location!=NSNotFound) {
                return  InternalServerError;
            }
        }
            break;
        case 507:
        {
            if ([codeInfo rangeOfString:@"insufficientstorage"].location!=NSNotFound||[codeInfo rangeOfString:@"insufficient storage"].location!=NSNotFound||[codeInfo rangeOfString:@"in sufficient storage"].location!=NSNotFound) {
                return  InsufficientStorage;
            }
        }
            break;

        default :
        {
            return UnkownError;
        }
            break;
    
    }
    return UnkownError;
}

    



@end
