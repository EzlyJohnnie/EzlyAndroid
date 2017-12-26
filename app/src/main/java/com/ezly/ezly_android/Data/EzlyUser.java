package com.ezly.ezly_android.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnnie on 29/10/16.
 */
public class EzlyUser implements Parcelable {
    private String id;
    private String displayName;
    private String avatarUrl;
    private int numOfFavourites;
    private int numOfLikes;
    private int numOfJobs;
    private int numOfServices;
    private boolean canLike;

    public EzlyUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getNumOfFavourites() {
        return numOfFavourites;
    }

    public void setNumOfFavourites(int numOfFavourites) {
        this.numOfFavourites = numOfFavourites;
    }

    public int getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(int numOfLikes) {
        this.numOfLikes = numOfLikes;
        if(this.numOfLikes < 0){
            this.numOfLikes = 0;
        }
    }

    public int getNumOfJobs() {
        return numOfJobs;
    }

    public void setNumOfJobs(int numOfJobs) {
        this.numOfJobs = numOfJobs;
    }

    public int getNumOfServices() {
        return numOfServices;
    }

    public void setNumOfServices(int numOfServices) {
        this.numOfServices = numOfServices;
    }

    public boolean isCanLike() {
        return canLike;
    }

    public void setCanLike(boolean canLike) {
        this.canLike = canLike;
    }



    public int getNumOfEvents() {
        return numOfJobs + numOfServices;
    }



    protected EzlyUser(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        avatarUrl = in.readString();
        numOfFavourites = in.readInt();
        numOfLikes = in.readInt();
        numOfJobs = in.readInt();
        numOfServices = in.readInt();
        canLike = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeString(avatarUrl);
        dest.writeInt(numOfFavourites);
        dest.writeInt(numOfLikes);
        dest.writeInt(numOfJobs);
        dest.writeInt(numOfServices);
        dest.writeByte((byte) (canLike ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyUser> CREATOR = new Parcelable.Creator<EzlyUser>() {
        @Override
        public EzlyUser createFromParcel(Parcel in) {
            return new EzlyUser(in);
        }

        @Override
        public EzlyUser[] newArray(int size) {
            return new EzlyUser[size];
        }
    };
}