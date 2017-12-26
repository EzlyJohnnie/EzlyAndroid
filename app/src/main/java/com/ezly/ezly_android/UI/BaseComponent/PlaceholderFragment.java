package com.ezly.ezly_android.UI.BaseComponent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezly.ezly_android.R;

import butterknife.ButterKnife;

/**
 * Created by Johnnie on 24/10/16.
 */

public class PlaceholderFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_placeholder, container, false);
        ButterKnife.bind(this, root);
        return root;
    }
}
