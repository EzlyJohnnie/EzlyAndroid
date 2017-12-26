package com.ezly.ezly_android.Model;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Johnnie on 20/11/16.
 */

public class AddressModel {

    private ServerHelper serverHelper;

    @Inject
    public AddressModel(ServerHelper serverHelper) {
        this.serverHelper = serverHelper;
    }

    public Observable<List<EzlyAddress>> getMyAddress(){
        return serverHelper.getMyAddress();
    }

    public Observable<Response<Void>> addAddress(EzlyAddress address){
        return serverHelper.addAddress(address);
    }

    public Observable<Response<Void>> updateAddress(EzlyAddress address){
        return serverHelper.updateAddress(address);
    }

    public Observable<Response<Void>> deleteAddress(String addressID){
        return serverHelper.deleteAddress(addressID);
    }
}
