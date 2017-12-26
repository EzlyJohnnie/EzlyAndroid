package com.ezly.ezly_android.UI.ViewInterFace;

import android.content.Intent;

import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;

/**
 * Created by Johnnie on 5/11/16.
 */

public interface LoginView extends BaseViewInterface {

    EzlyBaseActivity getParentActivity();
    void onLogin(EzlyUser currentUser);
    void startActivityForResult(Intent intent, int requestCode);
    void onLoginFailed(String errorMsg);
    void onLoginCancelled();

}
