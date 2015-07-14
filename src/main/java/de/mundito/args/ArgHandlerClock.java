package de.mundito.args;

/**
 * User: webbasan Date: 14.07.15 Time: 21:01
 */
public class ArgHandlerClock
        extends ArgHandler
{
    static final Factory FACTORY = new Factory() {
        @Override
        public Parameter getParameter() {
            return Parameter.CLOCK;
        }

        public ArgHandlerClock create(final String... values) {
            return new ArgHandlerClock(values);
        }
    };

    private Parameter.ClockVariant clockVariant;

    protected ArgHandlerClock(final String... values) {
        super(values);
    }

    /**
     * The "clock" argument is valid, if the given sub argument name is known.
     *
     * @return true, if the given value is valid.
     */
    @Override
    public boolean isValid() {
        return this.clockVariant != null;
    }

    @Override
    public String getInvalidValueMessage() {
        return "Invalid values for parameter " + getParameter().name() + "."
                + " Valid values are: " + Parameter.CLOCK.getDescription();
    }

    public Parameter.ClockVariant getVariant() {
        return this.clockVariant;
    }

    @Override
    protected void parse(final String... values) {
        this.clockVariant = Parameter.ClockVariant.valueOf(values[0].toUpperCase());
    }

    @Override
    public Parameter getParameter() {
        return FACTORY.getParameter();
    }
}
