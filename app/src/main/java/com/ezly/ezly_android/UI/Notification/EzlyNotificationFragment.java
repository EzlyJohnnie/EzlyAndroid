package com.ezly.ezly_android.UI.Notification;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.NotificationHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlyNotification;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.LoginPresenter;
import com.ezly.ezly_android.Presenter.NotificationPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.ViewInterFace.LoginView;
import com.ezly.ezly_android.UI.ViewInterFace.NotificationView;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailActivity;
import com.ezly.ezly_android.Utils.TextUtils;
import com.facebook.login.LoginManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 12/03/17.
 */

public class EzlyNotificationFragment extends EzlyBaseFragment implements NotificationView,
        LoginView,
        EzlyNotificationAdapter.NotificationListListener
{
    private static final String KEY_HAS_BACK_BTN = "key_hasBackBtn";

    @Inject NotificationPresenter notificationPresenter;
    @Inject LoginPresenter loginPresenter;
    @Inject NotificationHelper notificationHelper;

    @Inject MemberHelper memberHelper;

    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.notification_list) RecyclerView notificationList;
    @BindView(R.id.btn_back) View btnBack;

    private EzlyNotificationAdapter adapter;
    private ArrayList<EzlyNotification> notifications;
    private boolean hasMore = true;
    private boolean hasBackBtn;

    public static EzlyNotificationFragment getInstance(boolean hasBackBtn) {
        EzlyNotificationFragment fragment = new EzlyNotificationFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_HAS_BACK_BTN, hasBackBtn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification_list, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        loadNotifications();
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
        super.onDestroy();
        memberHelper.reset();
    }

    private void init(Bundle savedInstanceState, View root) {
        initDate(savedInstanceState);
        initializeInjector();
        notificationPresenter.setView(this);
        loginPresenter.setView(this);
        initView(root);
    }

    private void initDate(Bundle savedInstanceState) {
        if(savedInstanceState == null && getArguments() != null){
            hasBackBtn = getArguments().getBoolean(KEY_HAS_BACK_BTN);
        }
        else{
            hasBackBtn = savedInstanceState.getBoolean(KEY_HAS_BACK_BTN);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_HAS_BACK_BTN, hasBackBtn);
        super.onSaveInstanceState(outState);
    }


    private void loadNotifications() {
        if(memberHelper.hasLogin()){
            notifications = new ArrayList<>();
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            notificationPresenter.loadNotifications(notifications == null ? 0 : notifications.size());
        }
        else{
            refreshLayout.setRefreshing(false);
        }
    }

    private void initView(View root) {
        btnBack.setVisibility(hasBackBtn ? View.VISIBLE : View.GONE);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                loadNotifications();
            }
        });

        adapter = new EzlyNotificationAdapter(null, memberHelper, notificationHelper);
        adapter.setListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        notificationList.setLayoutManager(layoutManager);
        notificationList.setAdapter(adapter);
    }


    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnLogoutEvent event){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnLoginEvent event){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnReceivePushNotificationEvent event){
        loadNotifications();
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        getActivity().onBackPressed();
    }


    /////////////////////////////presenter listener/////////////////////////////////

    @Override
    public void onNotificationsPrepared(ArrayList<EzlyNotification> notifications) {
        refreshLayout.setRefreshing(false);
        if(this.notifications == null){
            this.notifications = notifications;
        }
        else if(notifications != null){
            this.notifications.addAll(notifications);
        }

        adapter.setNotifications(this.notifications);
        if(notifications == null || notifications.size() < NotificationPresenter.TOP){
            hasMore = false;
        }

    }

    @Override
    public void onNotificationsLoadFailed(String errorMsg) {
        refreshLayout.setRefreshing(false);
        if(TextUtils.isEmpty(errorMsg)){
            errorMsg = getContext().getResources().getString(R.string.load_notification_failed);
        }

        SingleToast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    @Override
    public EzlyBaseActivity getParentActivity() {
        return (EzlyBaseActivity)getActivity();
    }

    @Override
    public void onLogin(EzlyUser currentUser) {
        if(currentUser != null){
            loadNotifications();
        }
        else{
            onLoginFailed("");
        }
    }

    @Override
    public void onLoginFailed(final String errorMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String errorStr = errorMsg;
                if(TextUtils.isEmpty(errorMsg)){
                    errorStr = getContext().getResources().getString(R.string.login_failed);
                }
                SingleToast.makeText(getContext(), errorStr, Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onLoginCancelled() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }



    /////////////////////////////notification adapter listener/////////////////////////////////

    @Override
    public void onNotificationClicked(EzlyNotification notification) {
        notificationHelper.setNotificationRead(getContext(), notification.getId());
        if(notification.isJob()){
            EzlyJob event = new EzlyJob();
            event.setId(notification.getRefId());
            event.setTitle(notification.getDescription());
            EzlyEventDetailActivity.startActivity(getActivity(), event);
        }
        else if(notification.isService()){
            EzlyService event = new EzlyService();
            event.setId(notification.getRefId());
            event.setTitle(notification.getDescription());
            EzlyEventDetailActivity.startActivity(getActivity(), event);
        }
    }

    @Override
    public void loginWithWeChat() {
        loginPresenter.loginWithWeChat();
    }

    @Override
    public void loginWithFB() {
        loginPresenter.initFB();
        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
    }

    @Override
    public void loginWithGmail() {
        loginPresenter.loginWithGmail();
    }

    @Override
    public void onScrollToBottom() {
        if (hasMore){
            notificationPresenter.loadNotifications(notifications == null ? 0 : notifications.size());
        }
    }
}
