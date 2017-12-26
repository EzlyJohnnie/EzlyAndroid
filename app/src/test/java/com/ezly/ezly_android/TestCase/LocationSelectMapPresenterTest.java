package com.ezly.ezly_android.TestCase;

import android.app.Activity;
import android.content.Context;

import com.ezly.ezly_android.EzlyTestClasses.TestMainActivity;
import com.ezly.ezly_android.Presenter.LocationSelectMapPresenter;
import com.ezly.ezly_android.UI.ViewInterFace.LocationSelectMapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;


public class LocationSelectMapPresenterTest extends BaseTest {


    @Inject Context context;
    @Inject LocationSelectMapPresenter presenter;

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
//        presenter.setupMap();
        boolean hasInitMyLocation = (boolean) FieldUtils.readField(presenter, "hasInitMyLocation", true);
        assertTrue(hasInitMyLocation);
    }

}
