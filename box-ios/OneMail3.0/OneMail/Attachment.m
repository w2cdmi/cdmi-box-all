//
//  Attachment.m
//  OneMail
//
//  Created by cse  on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "Attachment.h"
#import "File.h"
#import "Message.h"
#import "AppDelegate.h"


@implementation Attachment

@dynamic attachmentId;
@dynamic attachmentFileId;
@dynamic attachmentFileOwner;
@dynamic attachmentMessageId;
@dynamic attachmentName;
@dynamic attachmentSize;
@dynamic attachmentType;
@dynamic attachmentUploadFlag;
@dynamic attachmentDisplayFlag;

+ (Attachment*)getAttachmentWithAttachmentId:(NSString *)attachmentId ctx:(NSManagedObjectContext *)ctx {
    if (!ctx) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"attachmentId = %@",attachmentId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Attachment" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"attachmentName" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [[ctx executeFetchRequest:request error:nil] lastObject];
}

+ (NSArray*)getAttachmentWithoutDisplayWithMessageId:(NSString*)messageId ctx:(NSManagedObjectContext *)ctx {
    if (!ctx) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"attachmentMessageId = %@ AND attachmentDisplayFlag = %@",messageId,@(0)];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Attachment" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"attachmentName" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [ctx executeFetchRequest:request error:nil];
}

+ (NSArray*)getAttachmentWithMessageId:(NSString *)messageId ctx:(NSManagedObjectContext *)ctx {
    if (!ctx) {
        AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
        ctx = appDelegate.localManager.managedObjectContext;
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"attachmentMessageId = %@",messageId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Attachment" inManagedObjectContext:ctx];
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:@"attachmentName" ascending:YES];
    [request setEntity:entity];
    [request setPredicate:predicate];
    [request setSortDescriptors:@[sort]];
    return [ctx executeFetchRequest:request error:nil];
}

+ (Attachment*)attachmentInsertWithInfo:(NSDictionary*)attachmentInfo ctx:(NSManagedObjectContext*)ctx {
    NSString *attachmentId = [attachmentInfo objectForKey:@"attachmentId"];
    NSString *attachmentMessageId = [attachmentInfo objectForKey:@"attachmentMessageId"];
    Attachment *attachment = [Attachment getAttachmentWithAttachmentId:attachmentId ctx:ctx];
    if (!attachment) {
        attachment = [NSEntityDescription insertNewObjectForEntityForName:@"Attachment" inManagedObjectContext:ctx];
    }
    attachment.attachmentId = attachmentId;
    attachment.attachmentMessageId = attachmentMessageId;
    attachment.attachmentName = [attachmentInfo objectForKey:@"attachmentName"];
    attachment.attachmentSize = [attachmentInfo objectForKey:@"attachmentSize"];
    attachment.attachmentType = [attachmentInfo objectForKey:@"attachmentType"];
    attachment.attachmentUploadFlag = [attachmentInfo objectForKey:@"attachmentUploadFlag"];
    attachment.attachmentDisplayFlag = @(0);
    
    attachment.attachmentFileId = [[attachmentInfo objectForKey:@"attachmentFileId"] stringValue];
    attachment.attachmentFileOwner = [[attachmentInfo objectForKey:@"attachmentFileOwner"] stringValue];
    if (attachment.attachmentFileId && attachment.attachmentFileOwner) {
        File *file = [File getFileWithFileId:attachment.attachmentFileId fileOwner:attachment.attachmentFileOwner];
        if (file) {
            file.fileAttachmentId = attachment.attachmentId;
            NSString *fileThumbnailLocalPath = [file fileThumbnailLocalPath];
            if (fileThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
                NSString *attachmentThumbnailLocalPath = [attachment attachmentThumbnailLocalPath];
                if (attachmentThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentThumbnailLocalPath]) {
                    [[NSFileManager defaultManager] removeItemAtPath:attachmentThumbnailLocalPath error:nil];
                }
                [[NSFileManager defaultManager] copyItemAtPath:fileThumbnailLocalPath toPath:attachmentThumbnailLocalPath error:nil];
            }
        }
    }
    
    NSString *attachmentDataPath = [Attachment attachmentDataCachePathWithId:attachment.attachmentId name:attachment.attachmentName];
    if (attachmentDataPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentDataPath]) {
        NSString *attachmentDataLocalPath = [attachment attachmentDataLocalPath];
        if (attachmentDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentDataLocalPath]) {
            [[NSFileManager defaultManager] removeItemAtPath:attachmentDataLocalPath error:nil];
        }
        [[NSFileManager defaultManager] moveItemAtPath:attachmentDataPath toPath:attachmentDataLocalPath error:nil];
    }
    
    NSString *attachmentThumbnailLocalPath = [attachment attachmentThumbnailLocalPath];
    if (attachmentThumbnailLocalPath && ![[NSFileManager defaultManager] fileExistsAtPath:attachmentThumbnailLocalPath]) {
        NSString *attachmentDataLocalPath = [attachment attachmentDataLocalPath];
        if (attachmentDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentDataLocalPath]) {
            if ([CommonFunction isImageResource:attachment.attachmentName]) {
                UIImage *sourceImage = [UIImage imageWithContentsOfFile:attachmentDataLocalPath];
                UIImage *thumbnailImage = [CommonFunction imageCompressWithImage:sourceImage targetSize:CGSizeMake(48, 48)];
                NSData *thumbnailData = UIImageJPEGRepresentation(thumbnailImage, 1.0);
                [[NSFileManager defaultManager] createFileAtPath:attachmentThumbnailLocalPath contents:thumbnailData attributes:nil];
            }
        }
    }
    return attachment;
}


- (void)removeAttachment {
    NSString *attachmentDataLocalPath = [self attachmentDataLocalPath];
    if (attachmentDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentDataLocalPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:attachmentDataLocalPath error:nil];
    }
    [self.managedObjectContext deleteObject:self];
}


+ (void)imageWithResourceThumbnail:(Attachment*)attachment imageView:(UIImageView*)imageView {
    NSString* extension = [[attachment.attachmentName pathExtension] lowercaseString];
    if ([extension isEqual:@"doc"]||[extension isEqual:@"docx"]) {
        imageView.image = [UIImage imageNamed:@"ic_list_word"];
    } else if ([extension isEqual:@"xls"]||[extension isEqual:@"xlsx"]) {
        imageView.image = [UIImage imageNamed:@"ic_list_excel"];
    } else if ([extension isEqual:@"ppt"]||[extension isEqual:@"pptx"]) {
        imageView.image = [UIImage imageNamed:@"ic_list_ppt"];
    } else if ([extension isEqual:@"pdf"]) {
        imageView.image = [UIImage imageNamed:@"ic_list_pdf"];
    } else if ([extension isEqual:@"txt"]) {
        imageView.image = [UIImage imageNamed:@"ic_list_txt"];
    } else if ([extension isEqual:@"jpeg"]||[extension isEqual:@"jpg"]||
               [extension isEqual:@"png"]||[extension isEqual:@"bmp"]||
               [extension isEqual:@"gif"]||[extension isEqual:@"tiff"]||
               [extension isEqual:@"raw"]||[extension isEqual:@"ppm"]||
               [extension isEqual:@"pgm"]||[extension isEqual:@"pbm"]||
               [extension isEqual:@"pnm"]||[extension isEqual:@"webp"]) {
        imageView.image = [UIImage imageNamed:@"ic_list_png"];
    } else if ([extension isEqual:@"avi"]||[extension isEqual:@"mov"]||
               [extension isEqual:@"wmv"]||[extension isEqual:@"3gp"]||
               [extension isEqual:@"flv"]||[extension isEqual:@"rmvb"]||
               [extension isEqual:@"mpg"]||[extension isEqual:@"mp4"]||
               [extension isEqual:@"mpeg"]||[extension isEqual:@"mkv"]) {
        imageView.image = [UIImage imageNamed:@"ic_att_video"];
    } else if ([extension isEqual:@"mp3"]||[extension isEqual:@"wma"]||
               [extension isEqual:@"wav"]||[extension isEqual:@"ape"]) {
        imageView.image = [UIImage imageNamed:@"ic_att_music"];
    } else if ([extension isEqual:@"zip"]||[extension isEqual:@"rar"]||
               [extension isEqual:@"gzip"]||[extension isEqual:@"tar"]) {
        imageView.image = [UIImage imageNamed:@"ic_att_rar"];
    } else {
        imageView.image = [UIImage imageNamed:@"ic_att_defualt"];
    }
}

- (NSString*)attachmentDataLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *attachmentDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Attachment"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *attachmentDataDirectory = [attachmentDirectory stringByAppendingPathComponent:@"Data"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentDataDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentDataDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [attachmentDataDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@",self.attachmentId,self.attachmentName]];
}

+ (NSString*)attachmentDataCachePathWithId:(NSString *)attachmentId name:(NSString *)attachmentName {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *attachmentDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Attachment"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *attachmentCacheDirectory = [attachmentDirectory stringByAppendingPathComponent:@"Cache"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentCacheDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentCacheDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [attachmentCacheDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@",attachmentId,attachmentName]];
}

- (NSString*)attachmentThumbnailLocalPath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *attachmentDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Attachment"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *attachmentThumbnailDirectory = [attachmentDirectory stringByAppendingPathComponent:@"Thumbnail"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentThumbnailDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentThumbnailDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [attachmentThumbnailDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@",self.attachmentId,self.attachmentName]];
}

- (NSString*)attachmentCompressImagePath {
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSString *attachmentDirectory = [appDelegate.localManager.userDataPath stringByAppendingPathComponent:@"Attachment"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *attachmentCompressDirectory = [attachmentDirectory stringByAppendingPathComponent:@"Compress"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:attachmentCompressDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:attachmentCompressDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [attachmentCompressDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@",self.attachmentId,self.attachmentName]];
}

- (void)saveAttachmentDisplayFlag:(NSNumber *)displayFlag {
    if (self.attachmentDisplayFlag.boolValue == displayFlag.boolValue) {
        return;
    }
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        Attachment *shadow = (Attachment*)[ctx objectWithID:self.objectID];
        shadow.attachmentDisplayFlag = displayFlag;
        [ctx save:nil];
    }];
}

@end
