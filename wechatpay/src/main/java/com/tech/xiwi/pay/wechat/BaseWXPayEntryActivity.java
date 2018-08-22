package com.tech.xiwi.pay.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tech.xiwi.pay.common.IPay;
import com.tech.xiwi.pay.common.InitConfig;
import com.tech.xiwi.pay.common.PayApi;
import com.tech.xiwi.pay.common.Utils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import static com.tech.xiwi.pay.common.IPay.TAG;

/**
 * Created by xiwi on 2017/10/17.
 */
public abstract class BaseWXPayEntryActivity extends Activity {

    private IWXAPI api;

    private static IPay.PayCallback payCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, appId());
        handleIntent(getIntent());
    }

    public static void setPayCallback(IPay.PayCallback callback) {
        payCallback = callback;
    }

    /**
     * WeChat_APP_ID
     */
    public String appId() {
        Utils.checkNull(InitConfig.getWeChatAppId());
        return InitConfig.getWeChatAppId();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
        finish();
    }

    private void handleIntent(Intent intent) {
        if (api != null && intent != null) {
            api.handleIntent(intent, new IWXAPIEventHandler() {
                @Override
                public void onReq(BaseReq baseReq) {
                    if (baseReq != null) {
                        Log.d(TAG, "onReq() called with: baseReq = [" + baseReq + "]");
                    }
                }

                @Override
                public void onResp(BaseResp baseResp) {
                    //支付结果回调 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5
                    if (baseResp != null) {
                        Log.d(TAG, "onResp() called with: baseResp = [" + baseResp.errStr + "]");
                        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

                            if (payCallback != null) {
                                if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {//支付成功
                                    Map<String, String> data = new HashMap<>();
                                    data.put("openId", baseResp.openId);
                                    data.put("errCode", String.valueOf(baseResp.errCode));
                                    data.put("transaction", baseResp.transaction);
                                    data.put("errStr", baseResp.errStr);
                                    payCallback.onSuccess(data);
                                } else if (baseResp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {//取消
                                    payCallback.onCancel();
                                } else {//支付失败
                                    payCallback.onFailed();
                                }
                            }
                        }
                        PayApi.isPaying = false;
                    }
                    finish();
                }
            });
        } else {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayApi.isPaying = false;
        if (api != null) {
            api.detach();
            payCallback = null;
            api = null;
        }
    }
}
