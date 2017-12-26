package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Data.EzlyNotification;

import java.util.ArrayList;

/**
 * Created by Johnnie on 12/03/17.
 */

public interface NotificationView extends BaseViewInterface {

    void onNotificationsPrepared(ArrayList<EzlyNotification> notifications);
    void onNotificationsLoadFailed(String errorMsg);
}
