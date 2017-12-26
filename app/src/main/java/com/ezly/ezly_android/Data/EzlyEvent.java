package com.ezly.ezly_android.Data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.TextUtils;
import com.ezly.ezly_android.Utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Johnnie on 15/10/16.
 */

public class EzlyEvent implements Comparable<EzlyEvent>, Parcelable {
    public static final int EXPIRE_TIME_ONE_DAY     = 0;
    public static final int EXPIRE_TIME_THREE_DAY   = 1;
    public static final int EXPIRE_TIME_ONE_WEEK    = 2;
    public static final int EXPIRE_TIME_ONE_MONTH   = 3;

    private String id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private EzlyCategory category;
    private EzlyAddress address;
    private EzlyUser user;
    private ArrayList<EzlyImage> photos;
    private int expireTime;
    private ArrayList<EzlyImage> imagesForPost;
    private boolean canFavourite;
    private boolean isActive;

    private float distance = 0.0f;

    public EzlyEvent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public EzlyCategory getCategory() {
        return category;
    }

    public void setCategory(EzlyCategory category) {
        this.category = category;
    }

    public EzlyAddress getAddress() {
        return address;
    }

    public void setAddress(EzlyAddress address) {
        this.address = address;
    }

    public EzlyUser getUser() {
        return user;
    }

    public void setUser(EzlyUser user) {
        this.user = user;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public ArrayList<EzlyImage> getImagesForPost() {
        return imagesForPost;
    }

    public boolean canBeFavourited() {
        return canFavourite;
    }

    public void setCanFavourite(boolean canFavourite) {
        this.canFavourite = canFavourite;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void addImageForPost(EzlyImage imageForPost) {
        if(this.imagesForPost == null){
            this.imagesForPost = new ArrayList<>();
        }
        this.imagesForPost.add(imageForPost);
    }

    public void addImagesForPost(ArrayList<EzlyImage> imagesForPost) {
        if(this.imagesForPost == null){
            this.imagesForPost = new ArrayList<>();
        }
        this.imagesForPost.addAll(imagesForPost);
    }

    public void removeImageForPost(int index){
        if(this.imagesForPost != null){
            this.imagesForPost.remove(index);
        }
    }

    public void removeImageForPost(EzlyImage image){
        if(this.imagesForPost != null){
            this.imagesForPost.remove(image);
        }
    }

    /**
     * use in compare() only
     * user getDistanceDiff() instead if from outside
     * @return
     */
    private float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public ArrayList<EzlyImage> getImages() {
        return photos;
    }

    public void setImages(ArrayList<EzlyImage> images) {
        this.photos = images;
    }

    public long getHourDiff(Context context) {
        long hour = 0;
        if(!TextUtils.isEmpty(startDate)){
            hour = TimeUtils.getHourDiff(context, "yyyy-MM-dd'T'HH:mm:ssZ", startDate);
        }
        return hour;
    }


    public float getDistanceDiff(Context context, EzlyAddress lastKnownLocation) {
        if(context != null){
            if(lastKnownLocation != null && lastKnownLocation.getLocation().isValidLocation()){
                float[] results = new float[1];
                if(address.getLocation().isValidLocation()){
                    android.location.Location.distanceBetween(address.getLocation().getLatitude(),
                            address.getLocation().getLongitude(),
                            lastKnownLocation.getLocation().getLatitude(),
                            lastKnownLocation.getLocation().getLongitude(),
                            results);
                    distance = results[0]/1000;
                }
            }
        }

        return distance;
    }




    @Override
    public int compareTo(EzlyEvent event) {
        int returnVal = 0;

        if(distance < event.getDistance()){
            returnVal =  -1;
        }else if(distance > event.getDistance()){
            returnVal =  1;
        }else if(distance == event.getDistance()){
            returnVal =  0;
        }
        return returnVal;

    }











    protected EzlyEvent(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        category = (EzlyCategory) in.readValue(EzlyCategory.class.getClassLoader());
        address = (EzlyAddress) in.readValue(EzlyAddress.class.getClassLoader());
        user = (EzlyUser) in.readValue(EzlyUser.class.getClassLoader());
        if (in.readByte() == 0x01) {
            photos = new ArrayList<EzlyImage>();
            in.readList(photos, EzlyImage.class.getClassLoader());
        } else {
            photos = null;
        }
        expireTime = in.readInt();
        if (in.readByte() == 0x01) {
            imagesForPost = new ArrayList<EzlyImage>();
            in.readList(imagesForPost, EzlyImage.class.getClassLoader());
        } else {
            imagesForPost = null;
        }
        canFavourite = in.readByte() != 0x00;
        isActive = in.readByte() != 0x00;
        distance = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeValue(category);
        dest.writeValue(address);
        dest.writeValue(user);
        if (photos == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(photos);
        }
        dest.writeInt(expireTime);
        if (imagesForPost == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(imagesForPost);
        }
        dest.writeByte((byte) (canFavourite ? 0x01 : 0x00));
        dest.writeByte((byte) (isActive ? 0x01 : 0x00));
        dest.writeFloat(distance);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyEvent> CREATOR = new Parcelable.Creator<EzlyEvent>() {
        @Override
        public EzlyEvent createFromParcel(Parcel in) {
            return new EzlyEvent(in);
        }

        @Override
        public EzlyEvent[] newArray(int size) {
            return new EzlyEvent[size];
        }
    };
}