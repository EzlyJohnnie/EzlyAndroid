package com.ezly.ezly_android.Utils.Helper;

import android.content.Context;

import com.ezly.ezly_android.Data.EzlyNotification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Johnnie on 13/03/17.
 */

public class NotificationHelper {
    private static  final String READ_NOTIFICATION_FILENAME = "readNotification.dat";
    private static NotificationHelper instance;
    public static long latestLoadTime;

    private ArrayList<String> readNotificationIDs;

    public static NotificationHelper getInstance(){
        if(instance == null){
            instance = new NotificationHelper();
        }

        return instance;
    }

    public ArrayList<String> getReadNotificationIDs(Context context){
        if(readNotificationIDs == null){
            String path = context.getFilesDir() + "/" + READ_NOTIFICATION_FILENAME;
            File file = new File(path);
            if(!file.exists()){
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                readNotificationIDs = new ArrayList<>();
            }
            else{
                String jsonString = loadDataFromFile(file);
                readNotificationIDs = new Gson().fromJson(jsonString, new TypeToken<ArrayList<String>>(){}.getType());
                if(readNotificationIDs == null){
                    readNotificationIDs = new ArrayList<>();
                }
            }
        }

        return readNotificationIDs;
    }

    public void setNotificationRead(Context context, String notificationID){
        getReadNotificationIDs(context);
        if(!readNotificationIDs.contains(notificationID)){
            readNotificationIDs.add(notificationID);
            String path = context.getFilesDir() + "/" + READ_NOTIFICATION_FILENAME;
            File settingFile = new File(path);
            String jsonData = new Gson().toJson(readNotificationIDs);
            saveToFile(settingFile, jsonData.getBytes());
        }
    }

    public boolean hasNotificationBeenRead(Context context, String notificationID){
        getReadNotificationIDs(context);
        return readNotificationIDs.contains(notificationID);
    }

    public boolean hasUnreadyNotification(Context context, ArrayList<EzlyNotification> notifications) {
        boolean hasUnreadyNotification = false;
        if(notifications != null){
            getReadNotificationIDs(context);
            for(EzlyNotification notification : notifications){
                long sentTime = notification.getSendTimestamp();
                if(latestLoadTime > 0 && sentTime <= latestLoadTime){
                    continue;
                }

                if(!readNotificationIDs.contains(notification.getId())){
                    hasUnreadyNotification = true;
                }
            }
        }
        return hasUnreadyNotification;
    }

    private String loadDataFromFile(File file) {
        if(file == null){
            return "";
        }
        String stringData = "";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            stringData = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringData;
    }

    private void saveToFile(File file, byte[] data) {
        try {
            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
