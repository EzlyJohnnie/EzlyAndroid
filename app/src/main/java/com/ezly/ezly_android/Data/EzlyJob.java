package com.ezly.ezly_android.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnnie on 11/10/16.
 */

public class EzlyJob extends EzlyEvent{
    public EzlyJob() {
        super();
    }

    protected EzlyJob(Parcel in) {
        super(in);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyJob> CREATOR = new Parcelable.Creator<EzlyJob>() {
        @Override
        public EzlyJob createFromParcel(Parcel in) {
            return new EzlyJob(in);
        }

        @Override
        public EzlyJob[] newArray(int size) {
            return new EzlyJob[size];
        }
    };
}
