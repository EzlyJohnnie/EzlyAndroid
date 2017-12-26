package com.ezly.ezly_android.Internal.DI.components;

import com.ezly.ezly_android.Utils.Helper.GAHelper;
import com.ezly.ezly_android.Internal.DI.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Chris on 4/06/15.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    //Exposed to sub-graphs.
    GAHelper gaHelper();
}
