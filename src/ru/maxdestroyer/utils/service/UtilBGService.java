/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import ru.maxdestroyer.utils.R;

/**
 * Created by Maxim Smirnov on 27.11.15.
 */
public abstract class UtilBGService extends Service {
    private static final String TAG = "LocationSvc";
    protected Class activtiyClass;
    protected String text;
    protected String title;
    private NotificationManager mNM;

    public UtilBGService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        parseIntent(intent);
        showNotification();

        return Service.START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    protected abstract void parseIntent(final Intent intent);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, activtiyClass), 0);

        // Set the info for the views that show in the notification panel.
        Notification.Builder notificationB = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(title)  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                ;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification not = null;
        if (Build.VERSION.SDK_INT < 16) {
            not = notificationB.getNotification();
            notificationManager.notify(123, not);
        } else {
            not = notificationB.build();
            notificationManager.notify(123, not);
        }

        startForeground(123, not);
//        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // Send the notification.
//        mNM.notify(123, notification);
    }
}
