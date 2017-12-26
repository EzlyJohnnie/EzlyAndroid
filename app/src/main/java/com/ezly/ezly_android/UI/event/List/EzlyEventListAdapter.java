package com.ezly.ezly_android.UI.event.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.CategoryIconHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.Utils.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 15/10/16.
 */

public class EzlyEventListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_EVENT_EMPTY = 0;
    private static final int VIEW_TYPE_EVENT = 1;

    private List<EzlyEvent> events;
    private EventListListener mListener;
    private MemberHelper memberHelper;
    private LocationHelper locationHelper;
    private EzlySearchParam searchParam;

    public EzlyEventListAdapter(ArrayList<EzlyEvent> events,
                                MemberHelper memberHelper,
                                LocationHelper locationHelper,
                                EzlySearchParam searchParam)
    {
        super();
        this.events = events;
        this.memberHelper = memberHelper;
        this.locationHelper = locationHelper;
        this.searchParam = searchParam;
    }

    public void setListener(EventListListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemViewType(int position) {
        return events != null && events.size() > 0 ?
                VIEW_TYPE_EVENT : VIEW_TYPE_EVENT_EMPTY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case VIEW_TYPE_EVENT_EMPTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event_empty, parent, false);
                viewHolder = new EventEmptyViewHolder(view);
                    break;
            case VIEW_TYPE_EVENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
                viewHolder = new EventViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(events != null && events.size() > 0){
            final EventViewHolder viewHolder = (EventViewHolder) holder;
            setupEvent(viewHolder, position);
        }
        else{
            final EventEmptyViewHolder viewHolder = (EventEmptyViewHolder) holder;
            String emptyStr = viewHolder.txtEmpty.getContext().getResources().getString(R.string.no_result);
            emptyStr += searchParam.getSearchMode() == EzlySearchParam.SEARCH_MODE_JOB ?
                    viewHolder.txtEmpty.getContext().getResources().getString(R.string.job)
                    : viewHolder.txtEmpty.getContext().getResources().getString(R.string.service);
            viewHolder.txtEmpty.setText(emptyStr);
        }
    }

    private void setupEvent(EventViewHolder holder, int position) {
        final EzlyEvent event = events.get(position);

        Context context = holder.itemView.getContext();

        Picasso.with(context).cancelRequest(holder.ivImage);
        if(event.getImages() != null && event.getImages().size() > 0
            &&!TextUtils.isEmpty(event.getImages().get(0).getUrl()))
        {
            Picasso.with(context)
                    .load(event.getImages().get(0).getUrl())
                    .fit()
                    .into(holder.ivImage);
        }

        Picasso.with(context)
                    .load(CategoryIconHelper.getCategoryIcon(event.getCategory().getCode()))
                    .fit()
                    .into(holder.ivCategory);

        holder.txtTitle.setText(event.getTitle());
        holder.txtDesc.setText(event.getDescription());
        holder.txtName.setText(event.getUser().getDisplayName());

        float distanceDiff = event.getDistanceDiff(context, locationHelper.getLastKnownLocation());
        String distanceDiffStr = "";
        if(distanceDiff > 0){
            distanceDiffStr = String.format(context.getResources().getString(R.string.away_from_string), distanceDiff);
        }
        if(TextUtils.isEmpty(distanceDiffStr)){
            holder.txtDistance.setVisibility(View.GONE);
        }
        else{
            holder.txtDistance.setText(distanceDiffStr);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onEventClicked(event);
                }
            }
        });

        if(mListener != null && position == events.size() - 1){
            mListener.onScrollToBottom();
        }

        boolean isSaved = !event.canBeFavourited();
        if(memberHelper.hasLogin() && memberHelper.getCurrentUser().getId().equals(event.getUser().getId())){
            isSaved = true;
        }
        Picasso.with(context)
                .load(isSaved ? R.drawable.saved : R.drawable.unsaved)
                .fit()
                .into(holder.ivSave);
        holder.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onSaveEventClicked(event, event.canBeFavourited());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(events != null){
            count = events.size() == 0 ? 1 :  events.size();;
        }
        return count;
    }

    public void setEvents(List<EzlyEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }


    class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_title)      TextView txtTitle;
        @BindView(R.id.txt_desc)       TextView txtDesc;
        @BindView(R.id.iv_category)    ImageView ivCategory;
        @BindView(R.id.txt_distance)   TextView txtDistance;
        @BindView(R.id.iv_image)       ImageView ivImage;
        @BindView(R.id.txt_name)       TextView txtName;
        @BindView(R.id.iv_save)        ImageView ivSave;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class EventEmptyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_empty) TextView txtEmpty;

        public EventEmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface EventListListener {
        void onEventClicked(EzlyEvent event);
        void onScrollToBottom();
        void onSaveEventClicked(EzlyEvent event, boolean toSave);
    }

}
