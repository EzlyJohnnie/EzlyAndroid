package com.ezly.ezly_android.UI.Post;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.ImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.MultiImagePickerHelper;
import com.ezly.ezly_android.Utils.Helper.PermissionHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.CategoryIconHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyImage;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.PostDetailPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.Address.EzlyAddressDetailFragment;
import com.ezly.ezly_android.UI.AlertView.ViewFilePickMenu;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.DialogView.LocationSelectDialogView;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.ViewInterFace.PostDetailView;
import com.ezly.ezly_android.UI.event.EzlyFullImageFragment;
import com.ezly.ezly_android.Utils.Config;
import com.mapbox.geocoder.GeocoderCriteria;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.geocoder.service.models.FeatureContext;
import com.mapbox.geocoder.service.models.GeocoderFeature;
import com.mapbox.geocoder.service.models.GeocoderResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Response;

/**
 * Created by Johnnie on 21/11/16.
 */

public class EzlyNewPostFragment extends EzlyBaseFragment implements PostDetailView,
        PostImageListAdapter.PostImageAdapterListener,
        EzlyFullImageFragment.OnImageDeletedListener,
        LocationSelectDialogView.LocationSelectViewListener,
        EzlyAddressDetailFragment.AddAddressListener,
        ViewFilePickMenu.FilePickMenuListener
{
    public static final int VIEW_TYPE_POST_EVENT = 0;
    public static final int VIEW_TYPE_UPDATE_EVENT = 1;

    public static final String KEY_EVENT = "key_event";
    public static final String KEY_VIEW_TYPE = "key_viewType";

    @Inject PostDetailPresenter presenter;

    @Inject MemberHelper memberHelper;
    @Inject LocationHelper locationHelper;
    @Inject ImagePickerHelper imagePickerHelper;
    @Inject MultiImagePickerHelper multiImagePickerHelper;
    @Inject PermissionHelper permissionHelper;

    @BindView(R.id.iv_category) ImageView ivCategory;
    @BindView(R.id.expire_spinner) Spinner expireSpinner;
    @BindView(R.id.txt_title) EditText txtTitle;
    @BindView(R.id.txt_desc) EditText txtDesc;
    @BindView(R.id.image_list) RecyclerView imageList;
    @BindView(R.id.txt_location) TextView txtLocation;
    @BindView(R.id.btn_post) Button btnPost;
    @BindView(R.id.loading_indicator) View loadingIndicator;

    private PostEventListener listener;
    private EzlyEvent event;
    private int viewType;
    private boolean isUploadingPhoto;
    private List<EzlyAddress> addresses;
    private EzlyAddress selectedAddress;
    ArrayList<EzlyImage> imagesToDelete = new ArrayList<>();
    private boolean hasClickPost;//post event after post current address

    private PostImageListAdapter imageListAdapter;
    private boolean hasLoadAddress;
    private float shouldSelectedAddressLat;
    private float shouldSelectedAddressLng;
    private boolean shouldReloadPreviousView;

    public static EzlyNewPostFragment getInstance(EzlyEvent event, int viewType){
        EzlyNewPostFragment fragment = new EzlyNewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_EVENT, event);
        bundle.putInt(KEY_VIEW_TYPE, viewType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(PostEventListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_post_event, container, false);
        ButterKnife.bind(this, root);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        if(listener != null && shouldReloadPreviousView){
            listener.reloadPreviousView();
        }
        listener = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        memberHelper.reset();
    }

    private void init(Bundle savedInstanceState, View root) {
        if(savedInstanceState == null){
            if(getArguments() != null){
                event = getArguments().getParcelable(KEY_EVENT);
                viewType = getArguments().getInt(KEY_VIEW_TYPE);
            }
        }
        else{
            event = savedInstanceState.getParcelable(KEY_EVENT);
            viewType = savedInstanceState.getInt(KEY_VIEW_TYPE);
        }

        initializeInjector();
        presenter.setView(this);
        initView(root);

        presenter.getMyAddressList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_EVENT, event);
        outState.putInt(KEY_VIEW_TYPE, viewType);
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

        initSpinner();
        initImageList();

        if(event.getAddress() != null && event.getAddress().getLocation().isValidLocation()){
            shouldSelectedAddressLat = event.getAddress().getLocation().getLatitude();
            shouldSelectedAddressLng = event.getAddress().getLocation().getLongitude();
        }

        switch (viewType){
            case VIEW_TYPE_POST_EVENT:
                setupEvent();
                break;
            case VIEW_TYPE_UPDATE_EVENT:
                showLoadingIndicator(true);
                btnPost.setText(getContext().getResources().getString(R.string.update));
                presenter.getEventDetail(event);
                break;
        }

    }

    private void setupEvent() {
        String formatStr = "";
        String desc = "";

        if(viewType == VIEW_TYPE_UPDATE_EVENT){
            txtTitle.setText(event.getTitle());
            txtDesc.setText(event.getDescription());
        }



        if(event instanceof EzlyJob){
            formatStr = getContext().getResources().getString(R.string.hint_post_job_title);
            desc = getContext().getResources().getString(R.string.hint_post_job_desc);
        }
        else if(event instanceof EzlyService){
            formatStr = getContext().getResources().getString(R.string.hint_post_service_title);
            desc = getContext().getResources().getString(R.string.hint_post_service_desc);
        }
        txtTitle.setHint(String.format(formatStr, event.getCategory().getName()));
        txtDesc.setHint(desc);

        ivCategory.setImageDrawable(getContext().getResources().getDrawable(CategoryIconHelper.getCategoryIcon(event.getCategory().getCode())));
    }

    private void initImageList() {
        List<EzlyImage> images = new ArrayList<>();
        if(event.getImages() != null){
             images.addAll(event.getImages());
        }
        imageListAdapter = new PostImageListAdapter(images);

        imageListAdapter.setListener(this);
        GridLayoutManager lLayout = new GridLayoutManager(getContext(), 5);
        imageList.setHasFixedSize(true);
        imageList.setLayoutManager(lLayout);
        imageList.setAdapter(imageListAdapter);
    }

    private void initSpinner() {
        String[] items = getContext().getResources().getStringArray(R.array.expire_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.view_expire_spinner_item, items);

        expireSpinner.setAdapter(adapter);

        expireSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                event.setExpireTime(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        expireSpinner.setSelection(0);
    }

    private void showLoadingIndicator(boolean isShow){
        loadingIndicator.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.location_pnl)
    public void onLocationClicked(){

    }

    @OnClick(R.id.loading_indicator)
    public void onLoadingIndicatorClicked(){
        //consuming click event
    }

    @OnClick(R.id.location_pnl)
    public void selectLocation(){
        if(hasLoadAddress) {
            LocationSelectDialogView locationSelectDialogView = LocationSelectDialogView.newInstance(getActivity(), addresses);
            locationSelectDialogView.setListener(this);
            UIHelper.presentBottomMenu(locationSelectDialogView);
        }
    }


    @OnClick(R.id.btn_post)
    public void onPostClicked(){
        hasClickPost = true;
        showLoadingIndicator(true);
        prepareEvent();
    }

    private void prepareEvent() {
        event.setTitle(txtTitle.getText().toString());
        event.setDescription(txtDesc.getText().toString());
        Calendar startC = Calendar.getInstance();
        Calendar endC = Calendar.getInstance();
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date startDate = startC.getTime();
        switch (event.getExpireTime()){
            case 0:
                endC.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case 1:
                endC.add(Calendar.DAY_OF_YEAR, 2);
                break;
            case 2:
                endC.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case 3:
                endC.add(Calendar.MONTH, 1);
                break;

        }
        Date endDate = endC.getTime();

        event.setStartDate(inFormat.format(startDate));
        event.setEndDate(inFormat.format(endDate));

        if(selectedAddress != null) {
            event.setAddress(selectedAddress);
            onEventWrapped();
        }
        else{
            //TODO: move to presenter
            locationHelper.getCurrentLocation((EzlyBaseActivity) getActivity(), new LocationHelper.LocationCallback() {
                @Override
                public void onLocationGet(final EzlyAddress address) {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MapboxGeocoder client = new MapboxGeocoder.Builder()
                                        .setAccessToken(Config.MAPBOX_ACCESS_TOKEN)
                                        .setCoordinates(address.getLocation().getLongitude(), address.getLocation().getLatitude())
                                        .setType(GeocoderCriteria.TYPE_ADDRESS)
                                        .build();

                                Response<GeocoderResponse> response = client.execute();

                                List<GeocoderFeature> geoAddresses = response.body().getFeatures();
                                if (geoAddresses != null && geoAddresses.size() > 0) {
                                    GeocoderFeature geoAddress = geoAddresses.get(0);

                                    String name = geoAddress.getText();
                                    String[] addressAry = geoAddress.getText().split(",");
                                    if(addressAry != null && addressAry.length > 0){
                                        name = addressAry[0];
                                    }
                                    address.setName(name);
                                    address.setLine1(name);

                                    List<FeatureContext> featureContexts = geoAddress.getContext();
                                    for(FeatureContext featureContext : featureContexts){
                                        if(featureContext.getId().contains("locality")){
                                            address.setSuburb(featureContext.getText());
                                        }
                                        else if(featureContext.getId().contains("place")){
                                            address.setCity(featureContext.getText());
                                        }
                                        else if(featureContext.getId().contains("country")){
                                            address.setCountry(featureContext.getText());
                                        }
                                        else if(featureContext.getId().contains("postcode")){
                                            address.setZipCode(featureContext.getText());
                                        }
                                    }

                                    shouldSelectedAddressLat = address.getLocation().getLatitude();
                                    shouldSelectedAddressLng = address.getLocation().getLongitude();
                                    presenter.addAddress(address);

                                    event.setAddress(address);
                                }
                                else{
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showLoadingIndicator(false);
                                            SingleToast.makeText(getContext(), getContext().getResources().getString(R.string.cannot_find_address), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();
                }

                @Override
                public void onLocationFail(boolean shouldShowRationale) {

                }
            });
        }
    }

    private void onEventWrapped(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isInputValid()){
                    showLoadingIndicator(false);
                    return;
                }

                switch (viewType){
                    case VIEW_TYPE_POST_EVENT:
                        presenter.postEvent(event);
                        break;
                    case VIEW_TYPE_UPDATE_EVENT:
                        presenter.updateEvent(event);
                        break;
                }

            }
        });
    }

    private boolean isInputValid() {
        boolean isInputValid = true;
        if(TextUtils.isEmpty(event.getTitle())){
            isInputValid = false;
            SingleToast.makeText(getContext(), getContext().getResources().getString(R.string.title_empty_error), Toast.LENGTH_SHORT).show();
        }
        return isInputValid;
    }

    private void finishPostEvent(){
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).pop();
        }
        EventBus.getDefault().post(new ResultEvent.ReloadMapEvent());
    }

    @OnClick(R.id.iv_category)
    public void onCategoryClicked(){
        UIHelper.displayYesNoDialog(getContext(), getContext().getResources().getString(R.string.cancel_post), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(getParentFragment() instanceof  EzlyPostHostFragment){
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    ((EzlyPostHostFragment)getParentFragment()).pop();
                }
            }
        });
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        if(isUploadingPhoto){
            UIHelper.displayYesNoDialog(getContext(),
                    "",
                    getContext().getResources().getString(R.string.cancel_upload_confirm),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismissSelf();
                        }
                    });
        }
        else{
            dismissSelf();
        }
    }

    private void dismissSelf(){
        UIHelper.hideKeyBoard(getView());
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pop();
        }
        else if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).pop();
        }
    }

    //image list listener
    @Override
    public void onAddImageClicked(int limit) {
        ViewFilePickMenu.presentView(getActivity(), limit, this);
    }

    @Override
    public void onImageClicked(EzlyImage image, int position) {
        ArrayList<EzlyImage> images = new ArrayList<>();
        images.addAll(imageListAdapter.getImages());

        EzlyFullImageFragment fullImageFragment = EzlyFullImageFragment.getInstanceForLocalPhotos(images, position);
        fullImageFragment.setListener(this);

        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).presentFragment(fullImageFragment);
        }
        else if(getActivity() instanceof EzlyBaseActivity){
            ((EzlyBaseActivity)getActivity()).presentFragment(fullImageFragment);
        }
    }

    @Override
    public void onEventPrepared(EzlyEvent event) {
        showLoadingIndicator(false);
        this.event = event;
        setupEvent();
        imageListAdapter.setImages(event.getImages());
    }

    //presenter callback
    @Override
    public void onPosted(boolean isSuccess, EzlyEvent event) {
        hasClickPost = false;
        String string = getContext().getResources().getString(isSuccess ? R.string.post_success : R.string.post_fail);
        SingleToast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
        if(isSuccess){
            if(event.getImagesForPost() != null && event.getImagesForPost().size() > 0) {
                presenter.postImage(event);
            }
            else{
                finishPostEvent();
            }
        }
        else{
            showLoadingIndicator(false);
        }
    }

    @Override
    public void onUpdateEvent(boolean isSuccess, EzlyEvent event) {
        String string = getContext().getResources().getString(isSuccess ? R.string.update_success : R.string.update_fail);
        SingleToast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
        if(isSuccess){
            shouldReloadPreviousView = true;

            if(imagesToDelete != null){
                presenter.deleteImagesForEvent(event, imagesToDelete);
            }

            if(event.getImagesForPost() != null && event.getImagesForPost().size() > 0) {
                presenter.postImage(event);
            }
            else{
                finishPostEvent();
            }
        }
        else{
            showLoadingIndicator(false);
        }
    }

    @Override
    public void onImageStartUploading(int imageIndex) {
        isUploadingPhoto = true;
    }

    @Override
    public void onImageUploading(int imageIndex, float progress) {

    }

    @Override
    public void onImageUploadCompleted(int imageIndex) {

    }

    @Override
    public void onImageUploadFailed(int imageIndex, String errorMsg) {

    }

    @Override
    public void onUploadCompleted(int totalSuccessCount) {
        isUploadingPhoto = false;
        showLoadingIndicator(false);
        if(totalSuccessCount == event.getImagesForPost().size()){
            finishPostEvent();
        }
    }

    @Override
    public void onAddressPrepared(List<EzlyAddress> addresses) {
        hasLoadAddress = true;
        this.addresses = addresses;
        if(addresses != null && addresses.size() > 0){
            if(shouldSelectedAddressLat != 0 && shouldSelectedAddressLng != 0) {
                for (EzlyAddress address : addresses) {
                    if(address.getLocation().getLatitude() == shouldSelectedAddressLat
                            && address.getLocation().getLongitude() == shouldSelectedAddressLng)
                    {
                        selectedAddress = address;
                    }
                }
            }
            else{
                selectedAddress = addresses.get(0);
            }

            txtLocation.setText(selectedAddress.getFullAddress());
        }
        else{
            txtLocation.setText(getContext().getResources().getString(R.string.current_address));
        }

        if(hasClickPost){
            prepareEvent();
        }
    }

    @Override
    public void onAddressAdd(boolean isSuccess, String message) {
        if(isSuccess){
            presenter.getMyAddressList();
        }
    }


    @Override
    public void onImageDeleted(int position) {
        imagesToDelete.add(event.getImages().get(position));
        imageListAdapter.removeImage(position);
        if(event.getImagesForPost() != null && event.getImagesForPost().size() > position){
            event.getImagesForPost().remove(position);
        }

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onUploadClicked(final int limit) {
        permissionHelper.requestPermission((EzlyBaseActivity) getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionHelper.PermissionCallBack() {
            @Override
            public void onGranted(String[] permissions) {
                multiImagePickerHelper.startPickImageFromGallery((EzlyBaseActivity)getActivity(),
                        limit,
                        new MultiImagePickerHelper.OnImageSelectedListener() {
                            @Override
                            public void onImagesSelected(ArrayList<EzlyImage> images) {
                                imageListAdapter.addImages(images);
                                event.addImagesForPost(images);
                            }
                        });
            }

            @Override
            public void onDenied(String[] permissions, int[] grantResults) {

            }

            @Override
            public void onShouldShowRationale(String permission) {

            }
        });
    }

    @Override
    public void onTakePhotoClicked() {
        imagePickerHelper.takePhoto((EzlyBaseActivity) getActivity(), new ImagePickerHelper.OnImageSelectedListener() {
            @Override
            public void onImageSelected(ArrayList<EzlyImage> images) {
                imageListAdapter.addImages(images);
                event.addImagesForPost(images);
            }
        });
    }

    @Override
    public void onAddressSelected(EzlyAddress address) {
        selectedAddress = address;
        txtLocation.setText(address.getFullAddress());
    }

    @Override
    public void onAddClick() {
        if(getParentFragment() instanceof  EzlyPostHostFragment){
            EzlyAddressDetailFragment fragment = EzlyAddressDetailFragment.newInstanceForPresentView();
            fragment.setListener(this);
            ((EzlyPostHostFragment)getParentFragment()).presentFragment(fragment);
        }
    }

    @Override
    public void onAddressAdd(EzlyAddress address) {
        shouldSelectedAddressLat = address.getLocation().getLatitude();
        shouldSelectedAddressLng = address.getLocation().getLongitude();
        presenter.getMyAddressList();
    }


    public interface PostEventListener{
        void reloadPreviousView();
    }
}
