package com.ezly.ezly_android.network;

import android.content.Context;
import android.util.Log;

import rx.functions.Action1;

/**
 * Created by Johnnie on 16/10/16.
 */

public class RequestErrorHandler {

    public static Action1<Throwable> onRequestError(Context context) {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //TODO: handle error
                if(throwable != null){
                    Log.e("requestError", throwable.getMessage());
                }

            }
        };
    }
}
