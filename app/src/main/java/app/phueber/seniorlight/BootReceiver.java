package app.phueber.seniorlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * After a restart, and after an app update (which ends the process and with it the
 * light), the light is off, but the icon and widget still show what they showed
 * before. This puts it back to "off" (LightApp's torch callback then confirms the
 * real state).
 */
public final class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        String a = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(a) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(a)) {
            Torch.remember(ctx, false);
        }
    }
}
