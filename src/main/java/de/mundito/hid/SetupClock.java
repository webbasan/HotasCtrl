package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerClock;
import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 14.07.15 Time: 21:29
 */
public class SetupClock
        implements SetupHandler
{
    public static final Parameter PARAMETER = Parameter.CLOCK;

    @Override
    public void setup(final Hotas hotas, final ArgHandler argHandler) {
        ArgHandlerClock argHandlerClock = (ArgHandlerClock)argHandler;
        hotas.enableClock(argHandlerClock.getVariant());
    }
}
