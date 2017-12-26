package com.ezly.ezly_android.network;

import com.ezly.ezly_android.Data.Response.WeChatTokenResponse;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Johnnie on 11/11/16.
 */

public interface WeChatAPI {

    String ENDPOINT_GET_TOKEN = "sns/oauth2/access_token";

    //token
    @GET(ENDPOINT_GET_TOKEN)
    Observable<WeChatTokenResponse> getWeChatToken(@QueryMap Map<String, String> paramsMap);
}
