package com.ezly.ezly_android.Model;

import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Data.Response.GetEventCommentsResponse;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.HashMap;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Johnnie on 29/10/16.
 */

public class CommentModel {

    private ServerHelper serverHelper;

    @Inject
    public CommentModel(ServerHelper serverHelper) {
        this.serverHelper = serverHelper;
    }

    public Observable<GetEventCommentsResponse> getComments(EzlyEvent event){
        Observable<GetEventCommentsResponse> observable = null;
        if(event instanceof EzlyJob){
            observable = serverHelper.getJobComments(event.getId());
        }
        else if(event instanceof EzlyService){
            observable = serverHelper.getServiceComments(event.getId());
        }
        return observable;
    }

    public Observable<Response<Void>> postComment(EzlyEvent event, HashMap<String, String> param){
        Observable<Response<Void>> observable = null;
        if(event instanceof  EzlyJob){
            observable = serverHelper.postJobComments(event.getId(), param);
        }
        else if(event instanceof  EzlyService){
            observable = serverHelper.postServiceComments(event.getId(), param);
        }
        return observable;
    }
}
