//
//  StringAES.h
//  Onebox
//
//  Created by CSE on 14-7-20.
//
//

#import <Foundation/Foundation.h>

@interface StringAES : NSObject

#pragma mark-method
-(id)init;
-(void)dealloc;

+(StringAES *)sharedInstance;
+(NSString *)stringtoAES:(NSString *)originalString;
+(NSString *)AEStoString:(NSString *)AESString;

@end
