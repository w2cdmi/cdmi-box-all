//
//  MemberShip.m
//  OneMail
//
//  Created by admin on 17/1/10.
//  Copyright © 2017年 cse. All rights reserved.
//

#import "MemberShip.h"

@implementation MemberShip
+ (NSArray *)getShipsFromInfo:(NSDictionary *)shipsInfo{
    NSMutableArray *ships = [[NSMutableArray alloc] init];
    NSArray *memberships = [shipsInfo objectForKey:@"memberships"];
    for (NSDictionary *shipInfo in memberships) {
        MemberShip *ship = [[MemberShip alloc] init];
        ship.teamId = [shipInfo objectForKey:@"teamId"];
        ship.role = [shipInfo objectForKey:@"role"];
        ship.teamRole = [shipInfo objectForKey:@"teamRole"];
        ship.ShipId = [shipInfo objectForKey:@"id"];
        NSDictionary *userInfo = [shipInfo objectForKey:@"member"];
        ship.userId = [userInfo objectForKey:@"id"];
        ship.userName = [userInfo objectForKey:@"name"];
        [ships addObject:ship];
    }
    return ships;
}
+ (NSString *)pretyName:(NSString *)role{
    if ([role isEqualToString:@"editor"]) {
        return @"编辑者";
    }
    if ([role isEqualToString:@"auther"]) {
        return @"管理者";
    }
    if([role isEqualToString:@"uploadAndView"]){
         return @"可上传可查看";
    }
    if([role isEqualToString:@"uploader"]){
        return @"上传者";
    }
    if([role isEqualToString:@"viewer"]){
        return @"查看者";
    }
    if([role isEqualToString:@"previewer"]){
        return @"预览者";
    }
    return nil;
}
@end
