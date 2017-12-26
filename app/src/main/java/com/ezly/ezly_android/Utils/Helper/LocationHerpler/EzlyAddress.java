package com.ezly.ezly_android.Utils.Helper.LocationHerpler;

import android.os.Parcel;
import android.os.Parcelable;

import com.ezly.ezly_android.Utils.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Created by Johnnie on 9/07/16.
 */
public class EzlyAddress implements Parcelable {

    @Expose private EzlyLocation location;
    @Expose private String name;
    @Expose private String line1;
    @Expose private String line2;
    @Expose private String suburb;
    @Expose private String city;
    @Expose private String zipCode;
    @Expose private String country;
    @Expose private String displayStreet;
    @Expose private String id;

    public EzlyAddress(){
        location = new EzlyLocation(0, 0);
    }

    public EzlyAddress(float lat, float longitude) {
        location = new EzlyLocation(lat, longitude);
    }

    public EzlyLocation getLocation() {
        return location;
    }

    public void setLocation(EzlyLocation location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayStreet() {
        return displayStreet;
    }

    public void setDisplayStreet(String displayStreet) {
        this.displayStreet = displayStreet;
    }

    public String getFullAddress(){
        String result  = line1;
        if(!TextUtils.isEmpty(line2)){
            result += ", " + line2;
        }

        if(!TextUtils.isEmpty(suburb)){
            result += ", " + suburb;
        }

        if(!TextUtils.isEmpty(city)){
            result += ", " + city;
        }

        if(!TextUtils.isEmpty(country)){
            result += ", " + country;
        }
        return result;
    }

    public String toJson(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }








    protected EzlyAddress(Parcel in) {
        location = (EzlyLocation) in.readValue(EzlyLocation.class.getClassLoader());
        name = in.readString();
        line1 = in.readString();
        line2 = in.readString();
        suburb = in.readString();
        city = in.readString();
        zipCode = in.readString();
        country = in.readString();
        displayStreet = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(location);
        dest.writeString(name);
        dest.writeString(line1);
        dest.writeString(line2);
        dest.writeString(suburb);
        dest.writeString(city);
        dest.writeString(zipCode);
        dest.writeString(country);
        dest.writeString(displayStreet);
        dest.writeString(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EzlyAddress> CREATOR = new Parcelable.Creator<EzlyAddress>() {
        @Override
        public EzlyAddress createFromParcel(Parcel in) {
            return new EzlyAddress(in);
        }

        @Override
        public EzlyAddress[] newArray(int size) {
            return new EzlyAddress[size];
        }
    };
}