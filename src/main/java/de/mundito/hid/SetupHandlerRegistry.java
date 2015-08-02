package de.mundito.hid;

import de.mundito.args.ArgHandler;
import de.mundito.args.Parameter;

import java.util.HashMap;
import java.util.Map;


/**
 * User: webbasan Date: 05.05.15 Time: 20:29
 */
public final class SetupHandlerRegistry {

    private static final Map<Parameter, SetupHandler> HANDLERS_BY_PARAMETER = new HashMap<>();

    static {
        HANDLERS_BY_PARAMETER.put(SetupLight.PARAMETER, new SetupLight());
        HANDLERS_BY_PARAMETER.put(SetupLed.PARAMETER, new SetupLed());
        HANDLERS_BY_PARAMETER.put(SetupTextLine1.PARAMETER, new SetupTextLine1());
        HANDLERS_BY_PARAMETER.put(SetupTextLine2.PARAMETER, new SetupTextLine2());
        HANDLERS_BY_PARAMETER.put(SetupTextLine3.PARAMETER, new SetupTextLine3());
        HANDLERS_BY_PARAMETER.put(SetupTextLines.PARAMETER, new SetupTextLines());
        HANDLERS_BY_PARAMETER.put(SetupClock.PARAMETER, new SetupClock());
        HANDLERS_BY_PARAMETER.put(Parameter.DAEMON, new SetupHandler() {
            @Override
            public void setup(final Hotas hotas, final ArgHandler argHandler) {
                // nothing to do.
            }
        });
    }

    private SetupHandlerRegistry() {
        // do not instantiate.
    }

    public static SetupHandler getHandler(final Parameter parameter) {
        return HANDLERS_BY_PARAMETER.get(parameter);
    }

}
