package com.ezly.ezly_android.Data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Created by Johnnie on 6/11/16.
 */

public class EzlyToken {
    @Expose private String access_token;
    @Expose private String refreshToken;
    @Expose private long expires_in;
    @Expose private long createTime;


    public String getCurrentToken() {
        return access_token;
    }

    public void setCurrentToken(String currentToken) {
        this.access_token = currentToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiry() {
        return expires_in;
    }

    public void setExpiry(long expiry) {
        this.expires_in = expiry;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String toJson(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() / 1000 >= createTime + expires_in - 300;
    }
}
