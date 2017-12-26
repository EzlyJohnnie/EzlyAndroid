package com.ezly.ezly_android.UI.BaseComponent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.R;

import butterknife.ButterKnife;

/**
 * Created by Johnnie on 2/10/16.
 */

public class EzlyBaseHostFragment extends EzlyBaseFragment {
    protected static final String KEY_LANDING_FRAGMENT = "key_landing_fragment";
    protected static final String KEY_PRESENTING_FRAGMENT = "key_presenting_fragment";

    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_PRESENT = 1;
    public static final int ANIMATION_RIGHT_IN = 2;
    public static final int ANIMATION_FADE_IN = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = initRootView(inflater, container);
        ButterKnife.bind(this, root);
        return root;
    }

    public View initRootView(LayoutInflater inflater, ViewGroup container){
        return inflater.inflate(R.layout.fragment_host_base, container, false);
    }

    public void push(Fragment fragment){
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .add(R.id.fragment_container, fragment, KEY_LANDING_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public void pushReplace(Fragment fragment){
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_container, fragment, KEY_LANDING_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public void replace(Fragment fragment){
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, KEY_LANDING_FRAGMENT)
                .commit();
    }

    public boolean dismissPresentedFragment(){
        return pop();
    }

    public void presentFragment(Fragment fragment){
        int in = R.anim.slide_bottom_in;
        int out = 0;
        int popIn = 0;
        int popOut = R.anim.slide_bottom_out;

        replaceTopFragment(fragment, in, out, popIn, popOut);
    }

    public void fadeInTopFragment(Fragment fragment){
        int in = R.anim.fade_in;
        int out = 0;
        int popIn = R.anim.fade_out;
        int popOut = 0;

        replaceTopFragment(fragment, in, out, popIn, popOut);
    }

    public void replaceTopFragment(Fragment fragment, int inAnim, int outAnim, int popEnterAnim, int popExitAnim){
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(inAnim, outAnim, popEnterAnim, popExitAnim)
                .replace(R.id.top_fragment_container, fragment, KEY_PRESENTING_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public boolean pop(){
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            getChildFragmentManager().popBackStackImmediate();
            return true;
        }
        return false;
    }

    public boolean onBackPressed() {
        boolean hasHandle = false;
        Fragment currentFragment = getChildFragmentManager().findFragmentByTag(KEY_LANDING_FRAGMENT);
        if(currentFragment != null && currentFragment instanceof EzlyBaseHostFragment){
            hasHandle = ((EzlyBaseHostFragment)currentFragment).onBackPressed();
        }

        if(!hasHandle){
            hasHandle = pop();
        }

        if(!hasHandle){
            Fragment presentingFragment = getChildFragmentManager().findFragmentByTag(KEY_PRESENTING_FRAGMENT);
            if(presentingFragment != null){
                dismissPresentedFragment();
                hasHandle = true;
            }
        }

        return hasHandle;
    }

    public boolean dismissSelf() {
        boolean hasHandle = false;
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            hasHandle = ((EzlyBaseHostFragment)getParentFragment()).dismissPresentedFragment();
        }
        else if(this instanceof EzlyBaseHostFragment){
            hasHandle = dismissPresentedFragment();
        }

        return hasHandle;
    }
}
