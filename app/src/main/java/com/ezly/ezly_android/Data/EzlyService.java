package com.ezly.ezly_android.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnnie on 11/10/16.
 */

public class EzlyService extends EzlyEvent{

    public EzlyService() {
        super();
    }

    protected EzlyService(Parcel in) {
        super(in);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyService> CREATOR = new Parcelable.Creator<EzlyService>() {
        @Override
        public EzlyService createFromParcel(Parcel in) {
            return new EzlyService(in);
        }

        @Override
        public EzlyService[] newArray(int size) {
            return new EzlyService[size];
        }
    };
}
