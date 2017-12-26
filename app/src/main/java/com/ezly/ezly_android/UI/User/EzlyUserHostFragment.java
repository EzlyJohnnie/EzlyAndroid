package com.ezly.ezly_android.UI.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.search.EzlySearchFragment;
import com.ezly.ezly_android.UI.search.EzlySearchHostFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by Johnnie on 6/11/16.
 */

public class EzlyUserHostFragment extends EzlyBaseHostFragment {
    public static final String KEY_USER_ID = "key_userID";
    public static final String KEY_IS_USER_LIST = "key_isUserList";

    @Inject EzlySearchParam searchParam;
    private String landingUserID;
    private boolean isUserList;

    public static EzlyUserHostFragment getMyInfoInstance(){
        EzlyUserHostFragment fragment = new EzlyUserHostFragment();
        return fragment;
    }

    public static EzlyUserHostFragment getUserDetailInstance(String userID){
        EzlyUserHostFragment fragment = new EzlyUserHostFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_USER_ID, userID);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static EzlyUserHostFragment getUserListInstance(){
        EzlyUserHostFragment fragment = new EzlyUserHostFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_USER_LIST, true);
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
        initializeInjector();
        if(getArguments() != null){
            landingUserID = getArguments().getString(KEY_USER_ID);
            isUserList = getArguments().getBoolean(KEY_IS_USER_LIST, false);
        }

        if(isUserList){
            //show user list
            replace(EzlyUserListFragment.getInstance());
        }
        else{
            if(landingUserID == null){
                //show my info
                replace(EzlyMyProfileFragment.getMyInfoInstance());
            }
            else{
                //show user detail
                replace(EzlyUserDetailFragment.getInstance(landingUserID));
            }
        }
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    public void showSearchFragment() {
        EzlySearchFragment fragment = EzlySearchFragment.newInstance(EzlySearchHostFragment.SEARCH_VIEW_TYPE_NORMAL);
        fragment.setOnSearchListener(new EzlySearchFragment.OnSearchListener() {
            @Override
            public void shouldReload() {
                if(searchParam.getSearchMode() == EzlySearchParam.SEARCH_MODE_USER){
                    EventBus.getDefault().post(new ResultEvent.ReloadUserEvent());
                }
                else{
                    if(getActivity() instanceof MainActivity){
                        ((MainActivity)getActivity()).setLandingFragment();
                    }
                }
            }
        });
        fadeInTopFragment(fragment);
    }
}
