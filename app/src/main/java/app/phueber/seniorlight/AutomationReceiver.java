package app.phueber.seniorlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Light on, off or toggle from another app (Trigly, Tasker, ...), sent as an explicit
 * broadcast. Goes through the same {@link Torch} code as a tap on the icon, so the app
 * icon, the widget and the notification follow exactly as they would after a tap.
 */
public final class AutomationReceiver extends BroadcastReceiver {
    private static final String TAG = "SeniorLight";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Automation.Command c = Automation.parse(intent.getAction());
        if (c == null) {
            Log.w(TAG, "Ignoring unknown automation action " + Automation.describe(intent.getAction()));
            return;
        }
        PendingResult result = goAsync();
        Torch.switchTo(ctx, Automation.target(c), result::finish);
    }
}
