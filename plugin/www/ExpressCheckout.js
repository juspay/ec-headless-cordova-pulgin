/**
 * Main module that packs the payment functionality and success / failure callbacks
 */
var exec = cordova.require('cordova/exec');

var ExpressCheckout = function () {
    console.log('ExpressCheckout instanced');
};

ExpressCheckout.prototype.startPayment = function (request) {
    var errorCallback = function (obj) {
        request.onError(obj);
    };

    var successCallback = function (obj) {
        request.onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ExpressCheckout', 'JuspayStartPayment', [request]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = ExpressCheckout;
}

