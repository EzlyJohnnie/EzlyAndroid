package com.ezly.ezly_android.Utils.Helper.UIHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.DialogView.EzlyBaseDialogView;

/**
 * Created by Johnnie on 2/11/15.
 */

public class UIHelper {
    private static final int TABLET_SMALLEST_WIDTH_DP = 600;
    public static final int TYPEFACE_REGULAR = 0;
    public static final int TYPEFACE_BOLD    = 1;
    public static final int TYPEFACE_LIGHT   = 2;

    public static void showConnectionError(Context context) {
        SingleToast.makeText(context, context.getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
    }

    public static int getRelativeX(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeX((View) myView.getParent());
    }

    public static int getRelativeY(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeY((View) myView.getParent());
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        if(context == null)
        {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void displayYesNoDialog(Context context, String message, DialogInterface.OnClickListener onYesClick){
        new AlertDialog.Builder(context)
                .setTitle(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes", onYesClick)
                .show();
    }

    public static void displayYesNoDialog(Context context, String title, String message, DialogInterface.OnClickListener onYesClick){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes", onYesClick)
                .show();
    }

    public static void displayConfirmDialog(Context context, String message){
        new AlertDialog.Builder(context)
                .setTitle(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public static void displayConfirmDialog(Context context, String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public static View presentView(Activity activity, Dialog dlg, View view){
        dlg.setContentView(view);
        Window window = dlg.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = activity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        lp.dimAmount = 0.2f;
        window.setAttributes(lp);
        dlg.show();
        return view;
    }

    public static void displayConfirmDialog(Context context, String message, DialogInterface.OnClickListener onOKClick){
        new AlertDialog.Builder(context)
                .setTitle(message)
                .setPositiveButton("Ok", onOKClick)
                .show();
    }

    public static int getStatusBarHeight(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isTabletSize(Context context, View view) {
        float widthInDp = px2dip(context, view.getWidth());
        return widthInDp >= TABLET_SMALLEST_WIDTH_DP;
    }

//    public static Typeface getTypeface(Context context, int type){
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "pingfang_regular.ttf");
//        switch (type) {
//            case TYPEFACE_BOLD:
//                typeface = Typeface.createFromAsset(context.getAssets(), "pingfang_bold.ttf");
//                break;
//            case TYPEFACE_LIGHT:
//                typeface = Typeface.createFromAsset(context.getAssets(), "pingfang_light.ttf");
//                break;
//        }
//
//        return typeface;
//    }


    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public static Bitmap fastBlur(View view){
        return fastBlur(getBitmapFromView(view));
    }

    public static Bitmap fastBlur(Bitmap bitmap){
        return BlurHelper.fastBlur(bitmap, 0.1f, 10);
    }

    public static void hideKeyBoard(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public static boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > 5 || differenceY > 5) {
            return false;
        }
        return true;
    }

    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        // point is inside view bounds
        return ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight())));
    }

    public static void presentBottomMenu(EzlyBaseDialogView dialogView){
        Window window = dialogView.getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = dialogView.getActivity().getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        lp.dimAmount = 0.2f;
        window.setAttributes(lp);
        dialogView.getDialog().show();
    }

    public static int hexToIntColor(String color) {
        //Default value
        int mColor = Color.BLACK;

        if(color != null)  {
            try {
                mColor = Color.parseColor("#"+color);
            }
            catch (NumberFormatException e) {
                Log.e("UIHelper", "NumberFormatException for "+color);
            }
            catch (Exception e) {
                Log.e("UIHelper", "Unknown color exception "+color);
            }
        }

        return mColor;
    }
}
