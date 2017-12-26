package com.ezly.ezly_android.Presenter;

import android.os.Handler;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.NotificationHelper;
import com.ezly.ezly_android.Data.EzlyNotification;
import com.ezly.ezly_android.UI.ViewInterFace.NotificationView;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Johnnie on 12/03/17.
 */

public class NotificationPresenter extends BasePresenter {
    public static final int TOP = 20;

    private NotificationView mView;
    private MemberHelper memberHelper;

    @Inject
    public NotificationPresenter(MemberHelper memberHelper) {
        this.memberHelper = memberHelper;
    }

    public void setView(NotificationView mView) {
        this.mView = mView;
    }

    public void loadNotifications(int skip) {
        if(!memberHelper.hasLogin()){
            return;
        }

        NotificationHelper.latestLoadTime = System.currentTimeMillis();
        //TODO: to implement
        if(mView != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    EzlyNotification notification = new EzlyNotification();
//                    notification.setId("1122334");
//                    notification.setRefType(EzlyNotification.TYPE_SERVICE);
//                    notification.setRefId("68af3b8f-04be-e611-a236-0225a8c1df01");
//                    notification.setDescription("Handyman Service");
//                    notification.setUser("Tony");
//                    notification.setCreatedUtc("2017-3-10T00:00:00+12:00");
//
//                    EzlyNotification notification2 = new EzlyNotification();
//                    notification2.setId("112233455667");
//                    notification2.setRefType(EzlyNotification.TYPE_JOB);
//                    notification2.setRefId("5faf3b8f-04be-e611-a236-0225a8c1df01");
//                    notification2.setDescription("Bedroom repaint");
//                    notification2.setUser("Mark");
//                    notification2.setCreatedUtc("2017-3-12T00:00:00+12:00");
//
                    ArrayList<EzlyNotification> notifications = new ArrayList<>();
//                    notifications.add(notification);
//                    notifications.add(notification2);
                    mView.onNotificationsPrepared(notifications);
                }
            }, 200);

        }
    }
}
