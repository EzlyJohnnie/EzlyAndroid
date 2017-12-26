package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyUser;

import java.util.List;

/**
 * Created by Johnnie on 13/11/16.
 */

public class GetUsersResponse extends EzlyBaseResponse {
    List<EzlyUser> data;

    public List<EzlyUser> getData() {
        return data;
    }

    public void setData(List<EzlyUser> data) {
        this.data = data;
    }
}
