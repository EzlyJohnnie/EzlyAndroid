package com.ezly.ezly_android.UI.event.List;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.EventListPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.EventListView;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailActivity;
import com.ezly.ezly_android.UI.login.EzlyLoginHostFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 8/10/16.
 */
public class EzlyEventListFragment extends EzlyBaseFragment implements EventListView, EzlyEventListAdapter.EventListListener {

    @Inject EventListPresenter presenter;
    @Inject MemberHelper memberHelper;
    @Inject LocationHelper locationHelper;
    @Inject EzlySearchParam searchParam;

    @BindView(R.id.refresh_layout)           SwipeRefreshLayout refreshLayout;
    @BindView(R.id.events_recycler_list)     RecyclerView eventsList;

    private EzlyEventListAdapter adapter;
    private boolean hasMore = true;
    private List<EzlyEvent> events;
    private boolean isProcessingFavourite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState);
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume touch event
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy(){
        adapter.setEvents(null);
        super.onDestroy();
    }

    private void init(Bundle savedInstanceState) {
        initializeInjector();
        presenter.setmView(this);
        initView();
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView() {
        initRecycleView();
    }

    private void initRecycleView() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                presenter.reloadEvent();
            }
        });

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        adapter = new EzlyEventListAdapter(null, memberHelper, locationHelper, searchParam);
        adapter.setListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventsList.setLayoutManager(layoutManager);
        eventsList.setAdapter(adapter);
        presenter.searchEvent(events == null ? 0 : events.size());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent. ReloadEventsListEvent event){
        if(isAdded()){
            presenter.reloadEvent();
        }
    }

    private void showLogin(){
        UIHelper.hideKeyBoard(getView());
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).presentFragment(EzlyLoginHostFragment.getInstance());
        }
    }

    private void updateFavouriteBtn(EzlyEvent event) {
        int position = events.indexOf(event);
        if(position >= 0){
            adapter.notifyItemChanged(position);
        }
    }

    ////////////////////////////////Onclick////////////////////////////////
    @Override
    public void onEventClicked(EzlyEvent event) {
        EzlyEventDetailActivity.startActivity(getActivity(), event);
    }

    @Override
    public void onScrollToBottom() {
        if (hasMore){
            presenter.searchEvent(events == null ? 0 : events.size());
        }
    }

    @Override
    public void onSaveEventClicked(EzlyEvent event, boolean toSave) {
        if(!memberHelper.hasLogin()){
            showLogin();
            return;
        }

        if(event.getUser().getId().equals(memberHelper.getCurrentUser().getId())){
            SingleToast.makeText(getContext(), getContext().getResources().getString(R.string.cannot_add_self_favourite), Toast.LENGTH_SHORT).show();
        }
        else if(!isProcessingFavourite){
            isProcessingFavourite = true;
            if(event.canBeFavourited()){
                presenter.addFavourite(event);
            }
            else{
                presenter.removeFavourite(event);
            }
        }
    }

    ////////////////////////////////View interface////////////////////////////////

    @Override
    public void onEventPrepared(List<EzlyEvent> events) {
        if(this.events == null){
            this.events = events;
        }
        else{
            this.events.addAll(events);
        }

        adapter.setEvents(this.events);
        showRefreshLayout(false);
        if(events == null || events.size() < EventListPresenter.TOP){
            hasMore = false;
        }
    }

    @Override
    public void showRefreshLayout(boolean isShow) {
        refreshLayout.setRefreshing(isShow);
    }

    @Override
    public void resetSearch() {
        hasMore = true;
        events = null;
    }

    @Override
    public void onAddFavourite(EzlyEvent event, boolean isSuccess) {
        isProcessingFavourite = false;
        if(isSuccess){
            event.setCanFavourite(false);
            updateFavouriteBtn(event);
        }
        else{
            UIHelper.showConnectionError(getContext());
        }
    }

    @Override
    public void onRemoveFavourite(EzlyEvent event, boolean isSuccess) {
        isProcessingFavourite = false;
        if(isSuccess){
            event.setCanFavourite(true);
            updateFavouriteBtn(event);
        }
        else{
            UIHelper.showConnectionError(getContext());
        }
    }
}
