# PhoneGap app integration 
JusPay has a native android and iOS client which can be used by PhoneGap applications. To get started, you first need to download the code for the plugin.

## Installation 
This requires phonegap/cordova CLI 5.0+

Add the plugin to your phonegap/cordova apps using these commands.

```sh
$ phonegap plugin add juspay/ec-headless-cordova-plugin
```
(or)
```sh
$ cordova plugin add juspay/ec-headless-cordova-plugin
```


## Dependencies 

#### For Android 

To add or change ec-hl-cordova-plugin specific dependencies, refer this file. 
```sh
platforms/android/ec-hl-cordova-plugin/<appname>-_plugin-dependencies.gradle
```

#### For iOS 

Minimum supporeted iOS version: 9.0

If you are getting the pod error like `Failed to install 'ec-hl-cordova-plugin': Error: pod: Command failed with exit code 1` while installing "ec-hl-cordova-plugin" for ios, please update `platform :ios` to '9.0' in the Podfile and run the command `pod install`.


## Usage 

```sh
  var nbPayload = {
                  opName: "nbTxn",
                  paymentMethodType: "NB",
                  paymentMethod: "enter bank code", // Eg: NB_SBI
                  redirectAfterPayment: "true",
                  format: "json"
                }
  //Here payload format is specified for netbanking transaction. For other operations, refer 
  [EC SDK Doc](https://www.juspay.in/docs/hyper-sdk/android/ExpressCheckout/index.html)

  var requestPayload = {
            baseParams: {
              merchant_id: "pass merchant id",
              client_id: "pass client id",
              transaction_id: "pass transaction id", //optional
              order_id: "pass order id",
              amount: "amount", //eg: "1.00"
              customer_id : "pass customer id",
              customer_email : "pass email",
              customer_phone_number : "pass phone number",
              environment: "pass environment" //eg: "sandbox" or "prod"
            },
            serviceParams: {
              service: "in.juspay.ec",
              session_token: "pass client auth token",
              endUrls: [], //eg: ["https://www.reload.in/recharge/", ".*www.reload.in/payment/f.*"]
              payload: JSON.stringify(nbPayload)
            },
            customParams: {}, //customParams are optional key value pairs. { udf_circle: "Andhra Pradesh" }
            onSuccess: function (successResponse) {
              console.log("Success Response", successResponse);
            },
            onError: function (errorResponse) {
              console.log("Error Response", errorResponse);
            }
     }
  var expressCheckout = new ExpressCheckout();
  expressCheckout.startPayment(requestPayload);
```
