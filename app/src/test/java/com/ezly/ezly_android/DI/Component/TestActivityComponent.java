package com.ezly.ezly_android.DI.Component;

import android.content.Context;

import com.ezly.ezly_android.DI.Module.TestActivityModule;
import com.ezly.ezly_android.Internal.DI.PerActivity;
import com.ezly.ezly_android.Internal.DI.components.ActivityComponent;
import com.ezly.ezly_android.TestCase.CommentPresenterTest;
import com.ezly.ezly_android.TestCase.LocationSelectMapPresenterTest;
import com.ezly.ezly_android.TestCase.SearchParamTest;

import dagger.Component;

@PerActivity
@Component(dependencies = {TestAppComponent.class}, modules = TestActivityModule.class)
public interface TestActivityComponent extends ActivityComponent{
    void inject(SearchParamTest searchParamTest);
    void inject(CommentPresenterTest commentPresenterTest);
    void inject(LocationSelectMapPresenterTest presenter);


    //expose
    Context provideContext();
}
