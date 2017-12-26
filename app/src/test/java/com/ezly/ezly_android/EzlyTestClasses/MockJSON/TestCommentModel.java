package com.ezly.ezly_android.EzlyTestClasses.MockJSON;

import com.ezly.ezly_android.Model.CommentModel;
import com.ezly.ezly_android.network.ServerHelper;

import javax.inject.Inject;

/**
 * Created by Johnnie on 26/12/17.
 */

public class TestCommentModel extends CommentModel {

    @Inject
    public TestCommentModel(ServerHelper serverHelper) {
        super(serverHelper);
    }


}
