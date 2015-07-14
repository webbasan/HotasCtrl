package de.mundito.app;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerRegistry;
import de.mundito.args.Parameter;
import de.mundito.configuration.Configuration;
import de.mundito.hid.Hotas;
import de.mundito.hid.HotasMock;
import de.mundito.hid.SetupHandler;
import de.mundito.hid.SetupHandlerRegistry;


/**
 * User: webbasan Date: 10.05.15 Time: 00:22
 */
public class HotasCtrlSim {
    private final Configuration configuration;

    private Hotas hotas;

    public HotasCtrlSim(Configuration configuration) {
        this.configuration = configuration;
    }

    public void init() {
        this.hotas = new HotasMock();
        this.hotas.init();
    }

    public void doIt() {
        for (ArgHandler argHandler : this.configuration.getArgHandlers()) {
            SetupHandler setupHandler = SetupHandlerRegistry.getHandler(argHandler.getParameter());
            if (setupHandler != null) {
                setupHandler.setup(this.hotas, argHandler);
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

        HotasCtrlSim app = new HotasCtrlSim(configuration);
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
