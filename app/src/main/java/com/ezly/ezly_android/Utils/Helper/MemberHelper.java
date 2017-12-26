package com.ezly.ezly_android.Utils.Helper;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyToken;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.Data.Response.WeChatTokenResponse;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.Utils.TextUtils;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 5/11/16.
 */

public class MemberHelper {

    private static final String WE_CHAT_APP_ID = "wxeed7c8ee93b4971b";
    private static final String WE_CHAT_APP_SECRET = "36a9c691d34b7a3d598cbd4caf777ecb";

    public static final int RC_SIGN_IN = 9001;
    public static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 9401;

    private static MemberHelper instance;
    private GoogleLoginCallback googleLoginCallback;
    private WeChatLoginCallback weChatLoginCallback;
    private CallbackManager facebookCallbackManager;

    private ServerHelper serverHelper;

    private boolean hasLogin;
    private EzlyToken token;
    private EzlyUser currentUser;
    private String currentGoogleEmail;
    private IWXAPI weChatAPI;

    /**
     * should be called by Dagger Module only
     * user @Inject to get instance
     * @param serverHelper
     * @return
     */
    public static MemberHelper getInstance(ServerHelper serverHelper){
        if(instance == null){
            instance = new MemberHelper();
            instance.serverHelper = serverHelper;
        }
        return instance;
    }

    private MemberHelper() {
    }

    /**
     * should only call once when app open
     * @param context
     */
    public void checkCachedToken(Context context){
        if(hasLogin && token != null && token.hasExpired()){
            EventBus.getDefault().post(new ResultEvent.OnLogoutEvent());
        }
        else{
            token = getSavedToken(context);
            if(token != null && !token.hasExpired()){
                hasLogin = true;
                ServerHelper.currentToken = token.getCurrentToken();
                getMyInfo()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<EzlyUser>() {
                            @Override
                            public void call(EzlyUser ezlyUser) {
                                currentUser = ezlyUser;
                                EventBus.getDefault().post(new ResultEvent.OnLoginEvent(ezlyUser));
                            }
                        }, RequestErrorHandler.onRequestError(context));;
            }
        }
    }

    public boolean hasLogin(){
        return hasLogin;
    }

    public EzlyUser getCurrentUser() {
        return currentUser;
    }

    public void initFB(Context context, FacebookCallback<LoginResult> facebookCallback) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, facebookCallback);
    }

    public void initWeChat(EzlyBaseActivity activity) {
        weChatAPI = WXAPIFactory.createWXAPI(activity.getApplicationContext(), WE_CHAT_APP_ID, false);
        weChatAPI.registerApp(WE_CHAT_APP_ID);
        weChatAPI.handleIntent(activity.getIntent(), activity);
    }

    public IWXAPI getWeChatAPI() {
        return weChatAPI;
    }

    public void setGoogleLoginCallback(GoogleLoginCallback googleLoginCallback) {
        this.googleLoginCallback = googleLoginCallback;
    }

    public void setWeChatLoginCallback(WeChatLoginCallback weChatLoginCallback) {
        this.weChatLoginCallback = weChatLoginCallback;
    }

    public Observable<WeChatTokenResponse> getWeChatToken(String code){
        HashMap<String, String> param = new HashMap<>();
        param.put("appid", WE_CHAT_APP_ID);
        param.put("secret", WE_CHAT_APP_SECRET);
        param.put("code", code);
        param.put("grant_type", "authorization_code");

        return serverHelper.getWeChatToken(param);
    }

    public Observable<EzlyUser> loginToEzly(Context context, String loginService, String serviceToken, String openID){
        String tokenBody = String.format("%s %s", loginService, serviceToken);
        String grantType = "External";

        HashMap<String, String> body = new HashMap<>();
        body.put("token", tokenBody);
        body.put("client_id", ServerHelper.CLIENT_ID);
        body.put("client_secret", ServerHelper.CLIENT_SECRET);
        body.put("grant_type", grantType);
        if(loginService.equals(ServerHelper.LOGIN_SERVICE_WECHAT)){
            body.put("openid", openID);
        }

        return serverHelper.login(body)
                .doOnNext(saveToken(context))
                .doOnNext(registerDevice(context))
                .flatMap(getCurrentUserInfo())
                .doOnNext(new Action1<EzlyUser>() {
                    @Override
                    public void call(EzlyUser user) {
                       if(user != null){
                           currentUser = user;
                       }
                    }
                });
    }

    private Action1<? super EzlyToken> registerDevice(final Context context) {
        return new Action1<EzlyToken>() {
            @Override
            public void call(EzlyToken ezlyToken) {
                //need to subscribe here, so user can still login if register device fail for push notification
                serverHelper.registerDevice(SharedPreferenceHelper.getGcmToken(context))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<EzlyBaseResponse>() {
                            @Override
                            public void call(EzlyBaseResponse baseResponse) {
                                Log.e("api", "register device");
                                //TODO: handle error
                            }
                        }, RequestErrorHandler.onRequestError(context));
            }
        };
    }

    private Action1<? super EzlyToken> saveToken(final Context context) {
        return new Action1<EzlyToken>() {
            @Override
            public void call(EzlyToken ezlyToken) {
                token = ezlyToken;
                token.setCreateTime(System.currentTimeMillis() / 1000);
                SharedPreferenceHelper.toSharedPreference(context,
                        SharedPreferenceHelper.FILE_KEY_TOKEN,
                        SharedPreferenceHelper.VALUE_KEY_TOKEN,
                        token.toJson());
            }
        };
    }

    public Observable<EzlyUser> getMyInfo(){
        return serverHelper.getMyInfo();
    }

    private Func1<EzlyToken, Observable<EzlyUser>> getCurrentUserInfo() {
        return new Func1<EzlyToken, Observable<EzlyUser>>() {
            @Override
            public Observable<EzlyUser> call(EzlyToken token) {
                if(token != null && !TextUtils.isEmpty(token.getCurrentToken())) {
                    hasLogin = true;
                    return getMyInfo();
                }
                return null;
            }
        };
    }

    public Observable<List<EzlyUser>> getMyLikedUser(int top, int skip){
        HashMap<String, String> requestParam = new HashMap<>();
        if(top >= 0){
            requestParam.put("Top", top  + "");
        }

        if(skip >= 0){
            requestParam.put("Skip", skip  + "");
        }

        return serverHelper.getMyLikedUser(requestParam);
    }

    public Observable<EzlyEvent> getMyPostHistory() {
        Observable<List<EzlyEvent>> postJobsHistoryOb = serverHelper.getMyJobsHistory().toList();
        Observable<List<EzlyEvent>> postServiceHistoryOb = serverHelper.getMyServicesHistory().toList();

        return Observable
                .combineLatest(postJobsHistoryOb, postServiceHistoryOb, new Func2<List<EzlyEvent>, List<EzlyEvent>, List<EzlyEvent>>() {
                    @Override
                    public List<EzlyEvent> call(List<EzlyEvent> ezlyEvents, List<EzlyEvent> ezlyEvents2) {
                        List<EzlyEvent> favourites = new ArrayList<EzlyEvent>();
                        favourites.addAll(ezlyEvents);
                        favourites.addAll(ezlyEvents2);
                        return favourites;
                    }
                })
                .flatMap(new Func1<List<EzlyEvent>, Observable<EzlyEvent>>() {
                    @Override
                    public Observable<EzlyEvent> call(List<EzlyEvent> ezlyEvents) {
                        return Observable.from(ezlyEvents);
                    }
                });
    }


    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean hasHandle = false;
        if (requestCode == RC_SIGN_IN) {
            //google login
            hasHandle = true;
            boolean success = false;
            if(data == null){
                success = false;
                currentGoogleEmail = "error:canceled";
            }
            else{
                currentGoogleEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                success = !TextUtils.isEmpty(currentGoogleEmail);
            }

            if(googleLoginCallback != null){
                googleLoginCallback.onGoogleLogin(success, currentGoogleEmail);
            }

        }
        else if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) {
            //google get user access token permission callback
            hasHandle = true;
            boolean success = resultCode == Activity.RESULT_OK;
            if (googleLoginCallback != null) {
                googleLoginCallback.onGoogleLogin(success, currentGoogleEmail);
            }
        }
        else{
            //facebook login
            if(facebookCallbackManager != null){
                hasHandle = facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }



        return hasHandle;
    }

    private EzlyToken getSavedToken(Context context){
        return SharedPreferenceHelper.fromSharedPreference(context, SharedPreferenceHelper.FILE_KEY_TOKEN, SharedPreferenceHelper.VALUE_KEY_TOKEN, EzlyToken.class);
    }


    public void reset(){
        facebookCallbackManager = null;
        googleLoginCallback = null;
        weChatLoginCallback = null;
    }

    public void loginToWeChat() {
        if (!weChatAPI.isWXAppInstalled()) {
            if(weChatLoginCallback != null){
                weChatLoginCallback.onWeChatNotFound();
            }
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        if(weChatAPI.sendReq(req)){
            if(weChatLoginCallback != null){
                weChatLoginCallback.willStartWeChatLogin();
            }
        }
    }

    public void getWeChatAccessToken(String weChatCode){
        if(weChatLoginCallback != null){
            weChatLoginCallback.onWeChatCodeGranted(weChatCode);
        }
    }

    public void onWeChatLoginCancelled(){
        if(weChatLoginCallback != null){
            weChatLoginCallback.onWeChatLoginCancelled();
        }
    }

    public void onWeChatLoginDenied() {
        if(weChatLoginCallback != null){
            weChatLoginCallback.onWeChatLoginDenied();
        }
    }

    public void logout(Context context) {
        ServerHelper.currentToken = null;
        hasLogin = false;
        token = null;
        currentUser = null;
        SharedPreferenceHelper.toSharedPreference(context,
                SharedPreferenceHelper.FILE_KEY_TOKEN,
                SharedPreferenceHelper.VALUE_KEY_TOKEN,
                "{}");
    }


    public interface GoogleLoginCallback{
        void onGoogleLogin(boolean success, String email);
    }

    public interface WeChatLoginCallback{
        void onWeChatNotFound();
        void onWeChatCodeGranted(String code);
        void willStartWeChatLogin();
        void onWeChatLoginCancelled();
        void onWeChatLoginDenied();
    }
}
