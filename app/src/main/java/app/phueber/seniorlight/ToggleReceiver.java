package app.phueber.seniorlight;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Target of the widget and of the notification's Turn off: switches the light like a tap on the icon. */
public final class ToggleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        PendingResult result = goAsync();
        Torch.toggle(ctx, result::finish);
    }

    static PendingIntent intent(Context ctx) {
        return PendingIntent.getBroadcast(ctx, 0, new Intent(ctx, ToggleReceiver.class),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
