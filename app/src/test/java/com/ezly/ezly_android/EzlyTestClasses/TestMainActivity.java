package com.ezly.ezly_android.EzlyTestClasses;

import com.ezly.ezly_android.DI.Component.DaggerTestActivityComponent;
import com.ezly.ezly_android.DI.Component.TestAppComponent;
import com.ezly.ezly_android.DI.Module.TestActivityModule;
import com.ezly.ezly_android.Internal.DI.components.DaggerActivityComponent;
import com.ezly.ezly_android.Internal.DI.modules.ActivityModule;
import com.ezly.ezly_android.UI.MainActivity;


public class TestMainActivity extends MainActivity {

    @Override
    protected void initializeInjector(){
        activityComponent = DaggerTestActivityComponent.builder()
                .testActivityModule(new TestActivityModule(this))
                .testAppComponent((TestAppComponent)getApplicationComponent())
                .build();
    }
}
