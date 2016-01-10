package de.mundito.args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User: webbasan Date: 23.04.15 Time: 22:27
 */
public final class ArgHandlerRegistry {
    private static final Map<Parameter, ArgHandler.Factory> FACTORIES = new HashMap<>();

    static {
        addFactory(ArgHandlerLight.FACTORY);
        addFactory(ArgHandlerLed.FACTORY);
        addFactory(ArgHandlerLine1.FACTORY);
        addFactory(ArgHandlerLine2.FACTORY);
        addFactory(ArgHandlerLine3.FACTORY);
        addFactory(ArgHandlerText.FACTORY);
        addFactory(ArgHandlerClock.FACTORY);
        addFactory(ArgHandlerDaemon.FACTORY);
    }

    private ArgHandlerRegistry() {
        // do not instantiate
    }


    public static ArgHandler getArgHandler(final String... values) {
        if (values.length > 0) {
            Parameter parameter = Parameter.valueOf(normalizeArgName(values[0]));
            ArgHandler.Factory factory = getFactory(parameter);
            if (factory != null) {
                return factory.create(Arrays.copyOfRange(values, 1, values.length));
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public static List<ArgHandler> readArgs(final String... values) {
        List<ArgHandler> argHandlers = new ArrayList<>(values.length);
        ArgsIterator iterator = new ArgsIterator(values);
        while (iterator.hasNext()) {
            ArgHandler argHandler = iterator.next();
            if (argHandler != null) {
                argHandlers.add(argHandler);
            }
        }
        return argHandlers;
    }

    private static String normalizeArgName(final String argName) {
        String result = argName;
        // get rid of "-" or "--" prefix
        if (result != null) {
            if (result.startsWith("--") && result.length() > 2) {
                result = result.substring(2);
            }
            else if (result.startsWith("-") && result.length() > 1) {
                result = result.substring(2);
            }
            // make sure name can match enum names
            result = result.toUpperCase();
        }
        return result;
    }

    static class ArgsIterator {

        private final String[] args;
        private int index;

        ArgsIterator(String[] args) {
            this.args = args;
            this.index = args != null && args.length > 0 ? 0 : -1;
        }

        public boolean hasNext() {
            return this.index != -1 && this.index < this.args.length;
        }

        public ArgHandler next() {
            if (hasNext()) {
                Parameter parameter = Parameter.valueOf(normalizeArgName(this.args[this.index++]));
                ArgHandler.Factory factory = getFactory(parameter);
                if (factory != null) {
                    int start = this.index;
                    int end = start + factory.getParameter().getNumArgs();
                    this.index = end;
                    return factory.create(Arrays.copyOfRange(this.args, start, end));
                }
                else {
                    // TODO: complain - but don't bail out...
                    System.err.println("Unknown argument '" + parameter + "'!");
                }
            }
            return null;
        }

    }

    private static ArgHandler.Factory getFactory(final Parameter parameter) {
        return FACTORIES.get(parameter);
    }

    private static void addFactory(final ArgHandler.Factory factory) {
        FACTORIES.put(factory.getParameter(), factory);
    }
}
