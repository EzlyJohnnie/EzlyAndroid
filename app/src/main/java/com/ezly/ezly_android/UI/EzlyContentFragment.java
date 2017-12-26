package com.ezly.ezly_android.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 10/03/17.
 */

public class EzlyContentFragment extends EzlyBaseFragment {
    private static final String KEY_TITLE_RES = "key_titleRes";
    private static final String KEY_CONTENT_RES = "key_contentRes";

    private int titleRes;
    private int contentRes;
    @BindView(R.id.txt_content) TextView txtContent;
    @BindView(R.id.txt_title) TextView txtTitle;

    public static EzlyContentFragment getInstance(int titleRes, int contentRes){
        EzlyContentFragment fragment = new EzlyContentFragment();
        Bundle arg = new Bundle();
        arg.putInt(KEY_TITLE_RES, titleRes);
        arg.putInt(KEY_CONTENT_RES, contentRes);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).showBottomTabbar(false);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).showBottomTabbar(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putInt(KEY_TITLE_RES, titleRes);
        outState.putInt(KEY_CONTENT_RES, contentRes);
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState, View root) {
        if(savedInstanceState == null && getArguments() != null){
            titleRes  = getArguments().getInt(KEY_TITLE_RES);
            contentRes  = getArguments().getInt(KEY_CONTENT_RES);
        }
        else{
            titleRes = savedInstanceState.getInt(KEY_TITLE_RES);
            contentRes = savedInstanceState.getInt(KEY_CONTENT_RES);
        }

        initView(root);
    }

    private void initView(View root) {
        txtTitle.setText(getString(titleRes));
        txtContent.setText(getString(contentRes));

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pop();
        }
        else if(getActivity() instanceof EzlyBaseActivity){
            ((EzlyBaseActivity)getActivity()).pop();
        }
    }

}
