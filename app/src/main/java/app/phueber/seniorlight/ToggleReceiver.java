package app.phueber.seniorlight;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Target of the widget (switches the light like a tap on the icon) and of the
 * notification's Turn off (only ever switches it off, so a stale notification
 * cannot switch the light back on).
 */
public final class ToggleReceiver extends BroadcastReceiver {
    private static final String ACTION_OFF = "app.phueber.seniorlight.action.NOTIFICATION_OFF";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        PendingResult result = goAsync();
        Boolean want = ACTION_OFF.equals(intent.getAction()) ? Boolean.FALSE : null;
        Torch.switchTo(ctx, want, result::finish);
    }

    /** For the widget: toggles. */
    static PendingIntent intent(Context ctx) {
        return PendingIntent.getBroadcast(ctx, 0, new Intent(ctx, ToggleReceiver.class),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /** For the notification: switches off. Its own action keeps it apart from {@link #intent}. */
    static PendingIntent offIntent(Context ctx) {
        return PendingIntent.getBroadcast(ctx, 1,
                new Intent(ctx, ToggleReceiver.class).setAction(ACTION_OFF),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
