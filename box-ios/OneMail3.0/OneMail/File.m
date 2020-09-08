//
//  File.m
//  OneMail
//
//  Created by cse  on 15/10/26.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "File.h"
#import "TransportTask.h"
#import "AppDelegate.h"
#import <ImageIO/ImageIO.h>
#import "FileMultiOperation.h"

@implementation File

@dynamic fileAlbumMain;
@dynamic fileAlbumFolderKey;
@dynamic fileAlbumUrl;
@dynamic fileSortTimeKey;
@dynamic fileId;
@dynamic fileObjectId;
@dynamic fileSize;
@dynamic fileSortNameKey;
@dynamic fileAttachmentId;
@dynamic fileAttachmentFolderTag;
@dynamic fileMD5;
@dynamic fileLocalMD5;
@dynamic fileModifiedDate;
@dynamic fileName;
@dynamic fileUpdateFlag;
@dynamic fileOwner;
@dynamic fileOwnerName;
@dynamic fileParent;
@dynamic fileShareNewFlag;
@dynamic fileShareUser;
@dynamic fileSyncDate;
@dynamic fileThumbnailRemotePath;
@dynamic fileType;
@dynamic fileSortSizeKey;
@dynamic transportTask;
@dynamic teamSpace;
@dynamic relationAsset;

#pragma mark attribute
- (BOOL) isFolder {
    return (self.fileType.integerValue == TypeFolder);
}

- (BOOL) isFile {
    return !(self.fileType.integerValue == TypeFolder);
}

- (BOOL) isCloudRoot {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    return ([self.fileId isEqual:@"0"] && [self.fileOwner isEqual:appDelegate.localManager.userCloudId]);
}

- (BOOL) isShareRoot {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    return ([self.fileId isEqual:@"-0"] && [self.fileOwner isEqual:appDelegate.localManager.userCloudId]);
}

- (BOOL) isShareFile {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    return ([self.fileShareUser isEqual:appDelegate.localManager.userCloudId]);
}

#pragma mark rootFolder
+ (File*) rootFolderWithOwner:(NSString*)fileOwner {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileId=%@ AND fileOwner=%@", @"0", fileOwner];
    NSManagedObjectContext* ctx = appDelegate.localManager.managedObjectContext;
    NSFetchRequest* request = [[NSFetchRequest alloc] init];
    NSEntityDescription * fileEntity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    [request setEntity:fileEntity];
    [request setPredicate:predicate];
    File* file = [[ctx executeFetchRequest:request error:nil] lastObject];
    return file;
}

+ (File*) rootMyFolder {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    File * rootMyFolder = nil;
    NSError* error = nil;
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileId=%@ AND fileOwner=%@", @"0", appDelegate.localManager.userCloudId];
    NSManagedObjectContext* ctx = appDelegate.localManager.managedObjectContext;
    NSFetchRequest* request = [[NSFetchRequest alloc] init];
    NSEntityDescription * fileEntity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    [request setEntity:fileEntity];
    [request setPredicate:predicate];
    File* file = [[ctx executeFetchRequest:request error:&error] lastObject];
    if (file) {
        rootMyFolder = file;
    } else {
        rootMyFolder = [NSEntityDescription insertNewObjectForEntityForName:@"File" inManagedObjectContext:ctx];
        rootMyFolder.fileOwner = appDelegate.localManager.userCloudId;
        rootMyFolder.fileId = @"0";
        rootMyFolder.fileType = @(TypeFolder);//768
    }
    rootMyFolder.fileName = NSLocalizedString(@"CloudFileTitle", nil);
    [ctx performBlockAndWait:^{
        [ctx save:nil];
    }];
    return rootMyFolder;
}

+ (File*) rootReceivedShareFolder {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    File * rootShareFolder = nil;
    NSError* error = nil;
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileId=%@ AND fileOwner=%@", @"-0", appDelegate.localManager.userCloudId];
    NSManagedObjectContext* ctx = appDelegate.localManager.managedObjectContext;
    NSFetchRequest* request = [[NSFetchRequest alloc] init];
    NSEntityDescription * fileEntity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    [request setEntity:fileEntity];
    [request setPredicate:predicate];
    File* file = [[ctx executeFetchRequest:request error:&error] lastObject];
    if (file) {
        rootShareFolder = file;
    } else {
        rootShareFolder = [NSEntityDescription insertNewObjectForEntityForName:@"File" inManagedObjectContext:ctx];
        rootShareFolder.fileOwner = appDelegate.localManager.userCloudId;
        rootShareFolder.fileShareUser = appDelegate.localManager.userCloudId;
        rootShareFolder.fileId = @"-0";
        rootShareFolder.fileType = @(TypeFolder);
    }
    rootShareFolder.fileName = NSLocalizedString(@"CloudShareTitle", nil);
    [ctx performBlockAndWait:^{
        [ctx save:nil];
    }];
    return rootShareFolder;
}

+ (File*) rootTeamSpaceFolder:(NSString*)teamId name:(NSString*)teamName {
    AppDelegate* appDelegate = [UIApplication sharedApplication].delegate;
    File * rootTeamFolder = nil;
    NSError* error = nil;
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileId=%@ AND fileOwner=%@", @"0", teamId];
    NSManagedObjectContext* ctx = appDelegate.localManager.managedObjectContext;
    NSFetchRequest* request = [[NSFetchRequest alloc] init];
    NSEntityDescription * fileEntity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    [request setEntity:fileEntity];
    [request setPredicate:predicate];
    File* file = [[ctx executeFetchRequest:request error:&error] lastObject];
    if (file) {
        rootTeamFolder = file;
    } else {
        rootTeamFolder = [NSEntityDescription insertNewObjectForEntityForName:@"File" inManagedObjectContext:ctx];
        rootTeamFolder.fileOwner= teamId;
        rootTeamFolder.fileId = @"0";
        rootTeamFolder.fileName = teamName;
        rootTeamFolder.fileType = @(TypeFolder);
        [ctx performBlockAndWait:^{
            [ctx save:nil];
        }];
    }
    return rootTeamFolder;
}

#pragma mark subItem
- (NSArray*) subItems {
    if (!self.fileId) {
        return nil;
    }
    return [self subItemsWithSorts:nil offset:nil limit:nil];
}

- (NSArray*) subFileItems {
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileParent=%@ AND fileOwner=%@ AND fileType!=%@", self.fileId, self.fileOwner, @(TypeFolder)];
    return  [self searchWithPredicate:predicate sorts:nil offset:nil limit:nil];
}

- (NSArray*) subFolderItems {
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileParent=%@ AND fileOwner=%@ AND fileType=%@", self.fileId, self.fileOwner, @(TypeFolder)];
    return  [self searchWithPredicate:predicate sorts:nil offset:nil limit:nil];
}

- (NSArray*) subItemsWithSorts:(NSArray*)sorts offset:(NSNumber *)offset limit:(NSNumber *)limit {
    NSPredicate* predicate = nil;
    if ([self isShareRoot]) {
        predicate = [NSPredicate predicateWithFormat:@"fileParent=%@ AND fileShareUser=%@", self.fileId, self.fileShareUser];
    } else {
        predicate = [NSPredicate predicateWithFormat:@"fileParent=%@ AND fileOwner=%@", self.fileId, self.fileOwner];
    }
    return [self searchWithPredicate:predicate sorts:sorts offset:offset limit:limit];
}

- (NSArray*) searchWithPredicate:(NSPredicate*)predicate sorts:(NSArray*)sorts offset:(NSNumber*)offset limit:(NSNumber*)limit {
    NSError* error = nil;
    NSFetchRequest* request = [[NSFetchRequest alloc] init];
    if (sorts) {
        [request setSortDescriptors:sorts];
    } else {
        NSSortDescriptor *nameSort = [[NSSortDescriptor alloc] initWithKey:@"fileName" ascending:YES];
        [request setSortDescriptors:@[nameSort]];
    }
    [request setEntity:self.entity];
    [request setPredicate:predicate];
    [request setFetchBatchSize:20];
    offset?[request setFetchOffset:offset.unsignedIntegerValue]:nil;
    limit?[request setFetchLimit:limit.unsignedIntegerValue]:nil;
    return [self.managedObjectContext executeFetchRequest:request error:&error];
}

#pragma mark parent
- (File*) parent {
    NSPredicate* predicate = [NSPredicate predicateWithFormat:@"fileId=%@ AND fileOwner=%@", self.fileParent, self.fileOwner];
    return [[self searchWithPredicate:predicate sorts:nil offset:nil limit:nil] lastObject];
}
#pragma mark fileSearch
+ (File*) getFileWithFileId:(NSString*)fileId fileOwner:(NSString*)fileOwner {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    return [File getFileWithFileId:fileId fileOwner:fileOwner ctx:ctx];
}

+ (File*)getFileWithFileId:(NSString *)fileId fileOwner:(NSString *)fileOwner ctx:(NSManagedObjectContext*)ctx {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileId = %@ AND fileOwner = %@",fileId,fileOwner];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setPredicate:predicate];
    [request setEntity:entity];
    return [[ctx executeFetchRequest:request error:nil] lastObject];
}

#pragma mark syncAlbumFolder
+ (File*)assetMainFolder {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileAlbumMain = %@ && fileOwner = %@",@(1),appDelegate.localManager.userCloudId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:ctx];
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    [fetchRequest setEntity:entity];
    [fetchRequest setPredicate:predicate];
    NSArray *array = [ctx executeFetchRequest:fetchRequest error:nil];
    return [array lastObject];
}
- (File*)assetFolderWithKey:(NSString *)albumKey {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileAlbumFolderKey=%@ AND fileParent=%@ AND fileOwner=%@",albumKey,self.fileId,self.fileOwner];
    return [[self searchWithPredicate:predicate sorts:nil offset:nil limit:nil] lastObject];
}

- (File*)assetFolderWithName:(NSString *)albumName {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileName=%@ AND fileParent=%@ AND fileOwner=%@",albumName,self.fileId,self.fileOwner];
    return [[self searchWithPredicate:predicate sorts:nil offset:nil limit:nil] lastObject];
}

#pragma mark attachmentFolder
- (File*)attachmentFolderWithTag:(NSString*)attachmentTag {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileAttachmentFolderTag=%@ AND fileParent=%@ AND fileOwner=%@",attachmentTag,self.fileId,self.fileOwner];
    return [[self searchWithPredicate:predicate sorts:nil offset:nil limit:nil] lastObject];
}

- (File*)attachmentFolderWithName:(NSString *)attachmentName {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fileName=%@ AND fileParent=%@ AND fileOwner=%@",attachmentName,self.fileId,self.fileOwner];
    return [[self searchWithPredicate:predicate sorts:nil offset:nil limit:nil] lastObject];
}

#pragma mark insertFile
+ (File*) fileInsertWithInfo:(NSDictionary*)fileInfo context:(NSManagedObjectContext*)ctx {
    NSNumber *fileId = [fileInfo objectForKey:@"id"]?[fileInfo objectForKey:@"id"]:[fileInfo objectForKey:@"nodeId"];
    NSNumber *fileOwner = [fileInfo objectForKey:@"ownedBy"] ? [fileInfo objectForKey:@"ownedBy"] : [fileInfo objectForKey:@"ownerId"];
    File *file;
    if (fileId) {
        file = [File getFileWithFileId:fileId.stringValue fileOwner:fileOwner.stringValue ctx:ctx];
    }
    if (!file) {
        file = [NSEntityDescription insertNewObjectForEntityForName:@"File" inManagedObjectContext:ctx];
    }
    file.fileId = fileId.stringValue;
    file.fileOwner = fileOwner.stringValue;
    file.fileShareUser = nil;
    file.fileMD5 = nil;
    
    file.fileAlbumMain = @(0);
    file.fileAlbumFolderKey = nil;
    file.fileAlbumUrl = nil;
    
    file.fileUpdateFlag = @(0);
    
    file.transportTask = nil;
    
    file.fileShareNewFlag = @(0);
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if ([fileInfo objectForKey:@"sharedUserId"] && [[[fileInfo objectForKey:@"sharedUserId"] stringValue] isEqualToString:appDelegate.localManager.userCloudId]) {
        file.fileShareNewFlag = (![UserSetting defaultSetting].cloudUserFirstLogin.boolValue) ? @(1):@(0);
    }
    
    file.fileUpdateFlag = @(0);
    
    [file setFileInfo:fileInfo];
    return file;
}

- (BOOL) sameWithFileInfo:(NSDictionary*)fileInfo {
    NSNumber *fileId = [fileInfo objectForKey:@"id"]?[fileInfo objectForKey:@"id"]:[fileInfo objectForKey:@"nodeId"];
    NSNumber *fileOwner = [fileInfo objectForKey:@"ownedBy"]?[fileInfo objectForKey:@"ownedBy"]:[fileInfo objectForKey:@"ownerId"];
    if (![self.fileId isEqualToString:fileId.stringValue]) {
        return NO;
    }
    if (![self.fileOwner isEqualToString:fileOwner.stringValue]) {
        return NO;
    }
    [self setFileInfo:fileInfo];
    return YES;
}

- (void) setFileInfo:(NSDictionary*)fileInfo {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    if ([fileInfo objectForKey:@"sharedUserId"] && [[[fileInfo objectForKey:@"sharedUserId"] stringValue] isEqualToString:appDelegate.localManager.userCloudId]) {
        self.fileShareUser = [[fileInfo objectForKey:@"sharedUserId"] stringValue];
        self.fileParent = @"-0";
    } else {
        self.fileUpdateFlag = @([self fileNeedUpdate:[fileInfo objectForKey:@"md5"]]);
        self.fileParent = [[fileInfo objectForKey:@"parent"] stringValue];
    }
    
    self.fileOwnerName = [fileInfo objectForKey:@"ownerName"];
    
    if (![self.fileName isEqualToString:[fileInfo objectForKey:@"name"]]) {
        self.fileName = [fileInfo objectForKey:@"name"];
        self.fileSortNameKey = [self sortNameKey:self.fileName];
    }
    
    if (![[fileInfo objectForKey:@"type"] boolValue]) {
        self.fileType = @(TypeFolder);
    } else {
        self.fileType = [self resourceType:self.fileName];
    }
    
    if ([fileInfo objectForKey:@"localMD5"]) {
        self.fileLocalMD5 = [fileInfo objectForKey:@"localMD5"];
    }
    
    self.fileModifiedDate = [fileInfo objectForKey:@"modifiedAt"]?[NSDate dateWithTimeIntervalSince1970:[[fileInfo objectForKey:@"modifiedAt"] doubleValue]/1000]:[NSDate date];
    self.fileSortTimeKey = [self sortTimeKey:self.fileModifiedDate];
    
    NSNumber *size = [fileInfo objectForKey:@"size"];
    if (size) {
        self.fileSize = size;
    }
    [fileInfo objectForKey:@"size"]?self.fileSize = [fileInfo objectForKey:@"size"]:@(0);
    self.fileSortSizeKey = self.fileSize;
    
    NSDictionary *thumbnailInfo = [[fileInfo objectForKey:@"thumbnailUrlList"] lastObject];
    NSString *thumbnailRemotePath = [thumbnailInfo objectForKey:@"thumbnailUrl"];
    self.fileThumbnailRemotePath = thumbnailRemotePath;
}
#pragma mark removeFile
- (void) fileRemove:(void (^)())completion {
    if ([self isFault]) {
        if (completion) {
            completion();
        }
        return;
    }
    NSString *fileThumbnailLocalPath = [self fileThumbnailLocalPath];
    if (fileThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:fileThumbnailLocalPath error:nil];
    }
    NSString *fileDataLocalPath = [self fileDataLocalPath];
    if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:fileDataLocalPath error:nil];
    }
    NSString *fileCompressImagePath = [self fileCompressImagePath];
    if (fileCompressImagePath && [[NSFileManager defaultManager] fileExistsAtPath:fileCompressImagePath]) {
        [[NSFileManager defaultManager] removeItemAtPath:fileCompressImagePath error:nil];
    }
    NSArray *subItems = self.subItems;
    FileMultiOperation *operation = [[FileMultiOperation alloc] init];
    operation.callingObj = [NSSet setWithArray:subItems];
    operation.completionBlock = ^(NSSet *succeeded, NSSet *failed) {
        if (self.transportTask) {
            [self.transportTask remove];
        }
        [self.managedObjectContext deleteObject:self];
        if (completion) {
            completion();
        }
    };
    if (subItems.count == 0) {
        operation.completionBlock(nil,nil);
    }
    for (File *subItem in subItems) {
        [subItem fileRemove:^{
            [operation onSuceess:subItem];
        }];
    }
}

#pragma mark fileUpdateFlag
- (BOOL) fileNeedUpdate:(NSString*)fileMD5 {
    if ((fileMD5 != nil) && ![fileMD5 isEqual:self.fileMD5]) {
        NSString *fileThumbnailLocalPath = [self fileThumbnailLocalPath];
        if (fileThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
            [[NSFileManager defaultManager] removeItemAtPath:fileThumbnailLocalPath error:nil];
        }
        self.fileMD5 = fileMD5;
        self.fileLocalMD5 = nil;
        NSString *fileDataLocalPath = [self fileDataLocalPath];
        if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
            return YES;
        }
    }
    return NO;
}

-(void)setFileUpdateFlagWithContext:(NSManagedObjectContext*)ctx
{
    File *file = (File*)[ctx objectWithID:self.objectID];
    self.fileUpdateFlag = @(0);
    file.fileUpdateFlag = @(0);
    NSArray *subItems = [file subItems];
    if ([file isCloudRoot]) {
        for (File* subItem in subItems) {
            if ([subItem isFile]) {
                continue;
            }
            if (subItem.transportTask.taskStatus.integerValue != TaskSucceed) {
                continue;
            }
            [subItem setFileUpdateFlagWithContext:ctx];
        }
        
    }
    if (file.transportTask.taskStatus.integerValue == TaskSucceed) {
        for (File* subItem in subItems) {
            if ([subItem isFolder]) {
                [subItem setFileUpdateFlagWithContext:ctx];
            }
            if (subItem.fileUpdateFlag.integerValue == 1) {
                self.fileUpdateFlag = @(1);
                file.fileUpdateFlag = @(1);
            }
            if (subItem.transportTask.taskStatus.integerValue != TaskSucceed) {
                self.fileUpdateFlag = @(1);
                file.fileUpdateFlag = @(1);
            }
        }
    }
    if (file.transportTask.taskStatus.integerValue == 0) {
        File *parent = [file parent];
        if (parent.transportTask.taskStatus.integerValue == TaskSucceed) {
            self.fileUpdateFlag = @(1);
            file.fileUpdateFlag = @(1);
            parent.fileUpdateFlag = @(1);
        }
    }
}

#pragma mark resourceType
- (NSNumber*) resourceType:(NSString*)fileName {
    NSString* extension = [[fileName pathExtension] lowercaseString];
    if ([extension isEqual:@"doc"]||[extension isEqual:@"docx"]) {
        return @(TypeWord);
    } else if ([extension isEqual:@"xls"]||[extension isEqual:@"xlsx"]) {
        return @(TypeExcel);
    } else if ([extension isEqual:@"ppt"]||[extension isEqual:@"pptx"]) {
        return @(TypePPT);
    } else if ([extension isEqual:@"pdf"]) {
        return @(TypePDF);
    } else if ([extension isEqual:@"txt"]) {
        return @(TypeTXT);
    } else if ([extension isEqual:@"jpeg"]||[extension isEqual:@"jpg"]||
               [extension isEqual:@"png"]||[extension isEqual:@"bmp"]||
               [extension isEqual:@"gif"]||[extension isEqual:@"tiff"]||
               [extension isEqual:@"raw"]||[extension isEqual:@"ppm"]||
               [extension isEqual:@"pgm"]||[extension isEqual:@"pbm"]||
               [extension isEqual:@"pnm"]||[extension isEqual:@"webp"]) {
        return @(TypeImage);
    } else if ([extension isEqual:@"avi"]||[extension isEqual:@"mov"]||
               [extension isEqual:@"wmv"]||[extension isEqual:@"3gp"]||
               [extension isEqual:@"flv"]||[extension isEqual:@"rmvb"]||
               [extension isEqual:@"mpg"]||[extension isEqual:@"mp4"]||
               [extension isEqual:@"mpeg"]||[extension isEqual:@"mkv"]) {
        return @(TypeVideo);
    } else if ([extension isEqual:@"mp3"]||[extension isEqual:@"wma"]||
               [extension isEqual:@"wav"]||[extension isEqual:@"ape"]) {
        return @(TypeAudio);
    } else if ([extension isEqual:@"zip"]||[extension isEqual:@"rar"]||
               [extension isEqual:@"gzip"]||[extension isEqual:@"tar"]) {
        return @(TypeRAR);
    } else {
        return @(TypeUnknow);
    }
}

#pragma mark sortKey
- (NSString*) sortNameKey:(NSString*)fileName {
    NSMutableString* fname = [NSMutableString stringWithString:fileName];
    CFRange range = CFRangeMake(0, 1);
    BOOL bSuccess = CFStringTransform((__bridge CFMutableStringRef)fname, &range, kCFStringTransformToLatin, NO);
    if (bSuccess) {
        bSuccess = CFStringTransform((__bridge CFMutableStringRef)fname, &range, kCFStringTransformStripCombiningMarks, NO);
    }
    NSString* firstLetter = nil;
    if (bSuccess && range.length > 0) {
        NSRange nsRange = NSMakeRange(range.location, 1);
        firstLetter = [[fname substringWithRange:nsRange] uppercaseString];
    }
    if (firstLetter && ([firstLetter compare:@"A"] < 0 || [firstLetter compare:@"Z"] > 0)) {
        firstLetter = @"#";
    }
    return firstLetter?firstLetter:@"#";
}

- (NSString*) sortTimeKey:(NSDate*)fileModifiedDate {
    NSTimeZone* GTMzone = [NSTimeZone timeZoneForSecondsFromGMT:0];
    NSDateFormatter *dateFormatter =[[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"YYYY/MM"];
    [dateFormatter setTimeZone:GTMzone];
    return [dateFormatter stringFromDate:fileModifiedDate];
}                                                                           
#pragma mark thumbnail
- (NSString*)fileDataLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *fileDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"File"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *fileDataDirectory = [fileDirectory stringByAppendingPathComponent:@"Data"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileDataDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileDataDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [fileDataDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", self.fileOwner, self.fileParent, self.fileName]];
}

- (NSString*) fileCacheLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *fileDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"File"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *fileCacheDirectory = [fileDirectory stringByAppendingPathComponent:@"Cache"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileCacheDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileCacheDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [fileCacheDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", self.fileOwner, self.fileParent, self.fileName]];
}

- (NSString*) fileThumbnailLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *fileDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"File"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *fileThumbnailDirectory = [fileDirectory stringByAppendingPathComponent:@"Thumbnail"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileThumbnailDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [fileThumbnailDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", self.fileOwner, self.fileParent, self.fileName]];
}

#pragma mark compress
- (NSString*) fileCompressImagePath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *fileDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"File"];
    BOOL isDirectory ;
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *fileCompressImageDirectory = [fileDirectory stringByAppendingPathComponent:@"Compress"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:fileCompressImageDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:fileCompressImageDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [fileCompressImageDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", self.fileOwner, self.fileParent, self.fileName]];
}

- (void) fileCompressImage {
    NSString *fileDataLocalPath = [self fileDataLocalPath];
    if (!fileDataLocalPath || ![[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
        return;
    }

    UIImage *imageSmall;
    UIImage *imageOrginal = [UIImage imageWithData:[NSData dataWithContentsOfFile:fileDataLocalPath options:NSDataReadingMappedIfSafe error:nil]];
    CGSize imageOrginalSize = imageOrginal.size;
    CGFloat boundLimit;
    CGSize imageSmallSize;
    CGFloat max = MAX(imageOrginalSize.width, imageOrginalSize.height);
    if (imageOrginalSize.width > imageOrginalSize.height) {
        boundLimit = [UIScreen mainScreen].bounds.size.width;
    }else{
        boundLimit = [UIScreen mainScreen].bounds.size.height;
    }
    
    CGFloat ratio = imageOrginalSize.height / imageOrginalSize.width;
    if (imageOrginalSize.width > imageOrginalSize.height) {
        imageSmallSize = CGSizeMake(boundLimit, boundLimit*ratio);
    } else {
        imageSmallSize = CGSizeMake(boundLimit/ratio, boundLimit);
    }
    
    if (max < boundLimit) {
        imageSmallSize = imageOrginalSize;
        CGFloat scale = [[UIScreen mainScreen] scale];
        UIGraphicsBeginImageContextWithOptions(imageSmallSize, YES, scale);
        [imageOrginal drawInRect:CGRectMake(0, 0, imageSmallSize.width, imageSmallSize.height)
                       blendMode:kCGBlendModeNormal alpha:1.0];
        imageSmall = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
    }else{
        long limit = imageSmallSize.height > imageSmallSize.width ? imageSmallSize.height:imageSmallSize.width;
        NSString *path = [fileDataLocalPath stringByExpandingTildeInPath];
        CGImageSourceRef imageSource = CGImageSourceCreateWithURL((__bridge CFURLRef)[NSURL fileURLWithPath: path], NULL);
        if (imageSource == NULL)
        {
            return;
        }
        
        if (CGImageSourceGetType(imageSource) == NULL)
        {
            CFRelease(imageSource);
            return;
        }
        
        NSDictionary *options = [[NSDictionary alloc] initWithObjectsAndKeys:
                                 [NSNumber numberWithBool:YES], (NSString *)kCGImageSourceCreateThumbnailFromImageAlways,
                                 [NSNumber numberWithLong:limit*2], (NSString *)kCGImageSourceThumbnailMaxPixelSize,
                                 nil];
        CGImageRef thumbnail = CGImageSourceCreateThumbnailAtIndex(imageSource, 0, (__bridge CFDictionaryRef)options);
        CFRelease(imageSource);
        if (thumbnail == NULL) {
            return;
        }
        imageSmall = [UIImage imageWithCGImage:thumbnail];
        CGImageRelease(thumbnail);
    }
    NSString *fileCompressImagePath = [self fileCompressImagePath];
    if (fileCompressImagePath && [[NSFileManager defaultManager]fileExistsAtPath:fileCompressImagePath]) {
        [[NSFileManager defaultManager] removeItemAtPath:fileCompressImagePath error:nil];
    }
    NSData* imageData = UIImageJPEGRepresentation(imageSmall, 0.1);
    [[NSFileManager defaultManager] createFileAtPath:fileCompressImagePath contents:imageData attributes:nil];
}

#pragma mark clearCache
- (void)clearCacheWithContext:(NSManagedObjectContext*)ctx {
    if (self.transportTask.taskStatus.integerValue != TaskSucceed) {
        return;
    }
    NSError* error = nil;
    File* file = (File*)[ctx objectWithID:self.objectID];
    NSString *fileDataLocalPath = [file fileDataLocalPath];
    if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:fileDataLocalPath error:&error];
    }
    NSString *fileCompressImagePath = [file fileCompressImagePath];
    if (fileCompressImagePath && [[NSFileManager defaultManager] fileExistsAtPath:fileCompressImagePath]) {
        [[NSFileManager defaultManager] removeItemAtPath:fileCompressImagePath error:&error];
    }
    if (error) {
        [SNLog Log:LFatal :@"Failed clear file:%@ ! %@",file.fileName, error];
    } else {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onebox.file_change" object:[NSURL URLWithString:[self fileDataLocalPath]]];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"com.huawei.onebox.file_change" object:[NSURL URLWithString:[self fileCompressImagePath]]];
        [SNLog Log:LInfo :@"success clear file:%@",file.fileName];
    }
    if (file.transportTask) {
        file.transportTask.taskStatus = @(0);
    }
    file.fileUpdateFlag = @(0);

    if ([file isFolder]) {
        NSArray *subItems = [file subItems];
        for (File *subItem in subItems) {
            [subItem clearCacheWithContext:ctx];
        }
    }
}

#pragma mark transportTaskDelete
- (void)deleteFileTransportTask {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        shadow.transportTask = nil;
        [ctx save:nil];
    }];
}

- (void)saveFileParent:(NSString *)FileParent{
    if ([self.fileParent isEqualToString:FileParent]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        shadow.fileParent = FileParent;
        [ctx save:nil];
    }];
}
- (void)saveFileName:(NSString *)fileName{
    if ([self.fileName isEqualToString:fileName]) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        shadow.fileName = fileName;
        shadow.fileSortNameKey = [shadow sortNameKey:fileName];
        [ctx save:nil];
    }];
}

- (void)saveFileAlbumFolderKey:(NSString *)key {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        if (key) {
            shadow.fileAlbumFolderKey = key;
        } else {
            shadow.fileAlbumMain = @(1);
        }
        [ctx save:nil];
    }];
}

- (void)saveFileShareNewFlag:(NSNumber *)fileShareNewFlag {
    if (self.fileShareNewFlag.boolValue == fileShareNewFlag.boolValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        shadow.fileShareNewFlag = fileShareNewFlag;
        [ctx save:nil];
    }];
}

- (void)saveFileMD5:(NSString *)fileMD5 {
    if (self.fileMD5 == fileMD5) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        shadow.fileMD5 = fileMD5;
        [ctx save:nil];
    }];
}

- (void)saveFileLocalMD5:(NSString *)fileLocalMD5 {
    if (self.fileLocalMD5 == fileLocalMD5) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        File *shadow = (File*)[ctx objectWithID:self.objectID];
        shadow.fileLocalMD5 = fileLocalMD5;
        [ctx save:nil];
    }];
}


@end
