package com.ezly.ezly_android.EzlyTestClasses;

import android.content.Context;

import com.ezly.ezly_android.Data.EzlyComment;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.Response.GetEventCommentsResponse;
import com.ezly.ezly_android.Data.Response.GetEventCommentsResponseData;
import com.ezly.ezly_android.EzlyTestClasses.MockJSON.MockSearchEventJson;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Data.EzlyToken;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.Response.GetJobsResponse;
import com.ezly.ezly_android.Data.Response.GetServiceResponse;
import com.ezly.ezly_android.network.ServerHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

public class TestServerHelper extends ServerHelper {
    public TestServerHelper(LocationHelper locationHelper, EzlySearchParam searchParam) {
        super(locationHelper, searchParam);
    }

    @Override
    public Observable<EzlyJob> getJobsRequest(HashMap<String, String> requestParam){
        GetJobsResponse getJobsResponse = new Gson().fromJson(MockSearchEventJson.getContent(), GetJobsResponse.class);
        Observable<EzlyJob> observer = Observable.just(getJobsResponse.getJobs())
                .flatMap(new Func1<List<EzlyJob>, Observable<EzlyJob>>() {
                    @Override
                    public Observable<EzlyJob> call(List<EzlyJob> jobs) {
                        if(jobs == null){
                            jobs = new ArrayList<EzlyJob>();
                        }
                        return Observable.from(jobs);
                    }
                });
        return observer;
    }

    @Override
    public Observable<EzlyService> getServiceRequest(HashMap<String, String> requestParam){
        GetServiceResponse getServiceResponse = new Gson().fromJson(MockSearchEventJson.getContent(), GetServiceResponse.class);
        Observable<EzlyService> observer = Observable.just(getServiceResponse.getServices())
                .flatMap(new Func1<List<EzlyService>, Observable<EzlyService>>() {
                    @Override
                    public Observable<EzlyService> call(List<EzlyService> services) {
                        if(services == null){
                            services = new ArrayList<EzlyService>();
                        }

                        return Observable.from(services);
                    }
                });
        return observer;
    }


    //Comments
    @Override
    public Observable<GetEventCommentsResponse> getJobComments(String jobID){
        return generateTestComments();
    }

    @Override
    public Observable<GetEventCommentsResponse> getServiceComments(String serviceID){
        return generateTestComments();
    }

    private Observable<GetEventCommentsResponse> generateTestComments(){
        Observable<GetEventCommentsResponse> observable = null;
        GetEventCommentsResponse getEventCommentsResponse = new GetEventCommentsResponse();
        GetEventCommentsResponseData data = new GetEventCommentsResponseData();

        ArrayList<EzlyComment> comments = new ArrayList<>();
        EzlyComment c1 = new EzlyComment();
        c1.setId("c1ID");
        c1.setText("c1 text");
        c1.setUserId("user 1");

        ArrayList<EzlyComment> c1Child = new ArrayList<>();
        EzlyComment c1_1 = new EzlyComment();
        c1_1.setId("c1_1ID");
        c1_1.setText("c1_1 text");
        c1_1.setUserId("user 2");
        c1Child.add(c1_1);
        c1.setChildComments(c1Child);

        EzlyComment c2 = new EzlyComment();
        c2.setId("c2ID");
        c2.setText("c2 text");
        c2.setUserId("user 2");

        EzlyComment c3 = new EzlyComment();
        c3.setId("c3ID");
        c3.setText("c3 text");
        c3.setUserId("user 1");

        comments.add(c1);
        comments.add(c2);
        comments.add(c3);


        ArrayList<EzlyUser> users = new ArrayList<>();
        EzlyUser u1 = new EzlyUser();
        u1.setId("user 1");

        EzlyUser u2 = new EzlyUser();
        u2.setId("user 2");

        EzlyUser u3 = new EzlyUser();
        u3.setId("user 3");

        users.add(u1);
        users.add(u2);
        users.add(u3);

        data.setComments(comments);
        data.setUsers(users);
        getEventCommentsResponse.setData(data);
        observable = Observable.just(getEventCommentsResponse);

        return observable;
    }

    @Override
    public Observable<Response<Void>> postJobComments(String jobID, HashMap<String, String> param){
        Response<Void> response = Response.success(null);
        Observable<Response<Void>> observable = Observable.just(response);
        return observable;
    }

    @Override
    public Observable<Response<Void>> postServiceComments(String serviceID, HashMap<String, String> param){
        Response<Void> response = Response.success(null);
        Observable<Response<Void>> observable = Observable.just(response);
        return observable;
    }

    @Override
    public Observable<EzlyToken> login(HashMap<String, String> body){
        EzlyToken token = new EzlyToken();
        token.setCurrentToken("dummy_token");
        return Observable.just(token);
    }

    @Override
    public Observable<EzlyUser> getMyInfo() {
        EzlyUser user = new EzlyUser();
        user.setId("userID");
        user.setDisplayName("my name");
        return Observable.just(user);
    }
}
