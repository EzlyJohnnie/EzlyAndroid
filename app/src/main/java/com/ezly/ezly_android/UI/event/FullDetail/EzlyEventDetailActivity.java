package com.ezly.ezly_android.UI.event.FullDetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 19/02/17.
 */

public class EzlyEventDetailActivity extends EzlyBaseActivity {
    private static final String KEY_EVENT = "key_event";

    @BindView(R.id.tab_bar) View tabbar;

    private EzlyEvent event;

    public static void startActivity(Activity activity, EzlyEvent event){
        Intent intent = new Intent(activity, EzlyEventDetailActivity.class);
        if(event instanceof EzlyJob){
            EzlyJob job = (EzlyJob)event;
            intent.putExtra(KEY_EVENT, job);
        }
        else if(event instanceof EzlyService){
            EzlyService service = (EzlyService)event;
            intent.putExtra(KEY_EVENT, service);
        }

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        setLandingFragment();
    }

    private void init() {
        event = getIntent().getParcelableExtra(KEY_EVENT);
        tabbar.setVisibility(View.GONE);
    }

    public void setLandingFragment() {
        replace(EzlyEventDetailFragmentHost.getInstance(event));
    }

    @Override
    public void onBackPressed() {
        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(KEY_LANDING_FRAGMENT);
        if(fragment != null && fragment instanceof EzlyBaseHostFragment && fragment.isAdded()){
            boolean hasHandle = ((EzlyBaseHostFragment)fragment).onBackPressed();
            if(hasHandle){
                return;
            }
        }

        if(getSupportFragmentManager().getBackStackEntryCount() <= 0){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }
}
