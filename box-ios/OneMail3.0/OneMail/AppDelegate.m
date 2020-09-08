//
//  AppDelegate.m
//  OneMail
//
//  Created by cse on 15/9/22.
//  Copyright (c) 2015年 cse. All rights reserved.
//
#import "AppDelegate.h"
#import "CloudLoadingViewController.h"
#import "CloudLoginViewController.h"
#import "MessageCacheOperation.h"
#import "CloudTransferViewController.h"
#import <AudioToolbox/AudioToolbox.h>
#import <AVFoundation/AVFoundation.h>
#import "UIAlertView+Blocks.h"
#import "TransportTask.h"
#import <Bugly/Bugly.h>

#import <ShareSDK/ShareSDK.h>
#import <ShareSDKConnector/ShareSDKConnector.h>
#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/QQApiInterface.h>
//微信sdk
#import "WXApi.h"

static dispatch_queue_t upload_queue() {
    static dispatch_queue_t ob_upload_queue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        ob_upload_queue = dispatch_queue_create("com.onebox.upload", DISPATCH_QUEUE_CONCURRENT);
    });
    return ob_upload_queue;
}

static dispatch_queue_t download_queue() {
    static dispatch_queue_t ob_download_queue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        ob_download_queue = dispatch_queue_create("com.onebox.download", DISPATCH_QUEUE_CONCURRENT);
    });
    return ob_download_queue;
}

static SystemSoundID sound_id = 0;

@interface AppDelegate ()
@property (nonatomic, strong) NSTimer *backgroundTimer;
@property (nonatomic, assign) UIBackgroundTaskIdentifier backgroundTaskIdentifier;
@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
//    float systemVersion = [[UIDevice currentDevice] systemVersion].floatValue;
//    if (systemVersion >= 8.0) {
//        UIUserNotificationType type = UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert;
//        UIUserNotificationSettings *setting = [UIUserNotificationSettings settingsForTypes:type categories:nil];
//        [[UIApplication sharedApplication] registerUserNotificationSettings:setting];
//    }
    self.window.backgroundColor = [UIColor whiteColor];
    
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    
    if (!self.localManager) {
        self.localManager = [[LocalDataManager alloc] init];
    }
    if (!self.remoteManager) {
        self.remoteManager = [[RemoteDataManager alloc] init];
    }
    
    self.transferTaskCount = 0;

    
    [MessageCacheOperation cleanExpiredMessages];
    
    self.networkReachability = [AFNetworkReachabilityManager sharedManager];
    
    [self.networkReachability startMonitoring];
    [self performSelector:@selector(initApp) withObject:nil afterDelay:0.01];
    
   
    return YES;
}

- (void)initApp
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
//    self.window.backgroundColor = [CommonFunction colorWithString:@"f0f0f0" alpha:1.0f];
    UIViewController *indexViewController;
    UserSetting * userSetting = [UserSetting defaultSetting];
    if (![userSetting.isFirstLogin boolValue]) {
        indexViewController = [[CloudLoadingViewController alloc] initWithNibName:nil bundle:nil];
        userSetting.isFirstLogin = [NSNumber numberWithBool:YES];
    } else {
        indexViewController = [[CloudLoginViewController alloc] initWithNibName:nil bundle:nil];
    }
    self.navigationController = [[UINavigationController alloc] initWithRootViewController:indexViewController];
    self.navigationController.navigationBarHidden = YES;
    self.window.rootViewController = self.navigationController;
    [self.window makeKeyAndVisible];
    
    self.leftViewOpened = NO;
    self.cloudLoginSuccess = NO;
    
    self.upload_queue = upload_queue();
    self.download_queue = download_queue();
    
    NSString *path = [[NSBundle mainBundle] pathForResource:@"nosound" ofType:@"wav"];
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)[NSURL URLWithString:path], &sound_id);
    
    [self.networkReachability addObserver:self forKeyPath:@"networkReachabilityStatus" options:NSKeyValueObservingOptionNew context:nil];
    
    [Bugly startWithAppId:@"900054200"];

    //应用外分享
    [self Share];
}

- (void)Share {
    //分享
    [ShareSDK registerApp:@"1d130e17c8fe4"
     
          activePlatforms:@[
                            @(SSDKPlatformTypeMail),
                            @(SSDKPlatformTypeSMS),
                            @(SSDKPlatformTypeCopy),
                            @(SSDKPlatformTypeWechat),
                            //                            @(SSDKPlatformTypeQQ)
                            ]
                 onImport:^(SSDKPlatformType platformType)
     {
         switch (platformType)
         {
             case SSDKPlatformTypeWechat:
                 [ShareSDKConnector connectWeChat:[WXApi class]];
                 break;
                 //             case SSDKPlatformTypeQQ:
                 //                 [ShareSDKConnector connectQQ:[QQApiInterface class] tencentOAuthClass:[TencentOAuth class]];
                 //                 break;
             default:
                 break;
         }
     }
          onConfiguration:^(SSDKPlatformType platformType, NSMutableDictionary *appInfo)
     {
         
         switch (platformType)
         {
             case SSDKPlatformTypeWechat:
                 [appInfo SSDKSetupWeChatByAppId:@"wx92be6216ef854b15"
                                       appSecret:@"1482e955f7fe6b68339d799de12b6ff4"];
                 break;
                 //             case SSDKPlatformTypeQQ:
                 //                 [appInfo SSDKSetupQQByAppId:@"1105841824"
                 //                                      appKey:@"qSLfEaDX8mjSmjk0"
                 //                                    authType:SSDKAuthTypeBoth];
                 //                 break;
             default:
                 break;
         }
     }];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    [SNLog Log:LInfo :@"App resign active,save data"];
    [self.localManager.managedObjectContext save:nil];
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];
    self.backgroundTaskIdentifier = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:nil];
    //self.backgroundTimer = [NSTimer scheduledTimerWithTimeInterval:10 target:self selector:@selector(tlk) userInfo:nil repeats:YES];
    //[self.backgroundTimer fire];
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}
- (void)tlk {
    NSLog(@"%d",(NSInteger)[[UIApplication sharedApplication] backgroundTimeRemaining]);
    if ([[UIApplication sharedApplication] backgroundTimeRemaining] < 10) {
        AudioServicesPlaySystemSound(sound_id);
    }
}
- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    [self.backgroundTimer invalidate];
    [MessageCacheOperation cleanExpiredMessages];
    if ([UserSetting defaultSetting].cloudAssetBackupOpen.boolValue) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Onebox.backup.autoStart" object:nil];
    }
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    // Saves changes in the application's managed object context before the application terminates.
    [SNLog Log:LInfo :@"App terminate,save data"];
    [CloudTransferViewController pauseAllTransferTask];
    [self.localManager.managedObjectContext save:nil];
    [self.networkReachability stopMonitoring];
   
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    [[UIApplication sharedApplication] cancelAllLocalNotifications];
}

- (AFNetworkReachabilityStatus)currentReachabilityStatus {
    return self.networkReachability.networkReachabilityStatus;
}

- (BOOL) hasNetwork {
    if (self.networkReachability.networkReachabilityStatus!=AFNetworkReachabilityStatusNotReachable) {
        return YES;
    }
    return NO;
}

- (BOOL) wifiNetwork {
    if (self.networkReachability.networkReachabilityStatus==AFNetworkReachabilityStatusReachableViaWiFi) {
        return YES;
    }
    return NO;
}
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context{
    
    if (object == self.networkReachability && [keyPath isEqualToString:@"networkReachabilityStatus"]) {
        if (self.startMonitor == NO) {
            return;
        }
        if (self.networkReachability.networkReachabilityStatus == AFNetworkReachabilityStatusNotReachable) {
            [CloudTransferViewController waitNetworkAllTransferTask];
        }
        if (self.networkReachability.networkReachabilityStatus == AFNetworkReachabilityStatusReachableViaWiFi) {
            [CloudTransferViewController waitAllTransferTask];
            [self.uploadOperation startOperation];
            [self.downloadOperation startOperation];
        }
        if (self.networkReachability.networkReachabilityStatus == AFNetworkReachabilityStatusReachableViaWWAN) {
            NSInteger waitNetWorkTasksCount = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitNetwork) ctx:nil].count;
            NSInteger initialingTasksCount = [TransportTask getAllTaskWithTaskStatus:@(TaskInitialing) ctx:nil].count;
            NSInteger transferingTasksCount = [TransportTask getAllTaskWithTaskStatus:@(TaskRunning) ctx:nil].count;
            NSInteger waitingTasksCount = [TransportTask getAllTaskWithTaskStatus:@(TaskWaitting) ctx:nil].count;
            if (waitNetWorkTasksCount + initialingTasksCount + transferingTasksCount + waitingTasksCount == 0) {
                return;
            }
            if ([UserSetting defaultSetting].cloudWiFiPrompt.boolValue) {
                [UIAlertView showAlertViewWithTitle:nil message:@"当前为非Wi-Fi网络,是否启动所有传输任务?" cancelButtonTitle:@"Cancel" otherButtonTitles:@[@"Confirm"] onDismiss:^(int buttonIndex) {
                    [CloudTransferViewController waitAllTransferTask];
                    [self.uploadOperation startOperation];
                    [self.downloadOperation startOperation];
                } onCancel:^{
                    [CloudTransferViewController waitNetworkAllTransferTask];
                }];
            } else {
                [CloudTransferViewController waitAllTransferTask];
                [self.uploadOperation startOperation];
                [self.downloadOperation startOperation];
            }
        }
    }
}
@end
