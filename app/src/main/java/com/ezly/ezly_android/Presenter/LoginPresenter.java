package com.ezly.ezly_android.Presenter;

import android.content.Intent;
import android.widget.Toast;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.Response.WeChatTokenResponse;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.ViewInterFace.LoginView;
import com.ezly.ezly_android.network.ServerHelper;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 5/11/16.
 */

public class LoginPresenter extends BasePresenter {

    private LoginView mView;
    private MemberHelper memberHelper;

    @Inject
    public LoginPresenter(MemberHelper memberHelper) {
        this.memberHelper = memberHelper;
    }

    public void setView(LoginView view) {
        this.mView = view;
    }

    public void setupLoginService() {
        initFB();
        memberHelper.setWeChatLoginCallback(new MemberHelper.WeChatLoginCallback() {
            @Override
            public void onWeChatNotFound() {
                if(mView != null){
                    mView.onLoginCancelled();
                    SingleToast.makeText(mView.getContext(), mView.getContext().getString(R.string.we_chat_not_install), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onWeChatCodeGranted(String code) {
                memberHelper.getWeChatToken(code)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<WeChatTokenResponse>() {
                            @Override
                            public void call(WeChatTokenResponse weChatTokenResponse) {
                                loginToEzly(ServerHelper.LOGIN_SERVICE_WECHAT, weChatTokenResponse.getAccess_token(), weChatTokenResponse.getOpenid());
                            }
                        }, onError());
            }

            @Override
            public void willStartWeChatLogin() {

            }

            @Override
            public void onWeChatLoginCancelled() {
                if(mView != null){
                    mView.onLoginCancelled();
                }
            }

            @Override
            public void onWeChatLoginDenied() {
                if(mView != null){
                    mView.onLoginFailed("");
                }
            }
        });
    }

    public void initFB() {
        memberHelper.initFB(mView.getContext(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginToEzly(ServerHelper.LOGIN_SERVICE_FACEBOOK, loginResult.getAccessToken().getToken(), "");
            }

            @Override
            public void onCancel() {
                if(mView != null){
                    mView.onLoginCancelled();
                }
            }

            @Override
            public void onError(FacebookException exception) {
                if(mView != null){
                    mView.onLoginFailed(exception.getMessage());
                }
            }
        });
    }

    public void loginToEzly(String loginService, String serviceToken, String openID) {
        Observable<EzlyUser> userObservable = memberHelper.loginToEzly(mView.getContext(), loginService, serviceToken, openID);
        userObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onLogin(), onError());

    }

    private Action1<EzlyUser> onLogin() {
        return new Action1<EzlyUser>() {
            @Override
            public void call(EzlyUser user) {
                if(user != null) {
                    EventBus.getDefault().post(new ResultEvent.OnLoginEvent(user));
                    if(mView != null){
                        mView.onLogin(user);
                    }
                }
                else{
                    if(mView != null){
                        mView.onLoginFailed("");
                    }
                }
            }
        };
    }

    private Action1<Throwable> onError(){
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(mView != null){
                    mView.onLoginFailed(throwable.getMessage());
                }
            }
        };
    }


    public void loginWithWeChat() {
        setupLoginService();
        memberHelper.loginToWeChat();
    }

    public void loginWithGmail() {
        setupLoginService();
        memberHelper.setGoogleLoginCallback(new MemberHelper.GoogleLoginCallback() {
            @Override
            public void onGoogleLogin(boolean success, final String email) {
                if (success) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/plus.me";
                            try {
                                final String token = GoogleAuthUtil.getToken(mView.getParentActivity(), email, scope);

                                mView.getParentActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginToEzly(ServerHelper.LOGIN_SERVICE_GOOGLE, token, "");
                                    }
                                });

                            } catch (UserRecoverableAuthException userRecoverableException) {
                                userRecoverableException.printStackTrace();
                                Intent intent = userRecoverableException.getIntent();
                                mView.startActivityForResult(intent,
                                        MemberHelper.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                            } catch (GoogleAuthException fatalException) {
                                fatalException.printStackTrace();
                                if (mView != null) {
                                    mView.onLoginFailed(fatalException.getMessage());
                                }
                            } catch (IOException e) {
                                if (mView != null) {
                                    mView.onLoginFailed(e.getMessage());
                                }
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                } else {
                    if (mView != null) {
                        if (email.contains("canceled")) {
                            mView.onLoginCancelled();
                        } else {
                            UIHelper.displayConfirmDialog(mView.getContext(), "Cannot login to Google.\nPlease try again");
                            mView.onLoginFailed("");
                        }
                    }
                }
            }

        });


        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        mView.getParentActivity().startActivityForResult(intent, MemberHelper.RC_SIGN_IN);
    }


}
