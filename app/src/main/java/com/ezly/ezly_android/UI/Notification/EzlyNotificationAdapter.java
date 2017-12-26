package com.ezly.ezly_android.UI.Notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.NotificationHelper;
import com.ezly.ezly_android.Data.EzlyNotification;
import com.ezly.ezly_android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Johnnie on 12/03/17.
 */

public class EzlyNotificationAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_NOTIFICATION = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private static final int VIEW_TYPE_LOGIN = 2;

    private List<EzlyNotification> notifications;
    private NotificationListListener mListener;
    
    private MemberHelper memberHelper;
    private NotificationHelper notificationHelper;

    public EzlyNotificationAdapter(ArrayList<EzlyNotification> notifications,
                                   MemberHelper memberHelper, 
                                   NotificationHelper notificationHelper){
        super();
        this.notifications = notifications;
        this.memberHelper = memberHelper;
        this.notificationHelper = notificationHelper;
    }

    public void setListener(NotificationListListener mListener) {
        this.mListener = mListener;
    }

    public void setNotifications(List<EzlyNotification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if(memberHelper.hasLogin()){
            viewType =  notifications != null && notifications.size() > 0 ?
                    VIEW_TYPE_NOTIFICATION : VIEW_TYPE_EMPTY;
        }
        else{
            viewType = VIEW_TYPE_LOGIN;
        }

        return viewType;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(memberHelper.hasLogin()){
            if(notifications != null){
                count = notifications.size() == 0 ? 1 :  notifications.size();;
            }
        }
        else{
            count = 1;
        }

        return count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case VIEW_TYPE_NOTIFICATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
                viewHolder = new NotificationViewHolder(view);
                break;
            case VIEW_TYPE_EMPTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event_empty, parent, false);
                viewHolder = new EmptyNotificationViewHolder(view);
                break;
            case VIEW_TYPE_LOGIN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_login_item, parent, false);
                viewHolder = new NotificationLoginViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NotificationViewHolder){
            setupNotification((NotificationViewHolder)holder, position);
        }
        else if(holder instanceof NotificationLoginViewHolder){
            setupLoginCell((NotificationLoginViewHolder)holder, position);
        }
    }

    private void setupLoginCell(final NotificationLoginViewHolder holder, int position) {
        holder.loadingIndicator.setVisibility(View.GONE);
        holder.btnWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.loadingIndicator.setVisibility(View.VISIBLE);
                if(mListener != null){
                    mListener.loginWithWeChat();
                }
            }
        });

        holder.btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.loadingIndicator.setVisibility(View.VISIBLE);
                if(mListener != null){
                    mListener.loginWithFB();
                }
            }
        });

        holder.btnGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.loadingIndicator.setVisibility(View.VISIBLE);
                if(mListener != null){
                    mListener.loginWithGmail();
                }
            }
        });
    }

    private void setupNotification(NotificationViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        final EzlyNotification notification = notifications.get(position);
        boolean hasNotificationBeenRead = notificationHelper.hasNotificationBeenRead(context, notification.getId());
        holder.itemView.setBackground(context.getResources().getDrawable(hasNotificationBeenRead ? R.drawable.menu_item_read_bg_color : R.drawable.menu_item_bg_color));
        holder.txtSendTime.setText(notification.getFormattedSentTime(context));
        holder.txtContent.setText(notification.getFormattedContent(context));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onNotificationClicked(notification);
                }
            }
        });

        if(mListener != null && position == notifications.size() - 1){
            mListener.onScrollToBottom();
        }
    }


    public class EmptyNotificationViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_empty) TextView txtContent;

        public EmptyNotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txtContent.setText(itemView.getContext().getResources().getString(R.string.empty_notification));
        }
    }

    public class NotificationLoginViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_desc)    TextView txtContent;
        @BindView(R.id.btn_wechat)  View btnWeChat;
        @BindView(R.id.btn_fb)      View btnFB;
        @BindView(R.id.btn_gmail)   View btnGmail;
        @BindView(R.id.loading_indicator)   View loadingIndicator;

        public NotificationLoginViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txtContent.setText(itemView.getContext().getResources().getString(R.string.notification_login_desc));
        }
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_send_time) TextView txtSendTime;
        @BindView(R.id.txt_content) TextView txtContent;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface NotificationListListener{
        void onNotificationClicked(EzlyNotification notification);
        void loginWithWeChat();
        void loginWithFB();
        void loginWithGmail();
        void onScrollToBottom();
    }
}
