package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.Model.EventModel;
import com.ezly.ezly_android.UI.ViewInterFace.EventDetailView;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 29/10/16.
 */

public class EventDetailPresenter extends BasePresenter {

    private EventDetailView mView;
    private EventModel eventModel;

    @Inject
    public EventDetailPresenter(EventModel eventModel) {
        this.eventModel = eventModel;
    }

    public void setView(EventDetailView view) {
        this.mView = view;
    }

    public void getEventDetail(EzlyEvent event) {
        Observable<EzlyEvent> observable = eventModel.getEventDetail(event);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onEventPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    private Action1<EzlyEvent> onEventPrepared() {
        return new Action1<EzlyEvent>() {
            @Override
            public void call(EzlyEvent event) {
                if(mView != null){
                    mView.onEventPrepared(event);
                }
            }
        };
    }


    public void removeFavourite(EzlyEvent event) {
        eventModel.removeFavourite(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onFavouriteResponse(false), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void addFavourite(EzlyEvent event) {
        eventModel.addFavourite(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onFavouriteResponse(true), onFavouriteResponseError(true));
    }

    private Action1<Throwable> onFavouriteResponseError(final boolean isAdd) {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(mView != null){
                    if(isAdd){
                        mView.onAddFavourite(false);
                    }
                    else{
                        mView.onRemoveFavourite(false);
                    }
                }
            }
        };
    }

    private Action1<Response<EzlyBaseResponse>> onFavouriteResponse(final boolean isAdd) {
        return new Action1<Response<EzlyBaseResponse>>() {
            @Override
            public void call(Response<EzlyBaseResponse> response) {
                boolean isSuccess = (response.code() == 200) || response.code() == 204;
                if(mView != null){
                    if(isAdd){
                        mView.onAddFavourite(isSuccess);
                    }
                    else{
                        mView.onRemoveFavourite(isSuccess);
                    }
                }
            }
        };
    }
}
