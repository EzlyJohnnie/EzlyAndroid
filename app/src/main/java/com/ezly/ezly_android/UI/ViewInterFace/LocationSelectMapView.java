package com.ezly.ezly_android.UI.ViewInterFace;

import android.app.Activity;

import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * Created by Johnnie on 1/02/17.
 */

public interface LocationSelectMapView extends BaseViewInterface {
    Activity getActivity();
    MapboxMap getMap();
}
