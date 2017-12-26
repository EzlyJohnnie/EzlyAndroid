package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Data.EzlyEvent;

import java.util.ArrayList;

/**
 * Created by Johnnie on 14/11/16.
 */

public interface UserPostView extends BaseViewInterface{

    void onEventPrepared(ArrayList<EzlyEvent> events);
    void showRefreshLayout(boolean isShow);
    void onRemoveFavourite(boolean isSuccess, int position);
    void onSetEventVisibilitySuccess();

}
