package com.ezly.ezly_android.UI.User;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.UIHelper.JCSlidableCellRecyclerViewTouchListener;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Presenter.UserPostPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.Post.EzlyNewPostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.UserPostView;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailActivity;
import com.ezly.ezly_android.Utils.TextUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 6/11/16.
 */

public class EzlyUserPostFragment extends EzlyBaseFragment implements UserPostView,
        UserEventListAdapter.UserEventClickedListener,
        EzlyNewPostFragment.PostEventListener,
        JCSlidableCellRecyclerViewTouchListener.SlidableCellTouchListenerDelegate
{
    public static final String KEY_HAS_TOOL_BAR = "key_hasToolBar";
    public static final String KEY_HAS_SEARCH_BAR = "key_hasSearchBar";
    public static final String KEY_VIEW_TYPE = "key_viewType";
    public static final String KEY_EVENT_LIST = "key_eventList";
    public static final String KEY_IS_FROM_MY_INFO = "key_isFromMyInfo";

    public static final int VIEW_TYPE_FAVOURITE = 0;
    public static final int VIEW_TYPE_POST_HISTORY = 1;


    @BindView(R.id.top_bar) View toolBar;
    @BindView(R.id.txt_title) TextView txtTitle;

    @BindView(R.id.post_list) RecyclerView postList;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;

    private int eventItemBottomViewWidth;

    @Inject UserPostPresenter presenter;

    private UserEventListAdapter postListAdapter;
    private ArrayList<EzlyEvent> events;
    private boolean hasToolBar;
    private boolean hasSearchBar;
    private String userID;
    private int viewType;
    private boolean isFromMyInfo;
    private JCSlidableCellRecyclerViewTouchListener recyclerViewItemTouchListener;

    public static EzlyUserPostFragment getInstance(String userID, boolean hasToolBar, boolean isFromMyInfo, int viewType){
        EzlyUserPostFragment fragment = new EzlyUserPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EzlyUserHostFragment.KEY_USER_ID, userID);
        bundle.putBoolean(KEY_HAS_TOOL_BAR, hasToolBar);
        bundle.putInt(KEY_VIEW_TYPE, viewType);
        bundle.putBoolean(KEY_IS_FROM_MY_INFO, isFromMyInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_post_list, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EzlyUserHostFragment.KEY_USER_ID, userID);
        outState.putBoolean(KEY_HAS_TOOL_BAR, hasToolBar);
        outState.putBoolean(KEY_HAS_SEARCH_BAR, hasSearchBar);
        outState.putInt(KEY_VIEW_TYPE, viewType);
        outState.putParcelableArrayList(KEY_EVENT_LIST, events);
        outState.putBoolean(KEY_IS_FROM_MY_INFO, isFromMyInfo);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(recyclerViewItemTouchListener != null) {
            recyclerViewItemTouchListener.purge();
            recyclerViewItemTouchListener = null;
        }
    }

    private void init(Bundle savedInstanceState, View root) {
        if(savedInstanceState == null) {
            userID = getArguments().getString(EzlyUserHostFragment.KEY_USER_ID);
            hasToolBar = getArguments().getBoolean(KEY_HAS_TOOL_BAR);
            hasSearchBar = getArguments().getBoolean(KEY_HAS_SEARCH_BAR);
            viewType = getArguments().getInt(KEY_VIEW_TYPE);
            isFromMyInfo = getArguments().getBoolean(KEY_IS_FROM_MY_INFO);
        }
        else{
            userID = savedInstanceState.getString(EzlyUserHostFragment.KEY_USER_ID);
            hasToolBar = savedInstanceState.getBoolean(KEY_HAS_TOOL_BAR);
            hasSearchBar = savedInstanceState.getBoolean(KEY_HAS_SEARCH_BAR);
            viewType = savedInstanceState.getInt(KEY_VIEW_TYPE);
            events = savedInstanceState.getParcelableArrayList(KEY_EVENT_LIST);
            isFromMyInfo = savedInstanceState.getBoolean(KEY_IS_FROM_MY_INFO);
        }

        initializeInjector();
        presenter.setView(this);
        initView(root);

        loadData();
    }

    private void loadData(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                showRefreshLayout(true);
            }
        });
        if(viewType == VIEW_TYPE_POST_HISTORY){
            eventItemBottomViewWidth = UIHelper.dip2px(getContext(), 120 + 30);
            if(!TextUtils.isEmpty(userID)){
                presenter.getPostHistory(userID);
            }
            else{
                presenter.getMyPostHistory();
            }

        }
        else if(viewType == VIEW_TYPE_FAVOURITE){
            eventItemBottomViewWidth = UIHelper.dip2px(getContext(), 40 + 30);
            eventItemBottomViewWidth = 0;
            presenter.getFavourites();
        }
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView(View root) {
        toolBar.setVisibility(hasToolBar ? View.VISIBLE : View.GONE);
        int titleRes = R.string.my_post_history;
        if(viewType == VIEW_TYPE_FAVOURITE){
            titleRes = R.string.my_favourite;
        }
        txtTitle.setText(getContext().getResources().getString(titleRes));

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });

//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadData();
//            }
//        });
        refreshLayout.setEnabled(false);

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postList.setLayoutManager(layoutManager);

        postListAdapter = new UserEventListAdapter(events, viewType);
        postListAdapter.setListener(this);
        postList.setAdapter(postListAdapter);
        recyclerViewItemTouchListener = new JCSlidableCellRecyclerViewTouchListener(this);
        postList.addOnItemTouchListener(recyclerViewItemTouchListener);
    }

    @OnClick(R.id.btn_reload)
    public void onReloadClicked(){
        loadData();
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).dismissSelf();
        }
        else if(getActivity() instanceof EzlyBaseActivity){
            ((EzlyBaseActivity)getActivity()).pop();
        }
    }

    @Override
    public void showRefreshLayout(boolean isShow) {
        refreshLayout.setRefreshing(isShow);
    }

    @Override
    public void onRemoveFavourite(boolean isSuccess, int position) {
        if(!isSuccess) {

        }
    }

    @Override
    public void onSetEventVisibilitySuccess() {

    }

    @Override
    public void onEventPrepared(ArrayList<EzlyEvent> events) {
        refreshLayout.setRefreshing(false);
        this.events = events;
        postListAdapter.setEvents(events);
    }

    @Override
    public void onEventClicked(EzlyEvent event) {
        recyclerViewItemTouchListener.resetPreCellIfNeed();
        EzlyEventDetailActivity.startActivity(getActivity(), event);
    }

    @Override
    public void onVisibleClicked(EzlyEvent event) {
        recyclerViewItemTouchListener.resetPreCellIfNeed();
        if(event.isActive()){
            presenter.setEventInvisible(event);
        }
        else{
            presenter.setEventVisible(event);
        }
        event.setActive(!event.isActive());
        postListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClicked(EzlyEvent event) {
        recyclerViewItemTouchListener.resetPreCellIfNeed();
        if(getActivity() instanceof MainActivity){
            EzlyNewPostFragment fragment = EzlyNewPostFragment.getInstance(event, EzlyNewPostFragment.VIEW_TYPE_UPDATE_EVENT);
            fragment.setListener(this);
            ((MainActivity)getActivity()).push(fragment);
        }
    }

    @Override
    public void onDeleteClicked(final int position) {
        recyclerViewItemTouchListener.resetPreCellIfNeed();
        final EzlyEvent event = events.get(position);
        String message = String.format(getContext().getResources().getString(R.string.delete_posting_confirm), event.getTitle());
        UIHelper.displayYesNoDialog(getContext(), message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                View view = postList.getLayoutManager().findViewByPosition(position);

                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_out);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        presenter.deleteEvent(event);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        events.remove(position);
                        getView().post(new Runnable() {
                            @Override
                            public void run() {
                                initRecyclerView();
                            }
                        });

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(anim);
            }
        });
    }

    @Override
    public void onUnfavouriteClicked(final int position) {
        View view = postList.getLayoutManager().findViewByPosition(position);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                presenter.removeFavourite(events.get(position), position);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                events.remove(position);
                postListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }


    @Override
    public void reloadPreviousView() {
        loadData();
    }


    ////////////////////////////////////SlidableCellTouchListenerDelegate////////////////////////////////////
    @Override
    public int getCellContentViewLayoutRes() {
        return R.id.content_view;
    }

    @Override
    public int getCellBottomMenuWidth() {
        return UIHelper.dip2px(getContext(), 145);
    }

    @Override
    public boolean allowSlideCell(int position) {
        return true;
    }
}
