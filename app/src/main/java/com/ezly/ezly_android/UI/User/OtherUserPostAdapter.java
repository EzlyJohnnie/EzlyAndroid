package com.ezly.ezly_android.UI.User;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.UIHelper.CategoryIconHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.Utils.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 19/01/17.
 */

public class OtherUserPostAdapter extends RecyclerView.Adapter {
    public static final int VIEW_TYPE_EVENT = 0;
    public static final int VIEW_TYPE_EMPTY = 1;
    public static final int VIEW_TYPE_PROFILE = 2;

    private EzlyUser user;
    private List<EzlyEvent> events;

    private OtherUserItemListener listener;

    public OtherUserPostAdapter(EzlyUser user, List<EzlyEvent> events) {
        this.events = events;
        this.user = user;
    }

    public void setEvents(List<EzlyEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public void setListener(OtherUserItemListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if(user != null && position == 0){
            type = VIEW_TYPE_PROFILE;
        }
        else{
            type = events != null && events.size() > 0 ?
                    VIEW_TYPE_EVENT : VIEW_TYPE_EMPTY;
        }

        return type;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View view;
        switch (viewType) {
            case VIEW_TYPE_EVENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favourite, parent, false);
                vh = new OtherUserEventItem(view);
                break;
            case VIEW_TYPE_EMPTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event_empty, parent, false);
                vh = new OtherUserEventEmptyItem(view);
                break;
            case VIEW_TYPE_PROFILE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile_header, parent, false);
                vh = new OtherUserProfileItem(view);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_EVENT) {
            final OtherUserEventItem viewHolder = (OtherUserEventItem) holder;
            setupEvent(viewHolder, position);
        }
        else if (getItemViewType(position) == VIEW_TYPE_EMPTY){
            final OtherUserEventEmptyItem viewHolder = (OtherUserEventEmptyItem) holder;
            String emptyStr = viewHolder.txtEmpty.getContext().getResources().getString(R.string.no_post_history);
            viewHolder.txtEmpty.setText(emptyStr);
        }
        else if (getItemViewType(position) == VIEW_TYPE_PROFILE){
            final OtherUserProfileItem viewHolder = (OtherUserProfileItem) holder;
            setupUser(viewHolder, user);
        }
    }

    private void setupUser(final OtherUserProfileItem viewHolder, final EzlyUser user) {
        final Context context = viewHolder.itemView.getContext();
        viewHolder.txtName.setText(user.getDisplayName());
        if (!TextUtils.isEmpty(user.getAvatarUrl())) {
            Picasso.with(context)
                    .load(user.getAvatarUrl())
                    .fit()
                    .placeholder(R.drawable.placeholder_user)
                    .into(viewHolder.ivAvatar);
        }

        String beLikedStr = String.format(context.getResources().getString(R.string.have_like), user.getNumOfLikes());
        viewHolder.txtLike.setText(beLikedStr);


        viewHolder.btnLike.setBackgroundDrawable(context.getResources().getDrawable(user.isCanLike() ? R.drawable.like_btn_bg : R.drawable.disabled_btn_bg));
        viewHolder.btnLike.setText(context.getResources().getString(user.isCanLike() ? R.string.like_ta : R.string.dislike));
        viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onLikeClicked();
                    viewHolder.btnLike.setBackgroundDrawable(context.getResources().getDrawable(user.isCanLike() ? R.drawable.like_btn_bg : R.drawable.disabled_btn_bg));

                }
            }
        });
    }

    private void setupEvent(final OtherUserEventItem holder, int position) {
        int realPosition = position - (user == null ? 0 : 1);
        final EzlyEvent event = events.get(realPosition);

        Context context = holder.itemView.getContext();

        Picasso.with(context)
                .load(CategoryIconHelper.getCategoryIcon(event.getCategory().getCode()))
                .fit()
                .into(holder.ivCategory);

        holder.txtTitle.setText(event.getTitle());
        holder.txtDesc.setText(event.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onEventClicked(event);
                }
            }
        });

        holder.invisibleCover.setVisibility(event.isActive() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (events != null) {
            count = events.size() == 0 ? 1 : events.size();
        }

        int userCount = user == null ? 0 : 1;
        return count + userCount;
    }

    public void setUser(EzlyUser user) {
        this.user = user;
        notifyDataSetChanged();
    }

    class OtherUserEventItem extends RecyclerView.ViewHolder {

        @BindView(R.id.content_view) View contentView;
        @BindView(R.id.btn_visible) View btnPreview;
        @BindView(R.id.btn_edit) View btnEdit;
        @BindView(R.id.btn_delete) View btnDelete;
        @BindView(R.id.btn_unfavourite) View btnUnfavourite;

        @BindView(R.id.iv_category) ImageView ivCategory;
        @BindView(R.id.txt_title) TextView txtTitle;
        @BindView(R.id.txt_desc) TextView txtDesc;
        @BindView(R.id.invisible_cover) View invisibleCover;

        public OtherUserEventItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            btnUnfavourite.setVisibility(View.GONE);
        }
    }


    class OtherUserEventEmptyItem extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_empty) TextView txtEmpty;

        public OtherUserEventEmptyItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class OtherUserProfileItem extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_my_avatar) ImageView ivAvatar;
        @BindView(R.id.txt_name) TextView txtName;
        @BindView(R.id.txt_like) TextView txtLike;
        @BindView(R.id.btn_like) TextView btnLike;

        public OtherUserProfileItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface OtherUserItemListener{
        void onLikeClicked();
        void onEventClicked(EzlyEvent event);
    }
}