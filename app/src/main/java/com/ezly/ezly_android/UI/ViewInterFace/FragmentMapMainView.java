package com.ezly.ezly_android.UI.ViewInterFace;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.List;

/**
 * Created by Johnnie on 2/10/16.
 */

public interface FragmentMapMainView extends BaseViewInterface{

    Activity getActivity();
    MapboxMap getMap();
    void onEventPrepared(List<EzlyEvent> events);
    Fragment getParentFragment();
}
