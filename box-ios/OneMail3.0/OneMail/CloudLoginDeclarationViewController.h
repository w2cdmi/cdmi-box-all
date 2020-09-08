//
//  CloudLoginDeclarationViewController.h
//  OneMail
//
//  Created by cse  on 15/11/25.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^DeclarationConfirmBlock)();

@interface CloudLoginDeclarationViewController : UIViewController

@property (nonatomic, copy) DeclarationConfirmBlock confirmBlock;
@property (nonatomic, strong) NSString *cloudLoginDeclarationString;
@property (nonatomic, strong) NSString *cloudLoginDeclarationId;

@end
