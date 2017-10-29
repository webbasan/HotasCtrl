package de.mundito.args;

/**
 * User: webbasan Date: 14.07.15 Time: 21:01
 */
public class ArgHandlerDaemon
        extends ArgHandler
{
    static final Factory FACTORY = new Factory() {
        @Override
        public Parameter getParameter() {
            return Parameter.DAEMON;
        }

        public ArgHandlerDaemon create(final String... values) {
            return new ArgHandlerDaemon(values);
        }
    };

    private ArgHandlerDaemon(final String... values) {
        super(values);
    }

    /**
     * The "daemon" argument is always valid, it doesn't have any sub arguments.
     *
     * @return always true.
     */
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getInvalidValueMessage() {
        return "";
    }

    @Override
    protected void parse(final String... values) {
        // nothing to do.
    }

    @Override
    public Parameter getParameter() {
        return FACTORY.getParameter();
    }
}
