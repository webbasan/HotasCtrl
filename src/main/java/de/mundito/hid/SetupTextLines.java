package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerText;
import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 06.05.15 Time: 23:14
 */
public class SetupTextLines
        implements SetupHandler
{
    public static final Parameter PARAMETER = Parameter.TEXT;

    @Override
    public void setup(final Hotas hotas, final ArgHandler argHandler) {
        ArgHandlerText argHandlerText = (ArgHandlerText)argHandler;
        hotas.setText(1, argHandlerText.getTextLine1());
        hotas.setText(2, argHandlerText.getTextLine2());
        hotas.setText(3, argHandlerText.getTextLine3());
    }
}
