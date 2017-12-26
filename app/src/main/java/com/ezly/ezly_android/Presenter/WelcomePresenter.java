package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.UI.ViewInterFace.WelcomeView;

import javax.inject.Inject;

/**
 * Created by Johnnie on 20/10/16.
 */

public class WelcomePresenter extends BasePresenter {

    private WelcomeView view;

    @Inject
    public WelcomePresenter(){}

    public void setView(WelcomeView view) {
        this.view = view;
    }

}
