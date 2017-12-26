package com.ezly.ezly_android.UI.Notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

/**
 * Created by Johnnie on 12/03/17.
 */

public class EzlyNotificationHostFragment extends EzlyBaseHostFragment{

    private static final String KEY_HAS_BACK_BTN = "key_hasBackBtn";
    private  boolean hasBackBtn;

    public static EzlyNotificationHostFragment getInstance(boolean hasBackBtn){
        EzlyNotificationHostFragment fragment = new EzlyNotificationHostFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_HAS_BACK_BTN, hasBackBtn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init();
        return root;
    }


    private void init() {
        if(getArguments() != null){
            hasBackBtn = getArguments().getBoolean(KEY_HAS_BACK_BTN);
        }
        replace(EzlyNotificationFragment.getInstance(hasBackBtn));
    }
}
