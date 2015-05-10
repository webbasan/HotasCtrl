package de.mundito.args;

/**
 * User: webbasan Date: 23.04.15 Time: 22:28
 */
public abstract class ArgHandler {
    interface Factory {
        Parameter getParameter();

        ArgHandler create(String... values);
    }


    private String[] values;

    protected ArgHandler(final String... values) {
        if (values == null) {
            throw new IllegalArgumentException(getParameter().name().toLowerCase() + ": no arguments!");
        }
        if (values.length != getParameter().getNumArgs()) {
            throw new IllegalArgumentException(getParameter().name().toLowerCase() + ": wrong number of arguments!");
        }
        this.values = values;
        parse(values);
    }

    public final String getValue(final int index) {
        return values[index];
    }

    public abstract boolean isValid();

    public abstract String getInvalidValueMessage();

    public abstract Parameter getParameter();


    protected abstract void parse(final String... values);

}
