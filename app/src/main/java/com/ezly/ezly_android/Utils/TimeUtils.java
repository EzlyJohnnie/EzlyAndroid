package com.ezly.ezly_android.Utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Johnnie on 13/03/17.
 */

public class TimeUtils {

    public static long getHourDiff(Context context, String dataFormatter, String dateStr) {
        long hours = 0;
        Date current = new Date();
        SimpleDateFormat inFormat = new SimpleDateFormat(dataFormatter);
        Date date = null;
        try {
            date = inFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return hours;
        }

        if(date != null){
            long diff = current.getTime() - date.getTime();
            hours = diff / 1000 / 60 / 60;

        }

        return hours;
    }
}
