package com.ezly.ezly_android.network;

import android.content.Context;
import android.os.Build;

import com.ezly.ezly_android.BuildConfig;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyImage;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Data.EzlySetting;
import com.ezly.ezly_android.Data.EzlyToken;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.ProgressRequestBody;
import com.ezly.ezly_android.Data.Response.EzlyBaseResponse;
import com.ezly.ezly_android.Data.Response.GetAddressResponse;
import com.ezly.ezly_android.Data.Response.GetCategoryResponse;
import com.ezly.ezly_android.Data.Response.GetEventCommentsResponse;
import com.ezly.ezly_android.Data.Response.GetJobDetailResponse;
import com.ezly.ezly_android.Data.Response.GetJobsResponse;
import com.ezly.ezly_android.Data.Response.GetServiceDetailResponse;
import com.ezly.ezly_android.Data.Response.GetServiceResponse;
import com.ezly.ezly_android.Data.Response.GetUserInfoResponse;
import com.ezly.ezly_android.Data.Response.GetUsersResponse;
import com.ezly.ezly_android.Data.Response.WeChatTokenResponse;
import com.ezly.ezly_android.Utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;


/**
 * Created by Johnnie on 21/08/15.
 */
public class ServerHelper {
    public static final String LOGIN_SERVICE_FACEBOOK = "Facebook";
    public static final String LOGIN_SERVICE_WECHAT   = "WeChat";
    public static final String LOGIN_SERVICE_GOOGLE   = "Google";

    private static final String HOST ;
    private static final String IDENTITY_HOST ;
    private static final String WECHAT_HOST ;

    public static final String CLIENT_ID;
    public static final String CLIENT_SECRET;


    static{
        if(BuildConfig.DEBUG){
            HOST = "http://13.54.199.110/TitanApi/";
            IDENTITY_HOST = "http://13.54.199.110/CentrifugeID/";
            WECHAT_HOST = "https://api.weixin.qq.com/";

            CLIENT_ID =  "45CBB74F-7B3C-4C55-8E62-59031D81A38E";
            CLIENT_SECRET =  "7miswaRyHH6fD9LxJm1FJQKYONaLIG";
        }
        else{
            HOST = "http://13.54.199.110/TitanApi/";
            IDENTITY_HOST = "http://13.54.199.110/CentrifugeID/";
            WECHAT_HOST = "https://api.weixin.qq.com/";

            CLIENT_ID =  "45CBB74F-7B3C-4C55-8E62-59031D81A38E";
            CLIENT_SECRET =  "7miswaRyHH6fD9LxJm1FJQKYONaLIG";
        }
    }

    private Ezly_API api;
    private Ezly_IdentityAPI identityAPI;
    private WeChatAPI weChatAPI;

    protected LocationHelper locationHelper;
    protected EzlySearchParam searchParam;

    public static String currentToken;


    public ServerHelper(LocationHelper locationHelper,
                        EzlySearchParam searchParam)
    {
        this.locationHelper = locationHelper;
        this.searchParam = searchParam;
        initApiClient();

        Retrofit identityRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(IDENTITY_HOST)
                .build();
        identityAPI = identityRetrofit.create(Ezly_IdentityAPI.class);

        Retrofit weChatRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WECHAT_HOST)
                .build();
        weChatAPI = weChatRetrofit.create(WeChatAPI.class);
    }

    private void initApiClient(){
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder builder = chain.request()
                                .newBuilder();
                        if(EzlySetting.getInstance().getLanguage() == EzlySetting.LANGUAGE_CHINESE){
                            builder.addHeader("Accept-Language", "zh");
                        }
                        Request request = builder.build();

                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(HOST)
                .client(httpClient)
                .build();
        api = retrofit.create(Ezly_API.class);

    }

    private String getTokenStr(){
        if(!TextUtils.isEmpty(currentToken)){
            return "Bearer " + currentToken;
        }
        return "";
    }



    //Jobs
    public Observable<EzlyJob> getJobsRequest(HashMap<String, String> requestParam){

        Observable<GetJobsResponse> observable = api.getJobs(getTokenStr(), requestParam);
        return observable.flatMap(new Func1<GetJobsResponse, Observable<EzlyJob>>() {
                    @Override
                    public Observable<EzlyJob> call(GetJobsResponse getJobsResponse) {
                        ArrayList<EzlyJob> jobs = getJobsResponse.getJobs();
                        if(jobs == null){
                            jobs = new ArrayList<EzlyJob>();
                        }
                        return Observable.from(jobs);
                    }
                });
    }

    public Observable<EzlyJob> getJobDetail(String eventID){
        return api
                .getJobDetail(getTokenStr(), eventID).map(new Func1<GetJobDetailResponse, EzlyJob>() {
                    @Override
                    public EzlyJob call(GetJobDetailResponse getJobDetailResponse) {
                        return getJobDetailResponse.getJob();
                    }
                });
    }

    public Observable<Response<EzlyBaseResponse>> addJobFavourite(EzlyEvent event){
        return api.addJobFavourite(getTokenStr(), event.getId());
    }

    public Observable<Response<EzlyBaseResponse>> removeJobFavourite(EzlyEvent event){
        return api.removeJobFavourite(getTokenStr(), event.getId());
    }

    public Observable<Response<Void>> visibleJob(String jobID, boolean isVisible){
        return api.visibleJob(getTokenStr(), jobID, isVisible ? "true" : "false");
    }

    public Observable<Response<Void>> postJob(HashMap<String, String> params){
        return api.postJob(getTokenStr(), params);
    }

    public Observable<Response<Void>> updateJob(String eventID, HashMap<String, String> params){
        return api.updateJob(getTokenStr(), eventID, params);
    }
    public Observable<Response<Void>> deleteJob(String jobID){
        return api.deleteJob(getTokenStr(), jobID);
    }


    //Services
    public Observable<EzlyService> getServiceRequest(HashMap<String, String> requestParam){
        Observable<GetServiceResponse> observable = api.getServices(getTokenStr(), requestParam);

        return observable
                .flatMap(new Func1<GetServiceResponse, Observable<EzlyService>>() {
                    @Override
                    public Observable<EzlyService> call(GetServiceResponse getServiceResponse) {
                        ArrayList<EzlyService> services = getServiceResponse.getServices();
                        if(services == null){
                            services = new ArrayList<EzlyService>();
                        }

                        return Observable.from(services);
                    }
                });
    }

    public Observable<EzlyService> getServiceDetail(String eventID){
        return api.getServiceDetail(getTokenStr(), eventID)
                .map(new Func1<GetServiceDetailResponse, EzlyService>() {
                    @Override
                    public EzlyService call(GetServiceDetailResponse getServiceDetailResponse) {
                        return getServiceDetailResponse.getService();
                    }
                });
    }

    public Observable<Response<EzlyBaseResponse>> addServiceFavourite(EzlyEvent event){
        return api.addServiceFavourite(getTokenStr(), event.getId());
    }

    public Observable<Response<EzlyBaseResponse>> removeServiceFavourite(EzlyEvent event){
        return api.removeServiceFavourite(getTokenStr(), event.getId());
    }

    public Observable<Response<Void>> visibleService(String serviceID, boolean isVisible){
        return api.visibleService(getTokenStr(), serviceID, isVisible ? "true" : "false");
    }

    public Observable<Response<Void>> postService(HashMap<String, String> params){
        return api.postService(getTokenStr(), params);
    }

    public Observable<Response<Void>> updateService(String eventID, HashMap<String, String> params){
        return api.updateService(getTokenStr(), eventID, params);
    }

    public Observable<Response<Void>> deleteService(String serviceID){
        return api.deleteService(getTokenStr(), serviceID);
    }



    //category
    public Observable<List<EzlyCategory>> getCategories() {
        HashMap<String, String> requestParam = new HashMap<>();

        return api.getCategories(getTokenStr(), requestParam)
                .flatMap(new Func1<GetCategoryResponse, Observable<EzlyCategory>>() {
                    @Override
                    public Observable<EzlyCategory> call(GetCategoryResponse getCategoryResponse) {
                        return Observable.from(getCategoryResponse.getCategories());
                    }
                })
                .toList();
    }

    //User
    public Observable<EzlyToken> login(HashMap<String, String> body){

        return identityAPI.login(body)
                .doOnNext(new Action1<EzlyToken>() {
                    @Override
                    public void call(EzlyToken token) {
                        //TODO: error handle
                        currentToken = token.getCurrentToken();
                    }
                });
    }

    public Observable<EzlyBaseResponse> registerDevice(String deviceToken){
        HashMap<String, String> body = new HashMap<>();
        body.put("token", deviceToken);
        body.put("type", String.format("%s %s", Build.MANUFACTURER, Build.MODEL));

        return api.registerDevice(getTokenStr(), body);
    }

    public Observable<EzlyUser> getUserInfo(String userID){
        return api.getUserInfo(getTokenStr(), userID)
                .map(new Func1<GetUserInfoResponse, EzlyUser>() {
                    @Override
                    public EzlyUser call(GetUserInfoResponse getUserInfoResponse) {
                        return getUserInfoResponse.getUser();
                    }
                });
    }

    public Observable<EzlyUser> getMyInfo(){
        return api.getMyInfo(getTokenStr())
                .map(new Func1<GetUserInfoResponse, EzlyUser>() {
                    @Override
                    public EzlyUser call(GetUserInfoResponse getUserInfoResponse) {
                        return getUserInfoResponse.getUser();
                    }
                });
    }

    public Observable<List<EzlyUser>> searchUsers(HashMap<String, String> params){
        return api.searchUsers(getTokenStr(), params)
                .flatMap(new Func1<GetUsersResponse, Observable<List<EzlyUser>>>() {
                    @Override
                    public Observable<List<EzlyUser>> call(GetUsersResponse usersResponse) {
                        return Observable.from(usersResponse.getData())
                                .toList();
                    }
                });
    }

    public Observable<List<EzlyUser>> getLikedUsers(String userID, HashMap<String, String> requestParam){
        return api.getLikedUser(getTokenStr(), userID, requestParam)
                .flatMap(new Func1<GetUsersResponse, Observable<List<EzlyUser>>>() {
                    @Override
                    public Observable<List<EzlyUser>> call(GetUsersResponse usersResponse) {
                        return Observable.from(usersResponse.getData())
                                .toList();
                    }
                });
    }

    public Observable<List<EzlyUser>> getMyLikedUser(HashMap<String, String> requestParam){
        return api.getMyLikedUser(getTokenStr(), requestParam)
                .flatMap(new Func1<GetUsersResponse, Observable<List<EzlyUser>>>() {
                    @Override
                    public Observable<List<EzlyUser>> call(GetUsersResponse usersResponse) {
                        return Observable.from(usersResponse.getData())
                                .toList();
                    }
                });
    }

    public Observable<Response<EzlyBaseResponse>> likeUser(String userID){
        return api.likeUser(getTokenStr(), userID);
    }

    public Observable<Response<EzlyBaseResponse>> unlikeUser(String userID){
        return api.unlikeUser(getTokenStr(), userID);
    }


    //Favourite
    public Observable<EzlyEvent> getFavouriteJobs(){
        return api.getFavouriteJobs(getTokenStr())
                .flatMap(new Func1<GetJobsResponse, Observable<EzlyJob>>() {
                    @Override
                    public Observable<EzlyJob> call(GetJobsResponse getJobsResponse) {
                        ArrayList<EzlyJob> jobs = getJobsResponse.getJobs();
                        if(jobs == null){
                            jobs = new ArrayList<EzlyJob>();
                        }
                        return Observable.from(jobs);
                    }
                })
                .map(new Func1<EzlyJob, EzlyEvent>() {
                    @Override
                    public EzlyEvent call(EzlyJob ezlyJob) {
                        return ezlyJob;
                    }
                });
    }

    public Observable<EzlyEvent> getFavouriteServices(){
        return api.getFavouriteServices(getTokenStr())
                .flatMap(new Func1<GetServiceResponse, Observable<EzlyService>>() {
                    @Override
                    public Observable<EzlyService> call(GetServiceResponse getServiceResponse) {
                        ArrayList<EzlyService> services = getServiceResponse.getServices();
                        if(services == null){
                            services = new ArrayList<EzlyService>();
                        }
                        return Observable.from(services);
                    }
                })
                .map(new Func1<EzlyService, EzlyEvent>() {
                    @Override
                    public EzlyEvent call(EzlyService ezlyService) {
                        return ezlyService;
                    }
                });
    }


    //Post history
    public Observable<EzlyEvent> getMyJobsHistory(){
        return api.getMyJobsHistory(getTokenStr())
                .flatMap(new Func1<GetJobsResponse, Observable<EzlyJob>>() {
                    @Override
                    public Observable<EzlyJob> call(GetJobsResponse getJobsResponse) {
                        ArrayList<EzlyJob> jobs = getJobsResponse.getJobs();
                        if(jobs == null){
                            jobs = new ArrayList<EzlyJob>();
                        }
                        return Observable.from(jobs);
                    }
                })
                .map(new Func1<EzlyJob, EzlyEvent>() {
                    @Override
                    public EzlyEvent call(EzlyJob ezlyJob) {
                        return ezlyJob;
                    }
                });
    }

    public Observable<EzlyEvent> getMyServicesHistory(){
        return api.getMyServicesHistory(getTokenStr())
                .flatMap(new Func1<GetServiceResponse, Observable<EzlyService>>() {
                    @Override
                    public Observable<EzlyService> call(GetServiceResponse getServiceResponse) {
                        ArrayList<EzlyService> services = getServiceResponse.getServices();
                        if(services == null){
                            services = new ArrayList<EzlyService>();
                        }
                        return Observable.from(services);
                    }
                })
                .map(new Func1<EzlyService, EzlyEvent>() {
                    @Override
                    public EzlyEvent call(EzlyService ezlyService) {
                        return ezlyService;
                    }
                });
    }

    public Observable<EzlyEvent> getJobsHistoryForUser(String userID){
        return api.getJobsHistoryForUser(getTokenStr(), userID)
                .flatMap(new Func1<GetJobsResponse, Observable<EzlyJob>>() {
                    @Override
                    public Observable<EzlyJob> call(GetJobsResponse getJobsResponse) {
                        ArrayList<EzlyJob> jobs = getJobsResponse.getJobs();
                        if(jobs == null){
                            jobs = new ArrayList<EzlyJob>();
                        }
                        return Observable.from(jobs);
                    }
                })
                .map(new Func1<EzlyJob, EzlyEvent>() {
                    @Override
                    public EzlyEvent call(EzlyJob ezlyJob) {
                        return ezlyJob;
                    }
                });
    }

    public Observable<EzlyEvent> getServiceHistoryForUser(String userID){
        return api.getServicesHistoryForUser(getTokenStr(), userID)
                .flatMap(new Func1<GetServiceResponse, Observable<EzlyService>>() {
                    @Override
                    public Observable<EzlyService> call(GetServiceResponse getServiceResponse) {
                        ArrayList<EzlyService> services = getServiceResponse.getServices();
                        if(services == null){
                            services = new ArrayList<EzlyService>();
                        }
                        return Observable.from(services);
                    }
                })
                .map(new Func1<EzlyService, EzlyEvent>() {
                    @Override
                    public EzlyService call(EzlyService service) {
                        return service;
                    }
                });
    }


    //Comments
    public Observable<GetEventCommentsResponse> getJobComments(String jobID){
        return api.getJobComments(getTokenStr(), jobID);
    }

    public Observable<GetEventCommentsResponse> getServiceComments(String serviceID){
        return api.getServiceComments(getTokenStr(), serviceID);
    }

    public Observable<Response<Void>> postJobComments(String jobID, HashMap<String, String> param){
        return  api.postJobComments(getTokenStr(), jobID, param);
    }

    public Observable<Response<Void>> postServiceComments(String serviceID, HashMap<String, String> param){
        return  api.postServiceComments(getTokenStr(), serviceID, param);
    }

    //Photo
    public Call<ResponseBody> uploadPhoto(EzlyEvent event, EzlyImage image, ProgressRequestBody.OnUploadListener listener){
        ProgressRequestBody body = new ProgressRequestBody(MediaType.parse(image.getFileTypeString()), image.getImageFile(), listener);
        Call<ResponseBody> call = null;
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", image.getName(), body);
        if(event instanceof EzlyJob){
            call = api.uploadJobPhoto(getTokenStr(), event.getId(), filePart);
        }
        else if(event instanceof EzlyService){
            call = api.uploadServerPhoto(getTokenStr(), event.getId(),filePart);
        }
        return call;
    }

    public Observable<Response<Void>> deleteImageFromEvent(Context context, EzlyEvent event, EzlyImage image){
        Observable<Response<Void>> observable = null;
        if(event instanceof  EzlyJob){
            observable = api.deleteJobPhoto(getTokenStr(), event.getId(), image.getId());
        }
        else if(event instanceof  EzlyService){
            observable = api.deleteServicePhoto(getTokenStr(), event.getId(), image.getId());
        }
        return observable;
    }

    //Address
    public Observable<List<EzlyAddress>> getMyAddress(){
        return api.getMyAddress(getTokenStr())
                .map(new Func1<GetAddressResponse, List<EzlyAddress>>() {
                    @Override
                    public List<EzlyAddress> call(GetAddressResponse getAddressResponse) {
                        return getAddressResponse.getAddress();
                    }
                });
    }

    public Observable<Response<Void>> addAddress(EzlyAddress address){
        return api.addAddress(getTokenStr(), address);
    }

    public Observable<Response<Void>> updateAddress(EzlyAddress address){
        return api.updateAddress(getTokenStr(), address.getId(), address);
    }

    public Observable<Response<Void>> deleteAddress(String addressID){
        return api.deleteAddress(getTokenStr(), addressID);
    }

    //weChat
    public Observable<WeChatTokenResponse> getWeChatToken(HashMap<String, String> param){
        return weChatAPI.getWeChatToken(param);
    }


}
