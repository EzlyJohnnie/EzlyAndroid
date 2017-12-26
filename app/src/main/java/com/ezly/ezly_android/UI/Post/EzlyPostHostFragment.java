package com.ezly.ezly_android.UI.Post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

/**
 * Created by Johnnie on 20/11/16.
 */

public class EzlyPostHostFragment extends EzlyBaseHostFragment {

    public static EzlyPostHostFragment getInstance(){
        EzlyPostHostFragment fragment = new EzlyPostHostFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init();
        return root;
    }

    @Override
    public void onStop(){
        super.onStop();
        showTabbar(true);
    }

    private void init() {
        replace(EzlyPostCategoryFragment.getInstance());
    }

    @Override
    public boolean onBackPressed() {
        boolean handled = false;

        Fragment fragment = getChildFragmentManager().findFragmentByTag(KEY_LANDING_FRAGMENT);
        if(fragment != null && fragment instanceof EzlyNewPostFragment && fragment.isAdded()){
            ((EzlyNewPostFragment)fragment).onCategoryClicked();
            handled = true;
        }

        if(false){
            return super.onBackPressed();
        }
        return handled;
    }
}
