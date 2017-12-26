package com.ezly.ezly_android.UI.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Johnnie on 21/06/16.
 */
public class BaseScrollView extends ScrollView {

    private OnScrollListener mOnScrollListener;

    public BaseScrollView(Context context) {
        super(context);
    }

    public BaseScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollListener(OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt){
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollListener != null){
            mOnScrollListener.onScroll(l, t, oldl, oldt);
        }
    }

    public interface OnScrollListener{
        void onScroll(int l, int t, int oldl, int oldt);
    }
}
