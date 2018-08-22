package com.tech.xiwi.pay.alipay;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.tech.xiwi.pay.common.IPay;
import com.tech.xiwi.pay.common.PayApi;

import java.util.Map;

/**
 * Created by xiwi on 2017/10/17.
 */
public class AliPay implements IPay<String> {
    private PayThread payThread;

    private AliPay() {

    }

    public static AliPay build() {
        return new AliPay();
    }

    @Override
    public void pay(Activity activity, String param, IPay.PayCallback callback) {
        Log.d(TAG, "pay: ");
        if (payThread == null || !payThread.isInterrupted()) {
            payThread = new PayThread();
            payThread.setActivity(activity);
            payThread.setParam(param);
            payThread.setCallback(callback);
            payThread.start();
        }
    }

    @Override
    public void release() {
        if (payThread != null && !payThread.isInterrupted()) {
            payThread.release();
            payThread.interrupt();
            payThread = null;
        }
    }

    private static class PayThread extends Thread {
        private Activity activity;
        private String param;
        private PayCallback callback;

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public void setCallback(PayCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            super.run();
            if (activity == null || param == null) {
                if (callback != null) {
                    callback.onFailed();
                }
                PayApi.isPaying = false;
                return;
            }

            PayTask alipay = new PayTask(activity);
            Map<String, String> result = alipay.payV2(param, true);
            Log.d(TAG, "result: " + result);
            PayResult payResult = new PayResult(result);
            String resultStatus = payResult.getResultStatus();
            PayApi.isPaying = false;
            if (TextUtils.equals(resultStatus, "9000")) {//订单支付成功
                if (callback != null) {
                    callback.onSuccess(result);
                }
            } else if (TextUtils.equals(resultStatus, "4000")) {//订单支付失败
                if (callback != null) {
                    callback.onFailed();
                }
            } else if (TextUtils.equals(resultStatus, "6001")) {//用户中途取消
                if (callback != null) {
                    callback.onCancel();
                }
            } else if (TextUtils.equals(resultStatus, "6002")) {//网络连接出错
                if (callback != null) {
                    callback.onFailed();
                }
            } else if (TextUtils.equals(resultStatus, "6004") || TextUtils.equals(resultStatus, "8000")) {//支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                if (callback != null) {
                    callback.onFailed();
                }
            } else {//订单支付失败
                if (callback != null) {
                    callback.onFailed();
                }
            }
        }

        void release() {
            PayApi.isPaying = false;
            activity = null;
            param = null;
            callback = null;
        }
    }

    private static class PayResult {
        private String resultStatus;
        private String result;
        private String memo;

        public PayResult(Map<String, String> rawResult) {
            if (rawResult == null) {
                return;
            }

            for (String key : rawResult.keySet()) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult.get(key);
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult.get(key);
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult.get(key);
                }
            }
        }

        public String getResultStatus() {
            return resultStatus;
        }


        public String getMemo() {
            return memo;
        }


        public String getResult() {
            return result;
        }
    }
}
