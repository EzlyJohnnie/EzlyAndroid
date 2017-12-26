package com.ezly.ezly_android.Utils.Helper.LocationHerpler;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Johnnie on 23/03/17.
 */

public class EzlyLocation implements Parcelable {

    @Expose private float latitude;
    @Expose private float longitude;

    public EzlyLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public boolean isValidLocation(){
        return latitude != 0 && longitude != 0;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }






    protected EzlyLocation(Parcel in) {
        latitude = in.readFloat();
        longitude = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyLocation> CREATOR = new Parcelable.Creator<EzlyLocation>() {
        @Override
        public EzlyLocation createFromParcel(Parcel in) {
            return new EzlyLocation(in);
        }

        @Override
        public EzlyLocation[] newArray(int size) {
            return new EzlyLocation[size];
        }
    };
}