package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerLed;
import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:25
 */
public class SetupLed
        implements SetupHandler
{
    public static final Parameter PARAMETER = Parameter.LED;

    @Override
    public void setup(final Hotas hotas, final ArgHandler argHandler) {
        ArgHandlerLed argHandlerLed = (ArgHandlerLed)argHandler;
        hotas.setLedColor(argHandlerLed.getLed(), argHandlerLed.getColor());
    }
}
