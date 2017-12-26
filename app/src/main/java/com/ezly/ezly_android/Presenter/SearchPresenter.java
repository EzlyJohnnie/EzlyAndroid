package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.Model.CategoryModel;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.ViewInterFace.EzlySearchView;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 20/10/16.
 */

public class SearchPresenter extends BasePresenter {
    private EzlySearchView mView;
    private CategoryModel categoryModel;

    @Inject
    public SearchPresenter(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    private List<EzlyCategory> categories;

    public void setView(EzlySearchView mView) {
        this.mView = mView;
    }

    public void getTopCategory(boolean addCategoryAll) {
        if(categories != null && categories.size() > 0){
            mView.onTopCategoryPrepared(categories);
            return;
        }

        categoryModel.getCategories().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onCategoryPrepared(addCategoryAll), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<List<EzlyCategory>> onCategoryPrepared(final boolean addCategoryAll) {
        return new Action1<List<EzlyCategory>>() {
            @Override
            public void call(List<EzlyCategory> categories) {
                if(addCategoryAll){
                    EzlyCategory categoryAll = new EzlyCategory();
                    categoryAll.setName(mView.getContext().getResources().getString(R.string.all_category));
                    categoryAll.setCode("all");
                    categories.add(0, categoryAll);
                }
                mView.onTopCategoryPrepared(categories);
            }
        };
    }
}
