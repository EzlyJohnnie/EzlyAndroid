package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Data.EzlyEvent;

/**
 * Created by Johnnie on 29/10/16.
 */

public interface EventDetailView extends BaseViewInterface {

    void onEventPrepared(EzlyEvent event);
    void onAddFavourite(boolean isSuccess);
    void onRemoveFavourite(boolean isSuccess);

}
