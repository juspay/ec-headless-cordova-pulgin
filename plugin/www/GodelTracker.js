var logger = function () {};

/*
	Performs a tracking of the payment status.

	By default, we set empty callbacks - which are never called.
*/
var trackPayment = function (paymentId, paymentStatus) {
	cordova.exec(
        logger,
        logger,
        "ExpressCheckout",
        "JuspayTrackStatus",
        [{
        	txnId: paymentId,
        	status: paymentStatus
        }]
    );
};

var godelTracker = {};

/*
	Define shorhand functions to track the status.

	Object.defineProperty is used as we dont want these to be enumerable properties.
*/
Object.defineProperty(godelTracker, 'trackSuccess', {
	enumerable: false,
	value: function (txnId) {
		trackPayment(txnId, 'SUCCESS');
	}
});

Object.defineProperty(godelTracker, 'trackFailure', {
	enumerable: false,
	value: function (txnId) {
		trackPayment(txnId, 'FAILURE');
	}
});

Object.defineProperty(godelTracker, 'trackCancelled', {
	enumerable: false,
	value: function (txnId) {
		trackPayment(txnId, 'CANCELLED');
	}
});

if('freeze' in Object) {
	godelTracker = Object.freeze(godelTracker);
}

module.exports = godelTracker;