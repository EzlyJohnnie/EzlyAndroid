package com.ezly.ezly_android.UI.User;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.R;
import com.ezly.ezly_android.Utils.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 13/11/16.
 */

public class UserListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_USER_EMPTY = 0;
    private static final int VIEW_TYPE_USER = 1;

    private List<EzlyUser> users;
    private UserListListener listener;

    public UserListAdapter(List<EzlyUser> users) {
        this.users = users;
    }

    public void setListener(UserListListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return users != null && users.size() > 0 ?
                VIEW_TYPE_USER : VIEW_TYPE_USER_EMPTY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case VIEW_TYPE_USER_EMPTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user_empty, parent, false);
                viewHolder = new ListItemUserEmpty(view);
                break;
            case VIEW_TYPE_USER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
                viewHolder = new ListItemUser(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(users != null && users.size() > 0){
            final ListItemUser viewHolder = (ListItemUser) holder;
            setupUser(viewHolder, position);
        }
    }

    private void setupUser(ListItemUser viewHolder, int position) {
        final EzlyUser user = users.get(position);

        if(!TextUtils.isEmpty(user.getAvatarUrl())){
            Picasso.with(viewHolder.ivAvatar.getContext())
                    .load(user.getAvatarUrl())
                    .fit()
                    .placeholder(R.drawable.placeholder_user)
                    .into(viewHolder.ivAvatar);
        }
        else{
            Picasso.with(viewHolder.ivAvatar.getContext())
                    .load(R.drawable.placeholder_user)
                    .fit()
                    .into(viewHolder.ivAvatar);
        }

        viewHolder.txtName.setText(user.getDisplayName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onUserClicked(user);
                }
            }
        });

        if(listener != null && position == users.size() - 1){
            listener.onScrollToBottom();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(users != null){
            count = users.size() == 0 ? 1 : users.size();
        }
        return count;
    }

    public void setUser(List<EzlyUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    class ListItemUser extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_avatar) ImageView ivAvatar;
        @BindView(R.id.txt_name) TextView txtName;

        public ListItemUser(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    class ListItemUserEmpty extends RecyclerView.ViewHolder{

        public ListItemUserEmpty(View itemView) {
            super(itemView);
        }

    }

    public interface UserListListener {
        void onUserClicked(EzlyUser user);
        void onScrollToBottom();
    }
}
