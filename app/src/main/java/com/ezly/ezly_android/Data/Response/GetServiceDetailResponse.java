package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyService;

/**
 * Created by Johnnie on 29/10/16.
 */

public class GetServiceDetailResponse extends EzlyBaseResponse {
    private EzlyService data;

    public EzlyService getService() {
        return data ;
    }

    public void setService(EzlyService service) {
        this.data = service;
    }
}
