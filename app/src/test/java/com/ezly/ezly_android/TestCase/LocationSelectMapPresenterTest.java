package com.ezly.ezly_android.TestCase;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.ezly.ezly_android.EzlyTestClasses.TestMainActivity;
import com.ezly.ezly_android.Presenter.LocationSelectMapPresenter;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.ViewInterFace.LocationSelectMapView;
import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class LocationSelectMapPresenterTest extends BaseTest {


    @Inject Context context;
    @Inject LocationSelectMapPresenter presenter;
    @Inject PermissionHelper permissionHelper;
    @Inject LocationServices locationServices;


    @Before
    public void setUp() {
        getTestActivityComponent().inject(this);
        presenter.setView(new LocationSelectMapView() {
            @Override
            public Context getContext() {
                return context;
            }

            @Override
            public Activity getActivity() {
                return Mockito.mock(TestMainActivity.class);
            }

            @Override
            public MapboxMap getMap() {
                return Mockito.mock(MapboxMap.class);
            }
        });
    }

    @After
    public void tearDown() {
        presenter = null;
    }

    @Test
    public void testInitMap() throws IllegalAccessException, InterruptedException {
        ArgumentCaptor<PermissionHelper.PermissionCallBack> permissionCallBackArgumentCaptor = ArgumentCaptor.forClass(PermissionHelper.PermissionCallBack.class);
        ArgumentCaptor<LocationListener> locationListenerArgumentCaptor = ArgumentCaptor.forClass(LocationListener.class);

        presenter.initMap();
        verify(permissionHelper, times(1)).requestPermission(any(EzlyBaseActivity.class), any(String.class), permissionCallBackArgumentCaptor.capture());
        verify(locationServices, times(1)).addLocationListener(locationListenerArgumentCaptor.capture());
        permissionCallBackArgumentCaptor.getValue().onGranted(new String[]{});
        locationListenerArgumentCaptor.getValue().onLocationChanged(Mockito.mock(android.location.Location.class));
        verify(presenter, times(1)).onLocationChanged(any(Location.class));

        boolean hasInitMyLocation = (boolean) FieldUtils.readField(presenter, "hasInitMyLocation", true);
        assertTrue(hasInitMyLocation);
    }

}
