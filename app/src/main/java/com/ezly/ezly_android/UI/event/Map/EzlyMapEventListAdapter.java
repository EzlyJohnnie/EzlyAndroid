package com.ezly.ezly_android.UI.event.Map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.UI.event.List.EzlyEventListAdapter;
import com.ezly.ezly_android.Utils.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 20/02/17.
 */

public class EzlyMapEventListAdapter extends RecyclerView.Adapter {
    private List<EzlyEvent> events;
    private EzlyEventListAdapter.EventListListener mListener;

    private LocationHelper locationHelper;
    private EzlySearchParam searchParam;

    public EzlyMapEventListAdapter(ArrayList<EzlyEvent> events,
                                   LocationHelper locationHelper,
                                   EzlySearchParam searchParam){
        super();
        this.events = events;
        this.locationHelper = locationHelper;
        this.searchParam = searchParam;
    }

    public void setListener(EzlyEventListAdapter.EventListListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_map_event, parent, false);
        RecyclerView.ViewHolder viewHolder = new EventViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final EventViewHolder viewHolder = (EventViewHolder) holder;
        setupEvent(viewHolder, position);
    }

    private void setupEvent(EventViewHolder holder, int position) {
        final EzlyEvent event = events.get(position);

        Context context = holder.itemView.getContext();

        Picasso.with(context).cancelRequest(holder.avatar);
        holder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder_user));
        if(!TextUtils.isEmpty(event.getUser().getAvatarUrl())){
            Picasso.with(context)
                    .load(event.getUser().getAvatarUrl())
                    .fit()
                    .placeholder(R.drawable.placeholder_user)
                    .into(holder.avatar);
        }


        String prefix = "";
        switch (searchParam.getSearchMode()){
            case EzlySearchParam.SEARCH_MODE_JOB:
                prefix = context.getResources().getString(R.string.need);
                break;
            case EzlySearchParam.SEARCH_MODE_SERVICE:
                prefix = context.getResources().getString(R.string.provide);
                break;
        }

        holder.txtType.setText(prefix);
        holder.txtTitle.setText(event.getTitle());
        holder.txtUsername.setText(event.getUser().getDisplayName());

        float distanceDiff = event.getDistanceDiff(context, locationHelper.getLastKnownLocation());
        String distanceDiffStr = "";
        if(distanceDiff > 0){
            distanceDiffStr = String.format(context.getResources().getString(R.string.away_from_string), distanceDiff);
        }
        holder.txtDistance.setText(TextUtils.isEmpty(distanceDiffStr) ? "" : distanceDiffStr);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onEventClicked(event);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    public void setEvents(List<EzlyEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }


    class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar)       ImageView avatar;
        @BindView(R.id.txt_username)    TextView txtUsername;
        @BindView(R.id.txt_type)        TextView txtType;
        @BindView(R.id.txt_title)       TextView txtTitle;
        @BindView(R.id.txt_distance)    TextView txtDistance;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
