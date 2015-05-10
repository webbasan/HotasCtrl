package de.mundito.args;

/**
 * User: webbasan Date: 23.04.15 Time: 22:35
 */
public final class ArgHandlerLed
        extends ArgHandler
{
    static final Factory FACTORY = new Factory() {
        @Override
        public Parameter getParameter() {
            return Parameter.LED;
        }

        public ArgHandlerLed create(final String... values) {
            return new ArgHandlerLed(values);
        }
    };

    private Parameter.Led led;
    private Parameter.LedColor color;


    private ArgHandlerLed(final String... values) {
        super(values);
    }

    /**
     * The "led" argument is valid, if the given LED name is known and the given color value is suitable.
     *
     * @return true, if the given value is valid.
     */
    @Override
    public boolean isValid() {
        return this.led != null && this.color != null;
    }

    @Override
    public String getInvalidValueMessage() {
        return "Invalid values for parameter " + getParameter().name() + "."
                + " Valid values are: " + Parameter.LED.getDescription();
    }

    public Parameter.Led getLed() {
        return this.led;
    }

    public Parameter.LedColor getColor() {
        return this.color;
    }

    @Override
    protected void parse(final String... values) {
        this.led = Parameter.Led.valueOf(values[0].toUpperCase());
        this.color = Parameter.LedColor.valueOf(values[1].toUpperCase());
    }

    @Override
    public Parameter getParameter() {
        return FACTORY.getParameter();
    }
}
