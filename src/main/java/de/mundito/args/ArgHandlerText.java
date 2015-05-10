package de.mundito.args;

/**
 * User: webbasan Date: 23.04.15 Time: 22:35
 */
public final class ArgHandlerText
        extends ArgHandler
{
    static final Factory FACTORY = new Factory() {
        @Override
        public Parameter getParameter() {
            return Parameter.TEXT;
        }

        public ArgHandlerText create(final String... values) {
            return new ArgHandlerText(values);
        }
    };

    private String textLine1;
    private String textLine2;
    private String textLine3;

    private ArgHandlerText(final String... values) {
        super(values);
    }

    public String getTextLine1() {
        return textLine1;
    }

    public String getTextLine2() {
        return textLine2;
    }

    public String getTextLine3() {
        return textLine3;
    }

    /**
     * The "text" argument is valid, if the given text is not null.
     *
     * @return true, if the given value is valid.
     */
    @Override
    public boolean isValid() {
        return this.textLine1 != null && this.textLine2 != null && this.textLine3 != null;
    }

    @Override
    public String getInvalidValueMessage() {
        return "Invalid values for parameter " + getParameter().name() + "."
                + " Valid values are: " + Parameter.TEXT.getDescription();
    }

    @Override
    protected void parse(final String... values) {
        if (values[0] != null) {
            for (int i = 0; i < 3; i++) {
                int firstLineBreakPos = values[0].indexOf("\\n");
                int secondLineBreakPos = firstLineBreakPos == -1 ? -1 : values[0].indexOf("\\n", firstLineBreakPos + 2);
                if (firstLineBreakPos > -1) {
                    this.textLine1 = values[0].substring(0, firstLineBreakPos);
                    if (secondLineBreakPos > -1) {
                        this.textLine2 = values[0].substring(firstLineBreakPos + 2, secondLineBreakPos);
                        this.textLine3 = values[0].substring(secondLineBreakPos + 2);
                    }
                    else {
                        this.textLine2 = values[0].substring(firstLineBreakPos + 2);
                        this.textLine3 = "";
                    }
                }
                else {
                    this.textLine1 = values[0];
                    this.textLine2 = "";
                    this.textLine3 = "";
                }
            }
        }
        else {
            this.textLine1 = "";
            this.textLine2 = "";
            this.textLine3 = "";
        }
    }

    @Override
    public Parameter getParameter() {
        return FACTORY.getParameter();
    }
}
