package com.ezly.ezly_android.UI.Widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Johnnie on 23/08/15.
 */
public class EzlyViewPager extends ViewPager {
    private boolean swipeEnabled = true;
    public EzlyViewPager(Context context) {
        super(context);
    }

    public EzlyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.swipeEnabled = swipeEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(!swipeEnabled){
            return swipeEnabled;
        }
        else{
            return super.onInterceptTouchEvent(event);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!swipeEnabled){
            return swipeEnabled;
        }
        else{
            return super.onTouchEvent(event);
        }
    }
}
