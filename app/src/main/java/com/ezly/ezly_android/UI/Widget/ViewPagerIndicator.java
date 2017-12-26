package com.ezly.ezly_android.UI.Widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.R;

import java.util.ArrayList;


/**
 * Created by Johnnie on 27/08/15.
 */
public class ViewPagerIndicator extends LinearLayout {

    private int count;
    private int currentIndex;
    private Context context;
    private ArrayList<ImageView> indicators;
    private int indicatorSize;
    private int leftMargin;
    private boolean isUseTwoLine = false;
    public ViewPagerIndicator(Context context) {
        super(context);
        this.context = context;
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void init(int count) {
        indicatorSize = UIHelper.dip2px(context, 8);
        leftMargin = UIHelper.dip2px(context, 10);

        this.setOrientation(VERTICAL);
        this.count = count;
        indicators = new ArrayList<ImageView>();
        int firstLineTotal = 0;
        int secondLineTotal = 0;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        if(getWidthToFit() >= screenWidth){
            isUseTwoLine = true;
            int oddAdditional = count % 2;
            firstLineTotal = count / 2;
            secondLineTotal = count / 2 + oddAdditional;
        }
        else{
            firstLineTotal = count;
        }

        LinearLayout firstLine = new LinearLayout(context);
        LayoutParams param1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        firstLine.setLayoutParams(param1);
        param1.gravity = Gravity.CENTER_HORIZONTAL;
        firstLine.setOrientation(HORIZONTAL);
        LinearLayout secondLine = null;
        if(isUseTwoLine){
            secondLine = new LinearLayout(context);
            LayoutParams param = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(0, UIHelper.dip2px(context, 8), 0, 0);
            secondLine.setLayoutParams(param);
            secondLine.setOrientation(HORIZONTAL);
        }

        for(int i = 0; i < count; i++){
            ImageView imageView = new ImageView(context);
            LayoutParams param = new LayoutParams(indicatorSize, indicatorSize);
            param.gravity = Gravity.CENTER;
            if(i == 0){
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.indicator_selected_bg));
            } else{

                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.indicator_normal_bg));
            }

            if(!(i == 0 || (isUseTwoLine && i == firstLineTotal))){
                param.setMargins(leftMargin, 0, 0, 0);
            }

            imageView.setLayoutParams(param);
            indicators.add(imageView);

            if(i < firstLineTotal){
                firstLine.addView(imageView);
            }
            else {
                secondLine.addView(imageView);
            }

        }
        this.addView(firstLine);
        if(isUseTwoLine){
            this.addView(secondLine);
        }
    }

    public void setCurrentIndex(int currentIndex){
        this.currentIndex = currentIndex;
        for(int i = 0; i < indicators.size(); i++){
            ImageView imageView = indicators.get(i);
            if(i == currentIndex){
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.indicator_selected_bg));
            }
            else{
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.indicator_normal_bg));
            }
        }
    }

    protected int getWidthToFit(){
        return count * indicatorSize + (count - 1) * leftMargin;
    }

    public int getCurrentIndex(){
        return currentIndex;
    }
}
