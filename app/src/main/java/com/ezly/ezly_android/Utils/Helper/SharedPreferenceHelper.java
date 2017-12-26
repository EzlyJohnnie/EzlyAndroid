package com.ezly.ezly_android.Utils.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Created by Johnnie on 16/10/16.
 */

public class SharedPreferenceHelper {
    public static final String FILE_KEY_APP_CONFIG = "fileKeyAppConfig";
    public static final String FILE_KEY_SEARCH_PARAM = "fileKeySearchParam";
    public static final String VALUE_KEY_SEARCH_PARAM = "fileKeySearchParam";
    public static final String FILE_KEY_TOKEN = "fileKeyToken";
    public static final String VALUE_KEY_TOKEN = "fileKeyToken";
    public static final String FILE_KEY_SETTING = "fileKeySetting";
    public static final String VALUE_KEY_SETTING = "valueKeySetting";
    public static final String VALUE_KEY_GCM_TOKEN = "valueKeyGcmToken";

    public static void toSharedPreference(Context context, String fileKey, String valueKey, String jsonStr){
        SharedPreferences preferences = context.getSharedPreferences(fileKey, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(valueKey, jsonStr);

        editor.apply();
    }

    public static <T> T fromSharedPreference(Context context, String fileKey, String valueKey, Class<T> objClass){
        SharedPreferences preferences = context.getSharedPreferences(fileKey, Context.MODE_WORLD_READABLE);
        String jsonString = preferences.getString(valueKey, "{}");

        return (objClass.isInstance(String.class)) ? (T)jsonString : new Gson().fromJson(jsonString, objClass);
    }

    public static void saveGcmToken(Context context, String deviceToken){
        SharedPreferences savedValues = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString(VALUE_KEY_GCM_TOKEN, deviceToken);
        editor.apply();

    }

    public static String getGcmToken(Context context){
        SharedPreferences savedValues = PreferenceManager.getDefaultSharedPreferences(context);
        return savedValues.getString(VALUE_KEY_GCM_TOKEN, "");
    }

}
