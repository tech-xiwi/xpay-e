package com.tech.xiwi.pay.wechat;

import android.app.Activity;
import android.util.Log;

import com.tech.xiwi.pay.common.IPay;
import com.tech.xiwi.pay.common.PayApi;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiwi on 2017/10/17.
 */

public class WXPay implements IPay<JSONObject> {
    //上下文
    private Activity activity;
    private IWXAPI api;

    private WXPay() {

    }


    public static WXPay build() {
        return new WXPay();
    }

    public static class ParamBuilder {
        //微信支付AppID
        private String appId;
        //微信支付商户号
        private String partnerId;
        //预支付码（重要）
        private String prepayId;
        //"Sign=WXPay"
        private String packageValue = "Sign=WXPay";

        private String nonceStr;
        //时间戳
        private String timeStamp;
        //签名
        private String sign;

        public ParamBuilder() {
            super();
        }

        /**
         * 设置微信支付AppID
         *
         * @param appId
         * @return
         */
        public ParamBuilder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        /**
         * 微信支付商户号
         *
         * @param partnerId
         * @return
         */
        public ParamBuilder setPartnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        /**
         * 设置预支付码（重要）
         *
         * @param prepayId
         * @return
         */
        public ParamBuilder setPrepayId(String prepayId) {
            this.prepayId = prepayId;
            return this;
        }

        /**
         * 设置
         *
         * @param packageValue
         * @return
         */
        public ParamBuilder setPackageValue(String packageValue) {
            this.packageValue = packageValue;
            return this;
        }

        /**
         * 设置
         *
         * @param nonceStr
         * @return
         */
        public ParamBuilder setNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        /**
         * 设置时间戳
         *
         * @param timeStamp
         * @return
         */
        public ParamBuilder setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        /**
         * 设置签名
         *
         * @param sign
         * @return
         */
        public ParamBuilder setSign(String sign) {
            this.sign = sign;
            return this;
        }

        public JSONObject create() {
            JSONObject json = new JSONObject();
            try {
                json.put("appid", appId);
                json.put("prepayid", prepayId);
                json.put("partnerid", partnerId);
                json.put("noncestr", nonceStr);
                json.put("timestamp", timeStamp);
                json.put("sign", sign);
                json.put("packagevalue", packageValue);
            } catch (JSONException e) {
                e.printStackTrace();
                PayApi.isPaying = false;
            }
            return json;
        }
    }

    @Override
    public void pay(Activity activity, JSONObject param, PayCallback callback) {
        Log.d(TAG, "pay: ");
        
        try {
            String appId = param.getString("appid");
            if (api == null) {
                api = WXAPIFactory.createWXAPI(activity, appId);
                api.registerApp(appId);
            }

            if (!api.isWXAppSupportAPI()) {//微信版本过低请升级微信客户端
                Log.e(TAG, "pay: 微信版本过低请升级微信客户端!!!");
                if (callback != null) {
                    callback.onFailed();
                }
                PayApi.isPaying = false;
                return;
            }

            PayReq payReq = new PayReq();

            payReq.appId = appId;
            payReq.partnerId = param.getString("partnerid");
            payReq.prepayId = param.getString("prepayid");
            payReq.nonceStr = param.getString("noncestr");
            payReq.timeStamp = param.getString("timestamp");
            payReq.sign = param.getString("sign");
            payReq.packageValue = param.getString("packagevalue");
            api.sendReq(payReq);
            BaseWXPayEntryActivity.setPayCallback(callback);
        } catch (JSONException e) {
            Log.e(TAG, "pay: 参数异常!!!");
            if (callback != null) {
                callback.onFailed();
            }
            PayApi.isPaying = false;
            return;
        }
    }

    @Override
    public void release() {
        if (api != null) {
            api.unregisterApp();
            api = null;
        }
        activity = null;
    }
}
