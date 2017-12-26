package com.ezly.ezly_android.TestCase;

import com.ezly.ezly_android.BuildConfig;
import com.ezly.ezly_android.DI.Component.DaggerTestActivityComponent;
import com.ezly.ezly_android.DI.Component.DaggerTestAppComponent;
import com.ezly.ezly_android.DI.Component.TestActivityComponent;
import com.ezly.ezly_android.DI.Component.TestAppComponent;
import com.ezly.ezly_android.DI.Module.TestActivityModule;
import com.ezly.ezly_android.DI.Module.TestAppModule;
import com.ezly.ezly_android.EzlyTestClasses.TestApplication;
import com.ezly.ezly_android.EzlyTestRunner;

import org.junit.runner.RunWith;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(EzlyTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = TestApplication.class)
public class BaseTest {
    public TestAppComponent testAppComponent;
    protected TestActivityComponent testActivityComponent;

    TestApplication application;

    protected TestAppComponent getTestAppComponent(){
        if(testAppComponent == null){
            application = (TestApplication) RuntimeEnvironment.application;
            testAppComponent = DaggerTestAppComponent.builder()
                    .testAppModule(new TestAppModule(application))
                    .build();
        }
        return testAppComponent;
    }

    protected TestActivityComponent getTestActivityComponent(){
        if(testActivityComponent == null){
            testActivityComponent = DaggerTestActivityComponent.builder()
                    .testAppComponent(getTestAppComponent())
                    .testActivityModule(new TestActivityModule(application.getApplicationContext()))
                    .build();
        }

        return testActivityComponent;
    }

}
