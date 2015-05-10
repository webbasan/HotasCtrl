package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerLight;
import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:25
 */
public class SetupLight
        implements SetupHandler
{
    public static final Parameter PARAMETER = Parameter.LIGHT;

    @Override
    public void setup(final Hotas hotas, final ArgHandler argHandler) {
        ArgHandlerLight argHandlerLight = (ArgHandlerLight)argHandler;
        hotas.setBrightness(argHandlerLight.getLightSource(), argHandlerLight.getBrightness());
    }
}
