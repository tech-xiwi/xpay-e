package com.tech.xiwi.pay.common;

import android.app.Activity;

import java.util.Map;

/**
 * Created by xiwi on 2017/10/17.
 */
public interface IPay<T> {

    public static final String TAG = "IPay";

    void pay(Activity activity, T param, PayCallback callback);

    void release();

    public static interface PayCallback {
        void onSuccess(Map<String, String> data);

        /**
         * 支付内部异常（包含客户端未安装）
         */
        void onFailed();

        void onCancel();
    }
}
