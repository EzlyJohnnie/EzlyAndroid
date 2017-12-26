package com.ezly.ezly_android.UI.welcome;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.ezly.ezly_android.Presenter.WelcomePresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.ViewInterFace.WelcomeView;
import com.ezly.ezly_android.UI.search.EzlySearchHostFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 20/10/16.
 */

public class EzlyWelcomeActivity extends EzlyBaseActivity implements WelcomeView{

    @Inject
    WelcomePresenter presenter;

    @BindView(R.id.tab_bar) View tabbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getActivityComponent().inject(this);
        presenter.setView(this);
        tabbar.setVisibility(View.GONE);
        setLandingFragment();
    }

    private void setLandingFragment() {
        replace(new EzlyWelcomeFragment());
    }

    public void startSearchFragment() {
        replace(EzlySearchHostFragment.newInstance(EzlySearchHostFragment.SEARCH_VIEW_TYPE_WELCOME));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public EzlyBaseActivity getActivity() {
        return this;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}
