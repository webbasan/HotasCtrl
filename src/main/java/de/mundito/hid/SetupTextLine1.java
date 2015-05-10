package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerLine1;
import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:26
 */
public class SetupTextLine1
        implements SetupHandler
{
    public static final Parameter PARAMETER = Parameter.LINE1;

    @Override
    public void setup(final Hotas hotas, final ArgHandler argHandler) {
        ArgHandlerLine1 argHandlerLine1 = (ArgHandlerLine1)argHandler;
        hotas.setText(1, argHandlerLine1.getTextLine());
    }
}
