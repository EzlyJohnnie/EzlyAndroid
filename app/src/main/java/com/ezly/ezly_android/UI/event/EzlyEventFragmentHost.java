package com.ezly.ezly_android.UI.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.event.Map.EzlyMapMainFragment;
import com.ezly.ezly_android.UI.search.EzlySearchFragment;
import com.ezly.ezly_android.UI.search.EzlySearchHostFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Johnnie on 2/10/16.
 */

public class EzlyEventFragmentHost extends EzlyBaseHostFragment {
    private static final String KEY_EVENT_LIST = "key_eventList";

    @Inject EzlySearchParam searchParam;
    private List<EzlyEvent> events;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            events = savedInstanceState.getParcelableArrayList(KEY_EVENT_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        init();
        return root;
    }

    @Override
    public void onDestroy(){
        events = null;
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if(this.events != null){
            ArrayList<EzlyEvent> events = new ArrayList<>();
            events.addAll(this.events);
            outState.putParcelableArrayList(KEY_EVENT_LIST, events);
        }

    }

    private void init() {
        initializeInjector();
        replace(new EzlyMapMainFragment());
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    public List<EzlyEvent> getEvents() {
        return events;
    }

    public void setEvents(List<EzlyEvent> events) {
        this.events = events;
    }

    public void showSearchFragment() {
        EzlySearchFragment fragment = EzlySearchFragment.newInstance(EzlySearchHostFragment.SEARCH_VIEW_TYPE_NORMAL);
        fragment.setOnSearchListener(new EzlySearchFragment.OnSearchListener() {
            @Override
            public void shouldReload() {
                reload();
            }
        });
        fadeInTopFragment(fragment);
    }

    public void reload() {
        if(searchParam.getSearchMode() == EzlySearchParam.SEARCH_MODE_USER){
            if(getActivity() instanceof MainActivity){
                ((MainActivity)getActivity()).setLandingFragment();
            }
        }
        else{
            events = null;
            EventBus.getDefault().post(new ResultEvent.ReloadMapEvent());
        }
    }
}
