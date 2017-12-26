package com.ezly.ezly_android.EzlyTestClasses;

import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;



public class TestPermissionHelper extends PermissionHelper {

    @Override
    public void requestPermissions(EzlyBaseActivity activity, String[] permissions, PermissionCallBack permissionCallBack) {
        if(permissionCallBack != null){
            permissionCallBack.onGranted(permissions);
        }
    }
}
