//
//  IrregularView.h
//  IrregularImages
//
//  Created by OranWu on 13-4-10.
//  Copyright (c) 2013年 Oran Wu. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

typedef void (^IrregularViewGestureAction)(UIGestureRecognizer *gesture);


@interface IrregularView : UIView
- (id)initWithFrame:(CGRect)frame UIBezierPath:(UIBezierPath*)path;
- (void)setMaskWithPath:(UIBezierPath*)path;
- (void)setMaskWithPath:(UIBezierPath*)path withBorderColor:(UIColor*)borderColor borderWidth:(float)borderWidth;
- (BOOL)containsPoint:(CGPoint)point;
- (void)onGesture:(IrregularViewGestureAction)gestureAction;
@end
