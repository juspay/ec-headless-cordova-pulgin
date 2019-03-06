//
//  ExpressCheckout.m
//  ec-hl-cordova-plugin
//
//

#import "ExpressCheckout.h"
#import <HyperSDK/HyperSDK.h>

@interface ExpressCheckout()

@property (nonatomic, strong) Hyper *hyper;

@end

@implementation ExpressCheckout

- (void)JuspayStartPayment:(CDVInvokedUrlCommand*)command {
    
    self.hyper = [[Hyper alloc] init];
    
    NSString* callbackId = command.callbackId;
    NSDictionary *arguments = [[command arguments] objectAtIndex:0];
    
    NSMutableDictionary *paymentParams = [[NSMutableDictionary alloc] init];
    
    [paymentParams setDictionary:[arguments valueForKey:@"baseParams"]];
    
    NSDictionary *serviceParams = [arguments valueForKey:@"serviceParams"];
    for (NSString *key in serviceParams) {
        [paymentParams setValue:[serviceParams valueForKey:key] ? [serviceParams valueForKey:key] : @"" forKey:key];
    }
    
    NSDictionary *customParams = [arguments valueForKey:@"customParams"];
    for (NSString *key in customParams) {
        [paymentParams setValue:[customParams valueForKey:key] ? [customParams valueForKey:key] : @"" forKey:key];
    }
    
    [self.hyper startViewController:self.viewController data:paymentParams callback:^(int status, id  _Nullable responseData, NSError * _Nullable error) {
    
        NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
        CDVPluginResult *result = nil;
        
        if (error) {
            [dict setObject:error.description forKey:@"response"];
            
            result = [CDVPluginResult
                      resultWithStatus:CDVCommandStatus_ERROR
                      messageAsDictionary:dict];
        } else if (status == 0) {
            [dict setObject:[self dictionaryToString:responseData] forKey:@"response"];
            
            result = [CDVPluginResult
                      resultWithStatus:CDVCommandStatus_ERROR
                      messageAsDictionary:dict];
        } else {
            [dict setObject:[self dictionaryToString:responseData] forKey:@"response"];
            
            result = [CDVPluginResult
                      resultWithStatus:CDVCommandStatus_OK
                      messageAsDictionary:dict];
        }
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        
    }];
}

- (NSString*)dictionaryToString:(id)dict{
    
    if (!dict || ![NSJSONSerialization isValidJSONObject:dict]) {
        return @"";
    }
    
    NSString *data = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dict options:0 error:nil] encoding:NSUTF8StringEncoding];
    return data;
}

- (CGRect)safeAreaFrame {
    
    if (@available(iOS 11.0, *)) {
        return self.viewController.view.safeAreaLayoutGuide.layoutFrame;
    }
    return self.viewController.view.frame;
}

@end
