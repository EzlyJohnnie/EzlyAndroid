package com.ezly.ezly_android.Model;

import com.ezly.ezly_android.DB.EzlyDBHelper;
import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.Data.EzlySetting;
import com.ezly.ezly_android.network.ServerHelper;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Johnnie on 20/10/16.
 */

public class CategoryModel {

    private ServerHelper serverHelper;
    private EzlyDBHelper dbHelper;

    /**
     * categories fo not often, so only request once per app start for checking updates.
     * also need to request again when language changed
     */
    private static boolean hasRequestedCategoriesFromServer;
    private static int currentLanguage;

    @Inject
    public CategoryModel(ServerHelper serverHelper, EzlyDBHelper dbHelper) {
        this.serverHelper = serverHelper;
        this.dbHelper = dbHelper;
    }

    public Observable<List<EzlyCategory>> getCategories(){
        Observable<List<EzlyCategory>> observable = null;
        try {
            if(shouldRequestFromServer()) {
                dbHelper.clearTable(EzlyCategory.class);
                currentLanguage = EzlySetting.getInstance().getLanguage();
                hasRequestedCategoriesFromServer = true;
                observable = serverHelper.getCategories()
                        .map(saveCategories());
            }
            else{
                observable = getCategoriesFromDB();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return observable;
    }

    private Func1<? super List<EzlyCategory>, List<EzlyCategory>> saveCategories() {
        return new Func1<List<EzlyCategory>, List<EzlyCategory>>() {
            @Override
            public List<EzlyCategory> call(List<EzlyCategory> ezlyCategories) {
                try {
                    dbHelper.getUserDao().create(ezlyCategories);
                } catch (SQLException e) {
                    hasRequestedCategoriesFromServer = false;
                    e.printStackTrace();
                }
                return ezlyCategories;
            }
        };
    }

    private Observable<List<EzlyCategory>> getCategoriesFromDB() throws SQLException {
        return Observable.from(dbHelper.getUserDao().queryForAll())
                .toList();
    }

    private boolean shouldRequestFromServer(){
        return currentLanguage != EzlySetting.getInstance().getLanguage() || !hasRequestedCategoriesFromServer;
    }

}
