package com.ezly.ezly_android.Presenter;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.Model.EventModel;
import com.ezly.ezly_android.UI.ViewInterFace.EventListView;
import com.ezly.ezly_android.UI.event.EzlyEventFragmentHost;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 11/10/16.
 */

public class EventListPresenter extends BasePresenter {
    public static final int TOP = 20;

    private EventListView mView;
    private EventModel eventModel;

    @Inject
    public EventListPresenter(EventModel eventModel) {
        this.eventModel = eventModel;
    }

    public void setmView(EventListView view) {
        this.mView = view;
    }

    public void searchEvent(int skip) {
        if(mView.getParentFragment() instanceof EzlyEventFragmentHost){
            List<EzlyEvent> events = ((EzlyEventFragmentHost)mView.getParentFragment()).getEvents();
            if(events != null){
                mView.onEventPrepared(events);
                return;
            }
        }

        mView.showRefreshLayout(true);
        Observable<List<EzlyEvent>> observable = eventModel.searchEvents(mView.getContext(), TOP, skip);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onEventPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void removeFavourite(EzlyEvent event) {
        eventModel.removeFavourite(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onFavouriteResponse(event, false), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void addFavourite(EzlyEvent event) {
        eventModel.addFavourite(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onFavouriteResponse(event, true), onFavouriteResponseError(event, true));
    }

    private Action1<Throwable> onFavouriteResponseError(final EzlyEvent event, final boolean isAdd) {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(mView != null){
                    if(isAdd){
                        mView.onAddFavourite(event, false);
                    }
                    else{
                        mView.onRemoveFavourite(event, false);
                    }
                }
            }
        };
    }

    private Action1<Response<EzlyBaseResponse>> onFavouriteResponse(final EzlyEvent event, final boolean isAdd) {
        return new Action1<Response<EzlyBaseResponse>>() {
            @Override
            public void call(Response<EzlyBaseResponse> response) {
                boolean isSuccess = (response.code() == 200) || response.code() == 204;
                if(mView != null){
                    if(isAdd){
                        mView.onAddFavourite(event, isSuccess);
                    }
                    else{
                        mView.onRemoveFavourite(event, isSuccess);
                    }
                }
            }
        };
    }

    private Action1<List<EzlyEvent>> onEventPrepared() {
        return new Action1<List<EzlyEvent>>() {
            @Override
            public void call(List<EzlyEvent> events) {
                mView.onEventPrepared(events);
            }
        };
    }

    public void reloadEvent() {
        mView.resetSearch();
        searchEvent(0);
    }
}
