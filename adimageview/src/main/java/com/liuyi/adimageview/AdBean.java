package com.liuyi.adimageview;

import android.graphics.drawable.Drawable;

public class AdBean {
    public AdBean(String title, String des, Drawable drawable, boolean isAd) {
        this.title = title;
        this.des = des;
        this.isAd = isAd;
    }

    public Drawable drawable;
    public String title;
    public String des;
    public boolean isAd;

}
