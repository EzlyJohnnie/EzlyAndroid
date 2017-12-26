package com.ezly.ezly_android.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Johnnie on 20/10/16.
 */
public class EzlyCategory implements Parcelable {
    @Expose private String id;
    @Expose private String code;
    @Expose private String name;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EzlyCategory() {
    }

    protected EzlyCategory(Parcel in) {
        id = in.readString();
        code = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(code);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyCategory> CREATOR = new Parcelable.Creator<EzlyCategory>() {
        @Override
        public EzlyCategory createFromParcel(Parcel in) {
            return new EzlyCategory(in);
        }

        @Override
        public EzlyCategory[] newArray(int size) {
            return new EzlyCategory[size];
        }
    };
}