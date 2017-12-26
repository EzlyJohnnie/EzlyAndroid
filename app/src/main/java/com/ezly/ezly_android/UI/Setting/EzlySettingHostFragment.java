package com.ezly.ezly_android.UI.Setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

/**
 * Created by Johnnie on 18/11/16.
 */
public class EzlySettingHostFragment extends EzlyBaseHostFragment {
    public static final String KEY_HAS_BACK_BTN = "key_hasBackBtn";

    private boolean hasBackBtn;

    public static EzlySettingHostFragment getInstance(boolean hasBackBtn){
        EzlySettingHostFragment fragment = new EzlySettingHostFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_HAS_BACK_BTN, hasBackBtn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init(savedInstanceState);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_HAS_BACK_BTN, hasBackBtn);
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        initData(savedInstanceState);
        replace(EzlySettingFragment.getInstance(hasBackBtn));
    }

    private void initData(Bundle savedInstanceState) {
        if(savedInstanceState == null && getArguments() != null){
            hasBackBtn = getArguments().getBoolean(KEY_HAS_BACK_BTN, false);
        }
        else{
            hasBackBtn = savedInstanceState.getBoolean(KEY_HAS_BACK_BTN, false);
        }
    }


}
