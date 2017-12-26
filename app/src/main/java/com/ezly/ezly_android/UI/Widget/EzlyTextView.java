package com.ezly.ezly_android.UI.Widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ezly.ezly_android.R;


/**
 * Created by Johnnie on 5/09/15.
 */
public class EzlyTextView extends TextView{

    private Context context;

    public EzlyTextView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public EzlyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public EzlyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EzlyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HomesTextView, 0, 0);
        String typeface = typedArray.getString(R.styleable.HomesTextView_typeface);

        if (!isInEditMode())
            setTypeface(TextUtils.isEmpty(typeface) ? "" : typeface);

        typedArray.recycle();
    }

    public void setTypeface(String typeface) {
        switch (typeface) {
            case "bold":
//                setTypeface(Typeface.createFromAsset(context.getAssets(), "pingfang_bold.ttf"));
                break;
            case "light":
//                setTypeface(Typeface.createFromAsset(context.getAssets(), "pingfang_light.ttf"));
                break;
            case "regular":
            default:
//                setTypeface(Typeface.createFromAsset(context.getAssets(), "pingfang_regular.ttf"));
                break;
        }
    }
}
