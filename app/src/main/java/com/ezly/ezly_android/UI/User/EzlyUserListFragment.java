package com.ezly.ezly_android.UI.User;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.UserListPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.UserListView;
import com.ezly.ezly_android.Utils.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 13/11/16.
 */

public class EzlyUserListFragment extends EzlyBaseFragment implements UserListView, UserListAdapter.UserListListener {
    public static final int VIEW_TYPE_STANDARD    = 0;
    public static final int VIEW_TYPE_WHO_LIKE_ME = 1;
    public static final int VIEW_TYPE_I_LIKE_WHOM = 2;

    public static final String KEY_VIEW_TYPE = "key_viewType";
    public static final String KEY_HAS_TOOL_BAR = "key_hasToolBar";
    public static final String KEY_HAS_SEARCH_BAR = "key_hasSearchBar";
    public static final String KEY_USER_LIST = "key_userList";

    @BindView(R.id.top_bar )       View toolBar;
    @BindView(R.id.search_bar)     View searchBar;
    @BindView(R.id.txt_search)     TextView txtSearch;

    @BindView(R.id.user_list)      RecyclerView userList;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;


    @Inject UserListPresenter presenter;
    @Inject EzlySearchParam searchParam;

    private int viewType;
    private UserListAdapter userListAdapter;
    private ArrayList<EzlyUser> users;
    private boolean hasToolBar;
    private boolean hasSearchBar;
    private String userID;
    private boolean hasMore = true;

    /**
     * show search user list result
     * @return
     */
    public static EzlyUserListFragment getInstance(){
        EzlyUserListFragment fragment = new EzlyUserListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_HAS_TOOL_BAR, false);
        bundle.putBoolean(KEY_HAS_SEARCH_BAR, true);
        bundle.putInt(KEY_VIEW_TYPE, VIEW_TYPE_STANDARD);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * show user list for liked user for user detail
     * @param userID
     * @param hasToolBar
     * @param hasSearchBar
     * @return
     */
    public static EzlyUserListFragment getInstance(String userID, boolean hasToolBar, boolean hasSearchBar, int viewType){
        EzlyUserListFragment fragment = new EzlyUserListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EzlyUserHostFragment.KEY_USER_ID, userID);
        bundle.putBoolean(KEY_HAS_TOOL_BAR, hasToolBar);
        bundle.putBoolean(KEY_HAS_SEARCH_BAR, hasSearchBar);
        bundle.putInt(KEY_VIEW_TYPE, viewType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EzlyUserHostFragment.KEY_USER_ID, userID);
        outState.putBoolean(KEY_HAS_TOOL_BAR, hasToolBar);
        outState.putBoolean(KEY_HAS_SEARCH_BAR, hasSearchBar);
        outState.putParcelableArrayList(KEY_USER_LIST, users);
        outState.putInt(KEY_VIEW_TYPE, viewType);
    }


    private void init(Bundle savedInstanceState, View root) {
        if(savedInstanceState == null) {
            userID = getArguments().getString(EzlyUserHostFragment.KEY_USER_ID);
            hasToolBar = getArguments().getBoolean(KEY_HAS_TOOL_BAR);
            hasSearchBar = getArguments().getBoolean(KEY_HAS_SEARCH_BAR);
            viewType = getArguments().getInt(KEY_VIEW_TYPE);
        }
        else{
            userID = savedInstanceState.getString(EzlyUserHostFragment.KEY_USER_ID);
            hasToolBar = savedInstanceState.getBoolean(KEY_HAS_TOOL_BAR);
            hasSearchBar = savedInstanceState.getBoolean(KEY_HAS_SEARCH_BAR);
            users = savedInstanceState.getParcelableArrayList(KEY_USER_LIST);
            viewType = savedInstanceState.getInt(KEY_VIEW_TYPE);
        }

        initializeInjector();
        presenter.setView(this);
        initView(root);
        loadUserList();
    }

    private void reload(){
        users = null;
        hasMore = true;
        loadUserList();
    }

    private void loadUserList(){
        switch (viewType){
            case VIEW_TYPE_STANDARD:
                presenter.searchUser();
                break;
            case VIEW_TYPE_I_LIKE_WHOM:
                refreshLayout.setEnabled(false);
                if(TextUtils.isEmpty(userID)){
                    presenter.getMyLikedUser(users == null ? 0 : users.size());
                }
                else{
                    presenter.getILikedUser(userID, users == null ? 0 : users.size());
                }
                break;
            case VIEW_TYPE_WHO_LIKE_ME:
                refreshLayout.setEnabled(false);
                if(TextUtils.isEmpty(userID)){
                    presenter.getUserLikedMe(userID);
                }
                else{
                    presenter.getUserLikedMe();
                }
                break;
        }
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView(View root) {
        setSearchText();
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
        refreshLayout.setEnabled(hasSearchBar);

        setUserListLayout();

        userListAdapter = new UserListAdapter(null);
        userListAdapter.setListener(this);
        userList.setAdapter(userListAdapter);

        toolBar.setVisibility(hasToolBar ? View.VISIBLE : View.GONE);
        searchBar.setVisibility(hasSearchBar ? View.VISIBLE : View.GONE);
    }

    private void setSearchText(){
        String searchStr = searchParam.getSearchStr();
        if(searchStr != null){
            txtSearch.setText(searchParam.getSearchStr());
        }
    }

    private void setUserListLayout(){
        int noOfColumns = 1;
        if(users != null && users.size() > 1){
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            noOfColumns = (int) (dpWidth / 160);
            if(noOfColumns > users.size()){
                noOfColumns = users.size();
            }
        }
        GridLayoutManager lLayout = new GridLayoutManager(getContext(), noOfColumns);
        userList.setHasFixedSize(true);
        userList.setLayoutManager(lLayout);
    }


    @OnClick(R.id.btn_reload)
    public void onReloadClicked(){
        reload();
    }

    @OnClick(R.id.search_box)
    public void onSearchClicked(){
        if(getParentFragment() instanceof EzlyUserHostFragment){
            ((EzlyUserHostFragment)getParentFragment()).showSearchFragment();
        }
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

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.ReloadUserEvent event){
        if(isAdded()){
            presenter.searchUser();
            setSearchText();
        }
    }

    @Override
    public void onUserClicked(EzlyUser user) {
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).push(EzlyUserHostFragment.getUserDetailInstance(user.getId()));
        }
    }

    @Override
    public void onScrollToBottom() {
        if(hasMore){
            loadUserList();
        }
    }

    @Override
    public void onUserPrepared(ArrayList<EzlyUser> users) {
        showRefreshLayout(false);
        if(this.users == null){
            this.users = users;
        }
        else{
            this.users.addAll(users);
        }

        if(users == null || users.size() < UserListPresenter.TOP){
            hasMore = false;
        }
        setUserListLayout();
        userListAdapter.setUser(this.users);
    }


    @Override
    public void showRefreshLayout(boolean isShow) {
        refreshLayout.setRefreshing(isShow);
    }
}
