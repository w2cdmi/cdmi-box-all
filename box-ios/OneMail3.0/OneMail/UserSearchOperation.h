//
//  UserSearchOperation.h
//  OneMail
//
//  Created by cse  on 16/1/21.
//  Copyright (c) 2016年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UserSearchOperation : NSObject

- (void)userSearchResultOperation:(NSArray*)usersInfo completion:(void(^)())completion;

@end
