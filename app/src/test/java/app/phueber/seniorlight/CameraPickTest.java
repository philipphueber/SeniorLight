package app.phueber.seniorlight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class CameraPickTest {
    private static CameraPick.Cam cam(String id, boolean flash, boolean back) {
        return new CameraPick.Cam(id, flash, back);
    }

    @Test
    public void prefersTheBackFlash() {
        assertEquals("0", CameraPick.choose(Arrays.asList(
                cam("1", true, false), cam("0", true, true))));
    }

    @Test
    public void takesAFrontFlashIfThereIsNoBackFlash() {
        assertEquals("1", CameraPick.choose(Arrays.asList(
                cam("0", false, true), cam("1", true, false))));
    }

    @Test
    public void takesTheFirstWhenSeveralBackCamerasHaveAFlash() {
        assertEquals("0", CameraPick.choose(Arrays.asList(
                cam("0", true, true), cam("2", true, true))));
    }

    @Test
    public void noFlashMeansNoFlashlight() {
        assertNull(CameraPick.choose(Arrays.asList(cam("0", false, true), cam("1", false, false))));
    }

    @Test
    public void noCamerasMeansNoFlashlight() {
        assertNull(CameraPick.choose(Collections.emptyList()));
    }
}
