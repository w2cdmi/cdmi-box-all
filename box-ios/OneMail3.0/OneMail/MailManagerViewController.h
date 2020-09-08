//
//  MailManagerViewController.h
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

@class User;
@class Session;

@interface MailManagerUserCell : UICollectionViewCell

@property (nonatomic, strong) NSString *userEmail;

//@property (nonatomic, assign) id <MailContactUserDelegate> delegate;

- (void)reuse;

@end

@interface MailManagerViewController : UIViewController

@property (nonatomic, strong) NSMutableArray *sessionUserArray;

- (id)initWithSession:(Session*)session;

@end
