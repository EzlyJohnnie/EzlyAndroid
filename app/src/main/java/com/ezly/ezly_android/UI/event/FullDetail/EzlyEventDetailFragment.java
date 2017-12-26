package com.ezly.ezly_android.UI.event.FullDetail;

import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.CategoryIconHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyComment;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.ResultEvent;
import com.ezly.ezly_android.Presenter.CommentPresenter;
import com.ezly.ezly_android.Presenter.EventDetailPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.User.EzlyUserDetailFragment;
import com.ezly.ezly_android.UI.User.EzlyUserHostFragment;
import com.ezly.ezly_android.UI.ViewInterFace.CommentView;
import com.ezly.ezly_android.UI.ViewInterFace.EventDetailView;
import com.ezly.ezly_android.UI.Widget.CircleImageView;
import com.ezly.ezly_android.UI.event.EzlyFullImageFragment;
import com.ezly.ezly_android.UI.login.EzlyLoginHostFragment;
import com.ezly.ezly_android.Utils.TextUtils;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 29/10/16.
 */

public class EzlyEventDetailFragment extends EzlyBaseFragment implements EventDetailView, CommentView{
    private static final String KEY_EVENT = "key_event";
    private static final String KEY_COMMENTS = "key_comments";
    private static final String KEY_COMMENT_USERS = "key_commentUsers";

    private static final int MAX_USER_IN_PNL = 5;

    @Inject EventDetailPresenter detailPresenter;
    @Inject CommentPresenter commentPresenter;
    @Inject MemberHelper memberHelper;
    @Inject LocationHelper locationHelper;

    @BindView(R.id.event_mapview) MapView mapView;
    private MapboxMap map;

    @BindView(R.id.scroll_view) ScrollView scrollView;

//    @BindView(R.id.loading_view) View loadingView;

    @BindView(R.id.iv_category) ImageView ivCategory;
    @BindView(R.id.btn_save) View btnSave;
    @BindView(R.id.iv_save) ImageView ivSave;
    @BindView(R.id.iv_my_avatar) ImageView ivMyAvatar;
    @BindView(R.id.txt_comment) EditText txtComment;

    @BindView(R.id.btn_back) View btnBack;
    @BindView(R.id.iv_avatar) ImageView ivUserAvatar;
    @BindView(R.id.txt_name) TextView txtUserName;
    @BindView(R.id.txt_like) TextView txtLikeCount;

    @BindView(R.id.txt_title) TextView txtTitle;
    @BindView(R.id.txt_expire) TextView txtExpire;
    @BindView(R.id.txt_desc) TextView txtDesc;

    @BindView(R.id.image_pnl) LinearLayout imagePnl;
    @BindView(R.id.iv_cover_image) ImageView ivCoverImage;
    @BindView(R.id.txt_image_count) TextView txtImageCount;
    @BindView(R.id.address_pnl) View addressPnl;
    @BindView(R.id.txt_street) TextView txtStreet;
    @BindView(R.id.txt_near) TextView txtNear;
    @BindView(R.id.txt_distance) TextView txtDistance;

    @BindView(R.id.btn_image) View btnImage;
    @BindView(R.id.btn_location) View btnLocation;

    @BindView(R.id.txt_total_message_user) TextView txtTotalCommentUser;
    @BindView(R.id.message_user_pnl) LinearLayout commentUsersPnl;
    @BindView(R.id.comment_pnl) LinearLayout commentsPnl;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;


    private EzlyEvent event;
    private ArrayList<EzlyComment> comments = new ArrayList<>();
    private ArrayList<EzlyUser> commentUsers = new ArrayList<>();
    private boolean shouldScrollToBottom;
    private String replyToParentCommentID = "";
    private boolean isProcessingFavourite;
    private boolean isPostingComment;

    public static EzlyEventDetailFragment getInstance(EzlyEvent event){
        EzlyEventDetailFragment fragment = new EzlyEventDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_EVENT, event);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, root);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
        mapView.onSaveInstanceState(outState);
        outState.putParcelable(KEY_EVENT, event);
        outState.putParcelableArrayList(KEY_COMMENTS, comments);
        outState.putParcelableArrayList(KEY_COMMENT_USERS, commentUsers);
        super.onSaveInstanceState(outState);
    }


    private void init(Bundle savedInstanceState, View root) {
        if (savedInstanceState != null) {
            event = savedInstanceState.getParcelable(KEY_EVENT);
            comments = savedInstanceState.getParcelableArrayList(KEY_COMMENTS);
            commentUsers = savedInstanceState.getParcelableArrayList(KEY_COMMENT_USERS);
        }
        else if( getArguments() != null) {
            event = getArguments().getParcelable(KEY_EVENT);
        }
        initializeInjector();
        detailPresenter.setView(this);
        commentPresenter.setView(this);
        initMap(savedInstanceState);
        initView(root);

        if (savedInstanceState == null) {
            getEventDetail();
        }
        else {
            onEventPrepared(event);
            onCommentsPrepared(comments, commentUsers);
        }
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
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setZoomGesturesEnabled(false);
                map.getUiSettings().setScrollGesturesEnabled(false);
                map.getUiSettings().setCompassEnabled(false);
                map.setMyLocationEnabled(false);
                if(event != null){
                    setMapPnl();
                }
            }
        });
    }

    private void initView(View root) {
        showTabbar(false);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int height = screenWidth / 2;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(screenWidth, height);
        lp.addRule(RelativeLayout.BELOW, R.id.txt_desc);
        lp.setMargins(0, UIHelper.dip2px(getContext(), 20), 0, 0);
        imagePnl.setLayoutParams(lp);

        txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txtComment.getText().toString())){
                    resetCommentInputBox();
                }
            }
        });

        txtComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    if(!memberHelper.hasLogin()){
                        showLogin();
                    }
                    else if(!isPostingComment){
                        UIHelper.hideKeyBoard(v);
                        if(!TextUtils.isEmpty(txtComment.getText().toString())){
                            isPostingComment = true;
                            showLoadingView(true);
                            commentPresenter.postComments(EzlyEventDetailFragment.this.event, txtComment.getText().toString(), replyToParentCommentID);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume click
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP && TextUtils.isEmpty(replyToParentCommentID)){
                    resetCommentInputBox();
                }
                return false;
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEventDetail();
            }
        });

        if(memberHelper.hasLogin() && !TextUtils.isEmpty(memberHelper.getCurrentUser().getAvatarUrl())){
            Picasso.with(getContext())
                    .load(memberHelper.getCurrentUser().getAvatarUrl())
                    .fit()
                    .placeholder(R.drawable.placeholder_user)
                    .into(ivMyAvatar);
        }
    }

    private void resetCommentInputBox(){
        replyToParentCommentID = "";
        txtComment.setHint(getContext().getResources().getString(R.string.comment_hint));
    }

    public void getEventDetail() {
        onEventPrepared(event);
        detailPresenter.getEventDetail(event);
        commentPresenter.getComments(event);
    }

    private void reloadComment(){
        commentsPnl.removeAllViews();
        commentUsersPnl.removeAllViews();
        commentPresenter.getComments(event);
    }

    private void updateView() {
        if(map != null){
            setMapPnl();
        }
        ivCategory.setImageDrawable(getContext().getResources().getDrawable(CategoryIconHelper.getCategoryIcon(event.getCategory().getCode())));
        updateUserPnl(event.getUser());
        updateContentPnl();
        updateImagePnl();
        updateFavouriteBtn();
    }

    private void updateFavouriteBtn() {
        boolean isSaved = !event.canBeFavourited();
        if(memberHelper.hasLogin() && memberHelper.getCurrentUser().getId().equals(event.getUser().getId())){
            isSaved = true;
        }

        ivSave.setImageDrawable(getContext().getResources().getDrawable(isSaved ?  R.drawable.saved : R.drawable.unsaved));
    }

    private void updateUserPnl(EzlyUser user) {
        if(user == null){
            return;
        }

        if(!TextUtils.isEmpty(user.getAvatarUrl())){
            Picasso.with(getContext())
                    .load(user.getAvatarUrl())
                    .fit()
                    .placeholder(R.drawable.placeholder_user)
                    .into(ivUserAvatar);
        }

        if(!TextUtils.isEmpty(user.getDisplayName())){
            txtUserName.setText(user.getDisplayName());
        }

        String beLikedStr = String.format(getContext().getResources().getString(R.string.have_like), user.getNumOfLikes());
        txtLikeCount.setText(beLikedStr);

    }

    private void updateContentPnl() {
        txtTitle.setText(event.getTitle());
        setExpireTimeLabel();
        if(!TextUtils.isEmpty(event.getDescription())){
            txtDesc.setText(event.getDescription());
        }
    }

    private void setExpireTimeLabel() {
        long diffHours = event.getHourDiff(getContext());
        int day = (int) diffHours / 24;
        int hours = (int) diffHours % 24;
        String expireStr = "";

        if(day > 0 && hours > 0){
            String formatStr = getContext().getResources().getString(R.string.expire_in_full);
            expireStr = String.format(formatStr, day, hours);
        }
        else if(hours > 0){
            String formatStr = getContext().getResources().getString(R.string.expire_in_hour_only);
            expireStr = String.format(formatStr, hours);
        }
        txtExpire.setText(expireStr);
    }

    private void updateImagePnl() {
        if(event.getImages() != null && event.getImages().size() > 0){
            LinearLayout.LayoutParams imageLP = (LinearLayout.LayoutParams)btnImage.getLayoutParams();
            imageLP.weight = 1;
            btnImage.setLayoutParams(imageLP);

            LinearLayout.LayoutParams locationLP = (LinearLayout.LayoutParams) btnLocation.getLayoutParams();
            locationLP.weight = 1;
            btnLocation.setLayoutParams(locationLP);


            Picasso.with(getContext())
                    .load(event.getImages().get(0).getUrl())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .into(ivCoverImage);
            txtImageCount.setText(String.format(getContext().getResources().getString(R.string.image_count), event.getImages().size()));
            txtImageCount.setVisibility(View.VISIBLE);
        }
        else{
            LinearLayout.LayoutParams imageLP = (LinearLayout.LayoutParams)btnImage.getLayoutParams();
            imageLP.weight = 0;
            btnImage.setLayoutParams(imageLP);

            LinearLayout.LayoutParams locationLP = (LinearLayout.LayoutParams) btnLocation.getLayoutParams();
            locationLP.weight = 2;
            btnLocation.setLayoutParams(locationLP);

            txtImageCount.setVisibility(View.GONE);
        }
    }

    private void setMapPnl(){
        if(event.getAddress() != null && event.getAddress().getLocation().isValidLocation() && map != null) {
            map.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(event.getAddress().getLocation().getLatitude(), event.getAddress().getLocation().getLongitude()))
                    .zoom(16)
                    .build());

            txtDistance.setText(String.format(getContext().getResources().getString(R.string.away_from_string), event.getDistanceDiff(getContext(), locationHelper.getLastKnownLocation())));
        }else{
            txtDistance.setVisibility(View.GONE);
        }

        if(event.getAddress() != null && !TextUtils.isEmpty(event.getAddress().getDisplayStreet())){
            txtStreet.setText(event.getAddress().getDisplayStreet());
        }
        else{
            txtStreet.setVisibility(View.GONE);
            txtNear.setVisibility(View.GONE);
        }
    }

    private void showLoadingView(boolean isShow) {
//        loadingView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if(isShow){
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
        }
        else{
            refreshLayout.setRefreshing(false);
        }
    }

    private void showLogin(){
        UIHelper.hideKeyBoard(getView());
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).presentFragment(EzlyLoginHostFragment.getInstance());
        }
    }

    //onClick
    @OnClick(R.id.btn_save)
    public void onFavouriteClicked(){
        if(!memberHelper.hasLogin()){
            showLogin();
            return;
        }

        if(event.getUser().getId().equals(memberHelper.getCurrentUser().getId())){
            SingleToast.makeText(getContext(), getContext().getResources().getString(R.string.cannot_add_self_favourite), Toast.LENGTH_SHORT).show();
        }
        else if(!isProcessingFavourite){
            isProcessingFavourite = true;
            if(event.canBeFavourited()){
                detailPresenter.addFavourite(event);
            }
            else{
                detailPresenter.removeFavourite(event);
            }
        }
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        UIHelper.hideKeyBoard(getView());
        boolean hasHandle = false;
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            hasHandle = ((EzlyBaseHostFragment)getParentFragment()).pop();
        }
        else if(getActivity() instanceof MainActivity){
            hasHandle = ((MainActivity)getActivity()).pop();
        }

        if(!hasHandle){
            getActivity().finish();
        }
    }

    @OnClick(R.id.btn_image)
    public void onImageClicked(){
        if(event.getImages() != null && event.getImages().size() > 0
                && getParentFragment() instanceof EzlyEventDetailFragmentHost)
        {
            ((EzlyEventDetailFragmentHost)getParentFragment()).presentFragment(EzlyFullImageFragment.getInstance(event.getImages()));
        }
    }

    @OnClick(R.id.iv_avatar)
    public void onUserClicked(){
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).pushReplace(EzlyUserDetailFragment.getInstance(event.getUser().getId()));
        }
    }

    private void initCommentsPnl(List<EzlyComment> comments) {
        commentsPnl.removeAllViews();
        for(final EzlyComment comment : comments){
            View commentView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_comment, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            commentView.setLayoutParams(lp);

            ImageView ivAvatar = (ImageView) commentView.findViewById(R.id.iv_avatar);
            TextView txtUsername = (TextView) commentView.findViewById(R.id.txt_user);
            TextView txtContent = (TextView) commentView.findViewById(R.id.txt_content);
            TextView txtTime = (TextView) commentView.findViewById(R.id.txt_time);

            final EzlyUser user = commentPresenter.getUserForComment(comment);
            if(user != null){
                if(!android.text.TextUtils.isEmpty(user.getAvatarUrl())){
                    Picasso.with(getContext())
                            .load(user.getAvatarUrl())
                            .fit()
                            .placeholder(R.drawable.placeholder_user)
                            .into(ivAvatar);
                }
                else{
                    Picasso.with(getContext())
                            .load(R.drawable.placeholder_user)
                            .fit()
                            .into(ivAvatar);
                }

                if(!TextUtils.isEmpty(user.getDisplayName())) {
                    txtUsername.setText(user.getDisplayName());
                }

            }

            if(!TextUtils.isEmpty(comment.getText())) {
                txtContent.setText(comment.getText());
            }

            if(!TextUtils.isEmpty(comment.getCreationTime())) {
                txtTime.setText(comment.getFormattedCreationTime());
            }


            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity() instanceof MainActivity){
                        ((MainActivity)getActivity()).push(EzlyUserHostFragment.getUserDetailInstance(user.getId()));
                    }
                }
            });

            commentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = user == null ? "" : user.getDisplayName();
                    prepareToReply(comment, username);
                }


            });

            LinearLayout childCommentsPnl = (LinearLayout) commentView.findViewById(R.id.child_comments_pnl);
            if(comment.getChildComments() != null && comment.getChildComments().size() > 0){
                childCommentsPnl.setVisibility(View.VISIBLE);
                for(EzlyComment chileComment : comment.getChildComments()){
                    View childCommentContentView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_child_comment, null);
                    TextView txtChildUser = (TextView)childCommentContentView.findViewById(R.id.txt_user);
                    TextView txtChildContent = (TextView)childCommentContentView.findViewById(R.id.txt_content);

                    if(memberHelper.hasLogin() && chileComment.getUserId().equals(memberHelper.getCurrentUser().getId())){
                        txtChildUser.setText(getContext().getResources().getString(R.string.me) + ":");
                        txtChildUser.setTypeface(null, Typeface.BOLD);
                    }
                    else{
                        txtChildUser.setText(commentPresenter.getUserForComment(chileComment).getDisplayName() + ":");
                        txtChildUser.setTextColor(getContext().getResources().getColor(R.color.btn_post));
                    }

                    txtChildContent.setText(chileComment.getText());
                    childCommentsPnl.addView(childCommentContentView);
                }
            }

            commentsPnl.addView(commentView);
        }
    }

    private void prepareToReply(EzlyComment comment, String username) {
        txtComment.requestFocus();
        txtComment.setHint(String.format(getContext().getResources().getString(R.string.reply_to), username));
        UIHelper.showKeyboard(getContext());
        replyToParentCommentID =comment.getId();
    }

    private void initCommentsUserPnl(List<EzlyUser> users) {
        int addedCount = 0;
        int avatarSize = UIHelper.dip2px(getContext(), 40);
        int margin = UIHelper.dip2px(getContext(), 10);
        if(users != null){
            txtTotalCommentUser.setText(String.format(getContext().getResources().getString(R.string.total_asked_user), users.size()));
        }

        commentUsersPnl.removeAllViews();
        for(final EzlyUser user : users){
            CircleImageView ivAvatar = new CircleImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(avatarSize, avatarSize);
            lp.setMargins(margin, 0, margin, 0);
            ivAvatar.setLayoutParams(lp);

            if(!android.text.TextUtils.isEmpty(user.getAvatarUrl())){
                Picasso.with(getContext())
                        .load(user.getAvatarUrl())
                        .fit()
                        .placeholder(R.drawable.placeholder_user)
                        .into(ivAvatar);
            }
            else{
                Picasso.with(getContext())
                        .load(R.drawable.placeholder_user)
                        .fit()
                        .into(ivAvatar);
            }

            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity() instanceof MainActivity){
                        ((MainActivity)getActivity()).push(EzlyUserHostFragment.getUserDetailInstance(user.getId()));
                    }
                }
            });

            commentUsersPnl.addView(ivAvatar);

            addedCount++;
            if(addedCount >= MAX_USER_IN_PNL){
                return;
            }
        }
    }


    //view interface

    @Override
    public void onCommentsPrepared(List<EzlyComment> comments, List<EzlyUser> users){
        refreshLayout.setRefreshing(false);
        if(!isAdded()){
            return;
        }
        this.comments.clear();
        this.comments.addAll(comments);

        this.commentUsers.clear();
        this.commentUsers.addAll(users);

        initCommentsUserPnl(users);
        initCommentsPnl(comments);
        if(shouldScrollToBottom){
            shouldScrollToBottom = false;
            scrollView.fullScroll(View.FOCUS_DOWN);
        }
    }

    @Override
    public void onCommentsLoadFailed(String errorMsg) {
        refreshLayout.setRefreshing(false);
        if(!TextUtils.isEmpty(errorMsg)){
            SingleToast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostCommentComplete(boolean isSuccess) {
        isPostingComment = false;
        showLoadingView(false);
        if(isSuccess){
            reloadComment();
            txtComment.setText("");
        }
    }

    @Override
    public void onEventPrepared(EzlyEvent event) {
        if(!isAdded()){
            return;
        }
        showLoadingView(false);
        this.event = event;
        updateView();
    }

    @Override
    public void onAddFavourite(boolean isSuccess) {
        isProcessingFavourite = false;
        if(isSuccess){
            event.setCanFavourite(false);
            updateFavouriteBtn();
        }
        else{
            UIHelper.showConnectionError(getContext());
        }
    }

    @Override
    public void onRemoveFavourite(boolean isSuccess) {
        isProcessingFavourite = false;
        if(isSuccess){
            event.setCanFavourite(true);
            updateFavouriteBtn();
        }
        else{
            UIHelper.showConnectionError(getContext());
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
                    .into(ivMyAvatar);
        }
    }
}
