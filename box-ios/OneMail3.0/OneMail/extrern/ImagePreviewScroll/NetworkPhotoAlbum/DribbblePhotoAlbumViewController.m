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


#import "AppDelegate.h"
#import "AFNetworking.h"
#import "UserSetting.h"
#import "DribbblePhotoAlbumViewController.h"
#import <ImageIO/ImageIO.h>
#import "File.h"
#import "Version.h"

@interface DribbblePhotoAlbumViewController ()


@end

@implementation DribbblePhotoAlbumViewController
- (id)initWithFile:(File *)object fileSearch:(NSString *)searchText fileTransport:(NSArray *)transportArrays{
    self = [self initWithNibName:nil bundle:nil];
    if (self) {
        if ([searchText isEqualToString:@""]) {
            contasinName = nil;
        } else {
            contasinName = searchText;
        }
        transportArray=(NSMutableArray*)transportArrays;
        self.previewFile=object;
    }
    return self;
}

- (BOOL)loadThumbnails {
    UserSetting *userSetting = [UserSetting defaultSetting];
    pathArray=[[NSMutableArray alloc]init];
    fileArray=[[NSMutableArray alloc]init];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *bgctx = appDelegate.localManager.managedObjectContext;
    //点选目标为传输列表文件
    if (transportArray) {
        [self getUseablePath:transportArray];
    }else{
        NSPredicate *predicate = nil;
        if (contasinName) {
            //点选目标为搜索列表文件
            predicate = [NSPredicate predicateWithFormat:@"fileOwner=%@ AND fileType=%@ AND name contains[cd] %@",appDelegate.localManager.userCloudId,@(TypeImage),contasinName];
        }else{
            //点选目标为文件夹文件
            predicate = [NSPredicate predicateWithFormat:@"fileOwner=%@ AND fileParent=%@ AND fileType=%@ ", self.previewFile.fileOwner,self.previewFile.fileParent,@(TypeImage)];
        }
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"File" inManagedObjectContext:bgctx];
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:userSetting.cloudSortType ascending:YES];
        NSSortDescriptor *nameDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"fileModifiedDate" ascending:NO];
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        [fetchRequest setEntity:entity];
        [fetchRequest setPredicate:predicate];
        [fetchRequest setSortDescriptors:[NSArray arrayWithObjects:sortDescriptor,nameDescriptor, nil]];
        [fetchRequest setFetchBatchSize:20];
        
        NSError *error;
        NSArray *array = [bgctx executeFetchRequest:fetchRequest error:&error];
        if (!array) {
            [SNLog Log:LInfo :@"There is not image can show"];
        }
        [self getUseablePath:array];
    }
    
    if ([pathArray count]<currentPathIndex+1) {
        [SNLog Log:LInfo :@"currentIndex exceed the count"];
        return NO;
    }
    
    NSString* path = [pathArray objectAtIndex:currentPathIndex];
    NSString* photoIndexKey = [self cacheKeyForPhotoIndex:currentPathIndex];
    
    if (![self.highQualityImageCache containsObjectWithName:photoIndexKey]) {
        [self requestImageFromSource: path photoSize: NIPhotoScrollViewPhotoSizeOriginal photoIndex: currentPathIndex async:NO];
    }
    return  [self.highQualityImageCache containsObjectWithName:photoIndexKey]?YES:NO;
}

-(void)getUseablePath:(NSArray*)array {
    currentPathIndex = 0;
    for (File *file in array) {
        NSString *fileCompressImagePath = [file fileCompressImagePath];
        if ([[NSFileManager defaultManager] fileExistsAtPath:fileCompressImagePath]) {
            [pathArray addObject:fileCompressImagePath];
            [fileArray addObject:file];
            if ([self.previewFile.fileId isEqualToString:file.fileId]) {
                currentPathIndex = (int)fileArray.count - 1;
            }
        }
    }
}

- (BOOL)loadAlbumInformation {
    NSString *fileCompressImagePath = [self.previewFile fileCompressImagePath];
    if (!fileCompressImagePath || ![[NSFileManager defaultManager]fileExistsAtPath:fileCompressImagePath]) {
        [SNLog Log:LInfo :@"There is no small image file with %@",self.previewFile.fileName];
        [self.previewFile fileCompressImage];
    }
    if ([self loadThumbnails]) {
        [self.photoAlbumView reloadData:currentPathIndex];
        [self.photoScrubberView reloadData];
        [self refreshChromeState];
        
        return YES;
    } else {
        return NO;
    }
}


- (id)initWithVersion:(Version *)version {
    self = [self initWithNibName:nil bundle:nil];
    if (self) {
        self.previewVersion = version;
    }
    return self;
}

- (BOOL)loadVersionThumbnails {
    pathArray=[[NSMutableArray alloc]init];
    versionArray=[[NSMutableArray alloc]init];
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *bgctx = appDelegate.localManager.managedObjectContext;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"versionOwner=%@ AND versionFileId=%@ ", self.previewVersion.versionOwner,self.previewVersion.versionFileId];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Version" inManagedObjectContext:bgctx];
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"versionModifiedDate" ascending:NO];
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    [fetchRequest setEntity:entity];
    [fetchRequest setPredicate:predicate];
    [fetchRequest setSortDescriptors:@[sortDescriptor]];
    [fetchRequest setFetchBatchSize:20];

    NSError *error;
    NSArray *array = [bgctx executeFetchRequest:fetchRequest error:&error];
    if (!array) {
        [SNLog Log:LInfo :@"There is not image can show"];
    }
    [self getUseableVersionPath:array];
    
    if ([pathArray count]<currentPathIndex+1) {
        [SNLog Log:LInfo :@"currentIndex exceed the count"];
        return NO;
    }
    
    NSString* path = [pathArray objectAtIndex:currentPathIndex];
    NSString* photoIndexKey = [self cacheKeyForPhotoIndex:currentPathIndex];
    
    if (![self.highQualityImageCache containsObjectWithName:photoIndexKey]) {
        [self requestImageFromSource: path photoSize: NIPhotoScrollViewPhotoSizeOriginal photoIndex: currentPathIndex async:NO];
    }
    return  [self.highQualityImageCache containsObjectWithName:photoIndexKey]?YES:NO;
}

-(void)getUseableVersionPath:(NSArray*)array {
    currentPathIndex = 0;
    for (Version *version in array) {
        NSString *versionCompressImagePath = [version versionCompressImagePath];
        if (versionCompressImagePath && [[NSFileManager defaultManager] fileExistsAtPath:versionCompressImagePath]) {
            [pathArray addObject:versionCompressImagePath];
            [versionArray addObject:version];
            if ([[self.previewVersion versionCompressImagePath] isEqualToString:versionCompressImagePath]) {
                currentPathIndex = (int)versionArray.count - 1;
                
            }
        }
    }
}

- (BOOL)loadVersionInformation {
    NSString *versionCompressImagePath = [self.previewVersion versionCompressImagePath];
    if (![[NSFileManager defaultManager]fileExistsAtPath:versionCompressImagePath]) {
        
    }
    if ([self loadVersionThumbnails]) {
        [self.photoAlbumView reloadData:currentPathIndex];
        [self.photoScrubberView reloadData];
        [self refreshChromeState];
        return YES;
    } else {
        return NO;
    }
}
#pragma mark - UIViewController


- (void)loadView {
    
    [super loadView];
    
    self.photoAlbumView.dataSource = self;
    
    // Dribbble is for mockups and designs, so we don't want to allow the photos to be zoomed
    // in and become blurry.
    self.photoAlbumView.zoomingAboveOriginalSizeIsEnabled = NO;
    
    // This title will be displayed until we get the results back for the album information.
    self.title = NSLocalizedString(@"Loading...", @"Navigation bar title - Loading a photo album");
    
}

- (void)viewDidUnload {
    pathArray=nil;
    fileArray=nil;
    [super viewDidUnload];
    
}

#pragma mark - NIPhotoScrubberViewDataSource


- (NSInteger)numberOfPhotosInScrubberView:(NIPhotoScrubberView *)photoScrubberView {
    return [pathArray count];
}



#pragma mark - NIPhotoAlbumScrollViewDataSource

-(void)refreshFile:(int)curentpageIndex
{
    self.previewFile=[fileArray objectAtIndex:curentpageIndex];
}
- (NSInteger)numberOfPagesInPagingScrollView:(NIPhotoAlbumScrollView *)photoScrollView {
    return [pathArray count];
}

- (UIImage *)photoAlbumScrollView: (NIPhotoAlbumScrollView *)photoAlbumScrollView
                     photoAtIndex: (NSInteger)photoIndex
                        photoSize: (NIPhotoScrollViewPhotoSize *)photoSize
                        isLoading: (BOOL *)isLoading
          originalPhotoDimensions: (CGSize *)originalPhotoDimensions {
    UIImage* image = nil;
    
    NSString* photoIndexKey = [self cacheKeyForPhotoIndex:photoIndex];
    
    NSString* path = [pathArray objectAtIndex:photoIndex];
    
    // Let the photo album view know how large the photo will be once it's fully loaded.
    //  *originalPhotoDimensions = [[photo defaultRepresentation] dimensions];
    
    image = [self.highQualityImageCache objectWithName:photoIndexKey];
    if (nil != image) {
        *photoSize = NIPhotoScrollViewPhotoSizeOriginal;
        
    } else {
        // NSString* source = [photo objectForKey:@"originalSource"];
        *photoSize = NIPhotoScrollViewPhotoSizeOriginal;
        [self requestImageFromSource: path
                           photoSize: NIPhotoScrollViewPhotoSizeOriginal
                          photoIndex: photoIndex
                               async:YES];
        
        *isLoading = YES;
        image = [self.highQualityImageCache objectWithName:photoIndexKey];
        if (image==nil) {
            
            [self requestImageFromSource: path
                               photoSize: NIPhotoScrollViewPhotoSizeOriginal
                              photoIndex: photoIndex
                                   async:NO];
            
            
        }
    }
    
    return image;
}
-(void)photoAlbumScrollViewDidLoadNextPhoto:(NIPhotoAlbumScrollView *)photoAlbumScrollView :(int)phtoIndex
{
    for (int i=1; i<3&&((i+phtoIndex)<[pathArray count]); i++) {
        NSString* path = [pathArray objectAtIndex:phtoIndex+i];
        [self requestImageFromSource: path
                           photoSize: NIPhotoScrollViewPhotoSizeOriginal
                          photoIndex: phtoIndex+i
                               async:YES];
    }
}

-(void)photoAlbumScrollViewDidLoadPreviousPhoto:(NIPhotoAlbumScrollView *)photoAlbumScrollView :(int)phtoIndex
{
    for (int i=1; i<3&&((phtoIndex-i)>=0); i++) {
        NSString* path = [pathArray objectAtIndex:phtoIndex-i];
        [self requestImageFromSource: path
                           photoSize: NIPhotoScrollViewPhotoSizeOriginal
                          photoIndex: phtoIndex-i
                               async:YES];
    }
    
}



- (void)photoAlbumScrollView: (NIPhotoAlbumScrollView *)photoAlbumScrollView
     stopLoadingPhotoAtIndex: (NSInteger)photoIndex {
    // TODO: Figure out how to implement this with AFNetworking.
}

- (id<NIPagingScrollViewPage>)pagingScrollView:(NIPagingScrollView *)pagingScrollView pageViewForIndex:(NSInteger)pageIndex {
    return [self.photoAlbumView pagingScrollView:pagingScrollView pageViewForIndex:pageIndex];
}

@end
