package com.ezly.ezly_android.UI.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

/**
 * Created by Johnnie on 20/10/16.
 */

public class EzlySearchHostFragment extends EzlyBaseHostFragment {
    public static final String KEY_SEARCH_VIEW_TYPE = "key_searchType";
    public static final int SEARCH_VIEW_TYPE_WELCOME = 0;
    public static final int SEARCH_VIEW_TYPE_NORMAL = 1;

    private int viewType;

    public static EzlySearchHostFragment newInstance(int searchViewType){
        EzlySearchHostFragment fragment = new EzlySearchHostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_SEARCH_VIEW_TYPE, searchViewType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init();
        return root;
    }

    private void init() {
        viewType = getArguments().getInt(EzlySearchHostFragment.KEY_SEARCH_VIEW_TYPE);
        setLandingFragment();
    }

    private void setLandingFragment() {
        replace(EzlySearchFragment.newInstance(viewType));
    }
}
