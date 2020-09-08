//
//  CommonFunction.h
//  OneMail
//
//  Created by cse  on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#define getLocalizedString(key,comment) [CommonFunction getLocalizedString:key Comment:comment]

@interface CommonFunction : NSObject

+ (UIColor*)colorWithString:(NSString *)colorString alpha:(CGFloat)alpha;

+ (NSString*)pretySize:(long long) fsize;

+ (CGSize)labelSizeWithString:(NSString*)string font:(UIFont*)font;
+ (CGSize)labelSizeWithString:(NSString*)string font:(UIFont*)font limitSize:(CGSize)size;
+ (CGSize)labelSizeWithLabel:(UILabel*)label limitSize:(CGSize)size;
+ (UILabel*) labelWithFrame:(CGRect)frame textFont:(UIFont*)textFont textColor:(UIColor*)textColor textAlignment:(NSTextAlignment)textAlignment;

+ (BOOL) betweenDisturbTime;
+ (void) noticification;

+ (BOOL) isImageResource:(NSString*)resourceName;
+ (UIImage*) imageCompressWithImage:(UIImage*)sourceImage targetSize:(CGSize)size;
+ (void) imageCompressFromPath:(NSString*)orignalDataPath toPath:(NSString*)destinationDataPath;

+ (UIView*) tableViewHeaderWithTitle:(NSString*)title;

+ (BOOL)checkMailAccountForm:(NSString*)emailAccount;

+ (UIImage*) thumbnailWithFileName:(NSString*)name;

+ (NSString*)stringFromArray:(NSArray *)stringArray;

+ (NSString *)getLocalizedString:(NSString *)key Comment:(NSString *)comment;

@end
