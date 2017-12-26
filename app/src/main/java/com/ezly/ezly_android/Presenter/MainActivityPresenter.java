package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.UI.ViewInterFace.MainActivityView;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;

import javax.inject.Inject;

/**
 * Created by Johnnie on 2/10/16.
 */

public class MainActivityPresenter extends BasePresenter {

    private MainActivityView view;
    private EzlySearchParam searchParam;

    @Inject
    public MainActivityPresenter(EzlySearchParam searchParam){
        this.searchParam = searchParam;
    }

    public void setView(MainActivityView view) {
        this.view = view;
    }

    public void initSearchParam() {
        searchParam.initSearchLocation(view.getActivity());
    }



}
