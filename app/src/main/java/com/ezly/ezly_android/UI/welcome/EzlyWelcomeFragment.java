package com.ezly.ezly_android.UI.welcome;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 20/10/16.
 */

public class EzlyWelcomeFragment extends EzlyBaseFragment {

    @BindView(R.id.iv_bg) ImageView ivBG;
    @BindView(R.id.iv_logo) ImageView ivLogo;
    @BindView(R.id.content_view) View contentView;
    @BindView(R.id.txt_version)
    TextView txtVersion;

    private boolean shouldKeepActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, root);
        Picasso.with(getContext())
                .load(R.drawable.login_bg)
                .fit()
                .into(ivBG);
        setupVersion();
        return root;
    }

    private void setupVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = String.format("ver %s", pInfo.versionName);
            txtVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        startAnimation();
    }


    @Override
    public void onStop(){
        if(!shouldKeepActivity){
            getActivity().finish();
        }
        super.onStop();
    }

    private void startAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivLogo.animate()
                        .setDuration(400)
                        .yBy(-100)
                        .translationYBy(-100)
                        .start();

                contentView.animate()
                        .setDuration(400)
                        .alpha(1)
                        .start();
            }
        }, 800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity() != null){
                    shouldKeepActivity = true;
                    MainActivity.startActivity(getActivity());
                    getActivity().finish();
                }
            }
        }, 3000);


    }

}
