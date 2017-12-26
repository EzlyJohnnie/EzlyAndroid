package com.ezly.ezly_android.Utils.PushNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ezly.ezly_android.EzlyApplication;
import com.ezly.ezly_android.Utils.Helper.SharedPreferenceHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyNotification;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.Utils.Config;
import com.ezly.ezly_android.Utils.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.greenrobot.eventbus.EventBus;

/*
 * This service is designed to run in the background and receive messages from gcm. If the app is in the foreground
 * when a message is received, it will immediately be posted. If the app is not in the foreground, the message will be saved
 * and a notification is posted to the NotificationManager.
 */
public class MessageReceivingService extends Service {
    private GoogleCloudMessaging gcm;
    public static EzlyApplication application;

    public static void sendToApp(Bundle extras, Context context){
        String json = extras.getString("default");
        EzlyNotification notification = EzlyNotification.fromJson(json);
        UIHelper.displayConfirmDialog(application.getCurrentActivity(), context.getResources().getString(R.string.new_push_notification_title), notification.getFormattedContent(context));
        EventBus.getDefault().post(new ResultEvent.OnReceivePushNotificationEvent(notification));
    }


    @Override
    public void onCreate(){
        application = (EzlyApplication)getApplication();
        super.onCreate();
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        String token = SharedPreferenceHelper.getGcmToken(getBaseContext());
        if(TextUtils.isEmpty(SharedPreferenceHelper.getGcmToken(getBaseContext()))){
            register();
        }
    }

    protected static void saveToLog(Bundle extras, Context context){
        String json = extras.getString("default");
        EzlyNotification notification = EzlyNotification.fromJson(json);

        Intent actionIntent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.newest_push_message), json);
        actionIntent.putExtras(bundle);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        postNotification(actionIntent, context, notification.getFormattedContent(context));
    }

    protected static void postNotification(Intent intentAction, Context context, String message){
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[] { 500, 1000 })
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle("Ezly Home")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        mNotificationManager.notify(R.string.notification_number, notification);
    }

    private void register() {
        new AsyncTask(){
            @Override
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(Config.PROJECT_NUMBER);
                    SharedPreferenceHelper.saveGcmToken(getBaseContext(), token);
                    Log.i("registrationId", token);
                }
                catch (Exception e) {
                    Log.i("Registration Error", e.getMessage());
                }


                SharedPreferences savedValues = PreferenceManager.getDefaultSharedPreferences(MessageReceivingService.this);
                SharedPreferences.Editor editor = savedValues.edit();
                editor.putBoolean(getString(R.string.has_register_sns), true);
                editor.commit();


                return true;
            }
        }.execute(null, null, null);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}