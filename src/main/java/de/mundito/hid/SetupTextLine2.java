package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerLine2;
import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:26
 */
public class SetupTextLine2
        implements SetupHandler
{
    public static final Parameter PARAMETER = Parameter.LINE2;

    @Override
    public void setup(final Hotas hotas, final ArgHandler argHandler) {
        ArgHandlerLine2 argHandlerLine2 = (ArgHandlerLine2)argHandler;
        hotas.setText(2, argHandlerLine2.getTextLine());
    }
}
