//
//  SpaceUsersManageViewController.h
//  OneMail
//
//  Created by admin on 17/1/10.
//  Copyright © 2017年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MemberShip.h"
#import "TeamSpace.h"
#import "User.h"
typedef void (^userChangeBlock)(BOOL result);
@protocol SpaceUserChange <NSObject>
- (void)spaceAddUser:(User *)user completion:(userChangeBlock)block;
- (void)spaceDeleteUser:(MemberShip *)ship completion:(userChangeBlock)block;
@end
@interface SpaceUsersManageCell : UITableViewCell
@property (nonatomic, strong) MemberShip *ship;
@property (nonatomic, strong) User *user;
@property (nonatomic, assign) BOOL isChangeOwer;
@property (nonatomic, assign) id <SpaceUserChange> delegate;
@end
@interface SpaceUsersManageViewController : UIViewController
- (id)initWithSpace:(TeamSpace *)space;
@end
