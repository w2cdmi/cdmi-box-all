//
//  CommonFunction.m
//  OneMail
//
//  Created by cse  on 15/11/14.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CommonFunction.h"
#import "UserSetting.h"
#import <ImageIO/ImageIO.h>
#import <AudioToolbox/AudioToolbox.h>

@implementation CommonFunction

+(UIColor*)colorWithString:(NSString *)colorString alpha:(CGFloat)alpha {
    if (colorString.length != 6) {
        return [UIColor blackColor];
    }
    NSMutableArray *array = [[NSMutableArray alloc] initWithCapacity:6];
    for (int i = 0; i < 6; i++) {
        unichar ch = [colorString characterAtIndex:i];
        if (ch == '0') {
            [array addObject:[NSString stringWithFormat:@"%d",0]];continue;
        } else if (ch == '1') {
            [array addObject:[NSString stringWithFormat:@"%d",1]];continue;
        } else if (ch == '2') {
            [array addObject:[NSString stringWithFormat:@"%d",2]];continue;
        } else if (ch == '3') {
            [array addObject:[NSString stringWithFormat:@"%d",3]];continue;
        } else if (ch == '4') {
            [array addObject:[NSString stringWithFormat:@"%d",4]];continue;
        } else if (ch == '5') {
            [array addObject:[NSString stringWithFormat:@"%d",5]];continue;
        } else if (ch == '6') {
            [array addObject:[NSString stringWithFormat:@"%d",6]];continue;
        } else if (ch == '7') {
            [array addObject:[NSString stringWithFormat:@"%d",7]];continue;
        } else if (ch == '8') {
            [array addObject:[NSString stringWithFormat:@"%d",8]];continue;
        } else if (ch == '9') {
            [array addObject:[NSString stringWithFormat:@"%d",9]];continue;
        } else if (ch == 'A' || ch == 'a') {
            [array addObject:[NSString stringWithFormat:@"%d",10]];continue;
        } else if (ch == 'B' || ch == 'b') {
            [array addObject:[NSString stringWithFormat:@"%d",11]];continue;
        } else if (ch == 'C' || ch == 'c') {
            [array addObject:[NSString stringWithFormat:@"%d",12]];continue;
        } else if (ch == 'D' || ch == 'd') {
            [array addObject:[NSString stringWithFormat:@"%d",13]];continue;
        } else if (ch == 'E' || ch == 'e') {
            [array addObject:[NSString stringWithFormat:@"%d",14]];continue;
        } else if (ch == 'F' || ch == 'f') {
            [array addObject:[NSString stringWithFormat:@"%d",15]];continue;
        } else {
            return [UIColor blackColor];
        }
    }
    CGFloat red = ([array[0] integerValue]*16+[array[1] integerValue])/255.0f;
    CGFloat green = ([array[2] integerValue]*16+[array[3] integerValue])/255.0f;
    CGFloat blue = ([array[4] integerValue]*16+[array[5] integerValue])/255.0f;
    return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
}

+ (NSString*) pretySize:(long long) fsize {
    
    NSDecimalNumberHandler* roundingBehavior = [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:NSRoundDown scale:2 raiseOnExactness:NO raiseOnOverflow:NO raiseOnUnderflow:NO raiseOnDivideByZero:NO];
    if (fsize > 0) {
        if (fsize / (1024 * 1024 * 1000 * 1.0) >= 0.8) {
            NSDecimalNumber *onceDecimal = [[NSDecimalNumber alloc] initWithFloat:fsize / (1024 * 1024 * 1024 * 1.00)];
            NSDecimalNumber *fileRound = [onceDecimal decimalNumberByRoundingAccordingToBehavior:roundingBehavior];
            
            return [NSString stringWithFormat:@"%@GB", fileRound];
        } else if(fsize / (1024 * 1000 * 1.0) >= 0.8) {
            NSDecimalNumber *onceDecimal = [[NSDecimalNumber alloc] initWithFloat:fsize / (1024 * 1024 * 1.00)];
            NSDecimalNumber *fileRound2 = [onceDecimal decimalNumberByRoundingAccordingToBehavior:roundingBehavior];
            
            return [NSString stringWithFormat:@"%@MB", fileRound2];
        } else if(fsize / (1000 * 1.0) >= 0.8) {
            NSDecimalNumber *onceDecimal = [[NSDecimalNumber alloc] initWithFloat:fsize / (1024 * 1.00)];
            NSDecimalNumber *fileRound3 = [onceDecimal decimalNumberByRoundingAccordingToBehavior:roundingBehavior];
            return [NSString stringWithFormat:@"%@KB", fileRound3];
        } else {
//            return [formatter stringFromNumber:fsize];
            return [NSString stringWithFormat:@"%lluB", fsize];
        }
    } else {
        return @"0KB";
    }

}
//NSRoundDown只舍不入
-(NSString *)notRounding:(long long)fsize afterPoint:(int)position{
    NSDecimalNumberHandler* roundingBehavior = [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:NSRoundDown scale:position raiseOnExactness:NO raiseOnOverflow:NO raiseOnUnderflow:NO raiseOnDivideByZero:NO];
    NSDecimalNumber *ouncesDecimal;
    NSDecimalNumber *roundedOunces;
    
    ouncesDecimal = [[NSDecimalNumber alloc] initWithFloat:fsize];
    roundedOunces = [ouncesDecimal decimalNumberByRoundingAccordingToBehavior:roundingBehavior];
    
    return [NSString stringWithFormat:@"%@",roundedOunces];
}

+ (CGSize)labelSizeWithString:(NSString*)string font:(UIFont*)font {
    UILabel *label = [[UILabel alloc] init];
    label.text = string;
    label.font = font;
    return [CommonFunction labelSizeWithLabel:label limitSize:CGSizeMake(1000, 1000)];
}

+ (CGSize)labelSizeWithString:(NSString*)string font:(UIFont*)font limitSize:(CGSize)size {
    UILabel *label = [[UILabel alloc] init];
    label.text = string;
    label.font = font;
    return [CommonFunction labelSizeWithLabel:label limitSize:size];
}

+ (CGSize)labelSizeWithLabel:(UILabel*)label limitSize:(CGSize)size {
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    NSDictionary *attributes = @{NSFontAttributeName:label.font,NSParagraphStyleAttributeName:paragraphStyle.copy};
    return [label.text boundingRectWithSize:size options:NSStringDrawingUsesLineFragmentOrigin attributes:attributes context:nil].size;
}

+ (BOOL)betweenDisturbTime {
    UserSetting *userSetting = [UserSetting defaultSetting];
    NSDate *dateCurrent = [NSDate date];
    NSTimeInterval dateCurrentInterval = [dateCurrent timeIntervalSince1970];
    
    NSCalendar *calendarCurrent = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSInteger unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSWeekdayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit;
    NSDateComponents *compsCurrent = [calendarCurrent components:unitFlags fromDate:dateCurrent];
    
    NSArray *dateToArray = [userSetting.emailDNDEnd componentsSeparatedByString:@":"];
    NSInteger dateToHour = [(NSString*)[dateToArray objectAtIndex:0] integerValue];
    NSInteger dateToMinute = [(NSString*)[dateToArray objectAtIndex:1] integerValue];
    NSDateComponents *compsTo = [[NSDateComponents alloc] init];
    [compsTo setYear:compsCurrent.year];
    [compsTo setMonth:compsCurrent.month];
    [compsTo setDay:compsCurrent.day];
    [compsTo setHour:dateToHour];
    [compsTo setMinute:dateToMinute];
    NSCalendar *calendarTo = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDate *dateTo = [calendarTo dateFromComponents:compsTo];
    NSTimeInterval dateToInterval = [dateTo timeIntervalSince1970];
    
    NSArray *dateFromArray = [userSetting.emailDNDStart componentsSeparatedByString:@":"];
    NSInteger dateFromHour = [(NSString*)[dateFromArray objectAtIndex:0] integerValue];
    NSInteger dateFromMinute = [(NSString*)[dateFromArray objectAtIndex:1] integerValue];
    NSDateComponents *compsFrom = [[NSDateComponents alloc] init];
    [compsFrom setYear:compsCurrent.year];
    [compsFrom setMonth:compsCurrent.month];
    [compsFrom setDay:compsCurrent.day];
    [compsFrom setHour:dateFromHour];
    [compsFrom setMinute:dateFromMinute];
    NSCalendar *calendarFrom = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDate *dateFrom = [calendarFrom dateFromComponents:compsFrom];
    NSTimeInterval dateFromInterval = [dateFrom timeIntervalSince1970];
    if (dateFromHour > dateToHour) {
        dateFromInterval = dateFromInterval - 60*60*24;
    }
    
    if ((dateCurrentInterval > dateFromInterval) && (dateCurrentInterval < dateToInterval)) {
        return YES;
    }
    return NO;
}

+ (void)noticification {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if (userSetting.emailDND.boolValue && [CommonFunction betweenDisturbTime]) {
        return;
    }
    if (userSetting.emailVibration.boolValue) {
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
    }
    if (userSetting.emailSounds.boolValue) {
        AudioServicesPlaySystemSound(1002);
    }
    if (userSetting.emailNotification.boolValue) {
        UILocalNotification *notification = [[UILocalNotification alloc]init];
        if (notification) {
            NSDate *ucrrentDate = [NSDate date];
            notification.timeZone = [NSTimeZone defaultTimeZone];
            notification.fireDate = [ucrrentDate dateByAddingTimeInterval:2.0];
            notification.repeatInterval = 0;
            notification.alertBody = NSLocalizedString(@"MailNotidicationTitle", nil);
            notification.alertAction = @"OneBox";
            notification.soundName = UILocalNotificationDefaultSoundName;
            
            [[UIApplication sharedApplication] scheduleLocalNotification:notification];
        }
    }
}

+ (UIImage*)imageCompressWithImage:(UIImage *)sourceImage targetSize:(CGSize)targetSize {
    CGSize sourceSize = sourceImage.size;
    CGFloat scaledFactor = 0;
    CGFloat scaledWidth = targetSize.width;
    CGFloat scaledHeight = targetSize.height;
    CGPoint targetPoint = CGPointMake(0, 0);
    if (CGSizeEqualToSize(sourceSize, targetSize) == NO) {
        CGFloat widthFactor = targetSize.width/sourceSize.width;
        CGFloat heightFactor = targetSize.height/sourceSize.height;
        scaledFactor = MAX(widthFactor, heightFactor);
        scaledWidth = sourceSize.width*scaledFactor;
        scaledHeight = sourceSize.height*scaledFactor;
        if (widthFactor>heightFactor) {
            targetPoint.y = (targetSize.height - scaledHeight)/2;
        } else {
            targetPoint.x = (targetSize.width - scaledWidth)/2;
        }
    }
    UIGraphicsBeginImageContextWithOptions(targetSize, NO, 3.0);
    CGRect targetRect = CGRectZero;
    targetRect.origin = targetPoint;
    targetRect.size.width = scaledWidth;
    targetRect.size.height = scaledHeight;
    [sourceImage drawInRect:targetRect];
    UIImage *compressImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return compressImage;
}

+ (void)imageCompressFromPath:(NSString *)orignalDataPath toPath:(NSString *)destinationDataPath {
    if (!orignalDataPath || ![[NSFileManager defaultManager] fileExistsAtPath:orignalDataPath]) {
        return;
    }
    
    UIImage *imageSmall;
    UIImage *imageOrginal = [UIImage imageWithData:[NSData dataWithContentsOfFile:orignalDataPath options:NSDataReadingMappedIfSafe error:nil]];
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
        NSString *path = [orignalDataPath stringByExpandingTildeInPath];
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
    if (destinationDataPath && [[NSFileManager defaultManager]fileExistsAtPath:destinationDataPath]) {
        [[NSFileManager defaultManager] removeItemAtPath:destinationDataPath error:nil];
    }
    NSData* imageData = UIImageJPEGRepresentation(imageSmall, 0.1);
    [[NSFileManager defaultManager] createFileAtPath:destinationDataPath contents:imageData attributes:nil];
}

+ (UILabel*)labelWithFrame:(CGRect)frame textFont:(UIFont *)textFont textColor:(UIColor *)textColor textAlignment:(NSTextAlignment)textAlignment {
    UILabel *label = [[UILabel alloc] initWithFrame:frame];
    label.font = textFont;
    label.textColor = textColor;
    label.textAlignment = textAlignment;
    return label;
}

+ (UIView*)tableViewHeaderWithTitle:(NSString *)title {
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 22)];
    headerView.backgroundColor = [CommonFunction colorWithString:@"f5f5f5" alpha:1.0f];
    UILabel *headerLabel = [CommonFunction labelWithFrame:CGRectMake(15, 0, CGRectGetWidth(headerView.frame)-15-15, CGRectGetHeight(headerView.frame)) textFont:[UIFont boldSystemFontOfSize:12.0f] textColor:[CommonFunction colorWithString:@"666666" alpha:1.0f] textAlignment:NSTextAlignmentLeft];
    headerLabel.text = title;
    [headerView addSubview:headerLabel];
    return headerView;
}

+ (BOOL)checkMailAccountForm:(NSString*)emailAccount {
    NSString *emailRegex = @"[A-Z0-9a-z]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,4}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",emailRegex];
    return [emailTest evaluateWithObject:emailAccount];
}

+ (BOOL) isImageResource:(NSString*)resourceName {
    NSString* extension = [[resourceName pathExtension] lowercaseString];
    if ([extension isEqual:@"jpeg"]||[extension isEqual:@"jpg"]||
        [extension isEqual:@"png"]||[extension isEqual:@"bmp"]||
        [extension isEqual:@"gif"]||[extension isEqual:@"tiff"]||
        [extension isEqual:@"raw"]||[extension isEqual:@"ppm"]||
        [extension isEqual:@"pgm"]||[extension isEqual:@"pbm"]||
        [extension isEqual:@"pnm"]||[extension isEqual:@"webp"]) {
        return YES;
    } else {
        return NO;
    }
}

+ (UIImage*) thumbnailWithFileName:(NSString*)name {
    if (!name) {
        return [UIImage imageNamed:@"ic_att_default"];
    }
    NSString* extension = [[name pathExtension] lowercaseString];
    if ([extension isEqual:@""]){
        return [UIImage imageNamed:@"ic_att_default"];
    } else if ([extension isEqual:@"doc"] || [extension isEqual:@"docx"]){
        return [UIImage imageNamed:@"ic_att_word"];
    } else if ([extension isEqual:@"xls"] || [extension isEqual:@"xlsx"]){
        return [UIImage imageNamed:@"ic_att_excel"];
    } else if ([extension isEqual:@"ppt"] || [extension isEqual:@"pptx"]){
        return [UIImage imageNamed:@"ic_att_ppt"];
    } else if ([extension isEqual:@"pdf"]){
        return [UIImage imageNamed:@"ic_att_pdf"];
    } else if ([extension isEqual:@"jpeg"]){
        return [UIImage imageNamed:@"ic_att_png"];
    } else if ([extension isEqual:@"jpg"]){
        return [UIImage imageNamed:@"ic_att_png"];
    } else if ([extension isEqual:@"png"]){
        return [UIImage imageNamed:@"ic_att_png"];
    } else if ([extension isEqual:@"bmp"]){
        return [UIImage imageNamed:@"ic_att_png"];
    } else if ([extension isEqual:@"avi"]){
        return [UIImage imageNamed:@"ic_att_video"];
    } else if ([extension isEqual:@"flv"]){
        return [UIImage imageNamed:@"ic_att_video"];
    } else if ([extension isEqual:@"rmvb"]){
        return [UIImage imageNamed:@"ic_att_video"];
    } else if ([extension isEqual:@"mp4"]){
        return [UIImage imageNamed:@"ic_att_video"];
    } else if ([extension isEqual:@"mov"]){
        return [UIImage imageNamed:@"ic_att_video"];
    } else if ([extension isEqual:@"zip"]){
        return [UIImage imageNamed:@"ic_att_rar"];
    } else if ([extension isEqual:@"gzip"]){
        return [UIImage imageNamed:@"ic_att_rar"];
    } else if ([extension isEqual:@"rar"]){
        return [UIImage imageNamed:@"ic_att_rar"];
    } else if ([extension isEqual:@"tar"]){
        return [UIImage imageNamed:@"ic_att_rar"];
    } else if ([extension isEqual:@"txt"]){
        return [UIImage imageNamed:@"ic_att_txt"];
    } else if ([extension isEqual:@"mp3"]){
        return [UIImage imageNamed:@"ic_att_mp3"];
    } else if ([extension isEqual:@"wav"]){
        return [UIImage imageNamed:@"ic_att_mp3"];
    } else {
        return [UIImage imageNamed:@"ic_att_default"];
    }
}

+ (NSString*)stringFromArray:(NSArray *)stringArray {
    if (!stringArray || stringArray.count == 0) {
        return nil;
    }
    
    NSArray *sortArray = [stringArray sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        NSString *string1 = obj1;
        NSString *string2 = obj2;
        return [string1 compare:string2];
    }];
    
    NSMutableString *result = [[NSMutableString alloc] init];
    for (NSString *string in sortArray) {
        [result appendFormat:@"%@,",string];
    }
    
    [result deleteCharactersInRange:NSMakeRange(result.length-1, 1)];
    return result;
}

+ (NSString *)getLocalizedString:(NSString *)key Comment:(NSString *)comment {
    UserSetting *userSetting = [UserSetting defaultSetting];
    if ([userSetting.cloudLanguage isEqualToString:@"system"]) {
        return NSLocalizedString(key, comment);
    }
    else{
        NSString *loc = userSetting.cloudLanguage;
        NSString *path = [[NSBundle mainBundle] pathForResource:@"Localizable" ofType:@"strings" inDirectory:nil forLocalization:loc];
        NSDictionary *dict = [NSDictionary dictionaryWithContentsOfFile:path];
        return [dict objectForKey:key];
    }
}



@end
