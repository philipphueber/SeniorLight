package app.phueber.seniorlight;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * The flashlight, through the platform's own torch API (CameraManager.setTorchMode).
 * No camera is opened and no permission prompt is needed; the system flashlight tile
 * uses the same switch, so both always agree.
 */
final class Torch {
    private Torch() {}

    private static final String PREFS = "light";
    private static final String KEY_ON = "on";
    /** How long to wait for the system to report the current state before guessing. */
    private static final long STATE_WAIT_MS = 1000;

    /** The camera whose flash is the flashlight, or null if the phone has none. */
    static String cameraId(CameraManager cm) {
        List<CameraPick.Cam> cams = new ArrayList<>();
        try {
            for (String id : cm.getCameraIdList()) {
                CameraCharacteristics c = cm.getCameraCharacteristics(id);
                Boolean flash = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer facing = c.get(CameraCharacteristics.LENS_FACING);
                cams.add(new CameraPick.Cam(id, Boolean.TRUE.equals(flash),
                        facing != null && facing == CameraCharacteristics.LENS_FACING_BACK));
            }
        } catch (CameraAccessException | RuntimeException e) {
            return null;
        }
        return CameraPick.choose(cams);
    }

    /**
     * Switches the light to the opposite of what it really is now (asked from the
     * system, not from our memory, so it is right even if the light was switched in
     * the quick settings), then runs {@code done}. Always runs {@code done}.
     */
    static void toggle(Context ctx, Runnable done) {
        Context app = ctx.getApplicationContext();
        CameraManager cm = app.getSystemService(CameraManager.class);
        String id = cm == null ? null : cameraId(cm);
        if (id == null) {
            toast(app, R.string.no_flash);
            done.run();
            return;
        }
        Handler main = new Handler(Looper.getMainLooper());
        boolean[] finished = {false};
        CameraManager.TorchCallback cb = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(String camId, boolean enabled) {
                if (finished[0] || !camId.equals(id)) return;
                finished[0] = true;
                cm.unregisterTorchCallback(this);
                set(app, cm, id, !enabled);
                done.run();
            }

            @Override
            public void onTorchModeUnavailable(String camId) {
                // Another app holds the camera; the light cannot be switched now.
                if (finished[0] || !camId.equals(id)) return;
                finished[0] = true;
                cm.unregisterTorchCallback(this);
                toast(app, R.string.busy);
                remember(app, false);
                done.run();
            }
        };
        // Registering makes the system report the current state straight away.
        cm.registerTorchCallback(cb, main);
        main.postDelayed(() -> {
            if (finished[0]) return;
            finished[0] = true;
            cm.unregisterTorchCallback(cb);
            set(app, cm, id, !isOn(app)); // no report: go by what we last knew
            done.run();
        }, STATE_WAIT_MS);
    }

    private static void set(Context app, CameraManager cm, String id, boolean on) {
        try {
            cm.setTorchMode(id, on);
            remember(app, on);
            if (on) LightService.start(app); else LightService.stop(app);
        } catch (CameraAccessException | RuntimeException e) {
            toast(app, R.string.busy);
        }
    }

    /** Last known state, used when the system does not report one in time. */
    static boolean isOn(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false);
    }

    /** Stores the state and makes the app icon show it. */
    static void remember(Context ctx, boolean on) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ON, on).apply();
        AppIcon.show(ctx, on);
    }

    private static void toast(Context app, int res) {
        Toast.makeText(app, res, Toast.LENGTH_LONG).show();
    }
}
