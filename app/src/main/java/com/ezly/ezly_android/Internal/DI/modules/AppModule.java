package com.ezly.ezly_android.Internal.DI.modules;

import com.ezly.ezly_android.DB.EzlyDBHelper;
import com.ezly.ezly_android.EzlyApplication;
import com.ezly.ezly_android.Internal.DI.PerActivity;
import com.ezly.ezly_android.Utils.Helper.GAHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Chris on 3/06/15.
 */
@Module
public class AppModule {
    private final EzlyApplication app;

    public AppModule(EzlyApplication app) {
        this.app = app;
    }

    @Provides @Singleton
    public GAHelper provideGAHelper(){
        return new GAHelper(app);
    }
}
