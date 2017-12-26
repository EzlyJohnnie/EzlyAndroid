package com.ezly.ezly_android.Utils.Helper;

import android.content.Intent;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.ezly.ezly_android.Data.EzlyImage;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.Utils.ImageUtils;

import java.util.ArrayList;

import javax.inject.Singleton;

/**
 * Created by Johnnie on 15/06/16.
 */

@Singleton
public class MultiImagePickerHelper {
    private static MultiImagePickerHelper instance;

    public static MultiImagePickerHelper getInstance(){
        if(instance == null){
            instance = new MultiImagePickerHelper();
        }

        return instance;
    }

    private OnImageSelectedListener mListener;

    public void setListener(OnImageSelectedListener mListener) {
        this.mListener = mListener;
    }

    public void startPickImageFromGallery(EzlyBaseActivity activity, int limit, OnImageSelectedListener mListener){
        setListener(mListener);
        Intent intent = new Intent(activity, AlbumSelectActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, limit);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE);
    }


    public void processImageData(EzlyBaseActivity activity, Intent data){
        ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
        final ArrayList<EzlyImage> ezlyImages = new ArrayList<>();
        for(Image image : images){
            EzlyImage ezlyImage = wrapImage(image);
            ezlyImages.add(ezlyImage);
        }

        ImageUtils.processImage(activity, ezlyImages, new ImageUtils.OnImageProcessedListener() {
            @Override
            public void onImageProcessed(ArrayList<EzlyImage> images) {
                if(mListener != null){
                    mListener.onImagesSelected(ezlyImages);
                }
            }
        });

    }

    private EzlyImage wrapImage(Image image){
        EzlyImage ezlyImage = new EzlyImage();
        ezlyImage.setPath(image.path);
        ezlyImage.setName(image.name);
        ezlyImage.setId(image.id + "");
        return ezlyImage;
    }

    public interface OnImageSelectedListener{
        void onImagesSelected(ArrayList<EzlyImage> images);
    }
}
