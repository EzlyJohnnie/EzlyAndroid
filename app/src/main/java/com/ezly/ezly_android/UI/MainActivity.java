package com.ezly.ezly_android.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.NotificationHelper;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlyNotification;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.MainActivityPresenter;
import com.ezly.ezly_android.Presenter.NotificationPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.Notification.EzlyNotificationHostFragment;
import com.ezly.ezly_android.UI.Post.EzlyPostHostFragment;
import com.ezly.ezly_android.UI.User.EzlyUserHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.MainActivityView;
import com.ezly.ezly_android.UI.ViewInterFace.NotificationView;
import com.ezly.ezly_android.UI.event.EzlyEventFragmentHost;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailActivity;
import com.ezly.ezly_android.UI.login.EzlyLoginHostFragment;
import com.ezly.ezly_android.Utils.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends EzlyBaseActivity implements MainActivityView,
        View.OnClickListener,
        NotificationView
{

    private static final String KEY_FRAGMENT_EVENT = "key_fragmentEvent";
    private static final String KEY_FRAGMENT_MY_PROFILE = "key_fragmentMyProfile";
    private static final String KEY_FRAGMENT_NOTIFICATION = "key_fragmentNotification";

    public static final String KEY_REFRESH = "Key_refresh";

    @Inject MainActivityPresenter presenter;
    @Inject NotificationPresenter notificationPresenter;
    @Inject MemberHelper memberHelper;
    @Inject EzlySearchParam searchParam;
    @Inject NotificationHelper notificationHelper;

    @BindView(R.id.tab_bar) View tabbar;

    @BindView(R.id.btn_job) View btnJob;
    @BindView(R.id.iv_job) ImageView ivJob;
    @BindView(R.id.txt_job) TextView txtJob;


    @BindView(R.id.btn_service) View btnService;
    @BindView(R.id.iv_service) ImageView ivService;
    @BindView(R.id.txt_service) TextView txtService;

    @BindView(R.id.btn_post) View btnPost;
    @BindView(R.id.iv_post) ImageView ivPost;
    @BindView(R.id.txt_post) TextView txtPost;

    @BindView(R.id.btn_my_profile) View btnMyProfile;
    @BindView(R.id.iv_my_profile) ImageView ivMyProfile;
    @BindView(R.id.txt_profile) TextView txtProfile;

    @BindView(R.id.btn_notification) View btnNotification;
    @BindView(R.id.iv_notification) ImageView ivNotification;
    @BindView(R.id.txt_notification) TextView txtNotification;
    @BindView(R.id.new_message_indicator) View newMessageIndicator;

//    private Fragment eventFragment;
//    private Fragment myProfileFragment;
//    private Fragment notificationFragment;

    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onResume(){
        super.onResume();
        notificationPresenter.loadNotifications(0);
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


    private void init() {
        getActivityComponent().inject(this);
        presenter.setView(this);
        presenter.initSearchParam();
        notificationPresenter.setView(this);
        setLandingFragment();
        initTabBar();
    }

    private void initTabBar() {
        btnJob.setOnClickListener(this);
        btnService.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        btnMyProfile.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
    }

    public void setLandingFragment() {
        if(searchParam.getSearchMode() == EzlySearchParam.SEARCH_MODE_USER){
            replace(EzlyUserHostFragment.getUserListInstance());
        }
        else{
            searchParam.setSearchMode(EzlySearchParam.SEARCH_MODE_JOB);
            currentFragmentTag = KEY_FRAGMENT_EVENT;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.app_fragment_container, new EzlyEventFragmentHost(), KEY_FRAGMENT_EVENT)
                    .commit();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        clearAllTabFragments();
        if(intent.getBooleanExtra(KEY_REFRESH, false)){
            setLandingFragment();
            resetToolbar();
        }
        else if(!TextUtils.isEmpty(intent.getStringExtra(getResources().getString(R.string.newest_push_message)))){
            setLandingFragment();
            processNotification(intent);
        }
    }

    private void processNotification(Intent intent) {
        String json = intent.getStringExtra(getResources().getString(R.string.newest_push_message));
        EzlyNotification notification = EzlyNotification.fromJson(json);
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

    private void resetToolbar() {
        txtJob.setText(getResources().getString(R.string.tab_job));
        txtService.setText(getResources().getString(R.string.tab_service));
        txtPost.setText(getResources().getString(R.string.post));
        txtNotification.setText(getResources().getString(R.string.message));
        txtProfile.setText(getResources().getString(R.string.tab_profile));

        updateTabBar(R.id.btn_job);
    }

    private void clearAllTabFragments() {
        Fragment eventFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_EVENT);
        Fragment myProfileFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_MY_PROFILE);
        Fragment notificationFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_NOTIFICATION);
        FragmentTransaction transition = getSupportFragmentManager().beginTransaction();

        if(eventFragment != null){
            transition.remove(eventFragment);
        }

        if(myProfileFragment != null){
            transition.remove(myProfileFragment);
        }

        if(notificationFragment != null){
            transition.remove(notificationFragment);
        }

        transition.commit();
    }

    @Override
    public EzlyBaseActivity getActivity() {
        return this;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_job:
                onJobTabClicked();
                break;

            case R.id.btn_service:
                onServiceTabClicked();
                break;

            case R.id.btn_post:
                onPostTabClicked();
                break;

            case R.id.btn_my_profile:
                onMyProfileTabClicked();
                break;

            case R.id.btn_notification:
                onNotificationTabClicked();
                break;
        }
    }

    private void onNotificationTabClicked() {
        newMessageIndicator.setVisibility(View.GONE);
        hideAllFragment();
        updateTabBar(R.id.btn_notification);
        currentFragmentTag = KEY_FRAGMENT_NOTIFICATION;
        if(getActivity() instanceof MainActivity){
            Fragment notificationFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_NOTIFICATION);
            if(notificationFragment == null){
                notificationFragment = EzlyNotificationHostFragment.getInstance(false);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.app_fragment_container, notificationFragment, KEY_FRAGMENT_NOTIFICATION)
                        .commit();
            }
            else{
                getSupportFragmentManager().beginTransaction()
                        .show(notificationFragment)
                        .commit();
            }
        }
    }

    private void onMyProfileTabClicked() {
        hideAllFragment();
        updateTabBar(R.id.btn_my_profile);
        currentFragmentTag = KEY_FRAGMENT_MY_PROFILE;
        if(getActivity() instanceof MainActivity){
            Fragment myProfileFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_MY_PROFILE);
            if(myProfileFragment == null){
                myProfileFragment = EzlyUserHostFragment.getMyInfoInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.app_fragment_container, myProfileFragment, KEY_FRAGMENT_MY_PROFILE)
                        .commit();
            }
            else{
                getSupportFragmentManager().beginTransaction()
                        .show(myProfileFragment)
                        .commit();
            }
        }
    }

    private void onPostTabClicked() {
        currentFragmentTag = KEY_PRESENTING_FRAGMENT;
        if(memberHelper.hasLogin()){
            presentFragment(EzlyPostHostFragment.getInstance());
        }
        else{
            presentFragment(EzlyLoginHostFragment.getInstance());
        }
    }

    private void onServiceTabClicked() {
        dismissPresentedFragment();
        hideAllFragment();
        currentFragmentTag = KEY_FRAGMENT_EVENT;
        updateTabBar(R.id.btn_service);
        searchParam.setSearchMode(EzlySearchParam.SEARCH_MODE_SERVICE);
        Fragment eventFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_EVENT);
        if(eventFragment == null){
            eventFragment = new EzlyEventFragmentHost();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.app_fragment_container, eventFragment, KEY_FRAGMENT_EVENT)
                    .commit();
        }
        else{
            EzlyEventFragmentHost hostFragment = (EzlyEventFragmentHost)eventFragment;
            if(hostFragment.isAdded()){
                hostFragment.reload();
            }
            getSupportFragmentManager().beginTransaction()
                    .show(eventFragment)
                    .commit();
        }
    }

    private void onJobTabClicked() {
        dismissPresentedFragment();
        hideAllFragment();
        currentFragmentTag = KEY_FRAGMENT_EVENT;
        updateTabBar(R.id.btn_job);
        searchParam.setSearchMode(EzlySearchParam.SEARCH_MODE_JOB);
        Fragment eventFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_EVENT);
        if(eventFragment == null){
            eventFragment = new EzlyEventFragmentHost();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.app_fragment_container, eventFragment, KEY_FRAGMENT_EVENT)
                    .commit();
        }
        else{
            EzlyEventFragmentHost hostFragment = (EzlyEventFragmentHost)eventFragment;
            if(hostFragment.isAdded()){
                hostFragment.reload();
            }
            getSupportFragmentManager().beginTransaction()
                    .show(eventFragment)
                    .commit();
        }
    }

    private void updateTabBar(int viewRes) {
//        int selectedTabBg = getContext().getResources().getColor(R.color.divider);
//        int unSelectedTabBg = getContext().getResources().getColor(R.color.white);
//        btnJob.setBackgroundColor(unSelectedTabBg);
//        btnService.setBackgroundColor(unSelectedTabBg);
//        btnPost.setBackgroundColor(unSelectedTabBg);
//        btnMyProfile.setBackgroundColor(unSelectedTabBg);
//        btnNotification.setBackgroundColor(unSelectedTabBg);

        ivJob.setImageDrawable(getResources().getDrawable(R.drawable.tab_job));
        ivService.setImageDrawable(getResources().getDrawable(R.drawable.tab_service));
        ivMyProfile.setImageDrawable(getResources().getDrawable(R.drawable.tab_my_profile));
        ivNotification.setImageDrawable(getResources().getDrawable(R.drawable.tab_notification));

        txtJob.setTextColor(getResources().getColor(R.color.tab_unselected));
        txtService.setTextColor(getResources().getColor(R.color.tab_unselected));
        txtNotification.setTextColor(getResources().getColor(R.color.tab_unselected));
        txtProfile.setTextColor(getResources().getColor(R.color.tab_unselected));

        switch (viewRes){
            case R.id.btn_job:
//                btnJob.setBackgroundColor(selectedTabBg);
                ivJob.setImageDrawable(getContext().getResources().getDrawable(R.drawable.tab_job_sel));
                txtJob.setTextColor(getResources().getColor(R.color.btn_post));
                break;
            case R.id.btn_service:
//                btnService.setBackgroundColor(selectedTabBg);
                ivService.setImageDrawable(getContext().getResources().getDrawable(R.drawable.tab_service_sel));
                txtService.setTextColor(getResources().getColor(R.color.btn_post));
                break;
            case R.id.btn_my_profile:
//                btnMyProfile.setBackgroundColor(selectedTabBg);
                ivMyProfile.setImageDrawable(getContext().getResources().getDrawable(R.drawable.tab_my_profile_sel));
                txtProfile.setTextColor(getResources().getColor(R.color.btn_post));
                break;
            case R.id.btn_notification:
//                btnNotification.setBackgroundColor(selectedTabBg);
                ivNotification.setImageDrawable(getContext().getResources().getDrawable(R.drawable.tab_notification_sel));
                txtNotification.setTextColor(getResources().getColor(R.color.btn_post));
                break;
        }
    }

    private void hideAllFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment eventFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_EVENT);
        if(eventFragment != null){
            transaction.hide(eventFragment);
        }

        Fragment myProfileFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_MY_PROFILE);
        if(myProfileFragment != null){
            transaction.hide(myProfileFragment);
        }

        Fragment notificationFragment = getSupportFragmentManager().findFragmentByTag(KEY_FRAGMENT_NOTIFICATION);
        if(notificationFragment != null){
            transaction.hide(notificationFragment);
        }

        transaction.commit();
    }

    public void showBottomTabbar(boolean isShow) {
        tabbar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnLoginEvent event){
        if(currentFragmentTag != KEY_FRAGMENT_NOTIFICATION){
            notificationPresenter.loadNotifications(0);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnReceivePushNotificationEvent event){
        if(currentFragmentTag != KEY_FRAGMENT_NOTIFICATION){
            newMessageIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNotificationsPrepared(ArrayList<EzlyNotification> notifications) {
        if(notificationHelper.hasUnreadyNotification(getContext(), notifications)){
            newMessageIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNotificationsLoadFailed(String errorMsg) {

    }
}
