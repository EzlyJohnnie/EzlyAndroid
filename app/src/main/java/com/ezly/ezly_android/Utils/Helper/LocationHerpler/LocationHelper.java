package com.ezly.ezly_android.Utils.Helper.LocationHerpler;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;

import java.util.List;


/**
 * Created by Johnnie on 3/11/15.
 */
public class LocationHelper {

    private EzlyAddress currentLocation;
    public  boolean isAskingLocationPermission = false;
    private static LocationHelper instance;
    private PermissionHelper permissionHelper;

    public static LocationHelper getInstance(PermissionHelper permissionHelper){
        if(instance == null){
            instance = new LocationHelper();
            instance.permissionHelper = permissionHelper;
        }

        return instance;
    }

    public void requestLocationPermission(final EzlyBaseActivity activity, PermissionHelper.PermissionCallBack callBack){
        permissionHelper.requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, callBack);
    }


    public EzlyAddress getLastKnownLocation() {
        return currentLocation;
    }

    public void getCurrentLocation(final EzlyBaseActivity activity, final LocationCallback locationCallback) {
        if (currentLocation != null) {
            locationCallback.onLocationGet(currentLocation);
            return;
        }

        if(isAskingLocationPermission){
            return;
        }

        if (hasLocationPermission(activity)) {
            getLocation(activity, locationCallback);
        }
        else {
            isAskingLocationPermission = true;
            permissionHelper.requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION,
                    new PermissionHelper.PermissionCallBack() {
                        @Override
                        public void onGranted(String[] permissions) {
                            getLocation(activity, locationCallback);
                            isAskingLocationPermission= false;
                        }

                        @Override
                        public void onDenied(String[] permissions, int[] grantResults) {
                            locationCallback.onLocationFail(false);
                            isAskingLocationPermission= false;
                        }

                        @Override
                        public void onShouldShowRationale(String permission) {
                            locationCallback.onLocationFail(true);
                            isAskingLocationPermission= false;
                        }
                    });
        }
    }

    private boolean hasLocationPermission(EzlyBaseActivity activity) {
        boolean result = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            result = false;
        }
        return result;
    }

    private void getLocation(EzlyBaseActivity activity, LocationCallback locationCallback) {
        if (currentLocation != null) {
            locationCallback.onLocationGet(currentLocation);
            return;
        }

        LocationManager mLocationManager = (LocationManager) activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        android.location.Location bestLocation = null;
        for (String provider : providers) {
            //permission checked in getCurrentLocation()
            android.location.Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        EzlyAddress ezlyAddress = new EzlyAddress(0, 0);
        if(bestLocation != null) {
            ezlyAddress.getLocation().setLatitude((float) bestLocation.getLatitude());
            ezlyAddress.getLocation().setLongitude((float) bestLocation.getLongitude());
        }

        if(ezlyAddress.getLocation().isValidLocation()){
            locationCallback.onLocationGet(ezlyAddress);
            currentLocation = ezlyAddress;
        }
        else{
            locationCallback.onLocationFail(false);
        }
    }

    public void setLastUserLocation(EzlyAddress ezlyAddress){
        currentLocation = ezlyAddress;
    }


    public interface LocationCallback {
        void onLocationGet(EzlyAddress ezlyAddress);

        /**
         *
         * @param shouldShowRationale true if already denied before,
         * false if denied this time from requesting permission OR getlocation failed
         */
        void onLocationFail(boolean shouldShowRationale);
    }
}
