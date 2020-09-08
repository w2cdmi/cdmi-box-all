//
//  FileThumbnail.m
//  OneMail
//
//  Created by cse  on 15/10/27.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "FileThumbnail.h"
#import "SDImageCache.h"
#import "UIImageView+WebCache.h"
#import "AppDelegate.h"
#import "File.h"
#import "Version.h"
#import "Attachment.h"

@implementation FileThumbnail
+ (void) imageWithVersion:(Version*)version imageView:(UIImageView *)imageView {
    if (!imageView || !version) {
        return;
    }
    File *file = [File getFileWithFileId:version.versionFileId fileOwner:version.versionOwner];
    [FileThumbnail imageWithFile:file imageView:imageView];
}

+ (void) imageWithFile:(File*)file imageView:(UIImageView*)imageView {
    if (!imageView || !file) {
        return;
    }
    imageView.contentMode = UIViewContentModeScaleAspectFill;
    imageView.clipsToBounds = YES;
    if (file.fileType.integerValue == TypeFolder) {
        NSString *folderDataLocalPath = [file fileDataLocalPath];
        BOOL isDirectory;
        if (folderDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:folderDataLocalPath isDirectory:&isDirectory] && isDirectory) {
            imageView.image = [UIImage imageNamed:@"ic_list_folder_download"];return;
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_folder"];return;
        }
    }
    NSString *fileThumbnailLocalPath = [file fileThumbnailLocalPath];
    if (fileThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
        [FileThumbnail imageWithLocalThumbnail:file imageView:imageView];return;
    } else {
        if (file.fileThumbnailRemotePath) {
            [FileThumbnail imageWithRemoteThumbnail:file imageView:imageView];return;
        } else {
            [FileThumbnail imageWithResourceThumbnail:file imageView:imageView];return;
        }
    }
}

+ (void) headerImageWithFolder:(File *)file imageView:(UIImageView *)imageView {
    NSString *fileThumbnailLocalPath = [file fileThumbnailLocalPath];
    if (fileThumbnailLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileThumbnailLocalPath]) {
        [FileThumbnail imageWithLocalThumbnail:file imageView:imageView];return;
    } else {
        if (file.fileThumbnailRemotePath) {
            [FileThumbnail imageWithRemoteThumbnail:file imageView:imageView];return;
        } else {
            imageView.image = [UIImage imageNamed:@"img_backup_empty"];return;
        }
    }
}

+ (void) imageWithLocalThumbnail:(File*)file imageView:(UIImageView*)imageView {
    NSString *fileThumbnailLocalPath = [file fileThumbnailLocalPath];
    UIImage *thumbnailImage = [[SDWebImageManager sharedManager].imageCache imageFromMemoryCacheForKey:fileThumbnailLocalPath];
    if (!thumbnailImage) {
        thumbnailImage = [UIImage imageWithContentsOfFile:fileThumbnailLocalPath];
        [[SDWebImageManager sharedManager].imageCache storeImage:thumbnailImage forKey:fileThumbnailLocalPath toDisk:NO];
    }
    if (thumbnailImage) {
        imageView.image = thumbnailImage;return;
    } else {
        [FileThumbnail imageWithResourceThumbnail:file imageView:imageView];
    }
}

+ (void) imageWithRemoteThumbnail:(File*)file imageView:(UIImageView*)imageView {
    NSURL *fileThumbnailRemoteURL = [NSURL URLWithString:file.fileThumbnailRemotePath];
    [imageView sd_setImageWithURL:fileThumbnailRemoteURL placeholderImage:imageView.image options:SDWebImageAllowInvalidSSLCertificates|SDWebImageRefreshCached completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, NSURL *imageURL) {
        if (error) {
            [FileThumbnail imageWithResourceThumbnail:file imageView:imageView];
        } else {
            imageView.image = image;
            NSString *fileThumbnailLocalPath = [file fileThumbnailLocalPath];
            [[SDWebImageManager sharedManager].imageCache storeImage:image forKey:fileThumbnailLocalPath toDisk:NO];
            NSData *imageData = UIImagePNGRepresentation(image);
            [[NSFileManager defaultManager] createFileAtPath:fileThumbnailLocalPath contents:imageData attributes:nil];
            
            if (file.fileAttachmentId) {
                Attachment *attachment = [Attachment getAttachmentWithAttachmentId:file.fileAttachmentId ctx:nil];
                NSString *attachmentThumbnailPath = [attachment attachmentThumbnailLocalPath];
                if (attachmentThumbnailPath && [[NSFileManager defaultManager] fileExistsAtPath:attachmentThumbnailPath]) {
                    [[NSFileManager defaultManager] removeItemAtPath:attachmentThumbnailPath error:nil];
                }
                [[NSFileManager defaultManager] copyItemAtPath:fileThumbnailLocalPath toPath:attachmentThumbnailPath error:nil];
            }
        }
    }];
}

+ (void) imageWithResourceThumbnail:(File*)file imageView:(UIImageView*)imageView {
    NSString *fileDataLocalPath = [file fileDataLocalPath];
    BOOL fileDataExits = NO;
    if (fileDataLocalPath && [[NSFileManager defaultManager] fileExistsAtPath:fileDataLocalPath]) {
        fileDataExits = YES;
    }
    NSString* extension = [[file.fileName pathExtension] lowercaseString];
    if ([extension isEqual:@"doc"]||[extension isEqual:@"docx"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_word_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_word"];
        }
    } else if ([extension isEqual:@"xls"]||[extension isEqual:@"xlsx"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_excel_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_excel"];
        }
    } else if ([extension isEqual:@"ppt"]||[extension isEqual:@"pptx"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_ppt_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_ppt"];
        }
    } else if ([extension isEqual:@"pdf"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_pdf_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_pdf"];
        }
    } else if ([extension isEqual:@"txt"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_txt_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_txt"];
        }
    } else if ([extension isEqual:@"jpeg"]||[extension isEqual:@"jpg"]||
               [extension isEqual:@"png"]||[extension isEqual:@"bmp"]||
               [extension isEqual:@"gif"]||[extension isEqual:@"tiff"]||
               [extension isEqual:@"raw"]||[extension isEqual:@"ppm"]||
               [extension isEqual:@"pgm"]||[extension isEqual:@"pbm"]||
               [extension isEqual:@"pnm"]||[extension isEqual:@"webp"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_png_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_png"];
        }
    } else if ([extension isEqual:@"avi"]||[extension isEqual:@"mov"]||
               [extension isEqual:@"wmv"]||[extension isEqual:@"3gp"]||
               [extension isEqual:@"flv"]||[extension isEqual:@"rmvb"]||
               [extension isEqual:@"mpg"]||[extension isEqual:@"mp4"]||
               [extension isEqual:@"mpeg"]||[extension isEqual:@"mkv"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_video_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_video"];
        }
    } else if ([extension isEqual:@"mp3"]||[extension isEqual:@"wma"]||
               [extension isEqual:@"wav"]||[extension isEqual:@"ape"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_music_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_music"];
        }
    } else if ([extension isEqual:@"zip"]||[extension isEqual:@"rar"]||
               [extension isEqual:@"gzip"]||[extension isEqual:@"tar"]) {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_rar_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_rar"];
        }
    } else {
        if (fileDataExits) {
            imageView.image = [UIImage imageNamed:@"ic_list_default_download"];
        } else {
            imageView.image = [UIImage imageNamed:@"ic_list_default"];
        }
    }
}
@end
