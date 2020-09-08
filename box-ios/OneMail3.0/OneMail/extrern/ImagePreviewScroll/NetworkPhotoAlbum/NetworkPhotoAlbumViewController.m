//
// Copyright 2011-2014 Jeff Verkoeyen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

#import "NetworkPhotoAlbumViewController.h"

#import "NIOverviewMemoryCacheController.h"
#import "NimbusOverview.h"
#import "NIOverviewView.h"
#import "NIOverviewPageView.h"
#import "AFNetworking.h"
#import  <ImageIO/ImageIO.h>
#ifdef DEBUG
//@interface NetworkPhotoAlbumViewController()
//@property (nonatomic, retain) NIOverviewMemoryCachePageView* highQualityPage;
//@end
#endif

@implementation NetworkPhotoAlbumViewController

#ifdef DEBUG
#endif


- (void)shutdown_NetworkPhotoAlbumViewController {


#ifdef DEBUG
 // [[NIOverview view] removePageView:self.highQualityPage];
#endif
}

- (void)dealloc {
    [_highQualityImageCache removeAllObjects];
    _highQualityImageCache=nil;
    [self shutdown_NetworkPhotoAlbumViewController];
}

- (NSString *)cacheKeyForPhotoIndex:(NSInteger)photoIndex {
  return [NSString stringWithFormat:@"%zd", photoIndex];
}

- (NSInteger)identifierWithPhotoSize:(NIPhotoScrollViewPhotoSize)photoSize
                          photoIndex:(NSInteger)photoIndex {
  BOOL isThumbnail = (NIPhotoScrollViewPhotoSizeThumbnail == photoSize);
  NSInteger identifier = isThumbnail ? -(photoIndex + 1) : photoIndex;
  return identifier;
}

- (id)identifierKeyFromIdentifier:(NSInteger)identifier {
  return [NSNumber numberWithInteger:identifier];
}

- (void)requestImageFromSource:(NSString *)source
                     photoSize:(NIPhotoScrollViewPhotoSize)photoSize
                    photoIndex:(NSInteger)photoIndex
                         async:(BOOL)isSyanc{
 
  BOOL isThumbnail = (NIPhotoScrollViewPhotoSizeThumbnail == photoSize);
  NSInteger identifier = [self identifierWithPhotoSize:photoSize photoIndex:photoIndex];
  id identifierKey = [self identifierKeyFromIdentifier:identifier];

  // Avoid duplicating requests.
  if ([_activeRequests containsObject:identifierKey]) {
    return;
  }
//__block   UIImage *image=nil;
    __block __weak typeof(_image) image=_image ;
    
  NSString* photoIndexKey = [self cacheKeyForPhotoIndex:photoIndex];

    if (isThumbnail) {

        
           image= [UIImage imageWithContentsOfFile:source];
            [_highQualityImageCache storeObject: image
                                   withName: photoIndexKey];


    } else {
        if (isSyanc) {
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
               // @autoreleasepool {
                __strong typeof(_image) myimage = image;
                if (![_highQualityImageCache containsObjectWithName:photoIndexKey]) {
                    // NSData *data=UIImageJPEGRepresentation([UIImage imageWithContentsOfFile:source], 0.0);
                    //                    UIImagePNGRepresentation([UIImage imageWithContentsOfFile:source]);
                    //                        myimage= [UIImage imageWithData:data];
                    //   data=nil;
                    // myimage= [UIImage imageWithData:[NSData dataWithContentsOfFile:source] scale:0.5];
                    
                    
                    // myimage=[UIImage imageWithCGImage:[self thumbnailForFile:source]];
                    myimage= [UIImage imageWithContentsOfFile:source];
                    [_highQualityImageCache storeObject: myimage
                                               withName: photoIndexKey];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.photoAlbumView didLoadPhoto: myimage
                                                  atIndex: photoIndex
                                                photoSize: photoSize];
                        
                    });
                }
                // }
                
                
            });
        }
        
        else{
            
            if (![_highQualityImageCache containsObjectWithName:photoIndexKey]) {
                //  image=[UIImage imageWithCGImage:[self thumbnailForFile:source]];
                UIImage *image =[UIImage imageWithContentsOfFile:source];
//                image= [UIImage imageWithContentsOfFile:source];
                [_highQualityImageCache storeObject: image
                                           withName: photoIndexKey];
                
                // }
                
            }
            
            
        }
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.photoAlbumView didLoadPhoto: image
                                  atIndex: photoIndex
                                photoSize: photoSize];
    });
    
    
}




#pragma mark - UIViewController


- (void)loadView {
    [super loadView];
    
    _activeRequests = [[NSMutableSet alloc] init];
    
    _highQualityImageCache = [[NIImageMemoryCache alloc] init];
    
    [_highQualityImageCache setMaxNumberOfPixels:1024L*1024L*5L];
    [_highQualityImageCache setMaxNumberOfPixelsUnderStress:1024L*1024L*0.2L];
    
    self.photoAlbumView.loadingImage = [UIImage imageWithContentsOfFile:
                                        NIPathForBundleResource(nil, @"NimbusPhotos.bundle/gfx/default.png")];
    
#ifdef DEBUG
    //  self.highQualityPage = [NIOverviewMemoryCachePageView pageWithCache:self.highQualityImageCache];
    //  [[NIOverview view] addPageView:self.highQualityPage];
#endif
}

- (void)viewDidUnload {
    [self shutdown_NetworkPhotoAlbumViewController];
    
    [super viewDidUnload];
}


@end
