package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Model.EventModel;
import com.ezly.ezly_android.Model.UserModel;
import com.ezly.ezly_android.UI.ViewInterFace.UserDetailView;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 14/11/16.
 */

public class UserDetailPresenter extends BasePresenter {

    private UserDetailView mView;
    private UserModel userModel;
    private EventModel eventModel;
    private MemberHelper memberHelper;
    private LocationHelper locationHelper;

    @Inject
    public UserDetailPresenter(UserModel userModel,
                               EventModel eventModel,
                               MemberHelper memberHelper,
                               LocationHelper locationHelper)
    {
        this.memberHelper = memberHelper;
        this.userModel = userModel;
        this.eventModel = eventModel;
        this.locationHelper = locationHelper;
    }

    public void setView(UserDetailView mView) {
        this.mView = mView;
    }

    public void getUser(String userID) {
        userModel.getUserInfo(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onUserPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void getMyInfo() {
        memberHelper.getMyInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onUserPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<EzlyUser> onUserPrepared() {
        return new Action1<EzlyUser>() {
            @Override
            public void call(EzlyUser user) {
                mView.onUserPrepared(user);
            }
        };
    }

    public void getPostHistory(String userID) {
        eventModel.getPostHistory(userID)
                .map(new Func1<EzlyEvent, EzlyEvent>() {
                    @Override
                    public EzlyEvent call(EzlyEvent ezlyEvent) {
                        ezlyEvent.getDistanceDiff(mView.getContext(), locationHelper.getLastKnownLocation());
                        return ezlyEvent;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onEventPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<List<EzlyEvent>> onEventPrepared() {
        return new Action1<List<EzlyEvent>>() {
            @Override
            public void call(List<EzlyEvent> events) {
                ArrayList<EzlyEvent> eventAry = new ArrayList<>();
                eventAry.addAll(events);
                mView.onEventPrepared(eventAry);
            }
        };
    }

    public void likeUser(String userID) {
        userModel.likeUser(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onLikeUserResponse(), RequestErrorHandler.onRequestError(mView.getContext()));
    }


    public void dislikeUser(String userID) {
        userModel.unlikeUser(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onLikeUserResponse(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<Response<EzlyBaseResponse>> onLikeUserResponse(){
        return new Action1<Response<EzlyBaseResponse>>() {
            @Override
            public void call(Response<EzlyBaseResponse> responseResponse) {
                boolean isSuccess = responseResponse.code() == 200 || responseResponse.code() == 204;
                if(mView != null){
                    mView.onLikeUser(isSuccess);
                }
            }
        };
    }

}
