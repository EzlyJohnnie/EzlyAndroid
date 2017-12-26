package com.ezly.ezly_android.Data.Response;

/**
 * Created by Johnnie on 28/11/16.
 */

public class GetEventCommentsResponse extends EzlyBaseResponse {

   private GetEventCommentsResponseData data;

    public GetEventCommentsResponseData getData() {
        return data;
    }

    public void setData(GetEventCommentsResponseData data) {
        this.data = data;
    }
}
