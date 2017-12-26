package com.ezly.ezly_android.UI.event.Map;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.CategoryIconHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.FragmentMapMainPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.Post.EzlyPostHostFragment;
import com.ezly.ezly_android.UI.User.EzlyUserHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.FragmentMapMainView;
import com.ezly.ezly_android.UI.event.EzlyEventFragmentHost;
import com.ezly.ezly_android.UI.event.FullDetail.EzlyEventDetailActivity;
import com.ezly.ezly_android.UI.event.List.EzlyEventListAdapter;
import com.ezly.ezly_android.UI.event.List.EzlyEventListFragment;
import com.ezly.ezly_android.UI.login.EzlyLoginHostFragment;
import com.ezly.ezly_android.Utils.TextUtils;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 2/10/16.
 */

public class EzlyMapMainFragment extends EzlyBaseFragment implements FragmentMapMainView,
        View.OnClickListener,
        MapboxMap.OnMapClickListener,
        MapboxMap.OnMarkerClickListener,
        EzlyEventListAdapter.EventListListener
{
    private static final String KEY_HAS_SHOW_LIST_VIEW = "key_hasShowListView";
    private static final String TAG_LIST_FRAGMENT = "tag_listFragment";

    @Inject FragmentMapMainPresenter presenter;
    @Inject LocationHelper locationHelper;
    @Inject MemberHelper memberHelper;
    @Inject EzlySearchParam searchParam;

    @BindView(R.id.mapview) MapView mapView;
    @BindView(R.id.event_list) RecyclerView eventList;


    private MapboxMap map;

    private boolean hasShowListView;
    private HashMap<String, Marker> markers = new HashMap<>();
    private HashMap<Marker, EzlyEvent> markersEventHashMap = new HashMap<>();
    private Marker selectedMarker;
    private EzlyMapEventListAdapter adapter;

    /**
     * toolbar
     */
    @BindView(R.id.search_box) View searchBox;
    @BindView(R.id.divider) View divider;
    @BindView(R.id.txt_search) TextView txtSearch;
    @BindView(R.id.btn_notification) View btnNotificationView;

    @BindView(R.id.event_list_container) FrameLayout listContainer;
    @BindView(R.id.btn_list_view) ImageView btnListView;

    @BindView(R.id.btn_my_location) View btnMyLocation;
    @BindView(R.id.user_avatar) ImageView ivUserAvatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map_main, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_SHOW_LIST_VIEW, hasShowListView);
    }



    private void init(Bundle savedInstanceState) {
        initializeInjector();

        if(savedInstanceState != null){
            hasShowListView = savedInstanceState.getBoolean(KEY_HAS_SHOW_LIST_VIEW);
        }
        else{
            memberHelper.checkCachedToken(getContext());
        }

        markers = new HashMap<>();
        markersEventHashMap = new HashMap<>();
        presenter.setView(this);
        initMap(savedInstanceState);
        initView();
    }

    private void initView() {
        setSearchText();
        txtSearch.setOnClickListener(this);
        btnMyLocation.setOnClickListener(this);
        btnNotificationView.setOnClickListener(this);
        btnListView.setOnClickListener(this);

        initBottomEventList();
        if(hasShowListView){
            showListView();
        }

    }

    private void initBottomEventList() {
        adapter = new EzlyMapEventListAdapter(null, locationHelper, searchParam);
        adapter.setListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        eventList.setLayoutManager(layoutManager);
        eventList.setAdapter(adapter);
        eventList.setVisibility(View.GONE);
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.getUiSettings().setAttributionEnabled(false);
                map.getUiSettings().setCompassMargins(0, UIHelper.dip2px(getContext(), 80), UIHelper.dip2px(getContext(), 10), 0);
                map.setOnMapClickListener(EzlyMapMainFragment.this);
                map.setOnMarkerClickListener(EzlyMapMainFragment.this);
                map.setMaxZoom(14);
                presenter.initMap();
            }
        });
    }

    private void showEventList(EzlyEvent event){
        eventList.setVisibility(View.VISIBLE);
        scrollToEvent(event);
    }

    private void scrollToEvent(EzlyEvent event) {
        if(getParentFragment() instanceof  EzlyEventFragmentHost){
            List<EzlyEvent> events = ((EzlyEventFragmentHost)getParentFragment()).getEvents();
            int index = events.indexOf(event);
            if(index >= 0){
                eventList.smoothScrollToPosition(index);
            }
        }

    }

    private void hideEventList(){
        eventList.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() != R.id.txt_search){
            UIHelper.hideKeyBoard(getView());
        }

        switch (v.getId()){
            case R.id.txt_search:
                onTextSearchClicked(v);
                break;
            case R.id.btn_my_location:
                presenter.onMyLocationClicked();
                break;
            case R.id.btn_notification:
                onNotificationClicked(v);
                break;
            case R.id.btn_list_view:
                if(!hasShowListView){
                    showListView();
                }
                else{
                    hideListView();
                }
                break;
        }
    }

    private void onNotificationClicked(View v) {

    }

    private void onTextSearchClicked(View v) {
        if(getParentFragment() instanceof EzlyEventFragmentHost){
            ((EzlyEventFragmentHost)getParentFragment()).showSearchFragment();
        }
    }

    private void showListView(){
        hideEventList();
        hasShowListView = true;
        btnListView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.map_view));
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_LIST_FRAGMENT);
        if(fragment == null){
            fragment = new EzlyEventListFragment();
        }
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_bottom_in, 0,
                        0, R.anim.slide_bottom_out)
                .replace(R.id.event_list_container, fragment, TAG_LIST_FRAGMENT)
                .addToBackStack(TAG_LIST_FRAGMENT)
                .commit();
    }

    private void hideListView(){
        hasShowListView = false;
        btnListView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.bullet_list));
        presenter.searchEvent();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_LIST_FRAGMENT);
        if(fragment != null){
            getChildFragmentManager().popBackStackImmediate();
        }
    }

    private void setSearchText(){
        String displaySearchText = searchParam.getSearchStr();

        String prefix = "";
        if(searchParam.getSearchMode() == EzlySearchParam.SEARCH_MODE_SERVICE){
            prefix = getContext().getResources().getString(R.string.need);
        }
        else{
            prefix = getContext().getResources().getString(R.string.provide);
        }

        if(!TextUtils.isEmpty(displaySearchText)){
            if(!TextUtils.isEmpty(prefix)){
                displaySearchText = String.format("%s %s", prefix, displaySearchText);
            }

            txtSearch.setText(displaySearchText);
        }
        else if(searchParam.getSelectedCategories() != null && searchParam.getSelectedCategories().size() > 0){
            displaySearchText = searchParam.getSelectedCategories().get(0).getName();
            if(!TextUtils.isEmpty(prefix)){
                displaySearchText = String.format("%s %s", prefix, displaySearchText);
            }

            txtSearch.setText(displaySearchText);
        }
        else{
            txtSearch.setText("");
        }
    }

    private void setSelectedMarker(Marker selectedMarker) {
        EzlyEvent event = markersEventHashMap.get(this.selectedMarker);
        IconFactory iconFactory = IconFactory.getInstance(getContext());
        if(this.selectedMarker != null && event != null){
            Drawable iconDrawable = ContextCompat.getDrawable(getContext(), CategoryIconHelper.getCategorySmallMarkerIcon(event.getCategory().getCode()));
            Icon icon = iconFactory.fromDrawable(iconDrawable);
            this.selectedMarker.setIcon(icon);
        }

        this.selectedMarker = selectedMarker;
        event = markersEventHashMap.get(this.selectedMarker);
        if(this.selectedMarker != null && event != null){
            Drawable iconDrawable = ContextCompat.getDrawable(getContext(), CategoryIconHelper.getCategoryBigMarkerIcon(event.getCategory().getCode()));
            Icon icon = iconFactory.fromDrawable(iconDrawable);
            this.selectedMarker.setIcon(icon);
            showEventList(event);
        }
        else{
            hideEventList();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.ReloadMapEvent event){
        if(isAdded()){
            if(hasShowListView){
                EventBus.getDefault().post(new ResultEvent.ReloadEventsListEvent());
            }
            else{
                presenter.searchEvent();
            }
           setSearchText();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnLoginEvent event){
        if(event.getCurrentUser() != null && !TextUtils.isEmpty(event.getCurrentUser().getAvatarUrl())){
            Picasso.with(getContext())
                    .load(event.getCurrentUser().getAvatarUrl())
                    .fit()
                    .placeholder(R.drawable.placeholder_user)
                    .into(ivUserAvatar);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ResultEvent.OnLogoutEvent event){
        Picasso.with(getContext())
                .load(R.drawable.placeholder_user)
                .fit()
                .into(ivUserAvatar);
    }


    @Override
    public void onMapClick(@NonNull LatLng point) {
        UIHelper.hideKeyBoard(getView());
        setSelectedMarker(null);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        UIHelper.hideKeyBoard(getView());
        setSelectedMarker(marker);
        return false;
    }

    @OnClick(R.id.user_avatar)
    public void onUserAvatarClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).presentFragment(EzlyUserHostFragment.getMyInfoInstance());
        }
    }

    @OnClick(R.id.btn_post)
    public void onPostClicked(){
        if(getActivity() instanceof MainActivity){
            if(memberHelper.hasLogin()){
                ((MainActivity)getActivity()).push(EzlyPostHostFragment.getInstance());
            }
            else{
                ((MainActivity)getActivity()).presentFragment(EzlyLoginHostFragment.getInstance());
            }

        }
    }

    @Override
    public MapboxMap getMap() {
        return map;
    }

    @Override
    public void onEventPrepared(List<EzlyEvent> events) {
        if(getParentFragment() instanceof EzlyEventFragmentHost){
            ((EzlyEventFragmentHost)getParentFragment()).setEvents(events);
        }

        adapter.setEvents(events);
        updateMarker(events);
    }

    private void updateMarker(List<EzlyEvent> events) {
        if(map != null){

            Set<String> markKeys = markers.keySet();
            for(EzlyEvent event : events){
                if(markKeys.contains(event.getId())){
                    markersEventHashMap.put(markers.get(event.getId()), event);
                }
                else{
                    addNewMarker(event);
                }
            }

            removeOldMarkersIfNeed(events);
        }
    }

    private void addNewMarker(EzlyEvent event) {
        IconFactory iconFactory = IconFactory.getInstance(getContext());
        Drawable iconDrawable = ContextCompat.getDrawable(getContext(), CategoryIconHelper.getCategorySmallMarkerIcon(event.getCategory().getCode()));
        Icon icon = iconFactory.fromDrawable(iconDrawable);

        MarkerViewOptions markerViewOptions = new MarkerViewOptions();
        markerViewOptions.position(new LatLng(event.getAddress().getLocation().getLatitude(), event.getAddress().getLocation().getLongitude()))
                .icon(icon);

        Marker marker = map.addMarker(markerViewOptions);
        markers.put(event.getId(), marker);
        markersEventHashMap.put(marker, event);
    }

    private void removeOldMarkersIfNeed(List<EzlyEvent> events) {
        Set<String> markKeys = markers.keySet();
        ArrayList<String> eventIDToRemove = new ArrayList<>();
        for(String eventID : markKeys){
            boolean hasFoundEvent = false;
            for(EzlyEvent event : events){
                if(eventID.equals(event.getId())){
                    hasFoundEvent = true;
                    break;
                }
            }

            if(!hasFoundEvent){
                eventIDToRemove.add(eventID);
            }
        }

        for(String removeEventID : eventIDToRemove){
            Marker marker = markers.get(removeEventID);
            marker.remove();
            markersEventHashMap.remove(marker);
            markers.remove(removeEventID);
        }
    }

    public void showEventDetail(EzlyEvent event){
        EzlyEventDetailActivity.startActivity(getActivity(), event);
    }

    ////////////////////////////////recycle view interface////////////////////////////////
    @Override
    public void onEventClicked(EzlyEvent event) {
        showEventDetail(event);
    }

    @Override
    public void onScrollToBottom() {}

    @Override
    public void onSaveEventClicked(EzlyEvent event, boolean toSave) {}
}
