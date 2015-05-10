package de.mundito.app;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerRegistry;
import de.mundito.args.Parameter;
import de.mundito.configuration.Configuration;
import de.mundito.hid.Hotas;
import de.mundito.hid.HotasX52Impl;
import de.mundito.hid.SetupHandler;
import de.mundito.hid.SetupHandlerRegistry;


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
        this.hotas = new HotasX52Impl();
        this.hotas.init();
    }

    public void doIt() {
        for (ArgHandler argHandler : this.configuration.getArgHandlers()) {
            SetupHandler setupHandler = SetupHandlerRegistry.getHandler(argHandler.getParameter());
            if (setupHandler != null) {
                setupHandler.setup(this.hotas, argHandler);
            }
            else {
                // FIXME: complain - don't know how to handle arg!
            }
        }
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
