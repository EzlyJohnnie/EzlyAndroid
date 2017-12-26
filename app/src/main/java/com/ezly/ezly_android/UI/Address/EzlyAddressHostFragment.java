package com.ezly.ezly_android.UI.Address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

/**
 * Created by Johnnie on 13/02/17.
 */

public class EzlyAddressHostFragment extends EzlyBaseHostFragment{

    public static EzlyAddressHostFragment newInstance(){
        EzlyAddressHostFragment fragment = new EzlyAddressHostFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init();
        return root;
    }

    private void init() {
        replace(EzlyMyAddressListFragment.newInstance());
    }

}
