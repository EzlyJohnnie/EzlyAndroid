package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Data.EzlyEvent;

import java.util.List;

/**
 * Created by Johnnie on 20/11/16.
 */

public interface PostDetailView extends BaseViewInterface{

    void onEventPrepared(EzlyEvent event);
    void onPosted(boolean isSuccess, EzlyEvent event);
    void onUpdateEvent(boolean isSuccess, EzlyEvent event);
    void onImageStartUploading(int imageIndex);
    void onImageUploading(int imageIndex, float progress);
    void onImageUploadCompleted(int imageIndex);
    void onImageUploadFailed(int imageIndex, String errorMsg);
    void onUploadCompleted(int totalSuccessCount);
    void onAddressPrepared(List<EzlyAddress> addresses);
    void onAddressAdd(boolean isSuccess, String message);

}
