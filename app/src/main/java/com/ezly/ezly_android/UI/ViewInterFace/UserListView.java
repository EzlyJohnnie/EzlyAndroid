package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Data.EzlyUser;

import java.util.ArrayList;

/**
 * Created by Johnnie on 13/11/16.
 */

public interface UserListView extends BaseViewInterface{

    void onUserPrepared(ArrayList<EzlyUser> users);
    void showRefreshLayout(boolean isShow);

}
