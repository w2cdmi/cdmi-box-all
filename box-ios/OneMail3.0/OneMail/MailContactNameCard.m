//
//  MailContactNameCard.m
//  OneMail
//
//  Created by cse  on 15/11/3.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define NameCardHeight 36
#define NameCardLabelDistanceLeft 10
#define NameCardLabelDistanceRight 10
#define NameCardCornerRadius 4

#import "MailContactNameCard.h"

@implementation MailContactNameCard

- (id)initWithName:(NSString *)userName {
    self = [super initWithFrame:CGRectZero];
    if (self) {
        self.cardName = userName;
        
        UILabel *nameLable = [[UILabel alloc] initWithFrame:CGRectZero];
        nameLable.text = userName;
        nameLable.font = [UIFont systemFontOfSize:14];
        CGSize adjustSize = [self adjustSize:nameLable limitSize:CGSizeMake(1000, NameCardHeight)];
        self.frame = CGRectMake(0, 0, adjustSize.width+NameCardLabelDistanceLeft+NameCardLabelDistanceRight, NameCardHeight);
        UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.frame), CGRectGetHeight(self.frame))];
        button.layer.cornerRadius = NameCardCornerRadius;
        button.backgroundColor = [UIColor colorWithRed:245/255.f green:245/255.f blue:245/255.f alpha:1];
        [button setTitle:userName forState:UIControlStateNormal];
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        button.titleLabel.textAlignment = NSTextAlignmentCenter;
        button.titleLabel.font = [UIFont systemFontOfSize:14];
        [self addSubview:button];
    }
    return self;
}

- (CGSize)adjustSize:(UILabel*)lable limitSize:(CGSize)size
{
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    NSDictionary *attributes = @{NSFontAttributeName:lable.font,NSParagraphStyleAttributeName:paragraphStyle.copy};
    return [lable.text boundingRectWithSize:size options:NSStringDrawingUsesLineFragmentOrigin attributes:attributes context:nil].size;
}

@end
