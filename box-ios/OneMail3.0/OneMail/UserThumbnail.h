//
//  UserThumbnail.h
//  OneMail
//
//  Created by cse  on 16/1/29.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class User;

@interface UserThumbnail : NSObject

+ (void) imageWithUser:(User*)user imageView:(UIImageView *)imageView refresh:(BOOL)refresh;

@end
