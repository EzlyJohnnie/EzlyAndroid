package com.ezly.ezly_android.Data;

/**
 * Created by Johnnie on 27/10/16.
 */

public class ResultEvent {
    public static class ReloadUserEvent{}

    public static class ReloadMapEvent{}

    public static class ReloadEventsListEvent {}

    public static class OnLoginEvent{
        private EzlyUser currentUser;

        public OnLoginEvent(EzlyUser currentUser) {
            this.currentUser = currentUser;
        }

        public EzlyUser getCurrentUser() {
            return currentUser;
        }
    }

    public static class OnLogoutEvent {}

    public static class OnReceivePushNotificationEvent {
        private EzlyNotification notification;

        public void setNotification(EzlyNotification notification) {
            this.notification = notification;
        }

        public OnReceivePushNotificationEvent(EzlyNotification notification) {
            this.notification = notification;
        }
    }
}
