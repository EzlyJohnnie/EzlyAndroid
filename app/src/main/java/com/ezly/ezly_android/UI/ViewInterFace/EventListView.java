package com.ezly.ezly_android.UI.ViewInterFace;

import android.support.v4.app.Fragment;

import com.ezly.ezly_android.Data.EzlyEvent;

import java.util.List;

/**
 * Created by Johnnie on 11/10/16.
 */

public interface EventListView extends BaseViewInterface {

    void onEventPrepared(List<EzlyEvent> events);
    void showRefreshLayout(boolean isShow);
    void resetSearch();
    Fragment getParentFragment();
    void onAddFavourite(EzlyEvent event, boolean isSuccess);
    void onRemoveFavourite(EzlyEvent event, boolean isSuccess);

}
