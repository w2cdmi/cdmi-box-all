//
//  CloudCustomActivity.m
//  OneMail
//
//  Created by hua on 17/3/20.
//  Copyright © 2017年 cse. All rights reserved.
//

#import "CloudCustomActivity.h"


NSString *const UIActivityTypeCloudZSCustomMine = @"CloudCustomActivityMine";
@implementation CloudCustomActivity

- (NSString *)activityType {
    return UIActivityTypeCloudZSCustomMine;
}

- (NSString *)activityTitle {

    return NSLocalizedString(@"Cloud Custom", @"");
}

- (UIImage *)activityImage {
    return [UIImage imageNamed:@"Icon-120"];
}

+ (UIActivityCategory)activityCategory {
    return UIActivityCategoryShare;
}

- (BOOL)canPerformWithActivityItems:(NSArray *)activityItems {
    NSLog(@"activityItems = %@",activityItems);
    return YES;
}


- (void)prepareWithActivityItems:(NSArray *)activityItems
{
    NSLog(@"Activity prepare");
}

- (void)performActivity
{
    NSLog(@"Activity run");
}

- (void)activityDidFinish:(BOOL)completed
{
    NSLog(@"Activity finish");
}


@end
