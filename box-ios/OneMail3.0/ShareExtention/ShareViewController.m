//
//  ShareViewController.m
//  ShareExtention
//
//  Created by hua on 17/3/20.
//  Copyright © 2017年 cse. All rights reserved.
//

#import "ShareViewController.h"
#import "CloudCustomActivity.h"
#import <MobileCoreServices/MobileCoreServices.h>
//#import <AssetsLibrary/AssetsLibrary.h>
//#import "File+Remote.h"
//#import "UIAlertView+Blocks.h"
//#import "UserSetting.h"

@interface ShareViewController ()

@property (nonatomic, strong) UIImage *attachedImage;


@property  SLComposeSheetConfigurationItem * item;
@property (strong, nonatomic) NSArray * array;
@property (strong, nonatomic) NSMutableArray * thingsToAdd;

//@property (nonatomic, strong) ALAssetsLibrary *library;

@end

static NSInteger const maxCharactersAllowed = 66;
static NSString *uploadURL = @"http://requestb.in/1di89fa1";


@implementation ShareViewController

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSLog(@"view will appear ani");
}

-(void)viewDidLoad {
    [super viewDidLoad];
    NSLog(@"view did load");
}

- (BOOL)isContentValid {
    // Do validation of contentText and/or NSExtensionContext attachments here
    NSInteger maxLength = maxCharactersAllowed;
    NSInteger length = self.contentText.length;
    self.charactersRemaining = @(maxLength - length);
    if (self.charactersRemaining.integerValue > 0) {
        return YES;
    }else {
        return NO;
    }
    
}

- (void)didSelectCancel {
    NSLog(@"点击取消");
    
    NSError *error =[[NSError alloc] init];
    
    [self.extensionContext cancelRequestWithError:error];
}


- (void)didSelectPost {
    // This is called after the user selects Post. Do the upload of contentText and/or NSExtensionContext attachments.
    
    // Inform the host that we're done, so it un-blocks its UI. Note: Alternatively you could call super's -didSelectPost, which will similarly complete the extension context.
    
//    NSUserDefaults *mySharedDefaults = [[NSUserDefaults alloc] initWithSuiteName:@"group.CSIBox"];
    
    //stores the user text and the selected option in an NSDictionary
//    NSDictionary * objectToAdd = [[NSDictionary alloc]initWithObjectsAndKeys:self.contentText, @"INFO", self.item.value, @"WHICH_OPTION", nil];
//    
    
   // examples of using the share items
//    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    
//    NSExtensionItem * exItem = self.extensionContext.inputItems[0];
//    NSItemProvider * itemProvider = exItem.attachments[0];
//    
//    __block BOOL hasExtistUrl = NO;
//    [self.extensionContext.inputItems enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
//        [exItem.attachments enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
//            //***URL***
//            if ([itemProvider hasItemConformingToTypeIdentifier:(NSString *)kUTTypeURL]) {
//                [itemProvider loadItemForTypeIdentifier:(NSString *)kUTTypeURL options:nil completionHandler:^(id<NSSecureCoding>  _Nullable item, NSError * _Null_unspecified error) {
//                    if ([(NSObject *)item isKindOfClass:[NSURL class]]) {
//                        NSLog(@"分享的URL = %@",item);
//                    }
//                }];
//                hasExtistUrl = YES;
//                *stop = YES;
//            }else if ([itemProvider hasItemConformingToTypeIdentifier:(NSString *)kUTTypeImage]) {
//                //***Image***
//                [itemProvider loadItemForTypeIdentifier:(NSString *)kUTTypeImage options:nil completionHandler: ^(NSData * data, NSError *error) {
//                    
//                    UIImage * image = [UIImage imageWithData:data];
//                    NSLog(@"image:%@,%@", image,self.contentText);
                    //Do Something Interesting with the Image:
//                    __strong typeof(self) strong = self;
//                    [self addAssetImage:image album:@"CSIBox" completion:^(ALAsset *asset) {
                        //__strong typeof(weak) strong = weak;
//                        if (asset) {
//                            [strong doUploadingWithAsset:asset];
//                            dispatch_async(dispatch_get_main_queue(), ^{
//                                NSLog(@"上传成功");
                    
//                                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudUploadAddSuccessPrompt", nil)];
                                
//                            });
//                        } else {
//                            dispatch_async(dispatch_get_main_queue(), ^{
//                                NSLog(@"上传失败");
//                                [[UIApplication sharedApplication].keyWindow makeToast:getLocalizedString(@"CloudUploadAddFailedPrompt", nil)];
//                            });
//                        }
//                    }];

                    
//
//                }];
//            }
//            
//        }];
//        if (hasExtistUrl) {
//            *stop = YES;
//        }
//    }];
//    
}

//- (void)addAssetImage:(UIImage*)image album:(NSString*)albumName completion:(void(^)(ALAsset *asset))completion {
//    __block ALAssetsGroup *album = nil;
//    __block BOOL albumExist = NO;
//    @autoreleasepool {
//        [self.library enumerateGroupsWithTypes:ALAssetsGroupAlbum usingBlock:^(ALAssetsGroup *group, BOOL *stop) {
//            if (group == nil) {
//                if (!albumExist) {
//                    [self.library addAssetsGroupAlbumWithName:albumName resultBlock:^(ALAssetsGroup *group) {
//                        album = group;
//                    } failureBlock:^(NSError *error) {
//                        completion(nil);
//                    }];
//                }
//            } else {
//                NSString *groupName = [group valueForProperty:ALAssetsGroupPropertyName];
//                if ([albumName isEqualToString:groupName]) {
//                    albumExist = YES; album = group; stop = false;
//                }
//            }
//        } failureBlock:^(NSError *error) {
//            completion(nil);
//        }];
//        
//        [self.library writeImageToSavedPhotosAlbum:image.CGImage orientation:(ALAssetOrientation)image.imageOrientation completionBlock:^(NSURL *assetURL, NSError *error) {
//            if (!error) {
//                [self.library assetForURL:assetURL resultBlock:^(ALAsset *asset) {
//                    [album addAsset:asset];
//                    completion(asset);
//                } failureBlock:^(NSError *error) {
//                    completion(nil);
//                }];
//            } else {
//                completion(nil);
//            }
//        }];
//    }
//}


//- (void)doUploadingWithAsset:(ALAsset*)asset {
////    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
////    BOOL WiFiNetWork = appDelegate.wifiNetwork;
//    File *file = [File getFileWithFileId:self.uploadTargetFolderId fileOwner:self.uploadTargetFolderOwner];
//    UserSetting *userSetting = [UserSetting defaultSetting];
//    if (userSetting.cloudAssetBackupWifi.integerValue == 1) {
//        [UIAlertView showAlertViewWithTitle:nil message:NSLocalizedString(@"CloudUploadWIFIPrompt", nil) cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:@[NSLocalizedString(@"Confirm", nil)] onDismiss:^(int buttonIndex) {
//            [file uploadAsset:asset force:YES];
//        } onCancel:^{
//            [file uploadAsset:asset force:NO];
//        }];
//    } else {
//        [file uploadAsset:asset force:YES];
//    }
//}




- (NSArray *)configurationItems {
    // To add configuration options via table cells at the bottom of the sheet, return an array of SLComposeSheetConfigurationItem here.
    return @[];
}

@end
