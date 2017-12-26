package com.ezly.ezly_android.Presenter;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.ezly.ezly_android.Data.EzlySetting;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.ViewInterFace.SettingView;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by Johnnie on 18/11/16.
 */
public class SettingPresenter extends BasePresenter {
    private static final String KEY_LANGUAGE = "key_language";

    private SettingView mView;

    @Inject
    public SettingPresenter() {
    }

    public void setView(SettingView mView) {
        this.mView = mView;
    }

    public void setLanguage(int language) {
        EzlySetting.getInstance().setLanguage(language);
        EzlySetting.getInstance().saveToSharedPreference(mView.getContext());

        String localeStr = "";
        switch (language){
            case EzlySetting.LANGUAGE_CHINESE:
                localeStr = "zh";
                break;
            case EzlySetting.LANGUAGE_ENGLISH:
                localeStr = "en";
                break;
        }
        Locale myLocale = new Locale(localeStr);
        Resources res = mView.getContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(mView.getContext(), MainActivity.class);
        refresh.putExtra(MainActivity.KEY_REFRESH, true);
        mView.getContext().startActivity(refresh);
    }

    public int getCurrentLanguage(){
        return EzlySetting.getInstance().getLanguage();
    }


}
