package com.ezly.ezly_android.UI.User;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Presenter.UserDetailPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.ViewInterFace.UserDetailView;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailActivity;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailFragment;
import com.ezly.ezly_android.UI.login.EzlyLoginHostFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 14/11/16.
 */

public class EzlyUserDetailFragment extends EzlyBaseFragment implements UserDetailView, OtherUserPostAdapter.OtherUserItemListener{
    private static final String KEY_EVENTS = "key_events";
    private static final String KEY_USER = "key_user";

    @Inject UserDetailPresenter presenter;

    @Inject MemberHelper memberHelper;

    @BindView(R.id.post_list) RecyclerView postList;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    private ArrayList<EzlyEvent> events;
    private EzlyUser user;

    private String userID;
    private OtherUserPostAdapter otherUserPostAdapter;

    public static EzlyUserDetailFragment getInstance(String userID){
        EzlyUserDetailFragment fragment = new EzlyUserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EzlyUserHostFragment.KEY_USER_ID, userID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_detail, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        showTabbar(false);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        showTabbar(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_EVENTS, events);
        outState.putParcelable(KEY_USER, user);
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState, View root) {
        if(savedInstanceState != null){
            user = savedInstanceState.getParcelable(KEY_USER);
            events = savedInstanceState.getParcelableArrayList(KEY_EVENTS);
        }
        else if(getArguments() != null) {
            userID = getArguments().getString(EzlyUserHostFragment.KEY_USER_ID);
        }
        initializeInjector();
        initView(root);
        presenter.setView(this);

        if(savedInstanceState == null){
            presenter.getUser(userID);
            presenter.getPostHistory(userID);
        }
        else{
            onUserPrepared(user);
            onEventPrepared(events);
        }
    }

    private void initView(View root) {
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getUser(userID);
                presenter.getPostHistory(userID);
            }
        });

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postList.setLayoutManager(layoutManager);

        otherUserPostAdapter = new OtherUserPostAdapter(null, null);
        otherUserPostAdapter.setListener(this);
        postList.setAdapter(otherUserPostAdapter);
    }


    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void showLogin(){
        UIHelper.hideKeyBoard(getView());
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).presentFragment(EzlyLoginHostFragment.getInstance());
        }
    }

    @Override
    public void onUserPrepared(EzlyUser user) {
        refreshLayout.setRefreshing(false);
        this.user = user;
        otherUserPostAdapter.setUser(user);
    }

    @Override
    public void onEventPrepared(ArrayList<EzlyEvent> events) {
        refreshLayout.setRefreshing(false);
        this.events = events;
        otherUserPostAdapter.setEvents(events);
    }

    @Override
    public void onLikeUser(boolean isSuccess) {}


    @OnClick(R.id.btn_back)
    public void back(){
        boolean hasHandle = false;
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            hasHandle = ((EzlyBaseHostFragment)getParentFragment()).pop();
        }

        if(!hasHandle && getParentFragment().getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment().getParentFragment()).pop();
        }
    }

    @Override
    public void onLikeClicked() {
        if(!memberHelper.hasLogin()){
            showLogin();
            return;
        }

        if(user.isCanLike()){
            user.setCanLike(false);
            presenter.likeUser(user.getId());
            user.setNumOfLikes(user.getNumOfLikes() + 1);
        }
        else{
            user.setCanLike(true);
            presenter.dislikeUser(user.getId());
            user.setNumOfLikes(user.getNumOfLikes() - 1);
        }


        if(((LinearLayoutManager)postList.getLayoutManager()).findFirstVisibleItemPosition() == 0){
            otherUserPostAdapter.setUser(user);
        }
    }

    @Override
    public void onEventClicked(EzlyEvent event) {
        if(getActivity() instanceof MainActivity){
            EzlyEventDetailActivity.startActivity(getActivity(), event);
        }
        else if(getActivity() instanceof EzlyEventDetailActivity){
            ((EzlyBaseHostFragment)getParentFragment()).pushReplace(EzlyEventDetailFragment.getInstance(event));
        }
    }
}
