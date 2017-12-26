package com.ezly.ezly_android.Data;

import android.content.Context;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.SharedPreferenceHelper;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Johnnie on 16/10/16.
 */

public class EzlySearchParam {
    public static final int SEARCH_MODE_SERVICE = 0;
    public static final int SEARCH_MODE_JOB     = 1;
    public static final int SEARCH_MODE_USER    = 2;

    private static EzlySearchParam instance;
    private LocationHelper locationHelper;

    @Expose
    private ArrayList<EzlyCategory> selectedCategories;

    @Expose
    private String searchStr;

    @Expose
    private EzlyAddress searchLocation;

    @Expose
    private int mapZoomLevel;

    @Expose
    private int SearchMode;


    public static EzlySearchParam getInstance(LocationHelper locationHelper){
        if(instance == null){
            instance = new EzlySearchParam();
            instance.locationHelper = locationHelper;
        }
        return instance;
    }


    private EzlySearchParam() {
        selectedCategories = new ArrayList<>();
        SearchMode = SEARCH_MODE_SERVICE;
        mapZoomLevel = 16;
    }

    public ArrayList<EzlyCategory> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(ArrayList<EzlyCategory> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public EzlyAddress getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(EzlyAddress searchLocation) {
        this.searchLocation = searchLocation;
    }

    public int getMapZoomLevel() {
        return mapZoomLevel;
    }

    public void setMapZoomLevel(int mapZoomLevel) {
        this.mapZoomLevel = mapZoomLevel;
    }

    public int getSearchMode() {
        return SearchMode;
    }

    public void setSearchMode(int searchMode) {
        SearchMode = searchMode;
    }

    public void initSearchLocation(EzlyBaseActivity activity) {
        locationHelper.getCurrentLocation(activity, new LocationHelper.LocationCallback() {
            @Override
            public void onLocationGet(EzlyAddress ezlyAddress) {
                searchLocation = ezlyAddress;
            }

            @Override
            public void onLocationFail(boolean shouldShowRationale) {

            }
        });
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public String toJsonStr(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public void toSharedPreference(Context context){
        SharedPreferenceHelper.toSharedPreference(context,
                SharedPreferenceHelper.FILE_KEY_SEARCH_PARAM,
                SharedPreferenceHelper.VALUE_KEY_SEARCH_PARAM,
                toJsonStr());
    }

    public EzlySearchParam fromSharePreference(Context context){
        instance = SharedPreferenceHelper.fromSharedPreference(context,
                SharedPreferenceHelper.FILE_KEY_SEARCH_PARAM,
                SharedPreferenceHelper.VALUE_KEY_SEARCH_PARAM,
                EzlySearchParam.class);
        return instance;
    }

    public void reset(){
        selectedCategories = new ArrayList<>();
        searchStr = "";
        searchLocation = null;
        mapZoomLevel = 0;
    }
}