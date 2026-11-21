package app.phueber.seniorlight;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StartGateTest {
    @Test
    public void stopsAtOnceWhenNoStartIsUnanswered() {
        StartGate g = new StartGate();
        assertTrue(g.stop());
        g.started();
        assertFalse(g.answered());
        assertTrue(g.stop());
    }

    @Test
    public void holdsAStopBackUntilTheStartIsAnswered() {
        StartGate g = new StartGate();
        g.started();
        assertFalse(g.stop());
        assertTrue(g.answered());
        assertFalse(g.answered()); // only once
    }

    @Test
    public void waitsForEveryUnansweredStart() {
        StartGate g = new StartGate();
        g.started();
        g.started();
        assertFalse(g.stop());
        assertFalse(g.answered());
        assertTrue(g.answered());
    }

    @Test
    public void aLaterStartCancelsAHeldBackStop() {
        StartGate g = new StartGate();
        g.started();
        assertFalse(g.stop());
        g.started();
        assertFalse(g.answered());
        assertFalse(g.answered());
    }
}
