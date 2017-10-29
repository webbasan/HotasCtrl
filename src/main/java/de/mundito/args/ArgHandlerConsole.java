package de.mundito.args;

/**
 * User: webbasan Date: 14.07.15 Time: 21:01
 */
public class ArgHandlerConsole
        extends ArgHandler
{
    static final Factory FACTORY = new Factory() {
        @Override
        public Parameter getParameter() {
            return Parameter.CONSOLE;
        }

        public ArgHandlerConsole create(final String... values) {
            return new ArgHandlerConsole(values);
        }
    };

    private ArgHandlerConsole(final String... values) {
        super(values);
    }

    /**
     * The "console" argument is always valid, it doesn't have any sub arguments.
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
