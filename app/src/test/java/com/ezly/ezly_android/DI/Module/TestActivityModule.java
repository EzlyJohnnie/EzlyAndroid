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
    private LocationServices locationServices;
    private MemberHelper memberHelper;
    private LocationHelper locationHelper;
    private EzlySearchParam searchParam;
    private ImagePickerHelper imagePickerHelper;
    private MultiImagePickerHelper multiImagePickerHelper;
    private NotificationHelper notificationHelper;
    private TestPermissionHelper permissionHelper;


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
            serverHelper = Mockito.spy(new TestServerHelper(provideLocationHelper(), provideSearchParam()));
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
        if(locationServices == null){
            locationServices = Mockito.spy(LocationServices.getLocationServices(context));;
        }
        return locationServices;

    }

    @Provides @PerActivity
    public MemberHelper provideMemberHelper(){
        if(memberHelper == null){
            memberHelper = Mockito.spy(MemberHelper.getInstance(provideServerHelper()));
        }
        return memberHelper;

    }

    @Provides @PerActivity
    public LocationHelper provideLocationHelper(){
        if(locationHelper == null){
            locationHelper = Mockito.spy(LocationHelper.getInstance(providePermissionHelper()));
        }
        return locationHelper;

    }

    @Provides @PerActivity
    public EzlySearchParam provideSearchParam(){
        if(searchParam == null){
            searchParam = Mockito.spy(EzlySearchParam.getInstance(provideLocationHelper()));
        }
        return searchParam;

    }

    @Provides @PerActivity
    public ImagePickerHelper provideImagePickerHelper(){
        if(imagePickerHelper == null){
            imagePickerHelper = Mockito.spy(ImagePickerHelper.getInstance(providePermissionHelper()));
        }
        return imagePickerHelper;

    }

    @Provides @PerActivity
    public MultiImagePickerHelper provideMultiImagePickerHelper(){
        if(multiImagePickerHelper == null){
            multiImagePickerHelper = Mockito.spy(MultiImagePickerHelper.getInstance());
        }
        return multiImagePickerHelper;

    }

    @Provides @PerActivity
    public NotificationHelper provideNotificationHelper(){
        if(notificationHelper == null){
            notificationHelper = Mockito.spy(NotificationHelper.getInstance());;
        }
        return notificationHelper;

    }

    @Provides @PerActivity
    public PermissionHelper providePermissionHelper(){
        if(permissionHelper == null){
            permissionHelper = Mockito.spy(TestPermissionHelper.getInstance());
        }
        return permissionHelper;

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
