//
//  FileThumbnail.h
//  OneMail
//
//  Created by cse  on 15/10/27.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class File;
@class Version;

@interface FileThumbnail : NSObject

+ (void) imageWithVersion:(Version*)version imageView:(UIImageView *)imageView;
+ (void) imageWithFile:(File*)file imageView:(UIImageView*)imageView;
+ (void) headerImageWithFolder:(File*)file imageView:(UIImageView*)imageView;

@end
