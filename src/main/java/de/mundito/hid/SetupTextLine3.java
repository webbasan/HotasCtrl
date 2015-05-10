package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerLine3;
import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:27
 */
public class SetupTextLine3
        implements SetupHandler
{
    public static final Parameter PARAMETER = Parameter.LINE3;

    @Override
    public void setup(final Hotas hotas, final ArgHandler argHandler) {
        ArgHandlerLine3 argHandlerLine3 = (ArgHandlerLine3)argHandler;
        hotas.setText(3, argHandlerLine3.getTextLine());
    }
}
