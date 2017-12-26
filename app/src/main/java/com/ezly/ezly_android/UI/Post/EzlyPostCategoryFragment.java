package com.ezly.ezly_android.UI.Post;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.Presenter.SearchPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.ViewInterFace.EzlySearchView;
import com.ezly.ezly_android.UI.search.CategoryAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 20/11/16.
 */

public class EzlyPostCategoryFragment extends EzlyBaseFragment implements EzlySearchView, CategoryAdapter.OnIemClickedListener{
    private static final int POST_TYPE_JOB = 0;
    private static final int POST_TYPE_SERVICE = 1;

    @Inject SearchPresenter presenter;
    @Inject MemberHelper memberHelper;

    @BindView(R.id.txt_need)      TextView txtNeed;
    @BindView(R.id.txt_provide)   TextView txtProvide;
    @BindView(R.id.category_list) RecyclerView categoryList;

    private int postType;
    private EzlyEvent selectedCategory;

    private CategoryAdapter adapter;

    public static EzlyPostCategoryFragment getInstance(){
        EzlyPostCategoryFragment fragment = new EzlyPostCategoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_post_category, container, false);
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
    public void onDestroy(){
        super.onDestroy();
        memberHelper.reset();
    }

    private void init(Bundle savedInstanceState, View root) {
        initializeInjector();
        presenter.setView(this);
        initView(root);
        presenter.getTopCategory(false);
    }


    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initView(View root) {
        showTabbar(false);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onclick event
            }
        });
        initCategoryList();
    }

    private void initCategoryList() {
        GridLayoutManager lLayout = new GridLayoutManager(getContext(), 3);
        categoryList.setHasFixedSize(true);
        categoryList.setLayoutManager(lLayout);

        adapter = new CategoryAdapter(null);
        adapter.setListener(this);
        categoryList.setAdapter(adapter);
    }

    private void updateCategoryList(List<EzlyCategory> categories) {
        adapter.setCategories(categories);
    }

    @OnClick(R.id.txt_need)
    public void onNeedClicked(){
        postType = POST_TYPE_JOB;
        txtNeed.setTextColor(getContext().getResources().getColor(R.color.btn_post));
        txtProvide.setTextColor(getContext().getResources().getColor(R.color.divider));
    }

    @OnClick(R.id.txt_provide)
    public void onProvideClicked(){
        postType = POST_TYPE_SERVICE;
        txtNeed.setTextColor(getContext().getResources().getColor(R.color.divider));
        txtProvide.setTextColor(getContext().getResources().getColor(R.color.btn_post));
    }

    @OnClick(R.id.btn_back)
    public void onBackClicked(){
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).pop();
        }
    }


    @Override
    public void onTopCategoryPrepared(List<EzlyCategory> categories) {
        updateCategoryList(categories);
    }

    @Override
    public void onCategoryClicked(EzlyCategory category) {
        switch (postType){
            case POST_TYPE_JOB:
                selectedCategory = new EzlyJob();
                break;
            case POST_TYPE_SERVICE:
                selectedCategory = new EzlyService();
                break;
        }
        selectedCategory.setCategory(category);

        if(getParentFragment() instanceof  EzlyPostHostFragment){
            ((EzlyPostHostFragment)getParentFragment()).push(EzlyNewPostFragment.getInstance(selectedCategory, EzlyNewPostFragment.VIEW_TYPE_POST_EVENT));
        }
    }
}
