//
//  AppDelegate.h
//  OneMail
//
//  Created by cse on 15/9/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import "LocalDataManager.h"
#import "RemoteDataManager.h"
#import "UserSetting.h"
#import "LeftSlideViewController.h"
#import "SNLog.h"
#import "UIView+Toast.h"
#import "AFNetworkReachabilityManager.h"
#import "CommonFunction.h"

#import "TransportUploadOperation.h"
#import "TransportDownloadOperation.h"
#import "AssetBackUpOperation.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) UIView *mainTabBar;
@property (strong, nonatomic) UINavigationController *navigationController;
@property (strong, nonatomic) LocalDataManager *localManager;
@property (strong, nonatomic) RemoteDataManager *remoteManager;
@property (strong, nonatomic) LeftSlideViewController *LeftSlideVC;
@property (strong, nonatomic) dispatch_queue_t upload_queue;
@property (strong, nonatomic) dispatch_queue_t download_queue;
@property (strong, nonatomic) TransportUploadOperation *uploadOperation;
@property (strong, nonatomic) TransportDownloadOperation *downloadOperation;
@property (strong, nonatomic) AssetBackUpOperation *backUpAssetOperation;
@property (strong, nonatomic) AFNetworkReachabilityManager * networkReachability;
@property (nonatomic, assign) BOOL startMonitor;
@property (assign) BOOL uploadHeadImage;
@property (assign) BOOL leftViewOpened;
@property (assign) BOOL cloudLoginSuccess;
@property (assign) NSInteger transferTaskCount;

-(BOOL) hasNetwork;
-(BOOL) wifiNetwork;
-(AFNetworkReachabilityStatus) currentReachabilityStatus;

@end

