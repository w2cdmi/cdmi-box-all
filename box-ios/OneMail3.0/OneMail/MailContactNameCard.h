//
//  MailContactNameCard.h
//  OneMail
//
//  Created by cse  on 15/11/3.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MailContactNameCard : UIView

@property (nonatomic, strong) NSString *cardName;

- (id)initWithName:(NSString*)userName;

@end
