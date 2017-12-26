package com.ezly.ezly_android.UI.BaseComponent;

import android.support.v4.app.Fragment;

import com.ezly.ezly_android.Utils.Helper.GAHelper;
import com.ezly.ezly_android.Internal.DI.components.ActivityComponent;
import com.ezly.ezly_android.Internal.DI.components.AppComponent;
import com.ezly.ezly_android.UI.MainActivity;

/**
 * Created by Johnnie on 9/07/16.
 */
public class EzlyBaseFragment extends Fragment {
    public GAHelper getGAHelper(){
        return ((EzlyBaseActivity)getActivity()).getGAHelper();
    }

    public AppComponent getApplicationComponent(){
        return ((EzlyBaseActivity)getActivity()).getApplicationComponent();
    }

    public ActivityComponent getActivityComponent(){
        return ((EzlyBaseActivity)getActivity()).getActivityComponent();
    }

    protected void showTabbar(boolean isShow) {
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).showBottomTabbar(isShow);
        }
    }
}
