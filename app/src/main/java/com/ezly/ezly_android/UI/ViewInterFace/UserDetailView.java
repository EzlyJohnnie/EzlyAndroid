package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyUser;

import java.util.ArrayList;

/**
 * Created by Johnnie on 14/11/16.
 */

public interface UserDetailView extends BaseViewInterface{

    void onUserPrepared(EzlyUser user);
    void onEventPrepared(ArrayList<EzlyEvent> events);
    void onLikeUser(boolean isSuccess);

}
