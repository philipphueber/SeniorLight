package app.phueber.seniorlight;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * The home-screen widget: a round button that looks like an app icon. Yellow with a
 * lit flashlight when the light is on, dark grey when it is off. A tap switches it.
 *
 * A widget rather than the app icon itself, because Android cannot change an app
 * icon at runtime; the workaround (swapping launcher entries) makes many launchers
 * drop the icon from the home screen.
 */
public final class LightWidget extends AppWidgetProvider {
    private static final String ACTION_TOGGLE = "app.phueber.seniorlight.TOGGLE";

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        // LightApp's torch callback corrects this as soon as the system reports.
        mgr.updateAppWidget(ids, views(ctx, Torch.isOn(ctx)));
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (ACTION_TOGGLE.equals(intent.getAction())) {
            PendingResult result = goAsync();
            Torch.toggle(ctx, result::finish);
            return;
        }
        super.onReceive(ctx, intent);
    }

    static void updateAll(Context ctx, boolean on) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        if (mgr == null) return;
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, LightWidget.class));
        if (ids.length > 0) mgr.updateAppWidget(ids, views(ctx, on));
    }

    private static RemoteViews views(Context ctx, boolean on) {
        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget);
        rv.setImageViewResource(R.id.light, on ? R.drawable.widget_on : R.drawable.widget_off);
        rv.setContentDescription(R.id.light, ctx.getString(on ? R.string.light_on : R.string.light_off));
        rv.setOnClickPendingIntent(R.id.light, toggleIntent(ctx));
        return rv;
    }

    /** Switches the light; used by the widget and by the notification's Turn off. */
    static PendingIntent toggleIntent(Context ctx) {
        Intent toggle = new Intent(ctx, LightWidget.class).setAction(ACTION_TOGGLE);
        return PendingIntent.getBroadcast(ctx, 0, toggle,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
