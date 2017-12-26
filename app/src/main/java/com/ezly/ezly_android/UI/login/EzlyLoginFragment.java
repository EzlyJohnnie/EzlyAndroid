package com.ezly.ezly_android.UI.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Presenter.LoginPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.ViewInterFace.LoginView;
import com.ezly.ezly_android.Utils.TextUtils;
import com.facebook.login.LoginManager;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 5/11/16.
 */

public class EzlyLoginFragment extends EzlyBaseFragment implements LoginView {

    @Inject LoginPresenter presenter;
    @Inject MemberHelper memberHelper;

    @BindView(R.id.loading_indicator) View loadingIndicator;

    private boolean isProcessingLogin;

    public static EzlyLoginFragment getInstance(){
        EzlyLoginFragment fragment = new EzlyLoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        memberHelper.reset();
    }

    private void init(Bundle savedInstanceState, View root) {
        initializeInjector();
        presenter.setView(this);
        initView(root);
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView(View root) {
        showLoadingIndicator(false);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });
    }


    private void showLoadingIndicator(final boolean isShow){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingIndicator.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });
    }

    @OnClick(R.id.btn_wechat)
    public void loginWithWeChat(){
        if(!isProcessingLogin){
            isProcessingLogin = true;
            showLoadingIndicator(true);
            presenter.loginWithWeChat();
        }
    }

    @OnClick(R.id.btn_fb)
    public void loginWithFB(){
        if(!isProcessingLogin){
            isProcessingLogin = true;
            showLoadingIndicator(true);
            presenter.initFB();
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
        }
    }

    @OnClick(R.id.btn_gmail)
    public void loginWithGmail(){
        if(!isProcessingLogin){
            isProcessingLogin = true;
            showLoadingIndicator(true);
            presenter.loginWithGmail();
        }
    }

    @OnClick(R.id.iv_close)
    public void dismiss(){
        boolean hasHandle = false;

        if(getParentFragment() instanceof EzlyBaseHostFragment){
            hasHandle = ((EzlyBaseHostFragment)getParentFragment()).dismissSelf();
        }

        if(!hasHandle && getActivity() instanceof MainActivity){
            hasHandle = ((MainActivity)getActivity()).pop();
        }

    }

    @Override
    public EzlyBaseActivity getParentActivity() {
        return (EzlyBaseActivity)getActivity();
    }

    @Override
    public void onLogin(EzlyUser currentUser) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadingIndicator(false);
                isProcessingLogin = false;
                dismiss();
            }
        });
    }

    @Override
    public void onLoginFailed(final String errorMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadingIndicator(false);
                isProcessingLogin = false;
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
                showLoadingIndicator(false);
                isProcessingLogin = false;
            }
        });
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }

}
