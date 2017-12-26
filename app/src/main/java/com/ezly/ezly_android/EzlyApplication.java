package com.ezly.ezly_android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.ezly.ezly_android.Internal.DI.components.AppComponent;
import com.ezly.ezly_android.Internal.DI.components.DaggerAppComponent;
import com.ezly.ezly_android.Internal.DI.modules.AppModule;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.Utils.Config;
import com.ezly.ezly_android.Utils.PushNotification.MessageReceivingService;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Johnnie on 9/07/16.
 */
public class EzlyApplication extends Application {

    protected AppComponent applicationComponent;
    private EzlyBaseActivity currentActivity;

    public void setCurrentActivity(EzlyBaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public EzlyBaseActivity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initAppComponent();
        initPushNotification();
//        initLeakcanary();
        initMapBox();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initLeakcanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not initSearchLocation your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    protected void initMapBox() {
        MapboxAccountManager.start(this, Config.MAPBOX_ACCESS_TOKEN);
    }

    private void initPushNotification() {
        startService(new Intent(this, MessageReceivingService.class));
    }

    public AppComponent getAppComponent() {
        return applicationComponent;
    }

    public void initAppComponent(){
        applicationComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
