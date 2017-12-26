package com.ezly.ezly_android.UI.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.R;

/**
 * Created by Johnnie on 20/10/16.
 */

public class LineWrapLayout extends LinearLayout
{

    private int width;//组件宽
    private int height;//组件高
    private int childCount;
    private int childMarginLeft = UIHelper.dip2px(getContext(),8);//子控件相对左边控件的距离
    private int childMarginHorizonTal = UIHelper.dip2px(getContext(),10);//子控件相对最左、最右的距离
    private int childMarginTop = UIHelper.dip2px(getContext(),8);//子控件相对顶部控件的距离
    private int childWidth;//子控件宽
    private int childHeight;//子控件高

    public LineWrapLayout(Context context) {
        super(context);
    }

    public LineWrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HomesTextView, 0, 0);
        childHeight = typedArray.getInt(R.styleable.linewarplayout_itemHeight, 42);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childCount = getChildCount();//得到子控件数量
        if(childCount>0) {
            childWidth = (width - childMarginLeft * 4) / 3;
            //根据子控件的高和子控件数目得到自身的高
            height = childHeight * ((childCount-1)/ 3+1) + childMarginHorizonTal * 2 + childMarginTop*((childCount-1)/3);
            Log.d("childHeight",childHeight+"");
        }else {
            //如果木有子控件，自身高度为0，即不显示
            height = 0;
        }
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        Log.d("height",height+"");
        //根据自身的宽度约束子控件宽度
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //设置自身宽度
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /**
         * 遍历所有子控件，并设置它们的位置和大小
         * 每行只能有三个子控件，且高度固定，宽度相同，且每行正好布满
         */
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);//得到当前子控件
            childView.layout((i%3) * childWidth + (i%3+1)*childMarginLeft
                    , (i / 3)*childHeight + childMarginHorizonTal + (i / 3)*childMarginTop
                    , (i%3+1) * childWidth + (i%3+1)*childMarginLeft
                    , (i / 3+1)*childHeight + childMarginHorizonTal + (i / 3)*childMarginTop);
        }
    }

}
