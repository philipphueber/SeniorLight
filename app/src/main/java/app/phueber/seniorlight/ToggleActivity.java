package app.phueber.seniorlight;

import android.app.Activity;
import android.os.Bundle;

/** Behind the app icon: switches the light and closes again, showing nothing. */
public final class ToggleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Torch.toggle(this, () -> {
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
