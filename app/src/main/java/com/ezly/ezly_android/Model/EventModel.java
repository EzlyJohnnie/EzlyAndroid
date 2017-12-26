package com.ezly.ezly_android.Model;

import android.content.Context;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.TextUtils;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Johnnie on 1/10/16.
 */

public class EventModel {

    private static final String PARAM_KEY_CATEGORY_ID = "CategoryId";
    private static final String PARAM_KEY_SEARCH_TEXT = "Text";

    private ServerHelper serverHelper;
    private EzlySearchParam searchParam;
    private LocationHelper locationHelper;

    @Inject
    public EventModel(ServerHelper serverHelper,
                      EzlySearchParam searchParam,
                      LocationHelper locationHelper) {
        this.serverHelper = serverHelper;
        this.searchParam = searchParam;
        this.locationHelper = locationHelper;
    }

    public Observable<Response<EzlyBaseResponse>> addFavourite(EzlyEvent event){
        Observable<Response<EzlyBaseResponse>> observable;
        if(event instanceof EzlyService){
            observable = serverHelper.addServiceFavourite(event);
        }
        else{
            observable = serverHelper.addJobFavourite(event);
        }

        return observable;
    }

    public Observable<Response<EzlyBaseResponse>> removeFavourite(EzlyEvent event){
        Observable<Response<EzlyBaseResponse>> observable;
        if(event instanceof EzlyService){
            observable = serverHelper.removeServiceFavourite(event);
        }
        else{
            observable = serverHelper.removeJobFavourite(event);
        }

        return observable;
    }

    /**
     * set top = -1, skip = -1 for load all data without pagination
     * @param context
     * @param top count each page
     * @param skip skip count
     * @return
     */
    public Observable<List<EzlyEvent>> searchEvents(final Context context, int top, int skip){
        String categoryID = searchParam.getSelectedCategories().size() > 0 ?
                searchParam.getSelectedCategories().get(0).getId() : "";
        String searchText = searchParam.getSearchStr();


        HashMap<String, String> requestParam = new HashMap<>();
        if(!TextUtils.isEmpty(categoryID)){
            requestParam.put(PARAM_KEY_CATEGORY_ID, categoryID);
        }

        if(!TextUtils.isEmpty(searchText)){
            requestParam.put(PARAM_KEY_SEARCH_TEXT, searchText);
        }

        if(top >= 0){
            requestParam.put("Top", top  + "");
        }

        if(skip >= 0){
            requestParam.put("Skip", skip  + "");
        }

        Observable<List<EzlyEvent>> eventsObservable = null;
        switch (searchParam.getSearchMode()){
            case EzlySearchParam.SEARCH_MODE_JOB:
                eventsObservable = serverHelper.getJobsRequest(requestParam)
                        .map(new Func1<EzlyJob, EzlyEvent>() {
                            @Override
                            public EzlyEvent call(EzlyJob ezlyJob) {
                                ezlyJob.getDistanceDiff(context, locationHelper.getLastKnownLocation());
                                return ezlyJob;
                            }
                        }).toSortedList();
                break;

            case EzlySearchParam.SEARCH_MODE_SERVICE:
                eventsObservable = serverHelper.getServiceRequest(requestParam)
                        .map(new Func1<EzlyService, EzlyEvent>() {
                            @Override
                            public EzlyEvent call(EzlyService ezlyService) {
                                ezlyService.getDistanceDiff(context, locationHelper.getLastKnownLocation());
                                return ezlyService;
                            }
                        }).toSortedList();
                break;
        }

        return eventsObservable;
    }

    public Observable<EzlyEvent> getEventDetail(EzlyEvent event){
        Observable<EzlyEvent> eventObservable = null;
        if(event instanceof EzlyJob){
            eventObservable = serverHelper.getJobDetail(event.getId())
                    .map(new Func1<EzlyJob, EzlyEvent>() {
                        @Override
                        public EzlyEvent call(EzlyJob ezlyJob) {
                            return ezlyJob;
                        }
                    });
        }
        else if(event instanceof EzlyService){
            eventObservable = serverHelper.getServiceDetail(event.getId())
                    .map(new Func1<EzlyService, EzlyEvent>() {
                        @Override
                        public EzlyEvent call(EzlyService ezlyService) {
                            return ezlyService;
                        }
                    });
        }

        return eventObservable;
    }

    public Observable<Response<Void>> setEventVisible(EzlyEvent event, boolean isVisible){
        Observable<Response<Void>> eventObservable = null;
        if(event instanceof EzlyJob){
            eventObservable = serverHelper.visibleJob(event.getId(), isVisible);
        }
        else if(event instanceof EzlyService){
            eventObservable = serverHelper.visibleService(event.getId(), isVisible);
        }

        return eventObservable;
    }

    public Observable<Response<Void>> postEvent(EzlyEvent event){
        Observable<Response<Void>> observable = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("title", event.getTitle());
        params.put("description", TextUtils.isEmpty(event.getDescription()) ? "" : event.getDescription());
        params.put("categoryId", event.getCategory().getId());
        params.put("startDate", event.getStartDate());
        params.put("endDate", event.getEndDate());
        params.put("addressId", event.getAddress().getId());

        if(event instanceof EzlyService){
            observable = serverHelper.postService(params);
        }
        else  if(event instanceof EzlyJob){
            observable = serverHelper.postJob(params);
        }

        return observable;
    }

    public Observable<Response<Void>> updateEvent(EzlyEvent event){
        Observable<Response<Void>> observable = null;
        HashMap<String, String> params = new HashMap<>();
        params.put("title", event.getTitle());
        params.put("description", TextUtils.isEmpty(event.getDescription()) ? "" : event.getDescription());
        params.put("categoryId", event.getCategory().getId());
        params.put("startDate", event.getStartDate());
        params.put("endDate", event.getEndDate());
        params.put("addressId", event.getAddress().getId());
        params.put("id", event.getId());


        if(event instanceof EzlyService){
            observable = serverHelper.updateService(event.getId(), params);
        }
        else  if(event instanceof EzlyJob){
            observable = serverHelper.updateJob(event.getId(), params);
        }

        return observable;
    }

    public Observable<Response<Void>> deleteEvent(EzlyEvent event){
        Observable<Response<Void>> observable = null;
        if(event instanceof EzlyService){
            observable = serverHelper.deleteService(event.getId());
        }
        else  if(event instanceof EzlyJob){
            observable = serverHelper.deleteJob(event.getId());
        }

        return observable;
    }

    public Observable<EzlyEvent> getFavourites() {
        Observable<List<EzlyEvent>> favouriteJobsOb = serverHelper.getFavouriteJobs().toList();
        Observable<List<EzlyEvent>> favouriteServiceOb = serverHelper.getFavouriteServices().toList();

        return Observable
                .combineLatest(favouriteJobsOb, favouriteServiceOb, new Func2<List<EzlyEvent>, List<EzlyEvent>, List<EzlyEvent>>() {
                    @Override
                    public List<EzlyEvent> call(List<EzlyEvent> events, List<EzlyEvent> events2) {
                        List<EzlyEvent> favourites = new ArrayList<EzlyEvent>();
                        favourites.addAll(events);
                        favourites.addAll(events2);
                        return favourites;
                    }
                })
                .flatMap(new Func1<List<EzlyEvent>, Observable<EzlyEvent>>() {
                    @Override
                    public Observable<EzlyEvent> call(List<EzlyEvent> ezlyEvents) {
                        return Observable.from(ezlyEvents);
                    }
                });
    }

    public Observable<EzlyEvent> getPostHistory(String userID) {
        Observable<List<EzlyEvent>> postJobsHistoryOb = serverHelper.getJobsHistoryForUser(userID).toList();
        Observable<List<EzlyEvent>> postServiceHistoryOb = serverHelper.getServiceHistoryForUser(userID).toList();

        return Observable
                .combineLatest(postJobsHistoryOb, postServiceHistoryOb, new Func2<List<EzlyEvent>, List<EzlyEvent>, List<EzlyEvent>>() {
                    @Override
                    public List<EzlyEvent> call(List<EzlyEvent> ezlyEvents, List<EzlyEvent> ezlyEvents2) {
                        List<EzlyEvent> favourites = new ArrayList<EzlyEvent>();
                        favourites.addAll(ezlyEvents);
                        favourites.addAll(ezlyEvents2);
                        return favourites;
                    }
                })
                .flatMap(new Func1<List<EzlyEvent>, Observable<EzlyEvent>>() {
                    @Override
                    public Observable<EzlyEvent> call(List<EzlyEvent> ezlyEvents) {
                        return Observable.from(ezlyEvents);
                    }
                });
    }
}
