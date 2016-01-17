package de.mundito.args;

/**
 * User: webbasan Date: 22.04.15 Time: 22:52
 */
public enum Parameter {
    LIGHT("(" + LightSource.getValidValueDescription() + ") "
            + "((0..127)|" + Brightness.getValidValueDescription() + ")", 2, false),
    LED("(" + Led.getValidValueDescription() + ") "
            + "(" + LedColor.getValidValueDescription() + ")", 2, false),
    TEXT("<text for line 1>\\n<text for line 2>\\n<text for line 3>", 1, false),
    LINE1("<text for line 1>", 1, false),
    LINE2("<text for line 2>", 1, false),
    LINE3("<text for line 3>", 1, false),
    CLOCK("(" + ClockVariant.getValidValueDescription() + ")", 1, true),
    CONSOLE("", 0, true),
    HTTP_PORT("<port number", 1, true),
    DAEMON("", 0, true);

    public interface Sub {}

    public enum LightSource implements Sub {
        MFD,
        LED,
        ALL;

        public static String getValidValueDescription() {
            String validValues = null;
            for (LightSource lightSource : LightSource.values()) {
                if (validValues == null) {
                    validValues = lightSource.toString().toLowerCase();
                }
                else {
                    validValues += "|" + lightSource.toString().toLowerCase();
                }
            }
            return validValues;
        }
    }

    public enum Brightness implements Sub {
        // Brightness seems to be changed in steps of 4; maximum seems to be reached at about 127/128.
        // Negative values also give maximum brightness.
        OFF(0), DARK(4), HALF(64), FULL(128), ON(128);

        public final int value;

        Brightness(int value) {
            this.value = value;
        }

        public static String getValidValueDescription() {
            String validValues = null;
            for (Brightness brightness : Brightness.values()) {
                if (validValues == null) {
                    validValues = brightness.toString().toLowerCase();
                }
                else {
                    validValues += "|" + brightness.toString().toLowerCase();
                }
            }
            return validValues;
        }
    }

    public enum Led implements Sub {
        A, B, D, E, I, T1, T2, T3, POV, FIRE, THROTTLE, ALL;

        public static String getValidValueDescription() {
            String validValues = null;
            for (Led led : Led.values()) {
                if (validValues == null) {
                    validValues = led.toString().toLowerCase();
                }
                else {
                    validValues += "|" + led.toString().toLowerCase();
                }
            }
            return validValues;
        }
    }

    public enum LedColor implements Sub {
        OFF, ON, RED, AMBER, GREEN;

        public static String getValidValueDescription() {
            String validValues = null;
            for (LedColor ledColor : LedColor.values()) {
                if (validValues == null) {
                    validValues = ledColor.toString().toLowerCase();
                }
                else {
                    validValues += "|" + ledColor.toString().toLowerCase();
                }
            }
            return validValues;
        }
    }

    public enum ClockVariant implements Sub {
        LOCAL_24H, LOCAL_12H, UTC_24H, UTC_12H, GMT_24H, GMT_12H, ZULU_24H, ZULU_12H;

        public static String getValidValueDescription() {
            String validValues = null;
            for (ClockVariant variant : ClockVariant.values()) {
                if (validValues == null) {
                    validValues = variant.toString().toLowerCase();
                }
                else {
                    validValues += "|" + variant.toString().toLowerCase();
                }
            }
            return validValues;
        }
    }

    private final String description;
    private final int numArgs;
    private final boolean enableDaemonMode;

    Parameter(final String description, final int numArgs, final boolean enableDaemonMode) {
        this.description = description;
        this.numArgs = numArgs;
        this.enableDaemonMode = enableDaemonMode;
    }

    public String getDescription() {
        return description;
    }

    public int getNumArgs() {
        return numArgs;
    }

    public boolean enableDaemonMode() {
        return enableDaemonMode;
    }
}
