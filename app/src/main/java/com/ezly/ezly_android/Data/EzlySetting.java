package com.ezly.ezly_android.Data;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.ezly.ezly_android.Utils.Helper.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.Locale;

/**
 * Created by Johnnie on 20/11/16.
 */

public class EzlySetting {
    public static final int LANGUAGE_CHINESE = 0;
    public static final int LANGUAGE_ENGLISH = 1;

    @Expose private boolean allMessageEnabled;
    @Expose private boolean importantMessageEnabled;
    @Expose private boolean notificationEnabled;
    @Expose private int language = -1;

    public static boolean hasInitInstance;
    private static EzlySetting instance;

    public static EzlySetting getInstance(){
        if(instance == null){
            instance = new EzlySetting();
        }
        return instance;
    }

    public static void initInstance(Context context){
        hasInitInstance = true;
        instance = SharedPreferenceHelper.fromSharedPreference(context,
                SharedPreferenceHelper.FILE_KEY_SETTING,
                SharedPreferenceHelper.VALUE_KEY_SETTING,
                EzlySetting.class);

        if(instance == null || instance.getLanguage() < 0){
            instance = new EzlySetting();
            Locale current = context.getResources().getConfiguration().locale;
            String language = current.getLanguage();
            if(language.toLowerCase().contains("en")){
                instance.setLanguage(LANGUAGE_ENGLISH);
            }
            else if(language.toLowerCase().contains("zh")){
                instance.setLanguage(LANGUAGE_CHINESE);
            }
            else{
                //default
                instance.setLanguage(LANGUAGE_ENGLISH);
            }
            instance.saveToSharedPreference(context);
        }
        else{
            String localeStr = "";
            switch (instance.getLanguage()){
                case EzlySetting.LANGUAGE_CHINESE:
                    localeStr = "zh";
                    break;
                case EzlySetting.LANGUAGE_ENGLISH:
                    localeStr = "en";
                    break;
            }
            Locale myLocale = new Locale(localeStr);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
    }

    public String toJson(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public boolean isAllMessageEnabled() {
        return allMessageEnabled;
    }

    public void setAllMessageEnabled(boolean allMessageEnabled) {
        this.allMessageEnabled = allMessageEnabled;
    }

    public boolean isImportantMessageEnabled() {
        return importantMessageEnabled;
    }

    public void setImportantMessageEnabled(boolean importantMessageEnabled) {
        this.importantMessageEnabled = importantMessageEnabled;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public void saveToSharedPreference(Context context) {
        SharedPreferenceHelper.toSharedPreference(context,
                SharedPreferenceHelper.FILE_KEY_SETTING,
                SharedPreferenceHelper.VALUE_KEY_SETTING,
                toJson());
    }
}
