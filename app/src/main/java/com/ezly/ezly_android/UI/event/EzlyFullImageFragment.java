package com.ezly.ezly_android.UI.event;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ezly.ezly_android.Data.EzlyImage;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.Widget.ZoomableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 1/09/15.
 */
public class EzlyFullImageFragment extends EzlyBaseFragment {
    private final static String KEY_PHOTOS_URLS = "key_photosURLs";
    private final static String KEY_PHOTOS_PATHS = "key_photosPaths";
    private final static String KEY_LANDING_INDEX = "key_landingIndex";

    @BindView(R.id.btn_delete) View btnDelete;
    @BindView(R.id.tool_bar) RelativeLayout toolBar;
    @BindView(R.id.btn_back) LinearLayout btn_back;
    @BindView(R.id.txt_index) TextView txt_index;
    @BindView(R.id.view_pager)ViewPager viewPager;

    private PagerAdapter adapter;

    private ArrayList<EzlyImage> photosUrls;//String: remote URL
    private ArrayList<EzlyImage> ezlyImages;//String: local URL
    private boolean toolBarFlag = true;
    private OnImageDeletedListener mListener;
    private int currentPagePosition;
    private int landingIndex;

    public static EzlyFullImageFragment getInstance(ArrayList<EzlyImage> photos){
        EzlyFullImageFragment fragment = new EzlyFullImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_PHOTOS_URLS, photos);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static EzlyFullImageFragment getInstanceForLocalPhotos(ArrayList<EzlyImage> photos, int landingIndex){
        EzlyFullImageFragment fragment = new EzlyFullImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_PHOTOS_PATHS, photos);
        bundle.putInt(KEY_LANDING_INDEX, landingIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(OnImageDeletedListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_full_image, container, false);
        ButterKnife.bind(this, root);
        photosUrls = getArguments().getParcelableArrayList(KEY_PHOTOS_URLS);
        ezlyImages = getArguments().getParcelableArrayList(KEY_PHOTOS_PATHS);
        landingIndex = getArguments().getInt(KEY_LANDING_INDEX);
        init(root);
        return root;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mListener = null;
    }

    /*
     * initialize the ui
     */
    private void init(View root){
        if(photosUrls != null){
            btnDelete.setVisibility(View.GONE);
        }
        else if(ezlyImages != null){
            btnDelete.setVisibility(View.VISIBLE);
        }

        initViewPager();
        addListener(root);
    }

    private void initViewPager(){
        adapter = new PagerAdapter(){
            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                int count = 0;
                if(photosUrls != null){
                    count = photosUrls.size();
                }
                else if(ezlyImages != null){
                    count = ezlyImages.size();
                }
                txt_index.setText((position + 1)  + " / " + count);
                currentPagePosition = position;
                resetImageScale(object);
            }

            @Override
            public int getCount() {
                int count = 0;
                if(photosUrls != null){
                    count = photosUrls.size();
                }
                else if(ezlyImages != null){
                    count = ezlyImages.size();
                }
                return count;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0==arg1;
            }

            @Override
            public void destroyItem(ViewGroup container,int position,Object o){
                container.removeView((View) o);
            }


            @Override
            public Object instantiateItem(ViewGroup container,int position){
                ZoomableImageView view = new ZoomableImageView(getContext());
                view.setDrawingCacheEnabled(true);
                if(photosUrls != null){
                    if(!TextUtils.isEmpty(photosUrls.get(position).getUrl())){
                        Picasso.with(getContext())
                                .load(photosUrls.get(position).getUrl())
                                .fit()
                                .centerInside()
                                .into(view);
                    }
                }
                else if(ezlyImages != null){
                    if(!TextUtils.isEmpty(ezlyImages.get(position).getPath())){
                        Bitmap myBitmap = BitmapFactory.decodeFile(ezlyImages.get(position).getPath());
                        view.setImageBitmap(myBitmap);
                    }
                    else if(!TextUtils.isEmpty(ezlyImages.get(position).getUrl())){
                        Picasso.with(getContext())
                                .load((ezlyImages.get(position).getUrl()))
                                .fit()
                                .centerInside()
                                .into(view);
                    }

                }


                if(view != null){
                    view.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            if(toolBarFlag){
                                //hide
                                toolBar.setVisibility(View.INVISIBLE);
                                toolBarFlag = false;
                            }
                            else{
                                //display
                                toolBar.setVisibility(View.VISIBLE);
                                toolBarFlag = true;
                            }
                        }
                    });
                }

                container.addView(view);
                return view;
            }

            public void resetImageScale(Object view){
                if(view instanceof ZoomableImageView){
                    ((ZoomableImageView)view).resetImageScale();
                }
            }
        };

        viewPager.setAdapter(adapter);
        if(ezlyImages != null && landingIndex > ezlyImages.size()){
            landingIndex = ezlyImages.size() - 1;
        }
        else if(photosUrls != null && landingIndex > photosUrls.size()){
            landingIndex = photosUrls.size() - 1;
        }
        viewPager.setCurrentItem(landingIndex, false);
    }


    private void addListener(View root) {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(getParentFragment() != null && getParentFragment() instanceof EzlyBaseHostFragment){
                    getParentFragment().getChildFragmentManager().popBackStackImmediate();
                }
                else if(getActivity() instanceof EzlyBaseActivity){
                    ((EzlyBaseActivity) getActivity()).pop();
                }

            }
        });
    }

    @OnClick(R.id.btn_delete)
    public void onDeleteClicked(){
        int count = 0;
        if(photosUrls != null){
            photosUrls.remove(currentPagePosition);
            count = photosUrls.size();
        }
        else if(ezlyImages != null){
            ezlyImages.remove(currentPagePosition);
            count = ezlyImages.size();
        }


        if(count <= 0){
            if(getParentFragment() instanceof EzlyBaseHostFragment){
                ((EzlyBaseHostFragment)getParentFragment()).pop();
            }
            else if(getActivity() instanceof EzlyBaseActivity){
                ((EzlyBaseActivity)getActivity()).pop();
            }
        }

        initViewPager();

        if(mListener != null){
            mListener.onImageDeleted(currentPagePosition);
        }
    }

    public interface OnImageDeletedListener{
        void onImageDeleted(int position);
    }
}
