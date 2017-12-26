package com.ezly.ezly_android.UI.Post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Presenter.LocationSelectMapPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.LocationSelectMapView;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 1/02/17.
 */

public class EzlyLocationSelectMapFragment extends EzlyBaseFragment implements LocationSelectMapView ,
        MapboxMap.OnMapClickListener
{

    private static final String KEY_AVATAR = "key_avatar";

    @Inject LocationSelectMapPresenter presenter;

    @BindView(R.id.mapview) MapView mapView;

    private MapboxMap map;
    private OnLocationFetchListener listener;
    private Marker myLocationMarker;
    private String avatar;

    public static EzlyLocationSelectMapFragment newInstance(String userAvatar){
        EzlyLocationSelectMapFragment fragment = new EzlyLocationSelectMapFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_AVATAR, userAvatar);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(OnLocationFetchListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map_main, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        if(getArguments() != null){
            avatar = getArguments().getString(KEY_AVATAR);
        }

        initializeInjector();
        presenter.setView(this);
        initMap(savedInstanceState);
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.getUiSettings().setAttributionEnabled(false);
                map.getUiSettings().setCompassMargins(0, UIHelper.dip2px(getContext(), 80), UIHelper.dip2px(getContext(), 10), 0);
                map.setOnMapClickListener(EzlyLocationSelectMapFragment.this);
                presenter.initMap();
            }
        });
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pop();
        }
    }

    @OnClick(R.id.btn_my_location)
    public void onMyLocationClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pop();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        map.animateCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(map.getMyLocation()))
                        .zoom(16)
                        .build()),
                300);
    }

    @Override
    public MapboxMap getMap() {
        return map;
    }




    public interface OnLocationFetchListener{
        void onLocationFetch(EzlyAddress location);
    }
}
