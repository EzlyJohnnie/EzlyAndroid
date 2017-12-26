package com.ezly.ezly_android.Presenter;

import android.content.res.Resources;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyImage;
import com.ezly.ezly_android.Data.ProgressRequestBody;
import com.ezly.ezly_android.Model.AddressModel;
import com.ezly.ezly_android.Model.EventModel;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.ViewInterFace.PostDetailView;
import com.ezly.ezly_android.Utils.TextUtils;
import com.ezly.ezly_android.network.RequestErrorHandler;
import com.ezly.ezly_android.network.ServerHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 21/11/16.
 */

public class PostDetailPresenter extends BasePresenter{

    private EventModel eventModel;
    private AddressModel addressModel;
    private ServerHelper serverHelper;
    private PostDetailView mView;
    private long totalSizeToUpload;
    private long totalSizeUploaded;
    private int totalCompletedUpload;
    private int totalSuccessCount;
    private ArrayList<Call<ResponseBody>> uploadTasks;

    @Inject
    public PostDetailPresenter(EventModel eventModel, AddressModel addressModel, ServerHelper serverHelper) {
        this.eventModel = eventModel;
        this.addressModel = addressModel;
        this.serverHelper = serverHelper;
    }

    public void setView(PostDetailView mView) {
        this.mView = mView;
    }

    public void getMyAddressList(){
        addressModel.getMyAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAddressListPrepared(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void addAddress(EzlyAddress address){
        addressModel.addAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAddressAdd(), RequestErrorHandler.onRequestError(mView.getContext()));
    }

    public void deleteImagesForEvent(EzlyEvent event, ArrayList<EzlyImage> imagesToDelete) {
        for(EzlyImage imageToDelete : imagesToDelete){
            deleteImageForEvent(event, imageToDelete);
        }
    }

    public void deleteImageForEvent(EzlyEvent event, EzlyImage imageToDelete) {
        serverHelper.deleteImageFromEvent(mView.getContext(), event, imageToDelete)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onImageDelete(), RequestErrorHandler.onRequestError(mView.getContext()));
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

    private Action1<Response<Void>> onImageDelete() {
        return new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> voidResponse) {

            }
        };
    }

    private Action1<? super List<EzlyAddress>> onAddressListPrepared() {
        return new Action1<List<EzlyAddress>>() {
            @Override
            public void call(List<EzlyAddress> addresses) {
                if(mView != null){
                    mView.onAddressPrepared(addresses);
                }
            }
        };
    }

    public Action1<Response<Void>> onAddressAdd(){
        return new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> response) {
                if(mView != null){
                    Resources resources = mView.getContext().getResources();
                    boolean isSuccess = (response.code() - 200 > 0 && response.code() - 200 < 100);
                    String message;
                    if(isSuccess){
                        message = String.format(resources.getString(R.string.address_operation_format_str), resources.getString(R.string.address_operation_add));
                    }
                    else{
                        message = !TextUtils.isEmpty(response.message()) ?
                                response.message()
                                : String.format(resources.getString(R.string.address_operation_error_format_str), resources.getString(R.string.address_operation_add));
                    }

                    mView.onAddressAdd(isSuccess, message);
                }
            }
        };
    }

    public void postEvent(EzlyEvent event) {
        eventModel.postEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onPostCompleted(event), onPostFailed(event));
    }


    public void updateEvent(EzlyEvent event) {
        eventModel.updateEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onUpdateCompleted(event), onUpdateFailed(event));
    }

    private Action1<Response<Void>> onPostCompleted(final EzlyEvent event){
        return new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> response) {
                if(mView != null) {
                    if (response.code() == 200 || response.code() == 201) {
                        String eventID = response.headers().get("Location");
                        eventID = getEventID(eventID);
                        event.setId(eventID);
                        mView.onPosted(true, event);
                    }
                    else{
                        mView.onPosted(false, null);
                    }
                }
            }
        };
    }

    private Action1<Response<Void>> onUpdateCompleted(final EzlyEvent event){
        return new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> response) {
                if(mView != null) {
                    boolean isSuccess = response.code() - 200 > 0 && response.code() - 200 < 100;
                    mView.onUpdateEvent(isSuccess, event);
                }
            }
        };
    }

    private String getEventID(String str){
        String result;
        int startIndex = str.lastIndexOf("/");
        result = str.substring(startIndex + 1, str.length());

        return result;
    }

    private Action1<Throwable> onPostFailed(final EzlyEvent event){
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(mView != null){
                    mView.onPosted(false, null);
                }
            }
        };
    }

    private Action1<Throwable> onUpdateFailed(final EzlyEvent event){
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(mView != null){
                    mView.onUpdateEvent(false, null);
                }
            }
        };
    }


    public void postImage(final EzlyEvent event) {
        //TODO: clean up and move logic to model
        uploadTasks = new ArrayList<>();
        totalSizeToUpload = 0;
        totalSizeUploaded = 0;
        totalCompletedUpload = 0;
        totalSuccessCount = 0;


        getTotalSizeToUpload(event);
        for(int i = 0; i < event.getImagesForPost().size(); i++){
            final int imageIndex = i;
            EzlyImage image = event.getImagesForPost().get(i);
            Call<ResponseBody> call = serverHelper.uploadPhoto(event, image, new ProgressRequestBody.OnUploadListener() {
                @Override
                public void onUploading(long totalUploadedSize) {
                    totalSizeUploaded += totalUploadedSize;
                    if(mView != null){
                        mView.onImageUploading(imageIndex, (int)(totalSizeUploaded / (totalSizeToUpload / 100)));
                    }
                }
            });

            if(mView != null){
                mView.onImageStartUploading(imageIndex);
            }

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    uploadTasks.remove(call);
                    boolean isSuccess = response.code() - 200 < 100;
                    onSingleUploadComplete(imageIndex, isSuccess, "");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    uploadTasks.remove(call);
                    onSingleUploadComplete(imageIndex, false, t.getMessage());
                }

                private void onSingleUploadComplete(int imageIndex, boolean isSuccess, String errorMsg) {
                    totalCompletedUpload++;
                    if(mView != null){
                        if(isSuccess){
                            totalSuccessCount++;
                            mView.onImageUploadCompleted(imageIndex);
                        }
                        else{
                            mView.onImageUploadFailed(imageIndex, errorMsg);
                        }

                        if(totalCompletedUpload == event.getImagesForPost().size()) {
                            mView.onUploadCompleted(totalSuccessCount);
                        }
                    }


                }
            });

            uploadTasks.add(call);
        }
    }

    public void cancelUpload(){
        if(uploadTasks != null){
            for(Call call : uploadTasks){
                call.cancel();
            }
        }
    }

    private void getTotalSizeToUpload(EzlyEvent event) {
        for(EzlyImage image : event.getImagesForPost()){
            totalSizeToUpload += image.getImageFile().length();
        }
    }
}
