package com.ezly.ezly_android.EzlyTestClasses;

import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;

import java.util.HashMap;


public class TestPermissionHelper extends PermissionHelper {

    public static TestPermissionHelper getInstance() {
        if(instance == null){
            instance = new TestPermissionHelper();
            mCallBacks = new HashMap<>();
        }
        return (TestPermissionHelper)instance;
    }


    @Override
    public void requestPermissions(EzlyBaseActivity activity, String[] permissions, PermissionCallBack permissionCallBack) {
        if(permissionCallBack != null){
            permissionCallBack.onGranted(permissions);
        }
    }
}
