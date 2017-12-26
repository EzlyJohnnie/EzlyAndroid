package com.ezly.ezly_android.DI.Module;

import com.ezly.ezly_android.EzlyApplication;
import com.ezly.ezly_android.Internal.DI.modules.AppModule;

import dagger.Module;

@Module
public class TestAppModule extends AppModule{
    public TestAppModule(EzlyApplication app) {
        super(app);
    }
}
