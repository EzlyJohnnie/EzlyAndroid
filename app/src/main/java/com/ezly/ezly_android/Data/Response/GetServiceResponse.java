package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyService;

import java.util.ArrayList;

/**
 * Created by Johnnie on 11/10/16.
 */

public class GetServiceResponse extends EzlyBaseResponse {
    ArrayList<EzlyService> data;

    public ArrayList<EzlyService> getServices() {
        return data;
    }

    public void setServices(ArrayList<EzlyService> services) {
        this.data = services;
    }
}
