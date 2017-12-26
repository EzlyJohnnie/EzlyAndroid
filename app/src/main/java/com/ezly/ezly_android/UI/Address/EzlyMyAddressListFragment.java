package com.ezly.ezly_android.UI.Address;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Presenter.AddressPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.AddressView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 6/11/16.
 */

public class EzlyMyAddressListFragment extends EzlyBaseFragment implements AddressView, AddressListAdapter.OnAddressClickedListener {
    private static final String KEY_ADDRESSES = "key_addresses";

    @BindView(R.id.txt_title) TextView txtTitle;

    @BindView(R.id.post_list) RecyclerView postList;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;

    @Inject
    AddressPresenter presenter;

    private AddressListAdapter addressListAdapter;
    private ArrayList<EzlyAddress> addresses;

    public static EzlyMyAddressListFragment newInstance(){
        EzlyMyAddressListFragment fragment = new EzlyMyAddressListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_address_list, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        loadData();
    }

    @Override
    public void onStop(){
        super.onStop();
        showTabbar(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ADDRESSES, addresses);
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState, View root) {
        if(savedInstanceState != null){
            addresses = savedInstanceState.getParcelableArrayList(KEY_ADDRESSES);
        }
        initializeInjector();
        presenter.setView(this);
        initView(root);
    }

    private void loadData(){
        presenter.getMyAddressList();
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

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postList.setLayoutManager(layoutManager);

        addressListAdapter = new AddressListAdapter(addresses);
        addressListAdapter.setOnAddressClickedListener(this);
        postList.setAdapter(addressListAdapter);
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).dismissSelf();
        }
    }

    @OnClick(R.id.btn_add)
    public void onAddClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pushReplace(EzlyAddressDetailFragment.newInstance());
        }
    }

    @Override
    public void onAddressClicked(EzlyAddress address) {
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pushReplace(EzlyAddressDetailFragment.newInstance(address));
        }
    }

    @Override
    public void onAddressListPrepared(List<EzlyAddress> addresses) {
        if(this.addresses == null){
            this.addresses = new ArrayList<>();
        }

        this.addresses.clear();
        this.addresses.addAll(addresses);

        refreshLayout.setRefreshing(false);
        addressListAdapter.setAddress(addresses);
    }

    @Override
    public void onSimpleRequestCompleted(boolean isSuccess, String errorMsg) {

    }
}
