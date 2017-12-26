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
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.Utils.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 17/11/16.
 */

public class UserEventListAdapter extends RecyclerView.Adapter {
    public static final int ADAPTER_TYPE_FAVOURITE = 0;
    public static final int ADAPTER_TYPE_POST_HISTORY = 1;

    public static final int VIEW_TYPE_EVENT = 0;
    public static final int VIEW_TYPE_EMPTY = 1;

    private int adapterType;
    private List<EzlyEvent> events;
    private UserEventClickedListener listener;

    public UserEventListAdapter(List<EzlyEvent> events, int adapterType) {
        this.events = events;
        this.adapterType = adapterType;
    }

    public void setListener(UserEventClickedListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<EzlyEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return events != null && events.size() > 0 ?
                VIEW_TYPE_EVENT : VIEW_TYPE_EMPTY;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View view;
        switch (viewType){
            case VIEW_TYPE_EVENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favourite, parent, false);
                vh = new UserEventItem(view);
                break;
            case VIEW_TYPE_EMPTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event_empty, parent, false);
                vh = new UserEventEmptyItem(view);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(events != null && events.size() > 0){
            setupEvent((UserEventItem) holder, position);
        }
        else{
            final UserEventEmptyItem viewHolder = (UserEventEmptyItem) holder;
            String emptyStr = "";
            switch (adapterType){
                case ADAPTER_TYPE_FAVOURITE:
                    emptyStr = viewHolder.txtEmpty.getContext().getResources().getString(R.string.no_favourite);
                    break;
                case ADAPTER_TYPE_POST_HISTORY:
                    emptyStr = viewHolder.txtEmpty.getContext().getResources().getString(R.string.no_post_history);
                    break ;
            }
            viewHolder.txtEmpty.setText(emptyStr);
        }
    }

    private void setupEvent(final UserEventItem holder, final int position) {
        final EzlyEvent event = events.get(position);

        Context context = holder.itemView.getContext();

        Picasso.with(context)
                .load(CategoryIconHelper.getCategoryIcon(event.getCategory().getCode()))
                .fit()
                .into(holder.ivCategory);

        if(event.getImages() != null && event.getImages().size() > 0 && !TextUtils.isEmpty(event.getImages().get(0).getUrl())){
            Picasso.with(context)
                    .load(event.getImages().get(0).getUrl())
                    .fit()
                    .into(holder.ivImage);
        }

        holder.txtTitle.setText(event.getTitle());
        holder.txtDesc.setText(event.getDescription());

        holder.invisibleCover.setVisibility(event.isActive() ? View.GONE : View.VISIBLE);

        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onEventClicked(events.get(position));
                }
            }
        });

        holder.btnUnfavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onUnfavouriteClicked(position);
                }
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onEditClicked(event);
                }
            }
        });

        holder.btnVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onVisibleClicked(event);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onDeleteClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(events != null){
            count = events.size() == 0 ? 1 : events.size();
        }
        return count;
    }

    class UserEventItem extends RecyclerView.ViewHolder{

        @BindView(R.id.content_view) View contentView;
        @BindView(R.id.btn_visible) View btnVisible;
        @BindView(R.id.btn_edit) View btnEdit;
        @BindView(R.id.btn_delete) View btnDelete;
        @BindView(R.id.btn_unfavourite) View btnUnfavourite;

        @BindView(R.id.iv_image) ImageView ivImage;
        @BindView(R.id.iv_category) ImageView ivCategory;
        @BindView(R.id.txt_title) TextView txtTitle;
        @BindView(R.id.txt_desc) TextView txtDesc;

        @BindView(R.id.invisible_cover) View invisibleCover;



        public UserEventItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            switch (adapterType){
                case ADAPTER_TYPE_FAVOURITE:
                    btnVisible.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                    break;
                case ADAPTER_TYPE_POST_HISTORY:
                    btnUnfavourite.setVisibility(View.GONE);
                    break ;
            }
        }
    }



    class UserEventEmptyItem extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_empty) TextView txtEmpty;

        public UserEventEmptyItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface UserEventClickedListener{
        void onEventClicked(EzlyEvent event);
        void onVisibleClicked(EzlyEvent event);
        void onEditClicked(EzlyEvent event);
        void onDeleteClicked(int position);
        void onUnfavouriteClicked(int position);
    }
}
