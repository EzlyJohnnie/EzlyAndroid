package com.ezly.ezly_android.TestCase;

import android.content.Context;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyLocation;
import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.Data.EzlySearchParam;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.ezly.ezly_android.Data.EzlySearchParam.SEARCH_MODE_SERVICE;
import static org.junit.Assert.assertEquals;

public class SearchParamTest extends BaseTest {

    @Inject Context context;
    @Inject EzlySearchParam searchParam;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        getTestActivityComponent().inject(this);
    }

    @After
    public void reset() {
        searchParam.reset();
    }

    @Test
    public void testSaveSearchParam() {
        ArrayList<EzlyCategory> selectedCategories = new ArrayList<>();
        EzlyCategory category = new EzlyCategory();
        category.setId("i am id");
        category.setName("i am name");
        category.setCode("i am code");
        selectedCategories.add(category);
        searchParam.setSelectedCategories(selectedCategories);

        searchParam.setSearchStr("i am search string");
        searchParam.setMapZoomLevel(12);
        searchParam.setSearchMode(SEARCH_MODE_SERVICE);

        EzlyAddress address = new EzlyAddress();
        address.setId("address id");
        address.setLocation(new EzlyLocation(1.213f, 2.345f));
        searchParam.setSearchLocation(address);

        searchParam.toSharedPreference(context);

        searchParam.reset();
        searchParam = searchParam.fromSharePreference(context);

        assertEquals(searchParam.getSelectedCategories().size(), 1);
        assertEquals(searchParam.getSelectedCategories().get(0).getId(), "i am id");
        assertEquals(searchParam.getSelectedCategories().get(0).getName(), "i am name");
        assertEquals(searchParam.getSelectedCategories().get(0).getCode(), "i am code");

        assertEquals(searchParam.getSearchStr(), "i am search string");
        assertEquals(searchParam.getMapZoomLevel(), 12);
        assertEquals(searchParam.getSearchMode(), SEARCH_MODE_SERVICE);

        assertEquals(searchParam.getSearchLocation().getId(), "address id");
        assertEquals(searchParam.getSearchLocation().getLocation().getLatitude(), 1.213f, 0);
        assertEquals(searchParam.getSearchLocation().getLocation().getLongitude(), 2.345f, 0);
    }

}
