package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Model.EventModel;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.ViewInterFace.FragmentMapMainView;
import com.ezly.ezly_android.UI.event.EzlyEventFragmentHost;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 2/10/16.
 */

public class FragmentMapMainPresenter extends BasePresenter {

    private FragmentMapMainView mView;
    private LocationServices locationServices;
    private LocationHelper locationHelper;
    private EventModel eventModel;
    private boolean hasInitMyLocation;
    private boolean hasDraggedMap;
    private Timer searchCountdown = new Timer();


    @Inject
    public FragmentMapMainPresenter(EventModel eventModel,
                                    LocationServices locationServices,
                                    LocationHelper locationHelper)
    {
        this.eventModel = eventModel;
        this.locationServices = locationServices;
        this.locationHelper = locationHelper;
    }

    public void setView(FragmentMapMainView view) {
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
                        if (location != null && !hasInitMyLocation && !hasDraggedMap) {
                            hasInitMyLocation = true;
                            mView.getMap().setCameraPosition(new CameraPosition.Builder()
                                    .target(new LatLng(location))
                                    .zoom(16)
                                    .build());
                        }
                        locationHelper.setLastUserLocation(new EzlyAddress((float)location.getLatitude(), (float)location.getLongitude()));
                    }
                });

                mView.getMap().setOnCameraChangeListener(new MapboxMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        hasDraggedMap = true;

                        searchCountdown.cancel();
                        searchCountdown.purge();
                        searchCountdown = new Timer();

                        searchCountdown.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(mView != null && mView.getActivity() != null){
                                    mView.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchEvent();
                                        }
                                    });
                                }
                            }
                        }, 300);

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

    public void onMyLocationClicked() {
        mView.getMap().animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                        .target(new LatLng(mView.getMap().getMyLocation()))
                                        .zoom(14)
                                        .build()),
                300);
    }

    public void searchEvent() {
        if(mView.getParentFragment() instanceof EzlyEventFragmentHost){
            List<EzlyEvent> events = ((EzlyEventFragmentHost)mView.getParentFragment()).getEvents();
            if(events != null){
                mView.onEventPrepared(events);
                return;
            }
        }

        Observable<List<EzlyEvent>> observable = eventModel.searchEvents(mView.getContext(), -1, -1);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onEventPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<List<EzlyEvent>> onEventPrepared() {
        return new Action1<List<EzlyEvent>>() {
            @Override
            public void call(List<EzlyEvent> events) {
                mView.onEventPrepared(events);
            }
        };
    }
}
