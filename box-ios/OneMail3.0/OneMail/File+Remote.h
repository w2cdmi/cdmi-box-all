//
//  File+Remote.h
//  OneMail
//
//  Created by cse  on 15/10/23.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "File.h"
#import "User.h"
#import "DataCommon.h"
#import "Attachment.h"
#import "TransportTaskHandle.h"

@class ALAsset;
@class Asset;

@interface File (Remote)

- (void) fileShare:(User*)user message:(NSString*)message succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileShareUser:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileShareCancel:(User*)user succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileShareCancel:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;


- (void) fileLinkOption:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileLinkList:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
+ (void) fileLinkObject:(NSString*)linkId succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileLinkCreate:(NSDictionary*)linkInfo succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileLinkRefresh:(NSDictionary*)linkInfo succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileLinkInfo:(NSString*)linkId success:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;
- (void) fileLinkDelete:(NSString*)linkId success:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;


- (void) fileMove:(File*)parentFolder autoRename:(BOOL)rename succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileCopy:(File*)parentFolder autoRename:(BOOL)rename succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileRemove:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileRename:(NSString*)newFileName succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) fileVersionList:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
+ (void) getFileContent:(NSDictionary*)fileInfo succeed:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;

- (void) folderCreate:(NSString*)folderName succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) folderItemsCount:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) folderReloadWithOffset:(NSNumber*)offset limit:(NSNumber*)limit succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) folderReload:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) folderRansack:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;
- (void) folderUpdate:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;

+ (void) fileSearch:(NSString*)searchString resourceOwner:(NSString*)searchOwner succeed:(HWRemoteSuccessBlock)succeed failed:(HWRemoteFailedBlock)failed;

- (TransportTaskHandle*) uploadAttachment:(Attachment*)attachment force:(BOOL)force;
- (TransportTask*)localAttachmentUpload:(ALAsset *)asset force:(BOOL)force;
- (void) uploadAsset:(ALAsset*)asset force:(BOOL)force;
- (TransportTask*) downloadVisiable:(BOOL)visiable force:(BOOL)force;
- (TransportTaskHandle*) previewWithForce:(BOOL)force;
- (TransportTaskHandle*) backUpAssets:(Asset*)asset force:(BOOL)force;
- (TransportTaskHandle*) uploadCameraPhoto:(NSString*)photoPath force:(BOOL)force;

/*发送邮件*/
//-(void)emailToUserArray:(NSArray*)userArray emailType:(NSString*)type info:(NSDictionary*)info description:(NSString*)description;
//
///*设置邮件信息*/
//- (void)setEmailMessage:(NSString*)type message:(NSString*)message succeed:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;
//
///*获取邮件信息*/
//- (void)getEmailMessage:(NSString*)type succeed:(HWRemoteSuccessBlock) succeed failed:(HWRemoteFailedBlock) failed;

@end
