package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Data.EzlyCategory;

import java.util.List;

/**
 * Created by Johnnie on 20/10/16.
 */
public interface EzlySearchView extends BaseViewInterface{

    void onTopCategoryPrepared(List<EzlyCategory> categories);

}
