//
//  CloudPreviewController.h
//  OneMail
//
//  Created by cse  on 15/11/12.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "DribbblePhotoAlbumViewController.h"

@class File;
@class Version;
@class TransportTask;

@interface CloudPreviewController : DribbblePhotoAlbumViewController
@property (nonatomic, strong) TransportTask *transportTask;

- (id)initWithFile:(File *)file;
- (id)initWithVersion:(Version *)version;
+ (BOOL) isSupportedImage:(NSString*) fileName;

@end
