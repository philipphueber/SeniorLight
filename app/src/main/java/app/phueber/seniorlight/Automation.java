package app.phueber.seniorlight;

import java.util.Locale;

/**
 * The commands other apps (Trigly, Tasker, ...) can send to {@link AutomationReceiver}
 * as explicit broadcasts. Pure, so it is unit tested. No extras: the action says it all.
 */
final class Automation {
    private Automation() {}

    private static final String PREFIX = "app.phueber.seniorlight.action.";
    static final String LIGHT_ON = PREFIX + "LIGHT_ON";
    static final String LIGHT_OFF = PREFIX + "LIGHT_OFF";
    static final String LIGHT_TOGGLE = PREFIX + "LIGHT_TOGGLE";

    enum Command { ON, OFF, TOGGLE }

    /** The command an action stands for, or null if it is none of ours. Lenient about case and spaces. */
    static Command parse(String action) {
        if (action == null) return null;
        String a = action.trim();
        if (a.equalsIgnoreCase(LIGHT_ON)) return Command.ON;
        if (a.equalsIgnoreCase(LIGHT_OFF)) return Command.OFF;
        if (a.equalsIgnoreCase(LIGHT_TOGGLE)) return Command.TOGGLE;
        return null;
    }

    /** The state to switch to: true on, false off, null for "the opposite of now". */
    static Boolean target(Command c) {
        switch (c) {
            case ON: return Boolean.TRUE;
            case OFF: return Boolean.FALSE;
            default: return null;
        }
    }

    /** For log lines only. */
    static String describe(String action) {
        return action == null ? "(none)" : action.toLowerCase(Locale.ROOT);
    }
}
