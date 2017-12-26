package com.ezly.ezly_android.Utils.Helper;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Data.EzlyImage;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.Utils.Constant;
import com.ezly.ezly_android.Utils.FileUtils;
import com.ezly.ezly_android.Utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Singleton;

/**
 * Created by Johnnie on 15/06/16.
 */

@Singleton
public class ImagePickerHelper {
    private static ImagePickerHelper instance;
    private PermissionHelper permissionHelper;

    public static ImagePickerHelper getInstance(PermissionHelper permissionHelper){
        if(instance == null){
            instance = new ImagePickerHelper();
            instance.permissionHelper = permissionHelper;
        }

        return instance;
    }

    private OnImageSelectedListener mListener;

    public void setListener(OnImageSelectedListener mListener) {
        this.mListener = mListener;
    }

    public void takePhoto(final EzlyBaseActivity activity, final OnImageSelectedListener mListener){
        permissionHelper.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted(String[] permissions) {
                ImagePickerHelper.this.mListener = mListener;
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                File file = new File(activity.getExternalCacheDir(),  "uploadImage.jpg");
                Uri outputFileUri = Uri.fromFile(file);
                cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);


                activity.startActivityForResult(cameraIntent, Constant.PICK_IMAGE_CODE);
            }

            @Override
            public void onDenied(String[] permissions, int[] grantResults) {
            }

            @Override
            public void onShouldShowRationale(String permission) {
                SingleToast.makeText(activity, activity.getResources().getString(R.string.camera_permission_rationale_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startPickImageFromGallery(EzlyBaseActivity activity, OnImageSelectedListener mListener){
        setListener(mListener);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constant.PICK_IMAGE_CODE);
    }

    public void processImageData(EzlyBaseActivity activity, Intent data){

        File imageFile = new File(activity.getExternalCacheDir(),  "uploadImage.jpg");
        Uri uri = Uri.fromFile(imageFile);
        String selectedFilePath = FileUtils.getPath(activity, uri);
        final File file = new File(selectedFilePath);
        EzlyImage ezlyImages = wrapImage(file);

        ImageUtils.processImage(activity, ezlyImages, new ImageUtils.OnImageProcessedListener() {
            @Override
            public void onImageProcessed(ArrayList<EzlyImage> images) {
                if(mListener != null){
                    mListener.onImageSelected(images);
                }
            }
        });
    }

    private EzlyImage wrapImage(File file){
        EzlyImage ezlyImage = new EzlyImage();
        ezlyImage.setPath(file.getAbsolutePath());
        ezlyImage.setName(file.getName());
        return ezlyImage;
    }

    public interface OnImageSelectedListener{
        void onImageSelected(ArrayList<EzlyImage> images);
    }
}
