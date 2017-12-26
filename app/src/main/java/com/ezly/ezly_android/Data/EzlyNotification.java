package com.ezly.ezly_android.Data;

import android.content.Context;

import com.ezly.ezly_android.R;
import com.ezly.ezly_android.Utils.TextUtils;
import com.ezly.ezly_android.Utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Johnnie on 12/03/17.
 */

public class EzlyNotification {
    public static final String TYPE_JOB     = "job";
    public static final String TYPE_SERVICE = "service";

    @Expose private String id;
    @Expose private String CreatedUtc;
    @Expose private String RefType;
    @Expose private String RefId;
    @Expose private String Description;
    @Expose private String User;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedUtc() {
        return CreatedUtc;
    }

    public void setCreatedUtc(String createdUtc) {
        this.CreatedUtc = createdUtc;
    }

    public String getRefType() {
        return RefType;
    }

    public void setRefType(String refType) {
        this.RefType = refType;
    }

    public String getRefId() {
        return RefId;
    }

    public void setRefId(String refId) {
        this.RefId = refId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        this.User = user;
    }

    public boolean isJob(){
        return RefType.toLowerCase().equals(TYPE_JOB);
    }

    public boolean isService(){
        return RefType.toLowerCase().equals(TYPE_SERVICE);
    }

    public String getFormattedSentTime(Context context) {
        String sentTimeStr = "";
        String  dataFormatter = "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat inFormat = new SimpleDateFormat(dataFormatter);
        Date date = null;
        try {
            date = inFormat.parse(CreatedUtc);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        long diffHours = TimeUtils.getHourDiff(context, dataFormatter, CreatedUtc);
        int diffDay = (int) diffHours / 24;
        String expireStr = "";
        String formatter = context.getResources().getString(R.string.notification_send_time);
        String dayStr = context.getResources().getString(diffDay > 1 ? R.string.days : R.string.day);
        String hourStr = context.getResources().getString(diffHours > 1 ? R.string.hours : R.string.hour);
        if(diffDay > 0){
            sentTimeStr = String.format(formatter, diffDay, dayStr, day, month);
        }
        else if(diffHours > 0){
            sentTimeStr = String.format(formatter, diffHours, hourStr, day, month);
        }
        else{
            sentTimeStr = "A few minutes ago";
        }

        return sentTimeStr;
    }


    public String getFormattedContent(Context context) {
        String content = "";

        String formatter = context.getResources().getString(R.string.notification_content);
        content = String.format(formatter, User, Description);
        return content;
    }

    public long getSendTimestamp(){
        long sendTimestamp = 0;
        String  dataFormatter = "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat inFormat = new SimpleDateFormat(dataFormatter);
        Date date = null;
        try {
            date = inFormat.parse(CreatedUtc);
            sendTimestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sendTimestamp;
    }

    public static EzlyNotification fromJson(String json){
        if(TextUtils.isEmpty(json)){
            return null;
        }

        return new Gson().fromJson(json, EzlyNotification.class);
    }
}
