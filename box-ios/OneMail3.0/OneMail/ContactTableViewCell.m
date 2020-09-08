//
//  ContactTableViewCell.m
//  OneMail
//
//  Created by cse  on 15/12/10.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "ContactTableViewCell.h"
#import "AppDelegate.h"
#import "User.h"

@interface ContactTableViewCell ()

@property (nonatomic, strong) UIImageView *contactUserIconBackground;
@property (nonatomic, strong) UIImageView *contactUserIcon;
@property (nonatomic, strong) UILabel *contactNameLabel;
@property (nonatomic, strong) UILabel *contactEmailLabel;
@property (nonatomic, strong) UIButton *contactUserAddButton;

@end

@implementation ContactTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];
    if (self) {
        self.contactUserIconBackground = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"img_contact_frame"]];
        [self.contentView addSubview:self.contactUserIconBackground];
        
        self.contactUserIcon = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self.contactUserIconBackground addSubview:self.contactUserIcon];
        
        self.contactNameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.contactNameLabel.font = [UIFont systemFontOfSize:15.0f];
        self.contactNameLabel.textColor = [CommonFunction colorWithString:@"000000" alpha:1.0f];
        self.contactNameLabel.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.contactNameLabel];
        
        self.contactEmailLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.contactEmailLabel.font = [UIFont systemFontOfSize:12.0f];
        self.contactEmailLabel.textColor = [CommonFunction colorWithString:@"666666" alpha:1.0f];
        self.contactEmailLabel.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.contactEmailLabel];
        
        self.contactUserAddButton = [[UIButton alloc] initWithFrame:CGRectZero];
        self.contactUserAddButton.layer.borderWidth = 0.5;
        self.contactUserAddButton.layer.borderColor = [CommonFunction colorWithString:@"d9d9d9" alpha:1.0f].CGColor;
        self.contactUserAddButton.layer.cornerRadius = 4;
        self.contactUserAddButton.layer.masksToBounds = YES;
        [self.contactUserAddButton addTarget:self action:@selector(contactUserAdd) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:self.contactUserAddButton];
        
        UILabel *label = [[UILabel alloc] init];
        label.font = [UIFont systemFontOfSize:14.0f];
        label.text = NSLocalizedString(@"ContactAddButton", nil);
        CGSize adjustAddSize = [CommonFunction labelSizeWithLabel:label limitSize:CGSizeMake(1000,1000)];
        label.text = NSLocalizedString(@"ContactAddedTitle", nil);
        CGSize adjustAddedSize = [CommonFunction labelSizeWithLabel:label limitSize:CGSizeMake(1000,1000)];
        self.contactUserAddButton.bounds = CGRectMake(0, 0, MAX(adjustAddSize.width, adjustAddedSize.width), 22);
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.contactUserIconBackground.frame = CGRectMake(15, 5.5, 44, 44);
    self.contactUserIconBackground.layer.cornerRadius = 22;
    self.contactUserIconBackground.layer.masksToBounds = YES;
    
    NSString *userHeadIconPath = [self.user userHeadIconPath];
    if (userHeadIconPath && [[NSFileManager defaultManager] fileExistsAtPath:userHeadIconPath]) {
        self.contactUserIcon.image = [UIImage imageWithContentsOfFile:userHeadIconPath];
    } else {
        self.contactUserIcon.image = [UIImage imageNamed:@"img_contact_default"];
    }
    self.contactUserIcon.frame = CGRectMake(2,2,40,40);
    self.contactUserIcon.layer.cornerRadius = 20;
    self.contactUserIcon.layer.masksToBounds = YES;
    self.contactUserIcon.layer.borderWidth = 0.0f;
    self.contactUserIcon.layer.borderColor = [UIColor clearColor].CGColor;
    
    self.contactNameLabel.text = [NSString stringWithFormat:@"%@(%@)",self.user.userName,self.user.userLoginName];
    self.contactNameLabel.frame = CGRectMake(CGRectGetMaxX(self.contactUserIconBackground.frame)+10, 8, CGRectGetWidth(self.frame)-CGRectGetMaxX(self.contactUserIconBackground.frame)-10-15, 20);
    
    self.contactEmailLabel.text = self.user.userEmail;
    self.contactEmailLabel.frame = CGRectMake(CGRectGetMinX(self.contactNameLabel.frame), CGRectGetMaxY(self.contactNameLabel.frame)+4, CGRectGetWidth(self.contactNameLabel.frame), 15);
    
    self.contactUserAddButton.frame = CGRectMake(CGRectGetWidth(self.frame)-15-CGRectGetWidth(self.contactUserAddButton.frame), (CGRectGetHeight(self.frame)-CGRectGetHeight(self.contactUserAddButton.frame))/2, CGRectGetWidth(self.contactUserAddButton.frame), CGRectGetHeight(self.contactUserAddButton.frame));
    if (self.searchingState) {
        self.contactUserAddButton.hidden = NO;
        if (self.user.userMyContactFlag.boolValue) {
            [self.contactUserAddButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"ContactAddedTitle", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"000000" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
        } else {
            [self.contactUserAddButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"ContactAddButton", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"000000" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
        }
    } else {
        self.contactUserAddButton.hidden = YES;
    }
}

- (void)contactUserAdd {
    if (self.user.userMyContactFlag.boolValue) {
        return;
    }
    [self.contactUserAddButton setAttributedTitle:[[NSAttributedString alloc] initWithString:NSLocalizedString(@"ContactAddedTitle", nil) attributes:@{NSForegroundColorAttributeName:[CommonFunction colorWithString:@"000000" alpha:1.0f],NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}] forState:UIControlStateNormal];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *ctx = appDelegate.localManager.backgroundObjectContext;
    [ctx performBlockAndWait:^{
        User *shadow = (User*)[ctx objectWithID:self.user.objectID];
        shadow.userMyContactFlag = @(1);
        [ctx save:nil];
    }];
}

- (void)prepareForReuse {
    self.contactUserIcon.image = nil;
    self.contactNameLabel.text = nil;
    self.contactEmailLabel.text = nil;
}

@end
