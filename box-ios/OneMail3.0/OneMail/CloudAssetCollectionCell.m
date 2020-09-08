//
//  CloudAssetCollectionCell.m
//  OneMail
//
//  Created by cse  on 15/11/15.
//  Copyright (c) 2015年 cse. All rights reserved.
//

#import "CloudAssetCollectionCell.h"

@interface CloudAssetCollectionCell ()

@property (nonatomic, strong) UIImageView *assetImageView;
@property (nonatomic, strong) UIImageView *assetSelectView;

@end

@implementation CloudAssetCollectionCell

- (void)setAsset:(ALAsset *)asset {
    if (_asset != asset) {
        _asset = asset;
    }
    if (!self.assetImageView) {
        self.assetImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.frame),CGRectGetHeight(self.frame))];
        [self.contentView addSubview:self.assetImageView];
    }
    self.assetImageView.image = [UIImage imageWithCGImage:self.asset.thumbnail];
}

- (void)setAssetSelect:(BOOL)assetSelect {
    if (_assetSelect != assetSelect) {
        _assetSelect = assetSelect;
    }
    if (!self.assetSelectView) {
        self.assetSelectView = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetWidth(self.assetImageView.frame)-22-4, 4, 15, 15)];
        self.assetSelectView.backgroundColor = [UIColor clearColor];
        [self.assetImageView addSubview:self.assetSelectView];
    }
    if (assetSelect) {
        self.assetSelectView.image = [UIImage imageNamed:@"ic_checkbox_on_nor"];
    } else {
        self.assetSelectView.image = [UIImage imageNamed:@"ic_choice"];
    }
}

- (void)reuse {
    self.assetImageView.image = nil;
    self.assetSelectView.image = nil;
}
@end
