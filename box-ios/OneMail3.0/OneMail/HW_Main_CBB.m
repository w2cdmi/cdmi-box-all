 //
//  HW_Main_CBB.m
//  OneMail
//
//  Created by CSE on 15/10/28.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "HW_Main_CBB.h"
#import "AppDelegate.h"

@implementation HW_Main_CBB

-(id) init
{
    self = [super init];
    if (self) {
        self.pszKeyStoreFile = [NSMutableArray array];
        self.pszKmcCfgFile = [NSMutableArray array];
    }
    return self;

}

WSEC_VOID WSECWriteLog(int nLever, const char* pszMoudleName, const char* pszOccurFileName, int nOccurLine, const char* pszLog)
{
    //printf("[%s]%s Line-%d, Lever-%d: %s", pszMoudleName, pszOccurFileName, nOccurLine, nLever, pszLog);
}

WSEC_VOID WSECReleaseCPU()
{
    
}

WSEC_VOID WSECFPNotify(WSEC_NTF_CODE_ENUM eNtfCode, const WSEC_VOID* pData, WSEC_SIZE_T bDataSize)
{
    
}

WSEC_VOID WSECFPRptProgress(WSEC_UINT32 ulTag, WSEC_UINT32 ulScale, WSEC_UINT32 ulCurrent, WSEC_BOOL* pbCancel)
{
    
}

-(NSInteger) cbbGetPlainLength: (WSEC_UINT32) encryptLen
{
    WSEC_UINT32 plainLen = 0;
    if (WSEC_SUCCESS != SDP_GetCipherDataLen(encryptLen, &plainLen)) {
        NSLog(@"========>SDP_GetCipherDataLen failure \n");
        return 0;
    }
    return (NSInteger)plainLen;
}

-(NSString*) cbbEncrypt: (NSString*) strEncrypt
{
    KMC_FILE_NAME_STRU kmcFileNameStru;
    WSEC_ERR_T rcRet = 0;
    WSEC_UINT32 encryptLen = 0, plainLen = 0;
    WSEC_BYTE *tmpPlain = 0;
    
    plainLen = encryptLen = (WSEC_UINT32)strEncrypt.length;

    memset_s(&kmcFileNameStru, sizeof(kmcFileNameStru), 0 ,sizeof(kmcFileNameStru));
    
    //初始化文件
    NSURL* docURL = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
    NSString *CBBDirectory = [docURL.path stringByAppendingPathComponent:@"CBB"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:CBBDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:CBBDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }

    kmcFileNameStru.pszKeyStoreFile[0] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/keystore.txt", CBBDirectory] UTF8String];
    kmcFileNameStru.pszKeyStoreFile[1] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/keystorebackup.txt", CBBDirectory] UTF8String];
    kmcFileNameStru.pszKmcCfgFile[0] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/configcbb.txt", CBBDirectory] UTF8String];
    kmcFileNameStru.pszKmcCfgFile[1] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/configcbbbackup.txt", CBBDirectory] UTF8String];
        
    [self.pszKeyStoreFile addObject:[NSString stringWithFormat:@"%@/keystore.txt", CBBDirectory]];
    [self.pszKeyStoreFile addObject:[NSString stringWithFormat:@"%@/keystorebackup.txt", CBBDirectory]];
    [self.pszKmcCfgFile addObject:[NSString stringWithFormat:@"%@/configcbb.txt", CBBDirectory]];
    [self.pszKmcCfgFile addObject:[NSString stringWithFormat:@"%@/configcbbbackup.txt", CBBDirectory]];
    
    
    //初始化回调函数
    WSEC_FP_CALLBACK_STRU wsecCallBack;
    memset_s(&wsecCallBack, sizeof(wsecCallBack), 0, sizeof(wsecCallBack));
    wsecCallBack.stRelyApp.pfWriLog = WSECWriteLog;
    wsecCallBack.stRelyApp.pfDoEvents = WSECReleaseCPU;
    wsecCallBack.stRelyApp.pfNotify = WSECFPNotify;
    
    WSEC_PROGRESS_RPT_STRU wsecProgressRptStru;
    wsecProgressRptStru.ulTag = 1;
    wsecProgressRptStru.pfRptProgress = WSECFPRptProgress;
    
    if (WSEC_SUCCESS != (rcRet = WSEC_Initialize(&kmcFileNameStru, &wsecCallBack, &wsecProgressRptStru, NULL))) {
        NSLog(@"========>Encrypt Initialize failure \n");
        if (WSEC_SUCCESS != WSEC_Finalize()) {
            NSLog(@"========>Encrypt WSEC_Finalize failure \n");
            return nil;
        }
        return nil;
    }
    
    if (WSEC_SUCCESS != SDP_GetCipherDataLen(encryptLen, &plainLen)) {
        NSLog(@"========>SDP_GetCipherDataLen failure \n");
        return nil;
    }
    
    tmpPlain = (WSEC_BYTE*)alloca(plainLen);
    
    if (WSEC_SUCCESS != SDP_Encrypt(0, (WSEC_BYTE*)[strEncrypt UTF8String], encryptLen, tmpPlain, &plainLen))
    {
        NSLog(@"========>SDP_Encrypt failure \n");
        tmpPlain = NULL;
        if (WSEC_SUCCESS != WSEC_Finalize()) {
            NSLog(@"========>Encrypt WSEC_Finalize failure \n");
        }
        return nil;
    }
    
    NSMutableString *strPlain = [[NSMutableString alloc] init];
    
    for (NSInteger i = 0; i < plainLen; i++) {
        [strPlain appendFormat:@"%x ",tmpPlain[i]];
    }
    
//    free(tmpPlain);
    
    if (WSEC_SUCCESS != WSEC_Finalize()) {
        NSLog(@"========>Encrypt WSEC_Finalize failure \n");
        return nil;
    }
    return strPlain;
}

-(NSString*) cbbDecrypt: (NSString*) strDecrypt
{
    KMC_FILE_NAME_STRU kmcFileNameStru;
    WSEC_ERR_T rcRet = 0;
    WSEC_UINT32 decryptLen = 0, plainLen = 0;
    WSEC_BYTE *tmpPlain = 0;
    
    unsigned char *decryptStr = 0;
    int tmpDecrypt;
    
    if (nil == strDecrypt) {
        return nil;
    }
    
    NSArray *decryptArr = [strDecrypt componentsSeparatedByString:@" "];
    
    decryptStr = (unsigned char *)alloca(decryptArr.count);
    
    for (NSInteger i = 0; i < decryptArr.count - 1; i++) {
        sscanf([[decryptArr objectAtIndex:i] UTF8String], "%x", &tmpDecrypt);
        decryptStr[i] = tmpDecrypt;
    }
    
    plainLen = decryptLen = (WSEC_UINT32)decryptArr.count - 1;

    memset_s(&kmcFileNameStru, sizeof(kmcFileNameStru), 0 ,sizeof(kmcFileNameStru));
    //初始化文件
    NSURL* docURL = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
    NSString *CBBDirectory = [docURL.path stringByAppendingPathComponent:@"CBB"];
    BOOL isDirectory;
    if (![[NSFileManager defaultManager] fileExistsAtPath:CBBDirectory isDirectory:&isDirectory] || !isDirectory) {
        [[NSFileManager defaultManager] createDirectoryAtPath:CBBDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    }

    kmcFileNameStru.pszKeyStoreFile[0] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/keystore.txt", CBBDirectory] UTF8String];
    kmcFileNameStru.pszKeyStoreFile[1] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/keystorebackup.txt", CBBDirectory] UTF8String];
    kmcFileNameStru.pszKmcCfgFile[0] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/configcbb.txt", CBBDirectory] UTF8String];
    kmcFileNameStru.pszKmcCfgFile[1] = (WSEC_CHAR*)[[NSString stringWithFormat:@"%@/configcbbbackup.txt", CBBDirectory] UTF8String];
    
    
    WSEC_FP_CALLBACK_STRU wsecCallBack;
    memset_s(&wsecCallBack, sizeof(wsecCallBack), 0, sizeof(wsecCallBack));
    wsecCallBack.stRelyApp.pfWriLog = WSECWriteLog;
    wsecCallBack.stRelyApp.pfDoEvents = WSECReleaseCPU;
    wsecCallBack.stRelyApp.pfNotify = WSECFPNotify;
    
    WSEC_PROGRESS_RPT_STRU wsecProgressRptStru;
    wsecProgressRptStru.ulTag = 1;
    wsecProgressRptStru.pfRptProgress = WSECFPRptProgress;
    
    if (WSEC_SUCCESS != (rcRet = WSEC_Initialize(&kmcFileNameStru, &wsecCallBack, &wsecProgressRptStru, NULL))) {
        printf("========>Decrypt Initialize failure \n");
        if (WSEC_SUCCESS != WSEC_Finalize()) {
            printf("========>Decrypt WSEC_Finalize failure \n");
        }
        return nil;
    }
    
    tmpPlain = (WSEC_BYTE*)alloca(plainLen);
    
    if (WSEC_SUCCESS != SDP_Decrypt(0, (WSEC_BYTE*)decryptStr, decryptLen, tmpPlain, &plainLen))
    {
        printf("========>SDP_Decrypt failure \n");
        tmpPlain = NULL;
        if (WSEC_SUCCESS != WSEC_Finalize()) {
            printf("========>Decrypt WSEC_Finalize failure \n");
        }
        return nil;
    }
    
    NSMutableString *strPlain = [NSMutableString stringWithUTF8String:(const char*)tmpPlain];
    
    //free(tmpPlain);
    
    if (WSEC_SUCCESS != WSEC_Finalize()) {
        printf("========>Decrypt WSEC_Finalize failure \n");
        return nil;
    }
    return strPlain;
}

@end
