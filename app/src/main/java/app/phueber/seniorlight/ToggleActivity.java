package app.phueber.seniorlight;

import android.app.Activity;
import android.os.Bundle;

/**
 * Behind the app icon: switches the light and closes again, showing nothing.
 * Also takes the automation actions (LIGHT_ON/OFF/TOGGLE) as an activity start:
 * started in the foreground, it may start LightService, which a background
 * broadcast may not on Android 12+ (see ARCHITECTURE.md, Automation).
 */
public final class ToggleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Automation.Command c = Automation.parse(getIntent().getAction());
        Torch.switchTo(this, c == null ? null : Automation.target(c), () -> {
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
