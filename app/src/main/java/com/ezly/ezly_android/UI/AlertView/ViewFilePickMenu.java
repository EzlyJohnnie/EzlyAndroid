package com.ezly.ezly_android.UI.AlertView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.R;

import butterknife.ButterKnife;

/**
 * Created by Johnnie on 21/03/16.
 */
public class ViewFilePickMenu extends RelativeLayout {

    private Activity activity;
    private View view;
    private Dialog dialog;
    protected FilePickMenuListener mListener;

    protected View btnUpload;
    protected View btnTakePhoto;
    private int limit;


    public static ViewFilePickMenu presentView(Activity activity, int limit, FilePickMenuListener mListener){
        Dialog dlg = new Dialog(activity, R.style.draw_dialog_bottom_in);
        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ViewFilePickMenu view = new ViewFilePickMenu(activity, limit, dlg);
        view.setListener(mListener);

        return (ViewFilePickMenu) UIHelper.presentView(activity, dlg, view);
    }

    public ViewFilePickMenu(Activity activity, int limit, Dialog dialog) {
        super(activity);
        this.activity = activity;
        this.dialog = dialog;
        this.limit = limit;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_file_pick_option_menu, this);
        ButterKnife.bind(this, view);
        init();
    }

    public void setListener(final FilePickMenuListener mListener) {
        this.mListener = mListener;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mListener != null){
                    mListener.onClose();
                }
            }
        });
    }

    private void init() {
        btnUpload = findViewById(R.id.btn_upload);
        btnTakePhoto = findViewById(R.id.btn_take_photo);

        setupView();
    }

    protected void setupView() {
        btnUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(mListener != null){
                    mListener.onUploadClicked(limit);
                }
            }
        });

        btnTakePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(mListener != null){
                    mListener.onTakePhotoClicked();
                }
            }
        });

        findViewById(R.id.top_view).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.hideKeyBoard(view);
                dialog.dismiss();
            }
        });
    }

    public interface FilePickMenuListener{
        void onClose();
        void onUploadClicked(int limit);
        void onTakePhotoClicked();

    }
}
