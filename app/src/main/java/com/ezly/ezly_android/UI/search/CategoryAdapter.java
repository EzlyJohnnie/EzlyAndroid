package com.ezly.ezly_android.UI.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.UIHelper.CategoryIconHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.Data.EzlyCategory;
import com.ezly.ezly_android.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 8/11/16.
 */

public class CategoryAdapter extends RecyclerView.Adapter{
    private List<EzlyCategory> categories;
    private OnIemClickedListener mListener;

    public CategoryAdapter(List<EzlyCategory> categories) {
        this.categories = categories;
    }

    public void setListener(OnIemClickedListener mListener) {
        this.mListener = mListener;
    }

    public void setCategories(List<EzlyCategory> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
        CategoryViewHolder viewHolder = new CategoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder vh = (CategoryViewHolder)holder;
        final EzlyCategory category = categories.get(position);
        Picasso.with(vh.ivCategory.getContext())
                .load(CategoryIconHelper.getCategoryIcon(category.getCode()))
                .fit()
                .into(vh.ivCategory);
        vh.txtTitle.setText(category.getName());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onCategoryClicked(category);
                }
            }
        });
        animateCategoryIn(vh.itemView);
    }

    private void animateCategoryIn(View view){
        TranslateAnimation animation = new TranslateAnimation(0, 0, UIHelper.dip2px(view.getContext(), 5), 0);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.animate()
                .alpha(1)
                .start();
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_category) ImageView ivCategory;
        @BindView(R.id.txt_title) public TextView txtTitle;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnIemClickedListener{
        void onCategoryClicked(EzlyCategory category);
    }
}
