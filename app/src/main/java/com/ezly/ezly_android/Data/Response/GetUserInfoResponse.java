package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyUser;

/**
 * Created by Johnnie on 6/11/16.
 */

public class GetUserInfoResponse extends EzlyBaseResponse {

    private EzlyUser data;

    public EzlyUser getUser() {
        return data;
    }

    public void setUser(EzlyUser user) {
        this.data = user;
    }
}
