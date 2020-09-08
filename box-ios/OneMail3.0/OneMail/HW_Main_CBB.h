//
//  HW_Main_CBB.h
//  OneMail
//
//  Created by CSE on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "wsec_cbb.h"

@interface HW_Main_CBB : NSObject;

@property (nonatomic, strong)NSMutableArray* pszKeyStoreFile;
@property (nonatomic, strong)NSMutableArray* pszKmcCfgFile;

-(NSInteger) cbbGetPlainLength: (WSEC_UINT32) encryptLen;

-(NSString*) cbbEncrypt: (NSString*) strEncrypt;

-(NSString*) cbbDecrypt: (NSString*) strDecrypt;

@end
