package in.juspay.ec.hybrid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import in.juspay.godel.PaymentActivity;


public class ExpressCheckout extends CordovaPlugin {

    private final static String LOG_TAG = "EC_HEADLESS_PLUGIN";
    private final static int REQUEST_CODE = 88;
    private static final String PAYMENT = "JuspayStartPayment";
    private static CordovaInterface cordova = null;
    private CallbackContext cordovaCallBack;

    /**** Cordova initialize function.
     * @param initCordova   A CordovaInterface Object which can later be used as a Context or Activity Object
     *                      to trigger intents.
     * @param webView       A CordovaWebview Object.
     */
    @Override
    public void initialize(CordovaInterface initCordova, CordovaWebView webView) {
        Log.d(LOG_TAG, "initialized");
        cordova = initCordova;
        super.initialize(cordova, webView);
    }


    /**** Executed when cordova/exec function in js is called. This is the entry point and acts as a bridge between js and java calls of cordova.
     * @param action          The action to execute.
     * @param args            The payload to make payment.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return
     * @throws JSONException
     */
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG, "executed");
        if (action.equals(PAYMENT)) {
            cordova.getThreadPool().execute(() -> makePayment(args.optJSONObject(0), callbackContext));
            return true;
        }
        return false;
    }


    /**** Core function which constructs the appropriate payload from the incoming js request and initiates the payment by
     * triggering an intent to PaymentActivity.class.
     * @param params The payload to make payment
     * @param callbackContext The callback context used when calling back into JavaScript.
     */
    private void makePayment(JSONObject params, final CallbackContext callbackContext) {
        try {
            this.cordovaCallBack = callbackContext;

            JSONObject baseParams = params.optJSONObject("baseParams");
            JSONObject serviceParams = params.optJSONObject("serviceParams");
            JSONArray customParams = params.optJSONObject("customParams");

            Log.d(LOG_TAG, params.toString());
            Log.d(LOG_TAG, baseParams.toString());
            Log.d(LOG_TAG, serviceParams.toString());
            Log.d(LOG_TAG, customParams.toString());

            Bundle intentBundle = jsonToBundle(baseParams);
            intentBundle.putAll(jsonToBundle(serviceParams));
            intentBundle.putAll(jsonToBundle(customParams));

            Intent intent = new Intent(cordova.getActivity(), PaymentActivity.class);
            intent.putExtras(intentBundle);

            cordova.startActivityForResult(this, intent, REQUEST_CODE);

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }


    /**** onActivityResult is called when Payment is finished inside PaymentActivity.class and returns back to
     * called Activity.
     * @param requestCode   The request code originally supplied to startActivityForResult(),
     *                      allowing you to identify who this result came from.
     * @param resultCode    The integer result code returned by the child activity through its setResult().
     * @param data          Returned by child activity i.e., PaymentActivity in this case. The PaymentResponse
     *                      can be extracted from intent object called data with key name "payload" and it is sent
     *                      back to JS using cordovaCallback.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            JSONObject jsonObj = new JSONObject();
            if (data != null && data.getStringExtra("payload") != null) {

                String result = data.getStringExtra("payload");
                jsonObj.put("response", result);

                if (resultCode == Activity.RESULT_OK) {
                    this.cordovaCallBack.success(jsonObj);
                } else {
                    this.cordovaCallBack.error(jsonObj);
                }

            } else {
                jsonObj.put("response", "data null");
                this.cordovaCallBack.error(jsonObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.cordovaCallBack.error(e.getMessage());
        }

    }

    /**** Helper function which converts JSONArray Object to Bundle Object
     * @param jsonArray     Input JSONArray Object
     * @return Returns converted Bundle Object
     * @throws Exception    Throws JSONException and/or NullPointerException
     */
    private Bundle jsonObjArraytoBundle(JSONArray jsonArray) throws Exception {
        Bundle bundle = new Bundle();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            JSONArray objKeys = obj.names();
            bundle.putString((objKeys.getString(0)), obj.getString(objKeys.getString(0)));
        }
        return bundle;
    }

    /**** Helper function which converts JSON Object to Bundle Object
     * @param jsonObject    Input JSON Object
     * @return Returns converted Bundle Object
     * @throws Exception    Throws JSONException and/or NullPointer Exception
     */
    private Bundle jsonToBundle(JSONObject jsonObject) throws Exception {
        Bundle bundle = new Bundle();
        JSONArray keys = jsonObject.names();

        for (int i = 0; i < keys.length(); ++i) {
            String key = keys.getString(i);
            Object value = jsonObject.getString(key);

            if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                String[] stringArray = null;
                if (jsonArray != null) {
                    stringArray = new String[jsonArray.length()];
                    for (int j = 0; j < jsonArray.length(); j += 1) {
                        Object jsonElement = jsonArray.opt(j);
                        if (jsonElement instanceof String)
                            stringArray[j] = (String) jsonElement;
                    }
                }
                ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(stringArray));
                bundle.putStringArrayList(key, arrayList);
            } else {
                bundle.putString(key, (String) value);
            }
        }

        return bundle;
    }


}
