package de.mundito.args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        addFactory(ArgHandlerConsole.FACTORY);
        addFactory(ArgHandlerDaemon.FACTORY);
        addFactory(ArgHandlerHttpPort.FACTORY);
    }

    private ArgHandlerRegistry() {
        // do not instantiate
    }

    /**
     * Read argument from a command line.
     *
     * This might be used in a context when no command line parser properly prepared the command line elements, i.e.
     * no handling of quote characters etc. So this method will try to do it internally.
     *
     * Command line elements will normally be "words", separated by spaces. To allow strings containing spaces in an
     * element, this element should be enclosed by quote characters, either " or '. To allow any of the quote characters
     * in a string, use the other quote character to enclose the element (similar to JavaScript).
     *
     * Therefore, an element containing spaces and both quote characters is NOT supported.
     *
     * @param values first element: argument, second element: additional parameters
     * @return the ArgHandler representing the command line argument, or null if the argument is unknown.
     */
    public static ArgHandler readArg(final String... values) {
        if (values.length > 0) {
            Parameter parameter = Parameter.valueOf(normalizeArgName(values[0]));
            ArgHandler.Factory factory = getFactory(parameter);
            if (factory != null) {
                if (values.length > 1) {
                    // parse the rest
                    String rest = values[1];
                    // Regex:
                    // - group 1: match string starting with ", anything not a " upto the next "
                    // - group 2: match string starting with ', anything not a ' upto the next '
                    // - group 3: match anything non whitespace
                    String regex = "\"([^\"]*)\"|'([^']*)'|(\\S+)";

                    List<String> result = new ArrayList<>();
                    Matcher matcher = Pattern.compile(regex).matcher(rest);
                    while (matcher.find()) {
                        if (matcher.group(1) != null) {
                            result.add(matcher.group(1));
                        }
                        else if (matcher.group(2) != null) {
                            result.add(matcher.group(2));
                        }
                        else if (matcher.group(3) != null) {
                            result.add(matcher.group(3));
                        }
                    }
                    return factory.create(result.toArray(new String[result.size()]));
                }
                else {
                    // no addition parameters
                    return factory.create();
                }
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
                    // complain - but don't bail out...
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
