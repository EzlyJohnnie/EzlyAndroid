package com.ezly.ezly_android.UI.search;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Presenter.SearchPresenter;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseFragment;
import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseHostFragment;
import com.ezly.ezly_android.UI.MainActivity;
import com.ezly.ezly_android.UI.ViewInterFace.EzlySearchView;
import com.ezly.ezly_android.Utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Johnnie on 20/10/16.
 */
public class EzlySearchFragment extends EzlyBaseFragment implements EzlySearchView, CategoryAdapter.OnIemClickedListener {


    @Inject SearchPresenter presenter;
    @Inject EzlySearchParam searchParam;

    @BindView(R.id.btn_skip) Button btnSkip;
    @BindView(R.id.txt_search) EditText txtSearch;
    @BindView(R.id.category_list) RecyclerView categoryList;
    @BindView(R.id.txt_need) TextView txtNeed;
    @BindView(R.id.txt_provide) TextView txtProvide;

    private CategoryAdapter adapter;
    private OnSearchListener onSearchListener;

    private int searchMode;
    private int viewType;
    private boolean hasShowCategoryList;

    public static EzlySearchFragment newInstance(int searchViewType){
        EzlySearchFragment fragment = new EzlySearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EzlySearchHostFragment.KEY_SEARCH_VIEW_TYPE, searchViewType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, root);
        viewType = getArguments().getInt(EzlySearchHostFragment.KEY_SEARCH_VIEW_TYPE);
        init(savedInstanceState, root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupView();
    }

    @Override
    public void onDestroy(){
        onSearchListener = null;
        super.onDestroy();
    }

    private void init(Bundle savedInstanceState, View root) {
        initializeInjector();
        presenter.setView(this);
        initView(root);
    }

    private void initView(View root) {
        initSearchView();
        initCategoryList();
        initSpinner();

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consume onClick event
            }
        });
    }

    private void initSpinner() {
        searchMode = searchParam.getSearchMode();
        txtNeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchParam.setSearchMode(EzlySearchParam.SEARCH_MODE_SERVICE);
                animateCategoryListIn();
                txtNeed.setTextColor(getContext().getResources().getColor(R.color.btn_post));
                txtProvide.setTextColor(getContext().getResources().getColor(R.color.divider));
            }
        });

        txtProvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchParam.setSearchMode(EzlySearchParam.SEARCH_MODE_JOB);
                animateCategoryListIn();
                txtProvide.setTextColor(getContext().getResources().getColor(R.color.btn_post));
                txtNeed.setTextColor(getContext().getResources().getColor(R.color.divider));
            }
        });

        if(!TextUtils.isEmpty(searchParam.getSearchStr())){
            txtSearch.setText(searchParam.getSearchStr());
        }
    }

    private void initCategoryList() {
        categoryList.setAlpha(0);
        GridLayoutManager lLayout = new GridLayoutManager(getContext(), 3);
        categoryList.setHasFixedSize(true);
        categoryList.setLayoutManager(lLayout);

        adapter = new CategoryAdapter(null);
        adapter.setListener(this);
        categoryList.setAdapter(adapter);
    }


    private void setupView(){
        switch(viewType){
            case EzlySearchHostFragment.SEARCH_VIEW_TYPE_WELCOME:
                btnSkip.setText(getContext().getResources().getString(R.string.skip));
                break;
            case EzlySearchHostFragment.SEARCH_VIEW_TYPE_NORMAL:
                btnSkip.setText(getContext().getResources().getString(R.string.close));
                break;
        }

        presenter.getTopCategory(true);
    }

    protected void initializeInjector(){
        getActivityComponent().inject(this);
    }

    private void initSearchView() {
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = txtSearch.getText().toString();
                    if(searchText != null && searchText.length() >= 2){
                        searchParam.setSearchStr(searchText);
                        searchParam.setSelectedCategories(new ArrayList<EzlyCategory>());
                        performSearch();
                    }
                    else{
                        UIHelper.displayConfirmDialog(getContext(), getContext().getResources().getString(R.string.search_text_length_error));
                    }
                    return true;
                }
                return false;
            }

        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    btnSkip.setText(getContext().getResources().getString(R.string.clear));
                }
                else{
                    switch(viewType){
                        case EzlySearchHostFragment.SEARCH_VIEW_TYPE_WELCOME:
                            btnSkip.setText(getContext().getResources().getString(R.string.skip));
                            break;
                        case EzlySearchHostFragment.SEARCH_VIEW_TYPE_NORMAL:
                            btnSkip.setText(getContext().getResources().getString(R.string.close));
                            break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateCategoryList(List<EzlyCategory> categories) {
        adapter.setCategories(categories);
        if(searchMode != EzlySearchParam.SEARCH_MODE_USER){
            animateCategoryListIn();
        }
    }

    private void animateCategoryListIn(){
        if(!hasShowCategoryList){
            hasShowCategoryList = true;
            categoryList.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0, 0, UIHelper.dip2px(getContext(), 5), 0);
            animation.setDuration(300);
            categoryList.startAnimation(animation);
            categoryList.animate()
                    .alpha(1)
                    .start();
        }
    }

    private void animateCategoryListOut(){
        if(hasShowCategoryList){
            hasShowCategoryList = false;
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, UIHelper.dip2px(getContext(), 5));
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    categoryList.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            categoryList.startAnimation(animation);
            categoryList.animate()
                    .alpha(0)
                    .start();
        }
    }

    @Override
    public void onCategoryClicked(EzlyCategory category) {
        ArrayList<EzlyCategory> categories = new ArrayList<>();
        if(category.getCode().equals("all")){
            searchParam.setSelectedCategories(categories);
        }
        else{
            categories.add(category);
            searchParam.setSelectedCategories(categories);
        }

        searchParam.setSearchStr(txtSearch.getText().toString());
        performSearch();
    }

    private void performSearch() {
        UIHelper.hideKeyBoard(getView());

        searchParam.setSearchMode(searchMode);
        if(onSearchListener != null){
            onSearchListener.shouldReload();
        }

        switch(viewType){
            case EzlySearchHostFragment.SEARCH_VIEW_TYPE_WELCOME:
                MainActivity.startActivity(getActivity());
                getActivity().finish();
                break;
            case EzlySearchHostFragment.SEARCH_VIEW_TYPE_NORMAL:
                dismiss();
                break;
        }
    }

    @OnClick(R.id.btn_skip)
    public void onNeedClick(){
        if(!TextUtils.isEmpty(txtSearch.getText().toString())){
            txtSearch.setText("");
            switch(viewType){
                case EzlySearchHostFragment.SEARCH_VIEW_TYPE_WELCOME:
                    btnSkip.setText(getContext().getResources().getString(R.string.skip));
                    break;
                case EzlySearchHostFragment.SEARCH_VIEW_TYPE_NORMAL:
                    btnSkip.setText(getContext().getResources().getString(R.string.close));
                    break;
            }
        }
        else{
            switch(viewType){
                case EzlySearchHostFragment.SEARCH_VIEW_TYPE_WELCOME:
                    performSearch();
                    break;
                case EzlySearchHostFragment.SEARCH_VIEW_TYPE_NORMAL:
                    dismiss();
                    break;
            }
        }
    }

    private void dismiss(){
        UIHelper.hideKeyBoard(getView());
        if(getParentFragment() instanceof EzlyBaseHostFragment){
            ((EzlyBaseHostFragment)getParentFragment()).dismissSelf();
        }
    }

    @Override
    public void onTopCategoryPrepared(List<EzlyCategory> categories) {
        updateCategoryList(categories);
    }

    public interface OnSearchListener{
        void shouldReload();
    }

}
