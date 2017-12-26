package com.ezly.ezly_android.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Johnnie on 29/10/16.
 */

public class EzlyComment implements Parcelable {
    private String id;
    private String text;
    private String userId;
    private String creationTime;
    private List<EzlyComment> childComments;

    public EzlyComment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public List<EzlyComment> getChildComments() {
        return childComments;
    }

    public void setChildComments(List<EzlyComment> childComments) {
        this.childComments = childComments;
    }

    public String getFormattedCreationTime() {
        String result = "";
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        Date date = null;
        try {
            date = inFormat.parse(creationTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date != null){
            inFormat = new SimpleDateFormat("dd MMM yyyy");
            result = inFormat.format(date);
        }


        return result;
    }

    protected EzlyComment(Parcel in) {
        id = in.readString();
        text = in.readString();
        userId = in.readString();
        creationTime = in.readString();
        if (in.readByte() == 0x01) {
            childComments = new ArrayList<EzlyComment>();
            in.readList(childComments, EzlyComment.class.getClassLoader());
        } else {
            childComments = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(userId);
        dest.writeString(creationTime);
        if (childComments == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(childComments);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyComment> CREATOR = new Parcelable.Creator<EzlyComment>() {
        @Override
        public EzlyComment createFromParcel(Parcel in) {
            return new EzlyComment(in);
        }

        @Override
        public EzlyComment[] newArray(int size) {
            return new EzlyComment[size];
        }
    };
}