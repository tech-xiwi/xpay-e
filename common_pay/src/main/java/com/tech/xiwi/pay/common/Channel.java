package com.tech.xiwi.pay.common;

public enum Channel {
    Ali("com.eg.android.AlipayGphone"), Wechat("com.tencent.mm");
    private String pkg;

    Channel(String pkg) {
        this.pkg = pkg;
    }

    public String getPkg() {
        return pkg;
    }
}
