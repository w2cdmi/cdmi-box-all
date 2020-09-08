//
//  UIAlertView+Blocks.m
//  UIKitCategoryAdditions
//

#import "UIAlertView+Blocks.h"

static DismissBlock _dismissBlock;
static CancelBlock _cancelBlock;

@implementation UIAlertView (Blocks)

+ (UIAlertView*) showAlertViewWithTitle:(NSString*) title                    
                                message:(NSString*) message 
                      cancelButtonTitle:(NSString*) cancelButtonTitle
                      otherButtonTitles:(NSArray*) otherButtons
                              onDismiss:(DismissBlock) dismissed                   
                               onCancel:(CancelBlock) cancelled {
  
  _cancelBlock  = [cancelled copy];
  
  _dismissBlock  = [dismissed copy];
  
  UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title
                                                  message:message
                                                 delegate:[self self]
                                        cancelButtonTitle:cancelButtonTitle
                                        otherButtonTitles:nil];
  
  for(NSString *buttonTitle in otherButtons)
    [alert addButtonWithTitle:buttonTitle];
  
  [alert show];
  return alert;
}

+ (void)alertView:(UIAlertView*) alertView didDismissWithButtonIndex:(NSInteger) buttonIndex {
	if(buttonIndex == [alertView cancelButtonIndex]) {
		_cancelBlock();
	}else{
    _dismissBlock(buttonIndex - 1); // cancel button is button 0
  }  
}

+ (void)InternetFailureAlertTitle:(NSString *) titile message:(NSString*) message{
    UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:titile
                                                         message:message
                                                        delegate:nil cancelButtonTitle:nil otherButtonTitles:nil, nil];
    [alertView show];
    [UIAlertView performBlock:^{
        [alertView dismissWithClickedButtonIndex:0 animated:YES];
    } afterDelay:1.0];
}
+ (void)performBlock:(void(^)())block afterDelay:(NSTimeInterval)delay {
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), block);
}

@end