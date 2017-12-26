package com.ezly.ezly_android.UI.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

/**
 * Created by Johnnie on 5/11/16.
 */

public class EzlyLoginHostFragment extends EzlyBaseHostFragment {

    public static EzlyLoginHostFragment getInstance(){
        EzlyLoginHostFragment fragment = new EzlyLoginHostFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init();
        return root;
    }


    private void init() {
        replace(EzlyLoginFragment.getInstance());
    }
}
