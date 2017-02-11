package com.harsha.eevee;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncEvents {
    private OnlineEventDetailsDBHandler onlineEventDetailsDBHandler;
    private WebEventDetailsDBHandler webEventDetailsDBHandler;
    private Context context;

    public SyncEvents(WebEventDetailsDBHandler web, OnlineEventDetailsDBHandler online) {
        onlineEventDetailsDBHandler = online;
        webEventDetailsDBHandler = web;
    }

    public SyncEvents(WebEventDetailsDBHandler web, OnlineEventDetailsDBHandler online, Context context) {
        onlineEventDetailsDBHandler = online;
        webEventDetailsDBHandler = web;
        this.context = context;
    }

    public void completePush() {
        onlineEventDetailsDBHandler.dropTable();
        int webCount = webEventDetailsDBHandler.getCount();
        for (int i = 0; i < webCount; i++) {
            OnlineEventDetails newRow = new OnlineEventDetails(webEventDetailsDBHandler.getObjectWith(WebEventDetailsDBHandler.COLUMN_ID, String.valueOf(i + 1)));
            onlineEventDetailsDBHandler.insertRow(newRow);
        }
    }

    // DO NOT CALL CARELESSLY
    public void filterOutdatedEvents() {
        onlineEventDetailsDBHandler.deleteOutdatedEvents();
        webEventDetailsDBHandler.deleteOutdatedEvents();
    }

    // TODO : remove all the non common events in onlineEventDetailsDBHandler

    public void startPushing() {
        int webCount = webEventDetailsDBHandler.getCount();
        int noOldEvents = 0;
        for (int i = 0; i < webCount; i++) {
            
            WebEventDetails webEvent = webEventDetailsDBHandler.getObjectWith(WebEventDetailsDBHandler.COLUMN_ID, String.valueOf(i + 1));
            String iTHeeVeeID = webEvent.get_eeVeeID();
            OnlineEventDetails onlineEvent = onlineEventDetailsDBHandler.getObjectWith(OnlineEventDetailsDBHandler.COLUMN_EEVEE_ID, iTHeeVeeID);

            if (onlineEvent != null) {
                /** common eeVee ID case */
                noOldEvents++;
                oldEventCase(webEvent, onlineEvent);
            } else {
                /** new eeVee ID case */
                newEventCase(webEvent);
            }
        }
    }

    private void oldEventCase(WebEventDetails webEvent, OnlineEventDetails onlineEvent) {
        /**
         * cases are TS
         *      same => no action
         *      different => cases are Status
         *          Online        Web
         *          created       edited  => issue edited notification and update in online
         *          created       deleted => issue deleted notification and update status to deleted in online
         *          edited        edited  => issue edited notification and update in online (over action no change edit case)
         *          edited        deleted => issue deleted notification and update status to deleted in online
         * */
        TimeStamp webTS = new TimeStamp(webEvent.get_TimeStamp());
        TimeStamp onlineTS = new TimeStamp(onlineEvent.get_TimeStamp());

        if (TimeStamp.isGreater(webTS, onlineTS)) {
            String webStatus = webEvent.get_Status();
            String onlineAproval = onlineEvent.get_Approval();

            if (webStatus.matches("edited") && onlineAproval.matches(Constants.APPROVAL_ACCEPTED)) {
                oldEventEditedNotification(webEvent, onlineEvent);
            } else if (webStatus.matches("deleted") && onlineAproval.matches(Constants.APPROVAL_ACCEPTED)) {
                oldEventDeletedNotification(webEvent, onlineEvent);
            }

            OnlineEventDetails updateWithThis = new OnlineEventDetails(webEvent, onlineEvent.get_Approval());
            Log.i("syncUpdate", onlineEvent.toString());
            onlineEventDetailsDBHandler.updateRow(updateWithThis, Integer.parseInt(webEvent.get_eeVeeID()));
        }
        return;
    }

    private void newEventCase(WebEventDetails webEvent) {
        /**
         * cases are Status
         *      created => insert into online and issue a approval notification
         *      edited => insert into online and issue a approval notification
         *      deleted => insert into online and DO NOT issue a approval notification
         * */
        String status = webEvent.get_Status();
        if (!status.matches("deleted")) {
            newEventNotification(webEvent);
        }

        OnlineEventDetails newOnlineEvent = new OnlineEventDetails(webEvent);
        onlineEventDetailsDBHandler.insertRow(newOnlineEvent);
    }

    private void newEventNotification(WebEventDetails webEvent) {
        Log.i("hello", webEvent.get_EventName());

        Intent postNoti = new Intent(context, Home.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, postNoti, PendingIntent.FLAG_UPDATE_CURRENT);

        DateTime present = new DateTime(true);

        Notification n = new Notification.Builder(context)
                .setContentTitle("Got New Events @ " + present.getTimeInput().timeDispString() + " " + present.getDateInput().dateDispString())
                .setContentText("I've fetched some new events \n" + "Now you can View , Accept or Ignore them ")
                .setSmallIcon(R.drawable.ic_sentiment_very_satisfied_white_24px)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int fixedID = 12345;
        notificationManager.notify(fixedID,n);
    }

    private void oldEventEditedNotification(WebEventDetails webEvent, OnlineEventDetails onlineEvent) {
        Intent postNoti = new Intent(context,ViewOnlineEvent.class);
        postNoti.putExtra("id" , Integer.parseInt(onlineEvent.get_eeVeeID()));
        postNoti.putExtra(Constants.FROM_INTENT_EXTRAS , Constants.FROM_EDITED_NOTIFICATIONS);

        int uniqueInt = (int) (System.currentTimeMillis());

        PendingIntent pIntent = PendingIntent.getActivity(context,uniqueInt,postNoti,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new Notification.Builder(context)
                .setContentTitle("Event Changed")
                .setContentText(onlineEvent.get_EventName() + " has been changed")
                .setSmallIcon(R.drawable.ic_sentiment_satisfied_white_24px)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setTicker(onlineEvent.get_EventName() + " has been changed")
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(onlineEvent.get_eeVeeID()), n);
    }

    private void oldEventDeletedNotification(WebEventDetails webEvent, OnlineEventDetails onlineEvent) {
        Intent postNoti = new Intent(context, ViewOnlineEvent.class);
        postNoti.putExtra("id" , Integer.parseInt(onlineEvent.get_eeVeeID()));
        postNoti.putExtra(Constants.FROM_INTENT_EXTRAS , Constants.FROM_DELETED_NOTIFICATIONS);

        int uniqueInt = (int) (System.currentTimeMillis());

        PendingIntent pIntent = PendingIntent.getActivity(context,uniqueInt, postNoti, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new Notification.Builder(context)
                .setContentTitle("Event Cancelled")
                .setContentText(webEvent.get_EventName() + " has been cancelled")
                .setSmallIcon(R.drawable.ic_sentiment_very_dissatisfied_white_24px)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setTicker(onlineEvent.get_EventName() + " has been cancelled")
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(onlineEvent.get_eeVeeID()),n);
    }
}