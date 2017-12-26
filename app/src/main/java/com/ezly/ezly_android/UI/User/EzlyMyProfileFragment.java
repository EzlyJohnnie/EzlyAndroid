package com.ezly.ezly_android.UI.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.LoginPresenter;
import com.ezly.ezly_android.Presenter.UserDetailPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.Address.EzlyAddressHostFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.Notification.EzlyNotificationHostFragment;
import com.ezly.ezly_android.UI.Setting.EzlySettingHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.LoginView;
import com.ezly.ezly_android.UI.ViewInterFace.UserDetailView;
import com.ezly.ezly_android.Utils.TextUtils;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 14/11/16.
 */

public class EzlyMyProfileFragment extends EzlyBaseFragment implements UserDetailView, LoginView, View.OnClickListener{

    final int LIKED_USER = 0;
    final int MY_FAVOURITE = 1;
    final int MY_POST_HISTORY = 2;
    final int MY_ADDRESS = 3;
    final int SYSTEM_NOTIFICATION = 4;
    final int SETTING = 5;

    @Inject UserDetailPresenter presenter;
    @Inject LoginPresenter loginPresenter;
    
    @Inject MemberHelper memberHelper;

    @BindView(R.id.scroll_view) ScrollView scrollView;
    @BindView(R.id.my_profile_header) View myProfileHeader;
    @BindView(R.id.iv_my_avatar) ImageView ivAvatar;
    @BindView(R.id.txt_name) TextView txtName;
    @BindView(R.id.txt_like) TextView txtLike;
    @BindView(R.id.btn_like) TextView btnLike;
    @BindView(R.id.footer) View footer;

    @BindView(R.id.login_pnl) View loginPnl;

    @BindView(R.id.my_item_pnl) LinearLayout myItemPnl;
    @BindView(R.id.setting_item_pnl) LinearLayout settingItemPnl;
    @BindView(R.id.loading_indicator) View loadingIndicator;


    private View btnUserILike;
    private View btnMyFavourite;
    private View btnPostHistory;
    private View btnNotification;
    private View btnAddress;
    private View btnSetting;

    private String userID;
    private EzlyUser user;

    public static EzlyMyProfileFragment getMyInfoInstance(){
        EzlyMyProfileFragment fragment = new EzlyMyProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_profile, container, false);
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
    public void onDestroy(){
        super.onDestroy();
        memberHelper.reset();
    }

    private void init(Bundle savedInstanceState, View root) {
        if(getArguments() != null) {
            userID = getArguments().getString(EzlyUserHostFragment.KEY_USER_ID);
        }
        initializeInjector();
        presenter.setView(this);
        loginPresenter.setView(this);
        initView(root);

        presenter.getMyInfo();
    }


    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView(final View root) {
        btnLike.setVisibility(View.GONE);
        footer.setVisibility(View.GONE);
        showLoadingIndicator(false);
        initMyItemPnl();
        initSettingItemPnl();
        setBtnsEnable();
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    UIHelper.hideKeyBoard(root);
                }
                return false;
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                UIHelper.hideKeyBoard(root);
            }
        });
    }

    private void setBtnsEnable() {
        if(memberHelper.hasLogin()){
            setBtnEnable(btnUserILike, true);
            setBtnEnable(btnMyFavourite, true);
            setBtnEnable(btnPostHistory, true);
            setBtnEnable(btnNotification, true);
            setBtnEnable(btnAddress, true);
            loginPnl.setVisibility(View.GONE);
            myProfileHeader.setVisibility(View.VISIBLE);
        }
        else{
            setBtnEnable(btnUserILike, false);
            setBtnEnable(btnMyFavourite, false);
            setBtnEnable(btnPostHistory, false);
            setBtnEnable(btnNotification, false);
            setBtnEnable(btnAddress, false);
            loginPnl.setVisibility(View.VISIBLE);
            myProfileHeader.setVisibility(View.GONE);
        }
    }

    private void setBtnEnable(View button, boolean isEnable){
        if(button == null){
            return;
        }

        int textColor = 0;
        button.setBackgroundDrawable(null);
        if(isEnable){
            button.setOnClickListener(this);
            button.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.menu_item_bg_color));
            button.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.menu_item_bg_color));
            textColor = getContext().getResources().getColor(R.color.grey_66);
        }
        else{button.setOnClickListener(null);
            button.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            textColor = getContext().getResources().getColor(R.color.divider);
        }

        ((TextView)button.findViewById(R.id.txt_title)).setTextColor(textColor);
    }

    private void initMyItemPnl() {
        int[] imagesRes = new int[]{R.drawable.ic_my_liked_user, R.drawable.ic_my_favourite, R.drawable.ic_my_post_history, R.drawable.ic_my_liked_user};
        int[] titlesRes = new int[]{R.string.my_liked_user, R.string.my_favourite, R.string.my_post_history, R.string.my_address};

        for(int i = 0; i < titlesRes.length; i++){
            View itemBaseView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_my_profile_item, null, false);

            int tag = 0;
            switch (i){
                case LIKED_USER:
                    btnUserILike = itemBaseView;
                    tag = LIKED_USER;
                    break;
                case MY_FAVOURITE:
                    btnMyFavourite = itemBaseView;
                    tag = MY_FAVOURITE;
                    break;
                case MY_POST_HISTORY:
                    btnPostHistory = itemBaseView;
                    tag = MY_POST_HISTORY;
                    break;
                case MY_ADDRESS:
                    btnAddress = itemBaseView;
                    tag = MY_ADDRESS;
                    break;
            }
            itemBaseView.setTag(tag);

            ImageView ivImage = (ImageView)itemBaseView.findViewById(R.id.iv_image);
            TextView txtTitle = (TextView) itemBaseView.findViewById(R.id.txt_title);
            View divider =  itemBaseView.findViewById(R.id.divider);

            divider.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            ivImage.setImageDrawable(getContext().getResources().getDrawable(imagesRes[i]));
            txtTitle.setText(getContext().getResources().getString(titlesRes[i]));

            itemBaseView.setOnClickListener(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIHelper.dip2px(getContext(), 50));
            itemBaseView.setLayoutParams(lp);
            myItemPnl.addView(itemBaseView);
        }
    }

    private void initSettingItemPnl() {

        int[] imagesRes = new int[]{/*R.drawable.ic_notification,*/ R.drawable.ic_setting};
        int[] titlesRes = new int[]{/*R.string.system_notification,*/ R.string.setting};

        for(int i = 0; i < imagesRes.length; i++){
            View itemBaseView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_my_profile_item, null, false);

            int tag = 0;
            switch (i){
//                case 0:
//                    btnNotification = itemBaseView;
//                    tag = SYSTEM_NOTIFICATION;
//                    break;
                case 0:
                    btnSetting = itemBaseView;
                    tag = SETTING;
                    break;
            }
            itemBaseView.setTag(tag);

            ImageView ivImage = (ImageView)itemBaseView.findViewById(R.id.iv_image);
            TextView txtTitle = (TextView) itemBaseView.findViewById(R.id.txt_title);
            View divider =  itemBaseView.findViewById(R.id.divider);

            divider.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            ivImage.setImageDrawable(getContext().getResources().getDrawable(imagesRes[i]));
            txtTitle.setText(getContext().getResources().getString(titlesRes[i]));

            itemBaseView.setOnClickListener(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIHelper.dip2px(getContext(), 50));
            itemBaseView.setLayoutParams(lp);
            settingItemPnl.addView(itemBaseView);
        }
    }


    private void pushSettingFragment() {
        if(getParentFragment() instanceof EzlyBaseHostFragment) {
            ((EzlyBaseHostFragment) getParentFragment()).push(EzlySettingHostFragment.getInstance(true));
        }
    }

    private void pushNotificationFragment() {
        if(getParentFragment() instanceof EzlyBaseHostFragment) {
            ((EzlyBaseHostFragment) getParentFragment()).push(EzlyNotificationHostFragment.getInstance(true));
        }

    }

    private void pushMyAddressFragment() {
        if(getParentFragment() instanceof EzlyBaseHostFragment) {
            ((EzlyBaseHostFragment) getParentFragment()).push(EzlyAddressHostFragment.newInstance());
        }
    }

    private void pushPostHistoryFragment() {
        if(getParentFragment() instanceof EzlyBaseHostFragment) {
            ((EzlyBaseHostFragment) getParentFragment()).push(EzlyUserPostFragment.getInstance(userID, true, true, EzlyUserPostFragment.VIEW_TYPE_POST_HISTORY));
        }
    }

    private void pushMyFavouriteFragment() {
        if(getParentFragment() instanceof EzlyBaseHostFragment) {
            ((EzlyBaseHostFragment) getParentFragment()).push(EzlyUserPostFragment.getInstance(userID, true, true, EzlyUserPostFragment.VIEW_TYPE_FAVOURITE));
        }
    }

    private void pushLikedUserFragment() {
        if(getParentFragment() instanceof EzlyBaseHostFragment) {
            ((EzlyBaseHostFragment) getParentFragment()).push(EzlyUserListFragment.getInstance(userID, true, false, EzlyUserListFragment.VIEW_TYPE_I_LIKE_WHOM));
        }
    }

    private void updateUser() {
        if(!TextUtils.isEmpty(user.getAvatarUrl())){
            Picasso.with(getContext())
                    .load(user.getAvatarUrl())
                    .fit()
                    .into(ivAvatar);
        }
        else{
            Picasso.with(getContext())
                    .load(R.drawable.placeholder_user)
                    .fit()
                    .into(ivAvatar);
        }

        if(!TextUtils.isEmpty(user.getDisplayName())){
            txtName.setText(user.getDisplayName());
        }

        String beLikedStr = String.format(getContext().getResources().getString(R.string.have_like), user.getNumOfLikes());
        txtLike.setText(beLikedStr);
    }

    private void showLoadingIndicator(boolean isShow){
        loadingIndicator.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void setUnloginView(){
        showLoadingIndicator(false);
        setBtnEnable(btnUserILike, false);
        setBtnEnable(btnMyFavourite, false);
        setBtnEnable(btnPostHistory, false);
        setBtnEnable(btnNotification, false);
        setBtnEnable(btnAddress, false);
        loginPnl.setVisibility(View.VISIBLE);
        myProfileHeader.setVisibility(View.GONE);
    }
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnLogoutEvent event){
        setBtnsEnable();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnLoginEvent event){
        if(event.getCurrentUser() != null){
            onLogin(event.getCurrentUser());
        }
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment) getParentFragment()).pop();
        }
        else if(getActivity() instanceof EzlyBaseActivity){
            ((EzlyBaseActivity)getActivity()).onBackPressed();
        }
    }

    @OnClick(R.id.btn_wechat)
    public void loginWithWeChat(){
        showLoadingIndicator(true);
        loginPresenter.loginWithWeChat();
    }

    @OnClick(R.id.btn_fb)
    public void loginWithFB(){
        showLoadingIndicator(true);
        loginPresenter.initFB();
        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
    }

    @OnClick(R.id.btn_gmail)
    public void loginWithGmail(){
        showLoadingIndicator(true);
        loginPresenter.loginWithGmail();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }


    @Override
    public void onUserPrepared(EzlyUser user) {
        this.user = user;
        updateUser();
    }

    @Override
    public EzlyBaseActivity getParentActivity() {
        return (EzlyBaseActivity)getActivity();
    }

    @Override
    public void onLogin(final EzlyUser currentUser) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EzlyMyProfileFragment.this.user = currentUser;
                showLoadingIndicator(false);
                setBtnEnable(btnUserILike, true);
                setBtnEnable(btnMyFavourite, true);
                setBtnEnable(btnPostHistory, true);
                setBtnEnable(btnNotification, true);
                setBtnEnable(btnAddress, true);

                loginPnl.setVisibility(View.GONE);
                myProfileHeader.setVisibility(View.VISIBLE);
                updateUser();
            }
        });

    }

    @Override
    public void onLoginFailed(final String errorMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUnloginView();
                String errorStr = errorMsg;
                if(TextUtils.isEmpty(errorMsg)){
                    errorStr = getContext().getResources().getString(R.string.login_failed);
                }
                SingleToast.makeText(getContext(), errorStr, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoginCancelled() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUnloginView();
            }
        });
    }

    @Override public void onEventPrepared(ArrayList<EzlyEvent> events) {}
    @Override public void onLikeUser(boolean isSuccess) {}

    @Override
    public void onClick(View v) {
        switch ((int)v.getTag()){
            case LIKED_USER:
                pushLikedUserFragment();
                break;
            case MY_FAVOURITE:
                pushMyFavouriteFragment();
                break;
            case MY_POST_HISTORY:
                pushPostHistoryFragment();
                break;
            case MY_ADDRESS:
                pushMyAddressFragment();
                break;
            case SYSTEM_NOTIFICATION:
                pushNotificationFragment();
                break;
            case SETTING:
                pushSettingFragment();
                break;
        }
    }
}
