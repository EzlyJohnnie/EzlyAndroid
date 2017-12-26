package com.ezly.ezly_android.EzlyTestClasses;

import com.crashlytics.android.Crashlytics;
import com.ezly.ezly_android.DI.Component.DaggerTestAppComponent;
import com.ezly.ezly_android.DI.Module.TestAppModule;
import com.ezly.ezly_android.EzlyApplication;

import io.fabric.sdk.android.Fabric;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

public class TestApplication extends EzlyApplication {


    @Override
    public void onCreate() {
        setupRxSchedulers();
        super.onCreate();
    }

    private void setupRxSchedulers() {
        RxJavaPlugins.getInstance().reset();
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @Override
    protected void initMapBox() {
        //ignore mapBox in test
    }

    @Override
    public void initAppComponent(){
        applicationComponent = DaggerTestAppComponent.builder()
                .testAppModule(new TestAppModule(this))
                .build();
    }
}
