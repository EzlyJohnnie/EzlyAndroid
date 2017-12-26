package com.ezly.ezly_android.UI.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Johnnie on 15/10/16.
 */

public class AutoSquareImageView extends ImageView {


    public AutoSquareImageView(Context context) {
        super(context);
    }

    public AutoSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int size = Math.max(measuredWidth, measuredHeight);
        setMeasuredDimension(size, size);
    }
}
