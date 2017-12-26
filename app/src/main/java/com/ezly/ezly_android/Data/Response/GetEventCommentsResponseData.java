package com.ezly.ezly_android.Data.Response;

import com.ezly.ezly_android.Data.EzlyComment;
import com.ezly.ezly_android.Data.EzlyUser;

import java.util.ArrayList;

/**
 * Created by Johnnie on 29/11/16.
 */

public class GetEventCommentsResponseData {
    private ArrayList<EzlyComment> comments;
    private ArrayList<EzlyUser> users;

    public ArrayList<EzlyComment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<EzlyComment> comments) {
        this.comments = comments;
    }

    public ArrayList<EzlyUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<EzlyUser> users) {
        this.users = users;
    }
}
