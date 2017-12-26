package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;

import java.util.List;

/**
 * Created by Johnnie on 12/02/17.
 */

public interface AddressView extends BaseViewInterface{

    void onAddressListPrepared(List<EzlyAddress> addresses);
    void onSimpleRequestCompleted(boolean isSuccess, String errorMsg);
}
