package app.phueber.seniorlight;

import android.app.Application;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;

/**
 * Keeps the widget in step with the real light while the app's process is alive,
 * including when the light is switched from the quick settings or turned off by
 * the system.
 */
public final class LightApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CameraManager cm = getSystemService(CameraManager.class);
        String id = cm == null ? null : Torch.cameraId(cm);
        if (id == null) return;
        cm.registerTorchCallback(new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(String camId, boolean enabled) {
                if (!camId.equals(id)) return;
                Torch.remember(LightApp.this, enabled);
                // Off by any route (quick settings, notification, system): nothing to keep alive.
                if (!enabled) LightService.stop(LightApp.this);
            }

            @Override
            public void onTorchModeUnavailable(String camId) {
                if (!camId.equals(id)) return;
                Torch.remember(LightApp.this, false);
                LightService.stop(LightApp.this);
            }
        }, new Handler(Looper.getMainLooper()));
    }
}
