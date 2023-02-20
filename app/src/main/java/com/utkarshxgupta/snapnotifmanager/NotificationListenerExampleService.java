package com.utkarshxgupta.snapnotifmanager;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.utkarshxgupta.snapnotifmanager.Model.WhitelistModel;
import com.utkarshxgupta.snapnotifmanager.Utils.DatabaseHandler;

import java.util.List;

/**
 * MIT License
 *
 *  Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class NotificationListenerExampleService extends NotificationListenerService {

    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private static String title = "";
    DatabaseHandler db;
    private List<WhitelistModel> names;
    private static final class ApplicationPackageNames {
        public static final String SNAPCHAT_PACK_NAME = "com.snapchat.android";
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        PackageManager pom = getPackageManager();
//        pom.setComponentEnabledSetting(new ComponentName(this,com.utkarshxgupta.snapnotifmanager.NotificationListenerExampleService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
//        pom.setComponentEnabledSetting(new ComponentName(this,com.utkarshxgupta.snapnotifmanager.NotificationListenerExampleService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);

    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */

    public static final class InterceptedNotificationCode {
        public static final int SNAPCHAT_CODE = 1;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE){

            title = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
            if (checkTitle()) {
                cancelNotification(sbn.getKey());
            }
        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if(packageName.equals(ApplicationPackageNames.SNAPCHAT_PACK_NAME)){
            return(InterceptedNotificationCode.SNAPCHAT_CODE);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }

    private boolean checkTitle() {
        db = MainActivity.db;
        names = db.getAllPersons();

        for (WhitelistModel name:names) {
            if (name.getTask().equals(title))
                return false;
        }
        return true;
    }

    public static String sendTitle() {
        return title;
    }
}
