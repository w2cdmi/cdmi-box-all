#import "IrregularView.h"
@interface IrregularView (){
    UIBezierPath *bezierPath;
    IrregularViewGestureAction actionBlock;
}
@end

@implementation IrregularView
- (id)initWithFrame:(CGRect)frame UIBezierPath:(UIBezierPath*)path {
    self = [super initWithFrame:frame];
    if (self) {
        self.userInteractionEnabled = YES;
        bezierPath = path;
        [self setMaskWithPath:bezierPath];

    }
    return self;
}
- (void)setMaskWithPath:(UIBezierPath*)path {
    bezierPath = path;
    [self setMaskWithPath:path withBorderColor:nil borderWidth:0];
}
- (void)setMaskWithPath:(UIBezierPath*)path withBorderColor:(UIColor*)borderColor borderWidth:(float)borderWidth{
    CAShapeLayer *maskLayer = [CAShapeLayer layer];
    maskLayer.path = [path CGPath];
    maskLayer.fillColor = [[UIColor whiteColor] CGColor];
    maskLayer.frame = self.frame;
    self.layer.mask = maskLayer;
    
    if (borderColor && borderWidth>0) {
        CAShapeLayer *maskBorderLayer = [CAShapeLayer layer];
        maskBorderLayer.path = [path CGPath];
        maskBorderLayer.fillColor = [[UIColor clearColor] CGColor];
        maskBorderLayer.strokeColor = [borderColor CGColor];
        maskBorderLayer.lineWidth = borderWidth;
        [self.layer addSublayer:maskBorderLayer];
    }
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapGesture:)];
    [self addGestureRecognizer:tap];
}
- (BOOL)containsPoint:(CGPoint)point{
    return [bezierPath containsPoint:point];
}
- (void)onGesture:(IrregularViewGestureAction)gestureAction{
    actionBlock = [gestureAction copy];
}
- (void)tapGesture:(UITapGestureRecognizer*)tapGesture{
    //单纯显示点击Point-------------
    CGPoint tapPoint = [tapGesture locationInView:tapGesture.view];
    IrregularView *tapIndicator = [[IrregularView alloc] initWithFrame:CGRectMake(0, 0, 10, 10)];
    tapIndicator.backgroundColor = [UIColor blackColor];
    [self addSubview:tapIndicator];
    [tapIndicator setMaskWithPath:[UIBezierPath bezierPathWithRoundedRect:tapIndicator.frame cornerRadius:50]];
    tapIndicator.center = CGPointMake(tapPoint.x, tapPoint.y);
    [UIView animateWithDuration:0.5 animations:^{
        tapIndicator.alpha = 0;
    }completion:^(BOOL finished) {
        [tapIndicator removeFromSuperview];
    }];
    //单纯显示点击Point-------------可以删除
    if ([self containsPoint:[tapGesture locationInView:self]]){
        actionBlock(tapGesture);
    }
}
@end