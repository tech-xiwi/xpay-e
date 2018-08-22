package com.tech.xiwi.pay.common;

import android.app.Activity;

public class PayApi {
    public static volatile boolean isPaying = false;
    private IPay pay;

    public <T> void pay(Activity activity, IPay pay, Channel channel, T param, IPay.PayCallback callback) {
        if (activity != null && param != null && !isPaying) {
            if (!Utils.isInstall(activity, channel.getPkg())) {
                if (callback != null) {
                    callback.onFailed();
                }
                return;
            }
            isPaying = true;
            this.pay = pay;
            this.pay.pay(activity, param, callback);
        }
    }

    public void release() {
        if (pay != null && isPaying) {
            pay.release();
            pay = null;
        }
        isPaying = false;
    }
}
