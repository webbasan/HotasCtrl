package de.mundito.app;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerRegistry;
import de.mundito.args.Parameter;
import de.mundito.configuration.Configuration;
import de.mundito.hid.Hotas;
import de.mundito.hid.SetupHandler;
import de.mundito.hid.SetupHandlerRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


/**
 * User: webbasan Date: 01.05.15 Time: 23:25
 */
public final class HotasCtrl {

    private final Configuration configuration;

    private Hotas hotas;

    public HotasCtrl(Configuration configuration) {
        this.configuration = configuration;
    }

    public void init() {
        this.hotas = this.configuration.createHotas();
        this.hotas.init();
    }

    public void doIt() {
        for (ArgHandler argHandler : this.configuration.getArgHandlers()) {
            SetupHandler setupHandler = SetupHandlerRegistry.getHandler(argHandler.getParameter());
            if (setupHandler != null) {
                setupHandler.setup(this.hotas, argHandler);
            }
            else {
                // complain - don't know how to handle arg!
                System.out.println("Unable to handle argument '" + argHandler.getParameter() + "'!");
            }
        }
        this.hotas.update();

        // do something different while hotas does what it has to do:
        // TODO: - open socket: expect to receive HTTP GET requests -> mapped to configuration commands

        // - if in foreground: open input stream, waiting to get new configuration commands
        // TODO: this should be enabled only on demand => Parameter "console" missing
        handleConsoleInput();
        // TODO: - if in background: wait until daemon thread dies...
    }

    public void shutdown() {
        this.hotas.shutdown();
        this.hotas = null;
    }

    public static void main(String... args) {
        Configuration configuration = new Configuration(ArgHandlerRegistry.readArgs(args));
        if (!configuration.isValid()) {
            System.err.println(configuration.getInvalidMessage());
            printUsage();
            return;
        }

        HotasCtrl app = new HotasCtrl(configuration);
        try {
            app.init();
            app.doIt();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        finally {
            app.shutdown();
        }
    }

    private void handleConsoleInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                String input = reader.readLine();
                if (input == null) {
                    break;
                }
                else if (input.equals("")) {
                    continue;
                }

                input = input.toLowerCase();
                if (input.startsWith("quit") || input.startsWith("exit")) {
                    break;
                }
                else if (input.startsWith("help")) {
                    printUsage();
                }
                else {
                    boolean stateChanged = false;
                    try {
                        List<ArgHandler> argHandlers = ArgHandlerRegistry.readArgs(input.split(" "));
                        for (ArgHandler argHandler : argHandlers) {
                            if (argHandler.isValid()) {
                                SetupHandler setupHandler = SetupHandlerRegistry.getHandler(argHandler.getParameter());
                                if (setupHandler != null) {
                                    setupHandler.setup(this.hotas, argHandler);
                                    stateChanged = true;
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Unable to handle input '" + input + "': " + e);
                    }
                    finally {
                        if (stateChanged) {
                            this.hotas.update();
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error while reading input: " + e);
        }
    }

    private static void printUsage() {
        StringBuilder usageDescription = new StringBuilder("Usage: HotasCtrl ");

        for (Parameter parameter : Parameter.values()) {
            usageDescription.append(getParameterUsage(parameter));
        }

        System.err.println(usageDescription);
    }

    private static String getParameterUsage(Parameter parameter) {
        return " [" + parameter.name().toLowerCase() + " " + parameter.getDescription() + "]";
    }

}
