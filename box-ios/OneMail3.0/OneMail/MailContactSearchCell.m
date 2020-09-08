//
//  MailContactSearchCell.m
//  OneMail
//
//  Created by cse  on 15/11/3.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#define CellHeaderBackgroundViewWidth 44
#define CellHeaderBackgroundViewHieght 44
#define CellHeaderBackgroundViewDistanceLeft 15
#define CellHeaderBackgroundViewDistanceRight 10
#define CellHeaderBackgroundViewDistanceTop 5.5
#define CellHeaderBackgroundViewDistanceBottom 5.5
#define CellHeaderImageViewWidth 40
#define CellHeaderImageViewHeight 40

#define CellNameLabelHeight 20
#define CellNameLabelDistanceTop 8
#define CellNameLabelDistanceBottom 4
#define CellNameLabelDistanceLeft CellHeaderBackgroundViewDistanceRight
#define CellNameLabelDistanceRight 5
#define CellNameLabelFont [UIFont systemFontOfSize:15]

#define CellEmailLabelHeight 15
#define CellEmailLabelDistanceTop CellNameLabelDistanceBottom
#define CellEmailLabelDistanceBottom 8
#define CellEmailLabelDistanceLeft CellNameLabelDistanceLeft
#define CellEmailLabelDistanceRight CellNameLabelDistanceRight
#define CellEmailLabelFont [UIFont systemFontOfSize:12]

#define CellCheckBoxHeight 22
#define CellCheckBoxWidth 22
#define CellCheckBoxDistanceTop 16.5
#define CellCheckBoxDistanceBottom 16.5
#define CellCheckBoxDistanceLeft CellNameLabelDistanceRight
#define CellCheckBoxDistanceRight 15

#define AddButtonHeight 22
#define AddButtonWidth 50
#define AddButtonDistanceTop 16.5
#define AddButtonDistanceRight 15
#define AddButtonFont [UIFont systemFontOfSize:15]

#import "MailContactSearchCell.h"
#import "User.h"
#import "AppDelegate.h"
@interface MailContactSearchCell ()

@property (nonatomic, strong) UIImageView *contactHeaderBackground;
@property (nonatomic, strong) UIImageView *contactHeaderImageView;
@property (nonatomic, strong) UILabel *contactNameLabel;
@property (nonatomic, strong) UILabel *contactEmailLabel;
@property (nonatomic, strong) UIButton *contactCheckBox;
@property (nonatomic, strong) UIButton *contactAddButton;
@property (nonatomic, strong) UIView *contactLineView;
@end
@implementation MailContactSearchCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier]) {
        [self.imageView removeFromSuperview];
        [self.textLabel removeFromSuperview];
        [self.detailTextLabel removeFromSuperview];
        
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _contactSelected = NO;
        self.contactHeaderBackground = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.contactHeaderBackground.image = [UIImage imageNamed:@"img_user_frame"];
        [self.contentView addSubview:self.contactHeaderBackground];
        
        self.contactHeaderImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        self.contactHeaderImageView.image = [UIImage imageNamed:@"img_user_default"];
        [self.contactHeaderBackground addSubview:self.contactHeaderImageView];
        
        self.contactNameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.contactNameLabel.font = CellNameLabelFont;
        self.contactNameLabel.textColor = [UIColor blackColor];
        self.contactNameLabel.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:self.contactNameLabel];
        
        self.contactEmailLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        self.contactEmailLabel.font = CellEmailLabelFont;
        self.contactEmailLabel.textColor = [UIColor colorWithRed:102/255.f green:102/255.f blue:102/255.f alpha:1];
        [self.contentView addSubview:self.contactEmailLabel];
        
        self.contactCheckBox = [[UIButton alloc] initWithFrame:CGRectZero];
        self.contactCheckBox.hidden = YES;
        [self.contentView addSubview:self.contactCheckBox];
        [self.contactCheckBox addTarget:self action:@selector(contactSelectedClick) forControlEvents:UIControlEventTouchUpInside];
        
        self.contactAddButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [self.contactAddButton addTarget:self action:@selector(addMyUsers) forControlEvents:UIControlEventTouchUpInside];
        self.contactAddButton.titleLabel.font = AddButtonFont;
        self.contactAddButton.hidden = YES;
        [self addSubview:self.contactAddButton];
        
        self.contactLineView = [[UIView alloc] initWithFrame:CGRectZero];
        self.contactLineView.backgroundColor = [UIColor colorWithRed:217/255.f green:217/255.f blue:217/255.f alpha:1];
        [self addSubview:self.contactLineView];

    }
    return self;
}
- (void)addMyUsers{
    if (self.user.userMyContactFlag.integerValue == 1) {
        return;
    }
    else{
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *ctx = delegate.localManager.backgroundObjectContext;
        User *user = (User *)[ctx objectWithID:self.user.objectID];
        user.userMyContactFlag = @1;
        [ctx save:nil];
        self.user.userMyContactFlag = @1;
        [self.contactAddButton setTitle:NSLocalizedString(@"ContactAddedTitle", nil) forState:UIControlStateNormal];
        [self.contactAddButton setTitleColor:[UIColor colorWithRed:102/255.f green:102/255.f blue:102/255.f alpha:1] forState:UIControlStateNormal];
        
    }
}

- (void)setContactSelected:(BOOL)contactSelected {
    _contactSelected = contactSelected;
    self.contactAddButton.hidden = YES;
    self.contactCheckBox.hidden = NO;
}

- (void)contactSelectedClick {
    if (_contactSelected) {
        _contactSelected = NO;
        [_contactCheckBox setImage:[UIImage imageNamed:@"ic_checkbox_off_nor"] forState:UIControlStateNormal];
        [self.delegate deselectMailContactUser:self.user];
    } else {
        _contactSelected = YES;
        [_contactCheckBox setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
        [_delegate selectMailContactUser:self.user];
    }
}

- (void)setIsNeedAdd:(BOOL)isNeedAdd{
    _isNeedAdd = YES;
    if (self.user.userMyContactFlag.integerValue == 1) {
        [self.contactAddButton setTitle:NSLocalizedString(@"ContactAddedTitle", nil) forState:UIControlStateNormal];
        [self.contactAddButton setTitleColor:[UIColor colorWithRed:102/255.f green:102/255.f blue:102/255.f alpha:1] forState:UIControlStateNormal];
    }
    else{
        [self.contactAddButton setTitle:NSLocalizedString(@"ContactAddTitle", nil) forState:UIControlStateNormal];
        [self.contactAddButton setTitleColor:[UIColor colorWithRed:0 green:139/255.f blue:232/255.f alpha:1] forState:UIControlStateNormal];
    }
    self.contactAddButton.hidden = NO;
    self.contactCheckBox.hidden = YES;
}

- (void)setUser:(User *)user {
    if (_user != user) {
        _user = user;
        [self layoutIfNeeded];
    }
}


- (void)layoutSubviews{
    self.contactHeaderBackground.frame = CGRectMake(CellHeaderBackgroundViewDistanceLeft, CellHeaderBackgroundViewDistanceTop, CellHeaderBackgroundViewWidth, CellHeaderBackgroundViewHieght);
    self.contactHeaderImageView.frame = CGRectMake((CellHeaderBackgroundViewWidth-CellHeaderImageViewWidth)/2, (CellHeaderBackgroundViewHieght-CellHeaderImageViewHeight)/2, CellHeaderImageViewWidth, CellHeaderImageViewHeight);
    if (self.isNeedAdd == NO) {
        self.contactNameLabel.frame = CGRectMake(CGRectGetMaxX(self.contactHeaderBackground.frame) + CellHeaderBackgroundViewDistanceRight, CellNameLabelDistanceTop, self.frame.size.width - CellHeaderBackgroundViewWidth - CellHeaderBackgroundViewDistanceRight - CellHeaderBackgroundViewDistanceLeft - CellCheckBoxDistanceRight - CellCheckBoxWidth - CellNameLabelDistanceRight ,CellNameLabelHeight);
    }else{
         self.contactNameLabel.frame = CGRectMake(CGRectGetMaxX(self.contactHeaderBackground.frame) + CellHeaderBackgroundViewDistanceRight, CellNameLabelDistanceTop, self.frame.size.width - CellHeaderBackgroundViewWidth - CellHeaderBackgroundViewDistanceRight - CellHeaderBackgroundViewDistanceLeft - AddButtonDistanceRight - AddButtonWidth - CellNameLabelDistanceRight ,CellNameLabelHeight);
    }
    self.contactNameLabel.text = self.user.userName;
    
    self.contactEmailLabel.frame = CGRectMake(CGRectGetMaxX(self.contactHeaderBackground.frame)+CellEmailLabelDistanceLeft, CGRectGetMaxY(self.contactNameLabel.frame)+CellEmailLabelDistanceTop, CGRectGetWidth(self.contactNameLabel.frame), CellEmailLabelHeight);
    self.contactEmailLabel.text = self.user.userEmail;
    
    self.contactCheckBox.frame = CGRectMake(CGRectGetWidth(self.frame)-CellCheckBoxDistanceRight - CellCheckBoxWidth, CellCheckBoxDistanceTop, CellCheckBoxWidth, CellCheckBoxHeight);
    if (_contactSelected) {
        [self.contactCheckBox setImage:[UIImage imageNamed:@"ic_checkbox_on_nor"] forState:UIControlStateNormal];
    } else {
        [self.contactCheckBox setImage:[UIImage imageNamed:@"ic_checkbox_off_nor"] forState:UIControlStateNormal];
    }
    
    self.contactAddButton.frame = CGRectMake(self.frame.size.width - AddButtonDistanceRight - AddButtonWidth, AddButtonDistanceTop, AddButtonWidth, AddButtonHeight);
    self.contactLineView.frame = CGRectMake(0, self.frame.size.height - 0.5, self.frame.size.width, 0.5);
}
@end
