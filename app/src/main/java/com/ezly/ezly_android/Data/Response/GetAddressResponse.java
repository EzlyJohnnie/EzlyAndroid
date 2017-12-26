package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;

import java.util.ArrayList;

/**
 * Created by Johnnie on 11/10/16.
 */

public class GetAddressResponse extends EzlyBaseResponse {
    ArrayList<EzlyAddress> data;

    public ArrayList<EzlyAddress> getAddress() {
        return data;
    }

    public void setServices(ArrayList<EzlyAddress> address) {
        this.data = address;
    }
}
