package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyComment;

import java.util.ArrayList;

/**
 * Created by Johnnie on 29/10/16.
 */

public class GetCommentResponse extends EzlyBaseResponse {
    private ArrayList<EzlyComment> data;

    public EzlyComment getComment() {
        return data != null && data.size() > 0 ? data.get(0) : null;
    }

    public void setData(ArrayList<EzlyComment> data) {
        this.data = data;
    }
}
