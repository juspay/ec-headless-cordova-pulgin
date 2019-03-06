//
//  ExpressCheckout.h
//  ec-hl-cordova-plugin
//
//

#import <Cordova/CDV.h>

@interface ExpressCheckout : CDVPlugin
- (void)JuspayStartPayment:(CDVInvokedUrlCommand*)command;
@end
