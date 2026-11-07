package app.phueber.seniorlight;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Makes the app icon show the light's state. Android cannot change an app's icon,
 * so the manifest has two launcher entries (activity-aliases of ToggleActivity),
 * IconOff (dark grey, crossed-out flashlight) and IconOn (yellow, flashlight), and
 * exactly one of them is enabled. On the Pixel launcher (Android 11 and 15 tested)
 * a home-screen icon stays in place and follows the swap.
 */
final class AppIcon {
    private AppIcon() {}

    static void show(Context ctx, boolean on) {
        PackageManager pm = ctx.getPackageManager();
        ComponentName want = new ComponentName(ctx, on ? IconOn.NAME : IconOff.NAME);
        ComponentName other = new ComponentName(ctx, on ? IconOff.NAME : IconOn.NAME);
        // Enable the new entry before disabling the old one, so there is never a
        // moment without a launcher entry. DONT_KILL_APP: the process holds the light.
        set(pm, want, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        set(pm, other, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    private static void set(PackageManager pm, ComponentName c, int state) {
        if (pm.getComponentEnabledSetting(c) == state) return; // no needless launcher refresh
        pm.setComponentEnabledSetting(c, state, PackageManager.DONT_KILL_APP);
    }

    /** Names of the two aliases declared in the manifest. */
    private static final class IconOn { static final String NAME = "app.phueber.seniorlight.IconOn"; }
    private static final class IconOff { static final String NAME = "app.phueber.seniorlight.IconOff"; }
}
