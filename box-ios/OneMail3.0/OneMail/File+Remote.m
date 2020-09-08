//
//  File+Remote.m
//  OneMail
//
//  Created by cse  on 15/10/23.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#define loadLimit 1000
#import "File+Remote.h"
#import "AppDelegate.h"
#import "FileMultiOperation.h"
#import "TransportTask.h"
#import "User.h"
#import "User+Remote.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <AVFoundation/AVFoundation.h>
#import "Asset.h"
#import "FileMD5Hash.h"

@implementation File(Remote)

- (void) fileShareUser:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        RequestEntity *requestEntity=[[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setListOffset:@(0)];
        [requestEntity setListLimit:@(50)];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceShareUserList completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Get Shared Users"); return;
            }
            
            NSArray *shareInfoArray = [responseObject objectForKey:@"contents"];
            if (shareInfoArray.count == 0) succeed(nil);
            
            NSMutableArray *users = [[NSMutableArray alloc] init];
            NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
            
            FileMultiOperation *operation = [[FileMultiOperation alloc] init];
            operation.callingObj = [NSSet setWithArray:shareInfoArray];
            operation.completionBlock = ^(NSSet* succeeded, NSSet* failed) {
                [ctx performBlockAndWait:^{
                    [ctx save:nil];
                    succeed(users);
                }];
            };
            for (NSDictionary *shareInfo in shareInfoArray) {
                NSString *userCloudId = [[shareInfo objectForKey:@"sharedUserId"] stringValue];
                if (!userCloudId) {
                    [operation onFailed:shareInfo]; continue;
                }
                User *user = [User getUserWithUserCloudId:userCloudId context:ctx];
                if (!user) {
                    NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
                    [userInfo setObject:[shareInfo objectForKey:@"sharedUserId"] forKey:@"cloudUserId"];
                    [userInfo setObject:[shareInfo objectForKey:@"sharedUserName"] forKey:@"name"];
                    user = [User userInsertWithInfo:userInfo context:ctx];
                }
                [users addObject:user.userCloudId];
                [operation onSuceess:shareInfo];
            }
        }];
    } failed:failed];
}

- (void) fileShare:(User *)user message:(NSString *)message succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity=[[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setShareRecevierType:@"user"];
        [requestEntity setShareRecevierId:@(user.userCloudId.integerValue)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceShareAdd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Share to");
            } else {
            succeed:SERVICE_SUCCEED(@"Share to");
            }
        }];
    } failed:failed];
}

- (void) fileShareCancel:(User *)user succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setShareRecevierType:@"user"];
        [requestEntity setShareRecevierId:@(user.userCloudId.integerValue)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceShareDelete completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Cancel Share to");
            } else {
            succeed:SERVICE_SUCCEED(@"Cancel Share to");
            }
        }];
    } failed:failed];
}

- (void) fileShareCancel:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceShareDelete completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Cancel Share");
            } else {
            succeed:SERVICE_SUCCEED(@"Cancel Share");
            }
        }];
    } failed:failed];
}

- (void) fileLinkOption:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.uam];
        [appDelegate.remoteManager.httpService doEntityRequst:nil serviceType:ServiceUserSystermLinksOption completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Get LinkOption");
            } else {
            succeed:SERVICE_SUCCEED(@"Get LinkOption");
            }
        }];
    } failed:failed];
}

- (void) fileLinkList:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceLinksInfoList completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"get Share Links");
            } else {
            succeed:SERVICE_SUCCEED(@"get Share Links");
            }
        }];
    } failed:failed];
}

+ (void) fileLinkObject:(NSString *)linkId succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setLinksId:linkId];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceLinksObject completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            [appDelegate.remoteManager.httpService setHttpRequestHeaderWithToken:appDelegate.remoteManager.token];
            if (error) {
            failed:SERVICE_FAILED(@"Get LinkObject");
            } else {
            succeed:SERVICE_SUCCEED(@"Get LinkObject");
            }
        }];
    } failed:failed];
}

- (void) fileLinkCreate:(NSDictionary *)linkInfo succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity=[[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setLinksAccess:@"object"];
        [requestEntity setLinksAccessCodeMode:@"static"];
        if ([linkInfo objectForKey:@"PlainAccessCode"]) {
            [requestEntity setLinksPlainAccessCode:[linkInfo objectForKey:@"PlainAccessCode"]];
        }
        if ([linkInfo objectForKey:@"EffectiveAt"]) {
            [requestEntity setLinksEffectiveAt:[linkInfo objectForKey:@"EffectiveAt"]];
        }
        if ([linkInfo objectForKey:@"ExpireAt"]) {
            [requestEntity setLinksExpireAt:[linkInfo objectForKey:@"ExpireAt"]];
        }
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceLinksCreate completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Create Share Links");
            } else {
            succeed:SERVICE_SUCCEED(@"Create Share Links");
            }
        }];
    } failed:failed];
}

- (void) fileLinkRefresh:(NSDictionary *)linkInfo succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity=[[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setLinksId:[linkInfo objectForKey:@"id"]];
        [requestEntity setLinksAccess:@"object"];
        [requestEntity setLinksAccessCodeMode:@"static"];
        [requestEntity setLinksRole:[linkInfo objectForKey:@"role"]];
        if ([linkInfo objectForKey:@"PlainAccessCode"]) {
            [requestEntity setLinksPlainAccessCode:[linkInfo objectForKey:@"PlainAccessCode"]];
        }
        if ([linkInfo objectForKey:@"EffectiveAt"]) {
            [requestEntity setLinksEffectiveAt:[linkInfo objectForKey:@"EffectiveAt"]];
        }
        if ([linkInfo objectForKey:@"ExpireAt"]) {
            [requestEntity setLinksExpireAt:[linkInfo objectForKey:@"ExpireAt"]];
        }
        
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceLinksRefresh completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Refresh Share");
            } else {
            succeed:SERVICE_SUCCEED(@"Refresh Share");
            }
        }];
    } failed:failed];
}

- (void) fileLinkInfo:(NSString *)linkId success:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setLinksId:linkId];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceLinkInfo completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Get LinkInfo");
            } else {
            succeed:SERVICE_SUCCEED(@"Get LinkInfo");
            }
        }];
    } failed:failed];
}

- (void) fileLinkDelete:(NSString *)linkId success:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setLinksId:linkId];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceLinksDelete completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Cancel ShareLinks");
            } else {
            succeed:SERVICE_SUCCEED(@"Cancel ShareLinks");
            }
        }];
    } failed:failed];
}

- (void) fileMove:(File *)parentFolder autoRename:(BOOL)rename succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setObjectAutoRename:@(rename)];
        [requestEntity setObjectDestOwnerId:@(parentFolder.fileOwner.integerValue)];
        [requestEntity setObjectDestParentId:@(parentFolder.fileId.integerValue)];
        ServiceType serviceCmd = self.isFile ? ServiceFileMove : ServiceFolderMove;
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"move file/folder");
            } else {
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                [ctx performBlockAndWait:^{
                    File *shadow = (File*)[ctx objectWithID:self.objectID];
                    shadow.fileParent = parentFolder.fileId;
                    [ctx save:nil];
                }];
            succeed:SERVICE_SUCCEED(@"move file/folder");
            }
        }];
    } failed:failed];
}

- (void)fileCopy:(File *)parentFolder autoRename:(BOOL)rename succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setObjectAutoRename:@(rename)];
        [requestEntity setObjectDestOwnerId:@(parentFolder.fileOwner.integerValue)];
        [requestEntity setObjectDestParentId:@(parentFolder.fileId.integerValue)];
        ServiceType serviceCmd = self.isFile ? ServiceFileCopy : ServiceFolderCopy;
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"copy file/folder");
            } else {
            succeed:SERVICE_SUCCEED(@"copy file/folder");
            }
        }];
    } failed:failed];
}

- (void)fileRemove:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        ServiceType serviceCmd = 0;
        if (self.isShareFile) {
            [requestEntity setShareRecevierType:@"user"];
            [requestEntity setShareRecevierId:@(self.fileShareUser.integerValue)];
            serviceCmd = ServiceShareDelete;
        } else {
            serviceCmd = [self isFile] ? ServiceFileDelete : ServiceFolderDelete;
        }
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Remove/RejectShare file/folder");
            } else {
                [self fileRemove:nil];
            succeed:SERVICE_SUCCEED(@"Remove/RejcteShare file/folder");
            }
        }];
    } failed:failed];
}

- (void) fileRename:(NSString *)newFileName succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setObjectNewName:newFileName];
        ServiceType serviceCmd = self.isFile ? ServiceFileRename : ServiceFolderRename;
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Rename file/folder");
            } else {
            succeed:SERVICE_SUCCEED(@"Rename file/folder");
            }
        }];
    } failed:failed];
}
- (void)fileVersionList:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc] init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        ServiceType seviceCmd = ServiceFileVersionList;
         [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:seviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"VerSionList");
            } else {
                NSMutableArray *versionArray = [[NSMutableArray alloc] init];
                AppDelegate *delegate = [UIApplication sharedApplication].delegate;
                NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
                [ctx performBlockAndWait:^{
                    NSArray *versionInfos = [NSArray arrayWithArray:[responseObject objectForKey:@"versions"]];
                    for (NSDictionary *resultInfo in versionInfos) {
                        Version *version = [Version getVersionWithVersionIdAndFileId:[resultInfo objectForKey:@"objectId"] FileId:self.fileId];
                        if (!version) {
                            version = [NSEntityDescription insertNewObjectForEntityForName:@"Version" inManagedObjectContext:ctx];
                        }
                        NSMutableDictionary *versionInfo = [NSMutableDictionary dictionaryWithDictionary:resultInfo];
                        [versionInfo setValue:self.fileId forKey:@"fileId"];
                        [versionInfo setValue:self.fileOwner forKey:@"fileOwner"];
                        [version setVersion:versionInfo];
                        [versionArray addObject:version.objectID];
                    }
                    [ctx save:nil];
                    succeed(versionArray);
                }];
            }
        }];
    } failed:failed];
}

- (void) folderCreate:(NSString *)folderName succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectName:folderName];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectParentId:@(self.fileId.integerValue)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:ServiceFolderCreate completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"Create folder");
            } else {
                NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
                [ctx performBlockAndWait:^{
                    [File fileInsertWithInfo:responseObject context:ctx];
                    [ctx save:nil];
                }];
            succeed:SERVICE_SUCCEED(@"Create folder");
            }
        }];
    } failed:failed];
}

- (void) folderItemsCount:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setListOffset:@(0)];
        [requestEntity setListLimit:@(1)];
        ServiceType serviceCmd = self.isShareRoot ? ServiceShareReceiveList : ServiceFolderList;
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(([NSString stringWithFormat:@"Reload folder %@", self.name]));
            } else {
                succeed([responseObject objectForKey:@"totalCount"]);
            }
        }];
    } failed:failed];
}

- (void) folderReloadWithOffset:(NSNumber *)offset limit:(NSNumber *)limit succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *requestEntity = [[RequestEntity alloc]init];
        [requestEntity setObjectOwnerId:@(self.fileOwner.integerValue)];
        [requestEntity setObjectId:@(self.fileId.integerValue)];
        [requestEntity setListOffset:offset];
        [requestEntity setListLimit:limit];
        [requestEntity setListField:@"name"];
        [requestEntity setListDirection:@"ASC"];
        [requestEntity setThumbnailHeight:@(160)];
        [requestEntity setThumbnailWidth:@(160)];
        ServiceType serviceCmd = self.isShareRoot ? ServiceShareReceiveList : ServiceFolderList;
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"reload folder");
            } else {
                NSManagedObjectContext* ctx = appDelegate.localManager.backgroundObjectContext;
                [ctx performBlockAndWait:^{
                    File* shadowFile = (File*)[ctx objectWithID:self.objectID];
                    if (!shadowFile) {
                        [SNLog Log:LWarn :@"Local folder %@ may be deleted while reloading!", self.fileName];
                        return;
                    }
                    NSSortDescriptor *nameSort = [[NSSortDescriptor alloc] initWithKey:@"fileName" ascending:YES];
                    NSArray *localItems = [shadowFile subItemsWithSorts:@[nameSort] offset:offset limit:limit];
                    localItems = [File sortLocalItemsFromArray:[NSMutableArray arrayWithArray:localItems]];
                    NSArray *remoteItems = [File sortRemoteItemsFromResponse:responseObject];
                    [File compareWithFileInfo:remoteItems localItems:localItems context:ctx];
                    shadowFile.fileSyncDate = [NSDate date];
                    if ([ctx hasChanges]) {
                        [ctx save:nil];
                    }
                    
                    if ([shadowFile isShareRoot]) {
                        [UserSetting defaultSetting].cloudUserFirstLogin = @(0);
                    }
                }];
                succeed(nil);
            }
        }];
    } failed:failed];
}

+ (NSArray*) sortLocalItemsFromArray:(NSMutableArray*)localItems {
    [localItems sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        File* dic1 = obj1;
        File* dic2 = obj2;
        
        NSString* iNode1 = dic1.fileId;
        NSString* iNode2 = dic2.fileId;
        
        NSString* owner1 = dic1.fileOwner;
        NSString* owner2 = dic2.fileOwner;
        
        NSComparisonResult result =  [iNode1 compare:iNode2];
        return !result ? [owner1 compare:owner2] : result;
    }];
    return (NSArray*)localItems;
}

+ (NSArray*) sortRemoteItemsFromResponse:(NSMutableDictionary*) response {
    NSMutableArray *remoteItems = [[NSMutableArray alloc] init];
    if ([response objectForKey:@"contents"]) {
        NSArray* share = [response objectForKey:@"contents"];
        [remoteItems addObjectsFromArray:share];
    } else {
        NSArray* files = [response objectForKey:@"files"];
        NSArray* folders = [response objectForKey:@"folders"];
        [remoteItems addObjectsFromArray:files];
        [remoteItems addObjectsFromArray:folders];
    }
    if ([remoteItems count] == 0) {
        return (NSArray*)remoteItems;
    }
    if ([response objectForKey:@"contents"]) {
        [remoteItems sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
            NSDictionary* dic1 = obj1;
            NSDictionary* dic2 = obj2;
            
            NSString* iNode1 = [[dic1 objectForKey:@"nodeId"] stringValue];
            NSString* iNode2 = [[dic2 objectForKey:@"nodeId"] stringValue];
            
            NSString* owner1 = [[dic1 objectForKey:@"ownerId"] stringValue];
            NSString* owner2 = [[dic2 objectForKey:@"ownerId"] stringValue];
            
            NSComparisonResult result =  [iNode1 compare:iNode2];
            return !result ? [owner1 compare:owner2] : result;
        }];
    } else {
        [remoteItems sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
            NSDictionary* dic1 = obj1;
            NSDictionary* dic2 = obj2;
            
            NSString* fid1 = [[dic1 objectForKey:@"id"] stringValue];
            NSString* fid2 = [[dic2 objectForKey:@"id"] stringValue];
            
            NSString* owner1 = [[dic1 objectForKey:@"ownedBy"] stringValue];
            NSString* owner2 = [[dic2 objectForKey:@"ownedBy"] stringValue];
            
            NSComparisonResult result =  [fid1 compare:fid2];
            return !result ? [owner1 compare:owner2] : result;
        }];
    }
    return (NSArray*)remoteItems;
}

+ (void) compareWithFileInfo:(NSArray*)remoteItems localItems:(NSArray*)localItems context:(NSManagedObjectContext*)ctx {
    int rowOfLocal = 0 , rowOfRemote = 0;
    while (rowOfLocal < localItems.count && rowOfRemote < remoteItems.count) {
        if ([(File*)localItems[rowOfLocal] sameWithFileInfo:remoteItems[rowOfRemote]]) {
            rowOfLocal++;rowOfRemote++;
            continue;
        }
        int rowOfRemoteNext = rowOfRemote;
        for (; rowOfRemoteNext < remoteItems.count; ++rowOfRemoteNext) {
            if ([(File*)localItems[rowOfLocal] sameWithFileInfo:remoteItems[rowOfRemoteNext]]) {
                break;
            }
        }
        if (rowOfRemoteNext < remoteItems.count) {
            for (int rowOfRemoteAdd = rowOfLocal; rowOfRemoteAdd < rowOfRemoteNext; ++rowOfRemoteAdd) {
                [File fileInsertWithInfo:remoteItems[rowOfRemoteAdd] context:ctx];
            }
            rowOfRemote = rowOfRemoteNext+1;
            rowOfLocal ++;
        } else {
            File *file = (File*)localItems[rowOfLocal];
            if (file.transportTask && file.transportTask.taskType.integerValue != TaskFileDownload && file.transportTask.taskStatus.integerValue != TaskSucceed) {
            } else {
                [file fileRemove:nil];
            }
            rowOfLocal++;
        }
    }
    while (rowOfLocal < localItems.count) {
        [NSThread sleepForTimeInterval:0.02];
        File *file = (File*)localItems[rowOfLocal];
        if (file.transportTask && file.transportTask.taskType.integerValue != TaskFileDownload && file.transportTask.taskStatus.integerValue != TaskSucceed) {
        } else {
            [file fileRemove:nil];
        }
        rowOfLocal++;
    }
    while (rowOfRemote < remoteItems.count) {
        [File fileInsertWithInfo:remoteItems[rowOfRemote] context:ctx];
        rowOfRemote++;
    }
}

- (void) folderReload:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    [self folderItemsCount:^(id retobj) {
        NSInteger totalCount = [retobj integerValue];
        NSMutableArray *offsetArray = [[NSMutableArray alloc]initWithCapacity:totalCount/loadLimit+1];
        for (int i = 0; i < totalCount/loadLimit+1; i++) {
            [offsetArray addObject:[NSNumber numberWithInt:loadLimit*i]];
        }
        FileMultiOperation *operation = [[FileMultiOperation alloc] init];
        operation.completionBlock = ^(NSSet *succeedSet,NSSet *failedSet) {
            succeed(nil);
        };
        operation.callingObj = [NSSet setWithArray:(NSArray*)offsetArray];
        for (NSNumber *offset in offsetArray) {
            [self folderReloadWithOffset:offset limit:@(loadLimit) succeed:^(id retobj) {
                [operation onSuceess:offset];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                [operation onFailed:offset];
            }];
        }
    } failed:failed];
}

- (void)folderRansack:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed {
    FileMultiOperation *operation = [[FileMultiOperation alloc] init];
    operation.completionBlock = ^(NSSet *succeedSet,NSSet *failedSet) {
        succeed(nil);
    };
    [self folderReload:^(id retobj) {
        NSArray *subFolderItems = [self subFolderItems];
        if (subFolderItems.count == 0) {
            succeed(nil);return;
        }
        operation.callingObj = [NSSet setWithArray:(NSArray*)subFolderItems];
        for (File *subItem in subFolderItems) {
            [subItem folderRansack:^(id retobj) {
                [operation onSuceess:subItem];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                [operation onFailed:subItem];
            }];
        }
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
    failed:SERVICE_FAILED(@"ransack folder");
    }];
}

- (void)folderUpdate:(HWRemoteSuccessBlock)succeed
            failed:(HWRemoteFailedBlock)failed {
    FileMultiOperation *operation = [[FileMultiOperation alloc] init];
    operation.completionBlock = ^(NSSet *succeedSet,NSSet *failedSet) {
        succeed(nil);
    };
    [self folderReload:^(id retobj) {
        NSArray *subFolderItems = [self subFolderItems];
        if (subFolderItems.count == 0) {
            succeed(nil);return;
        }
        operation.callingObj = [NSSet setWithArray:(NSArray*)subFolderItems];
        for (File *subitem in subFolderItems) {
            if (subitem.transportTask.taskStatus.integerValue != TaskSucceed) {
                [operation onSuceess:subitem];continue;
            }
            [subitem folderRansack:^(id retobj) {
                [operation onSuceess:subitem];
            } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
                [operation onFailed:subitem];
            }];
        }
    } failed:failed];
}

+ (void) fileSearch:(NSString *)searchString
      resourceOwner:(NSString *)searchOwner
            succeed:(HWRemoteSuccessBlock)succeed
             failed:(HWRemoteFailedBlock)failed {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate.remoteManager ensureCloudLogin:^{
        RequestEntity *fileSearchEntity=[[RequestEntity alloc]init];
        [fileSearchEntity setObjectOwnerId:@(searchOwner.integerValue)];
        [fileSearchEntity setListOffset:@(0)];
        [fileSearchEntity setListLimit:@(loadLimit)];
        [fileSearchEntity setListField:@"modifiedAt"];
        [fileSearchEntity setListDirection:@"DESC"];
        [fileSearchEntity setObjectSearchWord:searchString];
        [fileSearchEntity setThumbnailHeight:@(80)];
        [fileSearchEntity setThumbnailWidth:@(80)];
        [appDelegate.remoteManager.httpService setBaseURL:appDelegate.remoteManager.httpService.ufm];
        [appDelegate.remoteManager.httpService doEntityRequst:fileSearchEntity serviceType:ServiceFileSearch completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    failed:SERVICE_FAILED(@"search");
                });
            } else {
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    NSManagedObjectContext* ctx = appDelegate.localManager.backgroundObjectContext;
                    [ctx performBlockAndWait:^{
                        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileName contains[cd] %@ AND fileOwner = %@ AND fileId != %@ AND fileId != %@",searchString,searchOwner,@"0",@"-0"];
                        NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
                        NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"fileModifiedDate" ascending:NO];
                        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
                        [fetchRequest setPredicate:predicate];
                        [fetchRequest setEntity:entity];
                        [fetchRequest setSortDescriptors:@[sort]];
                        [fetchRequest setFetchBatchSize:20];
                        [fetchRequest setFetchLimit:loadLimit];
                        NSArray * localItems = [ctx executeFetchRequest:fetchRequest error:nil];
                        localItems = [File sortLocalItemsFromArray:[NSMutableArray arrayWithArray:localItems]];
                        NSArray * remoteItems = [File sortRemoteItemsFromResponse:responseObject];
                        [File compareWithFileInfo:remoteItems localItems:localItems context:ctx];
                        if ([ctx hasChanges]) {
                            [ctx save:nil];
                        }
                    }];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        succeed:SERVICE_SUCCEED("search");
                    });
                });
            }
        }];
    } failed:failed];
}
+ (void) getFileContent:(NSDictionary*)fileInfo
                succeed:(HWRemoteSuccessBlock) succeed
                 failed:(HWRemoteFailedBlock) failed {
    AppDelegate *delegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    [delegate.remoteManager ensureCloudLogin:^{
        NSNumber *fileId = [fileInfo objectForKey:@"id"]?[fileInfo objectForKey:@"id"]:[fileInfo objectForKey:@"nodeId"];
        NSNumber *ownerBy = [fileInfo objectForKey:@"ownedBy"] ? [fileInfo objectForKey:@"ownedBy"] : [fileInfo objectForKey:@"ownerId"];
        NSNumber *type = [fileInfo objectForKey:@"type"];
        ServiceType serviceCmd = ServiceFileInfo;
        if (type.boolValue == 0) {
            serviceCmd = ServiceFolderInfo;
        }
        RequestEntity *requestEntity = [[RequestEntity alloc] init];
        [requestEntity setObjectOwnerId:ownerBy];
        [requestEntity setObjectId:fileId];
        [delegate.remoteManager.httpService setBaseURL:delegate.remoteManager.httpService.ufm];
        [delegate.remoteManager.httpService doEntityRequst:requestEntity serviceType:serviceCmd completionHandler:^(NSURLResponse *response, id responseObject, NSError *error, ServiceType serviceType, ErrorType errorType) {
            if (error) {
            failed:SERVICE_FAILED(@"get file/folder infomation");
            }
        succeed:SERVICE_SUCCEED(@"get file/folder infomation");
        }];
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int ErrorType) {
        
    }];
}

- (TransportTaskHandle*)uploadAttachment:(Attachment *)attachment force:(BOOL)force {
    if (!attachment) {
        return nil;
    }
    NSString *attachmentDataLocalPath = [attachment attachmentDataLocalPath];
    if (!attachmentDataLocalPath || ![[NSFileManager defaultManager] fileExistsAtPath:attachmentDataLocalPath]) {
        return nil;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    __block TransportTask *attachmentTask = nil;
    [ctx performBlockAndWait:^{
        Attachment *shadow = (Attachment*)[ctx objectWithID:attachment.objectID];
        
        File *file;
        if (shadow.attachmentFileId && shadow.attachmentFileOwner) {
            file = [File getFileWithFileId:shadow.attachmentFileId fileOwner:shadow.attachmentFileOwner ctx:ctx];
        }
        if (!file) {
            NSMutableDictionary *fileInfo = [[NSMutableDictionary alloc] init];
            [fileInfo setObject:attachment.attachmentName forKey:@"name"];
            [fileInfo setObject:@(attachment.attachmentSize.integerValue) forKey:@"size"];
            [fileInfo setObject:@(1) forKey:@"type"];
            [fileInfo setObject:@(self.fileId.integerValue) forKey:@"parent"];
            [fileInfo setObject:@(self.fileOwner.integerValue) forKey:@"ownedBy"];
            file = [File fileInsertWithInfo:fileInfo context:ctx];
        }
        if (file) {
            shadow.attachmentFileId = file.fileId;
            shadow.attachmentFileOwner = file.fileOwner;
            file.fileAttachmentId = shadow.attachmentId;
            attachmentTask = [TransportTask taskInsertWithFile:file type:TaskAttachmentUpload recoverable:NO force:force];
        }
        [ctx save:nil];
    }];
    if (attachmentTask) {
        TransportTask *mainTask = (TransportTask*)[appDelegate.localManager.managedObjectContext objectWithID:attachmentTask.objectID];
        return mainTask.taskHandle;
    }
    return nil;
}

- (void)uploadAsset:(ALAsset *)asset force:(BOOL)force {
    if (!asset) return;
    NSString *assetLocalMD5 = [self getMD5WithAsset:asset];
    ALAssetRepresentation* representation = [asset defaultRepresentation];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileName=%@ AND fileParent=%@ AND fileOwner=%@",representation.filename,self.fileId,self.fileOwner];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    NSArray *fetchArray = [ctx executeFetchRequest:request error:nil];
    
    __block File *assetFile = [fetchArray lastObject];
    if (assetFile) {
        if ([assetFile.fileLocalMD5 isEqualToString:assetLocalMD5]) {
            return;
        } else {
            if (assetFile.transportTask &&
                assetFile.transportTask.taskType.integerValue == TaskAssetUpload &&
                assetFile.transportTask.taskStatus.integerValue == TaskSucceed) {
                [ctx performBlockAndWait:^{
                    File *shadow = (File*)[ctx objectWithID:assetFile.objectID];
                    [shadow.transportTask remove];
                    [ctx save:nil];
                }];
            }
        }
    }
    
    __block TransportTask *assetTask = nil;
    [ctx performBlockAndWait:^{
        if (!assetFile) {
            NSMutableDictionary *fileInfo = [[NSMutableDictionary alloc] init];
            [fileInfo setObject:representation.filename forKey:@"name"];
            [fileInfo setObject:@(self.fileOwner.integerValue) forKey:@"ownedBy"];
            [fileInfo setObject:@(1) forKey:@"type"];
            [fileInfo setObject:@(representation.size) forKey:@"size"];
            [fileInfo setObject:@(self.fileId.integerValue) forKey:@"parent"];
            [fileInfo setObject:assetLocalMD5 forKey:@"localMD5"];
            assetFile = [File fileInsertWithInfo:fileInfo context:ctx];
        }
        if (assetFile) {
            assetFile.fileAlbumUrl = [representation.url absoluteString];
            assetFile.fileLocalMD5 = assetLocalMD5;
            assetTask = [TransportTask taskInsertWithFile:assetFile type:TaskAssetUpload recoverable:YES force:force];
            if (assetTask) {
                appDelegate.transferTaskCount = appDelegate.transferTaskCount + 1;
            }
        }
        NSString *fileThumbnailLocalPath = [assetFile fileThumbnailLocalPath];
        if (fileThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
            
        } else {
            UIImage *assetThumbnail = [UIImage imageWithCGImage:asset.thumbnail];
            NSData *assetThumbnailData = UIImageJPEGRepresentation(assetThumbnail, 1);
            [[NSFileManager defaultManager] createFileAtPath:fileThumbnailLocalPath contents:assetThumbnailData attributes:nil];
        }
        [ctx save:nil];
    }];
}

- (NSString*) getMD5WithAsset:(ALAsset*)asset {
    ALAssetRepresentation *assetRep = [asset defaultRepresentation];
    NSError* error = nil;
    
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *tmpDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Tmp"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:tmpDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:tmpDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *assetCachePath = [tmpDirectory stringByAppendingPathComponent:[assetRep filename]];
    if (assetCachePath && [[NSFileManager defaultManager] fileExistsAtPath:assetCachePath]) {
        [[NSFileManager defaultManager] removeItemAtPath:assetCachePath error:&error];
        if (error) return nil;
    }
    [[NSFileManager defaultManager] createFileAtPath:assetCachePath contents:nil attributes:nil];
    
    NSFileHandle* handler = [NSFileHandle fileHandleForWritingToURL:[NSURL URLWithString:assetCachePath] error:&error];
    if (error) return nil;

    uint8_t buffer[READ_BUFFER_SIZE] = {0};
    long long offset = 0;
    NSUInteger nRead = 0;
    do {
        nRead = [assetRep getBytes:buffer fromOffset:offset length:(sizeof(buffer) / sizeof(uint8_t)) error:&error];
        offset += nRead;
        [handler writeData:[NSData dataWithBytes:buffer length:nRead]];
        if (error) {
            [handler closeFile];
            return nil;
        }
    } while(nRead > 0);
    [handler closeFile];
    
    NSString *md5 = [FileMD5Hash computeMD5HashOfFileInPath:assetCachePath];
    [[NSFileManager defaultManager] removeItemAtPath:assetCachePath error:nil];
    return md5;
}

- (TransportTaskHandle*) backUpAssets:(Asset *)asset force:(BOOL)force {
    AppDelegate* delegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
    
    __block TransportTask* assetTask = nil;
    [ctx performBlockAndWait:^{
        Asset *shadow = (Asset*)[ctx objectWithID:asset.objectID];
        File *assetFile = shadow.relationFile;
        if (!assetFile) {
            NSMutableDictionary *fileInfo = [[NSMutableDictionary alloc] init];
            [fileInfo setObject:asset.assetName forKey:@"name"];
            [fileInfo setObject:@(self.fileId.integerValue) forKey:@"parent"];
            [fileInfo setObject:@(self.fileOwner.integerValue) forKey:@"ownedBy"];
            [fileInfo setObject:@(1) forKey:@"type"];
            assetFile = [File fileInsertWithInfo:fileInfo context:ctx];
            assetFile.relationAsset = shadow;
            shadow.relationFile = assetFile;
            NSString *assetThumbnailPath = [asset assetThumbnailPath];
            NSString *fileThumbnailLocalPath = [assetFile fileThumbnailLocalPath];
            if ([[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
                [[NSFileManager defaultManager] removeItemAtPath:fileThumbnailLocalPath error:nil];
            }
            [[NSFileManager defaultManager] copyItemAtPath:assetThumbnailPath toPath:fileThumbnailLocalPath error:nil];
        }
        if (assetFile) {
            assetTask = [TransportTask taskInsertWithFile:assetFile type:TaskAssetBackUpload recoverable:NO force:force];
            [ctx save:nil];
        }
    }];
    if (assetTask) {
        TransportTask* mainTask = (TransportTask*)[[delegate.localManager managedObjectContext] objectWithID:assetTask.objectID];
        return mainTask.taskHandle;
    }
    return nil;
}

- (TransportTask *)localAttachmentUpload:(ALAsset *)asset force:(BOOL)force {
    if (!asset) return nil;
    ALAssetRepresentation* representation = [asset defaultRepresentation];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileName=%@ AND fileParent=%@ AND fileOwner=%@",representation.filename,self.fileId,appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    NSArray *fetchArray = [ctx executeFetchRequest:request error:nil];
    
    __block File *assetFile = [fetchArray lastObject];
    if (assetFile) {
        if (!(assetFile.fileSize.longLongValue == representation.size)) {
            assetFile = nil;
        }
    }
    
    __block TransportTask *assetTask = nil;
    [ctx performBlockAndWait:^{
        if (!assetFile) {
            NSMutableDictionary *fileInfo = [[NSMutableDictionary alloc] init];
            [fileInfo setObject:representation.filename forKey:@"name"];
            [fileInfo setObject:@(self.fileId.integerValue) forKey:@"parent"];
            [fileInfo setObject:@(self.fileOwner.integerValue) forKey:@"ownedBy"];
            [fileInfo setObject:@(1) forKey:@"type"];
            [fileInfo setObject:@(representation.size) forKey:@"size"];
            assetFile = [File fileInsertWithInfo:fileInfo context:ctx];
            assetFile.fileAlbumUrl = [representation.url absoluteString];
        }
        if (assetFile) {
            assetFile.fileAlbumUrl = [representation.url absoluteString];
            assetTask = [TransportTask taskInsertWithFile:assetFile type:TaskAssetUpload recoverable:NO force:force];
        }
        NSString *fileThumbnailLocalPath = [assetFile fileThumbnailLocalPath];
        if (fileThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
            
        } else {
            UIImage *assetThumbnail = [UIImage imageWithCGImage:asset.thumbnail];
            NSData *assetThumbnailData = UIImageJPEGRepresentation(assetThumbnail, 1);
            [[NSFileManager defaultManager] createFileAtPath:fileThumbnailLocalPath contents:assetThumbnailData attributes:nil];
        }
        [ctx save:nil];
    }];
    if (assetTask) {
        TransportTask *mainTask = (TransportTask*)[appDelegate.localManager.managedObjectContext objectWithID:assetTask.objectID];
        return mainTask;
    }
    return nil;
}

- (TransportTask*)downloadVisiable:(BOOL)visiable force:(BOOL)force {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    __block TransportTask *downloadTask = nil;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        if ([shadow isFile]) {
            downloadTask = [TransportTask taskInsertWithFile:shadow type:TaskFileDownload recoverable:visiable force:force];
        } else {
            downloadTask = [TransportTask taskInsertWithFile:shadow type:TaskFolderDownload recoverable:visiable force:force];
        }
        [ctx save:nil];
    }];
    if (downloadTask) {
        if (visiable) {
            appDelegate.transferTaskCount = appDelegate.transferTaskCount + 1;
        }
        TransportTask *mainTask = (TransportTask*)[appDelegate.localManager.managedObjectContext objectWithID:downloadTask.objectID];
        return mainTask;
    }
    return nil;
}

- (TransportTaskHandle*)previewWithForce:(BOOL)force {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    __block TransportTask *downloadTask = nil;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        downloadTask = [TransportTask taskInsertWithFile:shadow type:TaskFilePreview recoverable:NO force:force];
        [ctx save:nil];
    }];
    if (downloadTask) {
        TransportTask *mainTask = (TransportTask*)[appDelegate.localManager.managedObjectContext objectWithID:downloadTask.objectID];
        return mainTask.taskHandle;
    }
    return nil;
}

- (TransportTaskHandle*)uploadCameraPhoto:(NSString *)photoPath force:(BOOL)force {
    if (!photoPath || ![[NSFileManager defaultManager] fileExistsAtPath:photoPath]) return nil;
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext* ctx = appDelegate.localManager.backgroundObjectContext;
    __block TransportTask* uploadTask = nil;
    [ctx performBlockAndWait:^{
        NSData *data = [NSData dataWithContentsOfFile:photoPath];
        NSMutableDictionary *fileInfo = [[NSMutableDictionary alloc] init];
        [fileInfo setObject:[photoPath lastPathComponent] forKey:@"name"];
        [fileInfo setObject:@(self.fileId.integerValue) forKey:@"parent"];
        [fileInfo setObject:@(self.fileOwner.integerValue) forKey:@"ownedBy"];
        [fileInfo setObject:@([data length]) forKey:@"size"];
        [fileInfo setObject:@(1) forKey:@"type"];
        File *assetFile = [File fileInsertWithInfo:fileInfo context:ctx];
        if (assetFile) {
            NSString *fileCacheLocalPath = [assetFile fileCacheLocalPath];
            if (fileCacheLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileCacheLocalPath]) {
                [[NSFileManager defaultManager] removeItemAtPath:fileCacheLocalPath error:nil];
            }
            [[NSFileManager defaultManager] moveItemAtPath:photoPath toPath:fileCacheLocalPath error:nil];
            uploadTask = [TransportTask taskInsertWithFile:assetFile type:TaskCamerPhotoUpload recoverable:YES force:force];
        }
        [ctx save:nil];
    }];
    if (uploadTask) {
        TransportTask* mainTask = (TransportTask*)[appDelegate.localManager.managedObjectContext objectWithID:uploadTask.objectID];
        return mainTask.taskHandle;
    }
    return nil;
}

@end
