package com.ezly.ezly_android.UI.DialogView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlySetting;
import com.ezly.ezly_android.R;

import butterknife.ButterKnife;

/**
 * Created by Johnnie on 21/03/16.
 */
public class LanguageDialogView extends EzlyBaseDialogView {

    private LanguageDialogListener mListener;

    private View topView;
    private TextView btnChinese;
    private TextView btnEnglish;
    private int currentLanguage;

    public static LanguageDialogView newInstance(Activity activity, int currentLanguage){
        Dialog dlg = new Dialog(activity, R.style.draw_dialog_bottom_in);
        LanguageDialogView languageDialogView = new LanguageDialogView(activity, dlg, currentLanguage);
        return languageDialogView;
    }

    private LanguageDialogView(Activity activity, Dialog dialog, int currentLanguage) {
        super(activity);
        this.activity = activity;
        this.dialog = dialog;
        this.currentLanguage = currentLanguage;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_language, this);
        dialog.setContentView(this);
        ButterKnife.bind(this, view);
        init(view);
    }

    private void init(View view) {
        initViewComponents(view);
        initView(view);
        initListener(view);
    }

    private void initView(View view) {
        if(currentLanguage == EzlySetting.LANGUAGE_CHINESE){
            btnChinese.setEnabled(false);
            btnChinese.setTextColor(activity.getResources().getColor(R.color.divider));
        }
        else if(currentLanguage == EzlySetting.LANGUAGE_ENGLISH){
            btnEnglish.setEnabled(false);
            btnEnglish.setTextColor(activity.getResources().getColor(R.color.divider));
        }
    }

    public void setListener(LanguageDialogListener listener) {
        this.mListener = listener;
    }

    private void initListener(final View view) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mListener != null){
                    mListener.onClose();
                }
            }
        });

        topView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.hideKeyBoard(view);
                dialog.dismiss();
            }
        });

        btnEnglish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(mListener != null){
                    mListener.onEnglishClicked();
                }
            }
        });

        btnChinese.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(mListener != null){
                    mListener.onChineseClicked();
                }
            }
        });

    }

    private void initViewComponents(View view) {
        topView = view.findViewById(R.id.top_view);
        btnChinese = (TextView) view.findViewById(R.id.btn_chinese);
        btnEnglish = (TextView) view.findViewById(R.id.btn_english);
    }



    public interface LanguageDialogListener{
        void onClose();
        void onEnglishClicked();
        void onChineseClicked();
    }
}
