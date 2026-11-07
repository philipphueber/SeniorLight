package app.phueber.seniorlight;

import java.util.List;

/** Picks which camera's flash to use. Pure, so it is unit tested. */
final class CameraPick {
    private CameraPick() {}

    /** What the picker needs to know about one camera. */
    static final class Cam {
        final String id;
        final boolean hasFlash;
        final boolean facesBack;

        Cam(String id, boolean hasFlash, boolean facesBack) {
            this.id = id;
            this.hasFlash = hasFlash;
            this.facesBack = facesBack;
        }
    }

    /**
     * The back camera's flash if there is one (that is the flashlight people know),
     * otherwise any camera with a flash, otherwise null (no flashlight).
     */
    static String choose(List<Cam> cams) {
        String any = null;
        for (Cam c : cams) {
            if (!c.hasFlash) continue;
            if (c.facesBack) return c.id;
            if (any == null) any = c.id;
        }
        return any;
    }
}
