package com.ezly.ezly_android.Model;

import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Johnnie on 20/10/16.
 */

public class CategoryModel {

    private ServerHelper serverHelper;

    @Inject
    public CategoryModel(ServerHelper serverHelper) {
        this.serverHelper = serverHelper;
    }

    public Observable<List<EzlyCategory>> getCategories(){
        return serverHelper.getCategories();
    }
}
