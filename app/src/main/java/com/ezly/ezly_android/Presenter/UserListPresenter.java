package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Model.UserModel;
import com.ezly.ezly_android.UI.ViewInterFace.UserListView;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 13/11/16.
 */

public class UserListPresenter extends BasePresenter {
    public static final int TOP = 20;

    private UserListView mView;
    private UserModel userModel;
    private MemberHelper memberHelper;
    private EzlySearchParam searchParam;

    @Inject
    public UserListPresenter(UserModel userModel,
                             MemberHelper memberHelper,
                             EzlySearchParam searchParam)
    {
        this.userModel = userModel;
        this.memberHelper = memberHelper;
        this.searchParam = searchParam;
    }

    public void setView(UserListView mView) {
        this.mView = mView;
    }

    public void searchUser() {
        mView.showRefreshLayout(true);
        userModel.searchUsers(searchParam.getSearchStr())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onUserPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void getILikedUser(String userID, int skip) {
        mView.showRefreshLayout(true);
        userModel.getLikedUsers(userID, TOP, skip)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onUserPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    /**
     * for login user
     */
    public void getMyLikedUser(int skip) {
        mView.showRefreshLayout(true);
        memberHelper.getMyLikedUser(TOP, skip)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onUserPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<List<EzlyUser>> onUserPrepared() {
        return new Action1<List<EzlyUser>>() {
            @Override
            public void call(List<EzlyUser> users){
                ArrayList<EzlyUser> userAry = new ArrayList<>();
                for(EzlyUser user : users){
                    userAry.add(user);
                }
                mView.onUserPrepared(userAry);
            }
        };
    }

    public void getUserLikedMe(String userID) {

    }

    /**
     * for login user
     */
    public void getUserLikedMe() {

    }
}
