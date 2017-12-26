package com.ezly.ezly_android.network;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
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

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Johnnie on 23/08/15.
 */
public interface Ezly_API {

    String ENDPOINT_GET_CATEGORY = "api/Categories";

    String ENDPOINT_GET_JOB = "view/jobs";
    String ENDPOINT_GET_JOB_DETAIL = "view/jobs/{id}";
    String ENDPOINT_DELETE_JOB = "api/jobs/{id}";
    String ENDPOINT_VISIBLE_JOB = "api/jobs/{id}";
    String ENDPOINT_UPDATE_JOB = "api/jobs/{id}";


    String ENDPOINT_POST_JOB = "api/jobs";
    String ENDPOINT_POST_SERVICE = "api/services";
    String ENDPOINT_DELETE_SERVICE = "api/services/{id}";
    String ENDPOINT_UPDATE_SERVICE = "api/services/{id}";
    String ENDPOINT_VISIBLE_SERVICE = "api/services/{id}";

    String ENDPOINT_GET_SERVICE = "view/services";
    String ENDPOINT_GET_SERVICE_DETAIL = "view/services/{id}";

    String ENDPOINT_GET_USER_INFO = "view/users/{id}";
    String ENDPOINT_GET_MY_INFO = "view/users/me";
    String ENDPOINT_SEARCH_USER = "view/users";

    String ENDPOINT_REGISTER_DEVICE = "api/userdevices";

    String ENDPOINT_MY_LIKED_USER = "view/users/likes/me";
    String ENDPOINT_LIKED_USER = "view/users/likes/{id}";

    String ENDPOINT_LIKE_USER = "api/Likes/{userId}";

    String ENDPOINT_FAVOURITE_JOBS = "view/jobs/favourites";
    String ENDPOINT_FAVOURITE_SERVICE = "view/services/favourites";

    String ENDPOINT_MY_JOBS_HISTORY = "view/jobs/mine";
    String ENDPOINT_MY_SERVICE_HISTORY = "view/services/mine";
    String ENDPOINT_USER_JOB_HISTORY = "view/jobs/users/{userId}";
    String ENDPOINT_USER_SERVICE_HISTORY = "view/services/users/{userId}";

    String ENDPOINT_JOB_COMMENTS = "view/jobs/{id}/comments";
    String ENDPOINT_SERVICE_COMMENTS = "view/services/{id}/comments";
    String ENDPOINT_POST_SERVICE_COMMENTS = "api/comments/service/{id}";
    String ENDPOINT_POST_JOB_COMMENTS = "api/comments/job/{id}";

    String ENDPOINT_LIKE_SERVICE = "api/favourites/service/{serviceId}";
    String ENDPOINT_LIKE_JOB = "api/favourites/job/{jobId}";

    String ENDPOINT_POST_JOB_PHOTO = "api/photos/job/{id}";
    String ENDPOINT_DELETE_JOB_PHOTO = "api/photos/job/{id}/{photoID}";
    String ENDPOINT_POST_SERVICE_PHOTO = "api/photos/service/{id}";
    String ENDPOINT_DELETE_SERVICE_PHOTO = "api/photos/service/{id}/{photoID}";

    String ENDPOINT_GET_ADDRESS = "api/Addresses";
    String ENDPOINT_OPERATE_ADDRESS = "api/Addresses/{id}";



    //categories
    @GET(ENDPOINT_GET_CATEGORY)
    Observable<GetCategoryResponse> getCategories(@Header("Authorization") String token, @QueryMap Map<String, String> paramsMap);

    //Jobs
    @GET(ENDPOINT_GET_JOB)
    Observable<GetJobsResponse> getJobs(@Header("Authorization") String token, @QueryMap Map<String, String> paramsMap);

    @GET(ENDPOINT_GET_JOB_DETAIL)
    Observable<GetJobDetailResponse> getJobDetail(@Header("Authorization") String token, @Path("id") String id);

    @POST(ENDPOINT_POST_JOB)
    Observable<Response<Void>> postJob(@Header("Authorization") String token, @Body Map<String, String> paramsMap);

    @PUT(ENDPOINT_UPDATE_JOB)
    Observable<Response<Void>> updateJob(@Header("Authorization") String token, @Path("id") String jobID, @Body Map<String, String> paramsMap);

    @DELETE(ENDPOINT_DELETE_JOB)
    Observable<Response<Void>> deleteJob(@Header("Authorization") String token, @Path("id") String id);

    @PATCH(ENDPOINT_VISIBLE_JOB)
    @FormUrlEncoded
    Observable<Response<Void>> visibleJob(@Header("Authorization") String token, @Path("id") String id, @Field("active") String isAcive);

    //Services
    @GET(ENDPOINT_GET_SERVICE)
    Observable<GetServiceResponse> getServices(@Header("Authorization") String token, @QueryMap Map<String, String> paramsMap);

    @GET(ENDPOINT_GET_SERVICE_DETAIL)
    Observable<GetServiceDetailResponse> getServiceDetail(@Header("Authorization") String token, @Path("id") String id);

    @POST(ENDPOINT_POST_SERVICE)
    Observable<Response<Void>> postService(@Header("Authorization") String token, @Body Map<String, String> paramsMap);

    @PUT(ENDPOINT_UPDATE_SERVICE)
    Observable<Response<Void>> updateService(@Header("Authorization") String token, @Path("id") String serviceID, @Body Map<String, String> paramsMap);

    @DELETE(ENDPOINT_DELETE_SERVICE)
    Observable<Response<Void>> deleteService(@Header("Authorization") String token, @Path("id") String id);

    @PATCH(ENDPOINT_VISIBLE_SERVICE)
    @FormUrlEncoded
    Observable<Response<Void>> visibleService(@Header("Authorization") String token, @Path("id") String id, @Field("active") String isAcive);



    //User
    @GET(ENDPOINT_GET_USER_INFO)
    Observable<GetUserInfoResponse> getUserInfo(@Header("Authorization") String token, @Path("id") String userId);

    @GET(ENDPOINT_GET_MY_INFO)
    Observable<GetUserInfoResponse> getMyInfo(@Header("Authorization") String token);

    @GET(ENDPOINT_SEARCH_USER)
    Observable<GetUsersResponse> searchUsers(@Header("Authorization") String token, @QueryMap Map<String, String> paramsMap);

    @GET(ENDPOINT_MY_LIKED_USER)
    Observable<GetUsersResponse> getMyLikedUser(@Header("Authorization") String token, @QueryMap Map<String, String> params);

    @GET(ENDPOINT_LIKED_USER)
    Observable<GetUsersResponse> getLikedUser(@Header("Authorization") String token, @Path("id") String userId, @QueryMap Map<String, String> params);

    @POST(ENDPOINT_LIKE_USER)
    Observable<Response<EzlyBaseResponse>> likeUser(@Header("Authorization") String token, @Path("userId") String userId);

    @DELETE(ENDPOINT_LIKE_USER)
    Observable<Response<EzlyBaseResponse>> unlikeUser(@Header("Authorization") String token, @Path("userId") String userId);


    //Comments
    @GET(ENDPOINT_JOB_COMMENTS)
    Observable<GetEventCommentsResponse> getJobComments(@Header("Authorization") String token, @Path("id") String userId);

    @GET(ENDPOINT_SERVICE_COMMENTS)
    Observable<GetEventCommentsResponse> getServiceComments(@Header("Authorization") String token, @Path("id") String userId);

    @POST(ENDPOINT_POST_SERVICE_COMMENTS)
    Observable<Response<Void>> postServiceComments(@Header("Authorization") String token, @Path("id") String userId, @Body HashMap<String, String> param);

    @POST(ENDPOINT_POST_JOB_COMMENTS)
    Observable<Response<Void>> postJobComments(@Header("Authorization") String token, @Path("id") String userId, @Body HashMap<String, String> param);

    //Register device
    @POST(ENDPOINT_REGISTER_DEVICE)
    Observable<EzlyBaseResponse> registerDevice(@Header("Authorization") String token, @Body Map<String, String> paramsMap);

    //Favourite
    @GET(ENDPOINT_FAVOURITE_JOBS)
    Observable<GetJobsResponse> getFavouriteJobs(@Header("Authorization") String token);

    @GET(ENDPOINT_FAVOURITE_SERVICE)
    Observable<GetServiceResponse> getFavouriteServices(@Header("Authorization") String token);

    @POST(ENDPOINT_LIKE_SERVICE)
    Observable<Response<EzlyBaseResponse>> addServiceFavourite(@Header("Authorization") String token, @Path("serviceId") String serviceId);

    @DELETE(ENDPOINT_LIKE_SERVICE)
    Observable<Response<EzlyBaseResponse>> removeServiceFavourite(@Header("Authorization") String token, @Path("serviceId") String serviceId);

    @POST(ENDPOINT_LIKE_JOB)
    Observable<Response<EzlyBaseResponse>> addJobFavourite(@Header("Authorization") String token, @Path("jobId") String serviceId);

    @DELETE(ENDPOINT_LIKE_JOB)
    Observable<Response<EzlyBaseResponse>> removeJobFavourite(@Header("Authorization") String token, @Path("jobId") String serviceId);

    //Post history
    @GET(ENDPOINT_MY_JOBS_HISTORY)
    Observable<GetJobsResponse> getMyJobsHistory(@Header("Authorization") String token);

    @GET(ENDPOINT_MY_SERVICE_HISTORY)
    Observable<GetServiceResponse> getMyServicesHistory(@Header("Authorization") String token);


    @GET(ENDPOINT_USER_JOB_HISTORY)
    Observable<GetJobsResponse> getJobsHistoryForUser(@Header("Authorization") String token, @Path("userId") String serviceId);

    @GET(ENDPOINT_USER_SERVICE_HISTORY)
    Observable<GetServiceResponse> getServicesHistoryForUser(@Header("Authorization") String token, @Path("userId") String serviceId);

    //photo
    @Multipart
    @POST(ENDPOINT_POST_JOB_PHOTO)
    Call<ResponseBody> uploadJobPhoto(@Header("Authorization") String token,
                                      @Path("id") String jobID,
                                      @Part() MultipartBody.Part file);

    @Multipart
    @POST(ENDPOINT_POST_SERVICE_PHOTO)
    Call<ResponseBody> uploadServerPhoto(@Header("Authorization") String token,
                                         @Path("id") String serverID,
                                         @Part() MultipartBody.Part file);

    @DELETE(ENDPOINT_DELETE_JOB_PHOTO)
    Observable<Response<Void>> deleteJobPhoto(@Header("Authorization") String token, @Path("id") String jobID, @Path("photoID") String photoID);

    @DELETE(ENDPOINT_DELETE_SERVICE_PHOTO)
    Observable<Response<Void>> deleteServicePhoto(@Header("Authorization") String token, @Path("id") String serverID, @Path("photoID") String photoID);


    //address
    @GET(ENDPOINT_GET_ADDRESS)
    Observable<GetAddressResponse> getMyAddress(@Header("Authorization") String token);

    @POST(ENDPOINT_GET_ADDRESS)
    Observable<Response<Void>> addAddress(@Header("Authorization") String token, @Body EzlyAddress param);

    @PUT(ENDPOINT_OPERATE_ADDRESS)
    Observable<Response<Void>> updateAddress(@Header("Authorization") String token, @Path("id") String addressID, @Body EzlyAddress param);

    @DELETE(ENDPOINT_OPERATE_ADDRESS)
    Observable<Response<Void>> deleteAddress(@Header("Authorization") String token, @Path("id") String addressID);

}