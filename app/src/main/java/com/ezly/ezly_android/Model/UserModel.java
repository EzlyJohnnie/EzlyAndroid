package com.ezly.ezly_android.Model;

import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Johnnie on 13/11/16.
 *
 * User model for other users
 * use {@link com.ezly.ezly_android.Utils.Helper.MemberHelper} for current login user related apis
 */
public class UserModel {
    private ServerHelper serverHelper;

    @Inject
    public UserModel(ServerHelper serverHelper) {
        this.serverHelper = serverHelper;
    }

    public Observable<List<EzlyUser>> searchUsers(String searchText){
        HashMap<String, String> params = new HashMap<>();
        params.put("Text", searchText);

        return serverHelper.searchUsers(params);
    }

    public Observable<EzlyUser> getUserInfo(String userID){
        return serverHelper.getUserInfo(userID);
    }

    public Observable<List<EzlyUser>> getLikedUsers(String userID, int top, int skip){
        HashMap<String, String> requestParam = new HashMap<>();
        if(top >= 0){
            requestParam.put("Top", top  + "");
        }

        if(skip >= 0){
            requestParam.put("Skip", skip  + "");
        }

        return serverHelper.getLikedUsers(userID, requestParam);
    }

    public Observable<Response<EzlyBaseResponse>> likeUser(String userID){
        return serverHelper.likeUser(userID);
    }

    public Observable<Response<EzlyBaseResponse>> unlikeUser(String userID){
        return serverHelper.unlikeUser(userID);
    }
}
