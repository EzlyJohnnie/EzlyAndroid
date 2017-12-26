package com.ezly.ezly_android.UI.event.FullDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

/**
 * Created by Johnnie on 30/10/16.
 */

public class EzlyEventDetailFragmentHost extends EzlyBaseHostFragment {
    private static final String KEY_EVENT = "key_event";

    private EzlyEvent event;

    public static EzlyEventDetailFragmentHost getInstance(EzlyEvent event){
        EzlyEventDetailFragmentHost fragment = new EzlyEventDetailFragmentHost();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_EVENT, event);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View initRootView(LayoutInflater inflater, ViewGroup container){
        return inflater.inflate(R.layout.fragment_host_with_loading_view, container, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init();
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
        showTabbar(true);
    }

//    @Override
//    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
//
//        anim.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {}
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                init();
//            }
//        });
//
//        return anim;
//    }

    private void init() {
        event = getArguments().getParcelable(KEY_EVENT);
        replace(EzlyEventDetailFragment.getInstance(event));
    }
}
