//
//  MailContactSearchMoreCell.m
//  OneMail
//
//  Created by cse  on 15/11/3.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#define CellContactMoreHeight 20
#define CellContactMoreDistanceTop 12

#import "MailContactSearchMoreCell.h"

@implementation MailContactSearchMoreCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        self.contactMore = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.contactMore setTitle:NSLocalizedString(@"ContactMore", nil) forState:UIControlStateNormal];
        [self.contactMore setTitleColor:[UIColor colorWithRed:0 green:139/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
        self.contactMore.titleLabel.font = [UIFont systemFontOfSize:15];
        self.contactMore.titleLabel.textAlignment = NSTextAlignmentCenter;
        [self.contentView addSubview:self.contactMore];
    }
    return self;
}

- (void)layoutSubviews{
    self.contactMore.frame = CGRectMake(0, 0, CGRectGetWidth(self.frame), CGRectGetHeight(self.frame));
}

@end
