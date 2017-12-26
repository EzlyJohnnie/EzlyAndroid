package com.ezly.ezly_android.Utils.Helper;

import android.content.Context;

import com.ezly.ezly_android.Utils.Config;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Johnnie on 1/10/15.
 */
@Singleton
public class GAHelper {
    public enum ScreenName{

    }

    public enum Category{

    }

    public enum Action{

    }

    private static Tracker mTracker;
    private Context context;

    @Inject
    public GAHelper(Context context) {
        this.context = context;
        initTrack();
    }

    private void initTrack(){
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            mTracker = analytics.newTracker(Config.GA_TRACKER_ID);
        }
    }

    synchronized public Tracker getDefaultTracker() {
        return mTracker;
    }

    public void sendScreen(ScreenName screenName){
        mTracker.setScreenName(screenName.toString());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendEvent(Category category, Action action){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category.toString())
                .setAction(action.toString())
                .build());
    }
}
