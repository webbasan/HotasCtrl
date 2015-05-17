package de.mundito.args;

/**
 * User: webbasan Date: 23.04.15 Time: 22:35
 */
public final class ArgHandlerLight
        extends ArgHandler
{
    static final Factory FACTORY = new Factory() {
        @Override
        public Parameter getParameter() {
            return Parameter.LIGHT;
        }

        public ArgHandlerLight create(final String... values) {
            return new ArgHandlerLight(values);
        }
    };

    private Parameter.LightSource lightSource;
    private int brightness;

    private ArgHandlerLight(final String... values) {
        super(values);
    }

    public Parameter.LightSource getLightSource() {
        return this.lightSource;
    }

    public int getBrightness() {
        return this.brightness;
    }

    /**
     * The "light" argument is valid, if the given light source name is known. The brightness value is irrelevant: any
     * will do and if the value was not parsable the lights will just go off.
     *
     * @return true, if the given value is valid.
     */
    @Override
    public boolean isValid() {
        return this.lightSource != null;
    }

    @Override
    public String getInvalidValueMessage() {
        return "Invalid values for parameter " + getParameter().name() + "."
                + " Valid values are: " + Parameter.LIGHT.getDescription();
    }

    @Override
    protected void parse(final String... values) {
        this.lightSource = Parameter.LightSource.valueOf(values[0].toUpperCase());

        Parameter.Brightness brightness = null;

        String brightnessValue = values[1].toUpperCase();
        for (Parameter.Brightness namedBrightness : Parameter.Brightness.values()) {
            if (namedBrightness.name().equals(brightnessValue)) {
                brightness = namedBrightness;
                this.brightness = brightness.value;
            }
        }

        if (brightness == null) {
            try {
                this.brightness = Integer.parseInt(values[1]);
            }
            catch (NumberFormatException nfe) {
                // unexpected values will just switch off the light
                this.brightness = Parameter.Brightness.OFF.value;
            }
        }
    }

    @Override
    public Parameter getParameter() {
        return FACTORY.getParameter();
    }
}
