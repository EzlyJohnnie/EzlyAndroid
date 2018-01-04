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
package com.ezly.ezly_android.Internal.DI.components;


import com.ezly.ezly_android.DB.EzlyDBHelper;
import com.ezly.ezly_android.Internal.DI.PerActivity;
import com.ezly.ezly_android.Internal.DI.modules.ActivityModule;
import com.ezly.ezly_android.UI.Address.EzlyAddressDetailFragment;
import com.ezly.ezly_android.UI.Address.EzlyMyAddressListFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.Notification.EzlyNotificationFragment;
import com.ezly.ezly_android.UI.Post.EzlyLocationSelectMapFragment;
import com.ezly.ezly_android.UI.Post.EzlyNewPostFragment;
import com.ezly.ezly_android.UI.Post.EzlyPostCategoryFragment;
import com.ezly.ezly_android.UI.Setting.EzlySettingFragment;
import com.ezly.ezly_android.UI.User.EzlyMyProfileFragment;
import com.ezly.ezly_android.UI.User.EzlyUserDetailFragment;
import com.ezly.ezly_android.UI.User.EzlyUserHostFragment;
import com.ezly.ezly_android.UI.User.EzlyUserListFragment;
import com.ezly.ezly_android.UI.User.EzlyUserPostFragment;
import com.ezly.ezly_android.UI.event.EzlyEventFragmentHost;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailFragment;
import com.ezly.ezly_android.UI.event.List.EzlyEventListFragment;
import com.ezly.ezly_android.UI.event.Map.EzlyMapMainFragment;
import com.ezly.ezly_android.UI.login.EzlyLoginFragment;
import com.ezly.ezly_android.UI.search.EzlySearchFragment;
import com.ezly.ezly_android.UI.welcome.EzlyWelcomeActivity;
import com.ezly.ezly_android.network.ServerHelper;

import dagger.Component;


@PerActivity
@Component(dependencies = {AppComponent.class}, modules = ActivityModule.class)
public interface ActivityComponent {
  void inject(EzlyBaseActivity activity);
  void inject(MainActivity activity);
  void inject(EzlyWelcomeActivity activity);

  void inject(EzlyEventFragmentHost fragment);
  void inject(EzlyUserHostFragment fragment);

  void inject(EzlyMapMainFragment fragment);
  void inject(EzlyEventListFragment fragment);
  void inject(EzlySearchFragment fragment);
  void inject(EzlyEventDetailFragment fragment);
  void inject(EzlyLoginFragment fragment);
  void inject(EzlyUserListFragment fragment);
  void inject(EzlyMyProfileFragment fragment);
  void inject(EzlyUserPostFragment fragment);
  void inject(EzlySettingFragment fragment);
  void inject(EzlyPostCategoryFragment fragment);
  void inject(EzlyNewPostFragment fragment);
  void inject(EzlyUserDetailFragment fragment);
  void inject(EzlyLocationSelectMapFragment fragment);
  void inject(EzlyMyAddressListFragment fragment);
  void inject(EzlyAddressDetailFragment fragment);
  void inject(EzlyNotificationFragment fragment);

  //Exposed to sub-graphs.EzlyUserListFragment
  ServerHelper serverHelper();
}
