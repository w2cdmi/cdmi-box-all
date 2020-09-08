//
//  UserThumbnail.m
//  OneMail
//
//  Created by cse  on 16/1/29.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import "UserThumbnail.h"
#import "SDImageCache.h"
#import "UIImageView+WebCache.h"
#import "AppDelegate.h"
#import "User+Remote.h"

@implementation UserThumbnail

+ (void) imageWithUser:(User *)user imageView:(UIImageView*)imageView refresh:(BOOL)refresh {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if (refresh) {
            [UserThumbnail imageWithRemoteThumbnail:user imageView:imageView];
        } else {
            [UserThumbnail imageWithLocalThumbnail:user imageView:imageView];
        }
    });
}

+ (void) imageWithLocalThumbnail:(User *)user imageView:(UIImageView *)imageView {
    NSString *userHeadIconPath = [user userHeadIconPath];
    UIImage *image = [[SDWebImageManager sharedManager].imageCache imageFromMemoryCacheForKey:userHeadIconPath];
    if (!image) {
        image = [UIImage imageWithContentsOfFile:userHeadIconPath];
        [[SDWebImageManager sharedManager].imageCache storeImage:image forKey:userHeadIconPath toDisk:NO];
    }
    if (image) {
        dispatch_async(dispatch_get_main_queue(), ^{
            imageView.image = image;
        });
        return;
    } else {
        [UserThumbnail imageWithRemoteThumbnail:user imageView:imageView];
    }
}

+ (void) imageWithRemoteThumbnail:(User*)user imageView:(UIImageView*)imageView {
    if (!user.userSingleId) {
        dispatch_async(dispatch_get_main_queue(), ^{
            imageView.image = [UIImage imageNamed:@"img_portrait_default"];
        });
        return;
    }
    NSString *userHeadIconPath = [user userHeadIconPath];
    [user getUserHeadIcon:^(id retobj) {
        [user saveUserHeadIcon:retobj];
        UIImage *image = [UIImage imageWithData:retobj];
        [[SDWebImageManager sharedManager].imageCache storeImage:image forKey:userHeadIconPath toDisk:NO];
        dispatch_async(dispatch_get_main_queue(), ^{
            imageView.image = [UIImage imageWithData:retobj];
        });
    } failed:^(NSURLRequest *request, NSURLResponse *response, NSError *error, int errorType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSString *userHeadIconPath = [user userHeadIconPath];
            if (userHeadIconPath && [[NSFileManager defaultManager] fileExistsAtPath:userHeadIconPath]) {
                imageView.image = [UIImage imageWithContentsOfFile:userHeadIconPath];
            } else {
                imageView.image = [UIImage imageNamed:@"img_portrait_default"];
            }
        });
    }];
}
@end

