//
//  MailUtils.h
//  StoryClient
//
//  Created by mengxianzhi on 15-1-9.
//  Copyright (c) 2015年 LiuQi. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#define kFontSize 14


@interface STMailUtils : NSObject

/**
 *  单例
 *
 *  @return self
 */
+ (instancetype)shareInstance;

/**
 *  获取文字高度
 *
 *  @param text     当前要显示的文字
 *  @param maxWidth 设置初始默认最大宽度
 *
 *  @return 字体宽度
 */
+ (CGFloat)widthForText:(NSString *)text maxWidth:(CGFloat)maxWidth fontOfSize:(CGFloat)fontOfSize;
/**
 *  获取文字高度
 *
 *  @param text     当前要文字
 *  @param maxWidth 默认最大宽度
 *
 *  @return 文字高度
 */
+ (CGFloat)heightForText:(NSString *)text maxHigh:(CGFloat)maxHigh fontOfSize:(CGFloat)fontOfSize;

+ (CGSize)boundingRectWithSize:(CGSize)size stringStr:(NSString *)stringStr fondSize:(CGFloat)fontOfSize;

//拼接mail邮件

+ (NSMutableString *)getMailHead;//1
+ (NSMutableString *)getSendMan:(NSString *)title mailSub:(NSString *)mailSub;//2
+ (NSMutableString *)getSendTime:(NSString *)timeStr;//3
+ (NSMutableString *)getToMan:(NSString *)title mailNo:(NSString *)mailNo isHaveSendName:(BOOL)flg;//4
+ (NSMutableString *)getCCMan:(NSString *)title mailNo:(NSString *)mailNo isHaveSendMan:(BOOL)flg;//5
+ (NSMutableString *)getMailTheme:(NSString *)theme;//6
+ (NSMutableString *)getHtml;//7

@end
