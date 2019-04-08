package com.viseeointernational.battmon.view.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.util.TimeUtil;
import com.viseeointernational.battmon.view.page.main.MainActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Notifications {

    private static final String TAG = Notifications.class.getSimpleName();

    public static final int NOTIFICATION_ID_FOREGROUND = 1;
    private static final int NOTIFICATION_ID_MSG_START = 2;

    public static final String CHANNEL_ID_FOREGROUND = "1";
    private static final String CHANNEL_ID_MSG = "2";

    private Context context;
    private NotificationManager notificationManager;

    private Map<String, Integer> msgNotificationIds = new HashMap<>();
    private int lastMsgNotificationId = NOTIFICATION_ID_MSG_START;

    @Inject
    public Notifications(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void sendMsgNotification(String deviceId, String deviceName, long time, CharSequence text) {
        Notification.Builder builder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID_MSG);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID_MSG, "Message", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            builder.setChannelId(CHANNEL_ID_MSG);
        }
        builder.setStyle(new Notification.BigTextStyle());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (TextUtils.isEmpty(deviceName) && time == -1) {
            builder.setContentTitle(context.getText(R.string.app_name));
        } else {
            String title = "";
            if (!TextUtils.isEmpty(deviceName)) {
                title += deviceName + "  ";
            }
            if (time != -1) {
                title += TimeUtil.getFormatTime(time, "HH:mm");
            }
            builder.setContentTitle(title);
        }
        builder.setContentText(text);
        builder.setAutoCancel(true);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        int notificationId;
        if (msgNotificationIds.containsKey(deviceId)) {
            notificationId = msgNotificationIds.get(deviceId);
        } else {
            notificationId = lastMsgNotificationId;
            msgNotificationIds.put(deviceId, lastMsgNotificationId);
            lastMsgNotificationId++;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    public void noDeviceFound() {
        sendMsgNotification("10", null, -1, "No device is found nearby. If you have the device with you please check if battery is installed properly then rescan.");
    }

    public void connected(String deviceId, String name, long time) {
        sendMsgNotification(deviceId, name, time, "This device is connected with your phone.");
    }

    public void lostConnection(String deviceId, String name, long time) {
        sendMsgNotification(deviceId, name, time, "You have lost connection with this device.");
    }
}
