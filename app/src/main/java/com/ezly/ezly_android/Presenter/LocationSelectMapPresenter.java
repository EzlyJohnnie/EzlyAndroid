package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.ViewInterFace.LocationSelectMapView;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;

import javax.inject.Inject;

/**
 * Created by Johnnie on 1/02/17.
 */

public class LocationSelectMapPresenter extends BasePresenter {

    private LocationSelectMapView mView;
    private LocationHelper locationHelper;

    private boolean hasInitMyLocation;

    private LocationServices locationServices;

    @Inject
    public LocationSelectMapPresenter(LocationHelper locationHelper, LocationServices locationServices) {
        this.locationHelper = locationHelper;
        this.locationServices = locationServices;
    }

    public void setView(LocationSelectMapView view) {
        this.mView = view;
    }

    public void initMap() {
        locationHelper.requestLocationPermission((EzlyBaseActivity) mView.getActivity(), new PermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted(String[] permissions) {

                mView.getMap().setMyLocationEnabled(true);
                locationServices.addLocationListener(new LocationListener() {
                    @Override
                    public void onLocationChanged(android.location.Location location) {
                        if (location != null && !hasInitMyLocation) {
                            hasInitMyLocation = true;
                            mView.getMap().setCameraPosition(new CameraPosition.Builder()
                                    .target(new LatLng(location))
                                    .zoom(16)
                                    .build());
                        }
                        locationHelper.setLastUserLocation(new EzlyAddress((float)location.getLatitude(), (float)location.getLongitude()));
                    }
                });
            }

            @Override
            public void onDenied(String[] permissions, int[] grantResults) {

            }

            @Override
            public void onShouldShowRationale(String permission) {
                UIHelper.displayConfirmDialog(mView.getActivity(), mView.getActivity().getResources().getString(R.string.location_rationale));
            }
        });
    }
}
