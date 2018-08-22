package com.tech.xiwi.xpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tech.xiwi.pay.alipay.AliPay;
import com.tech.xiwi.pay.common.Channel;
import com.tech.xiwi.pay.common.IPay;
import com.tech.xiwi.pay.common.PayApi;
import com.tech.xiwi.pay.wechat.WXPay;

import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void patAli() {
        PayApi payApi = new PayApi();
        payApi.pay(this, AliPay.build(), Channel.Ali, "", new IPay.PayCallback() {
            @Override
            public void onSuccess(Map<String, String> data) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void payWechat() {
        PayApi payApi = new PayApi();
        WXPay.ParamBuilder builder = new WXPay.ParamBuilder();
        payApi.pay(this, WXPay.build(), Channel.Wechat, builder.create(), new IPay.PayCallback() {
            @Override
            public void onSuccess(Map<String, String> data) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onCancel() {

            }
        });
    }
}
