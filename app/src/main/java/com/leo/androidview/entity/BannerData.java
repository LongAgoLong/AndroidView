package com.leo.androidview.entity;

import com.leo.recyclerbanner.callback.IBannerData;

public class BannerData implements IBannerData {
    private String url;

    public BannerData(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
