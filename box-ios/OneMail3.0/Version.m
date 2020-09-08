//
//  Version.m
//  OneMail
//
//  Created by cse  on 15/11/19.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "Version.h"
#import "AppDelegate.h"
#import "TransportTask.h"
#import <ImageIO/ImageIO.h>

@implementation Version

@dynamic versionId;
@dynamic versionFileId;
@dynamic versionFileName;
@dynamic versionOwner;
@dynamic versionModifiedDate;
@dynamic versionModifiedDateString;
@dynamic versionSize;
@dynamic versionObjectId;
@dynamic transportTask;

- (void)setVersion:(NSDictionary *)versionInfo{
    self.versionId = [[versionInfo objectForKey:@"id"] stringValue];
    self.versionFileId = [versionInfo objectForKey:@"fileId"];
    self.versionFileName = [versionInfo objectForKey:@"name"];
    self.versionModifiedDate = [NSDate dateWithTimeIntervalSince1970:[[versionInfo objectForKey:@"modifiedAt"] doubleValue]/1000];
    self.versionSize = [versionInfo objectForKey:@"size"];
    self.versionObjectId = [versionInfo objectForKey:@"objectId"];
    self.versionOwner = [versionInfo objectForKey:@"fileOwner"];
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd hh:mm:ss"];
    self.versionModifiedDateString = [dateFormatter stringFromDate:self.versionModifiedDate];
}
+ (Version *)getVersionWithVersionIdAndFileId:(NSString*)versionObjectId FileId:(NSString *)fileId{
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = delegate.localManager.managedObjectContext;
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"versionObjectId = %@ AND versionFileId = %@",versionObjectId,fileId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Version" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"versionModifiedDate" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [ctx executeFetchRequest:request error:nil].firstObject;
}

+ (Version *)getVersionWithObjectID:(NSManagedObjectID*)objectID {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.managedObjectContext;
    return (Version*)[ctx objectWithID:objectID];
}

- (TransportTaskHandle*)download {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    __block TransportTask *downloadTask = nil;
    [ctx performBlockAndWait:^{
        Version *shadow = (Version*)[ctx objectWithID:self.objectID];
        downloadTask = [TransportTask taskInsertWithVersion:shadow type:TaskVersionDownload];
        [ctx save:nil];
    }];
    if (downloadTask) {
        TransportTask *mainTask = (TransportTask*)[appDelegate.localManager.managedObjectContext objectWithID:downloadTask.objectID];
        return mainTask.taskHandle;
    }
    return nil;
}

- (NSString*)versionDataLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *versionDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Version"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:versionDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:versionDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *versionDataDirectory = [versionDirectory stringByAppendingPathComponent:@"Data"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:versionDataDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:versionDataDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [versionDataDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@",self.versionOwner,self.versionObjectId,self.versionFileName]];
}

- (NSString*)versionCacheLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *versionDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Version"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:versionDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:versionDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *versionCacheDirctory = [versionDirectory stringByAppendingPathComponent:@"Cache"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:versionCacheDirctory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:versionCacheDirctory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [versionCacheDirctory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@", self.versionOwner, self.versionObjectId, self.versionFileName]];
}

- (NSString*)versionCompressImagePath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *versionDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Version"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:versionDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:versionDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *versionCompressDirectory = [versionDirectory stringByAppendingPathComponent:@"Compress"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:versionCompressDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:versionCompressDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [versionCompressDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@_%@",appDelegate.localManager.userCloudId,self.versionObjectId,self.versionFileName]];
}

- (void) versionCompressImage {
    NSString *versionDataLocalPath = [self versionDataLocalPath];
    if (!versionDataLocalPath || ![[NSFileManager defaultManager] fileExistsAtPath:versionDataLocalPath]) {
        return;
    }
    
    UIImage *imageSmall;
    UIImage *imageOrginal = [UIImage imageWithData:[NSData dataWithContentsOfFile:versionDataLocalPath options:NSDataReadingMappedIfSafe error:nil]];
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
        NSString *path = [versionDataLocalPath stringByExpandingTildeInPath];
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
    NSString *versionCompressImagePath = [self versionCompressImagePath];
    if (versionCompressImagePath && [[NSFileManager defaultManager]fileExistsAtPath:versionCompressImagePath]) {
        [[NSFileManager defaultManager] removeItemAtPath:versionCompressImagePath error:nil];
    }
    NSData* imageData = UIImageJPEGRepresentation(imageSmall, 0.1);
    [[NSFileManager defaultManager] createFileAtPath:versionCompressImagePath contents:imageData attributes:nil];
}

@end
