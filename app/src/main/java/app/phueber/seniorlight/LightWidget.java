package app.phueber.seniorlight;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * The home-screen widget: one round button with the same face as the app icon,
 * yellow when the light is on, dark grey when it is off. A tap switches the light.
 * An extra way in for home screens where the swapping app icon does not redraw
 * reliably; the app icon works without it.
 */
public final class LightWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        // LightApp's torch callback corrects this as soon as the system reports.
        mgr.updateAppWidget(ids, views(ctx, Torch.isOn(ctx)));
    }

    /** Redraws every placed widget to show {@code on}. */
    static void updateAll(Context ctx, boolean on) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        if (mgr == null) return;
        int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, LightWidget.class));
        if (ids.length > 0) mgr.updateAppWidget(ids, views(ctx, on));
    }

    private static RemoteViews views(Context ctx, boolean on) {
        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget);
        rv.setImageViewResource(R.id.light, on ? R.drawable.ic_launcher_on : R.drawable.ic_launcher_off);
        rv.setContentDescription(R.id.light, ctx.getString(on ? R.string.light_on : R.string.light_off));
        rv.setOnClickPendingIntent(R.id.light, ToggleReceiver.intent(ctx));
        return rv;
    }
}
