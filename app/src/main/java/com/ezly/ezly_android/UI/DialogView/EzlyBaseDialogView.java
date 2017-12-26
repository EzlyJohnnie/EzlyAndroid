package com.ezly.ezly_android.UI.DialogView;

import android.app.Activity;
import android.app.Dialog;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Johnnie on 6/02/17.
 */

public class EzlyBaseDialogView extends RelativeLayout {

    protected Activity activity;
    protected Dialog dialog;

    public EzlyBaseDialogView(Activity activity) {
        super(activity);
    }

    public EzlyBaseDialogView(Activity activity, AttributeSet attrs) {
        super(activity, attrs);
    }

    public EzlyBaseDialogView(Activity activity, AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public Activity getActivity() {
        return activity;
    }
}
