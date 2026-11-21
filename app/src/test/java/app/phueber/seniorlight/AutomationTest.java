package app.phueber.seniorlight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AutomationTest {
    @Test
    public void parsesTheThreeActions() {
        assertEquals(Automation.Command.ON, Automation.parse("app.phueber.seniorlight.action.LIGHT_ON"));
        assertEquals(Automation.Command.OFF, Automation.parse("app.phueber.seniorlight.action.LIGHT_OFF"));
        assertEquals(Automation.Command.TOGGLE, Automation.parse("app.phueber.seniorlight.action.LIGHT_TOGGLE"));
    }

    @Test
    public void isLenientAboutCaseAndSpaces() {
        assertEquals(Automation.Command.ON, Automation.parse("  app.phueber.seniorlight.action.light_on "));
    }

    @Test
    public void ignoresAnythingElse() {
        assertNull(Automation.parse(null));
        assertNull(Automation.parse(""));
        assertNull(Automation.parse("app.phueber.seniorlight.action.LIGHT"));
        assertNull(Automation.parse("android.intent.action.MAIN"));
    }

    @Test
    public void mapsCommandsToTargets() {
        assertEquals(Boolean.TRUE, Automation.target(Automation.Command.ON));
        assertEquals(Boolean.FALSE, Automation.target(Automation.Command.OFF));
        assertNull(Automation.target(Automation.Command.TOGGLE));
    }
}
