package com.ezly.ezly_android.UI.Address;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 12/02/17.
 */

public class AddressListAdapter extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_ADDRESS_EMPTY = 0;
    private static final int VIEW_TYPE_ADDRESS = 1;

    private List<EzlyAddress> addresses;
    private AddressListAdapter.OnAddressClickedListener onAddressClickedListener;

    public AddressListAdapter(ArrayList<EzlyAddress> addresses){
        super();
        this.addresses = addresses;
    }

    public void setOnAddressClickedListener(AddressListAdapter.OnAddressClickedListener onAddressClickedListener) {
        this.onAddressClickedListener = onAddressClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        return addresses != null && addresses.size() > 0 ?
                VIEW_TYPE_ADDRESS : VIEW_TYPE_ADDRESS_EMPTY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case VIEW_TYPE_ADDRESS_EMPTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event_empty, parent, false);
                viewHolder = new EventEmptyViewHolder(view);
                break;
            case VIEW_TYPE_ADDRESS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_address, parent, false);
                viewHolder = new AddressViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(addresses != null && addresses.size() > 0){
            final AddressViewHolder viewHolder = (AddressViewHolder) holder;
            setupAddress(viewHolder, position);
        }
        else{
            final EventEmptyViewHolder viewHolder = (EventEmptyViewHolder) holder;
            String emptyStr = viewHolder.txtEmpty.getContext().getResources().getString(R.string.no_address_str);
            viewHolder.txtEmpty.setText(emptyStr);
        }
    }

    private void setupAddress(AddressViewHolder holder, int position) {
        final EzlyAddress address = addresses.get(position);
        holder.txtName.setText(address.getName());
        holder.txtAddress.setText(address.getFullAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onAddressClickedListener != null){
                    onAddressClickedListener.onAddressClicked(address);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(addresses != null){
            count = addresses.size() == 0 ? 1 :  addresses.size();;
        }
        return count;
    }

    public void setAddress(List<EzlyAddress> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }


    class AddressViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_name)    TextView txtName;
        @BindView(R.id.txt_address) TextView txtAddress;

        public AddressViewHolder(View itemView) {
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

    public interface OnAddressClickedListener{
        void onAddressClicked(EzlyAddress address);
    }
}
