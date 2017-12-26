package com.ezly.ezly_android.UI.Address;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Presenter.AddressPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.AddressView;
import com.ezly.ezly_android.Utils.TextUtils;
import com.mapbox.services.geocoding.v5.models.CarmenContext;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 13/02/17.
 */

public class EzlyAddressDetailFragment extends EzlyBaseFragment implements AddressView {
    private static final int VIEW_MODE_ADD = 0;
    private static final int VIEW_MODE_EDIT = 1;
    private static final String KEY_ADDRESS = "key_address";
    private static final String KEY_VIEW_MODE = "key_viewType";
    private static final String KEY_IS_FOR_PRESENT_VIEW = "key_isForPresentView";

    @Inject AddressPresenter presenter;
    @Inject LocationHelper locationHelper;
    private EzlyAddress address;
    private int viewMode;
    private AddAddressListener listener;
    private boolean isForPresentView;

    @BindView(R.id.txt_name)            EditText txtName;
    @BindView(R.id.txt_line_1)          AutoCompleteTextView txtLine1;
    @BindView(R.id.txt_line_2)          EditText txtLine2;
    @BindView(R.id.txt_suburb)          EditText txtSuburb;
    @BindView(R.id.txt_city)            EditText txtCity;
    @BindView(R.id.txt_zip_code)        EditText txtZipCode;
    @BindView(R.id.txt_country)         EditText txtCountry;
    @BindView(R.id.txt_title)           TextView txtTitle;
    @BindView(R.id.btn_delete)          View btnDelete;
    @BindView(R.id.btn_add)             TextView btnAdd;
    @BindView(R.id.loading_indicator)   View loadingIndicator;
    @BindView(R.id.iv_back)             ImageView ivBack;
    @BindView(R.id.txt_back)            TextView txtback;


    public static EzlyAddressDetailFragment newInstance(){
        return getInstance(false);

    }

    public static EzlyAddressDetailFragment newInstanceForPresentView(){
        return getInstance(true);
    }

    private static EzlyAddressDetailFragment getInstance(boolean isForPresentView){
        EzlyAddressDetailFragment fragment = new EzlyAddressDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_VIEW_MODE, VIEW_MODE_ADD);
        args.putBoolean(KEY_IS_FOR_PRESENT_VIEW, isForPresentView);
        fragment.setArguments(args);
        return fragment;
    }

    public static EzlyAddressDetailFragment newInstance(EzlyAddress address){
        EzlyAddressDetailFragment fragment = new EzlyAddressDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_ADDRESS, address);
        args.putInt(KEY_VIEW_MODE, VIEW_MODE_EDIT);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(AddAddressListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_address_detail, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        showTabbar(false);
    }

    @Override
    public void onStop(){
        super.onStop();
        showTabbar(true);
        listener = null;
    }

    private void init(Bundle savedInstanceState, View root) {
        initData(savedInstanceState);
        initializeInjector();
        presenter.setView(this);
        initView(root);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_ADDRESS, address);
        outState.putInt(KEY_VIEW_MODE, viewMode);
        outState.putBoolean(KEY_IS_FOR_PRESENT_VIEW, isForPresentView);
        super.onSaveInstanceState(outState);
    }

    private void initData(Bundle savedInstanceState) {
        if(savedInstanceState == null && getArguments() != null){
            address = getArguments().getParcelable(KEY_ADDRESS);
            viewMode = getArguments().getInt(KEY_VIEW_MODE);
            isForPresentView = getArguments().getBoolean(KEY_IS_FOR_PRESENT_VIEW);
        }
        else{
            address = savedInstanceState.getParcelable(KEY_ADDRESS);
            viewMode = savedInstanceState.getInt(KEY_VIEW_MODE);
            isForPresentView = savedInstanceState.getBoolean(KEY_IS_FOR_PRESENT_VIEW);
        }
    }


    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView(View root) {
        showTabbar(false);
        setupAutoConpoletEditText();
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });
        txtSuburb.setEnabled(false);
        txtCity.setEnabled(false);
        txtZipCode.setEnabled(false);
        txtCountry.setEnabled(false);
        showLoadingIndicator(false);

        if(isForPresentView){
            ivBack.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_close));
            txtback.setVisibility(View.GONE);
        }

        if(viewMode == VIEW_MODE_ADD){
            txtTitle.setText(getContext().getResources().getString(R.string.add_address_title));
            btnDelete.setVisibility(View.GONE);
            btnAdd.setText(getContext().getResources().getString(R.string.add));
        }
        else if(viewMode == VIEW_MODE_EDIT){
            txtTitle.setText(getContext().getResources().getString(R.string.update_address));
            btnAdd.setText(getContext().getResources().getString(R.string.update));
            txtName.setText(TextUtils.isEmpty(address.getName()) ? "" : address.getName());
            txtLine1.setText(TextUtils.isEmpty(address.getLine1()) ? "" : address.getLine1());
            txtLine2.setText(TextUtils.isEmpty(address.getLine2()) ? "" : address.getLine2());
            txtSuburb.setText(TextUtils.isEmpty(address.getSuburb()) ? "" : address.getSuburb());
            txtCity.setText(TextUtils.isEmpty(address.getCity()) ? "" : address.getCity());
            txtZipCode.setText(TextUtils.isEmpty(address.getZipCode()) ? "" : address.getZipCode());
            txtCountry.setText(TextUtils.isEmpty(address.getCountry()) ? "" : address.getCountry());
        }
    }

    private void setupAutoConpoletEditText() {
        final GeocoderAdapter adapter = new GeocoderAdapter(locationHelper);
        txtLine1.setAdapter(adapter);
        txtLine1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CarmenFeature result = adapter.getItem(position);
                onAddressSelected(result);
            }
        });

        // Add clear button to autocomplete
        final Drawable imgClearButton = getResources().getDrawable(R.drawable.ic_close);
        txtLine1.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
        txtLine1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AutoCompleteTextView et = (AutoCompleteTextView) v;
                if (et.getCompoundDrawables()[2] == null)
                    return false;
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > et.getWidth() - et.getPaddingRight() - imgClearButton.getIntrinsicWidth()) {
                    txtLine1.setText("");
                    txtLine2.setText("");
                    txtSuburb.setText("");
                    txtCity.setText("");
                    txtZipCode.setText("");
                    txtCountry.setText("");
                }
                return false;
            }
        });
    }

    private void onAddressSelected(CarmenFeature result) {
        UIHelper.hideKeyBoard(getView());
        if(address == null){
            address = new EzlyAddress();
        }

        address.getLocation().setLatitude((float)result.getCenter()[1]);
        address.getLocation().setLongitude((float)result.getCenter()[0]);

        List<CarmenContext> featureContexts = result.getContext();

        String street = result.getText();
        String fullAddress = result.getPlaceName();
        String[] fullAddressAry = fullAddress.split(",");
        if(fullAddressAry != null && fullAddressAry.length > 0){
            street = fullAddressAry[0];
        }
        address.setLine1(street);

        String suburb = null;
        String city = null;
        String region = null;
        String country = null;
        String postcode = null;
        for(CarmenContext featureContext : featureContexts){
            if(featureContext.getId().contains("locality")){
                suburb = featureContext.getText();
                address.setSuburb(suburb);
            }
            else if(featureContext.getId().contains("place")){
                city = featureContext.getText();
                address.setCity(city);
            }
            else if(featureContext.getId().contains("region")){
                region = featureContext.getText();
            }
            else if(featureContext.getId().contains("country")){
                country = featureContext.getText();
                address.setCountry(country);
            }
            else if(featureContext.getId().contains("postcode")){
                postcode = featureContext.getText();
                address.setZipCode(postcode);
            }
        }

        if(!TextUtils.isEmpty(street)){
            txtLine1.setText(street);
        }
        if(!TextUtils.isEmpty(suburb)){
            txtSuburb.setText(suburb);
        }
        if(!TextUtils.isEmpty(city)){
            txtCity.setText(city);
        }
        if(!TextUtils.isEmpty(postcode)){
            txtZipCode.setText(postcode);
        }
        if(!TextUtils.isEmpty(country)){
            txtCountry.setText(country);
        }

    }

    private void dismiss(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pop();
        }
    }

    private void showLoadingIndicator(boolean isShow){
        loadingIndicator.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        UIHelper.hideKeyBoard(getView());
        dismiss();
    }

    @OnClick(R.id.btn_delete)
    public void onDeleteClicked(){
        presenter.deleteAddress(address.getId());
    }

    @OnClick(R.id.btn_add)
    public void onPostClicked(){
        UIHelper.hideKeyBoard(getView());
        showLoadingIndicator(true);
        String line2 = txtLine2.getText().toString();
        if(!TextUtils.isEmpty(line2)){
            address.setLine2(line2);
        }
        String name = txtName.getText().toString();
        if(!TextUtils.isEmpty(name)){
            address.setName(name);
        }

        if(viewMode == VIEW_MODE_ADD){
            presenter.addAddress(address);
        }
        else if(viewMode == VIEW_MODE_EDIT){
            presenter.updateAddress(address);
        }
    }

    @Override
    public void onAddressListPrepared(List<EzlyAddress> addresses) {

    }

    @Override
    public void onSimpleRequestCompleted(boolean isSuccess, String errorMsg) {
        showLoadingIndicator(false);
        if(!TextUtils.isEmpty(errorMsg)){
            SingleToast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }

        if(isSuccess) {
            if(listener != null && errorMsg.contains(getContext().getResources().getString(R.string.address_operation_add))){
                listener.onAddressAdd(address);
            }
            UIHelper.hideKeyBoard(getView());
            dismiss();
        }
    }



    public interface AddAddressListener{
        void onAddressAdd(EzlyAddress address);
    }
}
