package com.ezly.ezly_android.Presenter;

import android.content.res.Resources;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Model.AddressModel;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.ViewInterFace.AddressView;
import com.ezly.ezly_android.Utils.TextUtils;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 12/02/17.
 */

public class AddressPresenter extends BasePresenter {

    private AddressView mView;

    private AddressModel addressModel;

    @Inject
    public AddressPresenter(AddressModel addressModel) {
        this.addressModel = addressModel;
    }

    public void setView(AddressView mView) {
        this.mView = mView;
    }

    public void getMyAddressList(){
        addressModel.getMyAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAddressListPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<? super List<EzlyAddress>> onAddressListPrepared() {
        return new Action1<List<EzlyAddress>>() {
            @Override
            public void call(List<EzlyAddress> addresses) {
                if(mView != null){
                    mView.onAddressListPrepared(addresses);
                }
            }
        };
    }

    public void addAddress(EzlyAddress address){
        addressModel.addAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSimpleRequestComplete(R.string.address_operation_add), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void deleteAddress(String addressID){
        addressModel.deleteAddress(addressID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSimpleRequestComplete(R.string.address_operation_delete), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void updateAddress(EzlyAddress address){
        addressModel.updateAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSimpleRequestComplete(R.string.address_operation_update), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public Action1<Response<Void>> onSimpleRequestComplete(final int actionStrRes){
        return new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> response) {
                if(mView != null){
                    Resources resources = mView.getContext().getResources();
                    boolean isSuccess = (response.code() - 200 > 0 && response.code() - 200 < 100);
                    String message;
                    if(isSuccess){
                        message = String.format(resources.getString(R.string.address_operation_format_str), resources.getString(actionStrRes));
                    }
                    else{
                        message = !TextUtils.isEmpty(response.message()) ?
                                response.message()
                                : String.format(resources.getString(R.string.address_operation_error_format_str), resources.getString(actionStrRes));
                    }

                    mView.onSimpleRequestCompleted(isSuccess, message);
                }
            }
        };
    }
}
