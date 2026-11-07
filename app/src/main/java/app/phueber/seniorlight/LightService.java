package app.phueber.seniorlight;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

/**
 * Runs only while the light is on, to keep the app's process alive: the system
 * turns the torch off when the process that switched it on dies, and Android ends
 * idle background processes whenever it needs memory. Its notification ("Flashlight
 * is on", with Turn off) is the one Android requires for this. On Android 13+ it is
 * only shown if notifications are allowed; the app does not ask, because a tap on it
 * must only switch the light. The service keeps the light on either way.
 */
public final class LightService extends Service {
    private static final String CHANNEL = "light";
    private static final int NOTIFICATION_ID = 1;

    static void start(Context ctx) {
        try {
            ctx.startForegroundService(new Intent(ctx, LightService.class));
        } catch (RuntimeException e) {
            // Not allowed now: the light still works, only not protected from process death.
        }
    }

    static void stop(Context ctx) {
        ctx.stopService(new Intent(ctx, LightService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager nm = getSystemService(NotificationManager.class);
        NotificationChannel ch = new NotificationChannel(CHANNEL, getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW); // silent: it is a status, not an alert
        nm.createNotificationChannel(ch);

        PendingIntent off = LightWidget.toggleIntent(this);
        Notification n = new Notification.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_flashlight_on)
                .setContentTitle(getString(R.string.notification_on))
                .setContentIntent(off)
                .addAction(new Notification.Action.Builder(null, getString(R.string.turn_off), off).build())
                .setOngoing(true)
                .build();
        try {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(NOTIFICATION_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(NOTIFICATION_ID, n);
            }
        } catch (RuntimeException e) {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
