package app.phueber.seniorlight;

/**
 * Whether {@link LightService} may be stopped now. A service started with
 * startForegroundService() must call startForeground() before it is stopped, or the
 * system ends the app ("did not then call Service.startForeground()"). So a stop that
 * arrives while a start is still unanswered is held back until onStartCommand has
 * answered every start. Pure and not thread-safe: used on the main thread only.
 */
final class StartGate {
    private int unanswered;
    private boolean stopWanted;

    /** startForegroundService() succeeded. A later start overrides an earlier stop. */
    void started() {
        unanswered++;
        stopWanted = false;
    }

    /** A stop is wanted. True: stop now. False: held back until the starts are answered. */
    boolean stop() {
        if (unanswered == 0) return true;
        stopWanted = true;
        return false;
    }

    /** onStartCommand answered one start. True: a stop was held back and is due now. */
    boolean answered() {
        if (unanswered > 0) unanswered--;
        if (unanswered > 0 || !stopWanted) return false;
        stopWanted = false;
        return true;
    }
}
