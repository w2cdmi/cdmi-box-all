//
//  MemberShip.h
//  OneMail
//
//  Created by admin on 17/1/10.
//  Copyright © 2017年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MemberShip : NSObject
@property (nonatomic, strong) NSString *teamId;
@property (nonatomic, strong) NSString *ShipId;
@property (nonatomic, strong) NSString *userId;
@property (nonatomic, strong) NSString *role;
@property (nonatomic, strong) NSString *teamRole;
@property (nonatomic, strong) NSString *userName;
+ (NSArray *)getShipsFromInfo:(NSDictionary *)shipsInfo;
+ (NSString *)pretyName:(NSString *)role;
@end
