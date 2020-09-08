//
//  File.h
//  OneMail
//
//  Created by cse  on 15/10/26.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

typedef enum: NSUInteger {
    TypeFolder,
    TypeWord,
    TypeExcel,
    TypePPT,
    TypePDF,
    TypeTXT,
    TypeImage,
    TypeAudio,
    TypeVideo,
    TypeRAR,
    TypeUnknow,
}FileResourceType;

@class TransportTask;
@class Attachment;
@class TeamSpace;
@class Asset;

@interface File : NSManagedObject

@property (nonatomic, retain) NSNumber * fileAlbumMain;
@property (nonatomic, retain) NSString * fileAlbumFolderKey;
@property (nonatomic, retain) NSString * fileAlbumUrl;
@property (nonatomic, retain) NSString * fileId;
@property (nonatomic, retain) NSString * fileObjectId;
@property (nonatomic, retain) NSString * fileName;
@property (nonatomic, retain) NSNumber * fileSize;
@property (nonatomic, retain) NSNumber * fileType;
@property (nonatomic, retain) NSString * fileMD5;
@property (nonatomic, retain) NSString * fileLocalMD5;
@property (nonatomic, retain) NSString * fileOwner;
@property (nonatomic, retain) NSString * fileOwnerName;
@property (nonatomic, retain) NSString * fileParent;
@property (nonatomic, retain) NSString * fileThumbnailRemotePath;

@property (nonatomic, retain) NSDate * fileModifiedDate;
@property (nonatomic, retain) NSDate * fileSyncDate;

@property (nonatomic, retain) NSNumber * fileShareNewFlag;
@property (nonatomic, retain) NSString * fileShareUser;

@property (nonatomic, retain) NSString * fileSortTimeKey;
@property (nonatomic, retain) NSString * fileSortNameKey;
@property (nonatomic, retain) NSNumber * fileSortSizeKey;

@property (nonatomic, retain) NSNumber * fileUpdateFlag;

@property (nonatomic, retain) NSString * fileAttachmentId;
@property (nonatomic, retain) NSString * fileAttachmentFolderTag;

@property (nonatomic, retain) Asset *relationAsset;
@property (nonatomic, retain) TransportTask *transportTask;
@property (nonatomic, retain) TeamSpace *teamSpace;

- (BOOL) isFolder;
- (BOOL) isFile;
- (BOOL) isCloudRoot;
- (BOOL) isShareRoot;
- (BOOL) isShareFile;

+ (File*) rootFolderWithOwner:(NSString*)fileOwner;
+ (File*) rootMyFolder;
+ (File*) rootReceivedShareFolder;
+ (File*) rootTeamSpaceFolder:(NSString*)teamId name:(NSString*)teamName;

- (NSArray*) subItems;
- (NSArray*) subFileItems;
- (NSArray*) subFolderItems;
- (NSArray*) subItemsWithSorts:(NSArray*)sorts offset:(NSNumber *)offset limit:(NSNumber *)limit;

- (File*) parent;
+ (File*) getFileWithFileId:(NSString*)fileId fileOwner:(NSString*)fileOwner;
+ (File*) getFileWithFileId:(NSString*)fileId fileOwner:(NSString*)fileOwner ctx:(NSManagedObjectContext*)ctx;

+ (File*) fileInsertWithInfo:(NSDictionary*)fileInfo context:(NSManagedObjectContext*)ctx;
- (void) fileRemove:(void(^)())completion;
- (BOOL) sameWithFileInfo:(NSDictionary*)fileInfo;

- (File*) attachmentFolderWithTag:(NSString*)attachmentTag;
- (File*) attachmentFolderWithName:(NSString *)attachmentName;

+ (File*) assetMainFolder;
- (File*) assetFolderWithKey:(NSString *)albumKey;
- (File*) assetFolderWithName:(NSString*)albumName;

- (NSString*) fileDataLocalPath;
- (NSString*) fileCacheLocalPath;
- (NSString*) fileThumbnailLocalPath;
- (NSString*) fileCompressImagePath;

- (void) fileCompressImage;

- (void) deleteFileTransportTask;

- (void) saveFileParent:(NSString*)FileParent;
- (void) saveFileName:(NSString*)fileName;
- (void) saveFileAlbumFolderKey:(NSString*)key;
- (void) saveFileShareNewFlag:(NSNumber*)fileShareNewFlag;
- (void) saveFileMD5:(NSString*)fileMD5;
- (void) saveFileLocalMD5:(NSString*)fileLocalMD5;

@end
