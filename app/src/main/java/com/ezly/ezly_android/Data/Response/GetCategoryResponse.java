package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyCategory;

import java.util.ArrayList;

/**
 * Created by Johnnie on 11/10/16.
 */

public class GetCategoryResponse extends EzlyBaseResponse {
    private ArrayList<EzlyCategory> data;

    public ArrayList<EzlyCategory> getCategories() {
        return data;
    }

    public void setCategories(ArrayList<EzlyCategory> categories) {
        this.data = categories;
    }
}
