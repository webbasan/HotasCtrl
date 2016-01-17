package de.mundito.args;

/**
 * User: webbasan Date: 14.07.15 Time: 21:01
 */
public class ArgHandlerHttpPort
        extends ArgHandler
{
    static final Factory FACTORY = new Factory() {
        @Override
        public Parameter getParameter() {
            return Parameter.HTTP_PORT;
        }

        public ArgHandlerHttpPort create(final String... values) {
            return new ArgHandlerHttpPort(values);
        }
    };

    private int port;

    protected ArgHandlerHttpPort(final String... values) {
        super(values);
    }

    public int getPort() {
        return this.port;
    }

    /**
     * The "http_port" argument is valid, if the given port number is either -1 ("disabled") or between 1 and 65535.
     *
     * @return true, if the given value is valid.
     */
    @Override
    public boolean isValid() {
        return this.port == -1 || ( 0 < this.port && this.port < 65536 );
    }

    @Override
    public String getInvalidValueMessage() {
        // We don't tell the user that well-known port are considered valid.
        // If somebody uses them anyway, he hopefully knows what he's doing...
        return "Invalid value for parameter " + getParameter().name() + "."
                + " Valid values are: -1 (:= disable http support) or a number between 1024 and 65535 (inclusive).";
    }

    @Override
    protected void parse(final String... values) {
        try {
            this.port = Integer.parseInt(values[0]);
        }
        catch (NumberFormatException nfe) {
            // unexpected values will result  "unset value"
            this.port = -1;
        }
    }

    @Override
    public Parameter getParameter() {
        return FACTORY.getParameter();
    }
}
