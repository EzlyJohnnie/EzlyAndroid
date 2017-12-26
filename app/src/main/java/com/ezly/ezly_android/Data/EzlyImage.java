package com.ezly.ezly_android.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Johnnie on 21/11/16.
 */

public class EzlyImage implements Parcelable {
    private float progress;
    private int drawableID;
    private String path;
    private String id;
    private String thumbnailUrl;
    private String name;
    private String url;
    private File imageFile;

    public EzlyImage() {
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getDrawableID() {
        return drawableID;
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getImageFile(){
        if(imageFile == null){
            imageFile = new File(path);
        }
        return imageFile;
    }

    public String getFileTypeString(){
        return getFileTypeString(getFileExtension(getImageFile().getName()));
    }

    public String getFileExtension(String fileName) {
        if (fileName.indexOf("?") > -1) {
            fileName = fileName.substring(0, fileName.indexOf("?"));
        }
        if (fileName.lastIndexOf(".") == -1) {
            return "";
        } else {
            String ext = fileName.substring(fileName.lastIndexOf("."));
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    public String getFileTypeString(String fileExt){
        String typeStr = "";
        switch (fileExt){
            case ".png":
                typeStr = "image/png";
                break;
            case ".gif":
                typeStr = "image/gif";
                break;
            case ".jpg":
                typeStr = "image/jpg";
                break;
            case ".jpeg":
                typeStr = "image/jpeg";
                break;
            case ".bmp":
                typeStr = "image/bmp";
                break;
        }
        if(typeStr.isEmpty()){
            typeStr = "*/*";
        }
        return typeStr;
    }

    protected EzlyImage(Parcel in) {
        progress = in.readFloat();
        drawableID = in.readInt();
        path = in.readString();
        id = in.readString();
        thumbnailUrl = in.readString();
        name = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(progress);
        dest.writeInt(drawableID);
        dest.writeString(path);
        dest.writeString(id);
        dest.writeString(thumbnailUrl);
        dest.writeString(name);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyImage> CREATOR = new Parcelable.Creator<EzlyImage>() {
        @Override
        public EzlyImage createFromParcel(Parcel in) {
            return new EzlyImage(in);
        }

        @Override
        public EzlyImage[] newArray(int size) {
            return new EzlyImage[size];
        }
    };
}