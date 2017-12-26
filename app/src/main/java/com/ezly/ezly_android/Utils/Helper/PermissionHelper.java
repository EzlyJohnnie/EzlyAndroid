package com.ezly.ezly_android.Utils.Helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;

import java.util.HashMap;

/**
 * Created by Johnnie on 8/03/16.
 */

public class PermissionHelper {
    protected static int callbackKey = 1;//=requestCode used as key for callback for each request
    protected static HashMap<Integer, PermissionCallBack> mCallBacks;
    protected static PermissionHelper instance;

    public static PermissionHelper getInstance() {
        if(instance == null){
            instance = new PermissionHelper();
            mCallBacks = new HashMap<>();
        }
        return instance;
    }

    public void requestPermission(EzlyBaseActivity activity, String permission, PermissionCallBack permissionCallBack){
        requestPermissions(activity, new String[]{permission}, permissionCallBack);
    }

    public void requestPermissions(EzlyBaseActivity activity, String[] permissions, PermissionCallBack permissionCallBack) {
        if(hasAllPermissions(activity, permissions)){//already granted
            if(permissionCallBack != null){
                permissionCallBack.onGranted(permissions);
            }
        }
        else if(shouldShowRequestPermissionRationale(activity, permissions, permissionCallBack)){
            //already denied, need to rationale
        }
        else{
            //not been asked, request permission here
            int requestCode = setPermissionCallBack(permissionCallBack);
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    private boolean hasPermission(Context context, String permission){
        return hasAllPermissions(context, new String[]{permission});
    }

    private boolean hasAllPermissions(Context context, String[] permissions){
        boolean hasAllPerMissions = true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(String permission : permissions){
                boolean hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                if(!hasPermission){
                    hasAllPerMissions = false;
                    break;
                }
            }
        }

        return hasAllPerMissions;
    }

    private boolean shouldShowRequestPermissionRationale(EzlyBaseActivity activity, String permissions[], PermissionCallBack permissionCallBack){
        boolean shouldShowRationale = false;
        for(String permission : permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
                if(permissionCallBack != null){
                    permissionCallBack.onShouldShowRationale(permission);
                    shouldShowRationale = true;
                }
            }
        }
        return shouldShowRationale;
    }


    private int setPermissionCallBack(PermissionCallBack permissionCallBack) {
        mCallBacks.put(callbackKey, permissionCallBack);
        int requestCode = callbackKey;
        callbackKey++;
        if(callbackKey > 255){//result code has to be 0-255
            callbackKey = 1;
        }
        return requestCode;
    }


    /**
     * this is called from activity after user interaction on permission request
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionCallBack permissionCallBack = mCallBacks.get(requestCode);
        if (permissionCallBack != null) {
            if (isSuccess(grantResults)) {
                permissionCallBack.onGranted(permissions);
            } else {
                permissionCallBack.onDenied(permissions, grantResults);
            }
            mCallBacks.remove(requestCode);
        }
    }

    private boolean isSuccess(int[] grantResults) {
        boolean result = true;
        for(int i : grantResults){
            if(i != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return result;
    }

    public interface PermissionCallBack{
        /**
         * has permission
         * @param permissions
         */
        void onGranted(String[] permissions);

        /**
         * at least one of permission denied from permission request
         * @param permissions
         * @param grantResults
         */
        void onDenied(String[] permissions, int[] grantResults);

        /**
         * this permission has already been denied before,
         * so need to show rationale here
         * @param permission
         */
        void onShouldShowRationale(String permission);
    }

}
