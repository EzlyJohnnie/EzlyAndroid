package com.ezly.ezly_android.network;

import com.ezly.ezly_android.Data.EzlyToken;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Johnnie on 6/11/16.
 */

public interface Ezly_IdentityAPI {


    String ENDPOINT_LOG_IN = "connect/token";

    //login
    @FormUrlEncoded
    @POST(ENDPOINT_LOG_IN)
    @Headers({
            "Content-Type:application/x-www-form-urlencoded"
    })
    Observable<EzlyToken> login(@FieldMap Map<String, String> body);
}
