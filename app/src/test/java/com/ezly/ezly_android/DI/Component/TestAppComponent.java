package com.ezly.ezly_android.DI.Component;

import com.ezly.ezly_android.DI.Module.TestAppModule;
import com.ezly.ezly_android.Internal.DI.components.AppComponent;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {TestAppModule.class})
public interface TestAppComponent extends AppComponent{
}
