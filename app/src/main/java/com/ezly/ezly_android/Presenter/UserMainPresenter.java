package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.UI.ViewInterFace.UserMainView;

import javax.inject.Inject;

/**
 * Created by Johnnie on 6/11/16.
 */

public class UserMainPresenter extends BasePresenter {

    private UserMainView mView;

    @Inject
    public UserMainPresenter() {}

    public void setView(UserMainView mView) {
        this.mView = mView;
    }
}
