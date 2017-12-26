package com.ezly.ezly_android.UI.Widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.R;


/**
 * Created by Johnnie on 1/09/15.
 */
public class BaseTabLayout extends TabLayout {
    private Context context;

    public BaseTabLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BaseTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BaseTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        setSelectedTabIndicatorColor(context.getResources().getColor(R.color.white));
        setTabTextColors(context.getResources().getColor(R.color.white), context.getResources().getColor(R.color.white));
        setSelectedTabIndicatorHeight(UIHelper.dip2px(context, 3));
        setBackgroundColor(context.getResources().getColor(R.color.tabLayout_bg));
    }
}
