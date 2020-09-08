//
//  CloudAssetCollectionCell.h
//  OneMail
//
//  Created by cse  on 15/11/15.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>

@interface CloudAssetCollectionCell : UICollectionViewCell

@property (nonatomic, strong) ALAsset *asset;
@property (nonatomic, assign) BOOL assetSelect;

- (void)reuse;

@end
