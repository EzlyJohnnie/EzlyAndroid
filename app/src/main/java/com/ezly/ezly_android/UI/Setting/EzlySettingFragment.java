package com.ezly.ezly_android.UI.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlySetting;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.SettingPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.DialogView.LanguageDialogView;
import com.ezly.ezly_android.UI.EzlyContentFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.ViewInterFace.SettingView;
import com.ezly.ezly_android.Utils.Config;
import com.ezly.ezly_android.Utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ezly.ezly_android.Data.EzlySetting.LANGUAGE_ENGLISH;

/**
 * Created by Johnnie on 18/11/16.
 */
public class EzlySettingFragment extends EzlyBaseFragment implements SettingView, LanguageDialogView.LanguageDialogListener{
    public static final String KEY_HAS_BACK_BTN = "key_hasBackBtn";

    @Inject SettingPresenter presenter;

    @Inject MemberHelper memberHelper;

    @BindView(R.id.normal_message_swtich)       SwitchCompat normalMessageSwitch;
    @BindView(R.id.important_message_swtich)    SwitchCompat importantMessageSwitch;
    @BindView(R.id.txt_logout) TextView txtLogout;
    @BindView(R.id.txt_cache_size) TextView txtCacheSize;
    @BindView(R.id.txt_version) TextView txtVersion;
    @BindView(R.id.terms_pnl) View termsPnl;
    @BindView(R.id.btn_back) View btnback;


    private boolean hasBackBtn;

    public static EzlySettingFragment getInstance(boolean hasBackBtn){
        EzlySettingFragment fragment = new EzlySettingFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_HAS_BACK_BTN, hasBackBtn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_HAS_BACK_BTN, hasBackBtn);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        memberHelper.reset();
    }

    private void init(Bundle savedInstanceState, View root) {
        initData(savedInstanceState);
        initializeInjector();
        presenter.setView(this);
        initView(root);
    }

    private void initData(Bundle savedInstanceState) {
        if(savedInstanceState == null && getArguments() != null){
            hasBackBtn = getArguments().getBoolean(KEY_HAS_BACK_BTN, false);
        }
        else{
            hasBackBtn = savedInstanceState.getBoolean(KEY_HAS_BACK_BTN, false);
        }
    }


    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView(View root) {
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = String.format("ver %s", pInfo.versionName);
            txtVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btnback.setVisibility(hasBackBtn ? View.VISIBLE : View.GONE);
        normalMessageSwitch.setChecked(EzlySetting.getInstance().isAllMessageEnabled());
        importantMessageSwitch.setChecked(EzlySetting.getInstance().isImportantMessageEnabled());

        txtCacheSize.setText(FileUtils.getSizeString(FileUtils.getCacheFileSize(getContext()), true));
        txtLogout.setEnabled(memberHelper.hasLogin());

        txtLogout.setTextColor(getContext().getResources().getColor(memberHelper.hasLogin() ? R.color.delete_red :R.color.divider ));

        normalMessageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EzlySetting.getInstance().setAllMessageEnabled(normalMessageSwitch.isChecked());
                saveSettingToSharedPreference();
            }
        });

        importantMessageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EzlySetting.getInstance().setImportantMessageEnabled(importantMessageSwitch.isChecked());
                saveSettingToSharedPreference();
            }
        });

        if(presenter.getCurrentLanguage() == LANGUAGE_ENGLISH){
            termsPnl.setVisibility(View.GONE);
        }
        else{
            termsPnl.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_back)
    public void back(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).dismissSelf();
        }
        else if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).pop();
        }
    }

    @OnClick(R.id.btn_clear_cache)
    public void clearCache(){
        FileUtils.clearCache(getContext());
        txtCacheSize.setText("0 B");
    }

    @OnClick(R.id.btn_language)
    public void changeLanguage(){
        LanguageDialogView languageDialogView = LanguageDialogView.newInstance(getActivity(), presenter.getCurrentLanguage());
        languageDialogView.setListener(this);
        UIHelper.presentBottomMenu(languageDialogView);
    }

    @OnClick(R.id.btn_privacy)
    public void showPrivacy(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).push(EzlyContentFragment.getInstance(R.string.privacy_title, R.string.privacy));
        }
    }

    @OnClick(R.id.btn_terms)
    public void showTerms(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).push(EzlyContentFragment.getInstance(R.string.terms_title, R.string.terms));
        }
    }

    @OnClick(R.id.btn_about_us)
    public void showAboutUs(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Config.ABOUT_US_URL));
        startActivity(i);
    }

    @OnClick(R.id.btn_logout)
    public void logout(){
        memberHelper.logout(getContext());
        txtLogout.setOnClickListener(null);
        txtLogout.setTextColor(getContext().getResources().getColor(R.color.divider));
        SingleToast.makeText(getContext(), "You have logout from Ezly", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new ResultEvent.OnLogoutEvent());
    }

    private void saveSettingToSharedPreference(){
        EzlySetting.getInstance().saveToSharedPreference(getContext());
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onEnglishClicked() {
        UIHelper.displayYesNoDialog(getContext(), "", getContext().getResources().getString(R.string.change_language_confirmation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performChangeLanguage(LANGUAGE_ENGLISH);
            }
        });
    }

    private void performChangeLanguage(int language) {
        presenter.setLanguage(language);
    }

    @Override
    public void onChineseClicked() {
        UIHelper.displayYesNoDialog(getContext(), "", getContext().getResources().getString(R.string.change_language_confirmation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performChangeLanguage(EzlySetting.LANGUAGE_CHINESE);
            }
        });
    }
}
