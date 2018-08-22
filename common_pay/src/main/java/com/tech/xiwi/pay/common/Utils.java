package com.tech.xiwi.pay.common;

import android.content.Context;
import android.content.pm.PackageManager;

public class Utils {

    /**
     * 通过包名找应用
     * com.tencent.mm微信
     * com.sina.weibo新浪微博
     * 判断是否安装了QQ "com.tencent.mobileqq"
     * true 安装了相应包名的app
     */
    public static boolean isInstall(Context context, String pkgName) {
        if (null == context || null == pkgName) {
            return false;
        }

        boolean bHas = true;
        try {
            context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_GIDS);
        } catch (PackageManager.NameNotFoundException e) {
            // 抛出找不到的异常，说明该程序已经被卸载
            bHas = false;
        }
        return bHas;
    }

    public static void checkNull(Object obj) {
        if (obj == null) {
            throw new RuntimeException("you have not init param,this must be init ...");
        }
    }
}
