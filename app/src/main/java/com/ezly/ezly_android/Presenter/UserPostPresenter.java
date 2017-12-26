package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Model.EventModel;
import com.ezly.ezly_android.UI.ViewInterFace.UserPostView;
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

public class UserPostPresenter extends BasePresenter {

    private UserPostView mView;
    private EventModel eventModel;
    private MemberHelper memberHelper;
    private LocationHelper locationHelper;

    @Inject
    public UserPostPresenter(EventModel eventModel,
                             MemberHelper memberHelper,
                             LocationHelper locationHelper)
    {
        this.memberHelper = memberHelper;
        this.eventModel = eventModel;
        this.locationHelper = locationHelper;
    }

    public void setView(UserPostView mView) {
        this.mView = mView;
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

    public void getMyPostHistory() {
        memberHelper.getMyPostHistory()
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

    public void getFavourites() {
        eventModel.getFavourites()
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

    public void setEventInvisible(EzlyEvent event) {
        setEventVisibility(event, false);
    }

    public void setEventVisible(EzlyEvent event) {
        setEventVisibility(event, true);
    }

    private void setEventVisibility(EzlyEvent event, boolean isVisible){
        eventModel.setEventVisible(event, isVisible)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onEventVisibleSetComplete(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<Response<Void>> onEventVisibleSetComplete() {
        return new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> response) {
                if(mView != null){
                    boolean isSuccess = ((response.code() - 200 > 0) && (response.code() - 200 < 100));
                    mView.onSetEventVisibilitySuccess();
                }
            }
        };
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

    public void removeFavourite(EzlyEvent event, int position) {
        eventModel.removeFavourite(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onFavouriteResponse(position), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<Response<EzlyBaseResponse>> onFavouriteResponse(final int position) {
        return new Action1<Response<EzlyBaseResponse>>() {
            @Override
            public void call(Response<EzlyBaseResponse> response) {
                boolean isSuccess = (response.code() == 200) || response.code() == 204;
                if(mView != null){
                    mView.onRemoveFavourite(isSuccess, position);
                }
            }
        };
    }

    public void deleteEvent(EzlyEvent event) {
        eventModel.deleteEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
