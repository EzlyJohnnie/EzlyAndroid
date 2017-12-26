package com.ezly.ezly_android.UI.BaseComponent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.darsh.multipleimageselect.helpers.Constants;
import com.ezly.ezly_android.EzlyApplication;
import com.ezly.ezly_android.Utils.Helper.GAHelper;
import com.ezly.ezly_android.Utils.Helper.ImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.MultiImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Internal.DI.components.ActivityComponent;
import com.ezly.ezly_android.Internal.DI.components.AppComponent;
import com.ezly.ezly_android.Internal.DI.components.DaggerActivityComponent;
import com.ezly.ezly_android.Internal.DI.modules.ActivityModule;
import com.ezly.ezly_android.Data.EzlySetting;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.Utils.Constant;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import javax.inject.Inject;

/**
 * Created by Johnnie on 9/07/16.
 */
public abstract class EzlyBaseActivity extends AppCompatActivity implements IWXAPIEventHandler{
    public static final int RETURN_MSG_TYPE_LOGIN = 1;
    public static final int RETURN_MSG_TYPE_SHARE = 2;

    protected static final String KEY_LANDING_FRAGMENT = "key_landing_fragment";
    protected static final String KEY_PRESENTING_FRAGMENT = "key_presenting_fragment";

    protected ActivityComponent activityComponent;

    //TODO: move inject gaHelper into fragment and presenter once track points are defined
    @Inject GAHelper gaHelper;
    @Inject MemberHelper memberHelper;
    @Inject ImagePickerHelper imagePickerHelper;
    @Inject MultiImagePickerHelper multiImagePickerHelper;
    @Inject PermissionHelper permissionHelper;

    protected String currentFragmentTag;

    private static int runningActivities = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeInjector();
        activityComponent.inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_bg));
        }
        if(memberHelper.getWeChatAPI() == null){
            memberHelper.initWeChat(this);
        }
        memberHelper.getWeChatAPI().handleIntent(getIntent(), this);
        if(!EzlySetting.hasInitInstance){
            EzlySetting.initInstance(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((EzlyApplication)getApplication()).setCurrentActivity(this);
        runningActivities++;
    }

    @Override
    public void onStop() {
        super.onStop();
        runningActivities--;
        if(runningActivities <= 0){
            ((EzlyApplication)getApplication()).setCurrentActivity(null);
        }
    }

    public static boolean isInBackGround(){
        return runningActivities == 0;
    }

    public GAHelper getGAHelper() {
        return gaHelper;
    }

    protected MemberHelper getMemberHelper() {
        return memberHelper;
    }

    @Override
    public SharedPreferences getPreferences(int mode) {
        return super.getPreferences(mode);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public AppComponent getApplicationComponent() {
        return ((EzlyApplication)getApplication()).getAppComponent();
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    protected void initializeInjector(){
        activityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(getApplicationComponent())
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(memberHelper.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (requestCode == Constant.PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            imagePickerHelper.processImageData(this, data);
        }
        else if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            multiImagePickerHelper.processImageData(this, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionResult(requestCode, permissions, grantResults);
    }


    public void push(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .add(R.id.app_fragment_container, fragment, KEY_LANDING_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public void replace(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(0, 0)
                .replace(R.id.app_fragment_container, fragment, KEY_LANDING_FRAGMENT)
                .commit();
    }

    public void replaceWithAnimation(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out,
                R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.app_fragment_container, fragment, KEY_LANDING_FRAGMENT)
                .commit();
    }

    public void dismissPresentedFragment(){
        Fragment presentingFragment = getSupportFragmentManager().findFragmentByTag(KEY_PRESENTING_FRAGMENT);
        if(presentingFragment != null){
            getSupportFragmentManager().beginTransaction()
                    .remove(presentingFragment)
                    .commit();
        }
    }

    public void presentFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_bottom_in, 0, 0, R.anim.slide_bottom_out)
                .replace(R.id.app_top_fragment_container, fragment, KEY_PRESENTING_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public boolean pop(){
        boolean hasHandle = false;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            hasHandle = true;
        }
        return hasHandle;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        if(fragment != null && fragment instanceof EzlyBaseHostFragment && fragment.isAdded()){
            boolean hasHandle = ((EzlyBaseHostFragment)fragment).onBackPressed();
            if(hasHandle){
                return;
            }
        }

        if(getSupportFragmentManager().getBackStackEntryCount() <= 0){
            UIHelper.displayYesNoDialog(this, getResources().getString(R.string.exit_app), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EzlyBaseActivity.super.onBackPressed();
                }
            });
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {

    }

}
