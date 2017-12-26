/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ezly.ezly_android.Internal.DI.modules;


import android.content.Context;

import com.ezly.ezly_android.Utils.Helper.ImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.MultiImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.NotificationHelper;
import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.Internal.DI.PerActivity;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.network.ServerHelper;
import com.mapbox.mapboxsdk.location.LocationServices;

import dagger.Module;
import dagger.Provides;


@Module
public class ActivityModule {
  private final Context context;
  private ServerHelper serverHelper;

  public ActivityModule(Context context) {
    this.context = context;
  }

  @Provides @PerActivity
  public ServerHelper provideServerHelper(){
    if(serverHelper == null){
      serverHelper = new ServerHelper(provideLocationHelper(), provideSearchParam());
    }

    return serverHelper;
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
    return PermissionHelper.getInstance();
  }

}
