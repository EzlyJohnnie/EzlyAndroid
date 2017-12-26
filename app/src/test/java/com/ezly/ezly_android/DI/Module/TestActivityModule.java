package com.ezly.ezly_android.DI.Module;

import android.content.Context;

import com.ezly.ezly_android.EzlyTestClasses.TestPermissionHelper;
import com.ezly.ezly_android.Utils.Helper.ImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.MultiImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.NotificationHelper;
import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.Internal.DI.PerActivity;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Model.CommentModel;
import com.ezly.ezly_android.Presenter.CommentPresenter;
import com.ezly.ezly_android.Presenter.LocationSelectMapPresenter;
import com.ezly.ezly_android.network.ServerHelper;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.ezly.ezly_android.EzlyTestClasses.TestServerHelper;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class TestActivityModule {
    private final Context context;
    private ServerHelper serverHelper;
    private CommentModel commentModel;

    public TestActivityModule(Context context) {
        this.context = context;
    }

    @Provides
    @PerActivity
    public Context provideContext(){
        return context;
    }

    @Provides
    @PerActivity
    public ServerHelper provideServerHelper(){
        if(serverHelper == null){
            serverHelper = new TestServerHelper(provideLocationHelper(), provideSearchParam());
        }

        return serverHelper;
    }

    @Provides
    @PerActivity
    public CommentModel provideCommentModel(){
        if(commentModel == null){
            commentModel = new CommentModel(provideServerHelper());
        }

        return commentModel;
    }

    @Provides @PerActivity
    public LocationServices provideLocationServices(){
        return LocationServices.getLocationServices(context);
    }

    @Provides @PerActivity
    public MemberHelper provideMemberHelper(){
        return MemberHelper.getInstance(provideServerHelper());
    }

    @Provides @PerActivity
    public LocationHelper provideLocationHelper(){
        return LocationHelper.getInstance(providePermissionHelper());
    }

    @Provides @PerActivity
    public EzlySearchParam provideSearchParam(){
        return EzlySearchParam.getInstance(provideLocationHelper());
    }

    @Provides @PerActivity
    public ImagePickerHelper provideImagePickerHelper(){
        return ImagePickerHelper.getInstance(providePermissionHelper());
    }

    @Provides @PerActivity
    public MultiImagePickerHelper provideMultiImagePickerHelper(){
        return MultiImagePickerHelper.getInstance();
    }

    @Provides @PerActivity
    public NotificationHelper provideNotificationHelper(){
        return NotificationHelper.getInstance();
    }

    @Provides @PerActivity
    public PermissionHelper providePermissionHelper(){
        return TestPermissionHelper.getInstance();
    }




    @Provides @PerActivity
    public CommentPresenter provideCommentPresenter(){
        return Mockito.spy(new CommentPresenter(provideCommentModel(), provideMemberHelper()));
    }

    @Provides @PerActivity
    public LocationSelectMapPresenter provideLocationSelectMapPresenter(){
        return Mockito.spy(new LocationSelectMapPresenter(provideLocationHelper(), provideLocationServices()));
    }

}
